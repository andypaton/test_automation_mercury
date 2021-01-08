package mercury.database.dao;

import static mercury.helpers.Constants.AUTOMATION_USER;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.Job;
import mercury.database.models.JobView;
import mercury.helpers.DbUtilityHelper;


@Repository
public class JobDao {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private DbUtilityHelper dbUtilityHelper;

    private static final String GET_JOB_BASE_SQL = "SELECT DISTINCT j.* FROM Job j "
            + "INNER JOIN JobStatus js ON j.JobStatusId = js.Id "
            + "INNER JOIN JobType jt ON j.JobTypeId = jt.Id "
            + "LEFT JOIN JobContact jc ON jc.jobId = j.Id ";

    private static final String GET_JOB_AND_INCIDENT_CRITERIA_SQL = "SELECT j.* FROM Job j "
            + "INNER JOIN JobStatus js ON j.JobStatusId = js.Id "
            + "INNER JOIN JobType jt ON j.JobTypeId = jt.Id "
            + "INNER JOIN Site s ON j.SiteId = s.Id "
            + "LEFT JOIN JobContact jc ON jc.jobId = j.Id "
            + "LEFT JOIN ResourceAssignment ra ON j.Id = ra.JobId "
            + "LEFT JOIN AssetClassification ac ON j.AssetClassificationId = ac.Id ";

    private static String NO_INCIDENT_CRITERIA = " ac.AssetSubTypeId NOT IN (SELECT AssetSubTypeId FROM LinkedIncidentCriterion) ";
    private static String UNALLOCATED = " ra.Id IS NULL ";
    private static String NOT_LOCKED = " j.id NOT IN (SELECT jobId FROM jobLock) ";

    private static final String RANDOM_SQL = "SELECT TOP(1) * FROM (%s) randomOrder ORDER BY NEWID()" ;

    private static final String GET_JOB_TYPES_SQL = "SELECT j.* FROM Job j "
            + "INNER JOIN JobStatus js ON j.JobStatusId = js.Id "
            + "INNER JOIN JobType jt ON j.JobTypeId = jt.Id "
            + "LEFT JOIN ResourceAssignment ra ON j.Id = ra.JobId "
            + "LEFT JOIN ResourceAssignmentStatus ras ON ras.Id = ra.resourceAssignmentStatusId "
            + "LEFT JOIN Resource r ON ra.ResourceId = r.Id "
            + "LEFT JOIN ResourceProfile rp ON rp.Id = r.resourceProfileId "
            + "LEFT JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId "
            + "JOIN AssetClassification a ON j.AssetClassificationId = a.Id "
            + "LEFT JOIN LinkedIncidentCriterion lic ON lic.AssetSubtypeId = a.AssetSubTypeId "
            + "LEFT JOIN AdditionalResourceRequirement arr ON arr.JobId = j.Id "
            + "WHERE j.id NOT IN (SELECT jobId FROM jobLock) AND j.JobStatusId != 0 AND j.JobReference IS NOT NULL AND lic.AssetSubTypeId IS NULL AND arr.JobId IS NULL AND ras.Id != 13 ";

    private static final String GET_JOB_TYPES_FOR_DUPLICATES_SQL = "SELECT j.* FROM Job j "
            + "JOIN (SELECT id, AssetClassificationId, FaultTypeId, LocationId, COUNT(Id) OVER (PARTITION BY AssetClassificationId, FaultTypeId, LocationId) AS cnt FROM Job j WHERE AssetClassificationId IS NOT NULL) Duplicates ON Duplicates.Id = j.Id "
            + "INNER JOIN JobStatus js ON j.JobStatusId = js.Id "
            + "INNER JOIN JobType jt ON j.JobTypeId = jt.Id "
            + "LEFT JOIN resourceAssignment ra ON j.Id = ra.JobId "
            + "LEFT JOIN ResourceAssignmentStatus ras ON ras.Id = ra.resourceAssignmentStatusId "
            + "LEFT JOIN Resource r ON ra.ResourceId = r.Id "
            + "LEFT JOIN ResourceProfile rp ON rp.Id = r.resourceProfileId "
            + "LEFT JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId "
            + "WHERE Duplicates.cnt > 1 AND j.id NOT IN (SELECT jobId FROM jobLock) AND j.JobStatusId != 0";

    private static final String GET_JOB_FOR_SITES_SQL_WITH_SINGLE_RESOURCE_ASSIGNED = " (SELECT j.*, COUNT(ra.ResourceId) OVER (PARTITION BY JobId) AS _count FROM job j "
            + "INNER JOIN JobStatus js ON j.JobStatusId = js.Id "
            + "INNER JOIN JobType jt ON j.JobTypeId = jt.Id "
            + "LEFT JOIN resourceAssignment ra ON j.Id = ra.JobId "
            + "LEFT JOIN ResourceAssignmentStatus ras ON ras.Id = ra.resourceAssignmentStatusId "
            + "LEFT JOIN Resource r ON ra.ResourceId = r.Id "
            + "LEFT JOIN ResourceProfile rp ON rp.Id = r.resourceProfileId "
            + "LEFT JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId "
            + "LEFT JOIN Site s ON s.Id = j.SiteId "
            + "LEFT JOIN AllInclusiveContractorSiteMapping ascm ON ascm.SiteId = j.SiteId "
            + "WHERE j.id NOT IN (SELECT jobId FROM jobLock) "
            + "AND ascm.Id IS NULL "
            + "AND jt.Name = 'Reactive' "
            + "AND s.id NOT IN (SELECT sr.SiteId FROM SiteResource sr, Resource r WHERE sr.ResourceId=r.Id AND r.ResourceProfileId IN (3, 31)) "
            + "AND s.active=1 AND s.SiteStatusId IN (1,2) "
            + "AND NOT ras.Name='Declined'";

    private static final String GET_HISTORIC_JOBS_SQL = "SELECT j.*, ra.* "
            + "FROM Job j LEFT JOIN ResourceAssignment ra ON j.id = ra.jobId "
            + "WHERE j.JobStatusId NOT IN (0, 8, 9, 10) AND j.JobTypeId IN (1, 2, 4, 5, 7) AND j.CreatedOn < DATEADD(day, -:daysOld , GETDATE()) "
            + "AND (ra.ResourceAssignmentStatusId IN (1, 2, 5, 6, 7) OR ra.Id IS NULL) "
            + "ORDER BY j.createdOn DESC";

    private static final String CANCELLED_JOBS = "SELECT j.* FROM Job j "
            + "INNER JOIN JobCancellation jc ON jc.Id = j.JobCancellationId "
            + "INNER JOIN JobStatus js ON jc.JobStatusIdAtCancellation = js.id "
            + "INNER JOIN JobType jt ON j.JobTypeId = jt.Id "
            + "LEFT JOIN resourceAssignment ra ON j.Id = ra.JobId "
            + "LEFT JOIN ResourceAssignmentEvent rae ON rae.ResourceAssignmentId = ra.Id "
            + "LEFT JOIN ResourceAssignmentStatus ras ON ras.Id = ra.resourceAssignmentStatusId "
            + "LEFT JOIN Resource r ON ra.ResourceId = r.Id "
            + "LEFT JOIN ResourceProfile rp ON rp.Id = r.resourceProfileId "
            + "LEFT JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId "
            + "WHERE jc.JobStatusIdAtCancellation NOT IN (5, 10) "
            + "AND j.JobStatusId = 10 AND j.id NOT IN (SELECT jobId FROM jobLock) AND jc.RequestedBy != '%s' "
            + "AND jt.Name NOT IN ('Landlord', 'Potential Landlord') "
            + "AND j.JobReference IS NOT NULL ";

    public static final String USES_GAS_JOBS = "SELECT TOP(1) j.* FROM Job j "
            + "JOIN AssetClassification ac ON ac.Id = j.AssetClassificationId "
            + "JOIN AssetSubType ast ON ast.Id = ac.AssetSubTypeId "
            + "JOIN AssetType at ON at.Id = ast.AssetTypeId "
            + "JOIN Asset a ON j.AssetId = a.Id "
            + "JOIN ResourceAssignment ra ON ra.JobId = j.Id "
            + "JOIN ResourceAssignmentEvent rae ON rae.ResourceAssignmentId = ra.Id "
            + "JOIN ResourceAssignmentEventType raet ON raet.Id = rae.ResourceAssignmentEventTypeId "
            + "JOIN SiteVisits sv ON sv.ResourceAssignmentId = ra.Id "
            + "WHERE at.UsesGas = 1 "
            + "AND raet.Name = '%s' "
            + "AND GasUsage IS NOT null AND GasUsage != '0' "
            + "AND ast.FGasShowGasType = 1 "
            + "AND a.RefrigerantCharge IS NOT null "
            + "AND j.id NOT IN (SELECT jobId FROM jobLock) "
            + "AND sv.GasTypeId IS NOT NULL "
            + "ORDER BY NEWID()";

    private static String AND = " AND ";

    private static String JOB_STATUS_NAME = " js.Name = '%s' ";
    private static String JOB_TYPE_NAME = " jt.Name = '%s' ";
    private static String RESOURCE_ASSIGNMENT_STATUS_NAME = " ras.Name = '%s' ";
    private static String RESOURCE_NOT_ASSIGNED = " ra.Id IS NULL ";
    private static String RESOURCE_TYPE_NAME = " rt.Name = '%s' ";
    private static final String ROTA = " (SELECT DISTINCT oss.SiteId FROM RotaEntry re INNER JOIN OrganisationStructureSite oss ON oss.id=re.OrganisationStructureSiteId LEFT JOIN RotaEntryType ret ON ret.Id=re.RotaEntryTypeId LEFT JOIN ResourceProfile rp ON rp.Id=re.ResourceProfileId WHERE re.StartAt < GETDATE() AND GETDATE() < coalesce(re.EndAt, '2999-12-01') AND ret.name IN (:rotaEntryTypes) AND rp.name IN ('RHVAC Technician', 'RHVAC Supervisor')) ";
    private static final String NO_ROTA_ENTRY = " s.Id NOT IN " + ROTA;
    private static final String PERMANENT_OR_ON_CALL = "'Permanent', 'On Call Scheduler'";
    private static final String JOB_TYPE_CHANGE = "(SELECT j.Id FROM Job j JOIN JobTimelineEvent jte ON jte.JobId = j.Id JOIN JobEventType jet ON jte.JobEventTypeId = jet.Id WHERE jet.Name = 'JobTypeChanged')";
    private static final String JOB_TYPE_CHANGED = " j.Id IN " + JOB_TYPE_CHANGE;
    private static final String JOB_TYPE_NOT_CHANGED = " j.Id NOT IN " + JOB_TYPE_CHANGE;

    @Autowired
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<JobView> getAll() {
        return entityManager.createQuery("from mercury.database.models.Job").getResultList();
    }

    public Job get(Integer id) {
        return entityManager.find(Job.class, id);
    }

    /**
     * Fetch Job entity by jobReference
     * @param id
     * @return
     */
    public Job getByJobReference(String jobReference) {
        String jpql = GET_JOB_BASE_SQL + " WHERE j.JobReference = %s";

        String queryStatement = String.format(jpql, jobReference);
        logger.debug(queryStatement);
        Query query =  entityManager.createNativeQuery(queryStatement, Job.class);
        return (Job) query.getSingleResult();
    }

    public Job getByDescription(String description) {
        String jpql = GET_JOB_BASE_SQL + " WHERE j.Description = '%s' ORDER BY CreatedOn desc";

        String queryStatement = String.format(jpql, description.replace("'", "''"));
        logger.debug("getByDescription: " + queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, Job.class);
            return (Job) query.getResultList().get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Job getByDescriptionAndStatus(String description, String status) {
        String jpql = GET_JOB_BASE_SQL + " WHERE j.Description = '%s' AND js.Name = '%s' ORDER BY CreatedOn desc";

        String queryStatement = String.format(jpql, description.replace("'", "''"), status);
        logger.debug("getByDescription: " + queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, Job.class);
            return (Job) query.getResultList().get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Job getJob(String jobType, String description) {
        String jpql = GET_JOB_BASE_SQL + " WHERE j.Description = '%s' AND jt.Name='%s' ORDER BY CreatedOn desc";

        String queryStatement = String.format(jpql, description.replace("'", "''"), jobType);
        logger.debug("getJob: " + queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, Job.class);
            return (Job) query.getResultList().get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Job getJob(String jobType, String description, String status) {
        String jpql = GET_JOB_BASE_SQL + " WHERE j.Description = '%s' AND js.Name = '%s' AND jt.Name='%s' ORDER BY CreatedOn desc";

        String queryStatement = String.format(jpql, description.replace("'", "''"), status, jobType);
        logger.debug("getJob: " + queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, Job.class);
            return (Job) query.getResultList().get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Job getByJobReference(Integer jobReference) {
        return getByJobReference(String.valueOf(jobReference));
    }


    /**
     * Fetch List<Job>
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Job> getOpenJobs(Integer siteId) {
        String jpql = GET_JOB_BASE_SQL
                + " WHERE j.SiteId = %s "
                + " AND j.JobStatusId NOT IN (0, 8, 9, 10)";

        String queryStatement = String.format(jpql, siteId);
        logger.debug("getOpenJobs: " + queryStatement);

        Query query =  entityManager.createNativeQuery(queryStatement, Job.class);
        return query.getResultList();
    }

    public Job getRandomLoggedJob() {
        String sql = GET_JOB_AND_INCIDENT_CRITERIA_SQL + " WHERE j.JobStatusId = 1"
                + AND
                + NOT_LOCKED
                + AND
                + "jc.Active = 1 AND j.jobSourceId = 6 " // Only helpdesk jobs
                + AND
                + String.format(JOB_TYPE_NAME, "reactive")
                + AND
                + NO_INCIDENT_CRITERIA;
             //   + "AND j.CallerId IN (SELECT Id FROM ResourceCaller)";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(RANDOM_SQL, sql);
        logger.debug("getRandomLoggedJob: " + sql);
        Query query =  entityManager.createNativeQuery(sql, Job.class);
        return (Job) query.getSingleResult();
    }

    public Job getLoggedJobWithOutstandingPartsOrder() {
        String sql = GET_JOB_AND_INCIDENT_CRITERIA_SQL
                + " INNER JOIN %portaldb.uvw_PartsOrdersAwaitingApproval poaa ON j.JobReference = poaa.[Job Number]"
                + " WHERE j.JobStatusId = 1"
                + AND
                + NOT_LOCKED
                + AND
                + " jc.Active = 1"
                + AND
                + String.format(JOB_TYPE_NAME, "reactive")
                + AND
                + NO_INCIDENT_CRITERIA
                + " AND j.CallerId IN (SELECT Id FROM ResourceCaller)";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(RANDOM_SQL, sql);
        logger.debug("getRandomLoggedJob: " + sql);
        Query query =  entityManager.createNativeQuery(sql, Job.class);
        try {
            return (Job) query.getSingleResult();
        } catch(Exception e){
            return null;
        }
    }

    public Job getLoggedJobWithoutOutstandingPartsOrder() {
        String sql = GET_JOB_AND_INCIDENT_CRITERIA_SQL
                + "  WHERE j.JobStatusId = 1"
                + AND
                + NOT_LOCKED
                + AND
                + " jc.Active = 1"
                + AND
                + String.format(JOB_TYPE_NAME, "reactive")
                + AND
                + NO_INCIDENT_CRITERIA
                + " AND j.CallerId IN (SELECT Id FROM ResourceCaller)"
                + " AND j.JobReference NOT IN ( SELECT JobRef FROM %portaldb.uvw_PartsOrdersAwaitingApproval)";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(RANDOM_SQL, sql);
        logger.debug("getRandomLoggedJob: " + sql);
        Query query =  entityManager.createNativeQuery(sql, Job.class);
        try {
            return (Job) query.getSingleResult();
        } catch(Exception e){
            return null;
        }
    }

    public Job getRandomLoggedJobWithSingleSiteContact() {
        String sql = GET_JOB_AND_INCIDENT_CRITERIA_SQL + " WHERE j.JobStatusId = 1"
                + AND
                + NOT_LOCKED
                + AND
                + "jc.Active = 1"
                + AND
                + String.format(JOB_TYPE_NAME, "reactive")
                + AND
                + NO_INCIDENT_CRITERIA
                + "AND j.CallerId IS NOT NULL "
                + "AND j.Id NOT IN (SELECT JobId FROM JobContact GROUP BY JobId HAVING COUNT(*) > 1) ";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(RANDOM_SQL, sql);
        logger.debug("getRandomLoggedJob: " + sql);
        Query query = entityManager.createNativeQuery(sql, Job.class);
        return (Job) query.getSingleResult();
    }

    public Job getRandomLoggedJobWithAttachment() {
        String sql = GET_JOB_AND_INCIDENT_CRITERIA_SQL + " WHERE j.JobStatusId = 1"
                + AND
                + NOT_LOCKED
                + AND
                + "j.id IN (SELECT JobId FROM JobDocument)"
                + AND
                + "jc.Active = 1"
                + AND
                + String.format(JOB_TYPE_NAME, "reactive")
                + AND
                + NO_INCIDENT_CRITERIA
                + "AND j.CallerId IN (SELECT Id FROM ResourceCaller)";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(RANDOM_SQL, sql);
        logger.debug("getRandomLoggedJob: " + sql);
        Query query = entityManager.createNativeQuery(sql, Job.class);
        return (Job) query.getSingleResult();
    }

    public Job getRandomUnallocatedReactiveLoggedJob(int immediateCallout) {
        String sql = GET_JOB_AND_INCIDENT_CRITERIA_SQL
                + "LEFT JOIN LinkedIncidentCriterion lic ON ac.AssetSubTypeId = lic.AssetSubTypeId "
                + "LEFT JOIN jobLock jl ON j.Id = jl.JobId "
                + "INNER JOIN uvw_faultprioritymappings fpm ON j.AssetClassificationId = fpm.AssetClassificationId AND fpm.SiteTypeId = s.SiteTypeId AND j.ResponsePriorityId = fpm.ResponsePriorityId "
                + "WHERE j.JobStatusId = 1 "
                + "AND jl.JobId IS NULL "
                + "AND jc.Active = 1 "
                + AND + UNALLOCATED
                + AND + String.format(JOB_TYPE_NAME, "reactive")
                + AND + String.format("fpm.ImmediateCallout = %d ", immediateCallout)
                + "AND lic.AssetSubTypeId IS NULL "
                + "AND fpm.TechBureau = 0 "
                + "AND j.JobReference IS NOT NULL "
                + "AND j.AssetId IS NULL";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(RANDOM_SQL, sql);
        logger.debug("getRandomUnallocatedLoggedJob: " + sql);
        try {
            Query query = entityManager.createNativeQuery(sql, Job.class);
            return (Job) query.getSingleResult();
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    public Job getRandomLoggedJobWithClientCaller() {
        String sql = GET_JOB_AND_INCIDENT_CRITERIA_SQL + " WHERE j.JobStatusId = 1" + AND + NOT_LOCKED + AND + "jc.Active = 1" + AND + NO_INCIDENT_CRITERIA;
        sql = sql + " AND j.CallerId IN (SELECT Id FROM Caller WHERE CallerTypeId = 1)"
                + " AND j.CallerId IN (SELECT Id FROM ClientCaller)";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(RANDOM_SQL, sql);
        logger.debug("getRandomLoggedJobWithNonCityTechCaller: " + sql);
        Query query =  entityManager.createNativeQuery(sql, Job.class);
        return (Job) query.getSingleResult();
    }

    /**
     * Fetch List<Job>
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Job> getHistoricJobs(Integer daysOld) {
        String jpql = GET_HISTORIC_JOBS_SQL;

        String queryStatement = jpql;
        logger.debug("getHistoricJobs: " + queryStatement.replace(":daysOld", daysOld.toString()));
        Query query =  entityManager.createNativeQuery(queryStatement, Job.class);

        try {
            query.setParameter("daysOld", daysOld);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Job> getHistoricJobsAwaitingAssignment(Integer daysOld) {
        String jpql = "SELECT DISTINCT j.* FROM uvw_MonitorHelpdeskJobAwaitingAssignment ujas "
                + "INNER JOIN Job j ON ujas.JobReference = j.JobReference "
                + "WHERE j.CreatedOn < DATEADD(day, -:daysOld , GETDATE()) "
                + "ORDER BY j.createdOn DESC";

        String queryStatement = jpql;
        logger.debug("getHistoricJobsAwaitingAssignment: " + queryStatement.replace(":daysOld", daysOld.toString()));
        Query query =  entityManager.createNativeQuery(queryStatement, Job.class);

        try {
            query.setParameter("daysOld", daysOld);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Job> getHistoricJobsAwaitingAcceptance(Integer daysOld) {
        String jpql = "SELECT DISTINCT j.* FROM uvw_MonitorJobAwaitingAcceptance ujaa "
                + "INNER JOIN Job j ON ujaa.JobReference = j.JobReference "
                + "WHERE j.CreatedOn < DATEADD(day, -:daysOld , GETDATE()) "
                + "ORDER BY j.createdOn DESC";

        String queryStatement = jpql;
        logger.debug("getHistoricJobsAwaitingAssignment: " + queryStatement.replace(":daysOld", daysOld.toString()));
        Query query =  entityManager.createNativeQuery(queryStatement, Job.class);

        try {
            query.setParameter("daysOld", daysOld);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Job> getActiveJobs(Integer siteId) {
        String jpql = GET_JOB_BASE_SQL +
                " WHERE j.SiteId = %s " +
                " AND j.JobStatusId IN (3)";

        String queryStatement = String.format(jpql, siteId);
        logger.debug("getOpenJobs: " + queryStatement);

        Query query =  entityManager.createNativeQuery(queryStatement, Job.class);
        return query.getResultList();
    }

    public Job getRandomJob(String jobStatusName, String jobTypeName, String resourceAssignmentStatusName, String resourceTypeName) {

        String sql = GET_JOB_TYPES_SQL;

        sql = jobStatusName == null ? sql : sql + AND + String.format(JOB_STATUS_NAME, jobStatusName);
        sql = jobTypeName == null ? sql : sql + AND + String.format(JOB_TYPE_NAME, jobTypeName);
        sql = resourceTypeName == null ? sql : sql + AND + String.format(RESOURCE_TYPE_NAME, resourceTypeName);

        if (resourceAssignmentStatusName != null) {
            sql = resourceAssignmentStatusName.equals("NONE") ? sql + AND + RESOURCE_NOT_ASSIGNED : sql + AND + String.format(RESOURCE_ASSIGNMENT_STATUS_NAME, resourceAssignmentStatusName);
        }

        sql = String.format(RANDOM_SQL, sql);

        logger.debug("getRandomJob: " + sql);
        Query query =  entityManager.createNativeQuery(sql, Job.class);
        try {
            return (Job) query.getSingleResult();
        } catch(Exception e){
            return null;
        }
    }

    public Job getRandomDuplicateJob(String jobStatusName, String jobTypeName, String resourceAssignmentStatusName, String resourceTypeName) {

        String sql = GET_JOB_TYPES_FOR_DUPLICATES_SQL;

        sql = jobStatusName == null ? sql : sql + AND + String.format(JOB_STATUS_NAME, jobStatusName);
        sql = jobTypeName == null ? sql : sql + AND + String.format(JOB_TYPE_NAME, jobTypeName);
        sql = resourceTypeName == null ? sql : sql + AND + String.format(RESOURCE_TYPE_NAME, resourceTypeName);

        if (resourceAssignmentStatusName != null) {
            sql = resourceAssignmentStatusName.equals("NONE") ? sql + AND + RESOURCE_NOT_ASSIGNED : sql + AND + String.format(RESOURCE_ASSIGNMENT_STATUS_NAME, resourceAssignmentStatusName);
        }

        sql = String.format(RANDOM_SQL, sql);

        logger.debug("getRandomDuplicateJob: " + sql);
        Query query =  entityManager.createNativeQuery(sql, Job.class);
        try {
            return (Job) query.getSingleResult();
        } catch(Exception e){
            return null;
        }
    }


    public Job getRandomJob() {

        String sql = String.format(RANDOM_SQL, GET_JOB_TYPES_SQL);

        logger.debug("getRandomJob: " + sql);
        Query query =  entityManager.createNativeQuery(sql, Job.class);
        try {
            return (Job) query.getSingleResult();
        } catch(Exception e){
            return null;
        }
    }

    public Job getRandomCancelledJob() {
        return getRandomCancelledJob(null);
    }

    public Job getRandomCancelledJob(String resourceTypeName) {
        String baseSql = String.format(CANCELLED_JOBS, AUTOMATION_USER);
        String sql = resourceTypeName == null ? baseSql : baseSql + AND + String.format(RESOURCE_TYPE_NAME, resourceTypeName);
        //		sql = sql + " AND JobStatusIdAtCancellation=12"; // Tech Bureau Triage - see MCP-2307
        sql = String.format(RANDOM_SQL, sql);

        logger.debug("getRandomCancelledJob: " + sql);
        Query query =  entityManager.createNativeQuery(sql, Job.class);
        try {
            return (Job) query.getSingleResult();
        } catch(Exception e){
            return null;
        }
    }

    public Job getRandomCancelledJobOfType(String jobType) {
        String baseSql = String.format(CANCELLED_JOBS, AUTOMATION_USER);
        String sql = baseSql + AND + String.format("jt.Name='%s'", jobType);
        sql = String.format(RANDOM_SQL, sql);

        logger.debug("getRandomCancelledJobOfType: " + sql);
        Query query = entityManager.createNativeQuery(sql, Job.class);
        try {
            return (Job) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Job getRandomJobUsesGas(String resourceAssignmentEventType) {
        String sql = String.format(USES_GAS_JOBS, resourceAssignmentEventType);
        logger.debug("getRandomJobUsesGas: " + sql);
        Query query = entityManager.createNativeQuery(sql, Job.class);
        try {
            return (Job) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Job getRandomFixedJob() {
        String sql = "SELECT j.* FROM Job j WHERE j.JobStatusId = 9 AND j.id NOT IN (SELECT jobId FROM jobLock)";
        sql = String.format(RANDOM_SQL, sql);

        logger.debug("getRandomCancelledJob: " + sql);
        Query query =  entityManager.createNativeQuery(sql, Job.class);
        try {
            return (Job) query.getSingleResult();
        } catch(Exception e){
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Job> getRandomContractorJobForVendorSiteWithSingleResourceAssigned() {

        String sql = "SELECT * FROM( SELECT TOP(1000) * FROM " + GET_JOB_FOR_SITES_SQL_WITH_SINGLE_RESOURCE_ASSIGNED;
        sql = sql + AND + String.format(JOB_STATUS_NAME, "Logged");
        sql = sql + AND + String.format(JOB_TYPE_NAME, "reactive");
        sql = sql + AND + String.format(RESOURCE_TYPE_NAME, "Contractor");
        sql = sql + AND + NO_ROTA_ENTRY.replace(":rotaEntryTypes", PERMANENT_OR_ON_CALL);
        sql = sql + " ) randomOrder ORDER BY NEWID()) AS Jobs where _count = 1 ";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql);

        logger.debug("getRandomContractorJobForVendorSiteWithSingleResourceAssigned: " + sql);
        Query query =  entityManager.createNativeQuery(sql, Job.class);
        List<Job> jobList =  query.getResultList();
        return jobList;
    }

    public Job getRandomCancelledJobWithSingleResource() {
        String baseSql = String.format(CANCELLED_JOBS, AUTOMATION_USER);
        String sql = baseSql + " AND rae.ResourceAssignmentEventTypeId = 15 "
                + " AND j.JobReference IN (  SELECT JobReference FROM ( "
                + " SELECT * FROM "
                + GET_JOB_FOR_SITES_SQL_WITH_SINGLE_RESOURCE_ASSIGNED
                + ") randomOrder where _count = 1 ) Jobs )";
        sql = String.format(RANDOM_SQL, sql);
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getRandomCancelledJobWithSingleResource: " + sql);
        Query query =  entityManager.createNativeQuery(sql, Job.class);
        try {
            return (Job) query.getSingleResult();
        } catch(Exception e){
            return null;
        }
    }

    public Job getRandomJob(String jobStatusName, String jobTypeName, String resourceAssignmentStatusName, String resourceTypeName, String jobTypeChanged) {

        String sql = GET_JOB_TYPES_SQL;

        sql = jobStatusName == null ? sql : sql + AND + String.format(JOB_STATUS_NAME, jobStatusName);
        sql = jobTypeName == null ? sql : sql + AND + String.format(JOB_TYPE_NAME, jobTypeName);
        sql = resourceTypeName == null ? sql : sql + AND + String.format(RESOURCE_TYPE_NAME, resourceTypeName);

        if (resourceAssignmentStatusName != null) {
            sql = resourceAssignmentStatusName.equals("NONE") ? sql + AND + RESOURCE_NOT_ASSIGNED : sql + AND + String.format(RESOURCE_ASSIGNMENT_STATUS_NAME, resourceAssignmentStatusName);
        }

        sql = jobTypeChanged.equalsIgnoreCase("IS") ? sql + AND + JOB_TYPE_CHANGED : sql + AND + JOB_TYPE_NOT_CHANGED;

        sql = String.format(RANDOM_SQL, sql);

        logger.debug("getRandomJob: " + sql);
        Query query = entityManager.createNativeQuery(sql, Job.class);
        try {
            return (Job) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}