SELECT TOP(1)  
  s.Name AS SiteName, s.Id AS SiteId, s.SiteTypeId 
  , r.Name AS ResourceName, r.Id AS ResourceId, r.ResourceProfileName, r.ResourceTypeName 
  , fpm.AssetClassificationId, fpm.AssetClassificationName, at.Name AS AssetTypeName, fpm.AssetSubTypeName, fpm.Priority, fpm.FaultTypeId, fpm.FaultTypeName 
FROM
(SELECT * FROM %testdb.uvw_Stores) s
LEFT JOIN %testdb.uvw_SiteResources sr ON s.Id = sr.SiteId
LEFT JOIN %testdb.uvw_Resources r ON sr.ResourceId = r.Id
LEFT JOIN (SELECT fpm1.* 
           FROM uvw_faultprioritymappings  fpm1
           INNER JOIN (SELECT * FROM FaultType WHERE  AvailableForStoreSelfLoggedJobs = 1) ft ON fpm1.FaultTypeId = ft.Id
           WHERE fpm1.priority IN (%s)
           ) fpm ON fpm.SiteTypeId = s.SiteTypeId
INNER JOIN AssetSubType ast ON ast.Id = fpm.AssetSubTypeId
INNER JOIN AssetType at ON at.Id = ast.AssetTypeId 
INNER JOIN AssetClassificationSite acs ON acs.SiteId = s.Id 