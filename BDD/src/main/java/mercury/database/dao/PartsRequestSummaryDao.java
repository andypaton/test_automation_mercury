package mercury.database.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.PartsRequestSummary;
import mercury.helpers.DbUtilityHelper;

@Repository
public class PartsRequestSummaryDao {

    @Autowired private EntityManager entityManager;
    @Autowired private DbUtilityHelper dbUtilityHelper;

    private static final String GET_NEW_PART_REQUEST_SUMARY_SQL = "SELECT TOP(1) prh.fld_int_ID HeaderId, prl.fld_int_ID LineId, prh.fld_int_HelpDeskFaultID JobReference " +
            "  , prl.fld_int_POID POId " +
            "  FROM %portaldb.tblPartsRequestHeader prh INNER JOIN %portaldb.tblPartsRequestLine prl " +
            "  ON prh.fld_int_ID = prl.fld_int_PartsRequestHeaderID " +
            "  WHERE prh.fld_int_HelpDeskFaultID = :jobReference " +
            "  AND prl.fld_str_SupplierCode = :supplierCode " +
            "  AND prl.fld_str_NewPartSupplierReference = :partNumber " +
            "  AND prl.fld_str_NewPartManufacturerReference = :manfRef " +
            "  AND prl.fld_str_NewPartAssetModel = :model " +
            "  AND prl.fld_str_NewPartDescription = :partDescription " +
            "  AND prl.fld_str_NewPartAssetSerialNumber = :serialNumber " +
            "  AND (prl.fld_cur_NewPartUnitPrice = convert(decimal(10, 2),:unitPrice) " +
            "   OR convert(decimal(10, 2), :unitPrice) = 0.00) " +
            "  AND prl.fld_flt_Quantity = :quantity" ;

    private static final String GET_EXISITING_PART_REQUEST_SUMARY_SQL = "SELECT TOP(1) prh.fld_int_ID HeaderId, prl.fld_int_ID LineId, prh.fld_int_HelpDeskFaultID JobReference " +
            "  , prl.fld_int_POID POId " +
            "  FROM %portaldb.tblPartsRequestHeader prh INNER JOIN %portaldb.tblPartsRequestLine prl " +
            "  ON prh.fld_int_ID = prl.fld_int_PartsRequestHeaderID " +
            "  WHERE prh.fld_int_HelpDeskFaultID = :jobReference " +
            "  AND prl.fld_str_SupplierCode = :supplierCode " +
            "  AND prl.fld_str_NewPartSupplierReference IS NULL " +
            "  AND prl.fld_str_NewPartManufacturerReference IS NULL " +
            "  AND prl.fld_str_NewPartAssetModel IS NULL " +
            "  AND prl.fld_str_NewPartDescription IS NULL " +
            "  AND prl.fld_str_NewPartAssetSerialNumber IS NULL " +
            "  AND prl.fld_cur_NewPartUnitPrice IS NULL " +
            "  AND prl.fld_flt_Quantity = :quantity" ;



    public PartsRequestSummary getPartRequestSummary(Integer jobReference, String supplierCode, String partNumber, String manfRef, String model,
            String partDescription, String serialNumber, Float unitPrice, Integer quantity, boolean newPart) {
        if(newPart == true) {
            return getNewPartRequestSummary(
                    jobReference,
                    supplierCode,
                    partNumber,
                    manfRef,
                    model,
                    partDescription,
                    serialNumber,
                    unitPrice,
                    quantity);
        } else {
            return getExistingPartRequestSummary(
                    jobReference,
                    supplierCode,
                    quantity);
        }
    }

    public PartsRequestSummary getNewPartRequestSummary(Integer jobReference, String supplierCode, String partNumber, String manfRef, String model,
            String partDescription, String serialNumber, Float unitPrice, Integer quantity) {
        String sql = GET_NEW_PART_REQUEST_SUMARY_SQL;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        try {
            Query query = entityManager.createNativeQuery(sql, PartsRequestSummary.class);
            query.setParameter("jobReference", jobReference);
            query.setParameter("supplierCode", supplierCode);
            query.setParameter("partNumber", partNumber.trim());
            query.setParameter("manfRef", manfRef.trim());
            query.setParameter("model", model.trim());
            query.setParameter("partDescription", partDescription.trim());
            query.setParameter("serialNumber", serialNumber.trim());
            query.setParameter("unitPrice", unitPrice);
            query.setParameter("quantity", quantity);
            return (PartsRequestSummary) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public PartsRequestSummary getExistingPartRequestSummary(Integer jobReference, String supplierCode, Integer quantity) {
        String sql = GET_EXISITING_PART_REQUEST_SUMARY_SQL;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        try {
            Query query = entityManager.createNativeQuery(sql, PartsRequestSummary.class);
            query.setParameter("jobReference", jobReference);
            query.setParameter("supplierCode", supplierCode);
            query.setParameter("quantity", quantity);
            return (PartsRequestSummary) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
