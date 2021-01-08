package mercury.steps.admin;

import static mercury.runtime.ThreadManager.getWebDriver;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.PopupAlert;
import mercury.pageobject.web.admin.companies.AdminCompaniesAddAssignmentRulePage;
import mercury.pageobject.web.admin.companies.AdminCompaniesAssignmentRuleDeleteModal;
import mercury.pageobject.web.admin.companies.AdminCompaniesAssignmentRuleEditPage;
import mercury.pageobject.web.admin.companies.AdminCompaniesAssignmentRulesPage;
import mercury.pageobject.web.admin.companies.AdminCompaniesEditAssignmentRuleModal;
import mercury.pageobject.web.admin.companies.AdminEditCompanyAdditionalDetailsPartial;
import mercury.pageobject.web.admin.companies.AdminEditCompanyAliasesPartial;
import mercury.pageobject.web.admin.companies.AdminEditCompanyCoreDetailsPartial;
import mercury.pageobject.web.admin.companies.AdminEditCompanyPage;
import mercury.pageobject.web.admin.companies.AdminEditCompanySaveModal;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.dbhelper.DbHelperCompanies;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;

public class AdminCompaniesSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private DbHelperCompanies dbHelperCompanies;
    @Autowired private TestData testData;
    @Autowired private CommonSteps commonSteps;
    @Autowired private AdminInvoiceLinesSteps adminInvoiceLinesSteps;

    @When("^a ((?:|non-))primary company is selected for editing$")
    public void a_type_of_company_is_selected_for_editing(String companyType) {
        runtimeState.scenario.write("Edit a " + companyType + " company");
        if (dbHelperCompanies.isPrimaryCompanyAvailable() == false) {
            dbHelperCompanies.updateCompanyToPrimary(dbHelperCompanies.getCompanyName(false));
        }
        String companyName = ("non-".equalsIgnoreCase(companyType)) ? dbHelperCompanies.getCompanyName(false) : dbHelperCompanies.getCompanyName(true);
        testData.addStringTag("companyName", companyName);
        assertNotNull("Cannot find any companies", companyName);
        runtimeState.scenario.write("Search for " + companyName + " company");
        runtimeState.adminCompaniesPage.searchInFilter(companyName );
        runtimeState.adminCompaniesPage.clickEditButton(companyName );
        outputHelper.takeScreenshots();
    }

    @When("^a \"([^\"]*)\" rule type company with \"([^\"]*)\" is selected for editing$")
    public void a_rule_type_company_with_orders_or_permanent_alias_is_selected_for_editing(String ruleType, String value) throws Throwable {
        String companyName = null;
        if("a permanent alias".equalsIgnoreCase(value)) {
            runtimeState.scenario.write("Edit a" + ruleType + " company which has a permanent alias");
            companyName = dbHelperCompanies.getPermanentAliasCompanyName(ruleType);
            testData.addStringTag("companyName", companyName);
        } else if("existing orders".equalsIgnoreCase(value)) {
            runtimeState.scenario.write("Edit a" + ruleType + " company which has existing orders");
            companyName = dbHelperCompanies.getCompanyWithExistingOrders(ruleType);
            testData.addStringTag("companyName", companyName);
            if (testData.getString("companyName") == null) {
                runtimeState.scenario.write("Adding a " + ruleType + " rule as none available.");
                adminInvoiceLinesSteps.the_user_selects_from_the_sub_menu("Assignment Rule");
                the_user_adds_a_new_rule_for_a_company(ruleType);
                the_new_rule_is_added_to_the_company();
                adminInvoiceLinesSteps.the_user_selects_from_the_sub_menu("Company");
            }
        }
        runtimeState.adminCompaniesPage.searchInFilter(testData.getString("companyName"));
        runtimeState.adminCompaniesPage.clickEditButton(testData.getString("companyName"));
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the Edit Company page is displayed$")
    public void the_edit_company_page_is_displayed(){
        runtimeState.adminEditCompanyPage = new AdminEditCompanyPage(getWebDriver()).get();
        assertTrue("Page not found: ", runtimeState.adminEditCompanyPage.isEditCompanyPageDisplayed());
        runtimeState.adminEditCompanyCoreDetailsPartial = new AdminEditCompanyCoreDetailsPartial(getWebDriver()).get();
        assertEquals("Unexpected Company name", testData.getString("companyName"), runtimeState.adminEditCompanyCoreDetailsPartial.getCompanyName());
        outputHelper.takeScreenshots();
    }

    @And("^the \"([^\"]*)\" section is selected$")
    public void the_section_is_selected(String sectionName) {
        if ("Additional details".equalsIgnoreCase(sectionName)) {
            runtimeState.scenario.write(sectionName + " opened" + " and selects purchased on behalf of client if its not already active");
            if (dbHelperCompanies.getCompanyType(testData.getString("companyName")) !=null && dbHelperCompanies.getCompanyType(testData.getString("companyName")).equalsIgnoreCase("Client company")) {
                runtimeState.adminEditCompanyCoreDetailsPartial = new AdminEditCompanyCoreDetailsPartial(getWebDriver()).get();
                runtimeState.adminEditCompanyCoreDetailsPartial.selectYesRadioOnProccessedOnBehalfOfClient();
            }
        } else {
            runtimeState.scenario.write(sectionName + " opened");
        }
        runtimeState.adminEditCompanyPage = new AdminEditCompanyPage(getWebDriver()).get();
        runtimeState.adminEditCompanyPage.expandPageSection(sectionName);
        outputHelper.takeScreenshots();
    }

    @And("^the company name is changed$")
    public void the_company_name_is_changed() {
        testData.addStringTag("renamedCompanyName", testData.getString("companyName") + (DataGenerator.GenerateRandomString(4, 4, 0, 0, 0, 0)));
        runtimeState.adminEditCompanyCoreDetailsPartial = new AdminEditCompanyCoreDetailsPartial(getWebDriver()).get();
        runtimeState.adminEditCompanyCoreDetailsPartial.setCompanyName(testData.getString("renamedCompanyName"));
        outputHelper.takeScreenshots();
    }

    @And("^the user confirms the update to the company name$")
    public void the_user_confirms_the_update_to_the_company_name() {
        Integer numberOfOrders = dbHelperCompanies.getCompanyPOCount(testData.getString("companyName"));
        if(numberOfOrders > 0) {
            //Assert the correct number of orders are shown in the save message
            runtimeState.adminEditCompanySaveModal = new AdminEditCompanySaveModal(getWebDriver()).get();
            assertEquals("Unexpected message", "There are "+ numberOfOrders.toString() +" orders which have been completed and sent out for this company. "
                    + "Please confirm you would like to update the company name?"
                    , runtimeState.adminEditCompanySaveModal.getParagraph());
            //Click confirm
            runtimeState.adminEditCompanySaveModal.clickConfirm();
            outputHelper.takeScreenshots();
        }
    }

    @And("^the primary company is setup$")
    public void the_primary_company_is_setup() throws Throwable {
        String companyName = dbHelperCompanies.getCompanyName(true);
        testData.addStringTag("companyName", companyName);
        if (companyName == null) {
            adminInvoiceLinesSteps.the_user_selects_from_the_sub_menu("Company");

            companyName = dbHelperCompanies.getCompanyName(false);
            testData.addStringTag("companyName", companyName);
            assertNotNull("No non-primary company is available.", companyName);
            runtimeState.adminCompaniesPage.searchInFilter(companyName);
            runtimeState.adminCompaniesPage.clickEditButton(companyName);

            if (dbHelperCompanies.getCompanyType(testData.getString("companyName")) != null && dbHelperCompanies.getCompanyType(testData.getString("companyName")).equalsIgnoreCase("Client company")) {
                runtimeState.adminEditCompanyCoreDetailsPartial = new AdminEditCompanyCoreDetailsPartial(getWebDriver()).get();
                runtimeState.adminEditCompanyCoreDetailsPartial.selectYesRadioOnProccessedOnBehalfOfClient();
            } else {
                runtimeState.scenario.write("Additional details section is opened");
            }
            runtimeState.adminEditCompanyPage = new AdminEditCompanyPage(getWebDriver()).get();
            runtimeState.adminEditCompanyPage.clickPageSection("Additional details");

            runtimeState.adminEditCompanyAdditionalDetailsPartial = new AdminEditCompanyAdditionalDetailsPartial(getWebDriver()).get();
            runtimeState.adminEditCompanyAdditionalDetailsPartial.clickPrimaryYes();

            commonSteps.the_button_is_clicked("Save");
            runtimeState.scenario.write("Primary company "+ companyName +" is now setup.");

        } else {
            runtimeState.scenario.write("Primary company "+ companyName +" is already setup.");
        }
    }

    @And("^the company is changed to a ((?:|non-))primary company$")
    public void the_company_primary_status_is_changed(String companyType) {
        String primaryCompanyName = dbHelperCompanies.getCompanyName(true);
        testData.addStringTag("primaryCompanyName", primaryCompanyName);
        runtimeState.adminEditCompanyAdditionalDetailsPartial = new AdminEditCompanyAdditionalDetailsPartial(getWebDriver()).get();

        if (primaryCompanyName != null) {
            if ("non-".equalsIgnoreCase(companyType)) {
                runtimeState.scenario.write("no is clicked for primary company");
                runtimeState.adminEditCompanyAdditionalDetailsPartial.clickPrimaryNo();
            } else {
                runtimeState.scenario.write("yes is clicked for primary company");
                runtimeState.adminEditCompanyAdditionalDetailsPartial.clickPrimaryYes();
            }
        } else {
            throw new PendingException("No primary company is available.");
        }

        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the company is set as a ((?:|non-))primary company$")
    public void the_company_primary_status_is_set(String companyType) {
        if ("non-".equalsIgnoreCase(companyType)) {
            assertFalse(dbHelperCompanies.isPrimaryCompany(testData.getString("companyName")));
        } else {
            assertTrue(dbHelperCompanies.isPrimaryCompany(testData.getString("companyName")));
        }
    }

    @And("^the company is confirmed to be a ((?:|non-))primary company$")
    public void the_company_is_confirmed_to_primary_or_non_primary(String companyType) {
        runtimeState.adminEditCompanySaveModal = new AdminEditCompanySaveModal(getWebDriver()).get();
        if ("non-".equalsIgnoreCase(companyType)) {
            assertThat("Unexpected confirmation message",runtimeState.adminEditCompanySaveModal.getParagraph(),
                    containsString("There are no companies which are currently set to the Primary"));
        } else {
            assertEquals("Unexpected message", "Company " + testData.getString("primaryCompanyName") +
                    " is already set up as the Primary company. If you proceed this will be overwritten with company "
                    + testData.getString("companyName") + ". Would you like to Continue?", runtimeState.adminEditCompanySaveModal.getParagraph());
        }
        outputHelper.takeScreenshots();
        runtimeState.adminEditCompanySaveModal.clickConfirm();
        outputHelper.takeScreenshots();
    }

    @And("^an alias is added to the company$")
    public void an_alias_is_added_to_the_company() throws Exception {
        runtimeState.adminEditCompanyAliasesPartial = new AdminEditCompanyAliasesPartial(getWebDriver()).get();
        runtimeState.adminEditCompanyAliasesPartial.clickAddCompanyAlias();
        Grid grid = runtimeState.adminEditCompanyAliasesPartial.getGrid();
        assertNotNull("Unexpected Null Grid", grid);
        List<Row> rows = grid.getRows();
        for (Row row : rows) {
            if (row.getCell("Permanent").getText().isEmpty()) {
                String alias = RandomStringUtils.randomAlphanumeric(5, 10);
                runtimeState.scenario.write("Click 'Add Company Alias', add " + alias + " alias to the company, click Update");
                testData.addStringTag("newAlias", alias);
                row.getCell("Name").sendText(alias);
                row.getCell(2).clickButton("Update");
                break;
            }
        }
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the alias is added to the company$")
    public void the_alias_is_added_to_the_company() {
        assertTrue(dbHelperCompanies.isCompanyAlias(testData.getString("companyName"), testData.getString("newAlias")));
    }

    @And("^the alias is removed from the company$")
    public void the_alias_is_removed_from_the_company() throws Exception {
        outputHelper.takeScreenshots();

        boolean found = false;
        Grid grid = runtimeState.adminEditCompanyAliasesPartial.getGrid();
        int lastPageNumber = GridHelper.getLastPageNumber(grid);

        for (int i = 0; i < lastPageNumber; i++) {
            assertNotNull("Unexpected Null Grid", grid);
            List<Row> rows = grid.getRows();
            for (Row row : rows) {
                if (row.getCell("Name").getText().equalsIgnoreCase(testData.getString("newAlias"))) {
                    row.getCell(2).clickButton("Delete");
                    runtimeState.popupAlert = new PopupAlert(getWebDriver()).get();
                    runtimeState.popupAlert.confirm();
                    runtimeState.scenario.write("Searched for alias " + testData.getString("newAlias") + " and removed it from a company.");
                    found = true;
                    break;
                }
            }

            if (found) break;

            if (GridHelper.getCurrentPageNumber(grid) < lastPageNumber) {
                grid = GridHelper.goToNextPage(grid);
            }
        }
        assertTrue("Nothing deleted!", found);
    }

    @ContinueNextStepsOnException
    @Then("^the alias is ((?:|not ))linked to the company$")
    public void the_alias_is_or_is_not_linked_to_the_company(String linked) {
        if ("not ".equalsIgnoreCase(linked)) {
            assertFalse("Unexpected alias linked to the company",dbHelperCompanies.isCompanyAlias(testData.getString("companyName"), testData.getString("newAlias")));
        } else {
            assertTrue("Unexpected alias linked to the company",dbHelperCompanies.isCompanyAlias(testData.getString("companyName"), testData.getString("newAlias")));
        }

    }

    @ContinueNextStepsOnException
    @Then("^the assignment rules list is shown$")
    public void the_assignment_rules_list_is_shown() throws Exception {
        runtimeState.scenario.write("search grid for the assignment rules list");
        runtimeState.adminCompaniesAssignmentRulesPage = new AdminCompaniesAssignmentRulesPage(getWebDriver()).get();
        Grid grid = runtimeState.adminCompaniesAssignmentRulesPage.getGrid();
        List<Row> rows = grid.getRows();
        if (rows != null && rows.size() >= 1) {
            assertNotNull("Unexpected grid result", rows.stream().findFirst().isPresent());
        }
    }

    @ContinueNextStepsOnException
    @Then("^the correct assignment rule is shown for the company$")
    public void the_correct_assignment_rule_is_shown_for_the_company() throws Exception {
        runtimeState.adminCompaniesAssignmentRulesPage = new AdminCompaniesAssignmentRulesPage(getWebDriver()).get();
        Grid grid = runtimeState.adminCompaniesAssignmentRulesPage.getGrid();
        List<Row> row = grid.getRows();
        for (int i = 1; i < row.size(); i++) {
            List<String> invoiceLines = dbHelperCompanies.getCompanyRuleAssignments(row.get(i).getCell("Company").getText());
            assertThat(invoiceLines, hasItems(row.get(i).getCell("Rule").getText()));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the primary company is visible in the grid$")
    public void the_primary_company_is_visible_in_the_grid() throws Exception {
        runtimeState.scenario.write("search for the primary company in the grid and assert the company name and rule is visible");
        runtimeState.adminCompaniesAssignmentRulesPage = new AdminCompaniesAssignmentRulesPage(getWebDriver()).get();
        runtimeState.adminCompaniesAssignmentRulesPage.searchInCompanyFilter(dbHelperCompanies.getCompanyName(true));
        Grid grid = runtimeState.adminCompaniesAssignmentRulesPage.getGrid();
        List<Row> row = grid.getRows();
        outputHelper.takeScreenshots();
        assertEquals("Unexpected company name",dbHelperCompanies.getCompanyName(true), row.get(0).getCell("Company").getText());
        assertTrue("Unexpected Primary Company", dbHelperCompanies.isPrimaryCompany(row.get(0).getCell("Company").getText()));
    }

    @ContinueNextStepsOnException
    @Then("^the primary company is the first in the grid$")
    public void the_primary_company_is_the_first_in_the_grid() throws Exception {
        runtimeState.scenario.write("Search the grid for the first entry and validate its the primary company");
        runtimeState.adminCompaniesAssignmentRulesPage = new AdminCompaniesAssignmentRulesPage(getWebDriver()).get();
        Grid grid = runtimeState.adminCompaniesAssignmentRulesPage.getGrid();
        List<Row> rows = grid.getRows();

        if (rows != null && rows.size() >= 1) {
            assertTrue("Unexpected Primary Company", dbHelperCompanies.isPrimaryCompany(rows.get(0).getCell("Company").getText()));
        }
    }

    @When("^the user adds a new \"([^\\\"]*)\" rule for a non-primary company$")
    public void the_user_adds_a_new_rule_for_a_company(String rule) throws Exception {
        runtimeState.scenario.write("Adding new assignment rule " + rule + " for a non-primary company");
        testData.addStringTag("companyName", dbHelperCompanies.getCompanyWithExistingOrders());
        runtimeState.adminCompaniesAssignmentRulesPage = new AdminCompaniesAssignmentRulesPage(getWebDriver()).get();
        runtimeState.adminCompaniesAssignmentRulesPage.clickAddNewRule();

        runtimeState.adminCompaniesAddAssignmentRulePage = new AdminCompaniesAddAssignmentRulePage(getWebDriver()).get();
        runtimeState.adminCompaniesAddAssignmentRulePage.searchAndSelectCompany(testData.getString("companyName"));
        runtimeState.adminCompaniesAddAssignmentRulePage.selectRule(rule);

        if ("Funding Route".equalsIgnoreCase(rule)) {
            runtimeState.scenario.write("Adding a" + rule + " rule for a non primary company, which has not already been used");
            testData.addStringTag("fundingRouteName", dbHelperCompanies.getUnusedFundingRouteRule());
            if (testData.getString("fundingRouteName") == null) {
                dbHelperCompanies.deleteRandomUsedFundingRouteRule();
                testData.addStringTag("fundingRouteName", dbHelperCompanies.getUnusedFundingRouteRule());
            }
            runtimeState.adminCompaniesAddAssignmentRulePage.selectRuleDetail(testData.getString("fundingRouteName"));
        } else {
            runtimeState.scenario.write("Adding a" + rule + " rule for a non primary company");
            testData.addStringTag("siteTypeName", dbHelperCompanies.getUnusedSiteTypeRule());
            if (testData.getString("siteTypeName") == null) {
                dbHelperCompanies.deleteRandomUsedSiteTypeRule();
                testData.addStringTag("siteTypeName", dbHelperCompanies.getUnusedSiteTypeRule());
            }
            runtimeState.adminCompaniesAddAssignmentRulePage.selectRuleDetail(testData.getString("siteTypeName"));
        }
        runtimeState.adminCompaniesAddAssignmentRulePage.selectCreate();
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the new rule is added to the company$")
    public void the_new_rule_is_added_to_the_company() {
        if (testData.getString("fundingRouteName") != null) {
            assertTrue("Unexpected rule detail", dbHelperCompanies.isFundingRouteAssignmentRule(testData.getString("companyName"), testData.getString("fundingRouteName")));
        } else {
            assertTrue("Unexpected rule detail", dbHelperCompanies.isSiteTypesAssignmentRule(testData.getString("companyName"), testData.getString("siteTypeName").replaceAll("'","''")));
        }
    }

    @And("^the user clicks on a \"([^\"]*)\" assignment rule to edit$")
    public void the_user_clicks_on_an_assignment_rule_to_edit(String ruleType) throws Throwable {
        runtimeState.scenario.write(ruleType + " rule" + " is searched for and selected for editing");
        runtimeState.adminCompaniesAssignmentRulesPage = new AdminCompaniesAssignmentRulesPage(getWebDriver()).get();
        testData.addStringTag("companyName", dbHelperCompanies.getUsedAssignmentRuleCompanyName(ruleType));

        if (testData.getString("companyName") == null) {
            runtimeState.scenario.write("Adding a " + ruleType + " rule as none available.");
            the_user_adds_a_new_rule_for_a_company(ruleType);
            the_new_rule_is_added_to_the_company();
        }

        if ("Site Type".equalsIgnoreCase(ruleType)) {

            if (dbHelperCompanies.isAnySiteTypeAvailable() == false) {
                runtimeState.scenario.write("Deleting a " + ruleType + " rule as all of them taken.");
                the_user_clicks_a_assignment_rule_to_delete(ruleType);
                the_user_clicks_delete();
                adminInvoiceLinesSteps.the_user_selects_from_the_sub_menu("Assignment Rule");
                if (dbHelperCompanies.getSiteTypeRuleDetail(testData.getString("companyName")) == null )
                {
                    testData.addStringTag("companyName", dbHelperCompanies.getUsedAssignmentRuleCompanyName(ruleType));
                    testData.addStringTag("ruleDetail", dbHelperCompanies.getSiteTypeRuleDetail(testData.getString("companyName")));
                    runtimeState.adminCompaniesAssignmentRulesPage.searchInRuleDetailFilter(testData.getString("ruleDetail"));
                }
            }
            testData.addStringTag("ruleDetail", dbHelperCompanies.getSiteTypeRuleDetail(testData.getString("companyName")));
            runtimeState.adminCompaniesAssignmentRulesPage.searchInRuleDetailFilter(testData.getString("ruleDetail"));
        } else {

            if (dbHelperCompanies.isAnyFundingRouteAvailable() == false) {
                runtimeState.scenario.write("Deleting a " + ruleType + " rule as all of them taken.");
                the_user_clicks_a_assignment_rule_to_delete(ruleType);
                the_user_clicks_delete();
                adminInvoiceLinesSteps.the_user_selects_from_the_sub_menu("Assignment Rule");
                if (dbHelperCompanies.getFundingRouteRuleDetail(testData.getString("companyName")) == null )
                {
                    testData.addStringTag("companyName", dbHelperCompanies.getUsedAssignmentRuleCompanyName(ruleType));
                    testData.addStringTag("ruleDetail", dbHelperCompanies.getFundingRouteRuleDetail(testData.getString("companyName")));
                    runtimeState.adminCompaniesAssignmentRulesPage.searchInRuleDetailFilter(testData.getString("ruleDetail"));
                }
            } else {
                testData.addStringTag("ruleDetail", dbHelperCompanies.getFundingRouteRuleDetail(testData.getString("companyName")));
                runtimeState.adminCompaniesAssignmentRulesPage.searchInRuleDetailFilter(testData.getString("ruleDetail"));
            }
        }

        Grid grid = runtimeState.adminCompaniesAssignmentRulesPage.getGrid();
        List<Row> row = grid.getRows();
        row.get(0).getCell(3).clickButton("Edit");
        outputHelper.takeScreenshots();
    }

    @When("^the user clicks edit on the primary company$")
    public void the_user_clicks_edit_on_the_primary_company() throws Throwable {
        runtimeState.scenario.write("Primary company is searched for and selected for editing");
        runtimeState.adminCompaniesAssignmentRulesPage = new AdminCompaniesAssignmentRulesPage(getWebDriver()).get();
        Grid grid = runtimeState.adminCompaniesAssignmentRulesPage.getGrid();
        List<Row> rows = grid.getRows();
        if (dbHelperCompanies.isPrimaryCompanyAvailable() == false) {
            dbHelperCompanies.updateCompanyToPrimary(dbHelperCompanies.getCompanyName(false));
            POHelper.refreshPage();
        }

        if (rows != null && rows.size() >= 1) {
            rows.get(0).getCell(4).clickButton("Edit");
        }
        outputHelper.takeScreenshots();
    }

    @When("^the user clicks on a \"([^\"]*)\" assignment rule to delete$")
    public void the_user_clicks_a_assignment_rule_to_delete(String ruleType) throws Throwable {
        runtimeState.scenario.write(ruleType + " rule" + " is searched for and selected for deletion");
        runtimeState.adminCompaniesAssignmentRulesPage = new AdminCompaniesAssignmentRulesPage(getWebDriver()).get();
        testData.addStringTag("companyName", dbHelperCompanies.getUsedAssignmentRuleCompanyName(ruleType));

        if("Site Type".equalsIgnoreCase(ruleType)) {

            if (dbHelperCompanies.isAnySiteTypeAvailable() == true) {
                runtimeState.scenario.write("Adding a " + ruleType + " rule as none available.");
                the_user_adds_a_new_rule_for_a_company(ruleType);
                the_new_rule_is_added_to_the_company();
                testData.addStringTag("ruleDetail", dbHelperCompanies.getSiteTypeRuleDetail(testData.getString("companyName")));
                runtimeState.adminCompaniesAssignmentRulesPage.searchInRuleDetailFilter(testData.getString("ruleDetail"));
            } else {
                testData.addStringTag("ruleDetail", dbHelperCompanies.getSiteTypeRuleDetail(testData.getString("companyName")));
                runtimeState.adminCompaniesAssignmentRulesPage.searchInRuleDetailFilter(testData.getString("ruleDetail"));
            }
        } else {

            if (dbHelperCompanies.isAnyFundingRouteAvailable() == true) {
                runtimeState.scenario.write("Adding a " + ruleType + " rule as none available.");
                the_user_adds_a_new_rule_for_a_company(ruleType);
                the_new_rule_is_added_to_the_company();
                testData.addStringTag("ruleDetail", dbHelperCompanies.getFundingRouteRuleDetail(testData.getString("companyName")));
                runtimeState.adminCompaniesAssignmentRulesPage.searchInRuleDetailFilter(testData.getString("ruleDetail"));
            } else {
                testData.addStringTag("ruleDetail", dbHelperCompanies.getFundingRouteRuleDetail(testData.getString("companyName")));
                runtimeState.adminCompaniesAssignmentRulesPage.searchInRuleDetailFilter(testData.getString("ruleDetail"));
            }

        }

        Grid grid = runtimeState.adminCompaniesAssignmentRulesPage.getGrid();
        List<Row> row = grid.getRows();
        row.get(0).getCell(5).clickButton("Delete");
        outputHelper.takeScreenshots();
    }

    @And("^the user clicks delete$")
    public void the_user_clicks_delete() {
        runtimeState.adminCompaniesAssignmentRuleDeleteModal = new AdminCompaniesAssignmentRuleDeleteModal(getWebDriver()).get();
        runtimeState.adminCompaniesAssignmentRuleDeleteModal.clickDelete();
        outputHelper.takeScreenshots();
        runtimeState.adminCompaniesAssignmentRulesPage = new AdminCompaniesAssignmentRulesPage(getWebDriver()).get();
        runtimeState.adminCompaniesAssignmentRulesPage.clearRuleDetailFilter();
    }

    @And("^the user changes the \"([^\"]*)\" assignment rule details$")
    public void the_user_changes_the_assignment_rule_details(String ruleType) throws Throwable {
        runtimeState.scenario.write(ruleType + " rule" + " is changed and a new " + ruleType + " is selected and saved");
        runtimeState.adminCompaniesAssignmentRuleEditPage = new AdminCompaniesAssignmentRuleEditPage(getWebDriver()).get();
        if("Site Type".equalsIgnoreCase(ruleType)) {
            testData.addStringTag("newSiteTypeRuleDetail", dbHelperCompanies.getUnusedSiteTypeRule());
            runtimeState.adminCompaniesAssignmentRuleEditPage.clickRuleDetail(testData.getString("newSiteTypeRuleDetail"));
        } else {
            testData.addStringTag("newFundingRouteRuleDetail", dbHelperCompanies.getUnusedFundingRouteRule());
            runtimeState.adminCompaniesAssignmentRuleEditPage.clickRuleDetail(testData.getString("newFundingRouteRuleDetail"));
        }
        commonSteps.the_button_is_clicked("Save");
        outputHelper.takeScreenshots();
    }

    @And("^the user changes the \"([^\"]*)\" assigned rule company name$")
    public void the_user_changes_the_assigned_rule_company_name(String ruleType) throws Throwable {
        runtimeState.adminCompaniesAssignmentRuleEditPage = new AdminCompaniesAssignmentRuleEditPage(getWebDriver()).get();
        testData.addStringTag("newCompanyName", dbHelperCompanies.getCompanyName(false));
        runtimeState.adminCompaniesAssignmentRuleEditPage.clickCompany(testData.getString("newCompanyName"));
        commonSteps.the_button_is_clicked("Save");
        outputHelper.takeScreenshots();
    }

    @When("^the user reassigns the primary company$")
    public void the_user_reassigns_the_primary_company() throws Throwable {
        // Gets a company name that has the isClientProcessed status as active and changes the primary company to it.
        runtimeState.adminCompaniesAssignmentRuleEditPage = new AdminCompaniesAssignmentRuleEditPage(getWebDriver()).get();
        testData.addStringTag("newCompanyName", dbHelperCompanies.getCompanyName(false));

        if (testData.getString("newCompanyName") == null) {
            dbHelperCompanies.resetToActiveCompany();
            testData.addStringTag("newCompanyName", dbHelperCompanies.getCompanyName(false));
        }

        runtimeState.adminCompaniesAssignmentRuleEditPage.clickCompany(testData.getString("newCompanyName"));
        commonSteps.the_button_is_clicked("Save");
        outputHelper.takeScreenshots();
        runtimeState.adminCompaniesEditAssignmentRuleModal = new AdminCompaniesEditAssignmentRuleModal(getWebDriver()).get();
        assertEquals("Unexpected Company", dbHelperCompanies.getCompanyName(true) + " is already setup as the Primary Company.If you proceed this will be overwritten with company " +
                testData.getString("newCompanyName") +". Would you like to continue?", runtimeState.adminCompaniesEditAssignmentRuleModal.getParagraph());
        outputHelper.takeScreenshots();
        commonSteps.the_button_is_clicked("Save");
    }


    @ContinueNextStepsOnException
    @Then("^the company uses the new \"([^\"]*)\" rule detail$")
    public void the_company_uses_the_new_rule_detail(String ruleType) {
        if ("Site Type".equalsIgnoreCase(ruleType)) {
            assertTrue("Unexpected Rule Detail", dbHelperCompanies.isSiteTypesAssignmentRule(testData.getString("companyName"), testData.getString("newSiteTypeRuleDetail").replaceAll("'","''")));
        } else {
            assertTrue("Unexpected Rule Detail", dbHelperCompanies.isFundingRouteAssignmentRule(testData.getString("companyName"), testData.getString("newFundingRouteRuleDetail")));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" rule detail is deleted$")
    public void the_rule_detail_is_deleted(String ruleType) {
        runtimeState.scenario.write(ruleType + " rule detail is deleted");
        assertFalse("Unexpected rule detail linked to the company",dbHelperCompanies.isAssignedRuleDetail(testData.getString("ruleDetail")));
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" rule is assigned to the new company$")
    public void the_rule_is_assigned_to_the_new_company(String ruleType) {
        if("Primary".equalsIgnoreCase(ruleType)) {
            assertTrue("Unexpected company name", dbHelperCompanies.isPrimaryCompany(testData.getString("newCompanyName")));
        } else {
            assertEquals("Unexpected Company", testData.getString("newCompanyName"), dbHelperCompanies.getCompanyNameForRuleDetail(testData.getString("ruleDetail"), ruleType));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the company is renamed$")
    public void the_company_is_renamed() {
        assertFalse("Unexpected company name", dbHelperCompanies.isCompanyName(testData.getString("companyName")));
        assertTrue("Unexpected company name", dbHelperCompanies.isCompanyName(testData.getString("renamedCompanyName")));
    }

    @And("^the previous company name is added as a permanent alias$")
    public void the_previous_company_name_is_added_as_a_permanent_alias() {
        //Checks if the original company name is saved as a permanent alias
        assertTrue("Unexpected permanent alias name", dbHelperCompanies.isPermanentAlias(testData.getString("companyName")));
    }

    @ContinueNextStepsOnException
    @Then("^the permanent alias is marked with the permanent flag$")
    public void the_permanent_alias_is_marked_with_the_permanent_flag() {
        //Finds the correct alias in the grid and checks its permanent flag status
        String aliasName = dbHelperCompanies.getPermanentAliasName(testData.getString("companyName"));
        runtimeState.adminEditCompanyAliasesPartial = new AdminEditCompanyAliasesPartial(getWebDriver()).get();
        Grid grid = runtimeState.adminEditCompanyAliasesPartial.getGrid();
        assertNotNull("Unexpected Null Grid", grid);
        List<Row> row = grid.getRows();
        for (int i = 0; i < row.size(); i++) {
            if(row.get(i).getCell(1).getText().equalsIgnoreCase(aliasName)) {
                assertEquals("Unexpected alias status",row.get(i).getCell(2).getText(), "Yes");
            }
        }
        outputHelper.takeScreenshots();
    }

}
