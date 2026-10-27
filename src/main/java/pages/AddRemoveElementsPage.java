package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.ConfigReader;

public class AddRemoveElementsPage extends BasePage {

    private static final By DELETE_BUTTONS = By.cssSelector("#elements button.added-manually");

    @FindBy(css = "button[onclick='addElement()']")
    private WebElement addElementButton;

    public AddRemoveElementsPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open add/remove elements page")
    public void open() {
        driver.get(ConfigReader.getProperty("baseUrl") + "/add_remove_elements/");
    }

    @Step("Add {count} elements")
    public void addElements(int count) {
        int before = getDeleteButtonCount();
        for (int i = 0; i < count; i++) {
            click(addElementButton);
        }
        wait.until(ExpectedConditions.numberOfElementsToBe(DELETE_BUTTONS, before + count));
    }

    public int getDeleteButtonCount() {
        return driver.findElements(DELETE_BUTTONS).size();
    }
}
