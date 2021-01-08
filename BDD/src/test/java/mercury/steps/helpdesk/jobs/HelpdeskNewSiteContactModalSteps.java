package mercury.steps.helpdesk.jobs;

import static mercury.helpers.Globalisation.LOCALE;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.When;
import mercury.databuilders.CallerContact;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.runtime.RuntimeState;

public class HelpdeskNewSiteContactModalSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private OutputHelper outputHelper;

    @When("^user adds a new site contact$")
    public void user_adds_a_new_contact() throws Throwable {

        runtimeState.helpdeskNewSiteContactModal = runtimeState.helpdeskLogJobPage.clickNewContactButton();
        CallerContact cc = new CallerContact.Builder().build();
        String siteContactName = cc.getName();
        String jobRole = cc.getJobTitle();
        String department = cc.getDepartment();

        String telephone = LOCALE.equalsIgnoreCase("en-GB") ? DataGenerator.generateUkPhoneNumber() :  DataGenerator.generateUsPhoneNumber();

        testData.addStringTag("siteContactName", siteContactName);
        testData.addStringTag("siteContactJobRole", jobRole);
        testData.addStringTag("siteContactDepartment", department);
        testData.addStringTag("siteContactTelephone", telephone);

        runtimeState.helpdeskNewSiteContactModal.enterName(siteContactName);
        runtimeState.helpdeskNewSiteContactModal.enterJobTitle(jobRole);
        runtimeState.helpdeskNewSiteContactModal.enterDepartment(department);
        runtimeState.helpdeskNewSiteContactModal.enterTelephone(telephone);

        outputHelper.takeScreenshots();
        runtimeState.helpdeskNewSiteContactModal.clickButton("Add");
    }

}
