package mercury.pageobject.web.helpdesk.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.pageobject.web.Base_Page;

public class HelpdeskJobActionsPanel extends Base_Page<HelpdeskJobActionsPanel>{

    private static final Logger logger = LogManager.getLogger();

    protected static final String JOB_ACTIONS_PANEL_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]//div[contains(@class, 'job-action-panel')]";
    protected static final String HEADER_XPATH = JOB_ACTIONS_PANEL_XPATH + "/div[@class='job-action-panel__header']//span";
    protected static final String CONTENT_XPATH = JOB_ACTIONS_PANEL_XPATH + "//div[@class='job-action-panel__content']";
    protected static final String CANCEL_BUTTON_XPATH = JOB_ACTIONS_PANEL_XPATH + "//button[contains(text(), 'Cancel')]";
    protected static final String CREATE_LINKED_QUOTE_JOB_BUTTON_XPATH = JOB_ACTIONS_PANEL_XPATH + "//button/span[contains(text(), 'Create linked quote job')]";
    protected static final String CONVERT_TO_QUOTE_JOB_BUTTON_XPATH = JOB_ACTIONS_PANEL_XPATH + "//button/span[contains(text(), 'Convert to quote job')]";

    @FindBy(xpath=HEADER_XPATH)
    WebElement header;

    @FindBy(xpath=CONTENT_XPATH)
    WebElement content;

    @FindBy(xpath=CANCEL_BUTTON_XPATH)
    WebElement cancel;

    @FindBy(xpath=CREATE_LINKED_QUOTE_JOB_BUTTON_XPATH)
    WebElement createLinkedQuoteJob;

    @FindBy(xpath=CONVERT_TO_QUOTE_JOB_BUTTON_XPATH)
    WebElement convertToQuoteJob;

    public HelpdeskJobActionsPanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            waitForLoadingToComplete();
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(JOB_ACTIONS_PANEL_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch(NoSuchElementException ex){

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }


    public String getHeaderText() {
        return header.getText();
    }

    public String getContentText() {
        return content.getText();
    }

    public HelpdeskJobPage cancel() {
        cancel.click();
        waitForAngularRequestsToFinish();
        waitUntilElementNotDisplayed(By.xpath(CANCEL_BUTTON_XPATH));
        return PageFactory.initElements(driver, HelpdeskJobPage.class).get();
    }

    public boolean isConvertToQuoteJobButtonDisplayed() {
        return isElementVisible(By.xpath(CONVERT_TO_QUOTE_JOB_BUTTON_XPATH));
    }

    public boolean isJobActionsPanelDisplayed() {
        return isElementVisible(By.xpath(JOB_ACTIONS_PANEL_XPATH));
    }

    public HelpdeskLogJobPage createLinkedQuoteJob() {
        createLinkedQuoteJob.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskLogJobPage.class).get();
    }

    public HelpdeskLogJobPage convertToQuoteJob() {
        convertToQuoteJob.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskLogJobPage.class).get();
    }

}
