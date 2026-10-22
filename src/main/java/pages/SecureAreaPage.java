package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class SecureAreaPage extends BasePage{


    @FindBy(css = "a[href='/logout']")
    private WebElement logoutButton;

    @FindBy(id = "flash")
    private WebElement flashMessage;



    public SecureAreaPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        try {
            waitForURL("/secure");
            waitForVisibilityOfElement(logoutButton);
            return true;
        }
        catch (TimeoutException e) {
            return false;
        }
    }

    public String getFlashMessage() {
        return getText(flashMessage);
    }

    @Step("Log out")
    public LoginPage logout() {
        click(logoutButton);
        return new LoginPage(driver);
    }
}
