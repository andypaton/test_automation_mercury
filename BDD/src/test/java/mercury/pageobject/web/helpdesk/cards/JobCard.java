package mercury.pageobject.web.helpdesk.cards;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class JobCard extends Base_Page<JobCard> {

	private static final Logger logger = LogManager.getLogger();

	private static final String VISIBLE_SIDE_PANEL_XPATH = "//div[@id='side-panel']";
	
    private static final String JOB_CARD_XPATH = VISIBLE_SIDE_PANEL_XPATH + "//div[@class='card job']";

    private static final String CARD_HEADER_XPATH = JOB_CARD_XPATH + "//div[@class='card-header']";
    private static final String CARD_HEADER_ICON_XPATH = JOB_CARD_XPATH + "//div[@class='card-header__icon-container']";

    private static final String CARD_SUBHEADER_LEFT_XPATH =JOB_CARD_XPATH +  "//div[@class='card-subheader__left']";
    private static final String CARD_SUBHEADER_RIGHT_XPATH = JOB_CARD_XPATH + "//div[@class='card-subheader__right']";
    private static final String CARD_CONTENT_XPATH = JOB_CARD_XPATH + "//div[@class='card-content']";
    
    private static final String WRENCH_XPATH = "//i[@class='icons__wrench']/..";
    private static final String EXCLAMATION_TRIANGLE_XPATH = "//i[@class='icons__exclamation-triangle']/..";
    
    @FindBy(xpath=VISIBLE_SIDE_PANEL_XPATH)
    private static WebElement sidePanel;
    
    @FindBy(xpath=JOB_CARD_XPATH)
    private static WebElement jobCard;
    
    @FindBy(xpath=CARD_HEADER_XPATH)
    private static WebElement header;    
    
    @FindBy(xpath=CARD_HEADER_ICON_XPATH)
    private static WebElement headerIcon;
    
    @FindBy(xpath=CARD_SUBHEADER_LEFT_XPATH)
    private static WebElement subHeader_left;
    
    @FindBy(xpath=CARD_SUBHEADER_RIGHT_XPATH)
    private static WebElement subHeader_right;
    
    @FindBy(xpath=CARD_CONTENT_XPATH)
    private static WebElement content;
    
    @FindBy(xpath=WRENCH_XPATH)
    private static WebElement wrench;
    
    @FindBy(xpath=EXCLAMATION_TRIANGLE_XPATH)
    private static WebElement exclamationTriangle;

	public JobCard(WebDriver driver) {
		super(driver);
	}
	
	@Override
	protected void isLoaded() throws Error {	
		try {			
			Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(JOB_CARD_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
	}
	
	public boolean isCardDisplayed() throws Exception {
		return isElementPresent(By.xpath(JOB_CARD_XPATH));
	}
    
    public String getHeader() throws Exception {
		return header.getText();
	}
    
    public String getSubHeaderLeft() throws Exception {
		return subHeader_left.getText();
	}
    
    public String getSubHeaderRight() throws Exception {
		return subHeader_right.getText();
	}
    
    public String getContent() throws Exception {
		return content.getText();
	}
    
    public Integer getWrenchCount() throws Exception {
    	String countAsString = wrench.getText();
		return countAsString.isEmpty() ? 0 : Integer.valueOf(countAsString);
	}
    
    public Integer getExclamationTriangleCount() throws Exception {
    	String countAsString = exclamationTriangle.getText();
		return countAsString.isEmpty() ? 0 : Integer.valueOf(countAsString);
	}
 
}
