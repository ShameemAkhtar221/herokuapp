package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.AlertsPage;

@Epic("the-internet")
@Feature("JavaScript alerts")
public class AlertsTest extends BaseTest {

    private AlertsPage alertsPage;

    @BeforeMethod(alwaysRun = true)
    public void initPages() {
        alertsPage = new AlertsPage(driver);
        alertsPage.open();
        Assert.assertTrue(alertsPage.isLoaded(), "Alerts page did not load");
    }

    @Severity(SeverityLevel.CRITICAL)
    @Description("Accepting a JS alert shows the success result")
    @Test(groups = {"Smoke", "Regression"}, testName = "Accept JS alert")
    public void acceptJsAlert() {
        alertsPage.acceptJsAlert();
        Assert.assertEquals(alertsPage.getResult(), "You successfully clicked an alert");
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Accepting a JS confirm shows \"You clicked: Ok\"")
    @Test(groups = {"Regression"}, testName = "Test JS confirm")
    public void testJsConfirm() {
        alertsPage.acceptJsConfirm();
        Assert.assertEquals(alertsPage.getResult(), "You clicked: Ok");

    }


    @Severity(SeverityLevel.NORMAL)
    @Description("Text entered in a JS prompt is shown in the result")
    @Test(groups = {"Regression"}, testName = "Prompt alert")
    public void TestPromptAlert(){
        alertsPage.jsPromptAlert("Prompt Alert");
        Assert.assertEquals(alertsPage.getResult(), "You entered: Prompt Alert");
    }
}
