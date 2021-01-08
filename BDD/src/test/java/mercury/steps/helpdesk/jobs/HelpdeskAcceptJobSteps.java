package mercury.steps.helpdesk.jobs;

import static mercury.helpers.Globalisation.FULL_DATE;
import static mercury.helpers.Globalisation.MEDIUM_DATE;
import static mercury.helpers.Globalisation.SHORT_DATE;
import static mercury.runtime.ThreadManager.getWebDriver;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.pageobject.web.helpdesk.HelpdeskTimelineTab;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;


public class HelpdeskAcceptJobSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private CommonSteps commonSteps;

    @When("^the ETA date and time are entered and an ETA ((?:is|is not)) advised to site$")
    public void eta_populated_advised(String advised) throws Throwable {
        String advisedButton = ("is".equalsIgnoreCase(advised)) ? "Yes" : "No";

        if (runtimeState.helpdeskAcceptJobPanel.isContractorReferenceNumberDisplayed()) {
            String randomData = String.valueOf(RandomUtils.nextInt(1000000, 9999999));
            runtimeState.helpdeskAcceptJobPanel.setContractorReferenceNumber(randomData);
            runtimeState.scenario.write("Contractor reference Number: " + randomData);
        }

        runtimeState.helpdeskAcceptJobPanel.selectEtaAdvisedToSite(advisedButton);

        if ("is".equalsIgnoreCase(advised)) {
            String advisedTo = DataGenerator.GenerateRandomString(4, 6, 2, 2, 0, 0);
            runtimeState.helpdeskAcceptJobPanel.setAdvisedTo(advisedTo);
            testData.addStringTag("advisedTo", advisedTo);
        }

        runtimeState.helpdeskAcceptJobPanel.selectEtaDate(DateHelper.getNowDatePlusOffset(24, FULL_DATE));
        runtimeState.helpdeskAcceptJobPanel.selectEtaTimeWindow();

        String dateStr = runtimeState.helpdeskAcceptJobPanel.getEtaDate();
        String time = runtimeState.helpdeskAcceptJobPanel.getEtaTimeWindow();
        testData.put("eta", "ST - " + DateHelper.convert(dateStr, SHORT_DATE, MEDIUM_DATE) + " between " + time);

        String etaAdvisedToSite = "IS NOT".equalsIgnoreCase(advised) ? "No" : "Yes";
        runtimeState.scenario.write("ETA advised to site: " + etaAdvisedToSite + "\nETA Date: " + dateStr + "\nETA Time Window: " + time);
        outputHelper.takeScreenshots();
        runtimeState.helpdeskManageResourcesPanel=runtimeState.helpdeskAcceptJobPanel.save();
        outputHelper.takeScreenshots();
        runtimeState.helpdeskTimelineTab = new HelpdeskTimelineTab(getWebDriver()).get();
    }

    @And("^the job is accepted for the ((?:initial resource|additional resource|resource)) and an ETA ((?:is|is not)) advised to site$")
    public void the_job_is_accepted_for_the_resource_and_an_eta_advised_to_site(String resource, String option) throws Throwable {
        commonSteps.the_action_is_selected("Manage Resources");
        if("IS NOT".equalsIgnoreCase(option)) {
            if ("ADDITIONAL RESOURCE".equalsIgnoreCase(resource)) {
                runtimeState.helpdeskAcceptJobPanel = runtimeState.helpdeskManageResourcesPanel.additionalResourceSelectAcceptJobAction(testData.getString("additionalResourceName"));
                eta_populated_advised("is not");
                testData.put("etaAdditional", testData.getString("eta"));
            } else {
                commonSteps.the_action_is_selected("Accept job");
                eta_populated_advised("is not");
                testData.put("etaInitial", testData.getString("eta"));
            }
        } else {
            if ("ADDITIONAL RESOURCE".equalsIgnoreCase(resource)) {
                runtimeState.helpdeskAcceptJobPanel = runtimeState.helpdeskManageResourcesPanel.additionalResourceSelectAcceptJobAction(testData.getString("additionalResourceName"));
                eta_populated_advised("is");
                testData.put("etaAdditional", testData.getString("eta"));
            } else {
                commonSteps.the_action_is_selected("Accept job");
                eta_populated_advised("is");
                testData.put("etaInitial", testData.getString("eta"));
            }
        }
    }
}
