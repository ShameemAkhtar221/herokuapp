package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utils.ConfigReader;

public class DynamicLoadingPage extends BasePage {


    public DynamicLoadingPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//button[contains(text(), 'Start')]")
    private WebElement startButton;

    @FindBy(xpath = "//h4[contains(text(), 'Hello World!')]")
    private WebElement helloWorld;


    @Step("Open dynamic loading page")
    public void open(){
        driver.get(ConfigReader.getProperty("baseUrl") + "/dynamic_loading/1");
    }

    public Boolean isLoaded() {
        try {
            waitForURL("/dynamic_loading/1");
            waitForVisibilityOfElement(startButton);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Step("Click Start")
    public void clickStartButton(){
        startButton.click();
    }

    public String DynamicElementVisibility(){
        return waitForVisibilityOfElement(helloWorld).getText();

    }
}
