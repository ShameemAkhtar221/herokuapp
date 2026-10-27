package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import utils.ConfigReader;

import java.util.List;

public class HoversPage extends BasePage {

    @FindBy(css = ".figure")
    private List<WebElement> figures;

    public HoversPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open hovers page")
    public void open() {
        driver.get(ConfigReader.getProperty("baseUrl") + "/hovers");
    }

    // index is 1-based, matching the "user1", "user2"... captions
    @Step("Hover over user {index}")
    public void hoverOverUser(int index) {
        WebElement figure = getFigure(index);
        new Actions(driver).moveToElement(waitForVisibilityOfElement(figure)).perform();
        waitForVisibilityOfElement(figure.findElement(By.cssSelector(".figcaption")));
    }

    public WebElement getFigure(int index) {
        return figures.get(index - 1);
    }

    public String getCaption(int index) {
        return getText(getFigure(index).findElement(By.cssSelector(".figcaption h5")));
    }
}
