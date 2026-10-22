package tests;

import io.qameta.allure.Allure;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import utils.ConfigReader;
import utils.DriverFactory;
import utils.ScreenshotUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;


public class BaseTest {

    private static final Path ALLURE_ENVIRONMENT = Paths.get("target", "allure-results", "environment.properties");
    private static final Set<String> browsersRun = new TreeSet<>();

    protected WebDriver driver;
    private String browser;

    // Browser comes from the testng.xml parameter, then -Dbrowser, then config.properties.
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(@Optional String browser) {
        if (browser == null) {
            browser = System.getProperty("browser", ConfigReader.getProperty("browser"));
        }
        boolean headless = Boolean.parseBoolean(
                System.getProperty("headless", ConfigReader.getProperty("headless")));

        this.browser = browser;
        writeAllureEnvironment(browser, headless);
        driver = DriverFactory.createDriver(browser, headless);
        driver.manage().window().setSize(new Dimension(1680, 1050));
        String baseUrl = ConfigReader.getProperty("baseUrl");
        driver.get(baseUrl);

    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result) {
        if (driver == null) {
            return;
        }
        if (result.getStatus() == ITestResult.FAILURE) {
            takeScreenshot(result);
        }
        driver.quit();
    }

    // Failed tests save a screenshot to target/screenshots/ before the driver quits.
    private void takeScreenshot(ITestResult result) {

        String name = getClass().getSimpleName() + "_" + result.getMethod().getMethodName() + "_" + browser;
        try {
            Path path = ScreenshotUtil.capture(driver, name);
            Reporter.log("Screenshot saved: " + path.toAbsolutePath(), true);
            try (InputStream screenshot = Files.newInputStream(path)) {
                Allure.addAttachment(name, "image/png", screenshot, ".png");
            }
        }
        catch (WebDriverException | IOException e) {
            Reporter.log("Could not take screenshot for " + name + ": " + e.getMessage(), true);
        }
    }

    // Fills the Environment widget of the Allure report with every browser used in this run.
    private static synchronized void writeAllureEnvironment(String browser, boolean headless) {
        if (!browsersRun.add(browser) && Files.exists(ALLURE_ENVIRONMENT)) {
            return;
        }
        Properties environment = new Properties();
        environment.setProperty("Browser", String.join(", ", browsersRun));
        environment.setProperty("Headless", String.valueOf(headless));
        environment.setProperty("Base URL", ConfigReader.getProperty("baseUrl"));
        try {
            Files.createDirectories(ALLURE_ENVIRONMENT.getParent());
            try (Writer writer = Files.newBufferedWriter(ALLURE_ENVIRONMENT)) {
                environment.store(writer, null);
            }
        }
        catch (IOException e) {
            Reporter.log("Could not write Allure environment: " + e.getMessage(), true);
        }
    }
}
