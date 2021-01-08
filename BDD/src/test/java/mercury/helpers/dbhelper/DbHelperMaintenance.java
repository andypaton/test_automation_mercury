package mercury.helpers.dbhelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mercury.database.config.DbConfigV2;
import mercury.helpers.DbUtilityHelper;
import mercury.helpers.FileHelper;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfigV2.class)
public class DbHelperMaintenance {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;
    @Autowired private NamedParameterJdbcTemplate jdbc_portal;
    @Autowired private DbHelperTimeZone dbHelperTimeZone;
    @Autowired private DbUtilityHelper dbUtilityHelper;


    public void createViewVendorStores() throws Exception {
        try {
            String sql = FileHelper.getResourceFile("sql/create/view/", "uvw_VendorStores.sql");
            jdbc_helpdesk.update(sql, new MapSqlParameterSource());
        } catch (UncategorizedSQLException e ) {
            logger.debug("Unexpected error but ok to ignore: " + e.getMessage());
        }
    }

    public void createViewCityTechStores() throws Exception {
        try {
            String sql = FileHelper.getResourceFile("sql/create/view/", "uvw_CityTechStores.sql");
            jdbc_helpdesk.update(sql, new MapSqlParameterSource());
        } catch (UncategorizedSQLException e ) {
            logger.debug("Unexpected error but ok to ignore: " + e.getMessage());
        }
    }

    public void createViewResources() throws Exception {
        try {
            String sql = FileHelper.getResourceFile("sql/create/view/", "uvw_Resources.sql");
            sql.replace("+00:00", dbHelperTimeZone.getTimezone());
            jdbc_helpdesk.update(sql, new MapSqlParameterSource());
        } catch (UncategorizedSQLException e ) {
            logger.debug("Unexpected error but ok to ignore: " + e.getMessage());
        }
    }

    public void createViewRota() throws Exception {
        try {
            String sql = FileHelper.getResourceFile("sql/create/view/", "uvw_Rota.sql");
            sql.replace("+00:00", dbHelperTimeZone.getTimezone());
            jdbc_helpdesk.update(sql, new MapSqlParameterSource());
        } catch (UncategorizedSQLException e ) {
            logger.debug("Unexpected error but ok to ignore: " + e.getMessage());
        }
    }

    public void createViewJobsWithSingleActiveResource() throws Exception {
        try {
            String sql = FileHelper.getResourceFile("sql/create/view/", "uvw_JobsWithSingleActiveResource.sql");
            jdbc_helpdesk.update(sql, new MapSqlParameterSource());
        } catch (UncategorizedSQLException e ) {
            logger.debug("Unexpected error but ok to ignore: " + e.getMessage());
        }
    }

    public void createViewQuoteApprovalScenarioUsers() throws Exception {
        try {
            String sql = FileHelper.getResourceFile("sql/create/view/", "uvw_QuoteApprovalScenariosUsers.sql");

            sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

            jdbc_helpdesk.update(sql, new MapSqlParameterSource());
        } catch (UncategorizedSQLException e ) {
            logger.debug("Unexpected error but ok to ignore: " + e.getMessage());
        }
    }

    public void createFunction(String function) throws Exception {
        try {
            String sql = FileHelper.getResourceFile("sql/create/function/", function + ".sql");
            jdbc_helpdesk.update(sql, new MapSqlParameterSource());
        } catch (UncategorizedSQLException e ) {
            logger.debug("Unexpected error but ok to ignore: " + e.getMessage());
        }
    }

    public void createProcedure(String procedure) throws Exception {
        try {
            String sql = FileHelper.getResourceFile("sql/create/procedure/", procedure + ".sql");

            sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

            jdbc_helpdesk.update(sql, new MapSqlParameterSource());
        } catch (UncategorizedSQLException e ) {
            logger.debug("Unexpected error but ok to ignore: " + e.getMessage());
        }
    }

    public List<String> getFailedJobs(String from, String to) throws Exception {
        String sql = "SELECT Description FROM %testdb.TestAutomation_Audit WHERE Title = 'log job' AND Status = 'FAIL' AND Created >= '%s' AND Created <= '%s'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, from, to);
        logger.debug("getFailedJobs: " + sql);

        List<String> failedJobs = jdbc_helpdesk.query(sql, new MapSqlParameterSource(), new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("Description"));
            }
        });
        return failedJobs;
    }

    public List<String> getFailedApiRequests(String from, String to) throws Exception {
        String sql = "SELECT Description FROM %testdb.TestAutomation_Audit WHERE Title LIKE 'https%' AND Status != '200' ";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = sql + String.format("AND Created >= '%s' AND Created <= '%s'", from, to);
        logger.debug("getFailedApiRequests: " + sql);

        List<String> failedJobs = jdbc_helpdesk.query(sql, new MapSqlParameterSource(), new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("Description"));
            }
        });
        return failedJobs;
    }

    public List<Map<String, Object>> getJobsForTermination(int keepCount, String jobStatus, String resourceStatus){
        String sql = "WITH JobsForDestruction AS ( "
                + "SELECT j.Id, j.JobReference, js.Name AS JobStatus, ras.Name AS ResourceAssignmentStatus, ra.ResourceId, ra.id resourceAssignmentId, j.CreatedOn, ROW_NUMBER() OVER (ORDER BY j.Id DESC) AS RowNum "
                + "FROM job j "
                + "INNER JOIN JobStatus js ON j.JobStatusId = js.Id "
                + "INNER JOIN JobType jt ON j.JobTypeId = jt.Id "
                + "LEFT JOIN ResourceAssignment ra ON j.Id = ra.JobId AND ra.Active = 1 "
                + "LEFT JOIN ResourceAssignmentStatus ras ON ras.Id = ra.ResourceAssignmentStatusId "
                + "WHERE js.IsTerminal = 0 "
                + "AND js.Name = '%s' "
                + "AND ras.Name = '%s' "
                + ") "
                + "SELECT * FROM JobsForDestruction WHERE RowNum > %d";

        if (resourceStatus == null || resourceStatus.isEmpty()) {
            sql = sql.replace("AND ras.Name = '%s'", "AND ras.Name IS NULL");
            sql = String.format(sql, jobStatus, keepCount);

        } else {
            sql = String.format(sql, jobStatus, resourceStatus, keepCount);
        }

        logger.debug("getJobsForCompletion: " + sql);
        return jdbc_helpdesk.queryForList(sql,new MapSqlParameterSource());
    }

    /**
     *
     * @param keepCount
     * @param jobStatus
     * @param resourceStatus
     * @param keepResourceIds - comma separated list of resource Ids where assigned jobs should not be cancelled
     * @return
     */
    public List<Map<String, Object>> getJobsForTermination(int keepCount, String jobStatus, String resourceAssignmentStatus, String keepResourceIds){
        String sql = "WITH JobsForDestruction AS (  "
                + "    SELECT j.Id, j.JobReference, js.Name AS JobStatus, ras.Name AS ResourceAssignmentStatus, ra.ResourceId, ra.id resourceAssignmentId, j.CreatedOn "
                + "    FROM job j  "
                + "    INNER JOIN JobStatus js ON j.JobStatusId = js.Id "
                + "    INNER JOIN JobType jt ON j.JobTypeId = jt.Id  "
                + "    LEFT JOIN ResourceAssignment ra ON j.Id = ra.JobId AND ra.Active = 1  "
                + "    LEFT JOIN ResourceAssignmentStatus ras ON ras.Id = ra.ResourceAssignmentStatusId  "
                + "    WHERE js.IsTerminal = 0 AND js.Name = ':jobStatus' AND ras.Name = ':resourceAssignmentStatus' "
                + "),  "
                + "JobsToKeep AS (  "
                + "    SELECT j.Id, j.JobReference, js.Name AS JobStatus, ras.Name AS ResourceAssignmentStatus, ra.ResourceId, ra.id resourceAssignmentId, j.CreatedOn "
                + "    FROM job j  "
                + "    INNER JOIN JobStatus js ON j.JobStatusId = js.Id "
                + "    INNER JOIN JobType jt ON j.JobTypeId = jt.Id  "
                + "    LEFT JOIN ResourceAssignment ra ON j.Id = ra.JobId AND ra.Active = 1  "
                + "    LEFT JOIN ResourceAssignmentStatus ras ON ras.Id = ra.ResourceAssignmentStatusId  "
                + "    WHERE js.IsTerminal = 0 AND js.Name = ':jobStatus' AND ras.Name = ':resourceAssignmentStatus'  "
                + "    AND ra.ResourceId IN (:keepResourceIds) "
                + "), "
                + "FinalResult AS ( "
                + "SELECT jfd.Id, jfd.JobReference, jfd.JobStatus, jfd.ResourceAssignmentStatus, jfd.ResourceId, jfd.resourceAssignmentId, jfd.CreatedOn, ROW_NUMBER() OVER (ORDER BY COALESCE(jtk.Id, '1') DESC) AS RowNum, jtk.Id KeepJobId "
                + "FROM JobsForDestruction jfd "
                + "LEFT JOIN JobsToKeep jtk ON jtk.Id = jfd.Id "
                + ") "
                + "SELECT * FROM FinalResult WHERE KeepJobId IS NULL AND RowNum > :keepCount ";

        if (resourceAssignmentStatus == null || resourceAssignmentStatus.isEmpty()) {
            sql = sql.replace("AND ras.Name = ':resourceAssignmentStatus'", "AND ras.Name IS NULL");
        } else {
            sql = sql.replaceAll(":resourceAssignmentStatus", resourceAssignmentStatus);
        }

        if (keepResourceIds == null || keepResourceIds.isEmpty()) {
            sql = sql.replace("AND ra.ResourceId IN (:keepResourceIds)", "AND ra.ResourceId IS NULL"); // ie. there are no jobs with an assigned resource that we have to keep
        } else {
            sql = sql.replaceAll(":keepResourceIds", keepResourceIds);
        }

        sql = sql.replaceAll(":jobStatus", jobStatus);
        sql = sql.replaceAll(":keepCount", String.valueOf(keepCount));

        logger.debug("getJobsForCompletion: " + sql);
        return jdbc_helpdesk.queryForList(sql,new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getChaseJobsForCancellation(int keepCount, String chaseType) throws Exception{
        String sql = FileHelper.getResourceFile("sql/select/GET_CHASE_JOBS_FOR_CANCELLATION.sql");
        sql = String.format(sql, chaseType, keepCount);
        logger.debug("getChaseJobsForCancellation: " + sql);
        return jdbc_helpdesk.queryForList(sql,new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getQuoteJobsForCancellation(int keepCount, String approvalStatus) throws Exception{
        String sql = FileHelper.getResourceFile("sql/select/GET_QUOTE_JOBS_FOR_CANCELLATION.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        if (approvalStatus == null || approvalStatus.isEmpty() || approvalStatus.equals("NULL")) {
            sql = sql.replace("AND aps.fld_str_Name = '%s'", "AND aps.fld_str_Name IS NULL");
            sql = String.format(sql, keepCount);

        } else {
            sql = String.format(sql, approvalStatus, keepCount);
        }

        logger.debug("getQuoteJobsForCancellation: " + sql);
        return jdbc_portal.queryForList(sql,new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getQuoteJobTotals() throws Exception{
        String sql = "SELECT aps.fld_str_Name AS ApprovalStatus, COUNT(hdft.fld_int_HelpDeskFaultID) AS QuoteJobs "
                + "FROM %portaldb.tblHelpDeskFault hdf "
                + "INNER JOIN %portaldb.tblHelpDeskFaultTime hdft ON hdf.fld_int_ID = hdft.fld_int_HelpDeskFaultID "
                + "INNER JOIN %portaldb.tblHelpDeskResource hdr ON hdft.fld_int_HelpDeskResourceID = hdr.fld_int_ID "
                + "INNER JOIN %portaldb.tblProjectHeader ph ON hdft.fld_int_ID = ph.fld_int_InvitationToQuoteID "
                + "LEFT JOIN %portaldb.tblHelpDeskResourceType hdrt ON hdr.fld_int_HelpDeskResourceTypeID = hdrt.fld_int_ID "
                + "LEFT JOIN %portaldb.tblQuoteApprovalScenario qas ON ph.fld_int_ID = qas.fld_int_ProjectHeaderID "
                + "LEFT JOIN %portaldb.tblApprovalStatus aps ON qas.fld_int_ApprovalStatusID = aps.fld_int_ID "
                + "WHERE hdf.fld_bit_QuoteJob = 1 "
                + "AND hdf.fld_int_HelpDeskFaultStageID != 6 "
                + "AND fld_int_HelpDeskTimeStampTypeID = 27 "
                + "AND hdft.fld_bit_IsCancelled = 0 "
                + "AND hdft.fld_bit_declined != 1 "
                + "GROUP BY aps.fld_str_Name "
                + "ORDER BY aps.fld_str_Name ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getQuoteJobTotals: " + sql);
        return jdbc_portal.queryForList(sql,new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getChaseJobTotals() throws Exception{
        String sql = "SELECT ct.Name, COUNT(jc.Id) AS ChaseJobs "
                + "FROM JobChase jc "
                + "INNER JOIN ChaseType ct ON jc.ChaseTypeId = ct.Id "
                + "WHERE Active = 1 "
                + "AND JobChaseStatusId = 0 "
                + "GROUP BY ct.Name "
                + "ORDER BY ct.Name";

        logger.debug("getChaseJobTotals: " + sql);
        return jdbc_helpdesk.queryForList(sql,new MapSqlParameterSource());
    }

    public void updateHelpDeskFaultTimeToCancelled(int helpDeskFaultTimeId) {
        String sql = "UPDATE %portaldb.tblHelpDeskFaultTime SET fld_bit_IsCancelled = 1 WHERE fld_int_ID = " + helpDeskFaultTimeId;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("updateHelpDeskFaultTimeToCancelled: " + sql);
        jdbc_portal.update(sql, new MapSqlParameterSource());
    }

    public void cancelPortalJob(int jobReference) {
        String sql = "UPDATE %portaldb.tblHelpDeskFault SET fld_int_HelpDeskFaultStageID = 6 WHERE fld_int_ID = " + jobReference;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("cancelPortalJob: " + sql);
        jdbc_portal.update(sql, new MapSqlParameterSource());
    }

    public void updateJobChaseToCancelled(int chaseId) {
        String sql = "UPDATE JobChase SET JobChaseStatusId = 2 WHERE Id = " + chaseId;
        logger.debug("updateJobChaseToCancelled: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getJobTotals() throws Exception{
        String sql =  "SELECT js.Name AS JobStatus, ras.Name AS ResourceAssignmentStatus, COUNT(j.JobReference) AS Total "
                + "FROM job j "
                + "INNER JOIN JobStatus js ON j.JobStatusId = js.Id "
                + "INNER JOIN JobType jt ON j.JobTypeId = jt.Id "
                + "LEFT JOIN ResourceAssignment ra ON j.Id = ra.JobId AND ra.Active = 1 "
                + "LEFT JOIN ResourceAssignmentStatus ras ON ras.Id = ra.ResourceAssignmentStatusId "
                + "WHERE js.IsTerminal = 0 "
                + "GROUP BY js.Name, ras.Name "
                + "ORDER BY js.Name, ras.Name";

        logger.debug("getJobTotals: " + sql);
        return jdbc_helpdesk.queryForList(sql,new MapSqlParameterSource());
    }

    public void removeOnSiteResourcesForTerminatedJobs() {
        String sql = "UPDATE ResourceAssignment "
                + "SET ResourceAssignmentStatusId = 15, ReasonId = 28, Active = 0 "
                + "WHERE Id IN ( "
                + "    SELECT ra.Id  "
                + "    FROM ResourceAssignment ra "
                + "    INNER JOIN Job j on j.id = ra.JobId "
                + "    INNER JOIN JobStatus js on js.id = j.JobStatusId "
                + "    WHERE ra.ResourceAssignmentStatusId = 9 "    // On Site
                + "    AND js.IsTerminal = 1 "
                + ")";
        logger.debug("removeOnSiteResourcesForTerminatedJobs: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }
}
