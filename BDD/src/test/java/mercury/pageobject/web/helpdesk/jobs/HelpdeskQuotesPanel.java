package mercury.pageobject.web.helpdesk.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class HelpdeskQuotesPanel extends Base_Page<HelpdeskQuotesPanel>{
	
    private static final Logger logger = LogManager.getLogger();
    
    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class, 'active')]";	

    private static final String QUOTES_PANEL_XPATH = ACTIVE_WORKSPACE_XPATH + "//ph-quotes";
    private static final String QUOTES_FOR_JOB_XPATH = ACTIVE_WORKSPACE_XPATH + "//span[contains(text(), 'Quotes for Job: ')]";
    private static final String APPROVER_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]//label[text()='Approver']/../div";

	@FindBy(xpath=QUOTES_FOR_JOB_XPATH)
	WebElement quotesForJob;
	
	@FindBy(xpath=APPROVER_XPATH)
	WebElement approver;
	
	
	public HelpdeskQuotesPanel(WebDriver driver) {
		super(driver);
	}
	
	@Override
	protected void isLoaded() throws Error {
		
		try {			
			Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(QUOTES_PANEL_XPATH)).isDisplayed());
            logger.info("Page loaded");
            
        } catch(NoSuchElementException ex){
        	
            logger.info("Page failed to load");
            throw new AssertionError();
        }
	}
	
	public String getJobReference() {	
	    return quotesForJob.getText().replace("Quotes for Job:", "").trim();
    }
	
	public String getApprover() {
		return approver.getText();
	}
	
}
