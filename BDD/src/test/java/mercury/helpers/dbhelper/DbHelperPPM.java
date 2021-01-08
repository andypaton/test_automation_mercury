package mercury.helpers.dbhelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.object.GenericStoredProcedure;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mercury.database.config.DbConfigV2;
import mercury.helpers.DbUtilityHelper;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfigV2.class)
public class DbHelperPPM {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private DbUtilityHelper dbUtilityHelper;
    @Autowired private DataSource helpdeskDataSource;
    @Autowired private NamedParameterJdbcTemplate jdbc_portal;
    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;

    /**
     * Inserts the row in portal db table %portaldb.tblPPMType to create new PPM Type for contractors.
     *
     * Default Values
     * 13 - PPM Frequency Type like day, week, month etc
     * 2 - Frequency Type
     * NULL, NULL - LastEditedBy, LastEdited
     * 34 - Resource Subtype e.g., Contractor
     * 0 - MilkRun of the stored proc
     * NULL - EBI Type
     * 1, 1 - fld_bit_DueAnyWeekDay, fld_int_PPMTypeClassificationID
     * 0, 0, 0, 0 - Document Required, Certificate Required, Certificate Type, fld_bit_CertificateAtAssetLevel
     */
    public void insertInToNewPPMType(String ppmTypeName, Integer resourceTypeID) {
        String sql = "INSERT INTO %portaldb.tblPPMType"
                + " (fld_str_Name, fld_int_MainTypeID, fld_int_ScheduleAdvanceTimeID, fld_int_ResourceTypeID, fld_int_ResourceID, fld_int_FrequencyTypeID,"
                + " fld_int_Frequency, fld_str_CreatedBy, fld_dat_CreatedDate, fld_str_LastEditedBy, fld_dat_LastEdited, fld_int_ResourceSubTypeID,"
                + " fld_bit_MilkRun, fld_int_EBITypeID, fld_bit_DueAnyWeekDay, fld_int_PPMTypeClassificationID, fld_bit_DocumentRequired, fld_str_ContractCode,"
                + " fld_int_HelpdeskFaultServicePropertyTypeID, fld_dat_checksumUpdated, fld_bit_CertificateRequired, fld_int_CertificateTypeID, fld_bit_CertificateAtAssetLevel)"
                + " SELECT TOP(1) '"+ ppmTypeName +"', amt.fld_int_ID, 1, "+ resourceTypeID +", hdr.fld_int_ID, 13, 2, 'Automation Testing', GETUTCDATE(), NULL, NULL, 34, 0, NULL, 1, 1, 0,"
                + " LEFT(SUBSTRING (RTRIM(RAND()) + SUBSTRING(RTRIM(RAND()),3,11), 3,11),3), NULL, GETUTCDATE(), 0, 0, 0"
                + " FROM %portaldb.tblAssetMainType amt, %portaldb.tblHelpDeskResource hdr"
                + " INNER JOIN %helpdeskdb.Resource r ON r.EpochId = hdr.fld_int_ID "   // avoid bad data missing from helpdesk db
                + " WHERE amt.fld_int_ID >= 1"
                + " AND hdr.fld_int_HelpDeskResourceTypeID = "+ resourceTypeID +""
                + " ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("insertInToNewPPMType: " + sql);
        try {
            jdbc_portal.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Exception " + e.getMessage());
        }
    }

    /**
     * Inserts the row in portal db table %portaldb.tblPPMTypeStoreCost to create new PPM Type store cost for contractors.
     * @param ppmTypeName
     */
    public void insertInToNewPPMTypeStoreCost(String ppmTypeName) {
        String sql = "INSERT INTO %portaldb.tblPPMTypeStoreCost"
                + " (fld_int_PPMTypeID, fld_int_StoreID, fld_cur_PPMCost)"
                + " SELECT TOP(1) MAX(pt.fld_int_ID), s.fld_int_ID, ROUND(RAND(CHECKSUM(NEWID())) * (10000), 2)"
                + " FROM %portaldb.tblPPMType pt, %portaldb.tblStore s"
                + " WHERE pt.fld_str_Name = '%s'"
                + " GROUP BY pt.fld_str_Name, s.fld_int_ID, pt.fld_dat_CreatedDate"
                + " ORDER BY pt.fld_dat_CreatedDate, NEWID() DESC";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = String.format(sql, ppmTypeName);
        logger.debug("insertInToNewPPMTypeStoreCost: " + sql);
        try {
            jdbc_portal.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Exception " + e.getMessage());
        }
    }

    /**
     * Inserts the rows in portal db table %portaldb.tblPPMScheduleLine to create new PPM orders for contractors.
     * @param dayNumber
     */
    public void insertInToNewPPMScheduleLine(int dayNumber, String ppmTypeName) {
        String sql = "INSERT INTO %portaldb.tblPPMScheduleLine"
                + " (fld_int_ScheduleID, fld_int_StoreID, fld_int_PPMTypeID, fld_int_StatusID, fld_int_CancellationReasonID, fld_str_CancellationReasonOther, fld_dat_WeekEnding,"
                + " fld_str_CreatedBy, fld_dat_CreatedDate, fld_str_LastEditedBy, fld_dat_LastEdited, fld_dat_ProjectCreatedDate, fld_dat_StartDate, fld_dat_ContractorAcceptedDate,"
                + " fld_int_ContractorAcceptedResourceID, fld_int_PPMTypePackageStoreID, fld_dat_checksumUpdated, fld_bit_Watched)"
                + " SELECT TOP(1) ps.fld_int_ID, ptsc.fld_int_StoreID, MAX(pt.fld_int_ID), 1, NULL, NULL, CAST(DATEADD(DAY,"+ dayNumber +",GETDATE()) AS DATE), 'Automation Testing', GETUTCDATE(),"
                + " 'Automation Testing', GETUTCDATE(), NULL, NULL, NULL, NULL, 0, GETUTCDATE(), NULL"
                + " FROM %portaldb.tblPPMSchedule ps, %portaldb.tblPPMTypeStoreCost ptsc, %portaldb.tblPPMType pt"
                + " WHERE ps.fld_str_Description = CONVERT(VARCHAR(10), YEAR(CURRENT_TIMESTAMP))"
                + " AND pt.fld_str_Name = ':ppmTypeName'"
                + " GROUP BY ps.fld_int_ID, ptsc.fld_int_StoreID, pt.fld_str_Name, ptsc.fld_int_ID"
                + " ORDER BY ptsc.fld_int_ID DESC";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = sql.replace(":ppmTypeName", ppmTypeName);
        logger.debug("insertInToNewPPMScheduleLine: " + sql);
        try {
            jdbc_portal.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Exception " + e.getMessage());
        }
    }

    /**
     * This stored procedure execution will creates the project and calls out the PPM to the resource.
     */
    public void executeCreatePPMProjectsStoredProcedure() {
        StoredProcedure procedure = new GenericStoredProcedure();
        procedure.setDataSource(helpdeskDataSource);
        String sql = "%portaldb.sp_CreatePPMProjects_NonMilkRun";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        procedure.setSql(sql);
        procedure.setFunction(false);
        procedure.compile();
        logger.debug(String.format("executeCreatePPMProjectsStoredProcedure: execute = %s", sql));
        procedure.execute();
    }

    /**
     * @return The list of PPM Schedule Line references.
     */
    public List<Integer> getPPMScheduleReferences(String ppmTypeName) {
        String sql = "SELECT psl.fld_int_ID ID FROM %portaldb.tblPPMScheduleLine psl"
                + " INNER JOIN %portaldb.tblPPMType pt ON pt.fld_int_ID = psl.fld_int_PPMTypeID"
                + " WHERE pt.fld_str_Name = '%s'"
                + " ORDER BY psl.fld_int_ID ASC";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = String.format(sql, ppmTypeName);
        logger.debug("getPPMScheduleReferences: " + sql);
        List<Integer> ppmScheduleReferences = jdbc_portal.query(sql, new MapSqlParameterSource(), new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getInt("ID"));
            }
        });
        return ppmScheduleReferences;
    }

    /**
     * @param PPMScheduleRefsList
     * @return true if there are PPM Schedule Line rows created for the PPM Schedule References.
     */
    public boolean isPPMScheduleLinesCreated(List<Integer> PPMScheduleRefsList) {
        String PPMScheduleRefs = PPMScheduleRefsList.stream()
                .map(PPMScheduleRef -> PPMScheduleRef.toString())
                .collect(Collectors.joining("','", "'", "'"));

        String sql = "SELECT COUNT(*) FROM %portaldb.tblProjectHeader"
                + " WHERE fld_int_PPMScheduleRef IN (%s)"
                + " AND fld_int_HelpdeskfaultID = 0";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = String.format(sql, PPMScheduleRefs);
        logger.debug("isPPMScheduleLinesCreated: " + sql);
        try {
            int count = jdbc_portal.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
            return count > 0;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return false;
        }
    }

    /**
     * Get purchase order created fpr PPM Job.
     * @param PPMScheduleRef
     * @return
     */
    public String getPPMOrderReference(Integer PPMScheduleRef) {
        String sql = "SELECT fld_str_PONumber FROM %portaldb.tblPO WHERE fld_int_PpmId = %d";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, PPMScheduleRef);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("PPMScheduleRef", PPMScheduleRef);
        logger.debug("getPPMOrderReference: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    /**
     * Get Job Reference for PPM
     * @param PPMScheduleRef
     * @return
     */
    public Integer getJobReferenceForPpm(int PPMScheduleRef) {
        String sql = "SELECT JobReference FROM Job WHERE PpmId = %d";
        sql = String.format(sql, PPMScheduleRef);
        logger.debug("getJobReferenceForPpm: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public Integer getJobReferenceCount(int PPMScheduleRef) {
        String sql = "SELECT count(*) FROM Job WHERE PpmId = %d";
        sql = String.format(sql, PPMScheduleRef);
        logger.debug("getJobReferenceCount: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    /**
     * Get Resource ID assigned to PPM Type.
     * @return
     */
    public Integer getResourceIDForPPMType(String ppmTypeName) {
        String sql = "SELECT r.Id FROM %portaldb.tblPPMType ppm "
                + " INNER JOIN %helpdeskdb.Resource r ON r.EpochId = ppm.fld_int_ResourceID "
                + " WHERE ppm.fld_str_Name = '%s'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, ppmTypeName);

        logger.debug("getResourceIDForPPMType: " + sql);
        return jdbc_portal.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    /**
     * Get Site ID assigned to PPM Type.
     * @return
     */
    public Integer getSiteIdForPPMType(String ppmTypeName) {
        String sql = "SELECT fld_int_StoreID FROM %portaldb.tblPPMTypeStoreCost sc "
                + " INNER JOIN %portaldb.tblPPMType pt ON pt.fld_int_ID = sc.fld_int_PPMTypeID AND pt.fld_str_Name = '%s'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, ppmTypeName);
        logger.debug("getSiteIdForPPMType: " + sql);
        return jdbc_portal.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    /**
     * Get PPM Job Start Date & Time
     * @param jobReference
     * @return
     */
    public Timestamp getPPMJobStartDate(Integer jobReference) {
        String sql = "SELECT StartedDate FROM %helpdeskdb.PortalEventPPMOnSite WHERE PPMId = :jobReference";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("jobReference", jobReference);
        logger.debug("getPPMJobStartDate: " + sql.replace(":jobReference", String.valueOf(jobReference)));
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Timestamp.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    /**
     * Get PPM job resource type id
     * @param profileName
     * @return
     */
    public Integer getResourceTypeId(String profileName) {
        String sql = "SELECT fld_int_ID FROM %portaldb.tblHelpDeskResourceType WHERE fld_str_Name = '%s'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, profileName);
        logger.debug("getResourceId: " + sql);
        return jdbc_portal.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public boolean hasPpmJobGotGsanCertificate(int ppmJobId) {
        String sql = "SELECT fld_bit_CertificateRequired "
                + "FROM  %portaldb.tblPPMType pt "
                + "INNER JOIN  %portaldb.tblPPMScheduleLine psl ON pt.fld_int_ID = psl.fld_int_PPMTypeID "
                + "WHERE psl.fld_int_ID = :ppmJobId ";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ppmJobId", ppmJobId);
        logger.debug("hasPpmJobGotGsanCertificate: " + sql.replace(":ppmJobId", "'" + ppmJobId + "'"));
        int count = jdbc_portal.queryForObject(sql, params, Integer.class);
        return count > 0;
    }

    /**
     * Get Supplier Invoice Header Id
     * @param orderReference
     * @return
     */
    public Integer getSupplierInvoiceHeaderId(String orderReference) {
        String sql = "SELECT sih.fld_int_Id FROM %portaldb.tblPO PO" +
                " INNER JOIN %portaldb.tblcfSupplierInvoiceHeader sih ON PO.fld_str_PONumber = sih.fld_str_PO_PONumber" +
                " WHERE PO.fld_str_PONumber = '%s'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, orderReference);
        logger.debug("getSupplierInvoiceHeaderId: " + sql);
        return jdbc_portal.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    public String getRandomContractorResourceIdWithNoConsolidatedInvoicesInProgress() {
        String sql = "SELECT TOP(1) r.Id FROM Resource r"
                + " INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id"
                + " WHERE r.Active = 1 AND rp.Active = 1 AND rp.JobCallouts = 1"
                + " AND rp.Id = 115 AND r.UsesEngineers = 1 AND r.OnlineInvoicingActive = 1"
                + " AND r.SupplierTCode NOT IN (SELECT fld_str_SupplierId FROM Portal.tblcfSupplierInvoiceHeader WHERE fld_bit_IsConsolidated = 1)"
                + " ORDER BY NEWID()";

        logger.debug("getRandomContractorResourceIdWithNoConsolidatedInvoices: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * Inserts the materials line in Supplier Invoice Line table.
     * @param supplierInvoiceHeaderId
     * @param description
     * @param quantity
     * @param unitPrice
     * @param unitTaxAmount
     * @param lineValue
     * @param partCode
     * @param orderReference
     */
    public void insertInToSupplierInvoiceLine(Integer supplierInvoiceHeaderId, Integer lineType, String description, Float quantity, Float unitPrice, Float unitTaxAmount, Float lineValue, String partCode, boolean isMandatoryLine, String orderReference) {
        String sql = "INSERT INTO %portaldb.tblcfSupplierInvoiceLine"
                + " (fld_int_SupplierInvoiceHeaderId, fld_int_SupplierInvoiceLineType, fld_str_Description, fld_dec_Quantity, fld_cur_UnitPrice, fld_cur_UnitTaxAmount, fld_cur_LineValue,"
                + " fld_str_PartNumber, fld_int_TransactionLineId, fld_bit_InvoiceLineFulfilled, fld_bit_NotApplicable, fld_bit_IsMandatoryLine, fld_str_OrderReference, fld_int_JobSheetFileId,"
                + " fld_int_ConsolidatedInvoiceStatusId, fld_int_RejectionReasonId, fld_str_RejectionReasonNotes, fld_cur_OriginalLineValue)"
                + " VALUES (%d, %d, '%s', %f, %f, %f, %f, '%s', 0, 0, 0, '%b', '%s', NULL, NULL, NULL, NULL, NULL)";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        //Commenting out below code, further investigation is required
        // partCode = partCode.contains("null") ? partCode.toUpperCase() : partCode;
        sql = String.format(sql, supplierInvoiceHeaderId, lineType, description, quantity, unitPrice, unitTaxAmount, lineValue, partCode, isMandatoryLine, orderReference);

        logger.debug("insertInToSupplierInvoiceLine: " + sql);
        try {
            jdbc_portal.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Exception " + e.getMessage());
        }
    }

    public void setOrderValue(String poNumber, String cost) {
        String sql = "UPDATE %portaldb.tblPO " +
                "SET fld_cur_CostExpected = :cost " +
                "WHERE fld_str_PONumber = :poNumber";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("poNumber", poNumber);
        params.addValue("cost", cost);

        logger.debug("setOrderValue: " + poNumber);
        try {
            jdbc_portal.update(sql, params);
        } catch (Exception e) {
            logger.debug("Exception " + e.getMessage());
        }
    }

    public Map<String, Object> getContractorWithOpenPpmJob() {
        String sql = "SELECT TOP(1) ppm.JobReference, r.Id ResourceId, r.Name ResourceName, ppm.MercurySiteId SiteId, Username, up.Name UserProfileName, ppm.CalloutStatus, ppm.SubTypeClassification, ppm.JobDescription, ppm.LoggedDate, ppm.WorksOrderValue, ppm.ContractorETADate "
                + "FROM uvw_ppmJobs ppm "
                + "INNER JOIN Resource r ON r.EpochId = ppm.ResourceEpochId "
                + "INNER JOIN ResourceAssignment ra ON ra.ResourceId = r.Id "
                + "INNER JOIN ApplicationUser au ON r.Id = au.ResourceId "
                + "INNER JOIN UserProfile up ON up.Id = au.UserProfileId "
                + "WHERE ra.Active = 1 AND up.Name LIKE 'Contractor Admin' "
                + "AND ppm.ScheduleLineStatus = 'Active'";

        logger.debug("getContractorWithOpenPpmJob: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public String getRandomPpmType() {
        String sql = "SELECT TOP(1) fld_str_Name"
                + " FROM %portaldb.tblPPMType"
                + " ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getRandomHelpdeskPpmType() {
        String sql = "SELECT TOP(1) Name"
                + " FROM PPMType"
                + " WHERE Active = 1"
                + " ORDER BY NEWID()";

        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getPpmTypeNameFromId(int ppmId) {
        String sql = "SELECT Name FROM PpmType WHERE Id = %d";

        sql = String.format(sql, ppmId);

        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getPpmType() {
        String sql = "SELECT TOP(1) ppmt.fld_int_ID PpmTypeId"
                + " FROM Portal.tblPPMType ppmt"
                + " JOIN Resource r ON ppmt.fld_int_ResourceID = r.Id"
                + " JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id"
                + " ORDER BY NEWID()";

        logger.debug("getPpmType: " + sql);

        try {
            return jdbc_portal.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            return null;
        }
    }

    public String getPpmTypeForResourceProfile(String resourceProfile) {
        String sql = "SELECT TOP(1) ppmt.fld_int_ID PpmTypeId"
                + " FROM %portaldb.tblPPMType ppmt"
                + " JOIN Resource r ON ppmt.fld_int_ResourceID = r.Id"
                + " JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id"
                + " WHERE rp.Name = '%s'"
                + " ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, resourceProfile);
        logger.debug("getPpmTypeForResourceProfile: " + sql);

        try {
            return jdbc_portal.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            return null;
        }
    }
}