package mercury.database.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.ResourceAssignment;


@Repository
public class ResourceAssignmentDao {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private EntityManager entityManager;

    private static final String GET_RESOURCEASSIGNMENT_BASE_SQL = "SELECT DISTINCT ra.*  " +
            "  FROM ResourceAssignment ra LEFT OUTER JOIN Job j ON ra.JobId = j.Id ";

    private static final String GET_RESOURCEASSIGNMENT_BY_RESOURCEASSIGNMENTSTATUS__REASON_WHERE_SQL = " WHERE j.jobreference = %d " +
            " AND ra.ResourceId = %d " +
            " AND ra.ResourceAssignmentStatusId = %d " +
            " AND ra.ReasonId = %d " +
            " --AND ra.UpdatedOn > DATEADD(minute, -2, GETDATE()) ";

    private static final String GET_RESOURCEASSIGNMENT_BY_RESOURCEASSIGNMENTSTATUS_WHERE_SQL = " WHERE j.jobreference = %d " +
            " AND ra.ResourceId = %d " +
            " AND ra.ResourceAssignmentStatusId = %d " +
            " --AND ra.UpdatedOn > DATEADD(minute, -2, GETDATE()) ";

    private static final String GET_RESOURCEASSIGNMENT_BY_JOB_REFERENCE_WHERE_SQL = "WHERE j.JobReference = :jobReference AND ra.Active = 1 ";
    private static final String FILTER_BY_UPDATEDON_SQL = " /*AND ra.UpdatedOn > CAST(:createdOn AS datetime2(7)) */";  // should remove the comment block when bug xxx fixed
    private static final String GET_RESOURCEASSIGNMENT_BY_JOB_ID_WHERE_SQL = "WHERE j.Id = :jobId";

    // ResourceAssignmentStatusID 14 = Removal Requested, JobStatusId 5 = Cancellation Requested
    private static final String GET_REMOVAL_REQUESTED_RESOURCEASSIGNMENT_WHERE_SQL = "WHERE ra.ResourceAssignmentStatusId = 14 AND j.JobStatusId = 5 AND j.CreatedOn < DATEADD(day, -:daysOld , GETDATE())  ";


    /**
     * Fetch ResourceAssigment for given jobReference, RoutingKey, EventType, ResourceAssignmentStatus, ResourceAssignmentReason and date
     * @param jobReference
     * @param routingKey
     * @param resourceAssignmentStatusId
     * @param resourceAssignmentReasonId
     * @return all matching record
     */
    public ResourceAssignment getAssigmentByStatusReason(Integer jobReference, Integer resourceId, Integer resourceAssignmentStatusId, Integer resourceAssignmentReasonId ) {
        String jpql = GET_RESOURCEASSIGNMENT_BASE_SQL + GET_RESOURCEASSIGNMENT_BY_RESOURCEASSIGNMENTSTATUS__REASON_WHERE_SQL;
        String queryStatement = String.format(jpql, jobReference, resourceId, resourceAssignmentStatusId, resourceAssignmentReasonId);
        logger.debug(queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, ResourceAssignment.class);
            return (ResourceAssignment) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Fetch ResourceAssigment for given jobReference, RoutingKey, EventType, ResourceAssignmentStatus and date
     * @param jobReference
     * @param routingKey
     * @param resourceAssignmentStatusId
     * @return all matching record
     */
    public ResourceAssignment getAssigmentByStatus(Integer jobReference, Integer resourceId, Integer resourceAssignmentStatusId ) {
        String jpql = GET_RESOURCEASSIGNMENT_BASE_SQL + GET_RESOURCEASSIGNMENT_BY_RESOURCEASSIGNMENTSTATUS_WHERE_SQL;
        String queryStatement = String.format(jpql, jobReference, resourceId, resourceAssignmentStatusId);
        logger.debug(queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, ResourceAssignment.class);
            return (ResourceAssignment) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ResourceAssigment for given jobReference
     * @param jobReference
     * @return all matching record
     */
    @SuppressWarnings("unchecked")
    public List<ResourceAssignment> getByJobReference(Integer jobReference) {
        String sql = "SELECT ra.* FROM ResourceAssignment ra INNER JOIN Job j ON j.Id = ra.JobId WHERE ra.Active = 1 AND JobReference = " + jobReference + " ORDER BY ra.Id DESC";
        logger.debug("getByJobReference: " + sql);
        try {
            Query query =  entityManager.createNativeQuery(sql, ResourceAssignment.class);
            List<ResourceAssignment> resourceAssignments =  query.getResultList();
            return resourceAssignments;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ResourceAssigment for given jobReference
     * @param jobReference
     * @return all matching record
     */
    public ResourceAssignment getAssigmentByJobReference(Integer jobReference ) {
        String queryStatement = GET_RESOURCEASSIGNMENT_BASE_SQL + GET_RESOURCEASSIGNMENT_BY_JOB_REFERENCE_WHERE_SQL;
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, ResourceAssignment.class);
            query.setParameter("jobReference", jobReference);
            return (ResourceAssignment) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ResourceAssigment for given jobId
     *
     * @param jobId
     * @return all matching record
     */
    public ResourceAssignment getAssigmentByJobId(Integer jobId) {
        String queryStatement = GET_RESOURCEASSIGNMENT_BASE_SQL + GET_RESOURCEASSIGNMENT_BY_JOB_ID_WHERE_SQL;
        try {
            Query query = entityManager.createNativeQuery(queryStatement, ResourceAssignment.class);
            query.setParameter("jobId", jobId);
            return (ResourceAssignment) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<ResourceAssignment> getRemovalRequestedJobs(Integer daysOld ) {
        String queryStatement = GET_RESOURCEASSIGNMENT_BASE_SQL + GET_REMOVAL_REQUESTED_RESOURCEASSIGNMENT_WHERE_SQL;
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, ResourceAssignment.class);
            query.setParameter("daysOld", daysOld);
            List<ResourceAssignment> resourceAssignment =  query.getResultList();
            return resourceAssignment;
        } catch (Exception e) {
            return null;
        }
    }


    @SuppressWarnings("unchecked")
    public Integer getRecordCount(Integer jobReference,  Map<String, Object> queryMap, String createdOn ) {
        String queryStatement = GET_RESOURCEASSIGNMENT_BASE_SQL;
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

        queryStatement = GET_RESOURCEASSIGNMENT_BASE_SQL + GET_RESOURCEASSIGNMENT_BY_JOB_REFERENCE_WHERE_SQL + FILTER_BY_UPDATEDON_SQL + whereClause;
        String loggerSQL = queryStatement;
        for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
            loggerSQL = loggerSQL.replaceAll(":" + entry.getKey(), entry.getValue().toString());
        }
        logger.debug("getRecordCount: " + loggerSQL.replaceAll(":jobReference", jobReference.toString()));
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, ResourceAssignment.class);
            query.setParameter("jobReference", jobReference);
            // should remove the comment block when bug xxx fixed
            //query.setParameter("createdOn", createdOn);
            for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            List<ResourceAssignment> resourceAssignment =  query.getResultList();
            return resourceAssignment.size();
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }
}
