package mercury.pageobject.web.helpdesk.resources;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;
import static mercury.helpers.State.ELEMENT_IS_VISIBLE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskFundingRequestsPanel;

public class HelpdeskAdditionalResourcePanel extends Base_Page<HelpdeskAdditionalResourcePanel> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ADDITIONAL_RESOURCE_PANEL_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]//ph-job-additional-resource/../../..";

    private static final String CREATED_REASON_XPATH = ADDITIONAL_RESOURCE_PANEL_XPATH + "//label[contains(text(), 'Created Reason')]/following-sibling::div[contains(@class, 'read-only-control')]";
    private static final String NOTES_XPATH = ADDITIONAL_RESOURCE_PANEL_XPATH + "//label[contains(text(), 'Notes')]/following-sibling::div[contains(@class, 'read-only-control')]";

    private static final String ALTERNATIVE_ADDITIONAL_RESOURCE_REQUIRED_SECTION_XPATH = "//div[@ng-if='row.additionalResourceRequirement']";
    private static final String ADDITIONAL_RESOURCE_RADIO_BUTTONS_XPATH = "//div[@class='yesNoRadio']//label[contains(text(), '%s')]";

    private static final String ADDITIONAL_RESOURCE_DETAILS_XPATH = ADDITIONAL_RESOURCE_PANEL_XPATH + "//div[contains(@class, 'resource-details')]";

    private static final String ACTIONS_BUTTON_XPATH = ADDITIONAL_RESOURCE_PANEL_XPATH + "//button[contains(text(), 'Actions')]";
    private static final String ACTION_XPATH = ACTIONS_BUTTON_XPATH + "/following-sibling::ul//*[contains(text(),'%s')]";

    @FindBy(xpath = ACTIONS_BUTTON_XPATH)
    WebElement actions;

    @FindBy(xpath = ALTERNATIVE_ADDITIONAL_RESOURCE_REQUIRED_SECTION_XPATH)
    private WebElement alternativeAditionalResourceRequiredsection;

    @FindBy(xpath = CREATED_REASON_XPATH)
    private WebElement createdReason;

    @FindBy(xpath = NOTES_XPATH)
    private WebElement notes;

    @FindBy(xpath = ADDITIONAL_RESOURCE_DETAILS_XPATH)
    private WebElement additionalResourceDetails;

    public HelpdeskAdditionalResourcePanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(ADDITIONAL_RESOURCE_PANEL_XPATH));
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectActions() {
        waitForElement(By.xpath(ACTIONS_BUTTON_XPATH), ELEMENT_IS_CLICKABLE);
        actions.click();
    }

    public HelpdeskFundingRequestsPanel selectFundingRequestAction() {
        POHelper.scrollToElement(actions);
        actions.click();
        WebElement fundingRequests = waitForElement(By.xpath(String.format(ACTION_XPATH, "Funding requests")), ELEMENT_IS_VISIBLE);
        fundingRequests.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskFundingRequestsPanel.class).get();
    }

    public boolean isAlternativeAdditionalResourceRequiredDisplayed() {
        return alternativeAditionalResourceRequiredsection.isDisplayed();
    }

    public boolean isIndividualResourceRadioButtonDisplayed() {
        WebElement individualResource = waitForElement(By.xpath(String.format(ADDITIONAL_RESOURCE_RADIO_BUTTONS_XPATH, "Individual Resource")), ELEMENT_IS_CLICKABLE);
        return individualResource.isDisplayed();
    }

    public boolean isResourceProfileRadioButtonDisplayed() {
        WebElement resourceProfile = waitForElement(By.xpath(String.format(ADDITIONAL_RESOURCE_RADIO_BUTTONS_XPATH, "Resource Profile")), ELEMENT_IS_CLICKABLE);
        return resourceProfile.isDisplayed();
    }

    public String getCreatedReason() {
        return createdReason.getText();
    }

    public String getNotes() {
        return notes.getText();
    }

    public String getAdditionalResourceDetails() {
        waitForElement(By.xpath(ADDITIONAL_RESOURCE_DETAILS_XPATH), ELEMENT_IS_VISIBLE);
        return additionalResourceDetails.getText();
    }
}
