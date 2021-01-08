package mercury.helpers.dbhelper;

import static mercury.helpers.Globalisation.CURRENCY_SYMBOL;
import static mercury.helpers.StringHelper.quote;

import java.math.BigDecimal;
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

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfigV2.class)
public class DbHelperQuotes {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private NamedParameterJdbcTemplate jdbc_portal;
    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;
    @Autowired private DbUtilityHelper dbUtilityHelper;

    public Map<String, Object> getMultiQuotesAwaitingQuoteForJobReference(int jobReference) {
        String sql = "SELECT TOP(1) FaultId, Store, LoggedDate, ScopeOfWorks, Location, SubTypeClassification, ApprovalStatusName, QuotePriority, NumberOfQuotesRequired, 'Edit Job' [Action] "
                + ", (SELECT SUM(CASE WHEN ApprovalStatusName IN ('AwaitingQuote', 'ItqAwaitingAcceptance', 'AwaitingResourceAssignment', 'QueryResourcePending') THEN 0 ELSE 1 END) FROM %portaldb.vw_QuoteApprovalScenarios qas WHERE FaultId = %d) AS [NumQuotes] "
                + "FROM %portaldb.vw_QuoteApprovalScenarios qas "
                + "WHERE IsMultiQuote = 1 AND FaultId = %d "
                + "AND qas.QuoteJobApprovalStatusId NOT IN (3, 7, 10, 12)";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, jobReference, jobReference);

        logger.debug("getMultiQuotesAwaitingQuoteForJobReference: " + sql);

        try {
            return jdbc_portal.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getMultiQuotesAwaitingQuoteForRFM(Integer resourceId) {
        String sql = "WITH alice AS ( " +
                "SELECT  CONVERT(varchar, FaultId) [Job Ref], CONVERT(varchar, Store) Site," +
                " LoggedDate AS [Logged Date]," +
                " ScopeOfWorks [Scope of Works], Location, SubTypeClassification [Subtype/Classification], QuotePriority Priority, " +
                " Approvalstatusname , NumberOfQuotesRequired, 'Edit Job' [Action]   " +
                " FROM %portaldb.vw_QuoteApprovalScenarios qas INNER JOIN %helpdeskdb.resource r " +
                " ON qas.managerapproverid = r.epochid " +
                " WHERE  IsMultiQuote = 1 " +
                " AND r.id = :resourceId " +
                " AND qas.QuoteJobApprovalStatusId NOT IN (3, 7, 10, 12) ) " +
                " SELECT  [Job Ref], Site, [Logged Date], [Scope of Works], Location, [Subtype/Classification], Priority, " +
                " CONVERT(varchar, SUM(case when Approvalstatusname IN ( 'AwaitingQuote', 'ItqAwaitingAcceptance', 'AwaitingResourceAssignment', 'QueryResourcePending') then 0 else 1 end) ) + ' of '  + CONVERT(varchar, NumberOfQuotesRequired)  [No. of Quotes], Action " +
                " FROM alice " +
                " GROUP BY [Job Ref], Site, [Logged Date], [Scope of Works], Location, [Subtype/Classification], Priority, NumberOfQuotesRequired, Action " +
                " HAVING  SUM(case when Approvalstatusname IN ( 'AwaitingQuote', 'ItqAwaitingAcceptance', 'AwaitingResourceAssignment', 'QueryResourcePending') then 0 else 1 end) < NumberOfQuotesRequired ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getMultiQuotesAwaitingQuoteForRFM: " + sql.replaceAll(":resourceId", resourceId.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);

        try {
            return jdbc_portal.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getMultiQuotesAwaitingReviewForJobReference(int jobReference) {
        String sql = "SELECT FaultId, Store, LoggedDate, ScopeOfWorks, Location, SubTypeClassification, ApprovalStatusName, QuotePriority, NumberOfQuotesRequired "
                + ", (CASE WHEN qas.ApprovalStatusId = 7 THEN 'Edit Resources' WHEN qas.ApprovalStatusId = 5 THEN 'Edit Job' END) [Action] "
                + ", (SELECT SUM(CASE WHEN Approvalstatusname IN ('AwaitingApproval') THEN 1 ELSE 0 END) FROM %portaldb.vw_QuoteApprovalScenarios qas WHERE FaultId = %d) AS [NumQuotes] "
                + "FROM %portaldb.vw_QuoteApprovalScenarios qas "
                + "WHERE IsMultiQuote = 1 AND FaultId = %d "
                + "AND qas.QuoteJobApprovalStatusId IN (4, 5) "
                + "GROUP BY FaultId, Store, LoggedDate, ScopeOfWorks, Location, SubTypeClassification, ApprovalStatusName, QuotePriority, NumberOfQuotesRequired, ApprovalStatusId";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, jobReference, jobReference);

        logger.debug("getMultiQuotesAwaitingReviewForJobReference: " + sql);

        try {
            return jdbc_portal.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getMultiQuotesAwaitingReviewForRFM(Integer resourceId) {
        String sql = "SELECT CONVERT(varchar,FaultId) [Job Ref], Store [Site], " +
                " LoggedDate AS [Logged Date]," +
                " ScopeOfWorks [Scope of Works], Location, SubTypeClassification [Subtype/Classification], QuotePriority Priority, " +
                " CONVERT(varchar, SUM(CASE WHEN Approvalstatusname IN ( 'AwaitingApproval') THEN 1 ELSE 0 END)) + ' of ' + " +
                " CONVERT(varchar, NumberOfQuotesRequired)  [No. of Quotes], " +
                " CASE WHEN qas.ApprovalStatusId = 7 THEN 'Edit Resources' " +
                " WHEN qas.ApprovalStatusId = 5 THEN 'Edit Job' " +
                " END AS Action " +
                " FROM %portaldb.vw_QuoteApprovalScenarios qas INNER JOIN %helpdeskdb.resource r " +
                " ON qas.managerapproverid = r.epochid " +
                " WHERE " +
                " IsMultiQuote = 1 " +
                " AND " +
                " r.id = :resourceId " +
                " AND qas.QuoteJobApprovalStatusId IN (4, 5) " +
                " GROUP BY FaultId, Store, LoggedDate, ScopeOfWorks, Location, SubTypeClassification, QuotePriority, NumberOfQuotesRequired, StoreId, qas.ApprovalStatusId";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getMultiQuotesAwaitingReviewForRFM: " + sql.replaceAll(":resourceId", resourceId.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);

        try{
            return jdbc_portal.queryForList(sql,params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getMultiQuotesAwaitingResourceForRFM(Integer resourceId) {
        String sql = "WITH alice AS ( "
                + "SELECT FaultId, Store, LoggedDate,"
                + " %testdb.ufn_StripSpaces(REPLACE(REPLACE(ScopeOfWorks, CHAR(13), ''), CHAR(10), ' ')) ScopeOfWorks, Location, SubTypeClassification, QuotePriority, NumberOfQuotesRequired, 'Edit Job' [Action]"
                + " FROM %portaldb.vw_QuoteApprovalScenarios qas INNER JOIN %helpdeskdb.resource r "
                + " ON qas.managerapproverid = r.epochid "
                + " WHERE  "
                + " IsMultiQuote = 1 "
                + " AND "
                + " r.id = :resourceId "
                + " AND qas.QuoteJobApprovalStatusId IN (3) ) "
                + " SELECT CONVERT(varchar, FaultId) [Job Ref], CONVERT(varchar(40), Store) Site,  LoggedDate [Logged Date], LTRIM(RTRIM(ScopeOfWorks)) [Scope of Works], "
                + " Location, SubTypeClassification [Subtype/Classification], QuotePriority Priority, Action "
                + " FROM alice "
                + " GROUP BY FaultId, Store, LoggedDate, ScopeOfWorks, Location, SubTypeClassification, QuotePriority, NumberOfQuotesRequired, Action";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getMultiQuotesAwaitingResourceForRFM: " + sql.replaceAll(":resourceId", resourceId.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);

        try{
            return jdbc_portal.queryForList(sql,params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getMultiQuotesAwaitingBypassReviewForRFM(Integer resourceId) {
        String sql = "WITH alice AS ( "
                + "SELECT FaultId, Store, LoggedDate,"
                + " %testdb.ufn_StripSpaces(REPLACE(REPLACE(ScopeOfWorks, CHAR(13), ''), CHAR(10), ' ')) ScopeOfWorks, Location, SubTypeClassification, QuotePriority, MultiQuoteBypassReasonName, 'Edit Job' [Action] "
                + " FROM %portaldb.vw_QuoteApprovalScenarios qas INNER JOIN %helpdeskdb.resource r "
                + " ON qas.managerapproverid = r.epochid "
                + " WHERE  "
                + " IsMultiQuote = 1 "
                + " AND "
                + " r.id = :resourceId "
                + " AND qas.QuoteJobApprovalStatusId IN (7) ) "
                + " SELECT CONVERT(varchar, FaultId) [Job Ref], CONVERT(varchar(50), Store) Site,  LoggedDate [Logged Date], LTRIM(RTRIM(ScopeOfWorks)) [Scope of Works], "
                + " Location, SubTypeClassification [Subtype/Classification], QuotePriority Priority, MultiQuoteBypassReasonName [Bypass Reason], Action "
                + " FROM alice "
                + " GROUP BY FaultId, Store, LoggedDate, ScopeOfWorks, Location, SubTypeClassification, QuotePriority, MultiQuoteBypassReasonName, Action";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getMultiQuotesAwaitingBypassReviewForRFM: " + sql.replaceAll(":resourceId", resourceId.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);

        try{
            return jdbc_portal.queryForList(sql,params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getFundingRequestsDataInPage(String resourceProfile, String page) throws Exception {
        String sql = "WITH Alice AS ( " +
                "SELECT FaultReference [Job Ref], Store Site, StoreDivision Division, LoggedDate AS [Logged Date], " +
                "ScopeOfWorks [Scope of Works], Location, SubTypeClassification [Subtype/Classification], " +
                "QuotePriority Priority, Approvalstatusname, ApprovalStatusDescription, NumberOfQuotesRequired " +
                "FROM %portaldb.vw_QuoteApprovalScenarios qas " +
                "INNER JOIN %helpdeskdb.Resource r ON qas.SeniorManagerApproverID = r.EpochId " +
                "INNER JOIN %helpdeskdb.ResourceProfile rp ON rp.Id = r.ResourceProfileId " +
                "WHERE rp.Name = :resourceProfile ";

        switch(page) {
        case "Funding Requests Pending Query Response":
            sql = sql + " AND QuoteJobApprovalStatusName = 'QueryManagerPending' AND QuoteJobApprovalStatusId = 8 ";
            break;
        case "Funding Requests Awaiting Response Review":
            sql = sql + " AND QuoteJobApprovalStatusName = 'QueryManagerAnswered' AND QuoteJobApprovalStatusId = 9 ";
            break;
        case "Funding Requests Awaiting Review":
            sql = sql + " AND QuoteJobApprovalStatusName = 'AwaitingSeniorManagerApproval' AND QuoteJobApprovalStatusId = 6 ";
            break;

        default:
            throw new Exception("Cannot find " + page);
        }

        sql = sql + " )" +
                " SELECT TOP(1) [Job Ref], Site, Division, [Logged Date], [Scope of Works], Location, [Subtype/Classification], Priority, " +
                " CONVERT(varchar, SUM(CASE WHEN ApprovalStatusDescription IN ( 'Awaiting Approval') THEN 1 ELSE 0 END)) + ' of ' + CONVERT(varchar, NumberOfQuotesRequired) [No. of Quotes] from Alice " +
                " GROUP BY [Job Ref], Site, Division, [Logged Date], [Scope of Works], Location, [Subtype/Classification], Priority, NumberOfQuotesRequired ORDER BY [Logged Date] DESC";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getFundingRequestsAwaitingReviewData: " + sql.replaceAll(":resourceProfile", resourceProfile));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceProfile", resourceProfile);

        try {
            return jdbc_portal.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getJobQuotesInPage(Integer jobReference, String pageName, String profileName) {
        String status = null;
        if (profileName.equalsIgnoreCase("RFM") && pageName.equalsIgnoreCase("Funding Request Query Response Required")) {
            status = "PENDING";
        } else {
            status = pageName.equalsIgnoreCase("Funding Request Query Response Required") ? "RECOMMENDED" : "RECOMMENDED  PENDING";
        }

        String sql = "SELECT DISTINCT ISNULL(pq.fld_str_QuoteRef, '') AS [Quote Ref], qas.Created AS [Quote Date],  "
                + " CONVERT(nvarchar, qas.MaximumQuoteCost, 0) [Quote Value (" + CURRENCY_SYMBOL + ")], LTRIM(RTRIM(qas.ResourceName)) Resource, IIF(Queried =0, 'No', 'Yes') AS Queried, "
                + " IIF(CAST(tqh.fld_int_QuoteDocumentFileId AS varchar) IS NULL, 'Not Specified', 'View')  AS [Quote Document],"
                + " IIF(CAST(tqh.fld_int_SupportingDocumentFileId AS varchar) IS NULL, 'Not Specified', 'View') AS [Supporting Documents],"
                + " IIF(ApprovalStatusDescription = 'Awaiting Approval', :status, ApprovalStatusDescription) AS Status "
                + " FROM %portaldb.vw_QuoteApprovalScenarios qas LEFT JOIN %portaldb.tblQuoteHeader tqh ON tqh.fld_int_HelpDeskFaultId = qas.FaultReference"
                + " LEFT JOIN %portaldb.tblProjectQuote pq ON pq.fld_int_ProjectHeaderID = qas.ProjectHeaderId"
                + " WHERE qas.FaultReference = :jobReference";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getJobQuotesInPage: " + sql.replaceAll(":jobReference", jobReference.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        params.addValue("status", status);

        try {
            return jdbc_portal.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getNumberofQuotesAwaitingApproval(Integer jobReference) {
        String sql = "SELECT COUNT(1) FROM %portaldb.vw_QuoteApprovalScenarios qas " +
                " WHERE qas.ApprovalStatusName = 'AwaitingApproval' " +
                " AND qas.FaultId = :jobReference ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getNumberofQuotesAwaitingApproval: " + sql.replaceAll(":jobReference", jobReference.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);

        try{
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getNumberOfQuotesRequired(Integer jobReference) {
        String sql = "SELECT MAX(NumberOfQuotesRequired) " +
                " FROM %portaldb.vw_QuoteApprovalScenarios qas " +
                " WHERE qas.FaultId = :jobReference ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getNumberOfQuotesRequired: " + sql.replaceAll(":jobReference", jobReference.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);

        try{
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getQuotePriority(Integer jobReference) {
        String sql = "SELECT DISTINCT qas.QuotePriority " +
                " FROM %portaldb.vw_QuoteApprovalScenarios qas " +
                " WHERE qas.FaultId = :jobReference ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getQuotePriority: " + sql.replaceAll(":jobReference", jobReference.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);

        try{
            return jdbc_portal.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getQuoteFundingRouteId(Integer jobReference) {
        String sql = "SELECT DISTINCT qas.FundingRouteID " +
                " FROM %portaldb.vw_QuoteApprovalScenarios qas " +
                " WHERE qas.FaultId = :jobReference ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getQuoteFundingRouteId: " + sql.replaceAll(":jobReference", jobReference.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);

        try{
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public boolean isApproverFinal(Integer jobReference, Integer fundingRouteId) {
        String sql = "WITH MaxQuotecost AS ( " +
                " SELECT MAX(cost) maxCost FROM %portaldb.vw_QuoteApprovalScenarios " +
                "WHERE faultid = :jobReference) " +
                "SELECT    " +
                "CASE    " +
                "WHEN iar.MaximumCost = 0 THEN 0    " +
                "WHEN(( iar.MaximumCost > 0 ) AND (MaxQuotecost.maxCost > iar.MaximumCost))  THEN 0 " +
                "WHEN((iar. MaximumCost > 0 ) AND (MaxQuotecost.maxCost < iar.MaximumCost))  THEN 1 " +
                "END    " +
                "FROM %helpdeskdb.InitialApproverRule iar, MaxQuotecost " +
                "WHERE iar.FundingRouteId = :fundingRouteId ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("isApproverFinal: " + sql.replaceAll(":jobReference", jobReference.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        params.addValue("fundingRouteId", fundingRouteId);

        try{
            return jdbc_portal.queryForObject(sql, params, Boolean.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return false;
        }
    }

    public boolean isApproverFinal(Integer jobReference, Integer fundingRouteId, Integer resourceProfileId) {
        String sql = "WITH MaxQuotecost AS ( " +
                " SELECT MAX(cost) maxCost FROM %portaldb.vw_QuoteApprovalScenarios " +
                "WHERE faultid = :jobReference) " +
                "SELECT    " +
                "CASE    " +
                "WHEN iar.MaximumCost = 0 THEN 0    " +
                "WHEN(( iar.MaximumCost > 0 ) AND (MaxQuotecost.maxCost > iar.MaximumCost))  THEN 0 " +
                "WHEN((iar. MaximumCost > 0 ) AND (MaxQuotecost.maxCost < iar.MaximumCost))  THEN 1 " +
                "END    " +
                "FROM %helpdeskdb.InitialApproverRule iar, MaxQuotecost " +
                "WHERE iar.FundingRouteId = :fundingRouteId " +
                "AND iar.ResourceProfileId = :resourceProfileId " +
                "AND iar.ApprovalTypeId = 1 -- Quote Approval Type";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("isApproverFinal: " + sql.replaceAll(":jobReference", jobReference.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        params.addValue("fundingRouteId", fundingRouteId);
        params.addValue("resourceProfileId", resourceProfileId);

        try{
            return jdbc_portal.queryForObject(sql, params, Boolean.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return false;
        }
    }

    public List<Map<String, Object>> getQuoteResources(Integer jobReference, String approvalStatusName) {
        String sql = "SELECT qas.ResourceName, qas.ApprovalStatusName, qas.ResourceId " +
                " FROM %portaldb.vw_QuoteApprovalScenariosWithRejected qas " +
                " WHERE qas.FaultId = :jobReference ";
        sql = approvalStatusName == null ? sql : sql + " AND qas.ApprovalStatusName = :approvalStatusName";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getQuoteResources: " + sql.replaceAll(":jobReference", jobReference.toString()).replaceAll("approvalStatusName", approvalStatusName));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        params.addValue("approvalStatusName", approvalStatusName);

        try{
            return jdbc_portal.queryForList(sql,params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }



    public List<Map<String, Object>> getQuoteJobInternalNotesOrQueries(Integer jobReference) {
        String sql = "WITH InternalNotesOrQueriesCTE AS"
                + " (SELECT hdr.fld_str_Name Name, q.fld_dat_CreatedOn OnDate,"
                + "  q.fld_str_QueryText Notes, q.fld_dat_CreatedOn CreatedOn, hft.fld_int_HelpDeskFaultID FaultReference"
                + " FROM %portaldb.tblScopeOfWorks sow"
                + " JOIN %portaldb.tblHelpDeskFaultTime hft ON sow.fld_int_HelpDeskFaultTimeID = hft.fld_int_ID  JOIN %portaldb.tblQuery q ON sow.fld_int_id = q.fld_int_ScopeOfWorksId"
                + " JOIN %portaldb.tblHelpDeskResource hdr ON q.fld_int_UserID = hdr.fld_int_ID"
                + " JOIN %portaldb.vw_QuoteApprovalScenarios qas ON hft.fld_int_HelpDeskFaultID = qas.FaultReference"
                + " UNION"
                + " SELECT hdr.fld_str_Name Name ,q.fld_dat_RespondedOn OnDate,"
                + " q.fld_str_Response Notes, q.fld_dat_RespondedOn CreatedOn, hft.fld_int_HelpDeskFaultID FaultReference"
                + " FROM %portaldb.tblScopeOfWorks sow"
                + " JOIN %portaldb.tblHelpDeskFaultTime hft ON sow.fld_int_HelpDeskFaultTimeID = hft.fld_int_ID  JOIN %portaldb.tblQuery q ON sow.fld_int_id = q.fld_int_ScopeOfWorksId"
                + " JOIN %portaldb.tblHelpDeskResource hdr ON q.fld_int_RespondentID = hdr.fld_int_ID"
                + " JOIN %portaldb.vw_QuoteApprovalScenarios qas ON hft.fld_int_HelpDeskFaultID = qas.FaultReference"
                + " WHERE q.fld_str_Response IS NOT NULL)"
                + " SELECT Name, OnDate, Notes FROM InternalNotesOrQueriesCTE WHERE FaultReference = :jobReference  ORDER BY CreatedOn";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getQuoteJobInternalNotesOrQueries: " + sql.replaceAll(":jobReference", jobReference.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);

        try {
            return jdbc_portal.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getBudgetRoute(Integer jobReference) {
        String sql = "SELECT TOP(1) b.Alias FROM %portaldb.vw_QuoteApprovalScenarios qas JOIN %helpdeskdb.Budget b ON qas.FundingRouteID = b.FundingRouteId " +
                " WHERE qas.FaultReference = :jobReference";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getBudgetRoute: " + sql.replaceAll(":jobReference", jobReference.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);

        try {
            return jdbc_portal.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }


    public String getFundingRouteAlias(String fundingRoute) {
        String sql = "SELECT Alias FROM FundingRoute WHERE Name = :fundingRoute";
        logger.debug("getFundingRouteAlias: " + sql.replaceAll(":fundingRoute", quote(fundingRoute)));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fundingRoute", fundingRoute);

        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getApprover(String approver) {
        String sql = "SELECT TOP(1) rp.Name " +
                "FROM organisationstructuremanager osm INNER JOIN applicationuser au ON osm.applicationuserid = au.id " +
                "INNER JOIN resource r ON au.resourceid = r.id " +
                "INNER JOIN resourceprofile rp ON r.resourceprofileid = rp.id " +
                "INNER JOIN AdditionalApproverRule aar ON rp.id = aar.resourceprofileid " +
                "INNER JOIN ApprovalType atype ON aar.approvaltypeid = atype.id " +
                "WHERE atype.name = 'Quote'";
        sql = "Additional Approver".equalsIgnoreCase(approver) ? sql + "  AND (aar.MaximumCost IS NOT NULL AND aar.MaximumCost = (SELECT MIN(MaximumCost) FROM AdditionalApproverRule))" : sql + " AND (aar.MaximumCost IS NULL OR aar.MaximumCost != (SELECT MIN(MaximumCost) FROM AdditionalApproverRule))";

        logger.debug("getApprover: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }


    public BigDecimal getApproverLimit(String resourceProfile, int fundingRouteId) {
        String sql = "WITH UNION_CTE AS ( " +
                " SELECT DISTINCT aar.* " +
                "FROM organisationstructuremanager osm INNER JOIN applicationuser au ON osm.applicationuserid = au.id " +
                "INNER JOIN resource r ON au.resourceid = r.id " +
                "INNER JOIN resourceprofile rp ON r.resourceprofileid = rp.id " +
                "INNER JOIN AdditionalApproverRule aar ON rp.id = aar.resourceprofileid " +
                "INNER JOIN ApprovalType atype ON aar.approvaltypeid = atype.id " +
                "WHERE atype.name = 'Quote' " +
                "UNION " +
                "SELECT DISTINCT iar.* " +
                "FROM organisationstructuremanager osm INNER JOIN applicationuser au ON osm.applicationuserid = au.id " +
                "INNER JOIN resource r ON au.resourceid = r.id " +
                "INNER JOIN resourceprofile rp ON r.resourceprofileid = rp.id " +
                "INNER JOIN InitialApproverRule iar ON rp.id = iar.resourceprofileid " +
                "INNER JOIN ApprovalType atype ON iar.approvaltypeid = atype.id " +
                "WHERE atype.name = 'Quote' " +
                ") " +
                "SELECT cte.MaximumCost FROM UNION_CTE cte INNER JOIN ResourceProfile rp ON cte.ResourceProfileId = rp.Id " +
                "WHERE rp.Name = :resourceProfile AND FundingRouteId = :fundingRouteId";

        logger.debug("getApproverLimit: " + sql.replaceAll(":resourceProfile", resourceProfile).replaceAll(":fundingRouteId", String.valueOf(fundingRouteId)));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceProfile", resourceProfile);
        params.addValue("fundingRouteId", fundingRouteId);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, BigDecimal.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return new BigDecimal(100);
        }
    }

    public int getQueryId(int scopeOfWorksId) {
        String sql = "SELECT fld_int_id FROM %portaldb.tblQuery WHERE fld_int_ScopeOfWorksId = :scopeOfWorksId and fld_int_QueryTypeId = 2"; //type 2 is additional approver query

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("scopeOfWorksId", scopeOfWorksId);
        logger.debug("getQueryId: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return 0;
        }
    }

    public Integer getQuoteApprovalScenariosRecordCount(Integer faultId,  Map<String, Object> queryMap ) {
        String sql = "SELECT * FROM %portaldb.vw_QuoteApprovalScenarios WHERE faultId = " + faultId;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        String whereClause = "";
        for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                whereClause = whereClause + " AND " + entry.getKey() + " LIKE '%" + entry.getValue() + "%'";
            } else {
                whereClause = whereClause + " AND " + entry.getKey() + " = " + entry.getValue();
            }
        }

        sql = sql + whereClause;

        logger.debug(sql);
        try {
            List<Map<String, Object>> query = jdbc_portal.queryForList(sql, new MapSqlParameterSource());
            return query.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public String getInitialApproverForQuote() {
        String sql = "SELECT DISTINCT rp.Name " +
                "FROM organisationstructuremanager osm INNER JOIN applicationuser au ON osm.applicationuserid = au.id " +
                "INNER JOIN resource r ON au.resourceid = r.id " +
                "INNER JOIN resourceprofile rp ON r.resourceprofileid = rp.id " +
                "INNER JOIN InitialApproverRule iar ON rp.id = iar.resourceprofileid " +
                "INNER JOIN ApprovalType atype ON iar.approvaltypeid = atype.id " +
                "WHERE atype.name = 'Quote' ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getInitialApproverForQuote: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public BigDecimal getNotNullAdditionalApprovalLimit(int fundingRouteId) {
        String sql = "SELECT TOP(1) MaximumCost FROM AdditionalApproverRule WHERE MaximumCost IS NOT NULL AND FundingRouteId = :fundingRouteId";

        logger.debug("getNotNullAdditionalApprovalLimit: " + sql.replaceAll(":fundingRouteId", String.valueOf(fundingRouteId)));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fundingRouteId", fundingRouteId);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, BigDecimal.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return new BigDecimal(100);
        }
    }

    public List<Map<String, Object>> getQuoteDetails(String jobReference) throws Exception {
        String sql = "SELECT qh.fld_str_DescriptionOfWorks AS [Description Of Work] ,qh.fld_str_ProposedWorkingTimes AS [Proposed working times], " +
                "ql.fld_str_PartDescription AS Description, qt.fld_str_Description AS Type, CAST (ql.fld_dec_Quantity AS INTEGER) AS Quantity, CAST (ql.fld_cur_UnitPrice AS INTEGER)AS [Unit Price], CAST (ql.fld_cur_TotalCost AS INTEGER)AS Value " +
                "FROM %portaldb.tblQuoteHeader qh " +
                "INNER JOIN %portaldb.tblQuoteLine ql ON qh.fld_int_ProjectQuoteId = ql.fld_int_ProjectQuoteID " +
                "INNER JOIN %portaldb.tblQuoteLineType qt ON qt.fld_str_PartCode = ql.fld_str_PartCode " +
                "WHERE qh.fld_int_HelpDeskFaultId = :jobReference ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        logger.debug("getQuoteDetails: " + sql);
        try {
            return jdbc_portal.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getResourceCountAssignedToTheJob(int jobReference) {
        String sql = "SELECT COUNT(*) FROM  %helpdeskdb.ResourceAssignment ra " +
                "INNER JOIN Job j ON j.Id = ra.JobId " +
                "WHERE j.JobReference = :jobReference";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        logger.debug("getResourceCountAssignedToTheJob: " + sql);

        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getJobsWithQuotesSubmitted(String noOfQuotesSubmitted) throws Exception {
        String sql = "WITH QAS_CTE AS"
                + " (SELECT FaultReference, qas.ManagerApproverName, COUNT(FaultReference) Count1"
                + " FROM %portaldb.vw_QuoteApprovalScenarios qas"
                + " WHERE IsMultiQuote = 1"
                + " GROUP BY FaultReference, qas.ManagerApproverName"
                + " HAVING COUNT(FaultReference) > 1),"
                + " AST_CTE AS"
                + " (SELECT FaultReference, qas.ManagerApproverName, COUNT(ApprovalStatusName) Count2"
                + " FROM %portaldb.vw_QuoteApprovalScenarios qas"
                + " WHERE IsMultiQuote = 1"
                + " AND QuoteJobApprovalStatusId IN (2, 18)"  //2 = Awaiting Resource Assignment, 18 = ITQ Awaiting Acceptance
                + " AND ApprovalStatusId IN (2, 3)"  //2 = ITQ Awaiting Acceptance, 3 = Awaiting Quote
                + " GROUP BY FaultReference , qas.ManagerApproverName)"
                + " SELECT TOP(1) QAS_CTE.FaultReference, QAS_CTE.ManagerApproverName"
                + " FROM QAS_CTE"
                + " JOIN AST_CTE ON QAS_CTE.FaultReference = AST_CTE.FaultReference"
                + " GROUP BY QAS_CTE.FaultReference, QAS_CTE.ManagerApproverName, Count1, Count2";

        if(noOfQuotesSubmitted.equalsIgnoreCase("0")) {
            sql = sql + " HAVING Count1 = Count2";
        } else {
            sql = sql + " HAVING Count1 > Count2";
        }
        sql = sql + " ORDER BY NEWID()";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getJobsWithQuotesSubmitted: " + sql);
        try {
            return jdbc_portal.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getJobsWithMultiQuotesAwaitingReviewWithBypass(String fundingRoute) throws Exception {
        String sql = "WITH Number_Of_Quotes_Required AS"
                + " (SELECT qas.FaultReference, qas.ManagerApproverName, qas.NumberOfQuotesRequired"
                + " FROM %portaldb.vw_QuoteApprovalScenarios qas"
                + " INNER JOIN %testdb.uvw_QuoteApprovalScenariosUsers qasu ON qas.FaultReference = qasu.JobReference"
                + " INNER JOIN %helpdeskdb.FundingRoute fr ON qasu.FundingRouteId = fr.Id"
                + " WHERE qas.IsMultiQuote = 1"
                + " AND qas.RequiresMultiQuote = 1"
                + " AND qas.QuoteJobApprovalStatusId = 3" //Awaiting Resource Assignment
                + " AND qas.ApprovalStatusId = 7" //Awaiting Approval
                + " AND fr.Name = '%s'),"
                + " Number_Of_Quotes AS"
                + " (SELECT qasu.FaultReference, qasu.ManagerApproverName, COUNT(FaultReference) NumberOfQuotes"
                + " FROM %portaldb.vw_QuoteApprovalScenariosWithRejected qasu"
                + " GROUP BY qasu.FaultReference, qasu.ManagerApproverName)"
                + " SELECT TOP(1) Number_Of_Quotes_Required.FaultReference, Number_Of_Quotes_Required.ManagerApproverName"
                + " FROM Number_Of_Quotes_Required"
                + " JOIN Number_Of_Quotes ON Number_Of_Quotes_Required.FaultReference = Number_Of_Quotes.FaultReference"
                + " WHERE Number_Of_Quotes.NumberOfQuotes > Number_Of_Quotes_Required.NumberOfQuotesRequired"
                + " ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, fundingRoute);
        logger.debug("getJobsWithMultiQuotesAwaitingReviewWithBypass: " + sql);
        try {
            return jdbc_portal.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getRandomQuoteRejectionReasonId() {
        String sql = "SELECT TOP(1) trr.fld_int_ID FROM %portaldb.tblRejectionReason trr " +
                "JOIN %portaldb.tblRejectionReasonType trrt ON trr.fld_int_RejectionReasonType = trrt.fld_int_ID " +
                "WHERE trrt.fld_str_Title = 'Quote Rejection' AND fld_bit_Active = 1 " +
                "ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getRandomQuoteRejectionReasonId: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getQuoteJobRejectionSummary(Integer jobReference) {
        String sql = " SELECT qaswr.ResourceName, qas.fld_str_RejectionNotes Notes " +
                " FROM %portaldb.vw_QuoteApprovalScenariosWithRejected qaswr " +
                " JOIN %portaldb.tblQuoteApprovalScenario qas ON qaswr.ProjectHeaderId = qas.fld_int_ProjectHeaderID " +
                " WHERE FaultReference = :jobReference " +
                " AND qaswr.ApprovalStatusName = 'Rejected'" +
                " ORDER BY qaswr.ResourceName DESC ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getQuoteJobRejectionSummary: " + sql.replaceAll(":jobReference", jobReference.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);

        try {
            return jdbc_portal.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getJobQuotesForFundingRequestRejected(Integer jobReference) {

        String sql = "SELECT DISTINCT ISNULL(pq.fld_str_QuoteRef, '') AS [Quote Ref], tqh.fld_dat_SubmissionDateUTC AS [Quote Date],  "
                + " CONVERT(nvarchar, pq.fld_cur_QuoteValue, 0) [Quote Value (" + CURRENCY_SYMBOL + ")], LTRIM(RTRIM(hdr.fld_str_Name)) Resource, IIF(Queried = 1, 'Yes', 'No') AS Queried, "
                + " IIF(CAST(tqh.fld_int_QuoteDocumentFileId AS varchar) IS NULL, 'Not Specified', 'View')  AS [Quote Document],"
                + " IIF(CAST(tqh.fld_int_SupportingDocumentFileId AS varchar) IS NULL, 'Not Specified', 'View') AS [Supporting Documents],"
                + " IIF(ApprovalStatusDescription = 'Awaiting Resource Assignment', 'REJECTED', ApprovalStatusDescription) AS Status "
                + " FROM %portaldb.vw_QuoteApprovalScenarios qas "
                + " LEFT JOIN %portaldb.tblQuoteHeader tqh ON tqh.fld_int_HelpDeskFaultId = qas.FaultReference"
                + " LEFT JOIN %portaldb.tblProjectQuote pq ON tqh.fld_int_ProjectQuoteId = pq.fld_int_ID"
                + " LEFT JOIN %portaldb.tblHelpDeskResource hdr ON tqh.fld_int_SubmittedByResourceId = hdr.fld_int_ID"
                + " WHERE qas.FaultReference = :jobReference";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getJobQuotesForFundingRequestRejected: " + sql.replaceAll(":jobReference", jobReference.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);

        try {
            return jdbc_portal.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getJobQuoteManagerName(Integer jobReference) {
        String sql = "SELECT TOP(1) ManagerApproverName FROM %portaldb.vw_QuoteApprovalScenarios WHERE FaultReference = :jobReference";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getJobQuoteManagerName: " + sql.replaceAll(":jobReference", jobReference.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        try {
            return jdbc_portal.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getDecisionNotesForQuoteResource(Integer jobReference, String resourceName, String jobState) {
        String sql = "SELECT qr.fld_str_Notes FROM %portaldb.tblQuoteRecommendation qr"
                + " JOIN %portaldb.vw_QuoteApprovalScenarios qas ON qr.fld_int_QuoteApprovalScenarioId = qas.QuoteApprovalScenarioId"
                + " WHERE qas.FaultReference = :jobReference AND qas.ResourceName = :resourceName AND fld_bit_Active = 1 AND fld_int_RecommendationType = 1";
        sql = jobState.equals("Funding Request Rejected") ? sql.replaceAll("vw_QuoteApprovalScenarios", "vw_QuoteApprovalScenariosWithRejected") : sql;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getDecisionNotesForQuoteResource: " + sql.replaceAll(":jobReference", jobReference.toString()).replaceAll(":resourceName", resourceName));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        params.addValue("resourceName", resourceName);
        try {
            return jdbc_portal.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getQuoteDetailsForResource(Integer jobReference, String resourceName, String jobState) {
        String sql = "SELECT DISTINCT qh.fld_str_DescriptionOfWorks DescriptionOfWorks, qh.fld_str_ProposedWorkingTimes ProposedWorkingTimes, "
                + " IIF(hrwt.fld_str_HighRiskWorkType IS NULL, 'No', 'Yes') IsHighRiskWork FROM %portaldb.tblQuoteHeader qh"
                + " JOIN %portaldb.tblQuoteLine ql ON qh.fld_int_ProjectQuoteId = ql.fld_int_ProjectQuoteID"
                + " JOIN %portaldb.vw_QuoteApprovalScenarios qas ON qas.FaultReference = qh.fld_int_HelpDeskFaultId"
                + " LEFT JOIN %portaldb.tblHighRiskWorkTypeQuoteLink hrwtql ON qh.fld_int_HelpDeskFaultId = hrwtql.fld_int_HelpDeskFaultId"
                + " LEFT JOIN %portaldb.tblHighRiskWorkType hrwt ON hrwtql.fld_int_HighRiskWorkTypeId = hrwt.fld_int_Id"
                + " WHERE qh.fld_int_HelpDeskFaultId = :jobReference AND qas.ResourceName = :resourceName";
        sql = jobState.equals("Funding Request Rejected") ? sql.replaceAll("vw_QuoteApprovalScenarios", "vw_QuoteApprovalScenariosWithRejected") : sql;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getQuoteDetailsForResource: " + sql.replaceAll(":jobReference", jobReference.toString()).replaceAll(":resourceName", resourceName));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        params.addValue("resourceName", resourceName);
        try {
            return jdbc_portal.queryForMap(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<String> getHighRiskWorkTypesForResource(Integer jobReference, String resourceName, String jobState) {
        String sql = "SELECT hrwt.fld_str_HighRiskWorkType HighRiskWorkTypes FROM %portaldb.vw_QuoteApprovalScenarios qas"
                + " JOIN %portaldb.tblHighRiskWorkTypeQuoteLink hrwtql ON qas.FaultReference = hrwtql.fld_int_HelpDeskFaultId"
                + " JOIN %portaldb.tblHighRiskWorkType hrwt ON hrwtql.fld_int_HighRiskWorkTypeId = hrwt.fld_int_Id"
                + " WHERE hrwtql.fld_int_HelpDeskFaultId = :jobReference AND qas.ResourceName = :resourceName";
        sql = jobState.equals("Funding Request Rejected") ? sql.replaceAll("vw_QuoteApprovalScenarios", "vw_QuoteApprovalScenariosWithRejected") : sql;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getHighRiskWorkTypesForResource: " + sql.replaceAll(":jobReference", jobReference.toString()).replaceAll(":resourceName", resourceName));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        params.addValue("resourceName", resourceName);
        List<String> highRiskWorkTypes = jdbc_portal.query(sql, new MapSqlParameterSource(), new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("HighRiskWorkTypes"));
            }
        });
        return highRiskWorkTypes;
    }

    public List<Map<String, Object>> getJobQuotesDetailsForResource(Integer jobReference, String resourceName, String jobState) {
        String sql = "SELECT DISTINCT ql.fld_str_PartDescription Description, IIF(ql.fld_str_PartCodeDetail IS NULL, qlt.fld_str_Description ,CONCAT(qlt.fld_str_Description,  ' (', ql.fld_str_PartCodeDetail, ') ')) Type,"
                + " ql.fld_dec_Quantity Quantity,  CONVERT(NVARCHAR, ql.fld_cur_UnitPrice, 0) UnitPrice, CONVERT(NVARCHAR, ql.fld_cur_TotalCost, 0) Value  FROM %portaldb.tblQuoteHeader qh"
                + " JOIN %portaldb.tblQuoteLine ql ON qh.fld_int_ProjectQuoteId = ql.fld_int_ProjectQuoteID"
                + " JOIN %portaldb.tblQuoteLineType qlt ON ql.fld_str_PartCode = qlt.fld_str_PartCode"
                + " JOIN %portaldb.vw_QuoteApprovalScenarios qas ON qas.FaultReference = qh.fld_int_HelpDeskFaultId"
                + " LEFT JOIN %portaldb.tblHighRiskWorkTypeQuoteLink hrwtql ON qh.fld_int_HelpDeskFaultId = hrwtql.fld_int_HelpDeskFaultId"
                + " LEFT JOIN %portaldb.tblHighRiskWorkType hrwt ON hrwtql.fld_int_HighRiskWorkTypeId = hrwt.fld_int_Id"
                + " WHERE qh.fld_int_HelpDeskFaultId = :jobReference AND qas.ResourceName = :resourceName";
        sql = jobState.equals("Funding Request Rejected") ? sql.replaceAll("vw_QuoteApprovalScenarios", "vw_QuoteApprovalScenariosWithRejected") : sql;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getJobQuotesDetailsForResource: " + sql.replaceAll(":jobReference", jobReference.toString()).replaceAll(":resourceName", resourceName));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        params.addValue("resourceName", resourceName);

        try {
            return jdbc_portal.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getMultiQuoteByPassReasonAndNotes(Integer jobReference) {
        String sql = "SELECT MultiQuoteBypassReasonName, MultiQuoteBypassNote FROM %portaldb.vw_QuoteApprovalScenarios WHERE FaultReference = :jobReference";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getMultiQuoteByPassReasonAndNotes: " + sql.replaceAll(":jobReference", jobReference.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        try {
            return jdbc_portal.queryForMap(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getQuotesRejectionReasons() {
        String sql = " SELECT fld_str_Reason AS RejectionReason "
                + " FROM %portaldb.tblQuoteInvitationDeclinedReason "
                + " ORDER BY fld_str_Reason";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("quotesRejectionReasons :" + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        try{
            return jdbc_portal.queryForList(sql,params);
        } catch (Exception e) {
            return null;
        }
    }

    public void updateQuoteDocumentDetails(Integer jobReference) throws Exception {
        String sql = "UPDATE %portaldb.tblQuoteHeader SET fld_int_QuoteDocumentFileId = (SELECT MAX(fld_int_QuoteDocumentFileId) FROM %portaldb.tblQuoteHeader) + 1" +
                " WHERE fld_int_HelpDeskFaultId = %d";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, jobReference);

        logger.debug("updateQuoteDocumentDetails: " + sql);
        int rows = jdbc_portal.update(sql, new MapSqlParameterSource());
        if (rows == 0) {
            throw new Exception("row not updated");
        }
    }

    public List<Map<String, Object>> getQueryReasonAndComments(Integer jobReference) {
        String sql = "SELECT qas.MostRecentQueryReason Reason, qas.MostRecentQueryText Comments " +
                "FROM %portaldb.vw_QuoteApprovalScenarios qas WHERE faultId = :jobReference ";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getQueryReasonAndComments :" + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);

        try{
            return jdbc_portal.queryForList(sql,params);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getRecordCount(Integer jobReference, String notification) {
        String sql = "SELECT COUNT(*) FROM Job j INNER JOIN JobTimelineEvent jtle ON j.id = jtle.jobid " +
                "WHERE j.jobreference = %d " +
                "AND jtle.Title LIKE '%s' ";

        sql = String.format(sql, jobReference, notification);

        logger.debug("getRecordCount: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getRandomFundingRouteSingleQuote() {
        String sql = "SELECT TOP(1) Name"
                + " FROM FundingRoute"
                + " WHERE Active = 1"
                + " AND Name NOT IN ('CAPEX', 'BMI')"
                + " ORDER BY NEWID()";

        logger.debug("getRandomFundingRouteSingleQuote");
        try {
            return jdbc_portal.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getRandomFundingRouteMultiQuote() {
        String sql = "SELECT TOP(1) Name"
                + " FROM FundingRoute"
                + " WHERE Active = 1"
                + " ORDER BY NEWID()";

        logger.debug("getRandomFundingRouteMultiQuote");
        try {
            return jdbc_portal.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getQuoteDetails(Integer jobReference) {
        String sql = "SELECT qas.QuoteApprovalScenarioId, r.Id ResourceId,"
                + " qas.ResourceId PortalResourceId, qas.Cost QuoteValue,"
                + " qas.FundingRouteID, qas.SeniorManagerApproverID"
                + " FROM Portal.vw_QuoteApprovalScenarios qas"
                + " JOIN Resource r ON qas.ResourceId = r.EpochId"
                + " WHERE qas.FaultId = :jobReference";

        logger.debug("getQuoteDetails: " + sql.replaceAll(":jobReference", jobReference.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        try {
            return jdbc_helpdesk.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }
}
