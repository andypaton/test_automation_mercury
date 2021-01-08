package mercury.helpers.asserter.dbassertions;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import mercury.database.dao.SiteVisitsDao;
import mercury.database.models.SiteVisits;
import mercury.databuilders.UpdateJob;
import mercury.helpers.asserter.common.AssertTask;

public class AssertSiteVisit implements AssertTask {

    private SiteVisitsDao siteVisitsDao;

    private UpdateJob updateJob;

    private String failureMessage;

    public AssertSiteVisit(SiteVisitsDao siteVisitsDao, UpdateJob updateJob) {
        this.updateJob = updateJob;
        this.siteVisitsDao = siteVisitsDao;
    }

    @Override
    public boolean execute() {
        List<SiteVisits> siteVisits;
        try {

            siteVisits = siteVisitsDao.getJobSiteVisits(updateJob);
            assertNotNull("Unexpected null recordset: could not find site visit", siteVisits);

        } catch (Throwable t) {
            failureMessage = t.getMessage();
            return false;
        }

        return true;
    }

    @Override
    public String getTaskName() {
        return this.getClass().getName();
    }

    @Override
    public String getTaskFailureMessage() {
        return failureMessage;
    }
}

