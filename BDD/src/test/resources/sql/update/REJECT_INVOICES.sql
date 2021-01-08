-- Reject all invoices at state 'x' except the latest 'y' for each supplier
UPDATE %portaldb.tblcfSupplierInvoiceHeader 
SET fld_bit_IsCancelled = 1
, fld_int_SupplierInvoiceStatusId = 6
, fld_int_Stage1RejectionReasonId = 35
, fld_str_Stage1RejectionNotes = 'Rejected by TestAutomation'
WHERE fld_str_InvoiceNumber IN (
    SELECT fld_str_InvoiceNumber FROM (
        SELECT ROW_NUMBER() OVER (PARTITION BY po.fld_str_SupplierID ORDER BY po.fld_dat_DateRaised DESC) AS RowNumber, sih.fld_str_InvoiceNumber, po.fld_str_SupplierID, po.fld_dat_DateRaised 
        FROM %portaldb.tblcfSupplierInvoiceHeader sih
        INNER JOIN %portaldb.tblPO po ON sih.fld_int_PO_ID = po.fld_int_ID
        INNER JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id
        WHERE sih.fld_bit_IsCancelled = 0
        AND sis.fld_str_Name = '%s'
    ) Groups
    WHERE RowNumber > %d
)