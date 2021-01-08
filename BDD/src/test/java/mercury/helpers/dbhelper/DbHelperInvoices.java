package mercury.helpers.dbhelper;

import static mercury.helpers.Globalisation.LOCALE;
import static mercury.helpers.Globalisation.MS_SHORT_DATE;
import static mercury.helpers.Globalisation.localize;
import static mercury.helpers.StringHelper.quote;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.object.GenericStoredProcedure;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mercury.database.config.DbConfigV2;
import mercury.helpers.DbUtilityHelper;
import mercury.helpers.FileHelper;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfigV2.class)
public class DbHelperInvoices {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private NamedParameterJdbcTemplate jdbc_portal;
    @Autowired
    private NamedParameterJdbcTemplate jdbc_helpdesk;
    @Autowired private DataSource helpdeskDataSource;
    @Autowired private DbUtilityHelper dbUtilityHelper;


    public boolean isNewInvoiceCreated(String invoiceNumber) {
        String sql =  "SELECT COUNT(1) "
                + "FROM %portaldb.tblcfSupplierInvoiceHeader "
                + "WHERE (fld_str_InvoiceNumber = ':invoiceNumber') "
                + "AND fld_int_SupplierInvoiceStatusId = 1";
        sql = sql.replace(":invoiceNumber", String.valueOf(invoiceNumber));
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("newInvoiceCreated: " + sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        int count = jdbc_portal.queryForObject(sql, params, Integer.class);
        return count > 0;
    }


    public Integer getInvoiceStateIdForInvoice(String invoiceNumber) {
        String sql = "SELECT TOP (1) fld_int_SupplierInvoiceStatusId " +
                " FROM  %portaldb.tblcfSupplierInvoiceHeader " +
                " WHERE fld_str_InvoiceNumber = :invoiceNumber";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getJobStatusId: " + sql.replace(":invoiceNumber", "'" + invoiceNumber + "'"));
        params.addValue("invoiceNumber", invoiceNumber);
        return jdbc_portal.queryForObject(sql, params, Integer.class);
    }

    public Integer getInvoiceStateIdForOrderRef(String orderRef) {
        String sql = "SELECT TOP (1) fld_int_SupplierInvoiceStatusId " +
                " FROM  %portaldb.tblcfSupplierInvoiceHeader " +
                " WHERE fld_str_PO_PONumber = :orderRef";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getJobStatusId: " + sql.replace(":orderRef", "'" + orderRef + "'"));
        params.addValue("orderRef", orderRef);
        return jdbc_portal.queryForObject(sql, params, Integer.class);
    }

    public Integer getInvoiceStateIdForName(String stateName) {
        String sql = "SELECT TOP (1) [fld_int_Id] invoiceStateId " +
                " FROM %portaldb.tblcfSupplierInvoiceStatus " +
                " WHERE fld_str_Name = :stateName";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("stateName: " + sql.replace(":stateName","'" +stateName + "'"));
        params.addValue("stateName", stateName);
        return jdbc_portal.queryForObject(sql, params, Integer.class);
    }

    /**
     * Invoices with 2 types of action (1.Values Changed, 2.No Approver) will be awaiting 'Accounts payable' user review.
     * @return
     */
    public String getRandomAPReviewOrderRefNumber(String action) {
        String sql = "SELECT TOP (1) invoices.fld_str_po_poNumber OrderRef" +
                "  FROM %portaldb.tblcfSupplierInvoiceHeader invoices" +
                "  INNER JOIN %portaldb.tblPO AS PO ON Invoices.fld_int_PO_ID = PO.fld_int_ID" +
                "  INNER JOIN %portaldb.tblTCodeSuppliers AS Suppliers ON PO.fld_str_SupplierID = Suppliers.Code" +
                "  WHERE fld_int_SupplierInvoiceStatusId = (SELECT fld_int_id FROM %portaldb.tblcfSupplierInvoiceStatus WHERE fld_str_Name = ':supplierInvoiceStatus')" +
                "  AND ISNULL(fld_bit_AccountsPayableSubmitted,0) = :accountsPayableSubmitted " +
                "  ORDER BY NEWID() ";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        if ("Values Changed".equals(action)) {
            sql = sql.replace(":supplierInvoiceStatus", "Awaiting AP Review").replace(":accountsPayableSubmitted", "0");

        } else if ("No Approver".equals(action)) {
            sql = sql.replace(":supplierInvoiceStatus", "Awaiting AP Review").replace(":accountsPayableSubmitted", "1");
        }
        logger.debug("getRandomAPReviewOrderRefNumber: " + sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_portal.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Invoices with 2 types of action (1.Values Changed, 2.No Approver) will be
     * awaiting 'Accounts payable' user review.
     * @return
     */
    public String getAliasFundingRoute(String name) {
        String sql = String.format("SELECT Alias FROM FundingRoute WHERE Name = '%s'", name);
        logger.debug("getAliasFundingRoute: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getAliasBudgetRoute(String name) {
        String sql = String.format("SELECT Alias FROM Budget WHERE AbbreviatedName = '%s'", name);
        logger.debug("getAliasBudgetRoute: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getInvoiceId(String supplierCode) {
        String sql = "SELECT TOP (1) fld_int_Id FROM Portal.tblcfSupplierInvoiceHeader "
                + "WHERE fld_str_SupplierId = '%s' ORDER BY NEWID()";

        sql = String.format(sql, supplierCode);
        logger.debug("getInvoiceNumber: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getInvoiceNumberOrderReference(Integer jobReference, String status) {
        String sql = "SELECT fld_str_InvoiceNumber InvoiceNumber, fld_str_PO_PONumber OrderReference" +
                "  FROM %portaldb.tblcfSupplierInvoiceHeader sih" +
                "  INNER JOIN %portaldb.tblPO AS PO ON sih.fld_str_PO_PONumber = PO.fld_str_PONumber" +
                "  WHERE PO.fld_int_HelpdeskFaultID = :jobReference ";
        sql = ("Invoice Awaiting Approval").equalsIgnoreCase(status) ? sql + " AND fld_int_SupplierInvoiceStatusId = 3 " : sql + " AND fld_int_SupplierInvoiceStatusId = 10 ";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getInvoiceNumber: " + sql.replace(":jobReference", jobReference.toString() ));
        params.addValue("jobReference", jobReference);
        return jdbc_portal.queryForList(sql, params).get(0);
    }

    public BigDecimal getWorksOrderAmount(Integer jobReference) {
        String sql = "SELECT TOP(1) fld_cur_CostExpected FROM %portaldb.tblPO WHERE fld_int_HelpdeskFaultId = %d AND fld_str_WorkOrderPDF IS NOT NULL ORDER BY fld_dat_DateRaised DESC";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, jobReference);

        logger.debug("getWorksOrderAmount: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, new MapSqlParameterSource(), BigDecimal.class);
        } catch (Exception e) {
            return new BigDecimal(0);
        }
    }

    public Integer getOrderRefNumber(int jobReference, String userName) {

        String userNameToPass = userName.contains("SO_") ? "%" + userName.split("_")[1].trim() + "%" : userName;
        String sql = "SELECT TOP(1) PO.fld_str_PONumber from %portaldb.tblPO PO" +
                "  INNER JOIN %portaldb.tblHelpDeskFault Fault ON PO.fld_int_HelpdeskFaultID = Fault.fld_int_ID" +
                "  INNER JOIN %portaldb.tblHelpDeskResource AS Resources  ON PO.fld_str_SupplierID = Resources.fld_str_SupplierID" +
                "  INNER JOIN %helpdeskdb.Resource r ON Resources.fld_int_ID = r.EpochId" +
                "  INNER JOIN %helpdeskdb.ApplicationUser au ON r.Id = au.ResourceId" +
                "  WHERE PO.fld_int_POStatusID = 7 AND PO.fld_bit_FullyInvoiced = 0" +
                "  AND ISNULL(fld_int_SupplementaryParentPOID,0) = 0 AND Fault.fld_int_ID = :jobReference" +
                "  AND (Resources.fld_str_LogOn LIKE :userNameToPass OR au.UserName LIKE :userNameToPass ) ";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getOrderRefNumber: " + sql.replaceAll(":jobReference", String.valueOf(jobReference)).replaceAll(":userNameToPass", "'" + userNameToPass + "'"));
        params.addValue("jobReference", jobReference);
        params.addValue("userNameToPass", userNameToPass);
        try{
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getOrderRefNumberForNewSupplierInvoice(int jobReference, Integer resourceId, String jobStatus) {
        String sql = "SELECT TOP(1) PO.fld_str_PONumber FROM %portaldb.tblPO PO" +
                "  INNER JOIN %portaldb.tblHelpDeskFault Fault ON PO.fld_int_HelpdeskFaultID = Fault.fld_int_ID" +
                "  INNER JOIN %portaldb.tblHelpDeskResource AS Resources  ON PO.fld_str_SupplierID = Resources.fld_str_SupplierID" +
                "  INNER JOIN %helpdeskdb.Resource r ON Resources.fld_int_ID = r.EpochId" +
                "  INNER JOIN %helpdeskdb.ApplicationUser au ON au.ResourceId = r.Id" +
                "  LEFT JOIN %portaldb.tblcfSupplierInvoiceHeader sih ON PO.fld_str_PONumber = sih.fld_str_PO_PONumber " +
                "  LEFT JOIN %portaldb.tblcfSupplierInvoiceLine sil ON sih.fld_int_Id = sil.fld_int_SupplierInvoiceHeaderId " +
                "  WHERE PO.fld_int_POStatusID = 7 AND PO.fld_bit_FullyInvoiced = 0" +
                "  AND ISNULL(PO.fld_int_SupplementaryParentPOID,0) = 0 AND Fault.fld_int_ID = :jobReference" +
                "  AND r.Id = :resourceId";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        String groupBy = " GROUP BY PO.fld_str_PONumber ";

        if (jobStatus.contains("With No Invoice")) {
            sql = sql + groupBy + " HAVING COUNT(sih.fld_str_PO_PONumber) = 0";
        } else if (jobStatus.contains("With Existing Invoice")) {
            sql = sql + groupBy + " HAVING COUNT(sih.fld_str_PO_PONumber) >= 1";
        }

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getOrderRefNumber: " + sql.replaceAll(":jobReference", String.valueOf(jobReference)).replaceAll(":resourceId", "'" + resourceId + "'"));
        params.addValue("jobReference", jobReference);
        params.addValue("resourceId", resourceId);
        try{
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getSupplierInvoiceHeader(int jobReference){
        String sql = "SELECT sih.* FROM %portaldb.tblcfSupplierInvoiceHeader sih JOIN %portaldb.tblPO po ON po.fld_str_PONumber = sih.fld_str_PO_PONumber WHERE fld_int_HelpdeskFaultID = %d";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, jobReference);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getSupplierInvoiceHeader: " + sql);

        try {
            return jdbc_portal.queryForList(sql, params).get(0);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

    public Integer getOrderRefNumber(int jobReference) {
        String sql = "SELECT TOP (1) fld_str_PONumber from %portaldb.tblPO WHERE fld_int_HelpdeskFaultID = :jobReference";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        logger.debug("getOrderRefNumber: " + sql.replaceAll(":jobReference", String.valueOf(jobReference)));
        try{
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getPONumberfromPPM(Integer jobReference) {
        String sql = "SELECT po.fld_str_PONumber poNumber FROM %portaldb.tblProjectHeader ph" +
                "  INNER JOIN %portaldb.tblPO po ON ph.fld_int_ID = po.fld_int_ProjectHeaderID" +
                "  WHERE ph.fld_int_ppmscheduleref = :jobReference";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getInvoiceNumber: " + sql.replace(":jobReference", jobReference.toString() ));
        params.addValue("jobReference", jobReference);
        try{
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> invoiceRejectionReasons() {
        String sql = "SELECT fld_str_Reason AS RejectionReason "
                + "  FROM %portaldb.tblcfSupplierInvoiceRejectionReason  "
                + "  WHERE fld_bit_AvailableForFirstApproval = 1"
                + "  ORDER BY RejectionReason ASC";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("invoiceRejectionReasons");
        MapSqlParameterSource params = new MapSqlParameterSource();
        try{
            return jdbc_portal.queryForList(sql,params);
        } catch (Exception e) {
            return null;
        }
    }

    public int getUploadedInvoiceTemplateId(Integer jobReference) {
        String sql = "SELECT MAX(it.fld_int_ID)"
                + " FROM %portaldb.tblPO po"
                + " INNER JOIN %portaldb.tblhelpdeskresource r ON po.fld_str_SupplierID = r.fld_str_SupplierID"
                + " INNER JOIN %portaldb.tblInvoiceTemplate it ON r.fld_int_id = it.fld_int_SupplierID"
                + " WHERE po.fld_int_HelpdeskFaultID = :jobReference";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        logger.debug("getUploadedInvoiceTemplateId: " + sql.replace(":jobReference", String.valueOf(jobReference)));
        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return -1;
        }
    }

    public void updateInvoiceTemplateCoordinates(int templateId, String taxWord) {
        StoredProcedure procedure = new GenericStoredProcedure();
        procedure.setDataSource(helpdeskDataSource);
        String sql = "%testdb.usp_Update_InvoiceTemplateCoordinatesInvoicing";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        procedure.setSql(sql);
        procedure.setFunction(false);

        SqlParameter[] parameters = { new SqlParameter(Types.INTEGER), new SqlParameter(taxWord, Types.VARCHAR)};

        procedure.setParameters(parameters);
        procedure.compile();
        logger.debug(String.format("updateInvoiceTemplateCoordinates: execute = %s templateId = %d taxword = %s", sql, templateId, taxWord));
        procedure.execute(templateId, taxWord);
    }

    public void updateInvoiceTemplateCoordinatesInvoicing(int templateId, String taxWord) {
        StoredProcedure procedure = new GenericStoredProcedure();
        procedure.setDataSource(helpdeskDataSource);
        String sql = "%testdb.usp_Update_InvoiceTemplateCoordinatesInvoicing";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        procedure.setSql(sql);
        procedure.setFunction(false);

        SqlParameter[] parameters = { new SqlParameter(Types.INTEGER), new SqlParameter(taxWord, Types.VARCHAR) };

        procedure.setParameters(parameters);
        procedure.compile();
        logger.debug(String.format("updateInvoiceTemplateCoordinates: execute = %s templateId = %d taxword = %s", sql, templateId, taxWord));
        procedure.execute(templateId, taxWord);
    }

    public List<Map<String, Object>> getInvoiceDocumentsFilePath(int orderRef) {
        String sql = "SELECT TOP (1) fld_str_InvoiceFilePath invoiceFilePath , fld_str_JobSheetFilePath jobSheetFilePath" +
                " FROM %portaldb.tblcfSupplierInvoiceHeader WHERE fld_bit_isCancelled <> 1 AND fld_str_PO_PONumber = '%d'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql,orderRef);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getInvoiceDocumentsFilePath:" + sql);
        return jdbc_portal.queryForList(sql, params);
    }

    public Integer getCountOfAllInvoicesInStatus(String status) {
        String sql = "SELECT COUNT(1)"
                + " FROM %portaldb.tblcfSupplierInvoiceHeader sih"
                + " JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id"
                + " WHERE fld_str_Name = :status";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("status", status);
        logger.debug("getCountOfAllInvoicesInStatus: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getInvoicesAwaitingApproval() throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_INVOICES_AWAITING_APPROVAL.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getInvoicesAwaitingApproval: " + sql);
        try {
            return jdbc_portal.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getContractorInvoicesAwaitingApproval() throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_INVOICES_AWAITING_APPROVAL.sql");
        sql = sql.replace("ORDER ", "AND Invoices.fld_str_JobSheetFilePath IS NOT NULL ORDER ");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getInvoicesAwaitingApproval: " + sql);
        try {
            return jdbc_portal.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public void updateToGeneralOrder(String orderRef) {
        assertNotNull(orderRef); // dont try and update DB if we havent received an Order Ref
        String sql = "UPDATE %portaldb.tblPO SET fld_int_POTypeID = 28 WHERE fld_str_PONumber = ':orderRef'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("orderRef", orderRef);
        logger.debug("updateToGeneralOrder: " + sql);
        jdbc_portal.update(sql, params);
    }

    public void assignToEpochId(int invoiceID, int epochId) {
        String sql = "UPDATE %portaldb.tblcfSupplierInvoiceHeader SET fld_int_ReassignedApproverId = :epochId WHERE fld_int_id = :invoiceId";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("epochId", epochId);
        params.addValue("invoiceId", invoiceID);
        logger.debug("assignToEpochId: " + sql);
        jdbc_portal.update(sql, params);
    }

    @SuppressWarnings({ "unchecked", "rawtypes"})
    public List<Map<String, Object>> getAllInvoicesAwaitingApproval(int awaitingApprovalStatusId, String dateFormat, String locale) throws Exception {
        StoredProcedure procedure = new GenericStoredProcedure();
        procedure.setDataSource(helpdeskDataSource);

        String sql = "%testdb.usp_GetInvoicesAwaitingApproval";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        procedure.setSql(sql);
        procedure.setFunction(false);

        SqlParameter[] parameters = { new SqlParameter(awaitingApprovalStatusId, Types.INTEGER),
                new SqlParameter(dateFormat, Types.VARCHAR),
                new SqlParameter(locale, Types.VARCHAR)
        };
        procedure.setParameters(parameters);
        procedure.compile();

        try {
            Map<String, Object> out = procedure.execute(awaitingApprovalStatusId, dateFormat, locale);
            logger.debug(
                    "usp_GetInvoicesAwaitingApproval: execute " + sql
                    + " @AwaitingApprovalStatusId = "
                    + " @dateFormat = "
                    + " @locale = "
                    + awaitingApprovalStatusId, dateFormat, locale);
            List<Map<String, Object>> result = ((ArrayList) out.get("#result-set-1"));
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getCountOfAllCreditNotesInStatus(String status) {
        String sql = "SELECT COUNT(1)"
                + " FROM %portaldb.tblcfSupplierCreditNoteHeader scnh"
                + " JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id"
                + " WHERE scns.fld_str_DisplayName = :status";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("status", status);
        logger.debug("getCountOfAllCreditNotesInStatus: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getAllCreditNotesAwaitingApproval() {
        String sql =  "(SELECT fld_str_CreditNoteNumber CreditNum, FORMAT(DATEADD(hh, 1, fld_dat_CreditNoteDate), 'M/d/yyyy') CreditDate,"
                + " scnt.fld_str_Description Type, po.[Supplier Name] Supplier, sih. fld_str_InvoiceNumber InvNum, sih.fld_str_PO_PONumber OrderRef,"
                + " scnh.fld_cur_NetAmount Net, scnh.fld_cur_TaxAmount Tax FROM %portaldb.tblcfSupplierCreditNoteHeader scnh"
                + " JOIN %portaldb.tblcfSupplierInvoiceHeader sih ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id"
                + " JOIN %portaldb.vw_purchase_order po ON po.[PO No] = sih.fld_str_PO_PONumber"
                + " JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id"
                + " LEFT JOIN %portaldb.tblcfSupplierCreditNoteType scnt ON scnh.fld_int_SupplierCreditNoteTypeId = scnt.fld_int_Id"
                + " WHERE scns.fld_str_DisplayName = 'Awaiting Approval' AND scnh.fld_int_SupplierInvoiceHeaderId IS NOT NULL)"
                + " UNION"
                +  "(SELECT fld_str_CreditNoteNumber CreditNum, FORMAT(DATEADD(hh, 1, fld_dat_CreditNoteDate), 'M/d/yyyy') CreditDate,"
                + " scnt.fld_str_Description Type, hdr.fld_str_Name Supplier, 'N/A' AS InvNum, 'N/A' AS OrderRef,"
                + " scnh.fld_cur_NetAmount Net, scnh.fld_cur_TaxAmount Tax FROM %portaldb.tblcfSupplierCreditNoteHeader scnh"
                + " JOIN %portaldb.tblHelpDeskResource hdr ON scnh.fld_str_SupplierId = hdr.fld_str_SupplierID"
                + " LEFT JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id"
                + " LEFT JOIN %portaldb.tblcfSupplierCreditNoteType scnt ON scnh.fld_int_SupplierCreditNoteTypeId = scnt.fld_int_Id"
                + " LEFT JOIN %portaldb.tblcfSupplierInvoiceHeader sih ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id"
                + " WHERE scns.fld_str_DisplayName = 'Awaiting Approval' AND scnh.fld_int_SupplierInvoiceHeaderId IS NULL)"
                + " ORDER By OrderRef";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getAllCreditNotesAwaitingApproval: " + sql);
        try{
            return jdbc_portal.queryForList(sql,params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getAllInvoicesAndCreditNotesAwaitingReview() throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_ALL_INVOICES_AND_CREDIT_NOTES_ON_INVOICES_AND_CREDITS_AWAITING_REVIEW_PAGE.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getAllInvoicesAndCreditNotesAwaitingReview: " + sql);
        try {
            return jdbc_portal.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getInvoicesAndCreditNotesAwaitingReviewData(String date) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_INVOICES_AND_CREDIT_NOTES_ON_INVOICES_AND_CREDITS_AWAITING_REVIEW_PAGE_DATA.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("date", date);
        logger.debug("getAllInvoicesAndCreditNotesAwaitingReview: " + sql);
        try {
            return jdbc_portal.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getCountOfSubmittedInvoicesAndCreditsWithInLast30Days() throws Exception {
        String sql = "SELECT COUNT(1) FROM %testdb.uvw_SubmittedInvoicesAndCredits";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getCountOfSubmittedInvoicesAndCreditsWithInLast30Days: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getCountOfSubmittedCreditsWithInLast30Days() {
        String sql = "SELECT COUNT(1) FROM %portaldb.tblcfSupplierCreditNoteHeader"
                + " WHERE fld_dat_SubmissionDate > DATEADD(DAY, -31, GETUTCDATE()) AND fld_dat_CreditNoteDate> DATEADD(DAY, -31, GETUTCDATE())"
                + " AND fld_int_SupplierCreditNoteTypeId = 1";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getCountOfSubmittedCreditsWithInLast30Days: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getCountOfSubmittedCreditsWithinLast30DaysForSupplier(String userName) {
        String userNameToPass = userName.contains("SO_") ? "%" + userName.split("_")[1].trim() + "%" : userName;
        String sql = "SELECT COUNT(1) FROM %portaldb.tblcfSupplierCreditNoteHeader"
                + " WHERE fld_dat_SubmissionDate > DATEADD(DAY, -31, GETUTCDATE()) AND fld_dat_CreditNoteDate> DATEADD(DAY, -31, GETUTCDATE())"
                + " AND fld_str_SupplierId LIKE :userNameToPass"
                + " AND fld_int_SupplierCreditNoteTypeId = 1";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userNameToPass", userNameToPass);
        logger.debug("getCountOfSubmittedCreditsWithinLast30DaysForSupplier: " + sql.replace(":userNameToPass", "'" + userNameToPass + "'"));
        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getCountOfInvoicesAndCreditsInStatus(String invoiceStatus, String creditNoteStatus) {
        String sql = "SELECT ( SELECT COUNT(1) "
                + " FROM %portaldb.tblcfSupplierCreditNoteHeader scnh "
                + " JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id "
                + " WHERE scns.fld_str_DisplayName = :creditNoteStatus) + "
                + " (SELECT COUNT(1) "
                + " FROM %portaldb.tblcfSupplierInvoiceHeader sih "
                + " JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id "
                + " WHERE fld_str_Name = :invoiceStatus )  + (SELECT COUNT(1) "
                + " FROM %portaldb.tblcfSupplierInvoiceHeader AS Invoices "
                + " INNER JOIN %portaldb.vw_purchase_order porder ON porder.[PO No] = Invoices.fld_str_PO_PONumber "
                + " INNER JOIN %portaldb.tblPO AS PO ON Invoices.fld_int_PO_ID = PO.fld_int_ID "
                + " INNER JOIN %portaldb.tblHelpDeskResource AS hdrSupp ON PO.fld_str_SupplierID = hdrSupp.fld_str_SupplierID "
                + " INNER JOIN %portaldb.tblHelpdeskFault AS Fault ON PO.fld_int_HelpdeskFaultID = Fault.fld_int_ID "
                + " INNER JOIN %portaldb.tblStore AS Store ON Fault.fld_int_StoreID = Store.fld_int_ID "
                + " INNER JOIN %portaldb.tblStoreCluster SC ON store.fld_int_StoreClusterID = SC.fld_int_ID "
                + " LEFT OUTER JOIN %portaldb.tblHelpdeskResource FirstApprover ON FirstApprover.fld_int_Id = Invoices.fld_int_Stage1Approver_HelpDeskResourceId "
                + " LEFT OUTER JOIN %portaldb.tblHelpdeskResource hdr ON hdr.fld_int_ID = SC.fld_int_AreaManagerID "
                + " LEFT OUTER JOIN %portaldb.tblHelpDeskResource hdr2 ON Invoices.fld_int_ReassignedApproverId = hdr2.fld_int_ID  "
                + " WHERE Invoices.fld_int_SupplierInvoiceStatusId = 3 "
                + " AND hdr.fld_str_Name IS NULL) AS TOTALCOUNT ";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("invoiceStatus", invoiceStatus);
        params.addValue("creditNoteStatus", creditNoteStatus);
        logger.debug("getCountOfInvoicesAndCreditsInStatus: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getSubmittedInvoicesAndCreditsForSupplier(String supplier, String orderRef, String dateFormat, String locale) throws Exception {
        String sql = "WITH Invoices AS (SELECT Type, Supplier, RefNum, OrderRef, JobRef, Site, FORMAT(Date,'" + dateFormat + "', '" + locale + "') Date, Net, Tax, Gross, Status,  "
                + " HasCredit FROM %testdb.uvw_SubmittedInvoicesAndCredits  "
                + " WHERE Supplier LIKE :supplier ), "
                + " Consolidated_invoices AS ( "
                + " SELECT 'Consolidated Invoice' AS Type, Resources.fld_str_Name AS Supplier,  "
                + " LTRIM(sih.fld_str_InvoiceNumber) RefNum,  "
                + " ISNULL(sih.fld_str_PO_PONumber, 'N/A') AS OrderRef, "
                + " ISNULL( CAST(hdf.fld_int_ID AS varchar), 'N/A') JobRef,"
                + " ISNULL(IIF(s.fld_bit_VIP = 1 , concat(s.fld_str_Name, ' (VIP) '), s.fld_str_Name) ,'N/A') AS Site, "
                + " FORMAT( sih.fld_dat_InvoiceDate, '" + dateFormat + "', '" + locale + "') Date, "
                + " sih.fld_cur_NetAmount Net, "
                + " sih.fld_cur_VatAmount Tax, "
                + " sih.fld_cur_GrossAmount Gross, "
                + " sis.fld_str_DisplayName Status, "
                + " 'No' AS HasCredit "
                + " FROM %portaldb.tblcfSupplierInvoiceHeader sih "
                + " INNER JOIN %portaldb.tblHelpDeskResource AS Resources ON Resources.fld_str_SupplierID = sih.fld_str_SupplierId "
                + " LEFT JOIN %portaldb.vw_purchase_order po ON po.[PO No] = sih.fld_str_PO_PONumber  "
                + " LEFT JOIN %portaldb.tblHelpdeskFault hdf ON po.FaultID = hdf.fld_int_ID "
                + " LEFT JOIN %portaldb.tblStore s ON hdf.fld_int_StoreID = s.fld_int_ID "
                + " LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id "
                + " WHERE sih.fld_bit_IsConsolidated = 1 "
                + " AND Resources.fld_str_Name LIKE ':supplier' "
                + " AND sih.fld_int_SupplierInvoiceStatusId NOT IN (1,2)), " // To avoid status 'New' and 'Submitted In Error'
                + " Submitted_Invoices_And_Credits AS ( "
                + " SELECT * FROM Invoices "
                + " UNION "
                + " SELECT * FROM Consolidated_invoices ) "
                + " SELECT TOP(1) * FROM Submitted_Invoices_And_Credits "
                + " WHERE OrderRef = :orderRef"
                + " ORDER BY OrderRef ASC, Status";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        // Need to add a like due to things like 'Amigo' or 'Amigo Mobility International, Inc.'
        supplier = "%".concat(supplier).concat("%");
        params.addValue("supplier", supplier);
        params.addValue("orderRef", orderRef);
        logger.debug("getSubmittedInvoicesAndCredits: " + sql);
        try{
            return jdbc_portal.queryForList(sql,params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getSubmittedInvoicesForSupplyOnlyUser(String userName, String dateFormat, String locale) throws Exception {
        String sql = "WITH Submitted_Invoices AS (SELECT DISTINCT ISNULL(sih.fld_str_PO_PONumber, 'N/A') AS OrderRef,"
                + "'Invoice' AS Type, LTRIM(sih.fld_str_InvoiceNumber) AS RefNum, "
                + "FORMAT( sih.fld_dat_InvoiceDate, '" + dateFormat + "', '" + locale + "') Date, "
                + "sih.fld_cur_NetAmount Net, sih.fld_cur_VatAmount Tax, sih.fld_cur_GrossAmount Gross, sis.fld_str_DisplayName Status, 'No' AS HasCredit "
                + "FROM %portaldb.tblcfSupplierInvoiceHeader sih "
                + "LEFT JOIN %portaldb.vw_purchase_order po ON sih.fld_str_PO_PONumber = po.[PO No] "
                + "LEFT JOIN %portaldb.tblPO tpo ON sih.fld_str_PO_PONumber = tpo.fld_str_PONumber "
                + "INNER JOIN %helpdeskdb.Job j ON tpo.fld_int_HelpdeskFaultID = j.JobReference "
                + "INNER JOIN %portaldb.tblHelpdeskFault hdf ON tpo.fld_int_HelpdeskFaultID = hdf.fld_int_ID "
                + "INNER JOIN %portaldb.tblHelpDeskResource hdr ON tpo.fld_str_SupplierID = hdr.fld_str_SupplierID "
                + "INNER JOIN %helpdeskdb.Resource r ON hdr.fld_int_ID = r.EpochId "
                + "LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id "
                + "WHERE po.[Supplier Name] = (SELECT r.Name FROM %helpdeskdb.Resource r INNER JOIN %helpdeskdb.ApplicationUser au ON au.ResourceId = r.Id WHERE au.UserName = :userName) "
                + "AND sih.fld_str_JobSheetFilePath IS NULL "
                + "AND sih.fld_int_SupplierInvoiceStatusId NOT IN (1,2)) "
                + "SELECT TOP (1) * FROM Submitted_Invoices WHERE OrderRef NOT IN (SELECT OrderRef FROM Submitted_Invoices GROUP BY OrderRef HAVING COUNT(*) > 1) ORDER BY NEWID() ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userName", userName);
        logger.debug("getSubmittedInvoicesForSupplyOnlyUser: " + sql.replace(":userName", "'" + userName + "'"));
        try {
            return jdbc_portal.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getRandomSupplierAndOrderRefFromSubmittedInvoicesAndCredits() throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_RANDOM_SUPPLIER_AND_ORDER_REF_FROM_SUBMITTED_INVOICES_AND_CREDITS_PAGE.sql");

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getRandomSupplierAndOrderRefFromSubmittedInvoicesAndCredits: " + sql);
        try {
            return jdbc_portal.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getRandomOrderRefFromAllOrdersForSupplier(String userName) throws Exception {
        String sql = "SELECT TOP(1) po.fld_str_PONumber AS OrderRef FROM %portaldb.tblPO po "
                + "INNER JOIN %portaldb.vw_purchase_order pov on po.fld_str_PONumber = pov.[PO No] "
                + "WHERE pov.[Supplier Name] = (SELECT r.Name FROM Resource r INNER JOIN ApplicationUser au ON au.ResourceId = r.Id WHERE au.UserName = :userName) "
                + "AND pov.[PO No] IS NOT NULL AND po.fld_bit_SupplementaryOrder = 0 AND po.fld_str_WorkOrderPDF IS NOT NULL "
                + "ORDER BY NEWID() ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userName", userName);
        logger.debug("getRandomOrderRefForSupplier: " + sql.replace(":userName", "'" + userName + "'"));
        try {
            return jdbc_portal.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getRandomOrderFromAllOrdersForSupplier(String userName, String dateFormat, String locale) throws Exception {
        String sql = "SELECT TOP(1) po.fld_str_PONumber AS OrderRef, po.fld_dat_DateRaised AS OrderDate, po.fld_cur_CostExpected AS OrderValue, po.fld_str_WorkOrderPDF AS Pdf "
                + "FROM %portaldb.tblPO po "
                + "INNER JOIN %portaldb.vw_purchase_order pov on po.fld_str_PONumber = pov.[PO No] "
                + "WHERE pov.[Supplier Name] = (SELECT r.Name FROM Resource r INNER JOIN ApplicationUser au ON au.ResourceId = r.Id WHERE au.UserName = :userName) "
                + "AND pov.[PO No] IS NOT NULL AND po.fld_bit_SupplementaryOrder = 0 AND po.fld_str_WorkOrderPDF IS NOT NULL "
                + "ORDER BY NEWID() ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userName", userName);
        logger.debug("getRandomOrderRefForSupplier: " + sql.replace(":userName", "'" + userName + "'"));
        try {
            return jdbc_portal.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getRandomOrderRefForSupplier(String userName) throws Exception {
        String sql = "SELECT TOP(1) po.[PO No] AS OrderRef FROM %portaldb.tblcfSupplierInvoiceHeader sih "
                + "INNER JOIN %portaldb.vw_purchase_order po ON sih.fld_str_PO_PONumber = po.[PO No] "
                + "WHERE po.[Supplier Name] = (SELECT r.Name FROM %helpdeskdb.Resource r INNER JOIN %helpdeskdb.ApplicationUser au ON au.ResourceId = r.Id WHERE au.UserName = :userName) "
                + "AND po.[PO No] IS NOT NULL "
                + "AND sih.fld_str_JobSheetFilePath IS NULL "
                + "ORDER BY NEWID() ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userName", userName);
        logger.debug("getRandomOrderRefForSupplier: " + sql.replace(":userName", "'" + userName + "'"));
        try {
            return jdbc_portal.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getCreditNoteStatus(String creditNoteNumber) throws Exception {
        String sql = "SELECT scns.fld_str_DisplayName FROM %portaldb.tblcfSupplierCreditNoteHeader scnh"
                + " JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id"
                + " WHERE scnh.fld_str_CreditNoteNumber = :creditNoteNumber";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("creditNoteNumber", creditNoteNumber);
        logger.debug("getCreditNoteStatus: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getStatusIdOfInvoices(String invoiceStatus) {
        String sql = "SELECT fld_int_Id FROM %portaldb.tblcfSupplierInvoiceStatus WHERE fld_str_Name = :invoiceStatus";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("invoiceStatus", invoiceStatus);
        logger.debug("getStatusIdOfInvoices: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public int getMandatoryInvoiceLineTypes(String lineType) {
        String sql = "SELECT fld_bit_IsMandatory FROM %portaldb.tblcfSupplierInvoiceLineType WHERE fld_str_Description LIKE :lineType";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("lineType", lineType);
        logger.debug("getMandatoryInvoiceLineTypes: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return -1;
        }
    }

    public int getLaborInvoiceLineTypes(String lineType) {
        String sql = "SELECT fld_bit_IsLabor FROM %portaldb.tblcfSupplierInvoiceLineType WHERE fld_str_Description LIKE :lineType ";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        lineType = localize(lineType);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("lineType", lineType);
        logger.debug("getLaborInvoiceLineTypes: " + sql.replace(":lineType", quote(lineType)));
        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return -1;
        }
    }

    public Map<String, Object> getStandaloneCreditNoteRejectionReasonAndNotes(String creditNoteNumber) throws Exception {
        String sql = "SELECT scnrr.fld_str_Reason RejectionReason, scnh.fld_str_RejectionNotes RejectionNotes FROM %portaldb.tblcfSupplierCreditNoteHeader scnh JOIN %portaldb.tblcfSupplierCreditNoteRejectionReason scnrr ON scnh.fld_int_RejectionReasonId = scnrr.fld_int_Id WHERE fld_str_CreditNoteNumber = :creditNoteNumber";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("creditNoteNumber", creditNoteNumber);
        logger.debug("getStandaloneCreditNoteRejectionReasonAndNotes: " + sql);
        try{
            return jdbc_portal.queryForList(sql,params).get(0);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getOrderLinesToInvoice(String poNumber) {
        String sql = "SELECT tl.fld_str_description Description, tl.fld_str_SuppPartNumber PartNumber, tl.fld_flt_Quantity - COALESCE(q1.total,0) Quantity, tl.fld_cur_UnitPrice UnitPrice" +
                "  FROM %portaldb.tblPO po" +
                "  INNER JOIN %portaldb.tblTransactionHeader th ON po.fld_int_id = th.fld_int_POID" +
                "  INNER JOIN %portaldb.tblTransactionLine tl ON th.fld_int_ID = tl.fld_int_TransactionHeaderID" +
                "  LEFT JOIN (" +
                "  SELECT il.fld_int_TransactionLineId, SUM(fld_dec_Quantity) total" +
                "  FROM %portaldb.tblcfsupplierinvoiceheader ih" +
                "  INNER JOIN %portaldb.tblcfsupplierinvoiceline il ON ih.fld_int_id = il.fld_int_SupplierInvoiceHeaderId" +
                "  WHERE ih.fld_str_PO_PONumber = :poNumber" +
                "  AND ih.fld_int_SupplierInvoiceStatusId <> 6" +
                "  GROUP BY il.fld_int_TransactionLineId" +
                "  ) q1 ON tl.fld_int_ID = q1.fld_int_TransactionLineId" +
                "  WHERE po.fld_str_PONumber = :poNumber" +
                "  AND tl.fld_flt_Quantity - COALESCE(q1.total,0) > 0";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("poNumber", poNumber);
        logger.debug("getOrderLinesToInvoice: " + sql);
        try{
            return jdbc_portal.queryForList(sql,params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getInvoiceLineFullfilledStatus(String poNumber) {
        String sql = "SELECT tl.fld_str_Description Description,  tl.fld_bit_InvoiceLineFulfilled lineFullfilled FROM  %portaldb.tblPO po" +
                "  INNER JOIN %portaldb.tblcfSupplierInvoiceHeader AS sih ON po.fld_int_id = sih.fld_int_PO_ID" +
                "  INNER JOIN %portaldb.tblcfSupplierInvoiceLine AS tl ON sih.fld_int_id = tl.fld_int_SupplierInvoiceHeaderId" +
                "  WHERE po.fld_str_PONumber = :poNumber" +
                "  AND tl.fld_bit_InvoiceLineFulfilled = 1";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("poNumber", poNumber);
        logger.debug("getInvoiceLineFullfilledStatus: " + sql);
        try{
            return jdbc_portal.queryForList(sql,params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Double getOrderValue(int jobReference, String jobStatus) {
        String sql = "SELECT TOP(1) PO.fld_cur_CostExpected orderValue FROM %portaldb.tblPO PO" +
                "  INNER JOIN %portaldb.tblHelpDeskFault Fault ON PO.fld_int_HelpdeskFaultID = Fault.fld_int_ID" +
                "  INNER JOIN %portaldb.tblHelpDeskResource AS Resources  ON PO.fld_str_SupplierID = Resources.fld_str_SupplierID" +
                "  LEFT JOIN %portaldb.tblcfSupplierInvoiceHeader sih ON PO.fld_str_PONumber = sih.fld_str_PO_PONumber" +
                "  WHERE PO.fld_int_POStatusID = 7" +
                // POStatusID = Complete
                "  AND PO.fld_bit_FullyInvoiced = 0" +
                "  AND ISNULL(PO.fld_int_SupplementaryParentPOID,0) = 0" +
                "  AND Fault.fld_int_ID = :jobRefernce" +
                "  GROUP BY PO.fld_cur_CostExpected";

        if (jobStatus.contains("With No Invoice")) {
            sql = sql + " HAVING COUNT(sih.fld_str_PO_PONumber) = 0";
        } else if (jobStatus.contains("With Existing Invoice")) {
            sql = sql + " HAVING COUNT(sih.fld_str_PO_PONumber) >= 1";
        }

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobRefernce", jobReference);
        logger.debug("getAssignedCompanyName: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, Double.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Double getOrderCost(String poNumber) {
        String sql = "SELECT fld_cur_OriginalCost orderValue FROM %portaldb.tblPO PO" +
                " WHERE po.fld_str_PONumber = :poNumber";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("poNumber", poNumber);
        logger.debug("getAssignedCompanyName: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, Double.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public int getCompanyId(String poNumber) {
        String sql = "SELECT fld_int_CompanyId FROM %portaldb.tblPO WHERE fld_str_PONumber = :poNumber";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("poNumber", poNumber);
        logger.debug("getCompanyId: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return 0;
        }
    }

    public String getAssignedCompanyName(String poNumber) {
        int companyId = getCompanyId(poNumber);
        String sqlWithCompanyId = "SELECT TOP(1) c.Name companyName" +
                " FROM %helpdeskdb.Company c " +
                " INNER JOIN %portaldb.tblPO po ON c.Id = po.fld_int_CompanyId" +
                " WHERE po.fld_str_PONumber = :poNumber";

        String sqlWithoutCompanyId = "SELECT TOP(1) c.Name companyName" +
                " FROM %helpdeskdb.Company c";

        String sql = companyId == 0 ? sqlWithoutCompanyId : sqlWithCompanyId;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("poNumber", poNumber);
        logger.debug("getAssignedCompanyName: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getAssignedCompanyNameFromJobReference(int jobrefernce) {
        String sql = " SELECT TOP(1) c.Name companyName" +
                " FROM %helpdeskdb.Company c" +
                " INNER JOIN %portaldb.tblPO po ON c.Id = po.fld_int_CompanyId" +
                " WHERE po.fld_int_HelpdeskFaultID = :jobrefernce";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobrefernce", jobrefernce);
        logger.debug("getAssignedCompanyNameFromJobRefernce: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getAssignedAliasNames(String poNumber) {
        String sql = "SELECT TOP(1) ca.Name aliasName" +
                "  FROM %helpdeskdb.Company c" +
                "  INNER JOIN %portaldb.tblpo po ON c.Id = po.fld_int_CompanyId" +
                "  INNER JOIN %helpdeskdb.CompanyAlias ca ON c.Id = ca.CompanyId" +
                "  WHERE po.fld_str_PONumber = :poNumber" +
                "  ORDER BY NEWID()";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("poNumber", poNumber);
        logger.debug("getAssignedAliasNames: " + sql);
        try{
            return jdbc_portal.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getAddedOrderLinesToInvoice(String poNumber) {
        String sql =  "WITH cte AS" +
                " (SELECT fld_int_ID FROM %portaldb.tblPO po1" +
                " WHERE po1.fld_str_PONumber = :poNumber" +
                " )" +
                " SELECT tl.fld_str_description Description " +
                ", tl.fld_str_SuppPartNumber PartNumber " +
                ", tl.fld_str_PartNumber SuppPartNumber " +
                ", tl.fld_flt_Quantity Quantity, tl.fld_cur_UnitPrice UnitPrice," +
                " sil.fld_bit_InvoiceLineFulfilled lineFulfilled, sil.fld_int_ID invoiceLineId FROM %portaldb.tblPO po2" +
                " INNER JOIN %portaldb.tbltransactionheader th ON po2.fld_int_id = th.fld_int_POID" +
                " INNER JOIN %portaldb.tbltransactionline tl ON th.fld_int_id = tl.fld_int_TransactionHeaderID" +
                " AND COALESCE(tl.fld_bit_Active, 1) = 1" +
                " AND tl.fld_flt_Quantity > 0" +
                " LEFT JOIN  %portaldb.tblcfSupplierInvoiceLine sil ON sil.fld_int_TransactionLineId = tl.fld_int_ID" +
                " WHERE (po2.fld_int_SupplementaryParentPOID IN (SELECT fld_int_ID FROM cte)" +
                " OR po2.fld_int_ID IN (SELECT fld_int_ID FROM cte))" +
                " AND po2.fld_bit_FullyInvoiced = 0" +
                " AND fld_bit_InvoiceLineFulfilled IS NOT NULL";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("poNumber", poNumber);
        logger.debug("getAddedOrderLinesToInvoice: " + sql);
        try{
            return jdbc_portal.queryForList(sql,params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public boolean isLineFulfilled(Integer invoiceLineId) {
        String sql =  "SELECT COUNT(1) FROM %portaldb.tbltransactionline tl" +
                "  LEFT JOIN %portaldb.tblcfSupplierInvoiceLine sil ON sil.fld_int_TransactionLineId = tl.fld_int_ID" +
                "  WHERE sil.fld_bit_InvoiceLineFulfilled = 1" +
                "  AND sil.fld_int_Id = :invoiceLineId";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("invoiceLineId", invoiceLineId);
        logger.debug("isLineFulfilled: " + sql.replace(":invoiceLineId", invoiceLineId.toString()));
        int count = jdbc_portal.queryForObject(sql, params, Integer.class);
        return count == 1;
    }

    public String getRandomMaterialsLineType() {
        String sql = "SELECT TOP(1) fld_str_Description " +
                "  FROM %portaldb.tblcfSupplierInvoiceLineType" +
                "  WHERE fld_bit_IsLabor = 0" +
                "  ORDER BY NEWID()";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getMaterialLineType: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getRandomLaborLineType() {
        String sql = "SELECT TOP(1) fld_str_Description " +
                "  FROM %portaldb.tblcfSupplierInvoiceLineType" +
                "  WHERE fld_bit_IsLabor = 1" +
                "  ORDER BY NEWID()";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getLaborLineType: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getContractorInvoiceAwaitingFinalApproval() {
        String sql = "SELECT TOP (1) fld_str_InvoiceNumber [Invoice Ref], FORMAT(fld_dat_InvoiceDate, '" + MS_SHORT_DATE + "', '" + LOCALE + "') [Invoice Date], fld_str_PO_PONumber [Order Ref],"
                + " hdf.fld_int_ID [Job Ref], hdr.fld_str_Name [Initial Approver], fr.Name Budget, po.Supplier, av.SiteCode Site, sd.Division Division,"
                + " CONVERT(NVARCHAR, sih.fld_cur_NetAmount, 0) Net, CONVERT(NVARCHAR, sih.fld_cur_VatAmount, 0) Tax, CONVERT(NVARCHAR, sih.fld_cur_GrossAmount, 0) Gross"
                + " FROM %portaldb.tblcfSupplierInvoiceHeader sih"
                + " LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_ID"
                + " LEFT JOIN %portaldb.tblHelpDeskResource hdr ON sih.fld_int_Stage1Approver_HelpdeskResourceId = hdr.fld_int_ID"
                + " LEFT JOIN %helpdeskdb.Budget b ON sih.fld_int_BudgetId = b.Id"
                + " LEFT JOIN %helpdeskdb.FundingRoute fr ON b.FundingRouteId = fr.Id"
                + " LEFT JOIN %portaldb.uvw_PartsOrders po ON sih.fld_str_PO_PONumber = po.PONumber"
                + " LEFT JOIN %portaldb.tblHelpDeskFault hdf ON po.JobRef = hdf.fld_int_ID"
                + " INNER JOIN %helpdeskdb.Site av ON hdf.fld_int_StoreID = av.Id"
                + " LEFT JOIN  %helpdeskdb.uvw_SitesDivision sd ON sd.SiteId = av.Id"
                + " WHERE sis.fld_str_Name = 'Awaiting Final Approval' AND sih.fld_str_JobSheetFilePath IS NOT NULL"
                + " ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getContractorInvoiceAwaitingFinalApproval: " + sql);
        try{
            return jdbc_portal.queryForMap(sql,params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }



    public List<Map<String, Object>> getInvoicesAwaitingFinalApproval() {
        String sql = "SELECT TOP (1) fld_str_InvoiceNumber [Invoice Ref], FORMAT(fld_dat_InvoiceDate, '" + MS_SHORT_DATE + "', '" + LOCALE + "') [Invoice Date], fld_str_PO_PONumber [Order Ref],"
                + " hdr.fld_str_Name [Initial Approver], fr.Name Budget, po.Supplier, av.SiteCode Site, sd.Division Division,"
                + " CONVERT(NVARCHAR, sih.fld_cur_NetAmount, 0) Net, CONVERT(NVARCHAR, sih.fld_cur_VatAmount, 0) Tax, CONVERT(NVARCHAR, sih.fld_cur_GrossAmount, 0) Gross"
                + " FROM %portaldb.tblcfSupplierInvoiceHeader sih"
                + " LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_ID"
                + " LEFT JOIN %portaldb.tblHelpDeskResource hdr ON sih.fld_int_Stage1Approver_HelpdeskResourceId = hdr.fld_int_ID"
                + " LEFT JOIN %helpdeskdb.Budget b ON sih.fld_int_BudgetId = b.Id"
                + " LEFT JOIN %helpdeskdb.FundingRoute fr ON b.FundingRouteId = fr.Id"
                + " LEFT JOIN %portaldb.uvw_PartsOrders po ON sih.fld_str_PO_PONumber = po.PONumber"
                + " LEFT JOIN %portaldb.tblHelpDeskFault hdf ON po.JobRef = hdf.fld_int_ID"
                + " INNER JOIN %helpdeskdb.Site av ON hdf.fld_int_StoreID = av.Id"
                + " LEFT JOIN  %helpdeskdb.uvw_SitesDivision sd ON sd.SiteId = av.Id"
                + " WHERE sis.fld_str_Name = 'Awaiting Final Approval'"
                + " ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getInvoicesAwaitingFinalApproval: " + sql);
        try{
            return jdbc_portal.queryForList(sql,params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getBudgetRouteOfInvoiceAwaitingApproval(String invoiceNumber, int SupplierInvoiceStatusId) {
        String sql = " SELECT fr.Name Budget"
                + " FROM %portaldb.tblcfSupplierInvoiceHeader sih"
                + " LEFT JOIN %helpdeskdb.Budget b ON sih.fld_int_BudgetId = b.Id"
                + " LEFT JOIN %helpdeskdb.FundingRoute fr ON b.FundingRouteId = fr.Id"
                + " WHERE sih.fld_str_InvoiceNumber = '%s' AND sih.fld_int_SupplierInvoiceStatusId = %d";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, invoiceNumber, SupplierInvoiceStatusId);

        logger.debug("getBudgetRouteOfInvoiceAwaitingFinalApproval: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getBudgetRouteOfInvoiceAwaitingFinalApproval(String invoiceNumber) {
        return getBudgetRouteOfInvoiceAwaitingApproval(invoiceNumber, 10);
    }

    public String getBudgetRouteOfInvoiceAwaitingInitialApproval(String invoiceNumber) {
        return getBudgetRouteOfInvoiceAwaitingApproval(invoiceNumber, 3);
    }

    public List<Map<String, Object>> getInvoiceTabDetailsInInvoiceAwaitingFinalApproval(String invoiceNumber) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_INVOICE_DETAILS_AWAITING_FINAL_APPROVAL.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, invoiceNumber);

        logger.debug("getInvoiceTabDetailsInInvoiceAwaitingFinalApproval: " + sql);
        try{
            return jdbc_portal.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getInvoiceLineType(String lineType) {
        String sql = "SELECT fld_int_Id Id, fld_str_Description Description, fld_str_PartCode PartCode, fld_bit_ForSupplier Supplier,"
                + " fld_bit_ForSupplyOnly SupplyOnly, fld_str_NominalCode NominalCode, fld_bit_IsMandatory IsMandatory, fld_bit_IsLabor IsLabor"
                + " FROM %portaldb.tblcfSupplierInvoiceLineType"
                + " WHERE fld_str_Description = '%s'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, localize(lineType));

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getInvoiceLineType: " + sql);

        try {
            return jdbc_portal.queryForList(sql, params).get(0);
        } catch (Exception e) {
            logger.debug("Exception: " + e.getMessage());
            return null;
        }
    }

    public Integer getCountOfFundingRequests(String invoiceNumber) {
        String sql = "SELECT COUNT(DISTINCT fr.Id) Uplifts"
                + " FROM %portaldb.tblcfSupplierInvoiceHeader sih"
                + " JOIN %portaldb.uvw_PartsOrders po ON sih.fld_str_PO_PONumber = po.PONumber"
                + " JOIN %helpdeskdb.Job j ON po.JobRef = j.JobReference"
                + " JOIN %helpdeskdb.ResourceAssignment ra ON j.Id = ra.JobId"
                + " JOIN %helpdeskdb.Resource r ON ra.ResourceId = r.Id"
                + " JOIN %helpdeskdb.FundingRequest fr ON fr.ResourceAssignmentId = ra.Id"
                + " LEFT JOIN %helpdeskdb.Reason res ON fr.FundingReasonId = res.Id"
                + " WHERE r.Name = po.Supplier"
                + " AND RejectionReasonId IS NULL"
                + " AND CancellationReasonId IS NULL"
                + " AND fr.AuthorisedOrRejectedById IS NOT NULL"
                + " AND sih.fld_str_InvoiceNumber  = :invoiceNumber";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("invoiceNumber", invoiceNumber);
        logger.debug("getCountOfFundingRequests: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getLabourLineType() {
        String sql = "SELECT fld_str_Description FROM %portaldb.tblcfSupplierInvoiceLineType WHERE fld_int_ID = 2";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getLabourLineType: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<String> getLineTypeDescriptions() {
        String sql = "SELECT fld_str_Description Description FROM %portaldb.tblcfSupplierInvoiceLineType";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getInvoiceLineTypeDescription: " + sql);
        List<String> lineTypeDescription = jdbc_portal.query(sql, new MapSqlParameterSource(), new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("Description"));
            }
        });
        return lineTypeDescription;
    }

    public List<Map<String, Object>> getInvoiceLineTypes() {
        String sql = "SELECT fld_str_Description Description, fld_bit_IsMandatory IsMandatory, fld_bit_IsLabor IsLabor FROM %portaldb.tblcfSupplierInvoiceLineType";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getSupplierInvoiceLineType: " + sql);
        try{
            return jdbc_portal.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public void resetInvoiceLineTypes(String description, boolean isMandatory, boolean isLabor) {
        String sql = "UPDATE %portaldb.tblcfSupplierInvoiceLineType"  +
                "  SET fld_bit_IsMandatory = :isMandatory, fld_bit_IsLabor = :isLabor" +
                "  WHERE fld_str_Description = :description";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("description", description);
        params.addValue("isMandatory", isMandatory);
        params.addValue("isLabor", isLabor);
        logger.debug("resetToMandatoryAndLabor: " + sql);
        jdbc_portal.update(sql, params);
    }

    public String getUnusedBudgetName(Integer jobReference) {
        String sql = "SELECT TOP(1)" +
                " COALESCE (Alias, Name) AS Name " +
                " FROM %helpdeskdb.Budget" +
                " WHERE Name NOT IN" +
                " (SELECT b.Name FROM %helpdeskdb.Budget b" +
                " INNER JOIN %portaldb.tblPO po ON b.Id = po.fld_int_Budget" +
                " WHERE po.fld_int_HelpDeskFaultID = :jobReference)" +
                " AND Active = 1" +
                " ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        logger.debug("getUnusedBudgetName: " + sql.replace(":jobReference", String.valueOf(jobReference)));
        try {
            return jdbc_portal.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getRandomSupplierInvoiceLineType() {
        String sql = "SELECT TOP(1) fld_str_Description FROM %portaldb.tblcfSupplierInvoiceLineType "
                + "WHERE fld_bit_ForSupplier = 1 "
                + "ORDER BY NEWID()";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_portal.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public void updateInvoiceDetails(Integer orderReference, String invoiceNumber) throws Exception {
        String sql = "UPDATE sih"  +
                " SET sih.fld_str_InvoiceNumber = '%s', " +
                " sih.fld_dat_InvoiceDate = CAST(GETDATE() AS DATE)," +
                " sih.fld_cur_NetAmount = po.fld_cur_CostExpected," +
                " sih.fld_cur_VatAmount = 0.00," +
                " sih.fld_cur_GrossAmount = po.fld_cur_CostExpected " +
                " FROM %portaldb.tblcfSupplierInvoiceHeader AS sih" +
                " INNER JOIN %portaldb.tblPO po ON sih.fld_int_PO_ID = po.fld_int_ID" +
                " WHERE sih.fld_str_PO_PONumber = '%s'";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, invoiceNumber, orderReference);

        logger.debug("updateInvoiceDetails: " + sql);
        int rows = jdbc_portal.update(sql, new MapSqlParameterSource());
        if (rows == 0) {
            throw new Exception("row not updated");
        }
    }

    public void updateConsolidatedInvoiceDetails(String supplierId, String invoiceNumber, Integer invoiceHeaderId, Float totalInvoiceAmount) throws Exception {
        String sql = "UPDATE %portaldb.tblcfSupplierInvoiceHeader"  +
                " SET fld_str_InvoiceNumber = '%s', " +
                " fld_dat_InvoiceDate = CAST(GETDATE() AS DATE)," +
                " fld_cur_NetAmount = %f, fld_cur_VatAmount = 0.00," +
                " fld_cur_GrossAmount = %f, fld_str_LegalEntity = 'City Facilities Management Ltd'," +
                " fld_bit_AccountsPayableSubmitted = 0, fld_int_CompanyId = 2" +
                " WHERE fld_str_SupplierId = '%s' AND fld_int_Id = %d";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, invoiceNumber, totalInvoiceAmount, totalInvoiceAmount, supplierId, invoiceHeaderId);

        logger.debug("updateConsolidatedInvoiceDetails: " + sql);
        int rows = jdbc_portal.update(sql, new MapSqlParameterSource());
        if (rows == 0) {
            throw new Exception("row not updated");
        }
    }

    public String getSupplierCode(Integer resourceId) {
        String sql = "SELECT SupplierTCode FROM Resource WHERE Id = %d";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, resourceId);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getSupplierCode: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message " + e.getMessage());
            return null;
        }
    }

    public Integer getSupplierResourceId(String supplierID) {
        String sql = "SELECT TOP(1) ap.ResourceId " +
                "FROM %portaldb.tblPartCodes PartCode  " +
                "INNER JOIN %portaldb.tblpricebook PriceBook ON PartCode.fld_str_PartCode = PriceBook.fld_str_PartCode " +
                "INNER JOIN %portaldb.tblHelpDeskResource hdr ON PriceBook.fld_int_ResourceId = hdr.fld_int_ID " +
                "INNER JOIN %helpdeskdb.ApplicationUser ap ON ap.UserName = hdr.fld_str_LogOn " +
                "LEFT JOIN %portaldb.tblTCodeSuppliers TCS ON LTRIM(RTRIM(PriceBook.fld_str_SupplierCode)) COLLATE DATABASE_DEFAULT = LTRIM(RTRIM(TCS.Code)) " +
                "WHERE fld_int_PartCodeCategoryID NOT IN (133) " +  // should not be of type SUBCON
                "AND fld_bit_Enabled = 1 " +
                "AND fld_int_PartCodeCategoryID IN (SELECT fld_int_id FROM %portaldb.tblPartCodeCategory WHERE fld_bit_enabledForPartsRequest=1) " +
                "AND PriceBook.fld_str_SupplierCode NOT IN (SELECT hdr.fld_str_SupplierID  FROM %edidb.SupplierEdiSetting edi INNER JOIN %portaldb.tblHelpDeskResource hdr ON edi.PortalHelpDeskResourceId = hdr.fld_int_ID) " +
                "AND fld_str_SupplierCode = :supplierID";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("supplierID", supplierID);
        logger.debug("getSupplierResourceId: " + sql.replace(":supplierID", supplierID));
        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Error message " + e.getMessage());
            return null;
        }
    }

    public void deleteInvoiceDocuments(Integer orderReference) throws Exception {
        String sql = "UPDATE %portaldb.tblcfSupplierInvoiceHeader SET fld_str_InvoiceFilePath = NULL, fld_str_JobSheetFilePath = NULL WHERE fld_str_PO_PONumber = '%s'";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, orderReference);

        logger.debug("deleteInvoiceDocuments: " + sql);
        int rows = jdbc_portal.update(sql, new MapSqlParameterSource());
        if (rows == 0) {
            throw new Exception("row not updated");
        }
    }

    public List<Map<String, Object>> getAllInvoicesAwaitingApprovalData(String date, String locale) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_ALL_INVOICES_AWAITING_APPROVAL_DATA.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("date", date);
        params.addValue("locale", locale);
        logger.debug("getAllInvoicesAwaitingApprovalData: " + sql);
        try {
            return jdbc_portal.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public List<Map<String, Object>> getAllInvoicesDetails(String orderRef) throws Exception {
        String sql = " SELECT po.fld_str_PONumber AS OrderRef, po.fld_cur_CostExpected AS OrderValue, "
                + " po.fld_int_HelpdeskFaultID AS JobRef, j.CreatedOn AS LoggedDate, "
                + " s.fld_str_Name AS Site, sih.fld_str_InvoiceNumber AS InvNum, "
                + " sih.fld_dat_InvoiceDate AS InvDate , sih.fld_dat_InvoiceDate AS InvoiceDate, "
                + " sih.fld_dat_SubmissionDateUTC DateSubmitted , sih.fld_dat_SubmissionDateUTC AS SubmissionDate, "
                + " hdrSupp.fld_str_Name AS Supplier, "
                + " sih.fld_cur_NetAmount AS Net, sih.fld_cur_VatAmount AS Tax,"
                + " sih.fld_cur_GrossAmount AS Gross "
                + " FROM %portaldb.tblPO po "
                + " INNER JOIN %portaldb.tblcfSupplierInvoiceHeader sih ON po.fld_str_PONumber = sih.fld_str_PO_PONumber "
                + " LEFT JOIN %portaldb.tblHelpDeskFault fault ON fault.fld_int_ID = po.fld_int_HelpdeskFaultID "
                + " LEFT JOIN %portaldb.tblStore s ON fault.fld_int_StoreID = s.fld_int_ID "
                + " LEFT JOIN %helpdeskdb.Job AS j ON j.jobReference = PO.fld_int_HelpdeskFaultId "
                + " LEFT JOIN %helpdeskdb.Site AS st ON st.Id = j.SiteId "
                + " INNER JOIN %portaldb.tblHelpDeskResource AS hdrSupp ON po.fld_str_SupplierID = hdrSupp.fld_str_SupplierID "
                + " WHERE fld_str_PONumber = :orderRef";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("orderRef", orderRef);
        logger.debug("getAllInvoicesDetails: " + sql.replace(":orderRef", quote(orderRef)));
        try {
            return jdbc_portal.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getInvoiceTotalCost(String jobRef) throws Exception {
        String sql = " SELECT CONVERT(NVARCHAR, SUM(fld_cur_CostExpected), 0) TotalJobCost " +
                " FROM %portaldb.tblPO " +
                " WHERE fld_int_HelpdeskfaultID = :jobRef " +
                " GROUP BY fld_int_HelpdeskfaultID ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobRef", jobRef);
        logger.debug("getInvoiceTotalCost: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getOnlineInvoicingActiveFlagForUser(Integer resourceId)  {
        String sql = "SELECT Resources.fld_bit_OnlineInvoicingActive FROM %portaldb.tblHelpDeskResource Resources " +
                " INNER JOIN %helpdeskdb.Resource r ON Resources.fld_int_ID = r.EpochID " +
                " where r.Id = :resourceId";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);
        logger.debug("getOnlineInvoicingActiveFlagForUser: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return 0;
        }
    }

    public void updateOnlineInvoicingActiveFlagForUser (Integer resourceId) throws Exception {
        String sql = "UPDATE %portaldb.tblHelpDeskResource SET %portaldb.tblHelpDeskResource.fld_bit_OnlineInvoicingActive = 1 " +
                " FROM %portaldb.tblHelpDeskResource  re " +
                " INNER JOIN %helpdeskdb.Resource r ON re.fld_int_ID = r.EpochID " +
                " WHERE r.ID = :resourceId";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("resourceId", resourceId);
        logger.debug("updateOnlineInvoicingActiveFlagForUser: " + sql);

        int rows = jdbc_portal.update(sql, params);
        if (rows == 0) {
            throw new Exception("row not updated");
        }
    }

    public void rejectInvoices(String state, int count) throws Exception {
        String sql = FileHelper.getResourceFile("sql/update/", "REJECT_INVOICES.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, state, count);
        logger.debug("rejectInvoices: " + sql);
        jdbc_portal.update(sql, new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getHeaderIdAndNetAmount(String orderRef) throws Exception {
        String sql = "SELECT fld_int_Id AS HeaderId, fld_cur_NetAmount AS UnitPrice FROM %portaldb.tblcfSupplierInvoiceHeader sih " +
                " WHERE sih.fld_str_PO_PONumber = :orderRef";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("orderRef", orderRef);
        logger.debug("getHeaderIdAndNetAmount: " + sql.replace(":orderRef", quote(orderRef)));
        try {
            return jdbc_portal.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public void updateInvoiceLineDetails(Integer headerId, Integer lineType, String description, Integer quantity, Integer unitPrice) throws Exception {
        String sql = "INSERT INTO %portaldb.tblcfSupplierInvoiceLine " +
                " ( fld_int_SupplierInvoiceHeaderId, fld_int_SupplierInvoiceLineType,fld_str_Description, fld_dec_Quantity, " +
                " fld_cur_UnitPrice, fld_cur_UnitTaxAmount, fld_cur_LineValue, fld_str_PartNumber, fld_int_TransactionLineId, " +
                " fld_bit_InvoiceLineFulfilled, fld_bit_NotApplicable, fld_bit_IsMandatoryLine," +
                " fld_str_OrderReference, fld_int_JobSheetFileId, fld_int_ConsolidatedInvoiceStatusId, " +
                " fld_int_RejectionReasonId, fld_str_RejectionReasonNotes,fld_cur_OriginalLineValue) " +
                " VALUES (%d, %d,'%s', %d, %d, 0.00, %d, NULL, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL)";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, headerId, lineType, description, quantity, unitPrice, unitPrice);

        logger.debug("updateInvoiceLineDetails: " + sql);
        int rows = jdbc_portal.update(sql, new MapSqlParameterSource());
        if (rows == 0) {
            throw new Exception("row not updated");
        }
    }

    public void updateInvoiceStatusForInvoiceApprover(Integer statusId, String orderRef) throws Exception {
        String sql = "UPDATE %portaldb.tblcfSupplierInvoiceHeader SET fld_int_SupplierInvoiceStatusId = :statusId WHERE fld_str_PO_PONumber = :orderRef";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("statusId", statusId);
        params.addValue("orderRef", orderRef);
        logger.debug("updateInvoiceStatusForInvoiceApprover: " + sql);

        int rows = jdbc_portal.update(sql, params);
        if (rows == 0) {
            throw new Exception("row not updated");
        }
    }

    public Integer getNextProcessStepIdForFinalApproval(int jobReference) {
        String sql = "SELECT NextInvoiceProcessStep FROM InvoiceProcessStep ops "
                + "INNER JOIN %portaldb.tblcfSupplierInvoiceHeader sih ON ops.BudgetId = sih.fld_int_BudgetId "
                + "INNER JOIN %portaldb.tblPO po ON po.fld_str_PONumber = sih.fld_str_PO_PONumber "
                + "WHERE ops.SupplierInvoiceStatusIdOnExit = 10 "
                + "AND fld_int_HelpdeskFaultID = %d";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql,  jobReference);

        logger.debug("getNextProcessStepIdForFinalApproval: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public void approveInvoiceAwaitingFinalApproval(String orderRef, int resourceId, int processStepId) throws Exception {
        String sql = "UPDATE %portaldb.tblcfSupplierInvoiceHeader "
                + "SET fld_int_SupplierInvoiceStatusId = 10 "
                + ",fld_int_Stage1Approver_HelpDeskResourceId = %d "
                + ",fld_int_InvoiceProcessStepId = %d "
                + ",fld_bit_ApprovalRejected = 0 "
                + ",fld_dat_Stage1Approval_Date = GETDATE() "
                + ",fld_bit_ExtremeWeather = 0 "
                + "WHERE fld_str_PO_PONumber = '%s'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, resourceId, processStepId, orderRef);

        logger.debug("approveInvoice: " + sql);

        int rows = jdbc_portal.update(sql, new MapSqlParameterSource());
        if (rows == 0) {
            throw new Exception("row not updated");
        }
    }

    public String getRandomOrderRefWithInvoicePdfdDocument() {
        String sql = " SELECT TOP (1) sih.fld_str_PO_PONumber AS OrderRef FROM %portaldb.tblcfSupplierInvoiceHeader sih " +
                "INNER JOIN %portaldb.tblPO po ON po.fld_str_PONumber = sih.fld_str_PO_PONumber " +
                "WHERE po.fld_str_WorkOrderPDF IS NOT NULL " +
                "AND sih.fld_dat_SubmissionDateUTC IS NOT NULL " +
                "AND  po.fld_dat_DateRaised > (SELECT DATEADD(day, -30, getdate())) " +
                "ORDER BY NEWID() ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getRandomOrderRefWithInvoicePdfdDocument: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getRandomSupplierNotUsingPortalForInvoicing() {
        String sql = "SELECT TOP(1) r.Id ResourceId, r.EpochId, r.Name ResourceName, hdr.fld_str_SupplierID SupplierId, tcs.Name SupplierName "
                + "FROM %portaldb.tblHelpDeskResource hdr "
                + "INNER JOIN Resource r ON hdr.fld_int_ID = r.EpochId "
                + "INNER JOIN %portaldb.tblTCodeSuppliers tcs ON tcs.Code = hdr.fld_str_SupplierID "
                + "WHERE hdr.fld_bit_OnlineInvoicingActive = 0 "
                + "AND hdr.fld_str_SupplierID LIKE 'SO:%' "
                + "AND r.Active = 1 AND r.ResourceProfileId IN (115, 138) "
                + "ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getSupplierNotUsingPortalForInvoicing: " + sql);

        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
    }

}