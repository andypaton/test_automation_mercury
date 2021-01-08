SELECT TOP (1)
    Invoices.fld_int_Id AS [Id],
    LTRIM(Invoices.fld_str_InvoiceNumber) AS [InvNum],
    COALESCE(hdrSupp.fld_str_Name, '') + ' ' + COALESCE(hdrSupp.fld_str_SecondName, '') AS [Supplier],
    PO.fld_str_PONumber [OrderRef],
    s.Name AS [Site],
    FORMAT(DATEADD(hh, 1, Invoices.fld_dat_SubmissionDateUTC), :date, :locale) AS [DateSubmitted],
    FORMAT(DATEADD(hh, 1, Invoices.fld_dat_InvoiceDate), :date, :locale) AS [InvDate],
    Invoices.fld_cur_NetAmount AS [InvNet],
    Invoices.fld_cur_VatAmount AS [InvTax],
    ISNULL(CASE WHEN hdr.fld_str_name IS NOT NULL THEN hdr.fld_str_name ELSE hdr2.fld_str_name END, 'No Approver') AS Approver
FROM
    %portaldb.tblcfSupplierInvoiceHeader AS Invoices
    INNER JOIN %portaldb.tblPO AS PO ON Invoices.fld_int_PO_ID = PO.fld_int_ID
	INNER JOIN %helpdeskdb.Job AS j ON j.jobReference = PO.fld_int_HelpdeskFaultId
	INNER JOIN %helpdeskdb.Site AS s ON s.Id = j.SiteId
    INNER JOIN %portaldb.tblHelpDeskResource AS hdrSupp ON PO.fld_str_SupplierID = hdrSupp.fld_str_SupplierID
    LEFT OUTER JOIN %portaldb.tblHelpdeskResource FirstApprover ON FirstApprover.fld_int_Id = Invoices.fld_int_Stage1Approver_HelpDeskResourceId
    LEFT OUTER JOIN %portaldb.tblHelpDeskResourceRelationship hdrr ON po.fld_int_RequesterID = hdrr.fld_int_ChildHelpDeskResourceID AND hdrr.fld_int_HelpDeskResourceRelationshipTypeID = 1
    LEFT OUTER JOIN %portaldb.tblHelpDeskResource hdr ON hdrr.fld_int_ParentHelpDeskResourceID = hdr.fld_int_ID AND hdr.fld_int_HelpdeskResourceSubTypeID = 133
    LEFT OUTER JOIN %portaldb.tblHelpDeskResource hdr2 ON Invoices.fld_int_ReassignedApproverId = hdr2.fld_int_ID
    LEFT OUTER JOIN %helpdeskdb.Budget Budgets ON Invoices.fld_int_BudgetId = Budgets.Id
WHERE
    Invoices.fld_int_SupplierInvoiceStatusId = 3  -- In Awaiting Approval status
	AND PO.fld_int_helpdeskFaultId != 0
	AND PO.fld_str_PONumber NOT IN (SELECT fld_str_PO_PONumber FROM %portaldb.tblcfSupplierInvoiceHeader 
	GROUP BY fld_str_PO_PONumber
	HAVING COUNT(fld_str_PO_PONumber) > 1 )
ORDER BY NEWID()



