package mercury.database.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.PartCode;
import mercury.helpers.DbUtilityHelper;

@Repository
public class PartCodeDao {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private DbUtilityHelper dbUtilityHelper;

    private static final String GET_RANDOM_PART_SQL = "SELECT TOP(1) PartCode.fld_int_ID Id, " +
            "PartCode.fld_str_PartCode PartCode, " +
            "fld_str_Description Description, " +
            "fld_str_ManufacturerRef ManufacturerRef, " +
            "fld_cur_CostEach UnitPrice, " +
            "fld_str_SupplierCode SupplierCode, " +
            "TCS.Name  SupplierName " +
            "FROM %portaldb.tblPartCodes PartCode  INNER JOIN %portaldb.tblpricebook PriceBook " +
            "ON PartCode.fld_str_PartCode = PriceBook.fld_str_PartCode " +
            "INNER JOIN %portaldb.tblHelpDeskResource hdr ON PriceBook.fld_int_ResourceId = hdr.fld_int_ID " +
            "INNER JOIN %helpdeskdb.ApplicationUser ap ON ap.UserName = hdr.fld_str_LogOn " +
            "LEFT JOIN %portaldb.tblTCodeSuppliers TCS " +
            "ON LTRIM(RTRIM(PriceBook.fld_str_SupplierCode)) COLLATE DATABASE_DEFAULT = LTRIM(RTRIM(TCS.Code)) " +
            "WHERE fld_int_PartCodeCategoryID NOT IN (133) " +
            "AND fld_bit_Enabled = 1 " +
            "AND hdr.fld_bit_OnlineInvoicingActive = 1 " +
            "AND fld_cur_CostEach != '0.00' " +
            "AND fld_int_PartCodeCategoryID IN (SELECT fld_int_id FROM %portaldb.tblPartCodeCategory WHERE fld_bit_enabledForPartsRequest=1) " +
            "AND PriceBook.fld_str_SupplierCode NOT IN (SELECT hdr.fld_str_SupplierID  FROM %edidb.SupplierEdiSetting edi INNER JOIN %portaldb.tblHelpDeskResource hdr ON edi.PortalHelpDeskResourceId = hdr.fld_int_ID) ";

    private static final String GET_RANDOM_PART_WIHTIN_PRICE_RANGE_SQL = "SELECT TOP(1) PartCode.fld_int_ID Id, " +
            "PartCode.fld_str_PartCode PartCode, " +
            "fld_str_Description Description, " +
            "fld_str_ManufacturerRef ManufacturerRef, " +
            "fld_cur_CostEach UnitPrice, " +
            "fld_str_SupplierCode SupplierCode, " +
            "TCS.Name  SupplierName " +
            "FROM %portaldb.tblPartCodes PartCode  INNER JOIN %portaldb.tblpricebook PriceBook " +
            "ON PartCode.fld_str_PartCode = PriceBook.fld_str_PartCode "  +
            "LEFT JOIN %portaldb.tblTCodeSuppliers TCS " +
            "ON LTRIM(RTRIM(PriceBook.fld_str_SupplierCode)) COLLATE DATABASE_DEFAULT = LTRIM(RTRIM(TCS.Code)) " +
            "WHERE fld_cur_CostEach BETWEEN :minUnitPrice AND :maxUnitPrice " +
            "AND fld_int_PartCodeCategoryID NOT IN (133) " +
            "AND fld_bit_Enabled = 1 " +
            "AND fld_int_PartCodeCategoryID IN (SELECT fld_int_id FROM %portaldb.tblPartCodeCategory WHERE fld_bit_enabledForPartsRequest=1) " +
            "AND PriceBook.fld_str_SupplierCode NOT IN (SELECT hdr.fld_str_SupplierID FROM %edidb.SupplierEdiSetting edi INNER JOIN %portaldb.tblHelpDeskResource hdr ON edi.PortalHelpDeskResourceId = hdr.fld_int_ID) " +
            "AND fld_str_SupplierCode NOT IN ('SO:C4448') " + // supplier SO:C4448 is incorrectly set up
            "ORDER BY NEWID()" ;

    @Autowired private EntityManager entityManager;


    public PartCode getRandomPartCode() {
        String jpql = GET_RANDOM_PART_SQL;
        jpql = dbUtilityHelper.dbNameSchemaReplacement(jpql);
        logger.debug("getRandomPartCode: " + jpql);
        try {
            Query query =  entityManager.createNativeQuery(jpql, PartCode.class);
            return (PartCode) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public PartCode getRandomCapitalPartCode() {
        String jpql = GET_RANDOM_PART_SQL;
        jpql = jpql.replaceAll("ORDER BY NEWID", " AND PartCode.fld_bit_isCapitalEquipment = 1 ORDER BY NEWID");
        jpql = dbUtilityHelper.dbNameSchemaReplacement(jpql);
        logger.debug("getRandomCapitalPartCode: " + jpql);
        try {
            Query query = entityManager.createNativeQuery(jpql, PartCode.class);
            return (PartCode) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public PartCode getRandomNonCapitalPartCode() {
        String jpql = GET_RANDOM_PART_SQL;
        jpql = jpql.replaceAll("ORDER BY NEWID", " AND PartCode.fld_bit_isCapitalEquipment = 0 ORDER BY NEWID");
        jpql = dbUtilityHelper.dbNameSchemaReplacement(jpql);
        logger.debug("getRandomNonCapitalPartCode: " + jpql);
        try {
            Query query = entityManager.createNativeQuery(jpql, PartCode.class);
            return (PartCode) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public PartCode getRandomPartCodeBetweenValues(Double minUnitPrice, Double maxUnitPrice) {
        String jpql = GET_RANDOM_PART_WIHTIN_PRICE_RANGE_SQL;
        jpql = dbUtilityHelper.dbNameSchemaReplacement(jpql);
        logger.debug("getRandomPartCodeBetweenValues: " + jpql);
        try {
            Query query =  entityManager.createNativeQuery(jpql, PartCode.class);
            query.setParameter("minUnitPrice", minUnitPrice);
            query.setParameter("maxUnitPrice", maxUnitPrice);
            return (PartCode) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public PartCode getRandomNonCapitalPartCodeBetweenValues(Double minUnitPrice, Double maxUnitPrice) {
        String jpql = GET_RANDOM_PART_WIHTIN_PRICE_RANGE_SQL;
        jpql = jpql.replaceAll("ORDER BY NEWID", " AND PartCode.fld_bit_isCapitalEquipment = 0 ORDER BY NEWID");
        jpql = dbUtilityHelper.dbNameSchemaReplacement(jpql);
        logger.debug("getRandomNonCapitalPartCodeBetweenValues: " + jpql);
        try {
            Query query = entityManager.createNativeQuery(jpql, PartCode.class);
            query.setParameter("minUnitPrice", minUnitPrice);
            query.setParameter("maxUnitPrice", maxUnitPrice);
            return (PartCode) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public PartCode getRandomCapitalPartCodeBetweenValues(Double minUnitPrice, Double maxUnitPrice) {
        String jpql = GET_RANDOM_PART_WIHTIN_PRICE_RANGE_SQL;
        jpql = jpql.replaceAll("ORDER BY NEWID", " AND PartCode.fld_bit_isCapitalEquipment = 1 ORDER BY NEWID");
        jpql = dbUtilityHelper.dbNameSchemaReplacement(jpql);
        logger.debug("getRandomCapitalPartCodeBetweenValues: " + jpql);
        try {
            Query query =  entityManager.createNativeQuery(jpql, PartCode.class);
            query.setParameter("minUnitPrice", minUnitPrice);
            query.setParameter("maxUnitPrice", maxUnitPrice);
            return (PartCode) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
