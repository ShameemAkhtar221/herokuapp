package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.SecureAreaPage;
import utils.ConfigReader;


public class LoginTest extends BaseTest {

    private String username;
    private String password;
    private String wrongPassword;



    private LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void initPages() {
        loginPage = new LoginPage(driver);
        username = ConfigReader.getProperty("username");
        password = ConfigReader.getProperty("password");
        wrongPassword = ConfigReader.getProperty("wrongPassword");
        loginPage.open();

    }

    @Test(groups = {"Smoke", "Regression"}, testName = "Login test")
    public void login() {
        loginPage.login(username, password);
        SecureAreaPage secureAreaPage = new SecureAreaPage(driver);

        String message = secureAreaPage.getFlashMessage();
        Assert.assertTrue(secureAreaPage.isLoaded(), "Secure area page did not load");
        Assert.assertTrue(message.contains("You logged into a secure area!"),
                "Unexpected flash message: " + message);
    }

    @Test(groups = {"Smoke", "Regression"}, testName = "Invalid login")
    public void invalidLogin() {
        loginPage.login(username, wrongPassword);

        String message = loginPage.getFlashMessage();
        Assert.assertTrue(message.contains("Your password is invalid!"),
                "Unexpected flash message: " + message);
        Assert.assertTrue(loginPage.isLoaded(), "User should stay on the login page");
    }

    @Test(groups = {"Smoke", "Regression"}, testName = "Logout test")
    public void logout() {
        loginPage.login(username, password);
        SecureAreaPage secureAreaPage = new SecureAreaPage(driver);
        Assert.assertTrue(secureAreaPage.isLoaded(), "Secure area page did not load");

        LoginPage loggedOutPage = secureAreaPage.logout();

        String message = loggedOutPage.getFlashMessage();
        Assert.assertTrue(loggedOutPage.isLoaded(), "Login page did not load after logout");
        Assert.assertTrue(message.contains("You logged out of the secure area!"),
                "Unexpected flash message: " + message);
    }

}
