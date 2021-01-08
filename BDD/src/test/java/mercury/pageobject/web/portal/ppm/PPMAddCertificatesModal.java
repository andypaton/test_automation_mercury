package mercury.pageobject.web.portal.ppm;

import static mercury.runtime.ThreadManager.getWebDriver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class PPMAddCertificatesModal extends Base_Page<PPMAddCertificatesModal> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "Add Certificates";

    private static final String MODAL_XPATH = "//div[contains(@class, 'modal-dialog') and contains(@class, 'modal-md')]";
    private static final String MODAL_CONTENT_XPATH = MODAL_XPATH + "//div[@class='modal-content']";
    private static final String MODAL_HEADER_XPATH = MODAL_CONTENT_XPATH + "//div[@class='view-header__headline']/h1";
    private static final String MODAL_BODY_XPATH = MODAL_CONTENT_XPATH + "//div[contains(@class, 'modal-body') and contains(@class, 'compliance')]";
    private static final String MODAL_FOOTER_XPATH = MODAL_CONTENT_XPATH + "//div[@class='footer-button-bar__buttons-container']";
    private static final String MODAL_TITLE_XPATH = MODAL_CONTENT_XPATH + "//h1[contains(text(), '" + PAGE_TITLE + "')]";

    private static final String CERTIFICATE_NUMBER_XPATH = MODAL_BODY_XPATH + "//input[@id='CertificateNumber']";
    private static final String CERTIFICATE_DATE_XPATH = MODAL_BODY_XPATH + "//label[contains(text(), 'Certificate Date')]/.." + DROPDOWN_CALENDAR_XPATH;
    private static final String OUTCOME_PASS_XPATH = MODAL_BODY_XPATH + "//label[@for='outComeYes']";
    private static final String OUTCOME_FAIL_XPATH = MODAL_BODY_XPATH + "//label[@for='outComeNo']";

    private static final String SAVE_BUTTON_XPATH = MODAL_FOOTER_XPATH + "//button[contains(text(), 'Save')]";
    private static final String CANCEL_BUTTON_XPATH = MODAL_FOOTER_XPATH + "//button[contains(text(), 'Cancel')]";


    @FindBy(xpath = MODAL_HEADER_XPATH)
    private WebElement header;

    @FindBy(xpath = CERTIFICATE_NUMBER_XPATH)
    private WebElement certificateNo;

    @FindBy(xpath = CERTIFICATE_DATE_XPATH)
    private WebElement certificateDate;

    @FindBy(xpath = OUTCOME_PASS_XPATH)
    private WebElement pass;

    @FindBy(xpath = OUTCOME_FAIL_XPATH)
    private WebElement fail;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement save;

    @FindBy(xpath = CANCEL_BUTTON_XPATH)
    private WebElement cancel;


    public PPMAddCertificatesModal(WebDriver driver) {
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

    public void enterCertificateNumber(String certificateNumber){
        certificateNo.sendKeys(certificateNumber);
    }

    public void selectCertificateDate(String date){
        certificateDate.click();
        waitForAngularRequestsToFinish();
        WebElement day = getWebDriver().findElement(By.xpath(String.format(VISIBLE_DROPDOWN_CALENDAR_OPTION_XPATH, date)));
        day.click();
        waitForAngularRequestsToFinish();
    }

    public void selectOutcomePass() {
        pass.click();
        waitForAngularRequestsToFinish();
    }

    public void selectOutcomeFail() {
        fail.click();
        waitForAngularRequestsToFinish();
    }

    public void clickSave() {
        save.click();
        waitForAngularRequestsToFinish();
    }

    public void clickCancel() {
        cancel.click();
        waitForAngularRequestsToFinish();
    }

}
