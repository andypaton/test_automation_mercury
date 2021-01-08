package mercury.database.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.EventSummary;
import mercury.databuilders.TestData;


@Repository
public class EventSummaryDao {

    private static final Logger logger = LogManager.getLogger();
    private static final String GET_EVENT_SUMMARY_FOR_JOB_REFERENCE = "SELECT ROW_NUMBER() OVER(ORDER BY LoggedAt DESC) AS Id, j.JobReference, es.* FROM EventSummary es JOIN Job j ON j.Id = es.JobId WHERE j.JobReference = %d";

    private static final String GET_EVENT_SUMMARY_FOR_JOB_BASE = "SELECT ROW_NUMBER() OVER(ORDER BY LoggedAt DESC) AS Id, j.JobReference, es.* FROM EventSummary es JOIN Job j ON j.Id = es.JobId WHERE j.JobReference = :jobReference";
    private static final String GET_BY_CREATION_DATE_WHERE = " AND es.LoggedAt >= CAST(:LoggedAt AS datetime2(7)) ";

    @Autowired private EntityManager entityManager;
    @Autowired private TestData testData;


    /**
     * Get all EventSummary rows for jobReference
     * @param jobReference
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<EventSummary> getEventSummaryForJobReference(int jobReference) {
        try {
            String sql = String.format(GET_EVENT_SUMMARY_FOR_JOB_REFERENCE, jobReference);
            logger.debug("getEventSummaryForJobReference: " + sql);
            Query query =  entityManager.createNativeQuery(sql, EventSummary.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.debug(e.getMessage());
            throw e;
        }
    }

    public EventSummary getEventSummaryLike(Integer jobReference,  Map<String, Object> queryMap, String loggedAt) {
        return getEventSummaryForJobReference(jobReference, queryMap, loggedAt, true);
    }

    public EventSummary getEventSummary(Integer jobReference,  Map<String, Object> queryMap, String loggedAt) {
        return getEventSummaryForJobReference(jobReference, queryMap, loggedAt, false);
    }

    public EventSummary getEventSummaryForJobReference(Integer jobReference,  Map<String, Object> queryMap, String loggedAt, boolean like) {
        String queryStatement = GET_EVENT_SUMMARY_FOR_JOB_BASE;
        String whereClause = "";
        for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
            String key = entry.getKey().equals("es.ResourceName") ? "LTRIM(RTRIM(es.ResourceName))" : entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String && like) {
                whereClause = whereClause + " AND " + key + " LIKE :" + entry.getKey();
            }  else {
                whereClause = whereClause + " AND " + key + " = :" + entry.getKey();
            }
        }

        queryStatement = GET_EVENT_SUMMARY_FOR_JOB_BASE + whereClause + GET_BY_CREATION_DATE_WHERE;
        String debugStr = queryStatement.replaceAll(":jobReference", String.valueOf(jobReference)).replaceAll(":LoggedAt", "'" + loggedAt + "'");
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, EventSummary.class);
            query.setParameter("jobReference", jobReference);
            query.setParameter("LoggedAt", loggedAt);
            for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
                debugStr = debugStr.replace(":" + entry.getKey(), "'" + entry.getValue() + "'");
            }
            logger.debug("getEventSummaryForJobReference: " + debugStr);
            EventSummary eventSummary =  (EventSummary) query.getSingleResult();
            return eventSummary;
        } catch (Exception e) {
            logger.debug(e.getMessage());
            testData.put("sql", debugStr);
            return null;
        }
    }

}
