package mercury.pageobject.web.portal.jobs.refrigerant;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

import static mercury.helpers.Constants.MAX_TIMEOUT;

public class PortalLeakSiteInformationPartial extends Base_Page<PortalLeakSiteInformationPartial> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "US Gas Regulations Refrigernat Leak Site Information Questions Partial";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";

    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[@id='resourceAssignmentUpdateUIRoot']";

    private static final String UPDATE_JOB_FORM_XPATH = PAGE_MAIN_CONTENT_XPATH + "//form[@id='updateJobForm']";
    private static final String US_GAS_REGULATIONS_XPATH = UPDATE_JOB_FORM_XPATH + "//us-gas-regulations";
    private static final String US_GAS_QUESTIONS_FORM_XPATH = US_GAS_REGULATIONS_XPATH + "//us-gas-leak-check-questions";

    private static final String ADD_REFRIGERANT_LEAK_SITE_XPATH = US_GAS_QUESTIONS_FORM_XPATH + "//div[contains(@class, 'btn-primary')]//i[contains(@class, 'icons__plus')]";

    private static final String REFRIGERANT_SOURCE_GRID_XPATH = US_GAS_REGULATIONS_XPATH + "//div[@id='LeakSiteData' and contains(@class, 'table')]";
    private static final String REFRIGERANT_SOURCE_GRID_HEADERS_CSS = "div.gas-section__gas-source-header-row div";

    private static final String LEAK_SITE_CHECK_FOLLOW_UP_XPATH =  US_GAS_QUESTIONS_FORM_XPATH + "//div[contains(@class, 'btn') and contains(text(), 'Follow Up')]";


    @FindBy(xpath = ADD_REFRIGERANT_LEAK_SITE_XPATH)
    private WebElement addLeakSite;

    @FindBy(xpath = LEAK_SITE_CHECK_FOLLOW_UP_XPATH)
    private List<WebElement> leakSiteCheckFollowUp;


    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(ADD_REFRIGERANT_LEAK_SITE_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    @Override
    public String getPageTitle() {
        return super.getPageTitle();
    }

    public PortalLeakSiteInformationPartial(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    public void addLeakSiteInformation() {
        addLeakSite.click();
        waitForAngularRequestsToFinish();
    }

    public Grid getGasGrid() {
        return GridHelper.getRowDivGrid(REFRIGERANT_SOURCE_GRID_XPATH, REFRIGERANT_SOURCE_GRID_HEADERS_CSS);
    }

    public boolean isLeakSiteCheckFollowUpDisplayed() {
        return isElementVisible(By.xpath(LEAK_SITE_CHECK_FOLLOW_UP_XPATH));
    }

    public int getleakSiteCheckFollowUpCount() {
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        int size = leakSiteCheckFollowUp.size();
        driver.manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        return size;
    }

    public void followUpLeakSiteCheck(int checkNumber) {
        leakSiteCheckFollowUp.get(checkNumber - 1).click();
        waitForAngularRequestsToFinish();
        //return PageFactory.initElements(driver, AdminLeakSiteInformationModal.class).get();
    }

}
