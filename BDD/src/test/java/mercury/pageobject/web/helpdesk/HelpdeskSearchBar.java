package mercury.pageobject.web.helpdesk;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.MAX_TIMEOUT;
import static org.awaitility.Awaitility.with;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.helpdesk.caller.HelpdeskCallerPage;
import mercury.pageobject.web.helpdesk.incidents.HelpdeskIncidentMonitorPage;
import mercury.pageobject.web.helpdesk.incidents.HelpdeskViewIncidentPage;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskAssetPage;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskJobPage;
import mercury.pageobject.web.helpdesk.resources.HelpdeskResourcePage;

public class HelpdeskSearchBar extends Base_Page<HelpdeskSearchBar> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_CSS = ".active";
    protected static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";

    // SEARCH BAR
    private static final String SEARCHBOX_CSS = ACTIVE_WORKSPACE_CSS + " .search-box  > input";
    private static final String SEARCHING_XPATH = ACTIVE_WORKSPACE_XPATH + "//p[contains(text(), 'Searching') and @aria-hidden='false']";
    private static final String SEARCH_RESULT_CSS = ACTIVE_WORKSPACE_CSS + " div.search-container__results-wrapper ";
    private static final String SEARCH_RESULTS_CSS = ACTIVE_WORKSPACE_CSS + " div.search-container__results-wrapper li";
    private static final String MATCHED_ITEM_XPATH = "//div[@class='search-container__results-wrapper']//li/a[contains(@data-ng-click, 'matchedItem')]";
    private static final String SEARCH_RESULT_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[contains(@class,'search-container__results')]//li//text()[contains(., \"%s\")]/..";
    private static final String NO_RESULTS_FOUND_XPATH = "//div[@class='search-container__results-wrapper']//p[contains(text(), 'Sorry, no matching results were found.') and contains(@aria-hidden, 'false')]";
    private static final String SHOW_MORE_CSS = ACTIVE_WORKSPACE_CSS +" div.search-container__results-wrapper > div .icons__chevron-down";
    private static final String LOZENGE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]//a[contains(., '%s')]";
    private static final String MONITOR_TITLE_CONTAINER = ACTIVE_WORKSPACE_XPATH + "//div[@class='monitors__tile-container']";
    private static final String INCIDENTS_MONITOR_XPATH = MONITOR_TITLE_CONTAINER + "//div//p[@class='monitors__tile--title' and text()='Incidents']";
    private static final String INCIDENTS_ACCESS_ERROR_MESSAGE = ACTIVE_WORKSPACE_XPATH + "//div[@ng-if='ctrlActions.incidentModel.authorisationFailed']";
    private static final String SEARCH_RESULT_WITH_ICON_XPATH = SEARCH_RESULT_XPATH + "//i[contains(@class,'icons__exclamation-triangle')]";


    @FindBy(css=SEARCHBOX_CSS)
    private WebElement searchBox;

    @FindBy(css=SEARCH_RESULTS_CSS)
    private List<WebElement> searchResults;

    @FindBy(xpath=NO_RESULTS_FOUND_XPATH)
    private WebElement noResultsFound;

    @FindBy(xpath=INCIDENTS_MONITOR_XPATH)
    private WebElement incidentMonitor;

    @FindBy(xpath=INCIDENTS_ACCESS_ERROR_MESSAGE)
    private WebElement incidentAccessErrorMessage;


    public HelpdeskSearchBar(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.cssSelector(SEARCHBOX_CSS)).isDisplayed());
            logger.info("Page loaded");

        } catch(NoSuchElementException ex){

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public HelpdeskSearchBar search(String searchStr) {
        enterMatchingSearch(searchStr);
        return new HelpdeskSearchBar(driver);
    }

    public HelpdeskCallerPage searchForCaller(String caller) {
        enterMatchingSearch(caller);
        return PageFactory.initElements(driver, HelpdeskCallerPage.class).get();
    }

    public HelpdeskResourcePage searchForResource(String resource) {
        enterMatchingSearch(resource);
        return PageFactory.initElements(driver, HelpdeskResourcePage.class).get();
    }

    public void searchFor(String type, String searchStr, int maxAttempts) {
        logger.debug("Searching for " + type + ": " + searchStr);
        int attempt = 0;
        boolean found = searchForMatchingSearchWithoutRemovingBraces(searchStr, type, attempt);
        while (!found && attempt < maxAttempts) {
            searchBox.sendKeys(Keys.CLEAR);
            driver.navigate().refresh();
            attempt++;
            found = searchForMatchingSearchWithoutRemovingBraces(searchStr, type, attempt);
        }
        waitForAngularRequestsToFinish();
        if (found) {
            // if search result found then select it
            WebElement visibleOption = driver.findElement(By.xpath(String.format(SEARCH_RESULT_XPATH, searchStr)));
            POHelper.clickJavascript(visibleOption);
        }
    }

    public HelpdeskSitePage searchForSite(String siteName) {
        enterMatchingSearch(siteName);
        return PageFactory.initElements(driver, HelpdeskSitePage.class).get();
    }

    public HelpdeskJobPage searchForJob(String jobReference) {
        enterMatchingSearch(jobReference, "wrench");
        return PageFactory.initElements(driver, HelpdeskJobPage.class).get();
    }

    public HelpdeskJobPage searchForJob(Integer jobReference) {
        return searchForJob(String.valueOf(jobReference));
    }

    public HelpdeskAssetPage searchForAssetSubtype(String assetSubtype) {
        if (assetSubtype.contains(">")) {
            String parts[] = assetSubtype.split(" > ");
            searchBox.sendKeys(parts[1]);
            waitForAngularRequestsToFinish();

            // expand 'show more' if present
            if (isElementPresent(By.cssSelector(SHOW_MORE_CSS))) {
                driver.findElement(By.cssSelector(SHOW_MORE_CSS)).click();
                waitForAngularRequestsToFinish();
            }

            WebElement visibleOption = driver.findElement(By.xpath(String.format(SEARCH_RESULT_XPATH, assetSubtype)));
            POHelper.clickJavascript(visibleOption);
        } else {
            enterMatchingSearch(assetSubtype);
        }
        return PageFactory.initElements(driver, HelpdeskAssetPage.class).get();
    }

    private void enterMatchingSearch(String searchStr) {
        logger.debug("Searching for: " + searchStr);
        int attempt = 0;
        boolean found = searchForMatchingSearch(searchStr, attempt);;
        while (!found && attempt < 15) {
            searchBox.sendKeys(Keys.CLEAR);
            driver.navigate().refresh();
            attempt++;
            found = searchForMatchingSearch(searchStr, attempt);
        }
        waitForAngularRequestsToFinish();
        WebElement visibleOption = driver.findElement(By.xpath(String.format(SEARCH_RESULT_XPATH, searchStr)));
        POHelper.clickJavascript(visibleOption);
    }

    private void enterMatchingSearch(String searchStr, String type) {
        logger.debug("Searching for: " + searchStr);
        int attempt = 1;
        boolean found = searchForMatchingSearch(searchStr, type);
        while (!found && attempt < 15) {
            searchBox.sendKeys(Keys.CLEAR);
            driver.navigate().refresh();
            waitForAngularRequestsToFinish();
            attempt++;
            found = searchForMatchingSearch(searchStr, type);
        }
        String debugStr = found ? "Found" : "Not found";
        logger.debug(debugStr + ", attempt " + attempt);
        waitForAngularRequestsToFinish();
        String searchItemXpath = SEARCH_RESULT_XPATH + "//i[contains(@class, 'icons__" + type + "')]";
        WebElement visibleOption = driver.findElement(By.xpath(String.format(searchItemXpath, searchStr)));
        logger.debug("Selecting " + searchStr);
        POHelper.clickJavascript(visibleOption);
    }

    private void enterMatchingSeachForIncident(String searchStr) {
        logger.debug("Searching for: " + searchStr);
        int attempt = 1;
        boolean found = searchForMatchingSearch(searchStr, attempt);
        with().await().pollInterval(250, MILLISECONDS).atMost(MAX_TIMEOUT, SECONDS).until(() -> found, equalTo(true));
        waitForAngularRequestsToFinish();
        WebElement visibleOption = driver.findElement(By.xpath(String.format(SEARCH_RESULT_WITH_ICON_XPATH, searchStr)));
        POHelper.clickJavascript(visibleOption);
    }

    private boolean searchForMatchingSearch(String searchStr, int attempt) {
        // in searchbox only enter substring pre '(' and '-'
        String str = searchStr.contains("(") ? searchStr.split("\\(")[0].trim() : searchStr;
        str = str.contains(" - ") ? StringUtils.substringBefore(str, " - ") : str;
        searchBox.sendKeys(str);

        if (str.length() < 2) {
            searchBox.sendKeys(" ");
        }

        waitForElement(By.cssSelector(SEARCH_RESULT_CSS), State.ELEMENT_IS_VISIBLE);
        waitUntilElementCountToBe(By.xpath(SEARCHING_XPATH), 0);

        if (isElementPresent(By.cssSelector(SHOW_MORE_CSS))) {
            driver.findElement(By.cssSelector(SHOW_MORE_CSS)).click();
            waitForAngularRequestsToFinish();
        }

        if (isElementPresent(By.xpath(MATCHED_ITEM_XPATH))) {
            return true;
        }
        return false;
    }

    private boolean searchForMatchingSearch(String searchStr, String type) {
        // in searchbox only enter substring pre '(' and '-'
        String str = searchStr.contains("(") ? searchStr.split("\\(")[0].trim() : searchStr;
        str = str.contains(" - ") ? StringUtils.substringBefore(str, " - ") : str;

        POHelper.sendKeys(searchBox, str);

        if (str.length() < 2) {
            searchBox.sendKeys(" ");
        }

        waitForElement(By.cssSelector(SEARCH_RESULT_CSS), State.ELEMENT_IS_VISIBLE);
        waitUntilElementCountToBe(By.xpath(SEARCHING_XPATH), 0);

        String xpath = MATCHED_ITEM_XPATH + "/i[contains(@class, 'icons__" + type + "')]";

        if (isElementPresent(By.cssSelector(SHOW_MORE_CSS))) {
            driver.findElement(By.cssSelector(SHOW_MORE_CSS)).click();
            waitForAngularRequestsToFinish();
        }

        if (isElementPresent(By.xpath(xpath))) {
            return true;
        }
        return false;
    }

    private boolean searchForMatchingSearchWithoutRemovingBraces(String searchStr, String type, int attempt) {
        searchBox.sendKeys(searchStr);

        waitForElement(By.cssSelector(SEARCH_RESULT_CSS), State.ELEMENT_IS_VISIBLE);
        waitUntilElementCountToBe(By.xpath(SEARCHING_XPATH), 0);

        String xpath = MATCHED_ITEM_XPATH + "/i[contains(@class, 'icons__" + type + "')]";

        if (isElementPresent(By.cssSelector(SHOW_MORE_CSS))) {
            driver.findElement(By.cssSelector(SHOW_MORE_CSS)).click();
            waitForAngularRequestsToFinish();
        }

        if (isElementPresent(By.xpath(xpath))) {
            return true;
        }
        return false;
    }

    public void enterNonMatchingSearch(String searchStr) {
        logger.debug("Searching for: " + searchStr);
        searchBox.sendKeys(searchStr);
        noResultsFound.isDisplayed();
        searchBox.sendKeys(Keys.ENTER);
    }

    public boolean isNoResultsFound() {
        return isElementPresent(By.xpath(NO_RESULTS_FOUND_XPATH));
    }

    public void enterSearchString(String searchStr) {
        searchBox.sendKeys(searchStr);
    }

    public boolean isLozengeDisplayed(String title) {
        return driver.findElement(By.xpath(String.format(LOZENGE_XPATH, title))).isDisplayed();
    }

    public HelpdeskViewIncidentPage searchForAnIncident(Integer incidentReference) {
        enterMatchingSeachForIncident(String.valueOf(incidentReference));
        return PageFactory.initElements(driver, HelpdeskViewIncidentPage.class).get();
    }

    public void searchForIncident(Integer incidentReference) {
        enterMatchingSeachForIncident(String.valueOf(incidentReference));
    }

    public HelpdeskIncidentMonitorPage clickIncidentMonitor() {
        incidentMonitor.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskIncidentMonitorPage.class).get();
    }

    public boolean IsIncidentsMonitorDisplayed() {
        return isElementPresent(By.xpath(INCIDENTS_MONITOR_XPATH));
    }

    public String getIncidentErrorMessage() {
        return incidentAccessErrorMessage.getText();
    }

    public List<String> getSearchResults() {
        List<String> results = new ArrayList<>();

        if (isElementPresent(By.cssSelector(SHOW_MORE_CSS))) {
            driver.findElement(By.cssSelector(SHOW_MORE_CSS)).click();
            waitForElement(By.xpath(MATCHED_ITEM_XPATH), State.ELEMENT_IS_VISIBLE);
        }

        for (WebElement we : searchResults) {
            results.add(we.getText());
        }
        return results;
    }

    public List<String> getSearchResults(String type) {
        List<String> results = new ArrayList<>();

        if (isElementPresent(By.cssSelector(SHOW_MORE_CSS))) {
            driver.findElement(By.cssSelector(SHOW_MORE_CSS)).click();
            waitForAngularRequestsToFinish();
        }

        List<WebElement> searchResults = driver.findElements(By.cssSelector(SEARCH_RESULTS_CSS + " icons__" + type));
        for (WebElement we : searchResults) {
            results.add(we.getText());
        }
        return results;
    }
}
