package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.DropDownPage;
import utils.ConfigReader;

public class DropDownTest extends BaseTest {

    private DropDownPage dropDownPage;

    @BeforeMethod(alwaysRun = true)
    public void initPages() {
        dropDownPage = new DropDownPage(driver);
        dropDownPage.open();
        Assert.assertTrue(dropDownPage.isLoaded(), "Dropdown page did not load");
    }

    @Test(groups = {"Regression"}, testName = "Select dropdown option 1")
    public void selectOption1() {
            dropDownPage.selectByVisibleText(ConfigReader.getProperty("option1"));
            Assert.assertEquals(dropDownPage.getSelectedOption(), "Option 1");
    }

    @Test(groups = {"Regression"}, testName = "Select dropdown option 2")
    public void selectOption2() {
        dropDownPage.selectByVisibleText(ConfigReader.getProperty("option2"));
        Assert.assertEquals(dropDownPage.getSelectedOption(), "Option 2");
    }
}
