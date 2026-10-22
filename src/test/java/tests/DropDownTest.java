package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.DropDownPage;
import utils.ConfigReader;

@Epic("the-internet")
@Feature("Dropdown")
public class DropDownTest extends BaseTest {

    private DropDownPage dropDownPage;

    @BeforeMethod(alwaysRun = true)
    public void initPages() {
        dropDownPage = new DropDownPage(driver);
        dropDownPage.open();
        Assert.assertTrue(dropDownPage.isLoaded(), "Dropdown page did not load");
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Option 1 can be selected from the dropdown")
    @Test(groups = {"Regression"}, testName = "Select dropdown option 1")
    public void selectOption1() {
            dropDownPage.selectByVisibleText(ConfigReader.getProperty("option1"));
            Assert.assertEquals(dropDownPage.getSelectedOption(), "Option 1");
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Option 2 can be selected from the dropdown")
    @Test(groups = {"Regression"}, testName = "Select dropdown option 2")
    public void selectOption2() {
        dropDownPage.selectByVisibleText(ConfigReader.getProperty("option2"));
        Assert.assertEquals(dropDownPage.getSelectedOption(), "Option 2");
    }
}
