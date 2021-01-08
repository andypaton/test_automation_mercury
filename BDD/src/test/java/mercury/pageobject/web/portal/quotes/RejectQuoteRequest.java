package mercury.pageobject.web.portal.quotes;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

import static mercury.helpers.StringHelper.normalize;

public class RejectQuoteRequest extends Base_Page<RejectQuoteRequest>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Reject Quote Modal";

    // Page elements

    // Modal popup
    private static final String CANCEL_QUOTE_REQUEST_MODAL_XPATH = "//div[@id='cancelQuoteRequestModal']";
    private static final String PAGE_MODAL_BODY_XPATH = CANCEL_QUOTE_REQUEST_MODAL_XPATH + "//div[contains(@class, 'modal-body')]";
    private static final String PAGE_MODAL_FOOTER_XPATH = CANCEL_QUOTE_REQUEST_MODAL_XPATH + "//div[contains(@class, 'modal-footer')]";

    // Reject form
    private static final String REJECT_QUOTE_FORM_XPATH = PAGE_MODAL_BODY_XPATH + "//form[@id='cancel-qr-form']";

    // Core
    private static final String REJECTION_REASON_XPATH = REJECT_QUOTE_FORM_XPATH + "//select[@id='QuoteRequestCancellationForm_CancellationReason']";
    private static final String RESOURCE_TYPE_SELECTOR_XPATH = REJECT_QUOTE_FORM_XPATH + "//select[@id='QuoteRequestCancellationForm_ResourceType']";
    private static final String RESOURCE_SELECTOR_XPATH = REJECT_QUOTE_FORM_XPATH + "//select[@id='QuoteRequestCancellationForm_NewResourceAssignment']";
    private static final String SHOW_ALL_CONTRACTORS_LABEL_XPATH = REJECT_QUOTE_FORM_XPATH + "//label[contains(text(),'Use alternative contractor')]";
    private static final String ADDITIONAL_COMMENTS_XPATH = REJECT_QUOTE_FORM_XPATH + "//*[@id='QuoteRequestCancellationForm_AdditionalCancellationComments']";
    private static final String REJECT_SAVE_CHANGES_XPATH = PAGE_MODAL_FOOTER_XPATH + "//button[@id='confirm-cancel-qr']";


    @FindBy(xpath = REJECTION_REASON_XPATH)
    private WebElement rejectReasonSelect;

    @FindBy(xpath = RESOURCE_TYPE_SELECTOR_XPATH)
    private WebElement resourceTypeSelect;

    @FindBy(xpath = RESOURCE_SELECTOR_XPATH)
    private WebElement resourceSelect;

    @FindBy(xpath = SHOW_ALL_CONTRACTORS_LABEL_XPATH)
    private WebElement showAllContractors;

    @FindBy(xpath = ADDITIONAL_COMMENTS_XPATH)
    private WebElement additionalComments;

    @FindBy(xpath = REJECT_SAVE_CHANGES_XPATH)
    private WebElement rejectButton;


    public RejectQuoteRequest(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(REJECT_QUOTE_FORM_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }


    // Page Interactions
    public void selectRejectReason(String option) {
        Select drop = new Select(rejectReasonSelect);
        drop.selectByVisibleText(option);
    }

    public void selectResourceType(String option) {
        Select drop = new Select(resourceTypeSelect );
        drop.selectByVisibleText(option);
    }

    public void selectResource(String option) {
        Select drop = new Select(resourceSelect);
        drop.selectByVisibleText(option);
    }

    public void selectRandomResource() {
        waitForElement(By.xpath(RESOURCE_SELECTOR_XPATH), State.ELEMENT_IS_VISIBLE);
        Select drop = new Select(resourceSelect);
        waitUntilSelectOptionsPopulated(drop);
        List<WebElement> visibleOptions = drop.getOptions();

        int randomSelection = RandomUtils.nextInt(1, visibleOptions.size());
        drop.selectByIndex(randomSelection);
    }

    public void showAllContractors() {
        showAllContractors.click();
        this.waitForAngularRequestsToFinish();
    }

    public void setAdditionalComments(String keysToSend) {
        additionalComments.sendKeys(keysToSend);
    }

    public OpenQuoteRequestsPage submitForm() {
        rejectButton.click();
        return PageFactory.initElements(driver, OpenQuoteRequestsPage.class).get();
    }

    // Get values
    public String getRejectReasonValue() {
        Select drop = new Select(rejectReasonSelect);
        WebElement option = drop.getFirstSelectedOption();
        return option.getAttribute("value");
    }

    public String getResourceTypeValue() {
        Select drop = new Select(resourceTypeSelect);
        WebElement option = drop.getFirstSelectedOption();
        return option.getAttribute("value");
    }

    public String getResourceValue() {
        Select drop = new Select(resourceSelect);
        WebElement option = drop.getFirstSelectedOption();
        return option.getAttribute("value");
    }

    public String getResourceValueText() {
        Select drop = new Select(resourceSelect);
        WebElement option = drop.getFirstSelectedOption();
        return option.getText();
    }

    public String getAdditionalComments() {
        return additionalComments.getText();
    }

    public List<String> getAllResources() throws InterruptedException {
        waitForElement(By.xpath(RESOURCE_SELECTOR_XPATH), State.ELEMENT_IS_VISIBLE);
        this.waitForAngularRequestsToFinish();
        List<String> allResources = new ArrayList<>();
        Select resourceSelect = new Select(this.resourceSelect);
        List<WebElement> visibleOptions = resourceSelect.getOptions();
        for (WebElement option : visibleOptions)
        {
            String optionText = option.getText();
            if(optionText.length()>0) {
                allResources.add(normalize(option.getText()));
            }
        }
        return allResources;
    }
}
