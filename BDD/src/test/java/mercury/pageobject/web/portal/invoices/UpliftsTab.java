package mercury.pageobject.web.portal.invoices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class UpliftsTab extends Base_Page<UpliftsTab>{
    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Uplifts";

    private static final String INVOICE_DETAILS_XPATH = "//div[@class='inv-details']";
    private static final String UPLIFTS_XPATH = INVOICE_DETAILS_XPATH + "//div[@id='tab4']/div[@id='uplifts']";
    private static final String VIEW_BUTTON_XPATH = UPLIFTS_XPATH + "/div[@class='uplifts-buttons']/input";

    public UpliftsTab(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(UPLIFTS_XPATH));
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


    @FindBy(xpath = UPLIFTS_XPATH)
    private WebElement uplifts;

    @FindBy(xpath = VIEW_BUTTON_XPATH)
    private WebElement viewButton;

    public String getUplifts() {
        return uplifts.getText();
    }

}
