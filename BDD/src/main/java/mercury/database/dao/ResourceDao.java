package mercury.database.dao;

import static mercury.helpers.StringHelper.quote;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.Resource;
import mercury.helpers.DbUtilityHelper;

@Repository
public class ResourceDao {
    private static final Logger logger = LogManager.getLogger();

    @Autowired private EntityManager entityManager;
    @Autowired private DbUtilityHelper dbUtilityHelper;

    private static final Integer DEFAULT_RECORDCOUNT = 1;
    private static final String RANDOM_FIELD_LIST = " Id, ResourceProfileId, Name, KnownAs, UsesJobClosedown, AlwaysChargeable, AutoAssign, ReferenceRequired, SlaFromTypeId, CreatedOn, CreatedBy, UpdatedOn, UpdatedBy, Active, EpochId, IsVip, CanAttachAsset, IsGeoFenced, UsesEngineers, HomeStoreId, OnlineInvoicingActive, SupplierTCode, PayrollTCode ";

    private static final String FIELD_LIST_WITH_PROFILE = " r.Id, r.ResourceProfileId, r.name + ' (' +  rp.alias + ')' AS Name, r.KnownAs, r.UsesJobClosedown, r.AlwaysChargeable, r.AutoAssign, r.ReferenceRequired, r.SlaFromTypeId, r.CreatedOn, r.CreatedBy, r.UpdatedOn, r.UpdatedBy, r.Active, r.EpochId, r.IsVip, r.CanAttachAsset, r.IsGeoFenced, r.UsesEngineers, r.HomeStoreId, r.OnlineInvoicingActive, r.SupplierTCode, r.PayrollTCode ";

    private static final String GET_RANDOM_SQL = "SELECT TOP(:recordCount) " + RANDOM_FIELD_LIST + " FROM (%s) randomOrder ORDER BY NEWID()";

    private static final String GET_RESOURCE_BASE_SQL = "SELECT r.* FROM Resource r INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id INNER JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId ";
    private static final String GET_RESOURCE_DROPDOWN_BASE_SQL = "SELECT " + FIELD_LIST_WITH_PROFILE + " FROM Resource r INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id INNER JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId ";

    private static final String GET_RESOURCE_BY_ID_WHERE_SQL = "WHERE r.active = 1 AND r.Id = %d ";

    private static final String GET_CITY_RESOURCES_BY_ID_WHERE_SQL = " WHERE rp.ResourceTypeId = 1 AND rp.Active = 1 AND rp.JobCallouts = 1 AND r.Active = 1 ";

    private static final String GET_CITY_RESOURCES_BY_ID_EXCLUDE_FGAS_WHERE_SQL = "WHERE r.active = 1 AND r.resourceprofileid IN (3, 134) ";

    private static final String GET_CONTRACT_SUPPLY_RESOURCES_BY_ID_WHERE_SQL = "WHERE r.active = 1 AND r.resourceprofileid IN (115) ";  // Remove supply only
    private static final String GET_CONTRACT_RESOURCES_BY_ID_WHERE_SQL = "WHERE r.active = 1 AND r.resourceprofileid IN (115) AND au.UserProfileId = 115 ";

    private static final String WHERE_IS_CITY_TECH = " WHERE r.active = 1 AND rt.Name = 'City Resource' AND rp.Active = 1 AND rp.JobCallouts = 1 ";

    private static final String AND_RESOURCE_AVAILABLE = " AND r.Id IN (SELECT ResourceId FROM uvw_ResourceWorkingHours rwh "
            + " WHERE ((rwh.StartAt <= CONVERT(TIME, SWITCHOFFSET(GETDATE(), ':timezone')) AND rwh.StartDayOfTheWeek = DATEPART(dw,GETDATE())) OR rwh.StartDayOfTheWeek < DATEPART(dw,GETDATE())) "
            + " AND ((rwh.EndAt >= CONVERT(TIME, SWITCHOFFSET(GETDATE(), ':timezone')) AND rwh.EndDayOfTheWeek = DATEPART(dw,GETDATE())) OR rwh.EndDayOfTheWeek > DATEPART(dw,GETDATE()))) ";

    private static final String AND_PHONE_CONFIGURED = " AND  r.Id IN (SELECT ResourceId FROM ResourcePhoneNumber) ";

    private static final String AND_APPLICATION_USER_ACTIVE = " AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) ";

    private static final String ORDER_BY_SQL = " ORDER BY r.name ";

    private static final String PORTAL_RESOURCE_FIELDS = "hr.fld_int_Id Id,  hr.fld_int_HelpDeskResourceTypeId ResourceProfileId, hr.fld_str_Name Name, null KnownAs, " +
            " null UsesJobClosedown, null AlwaysChargeable, null AutoAssign, null  ReferenceRequired, null  SLAFromTypeId, GETDATE() CreatedOn, null CreatedBy, GETDATE() UpdatedOn, null UpdatedBy, " +
            " hr.FLD_BIT_ACTIVE Active, null EpochId, null IsVip, null CanAttachAsset, null IsGeoFenced, null UsesEngineers, null HomeStoreId, null OnlineInvoicingActive, " +
            " hr.fld_str_SupplierID SupplierTCode, null PayrollTCode ";

    private static final String GET_PORTAL_RESOURCE_BASE_SQL = "SELECT  " + PORTAL_RESOURCE_FIELDS +
            " FROM %helpdeskdb.Resource r INNER JOIN %helpdeskdb.ResourceProfile rp ON r.ResourceProfileId = rp.Id " +
            " INNER JOIN %portaldb.tblhelpdeskresource hr ON r.EpochId = hr.fld_int_ID " +
            " INNER JOIN %helpdeskdb.ApplicationUser au ON au.ResourceId = r.id ";

    private static final String GET_PORTAL_RFM_RESOURCE = "WHERE rp.Name  = :resourceProfile AND r.Active = 1 ";

    @SuppressWarnings("unchecked")
    public List<Resource> getAll() {
        return entityManager.createQuery("from mercury.database.models.Resource").getResultList();
    }

    /**
     * Fetch Job entity by Id
     * @param id
     * @return
     */
    public Resource getByResourceId(Integer resourceId) {
        String jpql = GET_RESOURCE_BASE_SQL + GET_RESOURCE_BY_ID_WHERE_SQL;

        String queryStatement = String.format(jpql, resourceId);
        logger.debug("getByResourceId: " + queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, Resource.class);
            return (Resource) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch Job entity by resource Type
     * @param id
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public List<Resource> getByResourceType(String resourceType) throws Exception {
        String queryStatement = null;
        switch (resourceType) {
        case "Technician" :
            queryStatement = GET_RESOURCE_BASE_SQL + GET_CITY_RESOURCES_BY_ID_WHERE_SQL + ORDER_BY_SQL;
            break;
        case "Contractor" :
            queryStatement = GET_RESOURCE_BASE_SQL + GET_CONTRACT_SUPPLY_RESOURCES_BY_ID_WHERE_SQL + ORDER_BY_SQL;
            break;
        default :
            throw new Exception("Cannot find Resource Type" + resourceType);
        }

        logger.debug(queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, Resource.class);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch Job entity by resource Type
     * @param id
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public List<Resource> getByResourceTypeExcludeFGasSupply(String resourceType) throws Exception {
        String queryStatement = null;
        switch (resourceType) {
        case "Technician" :
            queryStatement = GET_RESOURCE_DROPDOWN_BASE_SQL + GET_CITY_RESOURCES_BY_ID_EXCLUDE_FGAS_WHERE_SQL;
            break;
        case "Contractor" :
            queryStatement = GET_RESOURCE_BASE_SQL + GET_CONTRACT_RESOURCES_BY_ID_WHERE_SQL;
            break;
        default :
            throw new Exception("Cannot find Resource Type" + resourceType);
        }

        logger.debug(queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, Resource.class);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public Resource getRandomAvailableCityTech(String timezone) {
        String jpql = GET_RESOURCE_BASE_SQL + WHERE_IS_CITY_TECH + AND_RESOURCE_AVAILABLE.replace(":timezone", timezone) + AND_PHONE_CONFIGURED;
        String randomRow = String.format(GET_RANDOM_SQL, jpql);
        logger.debug("getRandomAvailableCityTech: " + randomRow);
        Query query =  entityManager.createNativeQuery(randomRow, Resource.class);
        query.setParameter("recordCount", DEFAULT_RECORDCOUNT);
        try {
            return (Resource) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Resource getRandomCityTechResource() {
        String jpql = GET_RESOURCE_BASE_SQL + WHERE_IS_CITY_TECH + " AND r.Active = 1 ";
        String randomRow = String.format(GET_RANDOM_SQL, jpql);
        logger.debug("getRandomAvailableCityTech: " + randomRow);
        Query query = entityManager.createNativeQuery(randomRow, Resource.class);
        query.setParameter("recordCount", DEFAULT_RECORDCOUNT);
        try {
            return (Resource) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Resource getRandomActiveResource() {
        String jpql = GET_RESOURCE_BASE_SQL + " WHERE r.Active = 1";
        String randomRow = String.format(GET_RANDOM_SQL, jpql);
        logger.debug("getRandomActiveResource: " + randomRow);
        Query query =  entityManager.createNativeQuery(randomRow, Resource.class);
        query.setParameter("recordCount", DEFAULT_RECORDCOUNT);
        try {
            return (Resource) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Resource getRandomActiveResource(String resourceProfile) {
        String jpql = GET_RESOURCE_BASE_SQL + GET_PORTAL_RFM_RESOURCE;
        String randomRow = String.format(GET_RANDOM_SQL, jpql);
        logger.debug("getRandomActiveResource: " + randomRow);
        Query query =  entityManager.createNativeQuery(randomRow, Resource.class);
        query.setParameter("recordCount", DEFAULT_RECORDCOUNT);
        query.setParameter("resourceProfile", resourceProfile);
        try {
            return (Resource) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Resource getRandomActiveResourceWithActiveSite(String resourceProfile) {
        String jpql = GET_RESOURCE_BASE_SQL
                + "INNER JOIN uvw_ManagerToOrganisationStructures mtos ON r.Id = mtos.ManagerResourceId "
                + "INNER JOIN organisationstructuresite oss ON oss.OrganisationStructureId = mtos.OrganisationStructureId "
                + "INNER JOIN Site s ON s.id = oss.SiteId "
                + GET_PORTAL_RFM_RESOURCE
                + "AND s.Id IS NOT NULL";
        String randomRow = String.format(GET_RANDOM_SQL, jpql);

        logger.debug("getRandomActiveResourceWithActiveSite: " + randomRow.replace(":recordCount", DEFAULT_RECORDCOUNT.toString()).replace(":resourceProfile", quote(resourceProfile)));

        Query query =  entityManager.createNativeQuery(randomRow, Resource.class);
        query.setParameter("recordCount", DEFAULT_RECORDCOUNT);
        query.setParameter("resourceProfile", resourceProfile);
        try {
            return (Resource) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Resource>  getRandomCityTechQuoteResource(String multiQuote) {
        String jpql = GET_RESOURCE_BASE_SQL + GET_CITY_RESOURCES_BY_ID_WHERE_SQL;
        String randomRow = String.format(GET_RANDOM_SQL, jpql);
        logger.debug("getRandomCityTechQuoteResource: " + randomRow);
        Query query = entityManager.createNativeQuery(randomRow, Resource.class);
        Integer numberOfQuotes =  (multiQuote.equalsIgnoreCase("single")) ? DEFAULT_RECORDCOUNT : RandomUtils.nextInt(2, 4);
        query.setParameter("recordCount", numberOfQuotes);
        try {
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Resource>  getRandomContractQuoteResource(String multiQuote) {
        String jpql = GET_RESOURCE_BASE_SQL + GET_CONTRACT_RESOURCES_BY_ID_WHERE_SQL;
        String randomRow = String.format(GET_RANDOM_SQL, jpql);
        logger.debug("getRandomAvailableContractor: " + randomRow);
        Query query = entityManager.createNativeQuery(randomRow, Resource.class);
        Integer numberOfQuotes =  (multiQuote.equalsIgnoreCase("single")) ? DEFAULT_RECORDCOUNT : RandomUtils.nextInt(2, 4);
        query.setParameter("recordCount", numberOfQuotes);
        try {
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Resource>  getRandomPortalCityTechQuoteResource(String multiQuote) {
        String sql = GET_PORTAL_RESOURCE_BASE_SQL + GET_CITY_RESOURCES_BY_ID_WHERE_SQL + AND_APPLICATION_USER_ACTIVE;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        String randomRow = String.format(GET_RANDOM_SQL, sql);
        logger.debug("getRandomAvailableCityTech: " + randomRow);
        Query query = entityManager.createNativeQuery(randomRow, Resource.class);
        Integer numberOfQuotes =  (multiQuote.equalsIgnoreCase("single")) ? DEFAULT_RECORDCOUNT : RandomUtils.nextInt(2, 4);
        query.setParameter("recordCount", numberOfQuotes);
        try {
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Resource>  getRandomPortalContractorQuoteResourceForBypass() {
        String sql = GET_PORTAL_RESOURCE_BASE_SQL + GET_CONTRACT_RESOURCES_BY_ID_WHERE_SQL + AND_APPLICATION_USER_ACTIVE;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        String randomRow = String.format(GET_RANDOM_SQL, sql);
        logger.debug("getRandomPortalContractorQuoteResourceForBypass: " + randomRow);
        Query query = entityManager.createNativeQuery(randomRow, Resource.class);
        Integer numberOfQuotes = 3;
        query.setParameter("recordCount", numberOfQuotes);
        try {
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public Resource getPortalResourceForHelpdeskResourceId(int resourceId) {
        String sql = GET_PORTAL_RESOURCE_BASE_SQL + GET_RESOURCE_BY_ID_WHERE_SQL;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, resourceId);
        sql = sql + " AND au.UserName NOT LIKE '%_tech'";
        logger.debug("getPortalResourceForHelpdeskResourceId: " + sql);
        try {
            Query query =  entityManager.createNativeQuery(sql, Resource.class);
            return (Resource) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Resource>  getRandomPortalContractQuoteResource(String multiQuote) {
        String sql = GET_PORTAL_RESOURCE_BASE_SQL + GET_CONTRACT_RESOURCES_BY_ID_WHERE_SQL + AND_APPLICATION_USER_ACTIVE;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        String randomRow = String.format(GET_RANDOM_SQL, sql);
        logger.debug("getRandomAvailableContractor: " + randomRow);
        Query query = entityManager.createNativeQuery(randomRow, Resource.class);
        Integer numberOfQuotes = (multiQuote.equalsIgnoreCase("single")) ? DEFAULT_RECORDCOUNT : RandomUtils.nextInt(2, 5);
        query.setParameter("recordCount", numberOfQuotes);
        try {
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Resource>  getQuoteResources(int jobReference, Boolean isContractorAdmin) {
        String sql = GET_PORTAL_RESOURCE_BASE_SQL;
        sql = sql + "INNER JOIN %portaldb.vw_QuoteApprovalScenarios qas ON qas.ResourceId = r.EpochId "
                + " WHERE qas.FaultReference = :jobReference"
                + AND_APPLICATION_USER_ACTIVE ;

        sql = isContractorAdmin ? sql + " AND au.UserProfileId = 115 " : sql ;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getQuoteResources: " + sql);
        Query query = entityManager.createNativeQuery(sql, Resource.class);
        query.setParameter("jobReference", jobReference);
        try {

            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }
}
