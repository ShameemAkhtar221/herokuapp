package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utils.ConfigReader;

public class CheckBoxPage extends BasePage {

    @FindBy(css = "input:nth-child(1)")
    private WebElement checkBoxField;

    public CheckBoxPage(WebDriver driver) {
        super(driver);
    }

    public void open(){
        driver.get(ConfigReader.getProperty("baseUrl") + "/checkboxes");
    }

    public void clickCheckBox() {
        if(!checkBoxField.isSelected()){
            checkBoxField.click();
        }
    }

    public Boolean VerifyCheckboxIsSelected() {
        return checkBoxField.isSelected();

    }



}
