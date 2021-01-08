package mercury.pageobject.web.portal.jobs;

import static mercury.helpers.Constants.MAX_TIMEOUT;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.admin.refrigerantGasUsage.AdminLeakSiteInformationModal;
import mercury.pageobject.web.pageHelpers.QuestionHelper;

public class UpdateJobPage extends Base_Page<UpdateJobPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Update Job";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";

    // Update Job form
    private static final String UPDATE_JOB_FORM_ID = "updateJobForm";
    private static final String UPDATE_JOB_FORM_CSS = "#updateJobForm";
    private static final String UPDATE_JOB_FORM_XPATH = "//*[@id='updateJobForm']";

    private static final String BASIC_QUESTION_YES_XPATH = "//label[contains(text(),'%s')]/..//label[text()='Yes']";
    private static final String BASIC_QUESTION_NO_XPATH = "//label[contains(text(),'%s')]/..//label[text()='No']";
    private static final String BASIC_QUESTION_ANSWER_XPATH = "//label[contains(text(),'%s')]/..//label[text()='%s']";

    // Core
    private static final String REMOTE_JOB_CSS = UPDATE_JOB_FORM_CSS + " #RemoteFix ~label ";
    private static final String QUESTION_LABEL = UPDATE_JOB_FORM_XPATH + "//label[contains(text(),'%s')]";
    private static final String TRAVEL_TIME_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Travel Time')]/.." + DROPDOWN_PICK_WRAPPER_XPATH;
    private static final String TRAVEL_TIME_DROPDOWN_TOOLTIP_ERROR_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Travel Time')]/.." + DROPDOWN_INVALID_XPATH;

    private static final String FORM_CONTROL_TOOLTIP_ERROR_XPATH = "//label[contains(text(), '%s')]/following-sibling::div//*[contains(text(),'%s')]";

    private static final String UPDATE_JOB_QUESTIONS_YES_XPATH = UPDATE_JOB_FORM_XPATH + BASIC_QUESTION_YES_XPATH;
    private static final String UPDATE_JOB_QUESTIONS_NO_XPATH = UPDATE_JOB_FORM_XPATH + BASIC_QUESTION_NO_XPATH;
    private static final String UPDATE_JOB_QUESTIONS_ANSWER_XPATH = UPDATE_JOB_FORM_XPATH + BASIC_QUESTION_ANSWER_XPATH;

    private static final String WORK_START_DATE_XPATH = UPDATE_JOB_FORM_XPATH + "//ph-date-time[@id='dtWorkStartTime']//input";
    private static final String WORK_START_DATE_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Work Start')]/.." + DROPDOWN_CALENDAR_XPATH;
    private static final String WORK_START_TIME_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Work Start')]/.." + DROPDOWN_CLOCK_XPATH;
    private static final String WORK_START_TIME_TOOLTIP_ERROR_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Work Start')]/.." + DROPDOWN_INVALID_XPATH;

    private static final String TIME_SPENT_TOOLTIP_ERROR_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Time Spent')]/..//span[contains(@class, 'k-tooltip')]/span[contains(@class, 'k-warning')]";
    private static final String TIME_SPENT_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Time Spent')]/.." + DROPDOWN_PICK_WRAPPER_XPATH;

    private static final String WORK_END_TIME_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Work End')]/.." + DROPDOWN_CLOCK_XPATH;
    private static final String WORK_END_TIME_TOOLTIP_ERROR_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Work End')]/..//span[contains(@class, 'k-tooltip')]";// + DROPDOWN_INVALID_XPATH;
    // removed due to MCP-46
    private static final String WORK_END_TIME_VALUE_XPATH = UPDATE_JOB_FORM_XPATH + "//div[@id='dtWorkEndTime']";

    private static final String CONTROL_TOOLTIP_ERROR_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), '%s')]/..//span[contains(@class, 'k-tooltip')]";

    private static final String OVERTIME_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Overtime (optional)')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String STATUS_ON_DEPATURE_DROPDOWN_ARROW_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Status on Departure')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String STATUS_ON_DEPATURE_TOOLTIP_ERROR_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Status on Departure')]/.." + DROPDOWN_INVALID_XPATH;
    private static final String STATUS_ON_DEPATURE_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Status on Departure')]/" + "/following-sibling::div//span[contains(@class, 'k-dropdown')]";

    private static final String TRAVEL_TIME_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + " input[aria-owns='travelTimeTxt_timeview']";
    private static final String WORK_START_TIME_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + " input[name='workStartTime']";
    private static final String WORK_END_TIME_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + " input[name='workEndTime']";
    private static final String OVERTIME_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + " input[name='OverTime']";

    private static final String STATUS_ON_DEPARTURE_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + " span[aria-owns='statusDropdownList_listbox'] .k-input";

    //GSAN Section
    private static final String GSAN_REFERENCE_NUMBER_CSS = UPDATE_JOB_FORM_CSS + " #gsanReferenceNumber";
    private static final String GSAN_TYPE_XPATH = UPDATE_JOB_FORM_XPATH   + "//label[text() = 'Type']/.." + DROPDOWN_SEARCH_ARROW_XPATH;

    // Gas section
    private static final String GAS_OTHER_ASSET_PLANT_NOTES_XPATH = UPDATE_JOB_FORM_XPATH + "//textarea[@id='otherAssetPlantNotes']";
    private static final String GAS_OTHER_GAS_TYPE_NOTES_XPATH = UPDATE_JOB_FORM_XPATH + "//textarea[@id='otherGasTypeNotes']";
    private static final String GAS_OTHER_ASSET_NOTES_XPATH = UPDATE_JOB_FORM_XPATH + "//textarea[@id='otherAssetNotes']";
    private static final String GAS_SELECT_A_RACK_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Select a rack')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String GAS_SELECT_AN_ASSET_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Select an Asset')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String GAS_GAS_TYPE_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Gas Type')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String GAS_OTHER_GAS_TYPE_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Other Gas Type')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String GAS_GAS_LEAKAGE_CODE_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Gas Leakage Code')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String GAS_GAS_LEAKAGE_CHECK_METHOD_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Gas Leakage Check Method')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String GAS_GAS_LEAK_LOCATION_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Gas Leak Location')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String GAS_ACTION_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Action')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String GAS_FAULT_CODE_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Fault Code')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;

    private static final String GAS_GAS_USAGE_BOTTLE_NUMBER_XPATH = UPDATE_JOB_FORM_XPATH + "//input[@id='gasUsage[%s].bottleNumber']";
    private static final String GAS_GAS_USAGE_BOTTLE_QUANTITY_XPATH = UPDATE_JOB_FORM_XPATH + "//input[@id='gasUsage[%s].bottleQuantity']";

    private static final String LEAK_CHECK_REPAIR_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Has repair been leak checked?')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;

    private static final String GAS_SELECT_A_RACK_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + " span[aria-owns='assetPlant_listbox'] .k-input";
    private static final String GAS_SELECT_AN_ASSET_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + " span[aria-owns='selectedGasAssetId_listbox'] .k-input";
    private static final String GAS_GAS_TYPE_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + " span[aria-owns='selectedGasType_listbox'] .k-input";
    private static final String GAS_ALL_GAS_TYPE_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + " span[aria-owns='allGasTypes_listbox'] .k-input";
    private static final String GAS_GAS_LEAKAGE_CODE_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + " span[aria-owns='gasLeakageCode_listbox'] .k-input";
    private static final String GAS_GAS_LEAKAGE_CHECK_METHOD_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + " span[aria-owns='gasLeakageCheckMethod_listbox'] .k-input";
    private static final String GAS_GAS_LEAK_LOCATION_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + " span[aria-owns='gasLeakLocationId_listbox'] .k-input";
    private static final String GAS_ACTION_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + " span[aria-owns='GasActionReason_listbox'] .k-input";
    private static final String GAS_FAULT_CODE_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + " span[aria-owns='GasFaultCodeReason_listbox'] .k-input";

    // Leak Checks
    private static final String LEAK_CHECKS_XPATH = UPDATE_JOB_FORM_XPATH + "//us-gas-leak-check-questions";
    private static final String PRIMARY_COMPONENTS_XPATH = LEAK_CHECKS_XPATH + "//div[text()='Leak Check Information']/following-sibling::div[@class='row'][1]/div[contains(@class, 'col-md-6')][1]";
    private static final String SUB_COMPONENTS_XPATH = LEAK_CHECKS_XPATH + "//div[text()='Leak Check Information']/following-sibling::div[@class='row'][1]/div[contains(@class, 'col-md-6')][2]";
    private static final String PRIMARY_COMPONENTS_INFO_XPATH = LEAK_CHECKS_XPATH + "//div[text()='Leak Check Information']/following-sibling::div[@class='row']/div[contains(@class, 'col-md-12')]";
    private static final String LEAK_SITE_STATUSES_XPATH = LEAK_CHECKS_XPATH + "//div[text()='Leak Check Results']/following-sibling::div[@class='row']/div[contains(@class, 'col-md-4')][1]";
    private static final String INITIAL_TESTS_XPATH = LEAK_CHECKS_XPATH + "//div[text()='Leak Check Results']/following-sibling::div[@class='row']/div[contains(@class, 'col-md-4')][2]";
    private static final String FOLLOW_UP_TESTS_XPATH = LEAK_CHECKS_XPATH + "//div[text()='Leak Check Results']/following-sibling::div[@class='row']/div[contains(@class, 'col-md-4')][3]";
    private static final String FOLLOW_UP_BUTTON_XPATH = LEAK_CHECKS_XPATH + "//div[contains(@class, 'btn') and contains(text(), 'Follow Up')]";

    // Status Returning
    private static final String REASON_FOR_RETURN_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Reason for Returning')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String REASON_FOR_RETURN_VALUE_CSS = UPDATE_JOB_FORM_CSS + " span[aria-owns='returningToJobReasonDropdownList_listbox'] .k-input";

    // Status Returning
    // Status Awaiting Parts
    private static final String ETA_DATE_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'ETA Date')]/.." + DROPDOWN_CALENDAR_XPATH;
    private static final String ETA_DATE_SEARCH_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'ETA Date')]/..//input";
    private static final String ETA_DATE_PICKER_CONTAINER_XPATH = "//*[@id='etaPicker_dateview']";
    private static final String ETA_WINDOW_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'ETA Window')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String ETA_WINDOW_SEARCH_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'ETA Window')]/.." + DROPDOWN_SEARCH_XPATH;

    private static final String ETA_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + " #InitialEta";

    // Status Complete
    private static final String ASSET_CONDITON_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Asset Condition')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String ROOT_CAUSE_CATEGORY_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Root cause category')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String ROOT_CAUSE_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + "//label[text()='Root cause']/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String ROOT_CAUSE_DESCRIPTION_XPATH = UPDATE_JOB_FORM_XPATH + "//*[@id='RootCauseDescription']";
    private static final String ADDITIONAL_NOTES_XPATH = UPDATE_JOB_FORM_XPATH + "//*[@id='AdditionalNotes']";
    private static final String EXTRA_TO_CONTRACT_NOTES_XPATH = UPDATE_JOB_FORM_XPATH + "//*[@id='ExtraToContractNotes']";
    private static final String ASSET_CONDITION_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + " span[aria-owns='assetConditionDropdownList_listbox'] .k-input";
    private static final String ROOT_CAUSE_CATEGORY_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + " span[aria-owns='RootCauseCategory_listbox'] .k-input";
    private static final String ROOT_CAUSE_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + " span[aria-owns='RootCause_listbox'] .k-input";
    private static final String ADDITIONAL_NOTES_VALUE_CSS = UPDATE_JOB_FORM_CSS + " textarea[name='JobDetails.AdditionalNotes']";
    private static final String EXTRA_TO_CONTRACT_NOTES_CSS = UPDATE_JOB_FORM_CSS + " textarea[name='JobDetails.ExtraToContractNotes']";

    // Status Complete
    // Additional Resource Required section
    private static final String ADDITIONAL_RESOURCE_REQUIRED_CSS = UPDATE_JOB_FORM_CSS + " #AdditionalResourceRequired ~label ";
    private static final String RESOURCE_PROFILE_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Resource Profile')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String ADDITIONAL_RESOURCE_NOTES_XPATH = UPDATE_JOB_FORM_XPATH + "//*[@id='ResourceNotes']";

    // Status Complete
    // Required Quote section
    private static final String QUOTE_FORM_XPATH = "//*[@id='create_quote_form']";
    private static final String QUOTE_FORM_CSS = " #create_quote_form";
    private static final String QUOTE_REQUEST_QUOTE_CSS = UPDATE_JOB_FORM_CSS + " #QuoteRequired ~label";
    private static final String QUOTE_SCOPE_OF_WORK_XPATH = UPDATE_JOB_FORM_XPATH + QUOTE_FORM_XPATH + "//*[@id='quoteScopeOfWork']";

    private static final String QUOTE_ASSET_MAIN_TYPE_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + QUOTE_FORM_CSS + " #quoteAssetTypeId .k-input";
    private static final String QUOTE_ASSET_SUB_TYPE_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + QUOTE_FORM_CSS + " #quoteAssetSubTypeId .k-input";
    private static final String QUOTE_ASSET_CLASSIFICATION_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + QUOTE_FORM_CSS + " span[aria-owns='QuoteAssetClassificationId_listbox'] .k-input";
    private static final String QUOTE_ASSET_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + QUOTE_FORM_CSS + " span[aria-owns='QuoteAssetId_listbox'] .k-input";
    private static final String QUOTE_LOCATION_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + QUOTE_FORM_CSS + " span[aria-owns='QuoteLocationId_listbox'] .k-input";
    private static final String QUOTE_FAULT_TYPE_SELECTED_VALUE_CSS = UPDATE_JOB_FORM_CSS + QUOTE_FORM_CSS + " span[aria-owns='QuoteFaultTypeId_listbox'] .k-input";

    private static final String RADIO_BUTTON_QUESTION_ANSWER_XPATH = UPDATE_JOB_FORM_XPATH + QUOTE_FORM_XPATH +"//label[contains(text(),'%s')]/..//label[text()='%s']";
    private static final String QUOTE_PRIORITY_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + QUOTE_FORM_XPATH + "//label[contains(text(), 'Select Priority')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;

    private static final String INFORMATION_MESSAGE_XPATH = UPDATE_JOB_FORM_XPATH + "//div[contains(@class, 'information-message') and contains(text(), '%s')]";

    private static final String GAS_LEAK_CHECK_QUESTIONS_XPATH = "//us-gas-leak-check-questions";
    private static final String ADD_LEAK_SITE_DETAILS_XPATH = "//div[contains(@class, 'btn') and contains(., 'Add Leak Site Details')]";
    private static final String ADD_ADDITIONAL_LEAK_SITE_DETAILS_XPATH = "//div[contains(@class, 'btn') and contains(., 'Add Additional Leak Site Details')]";

    private static final String BUTTON_XPATH = "//div[@role = 'button' and text()[contains(., ':name')]] | //button[text()=':name'] | //div[contains(@class, 'btn') and text()[contains(., ':name')]]";

    private static final String LEAK_SITE_CHECKS_XPATH = "//div[@id='LeakSiteData']//div[contains(text(), 'Leak Site Check')]//span[contains(@class, 'gas-section__header-part2')]/..";

    private static final String GAS_CYLINDER_SECTION = UPDATE_JOB_FORM_XPATH + "//div[contains(@class, 'gas-section gas-source-data')]";
    private static final String CYLINDERS_XPATH = "//span[contains(@class, 'gas-section__header-part2')]";
    private static final String GAS_SOURCE_XPATH = "//div[text()='Refrigerant Source']/following-sibling::div[@class='row'][1]/div[contains(@class, 'col-md-3')][1]";
    private static final String GAS_FULL_PARTIAL_XPATH = "//div[text()='Refrigerant Source']/following-sibling::div[@class='row'][1]/div[contains(@class, 'col-md-3')][2]";
    private static final String GAS_CYLINDER_TYPE_XPATH = "//div[text()='Refrigerant Source']/following-sibling::div[@class='row'][1]/div[contains(@class, 'col-md-6')]";
    private static final String GAS_LBS_AVAILABLE_XPATH = "//div[text()='Refrigerant Installed']/following-sibling::div[@class='row']/div[contains(@class, 'col-md-3')][1]";
    private static final String GAS_LBS_INSTALLED_XPATH = "//div[text()='Refrigerant Installed']/following-sibling::div[@class='row']/div[contains(@class, 'col-md-3')][2]";
    private static final String GAS_SURPLUS_XPATH = "//div[text()='Refrigerant Installed']/following-sibling::div[@class='row']/div[contains(@class, 'col-md-6')]";
    private static final String GAS_DESTINATION_XPATH = "//div[text()='Refrigerant Surplus']/following-sibling::div[@class='row']/div[contains(@class, 'col-md-3')][1]";
    private static final String GAS_SURPLUS_TYPE_XPATH = "//div[text()='Refrigerant Surplus']/following-sibling::div[@class='row']/div[contains(@class, 'col-md-3')][2]";

    private static final String QUESTION_XPATH = "//label[text() = '%s']";

    // Job Photos
    private static final String JOB_PHOTOS_ALERT_XPATH = "//div[contains(@class,'sweet-alert')]//h2[contains(text(),'Job photos are required')]";
    private static final String JOB_PHOTOS_ALERT_OK_BUTTON_XPATH = "//div[contains(@class,'sweet-alert')]//button[@class='confirm' and contains(text(),'OK')]";
    private static final String UPLOAD_JOB_PHOTOS_INPUT_XPATH = "//div[@class='form-group']//h3[contains(text(),'Job Photos')]/../div";
    private static final String UPLOAD_JOB_PHOTOS_DROPZONE_XPATH = "//div[@class='form-group']//h3[contains(text(),'Job Photos')]/../div//div[@class='dropzone']";
    private static final String SELECT_JOB_PHOTO_XPATH = "//div[@class='form-group']//*[contains(text(), 'Select Photo')]";
    private static final String SELECT_JOB_PHOTO_INPUT_XPATH = "//div[@class='form-group']//div//mc-photo-picker//input";

    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(id = UPDATE_JOB_FORM_ID)
    private WebElement updateJobForm;

    @FindBy(css = REMOTE_JOB_CSS)
    private WebElement remoteJob;

    @FindBy(xpath = TRAVEL_TIME_DROPDOWN_XPATH)
    private WebElement timeTravelDropdown;

    @FindBy(xpath = WORK_START_DATE_XPATH)
    private WebElement workStartDate;

    @FindBy(xpath = WORK_START_DATE_DROPDOWN_XPATH)
    private WebElement workStartDateDropdown;

    @FindBy(xpath = WORK_START_TIME_DROPDOWN_XPATH)
    private WebElement workStartTimeDropdown;

    @FindBy(xpath = WORK_END_TIME_DROPDOWN_XPATH)
    private WebElement workEndTimeDropdown;

    @FindBy(xpath = WORK_END_TIME_VALUE_XPATH)
    private WebElement workEndTime;

    @FindBy(xpath = TIME_SPENT_DROPDOWN_XPATH)
    private WebElement timeSpentDropdown;

    @FindBy(xpath = OVERTIME_DROPDOWN_XPATH)
    private WebElement workOverTimeDropdown;

    @FindBy(xpath = STATUS_ON_DEPATURE_DROPDOWN_ARROW_XPATH)
    private WebElement statusOnDeparture;

    @FindBy(xpath = ASSET_CONDITON_DROPDOWN_XPATH)
    private WebElement assetCondition;

    @FindBy(xpath = ROOT_CAUSE_CATEGORY_DROPDOWN_XPATH)
    private WebElement rootCauseCategory;

    @FindBy(xpath = ROOT_CAUSE_DROPDOWN_XPATH)
    private WebElement rootCause;

    @FindBy(xpath = ROOT_CAUSE_DESCRIPTION_XPATH)
    private WebElement rootCauseDescription;

    @FindBy(xpath = ADDITIONAL_NOTES_XPATH)
    private WebElement additionalNotes;

    @FindBy(xpath = EXTRA_TO_CONTRACT_NOTES_XPATH)
    private WebElement extraToContractNotes;

    @FindBy(xpath = LEAK_CHECK_REPAIR_XPATH)
    private WebElement leakCheckRepair;

    //GSAN
    @FindBy(css = GSAN_REFERENCE_NUMBER_CSS)
    private WebElement gsanReferenceNumber;

    @FindBy(xpath = GSAN_TYPE_XPATH)
    private WebElement gsanType;

    // Gas
    @FindBy(xpath = GAS_OTHER_ASSET_PLANT_NOTES_XPATH)
    private WebElement otherAssetPlantNotes;

    @FindBy(xpath = GAS_OTHER_GAS_TYPE_NOTES_XPATH)
    private WebElement otherGasTypeNotes;

    @FindBy(xpath = GAS_OTHER_ASSET_NOTES_XPATH)
    private WebElement otherAssetNotes;

    @FindBy(xpath = GAS_SELECT_A_RACK_XPATH)
    private WebElement gasSelectARack;

    @FindBy(xpath = GAS_SELECT_AN_ASSET_XPATH)
    private WebElement selectAnAsset;

    @FindBy(xpath = GAS_GAS_TYPE_XPATH)
    private WebElement gasType;

    @FindBy(xpath = GAS_OTHER_GAS_TYPE_XPATH)
    private WebElement otherGasType;

    @FindBy(xpath = GAS_GAS_LEAKAGE_CODE_XPATH)
    private WebElement gasLeakageCode;

    @FindBy(xpath = GAS_GAS_LEAKAGE_CHECK_METHOD_XPATH)
    private WebElement gasLeakageCheckMethod;

    @FindBy(xpath = GAS_GAS_LEAK_LOCATION_XPATH)
    private WebElement gasLeakLocation;

    @FindBy(xpath = GAS_ACTION_XPATH)
    private WebElement action;

    @FindBy(xpath = GAS_FAULT_CODE_XPATH)
    private WebElement faultCode;

    // Additional Resource Required
    @FindBy(css = ADDITIONAL_RESOURCE_REQUIRED_CSS)
    private WebElement additionalResourceRequired;

    @FindBy(xpath = RESOURCE_PROFILE_DROPDOWN_XPATH)
    private WebElement resourceProfile;

    @FindBy(xpath = ADDITIONAL_RESOURCE_NOTES_XPATH)
    private WebElement additionalResourceNotes;

    // Quote Sections
    @FindBy(css = QUOTE_REQUEST_QUOTE_CSS)
    private WebElement quoteRequest;

    @FindBy(css = QUOTE_FORM_CSS)
    private WebElement quoteForm;

    @FindBy(xpath = QUOTE_SCOPE_OF_WORK_XPATH)
    private WebElement scopeOfWork;

    @FindBy(xpath = QUOTE_PRIORITY_DROPDOWN_XPATH)
    private WebElement quotePriority;

    // Returning Section
    @FindBy(xpath = REASON_FOR_RETURN_DROPDOWN_XPATH)
    private WebElement reasonForReturning;

    @FindBy(xpath = ETA_DATE_DROPDOWN_XPATH) private WebElement etaDate;

    @FindBy(xpath = ETA_DATE_SEARCH_XPATH)
    private WebElement etaDateSearch;

    @FindBy(xpath = ETA_DATE_PICKER_CONTAINER_XPATH)
    private WebElement eTADatePickerContainer;

    @FindBy(xpath = ETA_WINDOW_DROPDOWN_XPATH)
    private WebElement etaWindow;

    @FindBy(xpath = ETA_WINDOW_SEARCH_XPATH)
    private WebElement etaWindowSearch;

    // Selected Values
    @FindBy(css = TRAVEL_TIME_SELECTED_VALUE_CSS)
    private WebElement travelTimeSelectedValue;

    @FindBy(css = WORK_START_TIME_SELECTED_VALUE_CSS)
    private WebElement workStartTimeSelectedValue;

    @FindBy(css = WORK_END_TIME_SELECTED_VALUE_CSS)
    private WebElement workEndTimeSelectedValue;

    @FindBy(css = OVERTIME_SELECTED_VALUE_CSS)
    private WebElement overTimeSelectedValue;

    @FindBy(css = STATUS_ON_DEPARTURE_SELECTED_VALUE_CSS)
    private WebElement statusOnDepartureSelectedValue;

    @FindBy(css = ASSET_CONDITION_SELECTED_VALUE_CSS)
    private WebElement assetConditionSelectedValue;

    @FindBy(css = ROOT_CAUSE_CATEGORY_SELECTED_VALUE_CSS)
    private WebElement rootCauseCategorySelectedValue;

    @FindBy(css = ROOT_CAUSE_SELECTED_VALUE_CSS)
    private WebElement rootCauseSelectedValue;

    // GAS SELECTED VALUES
    @FindBy(css = GAS_SELECT_A_RACK_SELECTED_VALUE_CSS)
    private WebElement gasSelectARackSelectedValue;

    @FindBy(css = GAS_SELECT_AN_ASSET_SELECTED_VALUE_CSS)
    private WebElement selectAnAssetSelectedValue;

    @FindBy(css = GAS_GAS_TYPE_SELECTED_VALUE_CSS)
    private WebElement gasTypeSelectedValue;

    @FindBy(css = GAS_ALL_GAS_TYPE_SELECTED_VALUE_CSS)
    private WebElement allGasTypeSelectedValue;

    @FindBy(css = GAS_GAS_LEAKAGE_CODE_SELECTED_VALUE_CSS)
    private WebElement gasLeakageCodeSelectedValue;

    @FindBy(css = GAS_GAS_LEAKAGE_CHECK_METHOD_SELECTED_VALUE_CSS)
    private WebElement gasLeakageCheckMethodSelectedValue;

    @FindBy(css = GAS_GAS_LEAK_LOCATION_SELECTED_VALUE_CSS)
    private WebElement gasLeakLocationSelectedValue;

    @FindBy(css = GAS_ACTION_SELECTED_VALUE_CSS)
    private WebElement actionSelectedValue;

    @FindBy(css = GAS_FAULT_CODE_SELECTED_VALUE_CSS)
    private WebElement faultCodeSelectedValue;

    @FindBy(css = QUOTE_ASSET_MAIN_TYPE_SELECTED_VALUE_CSS)
    private WebElement quoteAssetMainTypeSelectedValue;

    @FindBy(css = QUOTE_ASSET_SUB_TYPE_SELECTED_VALUE_CSS)
    private WebElement quoteAssetSubTypeSelectedValue;

    @FindBy(css = QUOTE_ASSET_CLASSIFICATION_SELECTED_VALUE_CSS)
    private WebElement quoteAssetClassificationSelectedValue;

    @FindBy(css = QUOTE_ASSET_SELECTED_VALUE_CSS)
    private WebElement quoteAssetSelectedValue;

    @FindBy(css = QUOTE_LOCATION_SELECTED_VALUE_CSS)
    private WebElement quoteLocationSelectedValue;

    @FindBy(css = QUOTE_FAULT_TYPE_SELECTED_VALUE_CSS)
    private WebElement quoteFaultTypeSelectedValue;

    @FindBy(css = REASON_FOR_RETURN_VALUE_CSS)
    private WebElement reasonForReturningSelectedValue;

    @FindBy(css = ETA_SELECTED_VALUE_CSS)
    private WebElement returnEtaDate;

    // Tooltips
    @FindBy(xpath = TRAVEL_TIME_DROPDOWN_TOOLTIP_ERROR_XPATH)
    private WebElement travelTimeToolTip;

    @FindBy(xpath = WORK_START_TIME_TOOLTIP_ERROR_XPATH)
    private WebElement workStartToolTip;

    @FindBy(xpath = TIME_SPENT_TOOLTIP_ERROR_XPATH)
    private WebElement timeSpentToolTip;

    @FindBy(xpath = WORK_END_TIME_TOOLTIP_ERROR_XPATH)
    private WebElement workEndToolTip;

    @FindBy(xpath = STATUS_ON_DEPATURE_TOOLTIP_ERROR_XPATH)
    private WebElement statusOnDepartureToolTip;

    @FindBy(xpath = GAS_LEAK_CHECK_QUESTIONS_XPATH)
    private WebElement gasLeakCheckQuestions;

    @FindBy(xpath = ADD_LEAK_SITE_DETAILS_XPATH)
    private WebElement addLeakSiteDetails;

    @FindBy(xpath = ADD_ADDITIONAL_LEAK_SITE_DETAILS_XPATH)
    private WebElement addAdditionalLeakSiteDetails;

    // Leak Checks
    @FindBy(xpath = PRIMARY_COMPONENTS_XPATH)
    private List<WebElement> primaryComponents;

    @FindBy(xpath = SUB_COMPONENTS_XPATH)
    private List<WebElement> subComponents;

    @FindBy(xpath = PRIMARY_COMPONENTS_INFO_XPATH)
    private List<WebElement> primaryComponentsInfo;

    @FindBy(xpath = LEAK_SITE_STATUSES_XPATH)
    private List<WebElement> leakSiteStatuses;

    @FindBy(xpath = INITIAL_TESTS_XPATH)
    private List<WebElement> initialTests;

    @FindBy(xpath = FOLLOW_UP_TESTS_XPATH)
    private List<WebElement> followUpTests;

    @FindBy(xpath = FOLLOW_UP_BUTTON_XPATH)
    private WebElement followUp;

    @FindBy(xpath = GAS_CYLINDER_SECTION)
    private List<WebElement> gasCylinderSection;

    @FindBy(xpath = JOB_PHOTOS_ALERT_XPATH)
    private WebElement jobPhotosAlert;

    @FindBy(xpath = JOB_PHOTOS_ALERT_OK_BUTTON_XPATH)
    private WebElement jobPhotosAlertOKButton;

    @FindBy(xpath = UPLOAD_JOB_PHOTOS_INPUT_XPATH)
    private WebElement uploadJobPhotosInput;

    @FindBy(xpath = UPLOAD_JOB_PHOTOS_DROPZONE_XPATH)
    private WebElement uploadJobPhotosDropzone;

    @FindBy(xpath = SELECT_JOB_PHOTO_INPUT_XPATH)
    private WebElement uploadJobPhoto;

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.id(UPDATE_JOB_FORM_ID));
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

    public UpdateJobPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    // Page Interactions
    private Integer selectListOptionCount() {
        List<WebElement> visibleOptions = driver.findElements((By.xpath(VISIBLE_DROPDOWN_OPTIONS_XPATH)));
        return visibleOptions.size();
    }

    public boolean isQuestionVisible(String question) {
        return isElementVisible(By.xpath(String.format(QUESTION_LABEL, question)));
    }

    public void selectRemoteJob() {
        remoteJob.click();
        waitForAngularRequestsToFinish();
    }

    public void selectRandomTravelTime() {
        timeTravelDropdown.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectRandomWorkStartTime() {
        workStartTimeDropdown.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectRandomWorkEndTime() {
        workEndTimeDropdown.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void enterWorkStartDate(String date) throws InterruptedException {
        this.waitForAngularRequestsToFinish();
        workStartDate.sendKeys(date);
        workStartDate.sendKeys(Keys.TAB);
    }

    public void selectWorkStartDate(String date) throws InterruptedException {
        this.waitForAngularRequestsToFinish();
        selectCalendarDate(workStartDateDropdown, date);
    }

    public void selecWorkStartTime(String workStartTime) throws ParseException, InterruptedException {
        workStartTimeDropdown.click();
        waitForAngularRequestsToFinish();
        selectExactVisibleDropdownOption(workStartTime.toUpperCase());
    }

    public void selectRandomTimeSpent() {
        timeSpentDropdown.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectTimeSpent(String timeSpent) {
        timeSpentDropdown.click();
        waitForAngularRequestsToFinish();
        selectExactVisibleDropdownOption(timeSpent);
    }

    public void selectRandomWorkOverTime() {
        workOverTimeDropdown.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectStatusOnDeparture(String option) throws InterruptedException {

        statusOnDeparture.click();
        try {
            selectExactVisibleDropdownOption(option);
        } catch (Exception e) {
            statusOnDeparture.click();
            selectVisibleDropdownOption(option);
        }
    }

    public boolean isStatusOnDepartureDisabled() {
        WebElement we = driver.findElement(By.xpath(STATUS_ON_DEPATURE_DROPDOWN_XPATH));
        String attr = we.getAttribute("aria-disabled");
        return attr != null ? attr.equals("true") : false;
    }

    //GSAN Section
    public void enterGSANReferenceNumber(String referenceNumber) {
        gsanReferenceNumber.sendKeys(referenceNumber);
    }

    public void selectRandomGSANType() {
        gsanType.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    // Gas section
    public void enterOtherAssetPlantNotes(String notes) {
        otherAssetPlantNotes.sendKeys(notes);
    }

    public void enterOtherGasTypeNotes(String notes) {
        otherGasTypeNotes.sendKeys(notes);
    }

    public void enterOtherAssetNotes(String notes) {
        otherAssetNotes.sendKeys(notes);
    }

    public void selectRandomGasSelectARack() {
        gasSelectARack.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectRandomSelectAnAsset() {
        selectAnAsset.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selecSelectAnAsset(String option) throws InterruptedException {
        selectAnAsset.click();
        waitForAnimation(1);
        this.selectVisibleDropdownOption(option);
    }

    public void selectRandomGasType() {
        gasType.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectRandomOtherGasType() {
        otherGasType.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption(1);
    }

    public void selectRandomGasLeakageCode() {
        gasLeakageCode.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption(1);
    }

    public void selectRandomGasLeakageCheckMethod() {
        gasLeakageCheckMethod.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption(1);
    }

    public void selectRandomGasLeakLocation() {
        gasLeakLocation.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption(1);
    }

    public void selectRandomAction() {
        action.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption(1);
    }

    public void selectRandomFaultCode() {
        faultCode.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption(1);
    }

    public void selectLeakTestRepair(String option) {
        leakCheckRepair.click();
        waitForAnimation(1);
        this.selectVisibleDropdownOption(option);
    }

    public void enterGasUsageBottleNumber(Integer gasUsage) {
        WebElement bottleNumber = waitForElement(By.xpath(String.format(GAS_GAS_USAGE_BOTTLE_NUMBER_XPATH, gasUsage)), State.ELEMENT_IS_CLICKABLE);
        bottleNumber.sendKeys("1234");
    }

    public void enterGasUsageBottleQuantity(Integer gasUsage) {
        WebElement bottleNumber = waitForElement(By.xpath(String.format(GAS_GAS_USAGE_BOTTLE_QUANTITY_XPATH, gasUsage)), State.ELEMENT_IS_CLICKABLE);
        bottleNumber.sendKeys("1");
    }

    public void clickYes(String question) {
        WebElement yes = waitForElement(By.xpath(String.format(UPDATE_JOB_QUESTIONS_YES_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        yes.click();
        waitForAngularRequestsToFinish();
    }

    public void clickNo(String question) {
        WebElement no = waitForElement(By.xpath(String.format(UPDATE_JOB_QUESTIONS_NO_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        no.click();
        waitForAngularRequestsToFinish();
    }

    public void clickButtonAnswer(String question, String answer) {
        WebElement answerButton = waitForElement(By.xpath(String.format(UPDATE_JOB_QUESTIONS_ANSWER_XPATH, question, answer)), State.ELEMENT_IS_CLICKABLE);
        POHelper.scrollTo(POHelper.getElementHeight(By.xpath(String.format(UPDATE_JOB_QUESTIONS_ANSWER_XPATH, question, answer)))+50);
        answerButton.click();
        waitForAngularRequestsToFinish();
    }

    public void selectRandomAssetCondition() {
        assetCondition.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectRandomRootCauseCategory() {
        rootCauseCategory.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectRandomRootCause() {
        rootCause.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public boolean getRootCauseDescriptionVisible() {
        try {
            driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
            boolean isDisplayed = rootCauseDescription.isDisplayed();
            driver.manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
            return isDisplayed;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void updateRootCauseDescription(String notes) {
        rootCauseDescription.sendKeys(notes);
    }

    public void updateAdditionalNotes(String notes) {
        additionalNotes.sendKeys(notes);
    }

    public void updateExtraToContractNotes(String notes) {
        extraToContractNotes.sendKeys(notes);
    }

    public void updateNotes(String notes) {
        if (isElementClickable(By.cssSelector(ADDITIONAL_NOTES_VALUE_CSS))) {
            updateAdditionalNotes(notes);
        }
        if (isElementClickable(By.cssSelector(EXTRA_TO_CONTRACT_NOTES_CSS))) {
            updateExtraToContractNotes(notes);
        }
    }

    public void selectAdditionalResourceRequired() {
        additionalResourceRequired.click();
        waitForAngularRequestsToFinish();
    }

    public void selectRandomResourceProfile() {
        resourceProfile.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectResourceProfile(String resourceProfile) {
        this.resourceProfile.click();
        waitForAngularRequestsToFinish();

        WebElement visibleOption = driver.findElement(By.xpath(String.format(DROPDOWN_OPTION_XPATH, resourceProfile)));
        POHelper.clickJavascript(visibleOption);
    }

    public void updateAdditionalReourceNotes(String notes) {
        additionalResourceNotes.sendKeys(notes);
    }

    public void selectRequestQuote() {
        quoteRequest.click();
        waitForAngularRequestsToFinish();
    }

    public boolean getQuoteFormVisible() {
        try {
            return quoteForm.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void updateScopeOfWork(String workScope) {
        scopeOfWork.sendKeys(workScope);
    }

    public void clickAnswer(String question, String answer) {
        WebElement element = waitForElement(By.xpath(String.format(RADIO_BUTTON_QUESTION_ANSWER_XPATH, question, answer)), State.ELEMENT_IS_CLICKABLE);
        element.click();
        waitForAngularRequestsToFinish();
    }

    public void selectQuoteType(String type) {
        String xpath = String.format("//mc-portal-quote-budget-driven-questions//label[text() = '%s']/preceding-sibling::*[1][self::input]", type);
        WebElement we = driver.findElement(By.xpath(xpath));
        POHelper.clickJavascript(we);
        waitForAngularRequestsToFinish();
    }

    public void selectQuotePriority(String quotePriority) throws InterruptedException {
        POHelper.scrollToElement(this.quotePriority);
        this.quotePriority.click();
        waitForAngularRequestsToFinish();

        WebElement visibleOption = driver.findElement(By.xpath(String.format(DROPDOWN_OPTION_XPATH, quotePriority)));
        POHelper.clickJavascript(visibleOption);
    }

    public void selectRandomQuotePriority() {
        this.quotePriority.click();
        this.waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectRandomReasonForReturning() {
        POHelper.scrollToElement(reasonForReturning);
        reasonForReturning.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void enterETADate(String date) throws InterruptedException {
        POHelper.scrollToElement(etaDateSearch);
        etaDateSearch.clear();
        this.waitForAngularRequestsToFinish();
        if (!date.isEmpty()) {
            etaDateSearch.sendKeys(date);
            etaDateSearch.sendKeys(Keys.TAB);
        }
    }

    public void selectETADate(String etaDate) throws InterruptedException {
        etaDateSearch.clear();
        waitForAngularRequestsToFinish();
        selectCalendarDate(this.etaDate, etaDate);
        waitForAngularRequestsToFinish();
    }

    public void selectRandomETAWindow() {
        if ( !isDropdownDisplayed() ) {
            etaWindow.click();
        }
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
        waitForAngularRequestsToFinish();
    }

    public void updateJob() {
        updateJobForm.submit();
        waitForAngularRequestsToFinish();
    }

    // Get Values - Main
    public String getTravelTime() {
        return travelTimeSelectedValue.getAttribute("value");
    }

    public String getWorkStartTime() {
        return workStartTimeSelectedValue.getAttribute("value");
    }

    public String getWorkEndTime() {
        return workEndTime.getText();
    }

    public String getOverTime() {
        return overTimeSelectedValue.getAttribute("value");
    }

    public String getStatusOnDeparture() {
        return statusOnDepartureSelectedValue.getText();
    }

    public String getAssetCondition() {
        return assetConditionSelectedValue.getText();
    }

    public String getRootCauseCategory() {
        return rootCauseCategorySelectedValue.getText();
    }

    public String getRootCause() {
        return rootCauseSelectedValue.getText();
    }

    public Integer getRootCauseSize() {
        rootCause.click();
        waitForAngularRequestsToFinish();
        Integer rootCauseSize = selectListOptionCount();
        rootCause.click();
        waitForAngularRequestsToFinish();
        return rootCauseSize;
    }

    public Boolean isLeakTestRepairVisible() {
        return this.leakCheckRepair.isDisplayed();
    }

    public Boolean isLeakTestRepairPresent() {
        return isElementPresent(By.xpath(LEAK_CHECK_REPAIR_XPATH));
    }

    // Get Values - Quote Form
    public String getQuoteAssetMainTypeSelectedValue() {
        return quoteAssetMainTypeSelectedValue.getText();
    }

    public String getQuoteAssetSubTypeSelectedValue() {
        return quoteAssetSubTypeSelectedValue.getText();
    }

    public String getQuoteAssetClassificationSelectedValue() {
        return quoteAssetClassificationSelectedValue.getText();
    }

    public String getQuoteAssetSelectedValue() {
        return quoteAssetSelectedValue.getText();
    }

    public String getQuoteLocationSelectedValue() {
        return quoteLocationSelectedValue.getText();
    }

    public String getQuoteFaultTypeSelectedValue() {
        return quoteFaultTypeSelectedValue.getText();
    }

    public String getScopeOfWork() {
        return scopeOfWork.getText();
    }

    // Get Values - Reason For Returning
    public String getReasonForReturningSelectedValue() {
        return reasonForReturningSelectedValue.getText();
    }

    public String getETADate() {
        return etaDateSearch.getAttribute("value");
    }

    public String getETAWindow() {
        return this.etaWindowSearch.getText();
    }

    // Get Values - gas questions
    public String getGasSelectARackSelectedValue() {
        return gasSelectARackSelectedValue.getText();
    }

    public String getSelectAnAssetSelectedValue() {
        return selectAnAssetSelectedValue.getText();
    }

    public String getGasTypeSelectedValue() {
        return gasTypeSelectedValue.getText();
    }

    public String getAllGasTypeSelectedValue() {
        return allGasTypeSelectedValue.getText();
    }

    public String getGasLeakageCodeSelectedValue() {
        return gasLeakageCodeSelectedValue.getText();
    }

    public String getGasLeakageCheckMethodSelectedValue() {
        return gasLeakageCheckMethodSelectedValue.getText();
    }

    public String getGasLeakLocationSelectedValue() {
        return gasLeakLocationSelectedValue.getText();
    }

    public String getActionSelectedValue() {
        return actionSelectedValue.getText();
    }

    public String getFaultCodeSelectedValue() {
        return faultCodeSelectedValue.getText();
    }

    // Tool tips
    public boolean isTravelTimeToolTipVisible() {
        this.waitForElement(By.xpath(TRAVEL_TIME_DROPDOWN_TOOLTIP_ERROR_XPATH), State.ELEMENT_IS_VISIBLE);
        return travelTimeToolTip.isDisplayed();
    }

    public boolean isWorkStartToolTipVisible() {
        this.waitForElement(By.xpath(WORK_START_TIME_TOOLTIP_ERROR_XPATH), State.ELEMENT_IS_VISIBLE);
        return workStartToolTip.isDisplayed();
    }

    public boolean isTimeSpentToolTipVisible() {
        this.waitForElement(By.xpath(TIME_SPENT_TOOLTIP_ERROR_XPATH), State.ELEMENT_IS_VISIBLE);
        return timeSpentToolTip.isDisplayed();
    }

    public boolean isWorkEndToolTipVisible() {
        this.waitForElement(By.xpath(WORK_END_TIME_TOOLTIP_ERROR_XPATH), State.ELEMENT_IS_VISIBLE);
        return workEndToolTip.isDisplayed();
    }

    public boolean isStatusOnDepartureToolTipVisible() {
        this.waitForElement(By.xpath(STATUS_ON_DEPATURE_TOOLTIP_ERROR_XPATH), State.ELEMENT_IS_VISIBLE);
        return statusOnDepartureToolTip.isDisplayed();
    }

    public boolean theErrorMessageIsDisplayed(String question, String errorMessage) {
        WebElement tooltip = driver.findElement(By.xpath(String.format(FORM_CONTROL_TOOLTIP_ERROR_XPATH, question, errorMessage)));
        return tooltip.isDisplayed();
    }

    public boolean controlToolTipIsDisplayed(String question) {
        return isElementVisible(By.xpath(String.format(CONTROL_TOOLTIP_ERROR_XPATH, question)));
    }

    public boolean isInformationMessageDisplayed(String text) {
        WebElement infoMessage = driver.findElement(By.xpath(String.format(INFORMATION_MESSAGE_XPATH, text)));
        return infoMessage.isDisplayed();
    }

    public void addLeakSiteDetails() {
        addLeakSiteDetails.click();
        waitForElement(By.xpath("//label[text()='Primary Component']"), State.ELEMENT_IS_VISIBLE);
        waitForAngularRequestsToFinish();
        waitWhileBusy();
    }

    public void addAdditionalLeakSiteDetails() {
        addAdditionalLeakSiteDetails.click();
        waitForAngularRequestsToFinish();
        waitWhileBusy();
    }

    public QuestionHelper getQuestionHelper() {
        QuestionHelper questionHelper = PageFactory.initElements(driver, QuestionHelper.class);
        questionHelper.load(UPDATE_JOB_FORM_XPATH);
        return questionHelper;
    }

    public void scrollToGasLeakCheckQuestions() {
        POHelper.scrollToElement(gasLeakCheckQuestions);
    }

    private List<String> getLeakChecks(String xpath, List<WebElement> webElements) {
        List<String> results = new ArrayList<>();
        if (isElementPresent(By.xpath(xpath))) {
            for (WebElement we : webElements) {
                results.add(we.getText().trim());
            }
        }
        return results;
    }

    public List<String> getPrimaryComponents() {
        return getLeakChecks(PRIMARY_COMPONENTS_XPATH, primaryComponents);
    }

    public List<String> getSubComponents() {
        return getLeakChecks(SUB_COMPONENTS_XPATH, subComponents);
    }

    public List<String> getPrimaryComponentsInfo() {
        return getLeakChecks(PRIMARY_COMPONENTS_INFO_XPATH, primaryComponentsInfo);
    }

    public List<String> getLeakSiteStatuses() {
        return getLeakChecks(LEAK_SITE_STATUSES_XPATH, leakSiteStatuses);
    }

    public List<String> getInitialTests() {
        return getLeakChecks(INITIAL_TESTS_XPATH, initialTests);
    }

    public List<String> getFollowUpTests() {
        return getLeakChecks(FOLLOW_UP_TESTS_XPATH, followUpTests);
    }

    public Boolean isButtonDisplayed(String name) {
        String xpath = BUTTON_XPATH.replaceAll(":name", name);
        return isElementPresent(By.xpath(xpath));
    }

    public AdminLeakSiteInformationModal followUp() {
        followUp.click();
        waitForAngularRequestsToFinish();
        waitWhileBusy();
        return PageFactory.initElements(driver, AdminLeakSiteInformationModal.class).get();
    }

    public List<String> getLeakSiteChecks(){
        List<String> result = new ArrayList<>();

        if (!isElementPresent(By.xpath(LEAK_SITE_CHECKS_XPATH))) return null;

        List<WebElement> webElements = driver.findElements(By.xpath(LEAK_SITE_CHECKS_XPATH));
        for (WebElement we : webElements) {
            result.add(we.getText());
        }
        return result;
    }


    public List<String> getGasSectionDetails(String target) {
        List<String> results = new ArrayList<>();
        if (isElementVisible(By.xpath(GAS_CYLINDER_SECTION))) {
            for (int i = 1; i <= gasCylinderSection.size(); i++) {
                String xpath = String.format(GAS_CYLINDER_SECTION + "[%d]" + target, i);
                if (isElementPresent(By.xpath(xpath))) {
                    results.add(driver.findElement(By.xpath(xpath)).getText());
                } else {
                    results.add("");
                }
            }
        }
        return results;
    }

    public List<String> getCylinderSerialNumbers() {
        List<String> serialNumbers = new ArrayList<>();
        for (String serialNumber : getGasSectionDetails(CYLINDERS_XPATH)) {
            serialNumbers.add(serialNumber.replaceAll("^- ", "").trim());
        }
        return serialNumbers;
    }

    public List<String> getSources() {
        return getGasSectionDetails(GAS_SOURCE_XPATH);
    }

    public List<String> getFullPartial() {
        return getGasSectionDetails(GAS_FULL_PARTIAL_XPATH);
    }

    public List<String> getCylinderTypes() {
        return getGasSectionDetails(GAS_CYLINDER_TYPE_XPATH);
    }

    public List<String> getGasAvailable() {
        return getGasSectionDetails(GAS_LBS_AVAILABLE_XPATH);
    }

    public List<String> getGasInstalled() {
        return getGasSectionDetails(GAS_LBS_INSTALLED_XPATH);
    }

    public List<String> getSurplus() {
        return getGasSectionDetails(GAS_SURPLUS_XPATH);
    }

    public List<String> getDestinations() {
        return getGasSectionDetails(GAS_DESTINATION_XPATH);
    }

    public List<String> getSurplusTypes() {
        return getGasSectionDetails(GAS_SURPLUS_TYPE_XPATH);
    }

    public boolean isDisplayed() {
        return updateJobForm.isDisplayed();
    }

    public boolean isQuestionDisplayed(String question) {
        return isElementVisible(By.xpath(String.format(QUESTION_XPATH, question)));
    }

    public boolean isJobPhotosBoxDisplayed() {
        return isElementVisible(By.xpath(UPLOAD_JOB_PHOTOS_DROPZONE_XPATH));
    }

    public boolean isSelectJobPhotoButtonDisplayed() {
        return isElementVisible(By.xpath(SELECT_JOB_PHOTO_XPATH));
    }

    public boolean isUploadJobPhotosAlertDisplayed() {
        return isElementVisible(By.xpath(JOB_PHOTOS_ALERT_XPATH));
    }

    public void clickJobPhotosAlertOKButton() {
        WebElement we = driver.findElement(By.xpath(JOB_PHOTOS_ALERT_OK_BUTTON_XPATH ));
        we.click();
    }

    public void uploadJobPhotos(String filePath) {
        uploadJobPhoto.sendKeys(filePath);
    }

    public void dropFile(String pathname) {
        File filePath = new File(pathname);
        if(!filePath.exists()) throw new WebDriverException("File not found: " + pathname);

        int offsetX = 10;
        int offsetY = 10;

        WebElement we = driver.findElement(By.xpath(UPLOAD_JOB_PHOTOS_DROPZONE_XPATH));
        WebDriver driver = ((RemoteWebElement) we).getWrappedDriver();
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        WebDriverWait wait = new WebDriverWait(driver, 30);

        String JS_DROP_FILE =
                "var target = arguments[0]," +
                        "    offsetX = arguments[1]," +
                        "    offsetY = arguments[2]," +
                        "    document = target.ownerDocument || document," +
                        "    window = document.defaultView || window;" +
                        "" +
                        "var input = document.createElement('INPUT');" +
                        "input.type = 'file';" +
                        "input.style.display = 'none';" +
                        "input.onchange = function () {" +
                        "  var rect = target.getBoundingClientRect()," +
                        "      x = rect.left + (offsetX || (rect.width >> 1))," +
                        "      y = rect.top + (offsetY || (rect.height >> 1))," +
                        "      dataTransfer = { files: this.files };" +
                        "" +
                        "  ['dragenter', 'dragover', 'drop'].forEach(function (name) {" +
                        "    var evt = document.createEvent('MouseEvent');" +
                        "    evt.initMouseEvent(name, !0, !0, window, 0, 0, 0, x, y, !1, !1, !1, !1, 0, null);" +
                        "    evt.dataTransfer = dataTransfer;" +
                        "    target.dispatchEvent(evt);" +
                        "  });" +
                        "" +
                        "  setTimeout(function () { document.body.removeChild(input); }, 25);" +
                        "};" +
                        "document.body.appendChild(input);" +
                        "return input;";

        WebElement input =  (WebElement)jse.executeScript(JS_DROP_FILE, uploadJobPhotosDropzone, offsetX, offsetY);
        input.sendKeys(filePath.getAbsoluteFile().toString());
        wait.until(ExpectedConditions.stalenessOf(input));
    }

    public void selectPotentialInsuranceQuote(boolean isPotentialInsuranceQuote) {
        String id = isPotentialInsuranceQuote ? "isPotentialInsuranceYes" : "isPotentialInsuranceNo";
        WebElement we = driver.findElement(By.id(id));
        POHelper.clickJavascript(we);
    }

}
