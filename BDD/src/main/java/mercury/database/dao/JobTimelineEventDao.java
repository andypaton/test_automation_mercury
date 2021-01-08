package mercury.database.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.JobTimelineEvent;
import mercury.database.models.Quote;

@Repository
public class JobTimelineEventDao {
    private static final Logger logger = LogManager.getLogger();

    private static final String GET_JOB_TIMELINE_EVENT_BASE_SQL = "SELECT jtle.* FROM Job j INNER JOIN JobTimelineEvent jtle ON j.id = jtle.jobid ";
    private static final String GET_JOB_TIMELINE_EVENT_BY_JOB_REFERENCE_WHERE_SQL = "WHERE j.jobreference = %d ";
    private static final String GET_JOB_TIMELINE_EVENT_BY_JOB_REFERENCE_WHERE_PARAMETER_SQL = "WHERE j.jobreference = :jobReference ";


    private static final String GET_BY_EVENT_TYPE_AND_TITLE_WHERE = " AND jtle.JobEventTypeId = %d AND jtle.Title = '%s' ";

    private static final String GET_BY_DETAIL_1_WHERE = " AND jtle.Detail1 LIKE  '%s' ";
    private static final String GET_BY_REASON_WHERE = " AND jtle.Reason LIKE '%s' ";
    private static final String GET_BY_NOTES_WHERE = " AND jtle.Notes LIKE '%s' ";
    private static final String GET_BY_CREATION_DATE_WHERE = " AND jtle.CreatedOn >= CAST(:createdOn AS datetime2(7)) ";

    @Autowired
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<Quote> getAll() {
        return entityManager.createQuery("from mercury.database.models.JobTimelineEvent").getResultList();
    }

    /**
     * Fetch Job Timeline Event entity by jobReference
     * @param id
     * @return
     */
    public JobTimelineEvent getByJobReference(int jobReference) {
        String jpql = "SELECT TOP(1) jtle.* FROM Job j INNER JOIN JobTimelineEvent jtle ON j.id = jtle.jobid WHERE j.jobreference = %d ORDER BY CreatedOn DESC ";

        String queryStatement = String.format(jpql, jobReference);
        logger.debug(queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, JobTimelineEvent.class);
            return (JobTimelineEvent) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch Job Timeline Event entity by jobReference
     * @param id
     * @return
     */
    public JobTimelineEvent getLatest(int jobReference, String title) {
        String sql = "SELECT TOP(1) jtle.* FROM Job j INNER JOIN JobTimelineEvent jtle ON j.id = jtle.jobid WHERE j.jobreference = %d AND Title = '%s' ORDER BY CreatedOn DESC ";
        String queryStatement = String.format(sql, jobReference, title);

        logger.debug(queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, JobTimelineEvent.class);
            return (JobTimelineEvent) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch Job Timeline Event entity by jobReference, JobEventType, Title and Detail
     * @param id
     * @return
     */
    public JobTimelineEvent getByJobReference(Integer jobReference, Integer JobEventTypeId, String title, String detail1, String reason, String notes, String createdOn) {
        String queryStatement; // Stores the sql after any replacement of data values
        queryStatement = GET_JOB_TIMELINE_EVENT_BASE_SQL
                + String.format(GET_JOB_TIMELINE_EVENT_BY_JOB_REFERENCE_WHERE_SQL, jobReference)
                + String.format(GET_BY_EVENT_TYPE_AND_TITLE_WHERE, JobEventTypeId, title);

        if(!detail1.isEmpty()) {
            queryStatement = queryStatement.concat(String.format(GET_BY_DETAIL_1_WHERE, "%" + detail1 + "%"));
        }

        if(!reason.isEmpty()) {
            queryStatement = queryStatement.concat(String.format(GET_BY_REASON_WHERE, "%" + reason + "%"));
        }
        if(!notes.isEmpty()) {
            queryStatement = queryStatement.concat(String.format(GET_BY_NOTES_WHERE, "%" + notes + "%"));
        }
        queryStatement = queryStatement + GET_BY_CREATION_DATE_WHERE;

        logger.debug(queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, JobTimelineEvent.class);
            query.setParameter("createdOn", createdOn);
            return (JobTimelineEvent) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public JobTimelineEvent getByJobReferenceMap(Integer jobReference, Map<String, Object> queryMap, String createdOn) {
        String queryStatement = "SELECT TOP(1) jtle.* FROM Job j INNER JOIN JobTimelineEvent jtle ON j.id = jtle.jobid WHERE j.jobreference = :jobReference ";
        String whereClause = "";
        for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                whereClause = whereClause + " AND " + entry.getKey() + " LIKE :" + entry.getKey();
            } else if (value instanceof Integer) {
                whereClause = whereClause + " AND " + entry.getKey() + " = :" + entry.getKey();
            } else if (value instanceof Float) {
                whereClause = whereClause + " AND " + entry.getKey() + " = :" + entry.getKey();
            } else if (value instanceof Double) {
                whereClause = whereClause + " AND " + entry.getKey() + " = :" + entry.getKey();
            }  else {
                whereClause = whereClause + " AND " + entry.getKey() + " = :" + entry.getKey();
            }
        }

        queryStatement = queryStatement + whereClause + GET_BY_CREATION_DATE_WHERE;
        String debugStr = queryStatement.replaceAll(":jobReference", String.valueOf(jobReference)).replaceAll(":createdOn", "'" + createdOn + "'");
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, JobTimelineEvent.class);
            query.setParameter("jobReference", jobReference);
            query.setParameter("createdOn", createdOn);
            for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
                debugStr = debugStr.replace(":" + entry.getKey(), "'" + entry.getValue() + "'");
            }
            logger.debug("getByJobReferenceMap " + debugStr);
            return (JobTimelineEvent) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Integer getRecordCount(Integer jobReference,  Map<String, Object> queryMap, String createdOn ) {
        String queryStatement = GET_JOB_TIMELINE_EVENT_BASE_SQL;
        String whereClause = "";
        for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                whereClause = whereClause + " AND " + entry.getKey() + " LIKE :" + entry.getKey();
            } else if (value instanceof Integer) {
                whereClause = whereClause + " AND " + entry.getKey() + " = :" + entry.getKey();
            } else if (value instanceof Float) {
                whereClause = whereClause + " AND " + entry.getKey() + " = :" + entry.getKey();
            } else if (value instanceof Double) {
                whereClause = whereClause + " AND " + entry.getKey() + " = :" + entry.getKey();
            }  else {
                whereClause = whereClause + " AND " + entry.getKey() + " = :" + entry.getKey();
            }
        }

        queryStatement = GET_JOB_TIMELINE_EVENT_BASE_SQL + GET_JOB_TIMELINE_EVENT_BY_JOB_REFERENCE_WHERE_PARAMETER_SQL + whereClause  + GET_BY_CREATION_DATE_WHERE;
        String debugStr = queryStatement.replaceAll(":jobReference", String.valueOf(jobReference)).replaceAll(":createdOn", "'" + createdOn + "'");
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, JobTimelineEvent.class);
            query.setParameter("jobReference", jobReference);
            query.setParameter("createdOn", createdOn);
            for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
                debugStr = debugStr.replace(":" + entry.getKey(), "'" + entry.getValue() + "'");
            }
            logger.debug("getRecordCount: " + debugStr);
            List<JobTimelineEvent> jobTimelineEvents =  query.getResultList();
            return jobTimelineEvents.size();
        } catch (Exception e) {
            return null;
        }
    }

}
