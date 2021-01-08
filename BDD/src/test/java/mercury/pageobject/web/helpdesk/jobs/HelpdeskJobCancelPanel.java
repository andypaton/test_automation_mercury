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

import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.helpdesk.HelpdeskTimelineTab;

public class HelpdeskJobCancelPanel extends Base_Page<HelpdeskJobCancelPanel>{
	
    private static final Logger logger = LogManager.getLogger();
    
    private static final String ACTIVE_WORKSPACE_CSS = "div.job-cancel";	
    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class, 'job-cancel')]";	

	private static final String REQUESTED_BY_CSS = ACTIVE_WORKSPACE_CSS + " input[name='RequestedBy']";
    private static final String REASON_DROPDOWN_XPATH = ACTIVE_WORKSPACE_XPATH + "//label[contains(text(), 'Reason')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String OTHER_REASON_XPATH = ACTIVE_WORKSPACE_XPATH + "//textarea[@name='OtherReasonText']";
    private static final String ORIGINAL_JOB_DROPDOWN_XPATH = ACTIVE_WORKSPACE_XPATH + "//label[contains(text(), 'Original Job')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
	private static final String NOTES_XPATH = ACTIVE_WORKSPACE_XPATH + "//label[@for='Notes']/../textarea";
	
	private static final String CANCEL_JOB_BUTTON_XPATH = ACTIVE_WORKSPACE_XPATH + "//button[text()='Cancel Job']";
	private static final String DONT_CANCEL_BUTTON_XPATH = ACTIVE_WORKSPACE_XPATH + "//button[text()=\"Don't Cancel\"]";

	private static final String HEADER_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[contains(@class, 'job-action-panel__header')]/h1";


	@FindBy(css=REQUESTED_BY_CSS)
	private WebElement requestedBy;
	
	@FindBy(xpath=REASON_DROPDOWN_XPATH)
	private WebElement reasonDropdown;
	
	@FindBy(xpath=ORIGINAL_JOB_DROPDOWN_XPATH)
	private WebElement originalJobDropdown;
	
	@FindBy(xpath=OTHER_REASON_XPATH)
	private WebElement otherReason;
	
	@FindBy(xpath=NOTES_XPATH)
	private WebElement notes;
	
	@FindBy(xpath=CANCEL_JOB_BUTTON_XPATH)
	private WebElement cancelJob;
	
	@FindBy(xpath=DONT_CANCEL_BUTTON_XPATH)
	private WebElement dontCancel;
	
	@FindBy(xpath=HEADER_XPATH)
	private WebElement header;
	
	public HelpdeskJobCancelPanel(WebDriver driver) {
		super(driver);
	}
	
	@Override
	protected void isLoaded() throws Error {
		
		try {			
			Assert.assertTrue("Page is not displayed", driver.findElement(By.cssSelector(ACTIVE_WORKSPACE_CSS)).isDisplayed());
            logger.info("Page loaded");
            
        } catch(NoSuchElementException ex){
        	
            logger.info("Page failed to load");
            throw new AssertionError();
        }
	}
	
	public void enterRequestedBy(String by) {
		requestedBy.sendKeys(by);	
	}
	
	public HelpdeskTimelineTab cancelJob() {
		cancelJob.click();	
		waitForAngularRequestsToFinish();
		return PageFactory.initElements(driver, HelpdeskTimelineTab.class).get();
	}
	
	public HelpdeskTimelineTab dontCancelJob() {
		dontCancel.click();	
		return PageFactory.initElements(driver, HelpdeskTimelineTab.class).get();
	}
	
	public void selectReason(String reason) {
		reasonDropdown.click();
		selectExactVisibleDropdownOption(reason);
	}
	
	public void enterOtherReason(String reason) {
		waitForElement(By.xpath(OTHER_REASON_XPATH), State.ELEMENT_IS_VISIBLE);
		otherReason.sendKeys(reason);
	}


	public String selectRandomOriginalJobOption() {
		waitForElement(By.xpath(ORIGINAL_JOB_DROPDOWN_XPATH), State.ELEMENT_IS_CLICKABLE);
		originalJobDropdown.click();
		return selectRandomVisibleDropdownOption();
	}
	
	public void enterNotes(String note) {
		notes.sendKeys(note);	
	}
	
	public String getHeader() {
		return header.getText();	
	}
}
