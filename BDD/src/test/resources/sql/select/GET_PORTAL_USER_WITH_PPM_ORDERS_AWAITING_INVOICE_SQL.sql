SELECT TOP 1
        NEWID() Id,
        Resources.fld_int_ID AS ResourceID,
        PSL.fld_int_ID AS JobReference,
        au.UserName,
        s.Id SiteId,
        COUNT(sih.fld_str_PO_PONumber)
        
    FROM
        %portaldb.tblPO AS PO
        INNER JOIN %portaldb.tblProjectHeader PH ON PO.fld_int_ProjectHeaderID = PH.fld_int_id
        INNER JOIN %portaldb.tblPPMScheduleLine AS PSL ON PH.fld_int_PPMScheduleRef = PSL.fld_int_ID
        INNER JOIN %portaldb.tblHelpDeskResource AS Resources ON PO.fld_str_SupplierID = Resources.fld_str_SupplierID
	 INNER JOIN %helpdeskdb.Resource AS MercuryResource ON Resources.fld_int_ID = MercuryResource.EpochID
        INNER JOIN %helpdeskdb.ApplicationUser au ON au.ResourceId = MercuryResource.ID
        INNER JOIN %portaldb.tblStore AS Store ON PSL.fld_int_StoreID = Store.fld_int_ID
        INNER JOIN %helpdeskdb.Site s ON s.id = Store.fld_int_ID
        LEFT JOIN  %portaldb.tblcfSupplierInvoiceHeader sih ON PO.fld_str_PONumber = sih.fld_str_PO_PONumber
	    LEFT JOIN  %portaldb.tblcfSupplierInvoiceLine sil ON sih.fld_int_Id = sil.fld_int_SupplierInvoiceHeaderId
	    LEFT JOIN %portaldb.tblcfSupplierInvoiceLineType silt ON sil.fld_int_SupplierInvoiceLineType = silt.fld_int_Id
    WHERE
        au.UserName IS NOT NULL
        AND au.UserProfileId = (SELECT id FROM %helpdeskdb.UserProfile WHERE name = :profileName )
        AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL)
        AND PSL.fld_int_StatusID = 2
        AND PO.fld_int_POTypeID = 43
        AND Resources.fld_bit_OnlineInvoicingActive = 1
        AND fld_cur_CostExpected != 0.00
        AND sil.fld_cur_UnitPrice != 0.00
