SELECT TOP 1
        NEWID() id
        ,Resources.fld_int_ID AS [ResourceId]
        ,Fault.fld_int_ID  AS [JobReference]
		,au.UserName AS [UserName]
		,av.Id SiteId
		,COUNT(sih.fld_str_PO_PONumber)

	FROM
		%portaldb.tblPO AS PO
		INNER JOIN %portaldb.tblHelpDeskFault AS Fault ON PO.fld_int_HelpdeskFaultID = Fault.fld_int_ID
		INNER JOIN %portaldb.tblHelpDeskResource AS Resources ON PO.fld_str_SupplierID = Resources.fld_str_SupplierID
		INNER JOIN %helpdeskdb.Resource r ON Resources.fld_int_ID = r.EpochId
		INNER JOIN %helpdeskdb.ApplicationUser au ON r.Id = au.ResourceId
		INNER JOIN %helpdeskdb.Site s ON s.id = Fault.fld_int_StoreID
		INNER JOIN %iosdb.tblEpochNewPartsRequest enpr ON po.fld_int_HelpdeskFaultID = enpr.fld_int_jobid
		INNER JOIN %iosdb.tblEpochNewPartsRequestLine enprl ON enpr.fld_int_id = enprl.fld_int_requestID  
		LEFT JOIN %portaldb.tblcfSupplierInvoiceHeader sih ON PO.fld_str_PONumber = sih.fld_str_PO_PONumber
	    LEFT JOIN %portaldb.tblcfSupplierInvoiceLine sil ON sih.fld_int_Id = sil.fld_int_SupplierInvoiceHeaderId
	    LEFT JOIN %portaldb.tblcfSupplierInvoiceLineType silt ON sil.fld_int_SupplierInvoiceLineType = silt.fld_int_Id
        LEFT JOIN %portaldb.tblPO SuppPO ON PO.fld_int_ID= SuppPO.fld_int_SupplementaryParentPOID
		
	WHERE
	    au.UserName IS NOT NULL
	    AND Resources.fld_str_LogOn IS NOT NULL
        AND au.Active = 1
        AND au.UserProfileId = (SELECT id FROM %helpdeskdb.UserProfile WHERE name = :profileName )
        AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL)
	    AND PO.fld_bit_FullyInvoiced = 0
	    AND PO.fld_str_WorkOrderPDF IS NOT NULL
	    AND PO.fld_int_POTypeID = 5
	    AND ISNULL(PO.fld_int_SupplementaryParentPOID,0) = 0
	    AND PO.fld_bit_CancelledSuppExists IS NULL
	    AND Resources.fld_bit_OnlineInvoicingActive = 1
	    AND enprl.fld_int_qty > 1
	    AND sih.fld_str_InvoiceNumber != ' '
	    AND Fault.fld_int_ID IN (  SELECT fld_int_HelpdeskFaultID 
        FROM %portaldb.tblPO PO
        WHERE fld_int_HelpdeskFaultID IS NOT NULL
        AND fld_int_HelpdeskFaultID ! = 0
        GROUP BY fld_int_HelpdeskFaultID 
        HAVING COUNT(fld_int_HelpdeskFaultID) < 2 )
