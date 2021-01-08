package mercury.pageobject.web.portal.ppm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class PPMGasSafetyAdviceNoticeModal extends Base_Page<PPMGasSafetyAdviceNoticeModal> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "Add Gas Safety Advice Notice";

    private static final String MODAL_XPATH = "//div[contains(@class,'modal-dialog')]";
    private static final String MODAL_CONTENT_XPATH = MODAL_XPATH + "//div[@class='modal-content']";
    private static final String MODAL_HEADER_XPATH = MODAL_CONTENT_XPATH + "//h3[@class='modal-title']";
    private static final String MODAL_BODY_XPATH = MODAL_CONTENT_XPATH + "//div[@class='modal-body']";
    private static final String MODAL_FOOTER_XPATH = MODAL_CONTENT_XPATH + "//div[@class='footer-button-bar__buttons-container']";
    private static final String MODAL_TITLE_XPATH = MODAL_CONTENT_XPATH + "//h3[contains(text(), '" + PAGE_TITLE + "')]";

    private static final String GSAN_REFERENCE_NUMBER_XPATH = MODAL_BODY_XPATH + "//input[@id='gsanReferenceNumber']";
    private static final String GSAN_TYPE_XPATH = MODAL_BODY_XPATH + "//span[contains(text(), 'Please select a gas safety advice notice type')]/..//span[@class='k-select']//span[contains(@class, 'k-i-arrow-s')]";

    private static final String OK_BUTTON_XPATH = MODAL_FOOTER_XPATH + "//button[contains(text(), 'Ok')]";
    private static final String CANCEL_BUTTON_XPATH = MODAL_FOOTER_XPATH + "//button[contains(text(), 'Cancel')]";


    @FindBy(xpath = MODAL_HEADER_XPATH)
    private WebElement header;

    @FindBy(xpath = GSAN_REFERENCE_NUMBER_XPATH)
    private WebElement refNo;

    @FindBy(xpath = GSAN_TYPE_XPATH)
    private WebElement type;

    @FindBy(xpath = OK_BUTTON_XPATH)
    private WebElement ok;

    @FindBy(xpath = CANCEL_BUTTON_XPATH)
    private WebElement cancel;


    public PPMGasSafetyAdviceNoticeModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            waitForAngularRequestsToFinish();
            Assert.assertTrue("Modal is not displayed", driver.findElement(By.xpath(MODAL_TITLE_XPATH)).isDisplayed());
            logger.info(PAGE_TITLE + " Page loaded");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + " Page failed to load");
            throw new AssertionError();
        }
    }

    public String getPageHeader() {
        return header.getText();
    }

    public void enterReferenceNumber(String refNumber){
        refNo.sendKeys(refNumber);
    }

    public void selectRandomType(){
        type.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void clickOk() {
        ok.click();
        waitForAngularRequestsToFinish();
    }

    public void clickCancel() {
        cancel.click();
        waitForAngularRequestsToFinish();
    }

}
