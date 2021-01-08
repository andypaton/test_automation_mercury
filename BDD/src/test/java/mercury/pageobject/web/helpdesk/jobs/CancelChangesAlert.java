package mercury.pageobject.web.helpdesk.jobs;

import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.pageobject.web.Base_Page;

public class CancelChangesAlert extends Base_Page<CancelChangesAlert> {

	private static final Logger logger = LogManager.getLogger();

	private static final String CANCEL_CHANGES_MODAL_XPATH = "//div[contains(@class, 'sweet-alert') and contains(@class, 'visible')]//h2[text() = 'Cancel Changes']";

	private static final String CANCEL_BUTTON_XPATH = CANCEL_CHANGES_MODAL_XPATH + "/../div/button[contains(@class, 'cancel')]";
	private static final String CONFIRM_BUTTON_XPATH = CANCEL_CHANGES_MODAL_XPATH + "/../div/button[contains(@class, 'confirm')]";

	
	@FindBy(xpath = CANCEL_BUTTON_XPATH)
	private WebElement cancel;
	
	@FindBy(xpath = CONFIRM_BUTTON_XPATH)
	private WebElement confirm;
	
	public CancelChangesAlert(WebDriver driver) {
		super(driver);
	}

	@Override
	protected void isLoaded() throws Error {	
		try {
			waitForAngularRequestsToFinish();
			assertTrue("Cancel Changes alert not loaded!", driver.findElement(By.xpath(CANCEL_CHANGES_MODAL_XPATH)).isDisplayed());
			logger.info("Page loaded");
		} catch(NoSuchElementException ex){
			logger.info("Page failed to load");
			throw new AssertionError();
		}
	}
	
	public HelpdeskLogJobPage cancel() {
	    cancel.click();
		waitForAngularRequestsToFinish();
		waitUntilElementNotDisplayed(By.xpath(CANCEL_CHANGES_MODAL_XPATH));
		return PageFactory.initElements(driver, HelpdeskLogJobPage.class).get();
	}
	
	public HelpdeskJobPage confirm() {
		confirm.click();
		waitForAngularRequestsToFinish();
		waitUntilElementNotDisplayed(By.xpath(CANCEL_CHANGES_MODAL_XPATH));
		return PageFactory.initElements(driver, HelpdeskJobPage.class).get();
	}
}
