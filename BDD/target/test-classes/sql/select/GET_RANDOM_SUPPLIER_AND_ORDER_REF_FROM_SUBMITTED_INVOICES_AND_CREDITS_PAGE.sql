
WITH Invoices AS (
  SELECT DISTINCT sih.fld_int_Id Id,
  'Invoice' AS Type,
  po.[Supplier Name] Supplier,
  LTRIM(sih.fld_str_InvoiceNumber) RefNum,
  sih.fld_str_PO_PONumber OrderRef,
  CAST(hdf.fld_int_ID AS varchar) JobRef,
  IIF(s.fld_bit_VIP = 1 , concat(s.fld_str_Name, ' (VIP) '), s.fld_str_Name) AS Site,
  sih.fld_dat_InvoiceDate Date,
  sih.fld_cur_NetAmount Net,
  sih.fld_cur_VatAmount Tax,
  (sih.fld_cur_NetAmount + sih.fld_cur_VatAmount) AS Gross,
  sis.fld_str_DisplayName Status, 
  'No' AS HasCredit, sis.fld_str_DisplayName , rp.Name
FROM %portaldb.tblcfSupplierInvoiceHeader sih
JOIN %portaldb.vw_purchase_order po ON po.[PO No] = sih.fld_str_PO_PONumber
JOIN %portaldb.tblHelpdeskFault hdf ON po.FaultID = hdf.fld_int_ID
JOIN %portaldb.tblStore s ON hdf.fld_int_StoreID = s.fld_int_ID
JOIN %portaldb.tblStoreCluster sc ON s.fld_int_StoreClusterID = sc.fld_int_ID
JOIN %portaldb.tblHelpDeskResource hdr ON sc.fld_int_AreaManagerID = hdr.fld_int_ID
JOIN %helpdeskdb.Resource r ON r.EpochId = hdr.fld_int_ID
JOIN %helpdeskdb.ResourceProfile rp ON rp.Id = r.ResourceProfileId
LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteHeader scnh ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id  
WHERE scnh.fld_int_SupplierInvoiceHeaderId IS NULL
AND sis.fld_str_DisplayName NOT IN ('New', 'Submitted with Errors')
AND po.POtypeID IN (5, 17)
AND sih.fld_dat_InvoiceDate BETWEEN DATEADD(DAY, -31, GETUTCDATE()) AND GETUTCDATE()),

CreditNotes AS (SELECT sih.fld_int_Id Id,
  REPLACE('Credit', 'Against Invoice', scnt.fld_str_Description) Type,
  po.[Supplier Name] Supplier,
  LTRIM(scnh.fld_str_CreditNoteNumber) RefNum,
  sih.fld_str_PO_PONumber OrderRef,
  'N/A' AS JobRef,
  'N/A' AS Site,
  scnh.fld_dat_CreditNoteDate Date,
  scnh.fld_cur_NetAmount Net,
  scnh.fld_cur_TaxAmount Tax,
  (scnh.fld_cur_NetAmount + scnh.fld_cur_TaxAmount) AS Gross,
  scns.fld_str_DisplayName Status,
  'N/A' AS HasCredit
FROM %portaldb.tblcfSupplierCreditNoteHeader scnh
LEFT JOIN %portaldb.tblcfSupplierInvoiceHeader sih ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id
LEFT JOIN %portaldb.vw_purchase_order po ON po.[PO No] = sih.fld_str_PO_PONumber
LEFT JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scns.fld_int_Id = scnh.fld_int_SupplierCreditNoteStatusId
LEFT JOIN %portaldb.tblcfSupplierCreditNoteType scnt ON scnh.fld_int_SupplierCreditNoteTypeId = scnt.fld_int_Id
WHERE scnh.fld_int_SupplierInvoiceHeaderId IS NOT NULL
AND scns.fld_str_DisplayName NOT IN ('New', 'Submitted With Errors')
AND sih.fld_dat_InvoiceDate BETWEEN DATEADD(DAY, -31, GETUTCDATE()) AND GETUTCDATE()),

UNION_CTE as (
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from Invoices
UNION
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from CreditNotes )

SELECT  TOP (1) Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
FROM UNION_CTE
WHERE OrderRef NOT IN (SELECT OrderRef FROM UNION_CTE GROUP BY OrderRef HAVING COUNT(*) > 1)
ORDER BY NEWID()