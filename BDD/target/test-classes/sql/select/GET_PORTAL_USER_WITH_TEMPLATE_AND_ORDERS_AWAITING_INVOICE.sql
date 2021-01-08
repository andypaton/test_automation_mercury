SELECT TOP(1) NEWID() Id,
    au.UserName,
    Resources.fld_int_ID AS ResourceID,
    Fault.fld_int_ID AS JobReference,
    s.Id SiteId,
    COUNT(PH.fld_bit_InvoiceReceived) AS invoiceCount
FROM %portaldb.tblInvoiceTemplateField itf
    INNER JOIN %portaldb.tblInvoiceTemplate AS it ON itf.fld_int_invoiceTemplateID = it.fld_int_ID
    INNER JOIN %helpdeskdb.ApplicationUser AS au ON au.ResourceId = it.fld_int_SupplierID
    INNER JOIN %portaldb.tblPO AS PO ON PO.fld_str_SupplierID = au.UserName
    INNER JOIN %portaldb.tblHelpDeskFault Fault ON PO.fld_int_HelpdeskFaultID = Fault.fld_int_ID
    INNER JOIN %portaldb.tblHelpDeskFaultRedundantInfo AS FaultRedundant ON PO.fld_int_HelpdeskFaultID = FaultRedundant.faultID
    INNER JOIN %portaldb.tblStore AS Store ON Fault.fld_int_StoreID = Store.fld_int_ID
    INNER JOIN %portaldb.tblHelpDeskResource AS Resources ON PO.fld_str_SupplierID = Resources.fld_str_SupplierID
    INNER JOIN %portaldb.tblHelpDeskFaultTime AS FaultTime ON Fault.fld_int_ID = FaultTime.fld_int_HelpDeskFaultID
    INNER JOIN %helpdeskdb.Site s ON s.id = Fault.fld_int_StoreID
    LEFT JOIN %portaldb.tblHelpDeskFaultTime AS FT2 ON Ft2.fld_int_HelpDeskFaultID = FaultTime.fld_int_HelpDeskFaultID
        AND FT2.fld_int_id > FaultTime.fld_int_id
        AND FT2.fld_int_HelpDeskResourceID = FaultTime.fld_int_HelpDeskResourceID
    LEFT JOIN %portaldb.tblHelpDeskFaultTime AS StoreVerifications ON Fault.fld_int_ID = StoreVerifications.fld_int_HelpDeskFaultID
        AND StoreVerifications.fld_bit_IsJobComplete = 1
        AND StoreVerifications.fld_int_HelpDeskTimeStampTypeID IN (25, 29)
    INNER JOIN %portaldb.vw_LatestVisibleEventForJobPerResource latest ON latest.JobRef = fault.fld_int_ID
        AND latest.ResourceID = resources.fld_int_ID
    LEFT JOIN %portaldb.tblProjectHeader PH ON PO.fld_str_PONumber = PH.fld_int_id
    LEFT JOIN  %helpdeskdb.Job job ON Fault.fld_int_ID = job.JobReference
WHERE
    itf.fld_int_X1 <> 0
    AND itf.fld_int_Y1 <> 0
    AND itf.fld_int_Width <> 0
    AND itf.fld_int_Height <> 0
    AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL)
    AND PO.fld_bit_FullyInvoiced = 0
    AND ISNULL(fld_int_SupplementaryParentPOID,0) = 0
    AND PO.fld_bit_CancelledSuppExists IS NULL
    AND PO.fld_cur_CostExpected != 0
    AND PO.fld_int_POStatusID = 7 -- with PO status Completed
    AND Resources.fld_int_id = FaultTime.fld_int_HelpDeskResourceID
    AND Resources.fld_bit_OnlineInvoicingActive = 1
    AND FaultTime.fld_int_HelpDeskTimeStampTypeID = 4 -- with On Site event  
    AND FaultTime.fld_bit_IsCancelled = 0
    AND PH.fld_bit_InvoiceReceived IS NULL
    AND latest.StatusDescription = 'Complete'
    AND job.JobStatusId = 9 -- Only jobs with Fixed Status
    AND au.UserProfileId = (SELECT id FROM %helpdeskdb.UserProfile WHERE name = :profileName)
GROUP BY
    Resources.fld_int_ID, Fault.fld_int_ID, au.UserName, s.Id
HAVING COUNT(PH.fld_bit_InvoiceReceived ) = 0
ORDER BY NEWID()