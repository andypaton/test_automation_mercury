 WITH InvoicesAwitingFinalApprovalCTE AS (
	SELECT po.JobRef JobReference,  po.SupplierId, po.SiteID SiteId
	FROM %portaldb.tblcfSupplierInvoiceHeader sih
	LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_ID
	LEFT JOIN %portaldb.uvw_PartsOrders po ON sih.fld_str_PO_PONumber = po.PONumber
	INNER JOIN %helpdeskdb.Job j ON j.JobReference = po.JobRef
	WHERE sis.fld_str_Name = 'Awaiting Final Approval'
), UserCTE AS (
	SELECT au.UserName , rp.Name, au.ResourceId
	FROM %helpdeskdb.Resource r
	JOIN %helpdeskdb.ResourceProfile rp ON r.ResourceProfileId = rp.Id
	JOIN %helpdeskdb.ApplicationUser au ON au.ResourceId = r.Id
	JOIN %helpdeskdb.UserProfile up ON au.UserProfileId = up.Id
	AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL)
    AND r.Active = 1
)
SELECT TOP (1) NEWID() Id, iafcte.JobReference, ucte.UserName, ucte.ResourceId, iafcte.SiteId
FROM InvoicesAwitingFinalApprovalCTE iafcte, UserCTE ucte
WHERE ucte.Name = :profileName
AND ucte.UserName NOT LIKE 'george.campbell' 