package mercury.steps.helpdesk.resources;


import static mercury.helpers.Globalisation.FULL_DATE;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.And;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.runtime.RuntimeState;

public class HelpdeskResourceEtaSteps {
    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;


    @And("^the resource ETA is updated and an ETA ((?:is|is not)) advised to site$")
    public void the_resource_eta_is_updated(String advisedIn) throws Throwable {
        String advised = null;
        if ("is not".equalsIgnoreCase(advisedIn)) {
            advised = "No";
        } else {
            advised = "Yes";
        }
        runtimeState.helpdeskResourceETAPanel.selectEtaAdvisedToSite(advised);
        if (advised.equalsIgnoreCase("Yes")) {
            String advisedTo = DataGenerator.GenerateRandomString(4, 6, 2, 2, 0, 0);
            runtimeState.helpdeskResourceETAPanel.setAdvisedTo(advisedTo);
            testData.addStringTag("advisedTo", advisedTo);
        }
        runtimeState.helpdeskResourceETAPanel.selectEtaDate(DateHelper.getNowDatePlusOffset(24, FULL_DATE));
        runtimeState.helpdeskResourceETAPanel.selectEtaTimeWindow();
        String dateStr = runtimeState.helpdeskResourceETAPanel.getEtaDate();
        String time = runtimeState.helpdeskResourceETAPanel.getEtaTimeWindow();
        testData.put("eta", "ST - " + DateHelper.convert(dateStr, "MM/dd/yyy", "d MMM yyyy") + " From " + time.replace("-", "To"));
        runtimeState.scenario.write("Test Data : Filled following details in the resource ETA panel: ETA Advised to Site: " + advised	+ ", ETA Date: " + dateStr + ", ETA Time Window: " + time);
        outputHelper.takeScreenshots();
        runtimeState.helpdeskManageResourcesPanel=runtimeState.helpdeskResourceETAPanel.save();
        outputHelper.takeScreenshots();
    }
}
