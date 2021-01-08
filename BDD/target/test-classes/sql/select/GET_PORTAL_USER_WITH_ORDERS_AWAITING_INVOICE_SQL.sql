SELECT TOP(1)
        NEWID() AS Id,
        Resources.fld_int_ID AS ResourceID,
        Fault.fld_int_ID AS JobReference,
        au.UserName AS UserName,
        s.Id AS SiteId,
        COUNT(sih.fld_str_PO_PONumber)
    FROM
        %portaldb.tblPO AS PO
        INNER JOIN %portaldb.tblHelpDeskResource AS Resources ON PO.fld_str_SupplierID = Resources.fld_str_SupplierID
        INNER JOIN %helpdeskdb.Resource AS MercuryResource ON Resources.fld_int_ID = MercuryResource.EpochID
        INNER JOIN %helpdeskdb.ApplicationUser au ON au.ResourceId = MercuryResource.ID
        INNER JOIN %portaldb.tblHelpDeskFault Fault ON PO.fld_int_HelpdeskFaultID = Fault.fld_int_ID
        INNER JOIN %portaldb.tblHelpDeskFaultRedundantInfo AS FaultRedundant ON PO.fld_int_HelpdeskFaultID = FaultRedundant.faultID
        INNER JOIN %portaldb.tblStore AS Store ON Fault.fld_int_StoreID = Store.fld_int_ID
        INNER JOIN %portaldb.tblHelpDeskFaultTime AS FaultTime ON Fault.fld_int_ID = FaultTime.fld_int_HelpDeskFaultID
        INNER JOIN %helpdeskdb.Site s ON s.id = Fault.fld_int_StoreID
		LEFT JOIN %portaldb.tblHelpDeskFaultTime AS FT2 ON Ft2.fld_int_HelpDeskFaultID = FaultTime.fld_int_HelpDeskFaultID
            AND FT2.fld_int_id > FaultTime.fld_int_id
            AND FT2.fld_int_HelpDeskResourceID = FaultTime.fld_int_HelpDeskResourceID
        LEFT JOIN %portaldb.tblHelpDeskFaultTime AS StoreVerifications ON Fault.fld_int_ID = StoreVerifications.fld_int_HelpDeskFaultID
            AND StoreVerifications.fld_bit_IsJobComplete = 1
            AND StoreVerifications.fld_int_HelpDeskTimeStampTypeID IN (25, 29)
        INNER JOIN %portaldb.vw_LatestVisibleEventForJobPerResource AS latest ON latest.JobRef = fault.fld_int_ID
            AND latest.ResourceID = resources.fld_int_ID
        LEFT JOIN  %portaldb.tblcfSupplierInvoiceHeader AS sih ON PO.fld_str_PONumber = sih.fld_str_PO_PONumber
        LEFT JOIN  %helpdeskdb.Job AS job ON Fault.fld_int_ID = job.JobReference
        LEFT JOIN  %helpdeskdb.uvw_PpmJobs AS ppmj ON ppmj.ResourceEpochId = Resources.fld_int_ID
    WHERE
        au.UserName IS NOT NULL
        AND au.UserProfileId = (SELECT id FROM %helpdeskdb.UserProfile WHERE name = :profileName )
        AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL)
        AND au.Active = 1
        AND PO.fld_bit_FullyInvoiced = 0
        AND ISNULL(fld_int_SupplementaryParentPOID,0) = 0
        AND PO.fld_bit_CancelledSuppExists IS NULL
        AND PO.fld_cur_CostExpected != 0
        AND PO.fld_int_POStatusID = 7
        AND au.UserName != 'C000803' -- Added this condition until MCP-17583 is resolved
        AND Resources.fld_int_id = FaultTime.fld_int_HelpDeskResourceID
        AND Resources.fld_bit_OnlineInvoicingActive = 1
        AND FaultTime.fld_int_HelpDeskTimeStampTypeID = 4  -- with On Site event  
        AND FaultTime.fld_bit_IsCancelled = 0
        AND latest.StatusDescription = 'Complete'
        AND job.JobStatusId = 9  -- Only jobs with Fixed Status
        AND PO.fld_int_POTypeID IN (5,17)
        AND ppmj.ResourceEpochId IS NULL
        AND
        (
          ((:existingInvoice = 'Yes') AND (sih.fld_int_SupplierInvoiceStatusId <> 6 ))  -- Should not be with status Rejected 
          OR
          ((:existingInvoice = 'No') AND (ISNULL(PO.fld_cur_OriginalCost,0) != 0))
        )
