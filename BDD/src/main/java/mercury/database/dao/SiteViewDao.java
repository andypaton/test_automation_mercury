package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cucumber.api.PendingException;
import mercury.database.models.SiteView;
import mercury.helpers.DbUtilityHelper;

@Repository
public class SiteViewDao {

    @Autowired
    private DbUtilityHelper dbUtilityHelper;

    private static final Logger logger = LogManager.getLogger();

    private static final String ALL_FIELDS
    = " s.Id, s.SiteCode, s.Name, s.BrandId, s.Address1, s.Address2, s.Address3, s.Town, s.County, s.Postcode, s.TelNo, s.SiteStatusId, s.SiteTypeId, COALESCE(s.IsVIPStore, 0) AS IsVIPStore, st.name AS SiteType, ss.Name AS SiteStatus, spn.PhoneNumber ";

    private static final String BASE_SQL = "SELECT " + ALL_FIELDS
            + " FROM Site s LEFT OUTER JOIN SiteStatuses ss ON s.SiteStatusId = ss.Id "
            + " LEFT OUTER JOIN SitePhoneNumber spn ON s.Id = spn.SiteId "
            + " LEFT OUTER JOIN SiteTypes st ON st.Id = s.SiteTypeId "
            + " LEFT JOIN AssetClassificationSite acs ON acs.SiteId = s.Id ";  // site must have assets

    private static final String SITE_WITH_TECNICIANS_SQL = "SELECT " + ALL_FIELDS
            + " FROM  Site s INNER JOIN SiteResource sr ON s.Id = sr.SiteId "
            + " LEFT OUTER JOIN SiteStatuses ss ON s.SiteStatusId = ss.Id "
            + " LEFT OUTER JOIN SitePhoneNumber spn ON s.Id = spn.SiteId "
            + " LEFT OUTER JOIN SiteTypes st ON st.Id = s.SiteTypeId "
            + " LEFT JOIN Resource r ON sr.ResourceId = r.Id "
            + " LEFT JOIN AllInclusiveContractorSiteMapping ascm ON ascm.SiteId = s.Id "
            + " LEFT JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id "
            + " WHERE ascm.Id IS NULL AND r.Active = 1 "
            + " AND rp.ResourceTypeId = 1 AND rp.JobCallouts = 1 AND rp.Active = 1 ";

    private static final String RANDOM_SITE_SQL
    = "SELECT TOP(1) id, siteCode, name, brandId, address1, address2, address3, town, county, postcode, telNo, siteStatusId, SiteTypeId, IsVIPStore, SiteType, SiteStatus, PhoneNumber "
            + " FROM (%s) randomOrder ORDER BY NEWID()";

    private static final String CONTRACT_STORE = " INNER JOIN AllInclusiveContractorSiteMapping ascm ON ascm.SiteId = s.Id ";
    private static final String NON_CONTRACT_STORE = " LEFT JOIN AllInclusiveContractorSiteMapping ascm ON ascm.SiteId = s.Id WHERE ascm.Id IS NULL ";
    private static final String STATE_IS = " ss.name = '%s' ";
    private static final String FOR_SITE_WITH_CALLER = " AND s.Id IN (SELECT SiteId FROM ClientCaller) ";
    private static final String WHERE = " WHERE ";
    private static final String AND = " AND ";

    private static final String CITY_TECH_STORE = "SELECT " + ALL_FIELDS
            + " FROM Site s "
            + " LEFT JOIN AllInclusiveContractorSiteMapping ascm ON ascm.SiteId = s.Id "
            + " INNER JOIN SiteResource sr ON sr.SiteId = s.Id "
            + " INNER JOIN Resource r ON r.Id = sr.ResourceId "
            + " INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id "
            + " LEFT OUTER JOIN SiteStatuses ss ON s.SiteStatusId = ss.Id "
            + " LEFT OUTER JOIN SitePhoneNumber spn ON s.id = spn.siteId "
            + " LEFT OUTER JOIN SiteTypes st ON st.id = s.siteTypeId "
            + " WHERE ascm.Id IS NULL "
            + " AND rp.ResourceTypeId = 1 AND rp.JobCallouts = 1 AND r.Active = 1 "
            + " AND s.Active = 1 ";
    //to uncomment when advocate data improves
    //            + " AND s.StoreOpenDate < GETDATE() - 356";

    private static final String SITE_WITH_INCIDENTS_LOGGED_IN_LAST_30_DAYS = "SELECT TOP(1) " + ALL_FIELDS
            + " FROM Site s LEFT OUTER JOIN SiteStatuses ss ON s.SiteStatusId = ss.Id "
            + " LEFT OUTER JOIN SitePhoneNumber spn ON s.id = spn.siteId "
            + " LEFT OUTER JOIN SiteTypes st ON st.id = s.siteTypeId "
            + " LEFT OUTER JOIN Incident i ON s.Id = i.SiteId "
            + " WHERE i.IncidentStatusId = 1"
            + " AND i.IncidentDate BETWEEN DATEADD(DAY, -30, GETUTCDATE()) AND GETUTCDATE() ";

    private static final String CITY_TECH_STORE_WITH_CITY_TECH_CALLER = "SELECT " + ALL_FIELDS
            + " FROM Site s "
            + " LEFT JOIN AllInclusiveContractorSiteMapping ascm ON ascm.SiteId = s.Id "
            + " INNER JOIN SiteResource sr ON sr.SiteId = s.Id "
            + " INNER JOIN Resource r ON r.Id = sr.ResourceId "
            + " INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id "
            + " INNER JOIN ResourceCaller rc ON rc.ResourceId = r.Id "
            + " INNER JOIN Caller c ON c.Id = rc.Id "
            + " INNER JOIN CallerType ct ON c.CallerTypeId = ct.Id "
            + " INNER JOIN SiteStatuses ss ON s.SiteStatusId = ss.Id "
            + " LEFT OUTER JOIN SitePhoneNumber spn ON s.id = spn.siteId "
            + " LEFT OUTER JOIN SiteTypes st ON st.id = s.siteTypeId "
            + " WHERE ascm.Id IS NULL "
            + " AND rp.ResourceTypeId = 1 "
            + " AND rp.JobCallouts = 1 "
            + " AND r.Active = 1 "
            + " AND s.Active = 1 "
            + " AND c.CallerTypeId = 2 "
            + " AND ss.Name = 'occupied'";
    //to uncomment when advocate data improves
    //            + " AND s.StoreOpenDate < DATEADD(YEAR, -1, GETDATE()) ";

    @Autowired
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;


    public SiteView update() {
        return null;
    }

    /**
     * Fetch Site entity by primary key
     *
     * @param id
     * @return
     */
    public SiteView get(Integer id) {
        String sql = BASE_SQL + " where s.id = "
                + id;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug(sql);
        return (SiteView) entityManager.createNativeQuery(sql, SiteView.class).getSingleResult();
    }

    public SiteView getSiteByState(String state) {
        String sql = BASE_SQL + WHERE + String.format(STATE_IS, state);
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = String.format(RANDOM_SITE_SQL, sql);
        logger.debug("getSiteByState: " + sql);
        try {
            return (SiteView) entityManager.createNativeQuery(sql, SiteView.class).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public SiteView getSiteWithLinkedIncidentCriterion() {
        String sql = "SELECT TOP(1) " + ALL_FIELDS
                + " FROM Site s LEFT JOIN SiteStatuses ss ON s.SiteStatusId = ss.Id "
                + " LEFT JOIN SitePhoneNumber spn ON s.Id = spn.SiteId "
                + " LEFT JOIN SiteTypes st ON st.Id = s.siteTypeId "
                + " INNER JOIN uvw_FaultPriorityMappings fpm ON fpm.SiteTypeId = s.SiteTypeId "
                + " INNER JOIN LinkedIncidentCriterion lic ON lic.FaultTypeId = fpm.FaultTypeId "
                + " INNER JOIN incident i ON i.SiteId = s.Id "
                + " WHERE ss.Name = 'Occupied' "
                + " AND (lic.BrandId = s.BrandId OR lic.BrandId IS NULL) "
                + " AND (lic.SiteTypeId = s.SiteTypeId OR lic.SiteTypeId IS NULL) "
                + " AND lic.Active = 1"
                + " ORDER BY NEWID()";

        logger.debug("getSiteWithLinkedIncidentCriterion: " + sql);
        try {
            return (SiteView) entityManager.createNativeQuery(sql, SiteView.class).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public SiteView getRandomSite() {
        String sql = String.format(RANDOM_SITE_SQL, BASE_SQL);
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getRandomSite: " + sql);
        return (SiteView) entityManager.createNativeQuery(sql, SiteView.class).getSingleResult();
    }

    public SiteView getRandomSiteWithCaller(String state) {
        String sql = BASE_SQL + WHERE
                + String.format(STATE_IS, state)
                + FOR_SITE_WITH_CALLER;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = String.format(RANDOM_SITE_SQL, sql);
        logger.debug("getRandomSiteWithCaller: " + sql);
        return (SiteView) entityManager.createNativeQuery(sql, SiteView.class).getSingleResult();
    }

    public SiteView getRandomContractStoreWithCaller(String state) {
        String sql = BASE_SQL + CONTRACT_STORE
                + WHERE
                + String.format(STATE_IS, state)
                + FOR_SITE_WITH_CALLER;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = String.format(RANDOM_SITE_SQL, sql);
        logger.debug("getRandomSiteWithCaller: " + sql);
        return (SiteView) entityManager.createNativeQuery(sql, SiteView.class).getSingleResult();
    }

    public SiteView getRandomCityTechStoreWithCaller(String state) {
        String sql = CITY_TECH_STORE + AND
                + String.format(STATE_IS, state)
                + FOR_SITE_WITH_CALLER;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = String.format(RANDOM_SITE_SQL, sql);
        logger.debug("getRandomCityTechStoreWithCaller: " + sql);
        try {
            return (SiteView) entityManager.createNativeQuery(sql, SiteView.class).getSingleResult();
        } catch (Exception e) {
            throw new PendingException("No suitable test data found");
        }
    }

    public SiteView getRandomCityTechStoreWithCityTechCaller(String state) {
        String sql = CITY_TECH_STORE_WITH_CITY_TECH_CALLER;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = String.format(RANDOM_SITE_SQL, sql);
        logger.debug("getRandomCityTechStoreWithCityTechCaller: " + sql);
        try {
            return (SiteView) entityManager.createNativeQuery(sql, SiteView.class).getSingleResult();
        } catch (Exception e) {
            throw new PendingException("No suitable test data found");
        }
    }

    @SuppressWarnings("unchecked")
    public List<SiteView> getContractStores() {
        String sql = BASE_SQL + CONTRACT_STORE;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getContractStores: " + sql);
        try {
            return entityManager.createNativeQuery(sql, SiteView.class).getResultList();
        } catch (Exception e) {
            throw new PendingException("No suitable test data found");
        }
    }

    @SuppressWarnings("unchecked")
    public List<SiteView> getNonContractStores() {
        String sql = BASE_SQL + NON_CONTRACT_STORE;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getNonContractStores: " + sql);
        try {
            return entityManager.createNativeQuery(sql, SiteView.class).getResultList();
        } catch (Exception e) {
            throw new PendingException("No suitable test data found");
        }
    }

    public SiteView getSiteByName(String name) {
        String sql = BASE_SQL
                + " WHERE s.name = '" + name + "'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getSiteByName: " + sql);
        try {
            return (SiteView) entityManager.createNativeQuery(sql, SiteView.class).getSingleResult();
        } catch (Exception e) {
            throw new PendingException("No suitable test data found");
        }
    }

    public SiteView getRandomSiteWithCityTech() {
        String sql = String.format(RANDOM_SITE_SQL, SITE_WITH_TECNICIANS_SQL);
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getRandomSite: " + sql);
        try {
            return (SiteView) entityManager.createNativeQuery(sql, SiteView.class).getSingleResult();
        } catch (Exception e) {
            throw new PendingException("No suitable test data found");
        }
    }

    public SiteView getSiteWithLoggedIncidentsInLast30Days(String state) {
        String sql = SITE_WITH_INCIDENTS_LOGGED_IN_LAST_30_DAYS + AND
                + String.format(STATE_IS, state)
                + "ORDER BY NEWID()";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getSiteWithIncidentsLoggedInLast30Days: " + sql);
        try {
            return (SiteView) entityManager.createNativeQuery(sql, SiteView.class).getSingleResult();
        } catch (Exception e) {
            throw new PendingException("No suitable test data found");
        }
    }
}
