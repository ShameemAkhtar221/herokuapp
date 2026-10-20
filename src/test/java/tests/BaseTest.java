package tests;

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
import java.nio.file.Path;


public class BaseTest {

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
        }
        catch (WebDriverException | IOException e) {
            Reporter.log("Could not take screenshot for " + name + ": " + e.getMessage(), true);
        }
    }
}
