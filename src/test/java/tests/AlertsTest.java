package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.AlertsPage;

public class AlertsTest extends BaseTest {

    private AlertsPage alertsPage;

    @BeforeMethod(alwaysRun = true)
    public void initPages() {
        alertsPage = new AlertsPage(driver);
        alertsPage.open();
        Assert.assertTrue(alertsPage.isLoaded(), "Alerts page did not load");
    }

    @Test(groups = {"Smoke", "Regression"}, testName = "Accept JS alert")
    public void acceptJsAlert() {
        alertsPage.acceptJsAlert();
        Assert.assertEquals(alertsPage.getResult(), "You successfully clicked an alert");
    }

    @Test(groups = {"Regression"}, testName = "Test JS confirm")
    public void testJsConfirm() {
        alertsPage.acceptJsConfirm();
        Assert.assertEquals(alertsPage.getResult(), "You clicked: Ok");

    }


    @Test(groups = {"Regression"}, testName = "Prompt alert")
    public void TestPromptAlert(){
        alertsPage.jsPromptAlert("Prompt Alert");
        Assert.assertEquals(alertsPage.getResult(), "You entered: Prompt Alert");
    }
}
