package mercury.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mercury.database.dao.SiteViewDao;
import mercury.database.models.SiteView;
import mercury.databuilders.TestData;
import mercury.helpers.apihelper.ApiHelper;
import mercury.runtime.RuntimeState;

@Component
public class HelpdeskSearchHelper {

    @Autowired private SiteViewDao siteViewDao;
    @Autowired private TestData testData;
    @Autowired private SiteView siteView;
    @Autowired private ApiHelper apiHelper;
    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;

    public void searchForSiteHavingLoggedIncidentsInLast30_Days(String state) throws Exception {
        SiteView site = siteViewDao.getSiteWithLoggedIncidentsInLast30Days(state);
        searchForSite(site);
    }

    public void searchForSiteWithLinkedIncidentCriterion() throws Exception {
        SiteView site = siteViewDao.getSiteWithLinkedIncidentCriterion();
        if (site == null) {
            site = siteViewDao.getSiteByState("Occupied");  // get any occupied site
            apiHelper.createLinkedIncidentCriterion();      // create a linked incident criterion
        }

        searchForSite(site);
    }

    private void searchForSite(SiteView site) {
        siteView.copy(site);
        testData.put("siteId", siteView.getId());
        testData.put("siteName", siteView.getName());
        runtimeState.scenario.write("Searching for: " + siteView.getName());

        testData.put("siteName", siteView.getName());
        runtimeState.helpdeskSitePage = runtimeState.helpdeskSearchBar.searchForSite(siteView.getName());
        outputHelper.takeScreenshots();
    }

}
