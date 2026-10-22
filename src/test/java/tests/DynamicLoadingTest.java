package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.DynamicLoadingPage;

@Epic("the-internet")
@Feature("Dynamic loading")
public class DynamicLoadingTest extends BaseTest {

    DynamicLoadingPage dynamicLoadingPage;

    @BeforeMethod(alwaysRun = true)
    public void initPages(){
        dynamicLoadingPage = new DynamicLoadingPage(driver);
        dynamicLoadingPage.open();
        Assert.assertTrue(dynamicLoadingPage.isLoaded(), "Dynamic loading Page is not loaded");
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Clicking Start reveals the hidden \"Hello World!\" element")
    @Test(groups = {"Regression"}, testName = "Test visibility of Element")
    public void TestVisibilityOfDynamicElement(){
        try {
            dynamicLoadingPage.clickStartButton();
            String text = dynamicLoadingPage.DynamicElementVisibility();
            Assert.assertTrue(text.equals("Hello World!"), "Dynamic element is not visible");
        }
        catch (Exception e){
            Assert.assertTrue(false, "Dynamic element is not visible");
        }

    }
}
