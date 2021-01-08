package mercury.database.dao;

import static mercury.helpers.StringHelper.quote;

import java.util.Arrays;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.UserJob;
import mercury.helpers.DbUtilityHelper;
import mercury.helpers.FileHelper;

@Repository
public class UserJobDao {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private EntityManager entityManager;
    @Autowired private DbUtilityHelper dbUtilityHelper;

    private static final String GET_USER_AND_AWAITING_ACCEPTANCE_JOB_SQL = "SELECT TOP(1) NEWID() Id,  mjaa.JobReference, au.UserName, r.Id ResourceId, s.Id SiteId " +
            "FROM uvw_MonitorJobAwaitingAcceptance mjaa INNER JOIN Resource r ON mjaa.ResourceName = r.Name " +
            "INNER JOIN ApplicationUser au ON au.ResourceId = r.Id " +
            "INNER JOIN Job j ON mjaa.JobReference = j.JobReference " +
            "INNER JOIN Site s ON s.id = j.SiteId " +
            "WHERE au.Active = 1 AND au.PasswordExpiryDate > GETDATE() " +
            "AND au.UserProfileId = (SELECT Id FROM dbo.UserProfile WHERE Name = :profileName) " +
            "AND j.Id NOT IN (SELECT JobId FROM JobLock) " +
            "AND j.JobStatusId <> 10 " + // not cancelled
            "ORDER BY NEWID()";

    private static final String GET_USER_AND_AWAITING_ACCEPTANCE_JOB_WITH_ONE_RESOURCE_SQL = "SELECT TOP(1) NEWID() Id,  mjaa.JobReference, au.UserName, r.Id ResourceId, s.Id SiteId " +
            "FROM uvw_MonitorJobAwaitingAcceptance mjaa INNER JOIN Resource r ON mjaa.ResourceName = r.Name " +
            "INNER JOIN ApplicationUser au ON au.ResourceId = r.id " +
            "INNER JOIN %testdb.uvw_JobsWithSingleActiveResource jsr ON mjaa.id = jsr.jobID " +
            "INNER JOIN Site s ON s.id = mjaa.SiteId " +
            "WHERE au.Active = 1 AND au.PasswordExpiryDate > GETDATE() " +
            "AND au.UserProfileId = (SELECT Id FROM dbo.UserProfile WHERE Name = :profileName) " +
            "AND mjaa.Id NOT IN (SELECT jobId FROM JobLock) " +
            "AND mjaa.JobStatusId <> 10 " + // not cancelled
            "ORDER BY NEWID()";

    /*
     * Select a job and user where the job is in an open state and the resource is assigned to work ON it, additionally the user does not require
     * to select an asset when updating the job.
     */
    private static final String GET_USER_AND_OPEN_JOB_SQL = "SELECT TOP(1) NEWID() Id, ra.ResourceId, j.JobReference, au.UserName, s.Id SiteId " +
            "FROM Job j INNER JOIN ResourceAssignment ra ON j.Id = ra.JobId " +
            "INNER JOIN ApplicationUser au ON au.ResourceId = ra.ResourceId " +
            "INNER JOIN AssetClassification ac ON j.AssetClassificationId = ac.Id " +
            "INNER JOIN AssetSubType ast ON ac.AssetSubTypeId = ast.Id " +
            "INNER JOIN %testdb.uvw_JobsWithSingleActiveResource jsr ON j.id = jsr.jobID " +
            "INNER JOIN Site s ON s.id = j.SiteId " +
            "WHERE ra.ResourceAssignmentStatusId NOT IN (4, 5, 6, 13, 15, 16, 23) " +
            "AND j.JobStatusId IN (2, 3, 7, 15) " +
            "AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) " +
            "AND au.UserProfileId = (SELECT Id FROM dbo.UserProfile WHERE Name = :profileName) " +
            "AND ast.AssetRegisterRequiredId = 1 " +
            "AND j.id NOT IN (SELECT JobId FROM JobLock) " +
            "AND j.CreatedOn > DATEADD(MONTH, -1, GETDATE()) " +
            "GROUP BY ra.ResourceId, j.JobReference, au.UserName, s.Id HAVING COUNT(ra.ResourceId)=1 " +
            "ORDER BY NEWID()";


    private static final String GET_USER_AND_JOB_MULTI_RESORUCE_SQL = "SELECT TOP(1) NEWID() Id, ra.ResourceId, j.JobReference, au.UserName, s.Id SiteId " +
            "FROM Job j INNER JOIN ResourceAssignment ra ON j.Id = ra.JobId " +
            "INNER JOIN ApplicationUser au ON au.ResourceId = ra.ResourceId " +
            "INNER JOIN AssetClassification ac ON j.AssetClassificationId = ac.Id " +
            "INNER JOIN AssetSubType ast ON ac.AssetSubTypeId = ast.Id " +
            "INNER JOIN ResourceAssignmentStatus ras ON ra.ResourceAssignmentStatusId = ras.id " +
            "INNER JOIN JobStatus js ON j.JobStatusId = js.id " +
            "INNER JOIN Site s ON s.id = j.SiteId " +
            "WHERE ras.Name = :resourceAssignmentStatus " +
            "AND js.Name = :jobStatus " +
            "AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) " +
            "AND au.UserProfileId = (SELECT id FROM dbo.UserProfile WHERE name = :profileName) " +
            "AND j.ReasonNotLoggedAgainstAssetId IS NULL " +
            "AND ast.AssetRegisterRequiredId = 3 " +
            "AND j.id NOT IN (SELECT jobId FROM jobLock) " +
            "GROUP BY ra.ResourceId, j.JobReference, au.UserName, s.Id HAVING COUNT(ra.ResourceId)>1 " +
            "ORDER BY NEWID()";

    private static final String GET_USER_AND_OPEN_QUOTE_REQUEST_JOB_SQL = "SELECT DISTINCT TOP(1) NEWID() Id, qrr.JobReference, au.UserName, r.Id ResourceId, s.Id SiteId " +
            "FROM  uvw_MonitorAwaitingQuoteRequestReviewToDo qrr " +
            "INNER JOIN Quote q ON qrr.Id = q.JobId " +
            "INNER JOIN Resource r ON r.Id = q.QuoteRequestApproverId " +
            "INNER JOIN ApplicationUser au ON au.ResourceId = r.Id " +
            "INNER JOIN Site s ON s.id = qrr.SiteId " +
            "WHERE au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) " +
            "AND au.UserProfileId = (SELECT id FROM dbo.UserProfile WHERE name = :profileName) " +
            "AND qrr.id NOT IN (SELECT jobId FROM jobLock) " +
            "ORDER BY NEWID()";

    private static final String GET_USER_AND_PPM_JOB_SQL = "SELECT TOP(1) NEWID() Id, ppmj.JobReference,  au.UserName, au.ResourceId, s.Id SiteId " +
            "FROM %helpdeskdb.uvw_PpmJobs ppmj " +
            "INNER JOIN %helpdeskdb.Site s ON s.Id =  ppmj.siteId " +
            "INNER JOIN %helpdeskdb.Resource r ON ppmj.ResourceEpochId = r.EpochId " +
            "INNER JOIN %helpdeskdb.ApplicationUser au ON au.ResourceId = r.Id " +
            "LEFT JOIN %compliancedb.Certificate cc ON cc.JobReference = ppmj.JobReference AND cc.Active = 1 " +
            "LEFT JOIN %portaldb.tblPPMScheduleLine psl ON psl.fld_int_ID = ppmj.JobReference " +
            "INNER JOIN %portaldb.tblPPMType pt ON pt.fld_int_ID = psl.fld_int_PPMTypeID " +
            "WHERE ppmj.CalloutStatusId NOT IN (2, 3) " +
            "AND ppmj.ScheduleLineStatus = 'Active' " +
            "AND au.UserName IS NOT NULL " +
            "AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) " +
            "AND au.ResourceId IN (SELECT r.Id FROM Resource r INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id " +
            "INNER JOIN ResourceType rt ON rp.ResourceTypeId = rt.id WHERE rp.JobCallouts = 1 AND rp.Active = 1 AND rt.Name = :resourceType) " +
            "ORDER BY NEWID()";

    private static final String INVOICE_EXISTING_GROUP_BY_SQL =
            " GROUP BY " +
                    " Fault.fld_int_ID " +
                    " ,Resources.fld_int_ID " +
                    " ,PO.fld_str_SupplierID " +
                    " ,au.UserName , s.Id " +
                    " HAVING COUNT(sih.fld_str_PO_PONumber) = 1 " +
                    " ORDER BY NEWID() ";

    private static final String PPM_INVOICE_EXISTING_AND_COMPANY_ASSIGNED_AND_MATERIALS_LINE_GROUP_BY_SQL =
            " AND PO.fld_int_CompanyId IS NOT NULL" +
                    " AND sih.fld_int_SupplierInvoiceStatusId = 1" +
                    " AND sil.fld_int_SupplierInvoiceLineType = 1" +
                    " AND sil.fld_bit_IsMandatoryLine = 0" +
                    " GROUP BY " +
                    " Resources.fld_int_ID" +
                    " ,PSL.fld_int_ID" +
                    " ,PO.fld_str_SupplierID" +
                    " ,au.UserName, s.Id" +
                    " HAVING COUNT(sih.fld_str_PO_PONumber) > 0" +
                    " ORDER BY NEWID() ";

    private static final String PPM_INVOICE_EXISTING_AND_COMPANY_ASSIGNED_AND_LABOUR_LINE_GROUP_BY_SQL =
            " AND PO.fld_int_CompanyId IS NOT NULL" +
                    " AND sih.fld_int_SupplierInvoiceStatusId = 1" +
                    " AND sil.fld_int_SupplierInvoiceLineType = 2" +
                    " AND sil.fld_bit_IsMandatoryLine = 0" +
                    " GROUP BY " +
                    " Resources.fld_int_ID" +
                    " ,PSL.fld_int_ID" +
                    " ,PO.fld_str_SupplierID" +
                    " ,au.UserName, s.Id" +
                    " HAVING COUNT(sih.fld_str_PO_PONumber) > 0" +
                    " ORDER BY NEWID() ";

    private static final String PPM_INVOICE_EXISTING_AND_COMPANY_ASSIGNED_AND_MANDATORY_MATERIALS_LINE_GROUP_BY_SQL =
            " AND PO.fld_int_CompanyId IS NOT NULL" +
                    " AND sih.fld_int_SupplierInvoiceStatusId = 1" +
                    " AND sil.fld_int_SupplierInvoiceLineType = 1" +
                    " AND sil.fld_bit_NotApplicable = 0" +
                    " AND sil.fld_bit_IsMandatoryLine = 1" +
                    " AND silt.fld_bit_IsLabor = 0" +
                    " GROUP BY " +
                    " Resources.fld_int_ID" +
                    " ,PSL.fld_int_ID" +
                    " ,PO.fld_str_SupplierID" +
                    " ,au.UserName, s.Id" +
                    " HAVING COUNT(sih.fld_str_PO_PONumber) > 0" +
                    " ORDER BY NEWID() ";

    private static final String PPM_INVOICE_EXISTING_AND_COMPANY_ASSIGNED_AND_MANDATORY_LABOUR_LINE_GROUP_BY_SQL =
            " AND PO.fld_int_CompanyId IS NOT NULL" +
                    " AND sih.fld_int_SupplierInvoiceStatusId = 1" +
                    " AND sil.fld_int_SupplierInvoiceLineType = 2" +
                    " AND sil.fld_bit_NotApplicable = 0" +
                    " AND sil.fld_bit_IsMandatoryLine = 1" +
                    " AND silt.fld_bit_IsLabor = 1" +
                    " GROUP BY " +
                    " Resources.fld_int_ID" +
                    " ,PSL.fld_int_ID" +
                    " ,PO.fld_str_SupplierID" +
                    " ,au.UserName, s.Id" +
                    " HAVING COUNT(sih.fld_str_PO_PONumber) > 0" +
                    " ORDER BY NEWID() ";

    private static final String PPM_INVOICE_EXISTING_GROUP_BY_SQL =
            " AND sih.fld_int_SupplierInvoiceStatusId = 1" +
                    " AND silt.fld_int_Id IS NOT NULL" +
                    " GROUP BY " +
                    " Resources.fld_int_ID" +
                    " ,PSL.fld_int_ID" +
                    " ,PO.fld_str_SupplierID" +
                    " ,au.UserName, s.Id" +
                    " HAVING COUNT(sih.fld_str_PO_PONumber) > 0" +
                    " ORDER BY NEWID() ";

    private static final String PPM_INVOICE_EXISTING_WITH_DOCUMENTS_GROUP_BY_SQL =
            " AND sih.fld_int_SupplierInvoiceStatusId = 1" +
                    " AND sih.fld_str_InvoiceFilePath IS NOT NULL" +
                    " AND sih.fld_str_JobSheetFilePath IS NOT NULL" +
                    " AND silt.fld_int_Id IS NOT NULL" +
                    " GROUP BY " +
                    " Resources.fld_int_ID" +
                    " ,PSL.fld_int_ID" +
                    " ,PO.fld_str_SupplierID" +
                    " ,au.UserName, s.Id" +
                    " HAVING COUNT(sih.fld_str_PO_PONumber) > 0" +
                    " ORDER BY NEWID() ";


    private static final String PPM_NO_INVOICE_EXISTING_GROUP_BY_SQL =
            " GROUP BY " +
                    " Resources.fld_int_ID" +
                    " ,PSL.fld_int_ID" +
                    " ,PO.fld_str_SupplierID " +
                    " ,au.UserName, s.Id " +
                    " HAVING COUNT(sih.fld_str_PO_PONumber) = 0" +
                    " ORDER BY NEWID() ";

    private static final String PPM_NO_INVOICE_EXISTING_AND_COMPANY_ASSIGNED_GROUP_BY_SQL =
            " AND PO.fld_int_CompanyId IS NOT NULL" +
                    " GROUP BY " +
                    " Resources.fld_int_ID" +
                    " ,PSL.fld_int_ID" +
                    " ,PO.fld_str_SupplierID " +
                    " ,au.UserName, s.Id " +
                    " HAVING COUNT(sih.fld_str_PO_PONumber) = 0" +
                    " ORDER BY NEWID() ";

    private static final String INVOICE_EXISTING_AND_COMPANY_ASSIGNED_GROUP_BY_SQL =
            " AND PO.fld_int_CompanyId IS NOT NULL" +
                    " AND sih.fld_str_InvoiceFilePath IS NOT NULL" +
                    " AND sih.fld_str_InvoiceNumber <> ''" +
                    " GROUP BY " +
                    " Fault.fld_int_ID " +
                    " ,Resources.fld_int_ID " +
                    " ,PO.fld_str_SupplierID " +
                    " ,au.UserName, s.Id " +
                    " HAVING COUNT(sih.fld_str_PO_PONumber) = 1 " +
                    " ORDER BY NEWID() ";

    private static final String NO_INVOICE_EXISTING_AND_COMPANY_ASSIGNED_GROUP_BY_SQL =
            " AND PO.fld_int_CompanyId IS NOT NULL" +
                    " AND Fault.fld_bit_QuoteJob = 0" +
                    " GROUP BY " +
                    " Fault.fld_int_ID " +
                    " ,Resources.fld_int_ID " +
                    " ,PO.fld_str_SupplierID " +
                    " ,au.UserName, s.Id " +
                    " HAVING COUNT(sih.fld_str_PO_PONumber) = 0 " +
                    " ORDER BY NEWID() ";

    private static final String NO_INVOICE_EXISTING_GROUP_BY_SQL =
            " GROUP BY " +
                    " Fault.fld_int_ID " +
                    " ,Resources.fld_int_ID " +
                    " ,PO.fld_str_SupplierID " +
                    " ,au.UserName, s.Id " +
                    " HAVING COUNT(sih.fld_str_PO_PONumber) = 0 " +
                    " ORDER BY NEWID() ";

    private static final String NO_SUPPLIER_INVOICE_EXISTING_GROUP_BY_SQL =
            " GROUP BY " +
                    " Resources.fld_int_ID " +
                    " ,PO.fld_str_SupplierID " +
                    " ,au.UserName " +
                    " ,Fault.fld_int_ID, s.Id, PO.fld_cur_costexpected " +
                    " HAVING COUNT(sih.fld_str_PO_PONumber) = 0 " +
                    " AND PO.fld_cur_costexpected + SUM(ISNULL(SuppPO.fld_cur_costexpected,0)) > 0" +
                    " ORDER BY NEWID() ";

    private static final String NO_SUPPLIER_INVOICE_EXISTING_AND_COMPANY_ASSIGNED_GROUP_BY_SQL =
            " AND PO.fld_int_CompanyId IS NOT NULL" +
                    " AND sih.fld_str_InvoiceFilePath IS NULL" +
                    " GROUP BY " +
                    " Resources.fld_int_ID " +
                    " ,PO.fld_str_SupplierID " +
                    " ,au.UserName " +
                    " ,Fault.fld_int_ID, s.Id, PO.fld_cur_costexpected " +
                    " HAVING COUNT(sih.fld_str_PO_PONumber) = 0 " +
                    " AND PO.fld_cur_costexpected + SUM(ISNULL(SuppPO.fld_cur_costexpected,0)) > 0" +
                    " ORDER BY NEWID() ";

    private static final String SUPPLIER_INVOICE_EXISTING_AND_COMPANY_ASSIGNED_GROUP_BY_SQL =
            " AND PO.fld_int_CompanyId IS NOT NULL" +
                    " AND sih.fld_int_SupplierInvoiceStatusId = 1" +
                    " AND sih.fld_int_InvoiceProcessStepId = 1" +
                    " AND sih.fld_str_InvoiceNumber <> ''" +
                    " GROUP BY " +
                    " Resources.fld_int_ID " +
                    " ,PO.fld_str_SupplierID " +
                    " ,au.UserName " +
                    " ,Fault.fld_int_ID, s.Id " +
                    " HAVING COUNT(sih.fld_str_PO_PONumber) >= 1 " +
                    " ORDER BY NEWID() ";

    private static final String SUPPLIER_INVOICE_EXISTING_GROUP_BY_SQL =
            " AND sih.fld_int_SupplierInvoiceStatusId = 1" +
                    " AND sih.fld_int_InvoiceProcessStepId = 1" +
                    " AND sil.fld_bit_InvoiceLineFulfilled IS NULL" +
                    " GROUP BY " +
                    " Resources.fld_int_ID " +
                    " ,PO.fld_str_SupplierID " +
                    " ,au.UserName " +
                    " ,Fault.fld_int_ID, s.Id " +
                    " HAVING COUNT(sih.fld_str_PO_PONumber) >= 1 " +
                    " ORDER BY NEWID() ";

    private static final String GET_USER_WITH_FIXED_JOB_SQL = "SELECT TOP(1) NEWID() Id, j.JobReference, ms.ApplicationUserName UserName, ms.ResourceId ResourceId, s.Id SiteId FROM Job j "
            + "JOIN JobStatus js ON j.JobStatusId = js.Id "
            + "JOIN Site s ON j.SiteId = s.Id "
            + "LEFT JOIN JobFeedback jf ON j.Id = jf.JobId "
            + "JOIN uvw_ManagerSites ms ON s.Id = ms.SiteId "
            + "WHERE js.Name = 'Fixed' "
            + "AND jf.JobId IS NULL "
            + "AND j.UpdatedOn > DATEADD(DAY, -30, GETUTCDATE()) "
            + "AND ms.ResourceProfileName = :profileName "
            + "ORDER BY NEWID()";

    private static final String CAPITAL_PART = " AND poaa.POID IN (SELECT prl.fld_int_POID FROM %portaldb.tblPartsRequestLine prl INNER JOIN %portaldb.tblPartCodes pc ON pc.fld_str_PartCode = prl.fld_str_PartCode WHERE fld_bit_isCapitalEquipment = :capital) ";

    /**
     * Fetch ResourceId, UserName and JobReference entity who has jobs in the awaiting acceptance state and profile of profileName
     * @param profileName
     * @return UserJob
     */
    public UserJob getUserWithAwaitingAcceptanceJob(String profileName) {
        String sql = GET_USER_AND_AWAITING_ACCEPTANCE_JOB_SQL;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getUserWithAwaitingAcceptanceJob: " + sql.replace(":profileName", "'" + profileName + "'"));

        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        try {
            query.setParameter("profileName", profileName);
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ResourceId, UserName and JobReference entity who has orders awaiting invoice and profile of profileName
     * @param profileName
     * @return UserJob
     */

    private Query ordersAwaitingInvoiceQuery(String profileName, String sql, String isExistingInvoice) {
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        String loggerSql = sql;
        loggerSql = loggerSql.replaceAll(":profileName", "'" + profileName + "'");
        loggerSql = loggerSql.replaceAll(":existingInvoice", isExistingInvoice);
        logger.debug(loggerSql);
        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        query.setParameter("profileName", profileName);
        query.setParameter("existingInvoice", isExistingInvoice);
        return query;
    }

    private Query PPMOrdersAwaitingInvoiceQuery(String profileName, String sql) {
        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        query.setParameter("profileName", profileName);
        return query;
    }

    private Query supplierOrdersAwaitingInvoiceQuery(String profileName, String sql) {
        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        query.setParameter("profileName", profileName);
        return query;
    }

    public UserJob getUserWithOrdersAwaitingInvoiceJob(String profileName, String jobStatus) throws Exception {
        String sql = null;
        String isExistingInvoice = null;
        if ("Orders Awaiting Invoice With Existing Invoice".equalsIgnoreCase(jobStatus)) {
            sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_USER_WITH_ORDERS_AWAITING_INVOICE_SQL.sql") + INVOICE_EXISTING_GROUP_BY_SQL;
            isExistingInvoice = "Yes";
        } else if ("Complete / Orders Awaiting Invoice With Existing Invoice without documents".equalsIgnoreCase(jobStatus)) {
            sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_USER_WITH_ORDERS_AWAITING_INVOICE_SQL.sql")
                    + " AND sih.fld_str_InvoiceFilePath IS NULL AND sih.fld_str_JobSheetFilePath IS NULL " +  INVOICE_EXISTING_GROUP_BY_SQL;
            isExistingInvoice = "Yes";
        } else if ("Complete / Orders Awaiting Invoice With Existing Invoice".equalsIgnoreCase(jobStatus)) {
            sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_USER_WITH_ORDERS_AWAITING_INVOICE_SQL.sql") + INVOICE_EXISTING_AND_COMPANY_ASSIGNED_GROUP_BY_SQL;
            isExistingInvoice = "Yes";
        } else if ("Complete / Orders Awaiting Invoice With No Invoice".equalsIgnoreCase(jobStatus)) {
            sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_USER_WITH_ORDERS_AWAITING_INVOICE_SQL.sql") + NO_INVOICE_EXISTING_GROUP_BY_SQL;
            isExistingInvoice = "No";
        } else if ("Orders Awaiting Invoice With template uploaded and No Invoice".equalsIgnoreCase(jobStatus)) {
            sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_USER_WITH_TEMPLATE_AND_ORDERS_AWAITING_INVOICE.sql");
            isExistingInvoice = "Yes";
        } else if ("Orders Awaiting Invoice With No Invoice and Company Assigned".equalsIgnoreCase(jobStatus)) {
            sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_USER_WITH_ORDERS_AWAITING_INVOICE_SQL.sql") + NO_INVOICE_EXISTING_AND_COMPANY_ASSIGNED_GROUP_BY_SQL;
            isExistingInvoice = "No";
        } else {
            throw new Exception("Cannot find job status " + jobStatus);
        }

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        String loggerSql = sql;
        loggerSql = loggerSql.replaceAll(":profileName", "'" + profileName + "'");
        logger.debug(loggerSql);

        try {
            Query query = ordersAwaitingInvoiceQuery(profileName, sql, isExistingInvoice);
            return (UserJob) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }

    }

    public UserJob getUserWithPPMOrdersAwaitingInvoiceJob(String profileName, String jobStatus) throws Exception {
        String sql = null;
        if ("Complete / PPM Orders Awaiting Invoice With Existing Invoice".equalsIgnoreCase(jobStatus)) {
            sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_USER_WITH_PPM_ORDERS_AWAITING_INVOICE_SQL.sql") + PPM_INVOICE_EXISTING_GROUP_BY_SQL;
        } else if ("Complete / PPM Orders Awaiting Invoice With Existing Invoice and Documents".equalsIgnoreCase(jobStatus)) {
            sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_USER_WITH_PPM_ORDERS_AWAITING_INVOICE_SQL.sql") + PPM_INVOICE_EXISTING_WITH_DOCUMENTS_GROUP_BY_SQL;
        } else if ("Complete / PPM Orders Awaiting Invoice With No Invoice".equalsIgnoreCase(jobStatus)) {
            sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_USER_WITH_PPM_ORDERS_AWAITING_INVOICE_SQL.sql") + PPM_NO_INVOICE_EXISTING_GROUP_BY_SQL;
        } else if ("Complete / PPM Orders Awaiting Invoice With Existing Invoice and has a Materials Line".equalsIgnoreCase(jobStatus)) {
            sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_USER_WITH_PPM_ORDERS_AWAITING_INVOICE_SQL.sql") + PPM_INVOICE_EXISTING_AND_COMPANY_ASSIGNED_AND_MATERIALS_LINE_GROUP_BY_SQL;
        } else if ("Complete / PPM Orders Awaiting Invoice With Existing Invoice and has a Labour Line".equalsIgnoreCase(jobStatus)) {
            sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_USER_WITH_PPM_ORDERS_AWAITING_INVOICE_SQL.sql") + PPM_INVOICE_EXISTING_AND_COMPANY_ASSIGNED_AND_LABOUR_LINE_GROUP_BY_SQL;
        } else if ("Complete / PPM Orders Awaiting Invoice With Existing Invoice and has a Mandatory Materials Line".equalsIgnoreCase(jobStatus)) {
            sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_USER_WITH_PPM_ORDERS_AWAITING_INVOICE_SQL.sql") + PPM_INVOICE_EXISTING_AND_COMPANY_ASSIGNED_AND_MANDATORY_MATERIALS_LINE_GROUP_BY_SQL;
        } else if ("Complete / PPM Orders Awaiting Invoice With Existing Invoice and has a Mandatory Labour Line".equalsIgnoreCase(jobStatus)) {
            sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_USER_WITH_PPM_ORDERS_AWAITING_INVOICE_SQL.sql") + PPM_INVOICE_EXISTING_AND_COMPANY_ASSIGNED_AND_MANDATORY_LABOUR_LINE_GROUP_BY_SQL;
        } else if ("Complete / PPM Orders Awaiting Invoice With No Invoice and Company Assigned".equalsIgnoreCase(jobStatus)) {
            sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_USER_WITH_PPM_ORDERS_AWAITING_INVOICE_SQL.sql") + PPM_NO_INVOICE_EXISTING_AND_COMPANY_ASSIGNED_GROUP_BY_SQL;
        } else {
            throw new Exception("Cannot find job status " + jobStatus);
        }

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        String loggerSql = sql;
        loggerSql = loggerSql.replaceAll(":profileName", "'" + profileName + "'");
        logger.debug(loggerSql);

        try {
            Query query = PPMOrdersAwaitingInvoiceQuery(profileName, sql);
            return (UserJob) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }

    }

    public UserJob getSupplierUserWithOrdersAwaitingInvoiceJob(String profileName, String jobStatus) throws Exception {
        String sql = null;
        if("In Progress / Parts Requested / Awaiting Invoice With No Invoice".equalsIgnoreCase(jobStatus)) {
            sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_SUPPLIER_USER_WITH_ORDERS_AWAITING_INVOICE_SQL.sql") + NO_SUPPLIER_INVOICE_EXISTING_GROUP_BY_SQL;

        } else if ("In Progress / Parts Requested / Awaiting Invoice With Existing Invoice".equalsIgnoreCase(jobStatus)) {
            sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_SUPPLIER_USER_WITH_ORDERS_AWAITING_INVOICE_SQL.sql") + SUPPLIER_INVOICE_EXISTING_GROUP_BY_SQL;
        } else if ("Supplier Orders Awaiting Invoice With No Invoice and has a company assigned".equalsIgnoreCase(jobStatus)) {
            sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_SUPPLIER_USER_WITH_ORDERS_AWAITING_INVOICE_SQL.sql") + NO_SUPPLIER_INVOICE_EXISTING_AND_COMPANY_ASSIGNED_GROUP_BY_SQL;
        } else if ("Supplier Orders Awaiting Invoice With Existing Invoice and has a company assigned".equalsIgnoreCase(jobStatus)) {
            sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_SUPPLIER_USER_WITH_ORDERS_AWAITING_INVOICE_SQL.sql") + SUPPLIER_INVOICE_EXISTING_AND_COMPANY_ASSIGNED_GROUP_BY_SQL;
        } else {
            throw new Exception("Cannot find suitable data");
        }
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getSupplierUserWithOrdersAwaitingInvoiceJob: " + sql.replace(":profileName", "'" + profileName + "'"));

        try {
            Query query = supplierOrdersAwaitingInvoiceQuery(profileName, sql);
            return (UserJob) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public UserJob getUserWithInvoicesAwaitingApprovalJob() throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_USER_WITH_INVOICE_AWAITING_APPROVAL_SQL.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        Query query = entityManager.createNativeQuery(sql, UserJob.class);

        try {
            logger.debug("getUserWithInvoicesAwaitingApprovalJob: " + sql);
            return (UserJob) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ResourceId, UserName and JobReference entity who has PPM jobs available and profile of profileName
     * @param profileName
     * @return UserJob
     */
    public UserJob getUserWithPPMJob(String profileName, boolean withCertificate){
        String sql = GET_USER_AND_PPM_JOB_SQL;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        if (withCertificate) {
            sql = sql.replaceAll("ORDER BY", " AND cc.Id IS NOT NULL ORDER BY");
        } else {
            sql = sql.replaceAll("ORDER BY", " AND cc.Id IS NULL AND fld_bit_CertificateRequired = 0 ORDER BY");
        }

        logger.debug(" getUserWithPPMJob " + sql.replace(":profileName", quote(profileName)));

        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        try {
            query.setParameter("resourceType", profileName);
            UserJob userJob = (UserJob) query.getSingleResult();

            return userJob;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ResourceId, JobReference, siteId and UserName entity which has got PPM job with invoice
     * @param ppmTypeName
     * @return
     */
    public UserJob getPPMJobWithInvoice(String userProfileName, String ppmTypeName) {
        String sql = "SELECT Top(1) NEWID() Id, r.Id ResourceId, ppm.Id JobReference, au.UserName UserName, j.SiteId SiteId"
                + " FROM %portaldb.tblPO po"
                + " INNER JOIN %helpdeskdb.Ppm ppm ON ppm.Id = po.fld_int_PpmId"
                + " INNER JOIN %helpdeskdb.PPMType pt ON pt.Id = ppm.PpmTypeId"
                + " INNER JOIN %helpdeskdb.Job j ON j.PpmId = ppm.Id"
                + " INNER JOIN %helpdeskdb.Resource r ON j.SpecificResourceRequestId = r.Id"
                + " INNER JOIN %helpdeskdb.ApplicationUser au ON au.ResourceId = r.Id"
                + " INNER JOIN %helpdeskdb.UserProfile up ON up.Id = au.UserProfileId"
                + " WHERE up.Name = ':userProfileName'"
                + " AND pt.Name = ':ppmTypeName'"
                + " ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = sql.replace(":ppmTypeName", ppmTypeName).replace(":userProfileName", userProfileName);
        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        try {
            logger.debug("getPPMJobWithNoInvoice : " + sql);
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            return null;
        }
    }

    public UserJob getUserWithFundingRequestJobInStatus(String profileName, String quoteApprovalStatusName) throws Exception {
        String sql;
        sql = FileHelper.getResourceFile("sql/select/", "GET_USER_AND_FUNDING_REQUEST_BY_STATUS_JOB_SQL.sql");

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        Query query = entityManager.createNativeQuery(sql, UserJob.class);

        logger.debug("getUserWithFundingRequestJobInStatus : " + sql);

        try {
            query.setParameter("quoteApprovalStatusName", quoteApprovalStatusName);
            query.setParameter("profileName", profileName);
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    public UserJob getUserWithQuoteJobInStatus(Map<String, Object> queryMap) throws Exception {
        try {
            String sql = null;
            sql = FileHelper.getResourceFile("sql/select/", "GET_USER_QUOTE_IN_STATUS_QUOTE_RESOURCE.sql");

            sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

            sql = String.format(sql, queryMap.get("quoteJobApprovalStatusId"), queryMap.get("approvalStatusId"));

            String loggerSql = sql;
            loggerSql = loggerSql.replaceAll(":profileName", "'" + queryMap.get("profileName").toString() + "'");
            loggerSql = loggerSql.replaceAll(":fundingRoute", "'" +  queryMap.get("fundingRoute").toString() + "'");
            loggerSql = loggerSql.replaceAll(":budget", "'" +  queryMap.get("budget").toString() + "'");
            loggerSql = loggerSql.replaceAll(":multiQuote", "'" +  queryMap.get("multiQuote").toString() + "'");
            loggerSql = loggerSql.replaceAll(":useResourceTypeName", "'" +   Boolean.valueOf(queryMap.get("useResourceTypeName").toString()) + "'");
            loggerSql = loggerSql.replaceAll(":resourceTypeName", "'" +  queryMap.get("resourceTypeName").toString() + "'");
            loggerSql = loggerSql.replaceAll(":submitted", "'" +  queryMap.get("quotesSubmitted").toString() + "'");
            loggerSql = loggerSql.replaceAll(":aboveThreshhold", "'" +  queryMap.get("aboveThreshhold").toString() + "'");

            logger.debug(loggerSql);

            Query query = entityManager.createNativeQuery(sql, UserJob.class);

            query.setParameter("profileName", queryMap.get("profileName").toString());
            query.setParameter("fundingRoute", queryMap.get("fundingRoute").toString());
            query.setParameter("budget", queryMap.get("budget").toString());
            query.setParameter("multiQuote", queryMap.get("multiQuote").toString());
            query.setParameter("useResourceTypeName",  Boolean.valueOf(queryMap.get("useResourceTypeName").toString()));
            query.setParameter("resourceTypeName", queryMap.get("resourceTypeName").toString());
            query.setParameter("submitted", queryMap.get("quotesSubmitted").toString());
            query.setParameter("aboveThreshhold", queryMap.get("aboveThreshhold").toString());

            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    public UserJob getUserforQuoteJob(int jobReference, Map<String, Object> queryMap) throws Exception {
        try {
            String sql = null;

            sql = FileHelper.getResourceFile("sql/select/", "GET_USER_QUOTE_RESOURCE.sql");


            sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

            String loggerSql = sql;
            loggerSql = loggerSql.replaceAll(":jobReference",  String.valueOf(jobReference));

            loggerSql = loggerSql.replaceAll(":profileName", "'" + queryMap.get("profileName").toString() + "'");
            loggerSql = loggerSql.replaceAll(":useResourceTypeName", "'" +   Boolean.valueOf(queryMap.get("useResourceTypeName").toString()) + "'");
            loggerSql = loggerSql.replaceAll(":resourceTypeName", "'" +  queryMap.get("resourceTypeName").toString() + "'");

            logger.debug("getUserforQuoteJob " + loggerSql);

            Query query = entityManager.createNativeQuery(sql, UserJob.class);

            query.setParameter("jobReference", jobReference);

            query.setParameter("profileName", queryMap.get("profileName").toString());
            query.setParameter("useResourceTypeName",  Boolean.valueOf(queryMap.get("useResourceTypeName").toString()));
            query.setParameter("resourceTypeName", queryMap.get("resourceTypeName").toString());

            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    /**
     * Fetch ResourceId, UserName and JobReference entity who has jobs in the awaiting acceptance state and profile of profileName
     * @param profileName
     * @return UserJob
     */
    public UserJob getUserWithOpenJob(String profileName) {
        String sql = GET_USER_AND_OPEN_JOB_SQL;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getUserWithOpenJob: " + sql.replace(":profileName", "'" + profileName + "'"));
        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        try {
            query.setParameter("profileName", profileName);
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            return null;
        }
    }

    private String replaceResourceAssignmentStatus(String sql, String resourceAssignmentStatus) {
        if (resourceAssignmentStatus.contains(",")) {
            String statusList = Arrays.toString(quote(resourceAssignmentStatus.split(","))).replace("[", "").replace("]", "");
            return sql.replace("AND ras.Name = :resourceAssignmentStatus",  String.format("AND ras.Name IN (%s)", statusList));
        } else {
            return sql.replace(":resourceAssignmentStatus", quote(resourceAssignmentStatus));
        }
    }

    /**
     * Fetch ResourceId, UserName and JobReference entity who has jobs in the awaiting acceptance state and profile of profileName
     * @param profileName
     * @param resourceAssingmentStatus
     * @param jobStatus
     * @return UserJob
     * @throws Exception
     */
    public UserJob getUserWithJobSingleResource(String profileName, String resourceAssignmentStatus, String jobStatus, Boolean useResourceTypeName,  String resourceTypeName, String transferJob) throws Exception {
        String sql = null;
        sql = FileHelper.getResourceFile("sql/select/", "GET_USER_AND_JOB_SINGLE_RESOURCE.sql");

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        if (transferJob.equalsIgnoreCase("with")) {
            sql = sql.replaceAll(":jobTransferFilter", " AND tj.Id IS NOT NULL ");
        } else if (transferJob.equalsIgnoreCase("with out")) {
            sql = sql.replaceAll(":jobTransferFilter", " AND tj.Id IS NULL ");
        } else {
            sql = sql.replaceAll(":jobTransferFilter", " ");
        }

        sql = replaceResourceAssignmentStatus(sql, resourceAssignmentStatus);

        String log = sql.replace(":profileName", quote(profileName))
                .replace(":jobStatus", quote(jobStatus))
                .replace(":useResourceTypeName", String.valueOf(useResourceTypeName ? 1 : 0))
                .replace(":resourceTypeName", quote(resourceTypeName));

        logger.debug("getUserWithJobSingleResource : " + log);

        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        try {
            query.setParameter("profileName", profileName);
            query.setParameter("jobStatus", jobStatus);
            query.setParameter("useResourceTypeName", useResourceTypeName);
            query.setParameter("resourceTypeName", resourceTypeName);
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ResourceId, UserName and JobReference entity who has jobs in the awaiting acceptance state and profile of profileName
     * @param profileName
     * @param resourceAssingmentStatus
     * @param jobStatus
     * @return UserJob
     * @throws Exception
     */
    public UserJob getUserWithJobSingleResourceWithGas(String profileName, String resourceAssignmentStatus, String jobStatus, Boolean useResourceTypeName, String resourceTypeName, Boolean inPlant,
            Boolean withAsset)
                    throws Exception {
        String sql = null;
        sql = FileHelper.getResourceFile("sql/select/", "GET_USER_AND_JOB_SINGLE_RESOURCE_GAS_SQL.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = replaceResourceAssignmentStatus(sql, resourceAssignmentStatus);

        String loggerSql = sql;
        loggerSql = loggerSql.replaceAll(":profileName", "'" + profileName + "'");
        loggerSql = loggerSql.replaceAll(":jobStatus", "'" + jobStatus + "'");
        loggerSql = loggerSql.replaceAll(":useResourceTypeName", "'" + useResourceTypeName + "'");
        loggerSql = loggerSql.replaceAll(":resourceTypeName", "'" + resourceTypeName + "'");
        loggerSql = loggerSql.replaceAll(":inPlant", "'" + inPlant + "'");
        loggerSql = loggerSql.replaceAll(
                ":withAsset",
                "'" + withAsset
                + "'");

        logger.debug("getUserWithJobSingleResourceWithGas : ".concat(loggerSql));
        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        try {
            query.setParameter("profileName", profileName);
            query.setParameter("jobStatus", jobStatus);
            query.setParameter("useResourceTypeName", useResourceTypeName);
            query.setParameter("resourceTypeName", resourceTypeName);
            query.setParameter("inPlant", inPlant);
            query.setParameter("withAsset", withAsset);
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    /**
     * Fetch ResourceId, UserName and JobReference entity who has jobs in the awaiting acceptance state and profile of profileName
     * @param profileName
     * @param resourceAssingmentStatus
     * @param jobStatus
     * @return UserJob
     * @throws Exception
     */
    public UserJob getReturningUserWithJobSingleResourceWithGas(String profileName, String resourceAssignmentStatus, String jobStatus, Boolean useResourceTypeName, String resourceTypeName, Boolean gasUsage,
            Boolean leakCheck)
                    throws Exception {
        String sql = null;
        sql = FileHelper.getResourceFile("sql/select/", "GET_USER_AND_JOB_SINGLE_RESOURCE_GAS_USAGE_SQL.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = replaceResourceAssignmentStatus(sql, resourceAssignmentStatus);

        String loggerSql = sql;
        loggerSql = loggerSql.replaceAll(":profileName", "'" + profileName + "'");
        loggerSql = loggerSql.replaceAll(":jobStatus", "'" + jobStatus + "'");
        loggerSql = loggerSql.replaceAll(":useResourceTypeName", "'" + useResourceTypeName + "'");
        loggerSql = loggerSql.replaceAll(":resourceTypeName", "'" + resourceTypeName + "'");
        loggerSql = loggerSql.replaceAll(":gasUsage", "'" + gasUsage + "'");
        loggerSql = loggerSql.replaceAll(":leakCheck", "'" + leakCheck + "'");

        logger.debug("getReturningUserWithJobSingleResourceWithGas : ".concat(loggerSql));
        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        try {
            query.setParameter("profileName", profileName);
            query.setParameter("jobStatus", jobStatus);
            query.setParameter("useResourceTypeName", useResourceTypeName);
            query.setParameter("resourceTypeName", resourceTypeName);
            query.setParameter("gasUsage", gasUsage);
            query.setParameter("leakCheck", leakCheck);
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    /**
     * Fetch ResourceId, UserName and JobReference entity who has jobs in the awaiting acceptance state and profile of profileName
     * @param profileName
     * @param resourceAssingmentStatus
     * @param jobStatus
     * @return UserJob
     * @throws Exception
     */
    public UserJob getReturningUserWithJobSingleResourceWithGasRequired(String profileName, String resourceAssignmentStatus, String jobStatus, Boolean useResourceTypeName, String resourceTypeName, Boolean gasUsage,
            Boolean leakCheck, Boolean isRequiredToReturn)
                    throws Exception {
        String sql = null;
        sql = FileHelper.getResourceFile("sql/select/", "GET_USER_AND_JOB_SINGLE_RESOURCE_GAS_USAGE_RETURNING_SQL.sql");
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = replaceResourceAssignmentStatus(sql, resourceAssignmentStatus);

        String loggerSql = sql;
        loggerSql = loggerSql.replaceAll(":profileName", "'" + profileName + "'");
        loggerSql = loggerSql.replaceAll(":jobStatus", "'" + jobStatus + "'");
        loggerSql = loggerSql.replaceAll(":useResourceTypeName", "'" + useResourceTypeName + "'");
        loggerSql = loggerSql.replaceAll(":resourceTypeName", "'" + resourceTypeName + "'");
        loggerSql = loggerSql.replaceAll(":gasUsage", "'" + gasUsage + "'");
        loggerSql = loggerSql.replaceAll(":leakCheck", "'" + leakCheck + "'");
        loggerSql = loggerSql.replaceAll(":isRequiredToReturn", "'" + isRequiredToReturn + "'");

        logger.debug("getReturningUserWithJobSingleResourceWithGas : ".concat(loggerSql));
        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        try {
            query.setParameter("profileName", profileName);
            query.setParameter("jobStatus", jobStatus);
            query.setParameter("useResourceTypeName", useResourceTypeName);
            query.setParameter("resourceTypeName", resourceTypeName);
            query.setParameter("gasUsage", gasUsage);
            query.setParameter("leakCheck", leakCheck);
            query.setParameter("isRequiredToReturn", isRequiredToReturn);
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    /**
     * Fetch ResourceId, UserName and JobReference entity for a given job reference
     * @param profileName
     * @param resourceAssingmentStatus
     * @param jobStatus
     * @return UserJob
     * @throws Exception
     */
    public UserJob getUserForJobResource(Integer jobReference, String profileName, Boolean useResourceTypeName,  String resourceTypeName) throws Exception {
        String sql = null;
        sql = FileHelper.getResourceFile("sql/select/", "GET_USER_FOR_JOB_RESOURCE.sql");

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        String debugStr = sql.replaceAll(":jobReference", jobReference.toString()).replaceAll(":profileName", "'" + profileName + "'").replaceAll(":useResourceTypeName", "'" + useResourceTypeName + "'").replaceAll(":resourceTypeName", "'" + resourceTypeName + "'");
        logger.debug("getUserForJobResource: " + debugStr);
        try {
            query.setParameter("jobReference", jobReference);
            query.setParameter("profileName", profileName);
            query.setParameter("useResourceTypeName", useResourceTypeName);
            query.setParameter("resourceTypeName", resourceTypeName);
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ResourceId, UserName and JobReference entity who has jobs in the awaiting acceptance state and profile of profileName
     * @param profileName
     * @param resourceAssingmentStatus
     * @param jobStatus
     * @return UserJob
     */
    public UserJob getUserWithJobMultiResource(String profileName, String resourceAssignmentStatus, String jobStatus) {
        String sql = GET_USER_AND_JOB_MULTI_RESORUCE_SQL;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        sql = replaceResourceAssignmentStatus(sql, resourceAssignmentStatus);

        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        logger.debug(sql);
        try {
            query.setParameter("profileName", profileName);
            query.setParameter("jobStatus", jobStatus);
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ResourceId, UserName and JobReference entity who has jobs in the awaiting quote approval state and profile of profileName
     * @param profileName
     * @return UserJob
     */
    public UserJob getUserWithOpenQuoteApprovalJob(String profileName) {
        String sql = GET_USER_AND_OPEN_QUOTE_REQUEST_JOB_SQL;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        try {
            query.setParameter("profileName", profileName);
            logger.debug(sql);
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ResourceId, UserName and JobReference entity who has jobs in the awaiting state and profile of profileName
     * @param profileName
     * @param jobType
     * @return UserJob
     */
    public UserJob getUserWithAwaitingJobsWithOneResource(String profileName, String jobType) {
        String sql = GET_USER_AND_AWAITING_ACCEPTANCE_JOB_WITH_ONE_RESOURCE_SQL;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        try {
            query.setParameter("profileName", profileName);
            query.setParameter("jobType", jobType);
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ResourceId, UserName and JobReference entity who has jobs in the Parts Awaiting Review state and assigned to the profile of profileName
     * @param profileName
     * @param jobType
     * @param jobStatus
     * @return UserJob
     * @throws Exception
     */
    public UserJob getUserWithJobPartsAwaitingReview(String profileName, String jobType, String jobStatus) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_USER_AND_JOB_PARTS_AWAITING_REVIEW.sql");

        if (jobStatus.equalsIgnoreCase("Capital Parts Awaiting Approval")) {
            sql = sql + CAPITAL_PART.replace(":capital", "1");

        } else if (jobStatus.equalsIgnoreCase("Non Capital Parts Awaiting Approval")) {
            sql = sql + CAPITAL_PART.replace(":capital", "0");
        }

        sql = sql + " AND r.ResourceProfileId IN (SELECT Id FROM ResourceProfile WHERE Name = ':rp' OR Alias = ':rp') ".replaceAll(":rp", profileName);
        sql = sql + " GROUP BY  poaa.[Job Number] , au.UserName, ra.ResourceId, s.Id HAVING COUNT(poaa.[Job Number]) = 1 ORDER BY NEWID() ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getUserWithJobPartsAwaitingReview : " + sql);

        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        try {
            query.setParameter("jobType", jobType);
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            return null;
        }
    }

    public UserJob getUserJob(String jobStatus, String profileName, String jobType, String quoteType) throws Exception {
        switch (jobStatus) {
        case "Open" :
            return this.getUserWithOpenJob(profileName);

        case "Awaiting Acceptance" :
            if("reactive".equalsIgnoreCase(jobType)) {
                return this.getUserWithAwaitingJobsWithOneResource(profileName, jobType);
            } else {
                return this.getUserWithAwaitingAcceptanceJob(profileName);
            }

        case "Open Quote Requests" :
            return this.getUserWithOpenQuoteApprovalJob(profileName);

        case "PPM Job":
            return  this.getUserWithPPMJob(profileName, false);

        default :
            throw new Exception("Cannot find user with jobs " + jobStatus);
        }
    }

    /**
     * Fetch ResourceId, UserName and JobReference entity who has jobs in the fixed state and profile of profileName
     * @param profileName
     * @return UserJob
     */
    public UserJob getUserWithFixedJob(String profileName) {
        String sql = GET_USER_WITH_FIXED_JOB_SQL;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        try {
            query.setParameter("profileName", profileName);
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ResourceId, UserName and JobReference entity who has jobs with invoice in the Awaiting Final Approval state and assigned to the profile of profileName
     * @param profileName
     * @Param jobStatus
     * @return UserJob
     * @throws Exception
     */
    public UserJob getUserWithInvoiceAwaitingFinalApprovalJob(String profileName, String jobStatus, String fundingRoute) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_USER_WITH_INVOICE_AWAITING_FINAL_APPROVAL_SQL.sql");
        if ("Non Supply Only Invoice Awaiting Final Approval".equalsIgnoreCase(jobStatus)) {
            sql = sql + " AND iafcte.SupplierId NOT LIKE 'SO:%' ";
        } else if ("Supply Only Invoice Awaiting Final Approval".equalsIgnoreCase(jobStatus)) {
            sql = sql + " AND iafcte.SupplierId LIKE 'SO:%' ";
        }

        if (fundingRoute != null && !fundingRoute.equalsIgnoreCase("N/A")) {
            sql = sql.replace("WHERE sis.fld_str_Name", String.format("WHERE po.FundingRouteName = '%s' AND sis.fld_str_Name", fundingRoute));
        }
        sql = sql + " ORDER BY NEWID() ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getUserWithInvoiceAwaitingFinalApprovalJob: " + sql.replace(":profileName", quote(profileName)));

        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        try {
            query.setParameter("profileName", profileName);
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ResourceId, UserName and JobReference entity who has jobs with invoice in the Awaiting Approval state and assigned to the profile of profileName
     * @param profileName
     * @Param jobStatus
     * @return UserJob
     * @throws Exception
     */
    public UserJob getUserWithInvoiceAwaitingApprovalJob(String profileName, String jobStatus, String fundingRoute) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_PORTAL_USER_WITH_INVOICE_AWAITING_APPROVAL_SQL.sql");
        if (fundingRoute != null && !fundingRoute.equalsIgnoreCase("N/A")) {
            sql = sql.replace("WHERE ia.ApprovalTypeId", String.format("WHERE ia.ApprovalTypeId", fundingRoute));
        }

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getUserWithInvoiceAwaitingApprovalJob: ");

        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        try {
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            return null;
        }
    }

    public UserJob getQuoteUserJob(Map<String, Object> queryMap) throws Exception {
        String approvalstatus = queryMap.get("approvalStatus").toString();
        switch (approvalstatus) {
        case "ItqAwaitingAcceptance":
        case "AwaitingSeniorManagerApproval" :
        case "InResourceQuery":
        case "QueryResourcePending":
        case "AwaitingManagerApproval":
        case "AwaitingApproval":
        case "QueryResourceAnswered":
        case "AwaitingResourceAssignment":
        case "AwaitingQuote":
        case "None":
            return this.getUserWithQuoteJobInStatus(queryMap);
        default :
            throw new Exception("Cannot find switch case for  " + approvalstatus);
        }
    }

    public UserJob getUserWithInitialFundingRequest(String profileName, String additionalResources) throws Exception {
        // Pull back
        // resourceId - contractor assigned to the job
        // JobReference - self explanatory
        // UserName - RFM who has to approve the funding request for the job reference above
        // siteid for the job
        String sql = "SELECT ra.resourceid ResourceId, j.JobReference, au.UserName, s.Id SiteId " +
                "FROM Job j " +
                "INNER JOIN ResourceAssignment ra ON j.id = ra.jobId " +
                "INNER JOIN FundingRequest f ON f.ResourceAssignmentId = ra.Id " +
                "INNER JOIN Resource ar ON f.InitialRequestApproverId = ar.Id " +
                "INNER JOIN ApplicationUser au ON au.ResourceId = ar.Id " +
                "INNER JOIN UserProfile up ON au.UserProfileId = up.Id " +
                "INNER JOIN Site s ON s.id = j.SiteId " +
                "INNER JOIN ResourceAssignment ra2 ON j.id = ra2.jobId " +
                "WHERE ra.ResourceAssignmentStatusId = 19 " +
                "AND f.StatusId = 1 " +
                "AND f.InitialFunding = 1 " +
                "AND j.JobStatusId IN (1, 2, 3) " +
                "AND " +
                "( " +
                "j.DeferralDate IS NULL " +
                "OR " +
                "(j.DeferralDate IS NOT NULL AND DATEDIFF(mi, j.DeferralDate, GETUTCDATE()) > 0) " +
                ") " +
                "AND up.Name = :profileName ";

        if (additionalResources.equalsIgnoreCase("with")) {
            sql = "WITH InitialFundingRequestsCTE AS ( " + sql + " GROUP BY ra.ResourceId, j.JobReference, au.UserName, s.Id HAVING COUNT(ra2.ResourceId)>1 ), "
                    + "ResourceAssignmentStatusCTE AS ( "
                    + "SELECT j.JobReference JobReference, ResourceId, r.Name ResourceName, ras.Name from Job j "
                    + "JOIN ResourceAssignment ra ON ra.JobId = j.Id "
                    + "JOIN Resource r ON ra.ResourceId = r.Id "
                    + "JOIN ResourceAssignmentStatus ras ON ra.ResourceAssignmentStatusId = ras.Id "
                    + "WHERE ra.ResourceAssignmentStatusId ! = 19 "
                    + "AND ras.IsTerminal = 0) "
                    + "SELECT TOP(1) NEWID() id, ifrCTE.* FROM InitialFundingRequestsCTE ifrCTE JOIN ResourceAssignmentStatusCTE rasCTE ON ifrCTE.JobReference = rasCTE.JobReference";
        } else {
            sql = " WITH InitialFundingRequestsCTE AS ( " + sql + " GROUP BY ra.ResourceId, j.JobReference, au.UserName, s.Id HAVING COUNT(ra2.ResourceId)=1 ) "
                    + " SELECT TOP(1) NEWID() id, ifrCTE.* FROM InitialFundingRequestsCTE ifrCTE ";
        }
        sql = sql +" ORDER BY NEWID() ";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getUserWithInitialFundingRequest : " +sql);
        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        try {
            query.setParameter("profileName", profileName);
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            return null;
        }
    }

    public UserJob getUserJob(Map<String, Object> queryMap) throws Exception {
        if  ( null != queryMap.get("jobType")) {
            if ("quote".equalsIgnoreCase(queryMap.get("jobType").toString())){
                return getQuoteUserJob(queryMap);
            }
        }

        String jobStatus = queryMap.get("jobStatus").toString();
        String profileName = queryMap.get("profileName").toString();
        String fundingRoute = null;
        if (null != queryMap.get("gasUsage") && null != queryMap.get("inPlant")) {
            return this.getUserWithJobSingleResourceWithGas(
                    profileName,
                    queryMap.get("resourceAssignmentStatus").toString(),
                    queryMap.get("jobStatus").toString(),
                    Boolean.valueOf(queryMap.get("useResourceTypeName").toString()),
                    queryMap.get("resourceTypeName").toString(),
                    Boolean.valueOf(queryMap.get("inPlant").toString()),
                    Boolean.valueOf(queryMap.get("asset").toString()));
        }

        if (null != queryMap.get("gasUsage") && null != queryMap.get("leakCheck") && null != queryMap.get("isRequiredToReturn")) {
            return this.getReturningUserWithJobSingleResourceWithGasRequired(
                    profileName,
                    queryMap.get("resourceAssignmentStatus").toString(),
                    queryMap.get("jobStatus").toString(),
                    Boolean.valueOf(queryMap.get("useResourceTypeName").toString()),
                    queryMap.get("resourceTypeName").toString(),
                    Boolean.valueOf(queryMap.get("gasUsage").toString()),
                    Boolean.valueOf(queryMap.get("leakCheck").toString()),
                    Boolean.valueOf(queryMap.get("isRequiredToReturn").toString()));
        }

        if (null != queryMap.get("gasUsage") && null != queryMap.get("leakCheck") && null == queryMap.get("isRequiredToReturn")) {
            return this.getReturningUserWithJobSingleResourceWithGas(
                    profileName,
                    queryMap.get("resourceAssignmentStatus").toString(),
                    queryMap.get("jobStatus").toString(),
                    Boolean.valueOf(queryMap.get("useResourceTypeName").toString()),
                    queryMap.get("resourceTypeName").toString(),
                    Boolean.valueOf(queryMap.get("gasUsage").toString()),
                    Boolean.valueOf(queryMap.get("leakCheck").toString()));
        }

        switch (jobStatus) {
        case "Allocated":
        case "Awaiting ETA":
        case "In Progress":
        case "Logged":
            // Return this for "In Progress" and "Allocated"
            String resourceAssignmentStatus = queryMap.get("resourceAssignmentStatus") == null ? null : queryMap.get("resourceAssignmentStatus").toString();
            if("SINGLE".equalsIgnoreCase(queryMap.get("resourceCount").toString())) {
                return this.getUserWithJobSingleResource(profileName, resourceAssignmentStatus, queryMap.get("jobStatus").toString(), Boolean.valueOf(queryMap.get("useResourceTypeName").toString()), queryMap.get("resourceTypeName").toString(), queryMap.get("transferJob").toString());
            } else {
                return this.getUserWithJobMultiResource(profileName, resourceAssignmentStatus, queryMap.get("jobStatus").toString());
            }

        case "Awaiting Acceptance" :
            if("reactive".equalsIgnoreCase(queryMap.get("jobType").toString())) {
                return this.getUserWithAwaitingJobsWithOneResource(profileName, queryMap.get("jobType").toString());
            } else {
                return this.getUserWithAwaitingAcceptanceJob(profileName);
            }

        case "AwaitingSeniorManagerApproval" :
        case "AwaitingBypassApproval" :
            return this.getUserWithFundingRequestJobInStatus(profileName, queryMap.get("jobStatus").toString());

        case "Awaiting Feedback Response":
            throw new Exception("Use alternative method!");

        case "With Feedback Response":
            throw new Exception("Use alternative method!");

        case "Capital Parts Awaiting Approval":
        case "Non Capital Parts Awaiting Approval":
        case "Parts Awaiting Approval":
            return this.getUserWithJobPartsAwaitingReview(profileName, queryMap.get("jobType").toString(), jobStatus);

        case "Logged / Awaiting Funding Authorisation":
        case "Logged / Allocated / Awaiting Funding Authorisation":
            return getUserWithInitialFundingRequest(profileName, queryMap.get("additionalResources").toString());

        case "Invoices Awaiting Approval":
            fundingRoute = queryMap.get("fundingRoute").toString(); // if null then we don't care about funding route
            return this.getUserWithInvoiceAwaitingApprovalJob(profileName, jobStatus, fundingRoute);

        case "Invoice Awaiting Final Approval":
        case "Non Supply Only Invoice Awaiting Final Approval":
        case "Supply Only Invoice Awaiting Final Approval":
            fundingRoute = queryMap.get("fundingRoute").toString(); // if null then we don't care about funding route
            return this.getUserWithInvoiceAwaitingFinalApprovalJob(profileName, jobStatus, fundingRoute);

        case "Open" :
            return this.getUserWithOpenJob(profileName);

        case "PPM Job with certificate":
            return  this.getUserWithPPMJob(profileName, true);
        case "PPM Job":
        case "PPM Job without certificate":
            return  this.getUserWithPPMJob(profileName, false);
        case "Orders Awaiting Invoice With Existing Invoice":
        case "Complete / Orders Awaiting Invoice With Existing Invoice without documents":
        case "Complete / Orders Awaiting Invoice With Existing Invoice":
        case "Complete / Orders Awaiting Invoice With No Invoice":
        case "Orders Awaiting Invoice With No Invoice and Company Assigned":
        case "Orders Awaiting Invoice With Template Uploaded and No Invoice":
            return this.getUserWithOrdersAwaitingInvoiceJob(profileName, jobStatus);

        case "Complete / PPM Orders Awaiting Invoice With Existing Invoice":
        case "Complete / PPM Orders Awaiting Invoice With Existing Invoice and Documents":
            return this.getUserWithPPMOrdersAwaitingInvoiceJob(profileName, jobStatus);

        case "In Progress / Parts Requested / Awaiting Invoice With No Invoice":
        case "In Progress / Parts Requested / Awaiting Invoice With Existing Invoice":
        case "Supplier Orders Awaiting Invoice With No Invoice and has a company assigned":
        case "Supplier Orders Awaiting Invoice With Existing Invoice and has a company assigned":
            return this.getSupplierUserWithOrdersAwaitingInvoiceJob(profileName,jobStatus);

        default :
            throw new Exception("Cannot find switch case for  getUserJob : Job Status " + jobStatus);
        }
    }

    public UserJob getUserWithAllocatedJobsToStartWork(String profileName) throws Exception {

        String sql = FileHelper.getResourceFile("sql/select/", "GET_USER_WITH_ALLOCATED_JOB_TO_START_WORK.sql");
        logger.debug("getUserWithAllocatedJobsToStartWork : " + sql.replace(":profileName", quote(profileName)));
        Query query =  entityManager.createNativeQuery(sql, UserJob.class);
        try {
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ResourceId, JobReference and UserName entity which has got supplier order awaiting invoice
     * @param jobReference
     * @return
     */
    public UserJob getSupplierWithNoInvoice(Integer jobReference) {
        String sql = "SELECT TOP(1) NEWID() Id, au.ResourceId ResourceId, enpr.fld_int_jobid JobReference, au.UserName UserName, j.SiteId"
                + " FROM %portaldb.tblPO po"
                + " INNER JOIN %iosdb.tblEpochNewPartsRequest enpr ON po.fld_int_HelpdeskFaultID = enpr.fld_int_jobid"
                + " INNER JOIN %iosdb.tblEpochNewPartsRequestLine enprl ON enpr.fld_int_id = enprl.fld_int_requestID"
                + " INNER JOIN %helpdeskdb.job j ON po.fld_int_HelpdeskFaultID = j.JobReference"
                + " INNER JOIN %portaldb.tblHelpDeskFault hdf ON po.fld_int_HelpdeskFaultID = hdf.fld_int_ID"
                + " INNER JOIN %portaldb.tblHelpDeskResource hdr ON po.fld_str_SupplierID = hdr.fld_str_SupplierID"
                + " INNER JOIN %helpdeskdb.Resource r ON hdr.fld_int_ID = r.EpochId"
                + " INNER JOIN %helpdeskdb.ApplicationUser au ON au.ResourceId = r.Id"
                + " WHERE po.fld_int_HelpdeskFaultID = :jobReference"
                + " AND au.UserProfileId = 129 " // 129 - Supply Only user"
                + " ORDER BY NEWID()";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        try {
            query.setParameter("jobReference", jobReference);
            logger.debug("getSupplierWithNoInvoice : " + sql);
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ResourceId, JobReference and UserName entity which has got invoices awaiting for approval
     * @param jobReference
     * @return
     */
    public UserJob getInvoiceApproverWithInvoicesAwaitingApprovalJobs(Integer jobReference) {
        String sql = "SELECT TOP(1) NEWID() AS Id," +
                " r.Id AS [ResourceId], " +
                " po.fld_int_HelpdeskFaultID AS [JobReference], " +
                " ms.SiteId AS [SiteId], " +
                " hdr.fld_str_LogOn AS [UserName] " +
                " FROM %helpdeskdb.InitialApproverRule ia " +
                " INNER JOIN %helpdeskdb.Budget b ON b.FundingRouteId = ia.FundingRouteId " +
                " INNER JOIN %helpdeskdb.ResourceProfile rp ON rp.id = ia.ResourceProfileId " +
                " INNER JOIN %helpdeskdb.uvw_ManagerSites ms ON ms.ResourceProfileName = rp.Name " +
                " INNER JOIN %helpdeskdb.ApplicationUser au ON au.Id = ms.ApplicationUserId " +
                " INNER JOIN %helpdeskdb.Resource r ON au.ResourceID = r.ID " +
                " INNER JOIN %portaldb.tblHelpDeskResource hdr ON hdr.fld_int_ID = r.EpochId " +
                " INNER JOIN %portaldb.uvw_HelpDeskSiteManagers sm ON sm.PortalSiteId = ms.SiteId  " +
                " INNER JOIN %portaldb.tblHelpDeskFault hdf ON hdf.fld_int_StoreID = ms.SiteId " +
                " INNER JOIN %helpdeskdb.Site s ON s.id = hdf.fld_int_StoreID " +
                " INNER JOIN %portaldb.tblPO po ON po.fld_int_HelpdeskFaultID = hdf.fld_int_ID " +
                " INNER JOIN %portaldb.tblcfSupplierInvoiceHeader sih ON sih.fld_int_PO_ID = po.fld_int_ID " +
                " WHERE po.fld_int_HelpdeskFaultID = :jobReference";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        Query query = entityManager.createNativeQuery(sql, UserJob.class);
        try {
            query.setParameter("jobReference", jobReference);
            logger.debug("getInvoiceApproverWithInvoicesAwaitingApprovalJobs : " + sql);
            UserJob userJob = (UserJob) query.getSingleResult();
            return userJob;
        } catch (Exception e) {
            return null;
        }
    }
}
