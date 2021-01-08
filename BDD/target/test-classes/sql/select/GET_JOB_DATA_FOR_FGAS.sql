-- Use data taken from a previous job with a site visit using FGAS
SELECT TOP(1)
s.Name AS SiteName, s.Id AS SiteId
, au.Id AS ApplicationUserId, au.UserName
, up.Name AS UserProfileName
, r.Name AS ResourceName, r.Id AS ResourceId
, j.AssetClassificationId, j.FaultPriorityId, j.FaultTypeId
, ac.Name AS AssetClassificationName
, l.Name AS LocationName, l.Id AS LocationId 
FROM SiteVisitGasDetails svgd
INNER JOIN SiteVisits sv ON sv.Id = svgd.SiteVisitId
INNER JOIN ResourceAssignment ra ON sv.ResourceAssignmentId = ra.Id
INNER JOIN Job j ON j.Id = ra.JobId 
INNER JOIN Resource r ON ra.ResourceId = r.Id 
INNER JOIN Site s ON j.SiteId = s.Id
INNER JOIN ApplicationUser au ON au.ResourceId = r.id 
INNER JOIN UserProfile up ON up.Id = au.UserProfileId 
INNER JOIN Location l ON j.LocationId = l.Id
INNER JOIN AssetClassification ac ON j.AssetClassificationId = ac.id 
INNER JOIN AssetSubType ast ON ast.Id = ac.AssetSubTypeId
INNER JOIN AssetType at ON at.Id = ast.AssetTypeId
INNER JOIN Asset a ON a.AssetClassificationId = ac.Id AND a.SiteId = s.Id
WHERE r.Active = 1 AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL)
AND ast.IsAssetPlant = 0 
AND s.Active = 1
AND ((up.Name = 'Contractor Admin' AND r.UsesEngineers = 0) OR up.Name = 'Contractor Technician')
--AND up.Name NOT LIKE 'Contractor%'
ORDER BY NEWID()