WITH FaultPrioityMappings AS (
    SELECT 
    fpm.Id, fpm.SiteTypeId, st.Name SiteTypeName, fpm.AssetSubTypeId, ast.Name AssetSubTypeName, fpm.AssetClassificationId
    , ac.Name AssetClassificationName, fpm.FaultTypeId, ft.Name FaultTypeName, fpm.FaultPriorityId, fp.Priority, fp.Detail, fp.SelfAssigned, fp.TechBureau
    , fp.ImmediateCallout, fp.RequiresReview
    FROM dbo.FaultPriorityMapping fpm 
    INNER JOIN dbo.FaultPriority fp ON fpm.FaultPriorityId = fp.Id 
    INNER JOIN dbo.SiteTypes st ON fpm.SiteTypeId = st.Id 
    INNER JOIN dbo.FaultType ft ON fpm.FaultTypeId = ft.Id AND AvailableForStoreSelfLoggedJobs = 1
    INNER JOIN dbo.AssetSubType ast ON fpm.AssetSubTypeId = ast.Id 
    INNER JOIN AssetType at ON at.Id = ast.AssetTypeId 
    INNER JOIN dbo.AssetClassification ac ON fpm.AssetClassificationId = ac.Id
    WHERE st.Id != 22
    --AND fp.NonDeferrable = 1 
    AND RequiresReview = 0
    AND ac.Name <> ''
    AND at.UsesGas = 1
    AND ast.IsAssetPlant = 0 
    AND ac.HiddenFromUser = 0 
), SITE_RESOURCES AS (
    SELECT TOP(1) oss.SiteId, r.Id AS ResourceId, au.Id AS ApplicationUserId, au.UserName, up.Name AS UserProfileName
    FROM ApplicationUser au 
    INNER JOIN UserProfile up ON up.Id = au.UserProfileId 
    INNER JOIN Resource r ON au.ResourceId = r.Id 
    INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id
    INNER JOIN GasSourceTypeResourceProfileMapping gstrpm ON rp.Id = gstrpm.ResourceProfileId
    INNER JOIN RotaEntry re ON r.Id = re.ResourceId
    INNER JOIN OrganisationStructureSite oss ON oss.id=re.OrganisationStructureSiteId 
    WHERE r.Active = 1 AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL)
    AND r.Name NOT LIKE '%*%'
    AND re.Active=1
    AND (re.StartAt < cast(SWITCHOFFSET(GETDATE(), '+00:00') AS DATE) AND cast(SWITCHOFFSET(GETDATE(), '+00:00') AS DATE) < coalesce(re.EndAt, '2999-12-01'))
)   
SELECT TOP(1) fpm.FaultTypeId, fpm.FaultTypeName, fpm.AssetClassificationId, fpm.AssetClassificationName, fpm.AssetSubTypeName, fpm.FaultPriorityId, sr.SiteId
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