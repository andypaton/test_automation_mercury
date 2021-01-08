package mercury.helpers.dbhelper;

import static mercury.runtime.ThreadManager.getWebDriver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mercury.database.config.DbConfigV2;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfigV2.class)
public class DbHelperMonitors {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;
    @Autowired private DbHelperTimeZone dbHelperTimeZone;

    public Integer getSiteId(String siteName) {
        String sql = String.format("SELECT Id FROM Site WHERE Name = '%s'", siteName);
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch(Exception e) {
            return null;
        }
    }

    public List<String> getMonitors(int userProfileId) {
        String sql = "SELECT DISTINCT DisplayName FROM MonitorArea ma INNER JOIN UserProfileToMonitorArea uptma ON ma.Id = uptma.MonitorAreaId WHERE uptma.UserProfileId = %d";
        sql = String.format(sql, userProfileId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getMonitors: " + sql);
        List<String> monitors = jdbc_helpdesk.query(sql, params, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("DisplayName"));
            }
        });
        return monitors;
    }

    public String getViewName(String monitorName) throws Exception {
        String view;
        switch(monitorName) {
        // Jobs
        case "Awaiting Assignment" : view = dbHelperTimeZone.isHelpdeskOutOfHours() ? "uvw_MonitorJobAwaitingAssignment" : "uvw_MonitorHelpdeskJobAwaitingAssignment"; break;
        case "Awaiting Acceptance" : view = "uvw_MonitorHelpdeskJobAwaitingAcceptance"; break;
        case "Funding Requests" : view = "uvw_MonitorJobFundingRequests"; break;
        case "Removal Requests" : view = "uvw_MonitorJobRemovalRequests"; break;
        case "Uplift Requests" : view = "uvw_MonitorJobUpliftRequests"; break;
        case "Chase" : view = "uvw_MonitorJobChaseLogged"; break;
        case "ETA not advised to site" : view = "uvw_MonitorJobETANotAdvised"; break;
        case "Notification Window" : view = "uvw_MonitorJobNotificationWindow"; break;
        case "Parked Jobs" : view = "uvw_MonitorHelpdeskJobParked"; break;
        case "Scheduled Call-Backs":
            view = "uvw_MonitorJobScheduledCallbacks";
            break;
        case "Triage" : view = "uvw_MonitorBureauJobTriage"; break;
        case "Deferred jobs" : view = "uvw_MonitorJobDeferred"; break;
        case "Watched Jobs":
            view = "uvw_monitorHelpdeskJobWatched";
            break;
        case "Jobs Pending Review":
            view = "uvw_MonitorJobsPendingReviews"; break;

        case "External Job Exceptions":
            view = "uvw_MonitorExternalJobExceptions"; break;

            // Incidents
        case "Pending To Do" : view = "uvw_MonitorIncidentPendingToDo"; break;
        case "Reviews" : view = "uvw_MonitorIncidentReview"; break;
        case "Follow ups" : view = "uvw_MonitorIncidentFollowUp"; break;
        case "Jobs For Review": view = "uvw_MonitorLinkedIncident"; break;
        case "Upcoming To Do": view = "uvw_MonitorIncidentUpcomingtoDo"; break;
        case "Initial Escalations":
            view = "uvw_MonitorIncidentInitialEscalations";
            break;

            // Below two status are the part of Incidents Pending To Do monitor
        case "Immediate Escalation": view = "ToDo t INNER JOIN Incident i ON t.incidentid = i.id WHERE TypeId = 8 AND ToDoStatusId = 0 "; break;
        case "Incident Telephone Escalation Callback": view = "ToDo t INNER JOIN Incident i ON t.incidentid = i.id WHERE TypeId = 3 AND ToDoStatusId = 0 "; break;

        // Focus
        case "ETA greater than response time" : view = "uvw_MonitorJobEtaExceedsSlaInHours"; break;
        case "ETA Expired" : view = "uvw_MonitorFocusJobETAExpired"; break;
        case "ETA Due To Expire" : view = "uvw_MonitorFocusJobETADueToExpire"; break;
        case "Focus Chase":
            view = "uvw_MonitorFocusJobChase";
            break;
        case "Scheduled Call Back":
            view = "uvw_MonitorFocusJobScheduledCallBack";
            break;
        case "SLA Near/Missed":
            view = "uvw_MonitorFocusJobRepairTargetMissed";
            break;

        case "ETA greater than response/repair time":
            view = "uvw_MonitorFocusJobEtaExceedsSla";
            break;

            // One Stop Shop
        case "Awaiting Quote Request Review To Do":
            view = "uvw_MonitorAwaitingQuoteRequestReviewToDo";
            break;

        case "Awaiting Resource Quote To Do":
            view = "uvw_MonitorAwaitingResourceQuoteToDo";
            break;

        case "Awaiting Resource Quote For Info":
            view = "uvw_monitorAwaitingResourceQuoteForInfo";
            break;

        case "Awaiting Quote Request Review For Info":
            view = "uvw_MonitorAwaitingQuoteRequestReviewForInfo";
            break;

        case "ITQ Awaiting Acceptance To Do":
            view = "uvw_MonitorAwaitingQuoteAcceptanceToDo";
            break;

        case "ITQ Awaiting Acceptance For Info":
            view = "uvw_MonitorAwaitingQuoteAcceptanceForInfo";
            break;

        case "Awaiting Quote Approval For Info":
            view = "uvw_MonitorAwaitingQuoteApprovalForInfo";
            break;

        default: throw new Exception("Unkown view for monitor: " + monitorName);
        }
        return view;
    }

    public int getMonitorCount(String monitorName, String category, String teams, String assetTypes) throws Exception {
        String sql;
        String view = getViewName(monitorName);
        if (category == null && teams == null && assetTypes == null) {
            sql = String.format("SELECT COUNT(*) FROM %s ", view);
        } else {
            teams = String.format("'%s'", teams).replaceAll(",", "', '");
            assetTypes = String.format("'%s'", assetTypes).replaceAll(",", "', '");
            sql = String.format("SELECT COUNT(*) FROM %s WHERE Divisions IN (%s) AND AssetType IN (%s)", view, teams, assetTypes);
        }

        logger.debug("getMonitorCount: " + sql);

        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }

    public List<String> getOrganisation() {
        String sql = "WITH alice AS ( " +
                "SELECT Id, CAST(Name AS NVarChar(MAX)) AS rabbithole " +
                "FROM OrganisationStructure " +
                "WHERE Name IN (SELECT Name FROM Team)  " +
                "UNION ALL " +
                "SELECT hole.id, rabbit.rabbithole + ', ' + hole.name " +
                "FROM OrganisationStructure hole " +
                "JOIN alice rabbit ON rabbit.id = hole.parentID  " +
                ") " +
                "SELECT DISTINCT alice.rabbithole FROM alice";

        logger.debug("getOrganisation: " + sql);
        List<String> rabbithole = jdbc_helpdesk.query(sql, new MapSqlParameterSource(), new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("rabbithole"));
            }
        });
        return rabbithole;
    }

    public Integer getJobReferenceFromMonitor(String monitorName, String clientStatus, String priority) throws Exception {
        String view = getViewName(monitorName);
        String columnName;
        columnName = monitorName.equalsIgnoreCase("Awaiting Quote Approval For Info") ? "JobId"
                : "JobReference";
        String sql = String.format("SELECT TOP(1) uvw.%s FROM %s uvw ", columnName, view);

        sql = clientStatus == null ? sql
                : sql + String.format("INNER JOIN Job j ON j.jobReference = uvw.%s INNER JOIN JobStatus js ON j.JobStatusId = js.Id WHERE js.Name = '%s' ", columnName, clientStatus);

        sql = priority == null ? sql
                : sql + String.format("WHERE FaultPriority = '%s' ", priority);

        sql = sql + "ORDER BY NEWID()";
        logger.debug("getJobReferenceFromMonitor: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

    public Integer getJobReferenceFromMonitorWithResourceStatus(String monitorName, String resourceStatus) throws Exception {
        String view = getViewName(monitorName);

        String sql = "SELECT TOP(1) vw.JobReference FROM %s "
                + " vw INNER JOIN (SELECT j.jobReference FROM Job j "
                + " INNER JOIN ResourceAssignment ra ON j.Id = ra.JobId "
                + " INNER JOIN ResourceAssignmentStatus ras ON ra.ResourceAssignmentStatusId = ras.Id "
                + " WHERE ras.Name = '%s' AND j.JobStatusId != 10 ) sub " //Should not be a cancelled job
                + " ON vw.JobReference = sub.JobReference "
                + " ORDER BY NEWID()";

        sql = String.format(sql, view, resourceStatus);
        logger.debug("getJobReferenceFromMonitor: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

    public Integer getJobReferenceWithNotificationPeriodHasPassed(String monitorName, String notificationPeriodPassed) throws Exception {
        String view = getViewName(monitorName);
        String symbol = notificationPeriodPassed.equals("Yes") ? ">" : "<";
        String sql = "SELECT TOP(1) vw.JobReference FROM %s "
                + " vw INNER JOIN Job j ON vw.JobReference = j.JobReference "
                + " INNER JOIN ResourceAssignment ra ON j.Id = ra.JobId ";

        sql = sql + String.format(" WHERE (ra.DeferralDate IS NULL AND DATEDIFF(mi, DATEADD(mi, 30, ra.CreatedOn), GETUTCDATE()) %s 0 ) ", symbol)
        + " AND j.JobStatusId != 10 " //Should not be a cancelled job
        + " ORDER BY NEWID()";

        sql = String.format(sql, view);
        logger.debug("getJobReferenceWithNotificationPeriodHasPassed: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

    public Integer getOutOfHoursJob() {
        String sql = "SELECT TOP(1) vwd.JobReference FROM uvw_MonitorJobDeferred vwd INNER JOIN Job j ON vwd.JobReference = j.jobReference "
                + " ORDER BY NEWID()";

        logger.debug("get Out Of Hours Job: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

    public Integer getJobsWithActiveDeferralEvents(String status) {

        String sql = "SELECT TOP(1) j.jobReference FROM Job j INNER JOIN ResourceAssignment ra ON j.id = ra.JobId "
                + " INNER JOIN ResourceAssignmentStatus ras ON ras.Id = ra.ResourceAssignmentStatusId "
                + " WHERE j.IsDeferred = 1 "
                + " AND (DATEDIFF(mi, GETUTCDATE(), j.DeferralDate) > 0) "
                + " AND j.JobStatusId NOT IN (9, 10) ";// Job should not be in fixed and cancelled status

        sql = status == null ? sql : String.format(sql + "AND ras.Name = '%s'", status);

        sql = sql + " ORDER BY NEWID()";

        logger.debug("getJobsWithActiveDeferralEvents: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

    public Integer getJobReferenceWithParkedDateAndTimeExpired() {
        String sql = "SELECT TOP(1) JobReference FROM Job WHERE UnParkedDate IS NULL AND JobReference IS NOT NULL "
                + "ORDER BY NEWID()";

        logger.debug("getJobReferenceWithParkedDateAndTimeExpired: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

    public Integer getJobReferenceWithNoResourceIsAssigned() {
        String monitorName = dbHelperTimeZone.isHelpdeskOutOfHours() ? "uvw_MonitorJobAwaitingAssignment" : "uvw_MonitorHelpdeskJobAwaitingAssignment";
        
        String sql = " SELECT TOP(1) maa.JobReference FROM %s maa "
                + "INNER JOIN Job j ON maa.JobReference = j.JobReference LEFT JOIN ResourceAssignment ra ON "
                + "j.Id = ra.JobId WHERE ra.JobId IS NULL "
                + "ORDER BY NEWID()";

        sql = String.format(sql, monitorName);
        
        logger.debug("getJobReferenceWithNoResourceIsAssigned: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

    /**
     *
     * @param resourceAssignmentStatus
     * @param requestedProfileId: 0 = dont care, -1 = resource profile can't be mapped to site, n = exact resource profile id
     * @param declineCount
     * @return
     */
    public Integer getJobReferenceWithDeclinedJob(String resourceAssignmentStatus, int requestedProfileId, int declineCount) {
        String monitorName = dbHelperTimeZone.isHelpdeskOutOfHours() ? "uvw_MonitorJobAwaitingAssignment" : "uvw_MonitorHelpdeskJobAwaitingAssignment";
        
        String sql = "SELECT TOP(1) mjaa.JobReference FROM %s mjaa "
                + "LEFT JOIN Job j ON mjaa.JobReference = j.JobReference "
                + "LEFT JOIN ResourceAssignment ra ON j.Id = ra.JobId "
                + "LEFT JOIN ResourceAssignmentStatus ras ON ras.Id = ra.ResourceAssignmentStatusId "
                + "LEFT JOIN AdditionalResourceRequirement arr ON arr.JobId = j.Id "
                + "WHERE ras.Name = 'Declined' ";
        
        sql = String.format(sql, monitorName);

        sql = resourceAssignmentStatus.equals("single") ? sql + "AND j.Id NOT IN (SELECT jobId FROM ResourceAssignment GROUP BY JobId HAVING COUNT(*) > 1) " : sql;

        if (requestedProfileId == -1) {
            String siteToResourceProfileIds = "(SELECT rp.Id FROM ResourceProfileSiteTypeMapping rpstm INNER JOIN ResourceProfile rp ON rp.Id = rpstm.ResourceProfileId INNER JOIN Site s ON s.SiteTypeId = rpstm.SiteTypeId WHERE s.Id = j.SiteId) ";
            sql = sql + "AND arr.RequestedResourceProfileId NOT IN " +  siteToResourceProfileIds; // not a mapped resource profile
            sql = sql + "AND arr.RequestedResourceProfileId != 115 ";   // not contractor

        } else if (requestedProfileId != 0) {
            sql = sql + "AND arr.RequestedResourceProfileId = :requestedProfileId "; // declined job for specified resource profile
        }

        sql = declineCount == 0 ? sql : sql + String.format("AND j.Id IN (SELECT JobId FROM ResourceAssignment WHERE ResourceAssignmentStatusId = 16 GROUP BY JobId HAVING COUNT(*) = %d) ", declineCount);

        sql = sql + "ORDER BY NEWID()";

        logger.debug("getJobReferenceWithDeclinedJob: " + sql.replace(":requestedProfileId", String.valueOf(requestedProfileId)));

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("requestedProfileId", String.valueOf(requestedProfileId));

        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

    public Integer getJobReferenceWhichHasBecomeUnparked() {
        
        String monitorName = dbHelperTimeZone.isHelpdeskOutOfHours() ? "uvw_MonitorJobAwaitingAssignment" : "uvw_MonitorHelpdeskJobAwaitingAssignment";
        
        String sql = " SELECT TOP(1) mjaa.jobReference FROM %s mjaa  "
                + "INNER JOIN JOB j ON mjaa.JobReference =j.jobReference "
                + "INNER JOIN JobPark jp ON jp.jobId = j.id "
                + "WHERE jp.UnParkDate IS NOT NULL "
                + "AND j.JobStatusId != 10 " // Not job with cancelled status
                + "ORDER BY NEWID() ";
        
        sql = String.format(sql, monitorName);

        logger.debug("getJobReferenceWhichHasBecomeUnparked: " + sql);

        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

    public Integer getJobReferenceWithScheduleCallBackTimeExpired() {
        String sql = " SELECT TOP(1) jobreference FROM Job j INNER JOIN ResourceAssignment ra ON j.id = ra.JobId "
                + "WHERE DATEDIFF(mi, GETUTCDATE(), CallbackDueAt) < 0 "
                + "AND j.JobStatusId != 10 "
                + "ORDER BY NEWID() ";

        logger.debug("getJobReferenceWithScheduleCallBackTimeExpired: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

    public Integer getJobReferenceWhereReopenedAndNoResourceAssigned() {
        
        String monitorName = dbHelperTimeZone.isHelpdeskOutOfHours() ? "uvw_MonitorJobAwaitingAssignment" : "uvw_MonitorHelpdeskJobAwaitingAssignment";
        String sql = "SELECT TOP(1) j.JobReference FROM Job j "
                + "INNER JOIN %s uvw ON uvw.JobReference = j.JobReference "
                + "INNER JOIN JobTimelineEvent jte ON j.Id = jte.JobId "
                + "INNER JOIN JobEventType jet ON jte.JobEventTypeId = jet.Id "
                + "WHERE jet.Name = 'Reopened' "
                + "ORDER BY NEWID() ";

        sql = String.format(sql, monitorName);
        logger.debug("getJobReferenceWhereReopenedAndNoResourceAssigned: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

    public Integer getIncidentReferenceNumberFromMonitor(String monitorName, String status) throws Exception {
        String view = getViewName(monitorName);

        String column = monitorName.equals("Jobs For Review") ? "JobNo" : "IncidentReference";
        String sql = String.format("SELECT TOP(1) %s FROM %s", column, view);

        sql = status == null ? sql : sql + String.format(" WHERE ToDoType = '%s'", status);
        sql = sql + " ORDER BY NEWID() ";

        logger.debug("getReferenceNumberFromMonitor: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

    public Integer getJobReferenceNotBeingWatched() {
        String sql = "SELECT TOP(1) j.jobReference FROM Job j "
                + "LEFT JOIN uvw_monitorHelpdeskJobWatched wj ON j.jobReference = wj.jobReference "
                + "WHERE wj.jobReference IS NULL ORDER BY NEWID() ";

        logger.debug("getJobReferenceNotBeingWatched: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

    public String getSiteWithAllFaultPriorities(String monitorName) throws Exception {
        String view = getViewName(monitorName);

        String sql = String.format(
                "WITH sub AS (SELECT DISTINCT Site, FaultPriority "
                        + "FROM %s WHERE Site NOT IN (SELECT Site FROM %s WHERE ResponseOrRepairDue IS NULL)) "
                        + "SELECT TOP (1) Site FROM sub "
                        + "GROUP BY Site "
                        + "HAVING COUNT(FaultPriority) = 3 "
                        + "ORDER BY NEWID() ",
                        view, view);

        logger.debug("getFaultPrioritiesFromMonitor: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

    public List<String> getAllFaultPriorities(String monitor, String site) throws Exception {
        String view = getViewName(monitor);

        String sql = String.format("SELECT DISTINCT FaultPriority FROM %s WHERE Site = '%s'", view, site);

        MapSqlParameterSource params = new MapSqlParameterSource();

        List<String> faultPriorities = jdbc_helpdesk.query(sql, params, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("FaultPriority"));
            }
        });
        return faultPriorities;
    }

    public String getSiteWithFaultPriority(String monitor) throws Exception {
        String view = getViewName(monitor);

        String sql = String.format("SELECT TOP(1) Site FROM %s WHERE Site NOT IN (SELECT Site FROM %s WHERE ResponseOrRepairDue IS NULL) ORDER BY NEWID() ", view, view);

        logger.debug("getSiteWithFaultPriority: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getMonitorSettingsDetails(String monitorDisplayName, String areaDisplayName) {

        String sql = String.format(
                "SELECT mcg.Id, ma.DisplayName AS AreaDisplayName, mcg.DisplayName AS CategoryDisplayName, mcg.Active, mcr.DisplayName, mcg.ExportName FROM MonitorConfig mcg "
                        + "INNER JOIN MonitorCategory mcr ON mcg.CategoryId = mcr.Id "
                        + "INNER JOIN MonitorArea ma ON ma.Id = mcr.AreaId "
                        + "WHERE mcg.DisplayName = '%s' AND ma.DisplayName = '%s' ", monitorDisplayName, areaDisplayName);

        logger.debug("getMonitorSettingsDetails: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            return jdbc_helpdesk.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

    public List<String> getActiveMonitors(String area, String category){
        String sql = "SELECT mconf.DisplayName FROM MonitorConfig mconf "
                + "INNER JOIN MonitorCategory mcat ON mcat.Id = mconf.CategoryId "
                + "INNER JOIN MonitorArea ma ON mcat.AreaId = ma.Id "
                + "WHERE mconf.Active = 1 "
                + "AND ma.DisplayName = '%s' "
                + "AND mcat.DisplayName = '%s' "
                + "AND mconf.DisplayName != ' ' ";
        sql = String.format(sql, area, category);

        logger.debug("getActiveMonitors: " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), String.class);
    }

    public boolean isMonitorActive(String displayName, String entityType){
        String sql = "SELECT COUNT(Id) FROM MonitorConfig WHERE Active = 1 "
                + "AND DisplayName = '%s' "
                + "AND EntityType = '%s' ";
        sql = String.format(sql, displayName, entityType);

        logger.debug("isMonitorActive: " + sql);
        int result = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        logger.debug("isMonitorActive result: " + result);
        return result > 0;
    }

    public int countJobsOnMonitor(String monitor, String ids) {
        String sql = String.format("SELECT COUNT(DISTINCT(Id)) FROM %s WHERE Id IN (%s)", monitor, ids);
        logger.debug("countJobsOnMonitor: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public List<Map<String, Object>> getJobsAwaitingAssignment() {
        String sql = String.format("SELECT * FROM uvw_MonitorJobAwaitingAssignment");
        logger.debug("getJobsAwaitingAssignment: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

    public List<String> getMonitornames(String monitor, String section) throws Exception {
        String sql = "SELECT mcc.DisplayName FROM MonitorArea ma INNER JOIN MonitorCategory mc ON ma.id = mc.AreaId " +
                "INNER JOIN MonitorConfig mcc ON mc.Id = mcc.CategoryId " +
                "WHERE ma.DisplayName = '%s' " +
                "AND mc.DisplayName = '%s' " +
                "AND Active = 1 " +
                "AND mcc.DisplayName != ' '";

        sql = String.format(sql, monitor, section);
        logger.debug("getMonitornames: " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), String.class);
    }

    public List<String> getMonitorSectionNames(String monitor) throws Exception {
        String sql = "SELECT DISTINCT mc.DisplayName FROM MonitorArea ma INNER JOIN MonitorCategory mc ON ma.id = mc.AreaId " +
                "INNER JOIN MonitorConfig mcc ON mc.Id = mcc.CategoryId " +
                "WHERE ma.DisplayName = '%s' " +
                "AND Active = 1 " +
                "AND mcc.DisplayName != ' ' ";

        sql = String.format(sql, monitor);
        logger.debug("getMonitorSectionNames: " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), String.class);
    }

    public List<Map<String, Object>> getMonitorAreas() {
        String sql = "select Id, DisplayName from MonitorArea";
        logger.debug("getMonitorAreas: " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getMonitorConfigs() {
        String sql = "select Id, DisplayName from MonitorConfig";
        logger.debug("getMonitorConfigs: " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
    }
    
    public Integer getInHoursJobReferenceWithPriority(String priority) {
        String sql = "SELECT JobReference FROM uvw_MonitorHelpdeskJobAwaitingAssignment maa " + 
                " WHERE maa.FaultPriority = %s";

        sql = String.format(sql, priority);
        
        logger.debug("getInHoursJobReferenceWithPriority: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

}