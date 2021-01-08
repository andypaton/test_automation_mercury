package mercury.steps.portal.ppm;

import static mercury.runtime.ThreadManager.getWebDriver;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.And;
import mercury.database.dao.QuotePriorityDao;
import mercury.database.models.QuotePriority;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperQuotes;
import mercury.pageobject.web.portal.ppm.PPMRequestQuotePage;
import mercury.runtime.RuntimeState;

public class PortalPPMRequestQuoteSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private QuotePriorityDao quotePriorityDao;
    @Autowired private DbHelperQuotes dbHelperQuotes;


    @And("^a random quote Location is selected$")
    public void a_random_quote_location_is_selected() throws Throwable {
        runtimeState.ppmRequestQuotePage.selectRandomLocation();
    }

    @And("^a random quote Asset Main Type is selected$")
    public void a_random_quote_asset_main_type_is_selected() throws Throwable {
        runtimeState.ppmRequestQuotePage = new PPMRequestQuotePage(getWebDriver()).get();
        List<String> options = runtimeState.ppmRequestQuotePage.getQuoteAssetMainTypes();
        String assetMainType = options.get(RandomUtils.nextInt(0, options.size()-1));
        runtimeState.ppmRequestQuotePage.selectQuoteAssetMainType(assetMainType);
    }

    @And("^a random quote Asset Sub Type is selected$")
    public void a_random_quote_asset_sub_type_is_selected() throws Throwable {
        runtimeState.ppmRequestQuotePage.selectRandomQuoteAssetSubType();
    }

    @And("^a random scope of work is added$")
    public void a_random_scope_of_work_is_entered() throws Throwable {
        String scope = "Please enter some notes to verify the database update " + DateHelper.dateAsString(new Date());
        runtimeState.scenario.write("Adding Description: " + scope);
        runtimeState.ppmRequestQuotePage.enterRandomScopeOfWork(scope);
    }

    @And("^a \"([^\"]*)\" priority is selected$")
    public void a_priority_is_selected(String priority) throws Throwable {
        QuotePriority quotePriority = quotePriorityDao.getQuotePriorityByUrgencyAndFundingRoute(false, priority);
        runtimeState.ppmRequestQuotePage.selectQuotePriority(quotePriority.getName());
    }

    @And("^the quote save button is selected$")
    public void the_quote_save_button_is_selected() throws Throwable {
        outputHelper.takeScreenshots();
        runtimeState.updateSavedPage = runtimeState.ppmRequestQuotePage.saveButton();
        outputHelper.takeScreenshots();
    }

    @And("^a fault type is selected at random$")
    public void a_fault_type_is_selected_at_random() throws Throwable {
        runtimeState.ppmRequestQuotePage.selectRandomFaultType();
        outputHelper.takeScreenshots();
    }

    @And("^a random quote is created for a \"([^\"]*)\" quote type and saved$")
    public void a_random_quote_is_created_for_a_quote_type_and_saved(String quoteType) throws Throwable {
        a_random_quote_asset_main_type_is_selected();
        a_random_quote_asset_sub_type_is_selected();
        a_random_quote_location_is_selected();
        a_fault_type_is_selected_at_random();
        a_random_scope_of_work_is_entered();
        String quoteTypeAlias = dbHelperQuotes.getFundingRouteAlias(quoteType);
        runtimeState.ppmRequestQuotePage.selectQuoteTypeAnswer(quoteTypeAlias);
        a_priority_is_selected(quoteType);
        the_quote_save_button_is_selected();
    }
}
