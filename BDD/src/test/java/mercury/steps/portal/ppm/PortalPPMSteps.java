package mercury.steps.portal.ppm;

import static mercury.helpers.Globalisation.FULL_DATE;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperPPM;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.portal.jobs.PartsRequestPage;
import mercury.pageobject.web.portal.jobs.PortalLogAJobPage;
import mercury.pageobject.web.portal.ppm.PPMAddCertificatesModal;
import mercury.pageobject.web.portal.ppm.PPMGasSafetyAdviceNoticeModal;
import mercury.pageobject.web.portal.ppm.PPMJobDetailsPage;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;
import mercury.steps.portal.MenuSteps;
import mercury.steps.portal.PortalCommon;
import mercury.steps.portal.PortalSteps;

public class PortalPPMSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testDataRequirements;
    @Autowired private PortalCommon portalCommon;
    @Autowired private CommonSteps commonSteps;
    @Autowired private TestData testData;
    @Autowired private MenuSteps menuSteps;
    @Autowired private PortalSteps portalSteps;
    @Autowired private DbHelperPPM dbHelperPPM;

    @When("^a PPM job is searched for and opened$")
    public void a_ppm_job_is_searched_for_and_opened() throws Throwable {
        runtimeState.scenario.write("Searching and Opening Job Reference: " + testData.getInt("jobReference"));

        menuSteps.sub_menu_is_selected_from_the_top_menu("Open Jobs", "Jobs");
        runtimeState.openAwaitingJobsPage = runtimeState.openAwaitingJobsPage.searchJobs(testData.getString("jobReference"));

        outputHelper.takeScreenshots();
        //        runtimeState.openAwaitingJobsPage.OpenOpenJob(testData.getString("jobReference"));
        Grid grid = runtimeState.openAwaitingJobsPage.getJobsGrid();
        Row row = grid.getRows().get(0);
        assertEquals(testData.getString("jobReference"), row.getCell(0).getText());
        String cssSelector = row.getCell(0).getCssSelector();
        getWebDriver().findElement(By.cssSelector(cssSelector)).click();

        portalSteps.the_asbestor_register_been_checked("has");

        runtimeState.ppmJobDetailsPage = new PPMJobDetailsPage(getWebDriver()).get();
    }

    @ContinueNextStepsOnException
    @Then("^the PPM job is visible$")
    public void a_ppm_job_is_visible() throws Throwable {
        assertTrue("Unexpected page", runtimeState.ppmJobDetailsPage.isPageLoaded());
        assertEquals("Unexpected Page Title", "PPM Details", runtimeState.ppmJobDetailsPage.getPpmDetailsTitle());
        assertEquals("Unexpected Type", "PPM Type", runtimeState.ppmJobDetailsPage.getPpmTypeLabel());
        String expectedPageHeader = "Job " +  testDataRequirements.getInt("jobReference").toString();
        assertEquals("Unexpected Page header", expectedPageHeader, runtimeState.ppmJobDetailsPage.getPageHeader());
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the question 'Do you want to raise a PPM remedial Job\\?' ([^\"]*) displayed$")
    public void the_question_Do_you_want_to_raise_a_PPM_remedial_Job_is_displayed(String displayed) throws Throwable {
        if (displayed.equals("is")) {
            assertTrue("Question not displayed: Do you want to raise a PPM remedial Job?", runtimeState.ppmJobDetailsPage.isPpmRemedialLabelDisplayed());
        } else {
            assertFalse("Question is displayed: Do you want to raise a PPM remedial Job?", runtimeState.ppmJobDetailsPage.isPpmRemedialLabelDisplayed());
        }
    }

    @And("^a random Travel Time is selected$")
    public void a_random_travel_time_is_selected() throws Throwable {
        runtimeState.ppmJobDetailsPage.selectTravelTime();
    }

    @When("^a random Sub Status is selected$")
    public void a_random_Sub_Status_is_selected() throws Throwable {
        runtimeState.ppmJobDetailsPage.selectRandomSubStatus();
    }

    @When("^a \"([^\"]*)\" Status is selected$")
    public void a_status_is_selected(String status) throws Throwable {
        runtimeState.ppmJobDetailsPage.selectStatus(status);
    }

    @And("^a note is added$")
    public void a_note_is_added() throws Throwable {
        String notes = "Please enter some notes to verify the database update " + DateHelper.dateAsString(new Date());
        runtimeState.scenario.write("Adding Notes: " + notes);
        runtimeState.ppmJobDetailsPage.enterNote(notes);
    }

    @When("^the PPM Details form is updated with a \"([^\"]*)\" Status$")
    public void the_ppm_details_form_is_updated_with_a_status(String status) throws Throwable {
        a_random_travel_time_is_selected();
        portalCommon.a_random_work_start_time_is_selected();
        portalCommon.a_random_work_end_time_is_selected();
        a_status_is_selected(status);
        a_random_Sub_Status_is_selected();
        a_note_is_added();

        Boolean hasPpmJobGotGsanCertificate = dbHelperPPM.hasPpmJobGotGsanCertificate(testData.getInt("jobReference"));
        if (hasPpmJobGotGsanCertificate) {
            add_gas_safety_advice_notice();
            if (runtimeState.ppmJobDetailsPage.isAddNewCertificateButtonDisplayed()) {
                add_gas_service_sheet_certificate();
            }
        }
    }

    public void add_gas_service_sheet_certificate() {
        runtimeState.ppmJobDetailsPage.addNewCertificate();
        if (runtimeState.ppmAddCertificatesModal == null) {
            runtimeState.ppmAddCertificatesModal = new PPMAddCertificatesModal(getWebDriver()).get();
        }
        runtimeState.ppmAddCertificatesModal.enterCertificateNumber(DataGenerator.GenerateRandomString(7, 10, 3, 3, 0, 0));
        runtimeState.ppmAddCertificatesModal.selectCertificateDate(DateHelper.getNowDatePlusOffset(-24, FULL_DATE));
        runtimeState.ppmAddCertificatesModal.selectOutcomePass();
        outputHelper.takeScreenshots();
        runtimeState.ppmAddCertificatesModal.clickSave();
    }

    public void add_gas_safety_advice_notice() {
        runtimeState.ppmJobDetailsPage.selectYesToGsanQuestion();
        runtimeState.ppmJobDetailsPage.addGsan();
        if (runtimeState.ppmGasSafetyAdviceNoticeModal == null) {
            runtimeState.ppmGasSafetyAdviceNoticeModal = new PPMGasSafetyAdviceNoticeModal(getWebDriver()).get();
        }
        runtimeState.ppmGasSafetyAdviceNoticeModal.enterReferenceNumber(DataGenerator.GenerateRandomString(7, 10, 3, 3, 0, 0));
        runtimeState.ppmGasSafetyAdviceNoticeModal.selectRandomType();
        outputHelper.takeScreenshots();
        runtimeState.ppmGasSafetyAdviceNoticeModal.clickOk();
    }

    @And("^the save button is selected$")
    public void the_save_button_is_selected() throws Throwable {
        outputHelper.takeScreenshot();
        runtimeState.ppmJobDetailsPage.saveButton();
    }

    @And("^an ETA is selected$")
    public void an_eta_is_selected() throws Throwable {
        runtimeState.ppmJobDetailsPage.selectETATimeAndDate();
    }

    @ContinueNextStepsOnException
    @Then("^the portal log a job form is displayed$")
    public void the_portal_log_a_job_form_is_displayed() throws Throwable {
        runtimeState.portalLogAJobPage = new PortalLogAJobPage(getWebDriver()).get();
    }

    @When("^the PPM Details form is updated with \"([^\"]*)\" Status and saved$")
    public void the_ppm_details_form_is_updated_with_status_and_saved(String status) throws Throwable {
        the_ppm_details_form_is_updated_with_a_status(status);
        an_eta_is_selected();
        commonSteps.the_button_is_clicked("Save");
        runtimeState.partsRequestPage = new PartsRequestPage(getWebDriver()).get();
    }

    @And("^the request a remedial job box is selected$")
    public void the_request_a_remedial_job_box_is_selected() throws Throwable {
        runtimeState.ppmJobDetailsPage.selectRequestRemedialJob();
    }

    @And("^the PPM Details form is completed with a \"([^\"]*)\" Status$")
    public void the_ppm_details_form_is_completed_with_a_status(String status) throws Throwable {
        a_random_travel_time_is_selected();
        portalCommon.a_random_work_start_time_is_selected();
        portalCommon.a_random_work_end_time_is_selected();
        a_status_is_selected(status);
        a_random_Sub_Status_is_selected();
        an_eta_is_selected();
        a_note_is_added();

        Boolean hasPpmJobGotGsanCertificate = dbHelperPPM.hasPpmJobGotGsanCertificate(testData.getInt("jobReference"));
        if (hasPpmJobGotGsanCertificate) {
            add_gas_safety_advice_notice();
            if (runtimeState.ppmJobDetailsPage.isAddNewCertificateButtonDisplayed()) {
                add_gas_service_sheet_certificate();
            }
        }
    }

    @When("^the request a quote button is clicked$")
    public void the_request_a_quote_button_is_clicked() throws Throwable {
        runtimeState.ppmJobDetailsPage.selectRequestQuote();
    }

}
