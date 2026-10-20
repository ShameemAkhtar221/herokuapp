package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CheckBoxPage;

public class CheckBoxTest extends BaseTest{

    public CheckBoxPage checkBoxPage;

    @BeforeMethod(alwaysRun = true)
    public void initPages(){
        checkBoxPage = new CheckBoxPage(driver);
        checkBoxPage.open();
    }
    @Test(groups = {"Regression"}, testName = "Click on the checkbox")
    public void CheckBox(){
        checkBoxPage.clickCheckBox();
        Assert.assertTrue(checkBoxPage.VerifyCheckboxIsSelected());
    }
}
