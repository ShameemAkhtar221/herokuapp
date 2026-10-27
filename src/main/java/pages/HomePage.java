package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utils.ConfigReader;

public class HomePage extends BasePage {

    @FindBy(css = "h1.heading")
    private WebElement heading;

    @FindBy(css = "#content ul")
    private WebElement exampleLinks;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Step("Open home page")
    public void open() {
        driver.get(ConfigReader.getProperty("baseUrl") + "/");
    }

    public boolean isLoaded() {
        try {
            waitForVisibilityOfElement(heading);
            waitForVisibilityOfElement(exampleLinks);
            return true;
        }
        catch (TimeoutException ex) {
            return false;
        }
    }

    public String getHeading() {
        return getText(heading);
    }
}
