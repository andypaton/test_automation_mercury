package mercury.pageobject.web.portal.invoices;

import java.util.ArrayList;
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

public class JobNotesTab extends Base_Page<JobNotesTab>{
    private static final Logger logger = LogManager.getLogger();
    // Page title
    private static final String PAGE_TITLE = "Job Notes";

    private static final String INVOICE_DETAILS_XPATH = "//div[@class='inv-details']";
    private static final String JOB_NOTES_TABLE_XPATH = INVOICE_DETAILS_XPATH + "//div[@id='tab3']/table[@class='horiz-table']";
    private static final String TABLE_HEADERS_XPATH = "//div[@aria-labelledby = '%s']//table//tr/th";
    private static final String TABLE_DATA_XPATH = "//div[@aria-labelledby = '%s']//table//tr/th[contains(text(), '%s')]/following-sibling::td";
    private static final String JOB_NOTES_TAB_XPATH = INVOICE_DETAILS_XPATH + "//div[@id='info-tables']//ul//li//a[contains(text(), 'Job Notes')]";

    @FindBy(xpath = JOB_NOTES_TAB_XPATH)
    private WebElement jobNotesTab;

    public JobNotesTab(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(JOB_NOTES_TABLE_XPATH));
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

    private List<String> getHeaders(String id) {
        List<String> headers = new ArrayList<>();
        List<WebElement> wes = driver.findElements(By.xpath(String.format(TABLE_HEADERS_XPATH, id)));
        for (WebElement we : wes) {
            headers.add(we.getAttribute("innerText"));
        }
        return headers;
    }

    public List<String> getJobNotesHeaders() {
        String id = jobNotesTab.getAttribute("id");
        return getHeaders(id);
    }

    public String getJobNotes(String title) {
        String id = jobNotesTab.getAttribute("id");
        WebElement we = driver.findElement(By.xpath(String.format(TABLE_DATA_XPATH, id, title)));
        return we.getText();
    }
}
