WITH FaultPrioityMappings AS (
    SELECT fpm.Id, fpm.SiteTypeId, st.Name SiteTypeName, fpm.AssetSubTypeId, ast.Name AssetSubTypeName, fpm.AssetClassificationId, 
                      ac.Name AssetClassificationName, fpm.FaultTypeId, ft.Name FaultTypeName, fpm.ResponsePriorityId, fpm1.Priority, fpm1.Detail, fpm1.SelfAssigned, fpm1.TechBureau, 
                      fpm1.ImmediateCallout, fpm1.RequiresReview
     FROM dbo.FaultPriorityMapping fpm 
    INNER JOIN uvw_faultprioritymappings fpm1 ON fpm.Id = fpm1.Id
	INNER JOIN JobRoutingProcess jp ON jp.Id = fpm.JobRoutingProcessId
    INNER JOIN dbo.SiteTypes st ON fpm.SiteTypeId = st.Id 
    INNER JOIN dbo.FaultType ft ON fpm.FaultTypeId = ft.Id 
	--AND AvailableForStoreSelfLoggedJobs = 1
    INNER JOIN dbo.AssetSubType ast ON fpm.AssetSubTypeId = ast.Id 
    INNER JOIN dbo.AssetClassification ac ON fpm.AssetClassificationId = ac.Id
    WHERE st.Id != 22
    AND jp.NonDeferrable = 1 
	AND fpm1.RequiresReview = 0
	AND fpm.ResponsePriorityId = fpm1.ResponsePriorityId
    --AND fp.priority = :priority
)
SELECT TOP(1) fpm.FaultTypeId, fpm.FaultTypeName, fpm.AssetClassificationId, fpm.AssetClassificationName, fpm.AssetSubTypeName, fpm.ResponsePriorityId FaultPriorityId, s.Id SiteId, s.Name AS SiteName
, stlm.LocationId, l.Name AS LocationName
FROM FaultPrioityMappings fpm
INNER JOIN Site s ON fpm.SiteTypeId = s.SiteTypeId
INNER JOIN SiteTypes st ON s.SiteTypeId = st.Id
LEFT JOIN ForcedIncidentCriterion fic ON fpm.AssetClassificationId = fic.AssetClassificationId AND fpm.FaultTypeId = fic.FaultTypeId
INNER JOIN AssetClassificationSite acs ON acs.SiteId = s.Id AND acs.AssetClassificationId = fpm.AssetClassificationId
INNER JOIN AssetClassificationSiteTypeLocationsMapping acstlm ON acstlm.AssetClassificationId = acs.AssetClassificationId
INNER JOIN SiteTypeLocationMapping stlm ON acstlm.SiteTypeLocationMappingId = stlm.Id AND st.Id = stlm.SiteTypeId
INNER JOIN Location l ON l.Id = stlm.LocationId
WHERE fic.AssetClassificationId IS NULL AND fic.FaultTypeId IS NULL  --no forced incidents
--AND s.StoreOpenDate < GETDATE() - 356         --not a warranty site
AND s.Active = 1