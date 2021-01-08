package mercury.steps.helpdesk.resources;

import static mercury.helpers.Globalisation.CURRENCY_SYMBOL;
import static mercury.helpers.Globalisation.MEDIUM;
import static mercury.helpers.Globalisation.localize;
import static mercury.helpers.Globalisation.toCurrency;
import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.database.dao.FundingRequestDao;
import mercury.database.dao.JobDao;
import mercury.database.dao.JobViewDao;
import mercury.database.dao.ReasonDao;
import mercury.database.dao.ResourceDao;
import mercury.database.models.FundingRequest;
import mercury.database.models.Job;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.LogJobHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperQuotes;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.dbhelper.DbHelperSystemToggles;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.helpdesk.jobs.ConfirmJobChangesModal;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskJobPage;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskLogJobPage;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskPreviousFundingRequestsPanel;
import mercury.pageobject.web.helpdesk.resources.HelpdeskAdditionalResourcePanel;
import mercury.pageobject.web.helpdesk.resources.HelpdeskManageResourcesPanel;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;

public class HelpdeskFundingRequestsSteps {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private CommonSteps commonSteps;
    @Autowired private FundingRequestDao fundingRequestDao;
    @Autowired private ReasonDao reasonDao;
    @Autowired private ResourceDao resourceDao;
    @Autowired private JobDao jobDao;
    @Autowired private TzHelper tzHelper;
    @Autowired private DbHelperQuotes dbHelperQuotes;
    @Autowired private DbHelperSystemToggles dbHelperSystemToggles;
    @Autowired private JobViewDao jobViewDao;

    @Given("^a new uplift funding request is created with ((?:known|unknown)) amount$")
    public void a_new_uplift_funding_request_is_created(String knownAmount) throws Throwable {
        runtimeState.helpdeskFundingRequestsPanel = runtimeState.helpdeskManageResourcesPanel.selectFundingRequestAction();

        if ("KNOWN".equalsIgnoreCase(knownAmount)) {
            float amount = testData.getFloat("upliftAmount") != null ? testData.getFloat("upliftAmount") : DataGenerator.randBetween(200, 500);
            testData.put("upliftAmount", amount);
            runtimeState.helpdeskFundingRequestsPanel.setAmountInFundingRequest(String.valueOf(amount).replaceAll("\\.00?", ""));
            runtimeState.scenario.write("Uplift Amount: " + amount);

        } else if ("UNKNOWN".equalsIgnoreCase(knownAmount)) {
            runtimeState.helpdeskFundingRequestsPanel.clickDontKnowTheAmount();
            String reason = runtimeState.helpdeskFundingRequestsPanel.selectReasonForNoAmount();
            runtimeState.scenario.write("Reason: " + reason);
        }

        String randomDescription = DataGenerator.generateRandomSentence();
        runtimeState.helpdeskFundingRequestsPanel.setDescriptionInFundingRequest(randomDescription);
        testData.put("upliftNotes", randomDescription);
        runtimeState.scenario.write("Description: " + randomDescription);

        outputHelper.takeScreenshots();
        runtimeState.helpdeskFundingRequestsPanel.save();
        runtimeState.helpdeskManageResourcesPanel = new HelpdeskManageResourcesPanel(getWebDriver()).get();
    }

    @And("^a new uplift funding request is created such that job cost exceeds FinanceNotificationValue$")
    public void a_new_uplift_funding_request_is_created_such_that_job_cost_exceeds_financenotificationvalue() throws Throwable {
        BigDecimal financeNotificationValue = dbHelper.getUpliftNotificationToFinanceAmount();
        BigDecimal initialfundingAmount = fundingRequestDao.getByJobReference(testData.getInt("jobReference")).get(0).getRequestedAmount();
        BigDecimal upliftAmount = financeNotificationValue.subtract(initialfundingAmount).add(new BigDecimal(1)).setScale(2);
        BigDecimal jobCost = financeNotificationValue.add(new BigDecimal(1)).setScale(2);
        testData.put("financeNotificationValue", financeNotificationValue);
        testData.put("upliftAmount", upliftAmount);
        testData.put("jobCost", jobCost);
        a_new_uplift_funding_request_is_created("known");
    }

    @ContinueNextStepsOnException
    @Then("^the Additional Resource Required section is shown where user will be able to select an Alternative Resource to pass the job to$")
    public void arr_section_is_shown_where_user_will_be_able_to_select_an_alternative_resource_to_pass_the_job_to() throws Throwable {
        runtimeState.helpdeskAdditionalResourcePanel = new HelpdeskAdditionalResourcePanel(getWebDriver()).get();
        assertTrue("Additional Resource Required section is not displayed",runtimeState.helpdeskAdditionalResourcePanel.isAlternativeAdditionalResourceRequiredDisplayed());
        assertTrue("Individual Resource radiobutton is not displayed",runtimeState.helpdeskAdditionalResourcePanel.isIndividualResourceRadioButtonDisplayed());
        assertTrue("Resource Profile radiobutton is not displayed",runtimeState.helpdeskAdditionalResourcePanel.isResourceProfileRadioButtonDisplayed());
        runtimeState.scenario.write("Additional Resource section with 'Individual Resource' and 'Resource Profile' options is displayed.");
        outputHelper.takeScreenshots();
    }

    @And("^the resultant draft quote job is saved after selecting \"([^\"]*)\" as the quote type$")
    public void the_resultant_draft_quote_job_is_saved_after_selecting_something_as_the_quote_type(String quoteType) throws Throwable {
        runtimeState.helpdeskLogJobPage = new HelpdeskLogJobPage(getWebDriver()).get();
        String aliasquoteType = dbHelperQuotes.getFundingRouteAlias(quoteType);
        quoteType = (aliasquoteType == null) ? quoteType : aliasquoteType;

        String jobType = jobViewDao.getByJobReference(testData.getInt("jobReference")).getJobTypeName();
        testData.addStringTag("change", "Job type changed from " + jobType + " to Quote");
        testData.addStringTag("notes", "Converted from " + jobType + " to Quote by Helpdesk User.");

        if (runtimeState.helpdeskLogJobPage.getClassification().isEmpty()) {
            runtimeState.scenario.write("Selecting random classification ");
            runtimeState.helpdeskLogJobPage.selectRandomClassification();
        }

        if (runtimeState.helpdeskLogJobPage.getLocation().isEmpty()) {
            runtimeState.scenario.write("Selecting random location ");
            runtimeState.helpdeskLogJobPage.selectRandomLocation();
        }

        runtimeState.helpdeskLogJobPage.selectQuoteType(quoteType);

        if (runtimeState.helpdeskLogJobPage.isPotentialInsuranceQuoteQuestionDisplayed()) {
            runtimeState.helpdeskLogJobPage.selectRandomAnswerForIsPotentialInsuranceQuote();
        }

        runtimeState.helpdeskLogJobPage.selectRandomJobDetailPriority();

        if (runtimeState.helpdeskLogJobPage.getFaultType().isEmpty()) {
            runtimeState.scenario.write("Selecting random fault type ");
            runtimeState.helpdeskLogJobPage.selectRandomFault();
        }

        LogJobHelper.answerJobQuestions(runtimeState, testData);

        if (runtimeState.helpdeskLogJobPage.isSameAsCallerButtonVisible()) {
            runtimeState.helpdeskLogJobPage.clickSameAsCaller();
        }

        outputHelper.takeScreenshots();
        runtimeState.helpdeskLogJobPage.save();
        actionWhenAssetNotselected();
        runtimeState.scenario.write("Draft job form is displayed where Quote Type: '" + quoteType + "' is selected and Job is saved");
        runtimeState.helpdeskJobPage = new HelpdeskJobPage(getWebDriver()).get();

        Job job = jobDao.getByJobReference(testData.getInt("jobReference"));
        int siteId = job.getSiteId();
        String rfm = dbHelperSites.getManagerInfoForSiteAndPosition(siteId, "RFM").get("ResourceName").toString();
        runtimeState.scenario.write("RFM for siteId " + siteId + ": " + rfm);
        testData.put("quoteRequestApprover", rfm);
    }

    @And("^the reason for changes made to the job is confirmed$")
    public void the_reason_for_changes_made_to_the_job_is_confirmed() throws Throwable {
        actionWhenAssetNotselected();
        runtimeState.confirmJobChangesModal = new ConfirmJobChangesModal(getWebDriver()).get();
        String reason = "Test reason entered on: " + DateHelper.dateAsString(new Date());
        testData.addStringTag("reason", reason);
        runtimeState.confirmJobChangesModal.enterReason(reason);
        outputHelper.takeScreenshots();
        runtimeState.confirmJobChangesModal.confirm();
    }

    @ContinueNextStepsOnException
    @Then("^a linked quote job is created$")
    public void a_linked_quote_job_is_created() throws Throwable {
        int expectedJobReference = testData.getInt("jobReference");
        runtimeState.helpdeskJobPage = new HelpdeskJobPage(getWebDriver()).get();
        runtimeState.helpdeskLinkedJobsModal = runtimeState.helpdeskJobPage.openLinkedJobsModal();
        Grid grid = runtimeState.helpdeskLinkedJobsModal.getGrid();
        logger.debug("Grid retrieved:" + grid.getHeaders().toString() + " (" + grid.getRows().size() + " rows)");
        List<Row> row = grid.getRows();
        int actualJobReference = Integer.parseInt(row.get(0).getCell("Job Ref.").getText());
        assertTrue("Quote job is not linked to the job whose funding request has been rejected with reason: 'Request a Quote'.", actualJobReference==expectedJobReference);
        runtimeState.scenario.write("Newly created quote job is linked to the job whose funding request was rejected with reason: 'Request a Quote'.");
        outputHelper.takeScreenshots();
        runtimeState.helpdeskJobPage = runtimeState.helpdeskLinkedJobsModal.closeLinkedJobsModal();
    }

    @When("^the ((?:initial|uplift)) funding request is rejected for \"([^\"]*)\" resource with reason \"([^\"]*)\" and notes$")
    public void the_funding_request_is_rejected_for_resource_with_reason_and_notes(String fundingRequestType, String resource, String rejectionReason) throws Throwable {

        selectFundingRequestforResource(resource);

        runtimeState.helpdeskFundingRequestsPanel.reject();
        String rejectionNotes = "Test funding request rejection notes entered on: " + DateHelper.dateAsString(new Date());
        runtimeState.helpdeskFundingRequestsPanel.enterRejectNotes(rejectionNotes);
        runtimeState.helpdeskFundingRequestsPanel.selectReason(rejectionReason);

        String rejectedBy = runtimeState.helpdeskFundingRequestsPanel.getRejectedBy();
        if ("None".equals(rejectedBy)) {
            rejectedBy = runtimeState.helpdeskFundingRequestsPanel.selectRandomRejectedBy();
        }

        String notes = "Rejected By: " + rejectedBy + " \nReason for Rejection: " + rejectionReason + "\n" + rejectionNotes;
        testData.addStringTag("notes", notes);

        runtimeState.scenario.write(notes);
        outputHelper.takeScreenshots();

        runtimeState.helpdeskFundingRequestsPanel.save();
        runtimeState.helpdeskJobPage = new HelpdeskJobPage(getWebDriver()).get();
    }

    @And("^the ((?:initial|uplift)) funding request is authorised for \"([^\"]*)\"$")
    public void the_funding_request_is_authorised_for(String fundingRequestType, String resource) throws Throwable {
        selectFundingRequestforResource(resource);

        runtimeState.helpdeskFundingRequestsPanel.authorize();
        if ("UPLIFT".equalsIgnoreCase(fundingRequestType)) {
            runtimeState.helpdeskFundingRequestsPanel.selectRandomReasonForFundingRequest();
            if (runtimeState.helpdeskFundingRequestsPanel.isAmountVisible()) {
                addAmountToAuthorizeInFundingRequest();
                testData.put("upliftAmount", testData.get("amount").toString().replaceAll("[$£,]", ""));
            }
        } else {
            testData.put("amount", Float.parseFloat(runtimeState.helpdeskFundingRequestsPanel.getAmount().replaceAll("[$£,]", "")));
        }
        if (runtimeState.helpdeskFundingRequestsPanel.isFundingEnabled()) {
            if (testData.get("fundingRoute") != null) {
                runtimeState.helpdeskFundingRequestsPanel.selectFundingRoute(testData.getString("fundingRoute"));
            } else {
                runtimeState.helpdeskFundingRequestsPanel.selectRandomFunding();
            }
        }

        if (dbHelperSystemToggles.getSystemFeatureToggle("Budget Review") == 1) {
            runtimeState.helpdeskFundingRequestsPanel.selectRandomAnswerForIsPotentialInsuranceQuestion();
        }

        addAuthorizeNotesInFundingRequest();

        String reasonForFundingRequest = runtimeState.helpdeskFundingRequestsPanel.getReasonForFundingRequest();
        reasonForFundingRequest = reasonForFundingRequest.isEmpty() ? "Other" : reasonForFundingRequest;
        testData.addStringTag("reasonForFundingRequest", reasonForFundingRequest);
        String upliftFundedBy = runtimeState.helpdeskFundingRequestsPanel.getFunding();
        testData.addStringTag("fundingRoute", upliftFundedBy);

        String authorizedBy = runtimeState.helpdeskFundingRequestsPanel.getAuthorizedBy();
        if ("None".equals(authorizedBy)) {
            authorizedBy = runtimeState.helpdeskFundingRequestsPanel.selectRandomAuthorizedBy();
        }
        testData.addStringTag("authorizedBy", authorizedBy);

        String notes = "Authorized By: " + authorizedBy + " \n" + reasonForFundingRequest + " \nAmount Authorized: %s \n" + "Uplift Funded by: " + upliftFundedBy + "\n" + testData.getString("authorizeNotes");
        if ("UPLIFT".equalsIgnoreCase(fundingRequestType)) {
            String amount = toCurrency(testData.getFloat("upliftAmount")).replace(",", "").replaceAll("\\.00?", "");
            testData.put("upliftNotes", String.format(notes, amount));

        } else {
            String amount = toCurrency(testData.getFloat("amount")).replace(",", "").replaceAll("\\.00?", "");
            testData.put("notes", String.format(notes, amount));
        }

        outputHelper.takeScreenshots();
        runtimeState.helpdeskFundingRequestsPanel.save();
        runtimeState.helpdeskJobPage = new HelpdeskJobPage(getWebDriver()).get();
    }

    @And("^the uplift funding request is being authorised for the \"([^\"]*)\"$")
    public void the_uplift_funding_request_is_being_authorised_for(String resource) throws Throwable {
        selectFundingRequestforResource(resource);
        runtimeState.helpdeskFundingRequestsPanel.authorize();
    }

    @And("^the ((?:initial|uplift)) funding request is authorised for \"([^\"]*)\" with funding route \"([^\"]*)\"$")
    public void the_funding_request_is_authorised_for_with_funding_route(String fundingRequestType, String resource, String fundingRoute) throws Throwable {
        if (fundingRoute.equalsIgnoreCase("BMI") && getWebDriver().getCurrentUrl().contains("-ukrb")) {
            fundingRoute = "INSURANCE";
        } else {
            String aliasFundingRoute = dbHelperQuotes.getFundingRouteAlias(fundingRoute);
            fundingRoute = (aliasFundingRoute == null) ? fundingRoute : aliasFundingRoute;
        }

        testData.put("fundingRoute", fundingRoute);
        the_funding_request_is_authorised_for(fundingRequestType, resource);
    }

    public void selectFundingRequestforResource(String resource) throws Throwable {
        if ("ADDITIONAL CONTRACTOR".equalsIgnoreCase(resource)) {
            runtimeState.helpdeskAdditionalResourcePanel = new HelpdeskAdditionalResourcePanel(getWebDriver()).get();
            runtimeState.helpdeskFundingRequestsPanel = runtimeState.helpdeskAdditionalResourcePanel.selectFundingRequestAction();
        } else if ("CONTRACTOR".equalsIgnoreCase(resource)){
            commonSteps.the_action_is_selected("Funding requests");
        }
    }

    @ContinueNextStepsOnException
    @Then("^the previous funding request details are displayed for that resource for that job$")
    public void the_previous_funding_request_details_are_displayed_for_that_resource_for_that_job() throws Throwable {


        if (testData.getString("fundingRoute") == null) {
            testData.put("fundingRoute", dbHelperQuotes.getFundingRouteAlias("OPEX"));
        }
        String resourceName = resourceDao.getByResourceId(testData.getInt("resourceId")).getName();
        FundingRequest fundingRequest = fundingRequestDao.getByJobReferenceResourceNameInitialFunding(testData.getInt("jobReference"), resourceName, "1");
        String expectedDescription = fundingRequest.getDescription();
        int statusId = fundingRequest.getStatusId();
        int fundingReasonId = fundingRequest.getFundingReasonId();
        String expectedAmount = toCurrency(fundingRequest.getRequestedAmount().setScale(2, BigDecimal.ROUND_UP));
        Date storeDate = tzHelper.getCurrentTimeAtSite(testData.getInt("siteId"));
        String expectedDate = DateHelper.dateAsString(storeDate, "dd MMM YYYY");
        String expectedReason = reasonDao.getReasonById(fundingReasonId).getName();
        String expectedStatus = dbHelper.getFundingRequestStatus(statusId);

        runtimeState.helpdeskPreviousFundingRequestsPanel = new HelpdeskPreviousFundingRequestsPanel(getWebDriver()).get();
        String[] amountAndFundingRoute = runtimeState.helpdeskPreviousFundingRequestsPanel.getAmountAndFundingRoute().split("\\s+");
        String actualAmount = amountAndFundingRoute[0];
        String actualFundingRoute = amountAndFundingRoute[1];
        String[] descriptionAndReason = runtimeState.helpdeskPreviousFundingRequestsPanel.getDescriptionAndReason().split("\\s{2}");
        String actualDescription = descriptionAndReason[0];
        String actualReason = descriptionAndReason[1];
        String totalApprovedForResource = runtimeState.helpdeskPreviousFundingRequestsPanel.getTotalApproved(resourceName);
        String totalApprovedForJob = runtimeState.helpdeskPreviousFundingRequestsPanel.getTotalApproved(String.valueOf(testData.getInt("jobReference")));
        String actualDate = DateHelper.convert(runtimeState.helpdeskPreviousFundingRequestsPanel.getDate(), MEDIUM, "dd MMM yyyy");
        String actualStatus = runtimeState.helpdeskPreviousFundingRequestsPanel.getStatus();

        runtimeState.scenario.write("Asserting Date is: " + expectedDate + "\nAsserting Status is: " + expectedStatus + "\nAsserting Description is: " + expectedDescription
                + "\nAsserting Reason is: " + expectedReason + "\nAsserting Amount is: " + expectedAmount + "\nAsserting Funding Route is: " + testData.getString("fundingRoute")
                + "\nAsserting Total Approved (" + resourceName + ") is: " + expectedAmount + "\nAsserting Total Approved (Job " + testData.getInt("jobReference") + ") is: " +expectedAmount);

        assertEquals("Expected Date: " + expectedDate + " But Actual Date is: " + actualDate, expectedDate, actualDate);
        assertEquals("Expected Status: " + expectedStatus + " But Actual Status is: " + actualStatus, expectedStatus, actualStatus);
        assertEquals("Expected Description: " + expectedDescription + " But Actual Description is: " + actualDescription, expectedDescription, actualDescription);
        assertEquals("Expected Reason: " + expectedReason + " But Actual Reason is: " + actualReason, expectedReason, actualReason);
        assertEquals("Expected Amount: " + expectedAmount + " But Actual Amount is: " + actualAmount, expectedAmount.replaceAll(",", ""), actualAmount.replaceAll(",", ""));
        assertEquals("Expected FundingRoute: " + testData.getString("fundingRoute") + " But Actual FundingRoute is: " + actualFundingRoute, testData.getString("fundingRoute"), actualFundingRoute);
        assertEquals("Expected Total Approved (" + resourceName + ") is: " + expectedAmount + " But Actual Total Approved (" + resourceName + ") is: " + totalApprovedForResource, expectedAmount.replaceAll(",", ""), totalApprovedForResource.replaceAll(",", ""));
        assertEquals("Expected Total Approved (Job " + testData.getInt("jobReference") + ") is: " + expectedAmount + " But Actual Total Approved (Job " + testData.getInt("jobReference") + ") is: " + totalApprovedForJob, expectedAmount.replaceAll(",", ""), totalApprovedForJob.replaceAll(",", ""));

        outputHelper.takeScreenshots();

    }

    @ContinueNextStepsOnException
    @Then("^the previous funding request is displayed$")
    public void the_previous_funding_request_is_displayed() {
        runtimeState.helpdeskPreviousFundingRequestsPanel = new HelpdeskPreviousFundingRequestsPanel(getWebDriver()).get();
        String jobreference = testData.getString("jobReference");

        String totalApprovedForJob = runtimeState.helpdeskPreviousFundingRequestsPanel.getTotalApproved(jobreference);

        String expectedAmount = CURRENCY_SYMBOL + testData.getString("expectedFundingAmount");
        runtimeState.scenario.write("Asserting Total Approved (Job " + jobreference + ") " + expectedAmount);
        assertEquals("Expected Total Approved (Job " + jobreference + ") " + expectedAmount, expectedAmount, totalApprovedForJob);
    }

    @And("^the cancel button is disabled for the initial funding request$")
    public void the_cancel_button_is_disabled_for_the_initial_funding_request() throws Throwable {
        assertFalse("Cancel button is enabled!", runtimeState.helpdeskPreviousFundingRequestsPanel.isCancelButtonClickable());
        runtimeState.scenario.write("Cancel button is disabled for initial funding request");
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the amount field in the funding request will show the new total approved amount for the \"([^\"]*)\"$")
    public void the_amount_field_in_the_funding_request_will_show_the_new_total_approved_amount_for_the_something(String resource) throws Throwable {
        selectFundingRequestforResource(resource);
        runtimeState.helpdeskPreviousFundingRequestsPanel = new HelpdeskPreviousFundingRequestsPanel(getWebDriver()).get();

        BigDecimal requestedAmount = fundingRequestDao.getByJobReferenceResourceNameInitialFunding(testData.getInt("jobReference"), testData.getString("additionalResourceName"), "1").getRequestedAmount();
        String expectedAmountForContractor = CURRENCY_SYMBOL + String.valueOf(requestedAmount.setScale(2));

        String[] amountAndFundingRoute = runtimeState.helpdeskPreviousFundingRequestsPanel.getAmountAndFundingRoute().split("\\s+");
        String actualAmountForContractor = amountAndFundingRoute[0];

        runtimeState.scenario.write("Reason: " + testData.getString("reasonForFundingRequest"));
        String notes = "Authorized By: " + testData.getString("authorizedBy")
        + " \nOther"
        + " \nAmount Authorized: "
        + expectedAmountForContractor.replaceAll(",", "").replaceAll(".00", "")
        + " \n"
        + "Uplift Funded by: "
        + testData.getString("fundingRoute")
        + " \n"
        + testData.getString("authorizeNotes");
        testData.addStringTag("notes", notes);

        runtimeState.scenario.write("Asserting new total approved amount for contractor is: " + expectedAmountForContractor);
        assertEquals("Expected Amount: " + expectedAmountForContractor + " But Actual Amount is: " + actualAmountForContractor, expectedAmountForContractor, actualAmountForContractor);

    }

    @When("^the approved uplift is cancelled for the \"([^\"]*)\"$")
    public void the_approved_uplift_is_cancelled_for_the_something(String resource) throws Throwable {
        selectFundingRequestforResource(resource);
        runtimeState.helpdeskPreviousFundingRequestsPanel = new HelpdeskPreviousFundingRequestsPanel(getWebDriver()).get();
        runtimeState.helpdeskCancelApprovedUpliftPanel = runtimeState.helpdeskPreviousFundingRequestsPanel.clickCancelButton(1);
        runtimeState.helpdeskCancelApprovedUpliftPanel.selectReason();
        String notes = "Test Notes entered to cancel approved uplift on: " + DateHelper.dateAsString(new Date());
        runtimeState.helpdeskCancelApprovedUpliftPanel.enterNotes(notes);

        testData.addStringTag("notes", notes);
        String authorizedBy;;
        authorizedBy = runtimeState.helpdeskCancelApprovedUpliftPanel.getAuthorizedBy();
        if ("None".equals(authorizedBy)) {
            authorizedBy = runtimeState.helpdeskCancelApprovedUpliftPanel.selectRandomAuthorizedBy();
        }
        testData.addStringTag("upliftCancellationAuthorizedBy", authorizedBy);

        testData.addStringTag("reason", runtimeState.helpdeskCancelApprovedUpliftPanel.getReason());

        String resourceName = resourceDao.getByResourceId(testData.getInt("resourceId")).getName();
        String info = "Confirmed uplift for resource " + normalize(resourceName) + " for amount " + toCurrency(testData.getFloat("upliftAmount")) + " has been " + localize("cancelled") + ".";
        testData.addStringTag("info", info);

        outputHelper.takeScreenshots();
        runtimeState.helpdeskCancelApprovedUpliftPanel.clickSaveButton();

        runtimeState.scenario.write("Cancelled approved uplift");
        POHelper.refreshPage();
    }

    @ContinueNextStepsOnException
    @Then("^the Amount and Description fields are not displayed in the funding request panel$")
    public void the_amount_and_description_fields_are_not_displayed_in_the_funding_request_panel() throws Throwable {
        runtimeState.scenario.write("Asserting amount and description fields are not displayed in the funding request section");
        assertFalse("Amount field is displayed", runtimeState.helpdeskFundingRequestsPanel.isAmountFundingRequestDisplayed());
        assertFalse("Description field is displayed", runtimeState.helpdeskFundingRequestsPanel.isDescriptionFundingRequestDisplayed());
    }

    @And("^the save button is greyed out in the funding request panel$")
    public void the_save_button_is_greyed_out_in_the_funding_request_panel() throws Throwable {
        runtimeState.scenario.write("Asserting Save button is not enabled");
        assertFalse("Save is enabled!", runtimeState.helpdeskFundingRequestsPanel.isSaveButtonClickable());
    }

    @And("^the cancel button is enabled$")
    public void the_cancel_button_is_enabled() throws Throwable {
        runtimeState.scenario.write("Asserting Cancel button is enabled");
        assertTrue("Save is enabled!", runtimeState.helpdeskFundingRequestsPanel.isCancelButtonClickable());
    }

    public void addAmountToAuthorizeInFundingRequest() {
        int randomAmount = DataGenerator.randBetween(200, 500);
        testData.addIntegerTag("amount", randomAmount);
        runtimeState.helpdeskFundingRequestsPanel.setAmount(String.valueOf(randomAmount));
    }

    public void addAuthorizeNotesInFundingRequest() {
        String authorizeNotes = "Test funding request " + localize("authorization") + " notes entered on: " + DateHelper.dateAsString(new Date());
        runtimeState.helpdeskFundingRequestsPanel.enterAuthoriseNotes(authorizeNotes);
        testData.addStringTag("authorizeNotes", authorizeNotes);
    }

    @When("^the funding request is authorised for \"([^\"]*)\" resource with no pre-approved amount$")
    public void the_funding_request_is_authorised_for_resource_with_no_preapproved_amount(String resource) throws Throwable {
        selectFundingRequestforResource(resource);
        runtimeState.helpdeskFundingRequestsPanel.authorize();
        runtimeState.helpdeskFundingRequestsPanel.selectRandomFunding();
        addAmountToAuthorizeInFundingRequest();
        addAuthorizeNotesInFundingRequest();

        String authorizedBy = runtimeState.helpdeskFundingRequestsPanel.getAuthorizedBy();
        if ("None".equals(authorizedBy)) {
            authorizedBy = runtimeState.helpdeskFundingRequestsPanel.selectRandomAuthorizedBy();
        }

        String fundedBy = runtimeState.helpdeskFundingRequestsPanel.getFunding();

        if (dbHelperSystemToggles.getSystemFeatureToggle("Budget Review") == 1) {
            runtimeState.helpdeskFundingRequestsPanel.selectRandomAnswerForIsPotentialInsuranceQuestion();
        }

        outputHelper.takeScreenshots();
        runtimeState.helpdeskFundingRequestsPanel.save();

        String amount = toCurrency(testData.getFloat("amount")).replace(",", "").replaceAll("\\.00?", "");
        String notes = "Authorized By: " + authorizedBy + " \nOther" + " \nAmount Authorized: " + amount + " \n" + "Uplift Funded by: " + fundedBy + " \n" + testData.getString("authorizeNotes");
        testData.addStringTag("notes", notes);
    }

    @When("^authorize action is selected for \"([^\"]*)\"$")
    public void authorize_action_is_selected_for(String resource) throws Throwable {
        runtimeState.helpdeskFundingRequestsPanel.authorize();
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" name is prepopulated in the Authorized By field$")
    public void the_name_is_prepopulated_in_the_authorized_by_field(String managerPosition) throws Throwable {
        String expectedAuthorizedBy = normalize(dbHelperSites.getRfmForSite(testData.getInt("siteId")).get("Name").toString());
        String actualAuthorizedBy = normalize(runtimeState.helpdeskFundingRequestsPanel.getAuthorizedBy());
        runtimeState.scenario.write("Asserting Authorized By is: " + expectedAuthorizedBy);
        assertEquals("Expected Authorized By: " + expectedAuthorizedBy + " .But Actual Authorized By: " + actualAuthorizedBy, expectedAuthorizedBy, actualAuthorizedBy);
    }

    public void actionWhenAssetNotselected() throws IOException {
        if (runtimeState.helpdeskLogJobPage.isAssetNotSelectedModalDisplayed()) {
            runtimeState.helpdeskLogJobPage.selectRandomAssetNotSelectedReason();
            outputHelper.takeScreenshots();
            runtimeState.helpdeskLogJobPage.assetNotSelectedContinue();
        }
    }

    @ContinueNextStepsOnException
    @Then("^the funding route will default to \"([^\"]*)\"$")
    public void the_funding_route_will_default_to(String fundingRoute) throws Throwable {
        if (fundingRoute.equalsIgnoreCase("BMI") && getWebDriver().getCurrentUrl().contains("-ukrb")) {
            fundingRoute = "INSURANCE";
        } else {
            String aliasFundingRoute = dbHelperQuotes.getFundingRouteAlias(fundingRoute);
            fundingRoute = (aliasFundingRoute == null) ? fundingRoute : aliasFundingRoute;
        }
        assertEquals("Expected fundig route: " + fundingRoute, fundingRoute, runtimeState.helpdeskFundingRequestsPanel.getFunding());
    }

    @ContinueNextStepsOnException
    @Then("^the funding route \"([^\"]*)\" be updated$")
    public void the_funding_route_be_updated(String arg1) throws Throwable {
        boolean updateable = "can".equals(arg1);
        assertTrue(runtimeState.helpdeskFundingRequestsPanel.isFundingRequestSelectable() == updateable);
    }

}
