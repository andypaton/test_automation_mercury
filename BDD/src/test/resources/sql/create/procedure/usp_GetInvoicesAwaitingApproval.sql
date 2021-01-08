-- =============================================
-- Description:   Returns the invoices awaiting approval.
-- Uses union to retreive Vendor (approved by RFM) or Supplier (approved by Department Head)
-- Returns all if no user passed in
-- =============================================
CREATE PROCEDURE %testdb.usp_GetInvoicesAwaitingApproval
                 @AwaitingApprovalStatusId INT,
                 @dateFormat VARCHAR(50),
                 @locale VARCHAR(50),
                 @HelpdeskResourceId int = 0
AS
BEGIN
                 
SET NOCOUNT ON;
DECLARE @AreaManagerResourceType INT
SELECT @AreaManagerResourceType = fld_int_ID
FROM %portaldb.tblHelpdeskResourceType
WHERE UPPER(fld_str_Name) = 'AREA MANAGER'  ;              
                                
-- Invoices awaiting approval for parts request or reactive job related orders where approver is linked via store linkage from helpdeskfault
WITH Budgets AS (
    SELECT Id, [Name], [AbbreviatedName]
    FROM %helpdeskdb.Budget
)
SELECT
    Invoices.fld_int_Id AS [Id],
    LTRIM(Invoices.fld_str_InvoiceNumber) AS [InvNum],
    COALESCE(hdrSupp.fld_str_Name, '') + ' ' + COALESCE(hdrSupp.fld_str_SecondName, '') AS [Supplier],
    PO.fld_str_PONumber AS [OrderRef],
    Store.fld_str_Name AS [Site],
    FORMAT(DATEADD(hh, 1, Invoices.fld_dat_SubmissionDateUTC), @dateFormat, @locale) AS [DateSubmitted],
    FORMAT(DATEADD(hh, 1, Invoices.fld_dat_InvoiceDate), @dateFormat, @locale) AS [InvDate],                                                              
    Invoices.fld_cur_NetAmount AS [InvNet],
    Invoices.fld_cur_VatAmount AS [InvTax],
    ISNULL(hdr.fld_str_name, 'No Approver') AS Approver
FROM %portaldb.tblcfSupplierInvoiceHeader AS Invoices
    INNER JOIN %portaldb.tblPO AS PO ON Invoices.fld_int_PO_ID = PO.fld_int_ID
    INNER JOIN %portaldb.tblHelpDeskResource AS hdrSupp ON PO.fld_str_SupplierID = hdrSupp.fld_str_SupplierID
    INNER JOIN %portaldb.tblHelpdeskFault AS Fault ON PO.fld_int_HelpdeskFaultID = Fault.fld_int_ID
    INNER JOIN %portaldb.tblStore AS Store ON Fault.fld_int_StoreID = Store.fld_int_ID
    INNER JOIN %portaldb.tblStoreCluster SC ON store.fld_int_StoreClusterID = SC.fld_int_ID
    LEFT OUTER JOIN %portaldb.tblHelpdeskResource FirstApprover ON FirstApprover.fld_int_Id = Invoices.fld_int_Stage1Approver_HelpDeskResourceId
    LEFT OUTER JOIN %portaldb.tblHelpdeskResource hdr ON hdr.fld_int_ID = SC.fld_int_AreaManagerID
    LEFT OUTER JOIN %portaldb.tblHelpDeskResource hdr2 ON Invoices.fld_int_ReassignedApproverId = hdr2.fld_int_ID
    LEFT OUTER JOIN  Budgets ON Invoices.fld_int_BudgetId = Budgets.Id
WHERE
    Invoices.fld_int_SupplierInvoiceStatusId = @AwaitingApprovalStatusId
    AND
    ((Invoices.fld_int_ReassignedApproverId IN (SELECT RfmId FROM [%portaldb].[fn_AllCurrentlyCoveredResourceIdsForRFM](@HelpdeskResourceId)))
     OR
     (SC.fld_int_AreaManagerID IN (SELECT RfmId FROM [%portaldb].[fn_AllCurrentlyCoveredResourceIdsForRFM](@HelpdeskResourceId)))                                              
     OR
     (-- Accounts Payable see all invoices awaiting approval, so in this instance the helpdeskresourceid is not passed in and defaults to 0
     @HelpdeskResourceId = 0
     ))
    AND
    hdr.fld_int_HelpDeskResourceTypeID = @AreaManagerResourceType
    AND
    (PO.fld_int_POTypeID = 5 OR PO.fld_int_POTypeID = 17) -- Only Parts Request or Reactive Job Related Orders
UNION
-- Invoices awaiting approval for general orders where approver is linked via department head
SELECT
    Invoices.fld_int_Id AS [Id],
    LTRIM(Invoices.fld_str_InvoiceNumber) AS [InvNum],
    COALESCE(hdrSupp.fld_str_Name, '') + ' ' + COALESCE(hdrSupp.fld_str_SecondName, '') AS [Supplier],
    PO.fld_str_PONumber [OrderRef],
    '' AS [Site],
    FORMAT(DATEADD(hh, 1, Invoices.fld_dat_SubmissionDateUTC), @dateFormat, @locale) AS [DateSubmitted],
    FORMAT(DATEADD(hh, 1, Invoices.fld_dat_InvoiceDate), @dateFormat, @locale) AS [InvDate],
    Invoices.fld_cur_NetAmount AS [InvNet],
    Invoices.fld_cur_VatAmount AS [InvTax],
    ISNULL(CASE WHEN hdr.fld_str_name IS NOT NULL THEN hdr.fld_str_name ELSE hdr2.fld_str_name END, 'No Approver') AS Approver
FROM
    %portaldb.tblcfSupplierInvoiceHeader AS Invoices
    INNER JOIN %portaldb.tblPO AS PO ON Invoices.fld_int_PO_ID = PO.fld_int_ID
    INNER JOIN %portaldb.tblHelpDeskResource AS hdrSupp ON PO.fld_str_SupplierID = hdrSupp.fld_str_SupplierID
    LEFT OUTER JOIN %portaldb.tblHelpdeskResource FirstApprover ON FirstApprover.fld_int_Id = Invoices.fld_int_Stage1Approver_HelpDeskResourceId
    LEFT OUTER JOIN %portaldb.tblHelpDeskResourceRelationship hdrr ON po.fld_int_RequesterID = hdrr.fld_int_ChildHelpDeskResourceID AND hdrr.fld_int_HelpDeskResourceRelationshipTypeID = 1
    LEFT OUTER JOIN %portaldb.tblHelpDeskResource hdr ON hdrr.fld_int_ParentHelpDeskResourceID = hdr.fld_int_ID AND hdr.fld_int_HelpdeskResourceSubTypeID = 133
    LEFT OUTER JOIN %portaldb.tblHelpDeskResource hdr2 ON Invoices.fld_int_ReassignedApproverId = hdr2.fld_int_ID
    LEFT OUTER JOIN  Budgets ON Invoices.fld_int_BudgetId = Budgets.Id
WHERE
    Invoices.fld_int_SupplierInvoiceStatusId = @AwaitingApprovalStatusId
    AND NULLIF(PO.fld_int_HelpdeskFaultID, 0) IS NULL
    AND ((Invoices.fld_int_ReassignedApproverId IN (SELECT RfmId FROM [%portaldb].[fn_AllCurrentlyCoveredResourceIdsForRFM](@HelpdeskResourceId)))
     OR
     (hdr.fld_int_ID IN (SELECT RfmId FROM [%portaldb].[fn_AllCurrentlyCoveredResourceIdsForRFM](@HelpdeskResourceId))) 
     OR 
     (-- Accounts Payable see all invoices awaiting approval, so in this instance the helpdeskresourceid is not passed in and defaults to 0
     @HelpdeskResourceId = 0
     ))
    AND (PO.fld_int_POTypeID = 28) -- Only General Orders
UNION
-- Invoices awaiting approval for PPM related job and part orders where approver is linked via store linkage from schedule line
SELECT
    Invoices.fld_int_Id AS [Id],
    LTRIM(Invoices.fld_str_InvoiceNumber) AS [InvNum],
    COALESCE(hdrSupp.fld_str_Name, '') + ' ' + COALESCE(hdrSupp.fld_str_SecondName, '') AS [Supplier],
    PO.fld_str_PONumber [OrderRef],
    Store.fld_str_Name AS [Site],
    FORMAT(DATEADD(hh, 1, Invoices.fld_dat_SubmissionDateUTC), @dateFormat, @locale) AS [DateSubmitted],
    FORMAT(DATEADD(hh, 1, Invoices.fld_dat_InvoiceDate), @dateFormat, @locale) AS [InvDate],
    Invoices.fld_cur_NetAmount AS [InvNet],
    Invoices.fld_cur_VatAmount AS [InvTax],
    ISNULL(hdr.fld_str_name, 'No Approver') AS Approver
FROM
    %portaldb.tblcfSupplierInvoiceHeader AS Invoices
    INNER JOIN %portaldb.tblPO AS PO ON Invoices.fld_int_PO_ID = PO.fld_int_ID
    INNER JOIN %portaldb.tblHelpDeskResource AS hdrSupp ON PO.fld_str_SupplierID = hdrSupp.fld_str_SupplierID
    INNER JOIN %portaldb.tblProjectHeader PH ON PO.fld_int_ProjectHeaderID = PH.fld_int_id
    INNER JOIN %portaldb.tblPPMScheduleLine AS PSL ON PH.fld_int_PPMScheduleRef = PSL.fld_int_ID
    INNER JOIN %portaldb.tblPPMType AS PT ON PT.fld_int_ID = PSL.fld_int_PPMTypeID
    INNER JOIN %portaldb.tblAssetMainType AMT ON PT.fld_int_MainTypeID = AMT.fld_int_ID
    INNER JOIN %portaldb.tblStore AS Store ON PSL.fld_int_StoreID = Store.fld_int_ID
    INNER JOIN %portaldb.tblStoreCluster SC ON store.fld_int_StoreClusterID = SC.fld_int_ID
    LEFT OUTER JOIN %portaldb.tblChildProject CP ON CP.fld_int_ProjectHeaderId = PH.fld_int_Id                                           
    LEFT OUTER JOIN %portaldb.tblAsset A ON CP.fld_int_AssetID = A.fld_int_ID
    LEFT JOIN %portaldb.tblAssetLocation AL ON A.fld_int_AssetLocationID = AL.fld_int_ID
    LEFT OUTER JOIN %portaldb.tblHelpdeskResource FirstApprover ON FirstApprover.fld_int_Id = Invoices.fld_int_Stage1Approver_HelpDeskResourceId
    LEFT OUTER JOIN %portaldb.tblHelpdeskResource hdr ON hdr.fld_int_ID = SC.fld_int_AreaManagerID
    LEFT OUTER JOIN %portaldb.tblHelpDeskResource hdr2 ON Invoices.fld_int_ReassignedApproverId = hdr2.fld_int_ID
    LEFT OUTER JOIN Budgets ON Invoices.fld_int_BudgetId = Budgets.Id
WHERE 
Invoices.fld_int_SupplierInvoiceStatusId = @AwaitingApprovalStatusId
AND
    ((Invoices.fld_int_ReassignedApproverId IN (SELECT RfmId FROM [%portaldb].[fn_AllCurrentlyCoveredResourceIdsForRFM](@HelpdeskResourceId)))
    OR
    (SC.fld_int_AreaManagerID IN (SELECT RfmId FROM [%portaldb].[fn_AllCurrentlyCoveredResourceIdsForRFM](@HelpdeskResourceId))) 
    OR
    (-- Accounts Payable see all invoices awaiting approval, so in this instance the helpdeskresourceid is not passed in and defaults to 0
    @HelpdeskResourceId = 0 
    ))
    AND
    hdr.fld_int_HelpDeskResourceTypeID = @AreaManagerResourceType
    AND
    (PO.fld_int_POTypeID = 43 OR PO.fld_int_POTypeID = 37) -- Only PPM Job related type 43 and PPM Part order type 37
ORDER BY InvNum

END
GO


