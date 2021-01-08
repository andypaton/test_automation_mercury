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
import mercury.helpers.DbUtilityHelper;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfigV2.class)
public class DbHelperParts {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private DbUtilityHelper dbUtilityHelper;
    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;
    @Autowired private NamedParameterJdbcTemplate jdbc_portal;

    /**
     * Using the below query we are placing a parts order when the order doesn't have any part order and iPad isn't available.
     * Inserts the part request row into portaldb.ios.tblEpochNewPartsRequest table
     * @param jobReference
     * @param resourceID
     */
    public void insertInToNewPartsRequest(Integer jobReference, int resourceID) {
        String sql = "INSERT INTO %iosdb.tblEpochNewPartsRequest"
                + " (fld_int_iPADID, fld_int_jobid, fld_dat_datePlaced, fld_str_orderNumber, fld_int_resourceID, fld_int_WSimportStatus, fld_int_EpochImportStatus,"
                + " fld_int_requiresApproval, fld_int_jobType, fld_int_childProject, fld_bit_isTruckStockReplenishment, uuid, fld_str_errorMsg,"
                + " fld_dat_dateEnteredToTable)"
                + " VALUES (0, %d, GETUTCDATE(), NULL, %d, 2, 50, 0, 1, 0, 0, NULL, NULL, GETUTCDATE())";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, jobReference, resourceID);
        logger.debug("insertInToNewPartsRequest: " + sql);
        try {
            jdbc_helpdesk.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Exception " + e.getMessage());
        }
    }

    /**
     * @param jobId
     * @return Part request ID
     */
    public Integer getPartRequestID(Integer jobId) {
        String sql = "SELECT fld_int_id FROM %iosdb.tblEpochNewPartsRequest "
                + " WHERE fld_int_jobid = :jobId";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobId", jobId);

        logger.debug("getPartRequestID: " + sql.replace(":jobId", String.valueOf(jobId)));
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Using the below query we are placing a parts order when the order doesn't have any part order and iPad isn't available.
     * Inserts the part request line row into portaldb.ios.tblEpochNewPartsRequestLine table
     * @param partRequestID
     * @param partCode
     * @param quantity
     * @param supplierID
     * @param notes
     * @param unitPrice
     * @param partDescription
     * @param resourceID
     */
    public void insertInToNewPartsRequestLine(Integer partRequestID, String partCode, Integer quantity, String supplierID, String notes, Float unitPrice, String partDescription, int resourceID) {
        String sql = "INSERT INTO %iosdb.tblEpochNewPartsRequestLine"
                + " (fld_int_requestID, fld_str_partCode, fld_int_qty, fld_str_supplierID, fld_int_deliveryMethodID, fld_bit_newPart, fld_str_note,"
                + " fld_Str_EmailFax, fld_str_supplierPartNumber, fld_str_ManufacturerReference, fld_str_model, fld_str_serialNumber, fld_flt_unitPrice,"
                + " fld_str_partDescription, fld_int_resourceID, fld_dat_dateAdded, fld_int_deliveryAddressType, fld_int_poPriorityType, fld_int_supplierBranchID,"
                + " uuid, fld_bit_ShouldSourceFromOriginalEquipmentManufacturer)"
                + " VALUES (%d, '%s', %d, '%s', 1, 1, '%s', '', '', '', '', '', %f, '%s', %d, GETUTCDATE(), 1, 1, 0, NULL, 0)";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, partRequestID, partCode, quantity, supplierID, notes, unitPrice, partDescription, resourceID);
        logger.debug("insertInToNewPartsRequestLine: " + sql);
        try {
            jdbc_helpdesk.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Exception " + e.getMessage());
        }
    }

    public void updatePoStatus(int jobReference, String description, boolean isCapitalEquipment) {

        String sql = "UPDATE %portaldb.tblPO "
                + "SET fld_int_POStatusID = (SELECT fld_int_ID FROM %portaldb.tblPOStatus WHERE fld_str_Description = '%s') "
                + ",fld_dat_CompleteDate = '1899-12-30 00:00:00.000' "
                + ",fld_dat_CompleteTime = '1899-12-30 00:00:00.000' "
                + ",fld_int_AuthorisedBy = -1 "
                + ",fld_str_CompletedBy = 'Automation Test' "
                + ",fld_int_SupplementaryParentPOID = 0 "
                + "WHERE fld_int_HelpdeskFaultId = %d";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = String.format(sql, description, jobReference);

        if (isCapitalEquipment) sql = sql.replace("WHERE fld_int_HelpdeskFaultId", ",fld_int_Budget = 22 WHERE fld_int_HelpdeskFaultId"); // update Budget to CAPEX - Capital

        logger.debug("updatePoStatus: " + sql);
        jdbc_portal.update(sql, new MapSqlParameterSource());
    }

    public Map<String, Object> getPartsOrderDetails(Integer jobReference) {
        String sql = "SELECT TOP(1) po.PONumber, Supplier, Type, po.Priority, po.RaisedBy, FundingRouteAlias, po.DateRaised, RequiredBy"
                + " FROM %portaldb.uvw_PartsOrders po"
                + " JOIN %portaldb.uvw_PartsOrdersAwaitingApproval poaa ON po.PONumber = poaa.PONumber"
                + " WHERE JobRef = :jobReference ORDER BY DateRaised DESC";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);

        logger.debug("getPartsOrderDetails: " + sql.replace(":jobReference", jobReference.toString()));
        try {
            return jdbc_portal.queryForMap(sql, params);
        } catch (Exception e) {
            return null;
        }
    }

}
