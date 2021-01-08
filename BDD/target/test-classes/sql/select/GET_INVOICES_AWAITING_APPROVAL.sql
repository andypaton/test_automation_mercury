WITH Budgets AS (
    SELECT Id, [Name], [AbbreviatedName]
    FROM %helpdeskdb.Budget
)
 SELECT
    Invoices.fld_int_Id AS [Id],
    LTRIM(Invoices.fld_str_InvoiceNumber) AS [InvNum],
    COALESCE(hdrSupp.fld_str_Name, '') + ' ' + COALESCE(hdrSupp.fld_str_SecondName, '') AS [Supplier],
    PO.fld_str_PONumber [OrderRef],
    PO.fld_int_HelpdeskFaultID AS [Job Ref],
    s.SiteCode AS [Site],
    Invoices.fld_dat_SubmissionDateUTC AS [DateSubmitted],
    Invoices.fld_dat_InvoiceDate AS [InvDate],
    Invoices.fld_cur_NetAmount AS [InvNet],
    Invoices.fld_cur_VatAmount AS [InvTax],
    ISNULL(CASE WHEN hdr.fld_str_name IS NOT NULL THEN hdr.fld_str_name ELSE hdr2.fld_str_name END, 'No Approver') AS Approver,
    ISNULL(CASE WHEN hdr.fld_str_name IS NOT NULL THEN hdr.fld_int_ID ELSE hdr2.fld_int_ID END, 0) AS EpochId
FROM
    %portaldb.tblcfSupplierInvoiceHeader AS Invoices
    INNER JOIN %portaldb.tblPO AS PO ON Invoices.fld_int_PO_ID = PO.fld_int_ID
    INNER JOIN %portaldb.tblHelpDeskResource AS hdrSupp ON PO.fld_str_SupplierID = hdrSupp.fld_str_SupplierID
    LEFT JOIN %portaldb.tblHelpdeskResource FirstApprover ON FirstApprover.fld_int_Id = Invoices.fld_int_Stage1Approver_HelpDeskResourceId
    LEFT JOIN %portaldb.tblHelpDeskResourceRelationship hdrr ON po.fld_int_RequesterID = hdrr.fld_int_ChildHelpDeskResourceID AND hdrr.fld_int_HelpDeskResourceRelationshipTypeID = 1
    LEFT JOIN %portaldb.tblHelpDeskResource hdr ON hdrr.fld_int_ParentHelpDeskResourceID = hdr.fld_int_ID AND hdr.fld_int_HelpdeskResourceSubTypeID = 133
    LEFT JOIN %portaldb.tblHelpDeskResource hdr2 ON Invoices.fld_int_ReassignedApproverId = hdr2.fld_int_ID
    LEFT JOIN Budgets ON Invoices.fld_int_BudgetId = Budgets.Id
    LEFT JOIN %portaldb.tblHelpDeskFault hdf ON PO.fld_int_HelpdeskFaultID = hdf.fld_int_ID
    INNER JOIN %helpdeskdb.Site s ON hdf.fld_int_StoreID = s.Id
WHERE
    Invoices.fld_int_SupplierInvoiceStatusId = 3
    AND PO.fld_int_HelpdeskFaultID IS NOT NULL
    ORDER BY NEWID()