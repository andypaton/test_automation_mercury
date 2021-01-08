package mercury.pageobject.web.portal.quotes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class EditQuoteDetailsPage extends Base_Page<EditQuoteDetailsPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Edit Quote Details";

    // Page elements

    private static final String PAGE_MAIN_CONTENT_XPATH = "//div[contains(@class,'main-content')]";
    private static final String SCOPE_OF_WORKS_LABEL_XPATH = PAGE_MAIN_CONTENT_XPATH + "//table[@id='scope-of-works']"; 
    private static final String QUOTE_EDIT_FORM_XPATH = PAGE_MAIN_CONTENT_XPATH + "//form[@id='editquoteform']";
    private static final String DESCRIPTION_OF_WORKS_EDIT_BOX_XPATH = QUOTE_EDIT_FORM_XPATH + "//textarea[@id='DescriptionOfWorks']";
    private static final String PROSPOSED_WORKING_TIMES_EDIT_BOX_XPATH = QUOTE_EDIT_FORM_XPATH + "//textarea[@id='DescriptionOfWorks']";
    
    // Buttons
    private static final String HIGH_RISK_WORKS_RADIO_BUTTON_XPATH = QUOTE_EDIT_FORM_XPATH + "//div[@class='yesNoRadio']//input/following-sibling::label[text()='%s']";
    private static final String SAVE_CHANGES_BUTTON_XPATH = QUOTE_EDIT_FORM_XPATH + "//p[@class='buttons']//input[@value='Save Changes']";

    @FindBy(xpath = SCOPE_OF_WORKS_LABEL_XPATH)
    private WebElement scopeOfWorks;
    
    @FindBy(xpath = DESCRIPTION_OF_WORKS_EDIT_BOX_XPATH)
    private WebElement descriptionOfWorksEditbox;
    
    @FindBy(xpath = PROSPOSED_WORKING_TIMES_EDIT_BOX_XPATH)
    private WebElement proposedWorkingHoursEditbox;
    
    @FindBy(xpath = SAVE_CHANGES_BUTTON_XPATH)
    private WebElement saveChangesButton;

    public EditQuoteDetailsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(SCOPE_OF_WORKS_LABEL_XPATH));
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

    @Override
    public String getPageTitle() {
        return super.getPageTitle();
    }

    public Boolean isPageLoaded() {
        return this.isElementClickable(By.xpath(SCOPE_OF_WORKS_LABEL_XPATH));
    }

    public boolean getDescriptionOfWorksEditState() {
        return descriptionOfWorksEditbox.isEnabled();
    }
    
    public void enterDescriptionOfWorks(String response) {
        this.descriptionOfWorksEditbox.sendKeys(response);
    }
    
    public boolean getYesRadioButtonEditState() {
        WebElement element = driver.findElement(By.xpath(String.format(HIGH_RISK_WORKS_RADIO_BUTTON_XPATH, "Yes")));
        return element.isEnabled();
    }
    
    public boolean getNoRadioButtonEditState() {
        WebElement element = driver.findElement(By.xpath(String.format(HIGH_RISK_WORKS_RADIO_BUTTON_XPATH, "No")));
        return element.isEnabled();
    }
    
    public boolean getProposedWorkingTimesEditState() {
        return proposedWorkingHoursEditbox.isEnabled();
    }
    
    public boolean getSaveChangesButtonEditState() {
        return saveChangesButton.isEnabled();
    }
    
    public void clickSaveChangesButton() {
        saveChangesButton.click();
        waitForAngularRequestsToFinish();
    }
    
}

