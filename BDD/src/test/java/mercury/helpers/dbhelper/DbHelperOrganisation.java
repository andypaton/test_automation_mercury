package mercury.helpers.dbhelper;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mercury.database.config.DbConfigV2;
import mercury.databuilders.TestData;
import mercury.helpers.FileHelper;
import mercury.helpers.PropertyHelper;
import static mercury.helpers.StringHelper.quote;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfigV2.class)
public class DbHelperOrganisation {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;
    @Autowired private TestData testData;
    @Autowired private PropertyHelper propertyHelper;


    /**
     * Get the organisation structure for a site
     * @param siteId
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getOrganisationForSite(Integer siteId) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_ORG_STRUCTURE_ABOVE.sql");
        sql = sql.replace(":whereClause", "WHERE OSS.SiteId = " + siteId.toString());

        logger.debug("getOrganisationForSite: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    /**
     * Get the organisation structure for a jobReference
     * @param jobReference
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getOrganisationForJob(Integer jobReference) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_ORG_STRUCTURE_ABOVE.sql");
        sql = sql.replace(":whereClause", "WHERE OSS.SiteId = (SELECT SiteId FROM Job WHERE JobReference = " + jobReference.toString() + ")");

        logger.debug("getOrganisationForJob: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    /**
     * Get the organisation structure above a resource
     * @param resourceId
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getOrgStructureAboveResource(Integer resourceId) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_ORG_STRUCTURE_ABOVE.sql");
        sql = sql.replace(":whereClause", "WHERE R.Id = " + resourceId.toString());

        logger.debug("getOrganisationForResource: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    /**
     * Get the organisation Line Management structure for a resource
     * @param resourceId
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getOrgStructureBelowResource(Integer resourceId) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_ORG_STRUCTURE_BELOW.sql");
        sql = sql.replace(":whereClause", "WHERE R.Id = " + resourceId.toString());

        logger.debug("getOrganisationForResource: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    /**
     * Get the organisation Line Management structure for a tech position
     * @return
     * @throws Exception
     */
    public Map<String, Object> getOrgStructureForTechPosition() throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_ORG_STRUCTURE_BELOW.sql");
        sql = sql.replace(":whereClause", "WHERE OSM.Id IS NOT NULL");
        sql = sql.replaceAll("DISTINCT", "TOP(1)");
        sql = sql.replace("ORDER BY ParentOrganisationStructureId", "AND os.OrgStructureType = 'Tech Position' ORDER BY NEWID()");
        logger.debug("getOrgStructureForTechPosition: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    /**
     * Get the resource with profile (eg. 'RFM') for a resource Id (eg. a city techs resourceId)
     * @param resourceId
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getResourceWithProfile(String resourceProfileName, Integer resourceId) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_ORG_STRUCTURE_ABOVE.sql");
        sql = sql.replace(":whereClause", "WHERE R.Id = " + resourceId.toString());
        sql = sql.replace("ORDER BY", String.format(" AND rp.Name = '%s' ORDER BY", resourceProfileName));

        logger.debug("getResourceWithProfile: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    /**
     * Get Line manager for resourceId (ie. a 'Refrigeration Supervisor' for a 'Refrigeration Technician', 'HVAC Supervisor' for a 'HVAC Technician', otherwise the 'RFM')
     * @param resourceId
     * @return
     * @throws Exception
     */
    public Map<String, Object> getLineManager(Integer resourceId) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_ORG_STRUCTURE_ABOVE.sql");
        sql = sql.replace(":whereClause", "WHERE R.Id = " + resourceId.toString());
        sql = sql.replaceAll("DISTINCT", "TOP(1)");

        String env = propertyHelper.getEnv().toLowerCase();
        if ( env.contains("uswm") || env.contains("usad") ) {
            sql = sql.replace("ORDER BY ParentOrganisationStructureId", " AND (rp.Name = 'RFM' OR rp.Name LIKE '%Director')");
        } else {
            sql = sql.replace("ORDER BY ParentOrganisationStructureId", " AND (rp.Name LIKE '%Supervisor' OR rp.Name LIKE '%Director')");
        }

        logger.debug("getLineManager: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getLineManager(Integer resourceId, String resourceProfile) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_ORG_STRUCTURE_ABOVE.sql");
        sql = sql.replace(":whereClause", "WHERE R.Id = " + resourceId.toString());
        sql = sql.replaceAll("DISTINCT", "TOP(1)");
        sql = sql.replace("ORDER BY ParentOrganisationStructureId", String.format("AND rp.Name = '%s'", resourceProfile));

        logger.debug("getLineManager: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getLineManagers(String resourceProfileName, Integer siteId, Integer resourceId) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_LINE_MANAGERS.sql");
        sql = sql.replace(":siteId", siteId.toString());
        sql = sql.replace(":resourceId", resourceId.toString());
        sql = sql.replace(":resourceProfileName", quote(resourceProfileName));

        logger.debug("getLineManagers: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getOperationalManagers(Integer siteId, Integer resourceId) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_OPERATIONAL_MANAGERS.sql");
        sql = sql.replace(":siteId", siteId.toString());
        sql = sql.replace(":resourceId", resourceId.toString());

        logger.debug("getOperationalManagers: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    /**
     * Get resources with profile (eg. 'RFM' or 'Supervisor' or 'City Tech')
     * @param orgStructureType
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getResourcesWithProfile(String profile) throws Exception {
        String sql = "SELECT DISTINCT r.Id AS ResourceId, r.Name AS ResourceName, rp.Name AS ResourceProfileName, rt.Name AS ResourceTypeName, au.UserName, OS.Name AS OrgStructureName, ost.Name AS OrgStructureType "
                + "FROM Resource r "
                + "INNER JOIN ResourceProfile rp ON rp.Id = r.ResourceProfileId "
                + "INNER JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId "
                + "INNER JOIN ApplicationUser au ON au.ResourceId = r.Id "
                + "INNER JOIN OrganisationStructureManager osm ON osm.ApplicationUserId = au.Id "
                + "INNER JOIN OrganisationStructure os ON os.Id = osm.OrganisationStructureId "
                + "INNER JOIN OrganisationStructureType ost ON ost.Id = os.OrganisationStructureTypeId "
                + "WHERE r.Active = 1 AND os.Active = 1 AND ost.Active = 1 AND osm.Active = 1 ";

        if (profile.equals("Supervisor")) {
            sql = sql + "AND rp.Name IN ('HVAC Supervisor', 'Refrigeration Supervisor')";

        } else if (profile.contains("Tech")) {
            sql = sql + "AND rp.JobCallouts = 1 AND ost.Name = 'Tech Position'";

        } else {
            sql = sql + String.format("AND rp.Name = '%s'", profile);
        }

        logger.debug("getResourcesWithProfile: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getOnCallResourceId(int siteId, String startAt, String endAt) {
        String sql = "SELECT TOP(1) re.ResourceId FROM RotaEntry re "
                + "INNER JOIN OrganisationStructureSite oss ON oss.Id = re.OrganisationStructureSiteId "
                + "WHERE RotaEntryTypeId = 8 AND re.Active = 1 "
                + "AND oss.SiteId = %d "
                + "AND re.StartAt <= '%s' "
                + "AND re.EndAt >= '%s'";
        sql = String.format(sql,  siteId, startAt, endAt);

        try {
            Integer result = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
            testData.put("sql", sql + ". Result: " + result);
            return result;
        } catch (Exception e) {
            testData.put("sql", sql + ". Result: null");
            return null;
        }
    }

    public List<Integer> getSitesInOrganisationStructure(int orgStructureId) {
        String sql = "SELECT SiteId FROM OrganisationStructureSite WHERE OrganisationStructureId = " + orgStructureId;
        testData.put("sql", sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getResourceWithCover(String profile) throws Exception {
        String sql = "SELECT TOP(1) ost.Name, au.ResourceId, r.Name AS ResourceName, rp.Name AS ResourceProfileName, rav.StartAt, rav.EndAt, rav.CoveringResourceId, cover.Name AS CoveringName "
                + "FROM ResourceAvailability rav "
                + "INNER JOIN Resource r ON rav.ResourceId = r.Id "
                + "INNER JOIN ResourceProfile rp ON rp.Id = r.ResourceProfileId "
                + "LEFT JOIN Resource cover ON rav.CoveringResourceId = cover.Id "
                + "INNER JOIN ApplicationUser au ON au.ResourceId = r.Id "
                + "INNER JOIN OrganisationStructureManager osm ON osm.ApplicationUserId = au.Id  "
                + "INNER JOIN OrganisationStructure os ON os.Id = osm.OrganisationStructureId  "
                + "INNER JOIN OrganisationStructureType ost ON ost.Id = os.OrganisationStructureTypeId  "
                + "WHERE r.Active = 1 AND rav.Active = 1 AND cover.Active = 1 ";

        if (profile.equals("RFM")) {
            sql = sql + "AND rp.Name = 'RFM'";

        } else if (profile.equals("Supervisor")) {
            sql = sql + "AND rp.Name IN ('HVAC Supervisor', 'Refrigeration Supervisor')";

        } else {
            sql = sql + "AND ost.Name = 'Tech Position'";
        }
        sql = sql + "ORDER BY NEWID()";

        logger.debug("getResourcesWithCover: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }
}