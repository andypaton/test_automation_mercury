WITH INVOICEDATA AS (

-- To get all the Credit notes
SELECT fld_str_CreditNoteNumber SupplierRef, scnt.fld_str_Description Type, sih.fld_str_PO_PONumber OrderRef, 
    scodes.Name Supplier, po.[Supplier Name] SupplierName,  FORMAT(DATEADD(hh, 1, fld_dat_CreditNoteDate), :date) Date, 
    scnh.fld_cur_NetAmount Net, scnh.fld_cur_TaxAmount VAT, scnh.fld_cur_NetAmount + scnh.fld_cur_TaxAmount Gross, scns.fld_str_DisplayName Action 
    FROM %portaldb.tblcfSupplierCreditNoteHeader scnh 
    JOIN %portaldb.tblcfSupplierInvoiceHeader sih ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id 
    JOIN %portaldb.vw_purchase_order po ON po.[PO No] = sih.fld_str_PO_PONumber
    JOIN %helpdeskdb.uvw_SupplierTCodes scodes ON scodes.Code = po.[Supplier Code] 
    JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id 
    LEFT JOIN %portaldb.tblcfSupplierCreditNoteType scnt ON scnh.fld_int_SupplierCreditNoteTypeId = scnt.fld_int_Id 
    WHERE scns.fld_str_DisplayName = 'Awaiting Approval' AND scnh.fld_int_SupplierInvoiceHeaderId IS NOT NULL         

UNION 
                  
SELECT fld_str_CreditNoteNumber SupplierRef, scnt.fld_str_Description Type, 'N/A' AS OrderRef, 
    scodes.Name Supplier, po.[Supplier Name] SupplierName, FORMAT(DATEADD(hh, 1, fld_dat_CreditNoteDate), :date) Date, 
    scnh.fld_cur_NetAmount Net, scnh.fld_cur_TaxAmount VAT, scnh.fld_cur_NetAmount + scnh.fld_cur_TaxAmount Gross, scns.fld_str_DisplayName 
    FROM %portaldb.tblcfSupplierCreditNoteHeader scnh 
    JOIN %portaldb.tblHelpDeskResource hdr ON scnh.fld_str_SupplierId = hdr.fld_str_SupplierID 
    JOIN %helpdeskdb.uvw_SupplierTCodes scodes ON scodes.Code = hdr.fld_str_LogOn
    LEFT JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id 
    LEFT JOIN %portaldb.tblcfSupplierCreditNoteType scnt ON scnh.fld_int_SupplierCreditNoteTypeId = scnt.fld_int_Id 
    LEFT JOIN %portaldb.tblcfSupplierInvoiceHeader sih ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id 
    LEFT JOIN %portaldb.vw_purchase_order po ON po.[PO No] = sih.fld_str_PO_PONumber 
    WHERE scns.fld_str_DisplayName = 'Awaiting Approval' AND scnh.fld_int_SupplierInvoiceHeaderId IS NULL                 

UNION 

-- To get all the invoices with approver and in status Awaiting AP Review

SELECT fld_str_InvoiceNumber SupplierRef, 'Invoice' AS Type , sih.fld_str_PO_PONumber OrderRef, 
    scodes.Name Supplier, po.[Supplier Name] SupplierName, FORMAT(DATEADD(hh, 1, fld_dat_InvoiceDate), :date) Date, 
    sih.fld_cur_NetAmount Net, sih.fld_cur_VatAmount VAT, sih.fld_cur_GrossAmount Gross, 'Values Changed' AS Action 
    FROM %portaldb.tblcfSupplierInvoiceHeader sih 
    JOIN %portaldb.vw_purchase_order po ON po.[PO No] = sih.fld_str_PO_PONumber 
    JOIN %helpdeskdb.uvw_SupplierTCodes scodes ON scodes.Code = po.[Supplier Code]
    JOIN %portaldb.tblcfSupplierInvoiceStatus scns ON sih.fld_int_SupplierInvoiceStatusId = scns.fld_int_Id 
    WHERE scns.fld_str_DisplayName = 'Awaiting AP Review' ) 
    
    SELECT TOP (1) SupplierRef, Type, OrderRef, Supplier, SupplierName, Date, Net, VAT, Gross, Action 
    FROM INVOICEDATA 
    WHERE Supplier IS NOT NULL 
    AND OrderRef IS NOT NULL 
    AND OrderRef != 'N/A' 
    AND OrderRef NOT IN (SELECT OrderRef FROM INVOICEDATA GROUP BY OrderRef HAVING COUNT(OrderRef) > 1)
    ORDER BY NEWID()