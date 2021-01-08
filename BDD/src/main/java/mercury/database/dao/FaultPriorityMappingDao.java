package mercury.database.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.FaultPriorityMapping;
import mercury.helpers.DbUtilityHelper;
import mercury.helpers.FileHelper;

@Repository
public class FaultPriorityMappingDao {

    @Autowired private DbUtilityHelper dbUtilityHelper;

    private static final Logger logger = LogManager.getLogger();

    private static final String ALL_FIELDS = " Id"	+
            ", SiteTypeId" +
            ", SiteTypeName" +
            ", AssetSubTypeId" +
            ", AssetSubTypeName" +
            ", AssetTypeName" +
            ", AssetClassificationId" +
            ", AssetClassificationName" +
            ", FaultTypeId" +
            ", FaultTypeName" +
            ", ResponsePriorityId" +
            ", Priority" +
            ", Detail" +
            ", SelfAssigned" +
            ", TechBureau" +
            ", ImmediateCallout ";

    private static final String GET_FAULT_PRIORITY_MAPPINGS_SQL
    = "SELECT fpm.Id, SiteTypeId, SiteTypeName, AssetSubTypeId, " + "AssetSubTypeName, at.Name AS AssetTypeName, AssetClassificationId, "
            + "AssetClassificationName, FaultTypeId, FaultTypeName, ResponsePriorityId, "
            + "Priority, Detail, SelfAssigned, TechBureau, ImmediateCallout "
            + " FROM uvw_FaultPriorityMappings fpm "
            + " INNER JOIN AssetSubType ast ON ast.Id = fpm.AssetSubTypeId "
            + " INNER JOIN AssetType at ON at.Id = ast.AssetTypeId"
            + " INNER JOIN FaultType ft ON fpm.FaultTypeId = ft.Id";

    private static final String GET_RANDOM_FAULT_PRIORITY_MAPPINGS_SQL = "SELECT TOP(1) " + ALL_FIELDS + " FROM (%s) randomOrder ORDER BY NEWID()";

    private static final String WHERE_SITE_ID_SQL = " WHERE SiteTypeId = (SELECT siteTypeId FROM Site WHERE Id = %d) ";

    private static final String AND_PRIORITY_DETAIL_SQL = " AND Detail = '%s' ";
    private static final String AND_PRIORITY_SQL = " AND Priority = %d ";
    private static final String AND_IMMEDIATE_CALLOUT = " AND ImmediateCallout = %d ";
    private static final String AND_FAULT_PRIORITY_SQL = " AND FaultPriorityId = %d ";
    private static final String AND_ASSET_CLASSIFICATION_ID_SQL = " AND AssetClassificationId = %d ";

    private static final String AND_TAGGED_ASSET_SQL = " AND AssetSubTypeId IN (SELECT AssetSubTypeId FROM uvw_Asset WHERE AssetTag IS NOT NULL) ";
    private static final String AND_NOT_TAGGED_ASSET_SQL = " AND AssetSubTypeId IN (SELECT AssetSubTypeId FROM uvw_Asset WHERE AssetTag IS NULL) ";

    private static final String RANDOM_ORDER = " ORDER BY NEWID() ";


    @Autowired
    private EntityManager entityManager;


    /**
     * Fetch random FaultPriorityMapping for siteType matching priority
     * @param siteType, priorityDetail, taggedAsset
     * @return
     */
    public FaultPriorityMapping getRandomFaultPriorityMapping(Integer siteType, String priorityDetail, boolean taggedAsset) {

        String sql = GET_FAULT_PRIORITY_MAPPINGS_SQL + String.format(dbUtilityHelper.dbNameSchemaReplacement(WHERE_SITE_ID_SQL), siteType)
        + String.format(AND_PRIORITY_DETAIL_SQL, priorityDetail);
        sql = taggedAsset ? sql + AND_TAGGED_ASSET_SQL : sql + AND_NOT_TAGGED_ASSET_SQL;

        String queryStatement = String.format(GET_RANDOM_FAULT_PRIORITY_MAPPINGS_SQL, sql);
        logger.debug("getRandomFaultPriorityMapping: " + queryStatement);
        Query query =  entityManager.createNativeQuery(queryStatement, FaultPriorityMapping.class);
        return (FaultPriorityMapping) query.getSingleResult();
    }

    public FaultPriorityMapping getFaultPriorityMappingForSite(Integer siteId, int immediateCallout) {

        String sql = GET_FAULT_PRIORITY_MAPPINGS_SQL + String.format(dbUtilityHelper.dbNameSchemaReplacement(WHERE_SITE_ID_SQL), siteId)
        + String.format(AND_IMMEDIATE_CALLOUT, immediateCallout)
        + " AND fpm.TechBureau = 0 " // dont want the job to end up in tech bureau
        + " AND ft.Active = 1";

        String queryStatement = String.format(GET_RANDOM_FAULT_PRIORITY_MAPPINGS_SQL, sql);
        logger.debug("getFaultPriorityMappingForSite: " + queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, FaultPriorityMapping.class);
            return (FaultPriorityMapping) query.getSingleResult();
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    public FaultPriorityMapping getRandomFaultPriorityMappingForSite(Integer siteId, Integer priority, Integer faultPriority, Integer assetClassificationId) {

        String sql = GET_FAULT_PRIORITY_MAPPINGS_SQL + String.format(dbUtilityHelper.dbNameSchemaReplacement(WHERE_SITE_ID_SQL), siteId)
        + String.format(AND_PRIORITY_SQL, priority)
        + String.format(AND_FAULT_PRIORITY_SQL, faultPriority)
        + String.format(AND_ASSET_CLASSIFICATION_ID_SQL, assetClassificationId);

        String queryStatement = String.format(GET_RANDOM_FAULT_PRIORITY_MAPPINGS_SQL, sql);
        logger.debug("getRandomFaultPriorityMappingForSite: " + queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, FaultPriorityMapping.class);
            return (FaultPriorityMapping) query.getSingleResult();
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    public FaultPriorityMapping getRandomFaultPriorityMappingForSite(Integer siteId, Integer assetClassificationId) {

        String sql = GET_FAULT_PRIORITY_MAPPINGS_SQL + String.format(dbUtilityHelper.dbNameSchemaReplacement(WHERE_SITE_ID_SQL), siteId)
        + String.format(AND_ASSET_CLASSIFICATION_ID_SQL, assetClassificationId);

        String queryStatement = String.format(GET_RANDOM_FAULT_PRIORITY_MAPPINGS_SQL, sql);
        logger.debug("getRandomFaultPriorityMappingForSite: " + queryStatement);
        Query query =  entityManager.createNativeQuery(queryStatement, FaultPriorityMapping.class);
        return (FaultPriorityMapping) query.getSingleResult();
    }

    public FaultPriorityMapping getRandomForSite(Integer siteId, String priorityCsvList) throws Exception {

        String sql = FileHelper.getResourceFile("sql/select/", "GET_FAULT_PRIORITY_MAPPING.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, priorityCsvList) + " AND s.Id = " + siteId + RANDOM_ORDER;

        String queryStatement = String.format(GET_RANDOM_FAULT_PRIORITY_MAPPINGS_SQL, sql);
        logger.debug("getRandomForSite: " + queryStatement);
        Query query = entityManager.createNativeQuery(queryStatement, FaultPriorityMapping.class);
        return (FaultPriorityMapping) query.getSingleResult();
    }

    public FaultPriorityMapping getRandomForSiteAndImmediateCallout(Integer siteId, String priorityCsvList, boolean immediateCallout) throws Exception {

        int bit = immediateCallout ? 1 : 0;

        String sql = FileHelper.getResourceFile("sql/select/", "GET_FAULT_PRIORITY_MAPPING.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, priorityCsvList)
                + " AND s.Id = " + siteId
                + " AND fpm.ImmediateCallout = " + bit
                + RANDOM_ORDER;

        String queryStatement = String.format(GET_RANDOM_FAULT_PRIORITY_MAPPINGS_SQL, sql);
        logger.debug("getRandomForSite: " + queryStatement);
        Query query = entityManager.createNativeQuery(queryStatement, FaultPriorityMapping.class);
        return (FaultPriorityMapping) query.getSingleResult();
    }

    public FaultPriorityMapping get(String priorityCsvList, int siteId, int contractorResourceId, int cityTechResourceProfileId) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_FAULT_PRIORITY_MAPPING.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, priorityCsvList)
                + " AND s.Id = " + siteId
                + " AND acscm.ResourceId = " + contractorResourceId
                + " AND rpc.ResourceProfileId = " + cityTechResourceProfileId
                + RANDOM_ORDER;
        logger.debug("getFaultPriorityMapping: " + sql);
        return getFaultPriorityMapping(sql);
    }

    private FaultPriorityMapping getFaultPriorityMapping(String sql) {
        try {
            Query query =  entityManager.createNativeQuery(sql, FaultPriorityMapping.class);
            return (FaultPriorityMapping) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
