WITH Invoice AS ( 
    SELECT hdr.fld_str_Name [Approver 1], fr.Name [Original Budget], IIF(sih.fld_int_OriginalBudgetId != sih.fld_int_BudgetId, 'Yes', 'No') AS BudgetChange, ipn.Note [Budget Change Reason], IIF(sih.fld_bit_ExtremeWeather = 1, 'Yes', 'No') AS [Extreme Weather], fld_str_PO_PONumber [Order Ref],po.JobRef [Job Ref], CONVERT(NVARCHAR, ph.fld_cur_TotalProjectCost, 0) OrderValue, j.CreatedOn [Logged Date], CONCAT(av.SiteCode, ' (',  st.Name, ')') Site, sih.fld_str_InvoiceNumber [Inv Num], sih.fld_dat_InvoiceDate [Inv Date], po.Supplier, po.SupplierId,  CONVERT(NVARCHAR, sih.fld_cur_NetAmount, 0) Net, CONVERT(NVARCHAR, sih.fld_cur_VatAmount, 0) Tax, CONVERT(NVARCHAR, sih.fld_cur_GrossAmount, 0) Gross, ipn.CreatedOn 
    FROM %portaldb.tblcfSupplierInvoiceHeader sih 
    LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_ID 
    LEFT JOIN %portaldb.tblHelpDeskResource hdr ON sih.fld_int_Stage1Approver_HelpdeskResourceId = hdr.fld_int_ID 
    LEFT JOIN %helpdeskdb.Budget b ON sih.fld_int_OriginalBudgetId = b.Id 
    LEFT JOIN %helpdeskdb.FundingRoute fr ON b.FundingRouteId = fr.Id 
    LEFT JOIN %portaldb.uvw_PartsOrders po ON sih.fld_str_PO_PONumber = po.PONumber 
    LEFT JOIN %portaldb.tblProjectHeader ph ON po.ProjectHeaderId = ph.fld_int_ID 
    LEFT JOIN %portaldb.tblHelpDeskFault hdf ON po.JobRef = hdf.fld_int_ID 
    INNER JOIN %helpdeskdb.Site av ON hdf.fld_int_StoreID = av.Id 
    LEFT JOIN %helpdeskdb.InvoiceProcessAction ipa ON ipa.InvoiceHeaderId = sih.fld_int_Id 
    LEFT JOIN %helpdeskdb.InvoiceProcessNote ipn ON ipa.Id = ipn.InvoiceProcessActionId 
    LEFT JOIN %helpdeskdb.Job j ON po.JobRef = j.JobReference 
    LEFT JOIN %helpdeskdb.SiteTypes st ON av.SiteTypeId = st.Id 
    WHERE sis.fld_str_Name = 'Awaiting Final Approval' 
), TotalJobCost AS ( 
    SELECT fld_int_HelpdeskfaultID JobRef, CONVERT(NVARCHAR, SUM(fld_cur_CostExpected), 0) [Total Job Cost] 
    FROM %portaldb.tblPO 
    GROUP BY fld_int_HelpdeskfaultID 
), OrderValue AS ( 
    SELECT MIN(PO.fld_cur_CostExpected) + COALESCE(SUM(POS.fld_cur_CostExpected), 0) [Order Value], PO.fld_str_ponumber OrderRef
    FROM portal.tblPO PO(NOLOCK)
    LEFT JOIN portal.tblPO POS(NOLOCK) ON PO.fld_int_ID=POS.fld_int_SupplementaryParentPOID
    GROUP BY PO.fld_str_ponumber
) 
SELECT i.*, tjc.[Total Job Cost], ov.[Order Value]
FROM Invoice i  
JOIN TotalJobCost tjc ON i.[Job Ref] = tjc.JobRef 
JOIN OrderValue ov ON i.[Order Ref] = ov.OrderRef 
WHERE i.[Inv Num] = '%s' 
ORDER BY i.CreatedOn DESC