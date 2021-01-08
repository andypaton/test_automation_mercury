package mercury.pageobject.web.portal.quotes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class CreateQuotePage  extends Base_Page<CreateQuotePage>{
    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Create Quote";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";

    // Core
    private static final String UPDATE_JOB_FORM_ID = "editquoteform";
    private static final String UPDATE_JOB_FORM_CSS = "#editquoteform";

    private static final String QUOTE_REFERENCE_CSS = UPDATE_JOB_FORM_CSS + " #QuoteRef";
    private static final String DESCRIPTON_OF_WORKS_CSS = UPDATE_JOB_FORM_CSS + " #DescriptionOfWorks";
    private static final String PROPOSED_WORKING_TIMES_CSS = UPDATE_JOB_FORM_CSS + " #ProposedWorkingTimes";
    private static final String HIGH_RISK_WORKS_YES_CSS = UPDATE_JOB_FORM_CSS + " #WorksAreHighRisk-true + label";
    private static final String HIGH_RISK_WORKS_NO_CSS = UPDATE_JOB_FORM_CSS + " #WorksAreHighRisk-false + label";
    private static final String HIGH_RISK_WORKS_DROPDOWN_CSS = UPDATE_JOB_FORM_CSS + " #RelevantHighRiskTypes_chosen";
    private static final String HIGH_RISK_WORKS_OPTION_XPATH = "//div[@id='RelevantHighRiskTypes_chosen']//div[contains(@class,'chosen-drop')]//li[contains(text(),'%s')]";
    private static final String START_QUOTE_CSS = UPDATE_JOB_FORM_CSS + " #start-quote-btn";


    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(id = UPDATE_JOB_FORM_ID)
    private WebElement updateJobForm;

    @FindBy(css = QUOTE_REFERENCE_CSS)
    private WebElement quoteReference;

    @FindBy(css = DESCRIPTON_OF_WORKS_CSS)
    private WebElement descriptionOfWorks;

    @FindBy(css = HIGH_RISK_WORKS_YES_CSS)
    private WebElement highRiskWorksYes;

    @FindBy(css = HIGH_RISK_WORKS_NO_CSS)
    private WebElement highRiskWorksNo;

    @FindBy(css = HIGH_RISK_WORKS_DROPDOWN_CSS)
    private WebElement highRiskDropdown;

    @FindBy(css = PROPOSED_WORKING_TIMES_CSS)
    private WebElement proposedWorkingTimes;

    @FindBy(css = START_QUOTE_CSS)
    private WebElement startQuote;


    public CreateQuotePage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }


    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.cssSelector(START_QUOTE_CSS));
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

    public String getPageHeaderText() {
        return pageHeaderTest.getText();
    }

    @Override
    public String getPageTitle() {
        return super.getPageTitle();
    }

    public Boolean isPageLoaded() {
        return this.isElementVisible(By.cssSelector(START_QUOTE_CSS));
    }

    // Page Interections

    public void setQuoteReference(String keysToSend) {
        quoteReference.sendKeys(keysToSend);
    }

    public void setDescriptionOfWorks(String keysToSend) {
        descriptionOfWorks.sendKeys(keysToSend);
    }

    public void setHighRiskWorksYes() {
        highRiskWorksYes.click();
    }

    public void setHighRiskWorksNo() {
        highRiskWorksNo.click();
    }

    public void selectHighRisk(String risk) throws InterruptedException {
        highRiskDropdown.click();
        WebElement visibleOption = waitForElement(By.xpath(String.format(HIGH_RISK_WORKS_OPTION_XPATH, risk)), State.ELEMENT_IS_VISIBLE);
        visibleOption.click();
    }

    public void setProposedWorkingTimes(String keysToSend) {
        proposedWorkingTimes.sendKeys(keysToSend);
    }

    public RegisterQuotePage startQuote() {
        startQuote.click();
        return PageFactory.initElements(driver, RegisterQuotePage.class).get();
    }

    public void clickStartQuote() {
        startQuote.click();
    }

}

