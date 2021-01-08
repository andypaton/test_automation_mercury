package mercury.database.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.LogJobData;
import mercury.helpers.DbUtilityHelper;
import mercury.helpers.FileHelper;

@Repository
public class LogJobDataDao {

    @Autowired private EntityManager entityManager;
    @Autowired private DbUtilityHelper dbUtilityHelper;

    private static final Logger logger = LogManager.getLogger();

    private static final String WHERE = " WHERE ";
    private static final String AND_MOBILE = " AND r.PhoneNumber IS NOT NULL AND r.PhoneNumberType = 'Mobile' AND r.Id NOT IN (SELECT Id FROM %testdb.uvw_Resources WHERE PhoneNumberType != 'Mobile') ";
    private static final String AND_LANDLINE = " AND r.PhoneNumber IS NOT NULL AND r.PhoneNumberType = 'Landline' AND r.Id NOT IN (SELECT Id FROM %testdb.uvw_Resources WHERE PhoneNumberType != 'Landline') ";
    private static final String AND_OTHER_PHONE = " AND r.PhoneNumber IS NOT NULL AND r.PhoneNumberType = 'Other' AND r.Id NOT IN (SELECT Id FROM %testdb.uvw_Resources WHERE PhoneNumberType != 'Other') ";
    private static final String AND_PHONE = " AND r.PhoneNumber IS NOT NULL ";
    private static final String AND_NO_PHONE = " AND r.PhoneNumber IS NULL ";
    private static final String AND_NOT_MOBILE = " AND r.PhoneNumber IS NOT NULL AND r.PhoneNumberType != 'Mobile' AND r.Id NOT IN (SELECT Id FROM %testdb.uvw_Resources WHERE PhoneNumberType = 'Mobile')";

    private static final String AND_EMAIL = " AND r.EmailAddress IS NOT NULL ";
    private static final String AND_NO_EMAIL = " AND r.EmailAddress IS NULL ";

    private static final String AND_CONTACTOR_CONFIGURED = " AND s.NumContractors > 0 ";

    //    Only need 10 site as its the fault priority mappings which are key - Tests in MS SQL SMS have shown that for contractors the number of sites return needs to be
    //    limited when non gas jobs are being created
    private static final String SITES_WITH_CONTACTOR = "SELECT DISTINCT s.Name, s.Id, s.SiteTypeId FROM Site s LEFT JOIN AssetClassificationSite acs ON acs.SiteId = s.Id  LEFT JOIN AssetClassificationSiteContractorMapping acscm ON acscm.AssetClassificationSiteId = acs.Id WHERE acscm.ResourceId IS NOT NULL AND s.SiteStatusId = 1 AND s.Active = 1 ";

    // Only need 1 site as its the fault priority mappings which are key
    private static final String SITES_WITH_CITY_RESOURCE = "SELECT DISTINCT s.Name, s.Id, s.SiteTypeId FROM Site s INNER JOIN SiteResource sr ON sr.SiteId = s.Id WHERE s.Active = 1";

    private static final String AND_RESOURCE_ON_CALL = " AND s.Id IN (SELECT SiteId FROM %testdb.uvw_Rota WHERE RotaEntryType = 'On Call Scheduler') "
            + " AND r.Id IN (SELECT ResourceId FROM %testdb.uvw_Rota WHERE RotaEntryType = 'On Call Scheduler') ";

    private static final String SITES_WITH_AVAILABLE_PERM_CITY_TECH = "(SELECT SiteId FROM %testdb.uvw_Rota r " + " JOIN uvw_ResourceWorkingHours rwh ON rwh.ResourceId = r.ResourceId "
            + " WHERE RotaEntryType = 'Permanent' "
            + " AND ((rwh.StartAt <= CONVERT(TIME, SWITCHOFFSET(GETDATE(), ':timezone')) AND rwh.StartDayOfTheWeek = DATEPART(dw,GETDATE())) OR rwh.StartDayOfTheWeek < DATEPART(dw,GETDATE())) "
            + " AND ((rwh.EndAt >= CONVERT(TIME, SWITCHOFFSET(GETDATE(), ':timezone')) AND rwh.EndDayOfTheWeek = DATEPART(dw,GETDATE())) OR rwh.EndDayOfTheWeek > DATEPART(dw,GETDATE())) "
            + " )";

    private static final String AND_NO_PERM_CITY_TECHS_AVAILABLE = " AND s.Id NOT IN " + SITES_WITH_AVAILABLE_PERM_CITY_TECH;

    private static final String CITY_TECH_CONFIGURED = " s.CityTechConfigured = 1 ";
    private static final String AND_CITY_TECH_NOT_CONFIGURED = " AND s.CityTechConfigured = 0 ";

    private static final String AND_CITY_RESOURCE = " AND r.ResourceTypeName = 'City Resource' ";
    private static final String AND_CONTRACTOR = " AND r.ResourceTypeName = 'Contractor' ";
    private static final String AND_CONTRACTOR_NOT_CONFIGURED = " AND s.NumContractors = 0 ";
    private static final String AND_CONTRACTOR_CONFIGURED = " AND s.NumContractors > 0 ";

    private static final String AND_APPLICATION_USER_WITH_IPAD = " AND r.NotificationMethodName = 'ipad' ";
    private static final String AND_NO_IPAD = " AND r.NotificationMethodName IS NULL ";

    private static final String RANDOM_ORDER = " ORDER BY NEWID() ";

    private static final String RESOURCES_CTE = "SELECT r.Name AS ResourceName, r.Id AS ResourceId, rp.Name AS ResourceProfileName, rt.Name AS ResourceTypeName " +
            "FROM Resource r INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id INNER JOIN ResourceType rt ON rp.ResourceTypeId = rt.Id " +
            "LEFT JOIN ResourcePhoneNumber rpn ON rpn.ResourceId = r.Id LEFT JOIN ResourceEmailAddress rea ON rea.ResourceId = r.Id " +
            "INNER JOIN ApplicationUser au ON au.ResourceId = r.id INNER JOIN UserProfile up ON up.Id = au.UserProfileId " +
            "INNER JOIN ResourceNotificationMethod rnm ON r.Id = rnm.ResourceId INNER JOIN NotificationMethodType nmt ON rnm.NotificationMethodTypeId = nmt.Id ";

    private static final String CONTRACTORS_CTE = "SELECT r.Name AS ResourceName, r.Id AS ResourceId, rp.Name AS ResourceProfileName, rt.Name AS ResourceTypeName "
            + "FROM Resource r INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id INNER JOIN ResourceType rt ON rp.ResourceTypeId = rt.Id "
            + "LEFT JOIN ResourcePhoneNumber rpn ON rpn.ResourceId = r.Id LEFT JOIN ResourceEmailAddress rea ON rea.ResourceId = r.Id "
            + "INNER JOIN ApplicationUser au ON au.ResourceId = r.id INNER JOIN UserProfile up ON up.Id = au.UserProfileId "
            + "INNER JOIN %portaldb.tblHelpDeskResource hdr ON r.EpochId = hdr.fld_int_ID "
            + "INNER JOIN %portaldb.tblPriceBook pb ON pb.fld_int_resourceid = hdr.fld_int_Id "
            + "WHERE up.Active = 1 AND r.Active = 1 AND pb.fld_str_PartCode = 'Standard Callout' AND pb.fld_cur_CostEach > 0 "
            + "AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) "
            + "AND rp.Name = 'Contractor' AND ((up.Name = 'Contractor Admin' AND r.UsesEngineers = 0) OR up.Name != 'Contractor Admin') ";

    private static final String FAULT_PRIORITY_MAPPINGS_CTE = "SELECT fpm1.* , at.Name AS AssetTypeName " +
            "FROM uvw_faultprioritymappings fpm1 " +
            "INNER JOIN FaultPriorityMapping fpm ON fpm.Id = fpm1.Id " +
            "INNER JOIN JobRoutingProcess jp ON jp.Id = fpm.JobRoutingProcessId " +
            "LEFT JOIN (SELECT * FROM FaultType WHERE AvailableForStoreSelfLoggedJobs = 1) ft ON fpm1.FaultTypeId = ft.Id " +
            "LEFT JOIN AssetSubType ast ON ast.Id = fpm1.AssetSubTypeId INNER JOIN AssetType at ON at.Id = ast.AssetTypeId " +
            "LEFT JOIN AssetClassification ac ON fpm1.AssetClassificationId = ac.id " +
            "WHERE jp.NonDeferrable = 1 AND fpm1.Priority != 0 ";

    private static final String GET_RESULT = "SELECT TOP(1) s.Name AS SiteName, s.Id AS SiteId, s.SiteTypeId, s.ResourceName, s.ResourceId, "
            + "s.ResourceProfileName, s.ResourceTypeName, fpm.AssetClassificationId, fpm.AssetClassificationName, fpm.AssetTypeName, "
            + "fpm.AssetSubTypeName, fpm.ResponsePriorityId, fpm.FaultTypeId, fpm.FaultTypeName, l.Name AS LocationName, l.Id AS LocationId "
            + "FROM SITE_RESOURCES_CTE s "
            + "INNER JOIN FPM fpm ON fpm.SiteTypeId = s.SiteTypeId "
            + "INNER JOIN SiteTypeLocationMapping stlm ON stlm.SiteTypeId = s.SiteTypeId INNER JOIN Location l ON stlm.LocationId = l.id "
            + "INNER JOIN Asset a ON fpm.AssetClassificationId = a.AssetClassificationId AND a.SiteId = s.Id";

    private static final String SITE_TO_RESOURCE_MAP = ""
            + "SELECT DISTINCT SiteId, ResourceId, ResourceType "
            + "FROM ( "
            + "  SELECT oss.SiteId, r.Id ResourceId, 'City Resource' ResourceType "
            + "  FROM OrganisationStructure OS "
            + "    LEFT JOIN OrganisationStructureSite OSS ON OS.Id = OSS.OrganisationStructureId AND OS.Active = 1 AND OS.Active = 1 "
            + "    LEFT JOIN OrganisationStructureManager OSM ON OSM.OrganisationStructureId = OS.Id AND OSM.Active = 1 "
            + "    LEFT JOIN ApplicationUser AU ON AU.Id = OSM.ApplicationUserId "
            + "    LEFT JOIN Resource R ON R.Id = AU.ResourceId "
            + "    INNER JOIN OrganisationStructureType OST ON OST.Id = OS.OrganisationStructureTypeId "
            + "    INNER JOIN dbo.RotaEntry re ON oss.id = re.OrganisationStructureSiteId "
            + "  WHERE oss.SiteId IS NOT NULL AND r.ID IS NOT NULL "
            + "    AND re.Active=1 "
            + "  UNION ALL "
            + "  SELECT SiteId, ResourceId, 'Contractor' ResourceType "
            + "  FROM AssetClassificationSiteContractorMapping acscm "
            + "    INNER JOIN AssetClassificationSite acs ON acscm.AssetClassificationSiteId = acs.Id "
            + ") AllSiteResources";

    private static final String SITE_RESOURCES_CTE = "SELECT s.Name, s.Id, s.SiteTypeId, " +
            "r.ResourceName, r.ResourceId, r.ResourceProfileName, r.ResourceTypeName  " +
            "FROM STORES s " +
            "INNER JOIN SITE_TO_RESOURCE_MAP sr ON s.Id = sr.SiteId " +
            "INNER JOIN RESOURCES r ON sr.ResourceId = r.ResourceId ";

    private static final String MAIN_CTE = "WITH RESOURCES AS (:resourcesCTE), "
            + "STORES AS (:storesCTE), "
            + "FPM AS (:fpmCTE), "
            + "SITE_TO_RESOURCE_MAP AS (" + SITE_TO_RESOURCE_MAP + "), "
            + "SITE_RESOURCES_CTE AS (" + SITE_RESOURCES_CTE + ") "
            + GET_RESULT;

    public LogJobData getLogJobDataForSite(String priorityCsvList, int siteId) throws Exception {

        String sql = FileHelper.getResourceFile("sql/select/", "GET_DATA_FOR_LOG_JOB.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = sql.concat(WHERE);

        sql = String.format(sql, priorityCsvList)
                + " AND s.Id = " + siteId
                + RANDOM_ORDER;

        logger.debug("getLogJobDataForSite: " + sql);
        return getLogJobData(sql);
    }

    public LogJobData cityTechResource(String priorityCsvList) throws Exception {

        String sql = "SELECT TOP(1) s.Name AS SiteName, s.Id AS SiteId, s.SiteTypeId , r.Name AS ResourceName, r.Id AS ResourceId, "
                + "rp.Name AS ResourceProfileName, rt.Name AS ResourceTypeName, fpm.AssetClassificationId, fpm.AssetClassificationName, "
                + "at.Name AS AssetTypeName, fpm.AssetSubTypeName, fpm.Priority, fpm.FaultTypeId, fpm.FaultTypeName, null AS LocationName, null AS LocationId "
                + "FROM Site s INNER JOIN SiteResource sr ON s.Id = sr.SiteId INNER JOIN Resource r ON sr.ResourceId = r.Id "
                + "INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id INNER JOIN ResourceType rt ON rp.ResourceTypeId = rt.Id "
                + "INNER JOIN uvw_faultprioritymappings fpm ON fpm.SiteTypeId = s.SiteTypeId INNER JOIN AssetSubType ast ON ast.Id = fpm.AssetSubTypeId "
                + "INNER JOIN AssetType at ON at.Id = ast.AssetTypeId INNER JOIN ResourcePhoneNumber rpn ON r.Id = rpn.ResourceId "
                + "INNER JOIN (SELECT SiteId, ResourceId FROM %testdb.uvw_Rota WHERE RotaEntryType = 'Permanent' AND ResourceTypeName = 'City Resource') ct ON ct.SiteId = s.Id "
                + "WHERE rt.Name = 'City Resource' AND rpn.PhoneNumber IS NOT NULL AND rpn.PhoneNumberTypeId = 1 AND fpm.Priority IN (:priorityCsvList)";

        sql = sql.replaceAll(":priorityCsvList", priorityCsvList);
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("cityTechResource: " + sql);
        return getLogJobData(sql);
    }

    public LogJobData cityTechResourceWithIpadAndMobile(String priorityCsvList) throws Exception {

        String sql = FileHelper.getResourceFile("sql/select/", "GET_DATA_FOR_LOG_JOB.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = sql.concat(WHERE);

        sql = String.format(sql, priorityCsvList) + CITY_TECH_CONFIGURED
                + AND_CITY_RESOURCE
                + AND_APPLICATION_USER_WITH_IPAD
                + AND_MOBILE
                + RANDOM_ORDER;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("cityTechResourceWithIpadAndMobile: " + sql);
        return getLogJobData(sql);
    }

    public LogJobData noCityTechResourceContractorConfigured(String priorityCsvList) throws Exception {

        String sql = FileHelper.getResourceFile("sql/select/", "GET_DATA_FOR_LOG_JOB.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = sql.concat(WHERE);

        sql = String.format(sql, priorityCsvList) + AND_CITY_TECH_NOT_CONFIGURED
                + AND_CONTACTOR_CONFIGURED
                + RANDOM_ORDER;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("noCityTechResourceContractorConfigured: " + sql);
        return getLogJobData(sql);
    }

    public LogJobData resourceOnCallNoPermsAvailable(String priorityCsvList, String timezone) throws Exception {

        String sql = FileHelper.getResourceFile("sql/select/", "GET_DATA_FOR_LOG_JOB.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = sql.concat(WHERE);

        sql = String.format(sql, priorityCsvList) + CITY_TECH_CONFIGURED
                + AND_CITY_RESOURCE
                + AND_RESOURCE_ON_CALL
                + AND_NO_PERM_CITY_TECHS_AVAILABLE.replace(":timezone", timezone)
                + RANDOM_ORDER;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("resourceOnCall: " + sql);
        return getLogJobData(sql);
    }

    public LogJobData noCityTechAndNoContractor(int siteId, String priorityCsvList) throws Exception {

        String sql = "SELECT TOP(1) s.Name AS SiteName, s.Id AS SiteId, s.SiteTypeId "
                + ", fpm.assetclassificationid, fpm.AssetClassificationName, fpm.assetsubtypename, fpm.priority, fpm.FaultTypeId, fpm.faulttypename  "
                + ", null AS ResourceName, null AS ResourceId, null AS ResourceProfileName, null AS ResourceTypeName "
                + "FROM %testdb.uvw_Stores s  "
                + "INNER JOIN uvw_faultprioritymappings fpm ON fpm.sitetypeid = s.sitetypeid AND fpm.FaultTypeId NOT IN (292, 293, 294)  "
                + "WHERE s.Id = %d AND priority IN (%s) AND";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = String.format(sql, siteId, priorityCsvList) + AND_CITY_TECH_NOT_CONFIGURED
                + AND_CONTRACTOR_NOT_CONFIGURED
                + RANDOM_ORDER;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("noCityTechAndNoContractor: " + sql);
        return getLogJobData(sql);
    }

    public LogJobData noCityTechAndWithContractor(int siteId, String priorityCsvList) throws Exception {

        String sql = "SELECT TOP(1) s.Name AS SiteName, s.Id AS SiteId, s.SiteTypeId "
                + ", fpm.assetclassificationid, null AS AssetTypeName, fpm.AssetClassificationName, fpm.assetsubtypename, fpm.priority, fpm.FaultTypeId, fpm.faulttypename  "
                + ", null AS ResourceName, null AS ResourceId, null AS ResourceProfileName, null AS ResourceTypeName "
                + "FROM %testdb.uvw_Stores s  "
                + "INNER JOIN uvw_faultprioritymappings fpm ON fpm.sitetypeid = s.sitetypeid AND fpm.FaultTypeId NOT IN (292, 293, 294)  "
                + "INNER JOIN uvw_faultprioritymappings fpm2 ON  fpm.AssetClassificationId = fpm2.AssetClassificationId"
                + "WHERE fpm.priority IN (%s) "
                + "AND fpm2.faultPriorityId = 4 ";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, priorityCsvList);
        sql = siteId == 0 ? sql : sql + String.format(" AND s.id = %d ", siteId);
        sql = sql + AND_CITY_TECH_NOT_CONFIGURED
                + AND_CONTRACTOR_CONFIGURED
                + RANDOM_ORDER;
        logger.debug("noCityTechAndWithContractor: " + sql);
        return getLogJobData(sql);
    }

    /**
     *
     * @param isImmediateCallout
     * @param withCityTech : null = dont care, false = without city tech, true = with city tech
     * @param withContractor : null = dont care, false = without contractor, true = with contractor
     * @return
     * @throws Exception
     */
    public LogJobData getLogJobData(boolean isImmediateCallout, Boolean withCityTech, Boolean withContractor) throws Exception {

        String sql = "WITH ROTA AS"
                + " (SELECT DISTINCT re.ResourceId , rt.Name AS ResourceTypeName , oss.SiteId , ret.Name AS RotaEntryType"
                + " FROM RotaEntry re"
                + " JOIN OrganisationStructureSite oss ON oss.id=re.OrganisationStructureSiteId"
                + " LEFT JOIN RotaEntryType ret ON ret.Id=re.RotaEntryTypeId"
                + " LEFT JOIN ResourceProfile rp ON rp.Id=re.ResourceProfileId"
                + " LEFT JOIN ResourceType rt ON rp.ResourceTypeId = rt.Id"
                + " LEFT JOIN .uvw_ResourceAbsence ra ON ra.ResourceId = re.ID"
                + " WHERE re.Active = 1"
                + " AND (COALESCE(ra.AbsenceStartAt, '2999-12-01') > CAST(SWITCHOFFSET(GETDATE(), '+00:00') AS DATETIMEOFFSET) OR COALESCE(ra.AbsenceEndAt, '2999-12-01') < CAST(SWITCHOFFSET(GETDATE(), '+00:00') AS DATETIMEOFFSET))"
                + " AND (re.StartAt < CAST(SWITCHOFFSET(GETDATE(), '+00:00') AS DATETIMEOFFSET) AND CAST(SWITCHOFFSET(GETDATE(), '+00:00') AS DATETIMEOFFSET) < COALESCE(re.EndAt, '2999-12-01'))),"
                + " CTE AS (SELECT TOP(100000) s.Name AS SiteName, s.Id AS SiteId, s.SiteTypeId,"
                + " fpm.AssetClassificationId, NULL AS AssetTypeName, fpm.AssetClassificationName,"
                + " fpm.AssetSubTypeName, fpm.ResponsePriorityId, fpm.FaultTypeId, fpm.FaultTypeName,"
                + " NULL AS ResourceName, NULL AS ResourceId, NULL AS ResourceProfileName, "
                + " NULL AS ResourceTypeName, l.Id AS LocationId, l.Name AS LocationName,"
                + " CASE WHEN ct.ResourceId IS NULL THEN 0 ELSE 1 END AS CityTechConfigured,"
                + " CASE WHEN c.SiteId IS NULL THEN 0 ELSE c.NumContractors END AS NumContractors,"
                + " fpm.ImmediateCallout AS ImmediateCallout"
                + " FROM Site s"
                + " JOIN uvw_faultprioritymappings fpm ON fpm.sitetypeid = s.sitetypeid"
                + " JOIN AssetClassificationSiteTypeLocationsMapping acstlm ON acstlm.AssetClassificationId = fpm.AssetClassificationId"
                + " JOIN SiteTypeLocationMapping stlm ON stlm.Id = acstlm.SiteTypeLocationMappingId"
                + " JOIN Location l ON l.Id = stlm.LocationId"
                + " LEFT JOIN (SELECT acs.SiteId, COUNT(DISTINCT(ResourceId)) AS NumContractors FROM AssetClassificationSiteContractorMapping acscm JOIN AssetClassificationSite acs ON acscm.AssetClassificationSiteId = acs.Id GROUP BY acs.SiteId) c ON c.SiteId = s.Id"
                + " LEFT JOIN (SELECT SiteId, ResourceId FROM ROTA rota WHERE RotaEntryType = 'Permanent' AND ResourceTypeName = 'City Resource') ct ON ct.SiteId = s.Id"
                + " WHERE fpm.RequiresReview = 0 AND fpm.ImmediateCallout = :immediateCallout)"
                + " SELECT TOP (1) SiteName, SiteId, SiteTypeId, AssetClassificationId, AssetTypeName, AssetClassificationName, AssetSubTypeName, ResponsePriorityId,"
                + " FaultTypeId, FaultTypeName, ResourceName, ResourceId, ResourceProfileName, ResourceTypeName,  LocationId, LocationName FROM CTE WHERE ImmediateCallout = :immediateCallout";

        String immediateCallout = isImmediateCallout ? "1" : "0";
        sql = sql.replaceAll(":immediateCallout", immediateCallout);

        if (withCityTech != null) {
            sql += withCityTech ? " AND CityTechConfigured > 0 " : " AND CityTechConfigured = 0 ";
        }

        if (withContractor != null) {
            sql += withContractor ? " AND NumContractors > 0 " : " AND NumContractors = 0 ";
        }

      //  sql = sql + RANDOM_ORDER;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("noCityTechAndWithContractor: " + sql);
        try {
            return getLogJobData(sql);
        } catch (Exception ex) {
            return null;
        }

    }

    public LogJobData noCityTechAndWithContractor(boolean isImmediateCallout) throws Exception {
        return getLogJobData(isImmediateCallout, false, true);
    }

    public LogJobData noCityTechAndNoContractor(boolean isImmediateCallout) throws Exception {
        return getLogJobData(isImmediateCallout, false, false);
    }

    public LogJobData get(String priorityCsvList, Boolean isContractor, Boolean hasIpad, String phoneType, Boolean hasEmail) throws Exception {

        String sql = FileHelper.getResourceFile("sql/select/", "GET_DATA_FOR_LOG_JOB.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = sql.concat(WHERE);

        sql = String.format(sql, priorityCsvList) + CITY_TECH_CONFIGURED ;

        if (isContractor != null) {
            sql = isContractor ? sql + AND_CONTRACTOR : sql + AND_CITY_RESOURCE;
        }

        sql = hasIpad ? sql + AND_APPLICATION_USER_WITH_IPAD
                : sql + AND_NO_IPAD;
        sql = hasEmail ? sql + AND_EMAIL
                : sql + AND_NO_EMAIL;

        if (phoneType == null) {
            sql = sql + AND_NO_PHONE;
        } else if ("NO MOBILE".equalsIgnoreCase(phoneType)) {
            sql = sql + AND_NOT_MOBILE;
        } else if ("OTHER".equalsIgnoreCase(phoneType)) {
            sql = sql + AND_OTHER_PHONE;
        } else if ("MOBILE".equalsIgnoreCase(phoneType)) {
            sql = sql + AND_MOBILE;
        } else if ("LANDLINE".equalsIgnoreCase(phoneType)) {
            sql = sql + AND_LANDLINE;
        } else if ("ANY".equalsIgnoreCase(phoneType)) {
            sql = sql + AND_PHONE;
        }
        sql = sql + RANDOM_ORDER;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("get: " + sql);
        return getLogJobData(sql);
    }

    public LogJobData getLogJobDataForContractor(String priorityCsvList) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_DATA_SUITABLE_FOR_CONTRACTOR.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = sql.replace("fpm1.Priority IN (1)", "fpm1.Priority IN (" + priorityCsvList + ")");

        logger.debug("getLogJobDataForContractor: " + sql);
        return getLogJobData(sql);
    }

    public LogJobData getLogJobDataForContractorTechnician(String priorityCsvList) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_JOB_DATA_SUITABLE_FOR_CONTRACTOR_TECH.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = sql.replace("fpm1.Priority IN (1)", "fpm1.Priority IN (" + priorityCsvList + ")");

        logger.debug("getLogJobDataForContractorTechnician: " + sql);
        return getLogJobData(sql);
    }


    /**
     * Runs a query to pull back all the data required to log a job
     *
     * @param priorityCsvList - can be either a list or 1 value.
     * @param isContractor - should the job be logged for a contractor
     * @param hasIpad - should the tech have an ipad
     * @param phoneType - phone number type (NO MOBILE, OTHER, MOBILE, LANDLINE, ANY)
     * @param hasEmail - has the tech to have an email address configured
     * @param assetType - type of asset if any the job should be logged against (HVAC, Refrigeration, Unknown, Non Gas, anything else)
     *
     * @return An instance of the object LogJobData
     * @throws Exception
     */
    public LogJobData getLogJobData_CTE(String priorityCsvList, Boolean isContractor, Boolean hasIpad, String phoneType, Boolean hasEmail) throws Exception {

        String storesCTE = isContractor ? SITES_WITH_CONTACTOR : SITES_WITH_CITY_RESOURCE;

        String resourcesCTE = isContractor ? CONTRACTORS_CTE : RESOURCES_CTE;

        if ( !isContractor ) {
            resourcesCTE = resourcesCTE.concat("WHERE up.Active = 1 AND r.Active = 1 AND rt.Name = 'City Resource' AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) ");
            resourcesCTE = hasIpad ? resourcesCTE + "AND nmt.Name = 'iPad' " : resourcesCTE + "AND nmt.Name IS NULL ";
            resourcesCTE = hasEmail ? resourcesCTE + "AND rea.EmailAddress IS NOT NULL " : resourcesCTE + "AND rea.EmailAddress IS NULL ";

            if (phoneType == null) {
                resourcesCTE = resourcesCTE.concat("AND rpn.PhoneNumber IS NULL ");
            } else if ("NO MOBILE".equalsIgnoreCase(phoneType)) {
                resourcesCTE = resourcesCTE.concat("AND rpn.PhoneNumber IS NOT NULL AND rpn.PhoneNumberTypeId != 1 ");
            } else if ("OTHER".equalsIgnoreCase(phoneType)) {
                resourcesCTE = resourcesCTE.concat("AND rpn.PhoneNumber IS NOT NULL AND rpn.PhoneNumberTypeId = 4 ");
            } else if ("MOBILE".equalsIgnoreCase(phoneType)) {
                resourcesCTE = resourcesCTE.concat("AND rpn.PhoneNumber IS NOT NULL AND rpn.PhoneNumberTypeId = 1 ");
            } else if ("LANDLINE".equalsIgnoreCase(phoneType)) {
                resourcesCTE = resourcesCTE.concat("AND rpn.PhoneNumber IS NOT NULL AND rpn.PhoneNumberTypeId = 2 ");
            } else if ("ANY".equalsIgnoreCase(phoneType)) {
                resourcesCTE = resourcesCTE.concat("AND rpn.PhoneNumber IS NOT NULL ");
            }
        }

        String fpmCTE = FAULT_PRIORITY_MAPPINGS_CTE;

        String sql = MAIN_CTE;
        sql = sql.replace(":fpmCTE", fpmCTE).replace(":storesCTE", storesCTE).replace(":resourcesCTE", resourcesCTE);

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getLogJobData_CTE query: " + sql);
        return getLogJobData(sql);
    }
    
    public LogJobData getLogJobData_DeferrableFaultPriorities(String priorityCsvList, Boolean isContractor, Boolean hasIpad, String phoneType, Boolean hasEmail) throws Exception {

        String storesCTE = isContractor ? SITES_WITH_CONTACTOR : SITES_WITH_CITY_RESOURCE;

        String resourcesCTE = isContractor ? CONTRACTORS_CTE : RESOURCES_CTE;

        if ( !isContractor ) {
            resourcesCTE = resourcesCTE.concat("WHERE up.Active = 1 AND r.Active = 1 AND rt.Name = 'City Resource' AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) ");
        }

        String fpmCTE = "SELECT fpm1.* , at.Name AS AssetTypeName " +
                "FROM uvw_faultprioritymappings fpm1 " +
                "INNER JOIN FaultPriorityMapping fpm ON fpm.Id = fpm1.Id " + 
                "INNER JOIN JobRoutingProcess jp ON jp.Id = fpm.JobRoutingProcessId " +
                "LEFT JOIN (SELECT * FROM FaultType WHERE AvailableForStoreSelfLoggedJobs = 1) ft ON fpm1.FaultTypeId = ft.Id " +
                "LEFT JOIN AssetSubType ast ON ast.Id = fpm1.AssetSubTypeId INNER JOIN AssetType at ON at.Id = ast.AssetTypeId " +
                "LEFT JOIN AssetClassification ac ON fpm1.AssetClassificationId = ac.id " +
                "WHERE jp.NonDeferrable = 0 AND fpm1.Priority != 0 AND jp.RequiresReview = 0 ";

        String sql = MAIN_CTE;
        sql = sql.replace(":fpmCTE", fpmCTE).replace(":storesCTE", storesCTE).replace(":resourcesCTE", resourcesCTE);

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getLogJobData_CTE query: " + sql);
        return getLogJobData(sql);
    }

    public LogJobData getLogJobData_CTE(String priorityCsvList, int siteId, int resourceId) throws Exception {
        String storesCTE = SITES_WITH_CITY_RESOURCE;
        String resourcesCTE = RESOURCES_CTE;

        String fpmCTE = FAULT_PRIORITY_MAPPINGS_CTE;

        String sql = MAIN_CTE;
        sql = sql.replace(":fpmCTE", fpmCTE).replace(":storesCTE", storesCTE).replace(":resourcesCTE", resourcesCTE).replace(":faultPriority", priorityCsvList);
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = sql + String.format(" AND siteId = %d AND resourceId = %d", siteId, resourceId);

        logger.debug("getLogJobData_CTE: " + sql);
        return getLogJobData(sql);
    }

    private LogJobData getLogJobData(String sql) {
        try {
            Query query = entityManager.createNativeQuery(sql, LogJobData.class);
            return (LogJobData) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
