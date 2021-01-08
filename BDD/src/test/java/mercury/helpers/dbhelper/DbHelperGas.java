package mercury.helpers.dbhelper;

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
import static mercury.helpers.StringHelper.quote;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DbConfigV2.class)
public class DbHelperGas {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private NamedParameterJdbcTemplate jdbc_portal;
    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;
    @Autowired private NamedParameterJdbcTemplate jdbc_test;
    @Autowired private DbUtilityHelper dbUtilityHelper;

    public Integer getGasLeakageCheckMethodForName(String name) {
        String sql = "SELECT Id FROM %helpdeskdb.GasLeakageCheckMethod WHERE Name = :name ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);

        try {
            logger.debug("getGasLeakageCheckMethodForName: " + sql);
            Integer result = jdbc_portal.queryForObject(sql, params, Integer.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getGasLeakageCheckMethod(int id) {
        String sql = "SELECT Name FROM GasLeakageCheckMethod WHERE Id = " + id;

        try {
            logger.debug("getGasLeakageCheckMethod: " + sql);
            String result = jdbc_helpdesk.queryForObject(sql,  new MapSqlParameterSource(), String.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getGasLeakageCodeForName(String name) {
        String sql = "SELECT Id FROM %helpdeskdb.GasLeakageCode WHERE Name = :name ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);

        try {
            logger.debug("getGasLeakageCodeForName: " + sql);
            Integer result = jdbc_portal.queryForObject(sql, params, Integer.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getGasLeakageCode(int id) {
        String sql = "SELECT Name FROM GasLeakageCode WHERE Id = " + id;

        try {
            logger.debug("getGasLeakageCode: " + sql);
            String result = jdbc_helpdesk.queryForObject(sql,  new MapSqlParameterSource(), String.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getGasFaultCode(int id) {
        String sql = "SELECT Name FROM GasAction WHERE Id = " + id;

        try {
            logger.debug("getGasAction: " + sql);
            String result = jdbc_helpdesk.queryForObject(sql,  new MapSqlParameterSource(), String.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getGasLeakLocationForName(String name) {
        String sql = "SELECT Id FROM %helpdeskdb.GasLeakLocation WHERE Name = :name ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);

        try {
            logger.debug("getGasLeakLocationForName: " + sql);
            Integer result = jdbc_portal.queryForObject(sql, params, Integer.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getGasLeakLocationGasLeakSubLocationMappingForName(String name) {
        String sql = "SELECT Id FROM %helpdeskdb.GasLeakLocationGasLeakSubLocationMapping WHERE Name = :name ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);

        try {
            logger.debug("getGasLeakLocationGasLeakSubLocationMappingForName: " + sql);
            Integer result = jdbc_portal.queryForObject(sql, params, Integer.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getGasLeakSubLocationForName(String name) {
        String sql = "SELECT Id FROM %helpdeskdb.GasLeakSubLocation WHERE Name = :name ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);

        try {
            logger.debug("getGasLeakSubLocationForName: " + sql);
            Integer result = jdbc_portal.queryForObject(sql, params, Integer.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getGasLeakLocation(int id) {
        String sql = "SELECT Name FROM GasLeakLocation WHERE Id = " + id;

        try {
            logger.debug("getGasLeakLocation: " + sql);
            String result = jdbc_helpdesk.queryForObject(sql,  new MapSqlParameterSource(), String.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getGasSafetyAdviceNoticeTypeForName(String name) {
        String sql = "SELECT Id FROM %helpdeskdb.GasSafetyAdviceNoticeType WHERE Name = :name ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);

        try {
            logger.debug("getGasSafetyAdviceNoticeTypeForName: " + sql);
            Integer result = jdbc_portal.queryForObject(sql, params, Integer.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getGasTypeIdForName(String name) {
        String sql = "SELECT Id FROM %helpdeskdb.GasType WHERE Name = :name ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);

        try {
            logger.debug("getGasTypeIdForName: " + sql);
            Integer result = jdbc_portal.queryForObject(sql, params, Integer.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getGasType(int id) {
        String sql = "SELECT Name FROM GasType WHERE Id = " + id;

        try {
            logger.debug("getGasType: " + sql);
            String result = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public int getGasApplianceTypeWithoutAsset() {
        String sql = "SELECT Id FROM GasApplianceType WHERE IsRemoteSystem = 0 AND Active = 1";

        try {
            logger.debug("getGasApplianceTypeWithoutAsset: " + sql);
            int result = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), int.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return 0;
        }
    }

    public float getGasCylinderCapacity(String gasType, String gasCylinderType) {
        String sql = "SELECT Capacity FROM GasCylinderCapacity gcc JOIN GasType gt ON gcc.GasTypeId = gt.Id JOIN GasCylinderType gct ON gct.Id = gcc.GasCylinderTypeId WHERE gt.Name = '%s' and gct.Name = '%s'";
        sql = String.format(sql, gasType, gasCylinderType);

        try {
            logger.debug("getGasCyclinderCapacity: " + sql);
            float result = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), float.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return 0;
        }
    }

    /**
     * get RefrigerantCharge for siteVisitId
     *
     * @param siteVisitId
     * @return refrigerantCharge
     */
    public float getRefrigerantCharge(int siteVisitId) {
        String sql = "SELECT * FROM ( " +
                "SELECT RefrigerantCharge FROM SiteVisits sv JOIN Asset a ON sv.AssetPlantId = a.Id WHERE sv.Id = %d " +
                "UNION " +
                "SELECT RefrigerantCharge FROM SiteVisits sv JOIN SiteVisitGasDetails svgd ON svgd.SiteVisitId = sv.Id JOIN Asset a ON svgd.AssetId = a.Id WHERE sv.Id = %d " +
                ") charge " +
                "WHERE RefrigerantCharge IS NOT NULL";
        sql = String.format(sql, siteVisitId, siteVisitId);

        try {
            logger.debug("getRefrigerantCharge: " + sql);
            float result = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Float.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return 0;
        }
    }

    public float getTotalGasUsedForSiteVisit(int siteVisitId) {
        String sql = "SELECT * FROM ( " +
                "SELECT SUM(BottleQuantity) AS TotalBottleQuantity FROM SiteVisitsGasUsage WHERE SiteVisitId = %d " +
                "UNION " +
                "SELECT SUM(BottleQuantity) AS TotalBottleQuantity FROM SiteVisitCylinderDetails WHERE Active = 1 AND SiteVisitGasDetailsId = %d " +
                ") bq " +
                "WHERE TotalBottleQuantity IS NOT NULL";
        sql = String.format(sql, siteVisitId, siteVisitId);

        try {
            logger.debug("getTotalGasUsedForSiteVisit: " + sql);
            float result = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Float.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return 0;
        }
    }

    public Integer getGasLeakCheckMethodForName(String name) {
        String sql = "SELECT Id FROM %helpdeskdb.GasLeakCheckMethod WHERE Name = :name ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);

        try {
            logger.debug("getGasLeakCheckMethodForName: " + sql);
            Integer result = jdbc_portal.queryForObject(sql, params, Integer.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getGasLeakCheckResultForName(String name) {
        String sql = "SELECT Id FROM %helpdeskdb.GasLeakCheckResultType WHERE Name = :name ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);

        try {
            logger.debug("getGasLeakCheckResultForName: " + sql);
            Integer result = jdbc_portal.queryForObject(sql, params, Integer.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getGasLeakCheckStatusForName(String name) {
        String sql = "SELECT Id FROM %helpdeskdb.GasLeakCheckStatus WHERE Name = :name ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);

        try {
            logger.debug("getGasLeakCheckStatusForName: " + sql);
            Integer result = jdbc_portal.queryForObject(sql, params, Integer.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getApplianceType(int id) {
        String sql = "SELECT Name FROM GasApplianceType WHERE Id = %d";
        sql = String.format(sql, id);

        try {
            logger.debug("getApplianceType: " + sql);
            String result = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getApplianceTypeForName(String name) {
        String sql = "SELECT Id FROM %helpdeskdb.GasApplianceType WHERE Name = :name AND Active = 1 ";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);

        try {
            logger.debug("getApplianceTypeForName: " + sql);
            Integer result = jdbc_portal.queryForObject(sql, params, Integer.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getApplianceTypeForNameList(String names) {
        String sql = "SELECT TOP(1) Id FROM %helpdeskdb.GasApplianceType WHERE Name IN (%s) AND Active = 1 ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, names);
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            logger.debug("getApplianceTypeForName: " + sql);
            Integer result = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getAssetId(String localIdentifier, String siteName) {
        String sql ="SELECT a.id "
                + " FROM %helpdeskdb.Site s INNER JOIN %helpdeskdb.Asset a  ON s.id = a.siteid"
                + " INNER JOIN %helpdeskdb.AssetClassification ac ON a.AssetClassificationId = ac.Id"
                + " INNER JOIN %helpdeskdb.AssetSubType ast ON ac.AssetSubTypeId = ast.Id"
                + " INNER JOIN %helpdeskdb.AssetType at ON at.id = ast.AssetTypeID"
                + " WHERE  ast.IsAssetPlant = 1"                // not sure this is required ... it restricts Asset Types to Refrigeration only!
                + " AND at.UsesGas = 1"
                + " /*AND at.Name = :assetTypeName*/"
                + " AND s.name = :siteName"
                + " AND a.LocalIdentifier = :localIdentifier";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("localIdentifier", localIdentifier);
        params.addValue("siteName", siteName);

        try {
            logger.debug("getAssetId: " + sql);
            Integer result = jdbc_portal.queryForObject(sql, params, Integer.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getAssetID(int siteId, boolean isPlant, String assetTypeName) {
        int isAssetPlant = isPlant ? 1 : 0;
        String sql ="SELECT TOP(1) a.Id "
                + "FROM Asset a "
                + "INNER JOIN AssetClassification ac ON a.AssetClassificationId = ac.Id "
                + "INNER JOIN AssetSubType ast ON ast.id = ac.AssetSubTypeId "
                + "INNER JOIN AssetType at ON ast.AssetTypeId = at.Id "
                + "WHERE a.SiteId = :siteId AND at.UsesGas = 1 "
                + "AND ast.IsAssetPlant = :isAssetPlant  AND at.Name = :assetTypeName ORDER BY NEWID()";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("siteId", siteId);
        params.addValue("isAssetPlant", isAssetPlant);
        params.addValue("assetTypeName", assetTypeName);

        try {
            logger.debug("getAssetID: " + sql.replace(":siteId", String.valueOf(siteId)).replace(":isAssetPlant", String.valueOf(isAssetPlant)).replace(":assetTypeName", quote(assetTypeName)));
            Integer result = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            logger.debug("getAssetID result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    /**
     * Used to determine if a follow up leak check visit is required
     * @param GasLeakCheckStatusId
     * @param GasLeakCheckResultTypeId
     * @param gasLeakFollowUpTestId
     * @return
     */
    public boolean followUpLeakCheckRequired(String GasLeakCheckStatusId, String GasLeakCheckResultTypeId, String gasLeakFollowUpTestId) {
        String sql =" SELECT  (glcs.IsResourceRequiredToReturn | glcrt.IsResourceRequiredToReturn | glfut.IsResourceRequiredToReturn) result  FROM"
                + " %helpdeskdb.GasLeakCheckStatus glcs,"
                + " %helpdeskdb.GasLeakCheckResultType glcrt,"
                + " %helpdeskdb.GasLeakFollowUpTest glfut"
                + " WHERE"
                + " glcs.Name = :GasLeakCheckStatusId AND glcs.Active = 1"
                + " AND glcrt.Name = : AND glcrt.Active = 1"
                + " AND glfut.Name = :gasLeakFollowUpTestId AND glfut.Active = 1";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("GasLeakCheckStatusId", GasLeakCheckStatusId);
        params.addValue("GasLeakCheckResultTypeId", GasLeakCheckResultTypeId);
        params.addValue("gasLeakFollowUpTestId", gasLeakFollowUpTestId);

        try {
            logger.debug("followUpLeakCheckRequired: " + sql);
            Boolean result = jdbc_portal.queryForObject(sql, params, Boolean.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return false;
        }
    }

    public Integer getGasSourceTypeIdForName(String name) {
        String sql = "SELECT Id FROM %helpdeskdb.GasSourceType WHERE Name = :name ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);

        try {
            logger.debug("getGasSourceTypeIdForName: " + sql);
            Integer result = jdbc_portal.queryForObject(sql, params, Integer.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getGasSurplusDestinationIdForName(String name) {
        String sql = "SELECT Id FROM %helpdeskdb.GasSurplusDestination WHERE Name = :name ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);

        try {
            logger.debug("getGasSourceTypeIdForName: " + sql);
            Integer result = jdbc_portal.queryForObject(sql, params, Integer.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getGasSurplusTypeIdForName(String name) {
        String sql = "SELECT Id FROM %helpdeskdb.GasSurplusType WHERE Name = :name ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);

        try {
            logger.debug("getGasSourceTypeIdForName: " + sql);
            Integer result = jdbc_portal.queryForObject(sql, params, Integer.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getGasCylinderTypeIdForName(String name) {
        String sql = "SELECT Id FROM GasCylinderType WHERE Name = :name ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);

        try {
            logger.debug("getGasCylinderTypeIdForName: " + sql);
            Integer result = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getGasCylinderType(int id) {
        String sql = String.format("SELECT Name FROM GasCylinderType WHERE Id = %d", id);
        logger.debug("getGasCylinderType: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getRandomCylinderType(String gasTypeName) {
        String sql = "SELECT TOP(1) gct.Name FROM GasCylinderCapacity gcc INNER JOIN GasType gt ON gcc.GasTypeId = gt.Id INNER JOIN GasCylinderType gct ON gcc.GasCylinderTypeId = gct.Id "
                + "WHERE gt.Name = '%s' ORDER BY NEWID()";
        sql = String.format(sql, gasTypeName);
        logger.debug("getRandomCylinderType: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    /**
     * Should a question be displayed. Based use ON the column DisplayFunction in
     * table MasterQuestion
     *
     * @param sql
     * @param params
     * @return
     */
    public boolean displayGasQuestion(String sql, MapSqlParameterSource params) {
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        try {
            return jdbc_test.queryForObject(sql, params, Boolean.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the maximum value for a column Based use ON the column MaxValueFunction
     * in table MasterQuestion
     *
     * @param sql
     * @param params
     * @return
     */
    public String getMaxValue(String sql, MapSqlParameterSource params) {
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getMaxValue " + sql);
        try {
            return jdbc_test.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getAssetSerialNo(int id) {
        String sql = "SELECT SerialNo FROM Asset WHERE Id = " + id;
        logger.debug("getAssetSerialNo: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getGasSourceType(int id) {
        String sql = "SELECT Name FROM GasSourceType WHERE Id = " + id;
        logger.debug("getGasSource: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getGasSurplusDestination(int id) {
        String sql = "SELECT Name FROM GasSurplusDestination WHERE Id = " + id;
        logger.debug("getGasSurplusDestination: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getGasSurplusType(int id) {
        String sql = "SELECT Name FROM GasSurplusType WHERE Id = " + id;
        logger.debug("getGasSurplusType: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getGasLeakCheckStatus(int id) {
        String sql = "SELECT Name FROM GasLeakCheckStatus WHERE Id = " + id;
        logger.debug("getGasLeakCheckStatus: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getGasLeakCheckMethod(int id) {
        String sql = "SELECT Name FROM GasLeakCheckMethod WHERE Id = " + id;
        logger.debug("getGasLeakCheckMethod: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getGasLeakCheckResultType(int id) {
        String sql = "SELECT Name FROM GasLeakCheckResultType WHERE Id = " + id;
        logger.debug("getGasLeakCheckResult: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getGasLeakSubLocation(int id) {
        String sql = "SELECT Name FROM GasLeakSubLocation WHERE Id = " + id;
        logger.debug("getGasLeakSubLocation: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getGasLeakSiteStatus(int id) {
        String sql = "SELECT Name FROM GasLeakSiteStatus WHERE Id = " + id;
        logger.debug("getGasLeakSiteStatus: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getGasLeakInitialTest(int id) {
        String sql = "SELECT Name FROM GasLeakInitialTest WHERE Id = " + id;
        logger.debug("getGasLeakInitialTest: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public String getGasLeakFollowUpTest(int id) {
        String sql = "SELECT Name FROM GasLeakFollowUpTest WHERE Id = " + id;
        logger.debug("getGasLeakFollowUpTest: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public List<String> getActiveGasTypes() {
        final String sql = "SELECT DISTINCT(gt.Name) FROM GasType gt "
                + "INNER JOIN GasCylinderCapacity gcc ON gcc.GasTypeId = gt.Id "
                + "INNER JOIN GasCylinderType gct ON gct.Id = gcc.GasCylinderTypeId "
                + "WHERE gt.Active = 1 AND gt.Name != 'Other' AND gcc.Capacity >= 10";
        logger.debug("getActiveGasTypes: " + sql);

        List<String> gasTypes = jdbc_helpdesk.query(sql, new MapSqlParameterSource(), new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("Name"));
            }
        });

        return gasTypes;
    }

    public Integer getAssetIdForSiteVisit(int siteVisitId) {
        String sql = String.format("SELECT a.Id FROM SiteVisits sv JOIN SiteVisitGasDetails svgd ON svgd.SiteVisitId = sv.Id JOIN Asset a ON svgd.AssetId = a.Id WHERE sv.Id = %d", siteVisitId);
        logger.debug("getAssetIdForSiteVisit: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public void updateRefrigerantCharge(int assetId, float maxCharge) {
        String sql = String.format("UPDATE Asset SET RefrigerantCharge = %d WHERE Id = %d", Math.round(maxCharge), assetId);
        logger.debug("updateRefrigerantCharge: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public String getRandomLeakCheckStatus(Boolean isResourceRequiredToReturn) {
        String sql = "SELECT TOP(1) Name FROM GasLeakCheckStatus ORDER BY NEWID()";
        if (isResourceRequiredToReturn != null) {
            int returnRequired = isResourceRequiredToReturn ? 1 : 0;
            sql = sql.replace("ORDER", String.format("WHERE IsResourceRequiredToReturn = %d AND NoCheckPerformed = 0 ORDER", returnRequired));
        }
        logger.debug("getRandomLeakCheckStatus: " + sql);
        try {
            return  jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getRandomLeakCheckResultType(Boolean isResourceRequiredToReturn) {
        String sql = "SELECT TOP(1) Name FROM GasLeakCheckResultType ORDER BY NEWID()";
        if (isResourceRequiredToReturn != null) {
            int returnRequired = isResourceRequiredToReturn ? 1 : 0;
            sql = sql.replace("ORDER", String.format("WHERE IsResourceRequiredToReturn = %d ORDER", returnRequired));
        }
        logger.debug("getRandomLeakCheckResultType: " + sql);
        try {
            return  jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getRandomLeakSiteStatus(Boolean isResourceRequiredToReturn) {
        String sql = "SELECT TOP(1) Name FROM GasLeakSiteStatus ORDER BY NEWID()";
        if (isResourceRequiredToReturn != null) {
            int returnRequired = isResourceRequiredToReturn ? 1 : 0;
            sql = sql.replace("ORDER", String.format("WHERE IsResourceRequiredToReturn = %d ORDER", returnRequired));
        }
        logger.debug("getRandomLeakSiteStatus: " + sql);
        try {
            return  jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getRandomGasLeakFollowUpTest(Boolean isResourceRequiredToReturn) {
        String sql = "SELECT TOP(1) Name FROM GasLeakFollowUpTest ORDER BY NEWID()";
        if (isResourceRequiredToReturn != null) {
            int returnRequired = isResourceRequiredToReturn ? 1 : 0;
            sql = sql.replace("ORDER", String.format("WHERE IsResourceRequiredToReturn = %d ORDER", returnRequired));
        }
        logger.debug("getRandomGasLeakFollowUpTest: " + sql);
        try {
            return  jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getRandomJobUpdatedByAdmin() {
        String sql = " SELECT TOP (1) JobReference FROM Job j INNER JOIN JobTimelineEvent jte ON jte.JobId = j.Id "
                + " WHERE jte.Title = 'Gas details updated' ";
        logger.debug("getRandomJobUpdatedByAdmin: " + sql);
        return  jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public String getLatestGasDetails(Integer jobReference) {
        String sql = " WITH CTE AS ( " +
                " SELECT ROW_NUMBER() OVER(ORDER BY LoggedAt DESC) AS Id,es.Detail1 FROM EventSummary es JOIN Job j ON j.Id = es.JobId " +
                " WHERE j.JobReference = :jobReference  " +
                " AND (ResourceAssignmentEventTypeId IN (5,36,30) or es.JobEventTypeId = 141) " +
                " ) " +
                " SELECT Detail1 FROM CTE WHERE Id = 2";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        try {
            return  jdbc_helpdesk.queryForObject(sql, params, String.class);
        }
        catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getValidGasCylinderDetails(String resourceTypeName) {
        String sql = "SELECT TOP(1) gt.Id GasTypeId, gt.Name GasTypeName, gcc.Capacity, " +
                "gcc.Id GasCylinderCapacityId, gct.id GasCylinderTypeId, gct.Name GasCylinderTypeName, gst.id GasSourceTypeId, gst.Name GasSourceTypeName " +
                "FROM GasType gt " +
                "INNER JOIN GasCylinderCapacity gcc ON gt.Id = gcc.GasTypeId " +
                "INNER JOIN GasCylinderType gct ON gcc.GasCylinderTypeId = gct.Id " +
                "INNER JOIN GasSourceTypeGasCylinderTypeMapping gstgctm ON gct.Id = gstgctm.GasCylinderTypeId " +
                "INNER JOIN GasSourceType gst ON gst.Id = gstgctm.GasSourceTypeId " +
                "INNER JOIN GasSourceTypeResourceProfileMapping gstrpm ON gst.Id = gstrpm.GasSourceTypeId " +
                "INNER JOIN ResourceProfile rp ON rp.Id = gstrpm.ResourceProfileId " +
                "INNER JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId " +
                "WHERE gt.Active = 1 AND gcc.Active = 1 and gct.Active = 1 AND gstgctm.Active = 1 AND gst.Active = 1 AND gstrpm.Active = 1 AND rp.Active = 1 " +
                "AND rp.Active =  1 AND rp.ActiveForClient = 1 AND rt.active = 1 " +
                "AND (rt.Name LIKE '%" + resourceTypeName + "%' OR rp.Name LIKE '%" + resourceTypeName + "%')" +
                "AND gcc.Capacity > 10 " +      // only required so that its possible to reduce installed amount to be less than capacity
                "ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getValidGasCylinderDetails: " + sql);
        try {
            return jdbc_helpdesk.queryForMap(sql, params);
        } catch (Exception e) {
            logger.debug("getValidGasCylinderDetails: " + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getValidLeakCheckDetails(Boolean returning) {
        int IsResourceRequiredToReturn = returning == true ? 1 : 0;

        String sql = "SELECT TOP(1) glcs.Id GasLeakCheckStatusId, glcs.Name GasLeakCheckStatusName, glcm.id GasLeakCheckMethodId, " +
                "glcm.Name GasLeakCheckMethodName, glcrt.id GasLeakCheckResultTypeId, glcrt.Name GasLeakCheckResultTypeName " +
                "FROM GasLeakCheckStatus glcs, GasLeakCheckMethod glcm, GasLeakCheckResultType glcrt " +
                "WHERE glcs.Active = 1 AND glcm.Active = 1 AND glcrt.Active = 1 " +
                "AND (glcrt.IsResourceRequiredToReturn = %d) " +
                "AND glcs.NoCheckPerformed = 0 " +
                "ORDER BY NEWID()";
        sql = String.format(sql, IsResourceRequiredToReturn, IsResourceRequiredToReturn);

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        logger.debug("getValidLeakCheckDetails: " + sql);
        try {
            return jdbc_helpdesk.queryForMap(sql, params);
        } catch (Exception e) {
            logger.debug("getValidLeakCheckDetails: " + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getValidLeakSiteInformation(Boolean returning) {
        int IsResourceRequiredToReturn = returning == true ? 1 : 0;

        String sql = "SELECT TOP(1) gll.Id GasLeakLocationId, gll.Name GasLeakLocationName, glss.Id GasLeakSiteStatusId, glss.Name GasLeakSiteStatusName, glsl.Id GasLeakSubLocationId, glsl.Name GasLeakSubLocationName, " +
                "glit.Id GasLeakInitialTestId, glit.Name GasLeakInitialTestName, glfut.Id GasLeakFollowUpTestId, glfut.Name GasLeakFollowUpTestName " +
                "FROM GasLeakLocation gll " +
                "INNER JOIN GasLeakLocationGasLeakSubLocationMapping gllglslm ON gllglslm.GasLeakLocationId = gll.Id " +
                "INNER JOIN GasLeakSubLocation glsl ON glsl.Id = gllglslm.GasLeakSubLocationId " +
                ", GasLeakSiteStatus glss, GasLeakInitialTest glit, GasLeakFollowUpTest glfut " +
                "WHERE gll.Active = 1 AND glsl.Active = 1 AND glss.Active = 1 AND glit.Active = 1 AND glfut.Active = 1 " +
                "AND ( glfut.IsResourceRequiredToReturn = %d AND glss.IsResourceRequiredToReturn = %d) " +
                "AND gll.FGasVersionId = 2 AND glsl.FGasVersionId = 2 " + // hard coded to latest version
                "ORDER BY NEWID()";

        sql = String.format(sql, IsResourceRequiredToReturn, IsResourceRequiredToReturn);

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        logger.debug("getValidLeakCheckDetails: " + sql);
        try {
            return jdbc_helpdesk.queryForMap(sql, params);
        } catch (Exception e) {
            logger.debug("getValidLeakCheckDetails: " + e.getMessage());
            return null;
        }
    }

    public void updateCylinderQtys(int cylinderId, Double bottleQuantity, Double surplus) {
        String sql = "UPDATE SiteVisitCylinderDetails SET BottleQuantity = %.2f, Surplus = %.2f WHERE Id = %d";
        sql = String.format(sql, bottleQuantity, surplus, cylinderId);
        logger.debug("updateCylinderQtys: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void updateCylinderInitialQty(int cylinderId, Double initalQuantity, Double surplus) {
        String sql = "UPDATE SiteVisitCylinderDetails SET InitialQuantity = %.2f, Surplus = %.2f WHERE Id = %d";
        sql = String.format(sql, initalQuantity, surplus, cylinderId);
        logger.debug("updateCylinderInitialQuantity: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public Double getGasCylinderCapacity(int gasCylinderCapacityId) {
        String sql = " SELECT Capacity FROM GasCylinderCapacity WHERE Id = " + gasCylinderCapacityId;
        logger.debug("getGasCylinderCapacity: " + sql);
        return  jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Double.class);
    }

    public String getRandomGasTypeWithConfiguredCapacity() {
        String sql = "SELECT TOP(1) GasTypeName FROM ( "
                + "SELECT gt.Id AS GasTypeId, gt.Name AS GasTypeName, 'Deposit' AS Type "
                + "FROM GasType gt "
                + "INNER JOIN GasCylinderCapacity gcc ON gcc.GasTypeId = gt.Id "
                + "WHERE gcc.GasCylinderTypeId = 1 "
                + "UNION "
                + "SELECT gt.Id AS GasTypeId, gt.Name AS GasTypeName, 'Disposable' As Type "
                + "FROM GasType gt "
                + "INNER JOIN GasCylinderCapacity gcc ON gcc.GasTypeId = gt.Id "
                + "WHERE gcc.GasCylinderTypeId = 2  "
                + ") x "
                + "GROUP BY GasTypeId, GasTypeName "
                + "HAVING COUNT(GasTypeName) = 2 "
                + "ORDER BY NEWID()";
        logger.debug("getRandomGasTypeWithConfiguredCapacity: " + sql);
        return  jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    public List<String> getGasTypesWithUnconfiguredCapacity() {
        String sql = "SELECT DISTINCT gt.Name FROM GasCylinderCapacity gcc RIGHT JOIN GasType gt ON gcc.GasTypeId = gt.Id WHERE gt.Name NOT IN ('Unknown', 'Other') AND gt.Active = 1";
        logger.debug("getGasTypesWithUnconfiguredCapacity: " + sql);
        List<String> gasTypes = jdbc_helpdesk.query(sql, new MapSqlParameterSource(), new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("Name"));
            }
        });
        return gasTypes;
    }

    public List<String> getConfiguredCylinderTypes(String gasTypeName) {
        String sql = "SELECT DISTINCT gct.Name FROM GasCylinderType gct "
                + "INNER JOIN GasCylinderCapacity gcc ON gcc.GasCylinderTypeId = gct.Id "
                + "INNER JOIN GasType gt ON gt.Id = gcc.GasTypeId "
                + "WHERE gt.Name = '%s'";
        sql = String.format(sql, gasTypeName);
        logger.debug("getConfiguredCylinderTypes: " + sql);
        List<String> gasTypes = jdbc_helpdesk.query(sql, new MapSqlParameterSource(), new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("Name"));
            }
        });
        return gasTypes;
    }

    public List<String> getConfiguredRefrigerantSources(String gasTypeName, int resourceAssignmentId) {
        String sql = "SELECT DISTINCT gst.Name FROM GasType gt "
                + "INNER JOIN GasCylinderCapacity gcc ON gcc.GasTypeId = gt.Id "
                + "INNER JOIN GasCylinderType gct ON gcc.GasCylinderTypeId = gct.Id "
                + "INNER JOIN GasSourceTypeGasCylinderTypeMapping gstgstm ON gstgstm.GasCylinderTypeId = gct.Id "
                + "INNER JOIN GasSourceType gst ON gst.Id = gstgstm.GasSourceTypeId "
                + "INNER JOIN GasSourceTypeResourceProfileMapping gstrpm ON gstrpm.GasSourceTypeId = gst.Id "
                + "INNER JOIN ResourceProfile rp ON rp.Id = gstrpm.ResourceProfileId "
                + "INNER JOIN Resource r ON rp.Id = r.ResourceProfileId "
                + "INNER JOIN ResourceAssignment ra ON ra.ResourceId = r.Id "
                + "WHERE gt.Name = '%s' AND ra.Id = %d";
        sql = String.format(sql, gasTypeName, resourceAssignmentId);
        logger.debug("getConfiguredRefrigerantSources: " + sql);
        List<String> refrigerantSources = jdbc_helpdesk.query(sql, new MapSqlParameterSource(), new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("Name"));
            }
        });
        return refrigerantSources;
    }

    public Map<String, Object> getAssetDetails(int assetId){
        String sql = "SELECT a.LocalIdentifier, ast.Name AS AssetSubTypeName, ast.IsAssetPlant, at.Name AS AssetTypeName, at.UsesGas FROM Asset a "
                + "INNER JOIN AssetClassification ac ON a.AssetClassificationId = ac.Id "
                + "INNER JOIN AssetSubType ast ON ac.AssetSubTypeId = ast.Id "
                + "INNER JOIN AssetType at ON ast.AssetTypeId = at.Id "
                + "WHERE a.Id = %d";
        sql = String.format(sql, assetId);

        logger.debug("getAssetDetails: " + sql);
        try {
            return jdbc_helpdesk.queryForMap(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getGasAssetTypes() {
        String sql = "SELECT Name FROM AssetType WHERE UsesGas = 1 AND Active = 1";
        logger.debug("getGasAssetTypes: " + sql);
        return (jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), String.class));
    }

    public boolean gasSiteVisitExists(int jobReference) {
        String sql = "SELECT sv.Id FROM SiteVisits sv "
                + "INNER JOIN ResourceAssignment ra ON ra.Id = sv.ResourceAssignmentId "
                + "INNER JOIN Job j ON j.Id = ra.JobId "
                + "WHERE sv.id IN (SELECT SiteVisitId FROM SiteVisitGasDetails) "
                + "AND j.JobReference = " + jobReference;

        logger.debug("gasSiteVisitExists: " + sql);
        try {
            Integer siteVisitId = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
            return siteVisitId != null;
        } catch (Exception e) {
            return false;
        }
    }
}
