package mercury.helpers.dbhelper;

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
import mercury.helpers.DbUtilityHelper;
import mercury.helpers.FileHelper;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfigV2.class)
public class DbHelperJobData {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private DbUtilityHelper dbUtilityHelper;
    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;
    @Autowired private TestData testData;


    public Map<String, Object> getJobData(Boolean withIpad, Boolean withEmail, Boolean withMobile) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_DATA_SUITABLE_FOR_CITY_TECH_AND_CONTRACTOR.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        if (withIpad != null) {
            sql = withIpad ? sql.replace("--with ipad: ", "") : sql.replace("--no ipad: ", "");
        }

        if (withEmail != null) {
            sql = withEmail ? sql.replace("--with email: ", "") : sql.replace("--no email: ", "");
        }

        if (withMobile != null) {
            sql = withMobile ? sql.replace("--with mobile: ", "") : sql.replace("--no mobile: ", "");
        }

        logger.debug("getJobData: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getJobDataForPriority(int faultPriority, String value, Boolean withIpad, Boolean withEmail, Boolean withMobile) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_DATA_SUITABLE_FOR_CITY_TECH_AND_CONTRACTOR.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        if (withIpad != null) {
            sql = withIpad ? sql.replace("--with ipad: ", "") : sql.replace("--no ipad: ", "");
        }

        if (withEmail != null) {
            sql = withEmail ? sql.replace("--with email: ", "") : sql.replace("--no email: ", "");
        }

        if (withMobile != null) {
            sql = withMobile ? sql.replace("--with mobile: ", "") : sql.replace("--no mobile: ", "");
        }

        sql = sql.replace("--with priority:", "");
        sql = String.format(sql, value, faultPriority);

        logger.debug("getJobData: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getJobDataForSite(int siteId, Integer priority) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_DATA_FOR_RESOURCE.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = priority != null ? sql.replace("--AND fp.priority", "AND fp.priority").replace(":priority", priority.toString()) : sql;
        sql = sql.replace("ORDER", " AND s.Id = " + siteId +" ORDER");

        logger.debug("getJobDataForSite: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getJobDataForResource(int resourceId, Integer priority) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_DATA_FOR_RESOURCE.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = priority != null ? sql.replace("--AND fp.priority", "AND fp.priority").replace(":priority", priority.toString()) : sql;
        sql = sql.replace("--AND r.Id", "AND r.Id").replace(":resourceId", String.valueOf(resourceId));

        logger.debug("getJobDataForResource: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getJobDataForResourceAndSite(Integer resourceId, int siteId, Integer priority) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_DATA_FOR_RESOURCE.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = priority != null ? sql.replace("--AND fp.priority", "AND fp.priority").replace(":priority", priority.toString()) : sql;
        sql = resourceId != null ? sql.replace("--AND r.Id", "AND r.Id").replace(":resourceId", String.valueOf(resourceId)) : sql;
        sql = sql.replace("ORDER BY l.Active", "AND sr.SiteId = " + String.valueOf(siteId));  // Replacing Order By statement due to query timeouts

        logger.debug("getJobDataForResourceAndSite: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getJobDataForAnyResource() throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_DATA_FOR_ANY_RESOURCE.sql");

        logger.debug("getJobDataForAnyResource: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getJobDataForFgas() throws Exception{
        boolean isCityTech = testData.getBoolean("useResourceTypeName") && testData.getString("resourceTypeName").equals("City Resource");
        String resourceFile = isCityTech ? "GET_JOB_DATA_FOR_FGAS_CITY_TECH.sql" : "GET_JOB_DATA_FOR_FGAS_CONTRACTOR.sql";

        String sql = FileHelper.getResourceFile("sql/select/", resourceFile);
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        if (testData.getString("assetType") != null && testData.getString("assetType").equalsIgnoreCase("Refrigeration")) {
            sql = sql.replace("AND ast.IsAssetPlant = 0", "AND ast.IsAssetPlant = 1");
        }

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getJobDataForFgas: " + sql);
        try {
            return jdbc_helpdesk.queryForMap(sql, params);

        } catch (Exception e) {
            logger.debug("getJobDataForFgas: " + e.getMessage());
            return null;
        }
    }


    /**
     * Fetch ResourceId, UserName and JobReference entity who has jobs in the awaiting feedback response state and profile of profileName
     * @param profileName
     * @return UserJob
     * @throws Exception
     */
    public Map<String, Object> getUserWithJobAwaitingFeedbackResponse(String profileName, int jobReference) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_AWAITING_FEEDBACK.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, jobReference, profileName);

        logger.debug("getUserWithJobAwaitingFeedbackResponse " + sql);
        try {
            return jdbc_helpdesk.queryForMap(sql, new MapSqlParameterSource());

        } catch (Exception e) {
            logger.debug("getUserWithJobAwaitingFeedbackResponse: " + e.getMessage());
            return null;
        }
    }
    
    public Integer getResoponsePriorityId(int priority) throws Exception {
        String sql = "SELECT TOP(1) fpm.ResponsePriorityId FROM FaultPriorityMapping fpm INNER JOIN uvw_FaultPriorityMappings fpm1 " + 
                " ON fpm.id = fpm1.id " + 
                " INNER JOIN JobRoutingProcess jp ON jp.id = fpm.JobRoutingProcessId " + 
                " WHERE jp.NonDeferrable = 1 " + 
                " AND jp.requiresReview = 0 " ;
        
        sql = priority == 0 ? sql : sql + String.format(" AND fpm1.Priority = %d", priority);
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getResoponsePriorityId " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);

        } catch (Exception e) {
            logger.debug("getResoponsePriorityId: " + e.getMessage());
            return null;
        }
    }
}
