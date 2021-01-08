-- To get all the Credit notes
SELECT fld_str_CreditNoteNumber SupplierRef, scnt.fld_str_Description Type, sih.fld_str_PO_PONumber OrderRef, 
    po.[Supplier Name] Supplier,  FORMAT(DATEADD(hh, 1, fld_dat_CreditNoteDate), 'dd/MM/yyyy') Date, 
    scnh.fld_cur_NetAmount Net, scnh.fld_cur_TaxAmount VAT, scnh.fld_cur_NetAmount + scnh.fld_cur_TaxAmount Gross, scns.fld_str_DisplayName Action 
    FROM %portaldb.tblcfSupplierCreditNoteHeader scnh 
    JOIN %portaldb.tblcfSupplierInvoiceHeader sih ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id 
    JOIN %portaldb.vw_purchase_order po ON po.[PO No] = sih.fld_str_PO_PONumber 
    JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id 
    LEFT JOIN %portaldb.tblcfSupplierCreditNoteType scnt ON scnh.fld_int_SupplierCreditNoteTypeId = scnt.fld_int_Id 
    WHERE scns.fld_str_DisplayName = 'Awaiting Approval' AND scnh.fld_int_SupplierInvoiceHeaderId IS NOT NULL         

UNION 
                  
SELECT fld_str_CreditNoteNumber SupplierRef, scnt.fld_str_Description Type, 'N/A' AS OrderRef, 
    hdr.fld_str_Name Supplier, FORMAT(DATEADD(hh, 1, fld_dat_CreditNoteDate), 'dd/MM/yyyy') Date, 
    scnh.fld_cur_NetAmount Net, scnh.fld_cur_TaxAmount VAT, scnh.fld_cur_NetAmount + scnh.fld_cur_TaxAmount Gross, scns.fld_str_DisplayName
	FROM %portaldb.tblcfSupplierCreditNoteHeader scnh 
    JOIN %portaldb.tblHelpDeskResource hdr ON scnh.fld_str_SupplierId = hdr.fld_str_SupplierID 
    LEFT JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id 
    LEFT JOIN %portaldb.tblcfSupplierCreditNoteType scnt ON scnh.fld_int_SupplierCreditNoteTypeId = scnt.fld_int_Id 
    LEFT JOIN %portaldb.tblcfSupplierInvoiceHeader sih ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id 
    WHERE scns.fld_str_DisplayName = 'Awaiting Approval' AND scnh.fld_int_SupplierInvoiceHeaderId IS NULL                 

UNION 

-- To get all the invoices with approver and in status Awaiting AP Review

SELECT fld_str_InvoiceNumber SupplierRef, 'Invoice' AS Type , scnh.fld_str_PO_PONumber OrderRef, 
    po.[Supplier Name] Supplier,  FORMAT(DATEADD(hh, 1, fld_dat_InvoiceDate), 'dd/MM/yyyy') Date, 
    scnh.fld_cur_NetAmount Net, scnh.fld_cur_VatAmount VAT, scnh.fld_cur_GrossAmount Gross, 'Values Changed' AS Action 
    FROM %portaldb.tblcfSupplierInvoiceHeader scnh 
    JOIN %portaldb.vw_purchase_order po ON po.[PO No] = scnh.fld_str_PO_PONumber 
    JOIN %portaldb.tblcfSupplierInvoiceStatus scns ON scnh.fld_int_SupplierInvoiceStatusId = scns.fld_int_Id 
    WHERE scns.fld_str_DisplayName = 'Awaiting AP Review'  

UNION

-- To get all the invoices with no approver

SELECT
    Invoices.fld_str_InvoiceNumber AS SupplierRef,
	'Invoice' AS Type , Invoices.fld_str_PO_PONumber OrderRef,
	porder.[Supplier Name] Supplier,
	FORMAT(DATEADD(hh, 1, Invoices.fld_dat_InvoiceDate), 'dd/MM/yyyy') Date,
    Invoices.fld_cur_NetAmount Net, Invoices.fld_cur_VatAmount VAT, Invoices.fld_cur_GrossAmount Gross,
    ISNULL(hdr.fld_str_name, 'No Approver') AS Action
    FROM %portaldb.tblcfSupplierInvoiceHeader AS Invoices
    INNER JOIN %portaldb.vw_purchase_order porder ON porder.[PO No] = Invoices.fld_str_PO_PONumber
    INNER JOIN %portaldb.tblPO AS PO ON Invoices.fld_int_PO_ID = PO.fld_int_ID
    INNER JOIN %portaldb.tblHelpDeskResource AS hdrSupp ON PO.fld_str_SupplierID = hdrSupp.fld_str_SupplierID
    INNER JOIN %portaldb.tblHelpdeskFault AS Fault ON PO.fld_int_HelpdeskFaultID = Fault.fld_int_ID
    INNER JOIN %portaldb.tblStore AS Store ON Fault.fld_int_StoreID = Store.fld_int_ID
    INNER JOIN %portaldb.tblStoreCluster SC ON store.fld_int_StoreClusterID = SC.fld_int_ID
    LEFT OUTER JOIN %portaldb.tblHelpdeskResource FirstApprover ON FirstApprover.fld_int_Id = Invoices.fld_int_Stage1Approver_HelpDeskResourceId
    LEFT OUTER JOIN %portaldb.tblHelpdeskResource hdr ON hdr.fld_int_ID = SC.fld_int_AreaManagerID
    LEFT OUTER JOIN %portaldb.tblHelpDeskResource hdr2 ON Invoices.fld_int_ReassignedApproverId = hdr2.fld_int_ID  
    WHERE
	Invoices.fld_int_SupplierInvoiceStatusId = 3
	AND hdr.fld_str_Name IS NULL
	ORDER By SupplierRef




