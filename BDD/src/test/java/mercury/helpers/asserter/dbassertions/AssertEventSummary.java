package mercury.helpers.asserter.dbassertions;

import java.util.List;

import mercury.database.dao.EventSummaryDao;
import mercury.database.models.EventSummary;
import mercury.helpers.asserter.common.AssertTask;

public class AssertEventSummary implements AssertTask {

    private EventSummaryDao eventSummaryDao;
    private Integer jobReference;
    private String event;
    public String timeline;
    public String impersonatedResourceName;

    private String failureMessage = "Event not found in EventSummary: ";

    public AssertEventSummary(EventSummaryDao eventSummaryDao, Integer jobReference, String event, String impersonatedResourceName) {
        this.eventSummaryDao = eventSummaryDao;
        this.jobReference = jobReference;
        this.event = event;
        this.impersonatedResourceName = impersonatedResourceName;
        failureMessage = failureMessage + event;
    }

    @Override
    public boolean execute() {

        List<EventSummary> events = eventSummaryDao.getEventSummaryForJobReference(jobReference);
        timeline = "Job Reference: " + jobReference + "\n-----------------------\n";
        boolean found = false;

        try {
            for (EventSummary eventSummary : events) {
                timeline = timeline + eventSummary.getLoggedAt().toString().replaceAll("\\..*$", "") + "\t" + eventSummary.getTitle() +  "\n";
                if (eventSummary.getTitle().matches(event + ".*")) {
                    if (impersonatedResourceName != null) {
                        String detail = eventSummary.getDetail1() + eventSummary.getDetail2();
                        if (detail.contains("Impersonating") && detail.contains(impersonatedResourceName)) {
                            found = true;
                        } else {
                            failureMessage = "Event found but does not contain Impersonating: " + impersonatedResourceName;
                        }

                    } else {
                        found = true;
                    }
                }
            }

        } catch (Throwable t) {
            failureMessage = t.getMessage();
            return false;
        }

        return found;
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
