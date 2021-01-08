package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.SiteContractorAsset;
import mercury.databuilders.TestData;
import mercury.helpers.DbUtilityHelper;
import mercury.helpers.FileHelper;

import static mercury.runtime.ThreadManager.getWebDriver;

@Repository
public class SiteContractorAssetDao {

    @Autowired private DbUtilityHelper dbUtilityHelper;
    @Autowired private TestData testData;

    private static final Logger logger = LogManager.getLogger();

    private static final String ALL_FIELDS = " SiteId, ResourceId, ResourceName, ResourceProfileName, AssetClassificationSiteId, AssetClassificationId, AssetClassificationName, AssetSubTypeName, Priority, ResponsePriorityId, FaultTypeId, FaultTypeName, StartAt, EndAt, StartDayOfTheWeek, EndDayOfTheWeek ";

    private static final String GET_RANDOM_SQL = "SELECT TOP(1) " + ALL_FIELDS + " FROM (%s) randomOrder ORDER BY NEWID()";


    private static final String RESOURCES_AVAILABLE_NOW = " ( " +
            " SELECT r.Id AS ResourceId, r.Name AS ResourceName, rp.name AS ResourceProfileName, null AS StartAt, null AS EndAt, null AS StartDayOfTheWeek, null AS EndDayOfTheWeek " +
            " FROM Resource r  " +
            " INNER JOIN ResourceProfile rp ON rp.Id = r.ResourceProfileId AND rp.ActiveForClient = 1  " +
            " INNER JOIN ResourceWorkingHours rwh ON r.Id = rwh.ResourceId " +
            " WHERE r.Active=1 " +
            " AND (rwh.startdayoftheweek < Datepart(dw, Getdate()) OR (rwh.startdayoftheweek = Datepart(dw, Getdate()) AND rwh.StartAt <= CONVERT(TIME, SWITCHOFFSET(GETDATE(), ':timezone')))) " +
            " AND (rwh.EndDayoftheweek > Datepart(dw, Getdate()) OR (rwh.enddayoftheweek = Datepart(dw, Getdate()) AND  rwh.EndAt >= CONVERT(TIME, SWITCHOFFSET(GETDATE(), ':timezone')))) " +
            " ) ";

    private static final String RESOURCES_AVAILABLE_TODAY = " ( " +
            " SELECT r.Id AS ResourceId, r.Name AS ResourceName, rp.name AS ResourceProfileName, rwh.StartAt, rwh.EndAt, rwh.StartDayOfTheWeek, rwh.EndDayOfTheWeek " +
            " FROM Resource r  " +
            " INNER JOIN ResourceProfile rp ON rp.Id = r.ResourceProfileId AND rp.ActiveForClient = 1  " +
            " INNER JOIN ResourceWorkingHours rwh ON r.Id = rwh.ResourceId AND startdayoftheweek = DATEPART(dw, GETDATE()) AND enddayoftheweek = DATEPART(dw, GETDATE()) " +
            " WHERE r.Active=1 " +
            " ) ";

    private static final String CONTRACT_STORE = " ( " +
            " SELECT s.Id, s.SiteTypeId " +
            " FROM Site s " +
            " INNER JOIN AllInclusiveContractorSiteMapping ascm ON ascm.SiteId = s.Id " +
            " WHERE s.Active = 1 and s.SiteStatusId IN (1,2) " +
            " ) ";

    private static final String CITY_TECH_STORE = " ( " +
            " SELECT DISTINCT s.Id, s.SiteTypeId " +
            " FROM Site s " +
            " LEFT JOIN AllInclusiveContractorSiteMapping ascm ON ascm.SiteId = s.Id " +
            " INNER JOIN SiteResource sr ON sr.SiteId = s.Id " +
            " INNER JOIN Resource r ON r.Id = sr.ResourceId " +
            " INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id " +
            " WHERE ascm.Id IS NULL " +
            " AND rp.ResourceTypeId = 1 AND rp.JobCallouts = 1 AND r.Active = 1 " +
            " AND s.Active = 1 and s.SiteStatusId IN (1,2) " +
            " ) ";

    private static final String VENDOR_STORE = " ( " +
            " SELECT s.Id, s.SiteTypeId " +
            " FROM Site s " +
            " LEFT JOIN AllInclusiveContractorSiteMapping ascm ON ascm.SiteId = s.Id " +
            " WHERE ascm.Id IS NULL " +
            " AND s.SiteTypeId != 22 " +
            " AND s.Id NOT IN (SELECT sr.SiteId FROM SiteResource sr INNER JOIN Resource r ON sr.ResourceId = r.Id WHERE r.ResourceProfileId IN (3, 31, 134, 148)) " +
            " AND s.Active = 1 AND s.SiteStatusId IN (1,2) AND (s.StoreOpenDate IS NULL OR s.StoreOpenDate < DATEADD(year, -1, GetDate())) " +
            " AND s.Id != 117 " +  // Avoiding bad data - This site has no callers in Advocate
            " ) ";

    // return top X rows to help with performance
    private static final String ALL_STORES_SQL = "SELECT TOP(10000) acs.SiteId, sr.ResourceId, r.Name AS ResourceName, acs.id AS AssetClassificationSiteId, acs.AssetClassificationId " +
            ", rwh.StartAt, rwh.EndAt, rwh.StartDayOfTheWeek, rwh.EndDayOfTheWeek " +
            ", fm.AssetClassificationName, fpm.AssetSubTypeName, fpm.Priority, fpm.FaultPriorityId, fpm.FaultTypeId, fpm.FaultTypeName " +
            "FROM AssetClassificationSite acs " +
            " INNER JOIN (SELECT s.Id, s.SiteTypeId FROM Site s " +
            " WHERE s.Active = 1 AND s.SiteStatusId IN (1,2) " +
            " ) s ON s.Id=acs.SiteId " +
            " INNER JOIN SiteResource sr ON sr.SiteId = s.Id " +
            " INNER JOIN (SELECT Id, Name FROM Resource WHERE active=1) r ON r.Id = sr.ResourceId " +
            " INNER JOIN (SELECT ResourceId, StartAt, EndAt, StartDayOfTheWeek, EndDayOfTheWeek FROM uvw_ResourceWorkingHours) rwh ON rwh.ResourceID = sr.ResourceId AND rwh.StartDayOfTheWeek = DATEPART(dw,GETDATE()) AND rwh.EndDayOfTheWeek = DATEPART(dw,GETDATE()) " +
            " INNER JOIN uvw_FaultPriorityMappings fpm ON fpm.SiteTypeId = s.SiteTypeId AND fpm.AssetClassificationId = acs.AssetClassificationId ";

    private static final String CITY_TECH_SQL = "SELECT TOP(10000) acs.SiteId, sr.ResourceId, r.ResourceName, r.ResourceProfileName, acs.id AS AssetClassificationSiteId, acs.AssetClassificationId " +
            ", r.StartAt, r.EndAt, r.StartDayOfTheWeek, r.EndDayOfTheWeek " +
            ", fpm.AssetClassificationName, fpm.AssetSubTypeName, fpm.Priority, fpm.FaultPriorityId, fpm.FaultTypeId, fpm.FaultTypeName " +
            " FROM AssetClassificationSite acs " +
            " INNER JOIN :store s ON s.Id = acs.SiteId " +
            " INNER JOIN SiteResource sr ON sr.SiteId = s.Id " +
            " INNER JOIN :resources r ON r.ResourceId = sr.ResourceId " +
            " LEFT OUTER JOIN AssetClassificationSiteContractorMapping acscm ON acscm.AssetClassificationSiteId = acs.Id  " +
            " INNER JOIN uvw_FaultPriorityMappings fpm ON fpm.SiteTypeId = s.SiteTypeId " +
            " AND fpm.AssetClassificationId = acs.AssetClassificationId " +
            " INNER JOIN uvw_FaultPriorityMappings fpm2 ON fpm.AssetClassificationId = fpm2.AssetClassificationId " +
            " AND fpm2.SiteTypeId = s.SiteTypeId " +
            " WHERE fpm.priority = :priority " +
            " AND fpm2.FaultPriorityId = 1 ";

    private static final String BASE_SQL = "SELECT TOP(1000) acs.siteid, acs.id AS AssetClassificationSiteId, acs.assetclassificationid, s.sitetypeid, fpm.AssetClassificationName, fpm.assetsubtypename, fpm.priority, fpm.FaultPriorityId, fpm.FaultTypeId, fpm.faulttypename, acscm.resourceid AS ResourceId, r.ResourceName, r.resourceprofilename, r.startat, r.endat, r.startdayoftheweek,  r.enddayoftheweek " +
            " FROM assetclassificationsite acs " +
            " INNER JOIN :store s ON s.id = acs.siteid " +
            " INNER JOIN uvw_faultprioritymappings fpm ON fpm.sitetypeid = s.sitetypeid AND fpm.assetclassificationid = acs.assetclassificationid " +
            " LEFT OUTER JOIN assetclassificationsitecontractormapping acscm ON acscm.assetclassificationsiteid = acs.id AND acscm.priority = :priority " +
            " INNER JOIN :resources r ON r.ResourceId = acscm.resourceid " +
            " WHERE fpm.priority = :priority ";

    // left outer join resources for vendor stores
    private static final String VENDOR_SQL = "SELECT TOP(1000) acs.siteid, acs.id AS AssetClassificationSiteId, acs.assetclassificationid, s.sitetypeid, fpm.AssetClassificationName, fpm.assetsubtypename, fpm.priority, fpm.ResponsePriorityId, fpm.FaultTypeId, fpm.faulttypename, acscm.resourceid AS ResourceId, r.ResourceName, r.resourceprofilename, r.startat, r.endat, r.startdayoftheweek, r.enddayoftheweek " +
            " FROM assetclassificationsite acs " +
            " INNER JOIN " + VENDOR_STORE + " s ON s.id = acs.siteid " +
            " INNER JOIN uvw_faultprioritymappings fpm ON fpm.sitetypeid = s.sitetypeid AND fpm.assetclassificationid = acs.assetclassificationid " +
            " LEFT OUTER JOIN assetclassificationsitecontractormapping acscm ON acscm.assetclassificationsiteid = acs.id " +
            " LEFT OUTER JOIN " + RESOURCES_AVAILABLE_TODAY + " r ON r.ResourceId = acscm.resourceid " +
            " WHERE fpm.priority = :priority AND fpm.RequiresReview = 0 ";

    private static final String VENDOR_WITH_MULTIPLE_CONTRACTORS_SQL = "SELECT TOP(1000) siteid, AssetClassificationSiteId, assetclassificationid, sitetypeid, assetClassificationName, assetsubtypename, priority, FaultPriorityId, FaultTypeId, faulttypename, ResourceId, ResourceName, resourceprofilename, startat, endat, startdayoftheweek, enddayoftheweek " +
            "FROM (" +
            "SELECT DENSE_RANK() OVER ( PARTITION BY acs.SiteId ORDER BY r.ResourceName) AS rnk, " +
            " acs.siteid, acs.id AS AssetClassificationSiteId, acs.assetclassificationid, s.sitetypeid, fpm.AssetClassificationName, fpm.assetsubtypename, fpm.priority, fpm.FaultPriorityId, fpm.FaultTypeId, fpm.faulttypename, acscm.resourceid AS ResourceId, r.ResourceName, r.resourceprofilename, r.startat, r.endat, r.startdayoftheweek, r.enddayoftheweek " +
            " FROM   assetclassificationsite acs " +
            " INNER JOIN " + VENDOR_STORE + " s ON s.id = acs.siteid " +
            " INNER JOIN uvw_faultprioritymappings fpm ON fpm.sitetypeid = s.sitetypeid AND fpm.assetclassificationid = acs.assetclassificationid " +
            " LEFT OUTER JOIN assetclassificationsitecontractormapping acscm ON acscm.assetclassificationsiteid = acs.id " +
            " LEFT OUTER JOIN :resources r ON r.ResourceId = acscm.resourceid " +
            " WHERE  fpm.priority = :priority " +
            " AND resourceProfileName='Contractor' " +
            ") ranked WHERE rnk > 1";

    private static final String FOR_SITE_ID = " SiteId =  %d ";
    private static final String NOT_ABSENT = " r.ResourceId NOT IN (SELECT ResourceId FROM uvw_ResourceAbsence WHERE CAST(AbsenceStartAt AS date) <= CAST(GETDATE() AS date) and CAST(GETDATE() AS date) <= CAST(AbsenceEndAt AS date)) ";
    private static final String PHONE_CONFIGURED = " r.ResourceId IN (SELECT ResourceId FROM ResourcePhoneNumber) ";
    private static final String CONTACTOR_CONFIGURED = " acscm.ResourceId IS NOT NULL ";
    private static final String CONTACTOR_NOT_CONFIGURED = " acscm.ResourceId IS NULL ";
    private static final String RESOURCE_WORKING_HOURS_TODAY = " r.StartAt IS NOT NULL ";

    private static final String ASSET_SUBTYPE_ID = " AND fpm.AssetSubTypeId NOT IN (SELECT AssetSubTypeId FROM LinkedIncidentCriterion) ";

    private static final String ROTA = " (SELECT DISTINCT oss.SiteId FROM RotaEntry re INNER JOIN OrganisationStructureSite oss ON oss.id=re.OrganisationStructureSiteId LEFT JOIN RotaEntryType ret ON ret.Id=re.RotaEntryTypeId LEFT JOIN ResourceProfile rp ON rp.Id=re.ResourceProfileId WHERE re.StartAt < GETDATE() AND GETDATE() < coalesce(re.EndAt, '2999-12-01') AND ret.name IN (:rotaEntryTypes) AND rp.name IN ('RHVAC Technician', 'RHVAC Supervisor')) ";
    private static final String WITH_ROTA_ENTRY = " s.Id IN " + ROTA;
    private static final String NO_ROTA_ENTRY = " s.Id NOT IN " + ROTA;

    private static final String PERMANENT = "Permanent";
    private static final String ON_CALL = "On Call Scheduler";
    private static final String PERMANENT_OR_ON_CALL = "'Permanent', 'On Call Scheduler'";

    private static final String WHERE = " WHERE ";
    private static final String AND = " AND ";

    private static final String BRAND_IN_HOURS = " (SELECT s.id FROM BrandWorkingHours bwh "
            + " LEFT JOIN brandWorkingHoursGroup bwhg ON bwhg.BrandWorkingHoursId = bwh.Id "
            + " INNER JOIN Site s ON s.BrandId = bwhg.BrandId "
            + " WHERE bwh.StartAt <= CONVERT(TIME, SWITCHOFFSET(GETDATE(), ':timezone')) "
            + " AND bwh.EndAt >= CONVERT(TIME, SWITCHOFFSET(GETDATE(), ':timezone')) "
            + " AND bwh.DayOfTheWeek = DATEPART(dw,GETDATE())) ";
    private static final String WITH_BRAND_IN_HOURS = " s.Id IN " + BRAND_IN_HOURS;
    private static final String WITH_BRAND_OUT_OF_HOURS = " s.Id NOT IN " + BRAND_IN_HOURS;

    private static final String DEFERRAL_IN_HOURS = " (SELECT s.id FROM DeferralWorkingHours dwh "
            + " LEFT JOIN DeferralWorkingHoursGroup dwhg ON dwhg.DeferralWorkingHoursId = dwh.Id "
            + " INNER JOIN Site s ON s.BrandId = dwhg.BrandId "
            + " WHERE dwh.StartAt <= CONVERT(TIME, SWITCHOFFSET(GETDATE(), ':timezone')) "
            + " AND dwh.EndAt >= CONVERT(TIME, SWITCHOFFSET(GETDATE(), ':timezone')) "
            + " AND dwh.DayOfTheWeek = DATEPART(dw,GETDATE())) ";
    private static final String WITH_DEFERRAL_IN_HOURS = " s.Id IN " + DEFERRAL_IN_HOURS;
    private static final String WITH_DEFERRAL_OUT_OF_HOURS = " s.Id NOT IN " + DEFERRAL_IN_HOURS;

    private static final String RESOURCE_IN_HOURS = " (SELECT ResourceId FROM uvw_ResourceWorkingHours rwh "
            + " WHERE ((rwh.StartAt <= CONVERT(TIME, SWITCHOFFSET(GETDATE(), ':timezone')) AND rwh.StartDayOfTheWeek = DATEPART(dw,GETDATE())) OR rwh.StartDayOfTheWeek < DATEPART(dw,GETDATE())) "
            + " AND ((rwh.EndAt >= CONVERT(TIME, SWITCHOFFSET(GETDATE(), ':timezone')) AND rwh.EndDayOfTheWeek = DATEPART(dw,GETDATE())) OR rwh.EndDayOfTheWeek > DATEPART(dw,GETDATE()))) ";
    private static final String WITH_RESOURCE_IN_HOURS = " r.ResourceId IN " + RESOURCE_IN_HOURS;
    private static final String WITH_RESOURCE_OUT_OF_HOURS = " r.ResourceId NOT IN " + RESOURCE_IN_HOURS;

    private static final String CITY_TECH_AND_CONTRACTOR_CAPABLE = " fpm.FaultTypeId IN (SELECT DISTINCT contractor.FaultTypeId "
            + " FROM (SELECT * FROM ResourceProfileCapability WHERE resourceProfileId = 115) contractor "
            + " INNER JOIN (SELECT * FROM ResourceProfileCapability WHERE resourceProfileId IN (3,31)) cityTech "
            + " ON contractor.SiteTypeId = cityTech.SiteTypeId "
            + " AND contractor.FaultTypeId = cityTech.FaultTypeId "
            + " AND contractor.assetClassificationId = cityTech.assetClassificationId)";

    @Autowired
    private EntityManager entityManager;


    public SiteContractorAsset getRandom() {
        String jpql = ALL_STORES_SQL;
        String randomRow = String.format(GET_RANDOM_SQL, jpql);

        logger.debug("getRandom: " + randomRow);

        Query query =  entityManager.createNativeQuery(randomRow, SiteContractorAsset.class);
        return (SiteContractorAsset) query.getSingleResult();
    }

    public SiteContractorAsset getRandomForContractStore(Integer priority) {
        String p = String.valueOf(priority);
        String sql = BASE_SQL.replace(":store", CONTRACT_STORE).replace(":resources", RESOURCES_AVAILABLE_TODAY).replace(":priority", p)
                + AND + CONTACTOR_CONFIGURED
                + AND + RESOURCE_WORKING_HOURS_TODAY;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        String randomRow = String.format(GET_RANDOM_SQL, sql);

        logger.debug("getRandomForContractStore: " + randomRow);

        try {
            Query query =  entityManager.createNativeQuery(randomRow, SiteContractorAsset.class);
            return (SiteContractorAsset) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private String getRota(List<String> rota) throws Exception {
        if (rota == null || rota.isEmpty()) {

            return NO_ROTA_ENTRY.replace(":rotaEntryTypes", PERMANENT_OR_ON_CALL);

        } else if (rota.contains(PERMANENT) && rota.contains(ON_CALL)) {

            return WITH_ROTA_ENTRY.replace(":rotaEntryTypes", PERMANENT_OR_ON_CALL);

        } else if (rota.contains(PERMANENT)) {

            return WITH_ROTA_ENTRY.replace(":rotaEntryTypes", "'" + PERMANENT + "'") + AND + NO_ROTA_ENTRY.replace(":rotaEntryTypes", "'" + ON_CALL + "'");

        } else if (rota.contains(ON_CALL)) {

            return WITH_ROTA_ENTRY.replace(":rotaEntryTypes", "'" + ON_CALL + "'");

        } else {
            throw new Exception("unexpected rota type requested");
        }
    }

    private String setContractorConfigured(boolean contractorConfigured) throws Exception {
        if (contractorConfigured) {
            return CONTACTOR_CONFIGURED;
        } else {
            return CONTACTOR_NOT_CONFIGURED;
        }
    }

    public SiteContractorAsset getRandomForCityTechStore(Integer priority) throws Exception {
        logger.debug(String.format("getRandomForCityTechStore [%d]", priority));

        String p = String.valueOf(priority);
        String sql = CITY_TECH_SQL.replace(":store", CITY_TECH_STORE).replace(":resources", RESOURCES_AVAILABLE_TODAY).replace(":priority", p);
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        String randomRow = String.format(GET_RANDOM_SQL, sql);

        logger.debug("getRandomForCityTechStore: " + randomRow);

        try {
            Query query =  entityManager.createNativeQuery(randomRow, SiteContractorAsset.class);
            return (SiteContractorAsset) query.getSingleResult();
        } catch (Exception e) {
            logger.debug("getRandomForCityTechStore priority " + priority + " : " + e.getMessage());
            return null;
        }
    }

    public SiteContractorAsset getForCityTechStoreAndDeferrable() throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_DATA_FOR_DEFERRABLE_JOB.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        testData.put("sql", sql);
        logger.debug("getForCityTechStoreAndDeferrable: " + sql);

        try {
            Query query =  entityManager.createNativeQuery(sql, SiteContractorAsset.class);
            SiteContractorAsset siteContractorAsset = (SiteContractorAsset) query.getSingleResult();
            logger.debug("result: " + siteContractorAsset.toString());
            return siteContractorAsset;
        } catch (Throwable e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    public SiteContractorAsset getRandomForCityTechStore(Integer priority, int brandInHours, int resourceInHours, List<String> rota, String timezone, boolean contractorConfigured, boolean storeWalkround, String capability) throws Exception {

        logger.debug(String.format("getRandomForCityTechStore [%d, %d, %d, %s, %s, %s, %s]", priority, brandInHours, resourceInHours, rota.toString(), timezone, String.valueOf(contractorConfigured), String.valueOf(storeWalkround)));

        String p = String.valueOf(priority);
        String jpql = CITY_TECH_SQL.replace(":store", CITY_TECH_STORE).replace(":resources", RESOURCES_AVAILABLE_TODAY).replace(":priority", p)
                + AND + NOT_ABSENT
                + AND + PHONE_CONFIGURED;

        jpql = jpql + AND + getRota(rota);
        jpql = jpql + AND + setContractorConfigured(contractorConfigured);

        if (!Boolean.valueOf(System.getProperty("updateWorkingHours"))) {
            if (brandInHours == 1 && resourceInHours == 1) {
                jpql = jpql + AND + WITH_BRAND_IN_HOURS.replace(":timezone", timezone);
                jpql = jpql + AND + WITH_RESOURCE_IN_HOURS.replace(":timezone", timezone);
            } else if (brandInHours == 1 && resourceInHours == -1) {
                jpql = jpql + AND + WITH_BRAND_IN_HOURS.replace(":timezone", timezone);
                jpql = jpql + AND + WITH_RESOURCE_OUT_OF_HOURS.replace(":timezone", timezone);
            } else if (brandInHours == -1 && resourceInHours == 0) {
                jpql = jpql + AND + WITH_BRAND_OUT_OF_HOURS.replace(":timezone", timezone);
            } else if (brandInHours == 0 && resourceInHours == 1) {
                jpql = jpql + AND + WITH_RESOURCE_IN_HOURS.replace(":timezone", timezone);
            } else if (brandInHours == 0 && resourceInHours == -1) {
                jpql = jpql + AND + WITH_RESOURCE_OUT_OF_HOURS.replace(":timezone", timezone);
            } else {
                throw new Exception("unexpected combination of brandInHours " +brandInHours+ " and resourceInHours " + resourceInHours);
            }
        } else {
            jpql = jpql + AND + RESOURCE_WORKING_HOURS_TODAY;
        }

        jpql = storeWalkround ? jpql : jpql + AND + "fpm.FaultTypeName != 'Store Walkround'";

        if (capability == null || (capability.contains("CityTech") && capability.contains("Contractor"))) {
            jpql = jpql + AND + CITY_TECH_AND_CONTRACTOR_CAPABLE;
        }

        jpql = dbUtilityHelper.dbNameSchemaReplacement(jpql);
        String randomRow = String.format(GET_RANDOM_SQL, jpql);

        logger.debug(randomRow);

        try {
            Query query =  entityManager.createNativeQuery(randomRow, SiteContractorAsset.class);
            return (SiteContractorAsset) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public SiteContractorAsset getRandomForVendorStore(Integer priority) throws Exception {
        logger.debug(String.format("getRandomForVendorStore [%d]", priority));

        String p = String.valueOf(priority);
        String jpql = VENDOR_SQL.replace(":priority", p);
        jpql = getWebDriver().getCurrentUrl().contains("ukrb") ? ASSET_SUBTYPE_ID : jpql;
        jpql = dbUtilityHelper.dbNameSchemaReplacement(jpql);
        String randomRow = String.format(GET_RANDOM_SQL, jpql);

        logger.debug("getRandomForVendorStore: " + randomRow);

        try {
            Query query =  entityManager.createNativeQuery(randomRow, SiteContractorAsset.class);
            return (SiteContractorAsset) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public SiteContractorAsset getRandomForVendorStore(Integer priority, int brandInHours, boolean contractorConfigured, List<String> rota, String timezone, boolean storeWalkround, int deferralInHours) throws Exception {

        if (rota != null) {
            logger.debug("getRandomForVendorStore [" +  priority + "," + brandInHours + "," + contractorConfigured + "," + rota.toString() + "," +  timezone + "," + storeWalkround + "," + deferralInHours + "]");
        } else {
            logger.debug("getRandomForVendorStore [" +  priority + "," + brandInHours + "," + contractorConfigured + ",null ," +  timezone + "," + storeWalkround + "," + deferralInHours + "]");
        }

        String p = String.valueOf(priority);
        String jpql = VENDOR_SQL.replace(":priority", p);
        jpql = jpql + AND + getRota(rota);
        jpql = jpql + AND + setContractorConfigured(contractorConfigured);
        jpql = !storeWalkround ? jpql + AND + "fpm.FaultTypeName != 'Store Walkround'" : jpql;

        if (!Boolean.valueOf(System.getProperty("updateWorkingHours"))) {
            if (brandInHours == 1) {
                jpql = jpql + AND + WITH_BRAND_IN_HOURS.replace(":timezone", timezone);
            } else if (brandInHours == -1){
                jpql = jpql + AND + WITH_BRAND_OUT_OF_HOURS.replace(":timezone", timezone);
            }
        }

        if (deferralInHours == -1) {
            jpql = jpql + AND + WITH_DEFERRAL_IN_HOURS.replace(":timezone", timezone);
        } else if (deferralInHours == 1){
            jpql = jpql + AND + WITH_DEFERRAL_OUT_OF_HOURS.replace(":timezone", timezone);
        }
        jpql = dbUtilityHelper.dbNameSchemaReplacement(jpql);
        String randomRow = String.format(GET_RANDOM_SQL, jpql);
        logger.debug("getRandomForVendorStore: " + randomRow);

        try {
            Query query =  entityManager.createNativeQuery(randomRow, SiteContractorAsset.class);
            SiteContractorAsset result = (SiteContractorAsset) query.getSingleResult();
            logger.debug("result: " + result.toString());
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public SiteContractorAsset getRandomForVendorStoreWithMultipleContractors(Integer priority, int brandInHours, String timezone, boolean storeWalkround, int deferralInHours) throws Exception {

        logger.debug("getRandomForVendorStore [" +  priority + "," + brandInHours + "," +  timezone + "," + storeWalkround + "]");

        String p = String.valueOf(priority);

        String resources = RESOURCES_AVAILABLE_NOW.replace(":timezone", timezone);

        String jpql = VENDOR_WITH_MULTIPLE_CONTRACTORS_SQL.replace(":resources", resources).replace(":priority", p);

        if (!Boolean.valueOf(System.getProperty("updateWorkingHours"))) {
            if (brandInHours == 1) {
                jpql = jpql + AND + WITH_BRAND_IN_HOURS.replace(":timezone", timezone);
            } else if (brandInHours == -1){
                jpql = jpql + AND + WITH_BRAND_OUT_OF_HOURS.replace(":timezone", timezone);
            }
        }

        if (deferralInHours == -1) {
            jpql = jpql + AND + WITH_DEFERRAL_IN_HOURS.replace(":timezone", timezone);
        } else if (deferralInHours == 1){
            jpql = jpql + AND + WITH_DEFERRAL_OUT_OF_HOURS.replace(":timezone", timezone);
        }
        jpql = dbUtilityHelper.dbNameSchemaReplacement(jpql);
        String randomRow = String.format(GET_RANDOM_SQL, jpql);
        logger.debug("getRandomForVendorStore: " + randomRow);

        try {
            Query query =  entityManager.createNativeQuery(randomRow, SiteContractorAsset.class);
            SiteContractorAsset result = (SiteContractorAsset) query.getSingleResult();
            logger.debug("result: " + result.toString());
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public SiteContractorAsset getRandomMappingForSite(Integer siteId) {
        String jpql = ALL_STORES_SQL + WHERE + String.format(FOR_SITE_ID, siteId);
        jpql = dbUtilityHelper.dbNameSchemaReplacement(jpql);
        String randomRow = String.format(GET_RANDOM_SQL, jpql);

        logger.debug("getRandomMappingForSite: " + randomRow);

        Query query =  entityManager.createNativeQuery(randomRow, SiteContractorAsset.class);
        return (SiteContractorAsset) query.getSingleResult();
    }

}
