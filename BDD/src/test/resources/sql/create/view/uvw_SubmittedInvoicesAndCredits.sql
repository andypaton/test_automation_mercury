USE %testdb;
GO
SET ANSI_NULLS ON 
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE VIEW %testschema.uvw_SubmittedInvoicesAndCredits
AS
WITH SubmittedWithErrors_1_CTE AS (SELECT sih.fld_int_Id Id,
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
  'No' AS HasCredit
FROM %portaldb.tblcfSupplierInvoiceHeader sih
JOIN %portaldb.vw_purchase_order po ON po.[PO No] = sih.fld_str_PO_PONumber
JOIN %portaldb.tblHelpdeskFault hdf ON po.FaultID = hdf.fld_int_ID
JOIN %portaldb.tblStore s ON hdf.fld_int_StoreID = s.fld_int_ID
JOIN %portaldb.tblStoreCluster sc ON s.fld_int_StoreClusterID = sc.fld_int_ID
JOIN %portaldb.tblHelpDeskResource hdr ON sc.fld_int_AreaManagerID = hdr.fld_int_ID
LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteHeader scnh ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id  
WHERE scnh.fld_int_SupplierInvoiceHeaderId IS NULL
AND sis.fld_str_DisplayName NOT IN ('New', 'Submitted With Errors')
AND po.POtypeID IN (5, 17)),
 
SubmittedWtihErrors_2_CTE AS (SELECT sih.fld_int_Id Id,
  'Invoice' AS Type,
  po.[Supplier Name] Supplier,
  LTRIM(sih.fld_str_InvoiceNumber) RefNum,
  sih.fld_str_PO_PONumber OrderRef,
  CAST(po.FaultID AS varchar) JobRef,
  IIF(s.fld_bit_VIP = 1 , concat(s.fld_str_Name, ' (VIP) '), s.fld_str_Name) AS Site,
  sih.fld_dat_InvoiceDate Date,
  sih.fld_cur_NetAmount Net,
  sih.fld_cur_VatAmount Tax,
  (sih.fld_cur_NetAmount + sih.fld_cur_VatAmount) AS Gross,
  sis.fld_str_DisplayName Status,
  'No' AS HasCredit
FROM %portaldb.tblcfSupplierInvoiceHeader sih
JOIN %portaldb.vw_purchase_order po ON po.[PO No] = sih.fld_str_PO_PONumber
JOIN %portaldb.tblHelpdeskFault hdf ON po.FaultID = hdf.fld_int_ID
JOIN %portaldb.tblStore s ON hdf.fld_int_StoreID = s.fld_int_ID
JOIN %portaldb.tblStoreCluster sc ON s.fld_int_StoreClusterID = sc.fld_int_ID
JOIN %portaldb.tblHelpDeskResource hdr ON sc.fld_int_AreaManagerID = hdr.fld_int_ID
LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteHeader scnh ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id  
WHERE scnh.fld_int_SupplierInvoiceHeaderId IS NOT NULL
AND sis.fld_str_DisplayName NOT IN ('New', 'Submitted With Errors')
AND scns.fld_str_DisplayName IN ('New', 'Submitted With Errors')
AND po.POtypeID IN (5, 17)),

SubmittedWtihErrors_3_CTE AS (SELECT sih.fld_int_Id Id,
  'Invoice' AS Type,
  po.[Supplier Name] Supplier,
  LTRIM(sih.fld_str_InvoiceNumber) RefNum,
  sih.fld_str_PO_PONumber OrderRef,
  (CASE WHEN CAST(po.FaultID AS varchar) = 0 THEN 'N/A' END) AS JobRef,
  ISNULL(StoreName, 'N/A') Site,
  sih.fld_dat_InvoiceDate Date,
  sih.fld_cur_NetAmount Net,
  sih.fld_cur_VatAmount Tax,
  (sih.fld_cur_NetAmount + sih.fld_cur_VatAmount) AS Gross,
  sis.fld_str_DisplayName Status,
  'No' AS HasCredit
FROM %portaldb.tblcfSupplierInvoiceHeader sih
LEFT JOIN %portaldb.vw_purchase_order po ON sih.fld_str_PO_PONumber = po.[PO No]
LEFT JOIN %portaldb.uvw_OpenPurchaseOrders opo ON opo.PONumber = sih.fld_str_PO_PONumber
LEFT JOIN %portaldb.tblHelpdeskResource hdr ON sih.fld_int_Stage1Approver_HelpDeskResourceId = hdr.fld_int_ID
LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteHeader scnh ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id
WHERE sis.fld_str_DisplayName NOT IN ('New', 'Submitted With Errors')
AND scnh.fld_int_SupplierInvoiceHeaderId IS NULL
AND po.POtypeID = 28),

SubmittedWtihErrors_4_CTE AS (SELECT sih.fld_int_Id Id,
  'Invoice' AS Type,
  po.[Supplier Name] Supplier,
  LTRIM(sih.fld_str_InvoiceNumber) RefNum,
  sih.fld_str_PO_PONumber OrderRef,
  CAST(po.FaultID AS varchar) JobRef,
  ISNULL(StoreName, 'N/A') Site,
  sih.fld_dat_InvoiceDate Date,
  sih.fld_cur_NetAmount Net,
  sih.fld_cur_VatAmount Tax,
  (sih.fld_cur_NetAmount + sih.fld_cur_VatAmount) AS Gross,
  sis.fld_str_DisplayName Status,
  'No' AS HasCredit
FROM %portaldb.tblcfSupplierInvoiceHeader sih
LEFT JOIN %portaldb.vw_purchase_order po ON sih.fld_str_PO_PONumber = po.[PO No]
LEFT JOIN %portaldb.uvw_OpenPurchaseOrders opo ON opo.PONumber = sih.fld_str_PO_PONumber
LEFT JOIN %portaldb.tblHelpdeskResource hdr ON sih.fld_int_Stage1Approver_HelpDeskResourceId = hdr.fld_int_ID
LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteHeader scnh ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id
WHERE scnh.fld_int_SupplierInvoiceHeaderId IS NOT NULL
AND sis.fld_str_DisplayName NOT IN ('New', 'Submitted With Errors')
AND scns.fld_str_DisplayName IN ('New', 'Submitted With Errors')
AND po.POtypeID = 28),

SubmittedWtihErrors_5_CTE AS (SELECT sih.fld_int_Id Id,
  'Invoice' AS Type,
  po.[Supplier Name] Supplier,
  LTRIM(sih.fld_str_InvoiceNumber) RefNum,
  sih.fld_str_PO_PONumber OrderRef,
  CAST(ppmsl.fld_int_ID AS VARCHAR) JobRef,
  IIF(s.fld_bit_VIP = 1 , concat(s.fld_str_Name, ' (VIP) '), s.fld_str_Name) AS Site,
  sih.fld_dat_InvoiceDate Date,
  sih.fld_cur_NetAmount Net,
  sih.fld_cur_VatAmount Tax,
  (sih.fld_cur_NetAmount + sih.fld_cur_VatAmount) AS Gross,
  sis.fld_str_DisplayName Status,
  'No' AS HasCredit
FROM %portaldb.tblcfSupplierInvoiceHeader sih
JOIN %portaldb.vw_purchase_order po ON sih.fld_str_PO_PONumber = po.[PO No]
JOIN %portaldb.tblPO tpo ON sih.fld_str_PO_PONumber = tpo.fld_str_PONumber
JOIN %portaldb.tblProjectHeader ph ON tpo.fld_int_ProjectHeaderId = ph.fld_int_Id
JOIN %portaldb.tblPPMScheduleLine ppmsl ON ph.fld_int_PPMScheduleRef = ppmsl.fld_int_ID
JOIN %portaldb.tblStore s ON ppmsl.fld_int_StoreID = s.fld_int_ID
JOIN %portaldb.tblStoreCluster sc ON s.fld_int_StoreClusterID = sc.fld_int_ID
LEFT JOIN %portaldb.tblHelpDeskResource hdr ON sc.fld_int_AreaManagerID = hdr.fld_int_ID
LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteHeader scnh ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id
WHERE sis.fld_str_DisplayName NOT IN ('New', 'Submitted With Errors')
AND scnh.fld_int_SupplierInvoiceHeaderId IS NULL
AND tpo.fld_int_POTypeID IN (37, 43)),

SubmittedWtihErrors_6_CTE AS (SELECT sih.fld_int_Id Id,
  'Invoice' AS Type,
  po.[Supplier Name] Supplier,
  LTRIM(sih.fld_str_InvoiceNumber) RefNum,
  sih.fld_str_PO_PONumber OrderRef,
  CAST(ppmsl.fld_int_ID AS VARCHAR) JobRef,
  IIF(s.fld_bit_VIP = 1 , concat(s.fld_str_Name, ' (VIP) '), s.fld_str_Name) AS Site,
  sih.fld_dat_InvoiceDate Date,
  sih.fld_cur_NetAmount Net,
  sih.fld_cur_VatAmount Tax,
  (sih.fld_cur_NetAmount + sih.fld_cur_VatAmount) AS Gross,
  sis.fld_str_DisplayName Status,
  'No' AS HasCredit
FROM %portaldb.tblcfSupplierInvoiceHeader sih
LEFT JOIN %portaldb.vw_purchase_order po ON sih.fld_str_PO_PONumber = po.[PO No]
LEFT JOIN %portaldb.tblPO tpo ON sih.fld_str_PO_PONumber = tpo.fld_str_PONumber
LEFT JOIN %portaldb.tblProjectHeader ph ON tpo.fld_int_ProjectHeaderId = ph.fld_int_Id
LEFT JOIN %portaldb.tblPPMScheduleLine ppmsl ON ph.fld_int_PPMScheduleRef = ppmsl.fld_int_ID
LEFT JOIN %portaldb.tblStore s ON ppmsl.fld_int_StoreID = s.fld_int_ID
LEFT JOIN %portaldb.tblStoreCluster sc ON s.fld_int_StoreClusterID = sc.fld_int_ID
LEFT JOIN %portaldb.tblHelpDeskResource hdr ON sc.fld_int_AreaManagerID = hdr.fld_int_ID
LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteHeader scnh ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id
WHERE scnh.fld_int_SupplierInvoiceHeaderId IS NOT NULL
AND sis.fld_str_DisplayName NOT IN ('New', 'Submitted With Errors')
AND scns.fld_str_DisplayName IN ('New', 'Submitted With Errors')
AND tpo.fld_int_POTypeID IN (37, 43)),

SubmittedWtihErrors_7_CTE AS (SELECT sih.fld_int_Id Id,
  'Invoice' AS Type,
  po.[Supplier Name] Supplier,
  LTRIM(sih.fld_str_InvoiceNumber) RefNum,
  sih.fld_str_PO_PONumber OrderRef,
  CAST(po.FaultID AS varchar) JobRef,
  StoreName Site,
  sih.fld_dat_InvoiceDate Date,
  sih.fld_cur_NetAmount Net,
  sih.fld_cur_VatAmount Tax,
  (sih.fld_cur_NetAmount + sih.fld_cur_VatAmount) AS Gross,
  sis.fld_str_DisplayName Status,
  'Yes' AS HasCredit
FROM %portaldb.tblcfSupplierInvoiceHeader sih
LEFT JOIN %portaldb.vw_purchase_order po ON po.[PO No] = sih.fld_str_PO_PONumber
LEFT JOIN %portaldb.uvw_OpenPurchaseOrders opo ON opo.PONumber = sih.fld_str_PO_PONumber
LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteHeader scnh ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id
WHERE scnh.fld_int_SupplierInvoiceHeaderId IS NOT NULL
AND scns.fld_str_DisplayName NOT IN ('New', 'Submitted With Errors')),

SubmittedWtihErrors_8_CTE AS (SELECT sih.fld_int_Id Id,
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
AND scns.fld_str_DisplayName NOT IN ('New', 'Submitted With Errors')),

SubmittedWtihErrors_9_CTE AS (SELECT sih.fld_int_Id Id,
 'Credit' AS Type,
  r.fld_str_Name Supplier,
  LTRIM(scnh.fld_str_CreditNoteNumber) RefNum,
  '' OrderRef,
  'N/A' AS JobRef,
  'N/A' AS Site,
  scnh.fld_dat_CreditNoteDate Date,
  scnh.fld_cur_NetAmount Net,
  scnh.fld_cur_TaxAmount Tax,
  (scnh.fld_cur_NetAmount + scnh.fld_cur_TaxAmount) AS Gross,
  scns.fld_str_DisplayName Status,
  'N/A' AS HasCredit
FROM %portaldb.tblcfSupplierCreditNoteHeader scnh
LEFT JOIN %portaldb.tblHelpDeskResource r ON r.fld_str_SupplierID = scnh.fld_str_SupplierId
LEFT JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scns.fld_int_Id = scnh.fld_int_SupplierCreditNoteStatusId
LEFT JOIN %portaldb.tblcfSupplierCreditNoteType scnt ON scnh.fld_int_SupplierCreditNoteTypeId = scnt.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierInvoiceHeader sih ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id
WHERE scnh.fld_int_SupplierInvoiceHeaderId IS NULL
AND scns.fld_str_DisplayName NOT IN ('New', 'Submitted With Errors')),

UNION_CTE AS (
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from SubmittedWithErrors_1_CTE
UNION
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from SubmittedWtihErrors_2_CTE 
UNION
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from SubmittedWtihErrors_3_CTE 
UNION
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from SubmittedWtihErrors_4_CTE 
UNION
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from SubmittedWtihErrors_5_CTE 
UNION
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from SubmittedWtihErrors_6_CTE 
UNION
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from SubmittedWtihErrors_7_CTE 
UNION
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from SubmittedWtihErrors_8_CTE
UNION
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from SubmittedWtihErrors_9_CTE)

SELECT  Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
FROM UNION_CTE
WHERE Date BETWEEN DATEADD(DAY, -31, GETUTCDATE()) AND GETUTCDATE()