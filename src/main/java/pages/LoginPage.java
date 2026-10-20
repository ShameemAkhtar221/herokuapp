package pages;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utils.ConfigReader;


public class LoginPage extends BasePage {


    @FindBy(id = "username")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    @FindBy(id = "flash")
    private WebElement flashMessage;




    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void open(){
        driver.get(ConfigReader.getProperty("baseUrl") + "/login");
    }

    public void enterUsername(String username) {
        typeText(usernameField, username);
    }

    public void enterPassword(String password) {
        typeText(passwordField, password);
    }

    public void clickLoginButton() {
        click(loginButton);
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    public boolean isLoaded() {
        try {
            waitForURL("/login");
            waitForVisibilityOfElement(usernameField);
            return true;
        }
        catch (TimeoutException ex) {
            return false;
        }
    }

    public String getFlashMessage() {
        return getText(flashMessage);
    }
}
