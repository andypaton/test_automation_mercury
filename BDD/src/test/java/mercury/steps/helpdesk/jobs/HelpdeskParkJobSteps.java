package mercury.steps.helpdesk.jobs;

import static mercury.helpers.Globalisation.FULL_DATE;
import static mercury.helpers.Globalisation.SHORT2;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.pageobject.web.helpdesk.resources.HelpdeskAdditionalResourcesRequiredPanel;
import mercury.pageobject.web.helpdesk.resources.HelpdeskManageResourcesPanel;
import mercury.runtime.RuntimeState;

public class HelpdeskParkJobSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private TzHelper tzHelper;

    @And("^the additional resource section is closed$")
    public void the_additional_resource_section_is_closed() throws Throwable {
        runtimeState.helpdeskAdditionalResourcesRequiredPanel = new HelpdeskAdditionalResourcesRequiredPanel(getWebDriver()).get();
        runtimeState.scenario.write("Closing Additional Resource section by clicking on X Icon");
        outputHelper.takeScreenshots();
        runtimeState.helpdeskAdditionalResourcesNotRequiredPanel = runtimeState.helpdeskAdditionalResourcesRequiredPanel.closeAdditionalResource();
    }

    @And("^the details for why no additional resource is required is entered$")
    public void the_details_for_why_no_additional_resource_is_required_is_entered() throws Throwable {
        String testDetails = "Test details entered on " + DateHelper.dateAsString(new Date());
        testData.addStringTag("reason", testDetails);
        runtimeState.helpdeskAdditionalResourcesNotRequiredPanel.setDetailWhyNoAdditionalResourceRequired(testDetails);
        runtimeState.scenario.write("Entered following details in the 'why no additional resource required' textbox: " + testDetails);
        outputHelper.takeScreenshots();
    }

    @And("^user selects park job action with reason and date to unpark$")
    public void user_selects_park_job_action_with_reason_and_date_to_unpark() throws Throwable {
        runtimeState.helpdeskAdditionalResourcesNotRequiredPanel.parkJob();
        runtimeState.helpdeskAdditionalResourcesNotRequiredPanel.selectRandomReason();
        runtimeState.helpdeskAdditionalResourcesNotRequiredPanel.selectDateToUnpark(DateHelper.getNowDatePlusOffset(24, FULL_DATE));
        String reason = runtimeState.helpdeskAdditionalResourcesNotRequiredPanel.getReason();
        testData.addStringTag("reason", reason);
        String dateToUnpark = runtimeState.helpdeskAdditionalResourcesNotRequiredPanel.getDateToUnpark();
        String parkedUntil = DateHelper.convert(dateToUnpark, SHORT2, "h:mma d MMM yyyy");
        testData.addStringTag("parkedUntil", parkedUntil);
        runtimeState.scenario.write("Selected Park job action with reason: " + reason + " and date to unpark: " + dateToUnpark);
        outputHelper.takeScreenshots();
        runtimeState.helpdeskManageResourcesPanel.save();
    }

    @And("^user selects reason and new date to unpark and saves the job$")
    public void user_selects_reason_and_new_date_to_unpark_and_saves_the_job() throws Throwable {
        runtimeState.helpdeskParkJobPanel.selectRandomReason();
        runtimeState.helpdeskParkJobPanel.selectDateToUnpark(DateHelper.getNowDatePlusOffset(24, FULL_DATE));
        String dateToUnpark = runtimeState.helpdeskParkJobPanel.getDateToUnpark();
        String parkedUntil = DateHelper.convert(dateToUnpark, SHORT2, "h:mma d MMM yyyy");
        testData.addStringTag("parkedUntil", parkedUntil);
        String reason = runtimeState.helpdeskParkJobPanel.getReason();
        testData.addStringTag("reason", reason);
        runtimeState.scenario.write("Selected reason is: " + reason + " and new date to unpark is: " + dateToUnpark);
        outputHelper.takeScreenshots();
        runtimeState.helpdeskParkJobPanel.save();
    }
    
    @When("^the job is parked$")
    public void the_job_is_parked() throws Throwable {
       
        if (!runtimeState.helpdeskJobPage.isJobActionDisabled("Park Job")) {
            runtimeState.helpdeskParkJobPanel = runtimeState.helpdeskJobPage.selectParkJobAction();
            runtimeState.helpdeskParkJobPanel.selectRandomReason();
            runtimeState.helpdeskParkJobPanel.selectDateToUnpark(DateHelper.getNowDatePlusOffset(24, FULL_DATE));
            String dateToUnpark = runtimeState.helpdeskParkJobPanel.getDateToUnpark();
            String parkedUntil = DateHelper.convert(dateToUnpark, SHORT2, "h:mma d MMM yyyy");
            testData.addStringTag("parkedUntil", parkedUntil);
            String reason = runtimeState.helpdeskParkJobPanel.getReason();
            testData.addStringTag("reason", reason);
            runtimeState.scenario.write("Selected reason is: " + reason + " and new date to unpark is: " + dateToUnpark);
            outputHelper.takeScreenshots();
            runtimeState.helpdeskParkJobPanel.save();
        } else {
            runtimeState.helpdeskJobPage.selectManageResourcesFromAction();
            the_additional_resource_section_is_closed();
            the_details_for_why_no_additional_resource_is_required_is_entered();
            runtimeState.helpdeskManageResourcesPanel = new HelpdeskManageResourcesPanel(getWebDriver()).get();
            user_selects_park_job_action_with_reason_and_date_to_unpark();
        }    
    }

    @ContinueNextStepsOnException
    @Then("^Unpark Date and Time should prepopulate for one hour time$")
    public void unpark_date_and_time_should_prepopulate_for_one_hour_time() throws Throwable {
        String actualDateToUnpark = runtimeState.helpdeskParkJobPanel.getDateToUnpark();
        Date actualUnparkDateAndTime = DateHelper.stringAsDate(actualDateToUnpark, SHORT2);

        int siteId = dbHelperSites.getSiteIdForJobRef(testData.getInt("jobReference"));

        Date homeOfficeDateAndTime = tzHelper.getCurrentTimeAtHomeOffice(siteId);

        long timeDifferenceInMinutes = DateHelper.getTimeDifferenceBetweenTwoDatesInMinutes(actualUnparkDateAndTime, homeOfficeDateAndTime);

        assertTrue(
                "Unpark Date and Time should pre-populate for 1 hour's time. Time difference in minutes: " + timeDifferenceInMinutes
                + ". Unpark date and time in application: " + actualUnparkDateAndTime
                + ". Home/HeadOfice time: " + homeOfficeDateAndTime,
                timeDifferenceInMinutes >= 57 && timeDifferenceInMinutes <= 60);

        runtimeState.scenario.write("Date to Unpark is prepopulated for 1 hour time and is: " + actualDateToUnpark);
        outputHelper.takeScreenshots();
    }

    @And("^user selects cancel job action$")
    public void user_selects_cancel_job_action() throws Throwable {
        runtimeState.helpdeskAdditionalResourcesNotRequiredPanel.cancelJob();
        runtimeState.scenario.write("Selected cancel job action");
        outputHelper.takeScreenshots();
        testData.put("reason", "Other");
        runtimeState.helpdeskManageResourcesPanel.save();
    }
}

