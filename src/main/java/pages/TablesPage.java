package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utils.ConfigReader;

import java.util.List;

public class TablesPage extends BasePage {

    private static final By LAST_NAMES = By.cssSelector("#table1 tbody tr td:nth-child(1)");

    @FindBy(css = "#table1 thead th:nth-child(1)")
    private WebElement lastNameHeader;

    public TablesPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open sortable data tables page")
    public void open() {
        driver.get(ConfigReader.getProperty("baseUrl") + "/tables");
    }

    // The table sorter reorders the rows asynchronously after the click, so wait until they are in order.
    @Step("Sort first table by last name")
    public void sortByLastName() {
        click(lastNameHeader);
        wait.until(d -> {
            List<String> lastNames = getLastNames();
            return lastNames.equals(lastNames.stream().sorted().toList());
        });
    }

    public List<String> getLastNames() {
        return driver.findElements(LAST_NAMES).stream().map(WebElement::getText).toList();
    }
}
