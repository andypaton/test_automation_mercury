package mercury.helpers.apihelper;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ApiHelperHangfire extends ApiHelper{

    private static final Logger logger = LogManager.getLogger();


    public void processDeferredJobs() throws ClientProtocolException, IOException, InterruptedException {
        triggerHangfireJob("IHangfireMediatrAdapter.UnDeferJobsAsync");
        int statusCode = restService.getStatusCode();
        assertTrue("Bad status returned from request: " + statusCode, statusCode == 204);

        triggerHangfireJob("IHangfireMediatrAdapter.UnDeferResourcesAsync");
        statusCode = restService.getStatusCode();
        assertTrue("Bad status returned from request: " + statusCode, statusCode == 204);

        rebuildAllMonitorCountData();
    }

    public void processRefreshMonitorDataAndCounts() throws ClientProtocolException, IOException {
        triggerHangfireJob("IMonitorService.RefreshMonitorDataAndCounts");
        int statusCode = restService.getStatusCode();
        assertTrue("Bad status returned from request: " + statusCode, statusCode == 204);
    }

    public void processDeferredJobsWithNoResources() throws ClientProtocolException, IOException {
        triggerHangfireJob("IJobDeferralService.ProcessDeferredJobsWithNoResources");
        int statusCode = restService.getStatusCode();
        assertTrue("Bad status returned from request: " + statusCode, statusCode == 204);
    }

    public void processPortalNotificationsQueue() throws ClientProtocolException, IOException {
        triggerHangfireJob("IQueueService.ProcessPortalNotificationsQueue");
        int statusCode = restService.getStatusCode();
        assertTrue("Bad status returned from request: " + statusCode, statusCode == 204);
    }

    public void processParkedJobs() throws ClientProtocolException, IOException {
        triggerHangfireJob("IQueueService.ProcessParkedJobs");
        int statusCode = restService.getStatusCode();
        assertTrue("Bad status returned from request: " + statusCode, statusCode == 204);
    }

    public void rebuildAllMonitorCountData() throws ClientProtocolException, IOException {
        triggerHangfireJob("IMonitorService.RefreshMonitorDataAndCounts");
        int statusCode = restService.getStatusCode();
        assertTrue("Bad status returned from request: " + statusCode, statusCode == 204);
    }

    public void processPurchaseOrderDocuments() throws ClientProtocolException, IOException {
        triggerHangfireJob("PurchaseOrderService.ProcessPurchaseOrderDocuments");
        int statusCode = restService.getStatusCode();
        assertTrue("Bad status returned from request: " + statusCode, statusCode == 204);
    }

    public void exportJobUpdates() throws ClientProtocolException, IOException {
        triggerHangfireJob("IExternalJobRequestService.ExportJobUpdates");
        int statusCode = restService.getStatusCode();
        assertTrue("Bad status returned from request: " + statusCode, statusCode == 204);
    }

    public void processPPMJobs() throws ClientProtocolException, IOException {
        triggerHangfireJob("IHangfireMediatrAdapter.CreatePpmsFromPpmSchedule");
        int statusCode = restService.getStatusCode();
        assertTrue("Bad status returned from request: " + statusCode, statusCode == 204);
    }

    private void triggerHangfireJob(String jobName) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + "/hangfire/recurring/trigger";
        logger.debug("triggerHangfireJob: " + jobName);
        restService.sendPostRequest(url, "jobs%5B%5D="+jobName, mercuryCookie);
    }

}