package tests;

import io.qameta.allure.Allure;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import utils.ConfigReader;
import utils.ImageComparator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Base for visual tests: compares screenshots with baselines in src/test/baselines/<browser>[-headless]/<TestClass>/.
// A missing baseline is recorded and the test skipped; -DupdateBaselines=true re-records all of them.
public abstract class VisualBaseTest extends BaseTest {

    private static final Path BASELINE_DIR = Paths.get("src", "test", "baselines");
    private static final Path OUTPUT_DIR = Paths.get("target", "visual");
    private static final String FREEZE_PAGE = """
            if (!document.getElementById('visual-test-freeze')) {
                const style = document.createElement('style');
                style.id = 'visual-test-freeze';
                style.textContent = '*, *::before, *::after { transition: none !important;'
                        + ' animation: none !important; caret-color: transparent !important; }';
                document.head.appendChild(style);
            }
            if (document.activeElement) { document.activeElement.blur(); }
            """;

    // Full viewport screenshot, scrolled to the top with the mouse moved out of the way.
    protected void assertMatchesBaseline(String name) {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
        new Actions(driver).moveToLocation(0, 0).perform();
        assertMatchesBaseline(name, (TakesScreenshot) driver);
    }

    // Screenshot of one element; the mouse stays where it is, so hover states are kept.
    protected void assertMatchesBaseline(String name, WebElement element) {
        assertMatchesBaseline(name, (TakesScreenshot) element);
    }

    private void assertMatchesBaseline(String name, TakesScreenshot source) {
        Allure.step("Compare screenshot with baseline: " + name, () -> {
            // Skip CSS transitions/animations to their end state (e.g. the 300ms button color fade after a click),
            // and hide the blinking text caret
            ((JavascriptExecutor) driver).executeScript(FREEZE_PAGE);
            byte[] screenshot = source.getScreenshotAs(OutputType.BYTES);

            Path baseline = BASELINE_DIR.resolve(variant()).resolve(getClass().getSimpleName()).resolve(name + ".png");
            boolean update = Boolean.parseBoolean(ConfigReader.getProperty("updateBaselines"));
            if (update || !Files.exists(baseline)) {
                write(baseline, screenshot);
                Allure.addAttachment("baseline", "image/png", new ByteArrayInputStream(screenshot), ".png");
                throw new SkipException("Baseline recorded: " + baseline.toAbsolutePath());
            }

            byte[] expectedBytes = Files.readAllBytes(baseline);
            ImageComparator.Result result = ImageComparator.compare(
                    read(expectedBytes), read(screenshot),
                    Integer.parseInt(ConfigReader.getProperty("visualColorTolerance")));
            double threshold = Double.parseDouble(ConfigReader.getProperty("visualThreshold"));
            if (result.sizeMismatch() || result.diffPercent() > threshold) {
                Path outputDir = OUTPUT_DIR.resolve(variant()).resolve(getClass().getSimpleName());
                byte[] diffBytes = toPng(result.diffImage());
                write(outputDir.resolve(name + "_actual.png"), screenshot);
                Path diff = outputDir.resolve(name + "_diff.png");
                write(diff, diffBytes);

                // Shown as a side-by-side/overlay viewer by Allure's screen-diff plugin
                Allure.label("testType", "screenshotDiff");
                Allure.addAttachment("expected", "image/png", new ByteArrayInputStream(expectedBytes), ".png");
                Allure.addAttachment("actual", "image/png", new ByteArrayInputStream(screenshot), ".png");
                Allure.addAttachment("diff", "image/png", new ByteArrayInputStream(diffBytes), ".png");

                String reason = result.sizeMismatch() ? "has a different size than the baseline"
                        : String.format("differs from baseline by %.3f%% (allowed %.3f%%)", result.diffPercent(), threshold);
                Reporter.log("Visual diff saved: " + diff.toAbsolutePath(), true);
                Assert.fail(name + " " + reason + ", see " + diff.toAbsolutePath());
            }
        });
    }

    // Headed and headless viewports differ, so each browser/mode pair has its own baselines.
    private static String variant() {
        String browser = System.getProperty("browser", ConfigReader.getProperty("browser")).trim().toLowerCase();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", ConfigReader.getProperty("headless")));
        return headless ? browser + "-headless" : browser;
    }

    private static BufferedImage read(byte[] png) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(png));
    }

    private static byte[] toPng(BufferedImage image) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            return out.toByteArray();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void write(Path path, byte[] bytes) throws IOException {
        Files.createDirectories(path.getParent());
        Files.write(path, bytes);
    }
}
