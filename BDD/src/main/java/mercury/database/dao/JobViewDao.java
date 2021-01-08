package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.JobView;
import mercury.helpers.DbUtilityHelper;
import mercury.helpers.FileHelper;
import static mercury.helpers.StringHelper.quote;


@Repository
public class JobViewDao {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private DbUtilityHelper dbUtilityHelper;

    private static final String GET_JOB_VIEW_OPEN_WHERE = " WHERE ra.ResourceId = :resourceId " +
            " AND ( ra.ResourceAssignmentStatusId NOT IN (4, 5, 18) " +
            " AND ( NOT (j.JobStatusId = 1 AND ra.ResourceAssignmentStatusId = 19))" +
            " AND sv.Id IS NOT NULL " +
            " AND js.IsTerminal <> 1 " +
            " AND ras.IsTerminal <> 1 ) ";

    private static final String GET_OPEN_JOBS_FOR_RESOURCE_NAME = " WHERE ra.ResourceId = (SELECT id FROM resource WHERE name= :resourceName)" +
            " AND ra.ResourceAssignmentStatusId NOT IN (4, 13, 14, 15, 16, 18, 20) " +
            " AND j.JobStatusId IN (1, 2, 3, 5, 7, 15, 17)  " +
            " AND rae.ResourceAssignmentEventTypeId IN (21, 4)  " +
            " And (sv.id = (SELECT max(id) FROM SiteVisits WHERE ResourceAssignmentId = ra.id) OR sv.id IS NULL) ";

    private static final String GET_JOB_VIEW_AWAITING_WHERE = " WHERE ra.ResourceId = :resourceId " +
            " AND ra.ResourceAssignmentStatusId IN (1, 4, 5) " +
            " AND j.JobStatusId NOT IN (SELECT Id FROM JobStatus WHERE IsTerminal = 1) ";

    private static final String GET_JOB_PREVIOUSLY_DECLINED_AWAITING_ACCEPTANCE_WHERE = "WHERE j.id = ( " +
            " SELECT  TOP(1) j.JobReference " +
            " FROM Job j INNER JOIN  " +
            " (SELECT ra.jobid FROM ResourceAssignment ra INNER JOIN ResourceAssignment ra_d ON ra.JobId = ra_d.JobId  " +
            " WHERE ra.ResourceId = :resourceId " +
            " AND ra.ResourceAssignmentStatusId IN (1, 4, 5)  " +
            " AND ra_d.ResourceAssignmentStatusId IN (16) " +
            " GROUP BY ra.jobid HAVING COUNT( ra.ResourceId) = 1 AND COUNT(ra_d.ResourceId) > 0 )  srj ON j.id = srj.jobid  " +
            " WHERE j.JobStatusId IN (1, 11)  " +
            " ORDER BY NEWID())";

    private static final String GET_COMPLETED_P1_WITHIN_LAST_FORTNIGHT = " WHERE fr.Id IS NOT NULL "
            + " AND ra.ResourceAssignmentStatusId = 13 "
            + " AND ra.CreatedOn > GETDATE() - 14 "
            + " AND fr.InitialFunding = 1 "
            + " AND fpms.Priority = 1 "
            + " AND jt.Name != 'Warranty'";
         //   + " ORDER BY NEWID()";  // Commented out as query was timing out

    //	private static final String GET_JOB_PREVIOUSLY_DECLINED_AWAITING_ACCEPTANCE_WHERE = "WHERE j.id = ( " +
    //			"SELECT  TOP(1) j.JobReference " +
    //			"FROM Job j LEFT JOIN ResourceAssignment ra   ON j.Id = ra.JobId " +
    //			"	LEFT JOIN ResourceAssignment ra_d ON j.Id = ra_d.JobId  " +
    //			"WHERE ra.ResourceId = '%s'  " +
    //			"	AND ra.ResourceAssignmentStatusId IN (1, 4, 5) " +
    //			"	AND j.JobStatusId NOT IN (1, 11) " +
    //			"	AND ra_d.ResourceAssignmentStatusId IN (16) " +
    //			"GROUP BY j.JobReference  HAVING COUNT(ra.ResourceId)>0 AND COUNT(ra_d.ResourceId)>0 " +
    //			"ORDER BY NEWID())";

    private static final String GET_JOB_WITH_ONLY_SPECIFIED_RESOURCE_ALLOCATED_WHERE = "WHERE j.JobReference = ( " +
            " SELECT TOP(1) j.JobReference " +
            " FROM Job j INNER join " +
            " (SELECT ra.jobid FROM ResourceAssignment ra INNER JOIN ResourceAssignment ra_d ON ra.JobId = ra_d.JobId " +
            " WHERE ra.ResourceId = :resourceId " +
            " AND ra.ResourceAssignmentStatusId NOT IN (4, 13, 15, 16) " +
            " GROUP BY ra.jobid HAVING COUNT( ra.ResourceId) = 1 AND COUNT(ra_d.ResourceId) = 1 )  srj ON j.id = srj.jobid " +
            " WHERE j.JobStatusId IN (2, 3, 7, 15)  ORDER BY NEWID()) ";

    private static final String GET_JOB_AWAITING_QUOTE_REQUEST_APPROVAL_WHERE =  "WHERE j.JobReference = ( " +
            " SELECT TOP(1) j.JobReference " +
            " FROM Job j LEFT JOIN Quote q    ON j.Id = q.JobId " +
            " INNER JOIN Resource r ON q.QuoteRequestApproverId = r.Id " +
            " WHERE  j.JobStatusId IN (16)  " +
            " and r.Id = :resourceId  " +
            " GROUP BY j.JobReference " +
            " HAVING COUNT(q.QuoteRequestApproverId)>0" +
            " ORDER BY NEWID())";


    private static final String GET_DUPLICATE_JOBS_SQL = " WITH duplicate_CTE AS ( %s JOIN (  SELECT TOP(1) j.SiteId, j.LocationId, j.faultTypeId, j.AssetClassificationId FROM " +
            "  %helpdeskdb.Job j  WHERE j.JobStatusId = 1 " +
            "  ORDER BY NEWID()) dup ON  " +
            "  j.SiteId = dup.SiteId and j.LocationId = dup.LocationId and j.FaultTypeId = dup.FaultTypeId and j.AssetClassificationId = dup.AssetClassificationId )" +
            "  SELECT TOP(1) * FROM duplicate_CTE ORDER BY NEWID()";

    private static final String ORDER_BY_JOB_REFERENCE =  " ORDER BY j.JobReference ";

    @Autowired
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<JobView> getAll() {
        return entityManager.createQuery("from mercury.database.models.JobView").getResultList();
    }

    /**
     * Fetch JobView entity by jobReference
     * @param id
     * @return
     * @throws Exception
     */
    public JobView getByJobReference(String jobReference) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_VIEW_BASE_SQL.sql") +
                "WHERE j.JobReference = :jobReference";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getByJobReference: " + sql.replace(":jobReference", jobReference));
        try {
            Query query = entityManager.createNativeQuery(sql, JobView.class);
            query.setParameter("jobReference", jobReference);
            return (JobView) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public JobView getByJobReference(Integer jobreference) throws Exception {
        return getByJobReference(String.valueOf(jobreference));
    }

    /**
     * Fetch JobView entity by jobReference
     * @param id
     * @return
     * @throws Exception
     */
    public JobView getByJobReference(String jobReference, String jobStatus) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_VIEW_BASE_SQL.sql") +
                "WHERE j.JobReference = :jobReference ";

        if (jobStatus.equals("Canceled") || jobStatus.equals("Cancelled")) {
            sql = sql + "AND (js.Name = :jobStatus OR j.JobStatusId = 10) ";
        } else {
            sql = sql + "AND (js.Name = :jobStatus OR j.JobStatusId = 12) ";
        }

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getByJobReference: " + sql.replace(":jobStatus", quote(jobStatus)).replace(":jobReference", jobReference));
        try {
            Query query = entityManager.createNativeQuery(sql, JobView.class);
            query.setParameter("jobStatus", jobStatus);
            query.setParameter("jobReference", jobReference);
            return (JobView) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public JobView getByJobReference(Integer jobreference, String jobStatus) throws Exception {
        return getByJobReference(String.valueOf(jobreference), jobStatus);
    }

    /**
     * Fetch JobView entity for jobs in logged state, by siteId
     * @param siteName
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public List<JobView> getDuplicateJobsBySiteName(String siteName) throws Exception {
        String jpql = FileHelper.getResourceFile("sql/select/", "GET_JOB_VIEW_BASE_SQL.sql") +
                "WHERE j.jobstatusid IN (1, 2) and s.name = :siteName and sl.Name != 'Store'";

        String sql = jpql;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getDuplicateJobsBySiteName: " + sql);
        try {
            Query query = entityManager.createNativeQuery(sql, JobView.class);
            query.setParameter("siteName", siteName);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Generic method for querying the database
     * @param whereClase
     * @return
     * @throws Exception
     */
    private JobView getGenericSingle(String whereClase, String jobReference) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_VIEW_BASE_SQL.sql") + whereClase
                + ORDER_BY_JOB_REFERENCE;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug(sql);
        Query query = entityManager.createNativeQuery(sql, JobView.class);
        query.setParameter("jobReference", jobReference);
        return (JobView) query.getSingleResult();
    }

    /**
     * Generic method for querying the database
     * @param whereClase
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private List<JobView> getGenericList(String whereClause, Integer resourceId) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_VIEW_BASE_SQL.sql") + whereClause
                + ORDER_BY_JOB_REFERENCE;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug(sql.replace(":resourceId", resourceId.toString()));
        Query query = entityManager.createNativeQuery(sql, JobView.class);
        query.setParameter("resourceId", resourceId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    private List<JobView> getGenericList(String whereClause, String resourceName) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_VIEW_BASE_SQL.sql") + whereClause
                + ORDER_BY_JOB_REFERENCE;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug(sql);
        Query query = entityManager.createNativeQuery(sql, JobView.class);
        query.setParameter("resourceName", resourceName);
        return query.getResultList();
    }

    /**
     * Fetch JobView entity by primary key
     * @param id
     * @return
     * @throws Exception
     */
    public JobView get(String id) throws Exception {
        String sql = " WHERE j.Id = :jobReference";
        return getGenericSingle(sql, id);
    }

    /**
     * Fetch List<Jobview>
     * @param id
     * @return
     * @throws Exception
     */
    public List<JobView> getOpenJobs(Integer resourceId) throws Exception {
        String sql = GET_JOB_VIEW_OPEN_WHERE;
        return getGenericList(sql, resourceId);
    }

    @SuppressWarnings("unchecked")
    public List<JobView> getOpenJobs(Integer resourceId, String username) throws Exception {

        String sql = "(" + FileHelper.getResourceFile("sql/select/", "GET_JOB_VIEW_BASE_SQL.sql")
        + GET_JOB_VIEW_OPEN_WHERE
        + "	 ) UNION ("
        + FileHelper.getResourceFile("sql/select/", "GET_JOB_PORTAL_SQL.sql") + ")";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        username = username + "%";
        logger.debug("getOpenJobs: " + sql.replace(":resourceId", resourceId.toString()).replace(":Username", "'" + username + "'"));

        Query query = entityManager.createNativeQuery(sql, JobView.class);
        query.setParameter("resourceId", resourceId);
        query.setParameter("Username", username);
        try {
            return query.getResultList();

        } catch (Exception e) {
            return null;
        }
    }

    public List<JobView> getOpenJobs(String resourceName) throws Exception {
        String sql = GET_OPEN_JOBS_FOR_RESOURCE_NAME;
        return getGenericList(sql, resourceName);
    }


    /**
     * Fetch List<Jobview>
     * @param id
     * @return
     * @throws Exception
     */
    public List<JobView> getAwaitingJobs(Integer resourceId) throws Exception {
        String sql = GET_JOB_VIEW_AWAITING_WHERE;
        return getGenericList(sql, resourceId);
    }

    /**
     * Fetch List<Jobview> to used when checking duplicate jobs
     *
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public List<JobView> getDuplicateJobs() throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_VIEW_BASE_SQL.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        String duplicateSQL = dbUtilityHelper.dbNameSchemaReplacement(GET_DUPLICATE_JOBS_SQL);
        sql = String.format(duplicateSQL, sql);

        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        logger.debug(this.getClass().getName() + ":" + methodName + ":" + sql);

        try {
            Query query = entityManager.createNativeQuery(sql, JobView.class);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }


    //GET_JOB_PREVIOUSLY_DECLINED_AWAITING_ACCEPTANCE_WHERE
    /**
     * Fetch a Job that is in awaiting acceptance state for the given resource but has been previously declined
     * @param resourceId
     * @return
     * @throws Exception
     */
    public JobView getDeclinedJob(Integer resourceId) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_VIEW_BASE_SQL.sql") + GET_JOB_PREVIOUSLY_DECLINED_AWAITING_ACCEPTANCE_WHERE;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug(sql);
        try {
            Query query = entityManager.createNativeQuery(sql, JobView.class);
            query.setParameter("resourceId", resourceId);
            return (JobView) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    //GET_JOB_WITH_ONLY_SPECIFIED_RESOURCE_ALLOCATED_WHERE
    /**
     * Fetch a Job that is in awaiting open state for the given resource and has only that resource assigned to it that is active
     * @param resourceId
     * @return
     * @throws Exception
     */
    public JobView getOpenJob(Integer resourceId) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_VIEW_BASE_SQL.sql") + GET_JOB_WITH_ONLY_SPECIFIED_RESOURCE_ALLOCATED_WHERE;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("Get jobs with only specified active resource  " + sql);
        try {
            Query query = entityManager.createNativeQuery(sql, JobView.class);
            query.setParameter("resourceId", resourceId);
            return (JobView) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    //GET_JOB_WITH_ONLY_SPECIFIED_RESOURCE_ALLOCATED_WHERE
    /**
     * Fetch a Job that is in awaiting open state for the given resource and has only that resource assigned to it
     * @param resourceId
     * @return
     * @throws Exception
     */
    public JobView getAwaitingQuoteRequestApprovalJob(Integer resourceId) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_VIEW_BASE_SQL.sql") + GET_JOB_AWAITING_QUOTE_REQUEST_APPROVAL_WHERE;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug(sql);
        try {
            Query query = entityManager.createNativeQuery(sql, JobView.class);
            query.setParameter("resourceId", resourceId);
            return (JobView) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch a Job that has been completed in last 14 days and was assigned to a contractor
     * @param
     * @return
     * @throws Exception
     */
    public JobView getCompletedContractorP1JobWithinLastFortnight() throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_VIEW_BASE_SQL.sql") + GET_COMPLETED_P1_WITHIN_LAST_FORTNIGHT;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = sql.replace("DISTINCT", "TOP(1)");

        logger.debug(sql);
        try {
            Query query = entityManager.createNativeQuery(sql, JobView.class);
            return (JobView) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
