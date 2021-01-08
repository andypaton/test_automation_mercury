package mercury.helpers.dbhelper;
import static mercury.helpers.Globalisation.LOCALE;
import static mercury.helpers.Globalisation.MEDIUM_DATE;

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
import mercury.helpers.DbUtilityHelper;
import mercury.helpers.PropertyHelper;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DbConfigV2.class)
public class DbHelperIncidents {

    @Autowired private DbUtilityHelper dbUtilityHelper;
    @Autowired private PropertyHelper propertyHelper;

    private static final Logger logger = LogManager.getLogger();

    private static final String INCIDENT_REFERENCE_SQL = "SELECT TOP(1) i.IncidentReference FROM Incident i "
            + "INNER JOIN IncidentStatus ins ON i.IncidentStatusId = ins.Id "
            + "INNER JOIN IncidentAnswerSet ias ON ias.IncidentId = i.Id "
            + "INNER JOIN IncidentAnswer ia ON ia.IncidentAnswerSetId = ias.Id  "
            + "INNER JOIN Site s ON s.Id = i.SiteId "
            + "LEFT JOIN IncidentLock il ON i.Id = il.IncidentId ";

    private static final String UNLOCKED_INCIDENT_REFERENCE_SQL = "WHERE ins.Name != 'Canceled' " + "AND il.IncidentId IS NULL "
            + "AND i.IncidentStatusId != 0 ";

    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;

    public Integer getRandomIncidentReferenceNumber() {
        List<Integer> incidents = getRandomIncidentReferences(1);
        if (incidents.size() > 0) {
            return incidents.get(0);
        } else {
            return null;
        }
    }

    public List<Integer> getRandomIncidentReferences(int count) {
        String sql = "SELECT TOP(%d) i.IncidentReference FROM Incident i "
                + "INNER JOIN IncidentStatus ins ON i.IncidentStatusId = ins.Id "
                + "INNER JOIN Site s ON s.Id = i.SiteId "
                + "LEFT JOIN IncidentLock il ON i.Id = il.IncidentId "
                + "WHERE ins.Name = 'Logged' "
                + "AND il.IncidentId IS NULL "
                + "AND i.IncidentStatusId != 0 "
                + "ORDER BY NEWID()";

        sql = String.format(sql, count);

        logger.debug("getRandomIncidentReferences : " + sql);
        List<Integer> incidents = jdbc_helpdesk.query(sql, new MapSqlParameterSource(), new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getInt("IncidentReference"));
            }
        });
        return incidents;
    }

    public Integer getRandomIncidentReferenceNumberWithNote() {
        String sql = "SELECT TOP(1) i.IncidentReference FROM Incident i "
                + "INNER JOIN IncidentStatus ins ON i.IncidentStatusId = ins.Id "
                + "INNER JOIN dbo.IncidentUpdateNote note ON i.id = note.IncidentId "
                + "INNER JOIN Site s ON s.Id = i.SiteId "
                + "INNER JOIN IncidentUpdateNoteType unt ON note.IncidentUpdateNoteTypeId = unt.Id "
                + "LEFT JOIN IncidentLock il ON i.Id = il.IncidentId "
                + "WHERE ins.Name != 'Canceled' "
                + "AND il.IncidentId IS NULL "
                + "AND i.IncidentStatusId != 0 "
                + "AND unt.Name = 'Incident update' ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    public Integer getRandomIncidentReferenceNumberWithSiteClosedAndNoEscalationCriteria() {
        String sql = INCIDENT_REFERENCE_SQL
                + " LEFT JOIN escalation e ON ia.QuestionId = e.IncidentQuestionID "
                + UNLOCKED_INCIDENT_REFERENCE_SQL
                + "AND i.SiteClosed = 1 "
                + "AND i.SiteReopened = 0 "
                + "AND ia.AnswerOptionId != e.AnswerGiven "
                + "AND i.CallerId IS NOT NULL "
                + "AND i.id IN "
                + "(SELECT IncidentId "
                + "FROM dbo.IncidentTimelineEvent ite JOIN "
                + "IncidentEventType iet ON ite.IncidentEventTypeId = iet.Id "
                + "WHERE iet.Name = 'Reviewed') ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    public Integer getRandomIncidentReferenceNumberWithLinkedJob() {
        String sql = INCIDENT_REFERENCE_SQL + UNLOCKED_INCIDENT_REFERENCE_SQL
                + "AND i.id IN "
                + "(SELECT IncidentId "
                + "FROM dbo.JobLinkedIncident "
                + "WHERE Active = 1) "
                + "ORDER BY NEWID() ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    public Integer getJobReferenceNumber(int incidentReferenceNumber) {
        String sql = "SELECT TOP(1) j.jobreference "
                + "FROM job j INNER JOIN joblinkedincident jli ON j.id =jli.jobid "
                + "INNER JOIN incident i ON jli.incidentid = i.id "
                + "INNER JOIN Site s ON s.Id = i.SiteId "
                + "WHERE incidentreference = :incidentReferenceNumber ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("incidentReferenceNumber", incidentReferenceNumber);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    public Integer getRandomIncidentReferenceNumberFollowUpWithNextFollowupRequiredCriteria(String incidentType) {
        String sql = "SELECT TOP(1) i.IncidentReference FROM Incident i "
                + "INNER JOIN dbo.uvw_MonitorIncidentFollowUp inf ON i.IncidentReference = inf.IncidentReference "
                + "INNER JOIN Site s ON s.Id = i.SiteId  "
                + "LEFT JOIN IncidentLock il ON i.Id = il.IncidentId "
                + "WHERE i.IncidentStatusId != 0 "
                + "AND inf.IncidentTypes = :incidentType "
                + "AND il.IncidentId IS NULL "
                + "AND i.Id NOT IN "
                + "(SELECT t.IncidentId "
                + "FROM ToDo t INNER JOIN TODoType tt ON t.TypeId = tt.Id "
                + "WHERE tt.Name = 'Incident Telephone Escalation Callback') "
                + "AND i.IncidentReference IN "
                + "(SELECT i.IncidentReference "
                + "FROM IncidentFollowUpAnswer ifa INNER JOIN IncidentFollowUp ifu ON ifa.IncidentFollowUpId = ifu.Id "
                + "INNER JOIN Incident i ON ifu.IncidentId = i.Id "
                + "WHERE IncidentFollowUpId IN "
                + "(SELECT MAX(Id) "
                + "FROM IncidentFollowUp "
                + "GROUP BY IncidentId "
                + "HAVING COUNT(*) >= 1) "
                + "AND ifa.QuestionText LIKE '%still ongoing%' "
                + "AND AnswerText = 'Yes') "
                + "ORDER BY NEWID() ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("incidentType", incidentType);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    public Integer getIncidentReferenceNumberInitialReviewWithNoEscalation(String incidentType) {
        String sql = "SELECT TOP(1) IncidentReference "
                + "FROM Incident WHERE Id IN ( "
                + "SELECT td.IncidentId FROM Incident i "
                + "INNER JOIN dbo.uvw_MonitorIncidentReview inr ON i.IncidentReference = inr.IncidentReference "
                + "INNER JOIN Site s ON S.Id = i.SiteId "
                + "INNER JOIN toDo td ON i.id = td.IncidentId "
                + "LEFT JOIN IncidentLock il ON i.Id = il.IncidentId "
                + "WHERE il.IncidentId IS NULL "
                + "AND inr.IncidentTypes = :incidentType "
                + "GROUP BY td.IncidentId "
                + "HAVING COUNT(*) < 3) "
                + "ORDER BY NEWID()";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("incidentType", incidentType);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    public Integer getIncidentReferenceNumberEscalationCriteria(String incidentType) {
        String baseSql = "SELECT TOP(1) i.incidentReference "
                + "FROM incident i INNER JOIN incidentanswerset ias ON i.id = ias.incidentid  "
                + "INNER JOIN incidentanswer ia ON ias.id = ia.incidentanswersetid "
                + "INNER JOIN Site s ON s.Id = i.SiteId "
                + "LEFT JOIN escalation e ON ia.answeroptionid = e.answergiven "
                + "LEFT JOIN IncidentLock il ON i.Id = il.IncidentId "
                + "WHERE il.IncidentId IS NULL "
                + "AND ia.questionid = e.incidentquestionid "
                + "AND i.incidentReference IN " ;
        String sqlReview = "(SELECT incidentReference FROM uvw_MonitorIncidentReview ";
        String sqlIncidentType = "WHERE IncidentTypes = :incidentType";
        String newId = " ) ORDER BY NEWID()";
        String sql = incidentType == null ? baseSql + sqlReview + newId : baseSql + sqlReview + sqlIncidentType + newId;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("incidentType", incidentType);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    public void unlockIncident(int incidentReference) {
        String sql = "DELETE IncidentLock "
                + "WHERE IncidentId IN "
                + "(SELECT Id FROM Incident "
                + "WHERE IncidentReference = :incidentReference)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("incidentReference", incidentReference);
        logger.debug("unlockIncident: " + sql);
        jdbc_helpdesk.update(sql, params);
    }

    public Integer getRandomIncidentReferenceNumberWithStatusTelephoneEscalationCallback() {
        String sql = "SELECT TOP(1) i.IncidentReference FROM Incident i "
                + "INNER JOIN ToDo td ON td.IncidentId = i.Id "
                + "INNER JOIN ToDoType tdt ON tdt.Id = td.TypeId "
                + "INNER JOIN IncidentTimelineEvent itm ON i.Id = itm.IncidentId  "
                + "INNER JOIN Site s ON s.Id = i.SiteId "
                + "LEFT JOIN IncidentLock il ON i.Id = il.IncidentId "
                + "WHERE tdt.Name = 'Incident Telephone Escalation Callback' "
                + "AND il.IncidentId IS NULL "
                + "AND i.IncidentStatusId != 0 "
                + "AND itm.Title != 'Incident reviewed' "
                + "AND td.ToDoStatusId = 0 "
                + "AND i.Id NOT IN "
                + "(SELECT sub.IncidentId "
                + "FROM ( SELECT IncidentId,COUNT(TypeId) "
                + "OVER (PARTITION BY IncidentId) AS Count "
                + "FROM ToDo WHERE TypeId IN "
                + "(SELECT Id FROM ToDoType "
                + "WHERE Name IN ('Incident Followup','Incident Telephone Escalation Callback') )) sub "
                + "GROUP BY IncidentId HAVING COUNT(*) > 1) "
                + "ORDER BY NEWID() ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    public String getSiteNameForIncidentReferenceNumber(int incidentReference) {
        String sql = "SELECT s.Name "
                + "FROM incident i INNER JOIN Site s ON i.siteId = s.Id "
                + "WHERE IncidentReference = :incidentReference";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("incidentReference", incidentReference);
        try {
            String siteCode = jdbc_helpdesk.queryForObject(sql, params, String.class);
            return siteCode;
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    public Boolean isIncidentTypeActive(String incidentType) {
        String sql = "SELECT IsActiveForLogging "
                + "FROM Incidenttype "
                + "WHERE description = :incidentType";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("incidentType", incidentType);
        return jdbc_helpdesk.queryForObject(sql, params, Boolean.class);
    }

    public Integer getRandomIncidentReferenceNumberWithStatusInitialReviewAndFollowUp(String incidentType) {
        String sql = "SELECT TOP(1) i.IncidentReference FROM Incident i "
                + "INNER JOIN dbo.uvw_MonitorIncidentFollowUp inf ON i.IncidentReference = inf.IncidentReference "
                + "INNER JOIN Site s ON s.Id = i.SiteId "
                + "LEFT JOIN IncidentLock il ON i.Id = il.IncidentId "
                + "WHERE i.IncidentStatusId != 0 "
                + "AND inf.IncidentTypes = :incidentType "
                + "AND il.IncidentId IS NULL "
                + "AND i.Id IN "
                + "(SELECT sub.IncidentId FROM "
                + "(SELECT IncidentId,COUNT(TypeId) "
                + "OVER (PARTITION BY IncidentId) AS Count "
                + "FROM ToDo "
                + "WHERE TypeId IN "
                + "(SELECT Id FROM ToDoType "
                + "WHERE Name IN ('Incident Initial Review','Incident Followup') ) "
                + "AND ToDoStatusId = 0) sub "
                + "GROUP BY IncidentId HAVING COUNT(*) > 1) "
                + "AND inf.Deadline < SYSDATETIMEOFFSET() ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("incidentType", incidentType);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    public String getIncidentQuestion(int incidentReference) {
        final String sql = "SELECT TOP(1) ia.QuestionText FROM  "
                + "IncidentAnswer ia JOIN IncidentAnswerSet ias ON ia.IncidentAnswerSetId = ias.Id "
                + "JOIN Incident i ON i.id = ias.IncidentId "
                + "WHERE i.incidentReference = :incidentReference "
                + "ORDER BY QuestionId ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("incidentReference", incidentReference);

        logger.debug("getIncidentQuestion: " + sql.replace(":incidentReference", String.valueOf(incidentReference)));

        String question = jdbc_helpdesk.queryForObject(sql, params, String.class);
        return question;
    }

    public String getIncidentTopQuestion(String incidentType) {
        final String sql = "SELECT  iqs.QuestionText "
                + "FROM incidenttype it "
                + "INNER JOIN IncidentQuestionSet iqs "
                + "ON it.id = iqs.IncidentTypeFk "
                + "WHERE it.Description = :incidentType "
                + "AND QuesSeq = 1 "
                + "AND it.IsActiveForLogging = 1 ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("incidentType", incidentType);
        String question = jdbc_helpdesk.queryForObject(sql, params, String.class);
        return question;
    }

    public String getRandomIncidentType() {
        String sql = "SELECT TOP(1) Description FROM IncidentType "
                + "WHERE IsActiveForLogging = 1 ORDER BY NEWID()";
        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public Integer getRandomIncidentReferenceNumberWithEscalationContact(String incidentType) {
        String sql
        = "SELECT TOP(1) i.incidentReference FROM incident i INNER JOIN incidentanswerset ias ON i.id = ias.incidentid  "
                + "INNER JOIN incidentanswer ia ON ias.id = ia.incidentanswersetid "
                + "INNER JOIN Site s ON s.Id = i.SiteId "
                + "INNER JOIN IncidentIncidentType iit ON iit.IncidentId = i.Id "
                + "INNER JOIN IncidentType it ON it.Id = iit.IncidentTypeId "
                + "INNER JOIN EscalationHistory eh ON i.Id = eh.IncidentId "
                + "LEFT JOIN  EscalationAddition ea ON ea.EscalationHistoryId = eh.Id "
                + "LEFT JOIN uvw_MonitorIncidentReview mir ON mir.IncidentReference = i.IncidentReference "
                + "LEFT JOIN escalation e ON ia.answeroptionid = e.answergiven "
                + "LEFT JOIN IncidentLock il ON i.Id = il.IncidentId "
                + "WHERE il.IncidentId IS NULL "
                + "AND ea.EscalationHistoryId IS NULL "
                + "AND ia.questionid = e.incidentquestionid "
                + "AND mir.IncidentReference IS NULL "
                + "AND i.IncidentStatusId != 2 "
                + "AND i.IncidentReference IN "
                + "(SELECT i.IncidentReference FROM incident i "
                + "INNER JOIN EscalationHistory eh ON i.Id = eh.IncidentId "
                + "LEFT JOIN EscalationHistoryContact ehc ON ehc.EscalationHistoryId = eh.Id "
                + "WHERE %s = 1) "
                + "AND eh.CreatedOn > (SELECT DATEADD(day, -30, getdate())) ";

        sql = incidentType == null ? sql
                : sql + "AND it.Description = :incidentType ";
        
        String contactMethod = propertyHelper.getMercuryUrl().contains("ukrb") ? "IsSmsNotification" : "IsEmailNotification";
        sql = String.format(sql, contactMethod);
        
        sql = sql + "ORDER BY NEWID()";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("incidentType", incidentType);
        try {
            logger.debug("getRandomIncidentReferenceNumberWithEscalationContact: " + sql.replaceAll(":incidentType", incidentType));
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getEscalationContactName(int incidentReference) {
        String sql = "SELECT TOP(1) ehc.Name "
                + "FROM Incident i INNER JOIN EscalationHistory eh ON i.Id = eh.IncidentId "
                + "INNER JOIN EscalationHistoryContact ehc ON ehc.EscalationHistoryId = eh.Id "
                + "WHERE ehc.%s = 1 "
                + "AND i.IncidentReference = :incidentReference "
                + "ORDER BY NEWID()";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("incidentReference", incidentReference);
        
        String contactMethod = propertyHelper.getMercuryUrl().contains("ukrb") ? "IsSmsNotification" : "IsEmailNotification";
        sql = String.format(sql, contactMethod);
        logger.debug(
                "getEscalationContactName for incident " + incidentReference
                + ": "
                + sql);
        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public int getEmailNotificationStatus(String name, int incidentReference) {
        String sql = "SELECT TOP(1) ehc.%s "
                + "FROM Incident i INNER JOIN EscalationHistory eh ON i.Id = eh.IncidentId "
                + "INNER JOIN EscalationHistoryContact ehc ON ehc.EscalationHistoryId = eh.Id "
                + "WHERE i.IncidentReference = :incidentReference "
                + "AND ehc.Name = :name "
                + "ORDER BY ehc.escalationHistoryId DESC ";
        
        String contactMethod = propertyHelper.getMercuryUrl().contains("ukrb") ? "IsSmsNotification" : "IsEmailNotification";
        sql = String.format(sql, contactMethod);
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("incidentReference", incidentReference);
        params.addValue("name", name);
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public List<String> getEscalationContactNames(int incidentReference) {
        String sql = "SELECT Distinct ehc.Name "
                + "FROM Incident i INNER JOIN EscalationHistory eh ON i.Id = eh.IncidentId "
                + "INNER JOIN EscalationHistoryContact ehc ON ehc.EscalationHistoryId = eh.Id "
                + "WHERE i.IncidentReference = :incidentReference";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("incidentReference", incidentReference);

        List<String> escalationContacts = jdbc_helpdesk.query(sql, params, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("Name"));
            }
        });
        return escalationContacts;
    }

    public int getCountOfLoggedIncidentsForSiteInLast30Days(int siteId) {
        String sql = "SELECT COUNT(1) FROM Incident WHERE SiteId = :siteId AND IncidentStatusId = 1 AND CreatedOn BETWEEN DATEADD(DAY, -30, GETUTCDATE()) AND GETUTCDATE()";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("siteId", siteId);
        logger.debug("getCountOfIncidentsLoggedForSiteWithIn30Days " + sql);
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public List<Map<String, Object>> getLoggedIncidentsForSiteInLast30Days(int siteId) {
        String sql = "SELECT DISTINCT i.IncidentReference AS Reference, s.Name AS Site,"
                + " STUFF( (SELECT ', ' + it.Description FROM IncidentType it"
                + " JOIN IncidentIncidentType iit ON i.Id = iit.IncidentId"
                + " WHERE iit.IncidentTypeId = it.Id"
                + " ORDER BY it.Description"
                + " FOR XML PATH ('')"
                + " ), 1, 2, ''"
                + " ) AS IncidentTypes,"
                + " i.Description, FORMAT(i.CreatedOn,'"
                + MEDIUM_DATE + "', '" + LOCALE + "' ) AS Logged"
                + " FROM Incident i"
                + " JOIN Site s ON i.SiteId = s.Id"
                + " JOIN IncidentIncidentType iit ON i.Id = iit.IncidentId"
                + " JOIN IncidentType it ON iit.IncidentTypeId = it.Id"
                + " JOIN IncidentStatus ins ON i.IncidentStatusId = ins.Id"
                + " WHERE s.Id = :siteId"
                + " AND ins.Name = 'Logged'"
                + " AND i.CreatedOn BETWEEN DATEADD(DAY, -30, GETUTCDATE()) AND GETUTCDATE()";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("siteId", siteId);

        logger.debug("getLoggedIncidentsForSiteInLast30Days: " + sql.replace(":siteId", String.valueOf(siteId)));
        try {
            return jdbc_helpdesk.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getRandomReviewedIncidentWithNoEscalationNoFollowUpAndWithLoggedStatus() {
        String sql = "SELECT TOP(1) i.IncidentReference FROM Incident i " +
                "INNER JOIN IncidentStatus ins ON i.IncidentStatusId = ins.Id " +
                "INNER JOIN dbo.IncidentTimelineEvent te ON i.Id = te.IncidentId " +
                "INNER JOIN IncidentAnswerSet ias ON ias.IncidentId = i.Id " +
                "INNER JOIN IncidentAnswer ia ON ia.IncidentAnswerSetId = ias.id " +
                "INNER JOIN Site s ON s.Id = i.SiteId " +
                "LEFT JOIN uvw_MonitorIncidentFollowUp uvf ON uvf.IncidentReference = i.IncidentReference " +
                "LEFT JOIN escalation e ON ia.QuestionId = e.IncidentQuestionID " +
                "WHERE ins.Name != 'Canceled' AND ia.AnswerOptionId != e.AnswerGiven AND te.IncidentEventTypeId = 3 " +
                "AND uvf.IncidentReference IS NULL " +
                "AND i.Id NOT IN (SELECT t.IncidentId FROM ToDo t INNER JOIN Incident i ON t.incidentid = i.id WHERE TypeId IN (3, 8) AND ToDoStatusId = 0 ) " +
                "ORDER BY NEWID()";

        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getIncidentReferenceWithStatusId(int statusId) {
        String sql = "SELECT TOP(1) IncidentReference FROM incident i "
                + "LEFT JOIN IncidentStatus ist ON i.IncidentStatusId = ist.Id "
                + "LEFT JOIN IncidentLock il ON il.IncidentId = i.Id "
                + "WHERE ist.Id = :statusId "
                + "AND il.IncidentId IS NULL "
                + "ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("statusId", statusId);

        logger.debug("getIncidentReferenceWithStatus: " + sql.replace(":statusId", String.valueOf(statusId)));

        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * get incident Id's for deletion
     * @param keepCount : the number of incidents per ToDoType to keep
     * @return
     */
    public List<Integer> getIncidentsForCancellation(int keepCount, String monitor){
        String sql = "SELECT Id FROM ( "
                + "SELECT Id, ROW_NUMBER() OVER (ORDER BY Id DESC) AS RowNum FROM (SELECT DISTINCT(Id) FROM %s) As TableA "
                + ") AS TableB WHERE TableB.RowNum > %d";
        sql = String.format(sql,  monitor, keepCount);
        logger.debug("getIncidentsForCancellation: " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), Integer.class);
    }

    public int getIncidentTypeId(String name) {
        String sql = String.format("SELECT Id FROM IncidentType WHERE Description = '%s'", name);
        logger.debug("getIncidentTypeId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public List<String> getAllIncidentTypes() {
        String sql = "SELECT Description FROM incidentType WHERE IsActiveForLogging = 1";
        MapSqlParameterSource params = new MapSqlParameterSource();

        List<String> incidentTypes = jdbc_helpdesk.query(sql, params, new RowMapper<String>() {
            @Override public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("Description"));
            }
        });
        return incidentTypes;
    }

    public String getRandomLinkedIncidentType() {
        String sql = "SELECT TOP(1) Name AS LinkedIncidentType"
                + " FROM LinkedIncidentCriterionType"
                + " ORDER BY NEWID()";

        logger.debug("getRandomLinkedIncidentType: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public boolean isThereAnInactiveIncident() {
        String sql = "SELECT COUNT(1) FROM LinkedIncidentCriterion WHERE Active = 0";
        logger.debug("isThereAnInactiveIncident: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            int count = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return count >= 1;

        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return false;
        }
    }

    public Integer getRandomLinkedIncidentCriterionTypeId() {
        String sql = "SELECT TOP(1) Id FROM LinkedIncidentCriterionType ORDER BY NEWID()";
        logger.debug("getRandomLinkedIncidentCriterionTypeId: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getRandomIncidentTypeId() {
        String sql = "SELECT TOP(1) Id FROM IncidentType ORDER BY NEWID()";
        logger.debug("getRandomIncidentTypeId: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void insertLinkedIncidentCriterion(Integer linkedIncidentCriterionTypeId, Integer brandId, Integer siteTypeId, Integer assetSubTypeId, Integer assetClassificationId, Integer faultTypeId,
            Integer incidentTypeId, Integer active) {
        String sql = "INSERT INTO LinkedIncidentCriterion  (LinkedIncidentCriterionTypeId, BrandId, SiteTypeId, AssetSubTypeId, AssetClassificationId, FaultTypeId, IncidentTypeId, CreatedOn, CreatedBy, UpdatedOn, UpdatedBy, Active)"
                + " VALUES (%d, %d, %d, %d, %d, %d, %d, GETUTCDATE(), '11111111-1111-1111-1111-111111111113', GETUTCDATE(), '11111111-1111-1111-1111-111111111113', %d)";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, linkedIncidentCriterionTypeId, brandId, siteTypeId, assetSubTypeId, assetClassificationId, faultTypeId, incidentTypeId, active);
        logger.debug("insertLinkedIncidentCriterion: " + sql);
        try {
            jdbc_helpdesk.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Exception " + e.getMessage());
        }
    }
}
