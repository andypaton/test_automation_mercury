SELECT TOP(1)
NEWID() AS Id,
hdr.fld_int_ID AS [EpochId],
r.Id AS [ResourceId],
po.fld_int_HelpdeskFaultID AS [JobReference],
ms.SiteId AS [SiteId],
hdr.fld_str_LogOn AS [UserName]
FROM
%helpdeskdb.InitialApproverRule ia 
INNER JOIN %helpdeskdb.Budget b ON b.FundingRouteId = ia.FundingRouteId 
INNER JOIN %helpdeskdb.ResourceProfile rp ON rp.id = ia.ResourceProfileId
INNER JOIN %helpdeskdb.uvw_ManagerSites ms ON ms.ResourceProfileName = rp.Name
INNER JOIN %helpdeskdb.ApplicationUser au ON au.Id = ms.ApplicationUserId
INNER JOIN %helpdeskdb.Resource r ON au.ResourceID = r.ID
INNER JOIN %portaldb.tblHelpDeskResource hdr ON hdr.fld_int_ID = r.EpochId
INNER JOIN %portaldb.uvw_HelpDeskSiteManagers sm ON sm.PortalSiteId = ms.SiteId     
INNER JOIN %portaldb.tblHelpDeskFault hdf ON hdf.fld_int_StoreID = ms.SiteId
INNER JOIN %helpdeskdb.Site s ON s.id = hdf.fld_int_StoreID
INNER JOIN %portaldb.tblPO po ON po.fld_int_HelpdeskFaultID = hdf.fld_int_ID
INNER JOIN %portaldb.tblcfSupplierInvoiceHeader sih ON sih.fld_int_PO_ID = po.fld_int_ID
WHERE ia.ApprovalTypeId = 4
AND sih.fld_int_SupplierInvoiceStatusId = 3
AND sih.fld_bit_IsCancelled <> 1
AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL)
AND au.UserName IS NOT NULL
AND b.Name IS NOT NULL
AND ia.ResourceProfileId = r.ResourceProfileId
ORDER BY NEWID()