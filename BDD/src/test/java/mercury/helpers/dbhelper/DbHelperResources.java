package mercury.helpers.dbhelper;

import static mercury.helpers.StringHelper.quote;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import mercury.helpers.PropertyHelper;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfigV2.class)
public class DbHelperResources {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private NamedParameterJdbcTemplate jdbc_portal;
    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;
    @Autowired private DbUtilityHelper dbUtilityHelper;
    @Autowired private PropertyHelper propertyHelper;


    public String getRandomActiveResourceName() {

        String sql = "SELECT TOP(1) Summary FROM uvw_ResourceForSearch ORDER BY NEWID()";

        MapSqlParameterSource params = new MapSqlParameterSource();

        logger.debug("getRandomActiveResourceName: " + sql);

        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getRandomActiveResource(){
        String sql = "SELECT TOP(1) rfs.* FROM uvw_ResourceForSearch rfs "
                + "LEFT JOIN ResourceAvailability rav ON rav.ResourceId = rfs.Id AND rav.Active = 1 "
                + "WHERE Summary NOT LIKE '%’%' "
                + "AND rav.Id IS NULL "
                + "ORDER BY NEWID()";
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getRandomActiveResource: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, params).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getContractorWithProfile(String userProfile){
        String sql = "SELECT TOP(1) up.Name AS ProfileName, au.Username AS UserName, r.Name AS ResourceName, r.Id AS ResourceId "
                + "FROM UserProfile up "
                + "INNER JOIN ApplicationUser au ON au.UserProfileId = up.Id "
                + "INNER JOIN Resource r ON au.ResourceId = r.Id "
                + "INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id "
                + "INNER JOIN AssetClassificationSiteContractorMapping acscm ON acscm.ResourceId = r.Id "
                + "INNER JOIN AssetClassificationSite acs ON acscm.AssetClassificationSiteId = acs.Id "
                + "INNER JOIN uvw_FaultPriorityMappings fpm ON fpm.AssetClassificationId = acs.AssetClassificationId "
                + "WHERE up.Active = 1 AND r.Active = 1 AND fpm.FaultPriorityId = 1 "
                + "AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) "
                + "AND up.Name = '%s' "
                + "ORDER BY NEWID()";
        sql = String.format(sql, userProfile);

        logger.debug("getContractorWithProfile: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getResource(String userProfile, int usesEngineers, int siteId){
        String sql = "SELECT TOP(1) up.Name AS ProfileName, au.Username AS UserName, r.Name AS ResourceName, r.Id AS ResourceId "
                + "FROM UserProfile up "
                + "INNER JOIN ApplicationUser au ON au.UserProfileId = up.Id "
                + "INNER JOIN Resource r ON au.ResourceId = r.Id "
                + "INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id "
                + "INNER JOIN AssetClassificationSiteContractorMapping acscm ON acscm.ResourceId = r.Id "
                + "INNER JOIN AssetClassificationSite acs ON acscm.AssetClassificationSiteId = acs.Id "
                + "WHERE up.Active = 1 AND r.Active = 1 "
                + "AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) "
                + "AND up.Name = '%s' "
                + "AND r.UsesEngineers = " + usesEngineers
                + " AND acs.SiteId = " + siteId
                + " ORDER BY NEWID()";
        sql = String.format(sql, userProfile);

        logger.debug("getResource: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    // TODO : fix subselect
    public String getRandomActiveResourceName(String resourceProfileName) {

        String subSelect = "SELECT DISTINCT rp.name from Resource r INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.id " +
                "WHERE rp.ResourceTypeId = 1 AND r.Active = 1 AND rp.Active = 1 AND rp.ActiveForClient = 1 " +
                "AND rp.JobCallouts = 1 ";

        String resourceProfile = resourceProfileName.equalsIgnoreCase("RFM") ? " = 'RFM'" : " IN (" + subSelect +")";

        String sql = "SELECT TOP(1) r.Name FROM Resource r JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id"
                + " LEFT JOIN ResourceAvailability rav ON rav.ResourceId = r.Id WHERE rp.Name :resourceProfile AND r.Active = 1"
                + " AND (rav.ResourceId IS NULL  OR (rav.ResourceId NOT IN (SELECT rav.ResourceId FROM ResourceAvailability rav WHERE (rav.Active = 1"
                + " AND ((rav.StartAt BETWEEN GETDATE() AND DATEADD(day, 14, GETDATE()) OR rav.EndAt BETWEEN GETDATE() AND DATEADD(day, 14, GETDATE()))"
                + " OR (rav.StartAt < GETDATE() AND rav.EndAt > DATEADD(day, 14, GETDATE())))  AND rav.ResourceAvailabilityTypeId NOT IN (4, 5))))) ORDER BY NEWID()";

        MapSqlParameterSource params = new MapSqlParameterSource();
        sql = sql.replace(":resourceProfile", resourceProfile);
        logger.debug("getRandomActiveResourceName: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch(Exception e) {
            return null;
        }
    }

    public Integer getEpochId(int resourceId) {
        String sql = "SELECT EpochId FROM Resource WHERE Id = " + resourceId;
        logger.debug("getEpochId: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getSiteIdForResource(int resourceId) {
        String sql = "With SiteForResource AS"
                + " ((SELECT DISTINCT s.Id SiteId, r.Id ResourceId"
                + " FROM uvw_ManagerToOrganisationStructures mtos"
                + " JOIN OrganisationStructure os ON mtos.OrganisationStructureId = os.Id"
                + " JOIN OrganisationStructureSite oss ON oss.OrganisationStructureId = os.Id"
                + " JOIN Site s ON s.Id = oss.SiteId"
                + " JOIN Resource r ON mtos.ManagerResourceId = r.Id)"
                + " UNION (SELECT s.Id SiteId, r.Id ResourceId"
                + " FROM RotaEntry re"
                + " JOIN OrganisationStructureSite  oss ON oss.Id = re.OrganisationStructureSiteId"
                + " JOIN Site s ON s.Id = oss.SiteId"
                + " JOIN Resource r ON r.HomeStoreId = oss.SiteId"
                + " JOIN RotaEntryType ret ON re.RotaEntryTypeId = ret.Id AND (re.EndAt IS NULL OR ret.Id = 1)))"
                + " SELECT TOP (1) SiteId FROM SiteForResource WHERE ResourceId = :resourceId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);
        logger.debug("getSiteIdForJobRef: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch(Exception e) {
            return null;
        }
    }

    public String getRandomInactiveResourceName() {

        String sql = "SELECT TOP(1) r.Name " +
                " FROM Resource r  " +
                " INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id  " +
                " INNER JOIN ApplicationUser au ON au.ResourceId = r.Id  " +
                " WHERE rp.ResourceTypeId IN (1, 5) AND r.Active=0 " +
                " ORDER BY NEWID()";

        MapSqlParameterSource params = new MapSqlParameterSource();

        logger.debug("getRandomInactiveResourceName: " + sql);

        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch(Exception e) {
            return null;
        }
    }

    public Integer getResourceProfileId(int resourceId) {
        String sql = "SELECT rp.Id FROM resource r, resourceProfile rp WHERE r.Id = :resourceId AND r.ResourceProfileId = rp.Id ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);

        logger.debug("getResourceProfileId: " + sql.replace(":resourceId", String.valueOf(resourceId)));
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public Integer getResourceProfileId(String name) {
        String sql = String.format("SELECT Id FROM ResourceProfile WHERE Name = '%s'", name);
        logger.debug("getResourceProfileId: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch(Exception e) {
            return null;
        }
    }

    public String getRandomResourceProfileWithJobCallouts() {
        String sql = "SELECT TOP(1) Name ResourceProfile"
                + " FROM ResourceProfile"
                + " WHERE Active = 1"
                + " AND JobCallouts = 1"
                + " ORDER BY NEWID()";

        logger.debug("getRandomResourceProfileWithJobCallouts: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            return null;
        }
    }

    public String getRandomContractorResourceIdWithJobCallouts() {
        String sql = "SELECT TOP(1) r.Id ResourceProfile"
                + " FROM Resource r"
                + " JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id"
                + " WHERE r.Active = 1 AND rp.Active = 1 AND rp.JobCallouts = 1"
                + " AND rp.Id = 115 AND r.UsesEngineers = 1 AND r.OnlineInvoicingActive = 1"
                + " ORDER BY NEWID()";

        logger.debug("getRandomContractorResourceIdWithJobCallouts: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            return null;
        }
    }

    public String getResourceProfileNameForSite(int siteId) {
        String sql = "SELECT rp.name " + "FROM OrganisationStructureResourceProfile osrp, OrganisationStructure os, OrganisationStructureSite oss, ResourceProfile rp "
                + "WHERE oss.siteId = :siteId "
                + "AND os.id = osrp.OrganisationStructureId "
                + "AND os.id = oss.OrganisationStructureId "
                + "AND osrp.ResourceProfileId = rp.id "
                + "AND os.OrganisationStructureTypeId = 26 -- region";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("siteId", siteId);
        logger.debug("getResourceProfileNameForSite: " + sql.replace(":siteId", String.valueOf(siteId)));
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getRandomResourceProfile() {
        String sql = "SELECT TOP(1) Name FROM ResourceProfile WHERE LEN(Name) > 2 AND LEN(Name) < 50 ORDER BY NEWID()";

        logger.debug("getRandomResourceProfile: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * For a given site name result a random tech resource profile alias that can be assigned to a job via the portal.
     * This is called from the portal log a job scenarios
     * @param name
     * @return
     */
    public String getResourceProfileNameForSite(String name) {
        String sql = "WITH resourceProfiles AS (SELECT DISTINCT alias " +
                "FROM Site s INNER JOIN SiteResource sr ON s.Id = sr.SiteId " +
                "INNER JOIN Resource r ON sr.ResourceId = r.id " +
                "INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.id " +
                "WHERE s.Name = :name " +
                "AND rp.ResourceTypeId = 1 AND r.Active = 1 AND rp.Active = 1 AND rp.JobCallouts = 1 " +
                "AND rp.ActiveForClient = 1) " +
                "SELECT TOP(1) alias FROM resourceProfiles " +
                "ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);
        logger.debug("getResourceProfileNameForSite: " + sql.replace(":name", quote(name)));
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getResourceProfileAlias(String name) {
        String sql = "SELECT alias FROM ResourceProfile rp WHERE s.Name = :name ";


        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);
        logger.debug("getResourceProfileNameForSite: " + sql.replace(":name", name));
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getResourceProfileName(int resourceId) {
        String sql = "SELECT rp.name FROM resource r, resourceProfile rp WHERE r.Id = :resourceId AND r.ResourceProfileId = rp.Id ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);

        logger.debug("getResourceProfileName: " + sql.replace(":resourceId", String.valueOf(resourceId)));
        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public String getResourceProfileName(String userName) {
        if (userName.contains("'")) {
            userName = userName.replaceAll("'", "''");
        }

        String sql = "SELECT rp.name FROM ApplicationUser au INNER JOIN resource r ON au.resourceId = r.Id "
                + " INNER JOIN resourceProfile rp ON r.ResourceProfileId = rp.Id WHERE au.userName = '%s' ";

        sql = String.format(sql, userName);

        logger.debug("getResourceProfileName: " + sql.replace(":userName", quote(userName)));
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getResourceType(String name) {
        String sql = " SELECT TOP(1) name FROM ( "
                + " SELECT rt.name FROM resource r, ResourceProfile rp, ResourceType rt  WHERE r.ResourceProfileId = rp.id  AND rp.ResourceTypeId = rt.id  AND r.name = :name "
                + " UNION "
                + " SELECT ct.name FROM ClientCaller cc, CallerType ct, Caller c WHERE c.CallerTypeId = ct.Id AND c.Id = cc.Id AND cc.name = :name"
                + " ) allcontacts";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);

        logger.debug("getResourceType: " + sql.replaceAll(":name", "'" + name + "'"));
        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public void updateResourceAssignmentDeferralDateToYesterday(int jobId) throws Exception {
        String sql = String.format("UPDATE ResourceAssignment SET DeferralDate = GetUTCDate()-1 WHERE jobId=%d ", jobId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("updateResourceAssignmentDeferralDate: " + sql);

        int rows = jdbc_helpdesk.update(sql, params);

        if (rows == 0) {
            throw new Exception("row not updated");
        }
    }

    public List<Integer> getResourcesAllocatedToSite(int siteId) {

        String sql = "SELECT r.Id FROM rotaentry re "
                + "JOIN OrganisationStructureSite  oss ON oss.Id = re.OrganisationStructureSiteId "
                + "JOIN Site s ON s.Id = oss.SiteId "
                + "JOIN Resource r ON r.Id = re.ResourceId "
                + "JOIN RotaEntryType ret ON re.RotaEntryTypeId = ret.Id "
                + "WHERE s.Id = :siteId "
                // "and r.AutoAssign=1 " +
                + "AND re.EndAt IS NULL";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("siteId", siteId);

        logger.debug("getResourcesAllocatedToSite: " + sql.replace(":siteId", String.valueOf(siteId)));

        List<Integer> resourceIds = jdbc_helpdesk.query(sql, params, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getInt("Id"));
            }
        });

        return resourceIds;
    }

    public List<Integer> getSuitableResourcesAllocatedToSite(int siteId, int assetClassificationId) {

        String sql = "SELECT DISTINCT acscm.ResourceId " + "FROM AssetClassificationSite acs  "
                + "INNER JOIN AssetClassificationSiteContractorMapping acscm ON acscm.AssetClassificationSiteId = acs.Id  "
                + "INNER JOIN Resource r ON acscm.ResourceId = r.Id  "
                + "INNER JOIN uvw_ResourceWorkingHours rwh ON rwh.ResourceID = r.Id  "
                + "LEFT JOIN FaultPriorityMapping fpm ON fpm.FaultPriorityId = acscm.Priority AND fpm.AssetClassificationId = acs.AssetClassificationId  "
                + "WHERE  rwh.StartDayOfTheWeek = DATEPART(dw,GETDATE()) AND rwh.EndDayOfTheWeek = DATEPART(dw,GETDATE())  "
                +
                // "AND acscm.Priority = 1" +
                "AND siteid = :siteId " + "AND acs.AssetClassificationId = :assetClassificationId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("siteId", siteId);
        params.addValue("assetClassificationId", assetClassificationId);

        logger.debug("getSuitableResourcesAllocatedToSite: " + sql.replace(":siteId", String.valueOf(siteId))
        .replace(":assetClassificationId", String.valueOf(assetClassificationId)));

        List<Integer> resourceIds = jdbc_helpdesk.query(sql, params, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getInt("ResourceId"));
            }
        });

        return resourceIds;
    }

    public List<String> getResourceEmailAddresses(Integer resourceId) {

        final String sql = "SELECT EmailAddress FROM ResourceEmailAddress WHERE ResourceId = :resourceId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);

        logger.debug("getResourceEmailAddresses: " + sql.replace(":resourceId", String.valueOf(resourceId)));

        List<String> emailAddresses = jdbc_helpdesk.query(sql, params, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("EmailAddress"));
            }
        });

        return emailAddresses;
    }

    public Integer getResourceIdWithIpad(String resourceType, boolean withiPad) throws Exception {
        // for Notifications to be sent the resource must have email and phone numbers configured!
        // and for a notification to an ipad a row must exist in ApplicationUserMobileApplication - that table gets populated when the user logs in via the MyJobs iOS app.

        String sql = "SELECT TOP(1) r.Id " + " FROM Resource r "
                + " LEFT JOIN ApplicationUser au ON au.ResourceId = r.Id "
                + " LEFT JOIN ApplicationUserMobileApplication auma ON auma.ApplicationUserId = au.Id "
                + " LEFT JOIN ResourceNotificationMethod rnm ON r.Id = rnm.ResourceId "
                + " LEFT JOIN NotificationMethodType nmt ON rnm.NotificationMethodTypeId = nmt.Id "
                + " LEFT JOIN resourceWorkingHours rwh ON rwh.ResourceId = r.Id "
                + " WHERE r.Active=1 AND ((rwh.startdayoftheweek = DATEPART(dw, Getdate()) AND rwh.enddayoftheweek = DATEPART(dw, Getdate()))) "
                + " AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) "
                + " AND r.Id NOT IN (SELECT ResourceId FROM uvw_ResourceAbsence WHERE CAST(AbsenceStartAt AS date) <= CAST(GETDATE() AS date) "
                + " AND CAST(GETDATE() AS date) <= CAST(AbsenceEndAt AS date)) ";

        if ("CITY TECH".equalsIgnoreCase(resourceType) && withiPad) {
            sql = sql + "AND r.ResourceProfileId IN (3, 31, 134) AND nmt.name='iPad' AND auma.Id IS NOT NULL ";
        } else if ("CITY TECH".equalsIgnoreCase(resourceType) && !withiPad) {
            sql = sql + "AND r.ResourceProfileId IN (3, 31, 134) AND nmt.name IS NULL ";
        } else if ("CONTRACTOR".equalsIgnoreCase(resourceType) && withiPad) {
            sql = sql + "AND r.ResourceProfileId = 115 AND nmt.name='iPad' AND auma.Id IS NOT NULL ";
        } else  if ("CONTRACTOR".equalsIgnoreCase(resourceType) && !withiPad) {
            sql = sql + "AND r.ResourceProfileId = 115 AND nmt.name IS NULL ";
        } else {
            throw new Exception ("unknown resource type: " + resourceType);
        }
        sql = sql + " ORDER BY NEWID()";

        logger.debug("getResourceIdWithIpad: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getResourceIdWithEmail(String resourceType, boolean withEmail) throws Exception {

        String sql = "SELECT TOP(1) r.Id "
                + " FROM Resource r "
                + " LEFT JOIN ResourceEmailAddress rea ON r.Id = rea.ResourceId "
                + " LEFT JOIN resourceWorkingHours rwh ON rwh.ResourceId = r.Id "
                + " WHERE r.Active=1 "
                + " AND rwh.startdayoftheweek = Datepart(dw, Getdate()) AND rwh.enddayoftheweek = Datepart(dw, Getdate()) ";

        if ("CITY TECH".equalsIgnoreCase(resourceType) && withEmail) {
            sql = sql + "AND r.ResourceProfileId IN (3, 31) AND rea.Id IS NOT NULL ";
        } else if ("CITY TECH".equalsIgnoreCase(resourceType) && !withEmail) {
            sql = sql + "AND r.ResourceProfileId IN (3, 31) AND rea.Id IS NULL ";
        } else if ("CONTRACTOR".equalsIgnoreCase(resourceType) && withEmail) {
            sql = sql + "AND r.ResourceProfileId = 115 AND rea.Id IS NOT NULL ";
        } else  if ("CONTRACTOR".equalsIgnoreCase(resourceType) && !withEmail) {
            sql = sql + "AND r.ResourceProfileId = 115 AND rea.Id IS NULL ";
        } else {
            throw new Exception ("unknown resource type: " + resourceType);
        }
        sql = sql + " ORDER BY NEWID()";

        logger.debug("getResourceIdWithEmail: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public Integer getCityTechWithoutIpad() {
        String sql = "SELECT TOP(1) r.id FROM Resource r "
                + "INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.id "
                + "LEFT JOIN ResourceNotificationMethod rnm ON r.Id = rnm.ResourceId "
                + "LEFT JOIN NotificationMethodType nmt ON rnm.NotificationMethodTypeId = nmt.Id "
                + "WHERE rp.ResourceTypeId = 1 AND r.Active = 1 AND rp.Active = 1 AND rp.ActiveForClient = 1 AND rp.JobCallouts = 1 "
                + "AND nmt.Name IS NULL "
                + "ORDER BY NEWID()";
        logger.debug("getCityTechWithoutIpad: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public Integer getCityTechWithIpad() {
        String sql = "SELECT TOP(1) r.Id FROM Resource r "
                + "INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.id "
                + "INNER JOIN ResourceNotificationMethod rnm ON r.Id = rnm.ResourceId "
                + "INNER JOIN NotificationMethodType nmt ON rnm.NotificationMethodTypeId = nmt.Id "
                + "LEFT JOIN ApplicationUser au ON au.ResourceId = r.Id "
                + "LEFT JOIN ApplicationUserMobileApplication auma ON auma.ApplicationUserId = au.Id "
                + "WHERE rp.ResourceTypeId = 1 AND r.Active = 1 AND rp.Active = 1 AND rp.ActiveForClient = 1 AND rp.JobCallouts = 1 "
                + "AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) "
                + "AND nmt.Name='iPad' AND auma.Id IS NOT null "
                + "ORDER BY NEWID()";
        logger.debug("getCityTechWithIpad: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public List<String> getUsernamesForCityTechsWithIpad() {
        String sql = "SELECT au.Username FROM Resource r "
                + "INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.id "
                + "INNER JOIN ResourceNotificationMethod rnm ON r.Id = rnm.ResourceId "
                + "INNER JOIN NotificationMethodType nmt ON rnm.NotificationMethodTypeId = nmt.Id "
                + "LEFT JOIN ApplicationUser au ON au.ResourceId = r.Id "
                + "LEFT JOIN ApplicationUserMobileApplication auma ON auma.ApplicationUserId = au.Id "
                + "WHERE rp.ResourceTypeId = 1 AND r.Active = 1 AND rp.Active = 1 AND rp.ActiveForClient = 1 AND rp.JobCallouts = 1 "
                + "AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) "
                + "AND nmt.Name='iPad' AND auma.Id IS NOT null "
                + "ORDER BY NEWID()";
        logger.debug("getCityTechsWithIpad: " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), String.class);
    }

    //TODO : Fix subselect
    public Integer getCityTechWithIpadAndEmailAndPhone() {

        String sql = "SELECT TOP(1) r.Id FROM SiteResource sr "
                + "INNER JOIN Resource r ON r.Id = sr.ResourceId  "
                + "INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id  "
                + "LEFT JOIN ResourceNotificationMethod rnm ON r.Id = rnm.ResourceId "
                + "LEFT JOIN NotificationMethodType nmt ON rnm.NotificationMethodTypeId = nmt.Id "
                + "LEFT JOIN ResourcePhoneNumber rpn ON rpn.ResourceId = r.Id "
                + "INNER JOIN PhoneNumberType pnt ON pnt.Id = rpn.PhoneNumberTypeId "
                + "LEFT JOIN ResourceEmailAddress rea ON rea.ResourceId = r.Id "
                + "LEFT JOIN ApplicationUser au ON au.ResourceId = r.Id "
                + "LEFT JOIN ApplicationUserMobileApplication auma ON auma.ApplicationUserId = au.Id "
                + "WHERE rp.ResourceTypeId = 1 AND r.Active = 1 AND rp.Active = 1 AND rp.ActiveForClient = 1 AND rp.JobCallouts = 1 "
                + "AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) "
                + "AND rea.Id IS NOT NULL "
                + "AND nmt.Name='iPad' AND auma.Id IS NOT NULL "
                + "ORDER BY NEWID()";

        logger.debug("getCityTechWithIpadAndEmailAndPhone: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public Integer getContractorWithReferenceRequired() {
        String sql = "SELECT TOP(1) r.Id FROM Resource r "
                + "INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id "
                + "WHERE r.ResourceProfileId = 115 AND Referencerequired = 1 "
                + "ORDER BY NEWID()";
        logger.debug("getContractorWithReferenceRequired: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public Integer getRandomJobWithResourceHavingPhoneNumber() {
        String sql = "SELECT TOP(1) mjaa.JobReference FROM uvw_MonitorJobAwaitingAcceptance mjaa"
                + " INNER JOIN job j ON j.jobreference = mjaa.JobReference"
                + " INNER JOIN JobStatus js ON j.JobStatusId = js.Id"
                + " INNER JOIN AssetClassification ac ON ac.Id = j.AssetClassificationId"
                + " INNER JOIN AssetSubType ast ON ast.Id = ac.AssetSubTypeId"
                + " INNER JOIN uvw_JobResources jr ON j.id = jr.JobId"
                + " INNER JOIN Resource r ON jr.ResourceNames = r.Name"
                + " INNER JOIN ResourcePhoneNumber rpn ON r.Id = rpn.ResourceId"
                + " LEFT JOIN LinkedIncidentCriterion lic ON lic.AssetSubTypeId = ast.Id"
                + " LEFT JOIN Joblock jl ON j.Id = jl.JobId"
                + " LEFT JOIN AdditionalResourceRequirement arr ON arr.JobId= j.Id"
                + " WHERE rpn.PhoneNumber != ''"
                + " AND rpn.PhoneNumberTypeId = 1"
                + " AND jr.ResourceNames NOT LIKE '%,%'"
                + " AND lic.AssetSubTypeId IS NULL"
                + " AND jl.JobId IS NULL"
                + " AND arr.JobId IS NULL"
                + " AND js.Name = 'Logged'"
                + " ORDER BY NEWID()";

        logger.debug("getRandomJobWithResourceHavingPhoneNumber: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }

    public Integer getJobOfRequiredTypeWithCityTechResourceHavingPhoneNumber(String jobType) {
        String sql = "SELECT TOP(1) mjaa.JobReference FROM uvw_MonitorJobAwaitingAcceptance mjaa"
                + " INNER JOIN Job j ON j.jobreference = mjaa.JobReference"
                + " INNER JOIN ResourceAssignment ra ON j.Id = ra.JobId"
                + " INNER JOIN Resource r ON r.Id = ra.ResourceId"
                + " INNER JOIN ResourcePhoneNumber rpn ON r.Id = rpn.ResourceId"
                + " INNER JOIN Site s ON s.Id = mjaa.SiteId"
                + " INNER JOIN JobStatus js ON j.JobStatusId = js.Id"
                + " LEFT JOIN ResourceProfile rp ON rp.Id = r.ResourceProfileId"
                + " LEFT JOIN LinkedIncidentCriterion lic ON lic.FaultTypeId = j.FaultTypeId"
                + " LEFT JOIN Joblock jl ON j.Id = jl.JobId"
                + " LEFT JOIN AdditionalResourceRequirement arr ON arr.JobId= j.Id"
                + " WHERE rpn.PhoneNumber != ''"
                + " AND rpn.PhoneNumberTypeId = 1"
                + " AND mjaa.JobType = :jobType"
                + " AND r.Name NOT LIKE '%,%'"
                + " AND rp.ResourceTypeId = 1 AND rp.JobCallouts = 1 AND r.Active = 1 AND rp.Active = 1 AND rp.ActiveForClient = 1 "
                + " AND js.Name = 'Logged'"
                + " AND lic.FaultTypeId IS NULL"
                + " AND jl.JobId IS NULL"
                + " AND arr.JobId IS NULL"
                + " AND ra.IsTerminal = 0"
                + " AND ra.ResourceAssignmentStatusId NOT IN (14, 17)" // Removal Requested or Pending Removal
                + " ORDER BY NEWID()";

        logger.debug("getJobOfRequiredTypeWithResourceHavingPhoneNumber: " + sql.replace(":jobType", quote(jobType)));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobType", jobType);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * get random resourceId for Active Contractor who is configured to at least one Site
     * @return
     */
    public Integer getRandomContractorId() {
        String sql = "SELECT TOP(1) r.Id FROM Resource r " +
                " JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id " +
                " JOIN AssetClassificationSiteContractorMapping acscm ON r.Id = acscm.ResourceId " +
                " JOIN Site s ON s.Id = acscm.AssetClassificationSiteId " +
                " WHERE r.Active = 1 AND rp.Name = 'Contractor' ORDER BY NEWID()";

        logger.debug("getRandomContractorId: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    /**
     * get random resourceId for Active CityTech (with ipad & email & mobile phone) who is configured to at least one Site
     * @return
     */
    public Integer getRandomCityTechId(String resourceTypeName) {
        String sql = "SELECT TOP(1) Id FROM %testdb.uvw_Resources WHERE ResourceTypeName = '%s' AND PhoneNumberType IS NOT NULL AND emailAddress IS NOT NULL";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = String.format(sql, resourceTypeName);
        logger.debug("getRandomCityTechId: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public BigDecimal getConfiguredAmountForContractor(int contractorId) {
        String sql = "SELECT DISTINCT fld_cur_CostEach FROM %portaldb.tblPricebook pb" + " JOIN %helpdeskdb.Resource r ON pb.fld_int_ResourceId = r.EpochId "
                + " WHERE fld_str_PartCode = 'Standard Callout'"
                + " AND r.Id = %d";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, contractorId);
        logger.debug("getConfiguredAmountForContractor: " + sql);
        return jdbc_portal.queryForObject(sql, new MapSqlParameterSource(), BigDecimal.class);
    }

    public Integer getRandomContractorResource(String phone, String email, String ipad) {
        String andPhone = phone == null? " AND rpn.name IS NULL " : " AND pnt.name = '" + phone + "'";
        String andEmail = email.equals("with") ? " AND rea.id IS NOT null " : " AND rea.name IS NULL ";

        String sql = "SELECT TOP(1) r.Id " +
                "FROM AssetClassificationSiteContractorMapping acscm  " +
                "INNER JOIN AssetClassificationSite acs ON acscm.AssetClassificationSiteId = acs.Id " +
                "INNER JOIN Resource r ON r.Id = acscm.ResourceId " +
                "LEFT JOIN ResourceNotificationMethod rnm ON r.Id = rnm.ResourceId " +
                "LEFT JOIN NotificationMethodType nmt ON rnm.NotificationMethodTypeId = nmt.Id " +
                "LEFT JOIN ResourcePhoneNumber rpn ON rpn.ResourceId = r.Id " +
                "INNER JOIN PhoneNumberType pnt ON pnt.Id = rpn.PhoneNumberTypeId " +
                "LEFT JOIN ResourceEmailAddress rea ON rea.ResourceId = r.Id " +
                "WHERE r.Active = 1 ";

        sql = sql + andPhone + andEmail;
        sql = ipad.equals("with") ? sql + " AND nmt.name IS NOT null " : sql;

        sql = sql + " ORDER BY NEWID()";
        logger.debug("getRandomContractorResource: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    // TODO : Fix subselect
    public Integer getRandomCityResource(String phone, String email, String ipad) {
        String andPhone = phone == null? " AND rpn.name IS NULL " : " AND rpn.PhoneNumber ! = '' AND pnt.name = '" + phone + "'";
        String andEmail = email.equals("with") ? " AND rea.id IS NOT null " : " AND rea.name IS NULL ";
        String andIpad = ipad.equals("with") ? " AND nmt.name='iPad' AND auma.Id IS NOT NULL " : " AND nmt.name IS NULL ";

        String sql = "SELECT TOP(1) r.Id FROM SiteResource sr " +
                "INNER JOIN Resource r ON r.Id = sr.ResourceId  " +
                "INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id  " +
                "LEFT JOIN ResourceNotificationMethod rnm ON r.Id = rnm.ResourceId " +
                "LEFT JOIN NotificationMethodType nmt ON rnm.NotificationMethodTypeId = nmt.Id " +
                "LEFT JOIN ResourcePhoneNumber rpn ON rpn.ResourceId = r.Id " +
                "INNER JOIN PhoneNumberType pnt ON pnt.Id = rpn.PhoneNumberTypeId " +
                "LEFT JOIN ResourceEmailAddress rea ON rea.ResourceId = r.Id " +
                "LEFT JOIN ApplicationUser au ON au.ResourceId = r.Id " +
                "LEFT JOIN ApplicationUserMobileApplication auma ON auma.ApplicationUserId = au.Id " +
                "WHERE r.Active = 1 " +
                "AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) " +
                "AND rp.Name IN (SELECT DISTINCT rp.name from Resource r INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.id " +
                "WHERE rp.ResourceTypeId = 1 AND r.Active = 1 AND rp.Active = 1 AND rp.ActiveForClient = 1 " +
                "AND rp.JobCallouts = 1) ";
        sql = sql + andPhone + andEmail + andIpad;
        sql = sql + " ORDER BY NEWID()";
        logger.debug("getRandomCityResource: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Integer> getContractorResources(int siteId){
        String sql = "SELECT DISTINCT ResourceId FROM AssetClassificationSiteContractorMapping acscm JOIN AssetClassificationSite acs ON acscm.AssetClassificationSiteId = acs.Id "
                + " WHERE SiteId = %d";
        sql = String.format(sql, siteId);
        logger.debug("getContractorResources: " + sql);

        List<Integer> resourceIds = jdbc_helpdesk.query(sql, new MapSqlParameterSource(), new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getInt("ResourceId"));
            }
        });
        return resourceIds;
    }

    // TODO : Fix subselect
    public List<Integer> getCityTechResources(int siteId){
        String sql = "SELECT ResourceId FROM SiteResource sr " +
                "JOIN Resource r ON r.Id = sr.ResourceId  " +
                "JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id  " +
                "WHERE r.Active = 1  " +
                "AND rp.Name IN (SELECT DISTINCT rp.name from Resource r INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.id " +
                "WHERE rp.ResourceTypeId = 1 AND r.Active = 1 AND rp.Active = 1 AND rp.ActiveForClient = 1 " +
                "AND rp.JobCallouts = 1) " +
                "AND sr.SiteId = %d";

        sql = String.format(sql, siteId);
        logger.debug("getCityTechResources: " + sql);

        List<Integer> resourceIds = jdbc_helpdesk.query(sql, new MapSqlParameterSource(), new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getInt("ResourceId"));
            }
        });
        return resourceIds;
    }

    // TODO : Fix subselct
    public List<Integer> getAllActiveResourcesForSite(int siteId){
        String sql = "(SELECT DISTINCT ResourceId "
                + "FROM AssetClassificationSiteContractorMapping acscm "
                + "JOIN AssetClassificationSite acs ON acscm.AssetClassificationSiteId = acs.Id "
                + "WHERE SiteId = :siteId) "
                + "UNION "
                + "(SELECT ResourceId "
                + "FROM SiteResource sr "
                + "INNER JOIN Resource r ON r.Id = sr.ResourceId "
                + "INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id "
                + "WHERE r.Active = 1 "
                + "AND rp.ResourceTypeId = 1 AND r.Active = 1 AND rp.Active = 1 AND rp.ActiveForClient = 1 AND rp.JobCallouts = 1 "
                + "AND sr.SiteId = :siteId)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("siteId", siteId);
        logger.debug("getResourcesForSite: " + sql);

        List<Integer> resourceIds = jdbc_helpdesk.query(sql, params, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getInt("ResourceId"));
            }
        });
        return resourceIds;
    }

    public List<Integer> getOnCallResources(int siteId){
        String sql = "SELECT * FROM RotaEntry re " + " INNER JOIN OrganisationStructureSite oss ON oss.id=re.OrganisationStructureSiteId "
                + " LEFT JOIN RotaEntryType ret ON ret.Id=re.RotaEntryTypeId "
                + " WHERE ret.Name = 'On Call Scheduler' AND oss.SiteId = %d";
        sql = String.format(sql, siteId);
        logger.debug("getOnCallResources: " + sql);

        List<Integer> resourceIds = jdbc_helpdesk.query(sql, new MapSqlParameterSource(), new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getInt("ResourceId"));
            }
        });
        return resourceIds;
    }

    public Integer getResourceId(String resourceName) {
        String sql = "SELECT r.Id FROM Resource r WHERE r.Name = :resourceName ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceName", resourceName);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getResourceIdFromUserName(String userName) {
        if (userName.contains("'")) {
            userName = userName.replaceAll("'", "''");
        }
        String sql = "SELECT ResourceId FROM ApplicationUser WHERE UserName = '%s'";
        sql = String.format(sql, userName);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getUserProfileId(String userProfileName) {
        String sql = "SELECT Id FROM UserProfile WHERE Name = :userProfileName ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userProfileName", userProfileName);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getUserId(String userName) {
        String sql = "SELECT Id FROM ApplicationUser WHERE UserName = '%s'";
        sql = String.format(sql, userName);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * due to the way test environments are restored - it is possible to have resources in tblHelpdeskResource but not in the helpdesk Resource table
     * @param resourceName
     * @return
     */
    public Integer getPortalResourceId(String resourceName) {
        String sql = "SELECT r.fld_int_ID FROM %portaldb.tblHelpdeskResource r WHERE r.fld_str_name = :resourceName ";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getPortalResourceId: " + sql.replaceAll(":resourceName", resourceName));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceName", resourceName);
        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void updateResourceAssignmentStatus(int jobId, int resourceId, int rasStatus) throws Exception {
        String sql = "UPDATE ResourceAssignment SET ResourceAssignmentStatusId = :rasStatus WHERE ResourceId = :resourceId AND JobId = jobId ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);
        params.addValue("jobId", jobId);
        params.addValue("rasStatus", rasStatus);
        int rows = jdbc_helpdesk.update(sql, params);
        if (rows == 0) {
            throw new Exception("row not updated");
        }
    }

    public void updateResourceAssignmentStatus(int jobId, String resourceName, int rasStatus) throws Exception {
        String sql = "UPDATE ResourceAssignment SET ResourceAssignmentStatusId = %d WHERE JobId = %d AND ResourceId IN (SELECT r.Id FROM Resource r WHERE r.Name = '%s')";
        sql = String.format(sql,  rasStatus, jobId, resourceName);
        int rows = jdbc_helpdesk.update(sql, new MapSqlParameterSource());
        if (rows == 0) {
            throw new Exception("row not updated");
        }
    }

    public Integer getContractorWithConfiguredRate() {
        String sql ="SELECT TOP(1) r.Id FROM %helpdeskdb.Resource r"
                + " JOIN %helpdeskdb.ResourceProfile rp ON rp.Id = r.ResourceProfileId"
                + " LEFT JOIN %portaldb.tblPricebook tpb ON tpb.fld_int_ResourceId = r.EpochId"
                + " WHERE rp.Name = 'Contractor'"
                + " AND tpb.fld_str_PartCode = 'Standard Callout'"
                + " AND r.AlwaysChargeable = '1'"
                + " AND r.Active = 1"
                + " AND fld_cur_CostEach > 0"
                + " ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getContractorWithConfiguredRate: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getContractorWithNoConfiguredRate() {
        String sql = "SELECT TOP(1) r.Id FROM %helpdeskdb.Resource r"
                + " LEFT JOIN %helpdeskdb.ResourceProfile rp ON rp.Id = r.ResourceProfileId"
                + " LEFT JOIN %portaldb.tblPricebook tpb ON tpb.fld_int_ResourceId = r.EpochId"
                + " WHERE rp.Name = 'Contractor'"
                + " AND tpb.fld_int_ResourceId IS NULL"
                + " AND r.AlwaysChargeable = '1'"
                + " AND r.Active = 1"
                + " ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getContractorWithNoConfiguredRate: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void updateResourceAssignmentCreationDate(int jobReference) throws Exception {
        String sql = String.format("UPDATE ResourceAssignment SET CreatedOn = DATEADD(HOUR, -72, GETUTCDATE()) WHERE JobId = (SELECT Id FROM Job WHERE JobReference = %d) ", jobReference);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("updateResourceAssignmentCreationDate: " + sql);

        int rows = jdbc_helpdesk.update(sql, params);

        if (rows == 0) {
            throw new Exception("row not updated");
        }
    }

    public Timestamp getResourceAssignmentCreationDate(Integer jobReference, Integer resourceId) throws Exception {
        String sql = "SELECT MAX(ra.CreatedOn) FROM ResourceAssignment ra INNER JOIN Job j ON J.Id = ra.JobId WHERE j.jobReference = :jobReference AND ra.ResourceId = :resourceId ";

        logger.debug("getContractorWithConfiguredRat: " + sql.replace(":jobReference", jobReference.toString()).replace(":resourceId", resourceId.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        params.addValue("resourceId", resourceId);
        return jdbc_helpdesk.queryForObject(sql, params, Timestamp.class);
    }

    public String getAdditionalCityTechForJobWithoutIpadWithPhoneNumber(String resourceName) {
        String sql = "SELECT TOP(1) r.Name FROM Resource r"
                + " JOIN ResourceProfile rp ON rp.Id = r.ResourceProfileId"
                + " LEFT JOIN ResourceNotificationMethod rnm ON r.Id = rnm.ResourceId"
                + " LEFT JOIN NotificationMethodType nmt ON rnm.NotificationMethodTypeId = nmt.Id"
                + " LEFT JOIN ResourcePhoneNumber rpn ON rpn.ResourceId = r.Id"
                + " WHERE rp.ResourceTypeId = 1 AND rp.JobCallouts = 1 AND r.Active = 1 AND rp.Active = 1 AND rp.ActiveForClient = 1"
                + " AND nmt.name IS NULL"
                + " AND rpn.PhoneNumber != ''"
                + " AND r.Name != '%s'"
                + " ORDER BY NEWID()";
        sql = String.format(sql, resourceName);

        logger.debug("getAdditionalCityTechForJobWithoutIpadWithPhoneNumber: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * user must have logged in to iPad at least once for it to receive notifications
     * @param resourceId
     */
    public boolean ipadUserHasLogin(int resourceId) {
        String sql = "SELECT COUNT(*) FROM ApplicationUserMobileApplication auma INNER JOIN ApplicationUser au ON au.Id = auma.ApplicationUserId WHERE au.resourceId = %d";
        sql = String.format(sql,  resourceId);
        logger.debug("ipadUserHasLogin: " + sql);
        int count = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        return count > 0;
    }

    public void insertNewRowApplicationUserMobileApplication(int resourceId){
        String sql = "INSERT INTO ApplicationUserMobileApplication (ApplicationUserId, MobileApplicationId, Token, CreatedOn, CreatedBy) SELECT Id, 1, 'xxx', GETUTCDATE(), 'TestAutomation' FROM ApplicationUser WHERE ResourceId = %d";
        sql = String.format(sql, resourceId);
        logger.debug("addAssetsToSite: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void setActive(int resourceId, boolean active) throws Exception {
        int bit = active ? 1 : 0;
        String sql = String.format("UPDATE Resource SET Active = %d WHERE Id = %d", bit, resourceId);
        logger.debug("setActive: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void updateRotaEntryToActive(int resourceId, int siteId) throws Exception {
        String sql = String.format("UPDATE RotaEntry SET EndAt = null WHERE Id IN (SELECT TOP(1) re.Id FROM RotaEntry re JOIN OrganisationStructureSite oss ON re.OrganisationStructureSiteId = oss.Id WHERE ResourceId=%d AND oss.SiteId=%d ORDER BY re.Id DESC)", resourceId, siteId);
        logger.debug("updateRotaEntryToActive: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void updateRotaEntryToInactive(int resourceId, int siteId) throws Exception {
        String sql = String.format("UPDATE RotaEntry SET EndAt = GETUTCDATE()-1 WHERE Id IN (SELECT TOP(1) re.Id FROM RotaEntry re JOIN OrganisationStructureSite oss ON re.OrganisationStructureSiteId = oss.Id WHERE ResourceId=%d AND oss.SiteId=%d ORDER BY re.Id DESC)", resourceId, siteId);
        logger.debug("updateRotaEntryToInactive: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void cleaupRotaEntries(int siteId, int resourceId){
        String sql = "DELETE RotaEntry WHERE Id IN ( " +
                "SELECT re.Id FROM RotaEntry re  " +
                "INNER JOIN OrganisationStructureSite oss ON oss.id=re.OrganisationStructureSiteId " +
                "WHERE re.ResourceId = %d AND oss.SiteId = %d " +
                "AND re.EndAt < GETUTCDATE()-1 )";
        sql = String.format(sql, resourceId, siteId);
        logger.debug("cleaupRotaEntries: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public String getResourceName(int resourceId) {
        String sql = "SELECT LTRIM(RTRIM(r.Name)) FROM resource r WHERE r.Id = :resourceId ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);
        logger.debug("getResourceName: " + sql.replaceAll(":resourceId", String.valueOf(resourceId)));
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * The Portal and Helpdesk maintain there own list of Resources.  The Portal should ideally use the table tblHelpDeskResource.
     * Both tables should be in sync but occasionally they do not match
     * @param resourceId
     * @return
     */
    public String getPortalResourceName(int resourceId) {
        String sql = "SELECT pr.fld_str_Name FROM %helpdeskdb.resource r INNER JOIN %portaldb.tblHelpDeskResource pr " +
                "ON r.EpochId = pr.fld_int_id " +
                " WHERE r.Id = :resourceId ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);
        logger.debug("getPortalResourceName: " + sql.replaceAll(":resourceId", String.valueOf(resourceId)));
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getRandomActiveBackupResourceId(String profileName, int resourceId) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_AVAILABLE_RESOURCES.sql");

        profileName = profileName.equalsIgnoreCase("City Tech") ? "City Resource" : profileName;

        sql = sql.replaceAll(":profileName", quote(profileName)).replace(":resourceId", String.valueOf(resourceId));
        logger.debug("getRandomActiveBackupResourceId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public List<Map<String, Object>> getResourceProfilesUnderRFM(int resourceId) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_RESOURCE_PROFILE_UNDER_RFM.sql");
        //sql = String.format(sql, siteId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);
        logger.debug("getResourceProfilesUnderRFM: " + sql);
        return jdbc_helpdesk.queryForList(sql, params);
    }

    public List<Map<String, Object>> getResourceProfileUnderRFMWithAlias(int resourceId) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_RESOURCE_PROFILE_UNDER_RFM_WITH_ALIAS.sql");
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);
        logger.debug("getResourceProfilesUnderRFMWithAlias: " + sql);
        return jdbc_helpdesk.queryForList(sql, params);
    }

    public String getResourceNameForJobReference(int jobReference) {
        String sql = "SELECT TOP(1) r.Name FROM %helpdeskdb.Job j INNER JOIN %helpdeskdb.ResourceAssignment ra ON j.Id = ra.JobId "
                + "INNER JOIN %helpdeskdb.Resource r ON ra.ResourceId = r.Id "
                + "WHERE j.JobReference = :jobReference "
                + "ORDER BY ra.CreatedOn DESC ";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getResourceName: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getResourceNameForResourceAssignmentId(int resourceAssignmentId) {
        String sql = "SELECT r.Name FROM Resource r INNER JOIN ResourceAssignment ra ON ra.ResourceId = r.Id WHERE ra.id = %d";
        sql = String.format(sql, resourceAssignmentId);
        logger.debug("getResourceNameForResourceAssignmentId: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getAvailableResourceProfileForSite(int siteId, String resourceName) {
        String sql = "SELECT TOP (1) rp.Name FROM %helpdeskdb.SiteResource sr " + " INNER JOIN %helpdeskdb.Resource r ON r.Id = sr.ResourceId "
                + " INNER JOIN %helpdeskdb.ResourceProfile rp ON r.ResourceProfileId = rp.Id "
                + " WHERE r.Active = 1 "
                + " AND rp.JobCallouts = 1 "
                + " AND sr.SiteId = :siteId "
                + " AND r.Name = :resourceName "
                + " ORDER BY NEWID() ";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getAvailableResourceForSite: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("siteId", siteId);
        params.addValue("resourceName", resourceName);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getOtherAvailableActiveResourceForSite(int siteId, int resourceId) {
        String sql = "SELECT TOP(1) r.Name FROM Resource r"
                + " JOIN SiteResource sr ON sr.ResourceId = r.Id"
                + " JOIN Site s ON s.Id = sr.SiteId"
                + " JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id"
                + " WHERE s.Id = :siteId"
                + " AND r.Id ! = :resourceId"
                + " AND r.Active = 1"
                + " AND rp.JobCallouts = 1"
                + " ORDER BY NEWID()";

        logger.debug("getOtherAvailableActiveResourceForSite: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("siteId", siteId);
        params.addValue("resourceId", resourceId);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getAliasForResourceProfile(String resourceProfile) {
        String sql = "SELECT DISTINCT TOP(1) alias"
                + " FROM Resource r  INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.id"
                + " WHERE rp.Name = :resourceProfile"
                + " AND r.Active = 1 AND rp.Active = 1"
                + " AND rp.ActiveForClient = 1";

        logger.debug("getAliasForResourceProfile: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceProfile", resourceProfile);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch(Exception e) {
            return null;
        }
    }

    public String getResourceNameFromUsername(String userName){
        String sql = String.format("SELECT r.Name FROM Resource r INNER JOIN ApplicationUser au ON au.ResourceId = r.Id WHERE au.UserName = '%s'", userName);
        logger.debug("getResourceNameFromUsername: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            return null;
        }
    }

    public boolean helpDeskResourceRelationshipExists(int parentId, int childId){
        String sql = "SELECT COUNT(*) FROM %portaldb.tblHelpDeskResourceRelationship where fld_int_ChildHelpDeskResourceID = %d AND fld_int_ParentHelpDeskResourceID = %d";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql,  childId, parentId);
        logger.debug("helpDeskResourceRelationshipExists: " + sql);
        int count = jdbc_portal.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        return count > 0;
    }

    public Integer getResourceIdForSite(Integer siteId) {
        String sql = String.format("SELECT TOP(1) ResourceId from SiteResource WHERE SiteId = %d ", siteId);
        logger.debug("getResourceIdForSite : " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public List<String> getRFMNamesWhoseAbsenceIsCoveredBy(int coveringRFMId) {
        String sql = "SELECT r.Name ResourceName FROM ResourceAvailability rav JOIN Resource r ON rav.ResourceId = r.Id"
                + " JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id WHERE rp.Name = 'RFM'"
                + " AND r.Active = 1 AND rav.Active = 1 AND rav.StartAt <= GETDATE() AND rav.CoveringResourceId = :coveringRFMId"
                + " AND rav.EndAt >= GETDATE() AND rav.ResourceAvailabilityTypeId NOT IN (4, 5)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("coveringRFMId", coveringRFMId);

        logger.debug("getRFMNamesWhoseAbsenceIsCoveredBy: " + sql);

        List<String> rfmNames = jdbc_helpdesk.query(sql, params, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("ResourceName"));
            }
        });
        return rfmNames;
    }

    /**
     * @param resource
     * @return
     */
    public List<Map<String, Object>> getOutstandingAbsencesForResource(String resource) {
        String sql = "SELECT FORMAT(StartAt, 'd MMM yyyy h:mmtt') FromDate, "
                + " FORMAT(EndAt, 'd MMM yyyy h:mmtt') ToDate, ravt.Name Type,"
                + " ISNULL(r.Name, '') BackupResource, ISNULL(rav.Notes, '') Notes, IIF(rav.Active = 1, 'Active', 'Ignored') AS Status "
                + " FROM ResourceAvailability rav"
                + " JOIN ResourceAvailabilityType ravt ON ravt.Id = rav.ResourceAvailabilityTypeId LEFT JOIN Resource r ON rav.CoveringResourceId= r.Id"
                + " LEFT JOIN uvw_ResourceForSearch rfs ON rfs.Id = rav.ResourceId "
                + " WHERE (rav.StartAt >= GETDATE() OR (rav.StartAt <= GETDATE() AND rav.EndAt >= GETDATE())) AND rfs.Summary = :resource "
                + " ORDER BY EndAt";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resource", resource);

        logger.debug("getOutstandingAbsencesForResource: " + sql.replaceAll(":resource", "'" + resource + "'"));

        try {
            return jdbc_helpdesk.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getResourceAvailability(int resourceId) {
        String sql = "SELECT r.Name, rav.StartAt, rav.EndAt, rav.CoveringResourceId, cover.Name AS CoveringResourceName "
                + "FROM ResourceAvailability rav "
                + "INNER JOIN Resource r ON rav.ResourceId = r.Id "
                + "LEFT JOIN Resource cover ON rav.CoveringResourceId = cover.Id "
                + "WHERE r.Active = 1 AND rav.Active = 1 "
                + "AND ( "
                + "(rav.StartAt BETWEEN GETDATE() AND DATEADD(day, 13, GETDATE())) "
                + "OR "
                + "(rav.EndAt BETWEEN GETDATE() AND DATEADD(day, 13, GETDATE())) "
                + "OR "
                + "(rav.StartAt < GETDATE() AND rav.EndAt > GETDATE()) "
                + ") "
                + "AND r.Id = %d";

        sql = String.format(sql,  resourceId);

        logger.debug("getResourceAvailability: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getResourceAvailabilityToday(int resourceId) {
        String sql = "SELECT r.Name, rav.StartAt, rav.EndAt, rav.CoveringResourceId, cover.Name AS CoveringResourceName "
                + "FROM ResourceAvailability rav "
                + "INNER JOIN Resource r ON rav.ResourceId = r.Id "
                + "LEFT JOIN Resource cover ON rav.CoveringResourceId = cover.Id "
                + "WHERE r.Active = 1 AND rav.Active = 1 "
                + "AND rav.StartAt <= GETUTCDATE() AND rav.EndAt >= GETUTCDATE()"
                + "AND r.Id = %d";

        sql = String.format(sql,  resourceId);

        logger.debug("getResourceAvailability: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public void deleteAbsences(int resourceId) {
        String sql = "DELETE ResourceAvailability WHERE ResourceId = %d";
        sql = String.format(sql, resourceId);
        logger.debug("deleteAbsences: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public Map<String, Object> getRandomActiveResourceWithOutstandingAbsences() {
        String sql = "SELECT TOP(1) rfs.* FROM uvw_ResourceForSearch rfs JOIN ResourceAvailability rav ON rav.ResourceId = rfs.Id "
                + "WHERE ( rav.StartAt >= GETDATE() OR (rav.StartAt <= GETDATE() AND rav.EndAt >= GETDATE())) AND rav.Active = 1 "
                + "ORDER BY NEWID()";

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getRandomActiveResourceName: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, params).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getHomeStoreIdForResourceId(Integer resourceId) {
        String sql = String.format("SELECT HomeStoreId FROM dbo.Resource WHERE Id = %d", resourceId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getHomeStoreIdForResourceId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
    }

    public Integer getHelpdeskResourceId(Integer portalResourceId) {
        String sql = "SELECT Id FROM %helpdeskdb.Resource WHERE EpochId = :portalResourceId ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("portalResourceId", portalResourceId);
        logger.debug("getHelpdeskResourceId", sql);
        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getResourceNotificationMethodType(Integer resourceId) {
        String sql = "SELECT NotificationMethodTypeId FROM ResourceNotificationMethod rnm " +
                "JOIN ApplicationUser au ON rnm.ResourceId = au.ResourceId " +
                "JOIN ApplicationUserMobileApplication auma ON au.Id = auma.ApplicationUserId " +
                "WHERE rnm.ResourceId = :resourceId";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);
        logger.debug("getResourceNotificationMethodType", sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getLineManager(Integer resourceId) {
        String sql =  "WITH CTE_Manager  AS ( "
                + " SELECT mtos.ManagerResourceId, rp.EscalationHierarchy, r.Id, sd.Division FROM OrganisationStructureManager osm "
                + " JOIN ApplicationUser au ON osm.ApplicationUserId = au.Id "
                + " JOIN Resource r ON au.ResourceId = r.Id "
                + " JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id "
                + " JOIN uvw_ManagerToOrganisationStructures mtos ON mtos.OrganisationStructureId = osm.OrganisationStructureId "
                + " LEFT JOIN uvw_SitesDivision sd ON r.HomeStoreId = sd.SiteId "
                + " WHERE r.Id = %d AND osm.Active = 1) "
                + " SELECT TOP(1) r.Name, IIF(m.Division = sd.Division, 1, 2) DivisionMatch FROM Resource r "
                + " JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id "
                + " JOIN CTE_Manager m ON m.ManagerResourceId = r.Id "
                + " LEFT JOIN uvw_SitesDivision sd ON r.HomeStoreId = sd.SiteId  "
                + " WHERE (rp.EscalationHierarchy < m.EscalationHierarchy OR rp.EscalationHierarchy IS NULL) "
                + " AND r.Id ! = m.Id "
                + " ORDER BY DivisionMatch, rp.EscalationHierarchy DESC, r.Name ";
        sql = String.format(sql, resourceId);
        logger.debug("getLineManager: " + sql);
        try {
            return (String) jdbc_helpdesk.queryForMap(sql, new MapSqlParameterSource()).get("Name");
        } catch(Exception e) {
            return null;
        }
    }

    public List<String> getResourceAssignmentStatus(Integer jobReference, Integer resourceId) {
        String sql = "SELECT ras.Name FROM ResourceAssignment ra LEFT OUTER JOIN Job j ON ra.JobId = j.Id"
                + " LEFT OUTER JOIN ResourceAssignmentStatus ras ON ra.ResourceAssignmentStatusId = ras.Id"
                + " WHERE j.JobReference = :jobReference"
                + " AND ra.ResourceId = :resourceId";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        params.addValue("resourceId", resourceId);
        logger.debug("getResourceAssignmentStatus: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, params, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getResourcePhoneNumber(Integer resourceId) {
        String sql = "SELECT PhoneNumber FROM ResourcePhoneNumber WHERE ResourceId = :resourceId";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);
        logger.debug("getResourcePhoneNumber", sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getRandomCityTechWithIpad(boolean withIpad) {
        String sql = "SELECT TOP(1) r.id, r.Name FROM Resource r "
                + "LEFT JOIN ResourceNotificationMethod rnm ON r.Id = rnm.ResourceId "
                + "LEFT JOIN NotificationMethodType nmt ON rnm.NotificationMethodTypeId = nmt.Id "
                + "LEFT JOIN ResourcePhoneNumber rpn ON r.Id = rpn.ResourceId "
                + "LEFT JOIN uvw_ManagerSites ms ON r.Id = ms.ResourceId "
                + "LEFT JOIN ApplicationUser au ON au.ResourceId = r.Id "
                + "LEFT JOIN ApplicationUserMobileApplication auma ON auma.ApplicationUserId = au.Id "
                + "WHERE r.ResourceProfileId IN (SELECT DISTINCT id FROM ResourceProfile WHERE ResourceTypeId = 1 AND Active = 1 AND ActiveForClient = 1 AND JobCallouts = 1) "
                + "AND r.Active = 1 "
                + "AND r.Id NOT IN (SELECT ResourceId FROM ResourceAvailability WHERE Active = 1 AND EndAt >= GETUTCDATE()) ";

        sql = withIpad ? sql + "AND nmt.name='iPad' AND auma.Id IS NOT NULL " : sql + "AND nmt.name IS NULL ";
        sql = sql + "ORDER BY NEWID() ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getRandomCityTechWithIpad: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getRandomActiveCityTech() {
        String sql = "SELECT TOP(1) ResourceId FROM SiteResource sr"
                + " JOIN Resource r ON r.Id = sr.ResourceId"
                + " JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id"
                + " WHERE r.Active = 1 AND rp.Active = 1 AND rp.ResourceTypeId = 1"
                + " AND rp.ActiveForClient = 1  AND rp.JobCallouts = 1 ORDER BY NEWID()"  ;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        logger.debug("getRandomActiveCityTech", sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getMenuItems(int resourceId, String profileName) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_MENU_ITEMS_FOR_RESOURCE.sql");

        if (profileName == null) {
            sql = sql.replace("WHERE UserProfileName =", "--WHERE UserProfileName =");
            sql = sql.replace("%d", String.valueOf(resourceId));
        } else {
            sql = String.format(sql, resourceId, profileName);
        }

        logger.debug("getMenuItems: " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
    }

    public List<String> getActiveCityTechProfileNames() {
        String sql = "SELECT DISTINCT rp.Alias FROM  Resource r"
                + " JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id"
                + " WHERE r.Active = 1 AND rp.Active = 1 AND rp.ResourceTypeId = 1"
                + " AND rp.ActiveForClient = 1  AND rp.JobCallouts = 1";

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getActiveCityTechProfileNames: " + sql);

        List<String> getActiveCityTechProfileNames = jdbc_helpdesk.query(sql, params, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("Alias"));
            }
        });

        return getActiveCityTechProfileNames;
    }

    public boolean contractorHasPricebookEntry(int resourceId) {
        String sql = "SELECT COUNT(1) FROM %portaldb.tblPricebook pb JOIN Resource r ON pb.fld_int_ResourceId = r.EpochId WHERE r.Id = :resourceId";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);
        sql = String.format(sql, resourceId);
        logger.debug("contractorHasPricebookEntry: " + sql);
        int count = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        return count > 0;
    }

    public void insertResourceNotificationMethod(int resourceId, String notificationMethod) {
        String sql = "INSERT INTO ResourceNotificationMethod SELECT %d, nmt.Id FROM NotificationMethodType nmt WHERE nmt.Name = '%s'";
        sql = String.format(sql, resourceId, notificationMethod);
        logger.debug("insertResourceNotificationMethod: " + sql);
        try {
            jdbc_helpdesk.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            // ignore
        }
    }

    public Integer getResourceProfileIdThatCantBeMappedToSite(int siteId) {
        String siteToResourceProfileIds = "SELECT rp.Id FROM ResourceProfileSiteTypeMapping rpstm INNER JOIN ResourceProfile rp ON rp.Id = rpstm.ResourceProfileId INNER JOIN Site s ON s.SiteTypeId = rpstm.SiteTypeId WHERE s.Id = " + siteId;
        String sql = "SELECT TOP(1) rp.Id FROM ResourceProfile rp WHERE Active = 1 AND JobCallouts = 1 AND Id NOT IN (%s) ORDER BY NEWID()";
        sql = String.format(sql, siteToResourceProfileIds);
        logger.debug("getResourceProfileIdThatCantBeMappedToSite: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public void deletePricebookEntryForContractor(int resourceId) {
        String sql = "DELETE FROM %portaldb.tblPricebook WHERE fld_int_ResourceId = (SELECT EpochId FROM Resource WHERE Id = %d)";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, resourceId);
        logger.debug("deletePricebookEntryForContractor: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public Integer getResourceIdWithPhoneNumber(int siteId) {
        String sql = "SELECT TOP(1) rpn.ResourceId FROM ResourcePhoneNumber rpn INNER JOIN SiteResource sr ON sr.ResourceId = rpn.ResourceId WHERE sr.SiteId = %d AND rpn.PhoneNumber > '' ORDER BY NEWID()";
        sql = String.format(sql, siteId);
        logger.debug("getResourceIdWithPhoneNumber: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public Integer getChargeableContractor(boolean chargeable, String zeroPriceInThePricebook) {
        String sql = "SELECT TOP(1) Id FROM Resource r"
                + " LEFT JOIN %portaldb.tblPricebook pb ON pb.fld_int_ResourceId = r.EpochId"
                + " WHERE r.ResourceProfileId = 115"
                + " AND r.Active = 1";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = chargeable ? sql + " AND r.AlwaysChargeable = 1" : sql + " AND r.AlwaysChargeable = 0";
        if ("with".equalsIgnoreCase(zeroPriceInThePricebook)) {
            sql = sql + " AND pb.fld_str_PartCode = 'OOH Callout' AND fld_cur_CostEach = 0";
        } else if ("without".equalsIgnoreCase(zeroPriceInThePricebook)) {
            sql = sql + " AND ((pb.fld_str_PartCode = 'Standard Callout' AND fld_cur_CostEach > 0) OR pb.fld_int_ResourceId IS NULL) ";
        }
        sql = sql + " ORDER BY NEWID()";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getChargeableContractor: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void updateAlwaysChargeableValueOfResource(boolean alwaysChargeable, int resourceId) {
        String sql = alwaysChargeable ? "UPDATE Resource SET AlwaysChargeable = 1 WHERE Id = %d" : "UPDATE Resource SET AlwaysChargeable = 0 WHERE Id = %d";
        sql = String.format(sql, resourceId);
        logger.debug("updateAlwaysChargeableValueOfResource: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public Integer getUnassignedResourceId(int jobReference) {
        String sql = "SELECT TOP(1) sr.ResourceId FROM SiteResource sr "
                + ",(SELECT ra.ResourceId, j.SiteId FROM ResourceAssignment ra INNER JOIN Job j ON ra.JobId = j.Id WHERE j.JobReference = %d AND ra.Active = 1) ra "
                + "WHERE sr.SiteId = ra.SiteId "
                + "AND ra.ResourceId <> sr.ResourceId";
        sql = String.format(sql,  jobReference);

        logger.debug("getUnassignedResourceId: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Return resource type name for the given resourceId
     * @param resourceId
     * @return
     */
    public String getResourceType(int resourceId) {
        String sql = "SELECT rt.Name " +
                "FROM Resource r INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id " +
                "INNER JOIN ResourceType rt ON rp.ResourceTypeId = rt.Id " +
                "WHERE r.Id = %d";
        sql = String.format(sql, resourceId);
        logger.debug("getResourceType: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getRandomResourceType() {
        String sql = "SELECT TOP(1) Name " +
                "FROM ResourceType " +
                "ORDER BY NEWID()";

        logger.debug("getRandomResourceType: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getResourceTypeByEpochId(int epochId) {
        String sql = "SELECT CASE  " +
                "WHEN rt.id = 1 THEN 'Resource' " +
                "WHEN rt.id = 5 THEN 'Vendor' " +
                "ELSE 'Unknown' " +
                "END " +
                "FROM Resource r INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id " +
                "INNER JOIN ResourceType rt ON rp.ResourceTypeId = rt.Id " +
                "WHERE r.epochId = %d";
        sql = String.format(sql, epochId);
        logger.debug("getResourceTypeByEpochId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public boolean resourceHasMobilePhone(int resourceId) {
        String sql = "SELECT COUNT(1) FROM ResourcePhoneNumber WHERE PhoneNumberTypeId = 1 AND ResourceId = :resourceId";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);
        sql = String.format(sql, resourceId);
        logger.debug("resourceHasMobilePhone: " + sql);
        int count = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        return count > 0;
    }

    public Map<String, Object> getResourceWithUpcomingAbsence(String resourceType, String cover) throws Exception{
        String sql = FileHelper.getResourceFile("sql/select/", "GET_ABSENCE_MANAGER_AND_RESOURCE.sql");

        String with = "with".equalsIgnoreCase(cover) ? "IS NOT" : "IS";
        sql = sql + String.format(" WHERE CoveringResourceId %s NULL", with);

        String isCityTech =  resourceType.equals("City Tech") ? "=" : "!=";
        sql = sql + String.format(" AND OrgStructureType %s 'Tech Position'", isCityTech);

        logger.debug("getResourceWithUpcomingAbsence: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getContractorResourceIdForSite(int siteId, int assetClassificationId) {
        String sql = "SELECT TOP (1) acscm.ResourceId FROM assetClassificationSite acs "
                + "INNER JOIN AssetClassificationSiteContractorMapping acscm ON acscm.AssetClassificationSiteid = acs.Id "
                + "WHERE acs.SiteId = %d AND acs.AssetClassificationId = %d";
        sql = String.format(sql, siteId, assetClassificationId);
        logger.debug("getContractorResourceIdForSite: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public Integer getAdditionalContractorWithConfiguredRate(int contractorId) {
        String sql ="SELECT TOP(1) r.Id FROM %helpdeskdb.Resource r"
                + " JOIN %helpdeskdb.ResourceProfile rp ON rp.Id = r.ResourceProfileId"
                + " LEFT JOIN %portaldb.tblPricebook tpb ON tpb.fld_int_ResourceId = r.EpochId"
                + " WHERE rp.Name = 'Contractor'"
                + " AND tpb.fld_str_PartCode = 'Standard Callout'"
                + " AND r.AlwaysChargeable = '1'"
                + " AND r.Active = 1"
                + " AND fld_cur_CostEach > 0"
                + " AND r.Id != :contractorId"
                + " ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("contractorId", contractorId);
        sql = String.format(sql, contractorId);
        logger.debug("getAdditionalContractorWithConfiguredRate: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getLabourTypeForResourceProfile() {
        String sql = " SELECT TOP(1) rp.Name AS ResourceProfile, rp.Alias, rplrt.Name AS LabourType"
                + " FROM ResourceProfile rp"
                + " INNER JOIN ResourceProfileLabourRate rplr ON rp.Id = rplr.ResourceProfileId"
                + " INNER JOIN ResourceProfileLabourRateType rplrt ON rplr.ResourceProfileLabourRateTypeId = rplrt.Id"
                + " INNER JOIN USState uss ON rplr.USStateId = uss.Id";

        String environment = propertyHelper.getEnv();
        if(environment.contains("USWM")) {
            sql = sql + " WHERE uss.FullName = 'Florida'";
        }

        //RHVAC is old profile so won't appear in list
        sql = sql + " AND rp.Alias != 'RHVAC' ORDER BY NEWID()";

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getLabourTypeForResourceProfile: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, params).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public String getResourceUserProfileName(Integer resourceId) {
        String sql = "SELECT TOP(1) up.Name FROM ApplicationUser au" +
                " JOIN UserProfile up ON au.UserProfileId = up.Id" +
                " WHERE au.ResourceId = :resourceId";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);
        logger.debug("getResourceUserProfileName", sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getRandomContractorOrLandlordResource() {
        String sql = "SELECT TOP(1) r.Name FROM Resource r"
                + " JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id"
                + " WHERE r.Active = 1 AND rp.ResourceTypeId IN (5,8)"
                + " ORDER BY NEWID()";

        logger.debug("getRandomContractorOrLandlordResource", sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getRandomContractorResource() {
        String sql = "SELECT TOP(1) r.Name FROM Resource r"
                + " JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id"
                + " WHERE r.Active = 1 AND rp.ResourceTypeId = 5"
                + " ORDER BY NEWID()";

        logger.debug("getRandomContractorOrLandlordResource", sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getRandomLandlordResource() {
        String sql = "SELECT TOP(1) r.Name FROM Resource r"
                + " JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id"
                + " WHERE r.Active = 1 AND rp.ResourceTypeId = 8"
                + " ORDER BY NEWID()";

        logger.debug("getRandomContractorOrLandlordResource", sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getRandomActiveUserProfile() {
        String sql = "SELECT TOP(1) Name FROM UserProfile"
                + " WHERE Active = 1 ORDER BY NEWID()";

        logger.debug("getRandomContractorOrLandlordResource", sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getCityResourceAdLoginState() {
        String sql = "SELECT AdLogin FROM ResourceType"
                + " WHERE Name = 'City Resource'";

        logger.debug("getCityResourceAdLoginState: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getResourceCallerId(int resourceId) {
        String sql = "SELECT rc.Id FROM ResourceCaller rc JOIN Resource r ON rc.ResourceId = r.Id WHERE r.Id = :resourceId ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);
        logger.debug("getResourceCallerId: " + sql.replaceAll(":resourceId", String.valueOf(resourceId)));
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void updateCityResourceSoTheyCanHaveIpad(int adLogin) {
        String sql = "UPDATE ResourceType"
                + " SET AdLogin = :adLogin"
                + " WHERE Name = 'City Resource'";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("adLogin", adLogin);
        logger.debug("updateCityResourceSoTheyCanHaveIpad: " + sql);
        jdbc_helpdesk.update(sql, params);
    }

    /**
     * Get list of resource Id's assigned to jobReference
     * @param jobReference
     * @return
     */
    public List<Integer> getAssignedResources(int jobReference){
        String sql = "SELECT ResourceId FROM ResourceAssignment ra INNER JOIN Job j ON j.id = ra.JobId WHERE ra.Active = 1 AND j.JobReference = %d";
        sql = String.format(sql,  jobReference);
        logger.debug("getAssignedResources : " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getAssignedResourcesByName(int jobReference){
        String sql = "SELECT Name ResourceName"
                + " FROM Resource r"
                + " JOIN ResourceAssignment ra ON r.Id = ra.ResourceId"
                + " JOIN Job j ON ra.JobId = j.Id"
                + " WHERE j.JobReference = %d";

        sql = String.format(sql,  jobReference);
        logger.debug("getAssignedResourcesByName : " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getResourceWithMenu(String menuItem) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_RESOURCES_WITH_MENU.sql");
        sql = sql + String.format(" AND Menu = '%s'", menuItem);

        logger.debug("getResourceWithMenu: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getResourceWithMenuAndRole(String role, String resourceProfile) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_RESOURCES_WITH_MENU.sql");
        sql = sql.replace("rm.*", "TOP (1) rm.*");
        sql = sql + String.format(" AND RoleName = '%s'", role);
        sql = propertyHelper.getMercuryUrl().contains("ukrb") ? sql + " AND ResourceProfile LIKE '%Finance%'" : sql + String.format(" AND UserProfileName = '%s'", resourceProfile);

        logger.debug("getResourceWithMenuAndRole: " + sql);
        try {
            return jdbc_helpdesk.queryForMap(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getResourceWithMenuRole(String role, String resourceProfile) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_RESOURCES_WITH_MENU.sql");
        sql = sql + String.format(" AND RoleName = '%s'", role);
        sql = propertyHelper.getMercuryUrl().contains("ukrb") ? sql + " AND ResourceProfile LIKE '%Finance%'" : sql + String.format(" AND UserProfileName = '%s'", resourceProfile);
        sql = sql + " ORDER BY Id";

        logger.debug("getResourceWithMenuRole: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getResourceForJob(String resourceType, int jobReference) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_RESOURCE_FOR_JOB.sql");
        sql = sql.replace(":jobReference", String.valueOf(jobReference)).replaceAll(":resourceType",  quote(resourceType));

        logger.debug("getResourceForJob: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public String getUserNameFromFirstAndSecondNames(String firstName, String secondName) {
        String sql = "SELECT UserName FROM ApplicationUser"
                + " WHERE FirstName = '%s' AND LastName = '%s'";

        sql = String.format(sql, firstName, secondName);
        logger.debug("getUserNameFromFirstAndSecondNames " + sql.replace(":firstName", firstName).replace(":secondName", secondName));
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getRandomResourceProfileAlias() {
        String sql = "SELECT TOP(1) Alias, Name FROM ResourceProfile"
                + " WHERE Active = 1 AND ActiveForClient = 1"
                + " AND Alias NOT LIKE 'system%'"
                + " AND Alias NOT LIKE 'Head of Managed Contracts'"
                + " AND Alias NOT LIKE 'Store User' ORDER BY NEWID()";

        logger.debug("getRandomResourceProfileAlias " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getRandomResourceProfileAliasWithWorkingHours() {
        String sql = "SELECT TOP(1) Alias, Name FROM ResourceProfile rp"
                + " JOIN ResourceProfileWorkingHours rpwh ON rp.Id = rpwh.ResourceProfileId"
                + " WHERE Active = 1 AND Alias NOT LIKE 'system%'"
                + " AND Alias NOT LIKE 'Head of Managed Contracts'"
                + " AND Alias NOT LIKE 'Store User' "
                + " AND rpwh.StartAt != '00:00:00.0000000'"
                + " AND rpwh.EndAt != '00:00:00.0000000'"
                + " ORDER BY NEWID()";

        logger.debug("getRandomResourceProfileAliasWithWorkingHours " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getRandomResourceProfileAliasWhoWorksFiveDaysOrLess() {
        String sql = "WITH NO_OF_DAYS AS("
                + " SELECT rp.Alias, rp.Name, COUNT(rpwh.StartDayOfTheWeek) NumberOfDays"
                + " FROM ResourceProfileWorkingHours rpwh"
                + " JOIN ResourceProfile rp ON rpwh.ResourceProfileId = rp.Id"
                + " WHERE rp.Active = 1"
                + " GROUP BY rp.Alias, rp.Name)"
                + " SELECT TOP(1) Alias, Name"
                + " FROM NO_OF_DAYS"
                + " WHERE NumberOfDays <= 5"
                + " ORDER BY NEWID()";

        logger.debug("getRandomResourceProfileAliasWhoWorksFiveDaysOrLess " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getResourceGrid(String resourceName, String resourceProfile) {
        String sql = "SELECT r.Name AS ResourceName, rp.Name AS ResourceProfile FROM Resource r"
                + " INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id"
                + " WHERE r.Name = '%s' AND rp.Name = '%s'";

        sql = String.format(sql, resourceProfile);
        logger.debug("getResourceAndResourceProfileAssignedToSite: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getResourceAndResourceProfileAssignedToSite(String resourceProfile) {
        String sql = "SELECT TOP(1) rp.Name ResourceProfileName, rp.Alias ResourceProfileAlias, r.Name ResourceName, s.Name SiteName"
                + " FROM Resource r"
                + " JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id"
                + " JOIN SiteResource sr ON r.Id = sr.ResourceId"
                + " JOIN Site s ON sr.SiteId = s.Id"
                + " WHERE r.Active = 1 AND rp.Name = '%s'"
                + " AND s.SiteStatusId = 1"
                + " ORDER BY NEWID()";

        sql = String.format(sql, resourceProfile);
        logger.debug("getResourceAndResourceProfileAssignedToSite: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getSupplierCodeNotInUse() {
        String sql = "SELECT TOP(1) Code"
                + " FROM uvw_SupplierTCodes"
                + " WHERE CodeInUse = 0"
                + " AND Code NOT LIKE 'SO:%'"
                + " ORDER BY NEWID()";

        logger.debug("getSupplierCodeNotInUse " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getPayrollCodeNotInUse() {
        String sql = "SELECT TOP(1) Code, Name"
                + " FROM uvw_PayrollTCodes"
                + " WHERE CodeInUse = 0 AND DATALENGTH(Name) > 0 "
                + " AND Code NOT IN (SELECT PayrollTCode FROM Resource WHERE PayrollTCode IS NOT NULL)"
                + " ORDER BY NEWID()";

        logger.debug("getPayrollCodeNotInUse " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public void insertIntoPayrollTable5(String code, String name) {
        String sql = "INSERT INTO %portaldb.[tblSun6TCode5](Code, Name)"
                + " VALUES ('%s', '%s')";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, code, name);
        logger.debug("insertIntoPayrollTable5: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void insertIntoPayrollTable6(String code, String name) {
        String sql = "INSERT INTO %portaldb.[tblSun6TCode6](Code, Name)"
                + " VALUES ('%s', '%s')";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, code, name);
        logger.debug("insertIntoPayrollTable6: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void insertIntoPayrollTable7(String code, String name) {
        String sql = "INSERT INTO %portaldb.[tblSun6TCode7](Code, Name)"
                + " VALUES ('%s', '%s')";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, code, name);
        logger.debug("insertIntoPayrollTable7: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void insertIntoTCodeSuppliersTable(String code, String name) {
        String sql = "INSERT INTO %portaldb.[tbltcodesuppliers](Code, Name)"
                + " VALUES ('%s', '%s')";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, code, name);
        logger.debug("insertIntoTCodeSuppliersTable: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public Integer getResourceAssignmentId(int resourceId) {
        String sql = "SELECT Id FROM ResourceAssignment"
                + " WHERE ResourceId = %d";

        sql = String.format(sql, resourceId);
        logger.debug("getResourceAssignmentId: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch(Exception e) {
            return null;
        }
    }

    public Integer getResourceAssignmentEventId(int resourceAssignmentId) {
        String sql = "SELECT ResourceAssignmentEventId FROM ResourceAssignmentStatusChange"
                + " WHERE ResourceAssignmentId = %d";

        sql = String.format(sql, resourceAssignmentId);
        logger.debug("getResourceAssignmentEventId: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch(Exception e) {
            return null;
        }
    }

    public List<Integer> getResourceAssignmentEventIds(int resourceAssignmentId) {
        String sql = "SELECT ResourceAssignmentEventId FROM ResourceAssignmentStatusChange"
                + " WHERE ResourceAssignmentId = %d";

        sql = String.format(sql, resourceAssignmentId);
        logger.debug("getResourceAssignmentEventIds: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), Integer.class);
        } catch(Exception e) {
            return null;
        }
    }

    public List<Integer> getResourceAssignmentEventIds(String resourceAssignmentIds) {
        String sql = "SELECT ResourceAssignmentEventId FROM ResourceAssignmentStatusChange"
                + " WHERE ResourceAssignmentId IN (%s)";

        sql = String.format(sql, resourceAssignmentIds);
        logger.debug("getResourceAssignmentEventIds: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), Integer.class);
        } catch(Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getResourceWorkingHours(String resourceProfileAlias) {
        String sql = "SELECT CASE WHEN rpwh.StartDayOfTheWeek = 1 THEN 7"
                + " WHEN rpwh.StartDayOfTheWeek = 2 THEN 1"
                + " WHEN rpwh.StartDayOfTheWeek = 3 THEN 2"
                + " WHEN rpwh.StartDayOfTheWeek = 4 THEN 3"
                + " WHEN rpwh.StartDayOfTheWeek = 5 THEN 4"
                + " WHEN rpwh.StartDayOfTheWeek = 6 THEN 5"
                + " WHEN rpwh.StartDayOfTheWeek = 7 THEN 6"
                + " END AS StartDayOfTheWeek,"
                + " DATENAME(WEEKDAY, rpwh.StartDayOfTheWeek - 2) DayOfTheWeek,"
                + " CONVERT(varchar(5), rpwh.StartAt, 108) StartTime, CONVERT(varchar(5), rpwh.EndAt, 108) EndTime"
                + " FROM ResourceProfileWorkingHours rpwh"
                + " JOIN ResourceProfile rp ON rpwh.ResourceProfileId = rp.Id"
                + " WHERE rp.Alias = '%s'"
                + " AND rp.Active = 1"
                + " ORDER BY StartDayOfTheWeek";

        sql = String.format(sql, resourceProfileAlias);
        logger.debug("getResourceWorkingHours: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getSitesAssignedToContractor(String contractorName) {
        String sql = "SELECT DISTINCT cbsom.SiteName, r.Name ContractorName"
                + " FROM Resource r"
                + " JOIN uvw_ContractorsBySiteOrManager cbsom ON r.Id = cbsom.ContractorResourceId"
                + " WHERE r.Name = '%s'";

        sql = String.format(sql, contractorName);
        logger.debug("getSitesAssignedToContractor " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getResourceProfileWhichCanBeAllocatedPermanentSite() {
        String sql = "SELECT TOP(1) Alias, Name FROM ResourceProfile"
                + " WHERE CanBeAllocatedPermanentSites = 1";

        logger.debug("getResourceProfileWhichCanBeAllocatedPermanentSite " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getRotaEntryForResource(int resourceId, int resourceProfileId, int rotaEntryTypeId) {
        String sql = "SELECT * FROM RotaEntry WHERE ResourceId = %d"
                + " AND ResourceProfileId = %d AND RotaEntryTypeId = %d";

        sql = String.format(sql, resourceId, resourceProfileId, rotaEntryTypeId);
        logger.debug("getResourceProfileWhichCanBeAllocatedPermanentSite " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isResourceGeoFenced(String resourceName) {
        String sql = "SELECT IsGeoFenced FROM Resource"
                + " WHERE Name = '%s'";

        sql = String.format(sql, resourceName);
        logger.debug("isResourceGeoFenced " + sql);

        int count = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        return count > 0;
    }

    public Map<String, Object> getAbsentResource(boolean isAbsent) {
        String sql = "SELECT r.Id, r.Name, rav.StartAt, rav.EndAt FROM Resource r "
                + "INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id "
                + "LEFT JOIN ResourceAvailability rav ON rav.ResourceId = r.Id AND rav.Active = 1 AND (rav.StartAt <= GETUTCDATE() AND rav.EndAt >= GETUTCDATE()) "
                + "WHERE rp.ResourceTypeId = 1 AND rp.JobCallouts = 1 AND r.Active = 1 AND rp.Active = 1  ";

        sql = isAbsent ? sql + "AND rav.Id IS NOT NULL" : sql + "AND rav.Id IS NULL";

        logger.debug("getAbsentResource: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public boolean isResourceAvailable(String resourceName) {
        String sql = "SELECT COUNT(r.Id) FROM Resource r "
                + "INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id "
                + "LEFT JOIN ResourceAvailability rav ON rav.ResourceId = r.Id AND rav.Active = 1 AND (rav.StartAt <= GETUTCDATE() AND rav.EndAt >= GETUTCDATE()) "
                + "WHERE r.Active = 1 AND rp.Active = 1 "
                + "AND rav.Id IS NULL "
                + "AND LTRIM(RTRIM(r.Name)) like '%s'";

        sql = String.format(sql, resourceName.trim().replace("'", "%"));

        logger.debug("isResourceAvailable: " + sql);
        int count = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        return count > 0;
    }

    public Map<String, Object> getContractorTechWithMultipleSites() throws Exception{
        String sql = FileHelper.getResourceFile("sql/select/GET_CONTRACTOR_WITH_MULTIPLE_SITES.sql");
        logger.debug("getContractorTechWithMultipleSites: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getUserToImpersonate(String resourceType) throws Exception{
        String sql = "SELECT TOP(1) au.FirstName, au.LastName, au.UserName, au.ResourceId, r.Name ResourceName"
                + " FROM ApplicationUser au"
                + " JOIN Resource r ON au.ResourceId = r.Id"
                + " JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id"
                + " JOIN ResourceType rt ON rp.ResourceTypeId = rt.Id"
                + " JOIN UserProfile up ON au.UserProfileId = up.Id";

        if(resourceType.equalsIgnoreCase("Contractor Admin")) {
            sql = sql + " JOIN %portaldb.tblPricebook pb on r.EpochId = pb.fld_int_ResourceId";
        }

        sql = sql + " WHERE au.Active = 1";

        if(resourceType.equalsIgnoreCase("Landlord")) {
            sql = sql + " AND up.Name = 'Landlord'";
        } else if(resourceType.equalsIgnoreCase("Contractor Tech")) {
            sql = sql + " AND up.Name = 'Contractor Technician'";
        } else if(resourceType.equalsIgnoreCase("Contractor Admin")) {
            sql = sql + " AND up.Name = 'Contractor Admin'"
                    + " AND pb.fld_str_PartCode = 'Standard Callout'"
                    + " AND pb.fld_cur_CostEach != 0.00"
                    + " AND r.OnlineInvoicingActive = 1"
                    + " AND r.UsesEngineers = 1";
        } else if(resourceType.equalsIgnoreCase("AMM Admin")) {
            sql = sql + " AND rp.Name = 'RFM'";
        }

        sql = sql + " ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getUserToImpersonate: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public boolean isResourceAbsent(int resourceId) {
        String sql = "SELECT COUNT(*) FROM"
                + " (SELECT r.Name"
                + " FROM ResourceAvailability ra"
                + " JOIN Resource r on ra.ResourceId = r.Id"
                + " WHERE r.Id = %d"
                + " AND GETDATE() BETWEEN StartAt AND EndAt) Absences";

        sql = String.format(sql, resourceId);
        logger.debug("isResourceAbsent " + sql);

        int count = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        return count > 0;
    }

    public Map<String, Object> getResource(int id) throws Exception{
        String sql = "SELECT r.Id ResourceId, r.Name ResourceName, au.UserName, up.Name UserProfileName, rp.Name ResourceProfileName, rt.Name ResourceTypeName "
                + "FROM Resource r "
                + "RIGHT JOIN ApplicationUser au ON r.Id = au.ResourceId "
                + "RIGHT JOIN UserProfile up ON au.UserProfileId = up.Id "
                + "RIGHT JOIN ResourceProfile rp ON rp.id = r.ResourceProfileId "
                + "RIGHT JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId "
                + "WHERE r.Active = 1 AND au.Active = 1 AND up.Active = 1 AND rp.Active = 1 AND rt.Active = 1 "
                + "AND r.Id = " + id;

        logger.debug("getResource: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getResourceWithEpochId(int epochId) throws Exception{
        String sql = "SELECT r.Id ResourceId, r.Name ResourceName, au.UserName, up.Name UserProfileName, rp.Name ResourceProfileName, rt.Name ResourceTypeName "
                + "FROM Resource r "
                + "RIGHT JOIN ApplicationUser au ON r.Id = au.ResourceId "
                + "RIGHT JOIN UserProfile up ON au.UserProfileId = up.Id "
                + "RIGHT JOIN ResourceProfile rp ON rp.id = r.ResourceProfileId "
                + "RIGHT JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId "
                + "WHERE r.Active = 1 AND au.Active = 1 AND up.Active = 1 AND rp.Active = 1 AND rt.Active = 1 "
                + "AND r.EpochId = " + epochId;

        logger.debug("getResourceWithEpochId: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getAvailableCityResource(int siteId){
        String sql = "SELECT r.Id ResourceId, r.Name ResourceName, au.UserName, up.Name UserProfileName, rp.Name ResourceProfileName, rt.Name ResourceTypeName "
                + "FROM Resource r "
                + "INNER JOIN ApplicationUser au ON r.Id = au.ResourceId "
                + "INNER JOIN UserProfile up ON au.UserProfileId = up.Id "
                + "INNER JOIN ResourceProfile rp ON rp.id = r.ResourceProfileId "
                + "INNER JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId "
                + "LEFT JOIN ResourceAvailability rav ON rav.ResourceId = r.Id AND rav.Active = 1 AND (rav.StartAt <= GETUTCDATE() AND rav.EndAt >= GETUTCDATE()) "
                + "INNER JOIN RotaEntry re ON r.Id = re.ResourceId "
                + "INNER JOIN OrganisationStructureSite oss ON oss.Id = re.OrganisationStructureSiteId "
                + "INNER JOIN Site s ON s.Id = oss.SiteId "
                + "WHERE r.Active = 1 AND au.Active = 1 AND up.Active = 1 AND rp.Active = 1 AND rt.Active = 1 "
                + "AND rav.Id IS NULL "
                + "AND s.Id = %d AND re.EndAt IS NULL";
        sql = String.format(sql, siteId);
        logger.debug("getAvailableCityResource: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getResourceFromUserName(String userName) throws Exception{
        String sql = "SELECT r.Id ResourceId, r.Name ResourceName, au.UserName, up.Name UserProfileName, rp.Name ResourceProfileName, rt.Name ResourceTypeName "
                + "FROM Resource r "
                + "RIGHT JOIN ApplicationUser au ON r.Id = au.ResourceId "
                + "RIGHT JOIN UserProfile up ON au.UserProfileId = up.Id "
                + "RIGHT JOIN ResourceProfile rp ON rp.id = r.ResourceProfileId "
                + "RIGHT JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId "
                + "WHERE r.Active = 1 AND au.Active = 1 AND up.Active = 1 AND rp.Active = 1 AND rt.Active = 1 "
                + "AND au.UserName = '%s'";
        sql = String.format(sql, userName.replace("'", "''"));

        logger.debug("getResourceFromUserName: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getCityResources() throws Exception {
        String sql = "SELECT DISTINCT r.Id ResourceId, r.Name ResourceName, au.UserName, up.Name UserProfileName, rp.Name ResourceProfileName, rt.Name ResourceTypeName "
                + "FROM Resource r "
                + "INNER JOIN ApplicationUser au ON r.Id = au.ResourceId "
                + "INNER JOIN UserProfile up ON au.UserProfileId = up.Id "
                + "INNER JOIN ResourceProfile rp ON rp.id = r.ResourceProfileId "
                + "INNER JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId "
                + "INNER JOIN RotaEntry re ON re.ResourceId = r.Id "
                + "INNER JOIN OrganisationStructureSite oss ON oss.id=re.OrganisationStructureSiteId "
                + "WHERE r.Active = 1 AND au.Active = 1 AND up.Active = 1 AND rp.Active = 1 AND rt.Active = 1 "
                + "AND rt.Name = 'City Resource' AND (up.Name LIKE '%Technician%' OR rp.Name LIKE '%Technician%') "
                + "AND (re.StartAt < cast(SWITCHOFFSET(GETDATE(), '+00:00') AS DATE) AND cast(SWITCHOFFSET(GETDATE(), '+00:00') AS DATE) < coalesce(re.EndAt, '2999-12-01'))";

        logger.debug("getCityResources: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getContractorAdminResources() throws Exception {
        String sql = "SELECT DISTINCT r.Id ResourceId, r.Name ResourceName, au.UserName, up.Name UserProfileName, rp.Name ResourceProfileName, rt.Name ResourceTypeName "
                + "FROM Resource r " + "RIGHT JOIN ApplicationUser au ON r.Id = au.ResourceId "
                + "INNER JOIN UserProfile up ON au.UserProfileId = up.Id "
                + "INNER JOIN ResourceProfile rp ON rp.id = r.ResourceProfileId "
                + "INNER JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId "
                + "INNER JOIN AssetClassificationSiteContractorMapping acscm ON acscm.ResourceId = r.Id "
                + "INNER JOIN AssetClassificationSite acs ON acscm.AssetClassificationSiteId = acs.Id "
                + "WHERE r.Active = 1 AND au.Active = 1 AND up.Active = 1 AND rp.Active = 1 AND rt.Active = 1 "
                + "AND up.Name = 'Contractor Admin' ";

        logger.debug("getContractorAdminResources: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getContractorTechResources() throws Exception {
        String sql = "SELECT DISTINCT r.Id ResourceId, r.Name ResourceName, au.UserName, up.Name UserProfileName, rp.Name ResourceProfileName, rt.Name ResourceTypeName "
                + "FROM Resource r " + "RIGHT JOIN ApplicationUser au ON r.Id = au.ResourceId "
                + "INNER JOIN UserProfile up ON au.UserProfileId = up.Id "
                + "INNER JOIN ResourceProfile rp ON rp.id = r.ResourceProfileId "
                + "INNER JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId "
                + "INNER JOIN AssetClassificationSiteContractorMapping acscm ON acscm.ResourceId = r.Id "
                + "INNER JOIN AssetClassificationSite acs ON acscm.AssetClassificationSiteId = acs.Id "
                + "WHERE r.Active = 1 AND au.Active = 1 AND up.Active = 1 AND rp.Active = 1 AND rt.Active = 1 "
                + "AND up.Name = 'Contractor Technician' ";

        logger.debug("getContractorTechResources: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getResources(String ids) throws Exception {
        String sql = "SELECT r.Id ResourceId, r.Name ResourceName, au.UserName, up.Name UserProfileName, rp.Name ResourceProfileName, rt.Name ResourceTypeName "
                + "FROM Resource r " + "RIGHT JOIN ApplicationUser au ON r.Id = au.ResourceId "
                + "RIGHT JOIN UserProfile up ON au.UserProfileId = up.Id "
                + "RIGHT JOIN ResourceProfile rp ON rp.id = r.ResourceProfileId "
                + "RIGHT JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId "
                + "WHERE r.Active = 1 AND au.Active = 1 AND up.Active = 1 AND rp.Active = 1 AND rt.Active = 1 "
                + "AND r.Id IN (%s)";        sql = String.format(sql, ids);

                logger.debug("getResource: " + sql);
                try {
                    return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
                } catch (Exception e) {
                    return null;
                }
    }

    public List<Map<String, Object>> getResources(String ids, String userProfileName) throws Exception {
        String sql = "SELECT r.Id ResourceId, r.Name ResourceName, au.UserName, up.Name UserProfileName, rp.Name ResourceProfileName, rt.Name ResourceTypeName "
                + "FROM Resource r " + "RIGHT JOIN ApplicationUser au ON r.Id = au.ResourceId "
                + "RIGHT JOIN UserProfile up ON au.UserProfileId = up.Id "
                + "RIGHT JOIN ResourceProfile rp ON rp.id = r.ResourceProfileId "
                + "RIGHT JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId "
                + "WHERE r.Active = 1 AND au.Active = 1 AND up.Active = 1 AND rp.Active = 1 AND rt.Active = 1 "
                + "AND r.Id IN (%s)";
        sql = String.format(sql, ids);

        if (userProfileName != null) {
            if (userProfileName.equals("City Technician")) {
                sql = sql + " AND rt.Name = 'City Resource' AND (up.Name LIKE '%Technician%' OR rp.Name LIKE '%Technician%')";
            } else {
                sql = sql + " AND up.Name = " + quote(userProfileName);
            }
        }

        logger.debug("getResources: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getNumOfResources(int count, String userProfileName) throws Exception {
        String sql = "SELECT TOP (%d) r.Id ResourceId, r.Name ResourceName, au.UserName, up.Name UserProfileName, rp.Name ResourceProfileName, rt.Name ResourceTypeName "
                + "FROM Resource r "
                + "RIGHT JOIN ApplicationUser au ON r.Id = au.ResourceId "
                + "RIGHT JOIN UserProfile up ON au.UserProfileId = up.Id "
                + "RIGHT JOIN ResourceProfile rp ON rp.id = r.ResourceProfileId "
                + "RIGHT JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId "
                + "WHERE r.Active = 1 AND au.Active = 1 AND up.Active = 1 AND rp.Active = 1 AND rt.Active = 1 ";
        sql = String.format(sql, count);

        if (userProfileName != null) {
            if (userProfileName.equals("City Technician")) {
                sql = sql + " AND rt.Name = 'City Resource' AND (up.Name LIKE '%Technician%' OR rp.Name LIKE '%Technician%')";
            } else {
                sql = sql + " AND up.Name = " + quote(userProfileName);
            }
        }

        sql = sql + " ORDER BY NEWID()";

        logger.debug("getResources: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getAssignedQuoteResourceID(int jobReference) {
        String sql = "SELECT TOP(1) r.Id FROM %helpdeskdb.Resource r INNER JOIN %helpdeskdb.ResourceProfile rp ON r.ResourceProfileId = rp.Id  " +
                " INNER JOIN %portaldb.tblhelpdeskresource hr ON r.EpochId = hr.fld_int_ID  " +
                " INNER JOIN %helpdeskdb.ApplicationUser au ON au.ResourceId = r.id " +
                " INNER JOIN %portaldb.vw_QuoteApprovalScenarios qas ON qas.ResourceId = r.EpochId  WHERE qas.FaultReference = " + jobReference +
                " AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, jobReference);
        logger.debug("getAssignedQuoteResourceID: " + sql);

        try {

            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getResourceReturningAwaitingPartsStatus(int jobRef) {
        String sql = "SELECT rae.Title FROM Job j"
                + " JOIN ResourceAssignment ra ON j.Id = ra.JobId"
                + " JOIN ResourceAssignmentEvent rae ON ra.Id = rae.ResourceAssignmentId"
                + " WHERE JobReference = %d"
                + " AND rae.ResourceAssignmentEventTypeId = 30";

        sql = String.format(sql, jobRef);
        logger.debug("getResourceReturningAwaitingPartsStatus: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            return null;
        }
    }

    public String getResourceReturningStatus(int jobRef) {
        String sql = "SELECT r.Name FROM Job j"
                + " JOIN ResourceAssignment ra ON j.Id = ra.JobId"
                + " JOIN SiteVisits sv ON ra.Id = sv.ResourceAssignmentId"
                + " JOIN Reason r ON r.Id = sv.ReturningToJobReason"
                + " WHERE JobReference = %d";

        sql = String.format(sql, jobRef);
        logger.debug("getResourceReturningStatus: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            return null;
        }
    }

    public String getNameFromApplicationUserId(String appUserId) {
        String sql = "SELECT FirstName + ' ' + LastName AS Name"
                + " FROM ApplicationUser"
                + " WHERE Id = '%s'";

        sql = String.format(sql, appUserId);
        logger.debug("getNameFromApplicationUserId: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            return null;
        }
    }

    public List<Integer> getResourceIdsCreatedByAutoTest() {

        String sql = "SELECT Id FROM Resource"
                + " WHERE Name LIKE 'TestAuto%'";

        logger.debug("getResourceIdsCreatedByAutoTest: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Integer> getResourceProfileIdsCreatedByAutoTest() {

        String sql = "SELECT Id FROM ResourceProfile"
                + " WHERE Name LIKE 'TestAuto%'";

        logger.debug("getResourceProfileIdsCreatedByAutoTest: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getUserIdsCreatedByAutoTest() {

        String sql = "SELECT Id FROM ApplicationUser"
                + " WHERE UserName LIKE 'TestAuto%'";

        logger.debug("getUserIdsCreatedByAutoTest: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Integer> getUserProfileIdsCreatedByAutoTest() {

        String sql = "SELECT Id FROM UserProfile"
                + " WHERE Name LIKE 'TestAuto%'";

        logger.debug("getUserProfileIdsCreatedByAutoTest: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Integer> getResourceAssignmentIdsCreatedByAutoTest(String resourceIds) {
        String sql = "SELECT Id FROM ResourceAssignment"
                + " WHERE ResourceId IN (%s)";

        sql = String.format(sql, resourceIds);
        logger.debug("getResourceAssignmentIdsCreatedByAutoTest: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), Integer.class);
        } catch(Exception e) {
            return null;
        }
    }

    public boolean isUserNamePresentInDb(String username) {
        if (username.contains("'")) {
            username = username.replaceAll("'", "''");
        }
        String sql = "SELECT COUNT(*) FROM ApplicationUser WHERE UserName = '%s'";
        sql = String.format(sql,  username);
        logger.debug("isUserNamePresentInDb: " + sql);
        int count = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        return count > 0;
    }

    public String getRandomResourceProfileForPpmType() {
        String sql = "SELECT TOP(1) Name FROM ResourceProfile"
                + " WHERE JobCallouts = 1 AND Active = 1 AND PartsDelivery = 1"
                + " ORDER BY NEWID()";

        logger.debug("getRandomResourceProfileForPpmType: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            return null;
        }
    }

    public Integer getRandomResourceWithResourceProfile(String resourceProfile) {
        String sql = "SELECT TOP(1) r.Id FROM ResourceProfile rp"
                + " JOIN Resource r ON rp.Id = r.ResourceProfileId"
                + " WHERE rp.Name = '%s'"
                + " ORDER BY NEWID()";

        sql = String.format(sql,  resourceProfile);
        logger.debug("getRandomResourceWithResourceProfile: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch(Exception e) {
            return null;
        }
    }

    public boolean doesJobHaveResourceAssigned(int jobRef) {
        String sql = "SELECT COUNT(*) FROM ResourceAssignment ra"
                + " JOIN Job j ON ra.JobId = j.Id"
                + " WHERE j.JobReference = %d";
        sql = String.format(sql,  jobRef);
        logger.debug("doesJobHaveResourceAssigned: " + sql);
        int count = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        return count > 0;
    }
}