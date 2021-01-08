package mercury.pageobject.web.portal;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class PortalPopupPage extends Base_Page<PortalPopupPage> {


    private static final Logger logger = LogManager.getLogger();

	// Page title
	private static final String PAGE_TITLE = "Portal Popup";
	
	private static final String POPUP_CSS = "div.sweet-alert";
	private static final String POPUP_CONFIRM_CSS = POPUP_CSS + " button.confirm";
	
	@FindBy(css = POPUP_CONFIRM_CSS)
	private WebElement confirmYes;
	
	public PortalPopupPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void isLoaded() throws Error {
		logger.info(PAGE_TITLE + " isloaded");
		try {
			POHelper.isLoaded().isFluentElementIsVisible(By.cssSelector(POPUP_CSS));
			logger.info(PAGE_TITLE + " isloaded success");
		} catch (NoSuchElementException ex) {
			logger.info(PAGE_TITLE + " isloaded error");
			throw new AssertionError();
		}
	}

	@Override
	protected void load() {
		logger.info(PAGE_TITLE + " Load");
	}
	
	public boolean isPopupVisible() {
	    this.waitForAngularRequestsToFinish();
		return this.isElementClickable(By.cssSelector(POPUP_CONFIRM_CSS));
	}
	
	public void clickYes() {		
	    this.waitForAngularRequestsToFinish();
		waitForElement(By.cssSelector(POPUP_CONFIRM_CSS), ELEMENT_IS_CLICKABLE);
		confirmYes.click();	
		this.waitForAngularRequestsToFinish();
	}
	
}
