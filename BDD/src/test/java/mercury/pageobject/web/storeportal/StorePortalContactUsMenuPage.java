package mercury.pageobject.web.storeportal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class StorePortalContactUsMenuPage extends Base_Page<StorePortalContactUsMenuPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String CONTACT_US_CSS = "div.cdk-overlay-container";
    private static final String PAGE_TITLE = "Contact Us";

    private static final String CONTACT_DETAILS_GRID = "//mat-table[@role='grid']";
    private static final String CONTACT_US_NUMBER_XPATH = CONTACT_DETAILS_GRID + "//mat-cell[contains(text(), 'Helpdesk')]/following-sibling::mat-cell[2]";
    private static final String CONTACT_US_ROLE_XPATH = CONTACT_DETAILS_GRID + "//mat-cell[contains(text(), '%s')]/following-sibling::mat-cell";

    public StorePortalContactUsMenuPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.cssSelector(CONTACT_US_CSS));
            logger.info(PAGE_TITLE + "Page loaded");
        } catch (Exception e){
            logger.info(PAGE_TITLE + "Page failed to load");
            throw new AssertionError();
        }
    }

    public String getContactUsNumber() {
        WebElement detail = driver.findElement(By.xpath(CONTACT_US_NUMBER_XPATH));
        return detail.getText();
    }

    public String getContactUsRole(String role) {
        WebElement detail = driver.findElement(By.xpath(String.format(CONTACT_US_ROLE_XPATH, role)));
        return detail.getText();
    }

}
