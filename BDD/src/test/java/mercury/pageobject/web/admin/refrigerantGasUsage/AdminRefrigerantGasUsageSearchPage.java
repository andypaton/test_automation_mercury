package mercury.pageobject.web.admin.refrigerantGasUsage;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.pageobject.web.Base_Page;

public class AdminRefrigerantGasUsageSearchPage extends Base_Page<AdminRefrigerantGasUsageSearchPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String EDIT_GAS_USAGE_XPATH = "//div[@ng-app='editGasUsageAdmin']";

    private static final String SEARCHBOX_XPATH = EDIT_GAS_USAGE_XPATH + "//label[text() = 'Job reference:']/following-sibling::input";
    private static final String SEARCH_BUTTON_XPATH = EDIT_GAS_USAGE_XPATH + "//button[text() = 'Search']";

    // visit table
    private static final String FGAS_ADMIN_VISITS_XPATH = EDIT_GAS_USAGE_XPATH + "//div[@id = 'fgas-admin-visits' and @aria-hidden = 'false']";
    private static final String VIEW_BUTTONS_XPATH = FGAS_ADMIN_VISITS_XPATH + "//div[@class = 'col-md-3']//button";
    private static final String VISIT_CELLS_XPATH = FGAS_ADMIN_VISITS_XPATH + "//div[@class = 'col-md-3']";

    @FindBy(xpath = SEARCHBOX_XPATH)
    private WebElement searchBox;

    @FindBy(xpath = SEARCH_BUTTON_XPATH)
    private WebElement searchButton;

    @FindBy(xpath = VIEW_BUTTONS_XPATH)
    private List<WebElement> viewButtons;

    @FindBy(xpath = VISIT_CELLS_XPATH)
    private List<WebElement> visitCells;


    public AdminRefrigerantGasUsageSearchPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(SEARCHBOX_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void enterJobReference(Integer jobReference) {
        searchBox.sendKeys(String.valueOf(jobReference));
    }

    public AdminRefrigerantGasUsageEditPage search() {
        searchButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminRefrigerantGasUsageEditPage.class).get();
    }

    public List<String> getVisitDates(){
        List<String> dates = new ArrayList<>();
        for (int i = 0; i < visitCells.size(); i = i + 4) {
            dates.add(visitCells.get(i).getText());
        }
        return dates;
    }

    public List<String> getVisitResources(){
        List<String> resources = new ArrayList<>();
        for (int i = 1; i < visitCells.size(); i = i + 4) {
            resources.add(visitCells.get(i).getText());
        }
        return resources;
    }

    public List<String> getVisitAssets(){
        List<String> assets = new ArrayList<>();
        for (int i = 2; i < visitCells.size(); i = i + 4) {
            assets.add(visitCells.get(i).getText());
        }
        return assets;
    }

    public void viewVisit(int row){
        viewButtons.get(row).click();
        waitForAngularRequestsToFinish();
    }

    public void viewVisit(String visitDate) {
        int row = 0;
        for (int i = 0; i < visitCells.size(); i = i + 4) {
            if (visitCells.get(i).getText().contains(visitDate)) {
                viewButtons.get(row).click();
                break;
            }
            row++;
        }
        waitForAngularRequestsToFinish();
    }

    public boolean multipleVisitsDisplayed() {
        return isElementPresent(By.xpath(FGAS_ADMIN_VISITS_XPATH));
    }

    public boolean isDisplayed() {
        return isElementPresent(By.xpath(SEARCHBOX_XPATH));
    }
}
