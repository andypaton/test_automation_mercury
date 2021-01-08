package mercury.helpers.dbhelper;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.object.GenericStoredProcedure;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mercury.database.config.DbConfigV2;
import mercury.helpers.DbUtilityHelper;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DbConfigV2.class)
public class DbHelperSites {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;
    @Autowired private DataSource helpdeskDataSource;
    @Autowired private DbUtilityHelper dbUtilityHelper;

    public String getSiteStatusName(int siteStatusId) {
        String sql = "SELECT name FROM SiteStatuses WHERE id=:siteStatusId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("siteStatusId", siteStatusId);

        logger.debug(
                "getSiteStatusName: " + sql.replace(
                        ":siteStatusId",
                        "'" + siteStatusId
                        + "'"));

        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }


    public Integer getRandomNonWarrantySiteIdForResourceId(int resourceId){
        String sql = "SELECT TOP(1) SiteId FROM (" +
                "SELECT SiteId FROM SiteResource WHERE ResourceId = :resourceId " +
                "UNION " +
                "SELECT SiteId FROM AssetClassificationSiteContractorMapping acscm " +
                "INNER JOIN AssetClassificationSite acs ON acscm.AssetClassificationSiteId = acs.Id " +
                "WHERE ResourceId = :resourceId " +
                ") u " +
                "INNER JOIN Site s ON s.Id = u.SiteId " +
                "WHERE StoreOpenDate < GETDATE() - 356 " +
                "ORDER BY NEWID()";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);

        logger.debug("getRandomSiteIdForResourceId: " + sql.replaceAll(":resourceId", String.valueOf(resourceId)));
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getSiteStatusId(String siteStatusName) {
        String sql = "SELECT Id FROM SiteStatuses WHERE Name=:siteStatusName";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("siteStatusName", siteStatusName);
        logger.debug(
                "getSiteStatusId: " + sql.replace(
                        ":siteStatusName",
                        "'" + siteStatusName
                        + "'"));
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public String getStatusNamePriorToCancellation(Integer jobReference) {
        String sql = "SELECT js.Name FROM Job j " + "INNER JOIN JobCancellation jc ON jc.Id = j.JobCancellationId "
                + "INNER JOIN JobStatus js ON jc.JobStatusIdAtCancellation = js.id "
                + "WHERE j.JobReference = %d";
        sql = String.format(sql, jobReference);

        MapSqlParameterSource params = new MapSqlParameterSource();

        logger.debug("getStatusNamePriorToCancellation: " + sql);
        String result = jdbc_helpdesk.queryForObject(sql, params, String.class);
        logger.debug("getStatusNamePriorToCancellation result: " + result);

        return result;
    }

    public String getSiteName(int siteId) {
        String sql = String.format("SELECT Name FROM Site WHERE Id = (%d)", siteId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public Integer getSiteId(String siteName) {
        String sql = String.format("SELECT Id FROM Site WHERE Name = '%s'", siteName);
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getSiteIdFromSiteCode(String siteCode) {
        String sql = String.format("SELECT Id FROM Site WHERE SiteCode = '%s'", siteCode);
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getSiteNameForJobRef(Integer jobref) {
        return getSiteNameForJobRef(String.valueOf(jobref));
    }

    public String getSiteNameForJobRef(String jobref) {
        String sql = "SELECT s.name FROM dbo.job j, dbo.site s WHERE j.siteid = s.id AND j.jobreference=:jobref";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobref", jobref);

        logger.debug(
                "getSiteNameForJobRef: " + sql.replace(
                        ":jobref",
                        "'" + jobref
                        + "'"));

        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public String getSiteNameForIncidentRef(int incidentRef) {
        String sql = "SELECT s.Name FROM Incident i, Site s WHERE i.SiteId = s.Id AND i.incidentReference = :incidentRef";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("incidentRef", incidentRef);

        logger.debug(
                "getSiteNameForIncidentRef: " + sql.replace(
                        ":incidentRef",
                        "'" + incidentRef
                        + "'"));

        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public Integer getSiteIdForJobRef(Integer jobref) {
        String sql = String.format("SELECT s.Id FROM dbo.job j, dbo.site s WHERE j.siteid = s.id AND j.jobreference = %d", jobref);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getSiteIdForJobRef: " + sql);
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public Integer getSiteIdForIncidentRef(Integer incidentRef) {
        String sql = String.format("SELECT SiteId FROM Incident WHERE IncidentReference = %d", incidentRef);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getSiteIdForIncidentRef: " + sql);
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    /**
     * Execute permission denied for the Jenkins user. Once permission granted on
     * new databases then this method will be renamed and the new method removed
     *
     * @param siteId
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map<String, Object> getRfmForSite(int siteId) {

        StoredProcedure procedure = new GenericStoredProcedure();
        procedure.setDataSource(helpdeskDataSource);
        procedure.setSql("usp_GetSiteRFM");
        procedure.setFunction(false);

        SqlParameter[] parameters = { new SqlParameter(Types.BIGINT) };

        procedure.setParameters(parameters);
        procedure.compile();

        logger.debug("getRfmForSite: execute usp_GetSiteRFM @SiteId = " + siteId);
        Map<String, Object> out = procedure.execute(siteId);
        Map<String, Object> row = (Map<String, Object>) ((ArrayList) out.get("#result-set-1")).get(0);
        return row;
    }

    /**
     * Replacement for method above. Temporary fix.
     *
     * @param siteId
     * @return
     */
    public Map<String, Object> getManagerInfoForSiteAndPosition(int siteId, String managerPosition) throws Exception {
        String sql = "SELECT TOP(1) ResourceId, ResourceName FROM uvw_ManagerSites WHERE SiteId = :siteId AND ResourceProfileName = :managerPosition"
                + " ORDER BY NEWID()";
        sql = String.format(sql, siteId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("siteId", siteId);
        params.addValue("managerPosition", managerPosition);
        logger.debug("getManagerForSite: " + sql.replace(":siteId", String.valueOf(siteId)).replace(":managerPosition", "'" + managerPosition + "'"));
        try {
            return jdbc_helpdesk.queryForMap(sql, params);
        } catch (Exception e) {
            return null;
        }
    }

    public String getManagerNameForSiteAndPosition(int siteId, String managerPosition) throws Exception {
        String sql = "SELECT ResourceName FROM uvw_ManagerSites WHERE SiteId = :siteId AND ResourceProfileAlias = :managerPosition";
        sql = String.format(sql, siteId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("siteId", siteId);
        params.addValue("managerPosition", managerPosition);
        logger.debug("getManagerForSite: " + sql.replace(":siteId", String.valueOf(siteId)).replace(":managerPosition", "'" + managerPosition + "'"));
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getGsmForSite(int siteId) {

        String sql = "SELECT DISTINCT Resource.Name from RotaEntry "
                + " INNER JOIN Resource ON RotaEntry.ResourceId = Resource.Id "
                + " INNER JOIN OrganisationStructureSite ON RotaEntry.OrganisationStructureSiteId = OrganisationStructureSite.Id "
                + " INNER JOIN Site ON OrganisationStructureSite.SiteId = Site.Id "
                + " WHERE RotaEntry.ResourceProfileId = 114 "    // GSM
                + " AND RotaEntry.RotaEntryTypeId = 1 "     // Permanent
                + " AND RotaEntry.StartAt <= GETUTCDATE() "
                + " AND (RotaEntry.EndAt IS NULL OR RotaEntry.EndAt > GETUTCDATE()) "
                + " AND Resource.Active = 1 "
                + " AND Site.Id = %d";

        sql = String.format(sql, siteId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getGsmForSite: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            return "Unknown";
        }
    }

    public Integer getRandomSiteWithConfiguredJobQuestion(String siteStatus, String jobQuestion) {
        String sql = "SELECT TOP(1) s.Id FROM Site s"
                + " FULL JOIN SiteStatuses ss ON s.SiteStatusId = ss.Id"
                + " WHERE ss.Name = :siteStatus AND s.Id IN"
                + " (SELECT  relc.ComparisonValue FROM RulesEngineLookupCondition relc"
                + " JOIN RulesEngineRule rer ON relc.RuleId=rer.Id"
                + " JOIN RulesEngineRuleQuestionOutput rerqo ON rer.Id = rerqo.RuleId"
                + " JOIN question q ON q.Id = rerqo.QuestionId"
                + " WHERE rerqo.RuleId IN (SELECT RuleId FROM RulesEngineRuleQuestionOutput GROUP BY RuleId HAVING COUNT(1) = 1 INTERSECT"
                + " SELECT RuleId FROM RulesEngineRuleQuestionOutput rerqo JOIN Question q ON rerqo.QuestionId = q.Id WHERE q.Name = :jobQuestion)"
                + " AND relc.RuleId NOT IN (SELECT relc.RuleId FROM RulesEngineLookupCondition relc JOIN RulesEngineLookupField relf"
                + " ON relc.LookupFieldId = relf.Id WHERE relf.Alias != 'Site' GROUP BY relc.RuleId HAVING COUNT(1) >= 1)"
                + " AND s.Id IN (SELECT ComparisonValue from RulesEngineLookupCondition GROUP BY ComparisonValue HAVING COUNT(1) = 1)"
                + " AND ((rer.ActiveTo >= GETDATE()) OR (rer.ActiveTo IS NULL)))"
                + " ORDER BY NEWID()";

        logger.debug("randomSiteWIthoutCustomQuestion: " + sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("siteStatus", siteStatus);
        params.addValue("jobQuestion", jobQuestion);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void updateSiteStatus(int siteId, int statusId) {
        String sql = String.format("UPDATE Site SET SiteStatusId = %d WHERE id = %d ", statusId, siteId);
        logger.debug("updateSiteState: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    /**
     * Execute permission denied for the Jenkins user. Once permission granted on
     * new databases then this method will be renamed and the new method removed
     *
     * @param siteId
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Integer getRfmResourceIdForSite_Old(int siteId) {

        StoredProcedure procedure = new GenericStoredProcedure();
        procedure.setDataSource(helpdeskDataSource);
        procedure.setSql("usp_GetSiteRFM");
        procedure.setFunction(false);

        SqlParameter[] parameters = { new SqlParameter(Types.BIGINT) };

        procedure.setParameters(parameters);
        procedure.compile();

        logger.debug("getRfmForSite: execute usp_GetSiteRFM @SiteId = " + siteId);
        Map<String, Object> out = procedure.execute(siteId);
        Map<String, Object> row = (Map<String, Object>) ((ArrayList) out.get("#result-set-1")).get(0);
        return (Integer) row.get("ResourceId");
    }

    public Integer getOrganisationStructureSiteId(int siteId) {
        String sql = String.format("SELECT Id FROM OrganisationStructureSite WHERE SiteId = %d", siteId);
        logger.debug("getOrganisationStructureSiteId: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public int getRandomSiteIdWithCityTechAndContractor() {
        String sql = "SELECT TOP(1) Id FROM %testdb.uvw_Stores WHERE NumContractors > 0 AND CityTechConfigured > 0 ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getRandomSiteIdWithCityTechAndContractor: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public int getRandomSiteIdForType(String siteType) {
        siteType = siteType.replace("'",  "''");
        String sql = "SELECT TOP(1) s.Id FROM Site s JOIN SiteTypes st ON s.SiteTypeId = st.Id WHERE st.Name = '%s' ORDER BY NEWID()";

        sql = String.format(sql,  siteType);
        logger.debug("getRandomSiteIdForType: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public int getRandomSiteId() {
        String sql = "SELECT TOP(1) Id SiteId"
                + " FROM Site"
                + " WHERE Active = 1"
                // not currently working for advocate - to add back in when data improves
                //                + " AND StoreOpenDate < GETDATE() - 356"
                + " ORDER BY NEWID()";

        logger.debug("getRandomSiteId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public int getSitePrefferedDistance(int jobReference) {
        String sql = "SELECT MobileAppPreferredDistance FROM dbo.Site " +
                " WHERE Id = (SELECT SiteId FROM Job WHERE JobReference = :jobReference )";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        logger.debug("getRandomSiteIdWithCityTechAndContractor: " + sql);
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public Map<String, Object> getGeoLocationForJobReference(Integer jobReference) {
        String sql = "SELECT s.Location.STSrid AS STSrid, s.Location.Lat AS Latitude, s.Location.Long AS Longitude FROM Site s INNER JOIN Job j ON j.SiteId = s.Id WHERE j.JobReference = %d";
        sql = String.format(sql, jobReference);
        logger.debug("getGeoLocationForJobReference: "+ sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
    }

    public List<Map<String, Object>> getManagerSites(int resourceID) throws Throwable {
        String sql = "SELECT SiteName FROM uvw_ManagerSites " +
                "WHERE uvw_ManagerSites.ResourceId = :resourceID ORDER BY SiteName";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceID", resourceID);
        logger.debug("GetManagerToSites : " + sql);
        return jdbc_helpdesk.queryForList(sql, params);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<Map<String, Object>> getOnCallSummaryData(int resourceID, String dateFormat, String startOfWeekOffset) throws Throwable {
        StoredProcedure procedure = new GenericStoredProcedure();
        procedure.setDataSource(helpdeskDataSource);
        String sql = "%testdb.usp_getOnCallSummaryGrid";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        procedure.setSql(sql);
        procedure.setFunction(false);

        SqlParameter[] parameters = { new SqlParameter("resourceID", Types.BIGINT),
                new SqlParameter("dateFormat", Types.NVARCHAR),
                new SqlParameter("startOfWeekOffset", Types.NVARCHAR)
        };

        procedure.setParameters(parameters);
        procedure.compile();
        logger.debug("getOnCallSummaryData: execute usp_getOnCallSummaryGrid @resourceID = " + resourceID + ", @dateFormat = " + dateFormat + ", @startOfWeekOffset = " + startOfWeekOffset);
        Map<String, Object> out = procedure.execute(resourceID, dateFormat, startOfWeekOffset);
        List<Map<String, Object>> rows = ((ArrayList) out.get("#result-set-1"));

        return rows;
    }

    public Integer getSiteTypeId(int siteId) {
        String sql = " SELECT SiteTypeId FROM Site WHERE Id = " + siteId;
        logger.debug("getSiteTypeId: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getSiteType(int siteId) {
        String sql = " SELECT st.Name FROM Site s JOIN SiteTypes st ON st.Id = st.SiteTypeId WHERE Id = " + siteId;
        logger.debug("getSiteType: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getSiteAssetId(Integer siteId, Integer assetClassificationId) {
        String sql = String.format("SELECT TOP(1) Id FROM Asset WHERE SiteId = %d AND AssetClassificationId = %d", siteId, assetClassificationId);
        logger.debug("getSiteAssetId : " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public List<String> getSitesInTeams(List<String> teamList){
        String teams = "'" + teamList.get(0).trim() + "'";
        for (int i = 1; i < teamList.size(); i++) {
            teams += ", '" + teamList.get(i).trim() + "'";
        }

        String sql = "SELECT %testdb.ufn_StripSpaces(s.Name) Name FROM Site s INNER JOIN TeamSiteMapping tsm ON s.Id = tsm.SiteId INNER JOIN Team t ON tsm.TeamId = t.Id WHERE t.Name IN (%s)";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql,  teams);
        logger.debug("getSitesInTeams : " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), String.class);
    }

    public List<String> getIconToolTips(int siteId){
        String sql = "SELECT IconTooltip FROM Location l INNER JOIN SiteLocationMapping slm ON l.id = slm.LocationId WHERE IconTooltip IS NOT NULL AND SiteId = " + siteId;
        logger.debug("getIconToolTips : " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), String.class);
    }

    public Map<String, Object> getFaultTypeIdAssetClassificationIdForSiteIdAndFaultPriority(int siteId, int faultPriorityId) {
        String sql = "SELECT TOP(1) fp.FaultTypeId, fp.AssetClassificationId FROM uvw_FaultPriorityMappings fp"
                + " JOIN Site s ON fp.SiteTypeId = s.SiteTypeId"
                + " LEFT JOIN ForcedIncidentCriterion fic ON fp.AssetClassificationId = fic.AssetClassificationId AND fp.FaultTypeId = fic.FaultTypeId"
                + " WHERE s.Id = :siteId AND fp.FaultPriorityId = :faultPriorityId"
                + " AND fp.FaultTypeId IS NOT NULL AND fp.AssetClassificationId IS NOT NULL"
                + " AND fic.AssetClassificationId IS NULL AND fic.FaultTypeId IS NULL"
                + " ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("siteId", siteId);
        params.addValue("faultPriorityId", faultPriorityId);
        logger.debug("getFaultTypeIdAssetClassificationIdForSiteIdAndFaultPriority: " + sql);
        try {
            return jdbc_helpdesk.queryForMap(sql, params);
        } catch (Exception e) {
            return null;
        }
    }


    public boolean isPreferredSiteResource(int siteId, int resourceId) {
        String sql = "WITH CTE AS ( " +
                "SELECT SiteId, ResourceId FROM SiteResource " +
                "WHERE resourceId = :resourceId AND SiteId = :siteId " +
                "UNION " +
                "SELECT  s.Id,  acscm.ResourceId " +
                "FROM Site s " +
                "LEFT JOIN AssetClassificationSiteContractorMapping acscm ON acscm.AssetClassificationSiteid = s.id " +
                "WHERE acscm.ResourceId IS NOT NULL " +
                "AND acscm.resourceId = :resourceId AND s.Id = :siteId " +
                ")  " +
                "SELECT COUNT(1) from CTE";

        MapSqlParameterSource params = new MapSqlParameterSource();


        logger.debug("getFaultTypeIdAssetClassificationIdForSiteIdAndFaultPriority: " + sql);
        try {
            params.addValue("siteId", siteId);
            params.addValue("resourceId", resourceId);
            logger.debug("isPreferredSiteResource : " + sql.replaceAll(":resourceId", String.valueOf(resourceId)).replaceAll(":siteId", String.valueOf(siteId)));
            int count = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return (count>1 ? true : false);
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> getSite(int siteId){
        String sql = "SELECT * FROM Site WHERE Id = %d";
        sql = String.format(sql, siteId);
        logger.debug("getPriorityDetail: " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
    }

    public String getRandomSiteCode() {
        String sql = "SELECT TOP (1) SiteCode FROM Site ORDER BY NEWID() ";
        logger.debug("getRandomSiteCode: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getRandomSiteForStorePortal() {
        String sql = "SELECT TOP(1) SiteCode FROM Site"
                + " WHERE Active = 1 AND SiteTypeId != 22" //Not Sam's Club
                + " AND StoreOpenDate < GETDATE() - 356" //Not a Warranty Site
                + " ORDER BY NEWID()";

        logger.debug("getRandomSiteForStorePortal: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void updateSiteTypeToAutomaticallyDefer() {
        String sql = "UPDATE SiteTypeDeferralPriorityConfiguration"
                + " SET AutomaticallyDefer = 1, ShowDeferralQuestion = 0"
                + " WHERE DayOfTheWeek = DATEPART(dw, GETDATE())";

        logger.debug("updateSiteTypeToAutomaticallyDefer: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public String getRandomSiteBrand() {
        String sql = "SELECT TOP(1) Name AS Brand FROM Brand WHERE Active = 1 ORDER BY NEWID()";

        logger.debug("getRandomSiteBrand: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getRandomSiteType() {
        String sql = "SELECT TOP(1) Name AS SiteType FROM SiteTypes WHERE Active = 1 ORDER BY NEWID()";

        logger.debug("getRandomSiteType: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getRandomSiteName() {
        String sql = "SELECT TOP(1) Name"
                + " FROM Site"
                + " WHERE Active = 1"
                + " AND SiteStatusId = 1"  //Occupied Site
                + " ORDER BY NEWID()";

        logger.debug("getRandomSiteName: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public List<Map<String, Object>> getRandomSiteNameAndSiteCodeInStatus(String status) {
        int active = status.equalsIgnoreCase("ACTIVE") ? 1 : 0;
        String sql = "SELECT TOP(1) Name, SiteCode [Site Code] FROM Site WHERE Active = %d ORDER BY NEWID()";
        sql = String.format(sql, active);
        logger.debug("getRandomSiteNameAndSiteCodeInStatus: " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getSiteNameAndSiteCode(String name) {
        String sql = "SELECT TOP(1) Name, SiteCode [Site Code] FROM Site WHERE Name = '%s' ORDER BY NEWID()";
        sql = String.format(sql, name);
        logger.debug("getSiteNameAndSiteCode: " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
    }

    public void updateSiteLocation(int siteId, Double latitude, Double longitude) {
        String sql = String.format("UPDATE Site SET Location = GEOGRAPHY::Point(%f, %f, 4326) WHERE Id = %d", latitude, longitude, siteId);
        logger.debug("updateSiteLocation: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public String getDistanceBetweenSites(int site1Id, int site2Id) {
        String sql = "SELECT GEOGRAPHY::Point(s1.Location.Lat, s1.Location.Long, 4326).STDistance(GEOGRAPHY::Point(s2.Location.Lat, s2.Location.Long, 4326)) AS Distance "
                + "FROM Site s1, Site s2 "
                + "WHERE s1.Id = %d AND s2.Id = %d";
        sql = String.format(sql, site1Id, site2Id);
        logger.debug("getDistanceBetweenSites: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public boolean isSitePresent(String siteName, String siteCode, String siteType, String brand, String siteStatus, String town, String country, String postcode) {
        String sql = String.format("SELECT COUNT(1) FROM Site s" +
                " JOIN SiteTypes st ON s.SiteTypeId = st.Id" +
                " JOIN SiteStatuses ss ON s.SiteStatusId = ss.Id" +
                " JOIN Brand b ON s.BrandId = b.Id" +
                " JOIN Country c ON s.CountryId = c.Id" +
                " WHERE s.Name = '%s'" +
                " AND s.SiteCode = '%s'" +
                " AND st.Name = '%s'" +
                " AND b.Name = '%s'" +
                " AND ss.Name = '%s'" +
                " AND s.Town = '%s'" +
                " AND c.Name = '%s'" +
                " AND s.Postcode = '%s'", siteName, siteCode, siteType, brand, siteStatus, town, country, postcode);

        MapSqlParameterSource params = new MapSqlParameterSource();

        logger.debug("isSitePresent: " + sql);
        int result = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        logger.debug("isSitePresent result: " + result);
        return result > 0;
    }

    public void deleteSiteLocationMappingAddedByAutoTest(String siteName) {
        String sql = "DELETE FROM SiteLocationMapping WHERE SiteId = (SELECT Id FROM Site WHERE Name = '%s')";
        sql = String.format(sql, siteName);
        logger.debug("deleteSiteLocationMappingAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteSiteLocationMappingsAddedByAutoTest(String siteIds) {
        String sql = "DELETE FROM SiteLocationMapping WHERE SiteId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteSiteLocationMappingAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteSiteOpeningHoursAddedByAutoTest(String siteName) {
        String sql = "DELETE FROM SiteOpeningHours WHERE Id = (SELECT Id FROM Site WHERE Name = '%s')";
        sql = String.format(sql, siteName);
        logger.debug("deleteSiteOpeningHoursAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteSitesOpeningHoursAddedByAutoTest(String siteIds) {
        String sql = "DELETE FROM SiteOpeningHours WHERE Id IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteSiteOpeningHoursAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteLockJobsForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE la FROM LockAudit la"
                + " JOIN Job j ON la.LockedJobId = j.Id"
                + " WHERE j.SiteId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteLockJobsForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteJobAnswerForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE a FROM Answer a"
                + " JOIN Job j ON a.JobId = j.Id"
                + " WHERE j.SiteId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteJobAnswerForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteJobContactForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE jc FROM JobContact jc"
                + " JOIN Job j ON jc.JobId = j.Id"
                + " WHERE j.SiteId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteJobContactForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteJobRepairResponseForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE jrr FROM JobRepairResponse jrr"
                + " JOIN Job j ON jrr.JobId = j.Id"
                + " WHERE j.SiteId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteJobRepairResponseForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteJobTimelineEventForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE jte FROM JobTimelineEvent jte"
                + " JOIN Job j ON jte.JobId = j.Id"
                + " WHERE j.SiteId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteJobTimelineEventForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteOrgStructureForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE FROM OrganisationStructureSite WHERE SiteId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteOrgStructureForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteRotaEntryForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE re FROM RotaEntry re"
                + " JOIN OrganisationStructureSite oss ON re.OrganisationStructureSiteId = oss.Id"
                + " WHERE oss.SiteId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteRotaEntryForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteTeamMappingForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE FROM TeamSiteMapping WHERE SiteId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteTeamMappingForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteAssetClassificationContractorMappingForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE acscm FROM AssetClassificationSiteContractorMapping acscm"
                + " JOIN AssetClassificationSite acs ON acscm.AssetClassificationSiteId = acs.Id"
                + " WHERE acs.SiteId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteAssetClassificationContractorMappingForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteAssetClassificationForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE FROM AssetClassificationSite WHERE SiteId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteAssetClassificationForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceCallerForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE rc FROM ResourceCaller rc"
                + " JOIN Resource r ON rc.ResourceId = r.Id"
                + " WHERE r.HomeStoreId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteResourceCallerForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceEmailAddressForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE rea FROM ResourceEmailAddress rea"
                + " JOIN Resource r ON rea.ResourceId = r.Id"
                + " WHERE r.HomeStoreId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteResourceEmailAddressForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceNotificationMethodForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE rnm FROM ResourceNotificationMethod rnm"
                + " JOIN Resource r ON rnm.ResourceId = r.Id"
                + " WHERE r.HomeStoreId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteResourceNotificationMethodForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourcePhoneNumberForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE rpn FROM ResourcePhoneNumber rpn"
                + " JOIN Resource r ON rpn.ResourceId = r.Id"
                + " WHERE r.HomeStoreId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteResourcePhoneNumberForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteTeamUserMappingForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE tum FROM TeamUserMapping tum"
                + " JOIN ApplicationUser au ON tum.UserId = au.Id"
                + " JOIN Resource r ON au.ResourceId = r.Id"
                + " WHERE r.HomeStoreId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteTeamUserMappingForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteApplicationUserForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE au FROM ApplicationUser au"
                + " JOIN Resource r ON au.ResourceId = r.Id"
                + " WHERE r.HomeStoreId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteApplicationUserForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceWorkingHoursForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE rwh FROM ResourceWorkingHours rwh"
                + " JOIN Resource r ON rwh.ResourceId = r.Id"
                + " WHERE r.HomeStoreId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteResourceWorkingHoursForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceSyncForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE FROM SiteResourceSync WHERE SiteId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteResourceSyncForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE FROM Resource WHERE HomeStoreId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteResourceForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deletePhoneNumberForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE FROM SitePhoneNumber WHERE SiteId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deletePhoneNumberForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteAdditionalInfoForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE FROM SiteAdditionalInformation WHERE SiteId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteAdditionalInfoForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteJobsForSiteAddedByAutoTest(String siteIds) {
        String sql = "DELETE FROM Job WHERE SiteId IN (%s)";
        sql = String.format(sql, siteIds);
        logger.debug("deleteJobsForSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteSiteAddedByAutoTest(String siteName) {
        String sql = "DELETE FROM Site WHERE Name = '%s'";
        sql = String.format(sql, siteName);
        logger.debug("deleteSiteAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteAllSitesAddedByAutoTest() {
        String sql = "DELETE FROM Site WHERE Name LIKE 'TestAuto%'";
        logger.debug("deleteAllSitesAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public String getRandomCountry() {
        String sql = "SELECT TOP(1) Name FROM Country WHERE Active = 1 ORDER BY NEWID()";

        logger.debug("getRandomCountry: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public void updateSiteStatusAndActiveColumns(int siteId, int statusId, int active) {
        String sql = String.format("UPDATE Site SET SiteStatusId = %d, Active = %d WHERE Id = %d ", statusId, active, siteId);
        logger.debug("updateSiteStatusAndActiveColumns: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getSiteTypes() {
        String sql = "SELECT Name SiteType FROM SiteTypes WHERE Active = 1 ORDER BY Name";
        logger.debug("getSiteTypes: " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
    }

    public Map<String, Object> getRandomLocationWithLinkedIcon(String siteType) {
        String sql = "SELECT TOP(1) IconPath Icon, l.Id, l.Name Location, stlm.IsOptional"
                + " FROM Location l"
                + " JOIN SiteTypeLocationMapping stlm ON l.Id = stlm.LocationId"
                + " WHERE l.Id IN"
                + " (SELECT l.Id FROM Location l"
                + " JOIN SiteTypeLocationMapping stlm ON l.Id = stlm.LocationId"
                + " JOIN SiteTypes st ON stlm.SiteTypeId = st.Id"
                + " WHERE st.Name = '%s')"
                + " AND IconPath IS NOT NULL"
                + " ORDER BY NEWID()";
        sql = String.format(sql, siteType);

        logger.debug("getSiteTypesLocationsMapping: " + sql);
        try {
            return jdbc_helpdesk.queryForMap(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getSiteTypesLocationsMapping(String siteType, boolean mapped, boolean hasIcon) {
        String sql = "SELECT TOP(1) IconPath Icon, 'Site' AS Area, l.Name Location, 'N/A' AS [Sub Location], stlm.IsOptional"
                + " FROM Location l"
                + " JOIN SiteTypeLocationMapping stlm ON l.Id = stlm.LocationId"
                + " WHERE l.Id IN"
                + " (SELECT l.Id FROM Location l"
                + " JOIN SiteTypeLocationMapping stlm ON l.Id = stlm.LocationId"
                + " JOIN SiteTypes st ON stlm.SiteTypeId = st.Id"
                + " WHERE st.Name = '%s')"
                + " AND IconPath IS NOT NULL"
                + " ORDER BY NEWID()";
        sql = String.format(sql, siteType);

        sql = !mapped ? sql.replace("l.Id IN", "l.Id NOT IN") : sql;
        sql = !hasIcon ? sql.replace("IconPath IS NOT NULL", "IconPath IS NULL") : sql;

        logger.debug("getSiteTypesLocationsMapping: " + sql);
        try {
            return jdbc_helpdesk.queryForMap(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public String getAreaName() {
        String sql = "SELECT Name from Location WHERE LocationLevelId = 1";

        logger.debug("getArea: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getSubLocationName(int locationId) {
        String sql = "SELECT Name from Location WHERE WithinLocationId = %d";

        sql = String.format(sql, locationId);
        logger.debug("getSubLocation: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getSiteCodeFromSiteId(int siteId) {
        String sql = "SELECT SiteCode FROM Site WHERE Id = %d";

        sql = String.format(sql, siteId);
        logger.debug("getSiteCodeFromSiteId: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getSiteTypeId(String siteTypeName) {
        String sql = String.format("SELECT Id FROM SiteTypes WHERE Name = '%s'", siteTypeName);
        logger.debug("getSiteTypeId: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteSiteTypeLocationMappingAddedByAutoTest(int locationId, int siteTypeId) {
        String sql = "DELETE FROM SiteTypeLocationMapping WHERE LocationId = %d AND SiteTypeId = %d";
        sql = String.format(sql, locationId, siteTypeId);
        logger.debug("deleteSiteTypeLocationMappingAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public String getSiteTypeMappedtoMinimumNumberOfLocations() {
        String sql = "WITH SiteTypeLocationCountCTE AS (SELECT SiteTypeId, COUNT(*) LocationCount FROM SiteTypeLocationMapping GROUP BY SiteTypeId)"
                + " SELECT st.Name FROM SiteTypeLocationCountCTE stlcc"
                + " JOIN SiteTypes st ON stlcc.SiteTypeId = st.Id"
                + " WHERE LocationCount = (SELECT MIN(LocationCount) FROM SiteTypeLocationCountCTE)";

        logger.debug("getSiteTypeMappedtoMinimumNumberOfLocations: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public boolean isSiteTypeLocationMappingPresent(String siteTypeName, String locationName, int isOptional) {
        String sql = String.format("SELECT COUNT(1) FROM SiteTypeLocationMapping stlm"
                + " JOIN SiteTypes st ON stlm.SiteTypeId = st.Id"
                + " JOIN Location l ON stlm.LocationId = l.Id"
                + " WHERE st.Name = '%s'"
                + " AND l.Name = '%s'"
                + " AND stlm.IsOptional = %d", siteTypeName, locationName, isOptional);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("isSiteTypeLocationMappingPresent: " + sql);
        int result = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        logger.debug("isSiteTypeLocationMappingPresent result: " + result);
        return result > 0;
    }

    public List<Integer> getSiteIdsCreatedByAutoTest() {

        String sql = "SELECT Id FROM Site"
                + " WHERE Name LIKE 'TestAuto%'";

        logger.debug("getSiteIdsCreatedByAutoTest: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isSitePresentInDb(int siteCode) {
        String sql = "SELECT COUNT(*) FROM Site WHERE SiteCode = %d";
        sql = String.format(sql, siteCode);
        logger.debug("isSitePresentInDb: " + sql);
        int count = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        return count > 0;
    }
}