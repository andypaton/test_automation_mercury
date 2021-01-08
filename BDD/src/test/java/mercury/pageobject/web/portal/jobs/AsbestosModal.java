package mercury.pageobject.web.portal.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class AsbestosModal extends Base_Page<AsbestosModal>{

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "Start Job";

    private static final String MODAL_XPATH = "//div[contains(@class, 'modal')]";
    private static final String MODAL_CONTENT_XPATH = MODAL_XPATH + "//div[@class='modal-content']";
    private static final String MODAL_HEADER_XPATH = MODAL_CONTENT_XPATH + "//div[@class='modal-header']";
    private static final String MODAL_BODY_XPATH = MODAL_CONTENT_XPATH + "//div[@class='modal-body']";
    private static final String MODAL_FOOTER_XPATH = MODAL_CONTENT_XPATH + "//div[@class='modal-footer']";

    private static final String MODAL_TITLE_XPATH = MODAL_HEADER_XPATH + "//h4[contains(text(), '" + PAGE_TITLE + "')]";
    private static final String ASBESTOS_CHECKED_XPATH = MODAL_BODY_XPATH + "//label[contains(text(), 'Yes')]";

    private static final String CANCEL_BUTTON_XPATH = MODAL_FOOTER_XPATH + "//button[contains(text(), 'Cancel')]";
    private static final String OK_BUTTON_XPATH = MODAL_FOOTER_XPATH + "//button[contains(text(), 'Ok')]";


    @FindBy(xpath = CANCEL_BUTTON_XPATH)
    private WebElement cancel;

    @FindBy(xpath = OK_BUTTON_XPATH)
    private WebElement ok;

    public AsbestosModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try{
            waitForAngularRequestsToFinish();
            waitForElement(By.xpath(MODAL_XPATH), State.ELEMENT_IS_VISIBLE, 5);
            Assert.assertTrue("Modal is not displayed", driver.findElement(By.xpath(MODAL_TITLE_XPATH)).isDisplayed());
            logger.info(PAGE_TITLE + " Page loaded");
        }catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public Boolean isPageLoaded() {
        waitForAngularRequestsToFinish();
        return this.isElementClickable(By.xpath(CANCEL_BUTTON_XPATH));
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public void clickOk() {
        ok.click();
        waitForAngularRequestsToFinish();
    }

    public void clickCancel() {
        cancel.click();
        waitForAngularRequestsToFinish();
    }

    public void clickAnswer(String answer) {
        WebElement element = driver.findElement(By.xpath(String.format(ASBESTOS_CHECKED_XPATH, answer)));
        element.click();
        waitForAngularRequestsToFinish();
    }
}
