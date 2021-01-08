package mercury.database.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.Message;


@Repository
public class MessageDao {

    private static final Logger logger = LogManager.getLogger();

    private static final String GET_MESSAGE_BASE_SQL = "SELECT DISTINCT  m.* " +
            "  FROM Message m LEFT JOIN JobStatus js ON m.JobStatusId = js.Id ";

    private static final String GET_MESSAGE_WHERE_SQL =  " WHERE m.JobReference = '%s' ";

    private static final String GET_MESSAGE_BY_ROUTING_EVENT_RESOURCE_WHERE_SQL = "WHERE m.jobreference = '%s' " +
            "AND m.RoutingKey = '%s' " +
            "AND m.EventType = '%s' " +
            "AND m.ResourceAssignmentStatusId = %d " +
            "AND m.ResourceAssignmentReasonId = %d " +
            "AND m.CreatedOn > DATEADD(minute, -4, GETUTCDATE()) ";

    private static final String GET_MESSAGE_BY_ROUTING_EVENT_WHERE_SQL = "WHERE m.jobreference = '%s' " +
            "AND m.RoutingKey = '%s' " +
            "AND m.EventType = '%s' " +
            "AND m.JobStatusId = %d " +
            "AND m.CreatedOn > DATEADD(minute, -4, GETUTCDATE()) ";

    private static final String GET_MESSAGE_BY_ROUTING_EVENT_JOBSTATUS_WHERE_SQL = "WHERE m.jobreference = '%s' " +
            "AND m.RoutingKey = '%s' " +
            "AND m.EventType = '%s' " +
            "AND js.Name = '%s' " +
            "AND m.CreatedOn > DATEADD(minute, -4, GETUTCDATE()) ";

    @Autowired
    private EntityManager entityManager;

    /**
     * Fetch Message for given jobReference
     * @param jobReference
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Message> getMessagesByJobReferencey(Integer jobReference, String resourceId) {
        String jpql = GET_MESSAGE_BASE_SQL + GET_MESSAGE_WHERE_SQL;

        String queryStatement = String.format(jpql, jobReference);
        logger.debug("getReasonCategory: " +queryStatement);
        return entityManager.createNativeQuery(queryStatement, Message.class).getResultList();
    }

    /**
     * Fetch an Message for given jobReference
     * @param jobReference
     * @return single record
     */
    public Message getMessagesById(String messageId) {
        String jpql =GET_MESSAGE_BASE_SQL + GET_MESSAGE_WHERE_SQL;
        String queryStatement = String.format(jpql, messageId);
        logger.debug(queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, Message.class);
            return (Message) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch Messages for given jobReference, RoutingKey, EventType, ResourceAssignmentStatus, ResourceAssignmentReason and date
     * @param jobReference
     * @param routingKey
     * @param eventType
     * @param resourceAssignmentStatusId
     * @param resourceAssignmentReasonId
     * @return all matching record
     */
    @SuppressWarnings("unchecked")
    public List<Message> getMessages(Integer jobReference, String routingKey, String eventType, Integer resourceAssignmentStatusId, Integer resourceAssignmentReasonId ) {
        String jpql = GET_MESSAGE_BASE_SQL + GET_MESSAGE_BY_ROUTING_EVENT_RESOURCE_WHERE_SQL;
        String queryStatement = String.format(jpql, jobReference, routingKey, eventType, resourceAssignmentStatusId, resourceAssignmentReasonId);
        logger.debug(queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, Message.class);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch Messages for given jobReference, RoutingKey, EventType, JobStatusId and date
     * @param jobReference
     * @param routingKey
     * @param eventType
     * @param jobStatusId
     * @return all matching record
     */
    @SuppressWarnings("unchecked")
    public List<Message> getMessages(Integer jobReference, String routingKey, String eventType, Integer jobStatusId) {
        String jpql = GET_MESSAGE_BASE_SQL + GET_MESSAGE_BY_ROUTING_EVENT_WHERE_SQL;
        String queryStatement = String.format(jpql, jobReference, routingKey, eventType, jobStatusId);
        logger.debug(queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, Message.class);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch Messages for given jobReference, RoutingKey, EventType, jobStatus and date
     * @param jobReference
     * @param routingKey
     * @param eventType
     * @param jobStatus
     * @return all matching record
     */
    @SuppressWarnings("unchecked")
    public List<Message> getMessages(Integer jobReference, String routingKey, String eventType, String jobStatus) {
        String jpql = GET_MESSAGE_BASE_SQL + GET_MESSAGE_BY_ROUTING_EVENT_JOBSTATUS_WHERE_SQL;
        String queryStatement = String.format(jpql, jobReference, routingKey, eventType, jobStatus);
        logger.debug(queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, Message.class);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Integer getRecordCount(Integer jobReference,  Map<String, Object> queryMap, String createdOn ) {
        String sql = GET_MESSAGE_BASE_SQL
                + String.format("WHERE m.JobReference = %d", jobReference)
                + " AND m.CreatedOn > DATEADD(minute, -4, GETUTCDATE())";

        for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                sql = sql + " AND " + entry.getKey() + " LIKE '%" + entry.getValue() + "%'";
            } else if (value instanceof Integer) {
                sql = sql + " AND " + entry.getKey() + " = " + entry.getValue();
            } else if (value instanceof Float) {
                sql = sql + " AND " + entry.getKey() + " = " + entry.getValue();
            } else if (value instanceof Double) {
                sql = sql + " AND " + entry.getKey() + " = " + entry.getValue();
            }  else {
                sql = sql + " AND " + entry.getKey() + " = '" + entry.getValue() + "'";
            }
        }

        logger.debug("getRecordCount: " + sql);
        try {
            Query query =  entityManager.createNativeQuery(sql, Message.class);
            List<Message> message =  query.getResultList();
            return message.size();
        } catch (Exception e) {
            return null;
        }
    }

}
