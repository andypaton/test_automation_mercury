package mercury.helpers.dbhelper;

import static mercury.helpers.Globalisation.LOCALE;
import static mercury.helpers.Globalisation.MS_MEDIUM;
import static mercury.helpers.StringHelper.quote;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.object.GenericStoredProcedure;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cucumber.api.PendingException;
import mercury.database.config.DbConfigV2;
import mercury.databuilders.TestData;
import mercury.helpers.DbUtilityHelper;
import mercury.helpers.FileHelper;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfigV2.class)
public class DbHelperJobs {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private DbUtilityHelper dbUtilityHelper;
    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;
    @Autowired private DataSource helpdeskDataSource;
    @Autowired private TestData testData;


    public boolean isJobAwaitingAssignment(String jobReference) {
        String sql = "SELECT COUNT(0) FROM uvw_MonitorJobAwaitingAssignment WHERE jobReference = :jobReference";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);

        logger.debug("isJobAwaitingAssignment: " + sql.replace(":jobReference", jobReference));

        int count = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        return count > 0;
    }

    public boolean isJobAwaitingAcceptance(String jobReference) {
        String sql = "SELECT COUNT(0) FROM uvw_MonitorJobAwaitingAcceptance WHERE jobReference = :jobReference";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);

        logger.debug("isJobAwaitingAcceptance: " + sql.replace(":jobReference", jobReference));

        int count = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        return count > 0;
    }

    public void updateJobDeferralDateToYesterday(int jobId) throws Exception {
        String sql = String.format("UPDATE Job SET DeferralDate = GetUTCDate()-1 WHERE id=%d ", jobId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("updateJobDeferralDate: " + sql);

        int rows = jdbc_helpdesk.update(sql, params);

        if (rows == 0) {
            throw new Exception("row not updated");
        }
    }


    /**
     * display name can map to multiple viewNames (eg. for landlord views)
     * @param displayName
     * @return list of views for displayName
     */
    public List<String> getMonitorViewNames(String displayName) {
        String sql = "SELECT ViewName FROM MonitorConfig WHERE DisplayName = '%s'";
        sql = String.format(sql, displayName);
        logger.debug("getMonitorViewName: " + sql);
        return (jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), String.class));
    }

    public String getJobMonitor(int jobReference) {
        String sql = "SELECT ViewName, DisplayName FROM MonitorConfig WHERE ViewName IN (SELECT Name FROM sys.views) AND EntityType = 'Job' AND DisplayName > ''";
        List<Map<String, Object>> views = jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());

        sql = "";
        for (Map<String, Object> view : views) {
            String viewName = (String) view.get("ViewName");
            String displayName = (String) view.get("DisplayName");
            String query = "";
            if (viewName.equals("uvw_MonitorLinkedIncident")) {
                query = "SELECT '" + displayName + "'" + " FROM " + viewName + " WITH (NOLOCK) WHERE JobNo = " + jobReference;

            } else if (viewName.equals("uvw_MonitorAwaitingQuoteApprovalToDo") || viewName.equals("uvw_MonitorAwaitingQuoteApprovalForInfo")) {
                query = "SELECT '" + displayName + "'" + " FROM " + viewName + " WITH (NOLOCK) WHERE JobId = (SELECT Id FROM Job WHERE JobReference = " + jobReference + ")";

            } else {
                query = "SELECT '" + displayName + "'" + " FROM " + viewName + " WITH (NOLOCK) WHERE JobReference = " + jobReference;
            }
            sql = sql.isEmpty() ? query : sql + " UNION " + query;
        }

        logger.debug("getJobMonitor: " + sql);
        try {
            String result = jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), String.class).toString();
            logger.debug("Found in monitors: " + result);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean isJobOnMonitor(String monitor, int jobRef) {
        String sql = null;

        if (monitor.equals("uvw_MonitorLinkedIncident")) {
            sql = "SELECT JobNo FROM :monitor WITH (NOLOCK) WHERE JobNo = :jobRef";

        } else if (monitor.equals("uvw_MonitorAwaitingQuoteApprovalToDo") || monitor.equals("uvw_MonitorAwaitingQuoteApprovalForInfo")) {
            sql = "SELECT JobId FROM :monitor WITH (NOLOCK) WHERE JobId = :jobRef";

        } else {
            sql = "SELECT JobReference FROM :monitor WITH (NOLOCK) WHERE JobReference = :jobRef";
        }

        sql = sql.replace(":monitor", monitor).replace(":jobRef", String.valueOf(jobRef));

        logger.debug("isJobOnMonitor: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            return jdbc_helpdesk.queryForObject(sql, params, Boolean.class);
        } catch (Exception e) {
            return false;
        }
    }


    public boolean areJobsLinked(int jobRef1, int jobRef2) {
        String sql = "SELECT COUNT(*) FROM jobLinkedJob jlj "
                + " INNER JOIN Job j1 ON j1.Id = jlj.JobId "
                + " INNER JOIN Job j2 ON j2.Id = jlj.LinkedJobId "
                + " WHERE (j1.jobReference = :jobRef1 AND j2.jobReference = :jobRef2) "
                + " OR (j2.jobReference = :jobRef1 AND j1.jobReference = :jobRef2)";

        sql = sql.replace(":jobRef1", String.valueOf(jobRef1)).replace(":jobRef2", String.valueOf(jobRef2));

        logger.debug("areJobsLinked: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        int count = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        return count > 0;
    }

    public Integer getRandomJobWithActiveChase(String chaseType, int chaseCreationRoute) {
        String sql = "SELECT TOP(1) monitor.JobReference "
                + "FROM dbo.uvw_MonitorJobChaseLogged monitor "
                + "INNER JOIN Site s ON monitor.Site = s.Name "
                + "LEFT JOIN JobChase js ON monitor.Id = js.JobId "
                + "LEFT JOIN ChaseType ct ON ct.Id = js.ChaseTypeId "
                + "LEFT JOIN Joblock jl ON monitor.Id = jl.JobId "
                + "WHERE ct.Name = :ChaseType "
                + "AND js.RaisedByHelpdesk = :ChaseCreationRoute "
                + "AND js.JobChaseStatusId = 0 "
                + "ORDER BY NEWID()";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ChaseType", chaseType);
        params.addValue("ChaseCreationRoute", chaseCreationRoute);

        logger.debug("getRandomJobWithActivechase: " + sql.replace(":ChaseType", quote(chaseType)).replace(":ChaseCreationRoute", String.valueOf(chaseCreationRoute)));
        try {
            int jobReference = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return jobReference;
        } catch (Exception e) {
            return 0;
        }
    }

    public Integer getRandomJobWithOnlyOneActiveChase(String chaseType, int chaseCreationRoute) {
        String sql = "SELECT TOP(1) monitor.JobReference "
                + "FROM dbo.uvw_MonitorJobChaseLogged monitor "
                + "INNER JOIN Site s ON monitor.Site = s.Name "
                + "INNER JOIN (SELECT JobId FROM JobChase GROUP BY JobId HAVING COUNT(JobId) = 1) jc ON monitor.Id = jc.JobId "
                + "LEFT JOIN JobChase js ON monitor.Id = js.JobId "
                + "LEFT JOIN ChaseType ct ON ct.Id = js.ChaseTypeId "
                + "LEFT JOIN Joblock jl ON monitor.Id = jl.JobId "
                + "WHERE ct.Name = :ChaseType "
                + "AND js.RaisedByHelpdesk = :ChaseCreationRoute "
                + "AND js.JobChaseStatusId = 0 "
                + "ORDER BY NEWID()";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ChaseType", chaseType);
        params.addValue("ChaseCreationRoute", chaseCreationRoute);

        logger.debug("getRandomJobWithActivechase: " + sql.replace(":ChaseType", quote(chaseType)).replace(":ChaseCreationRoute", String.valueOf(chaseCreationRoute)));
        try {
            int jobReference = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return jobReference;
        } catch (Exception e) {
            return 0;
        }
    }

    public Integer getRandomJobWithActiveChaseAndUpdate(String chaseType, int chaseCreationRoute) {
        String sql = "SELECT TOP(1) monitor.JobReference "
                + "FROM dbo.uvw_MonitorJobChaseLogged monitor "
                + "INNER JOIN Site s ON monitor.Site = s.Name "
                + "INNER JOIN JobChase js ON monitor.Id = js.JobId "
                + "INNER JOIN (SELECT JobId FROM JobChase GROUP BY JobId HAVING COUNT (JobId) = 1) jc ON jc.JobId = js.JobId "
                + "INNER JOIN JobChaseUpdate jcu ON js.Id = jcu.JobChaseId "
                + "INNER JOIN ChaseType ct ON ct.Id = js.ChaseTypeId "
                + "WHERE ct.Name = :ChaseType "
                + "AND js.RaisedByHelpdesk = :ChaseCreationRoute "
                + "AND js.JobChaseStatusId = 0 "    // Active
                + "ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ChaseType", chaseType);
        params.addValue("ChaseCreationRoute", chaseCreationRoute);

        logger.debug("getRandomJobWithActivechase: " + sql.replace(":ChaseType", quote(chaseType)).replace(":ChaseCreationRoute", String.valueOf(chaseCreationRoute)));
        try {
            int jobReference = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return jobReference;
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean confirmJobOnMonitor(Integer jobReference, String monitorIn) {
        String sql = "SELECT CAST(COUNT(1) AS BIT) FROM " + monitorIn + " monitor WHERE monitor.jobReference = :jobReference ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        logger.debug("checking for job chase ON the job monitor: " + sql);
        int counter = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        if (counter > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Integer getRandomJobAssignedToAResource(String jobType) {
        String sql = "SELECT TOP(1) mjaa.JobReference FROM uvw_MonitorJobAwaitingAcceptance mjaa"
                + " INNER JOIN job j ON j.jobreference = mjaa.JobReference"
                + " INNER JOIN JobStatus js ON j.JobStatusId = js.Id"
                + " INNER JOIN ResourceAssignment ra ON ra.JobId = j.Id"
                + " INNER JOIN uvw_JobResources jr ON j.id = jr.JobId"
                + " INNER JOIN Site s ON s.Id = mjaa.SiteId"
                + " LEFT JOIN LinkedIncidentCriterion lic ON lic.FaultTypeId = j.FaultTypeId"
                + " LEFT JOIN Joblock jl ON j.Id = jl.JobId"
                + " LEFT JOIN AdditionalResourceRequirement arr ON j.Id = arr.JobId"
                + " WHERE jr.ResourceNames NOT LIKE '%,%'"
                + " AND mjaa.JobType = :jobType"
                + " AND ra.ResourceAssignmentStatusId IN (4,5)"
                + " AND lic.FaultTypeId IS NULL"
                + " AND jl.JobId IS NULL"
                + " AND arr.JobId IS NULL"
                + " AND js.Name = 'Logged'"
                + " ORDER BY NEWID()";

        logger.debug("getRandomJobAutoAssignedToResource: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobType", jobType);
        try {
            int jobReference = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return jobReference;
        } catch (Exception e) {
            return 0;
        }
    }

    public Integer getRandomJobNotAssignedToAResourceNotOfTypeWithDesiredFaultPriority(String jobType, String faultPriority) {
        String sql = "SELECT TOP(1) mjaa.JobReference FROM uvw_MonitorJobAwaitingAssignment mjaa"
                + " INNER JOIN Job j ON j.jobreference = mjaa.JobReference"
                + " INNER JOIN JobStatus js ON j.JobStatusId = js.Id"
                + " INNER JOIN uvw_JobResources jr ON j.id = jr.JobId"
                + " INNER JOIN ResourceAssignment ra ON jr.JobId = ra.JobId"
                + " INNER JOIN ResourceAssignmentStatus ras ON ra.ResourceAssignmentStatusId = ras.Id"
                + " INNER JOIN Site s ON s.Id = mjaa.SiteId"
                + " LEFT JOIN LinkedIncidentCriterion lic ON lic.FaultTypeId = j.FaultTypeId"
                + " LEFT JOIN Joblock jl ON j.Id = jl.JobId"
                + " LEFT JOIN AdditionalResourceRequirement arr ON j.Id = arr.JobId"
                + " WHERE mjaa.FaultPriority = :faultPriority"
                + " AND mjaa.JobType != :jobType"
                + " AND jr.ResourceNames NOT LIKE '%,%'"
                + " AND js.Name = 'Logged'"
                + " AND ras.Name IN ('Declined','Removed')"
                + " AND lic.FaultTypeId IS NULL"
                + " AND jl.JobId IS NULL"
                + " AND js.Name != 'Parked'"
                + " AND arr.JobId IS NULL"
                + " ORDER BY NEWID()";

        logger.debug("getRandomJobNotAssignedToAResource: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("faultPriority", faultPriority);
        params.addValue("jobType", jobType);
        try {
            int jobReference = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return jobReference;
        } catch (Exception e) {
            return 0;
        }
    }

    public void unlockJob(int jobReference) {
        String sql = String.format("DELETE JobLock WHERE JobId IN (SELECT Id FROM Job WHERE JobReference = %d)", jobReference);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("unlockJob: " + sql);
        jdbc_helpdesk.update(sql, params);
    }

    public Integer getRandomJobWithETANotAdvised() {
        String sql = "SELECT TOP(1) "
                + "etaNotAdvised.JobReference FROM dbo.uvw_MonitorJobETANotAdvised etaNotAdvised "
                + "INNER JOIN Site s ON s.Id = etaNotAdvised.SiteId "
                + "LEFT JOIN Job j ON j.JobReference = etaNotAdvised.jobReference "
                + "LEFT JOIN ResourceCaller rc ON rc.id = j.CallerId "
                + "LEFT JOIN ResourcePhoneNumber rph ON rph.ResourceId = rc.ResourceId "
                + "LEFT JOIN Joblock jl ON j.Id = jl.JobId "
                + "WHERE rph.PhoneNumber > '' "
                + "AND jl.JobId IS NULL "
                + "ORDER BY NEWID()";

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("get job with ETA Not Advised: " + sql);
        try {
            int jobReference = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return jobReference;
        } catch (Exception e) {
            return 0;
        }
    }

    public Integer getJobWithETANotAdvisedAndContactsAssigned(String numberOfContacts) {
        String sql = "SELECT TOP(1) etaNotAdvised.JobReference "
                + " FROM dbo.uvw_MonitorJobETANotAdvised etaNotAdvised  "
                + " INNER JOIN Site s ON s.id = etaNotAdvised.SiteId  "
                + " INNER JOIN Job j ON j.JobReference = etaNotAdvised.jobReference  "
                + " INNER JOIN JobContact jc ON jc.JobId = j.id  "
                + " LEFT JOIN ResourceCaller rc ON rc.id = j.CallerId  "
                + " LEFT JOIN ClientCaller cc ON cc.Id = j.CallerId  "
                + " LEFT JOIN ResourcePhoneNumber rph ON rph.ResourceId = rc.ResourceId  "
                + " WHERE (rph.PhoneNumber IS NOT NULL OR cc.PhoneNumber IS NOT NULL) "
                + " AND rph.Priority = 1  "
                + " AND j.Id NOT IN (SELECT JobId FROM JobLock) "
                + " GROUP BY etaNotAdvised.JobReference  "
                + " HAVING COUNT(jc.id) %s 1";

        String operator;
        if (numberOfContacts.equalsIgnoreCase("multiple")){
            operator = ">";
        } else {
            operator = "=";
        }
        sql = String.format(sql, operator);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("get job with ETA Not Advised and multiple contacts assigned: " + sql);
        try {
            int jobReference = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return jobReference;
        } catch (Exception e) {
            return 0;
        }
    }

    public Integer getRandomJobAssignedToASingleCityResourceWithNoiPad() {
        String sql = "SELECT TOP(1) mjaa.JobReference FROM uvw_MonitorJobAwaitingAcceptance mjaa"
                + " INNER JOIN job j ON j.jobreference = mjaa.JobReference"
                + " INNER JOIN uvw_JobResources jr ON j.id = jr.JobId"
                + " INNER JOIN Resource r ON jr.ResourceNames = r.Name"
                + " INNER JOIN Site s ON s.Id = mjaa.SiteId"
                + " LEFT JOIN ResourceProfile rp ON rp.Id = r.ResourceProfileId"
                + " LEFT JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId"
                + " LEFT join ResourceNotificationMethod rnm ON r.Id = rnm.ResourceId"
                + " LEFT JOIN NotificationMethodType nmt ON rnm.NotificationMethodTypeId = nmt.Id"
                + " LEFT JOIN Joblock jl ON j.Id = jl.JobId"
                + " WHERE  jr.ResourceNames NOT LIKE '%,%'"
                + " AND rt.AdLogin = 1"
                + " AND (rp.Name LIKE 'RHVAC%' OR rp.Name LIKE 'MST%')"
                + " AND jl.JobId IS NULL"
                + " AND nmt.Name IS NULL"
                + " ORDER BY NEWID()";

        logger.debug("getRandomJobAssignedToASingleResourceWithNoiPad: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            int jobReference = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return jobReference;
        } catch (Exception e) {
            return 0;
        }
    }

    public Integer getParkedJobNotOfTypeWithDesiredFaultPriority(String jobType, String faultPriority) {
        String sql = "SELECT TOP(1) jobReference FROM uvw_MonitorHelpdeskJobParked mjp"
                + " INNER JOIN Site s ON s.Id = mjp.SiteId"
                + " LEFT JOIN Joblock jl ON mjp.Id = jl.JobId"
                + " WHERE FaultPriority = :faultPriority"
                + " AND mjp.JobType != :jobType"
                + " AND jl.JobId IS NULL"
                + " ORDER BY NEWID()";

        logger.debug("getFaultPriorityParkedJob: " + sql.replace(":faultPriority", "'" + faultPriority + "'"));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("faultPriority", faultPriority);
        params.addValue("jobType", jobType);
        try {
            int jobReference = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return jobReference;
        } catch (Exception e) {
            return 0;
        }
    }

    public Integer getJobReferenceNotOfTypeWithDesiredFaultPriority(String jobType, String faultPriority) {
        String sql = "SELECT TOP(1) mjaa.JobReference FROM uvw_MonitorJobAwaitingAcceptance mjaa"
                + " INNER JOIN job j ON j.jobreference = mjaa.JobReference"
                + " INNER JOIN JobStatus js ON j.JobStatusId = js.Id"
                + " INNER JOIN uvw_JobResources jr ON j.id = jr.JobId"
                + " INNER JOIN Resource r ON jr.ResourceNames = r.Name"
                + " INNER JOIN Site s ON s.SiteCode = mjaa.Site "
                + " LEFT JOIN LinkedIncidentCriterion lic ON lic.FaultTypeId = j.FaultTypeId"
                + " LEFT JOIN Joblock jl ON j.Id = jl.JobId"
                + " LEFT JOIN AdditionalResourceRequirement arr ON j.Id = arr.JobId"
                + " WHERE mjaa.FaultPriority = :faultPriority"
                + " AND jr.ResourceNames NOT LIKE '%,%'"
                + " AND mjaa.JobType != :jobType"
                + " AND js.Name = 'Logged'"
                + " AND lic.FaultTypeId IS NULL"
                + " AND jl.JobId IS NULL"
                + " AND arr.JobId IS NULL"
                + " ORDER BY NEWID()";

        logger.debug("getFaultPriorityJobAssignedToAResource: " + sql.replace(":faultPriority", "'" + faultPriority + "'"));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("faultPriority", faultPriority);
        params.addValue("jobType", jobType);
        try {
            int jobReference = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return jobReference;
        } catch (Exception e) {
            return 0;
        }
    }

    public Integer getJobReferenceNumberWithLinkedIncidents() {
        String sql = "SELECT TOP(1) j.JobReference FROM Job j INNER JOIN JobStatus js ON j.JobStatusId = js.Id "
                + "INNER JOIN Site s ON s.Id = j.SiteId "
                + "INNER JOIN JobLinkedIncident jli ON j.Id = jli.JobId "
                + "LEFT JOIN uvw_MonitorLinkedIncident mli ON mli.JobNo = j.JobReference "
                + "LEFT JOIN JobLock jl ON jl.JobId = j.Id "
                + "WHERE jl.JobId IS NULL "
                + "AND mli.JobNo IS NULL "
                + "AND j.JobStatusId != 10 "
                + "ORDER BY NEWID() ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }

    public Integer getRandomJobWhereETAExceedsSLA() {
        String sql = "SELECT TOP(1) "
                + "etaExceedsSLA.JobReference FROM "
                + "(SELECT * FROM dbo.uvw_MonitorJobEtaExceedsSlaInHours UNION SELECT * FROM  dbo.uvw_MonitorJobEtaExceedsSlaOutOfHours) etaExceedsSLA "
                + "INNER JOIN Site s ON s.Id = etaExceedsSLA.SiteId "
                + "INNER JOIN dbo.uvw_MonitorJobETANotAdvised mjena ON mjena.JobReference = etaExceedsSLA.JobReference "
                + "LEFT JOIN Joblock jl ON etaExceedsSLA.Id = jl.JobId "
                + "WHERE etaExceedsSLA.ETATo > GETUTCDATE() "
                + "AND etaExceedsSLA.EtaAcknowledged = 0 "
                + "AND etaExceedsSLA.ResourceName NOT LIKE ('%,%') "
                + "AND jl.JobId IS NULL "
                + "ORDER BY NEWID()";

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("get job with where ETA exceeds SLA: " + sql);
        try {
            int jobReference = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return jobReference;
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean confirmJobNotOnMonitor(Integer jobReference, String monitorIn) {
        String sql = "SELECT CAST(COUNT(1) AS BIT) FROM " + monitorIn + " monitor WHERE monitor.jobReference = :jobReference ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        logger.debug("checking for job ON " + monitorIn + " monitor : " + sql);
        int counter = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        if (counter > 0) {
            return false;
        } else {
            return true;
        }
    }

    public String getJobStatus(String jobReference) {
        String sql = String.format("SELECT js.Name FROM Job j JOIN JobStatus js ON j.JobStatusId = js.Id WHERE j.JobReference = '%s'", jobReference);
        logger.debug("getJobStatus: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public Integer getRandomP1JobWhereETAExpiredAndWithinSLA() {
        String sql = "SELECT TOP(1) etaWithinSLA.JobReference "
                + " FROM dbo.uvw_MonitorFocusJobETAExpired etaWithinSLA  "
                + " INNER JOIN Site s ON s.Id = etaWithinSLA.SiteId  "
                + " INNER JOIN job j ON j.jobreference = etaWithinSLA.JobReference  "
                + " INNER JOIN ResourceAssignment ra ON j.Id = ra.JobId  "
                + " INNER JOIN ResourceAssignmentStatus ras ON ra.ResourceAssignmentStatusId = ras.Id  "
                + " WHERE  "
                + " etaWithinSLA.SlaExceededByMin < 0 "
                + " AND etaWithinSLA.ResourceName NOT LIKE ('%,%')  "
                + " AND etaWithinSLA.FaultPriority = 'P1' "
                + " AND ras.Name IN ('ETA Provided','ETA Advised To Site','Returning') ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("get job with where ETA within SLA but ETA expired: " + sql);
        try {
            int jobReference = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return jobReference;
        } catch (Exception e) {
            return 0;
        }
    }

    public Integer getJobOfTypeWithRequiredStatusAndResourceProfile(String jobType, String jobStatus, String resourceProfile) {
        String sql = "SELECT TOP(1) j.JobReference FROM Job j"
                +  " INNER JOIN Site s ON s.Id = j.SiteId"
                +  " INNER JOIN JobStatus js ON j.JobStatusId = js.Id  "
                +  " INNER JOIN uvw_JobResources jr ON jr.JobId = j.Id"
                +  " INNER JOIN Resource r ON r.Name = jr.ResourceNames"
                +  " INNER JOIN ResourceProfile rp ON rp.Id = r.ResourceProfileId"
                +  " INNER JOIN JobType jt ON j.JobTypeId = jt.Id"
                +  " LEFT JOIN JobLock jl ON j.Id = jl.JobId"
                +  " LEFT JOIN %portaldb.tblPricebook pb ON r.EpochId = pb.fld_int_ResourceId"
                +  " WHERE js.Name = :jobStatus"
                +  " AND jr.ResourceNames NOT LIKE '%,%'"
                +  " AND rp.Name  = :resourceProfile"
                +  " AND jl.JobId IS NULL"
                +  " AND jt.Name = :jobType";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        if ("Contractor who is always chargeable and without zero price in the pricebook".equalsIgnoreCase(resourceProfile)) {
            sql = sql + " AND r.AlwaysChargeable = 1 AND ((pb.fld_str_PartCode = 'Standard Callout' AND fld_cur_CostEach > 0) OR pb.fld_int_ResourceId IS NULL)";
        } else if ("Contractor who is either non-chargeable or has zero price in the pricebook".equalsIgnoreCase(resourceProfile)) {
            sql = sql + " AND (r.AlwaysChargeable = 0 OR (r.AlwaysChargeable = 1 AND pb.fld_str_PartCode = 'Standard Callout' AND fld_cur_CostEach = 0))";
        } else if ("Contractor who is non-chargeable".equalsIgnoreCase(resourceProfile)) {
            sql = sql + " AND r.AlwaysChargeable = 0";
        }
        resourceProfile = resourceProfile.contains("Contractor") ? "Contractor" : resourceProfile;
        sql = sql + " ORDER BY NEWID()";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobStatus", jobStatus);
        params.addValue("resourceProfile", resourceProfile);
        params.addValue("jobType", jobType);
        logger.debug("getJobWithRequiredStatusAndResourceProfile: " + sql);
        try {
            int jobReference = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return jobReference;
        } catch (Exception e) {
            return 0;
        }
    }

    public Integer getJobId(Integer jobReference) {
        String sql = String.format("SELECT Id FROM Job WHERE JobReference = %d", jobReference);
        logger.debug("getJobId: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }

    public Integer getJobOfTypeAwaitingAcceptanceByCityTechResource(String jobType) {
        String sql = "SELECT TOP(1) mjaa.JobReference FROM uvw_MonitorJobAwaitingAcceptance mjaa"
                + " INNER JOIN job j ON j.jobreference = mjaa.JobReference"
                + " INNER JOIN JobStatus js ON j.JobStatusId = js.Id"
                + " INNER JOIN uvw_JobResources jr ON j.id = jr.JobId"
                + " INNER JOIN Resource r ON jr.ResourceNames = r.Name"
                + " INNER JOIN ResourceAssignment ra ON r.Id = ra.ResourceId"
                + " INNER JOIN ResourceAssignmentStatus ras ON ra.ResourceAssignmentStatusId = ras.Id"
                + " INNER JOIN Site s ON s.Id = j.SiteId"
                + " LEFT JOIN ResourceProfile rp ON rp.Id = r.ResourceProfileId"
                + " LEFT JOIN LinkedIncidentCriterion lic ON lic.FaultTypeId = j.FaultTypeId"
                + " LEFT JOIN Joblock jl ON j.Id = jl.JobId"
                + " WHERE  jr.ResourceNames NOT LIKE '%,%'"
                + " AND ras.Name = 'New Job Notification Sent'"
                + " AND mjaa.JobType = :jobType"
                + " AND rp.ResourceTypeId = 1 AND rp.JobCallouts = 1 AND r.Active = 1"
                + " AND jl.JobId IS NULL"
                + " AND lic.FaultTypeId IS NULL"
                + " AND js.Name = 'Logged'"
                + " ORDER BY NEWID()";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobType", jobType);
        logger.debug("getJobAwaitingAcceptanceByCitytechResource: " + sql);
        try {
            int jobReference = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return jobReference;
        } catch (Exception e) {
            return 0;
        }
    }

    public List<Integer> getRandomJobReferences(int count){
        String sql = "SELECT TOP(%d) JobReference FROM Job j LEFT JOIN JobLock jl ON j.Id = jl.JobId WHERE j.JobStatusId NOT IN (0, 8, 9, 10) AND jl.JobId IS NULL ORDER BY NEWID()";
        sql = String.format(sql, count);
        logger.debug("getRandomJobReferences");
        List<Integer> jobs = jdbc_helpdesk.query(sql, new MapSqlParameterSource(), new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getInt("JobReference"));
            }
        });
        return jobs;
    }

    public List<Map<String, Object>> getRandomJobAssignedToAResourceToAddResourceProfile(String jobType) {
        String sql = "SELECT TOP(1) mjaa.JobReference,r.Name AS ResourceName FROM uvw_MonitorJobAwaitingAcceptance mjaa "
                + " INNER JOIN job j ON j.jobreference = mjaa.JobReference "
                + " INNER JOIN uvw_JobResources jr ON j.id = jr.JobId "
                + " INNER JOIN Site s ON s.Id = mjaa.SiteId "
                + " LEFT JOIN LinkedIncidentCriterion lic ON lic.FaultTypeId = j.FaultTypeId "
                + " LEFT JOIN Joblock jl ON j.Id = jl.JobId "
                + " LEFT JOIN SiteResource sr ON j.SiteId = sr.SiteId "
                + " LEFT JOIN Resource r ON r.Id = sr.ResourceId "
                + " LEFT JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id "
                + " WHERE r.Active = 1 "
                + " AND jr.ResourceNames NOT LIKE '%,%' "
                + " AND mjaa.JobType = :jobType "
                + " AND lic.FaultTypeId IS NULL "
                + " AND jl.JobId IS NULL "
                + " AND rp.JobCallouts = 1 "
                + " AND jr.ResourceNames != r.Name "
                + " ORDER BY NEWID()";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobType", jobType);
        logger.debug("getRandomJobAssignedToAResourceToAddResourceProfile: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, params);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getPONumberCount(int jobReference) {
        String sql = "SELECT count(*) fld_str_PONumber FROM Portal.tblPO WHERE fld_int_HelpdeskFaultID = %d";
        sql = String.format(sql, jobReference);
        logger.debug("getPONumberCount: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public Integer getJobReference(Integer jobId) {
        String sql = String.format("SELECT JobReference FROM Job WHERE Id = %d", jobId);
        logger.debug("getJobReference: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public Integer getJobReferenceForSiteVisit(int siteVisitId) {
        String sql = "SELECT JobReference FROM Job j "
                + "INNER JOIN ResourceAssignment ra ON ra.JobId = j.Id "
                + "INNER JOIN SiteVisits sv ON ra.Id = sv.ResourceAssignmentId "
                + "WHERE sv.Id = %d";
        sql = String.format(sql,  siteVisitId);

        logger.debug("getJobReferenceForSiteVisit: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getJobReferenceWhereNoSiteVisit() {
        String sql = "SELECT TOP(1) JobReference FROM Job j "
                + "WHERE Jobreference NOT IN ( "
                + "SELECT JobReference FROM Job j  "
                + "INNER JOIN ResourceAssignment ra ON ra.JobId = j.Id "
                + "INNER JOIN SiteVisits sv ON ra.Id = sv.ResourceAssignmentId )  "
                + "ORDER BY NEWID()";

        logger.debug("getJobReferenceWhereNoSiteVisit: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getJobReferenceWhereNonGasSiteVisit() {
        String sql = "SELECT TOP(1) j.JobReference FROM SiteVisits sv "
                + "INNER JOIN ResourceAssignment ra ON ra.Id = sv.ResourceAssignmentId "
                + "INNER JOIN Job j ON j.Id = ra.JobId "
                + "WHERE sv.id NOT IN (SELECT SiteVisitId FROM SiteVisitGasDetails) "
                + "AND sv.id NOT IN (SELECT SiteVisitId FROM SiteVisitsGasUsage) "
                + "ORDER BY NEWID()";

        logger.debug("getJobReferenceWhereNoSiteVisit: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getJobReferenceOfTypeWithResourceAssignmentStatus(String jobType, String resourceAssignmentStatus) {
        String sql = "SELECT TOP(1) j.JobReference "
                + "FROM Job j "
                + "INNER JOIN Site s ON j.SiteId = s.Id "
                + "INNER JOIN ResourceAssignment ra ON ra.JobId = j.Id "
                + "INNER JOIN ResourceAssignmentStatus ras ON ra.ResourceAssignmentStatusId = ras.Id "
                + "INNER JOIN Resource r ON ra.ResourceId = r.Id "
                + "INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id "
                + "INNER JOIN JobType jt On j.JobTypeId = jt.Id "
                + "INNER JOIN JobStatus js ON js.Id = j.JobStatusId "
                + "LEFT JOIN JobLock jl ON j.Id = jl.JobId "
                + "WHERE rp.ResourceTypeId = 1 AND rp.JobCallouts = 1 AND r.Active = 1 "
                + "AND r.Name NOT LIKE '%,%' "
                + "AND ras.Name = :resourceAssignmentStatus "
                + "AND jt.Name = :jobType "
                + "AND jl.JobId IS NULL "
                + "AND js.IsTerminal = 0 "
                + "ORDER BY NEWID()";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceAssignmentStatus", resourceAssignmentStatus);
        params.addValue("jobType", jobType);
        logger.debug("getJobofTypeWithResourceAssignmentStatus: " + sql.replace(":resourceAssignmentStatus", quote(resourceAssignmentStatus)).replace(":jobType", quote(jobType)));
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            throw new PendingException("No suitable test data found");
        }
    }

    public Integer getJobReferenceOfJobType(String jobType) {
        String sql = "SELECT TOP(1) j.JobReference FROM Job j "
                + "JOIN Site s ON j.SiteId = s.Id "
                + "JOIN ResourceAssignment ra ON ra.JobId = j.Id "
                + "JOIN ResourceAssignmentStatus ras ON ra.ResourceAssignmentStatusId = ras.Id "
                + "JOIN JobType jt On j.JobTypeId = jt.Id "
                + "LEFT JOIN JobLock jl ON j.Id = jl.JobId "
                + "WHERE jt.Name = :jobType "
                + "AND jl.JobId IS NULL ORDER BY NEWID()";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobType", jobType);
        logger.debug("getJobReferenceOfJobType: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            throw new PendingException("No suitable test data found");
        }
    }

    public Integer getNonDeferredJobReferenceOfResourceStatus(String resourceStatus) {
        String sql = "SELECT TOP(1) j.jobReference FROM Job j "
                + "INNER JOIN Site s ON s.Id = j.SiteId "
                + "INNER JOIN ResourceAssignment ra ON j.Id = ra.JobId "
                + "INNER JOIN ResourceAssignmentStatus ras ON ra.ResourceAssignmentStatusId = ras.Id "
                + "LEFT JOIN uvw_MonitorJobDeferred uvw ON j.jobReference = uvw.jobReference "
                + "WHERE ras.Name = :resourceStatus "
                + "AND j.Id NOT IN (SELECT JobId FROM ResourceAssignment GROUP BY JobId HAVING COUNT(*) > 1) "
                + "AND j.JobReference NOT IN (SELECT jobReference FROM uvw_MonitorJobUpliftRequests) "
                + "AND uvw.JobReference IS NULL "
                + "AND j.JobTypeId = 1 " // Only Reactive jobs
                + "AND j.JobStatusId NOT IN ( 9, 10 )  " //Jobs not in fixed and cancelled status
                + "ORDER BY NEWID()";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceStatus", resourceStatus);
        logger.debug("getJobReferenceOfResourceStatus: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getJobReferenceWhereChaseIsResolvedOrActive(String chase, int chaseStatusId) {

        String symbol = chase.equals("single") ? "<" : ">=";

        String sql = "SELECT TOP(1) j.JobReference FROM dbo.JobChase jc INNER JOIN Job j ON jc.jobId = j.Id "
                + "INNER JOIN (SELECT jc.JobId FROM dbo.JobChase jc "
                + "INNER JOIN Job j ON jc.JobId = j.Id "
                + "GROUP BY jc.JobId HAVING COUNT(*) " + symbol + " 2) sub "
                + "ON jc.JobId = sub.JobId "
                + "WHERE jc.JobChaseStatusId = :chaseStatusId "
                + "AND j.JobStatusId NOT IN (9, 10) "  // should not be the fixed and cancelled jobs
                + "AND j.JobTypeId = 1 " // Only Reactive jobs "
                + "ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("chaseStatusId", chaseStatusId);

        logger.debug("getJobReferenceWhereChaseIsResolvedOrActive: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getJobsCreatedBetween(String description, String from, String to) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOBS_CREATED.sql");
        sql = String.format(sql, description, from, to);
        logger.debug("getJobsCreatedBetween: " + sql);

        List<String> jobs = jdbc_helpdesk.query(sql, new MapSqlParameterSource(), new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("JobReference,SiteName,SiteType,AssetType,AssetSubType,Classification,FaultType,Priority,CreatedOn"));
            }
        });
        return jobs;
    }

    public Map<String, Object> getFeedBackDetailsForJob(int jobReference) {
        String sql = "SELECT TOP(1) jf.FeedbackByName FeedBackLeftBy, jfr.Name Rating, jf.FeedbackComments SupportingMessage FROM JobFeedback jf"
                + " JOIN Job j ON jf.JobId = j.Id"
                + " JOIN JobFeedbackRating jfr ON jf.JobFeedbackRatingId = jfr.Id"
                + " WHERE j.JobReference = :jobReference "
                + " ORDER BY jf.id DESC";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        logger.debug("getJobDetails: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, params).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public String getFeedBackResponseTime(int jobReference) {
        String sql = "SELECT TOP(1) FORMAT(jfr.CreatedOn, '" + MS_MEDIUM + "', '" + LOCALE + "') FROM JobFeedbackResponse jfr"
                + " JOIN JobFeedback jf ON jfr.JobFeedbackId = jf.Id JOIN Job j ON jf.JobId = j.Id WHERE j.JobReference = :jobReference"
                + " ORDER BY jfr.id DESC ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        logger.debug("getFeedBackResponseTime: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public int getCountOfJobsAwaitingFeedbackResponseAssignedToRFM(String rfm) {
        String sql = "SELECT COUNT(DISTINCT j.JobReference) FROM JobFeedback jf LEFT JOIN JobFeedbackResponse jfr ON jf.Id = jfr.JobFeedbackId"
                + " JOIN Job j ON jf.JobId = j.Id INNER JOIN Site s ON j.SiteId = s.Id"
                + " JOIN OrganisationStructureSite oss ON oss.SiteId = s.Id JOIN OrganisationStructure os ON oss.OrganisationStructureId = os.Id"
                + " JOIN uvw_ManagerToOrganisationStructures mos ON mos.OrganisationStructureId = os.Id JOIN Resource r ON mos.ManagerResourceId = r.Id"
                + " WHERE jfr.JobFeedbackId IS NULL AND r.Name = :rfm";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rfm", rfm);
        logger.debug("getCountOfJobsAwaitingFeedbackResponseAssignedToRFM: " + sql);
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public int getResourceAssignmentID(Integer jobReference, Integer resourceId) {
        String sql = "SELECT TOP(1) ra.id FROM job j INNER JOIN resourceassignment ra ON j.id = ra.jobid " +
                "WHERE  j.jobreference = :jobReference " +
                "AND ra.resourceid = :resourceId " +
                "ORDER BY ra.CreatedOn DESC";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        params.addValue("resourceId", resourceId);

        logger.debug("getResourceAssignmentID: " + sql.replace(":jobReference", jobReference.toString()).replace(":resourceId", resourceId.toString()));
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public Integer getJobReferenceOfClientStatusWithFaultPriority(int clientStatusId, int faultPriority) {
        String sql = "SELECT TOP(1) j.JobReference FROM job j " +
                "INNER JOIN JobStatus js ON j.JobStatusId = js.Id " +
                "INNER JOIN FaultPriorityMapping fpm ON j.ResponsePriorityId = fpm.ResponsePriorityId " +
                "INNER JOIN uvw_faultprioritymappings fpm1 ON fpm.Id = fpm1.Id " +
                "WHERE js.Id = :clientStatusId ";

        sql = faultPriority == 0 ? sql
                : sql + "AND fpm1.Priority = :faultPriority ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("clientStatusId", clientStatusId);
        params.addValue("faultPriority", faultPriority);

        logger.debug("getJobReferenceOfClientStatusWithFaultPriority: " + sql);
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public Integer getRandomFeedbackRatingId() {
        String sql = "SELECT TOP(1) Id FROM JobFeedbackRating ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getRandomFeedbackRating: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void insertJobFeedback(Integer feedbackRatingId, String feedbackComments, Integer jobReference) {
        String sql = "INSERT INTO JobFeedback (JobId, JobFeedbackRatingId, FeedbackComments, FeedbackByName, CreatedOn, CreatedBy, UpdatedOn, UpdatedBy, CreatorId)"
                + " (SELECT j.Id, %d, '%s', 'Test Automation',  GETUTCDATE(), '11111111-1111-1111-1111-111111111113', GETUTCDATE(), '11111111-1111-1111-1111-111111111113', '11111111-1111-1111-1111-111111111113'"
                + " FROM Job j WHERE j.JobReference = %d)";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, feedbackRatingId, feedbackComments, jobReference);
        logger.debug("insertJobFeedback: " + sql);
        try {
            jdbc_helpdesk.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Exception " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getJobsForCancelation(int keepCount, String monitor){
        String sql = "WITH JobsForDestruction AS (SELECT DISTINCT Id, JobReference, ROW_NUMBER() OVER (ORDER BY Id DESC) AS RowNum FROM %s) "
                + "SELECT DISTINCT jfd.* "
                + "FROM JobsForDestruction jfd "
                + "WHERE jfd.RowNum > %d";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = String.format(sql,  monitor, keepCount);
        logger.debug("getJobsForCancelation: " + sql);
        return jdbc_helpdesk.queryForList(sql,new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getJobsForCompletion(int keepCount, String monitor){
        String sql = "WITH JobsForDestruction AS (SELECT DISTINCT Id, JobReference, SiteId, ResourceName, ROW_NUMBER() OVER (ORDER BY Id DESC) AS RowNum FROM %s) "
                + "SELECT DISTINCT jfd.*, ra.ResourceId, ra.id resourceAssignmentId, au.UserName "
                + "FROM JobsForDestruction jfd "
                + "INNER JOIN ResourceAssignment ra ON jfd.Id = ra.JobId "
                + "INNER JOIN Resource r ON jfd.ResourceName = r.Name and r.Id = ra.ResourceId "
                + "INNER JOIN ApplicationUser au ON au.ResourceId = r.Id "
                + "WHERE jfd.RowNum > %d ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = String.format(sql,  monitor, keepCount);
        logger.debug("getJobsForCompletion: " + sql);
        return jdbc_helpdesk.queryForList(sql,new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getJobsForResourceRemoval(int keepCount, String monitor){
        String sql = "WITH JobsForDestruction AS (SELECT DISTINCT Id, JobReference, ROW_NUMBER() OVER (ORDER BY Id DESC) AS RowNum FROM %s) "
                + "SELECT DISTINCT jfd.*, ra.id resourceAssignmentId "
                + "FROM JobsForDestruction jfd "
                + "INNER JOIN AdditionalResourceRequirement arr ON jfd.id = arr.jobid "
                + "INNER JOIN ResourceAssignment ra ON jfd.Id = ra.JobId "
                + "WHERE jfd.RowNum > %d "
                + "AND ra.ResourceAssignmentStatusId IN (14)";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = String.format(sql,  monitor, keepCount);
        logger.debug("getJobsForResourceRemoval: " + sql);
        return jdbc_helpdesk.queryForList(sql,new MapSqlParameterSource());
    }


    public List<Integer> getARRID(Integer jobId) {
        String sql = String.format("SELECT Id FROM AdditionalResourceRequirement WHERE JobId = %d AND ReasonId = 28", jobId);
        logger.debug("getARRID: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return new ArrayList<Integer>();
        }
    }

    public List<Map<String, Object>> getJobsForFundingRequestRejection(int keepCount, String monitor){
        String sql = "WITH JobsForDestruction AS (SELECT DISTINCT Id, JobReference, ROW_NUMBER() OVER (ORDER BY Id DESC) AS RowNum FROM %s) "
                + "SELECT DISTINCT jfd.*, ra.id resourceAssignmentId, fr.Id fundingRequestId, fr.InitialRequestApproverId "
                + "FROM JobsForDestruction jfd "
                + "INNER JOIN ResourceAssignment ra ON jfd.Id = ra.JobId "
                + "INNER JOIN FundingRequest fr ON ra.id = fr.ResourceAssignmentId "
                + "WHERE jfd.RowNum > %d "
                + "AND ra.ResourceAssignmentStatusId IN (19)";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = String.format(sql,  monitor, keepCount);
        logger.debug("getJobsForFundingRequestRejection: " + sql);
        return jdbc_helpdesk.queryForList(sql,new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getJobClosedownDetails(int jobReference) {
        String sql = "SELECT DISTINCT po.JobType, j.CompletedDate, sv.WorkStartTime, sv.WorkEndTime, REPLACE(REPLACE(SUBSTRING(rae.Notes, 11, (LEN(rae.Notes) - 12)), '\\r\\n', ''), '\\' , '') [Job Closedown] FROM Job j "
                + "JOIN %portaldb.uvw_PartsOrders po ON po.JobRef = j.JobReference "
                + "JOIN ResourceAssignment ra ON ra.JobId = j.Id "
                + "JOIN Resource r ON ra.ResourceId = r.Id "
                + "JOIN ResourceAssignmentEvent rae ON ra.Id = rae.ResourceAssignmentId "
                + "JOIN ResourceAssignmentEventType raet ON rae.ResourceAssignmentEventTypeId = raet.Id "
                + "JOIN SiteVisits sv on ra.Id = sv.ResourceAssignmentId "
                + "WHERE raet.Name = 'Assignment Completed' "
                + "AND j.JobReference = :jobReference "
                + "ORDER BY sv.WorkStartTime DESC";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        logger.debug("getJobClosedownDetails: " + sql.replace(":jobReference", String.valueOf(jobReference)));
        try {
            return jdbc_helpdesk.queryForList(sql, params);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Pulls back the correct fault priority for the given inputs
     * @param siteId
     * @param assetClassificationId
     * @param faultTypeId
     * @return
     */
    public Integer getFaultPriorityForJobCreationHelper(int siteId, int assetClassificationId, int faultTypeId) {
        String sql = String.format("SELECT ResponsePriorityId " +
                "FROM uvw_faultprioritymappings fpm INNER JOIN Site s ON fpm.SiteTypeId = s.SiteTypeId " +
                "WHERE fpm.faulttypeid=%d and fpm.assetclassificationid = %d " +
                "AND s.Id = %d", faultTypeId, assetClassificationId, siteId );
        logger.debug("getFaultPriorityForJobCreationHelper: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @param jobId
     * @return Epoch Import Status
     */
    public Integer getJobEpochImportStatus(Integer jobId) {
        String sql = "SELECT fld_int_EpochImportStatus FROM %iosdb.tblEpochNewPartsRequest "
                + " WHERE fld_int_jobid = :jobId";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobId", jobId);

        logger.debug("getJobEpochImportStatus: " + sql.replace(":jobId", String.valueOf(jobId)));

        try {
            int count = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return count;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return 0;
        }
    }

    /**
     * Processes the part requests raised from the iPad and updates the Epoch Import Status.
     */
    public void updateEpochImportStatus(Integer jobReference) {
        StoredProcedure procedure = new GenericStoredProcedure();
        procedure.setDataSource(helpdeskDataSource);
        String sql = "%testdb.usp_run_iPadSync";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        procedure.setSql(sql);
        procedure.setFunction(false);

        SqlParameter[] parameters = { new SqlParameter(Types.INTEGER) };
        procedure.setParameters(parameters);
        procedure.compile();
        logger.debug("updateEpochImportStatus: execute " + sql + " @jobReference = " + jobReference);
        procedure.execute(jobReference);
    }

    /**
     * Updates the PO Status & Cost Expected
     * @param jobId
     * @param costExpected
     */
    public void updateJobPOStatus(Integer jobId, Float costExpected) {
        String sql = "UPDATE %portaldb.tblPO"  +
                "  SET fld_int_POStatusID = 7, fld_cur_CostExpected = :costExpected," +
                "  fld_int_CompanyId = (SELECT Id from %helpdeskdb.Company WHERE IsPrimary = 1)" +
                "  WHERE fld_int_HelpdeskFaultID = :jobId";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobId", jobId);
        params.addValue("costExpected", costExpected);

        logger.debug("updateJobPOStatus: " + sql.replace(":jobId", String.valueOf(jobId)).replace(":costExpected", String.valueOf(costExpected)));
        try {
            jdbc_helpdesk.update(sql, params);
        } catch (Exception e) {
            logger.debug("Exception " + e.getMessage());
        }
    }

    public List<String> getJobReferencesAwaitingAssignment() throws Exception {
        String sql = "SELECT j.Jobreference FROM uvw_MonitorJobAwaitingAssignment j";
        List<String> faultPriorities = jdbc_helpdesk.query(sql, new MapSqlParameterSource(), new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("Jobreference"));
            }
        });
        return faultPriorities;
    }

    public Map<String, Object> getJobAwaitingReallocation() throws Exception{
        String sql = FileHelper.getResourceFile("sql/select/", "GET_USER_AND_JOB_AWAITING_REALLOCATION.sql");

        if ( !testData.getBoolean("withCoveringResource") ){
            sql = sql + " AND jar.CoveringResourceId IS NULL";

        } else if ( testData.getBoolean("withCoveringResource") ){
            sql = sql + " AND jar.CoveringResourceId IS NOT NULL";
        }
        sql = sql + " AND ja.NumResourcesAssigned = 1";   // only one active resource assigned

        logger.debug("getJobAwaitingReallocation: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getCompletedJobWithAbsentResource() throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_COMPLETED_JOB_WITH_ABSENT_RESOURCE.sql");
        logger.debug("getCompletedJobWithAbsentResource: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getCallerNameAndJobTitle(int jobRef) throws Exception{
        String sql = " SELECT cc.Name, s.Name AS SiteName, cc.JobTitle, cc.Extension"
                + " FROM ClientCaller cc"
                + " INNER JOIN Job j ON cc.Id = j.CallerId"
                + " INNER JOIN Site s ON cc.SiteId = s.Id"
                + " WHERE j.JobReference = :jobRef";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobRef", jobRef);
        logger.debug("getCallerNameAndJobTitle: " + sql.replace(":jobRef", String.valueOf(jobRef)));

        try {
            return jdbc_helpdesk.queryForList(sql, params);
        } catch(Exception e) {
            return null;
        }
    }

    public void deleteFeedbackResponses(int jobReference) {
        String sql = String.format("DELETE JobFeedbackResponse WHERE Id IN ( "
                + "SELECT jfr.Id AS Id FROM JobFeedbackResponse jfr "
                + "INNER JOIN JobFeedback jf ON jf.Id = jfr.JobFeedbackId "
                + "INNER JOIN Job j ON jf.JobId = j.Id "
                + "WHERE j.JobReference = %d )", jobReference);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("deleteFeedbackResponses: " + sql);
        jdbc_helpdesk.update(sql, params);
    }

    public Integer getJobReferenceForResourceAssignmentId(int resourceAssignmentId) {
        String sql = "SELECT j.JobReference FROM ResourceAssignment ra INNER JOIN Job j ON j.Id = ra.JobId AND ra.Id = %d";
        sql = String.format(sql, resourceAssignmentId);
        logger.debug("getJobReferenceForResourceAssignmentId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public String getCallerNameForJob(int jobReference) {
        String sql = String.format("WITH RC_CTE AS (SELECT r.Name, j.JobReference FROM Job j " +
                "  LEFT JOIN ResourceCaller rc ON j.CallerId = rc.Id" +
                "  LEFT JOIN Resource r ON rc.ResourceId = r.Id)," +
                "  CC_CTE AS (SELECT uc.Name, j.JobReference FROM Job j " +
                "  LEFT JOIN uvw_CallerForSearch uc ON j.CallerId = uc.Id)," +
                " UNION_CTE AS (SELECT * FROM RC_CTE UNION SELECT * FROM CC_CTE)" +
                "SELECT Name FROM UNION_CTE WHERE JobReference = %d AND Name IS NOT NULL", jobReference);
        logger.debug("getCallerNameForJob: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getCallerNameForJobId(int jobId) {
        String sql = String.format("WITH RC_CTE AS (SELECT r.Name, j.Id FROM Job j " +
                "  LEFT JOIN ResourceCaller rc ON j.CallerId = rc.Id" +
                "  LEFT JOIN Resource r ON rc.ResourceId = r.Id)," +
                "  CC_CTE AS (SELECT uc.Name, j.Id FROM Job j " +
                "  LEFT JOIN uvw_CallerForSearch uc ON j.CallerId = uc.Id)," +
                " UNION_CTE AS (SELECT * FROM RC_CTE UNION SELECT * FROM CC_CTE)" +
                "SELECT Name FROM UNION_CTE WHERE Id = %d AND Name IS NOT NULL", jobId);
        logger.debug("getCallerNameForJobId: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getResourceStatus(int jobReference) throws Exception{
        String sql = " SELECT ra.ResourceId, ras.Name AS ResourceStatus, js.Name AS ClientStatus FROM Job j "
                + "INNER JOIN JobStatus js ON js.Id = j.JobStatusId "
                + "INNER JOIN ResourceAssignment ra ON ra.JobId = j.Id "
                + "INNER JOIN ResourceAssignmentStatus ras ON ra.ResourceAssignmentStatusId = ras.Id "
                + "WHERE JobReference = " + jobReference;

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getResourceStatus: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, params);
        } catch(Exception e) {
            return null;
        }
    }

    public void updateJobStatus(Integer jobReference, Integer jobStatusId) {
        String sql = "UPDATE Job SET JobStatusId = :jobStatusId WHERE JobReference = :jobReference";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobStatusId", jobStatusId);
        params.addValue("jobReference", jobReference);

        logger.debug("updateJobStatus: " + sql.replace(":jobId", String.valueOf(jobReference)));
        try {
            jdbc_helpdesk.update(sql, params);
        } catch (Exception e) {
            logger.debug("Exception " + e.getMessage());
        }
    }

    public Map<String, Object> getChaseForJobReference(int jobReference) {
        String sql = "SELECT TOP(1) jc.ProblemDescription AS Description, ct.Name AS ChaseType, r.Name AS Resource, jcs.Name AS Status, jc.CreatedOn "
                + "FROM JobChase jc INNER JOIN Job j ON jc.JobId = j.Id INNER JOIN ChaseType ct ON jc.ChaseTypeId = ct.Id "
                + "INNER JOIN JobChaseStatus jcs ON jc.JobChaseStatusId = jcs.Id LEFT JOIN Resource r ON jc.ResourceId = r.Id "
                + "WHERE j.JobReference = :jobReference ORDER BY jc.CreatedOn DESC";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        logger.debug("getChaseForJobReference: " + sql.replace(":jobReference", String.valueOf(jobReference)));

        try {
            return jdbc_helpdesk.queryForList(sql, params).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getChaseUpdates(int jobReference) throws Exception {
        String sql = "SELECT jcu.ChaseUpdate, jcu.CreatedOn, CONCAT(LTRIM(RTRIM(au.FirstName)) + ' ', LTRIM(RTRIM(au.LastName))) AS Name "
                + "FROM JobChase jc "
                + "INNER JOIN Job j ON jc.JobId = j.Id INNER JOIN JobChaseUpdate jcu ON jc.Id = jcu.JobChaseId "
                + "INNER JOIN ApplicationUser au ON jcu.CreatedBy = au.Id WHERE j.JobReference = :jobReference";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        logger.debug("getChaseUpdates: " + sql.replace(":jobReference", String.valueOf(jobReference)));

        try {
            return jdbc_helpdesk.queryForList(sql, params);
        } catch(Exception e) {
            return null;
        }
    }

    public Integer getLinkedJobReference(int jobReference) {
        String sql = "SELECT JobReference FROM Job WHERE Id = (SELECT jlj.LinkedJobId FROM JobLinkedJob jlj JOIN Job j ON jlj.JobId = j.Id WHERE j.JobReference = %d)";
        sql = String.format(sql, jobReference);
        logger.debug("getLinkedJobReference: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getJobType(int jobReference) {
        String sql = "SELECT jt.Name FROM JobType jt JOIN Job j ON jt.Id = j.JobTypeId WHERE j.JobReference = %d";
        sql = String.format(sql, jobReference);
        logger.debug("getJobType: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void updateOrderValue(Integer jobReference, String cost) {
        String sql = "UPDATE %portaldb.tblPO " +
                "SET fld_cur_CostExpected = :cost " +
                "WHERE fld_int_HelpdeskFaultID = :jobReference";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        params.addValue("cost", cost);

        logger.debug("updateOrderValue: " + sql.replace(":jobReference", String.valueOf(jobReference)));
        try {
            jdbc_helpdesk.update(sql, params);
        } catch (Exception e) {
            logger.debug("Exception " + e.getMessage());
        }
    }

    public Map<String, Object> getRandomJobForDuplication() throws Exception {
        String sql = "SELECT TOP(1) j.JobReference, s.Id AS SiteId, s.SiteCode, s.Name AS SiteName, j.AssetClassificationId, ac.Name AS AssetClassificationName, ast.Name AS AssetSubTypeName, at.Name AS AssetTypeName, j.LocationId, l.Name AS LocationName, j.FaultTypeId, ft.Name AS FaultTypeName "
                + "FROM Job j "
                + "INNER JOIN Site s ON s.Id = j.SiteId "
                + "INNER JOIN ResourceAssignment ra ON ra.JobId = j.Id "
                + "INNER JOIN ResourceAssignmentStatus ras ON ra.ResourceAssignmentStatusId = ras.Id "
                + "INNER JOIN AssetClassification ac ON ac.Id = j.AssetClassificationId "
                + "INNER JOIN AssetSubType ast ON ast.Id = ac.AssetSubTypeId "
                + "INNER JOIN AssetType at ON at.Id = ast.AssetTypeId "
                + "INNER JOIN Location l ON l.Id = j.LocationId "
                + "INNER JOIN FaultType ft ON ft.Id = j.FaultTypeId "
                + "WHERE j.JobStatusId IN (1, 2, 3) "
                + "AND j.JobTypeId NOT IN (5, 7) " // 5 = Landlord, 7 = potential landlord
                + "AND ras.IsTerminal = 0 "
                + "AND ac.Name != '' "
                + "ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getRandomJobForDuplication: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Integer getJobStatusId(int jobReference) {
        String sql = "SELECT JobStatusId FROM Job WHERE jobReference = %d";
        sql = String.format(sql, jobReference);
        logger.debug("getJobStatusId: " + sql);

        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }

    public List<Map<String, Object>> getJobReferencesAndStatus(int jobReference) throws Exception {
        String sql = " SELECT j.JobReference [Job Ref], js.Name JobStatus FROM Job j JOIN JobStatus js ON j.JobStatusId = js.Id WHERE j.JobReference LIKE ':jobReference%'";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        logger.debug("getJobReferencesAndStatus: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, params);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getClosedJobs(int resourceId) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_CLOSED_JOBS_FOR_RESOURCE.sql");
        sql = String.format(sql, resourceId);
        logger.debug("getClosedJobs: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getNumberOfJobsAllocated(int resourceId) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_NUMBER_OF_JOBS_ALLOCATED.sql");
        sql = String.format(sql, resourceId);
        logger.debug("getNumberOfJobsAllocated: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getNumberOfJobsAwaitingAcceptance(int resourceId) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_NUMBER_OF_JOBS_AWAITING_ACCEPTANCE.sql");
        sql = String.format(sql, resourceId);
        logger.debug("getNumberOfJobsAwaitingAcceptance: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getJobChaseDetails(int jobReference) throws Exception {
        String sql = "SELECT jc.CallerName SubmittedBy, jc.CallerTelephone ContactNumber, jc.ProblemDescription Description"
                + " FROM JobChase jc JOIN Job j ON jc.JobId = j.Id WHERE j.JobReference = %d";

        sql = String.format(sql, jobReference);
        logger.debug("getJobChaseDetails: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getJobFeedbackDetails(int jobReference) throws Exception {
        String sql = "SELECT jf.JobFeedbackRatingId, jf.FeedbackByName SubmittedBy, jf.FeedbackComments"
                + " FROM JobFeedback jf JOIN Job j ON jf.JobId = j.Id WHERE j.JobReference = %d";

        sql = String.format(sql, jobReference);
        logger.debug("getJobFeedbackDetails: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getFixedJobWithNoPreviousFeedback() {
        String sql = "SELECT TOP(1) j.JobReference , s.SiteCode"
                + " FROM Job j"
                + " LEFT JOIN JobFeedback jf ON j.Id = jf.JobId"
                + " JOIN Site s ON j.SiteId = s.Id"
                + " WHERE j.JobStatusId = 9"    // Fixed status
                + " AND j.JobTypeId = 1"        // Reactive Job
                + " AND j.JobSourceId = 6"      // Created in Helpdesk
                + " AND jf.id IS NULL"
                + " AND j.CompletedDate >= GETDATE() - 90";

        logger.debug("getFixedJobWithNoPreviousFeedback: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getJobDetailsForStorePortalTables(int jobReference) throws Exception {
        String sql = "SELECT TOP(1) j.JobReference JobId, FORMAT(j.CreatedOn, 'd MMM yyyy') LoggedDate, jt.Name JobType, js.Name JobStatus,"
                + " CONCAT(at.Name, ' > ',ast.Name,' > ',ac.Name) AssetType, r.Name OnSiteResource, fp.Priority FaultPriority, fp.Detail Priority,"
                + " FORMAT(j.CompletedDate, 'd MMM yyyy') ClosedDate, jf.FeedbackByName RaisedBy, jfr.Name FeedbackRating, jf.FeedbackComments Comments"
                + " FROM Job j"
                + " JOIN JobType jt ON j.JobTypeId = jt.Id"
                + " JOIN JobStatus js ON j.JobStatusId = js.Id"
                + " LEFT JOIN ResourceAssignment ra ON j.Id = ra.JobId"
                + " LEFT JOIN Resource r ON ra.ResourceId = r.Id"
                + " JOIN AssetClassification ac ON j.AssetClassificationId = ac.Id"
                + " JOIN AssetSubType ast ON ac.AssetSubTypeId = ast.Id"
                + " JOIN AssetType at ON ast.AssetTypeId = at.Id"
                + " JOIN FaultPriority fp ON j.ResponsePriorityId = fp.Id"
                + " LEFT JOIN JobFeedback jf ON j.Id = jf.JobId"
                + " LEFT JOIN JobFeedbackRating jfr ON jf.JobFeedbackRatingId = jfr.Id"
                + " WHERE j.JobReference = %d";

        sql = String.format(sql, jobReference);
        logger.debug("getJobDetailsForStorePortalTables: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getJobWithFeedbackResponseWithinLast30Days() throws Exception {
        String sql = "SELECT TOP (1) * FROM Job j INNER JOIN JobFeedback jf on j.Id = jf.JobId"
                + " INNER JOIN JobFeedbackResponse jfr ON jf.Id = jfr.JobFeedbackId"
                + " WHERE j.CreatedOn > GETDATE()-30";
        logger.debug("getJobWithFeedbackResponse: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getPpmJobDetailsForStorePortalTables(int jobReference) throws Exception {
        String sql = "SELECT JobReference JobId, FORMAT(LoggedDate, 'd MMM yyyy') LoggedDate, 'PPM' JobType,"
                + " SubTypeClassification AssetType, FORMAT(ContractorETADate, 'd MMM yyyy') ETA"
                + " FROM uvw_PpmJobs WHERE JobReference = :jobReference";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getPpmJobDetailsForStorePortalTables: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, params).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getJobDetailsForStorePortal(int jobReference) throws Exception {
        String sql = "SELECT j.JobReference, CONCAT(at.Name, ' > ',ast.Name,' > ',ac.Name) AssetType, l.Name Location, ft.Name FaultType,"
                + " fp.Detail FaultPriority, j.Description, js.Name JobStatus, c.CallerTypeId"
                + " FROM Job j"
                + " JOIN AssetClassification ac ON j.AssetClassificationId = ac.Id"
                + " JOIN AssetSubType ast ON ac.AssetSubTypeId = ast.Id"
                + " JOIN AssetType at ON ast.AssetTypeId = at.Id"
                + " JOIN Location l ON j.LocationId = l.Id"
                + " JOIN FaultType ft ON j.FaultTypeId = ft.Id"
                + " JOIN FaultPriority fp ON j.ResponsePriorityId = fp.Id"
                + " JOIN JobStatus js ON j.JobStatusId = js.Id"
                + " JOIN Caller c ON j.CallerId = c.Id"
                + " WHERE j.JobReference = %d";

        sql = String.format(sql, jobReference);
        logger.debug("getJobDetailsForStorePortal: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getClientCallerDetails(int jobReference) throws Exception {
        String sql = "SELECT cc.Name SiteContact, cc.JobTitle JobRole"
                + " FROM ClientCaller cc"
                + " JOIN Job j ON cc.Id = j.CallerId"
                + " WHERE j.JobReference = %d";

        sql = String.format(sql, jobReference);
        logger.debug("getClientCallerDetails: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getPPMJobDetailsForStorePortal(int jobReference) throws Exception {
        String sql = "SELECT ppmj.JobDescription, js.Name JobStatus"
                + " FROM uvw_PpmJobs ppmj"
                + " JOIN JobStatus js ON ppmj.Status = js.Id"
                + " WHERE JobReference = %d";

        sql = String.format(sql, jobReference);
        logger.debug("getPPMJobDetailsForStorePortal: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getResourceEtaTimeAndDate(int jobReference, int resourceAssignmentEventTypeId) {
        String sql = "SELECT r.Name ResourceName, rae.Detail2"
                + " FROM Job j"
                + " JOIN ResourceAssignment ra ON j.Id = ra.JobId"
                + " JOIN Resource r ON ra.ResourceId = r.Id"
                + " JOIN ResourceAssignmentEvent rae ON ra.Id = rae.ResourceAssignmentId"
                + " WHERE rae.ResourceAssignmentEventTypeId = %d"
                + " AND j.JobReference = %d";

        sql = String.format(sql, resourceAssignmentEventTypeId, jobReference);
        logger.debug("getResourceEtaTimeAndDate: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getResourceNameAndCompletionDate(int jobReference) {
        String sql = "SELECT r.Name ResourceName, j.CompletedDate"
                + " FROM Job j"
                + " JOIN ResourceAssignment ra ON j.Id = ra.JobId"
                + " JOIN Resource r ON ra.ResourceId = r.Id"
                + " WHERE j.JobReference = %d";

        sql = String.format(sql, jobReference);
        logger.debug("getResourceNameAndcompletionDate: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getPpmJobWithDueDateForCurrentMonth() {
        String sql = "SELECT TOP(1) s.SiteCode, ppmj.JobReference, ppmj.JobDescription,"
                + " DATEPART(day, ppmj.ContractorETADate) DayOfCurrentMonth, ppmj.ContractorETADate ETA"
                + " FROM %portaldb.tblPPMScheduleLine ppmsl"
                + " INNER JOIN Site s ON ppmsl.fld_int_StoreID = s.Id"
                + " INNER JOIN uvw_PpmJobs ppmj ON ppmsl.fld_int_StoreID = ppmj.SiteId"
                + " WHERE DATEPART(day, ppmj.ContractorETADate) > 1 AND DATEPART(month, GETDATE()) = DATEPART(month, ppmj.ContractorETADate)"
                + " AND DATEPART(year, GETDATE()) = DATEPART(year, ppmj.ContractorETADate)"
                + " ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getPpmJobWithDueDateForCurrentMonth: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getActivePpmJobWithDueDateForCurrentMonth() {
        String sql = "SELECT TOP(1) ppmj.JobReference JobReference, s.SiteCode"
                + " FROM %portaldb.tblPPMScheduleLine ppmsl"
                + " INNER JOIN site s ON ppmsl.fld_int_StoreID = s.Id"
                + " INNER JOIN uvw_PpmJobs ppmj ON ppmsl.fld_int_StoreID = ppmj.SiteId"
                + " WHERE ppmj.Status = 2"
                + " AND DATEPART(month, GETDATE()) = DATEPART(month, ppmsl.fld_dat_WeekEnding)"
                + " AND DATEPART(year, GETDATE()) = DATEPART(year, ppmsl.fld_dat_WeekEnding)"
                + " ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getActivePpmJobWithDueDateForCurrentMonth: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getJobWithEtaForCurrentMonth() {
        String sql = "SELECT TOP(1) s.SiteCode, j.JobReference, CONCAT(at.Name, ' > ',ast.Name,' > ',ac.Name) AssetType,"
                + " l.Name Location, DATEPART(day, SUBSTRING(rae.Detail2, 14, 11)) DayOfCurrentMonth, rae.Detail2 EtaDetails"
                + " FROM Job j"
                + " JOIN Site s ON j.SiteId = s.Id"
                + " JOIN AssetClassification ac ON j.AssetClassificationId = ac.Id"
                + " JOIN AssetSubType ast ON ac.AssetSubTypeId = ast.Id"
                + " JOIN AssetType at ON ast.AssetTypeId = at.Id"
                + " JOIN Location l ON j.LocationId = l.Id"
                + " JOIN ResourceAssignment ra ON j.Id = ra.JobId"
                + " JOIN ResourceAssignmentEvent rae ON ra.Id = rae.ResourceAssignmentId"
                + " WHERE rae.ResourceAssignmentEventTypeId = 4"
                + " AND DATEPART(month, GETDATE()) = DATEPART(month, SUBSTRING(rae.Detail2, 14, 11))"
                + " AND DATEPART(year, GETDATE()) = DATEPART(year, SUBSTRING(rae.Detail2, 14, 11))"
                + " ORDER BY NEWID()";

        logger.debug("getJobWithEtaForCurrentMonth: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getJobWithEtaForCurrentMonth(int jobRef) {
        String sql = "SELECT TOP (1) s.SiteCode, j.JobReference, CONCAT(at.Name, ' > ',ast.Name,' > ',ac.Name) AssetType,"
                + " l.Name Location, DATEPART(day, SUBSTRING(rae.Detail2, 14, 11)) DayOfCurrentMonth, rae.Detail2 EtaDetails"
                + " FROM Job j"
                + " JOIN Site s ON j.SiteId = s.Id"
                + " JOIN AssetClassification ac ON j.AssetClassificationId = ac.Id"
                + " JOIN AssetSubType ast ON ac.AssetSubTypeId = ast.Id"
                + " JOIN AssetType at ON ast.AssetTypeId = at.Id"
                + " JOIN Location l ON j.LocationId = l.Id"
                + " JOIN ResourceAssignment ra ON j.Id = ra.JobId"
                + " JOIN ResourceAssignmentEvent rae ON ra.Id = rae.ResourceAssignmentId"
                + " WHERE rae.ResourceAssignmentEventTypeId = 4"
                + " AND DATEPART(month, GETDATE()) = DATEPART(month, SUBSTRING(rae.Detail2, 14, 11))"
                + " AND DATEPART(year, GETDATE()) = DATEPART(year, SUBSTRING(rae.Detail2, 14, 11))"
                + " AND j.JobReference = %d";

        sql = String.format(sql, jobRef);
        logger.debug("getJobWithEtaForCurrentMonth: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getJobReferenceAndCountWithMultipleActiveChases() {

        String sql = "SELECT TOP(1) j.JobReference, sub.CHASECOUNT FROM dbo.JobChase jc INNER JOIN Job j ON jc.jobId = j.Id " +
                " INNER JOIN (SELECT jc.JobId, COUNT(*) CHASECOUNT FROM dbo.JobChase jc " +
                " INNER JOIN Job j ON jc.JobId = j.Id " +
                " WHERE jc.JobChaseStatusId = 0 " +
                " GROUP BY jc.JobId HAVING COUNT(*) >= 2) sub " +
                " ON jc.JobId = sub.JobId " +
                " WHERE jc.JobChaseStatusId = 0" +  // 0 is for active chases
                " AND j.JobStatusId NOT IN (9, 10) " +  // 9, 10 is to avoid the fixed and cancelled jobs
                "  AND j.JobTypeId = 1 " + // Only Reactive jobs
                " ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getJobReferenceAndCountWithMultipleActiveChases: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getWatchedJob() {
        String sql = "SELECT TOP(1) mhjw.jobReference, s.SiteCode"
                + " FROM uvw_monitorHelpdeskJobWatched mhjw"
                + " JOIN Site s ON mhjw.SiteId = s.Id"
                + " ORDER BY NEWID()";

        logger.debug("getWatchedJob: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }


    public Map<String, Object> getContractorWithOpenJobType(String jobType) {
        String sql = "SELECT TOP(1) j.JobReference, j.PpmJobId, r.Id ResourceId, r.Name ResourceName, j.SiteId, Username, up.Name UserProfileName "
                + "FROM uvw_MonitorOpenJobsForContractors oj "
                + "INNER JOIN Job j ON oj.JobReference = j.JobReference "
                + "INNER JOIN ResourceAssignment ra ON ra.JobId = j.Id "
                + "INNER JOIN ResourceAssignmentStatus ras ON ras.Id = ra.ResourceAssignmentStatusId "
                + "INNER JOIN Resource r ON ra.ResourceId = r.Id "
                + "INNER JOIN ApplicationUser au ON r.Id = au.ResourceId "
                + "INNER JOIN UserProfile up ON up.Id = au.UserProfileId "
                + "WHERE ra.Active = 1 AND ras.IsTerminal = 0 AND up.Name LIKE 'Contractor Admin' "
                + "AND j.JobStatusId IN (2, 3, 7, 15) "
                + "AND ra.ResourceAssignmentStatusId NOT IN (4, 13, 15, 16) "
                + "AND j.Id NOT IN (SELECT jobId FROM ResourceAssignment GROUP BY JobId HAVING COUNT(*) > 1) "
                + "AND au.PasswordHash = 'ACf6bmCh1o2t1BxtF1U0BFi4RArKmKf5mmxubtBFcV0lakWIO+ay29uicgAEkc2DxA=='";

        sql = jobType.equals("PPM") ? sql + "AND j.PpmJobId IS NOT NULL" : sql + String.format("AND oj.JobType = '%s'", jobType);

        logger.debug("getContractorWithOpenJobType: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getJobAttachmentDetails(Integer jobReference) {
        String sql = "SELECT po.fld_str_PONumber PONumber, jt.Name FileType, jd.Note, jd.CreatedOn FROM %helpdeskdb.JobDocument jd " +
                "INNER JOIN %helpdeskdb.Job j ON jd.JobId = j.Id " +
                "INNER JOIN %portaldb.tblPO po ON po.fld_int_HelpdeskFaultID = j.JobReference " +
                "INNER JOIN %helpdeskdb.JobDocumentType jt ON jt.Id = jd.JobDocumentTypeId " +
                "WHERE j.JobReference = :jobReference";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getJobAttachmentDetails: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);

        try {
            return jdbc_helpdesk.queryForList(sql, params).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getRecordCountForStatus(Integer jobReference, String status) {
        String sql = "SELECT Count(*) FROM ResourceAssignment ra " +
                "INNER JOIN ResourceAssignmentStatus ras ON ras.Id = ra.ResourceAssignmentStatusId " +
                "LEFT OUTER JOIN Job j ON ra.JobId = j.Id " +
                "WHERE j.JobReference = %d " +
                "AND ra.Active = 1 " +
                "AND ras.Name = '%s'";

        sql = String.format(sql, jobReference, status);
        logger.debug("getRecordCountForStatus: " + sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }
}