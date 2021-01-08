package mercury.pageobject.web.portal.quotes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class DeclineInvitationToQuotePage extends Base_Page<DeclineInvitationToQuotePage>{


	private static final Logger logger = LogManager.getLogger();

	// Page title
	private static final String PAGE_TITLE = "Decline Invitation To Quote";
	
	// Core
	private static final String MAIN_CONTAINTER_XPATH = "//div[contains(@class, 'main-content')]";
	
	private static final String DECLINE_QUOTE_FORM_FORM_XPATH = MAIN_CONTAINTER_XPATH + "/..//form";
//	private static final String DECLINE_QUOTE_FORM_CSS = "div.main-content form " ;
//	private static final String DECLINE_QUOTE_FORM_XPATH = "//*[@id='ValidationSummary']"; 

	private static final String DECLINE_REASON_DROPDOWN_XPATH = DECLINE_QUOTE_FORM_FORM_XPATH + "//*[@id='QuoteInvitationDeclinedReasonId']";
	private static final String DECLINE_REASON_DESCRIPTION_XPATH = DECLINE_QUOTE_FORM_FORM_XPATH + "//*[@id='reason']";
	private static final String DECLINE_REASONS_XPATH = DECLINE_REASON_DROPDOWN_XPATH + "//option[text()='Select reason']/following-sibling::option";
	
	private static final String CANCEL_BUTTON_XPATH =  DECLINE_QUOTE_FORM_FORM_XPATH + "//*[@id='back']";
	private static final String SAVE_BUTTON_XPATH =  DECLINE_QUOTE_FORM_FORM_XPATH + "//*[@id='save']";
	
	@FindBy(xpath = DECLINE_QUOTE_FORM_FORM_XPATH)
	private WebElement declineInvitationToQuote;
	
	@FindBy(xpath = DECLINE_REASON_DROPDOWN_XPATH)
	private WebElement declineReason;
	
	@FindBy(xpath = DECLINE_REASON_DESCRIPTION_XPATH)
	private WebElement declineReasonDescription;
	
	@FindBy(xpath = CANCEL_BUTTON_XPATH)
	private WebElement cancelDecline;
	
	@FindBy(xpath = SAVE_BUTTON_XPATH)
	private WebElement saveDecline;
	
	public DeclineInvitationToQuotePage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void isLoaded() throws Error {
		logger.info(PAGE_TITLE + " isloaded");
		try {			

			POHelper.isLoaded().isFluentElementIsVisible(By.xpath(DECLINE_QUOTE_FORM_FORM_XPATH));
			logger.info(PAGE_TITLE + " isloaded success");            
		} catch(NoSuchElementException ex){
			logger.info(PAGE_TITLE + " isloaded error");
			throw new AssertionError();
		}
	}

	@Override
	protected void load() {  
		logger.info(PAGE_TITLE + " Load");
	}

	public Boolean isPageLoaded() {
		return this.isElementClickable(By.xpath(SAVE_BUTTON_XPATH));
	}
	

	// Page Interactions
	
	public void selectReasonByText(String option) {
		Select drop = new Select(declineReason);
		drop.selectByVisibleText(option);
	}
	
	public void selectRandomReason() {
		Select drop = new Select(declineReason);
		waitUntilSelectOptionsPopulated(drop);
		List<WebElement> visibleOptions = drop.getOptions();

		int randomSelection = RandomUtils.nextInt(1, visibleOptions.size());
		drop.selectByIndex(randomSelection);
	}

	public void selectReasonByValue(String option) {
		Select drop = new Select(declineReason);
		drop.selectByValue(option);
	}
	
	
	public void setDescriptiveReason(String reason) {
		declineReasonDescription.sendKeys(reason);
	}
	
	
	public void saveClick() {
		saveDecline.click();
	}
	
	public void cancelClick() {
		cancelDecline.click();
	}
	
	// 
	public String getReasonValue() {
		Select drop = new Select(declineReason);
		WebElement option = drop.getFirstSelectedOption();		
		return option.getAttribute("value");
	}
		
	public String getReasonText() {
		Select drop = new Select(declineReason);
		WebElement option = drop.getFirstSelectedOption();		
		return option.getText();
	}
	
	public Collection<String> getAllRejectionsReasons(int i) {
        
        List<String> allRejectionReasons = new ArrayList<>();
        Select reasonSelect = new Select(driver.findElement(By.xpath(String.format(DECLINE_REASON_DROPDOWN_XPATH, String.valueOf(i)))));
        List<WebElement> visibleOptions = reasonSelect.getOptions();
        for (WebElement option : visibleOptions)
        {
            String optionText = option.getText();
            if(optionText.length()>0) {
                allRejectionReasons.add(option.getText().trim());
            }
        }
        return allRejectionReasons;
    }


}
