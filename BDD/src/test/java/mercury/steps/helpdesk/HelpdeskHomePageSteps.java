package mercury.steps.helpdesk;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.en.When;
import mercury.runtime.RuntimeState;

public class HelpdeskHomePageSteps {

    @Autowired private RuntimeState runtimeState;

    @When("\"([^\"]*)\" is selected from the Mercury navigation menu")
    public void Admin_is_selected_from_the_Mercury_navigation_menu(String menuItem) {
        if ("Admin".equals(menuItem)) {
            runtimeState.adminHomePage = runtimeState.helpdeskNavBar.OpenAdminApp();
        } else if ("Helpdesk".equals(menuItem)) {
            runtimeState.helpdeskNavBar = runtimeState.helpdeskNavBar.OpenHelpdesk();
        } else if ("Portal".equals(menuItem)) {
            runtimeState.portalNavBar = runtimeState.helpdeskNavBar.OpenPortal();
        } else {
            throw new PendingException("Unexpected menu item: " + menuItem);
        }
    }

}
