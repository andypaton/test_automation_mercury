package mercury.helpers.dbhelper;

import static mercury.helpers.StringHelper.quote;
import static mercury.runtime.ThreadManager.getWebDriver;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
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
import mercury.helpers.FileHelper;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfigV2.class)
public class DbHelper {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private NamedParameterJdbcTemplate jdbc_portal;
    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;
    @Autowired private DbUtilityHelper dbUtilityHelper;

    public void updateUserProfile(String username, String profile) {
        // TODO: Verify the query - had to change this for the Portal tests.  Helpdesk tests still seem to work dbo.UserProfile
        String sql = "UPDATE dbo.ApplicationUser SET userprofileid = (SELECT id FROM dbo.UserProfile WHERE name=:profile) WHERE username=:username";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);
        params.addValue("profile", profile);

        logger.debug("updateUserProfile: " + sql.replace(":profile", "'" + profile + "'").replace(":username", "'" + username + "'"));

        jdbc_helpdesk.update(sql, params);
    }

    public String getUserProfileName(Integer userProfileId) {
        String sql = String.format("SELECT Name FROM UserProfile WHERE id = %d", userProfileId);
        logger.debug("getUserProfileName: " + sql);
        String result = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        return result;
    }

    public String getStatusNamePriorToCancellation(Integer jobReference) {
        String sql = "SELECT js.Name FROM Job j "
                + "INNER JOIN JobCancellation jc ON jc.Id = j.JobCancellationId "
                + "INNER JOIN JobStatus js ON jc.JobStatusIdAtCancellation = js.id "
                + "WHERE j.JobReference = %d";
        sql = String.format(sql,  jobReference);

        MapSqlParameterSource params = new MapSqlParameterSource();

        logger.debug("getStatusNamePriorToCancellation: " + sql);
        String result = jdbc_helpdesk.queryForObject(sql, params, String.class);
        logger.debug("getStatusNamePriorToCancellation result: " + result);

        return result;
    }

    public String getJobStatusName(int jobStatusId) {
        String sql = String.format("SELECT name FROM JobStatus WHERE id=%d", jobStatusId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getJobStatusName: " + sql);
        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public String getProfileID(String profileName) {

        String sql = "SELECT id FROM dbo.UserProfile WHERE name=:profileName";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("profileName", profileName);

        logger.debug("getProfileID: " + sql.replace(":profileName", "'" + profileName + "'"));

        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public String getUserName(String profile) {

        String sql = "SELECT TOP(1) au.username FROM dbo.ApplicationUser au, dbo.ResourceProfile rp WHERE au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) AND rp.name=:profile";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("profile", profile);

        logger.debug(sql.replace(":profile", "'" + profile + "'"));

        String result = jdbc_helpdesk.queryForObject(sql, params, String.class);

        logger.debug("result = '" + result + "'");

        return result;
    }

    public String getRandomAsset() {

        String sql = "SELECT TOP(1) FullTextSearch FROM dbo.uvw_AssetForSearch WHERE FullTextSearch IS NOT NULL";

        MapSqlParameterSource params = new MapSqlParameterSource();

        logger.debug("getRandomAsset: " + sql);

        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public String getRandomAssetSubType() {

        String sql = "SELECT TOP(1) summary FROM uvw_AssetSubTypeForSearch WHERE FullTextSearch IS NOT NULL AND FullTextSearch != '' ORDER BY NEWID()";

        MapSqlParameterSource params = new MapSqlParameterSource();

        logger.debug("getRandomAssetSubType: " + sql);

        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public Map<String, Object> getRandomAssetSubTypeWithFormat() {

        String sql = "SELECT TOP(1) CONCAT(ast.Name, ' (', at.Name, ')') AS AssetSubType, ast.Id AS AssetSubTypeId"
                + " FROM AssetSubType ast"
                + " INNER JOIN AssetType at ON ast.AssetTypeId = at.Id"
                + " WHERE ast.Active = 1 AND DATALENGTH(ast.Name) > 0"
                + " ORDER BY NEWID()";

        logger.debug("getRandomAssetSubTypeWithFormat: " + sql);

        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
    }

    public String getLocationName(Integer id) {
        String sql = String.format("SELECT name FROM Location WHERE id = %d", id);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getLocationName: " + sql);
        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public Integer getLocationId(String name) {
        String sql = String.format("SELECT TOP(1) Id FROM Location WHERE Name = '%s'", name);
        logger.debug("getLocationId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public Integer getAssetId(String assetTag) {
        String sql = String.format("SELECT Id FROM Asset WHERE AssetTag = '%s'", assetTag);
        logger.debug("getAssetId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public String getFaultTypeName(Integer id) {
        String sql = String.format("SELECT name FROM FaultType WHERE id = %d", id);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getFaultTypeName: " + sql);
        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public String getAssetSubTypeName(Integer assetClassificationId) {
        String sql = String.format("SELECT ast.name FROM AssetSubType ast INNER JOIN AssetClassification ac ON ast.Id = ac.AssetSubTypeId WHERE ac.Id = %d", assetClassificationId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getAssetSubTypeName: " + sql);
        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public String getCallerName(Integer id) {
        String sql = String.format("SELECT name + ' (' + department + ')' FROM clientCaller WHERE id = %d", id);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getCallerName: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch(Exception e) {
            return null;
        }
    }

    public String getDefaultPortal(String username) {
        String sql = "SELECT p.name FROM ApplicationUser au, UserProfile up, Portals p WHERE au.UserName=:username AND au.userprofileid = up.id AND up.DefaultPortal = p.id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);
        logger.debug("getDefaultPortal: " + sql.replace(":username", "'" + username + "'"));
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getProjectCost(String helpdeskFaultId, String supplierId) {
        //String sql = "SELECT ISNULL( (SELECT SUM(COALESCE(fld_cur_TotalProjectCost, 0.0)) FROM tblProjectHeader ph  WHERE ph.fld_int_HelpdeskfaultID = :helpdeskFaultId ),0)";

        String sql = "SELECT SUM(tl.fld_cur_LineCost) "
                + "FROM %portaldb.tblHelpDeskFault AS hdf "
                + "LEFT JOIN %portaldb.tblPO AS po ON hdf.fld_int_ID = po.fld_int_HelpdeskFaultID "
                + "LEFT JOIN %portaldb.tblTransactionHeader AS th ON po.fld_int_ID = th.fld_int_POID "
                + "LEFT JOIN %portaldb.tblTransactionLine AS tl ON th.fld_int_ID = tl.fld_int_TransactionHeaderID "
                + "WHERE hdf.fld_int_ID = :helpdeskFaultId AND po.fld_str_SupplierID = :supplierId";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("helpdeskFaultId", helpdeskFaultId);
        params.addValue("supplierId", supplierId);

        String totalCost;
        totalCost = jdbc_portal.queryForObject(sql, params, String.class);

        if(totalCost != null) {
            Double temp = Double.valueOf(totalCost);
            totalCost = temp==0.0 ? "0.00" : String.format("%.2f", temp);
        } else {
            totalCost = "0.00";
        }

        return totalCost;
    }

    public List<String> getJobStatuses(Integer siteId) {

        final String sql = "SELECT js.Name + ' ['+ CAST(JobStatusId AS varchar) + '] : ' + CAST(COUNT(JobStatusId) AS varchar) AS Status"
                + " FROM Job j, JobStatus js "
                + " WHERE j.siteId=:siteId and j.JobStatusId = js.id "
                + " GROUP BY JobStatusId, js.name "
                + " ORDER BY JobStatusId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("siteId", siteId);

        logger.debug("getJobStatuses: " + sql.replace(":siteId", String.valueOf(siteId)));

        List<String> statuses = jdbc_helpdesk.query(sql, params, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("Status"));
            }
        });

        return statuses;
    }

    public boolean isPhoneConfigured(Integer resourceId) {

        String sql = String.format("SELECT COUNT(phoneNumber) FROM ResourcePhoneNumber WHERE ResourceId=%d", resourceId);

        MapSqlParameterSource params = new MapSqlParameterSource();

        logger.debug("isPhoneConfigured: " + sql);
        int result = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        logger.debug("isPhoneConfigured result: " + result);
        return result > 0;
    }

    public int getJobStatusId(String name) {
        String sql = "SELECT js.Id FROM jobStatus js WHERE js.name = :name ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getJobStatusId: " + sql.replace(":name", "'" + name + "'"));
        params.addValue("name", name);
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public int getJobTypeId(String name) {
        String sql = "select jt.Id FROM jobType jt WHERE jt.name = :name ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getJobTypeId: " + sql.replace(":name", "'" + name + "'"));
        params.addValue("name", name);
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public int getAssetTypeId(String asset) {
        String sql = String.format("SELECT Id FROM AssetType WHERE Name = '%s'", asset);
        logger.debug("getAssetTypeId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public int getAssetSubTypeId(int assetTypeId, String assetSubType) {
        assetSubType = assetSubType.replace("'", "''");
        String sql = String.format("SELECT ast.Id FROM AssetSubType ast JOIN AssetType at ON at.Id = ast.AssetTypeId WHERE at.Id = %d AND ast.Name = '%s'", assetTypeId, assetSubType);
        logger.debug("getAssetSubTypeId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public int getAssetSubTypeId(String name) {
        String sql = String.format("SELECT TOP (1) Id FROM AssetSubType WHERE Name = '%s'", name);
        logger.debug("getAssetSubTypeId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }


    public int getClassificationId(String classification, int assetSubTypeId) {
        String sql = String.format("SELECT Id FROM AssetClassification WHERE Name = '%s' AND AssetSubTypeId = %d", classification, assetSubTypeId);
        logger.debug("getClassificationId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public int getFaultTypeId(String faultType) {
        String sql = String.format("SELECT Id FROM FaultType WHERE Name = '%s'", faultType);
        logger.debug("getFaultTypeId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public int getClassificationIdForAssetSubType(String assetSubtypeName) {
        String sql = "SELECT ac.Id FROM Assetsubtype ast, AssetClassification ac WHERE ast.id=ac.AssetSubTypeId AND ast.name = :assetSubtypeName ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getAssetSubtypeId: " + sql.replace(":assetSubtypeName", "'" + assetSubtypeName + "'"));
        params.addValue("assetSubtypeName", assetSubtypeName);
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public String getAssetClassificationSubtypeName(String siteName, String localIdentifier) {
        String sql = "SELECT TOP(1) '(' + ast.Name + ' > ' +  ac.Name + ')'  FROM Site s " +
                "INNER JOIN Asset a ON s.id = a.SiteId " +
                "INNER JOIN AssetClassification ac ON a.AssetClassificationId = ac.id " +
                "INNER JOIN AssetSubType ast ON ast.id = ac.AssetSubTypeId " +
                "WHERE s.name = '%s' " +
                "AND a.LocalIdentifier = '%s'";
        sql = String.format(sql, siteName, localIdentifier);
        logger.debug("getAssetClassificationSubtypeName: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getFaultTypeDetail(String faultTypeName, String assetSubTypeName, String siteName) {
        String sql = "SELECT DISTINCT TOP(1) fpm.Detail FROM uvw_FaultPriorityMappings fpm " +
                "INNER JOIN Site s ON fpm.SiteTypeId = s.SiteTypeId " +
                "WHERE FaultTypeName = '%s' AND AssetSubTypeName = '%s' AND s.NAME='%s' ";
        sql = String.format(sql, faultTypeName, assetSubTypeName, siteName);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getFaultTypeDetail: " + sql);
        String result = jdbc_helpdesk.queryForObject(sql, params, String.class);
        logger.debug("getFaultTypeDetail result: " + result);
        return result;
    }

    public String getFaultTypeDetail(String faultTypeName, String assetSubTypeName, String assetClassificationName, String siteName) {
        String sql = "SELECT DISTINCT fpm.Detail FROM uvw_FaultPriorityMappings fpm " +
                "INNER JOIN Site s ON fpm.SiteTypeId = s.SiteTypeId " +
                "WHERE FaultTypeName = '%s' AND AssetSubTypeName = '%s' AND AssetClassificationName = '%s' AND s.NAME='%s' ";
        sql = String.format(sql, faultTypeName, assetSubTypeName, assetClassificationName, siteName);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getFaultTypeDetail: " + sql);
        String result = jdbc_helpdesk.queryForObject(sql, params, String.class);
        logger.debug("getFaultTypeDetail result: " + result);
        return result;
    }

    public String getAssetTag(Integer jobReference) {
        String sql = "SELECT DISTINCT j.AssetTag FROM uvw_jobs j WHERE j.Jobreference = %d ";
        sql = String.format(sql, jobReference);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getAssetTag: " + sql);
        try {
            String result = jdbc_helpdesk.queryForObject(sql, params, String.class);
            logger.debug("getAssetTag result: " + result);
            return result;
        } catch(Exception e) {
            logger.debug("getAssetTag result: null");
            return null;
        }
    }

    public String getJobFundingRouteId(Integer jobReference) {
        String GET_FUNDINGROUTE_BASE_SQL = "SELECT DISTINCT FundingRouteId" +
                "  FROM Job j INNER JOIN Quote q ON q.JobId = j.Id ";
        String sql = GET_FUNDINGROUTE_BASE_SQL + " WHERE j.jobreference = :jobReference";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);

        logger.debug("getJobFundingRouteId: " + sql.replace(":jobReference", jobReference.toString()));
        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    /*
     * delete all job locks older than <minutes>
     */
    public void deleteJobLocksOlderThan(int minutes) {
        String sql = String.format("DELETE JobLock WHERE LockedOn < DATEADD(mi, -%d, GETDATE())", minutes);
        logger.debug("deleteLock: " + sql);

        try {
            jdbc_helpdesk.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            // ignore
        }
    }

    public Integer getRandomLocation() {
        String sql = "SELECT TOP(1) Id FROM Location WHERE Active = 1 AND LocationLevelId = 2 ORDER BY NEWID() ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getRandomLocation: " + sql);
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public Integer getRandomLocation(int siteId) {
        String sql = String.format("SELECT TOP(1) LocationId FROM SiteLocationMapping WHERE SiteId = %d ORDER BY NEWID() ", siteId);
        logger.debug("getRandomLocation: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public String getRandomLocationName(int siteId) {
        String sql = String.format("SELECT TOP(1) l.Name FROM SiteLocationMapping slm JOIN Location l ON l.Id = slm.LocationId WHERE slm.SiteId = %d ORDER BY NEWID() ", siteId);
        logger.debug("getRandomLocationName: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public Integer getRandomLocation(int siteId, int assetClassificationId) {
        String sql = "SELECT TOP(1) l.Id FROM AssetClassificationSiteTypeLocationsMapping acstlm "
                + "INNER JOIN SiteTypeLocationMapping stlm ON acstlm.SiteTypeLocationMappingId = stlm.Id "
                + "INNER JOIN Location l ON l.Id = stlm.LocationId "
                + "INNER JOIN Site s ON s.SiteTypeId = stlm.SiteTypeId "
                + "WHERE l.Active = 1 "
                + "AND s.Id = %d "
                + "AND acstlm.AssetClassificationId = %d "
                + "ORDER BY NEWID()";
        sql = String.format(sql, siteId, assetClassificationId);
        logger.debug("getRandomLocationName: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getRandomCaller(String type) throws Exception {
        String callers = FileHelper.getResourceFile("sql/select/", "GET_CALLERS.sql");
        String sql = String.format("SELECT TOP(1) Id FROM (%s) callers WHERE CallerType = '%s' ORDER BY NEWID()", callers, type);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getRandomCaller: " + sql);
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    /**
     *  A User profile can be mapped to more than one resource profile
     *  Some tests require a specific user profile and not a resource profile
     *  If a user/resource profile maps to both a AD and non-AD resource type then return AD authentication  CAST(MAX(CASt(rt.AdLogin AS int)) AS bit)
     *  if a user/resource profile maps to only one then the correct resource type will be returned
     *
     * @param profileName - maps to user profile name and resource profile name
     * @return true false depending on query result
     */
    public Boolean isActiveDirectoryLogin(String profileName) {

        String sql = "SELECT CAST(MAX(CAST(rt.AdLogin AS int)) AS bit) AS AdLogin " +
                "FROM ApplicationUser au  " +
                "INNER JOIN Userprofile up ON au.UserProfileId = up.id " +
                "INNER JOIN Resource r ON r.id = au.resourceId  " +
                "INNER JOIN ResourceProfile rp ON r.resourceProfileId = rp.id  " +
                "INNER JOIN ResourceType rt  ON rp.resourceTypeId = rt.Id " +
                "WHERE up.Name = '%s' or rp.Name = '%s'";
        sql = String.format(sql, profileName, profileName);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("isActiveDirectoryLogin: " + sql);
        return jdbc_helpdesk.queryForObject(sql, params, Boolean.class);
    }

    /**
     * does the resource use active directory to log in
     * @param resourceId - the resource Id
     * @return true false depending on query result
     */
    public Boolean isResourceInActiveDirectory(int resourceId) {

        String sql = "SELECT CAST(MAX(CAST(rt.AdLogin AS int)) AS bit) AS AdLogin " +
                "FROM Resource r " +
                "INNER JOIN ResourceProfile rp ON r.resourceProfileId = rp.id  " +
                "INNER JOIN ResourceType rt  ON rp.resourceTypeId = rt.Id " +
                "WHERE r.Id = %d" ;
        sql = String.format(sql, resourceId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("isResourceInActiveDirectory: " + sql);
        return jdbc_helpdesk.queryForObject(sql, params, Boolean.class);
    }

    /**
     * Depricated - get deployed version from Login page
     **/
    @Deprecated
    public String getDeployedVersion() {
        String sql = "SELECT TOP(1) Version FROM EnvironmentSettings ORDER BY DeploymentDate DESC";
        MapSqlParameterSource params = new MapSqlParameterSource();
        String result = jdbc_portal.queryForObject(sql, params, String.class);
        logger.debug("getDeployedVersion: " + sql + "\n" + result);
        return result;
    }

    public int getRowCount(String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public double getPartUnitPrice(String partCode) {
        String sql = "SELECT TOP (1) fld_cur_CostEach unitPrice" +
                "   FROM tblPricebook" +
                "   WHERE fld_str_PartCode = :partCode";
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getPartUnitsProce:" +sql.replace(":partCode","'" + partCode +"'"));
        params.addValue("partCode", partCode);
        return jdbc_portal.queryForObject(sql, params, Double.class);
    }

    public int getRandomOrgTechPositionId(String type) {
        String sql = "SELECT TOP(1) tech.Id FROM OrganisationStructure tech " +
                "LEFT JOIN OrganisationStructure supervisor ON supervisor.Id = tech.ParentId " +
                "LEFT JOIN OrganisationStructure region ON region.Id = supervisor.ParentId " +
                "LEFT JOIN OrganisationStructure division ON division.Id = region.ParentId " +
                "LEFT JOIN OrganisationStructure parent ON parent.Id = division.ParentId " +
                "WHERE parent.Name = '%s' AND tech.Active=1 ORDER BY NEWID()";
        sql = String.format(sql, type);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public String getFundingRequestStatus(int id) {
        String sql = "SELECT Name FROM FundingRequestStatus WHERE Id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        logger.debug("getFundingRequestStatus: " + sql);
        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public String getRandomFaultName(int priority, int assetClassificationId, int siteId) {
        String sql = "SELECT TOP(1) FaultTypeName " +
                "FROM uvw_FaultPriorityMappings fpm " +
                "JOIN Site s ON s.SiteTypeId = fpm.SiteTypeId " +
                "JOIN AssetClassification ac ON ac.AssetSubTypeId = fpm.AssetSubTypeId " +
                "WHERE Priority = %d " +
                "AND s.Id = %d " +
                "AND ac.Id = %d " +
                "ORDER BY NEWID()";
        sql = String.format(sql, priority, siteId, assetClassificationId);
        logger.debug("getRandomFaultName: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getApplicationUserId(String ResourceName) {
        String sql = "SELECT TOP(1) au.id FROM Resource r JOIN ApplicationUser au ON r.id = au.ResourceId " +
                "  WHERE r.Active = 1 AND r.Name = '" + ResourceName.replaceAll("'", "''") + "' ORDER BY NEWID()";
        logger.debug("getApplicationUserId: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public String getApplicationUserIdExcludingTechs(String ResourceName) {
        String sql = "SELECT TOP(1) au.id FROM Resource r JOIN ApplicationUser au ON r.id = au.ResourceId " + "  WHERE r.Active = 1 AND r.Name = '"
                + ResourceName.replaceAll("'", "''")
                + "' and au.UserName NOT LIKE '%_Tech%' ORDER BY NEWID()";
        logger.debug("getApplicationUserId: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    /**
     * use getApplicationUserIdForResourceId(Integer resourceId, String userProfileName)
     * @param resourceId
     * @return
     */
    public String getApplicationUserIdForResourceId(Integer resourceId) {
        String sql = "SELECT TOP(1) au.id FROM %helpdeskdb.Resource r INNER JOIN %helpdeskdb.ApplicationUser au ON r.id = au.ResourceId " +
                "INNER JOIN %helpdeskdb.UserProfile up ON up.Id = au.UserProfileId " +
                "INNER JOIN %helpdeskdb.ResourceProfile rp ON r.ResourceProfileId = rp.id " +
                "INNER JOIN %helpdeskdb.ResourceType rt ON rp.ResourceTypeId = rt.id " +
                "WHERE r.Active = 1 AND au.Active = 1 AND r.Id = %d " +
                "ORDER BY up.Name DESC";        // this would return Contractor Technician before Contractor Admin

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, resourceId);
        logger.debug("getApplicationUserIdForResourceId: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public String getApplicationUserIdForResourceId(Integer resourceId, String userProfileName) {
        String sql = "SELECT TOP(1) au.id FROM %helpdeskdb.Resource r JOIN %helpdeskdb.ApplicationUser au ON r.id = au.ResourceId " +
                "INNER JOIN %portaldb.tblHelpDeskResource hdr ON hdr.fld_int_ID = r.EpochID " +
                "INNER JOIN %helpdeskdb.ResourceProfile rp ON r.ResourceProfileId = rp.id " +
                "INNER JOIN %helpdeskdb.ResourceType rt ON rp.ResourceTypeId = rt.id " +
                "INNER JOIN %helpdeskdb.UserProfile up ON up.Id = au.UserProfileId " +
                "WHERE r.Active = 1 AND r.Id = %d AND (up.Name = '%s' OR rp.Name = '%s') ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, resourceId, userProfileName, userProfileName);
        logger.debug("getApplicationUserIdForResourceId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getApplicationUserIdForEpochId(Integer epochId) {
        String sql = "SELECT TOP(1) au.id FROM Resource r JOIN ApplicationUser au ON r.id = au.ResourceId " +
                "INNER JOIN %portaldb.tblHelpDeskResource hdr ON hdr.fld_int_ID = r.EpochID " +
                "INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.id " +
                "INNER JOIN ResourceType rt ON rp.ResourceTypeId = rt.id " +
                "WHERE r.Active = 1 AND r.EpochId = %d";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, epochId);
        sql = sql + " AND au.UserName NOT LIKE '%_tech'";
        logger.debug("getApplicationUserIdForEpochId: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public String getApplicationUsername(String ResourceName) {
        String sql = "SELECT TOP(1) au.UserName FROM Resource r JOIN ApplicationUser au ON r.id = au.ResourceId " +
                "  WHERE r.Active = 1 AND r.Name = '" + ResourceName + "' ORDER BY NEWID()";
        logger.debug("getApplicationUsername: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public String getAssetSubTypeNotInLinkedIncidentCriteria(int siteId) {
        String sql = "SELECT TOP(1) ast.Name from AssetSubType ast"
                + " JOIN AssetClassification ac ON ast.Id = ac.AssetSubTypeId"
                + " JOIN AssetClassificationSite acs ON ac.Id = acs.AssetClassificationId"
                + " LEFT JOIN LinkedIncidentCriterion lic ON ast.Id = lic.AssetSubTypeId"
                + " JOIN Site s ON acs.SiteId = s.Id"
                + " WHERE s.Id = %d"
                + " AND lic.AssetSubTypeId IS NULL"
                + " ORDER BY NEWID()";
        sql = String.format(sql, siteId);
        logger.debug("getAssetSubTypeNotInLinkedIncidentCriteria: " + sql);
        return jdbc_helpdesk.queryForObject(sql,  new MapSqlParameterSource(), String.class);
    }

    public void addAssetsToSite(int siteId) {
        String sql = "INSERT INTO AssetClassificationSite (AssetClassificationId, SiteId) SELECT AssetClassificationId, %d FROM AssetClassificationSite WHERE SiteId=1 AND AssetClassificationId NOT IN  (SELECT AssetClassificationId FROM AssetClassificationSite WHERE SiteId = %d)";
        sql = String.format(sql, siteId, siteId);
        logger.debug("addAssetsToSite: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void insertSupplierTcode() {
        String sql = "INSERT INTO %portaldb.tblTCodeSuppliers (Code, Name, Status) VALUES ('TA001','Test Automation Contractor',0)";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("insertSupplierTcode: " + sql);
        try {
            jdbc_portal.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            // ignore;
        }
    }

    public String getAvailablePayrollCode() {
        String sql = "SELECT TOP(1) Code FROM uvw_PayrollTCodes WHERE ExistingResourceId IS NULL";
        logger.debug("getAvailablePayrollCode: " + sql);
        return jdbc_helpdesk.queryForObject(sql,  new MapSqlParameterSource(), String.class);
    }

    public Map<String, Object> getRandomResourceCallerWithPhoneNumber(String type) {
        String sql = " SELECT TOP(1) c.Id, r.Name FROM Caller c"
                + " JOIN CallerType ct ON ct.Id = c.CallerTypeId"
                + " JOIN ResourceCaller rc ON c.Id = rc.Id"
                + " JOIN Resource r ON r.Id = rc.ResourceId"
                + " JOIN ResourceProfile rp ON rp.Id = r.ResourceProfileID AND rp.ResourceTypeId = 1"   // City Resource
                + " JOIN ResourcePhoneNumber rpn ON rpn.ResourceId = r.Id"
                + " WHERE c.Active = 1"
                + " AND ct.Name = '%s' AND rpn.PhoneNumber IS NOT NULL"
                + " ORDER BY NEWID()";

        sql = String.format(sql,  type);
        logger.debug("getRandomResourceCallerWithPhoneNumber: " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);

    }

    public Integer getFaultPriorityId(int siteTypeId, int assetSubTypeId, int assetClassificationId, int faultTypeId) {
        String sql = " SELECT FaultPriorityId FROM FaultPriorityMapping WHERE "
                + " SiteTypeId = " + siteTypeId
                + " AND AssetSubTypeId = " + assetSubTypeId
                + " AND AssetClassificationId = " + assetClassificationId
                + " AND FaultTypeId = " + faultTypeId;
        logger.debug("getFaultPriorityId: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isFaultPriorityValid(String faultPriority) {
        String sql = " SELECT COUNT(1) FROM FaultPriority WHERE Detail = '%s' AND Active = 1";
        sql = String.format(sql, faultPriority);
        logger.debug("isFaultPriorityValid");
        try {
            int count =  jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);

            return (count>0);
        } catch (Exception e) {
            return false;
        }
    }

    public void insertAudit(String title, Integer jobReference, String description, String status) {
        String sql = "INSERT INTO %testdb.TestAutomation_Audit (Title, JobReference, Description, Status) VALUES ('%s', %d, '%s', '%s')";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, title, jobReference, description.replaceAll("'", ""), status);
        logger.debug("insertAudit: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getJobCancellationAndResourceRemovalDetails(Integer jobReference) {
        String sql = "SELECT jc.RequestedBy AS JobCancellationRequestedBy, r.Name AS JobCancellationReason, "
                + "rae.Reason AS ResourceRemovalReason, rae.Notes AS ResourceRemovalNotes  FROM Job j "
                + "INNER JOIN JobCancellation jc ON j.JobCancellationId = jc.Id "
                + "INNER JOIN Reason r ON jc.ReasonId = r.Id "
                + "INNER JOIN ResourceAssignment ra ON j.Id = ra.JobId "
                + "INNER JOIN ResourceAssignmentEvent rae ON rae.ResourceAssignmentId = ra.Id "
                + "WHERE j.JobReference = :jobReference ";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        logger.debug("getJobCancellationAndResourceRemovalDetails: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getReason(int id) {
        String sql = "SELECT Name FROM Reason WHERE Id = " + id;

        try {
            logger.debug("getReason: " + sql);
            String result = jdbc_helpdesk.queryForObject(sql,  new MapSqlParameterSource(), String.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getPhoneNumberTypeId(String phone) {
        String sql = String.format("SELECT Id FROM PhoneNumberType WHERE Name = '%s'", phone);
        logger.debug("getPhoneNumberTypeId: " + sql);
        return jdbc_helpdesk.queryForObject(sql,  new MapSqlParameterSource(), Integer.class);
    }

    public String getDbTimestamp() {
        String sql = "SELECT CURRENT_TIMESTAMP";
        logger.debug("getDbTimestamp: " + sql);
        return jdbc_helpdesk.queryForObject(sql,  new MapSqlParameterSource(), String.class);
    }

    public Map<String, Object> getQuestion(String name){
        String sql = String.format("SELECT TOP(1) * FROM Question WHERE Name = '%s' ORDER BY CreatedOn DESC", name);
        logger.debug("getQuestion: " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
    }

    public String getSettingValue(String key) {
        String sql = "SELECT fld_str_Value FROM %portaldb.tblSetting WHERE fld_str_Key = '%s'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, key);

        logger.debug("getSettingValue: " + sql);
        return jdbc_portal.queryForObject(sql,  new MapSqlParameterSource(), String.class);
    }

    public Integer getServiceCentreTimezoneId() {
        String sql = "SELECT TimezoneId FROM ServiceCentre WHERE Active = 1";
        logger.debug("getServiceCentreTimezoneId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public BigDecimal getUpliftNotificationToFinanceAmount() {
        String sql = "SELECT UpliftNotificationToFinanceAmount FROM SystemConfig";
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getUpliftNotificationToFinanceAmount: " + sql);
        return jdbc_helpdesk.queryForObject(sql, params, BigDecimal.class);
    }

    public Integer getMaxAdditionalResourceRequirementId(int jobReference) {
        String sql = "SELECT MAX(arr.Id) "
                + "FROM AdditionalResourceRequirement arr "
                + "INNER JOIN Job j ON j.Id = arr.JobId "
                + "WHERE j.JobReference = " + jobReference;

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getMaxAdditionalResourceRequirementId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public List<String> getNonImmediateCalloutPriorities() {
        String sql = "SELECT DISTINCT Detail FROM FaultPriority WHERE ImmediateCallout = 0 ";

        logger.debug("getNonImmediateCalloutPriorities: " + sql);

        List<String> priorities = jdbc_helpdesk.query(sql, new MapSqlParameterSource(), new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("Detail"));
            }
        });

        return priorities;
    }

    /**
     * Select two random high risk works types to be used when creating quotes
     * @return 2 random high risk works types
     */
    public List<String> getRandomHighRiskWorkTypes() {
        String sql = "SELECT TOP(2) fld_str_HighRiskWorkType FROM %portaldb.tblHighRiskWorkType ORDER BY NEWID()";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getRandomHighRiskWorkTypes: " + sql);

        List<String> risks = jdbc_portal.query(sql, new MapSqlParameterSource(), new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("fld_str_HighRiskWorkType"));
            }
        });

        return risks;
    }

    public String getRandomTeamNameWhichIsNot(String teamName) {
        String sql = "SELECT TOP(1) Name FROM Team WHERE Name != '%s' ORDER BY NEWID()";
        sql = String.format(sql, teamName);
        logger.debug("getRandomTeamNameWhichIsNot: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getPriorityDetail(int faultPriorityId) {
        String sql = "SELECT Detail FROM FaultPriority WHERE Id = %d";
        sql = String.format(sql, faultPriorityId);
        logger.debug("getPriorityDetail: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getRandomQuestionType() {
        String sql = "SELECT TOP(1) FriendlyName FROM QuestionType ORDER BY NEWID()";
        logger.debug("getRandomQuestionType: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public Map<String, Object> isQuestionInDatabase() {
        String sql = "SELECT TOP(1) q.Name AS QuestionName, qt.FriendlyName AS QuestionType, rert.Name AS QuestionTemplate"
                + " FROM Question q"
                + " INNER JOIN QuestionType qt ON q.QuestionTypeId = qt.Id"
                + " INNER JOIN RulesEngineRuleTemplate rert ON q.RulesEngineRuleTemplateId = rert.Id"
                + " ORDER BY q.CreatedOn DESC";
        logger.debug("isQuestionInDatabase: " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
    }

    public Map<String, Object> getQuestionDetails(String question) {
        String sql = "SELECT q.Name AS QuestionName, qt.FriendlyName AS QuestionType, rert.Name AS QuestionTemplate, q.Active"
                + " FROM Question q "
                + " INNER JOIN QuestionType qt ON q.QuestionTypeId = qt.Id"
                + " INNER JOIN RulesEngineRuleTemplate rert ON q.RulesEngineRuleTemplateId = rert.Id"
                + " WHERE q.Name = '%s'";
        sql = String.format(sql, question);
        logger.debug("getQuestionDetails: " + sql);
        return jdbc_helpdesk.queryForMap(sql, new MapSqlParameterSource());
    }

    public Boolean isRuleInDatabase(String rule) {
        String sql = "SELECT COUNT(Name)"
                + " FROM RulesEngineRule"
                + " WHERE Name = '%s'";

        sql = String.format(sql, rule);
        logger.debug("isRuleInDatabase: " + sql);
        int result = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        return result > 0;
    }

    public List<String> getListOfJobQuestions() {
        String sql = "SELECT q.Name FROM Question q "
                + " JOIN RulesEngineRuleTemplate rert ON q.RulesEngineRuleTemplateId = rert.Id"
                + " WHERE rert.Name = 'Job Questions'";
        logger.debug("getListOfJobQuestions: " + sql);
        return (jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), String.class));
    }

    public Map<String, Object> getAssetClassificationFaultDetail(int assetClassificationId, int faultTypeId, int siteTypeId) {
        String sql = "SELECT at.Name AS AssetTypeName, fpm.* FROM uvw_FaultPriorityMappings fpm "
                + "INNER JOIN assetSubtype ast ON ast.Id = fpm.AssetSubTypeId "
                + "LEFT JOIN AssetType at ON ast.AssetTypeId = at.Id "
                + "WHERE AssetClassificationId = %d AND FaultTypeId = %d AND SiteTypeId = %d";
        sql = String.format(sql, assetClassificationId, faultTypeId, siteTypeId);
        logger.debug("getAssetClassificationFaultDetail: " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
    }

    public String getRandomTeamNameWithProfileName(String profileName) {
        String sql = "SELECT TOP(1) t.Name FROM Team t "
                + "INNER JOIN TeamUserMapping tm ON tm.TeamId = t.Id "
                + "INNER JOIN ApplicationUser au ON au.Id = tm.UserId "
                + "INNER JOIN UserProfile up ON up.Id = au.UserProfileId "
                + "WHERE t.Name IS NOT NULL "
                + "AND tm.TeamId IS NOT NULL "
                + "AND up.Name = '%s' "
                + "ORDER BY NEWID()";
        sql = String.format(sql, profileName);
        logger.debug("getRandomTeamNameWithProfileName: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public List<Integer> getResourceWithResourceAssignmentStatusId(int jobReference, int resourceAssignmentStatusId) {
        String sql = "SELECT ResourceId FROM ResourceAssignment ra INNER JOIN Job j ON j.Id = ra.JobId AND j.JobReference = %d AND ResourceAssignmentStatusId = %d";
        sql = String.format(sql, jobReference, resourceAssignmentStatusId);

        logger.debug("getResourceWithResourceAssignmentStatusId: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getRandomAssetTypeFromSiteCode(String siteCode) {
        //SiteTypeId 22 is for Sams Club, these Sites will never be able to log into the Store Portal
        String sql = "SELECT TOP(1) a.Id AS AssetTypeId, ftm.FaultTypeId, a.SiteId, a.AssetClassificationId, a.LocationId FROM Asset a "
                + "INNER JOIN Site s ON a.SiteId = s.Id INNER JOIN FaultTypeMapping ftm ON a.AssetClassificationId = ftm.AssetClassificationId "
                + "WHERE s.SiteCode = %s AND s.SiteTypeId != 22 AND s.Active = 1 ORDER BY NEWID()";

        sql = String.format(sql, siteCode);
        logger.debug("getRandomAssetTypeIdAndFaultTypeId: " + sql.replace("%d", siteCode));

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getJobWithResourceCount(String userProfileName, String jobType, String jobStatus, String resourceCount, String resourceAssignmentStatus) throws Exception{
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOBS_WITH_RESOURCE_COUNT.sql");

        sql = sql.replace(":jobType", quote(jobType));
        sql = sql.replace(":jobStatus", quote(jobStatus));
        String status = Arrays.toString(quote(resourceAssignmentStatus.split(","))).replace("[", "").replace("]", "");
        sql = sql.replace(":resourceAssignmentStatus", status);
        if (resourceCount.equalsIgnoreCase("Multiple")) sql = sql.replace("AND nr.Total = 1", "AND nr.Total > 1");

        if (userProfileName.equals("Contractor Admin")) sql = sql.replace("--INNER JOIN ContractorAdmin", "INNER JOIN ContractorAdmin");
        if (userProfileName.equals("Contractor Technician")) sql = sql.replace("--INNER JOIN ContractorTechnician", "INNER JOIN ContractorTechnician");
        if (userProfileName.contains("City")) sql = sql.replace("--INNER JOIN CityTech", "INNER JOIN CityTech");

        logger.debug("getJobWithResourceCount: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public String getRandomAssetSubTypeName() {
        String sql = "SELECT TOP(1) Name AS AssetSubType"
                + " FROM AssetSubType"
                + " ORDER BY NEWID()";

        logger.debug("getRandomAssetSubTypeName: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getRandomAssetTypeName() {
        String sql = "SELECT TOP(1) Name AS AssetType"
                + " FROM AssetType"
                + " ORDER BY NEWID()";

        logger.debug("getRandomAssetTypeName: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getRandomAssetClassificationName() {
        String sql = "SELECT TOP(1) Name AS AssetClassification"
                + " FROM AssetClassification"
                + " WHERE DATALENGTH(Name) > 0"
                + " ORDER BY NEWID()";

        logger.debug("getRandomAssetClassificationName: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getRandomAssetTag() {
        String sql = "SELECT TOP(1) AssetTag"
                + " FROM Asset"
                + " WHERE AssetTag IS NOT NULL"
                + " ORDER BY NEWID()";

        logger.debug("getRandomAssetTag: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getRandomBrand() {
        String sql = "SELECT TOP(1) Name AS Brand"
                + " FROM Brand"
                + " ORDER BY NEWID()";

        logger.debug("getRandomBrand: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getRandomFaultType() {
        String sql = "SELECT TOP(1) Name AS FaultType"
                + " FROM FaultType"
                + " ORDER BY NEWID()";

        logger.debug("getRandomFaultType: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getRandomLocationName() {
        String sql = "SELECT TOP(1) Name As Location"
                + " FROM Location"
                + " ORDER BY NEWID()";

        logger.debug("getRandomLocationName: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getRandomSiteName() {
        String sql = "SELECT TOP(1) Name AS Site"
                + " FROM Site"
                + " ORDER BY NEWID()";

        logger.debug("getRandomSiteName: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getRandomFaultTypeForAssetSubType(int assetSubTypeId) {
        String sql = "SELECT TOP(1) ft.Name AS FaultTypeName"
                + " FROM FaultType ft"
                + " INNER JOIN FaultPriorityMapping fpm ON ft.Id = fpm.FaultTypeId"
                + " INNER JOIN AssetSubType ast ON fpm.AssetSubTypeId = ast.Id"
                + " WHERE ast.Id = %d"
                + " AND ft.Active = 1"
                + " ORDER BY NEWID()";

        sql = String.format(sql, assetSubTypeId);
        logger.debug("getRandomFaultTypeForAssetSubType: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getRandomAssetClassificationForAssetSubType(int assetSubTypeId) {
        String sql = "SELECT TOP(1) ac.Name AS AssetClassification"
                + " FROM AssetClassification ac"
                + " INNER JOIN AssetSubType ast ON ac.AssetSubTypeId = ast.Id"
                + " WHERE ast.Id = %d AND ac.Active = 1"
                + " ORDER BY NEWID()";

        sql = String.format(sql, assetSubTypeId);
        logger.debug("getRandomAssetClassificationForAssetSubType: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getRandomPortal() {
        String sql = "SELECT TOP(1) Name"
                + " FROM Portals"
                + " WHERE Active = 1"
                + " ORDER BY NEWID()";

        logger.debug("getRandomPortal: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public List<String> getRandomPermissions(int numberOfPermissions) {
        String sql = "SELECT TOP(%d) Name"
                + " FROM AspNetRoles"
                + " ORDER BY NEWID()";

        sql = String.format(sql, numberOfPermissions);
        logger.debug("getRandomPermissions: " + sql);
        return (jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), String.class));
    }

    public List<String> getRandomMonitorAreas(int numberOfMonitors) {
        String sql = "SELECT TOP(%d) DisplayName"
                + " FROM MonitorArea"
                + " ORDER BY NEWID()";

        sql = String.format(sql, numberOfMonitors);
        logger.debug("getRandomMonitorAreas: " + sql);
        return (jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), String.class));
    }

    public List<Map<String, Object>> getIncidentCriteriaGrid(Integer isCriteriaActive, String potentialForced, String siteBrand, String siteType, String assetSubtype, String classification, String faultType, String incidentType) {
        if(siteBrand.contains("'")) {
            siteBrand = siteBrand.replaceAll("'", "''");
        }

        if(siteType.contains("'")) {
            siteType = siteType.replaceAll("'", "''");
        }

        String sql = "SELECT lict.Name AS 'Potential/Forced', ISNULL(b.Name,'') AS 'Site Brand', ISNULL(st.Name,'') AS 'Site type', ast.Name AS 'Asset Sub type',"
                + " ISNULL(ac.Name,'') AS Classification, ft.Name AS 'Fault type', it.Description AS 'Incident type'"
                + " FROM LinkedIncidentCriterion lic"
                + " LEFT JOIN LinkedIncidentCriterionType lict ON lic.LinkedIncidentCriterionTypeId = lict.Id"
                + " LEFT JOIN Brand b ON lic.BrandId = b.Id"
                + " LEFT JOIN SiteTypes st ON lic.SiteTypeId = st.Id"
                + " LEFT JOIN AssetSubType ast ON lic.AssetSubTypeId = ast.Id"
                + " LEFT JOIN AssetClassification ac ON lic.AssetClassificationId = ac.Id"
                + " LEFT JOIN FaultType ft ON lic.FaultTypeId = ft.Id"
                + " LEFT JOIN IncidentType it ON lic.IncidentTypeId = it.Id"
                + " WHERE lic.Active = %d"
                + " AND lict.Name = '%s'"
                + " AND b.Name = '%s'"
                + " AND st.Name = '%s'"
                + " AND ast.Name = '%s'";

        if(classification.equals("None")) {
            sql = sql + " AND ft.Name = '%s'"
                    + " AND it.Description = '%s'";

            sql = String.format(sql, isCriteriaActive, potentialForced, siteBrand, siteType, assetSubtype, faultType, incidentType);
        } else {
            sql = sql + " AND ac.Name = '%s'"
                    + " AND ft.Name = '%s'"
                    + " AND it.Description = '%s'";

            sql = String.format(sql, isCriteriaActive, potentialForced, siteBrand, siteType, assetSubtype, classification, faultType, incidentType);
        }

        logger.debug("getIncidentCriteriaGrid: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public void deleteQuestionAddedByAutoTest(String questionName) {
        String sql = "DELETE FROM Question WHERE Name = '%s'";
        sql = String.format(sql, questionName);
        logger.debug("deleteQuestionAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteAllQuestionAddedByAutoTest() {
        String sql = "DELETE FROM Question WHERE Name LIKE 'TestAuto%'";
        logger.debug("deleteAllQuestionAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteAllQuestionOptionAddedByAutoTest() {
        String sql = "DELETE FROM QuestionToQuestionOption WHERE QuestionId IN (SELECT Id FROM Question WHERE Name LIKE 'TestAuto%')";
        logger.debug("deleteQuestionOptionAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteQuestionOptionAddedByAutoTest(String questionName) {
        String sql = "DELETE FROM QuestionToQuestionOption WHERE QuestionId = (SELECT Id FROM Question WHERE Name = '%s')";
        sql = String.format(sql, questionName);
        logger.debug("deleteQuestionOptionAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteLinkedIncidentCriterionAddedByAutoTest() {
        String sql = "DELETE FROM LinkedIncidentCriterion WHERE CAST(CreatedOn AS DATE) =  CAST(GETDATE() AS DATE)";
        logger.debug("deleteLinkedIncidentCriterionAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteRulesEngineLookUpConditionAddedByAutoTest(String ruleName) {
        String sql = "DELETE FROM RulesEngineLookupCondition WHERE RuleId = (SELECT Id FROM RulesEngineRule WHERE Name = '%s')";
        sql = String.format(sql, ruleName);
        logger.debug("deleteRulesEngineLookUpConditionAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteAllRulesEngineLookUpConditionAddedByAutoTest() {
        String sql = "DELETE FROM RulesEngineLookupCondition WHERE RuleId = (SELECT Id FROM RulesEngineRule WHERE Name LIKE 'TestAuto%')";
        logger.debug("deleteAllRulesEngineLookUpConditionAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteRuleAddedByAutoTest(String ruleName) {
        String sql = "DELETE FROM RulesEngineRule WHERE Name = '%s'";
        sql = String.format(sql, ruleName);
        logger.debug("deleteRuleAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteAllRulesAddedByAutoTest() {
        String sql = "DELETE FROM RulesEngineRule WHERE Name LIKE 'TestAuto%'";
        logger.debug("deleteAllRulesAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteUserProfileAddedByAutoTest(String userProfileName) {
        String sql = "DELETE FROM UserProfile WHERE Name = '%s'";
        sql = String.format(sql, userProfileName);
        logger.debug("deleteUserProfileAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteAllUserProfilesAddedByAutoTest() {
        String sql = "DELETE FROM UserProfile WHERE Name LIKE 'TestAuto%'";
        logger.debug("deleteAllUserProfilesAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceCallerAddedByAutoTest(int resourceId) {
        String sql = "DELETE FROM ResourceCaller WHERE ResourceId = %d";
        sql = String.format(sql, resourceId);
        logger.debug("deleteResourceCallerAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceCallersAddedByAutoTest(String resourceIds) {
        String sql = "DELETE FROM ResourceCaller WHERE ResourceId IN (%s)";
        sql = String.format(sql, resourceIds);
        logger.debug("deleteResourceCallersAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceEmailAddressAddedByAutoTest(int resourceId) {
        String sql = "DELETE FROM ResourceEmailAddress WHERE ResourceId = %d";
        sql = String.format(sql, resourceId);
        logger.debug("deleteResourceEmailAddressAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceEmailAddressesAddedByAutoTest(String resourceIds) {
        String sql = "DELETE FROM ResourceEmailAddress WHERE ResourceId IN (%s)";
        sql = String.format(sql, resourceIds);
        logger.debug("deleteResourceEmailAddressesAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourcePhoneNumberAddedByAutoTest(int resourceId) {
        String sql = "DELETE FROM ResourcePhoneNumber WHERE ResourceId = %d";
        sql = String.format(sql, resourceId);
        logger.debug("deleteResourcePhoneNumberAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourcePhoneNumbersAddedByAutoTest(String resourceIds) {
        String sql = "DELETE FROM ResourcePhoneNumber WHERE ResourceId IN (%s)";
        sql = String.format(sql, resourceIds);
        logger.debug("deleteResourcePhoneNumbersAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteApplicationUserAddedByAutoTest(int resourceId) {
        String sql = "DELETE FROM ApplicationUser WHERE ResourceId = %d";
        sql = String.format(sql, resourceId);
        logger.debug("deleteApplicationUserAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteApplicationUsersAddedByAutoTest(String resourceIds) {
        String sql = "DELETE FROM ApplicationUser WHERE ResourceId IN (%s)";
        sql = String.format(sql, resourceIds);
        logger.debug("deleteApplicationUsersAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteApplicationUserAddedByAutoTestUserProfile(int userProfileId) {
        String sql = "DELETE FROM ApplicationUser WHERE ResourceId = %d";
        sql = String.format(sql, userProfileId);
        logger.debug("deleteApplicationUserAddedByAutoTestUserProfile: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteApplicationUserAddedByAutoTestUserProfile(String userProfileIds) {
        String sql = "DELETE FROM ApplicationUser WHERE ResourceId IN (%s)";
        sql = String.format(sql, userProfileIds);
        logger.debug("deleteApplicationUserAddedByAutoTestUserProfile: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteApplicationUserAddedByAutoTestUser(String userName) {
        String sql = "DELETE FROM ApplicationUser WHERE UserName = '%s'";
        sql = String.format(sql, userName);
        logger.debug("deleteApplicationUserAddedByAutoTestUser: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteAllApplicationUsersAddedByAutoTestUser() {
        String sql = "DELETE FROM ApplicationUser WHERE UserName LIKE 'TestAuto%'";
        logger.debug("deleteAllApplicationUsersAddedByAutoTestUser: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceNotificationMethodAddedByAutoTest(int resourceId) {
        String sql = "DELETE FROM ResourceNotificationMethod WHERE ResourceId = %d";
        sql = String.format(sql, resourceId);
        logger.debug("deleteResourceNotificationMethodAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceNotificationMethodsAddedByAutoTest(String resourceIds) {
        String sql = "DELETE FROM ResourceNotificationMethod WHERE ResourceId IN (%s)";
        sql = String.format(sql, resourceIds);
        logger.debug("deleteResourceNotificationMethodsAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceAddedByAutoTest(String resourceName) {
        String sql = "DELETE FROM Resource WHERE Name = '%s'";
        sql = String.format(sql, resourceName);
        logger.debug("deleteResourceAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourcesAddedByAutoTest() {
        String sql = "DELETE FROM Resource WHERE Name LIKE 'TestAuto%'";
        logger.debug("deleteResourcesAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteAssetClassificationSiteContractorMappingAddedByAutoTest(int resourceId) {
        String sql = "DELETE FROM AssetClassificationSiteContractorMapping WHERE ResourceId = %d";
        sql = String.format(sql, resourceId);
        logger.debug("deleteAssetClassificationSiteContractorMappingAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteAssetClassificationSiteContractorMappingAddedByAutoTest(String resourceIds) {
        String sql = "DELETE FROM AssetClassificationSiteContractorMapping WHERE ResourceId IN (%s)";
        sql = String.format(sql, resourceIds);
        logger.debug("deleteAssetClassificationSiteContractorMappingAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceAssignmentStatusChangeAddedByAutoTest(int resourceAssignmentEventId) {
        String sql = "DELETE FROM ResourceAssignmentStatusChange WHERE ResourceAssignmentEventId = %d";
        sql = String.format(sql, resourceAssignmentEventId);
        logger.debug("deleteResourceAssignmentStatusChangeAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceAssignmentStatusChangeAddedByAutoTest(String resourceAssignmentEventIds) {
        String sql = "DELETE FROM ResourceAssignmentStatusChange WHERE ResourceAssignmentEventId IN (%s)";
        sql = String.format(sql, resourceAssignmentEventIds);
        logger.debug("deleteResourceAssignmentStatusChangeAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceAssignmentEventAddedByAutoTest(int resourceAssignmentId) {
        String sql = "DELETE FROM ResourceAssignmentEvent WHERE ResourceAssignmentId = %d";
        sql = String.format(sql, resourceAssignmentId);
        logger.debug("deleteResourceAssignmentEventAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceAssignmentEventsAddedByAutoTest(String resourceAssignmentIds) {
        String sql = "DELETE FROM ResourceAssignmentEvent WHERE ResourceAssignmentId IN (%s)";
        sql = String.format(sql, resourceAssignmentIds);
        logger.debug("deleteResourceAssignmentEventsAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFundingRequestAddedByAutoTest(int resourceAssignmentId) {
        String sql = "DELETE FROM FundingRequest WHERE ResourceAssignmentId = %d";
        sql = String.format(sql, resourceAssignmentId);
        logger.debug("deleteFundingRequestAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFundingRequestAddedByAutoTest(String resourceAssignmentIds) {
        String sql = "DELETE FROM FundingRequest WHERE ResourceAssignmentId IN (%s)";
        sql = String.format(sql, resourceAssignmentIds);
        logger.debug("deleteFundingRequestAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteAdditionalResourceRequirementAddedByAutoTest(int additionalResourceAssignmentId) {
        String sql = "DELETE FROM AdditionalResourceRequirement WHERE AdditionalResourceAssignmentId = %d";
        sql = String.format(sql, additionalResourceAssignmentId);
        logger.debug("deleteAdditionalResourceRequirementAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteAdditionalResourceRequirementAddedByAutoTest(String additionalResourceAssignmentIds) {
        String sql = "DELETE FROM AdditionalResourceRequirement WHERE AdditionalResourceAssignmentId IN (%s)";
        sql = String.format(sql, additionalResourceAssignmentIds);
        logger.debug("deleteAdditionalResourceRequirementAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteOriginatingResourceAssignmentAddedByAutoTest(int originatingResourceAssignmentId) {
        String sql = "DELETE FROM AdditionalResourceRequirement WHERE OriginatingResourceAssignmentId = %d";
        sql = String.format(sql, originatingResourceAssignmentId);
        logger.debug("deleteOriginatingResourceAssignmentAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteOriginatingResourceAssignmentsAddedByAutoTest(String originatingResourceAssignmentIds) {
        String sql = "DELETE FROM AdditionalResourceRequirement WHERE OriginatingResourceAssignmentId IN (%s)";
        sql = String.format(sql, originatingResourceAssignmentIds);
        logger.debug("deleteOriginatingResourceAssignmentsAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteSiteVisitsAddedByAutoTest(int resourceAssignmentId) {
        String sql = "DELETE FROM SiteVisits WHERE ResourceAssignmentId = %d";
        sql = String.format(sql, resourceAssignmentId);
        logger.debug("deleteSiteVisitsAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteSiteVisitsAddedByAutoTest(String resourceAssignmentIds) {
        String sql = "DELETE FROM SiteVisits WHERE ResourceAssignmentId IN (%s)";
        sql = String.format(sql, resourceAssignmentIds);
        logger.debug("deleteSiteVisitsAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceAssignmentAddedByAutoTest(int resourceId) {
        String sql = "DELETE FROM ResourceAssignment WHERE ResourceId = %d";
        sql = String.format(sql, resourceId);
        logger.debug("deleteResourceAssignmentAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceAssignmentAddedByAutoTest(String resourceIds) {
        String sql = "DELETE FROM ResourceAssignment WHERE ResourceId IN (%s)";
        sql = String.format(sql, resourceIds);
        logger.debug("deleteResourceAssignmentAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteCoveringResourceAvailabilityAddedByAutoTest(String resourceIds) {
        String sql = "DELETE FROM ResourceAvailability WHERE CoveringResourceId IN (%s)";
        sql = String.format(sql, resourceIds);
        logger.debug("deleteCoveringResourceAvailabilityAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceAvailabilityAddedByAutoTest(String resourceIds) {
        String sql = "DELETE FROM ResourceAvailability WHERE ResourceId IN (%s)";
        sql = String.format(sql, resourceIds);
        logger.debug("deleteResourceAvailabilityAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceWorkingHoursAddedByAutoTest(int resourceId) {
        String sql = "DELETE FROM ResourceWorkingHours WHERE ResourceId = %d";
        sql = String.format(sql, resourceId);
        logger.debug("deleteResourceWorkingHoursAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceWorkingHoursAddedByAutoTest(String resourceIds) {
        String sql = "DELETE FROM ResourceWorkingHours WHERE ResourceId IN (%s)";
        sql = String.format(sql, resourceIds);
        logger.debug("deleteResourceWorkingHoursAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceProfileWorkingHoursAddedByAutoTest(int resourceProfileId) {
        String sql = "DELETE FROM ResourceProfileWorkingHours WHERE ResourceProfileId = %d";
        sql = String.format(sql, resourceProfileId);
        logger.debug("deleteResourceProfileWorkingHoursAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceProfileWorkingHoursAddedByAutoTest(String resourceProfileIds) {
        String sql = "DELETE FROM ResourceProfileWorkingHours WHERE ResourceProfileId IN (%s)";
        sql = String.format(sql, resourceProfileIds);
        logger.debug("deleteResourceProfileWorkingHoursAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceProfileLaborRateAddedByAutoTest(int resourceProfileId) {
        String sql = "DELETE FROM ResourceProfileLabourRate WHERE ResourceProfileId = %d";
        sql = String.format(sql, resourceProfileId);
        logger.debug("deleteResourceProfileLaborRateAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceProfileLaborRateAddedByAutoTest(String resourceProfileIds) {
        String sql = "DELETE FROM ResourceProfileLabourRate WHERE ResourceProfileId IN (%s)";
        sql = String.format(sql, resourceProfileIds);
        logger.debug("deleteResourceProfileLaborRateAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceProfileAddedByAutoTest(int resourceProfileId) {
        String sql = "DELETE FROM ResourceProfile WHERE Id = %d";
        sql = String.format(sql, resourceProfileId);
        logger.debug("deleteResourceProfileAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteResourceProfileAddedByAutoTest(String resourceProfileIds) {
        String sql = "DELETE FROM ResourceProfile WHERE Id IN (%s)";
        sql = String.format(sql, resourceProfileIds);
        logger.debug("deleteResourceProfileAddedByAutoTest: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromSiteResourceTable(int resourceId, int siteId) {
        String sql = "DELETE FROM SiteResource WHERE ResourceId = %d AND SiteId = %d";
        sql = String.format(sql, resourceId, siteId);
        logger.debug("deleteFromSiteResourceTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromSiteResourceTable(int resourceId) {
        String sql = "DELETE FROM SiteResource WHERE ResourceId = %d";
        sql = String.format(sql, resourceId);
        logger.debug("deleteFromSiteResourceTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromSiteResourceTable(String resourceIds) {
        String sql = "DELETE FROM SiteResource WHERE ResourceId IN (%s)";
        sql = String.format(sql, resourceIds);
        logger.debug("deleteFromSiteResourceTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromSiteResourceSyncTable(int resourceId) {
        String sql = "DELETE FROM SiteResourceSync WHERE ResourceId = %d";
        sql = String.format(sql, resourceId);
        logger.debug("deleteFromSiteResourceSyncTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromSiteResourceSyncTable(String resourceIds) {
        String sql = "DELETE FROM SiteResourceSync WHERE ResourceId IN (%s)";
        sql = String.format(sql, resourceIds);
        logger.debug("deleteFromSiteResourceSyncTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromRotaEntryTable(int resourceId) {
        String sql = "DELETE FROM RotaEntry WHERE ResourceId = %d";
        sql = String.format(sql, resourceId);
        logger.debug("deleteFromRotaEntryTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromRotaEntryTable(String resourceIds) {
        String sql = "DELETE FROM RotaEntry WHERE ResourceId IN (%s)";
        sql = String.format(sql, resourceIds);
        logger.debug("deleteFromRotaEntryTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromPermissionTable(int userProfileId) {
        String sql = "DELETE FROM Permission WHERE UserProfileId = %d";
        sql = String.format(sql, userProfileId);
        logger.debug("deleteFromPermissionTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromPermissionTable(String userProfileIds) {
        String sql = "DELETE FROM Permission WHERE UserProfileId IN (%s)";
        sql = String.format(sql, userProfileIds);
        logger.debug("deleteFromPermissionTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromUserProfileToMonitorAreaTable(int userProfileId) {
        String sql = "DELETE FROM UserProfileToMonitorArea WHERE UserProfileId = %d";
        sql = String.format(sql, userProfileId);
        logger.debug("deleteFromUserProfileToMonitorAreaTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromUserProfileToMonitorAreaTable(String userProfileIds) {
        String sql = "DELETE FROM UserProfileToMonitorArea WHERE UserProfileId IN (%s)";
        sql = String.format(sql, userProfileIds);
        logger.debug("deleteFromUserProfileToMonitorAreaTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromUserImpersonationTable(String userId) {
        String sql = "DELETE FROM UserImpersonation WHERE ImpersonatedUser = '%s'";
        sql = String.format(sql, userId);
        logger.debug("deleteFromUserImpersonationTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteUsersFromUserImpersonationTable(String userIds) {
        String sql = "DELETE FROM UserImpersonation WHERE ImpersonatedUser IN (%s)";
        sql = String.format(sql, userIds);
        logger.debug("deleteFromUserImpersonationTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteUsersFromLockAuditTable(String userIds) {
        String sql = "DELETE FROM LockAudit WHERE ApplicationUserId IN (%s)";
        sql = String.format(sql, userIds);
        logger.debug("deleteUsersFromLockAuditTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteUsersFromIncidentLockTable(String userIds) {
        String sql = "DELETE FROM IncidentLock WHERE LockedById IN (%s)";
        sql = String.format(sql, userIds);
        logger.debug("deleteUsersFromIncidentLockTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromAssetNoteTable(int assetId) {
        String sql = "DELETE FROM AssetNote WHERE AssetId = %d";
        sql = String.format(sql, assetId);
        logger.debug("deleteFromAssetNoteTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromAssetNoteTable(String assetIds) {
        String sql = "DELETE FROM AssetNote WHERE AssetId IN (%s)";
        sql = String.format(sql, assetIds);
        logger.debug("deleteFromAssetNoteTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromAssetTimelineEventTable(int assetId) {
        String sql = "DELETE FROM AssetTimelineEvent WHERE AssetId = %d";
        sql = String.format(sql, assetId);
        logger.debug("deleteFromAssetTimelineEventTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromAssetTimelineEventTable(String assetIds) {
        String sql = "DELETE FROM AssetTimelineEvent WHERE AssetId IN (%s)";
        sql = String.format(sql, assetIds);
        logger.debug("deleteFromAssetTimelineEventTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromAssetTable(String assetTag) {
        String sql = "DELETE FROM Asset WHERE AssetTag = '%s'";
        sql = String.format(sql, assetTag);
        logger.debug("deleteFromAssetTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteAllFromAssetTable() {
        String sql = "DELETE FROM Asset WHERE AssetTag LIKE 'TestAuto%'";
        logger.debug("deleteAllFromAssetTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteAllFromSunTCode5Table() {
        String sql = "DELETE FROM %portaldb.[tblSun6TCode5] WHERE Code LIKE 'TEST%'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("deleteAllFromSunTCode5Table: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteAllFromSunTCode6Table() {
        String sql = "DELETE FROM %portaldb.[tblSun6TCode6] WHERE Code LIKE 'TEST%'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("deleteAllFromSunTCode6Table: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteAllFromSunTCode7Table() {
        String sql = "DELETE FROM %portaldb.[tblSun6TCode7] WHERE Code LIKE 'TEST%'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("deleteAllFromSunTCode7Table: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteAllFromTCodeSuppliersTable() {
        String sql = "DELETE FROM %portaldb.[tbltcodesuppliers] WHERE Code LIKE 'TEST%'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("deleteAllFromTCodeSuppliersTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromSunTCode5Table(String code) {
        String sql = "DELETE FROM %portaldb.[tblSun6TCode5] WHERE Code = '%s'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, code);
        logger.debug("deleteFromSunTCode5Table: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromSunTCode6Table(String code) {
        String sql = "DELETE FROM %portaldb.[tblSun6TCode6] WHERE Code = '%s'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, code);
        logger.debug("deleteFromSunTCode6Table: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void deleteFromLinkedIncidentCriterionTable(String subType, String faultType, String incidentType) {
        String sql = "DELETE lic FROM LinkedIncidentCriterion lic"
                + " JOIN AssetSubType ast ON lic.AssetSubTypeId = ast.Id"
                + " JOIN FaultType ft ON lic.FaultTypeId = ft.Id"
                + " JOIN IncidentType it ON lic.IncidentTypeId = it.Id"
                + " WHERE ast.Name = '%s'"
                + " AND ft.Name = '%s'"
                + " AND it.Description = '%s'";

        sql = String.format(sql, subType, faultType, incidentType);
        logger.debug("deleteFromLinkedIncidentCriterionTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public Map<String, Object> getFaultDetailsForSite(int siteId) {
        String sql = "SELECT TOP(1) fpm.FaultTypeId, fpm.ResponsePriorityId, fpm.AssetClassificationId"
                + " FROM uvw_faultprioritymappings fpm"
                + " JOIN Site s ON fpm.SiteTypeId = s.SiteTypeId"
                + " WHERE s.Id = %d"
                + " AND fpm.FaultTypeId IS NOT NULL AND fpm.RequiresReview = 0 ";

        sql = String.format(sql, siteId);
        logger.debug("getFaultTypeIdForSite: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public String getUsState() {
        String sql = "SELECT TOP(1) FullName"
                + " FROM USState ORDER BY NEWID()";

        logger.debug("getUsState: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public Map<String, Object> getPurchaseOrderDetailsForImport(String supplierCode) {
        String sql = "SELECT TOP(1) fld_str_SupplierID Supplier, fld_str_PONumber PONumber, fld_dat_DateRaised OrderDate, fld_int_POTypeID POType, fld_int_HelpdeskFaultID Job "
                //                + ", tpc.fld_str_PartCode PartNumber, tpc.fld_str_Description PartDescription, tpb.fld_cur_CostEach UnitPrice "
                + ", tpo.fld_bit_RetrospectiveOrder Retrospective, b.AbbreviatedAlias Budget "
                + "FROM %portaldb.tblPO tpo "
                //                + "JOIN %portaldb.tblPricebook tpb ON tpo.fld_str_SupplierID = tpb.fld_str_SupplierCode "
                //                + "JOIN %portaldb.tblPartCodes tpc ON tpb.fld_str_PartCode = tpc.fld_str_PartCode "
                + "JOIN %helpdeskdb.Budget b ON tpo.fld_int_Budget = b.Id "
                + "WHERE tpo.fld_str_ImportedOn IS NULL "
                + "AND fld_int_POTypeID = 5 "
                + "AND fld_str_SupplierID = :supplierCode "
                + "ORDER BY fld_dat_DateRaised DESC ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = sql.replace(":supplierCode", quote(supplierCode));
        logger.debug("getPurchaseOrderDetailsForImport: " + sql);
        try {
            return jdbc_helpdesk.queryForMap(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getPurchaseOrderDetailsForImport() {
        String sql = "SELECT TOP(1) fld_str_SupplierID Supplier, fld_str_PONumber PONumber, fld_dat_DateRaised OrderDate, fld_int_POTypeID POType, fld_int_HelpdeskFaultID Job, "
                + "tpc.fld_str_PartCode PartNumber, tpc.fld_str_Description PartDescription, tpb.fld_cur_CostEach UnitPrice, tpo.fld_bit_RetrospectiveOrder Retrospective, b.AbbreviatedAlias Budget "
                + "FROM %portaldb.tblPO tpo "
                + "JOIN %portaldb.tblPricebook tpb ON tpo.fld_str_SupplierID = tpb.fld_str_SupplierCode "
                + "JOIN %portaldb.tblPartCodes tpc ON tpb.fld_str_PartCode = tpc.fld_str_PartCode "
                + "JOIN %helpdeskdb.Budget b ON tpo.fld_int_Budget = b.Id "
                + "WHERE tpo.fld_str_ImportedOn IS NULL "
                + "AND fld_str_SupplierID LIKE 'SO%' AND fld_int_POTypeID = 5 "
                + "AND tpo.fld_dat_DateRaised > GETDATE() - 30 ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getPurchaseOrderDetailsForImport: " + sql);
        try {
            return jdbc_helpdesk.queryForMap(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getSupplierCodeDetails(String supplierCode) {
        String sql = "SELECT * FROM %portaldb.tblPOImportRestriction"
                + " WHERE Code = '%s'";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, supplierCode);
        logger.debug("getSupplierCodeDetails: " + sql);
        try {
            return jdbc_helpdesk.queryForMap(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isSupplierSetUpWithCode(String supplierCode) {
        String sql = "SELECT COUNT(Code) FROM %portaldb.tblPOImportRestriction WHERE Code = '%s'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, supplierCode);

        logger.debug("isSupplierSetUpWithCode: " + sql);
        int result = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        return result > 0;
    }

    public void setUpSupplierWithcode(String supplierCode, String supplierName, String prefix) {
        String sql = "INSERT INTO %portaldb.tblPOImportRestriction (Code, Name, OrderPrefix, OrderRangeStart, OrderRangeEnd)"
                + " VALUES ('%s', '%s', '%s', '1', '99999999')";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, supplierCode, supplierName, prefix);
        logger.debug("setUpSupplierWithcode: " + sql);
        try {
            jdbc_helpdesk.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Exception " + e.getMessage());
        }
    }

    public String getSupplierName(String supplierCode) {
        String sql = "SELECT Name FROM uvw_SupplierTCodes"
                + " WHERE Code = '%s'";

        sql = String.format(sql, supplierCode);
        logger.debug("getSupplierName: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getAssetDetails() {
        String sql = "SELECT TOP(1) at.Name AssetType, ast.Name AssetSubtype, ac.Name AssetClassification"
                + " FROM AssetType at"
                + " JOIN AssetSubType ast ON at.Id = ast.AssetTypeId"
                + " JOIN AssetClassification ac ON ast.Id = ac.AssetSubTypeId"
                + " WHERE DATALENGTH(ast.Name) > 0";
        //                + " AND DATALENGTH(ac.Name) > 0"
        //                + " ORDER BY NEWID()";

        // There is currently no asset classification mapping on Advocate. Once mapping is added,
        // uncomment the 2 lines above and remove the following two lines.
        sql = getWebDriver().getCurrentUrl().contains("usad") ? sql : sql + " AND DATALENGTH(ac.Name) > 0";
        sql = sql + " ORDER BY NEWID()";
        logger.debug("getAssetDetails: " + sql);
        try {
            return jdbc_helpdesk.queryForMap(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getAssetSubTypeAndClassification(int assetClassificationId) {
        String sql = "SELECT ac.Name AssetClassification, ast.Name AssetSubType"
                + " FROM AssetClassification ac"
                + " JOIN AssetSubType ast ON ac.AssetSubTypeId = ast.Id"
                + " WHERE ac.Id = %d";

        sql = String.format(sql, assetClassificationId);
        logger.debug("getAssetSubTypeAndClassification: " + sql);
        try {
            return jdbc_helpdesk.queryForMap(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public String getFaultPriorityFromId(Integer id) {
        String sql = String.format("SELECT TOP(1) Detail FaultPriority FROM uvw_FaultPriorityMappings WHERE Priority = %d", id);
        logger.debug("getFaultPriorityFromId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getLocationNameFromId(Integer locationId) {
        String sql = String.format("SELECT Name Location FROM Location WHERE Id = %d", locationId);
        logger.debug("getLocationNameFromId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getFaultTypeNameFromId(Integer faultTypeId) {
        String sql = String.format("SELECT Name FROM FaultType WHERE Id = %d", faultTypeId);
        logger.debug("getFaultTypeNameFromId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public List<String> getSOSupplierCodes(int count){
        String sql = "SELECT DISTINCT TOP("
                + count
                + ") fld_str_SupplierID FROM Portal.tblPO tpo "
                + "INNER JOIN Portal.tblPricebook tpb ON tpo.fld_str_SupplierID = tpb.fld_str_SupplierCode "
                + "INNER JOIN Portal.tblPartCodes tpc ON tpb.fld_str_PartCode = tpc.fld_str_PartCode "
                + "WHERE fld_str_SupplierID LIKE 'SO%' AND tpo.fld_str_ImportedOn IS NULL "
                + "AND tpo.fld_str_SupplierID NOT IN (SELECT fld_str_SupplierID FROM Portal.tblPOImport)";
        logger.debug("getSOSupplierCodes: " + sql);
        return jdbc_portal.queryForList(sql, new MapSqlParameterSource(), String.class);
    }

    public List<String> getSOSupplierCodesAdvocate(int count){
        //Only 2 suppliers working atm in advocate
        String sql = "SELECT DISTINCT TOP("
                + count
                + ") fld_str_SupplierID FROM Portal.tblPO tpo "
                + "INNER JOIN Portal.tblPricebook tpb ON tpo.fld_str_SupplierID = tpb.fld_str_SupplierCode "
                + "INNER JOIN Portal.tblPartCodes tpc ON tpb.fld_str_PartCode = tpc.fld_str_PartCode "
                + "WHERE fld_str_SupplierID IN ('SO:C3039', 'SO:C3062') AND tpo.fld_str_ImportedOn IS NULL "
                + "AND tpo.fld_str_SupplierID NOT IN (SELECT fld_str_SupplierID FROM Portal.tblPOImport)";
        logger.debug("getSOSupplierCodes: " + sql);
        return jdbc_portal.queryForList(sql, new MapSqlParameterSource(), String.class);
    }

    public List<String> getSuppliersNotUsingPortalForInvoicing(int count) {
        String sql = "SELECT DISTINCT TOP(" + count + ") hdr.fld_str_SupplierID SupplierId "
                + "FROM Portal.tblHelpDeskResource hdr "
                + "INNER JOIN Resource r ON hdr.fld_int_ID = r.EpochId "
                + "INNER JOIN Portal.tblTCodeSuppliers tcs ON tcs.Code = hdr.fld_str_SupplierID "
                + "INNER JOIN Portal.tblPO tpo ON tpo.fld_str_SupplierID = hdr.fld_str_SupplierID "
                //                + "INNER JOIN Portal.tblPricebook tpb ON tpo.fld_str_SupplierID = tpb.fld_str_SupplierCode "
                //                + "INNER JOIN Portal.tblPartCodes tpc ON tpb.fld_str_PartCode = tpc.fld_str_PartCode "
                + "INNER JOIN dbo.Budget b ON tpo.fld_int_Budget = b.Id "
                + "WHERE hdr.fld_bit_OnlineInvoicingActive = 0 "
                + "AND hdr.fld_str_SupplierID LIKE 'SO:%' "
                + "AND r.Active = 1 AND r.ResourceProfileId = 138 "
                + "AND hdr.fld_str_SupplierID NOT IN (SELECT fld_str_SupplierID FROM Portal.tblPOImport) "
                + "AND fld_int_POTypeID = 5";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getSuppliersNotUsingPortalForInvoicing: " + sql);

        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), String.class);
    }

    public boolean isPOImported(String filename) {
        String sql = String.format("SELECT COUNT(*) FROM Portal.tblPOImport WHERE fld_Str_FileName = '%s'", filename);
        logger.debug("isPOImported: " + sql);
        int result = jdbc_portal.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        return result > 0;
    }

    public Integer getResourceIdForJob(Integer jobReference) {
        String sql = String.format("SELECT ra.Id FROM Job j " +
                " INNER JOIN ResourceAssignment ra ON j.Id = ra.JobId " +
                " WHERE j.JobReference = %d ", jobReference);
        logger.debug("getResourceIdForJob: " + sql);
        return jdbc_portal.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public boolean isReverseDatePresentInDb() {
        String sql = "SELECT COUNT(1) FROM SystemConfigDateTime WHERE FormatKey = 'reversedate'";

        logger.debug("isReverseDatePresentInDb");
        try {
            int count =  jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
            return (count > 0);
        } catch (Exception e) {
            return false;
        }
    }

    public void insertReverseDateIntoDb() {
        String sql = "INSERT INTO SystemConfigDateTime (Id, SystemConfigId, FormatKey, FormatValue)"
                + " VALUES (13, 1, 'reversedate', 'yyyy/MM/dd')";

        logger.debug("insertReverseDateIntoDb: " + sql);
        try {
            jdbc_helpdesk.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Exception " + e.getMessage());
        }
    }

    public String getRandomActiveAssetId() {
        String sql = "SELECT TOP(1) Id FROM AssetType"
                + " WHERE Active = 1 ORDER BY NEWID()";
        logger.debug("getRandomActiveAssetId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public List<Integer> getAssetIdsCreatedByAutoTest() {

        String sql = "SELECT Id FROM Asset"
                + " WHERE AssetTag LIKE 'TestAuto%'";

        logger.debug("getAssetIdsCreatedByAutoTest: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getAssetAndFaultDetailsForDeferred(String siteName) {
        String sql = "SELECT TOP(1) s.Name Site, at.Name AssetType, ast.Name AssetSubType,"
                + " ac.Name AssetClassification, ft.Name FaultType, fpm1.Detail"
                + " FROM FaultPriorityMapping fpm"
                + " JOIN JobRoutingProcess jrp ON fpm.JobRoutingProcessId = jrp.Id"
                + " JOIN Site s ON fpm.SiteTypeId = s.SiteTypeId"
                + " JOIN AssetClassification ac ON fpm.AssetClassificationId = ac.Id"
                + " JOIN AssetSubType ast ON fpm.AssetSubTypeId = ast.Id"
                + " JOIN AssetType at ON ast.AssetTypeId = at.Id"
                + " JOIN FaultType ft ON fpm.FaultTypeId = ft.Id"
                + " JOIN uvw_FaultPriorityMappings fpm1 ON fpm.Id = fpm1.Id"
                + " WHERE s.Name = '%s'"
                + " AND jrp.NonDeferrable = 0";

        sql = String.format(sql, siteName);
        logger.debug("getAssetAndFaultDetailsForDeferred: " + sql);
        try {
            return jdbc_helpdesk.queryForMap(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }
}