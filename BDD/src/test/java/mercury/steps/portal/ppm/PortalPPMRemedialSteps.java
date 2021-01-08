package mercury.steps.portal.ppm;

import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.pageobject.web.portal.ppm.PPMRemedialJobPage;
import mercury.runtime.RuntimeState;

public class PortalPPMRemedialSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testDataRequirements;


    @And("^assigned to me is clicked$")
    public void assigned_to_me_is_clicked() throws Throwable {
        runtimeState.ppmRemedialJobPage = new PPMRemedialJobPage(getWebDriver()).get();
        runtimeState.ppmRemedialJobPage.selectAssignToMe();
    }

    @And("^a random Asset Main Type is selected$")
    public void a_random_asset_main_type_is_selected() throws Throwable {
        runtimeState.ppmRemedialJobPage.selectRandomAssetMainType();
    }

    @And("^a random Asset Sub Type is selected$")
    public void a_random_asset_sub_type_is_selected() throws Throwable {
        runtimeState.ppmRemedialJobPage.selectRandomAssetSubType();
    }

    @And("^random Asset Types are selected$")
    public void random_asset_types_are_selected() throws Throwable {
        List<String> assetMainTypes = runtimeState.ppmRemedialJobPage.getAssetMainTypes();
        do {
            // randomly select a main asset type until it results in a selectable sub asset type
            assertTrue("No valid asset main types found", assetMainTypes.size() > 0);
            String mainType = assetMainTypes.get(RandomUtils.nextInt(0, assetMainTypes.size() - 1));
            assetMainTypes.remove(mainType);
            runtimeState.ppmRemedialJobPage.selectAssetMainType(mainType);
        } while (runtimeState.ppmRemedialJobPage.getAssetSubTypes().isEmpty() && assetMainTypes.size() > 0);

        runtimeState.ppmRemedialJobPage.selectRandomAssetSubType();
    }

    @And("^a random Location is selected$")
    public void a_random_location_is_selected() throws Throwable {
        runtimeState.ppmRemedialJobPage.selectRandomLocation();
    }

    @And("^a random Fault Type is selected")
    public void a_random_fault_type_is_selected() throws Throwable {
        runtimeState.ppmRemedialJobPage.selectRandomFaultType();
    }

    @And("^a random Description is added$")
    public void a_random_description_is_added() throws Throwable {
        String description = "Please enter some notes to verify the database update " + DateHelper.dateAsString(new Date());
        runtimeState.scenario.write("Adding Description: " + description);
        runtimeState.ppmRemedialJobPage.enterRandomDescription(description);
    }

    @And("^the remedial save button is selected$")
    public void the_remedial_save_button_is_selected() throws Throwable {
        outputHelper.takeScreenshots();
        runtimeState.updateSavedPage = runtimeState.ppmRemedialJobPage.saveButton();
    }

    @And("^not assigned to me is clicked$")
    public void not_assigned_to_me_is_clicked() throws Throwable {
        runtimeState.ppmRemedialJobPage = new PPMRemedialJobPage(getWebDriver()).get();
        runtimeState.ppmRemedialJobPage.selectNotAssignedToMe();
    }

    @ContinueNextStepsOnException
    @Then("^\"([^\"]*)\" message will be displayed$")
    public void message_will_be_displayed(String message) throws Throwable {
        Integer jobReference = Integer.valueOf(runtimeState.updateSavedPage.getJobReference(message));
        testDataRequirements.addIntegerTag("linkedJobReference", jobReference);
        assertNotNull("Unexpected Page displayed: Missing job reference ", jobReference);
        outputHelper.takeScreenshots();
    }

    @And("^the Remedial job form is updated and saved$")
    public void the_remedial_job_form_is_updated_and_saved() throws Throwable {
        random_asset_types_are_selected();
        a_random_location_is_selected();
        a_random_fault_type_is_selected();
        a_random_description_is_added();
        the_remedial_save_button_is_selected();
    }
}
