package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.Alert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.ConfigReader;

public class AlertsPage extends BasePage {

    @FindBy(xpath = "//h3[contains(text(), 'JavaScript Alerts')]")
    private WebElement heading;

    @FindBy(xpath = "//button[contains(text(), 'Click for JS Alert')]")
    private WebElement jsAlertButton;

    @FindBy(xpath = "//button[contains(text(), 'Click for JS Confirm')]")
    private WebElement jsConfirmButton;

    @FindBy(xpath = "//button[contains(text(), 'Click for JS Prompt')]")
    private WebElement jsPromptButton;

    @FindBy(id = "result")
    private WebElement result;

    public AlertsPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open JavaScript alerts page")
    public void open() {
        driver.get(ConfigReader.getProperty("baseUrl") + "/javascript_alerts");
    }

    public boolean isLoaded() {
        try {
            waitForURL("/javascript_alerts");
            waitForVisibilityOfElement(heading);
            return true;
        }
        catch (TimeoutException e) {
            return false;
        }
    }

    @Step("Accept JS alert")
    public void acceptJsAlert() {
        jsAlertButton.click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
    }

    @Step("Accept JS confirm")
    public void acceptJsConfirm() {
        jsConfirmButton.click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

    }

    @Step("Enter \"{text}\" in JS prompt")
    public void jsPromptAlert(String text) {
        jsPromptButton.click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.sendKeys(text);
        alert.accept();
    }

    public String getResult() {
        return getText(result);
    }
}
