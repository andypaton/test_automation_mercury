package mercury.pageobject.web.portal.quotes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class QuoteAwaitingResourceQuotePage extends Base_Page<QuoteAwaitingResourceQuotePage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "Quote Awaiting Resource Quote(s)";

    private static final String BODY_XPATH = "//div[@class='body-content']";

    private static final String FIELD_XPATH = BODY_XPATH + "//label[contains(text(), '%s')]/following-sibling::div";


    public QuoteAwaitingResourceQuotePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(BODY_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    public Boolean isFieldDisplayed(String fieldName) {
        WebElement field = driver.findElement(By.xpath(String.format(FIELD_XPATH, fieldName)));
        return field.isDisplayed();
    }

}
