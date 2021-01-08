package mercury.pageobject.web.helpdesk.monitors;

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

import mercury.pageobject.web.Base_Page;

public class MonitorsToDo extends Base_Page<MonitorsToDo> {

    private static final Logger logger = LogManager.getLogger();

    protected static final String TO_DO_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]//monitors-category-menu//div[contains(text(), 'To Do')]/ancestor::*[@class = 'monitors__menu']";

    private static final String EXPAND_XPATH = TO_DO_XPATH + "//i[contains(@class, 'glyphicon-chevron-right')]";
    private static final String COLLAPSE_XPATH = TO_DO_XPATH + "//i[contains(@class, 'glyphicon-chevron-down')]";

    protected static final String MONITORS_XPATH = TO_DO_XPATH + "//li//div[contains(@class, 'monitors__display-name')]";
    protected static final String MONITOR_XPATH = TO_DO_XPATH + "//div[@class='monitors__display-name' and text()='%s']/ancestor::li";

    protected static final String MY_LIST_COUNT_XPATH = MONITOR_XPATH + "//div[@class='monitors__value-col'][2]";
    protected static final String TEAM_LIST_COUNT_XPATH = MONITOR_XPATH + "//div[@class='monitors__value-col'][1]";

    protected static final String MY_LIST_XPATH = TO_DO_XPATH + "//div[contains(@class,'menu-section--header')]//div[text()='My List']";
    protected static final String TEAM_LIST_XPATH = TO_DO_XPATH + "//div[contains(@class,'menu-section--header')]//div[text()='Team List']";

    protected static final String COLLAPSED_XPATH = TO_DO_XPATH + "/div[contains(@class, 'monitors__menu-section monitors__menu-section--content') and @aria-expanded = 'false']";


    @FindBy(xpath = EXPAND_XPATH)
    WebElement expandToDo;

    @FindBy(xpath = COLLAPSE_XPATH)
    WebElement collapseToDo;

    @FindBy(xpath = MONITORS_XPATH)
    List<WebElement> monitors;


    public MonitorsToDo(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            waitForAngularRequestsToFinish();
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(TO_DO_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch(NoSuchElementException ex){

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public List<String> getMonitors(){
        List<String> monitorNames = new ArrayList<>();
        for (WebElement we : monitors) {
            monitorNames.add(we.getText());
        }
        return monitorNames;
    }

    public void selectMonitor(String monitor) {
        String xpath = String.format(MONITOR_XPATH, monitor);
        driver.findElement(By.xpath(xpath)).click();
        waitForAngularRequestsToFinish();
    }

    public int getMyListCount(String monitor) {
        String xpath = String.format(MY_LIST_COUNT_XPATH, monitor);
        return Integer.valueOf(driver.findElement(By.xpath(xpath)).getText());
    }

    public int getTeamListCount(String monitor) {
        String xpath = String.format(TEAM_LIST_COUNT_XPATH, monitor);
        return Integer.valueOf(driver.findElement(By.xpath(xpath)).getText());
    }

    public boolean isDisplayed() {
        return isElementVisible(By.xpath(TO_DO_XPATH));
    }

    public Boolean collapse() {
        collapseToDo.click();
        waitUntilCollapsed();
        waitForAngularRequestsToFinish();
        return true;
    }

    public boolean isMyListDisplayed() {
        return isElementVisible(By.xpath(MY_LIST_XPATH));
    }

    public boolean isTeamListDisplayed() {
        return isElementVisible(By.xpath(TEAM_LIST_XPATH));
    }

    public void waitUntilCollapsed() {
        driver.findElement(By.xpath(COLLAPSED_XPATH));
    }

    public boolean isMonitorDisplayed(String monitor) {
        return isElementVisible(By.xpath(String.format(MONITOR_XPATH, monitor)));
    }
}
