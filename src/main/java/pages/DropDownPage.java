package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import utils.ConfigReader;

public class DropDownPage extends BasePage {

    @FindBy(id = "dropdown")
    private WebElement dropdown;

    public DropDownPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open dropdown page")
    public void open() {
        driver.get(ConfigReader.getProperty("baseUrl") + "/dropdown");
    }

    public boolean isLoaded() {
        try {
            waitForURL("/dropdown");
            waitForVisibilityOfElement(dropdown);
            return true;
        }
        catch (TimeoutException e) {
            return false;
        }
    }

    @Step("Select \"{text}\" from dropdown")
    public void selectByVisibleText(String text) {
        waitForElementToBeClickable(dropdown);
        new Select(dropdown).selectByVisibleText(text);
    }

    public String getSelectedOption() {
        return new Select(waitForVisibilityOfElement(dropdown)).getFirstSelectedOption().getText();
    }
}
