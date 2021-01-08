package mercury.pageobject.web.portal.invoices;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class ClosedownTab extends Base_Page<ClosedownTab>{
    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Closedown";

    private static final String CLOSEDOWN_TABLE_HEADERS_XPATH = "//th";

    private static final String JOB_TYPE_XPATH = "//th[text() = 'Job Type']/following-sibling::td";
    private static final String FIRST_ARRIVAL_DATE_XPATH = "//th[text() = 'First Arrival Date/Time']/following-sibling::td";
    private static final String JOB_COMPLETED_DATE_XPATH = "//th[text() = 'Job Completed Date/Time']/following-sibling::td";
    private static final String TOTAL_ONSITE_HOURS_XPATH = "//th[text() = 'Total Onsite Hours']/following-sibling::td";
    private static final String SUPPLIER_NOTES_XPATH = "//th[text() = 'Supplier Notes']/following-sibling::td";
    private static final String ARRIVAL_DATE_XPATH = "//th[text() = 'Arrival Date/Time']//ancestor::table//tbody/tr[%d]/td[1]";
    private static final String ONSITE_HOURS_XPATH = "//th[text() = 'Onsite Hours']//ancestor::table//tbody/tr[%d]/td[2]";


    @FindBy(xpath = JOB_TYPE_XPATH)
    private WebElement jobType;

    @FindBy(xpath = FIRST_ARRIVAL_DATE_XPATH)
    private WebElement firstArrivalDate;

    @FindBy(xpath = JOB_COMPLETED_DATE_XPATH)
    private WebElement jobCompletedDate;

    @FindBy(xpath = TOTAL_ONSITE_HOURS_XPATH)
    private WebElement totalOnsiteHours;

    @FindBy(xpath = SUPPLIER_NOTES_XPATH)
    private WebElement supplierNotes;

    @FindBy(xpath = CLOSEDOWN_TABLE_HEADERS_XPATH)
    private List<WebElement> closedownTableHeaders;

    public ClosedownTab(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(JOB_TYPE_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + "isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public String getJobType() {
        return jobType.getText();
    }

    public String getFirstArrivalDate() {
        return firstArrivalDate.getText();
    }

    public String getJobCompletedDate() {
        return jobCompletedDate.getText();
    }

    public String getTotalOnsiteHours() {
        return totalOnsiteHours.getText();
    }

    public String getSupplierNotes() {
        return supplierNotes.getText();
    }

    public String getArrivalDate(int index) {
        WebElement we = driver.findElement(By.xpath(String.format(ARRIVAL_DATE_XPATH, index)));
        return we.getText();
    }

    public String getOnsiteHours(int index) {
        WebElement we = driver.findElement(By.xpath(String.format(ONSITE_HOURS_XPATH, index)));
        return we.getText();
    }

}
