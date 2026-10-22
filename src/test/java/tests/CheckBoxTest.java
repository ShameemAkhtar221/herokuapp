package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CheckBoxPage;

@Epic("the-internet")
@Feature("Checkboxes")
public class CheckBoxTest extends BaseTest{

    public CheckBoxPage checkBoxPage;

    @BeforeMethod(alwaysRun = true)
    public void initPages(){
        checkBoxPage = new CheckBoxPage(driver);
        checkBoxPage.open();
    }
    @Severity(SeverityLevel.NORMAL)
    @Description("Clicking the first checkbox selects it")
    @Test(groups = {"Regression"}, testName = "Click on the checkbox")
    public void CheckBox(){
        checkBoxPage.clickCheckBox();
        Assert.assertTrue(checkBoxPage.VerifyCheckboxIsSelected());
    }
}
