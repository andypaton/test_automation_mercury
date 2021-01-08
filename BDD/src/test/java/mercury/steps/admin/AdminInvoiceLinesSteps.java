package mercury.steps.admin;

import static mercury.helpers.Globalisation.localize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperInvoices;
import mercury.pageobject.web.admin.AdminMenuPage;
import mercury.runtime.RuntimeState;

public class AdminInvoiceLinesSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private DbHelperInvoices dbHelperInvoices;
    @Autowired private OutputHelper outputHelper;

    @And("^the user selects \"([^\"]*)\" from the sub menu$")
    public void the_user_selects_from_the_sub_menu(String subMenuName) throws Throwable {
        runtimeState.adminMenuPage = new AdminMenuPage(getWebDriver()).get();
        runtimeState.adminMenuPage.selectAdminMenu(subMenuName);
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" Invoice Line types are stored as \"([^\"]*)\" in the database$")
    public void the_invoice_line_types_are_stored_in_the_database(String type, String value) throws Throwable {
        for (String invoiceLineType : testData.getArray("activeInvoiceLineTypes")) {
            runtimeState.scenario.write("Asserting DB for " + invoiceLineType + " = " + value);
            if ("mandatory".equalsIgnoreCase(type)) {
                if ("active".equalsIgnoreCase(value)) {
                    assertTrue("Expected " + invoiceLineType + " to equal 1", dbHelperInvoices.getMandatoryInvoiceLineTypes(invoiceLineType) == 1);
                } else {
                    assertTrue("Expected " + invoiceLineType + " to equal 0", dbHelperInvoices.getMandatoryInvoiceLineTypes(invoiceLineType) == 0);
                }
            } else if (localize("labour").equalsIgnoreCase(localize(type))) {
                if ("active".equalsIgnoreCase(value)) {
                    assertTrue("Expected " + invoiceLineType + " to equal 1", dbHelperInvoices.getLaborInvoiceLineTypes(invoiceLineType) == 1);
                } else {
                    assertTrue("Expected " + invoiceLineType + " to equal 0", dbHelperInvoices.getLaborInvoiceLineTypes(invoiceLineType) == 0);
                }
            }
        }
    }

    @When("^the \"([^\"]*)\" invoice line types are set to \"([^\"]*)\"$")
    public void the_invoice_line_types_are_set_to_a_status(String type, String value) throws Throwable {
        List<Map<String, Object>> existingLineTypeSettings = dbHelperInvoices.getInvoiceLineTypes();
        testData.addAllMap("existingLineTypeSettings", existingLineTypeSettings);

        List<String> lineTypeDescriptions = dbHelperInvoices.getLineTypeDescriptions();

        for (String lineTypeDescription : lineTypeDescriptions) {
            if (localize("labour").equalsIgnoreCase(localize(type))) {
                if ("active".equalsIgnoreCase(value)) {
                    if (runtimeState.adminFinancePage.isLaborCheckBoxChecked(lineTypeDescription) == false) {
                        runtimeState.adminFinancePage.selectLaborCheckBoxInCell(lineTypeDescription, "ng-empty");
                    }

                } else {
                    if (runtimeState.adminFinancePage.isLaborCheckBoxChecked(lineTypeDescription) == true) {
                        runtimeState.adminFinancePage.selectLaborCheckBoxInCell(lineTypeDescription, "ng-not-empty");
                    }
                }
                testData.addToList("activeInvoiceLineTypes", lineTypeDescription);
                testData.addBooleanTag("haveLineTypeSettingsBeenUpdated", true);
            } else if ("mandatory".equalsIgnoreCase(type)) {
                if ("active".equalsIgnoreCase(value)) {
                    if (runtimeState.adminFinancePage.isMandatoryCheckBoxChecked(lineTypeDescription) == false) {
                        runtimeState.adminFinancePage.selectMandatoryCheckBoxInCell(lineTypeDescription, "ng-empty");
                    }

                } else {
                    if (runtimeState.adminFinancePage.isMandatoryCheckBoxChecked(lineTypeDescription) == true) {
                        runtimeState.adminFinancePage.selectMandatoryCheckBoxInCell(lineTypeDescription, "ng-not-empty");
                    }
                }
                testData.addToList("activeInvoiceLineTypes", lineTypeDescription);
                testData.addBooleanTag("haveLineTypeSettingsBeenUpdated", true);
            }
        }
    }
}
