package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.AddRemoveElementsPage;
import pages.HomePage;
import pages.HoversPage;
import pages.TablesPage;

import java.util.List;


@Epic("the-internet")
@Feature("Visual")
public class VisualTest extends VisualBaseTest {

    private HomePage homePage;
    private AddRemoveElementsPage addRemoveElementsPage;
    private HoversPage hoversPage;
    private TablesPage tablesPage;

    @BeforeMethod(alwaysRun = true)
    public void initPages() {
        homePage = new HomePage(driver);
        addRemoveElementsPage = new AddRemoveElementsPage(driver);
        hoversPage = new HoversPage(driver);
        tablesPage = new TablesPage(driver);
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Home page looks the same as its baseline")
    @Test(groups = {"Visual"}, testName = "Home page visual")
    public void homePage() {
        homePage.open();
        Assert.assertTrue(homePage.isLoaded(), "Home page did not load");
        Assert.assertEquals(homePage.getHeading(), "Welcome to the-internet");

        assertMatchesBaseline("home-page");
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Add/Remove Elements page with three added Delete buttons looks the same as its baseline")
    @Test(groups = {"Visual"}, testName = "Add/remove elements visual")
    public void addRemoveElements() {
        addRemoveElementsPage.open();
        addRemoveElementsPage.addElements(3);
        Assert.assertEquals(addRemoveElementsPage.getDeleteButtonCount(), 3);

        assertMatchesBaseline("add-remove-3-elements");
    }

    @Severity(SeverityLevel.MINOR)
    @Description("Hovering over a user shows its caption, matching the baseline")
    @Test(groups = {"Visual"}, testName = "Hover caption visual")
    public void hoverCaption() {
        hoversPage.open();
        hoversPage.hoverOverUser(1);
        Assert.assertEquals(hoversPage.getCaption(1), "name: user1");

        assertMatchesBaseline("hover-user-1", hoversPage.getFigure(1));
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Table sorted by last name looks the same as its baseline")
    @Test(groups = {"Visual"}, testName = "Sorted table visual")
    public void sortedTable() {
        tablesPage.open();
        tablesPage.sortByLastName();
        List<String> lastNames = tablesPage.getLastNames();
        Assert.assertEquals(lastNames, lastNames.stream().sorted().toList(), "Last names are not sorted");

        assertMatchesBaseline("tables-sorted-by-last-name");
    }
}
