WITH FaultPrioityMappings AS (
    SELECT 
    fpm.Id, fpm.SiteTypeId, st.Name SiteTypeName, fpm.AssetSubTypeId, ast.Name AssetSubTypeName, fpm.AssetClassificationId
    , ac.Name AssetClassificationName, fpm.FaultTypeId, ft.Name FaultTypeName, fpm.ResponsePriorityId, fpm1.Priority, fpm1.Detail
    FROM dbo.FaultPriorityMapping fpm 
    INNER JOIN uvw_faultprioritymappings fpm1 ON fpm.Id = fpm1.Id 
    INNER JOIN JobRoutingProcess jp ON jp.Id = fpm.JobRoutingProcessId
    INNER JOIN dbo.SiteTypes st ON fpm.SiteTypeId = st.Id 
    INNER JOIN dbo.FaultType ft ON fpm.FaultTypeId = ft.Id 
    -- AND AvailableForStoreSelfLoggedJobs = 1
    INNER JOIN dbo.AssetSubType ast ON fpm.AssetSubTypeId = ast.Id 
    INNER JOIN AssetType at ON at.Id = ast.AssetTypeId 
    INNER JOIN dbo.AssetClassification ac ON fpm.AssetClassificationId = ac.Id
    WHERE st.Id != 22
   -- AND fp.NonDeferrable = 1 
    AND jp.RequiresReview = 0
    AND ac.Name <> ''
    AND at.UsesGas = 1
    AND ast.IsAssetPlant = 0 
    AND ac.HiddenFromUser = 0 
), SITE_RESOURCES AS (
    SELECT TOP(1) acs.SiteId, r.Id AS ResourceId, au.Id AS ApplicationUserId, au.UserName, up.Name AS UserProfileName
    FROM ApplicationUser au 
    INNER JOIN UserProfile up ON up.Id = au.UserProfileId 
    INNER JOIN Resource r ON au.ResourceId = r.Id 
    INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id
    INNER JOIN GasSourceTypeResourceProfileMapping gstrpm ON rp.Id = gstrpm.ResourceProfileId
    INNER JOIN AssetClassificationSiteContractorMapping acscm ON acscm.ResourceId = r.Id
    INNER JOIN AssetClassificationSite acs ON acscm.AssetClassificationSiteId = acs.Id
    INNER JOIN %portaldb.tblHelpDeskResource hdr ON r.EpochId = hdr.fld_int_ID 
    INNER JOIN %portaldb.tblPriceBook pb ON pb.fld_int_resourceid = hdr.fld_int_Id 
    WHERE r.Active = 1 AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL)
    AND ((up.Name = 'Contractor Admin' AND r.UsesEngineers = 0) OR up.Name = 'Contractor Technician')
    AND r.Name NOT LIKE '%*%'
    AND pb.fld_str_PartCode = 'Standard Callout' AND pb.fld_cur_CostEach > 0  
)   
SELECT TOP(1) fpm.FaultTypeId, fpm.FaultTypeName, fpm.AssetClassificationId, fpm.AssetClassificationName, fpm.AssetSubTypeName, fpm.ResponsePriorityId, sr.SiteId
, s.Name AS SiteName, r.Id ResourceId, r.Name ResourceName, stlm.LocationId, l.Name AS LocationName, sr.ApplicationUserId, sr.UserName, sr.UserProfileName
FROM FaultPrioityMappings fpm
INNER JOIN Site s ON fpm.SiteTypeId = s.SiteTypeId
INNER JOIN SiteTypes st ON s.SiteTypeId = st.Id
LEFT JOIN ForcedIncidentCriterion fic ON fpm.AssetClassificationId = fic.AssetClassificationId AND fpm.FaultTypeId = fic.FaultTypeId
INNER JOIN AssetClassificationSite acs ON acs.SiteId = s.Id AND acs.AssetClassificationId = fpm.AssetClassificationId
INNER JOIN SITE_RESOURCES sr ON s.Id = sr.SiteId 
INNER JOIN Resource r ON sr.ResourceId = r.Id
INNER JOIN AssetClassificationSiteTypeLocationsMapping acstlm ON acstlm.AssetClassificationId = acs.AssetClassificationId
INNER JOIN SiteTypeLocationMapping stlm ON acstlm.SiteTypeLocationMappingId = stlm.Id AND st.Id = stlm.SiteTypeId
INNER JOIN Location l ON l.Id = stlm.LocationId
WHERE 
fic.AssetClassificationId IS NULL AND fic.FaultTypeId IS NULL  --no forced incidents
AND s.StoreOpenDate < GETDATE() - 356         --not a warranty site
AND s.Active = 1
ORDER BY NEWID()