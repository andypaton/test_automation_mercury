package mercury.pageobject.web.portal.quotes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class FundingRouteModal extends Base_Page<FundingRouteModal> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Alternative funding route for Job";

    // Page elements

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";

    // Modal popup
    private static final String ALT_FUNDING_ROUTET_MODAL_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[@id='alternativeFundingRouteModal']";
    private static final String PAGE_MODAL_HEADER_XPATH = ALT_FUNDING_ROUTET_MODAL_XPATH + "//div[contains(@class, 'modal-header')]";
    private static final String PAGE_MODAL_BODY_XPATH = ALT_FUNDING_ROUTET_MODAL_XPATH + "//div[contains(@class, 'modal-body')]";
    private static final String PAGE_MODAL_FOOTER_XPATH = ALT_FUNDING_ROUTET_MODAL_XPATH + "//div[contains(@class, 'modal-footer')]";

    // Core
    private static final String MODAL_HEADER_XPATH = PAGE_MODAL_HEADER_XPATH + "//h4[contains(text(), '" + PAGE_TITLE + "')]";

    private static final String ALT_FUNDING_ROUTE_XPATH = PAGE_MODAL_BODY_XPATH + "//label[contains(text(),'Alternative funding route')]/..//select";

    private static final String CLOSE_BUTTON = PAGE_MODAL_FOOTER_XPATH + "//button[contains(text(),'Close')]";
    private static final String SAVE_BUTTON_XPATH = PAGE_MODAL_FOOTER_XPATH + "//button[contains(text(),'Save')]";

    @FindBy(xpath = ALT_FUNDING_ROUTE_XPATH)
    private WebElement altFundingRoute;

    @FindBy(xpath = CLOSE_BUTTON)
    private WebElement close;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement save;

    public FundingRouteModal(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(MODAL_HEADER_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    public String selectRandomAltFundingRoute() {
        selectRandomOptionFromSelect(altFundingRoute);
        return selectRandomOptionFromSelect(altFundingRoute);
    }

    public void save() {
        this.save.click();
    }

    public void close() {
        this.close.click();
    }
}
