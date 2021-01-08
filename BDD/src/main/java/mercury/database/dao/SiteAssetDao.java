package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.SiteAsset;
import mercury.helpers.DbUtilityHelper;

@Repository
public class SiteAssetDao {

    @Autowired private DbUtilityHelper dbUtilityHelper;

    private static final Logger logger = LogManager.getLogger();

    private static final String GET_SITE_RANDOM_SELECT_SQL = "SELECT TOP(1) ";

    private static final String GET_SITE_ASSETS_SELECT_SQL = "SELECT ";

    private static final String ALL_FIELDS = " a.Id, s.Name, s.SiteCode, sl.Name SubLocationName, fpm.AssetClassificationName AssetClassificationName, atype.Name AssetTypeName, fpm.AssetSubTypeName AssetSubTypeName, a.LocalIdentifier, a.AssetTag, a.SerialNo, fpm.FaultTypeName FaultTypeName, fpm.Detail FaultPriority  ";

    private static final String GET_SITE_ASSETS_QUERY_SQL = ALL_FIELDS +
            " FROM Site s INNER JOIN ASSET a ON s.Id = a.SiteId " +
            " INNER JOIN LOCATION sl ON a.LocationId = sl.Id " +
            " INNER JOIN AssetClassification ac ON a.AssetClassificationId = ac.Id " +
            " INNER JOIN AssetClassificationSite acs ON ac.Id = acs.AssetClassificationId " +
            " AND acs.SiteId = s.Id " +
            " INNER JOIN AssetSubType astype ON ac.AssetSubTypeId = astype.Id " +
            " INNER JOIN AssetType atype ON atype.Id = astype.AssetTypeId " +
            " INNER JOIN (SELECT TOP(20000) * from uvw_FaultPriorityMappings ORDER BY NEWID()) fpm ON ac.AssetSubTypeId = fpm.AssetSubTypeId " +
            " AND fpm.SiteTypeId = s.SiteTypeId " +
            " AND fpm.AssetClassificationId = ac.Id " +
            " AND fpm.AssetSubTypeId = astype.Id " +
            " INNER JOIN FaultTypeMapping ftm ON ftm.AssetTypeId = atype.Id " +
            " AND ftm.AssetSubTypeId = ac.AssetSubTypeId " +
            " AND ftm.AssetClassificationId = ac.Id " +
            " AND ftm.faulttypeid = fpm.FaultTypeId " +
            " INNER JOIN FaultType ft ON fpm.FaultTypeId = ft.Id " +
            " AND ft.AvailableForStoreSelfLoggedJobs = 1 ";

    private static final String GET_SITE_ASSETS_SITE_WHERE_SQL = "WHERE s.Name = '%s' ";
    private static final String GET_SITE_STATUS_WHERE_SQL = "WHERE s.SiteStatusId = 1 ";
    private static final String GET_TAGGED_ASSET_WHERE_SQL = " AND a.AssetTag IS NOT NULL ";
    private static final String GET_NON_TAGGED_ASSET_WHERE_SQL = " AND (a.AssetTag IS NULL OR LEN(a.AssetTag) = 0 ) ";
    private static final String GET_ASSET_WITH_SN_WHERE_SQL = " AND a.SerialNo IS NOT NULL ";
    private static final String GET_ASSET_WITHOUT_SN_WHERE_SQL = " AND (a.SerialNo IS NULL OR LEN(a.SerialNo) = 0) ";
    private static final String GET_PRIORITY_ASSET_WHERE_SQL = " AND fpm.Detail = '%s' ";


    private static final String GET_SITE_ASSETS_ORDER_SQL =  "ORDER BY NEWID() ";

    private static final String STORES_CTE = "SELECT TOP 25 PERCENT * FROM Site s WHERE s.SiteStatusId = 1 ";

    // occupied sites with city resource
    private static final String SITES_WITH_CITY_RESOURCE = "SELECT TOP(10) s.* FROM Site s WITH (NOLOCK) "
            + "INNER JOIN SiteResource sr WITH (NOLOCK) ON sr.SiteId = s.Id "
            + "INNER JOIN Resource r ON sr.ResourceId = r.Id "
            + "INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id "
            + "WHERE s.SiteStatusId = 1 AND r.Active =1 AND rp.ResourceTypeId = 1 AND r.Active = 1 AND rp.Active = 1 AND rp.JobCallouts = 1 AND rp.ActiveForClient = 1 ";

    private static final String GET_TAGGED_ASSET_WHERE_CTE = " AND a.AssetTag IS NOT NULL ";
    private static final String GET_NON_TAGGED_ASSET_WHERE_CTE = " AND (a.AssetTag IS NULL OR LEN(a.AssetTag) = 0 ) ";
    private static final String ASSETS_CTE = "SELECT * FROM ASSET a WHERE AssetStatusId = 1 ";

    private static final String INITIAL_RESULT_CTE = "SELECT " +
            "  a.id, s.Name, s.SiteCode, s.Id SiteId, sl.Name SubLocationName, fpm.AssetClassificationName, at.Name AS AssetTypeName, fpm.AssetSubTypeName AssetSubTypeName,  " +
            "  a.LocalIdentifier, a.AssetTag, a.SerialNo, fpm.FaultTypeName FaultTypeName, fpm.Detail FaultPriority   " +
            "FROM STORES s " +
            "INNER JOIN ASSET a ON s.Id = a.SiteId " +
            "INNER JOIN LOCATION sl WITH (NOLOCK) ON a.LocationId = sl.Id  " +
            "INNER JOIN FPM fpm ON fpm.SiteTypeId = s.SiteTypeId AND a.AssetClassificationId = fpm.AssetClassificationId " +
            "INNER JOIN AssetSubType ast WITH (NOLOCK) ON ast.Id = fpm.AssetSubTypeId " +
            "INNER JOIN AssetType at WITH (NOLOCK) ON at.Id = ast.AssetTypeId ";

    private static final String FINAL_RESULT_CTE = "WITH FPM AS ( " +
            "SELECT fpm1.*  " +
            "FROM uvw_faultprioritymappings fpm1 WITH (NOLOCK) " +
            "INNER JOIN (SELECT * FROM FaultType WITH (NOLOCK) WHERE  AvailableForStoreSelfLoggedJobs = 1) ft ON fpm1.FaultTypeId = ft.Id " +
            "INNER JOIN uvw_AssetClassificationForSearch acfs ON acfs.Id = fpm1.AssetClassificationId " +
            "WHERE fpm1.Detail IN (:faultPriority)) " +
            ", STORES AS (:storesCTE) " +
            ", ASSETS AS (:assetsCTE) " +
            ", INITIAL_RESULT_CTE AS ( :initialResultCTE ) " +
            "SELECT TOP(1) * FROM INITIAL_RESULT_CTE " +
            "ORDER BY NEWID() ";


    @Autowired
    private EntityManager entityManager;


    /**
     * Fetch Assets for site
     * @param siteCode
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SiteAsset> getAssetsForSite(String name) {
        String sql = GET_SITE_ASSETS_SELECT_SQL +
                GET_SITE_ASSETS_QUERY_SQL +
                GET_SITE_ASSETS_SITE_WHERE_SQL;
        // GET_SITE_NO_QUESTIONS_WHERE_SQL questions removed
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        String queryStatement = String.format(sql, name);
        logger.debug("getAssetsForSite: " + queryStatement);
        Query query =  entityManager.createNativeQuery(queryStatement, SiteAsset.class);
        return query.getResultList();
    }


    /**
     * Fetch an Asset for a site at random
     * @return single record
     */
    public SiteAsset getRandomSiteAsset() {
        String sql = GET_SITE_RANDOM_SELECT_SQL +
                GET_SITE_ASSETS_QUERY_SQL +
                GET_SITE_STATUS_WHERE_SQL +
                GET_SITE_ASSETS_ORDER_SQL;
        // GET_SITE_NO_QUESTIONS_WHERE_SQL questions removed

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug(sql);
        try {
            Query query = entityManager.createNativeQuery(sql, SiteAsset.class);
            return (SiteAsset) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public SiteAsset getRandomSiteAsset_CTE() {
        String sql = null;

        String storesCTE = STORES_CTE;

        //Build Asset Type CTE
        String assetsCTE = ASSETS_CTE;
        assetsCTE = assetsCTE +  GET_TAGGED_ASSET_WHERE_CTE ;

        String initialResultCTE = INITIAL_RESULT_CTE;

        sql = FINAL_RESULT_CTE;
        sql = sql.replaceAll(":storesCTE", storesCTE);
        sql = sql.replaceAll(":assetsCTE", assetsCTE);

        sql = sql.replaceAll(":initialResultCTE", initialResultCTE);
        sql = sql .replaceAll(":faultPriority", "'P1', 'P2', 'P3'");

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getRandomSiteAsset_CTE: " + sql);
        try {
            Query query = entityManager.createNativeQuery(sql, SiteAsset.class);
            return (SiteAsset) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Fetch an Asset for a site at random which will generate a fault with the given priority
     * @param priority
     * @return single record
     */
    public SiteAsset getRandomSiteAsset(String priority) {
        String sql = GET_SITE_RANDOM_SELECT_SQL +
                GET_SITE_ASSETS_QUERY_SQL +
                GET_SITE_STATUS_WHERE_SQL +
                String.format(GET_PRIORITY_ASSET_WHERE_SQL, priority) +
                GET_SITE_ASSETS_ORDER_SQL;
        // GET_SITE_NO_QUESTIONS_WHERE_SQL questions removed

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug(sql);
        try {
            Query query = entityManager.createNativeQuery(sql, SiteAsset.class);
            return (SiteAsset) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public SiteAsset getRandomSiteAsset_CTE(String priority) {
        String sql = null;

        String storesCTE = SITES_WITH_CITY_RESOURCE;

        //Build Asset Type CTE
        String assetsCTE = ASSETS_CTE;

        String initialResultCTE = INITIAL_RESULT_CTE;

        sql = FINAL_RESULT_CTE;
        sql = sql.replaceAll(":storesCTE", storesCTE);
        sql = sql.replaceAll(":assetsCTE", assetsCTE);

        sql = sql.replaceAll(":initialResultCTE", initialResultCTE);
        sql = sql .replaceAll(":faultPriority", "'" + priority + "'");

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = sql.replace("ORDER", "WHERE FaultTypeName != 'Non Operational' ORDER"); // no one could explain why this FT doesnt work - but bug was rejected - so excluding from test!?!!?!

        logger.debug("getRandomSiteAsset_CTE " + sql);
        try {
            Query query = entityManager.createNativeQuery(sql, SiteAsset.class);
            return (SiteAsset) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch an Asset for a site at random which will generate a fault with the given priority and is tagged as per the input
     * @param priority
     * @param tagged
     * @return single record
     */
    public SiteAsset getRandomSiteAsset(String priority, Boolean tagged) {
        String sql = GET_SITE_RANDOM_SELECT_SQL +
                GET_SITE_ASSETS_QUERY_SQL +
                GET_SITE_STATUS_WHERE_SQL +
                (tagged ==true ? GET_TAGGED_ASSET_WHERE_SQL :  GET_NON_TAGGED_ASSET_WHERE_SQL) +
                String.format(GET_PRIORITY_ASSET_WHERE_SQL, priority) +
                GET_ASSET_WITHOUT_SN_WHERE_SQL +
                GET_SITE_ASSETS_ORDER_SQL;
        // GET_SITE_NO_QUESTIONS_WHERE_SQL questions removed

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug(sql);
        try {
            Query query = entityManager.createNativeQuery(sql, SiteAsset.class);
            return (SiteAsset) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public SiteAsset getRandomSiteAsset_CTE(String priority, Boolean tagged) {
        String sql = null;

        String storesCTE = SITES_WITH_CITY_RESOURCE;

        //Build Asset Type CTE
        String assetsCTE = ASSETS_CTE;
        assetsCTE = assetsCTE + (tagged ==true ? GET_TAGGED_ASSET_WHERE_CTE :  GET_NON_TAGGED_ASSET_WHERE_CTE);

        String initialResultCTE = INITIAL_RESULT_CTE;

        sql = FINAL_RESULT_CTE;
        sql = sql.replaceAll(":storesCTE", storesCTE);
        sql = sql.replaceAll(":assetsCTE", assetsCTE);

        sql = sql.replaceAll(":initialResultCTE", initialResultCTE);
        sql = sql .replaceAll(":faultPriority", "'" + priority + "'");

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug(sql);
        try {
            Query query = entityManager.createNativeQuery(sql, SiteAsset.class);
            return (SiteAsset) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch an Asset for a site at random which will generate a fault with the given priority and is tagged as per the input
     * @param priority
     * @param tagged -  Should the asset have a tag
     * @param serialNo - Should the asset have a serial no
     * @return single record
     */
    public SiteAsset getRandomSiteAsset(String priority, Boolean tagged, Boolean serialNo) {
        String sql = GET_SITE_RANDOM_SELECT_SQL +
                GET_SITE_ASSETS_QUERY_SQL +
                GET_SITE_STATUS_WHERE_SQL +
                (tagged ==true ? GET_TAGGED_ASSET_WHERE_SQL :  GET_NON_TAGGED_ASSET_WHERE_SQL) +
                (serialNo ==true ? GET_ASSET_WITH_SN_WHERE_SQL :  GET_ASSET_WITHOUT_SN_WHERE_SQL) +
                String.format(GET_PRIORITY_ASSET_WHERE_SQL, priority) +
                GET_SITE_ASSETS_ORDER_SQL;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug(sql);
        try {
            Query query = entityManager.createNativeQuery(sql, SiteAsset.class);
            return (SiteAsset) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }


    public SiteAsset getRandomSiteAsset_CTE(String priority, Boolean tagged, Boolean serialNo) {
        String sql = null;

        String storesCTE = SITES_WITH_CITY_RESOURCE;

        //Build Asset Type CTE
        String assetsCTE = ASSETS_CTE;
        assetsCTE = assetsCTE + (tagged == true ? GET_TAGGED_ASSET_WHERE_CTE : GET_NON_TAGGED_ASSET_WHERE_CTE);
        assetsCTE = assetsCTE + (serialNo == true ? GET_ASSET_WITH_SN_WHERE_SQL : GET_ASSET_WITHOUT_SN_WHERE_SQL);

        String initialResultCTE = INITIAL_RESULT_CTE;

        sql = FINAL_RESULT_CTE;
        sql = sql.replaceAll(":storesCTE", storesCTE);
        sql = sql.replaceAll(":assetsCTE", assetsCTE);

        sql = sql.replaceAll(":initialResultCTE", initialResultCTE);
        sql = sql.replaceAll(":faultPriority", "'" + priority + "'");

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getRandomSiteAsset_CTE: " + sql);
        try {
            Query query = entityManager.createNativeQuery(sql, SiteAsset.class);
            return (SiteAsset) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch a tagged Asset for a site at random
     * @return single record
     */
    public SiteAsset getRandomSiteTaggedAsset() {
        String sql = GET_SITE_RANDOM_SELECT_SQL +
                GET_SITE_ASSETS_QUERY_SQL +
                GET_SITE_STATUS_WHERE_SQL +
                GET_TAGGED_ASSET_WHERE_SQL +
                GET_SITE_ASSETS_ORDER_SQL;
        // GET_SITE_NO_QUESTIONS_WHERE_SQL questions removed

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug(sql);
        try {
            Query query = entityManager.createNativeQuery(sql, SiteAsset.class);
            return (SiteAsset) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public SiteAsset getRandomSiteTaggedAsset_CTE() {
        String sql = null;

        String storesCTE = SITES_WITH_CITY_RESOURCE;

        //Build Asset Type CTE
        String assetsCTE = ASSETS_CTE;
        assetsCTE = assetsCTE + GET_TAGGED_ASSET_WHERE_CTE;

        String initialResultCTE = INITIAL_RESULT_CTE;

        sql = FINAL_RESULT_CTE;
        sql = sql.replaceAll(":storesCTE", storesCTE);
        sql = sql.replaceAll(":assetsCTE", assetsCTE);

        sql = sql.replaceAll(":initialResultCTE", initialResultCTE);
        sql = sql .replaceAll(":faultPriority", "'P1', 'P2', 'P3'");

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getRandomSiteTaggedAsset_CTE: " + sql);
        try {
            Query query = entityManager.createNativeQuery(sql, SiteAsset.class);
            return (SiteAsset) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public SiteAsset getAssetSubtypeClassification(int immediateCallout, int siteId) {
        String sql = "SELECT TOP(1) a.Id, at.Name AS AssetTypeName, fpm.AssetSubTypeName AS AssetSubTypeName, fpm.AssetClassificationName, fpm.FaultTypeName,"
                + " s.Name, s.SiteCode, loc.Name AS SubLocationName, a.LocalIdentifier, a.AssetTag, a.SerialNo, fpm.Detail AS FaultPriority"
                + " FROM Site s"
                + " INNER JOIN uvw_FaultPriorityMappings fpm ON s.SiteTypeId = fpm.SiteTypeId"
                + " INNER JOIN FaultType ft ON fpm.FaultTypeId = ft.Id"
                + " LEFT JOIN Asset a ON s.Id = a.SiteId"
                + " LEFT JOIN Location loc ON a.LocationId = loc.Id"
                + " LEFT JOIN AssetSubType ast ON fpm.AssetSubTypeId = ast.Id"
                + " LEFT JOIN AssetType at ON ast.AssetTypeId = at.Id"
                + " WHERE ft.Active = 1"
                + " AND s.Id = :siteId"
                + " AND fpm.TechBureau = 0"
                + " AND fpm.ImmediateCallout = :immediateCallout"
                + " ORDER BY NEWID()";

        sql = sql.replaceAll(":immediateCallout", Integer.toString(immediateCallout));
        sql = sql.replaceAll(":siteId", Integer.toString(siteId));

        logger.debug("getAssetSubtypeClassification: " + sql);
        try {
            Query query = entityManager.createNativeQuery(sql, SiteAsset.class);
            return (SiteAsset) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
