WITH RESOURCES_AVAILABLE_TODAY AS
(
    SELECT sr.SiteId, r.Id AS ResourceId, r.Name AS ResourceName, rp.name AS ResourceProfileName, rwh.StartAt, rwh.EndAt, rwh.StartDayOfTheWeek, rwh.EndDayOfTheWeek 
    FROM Resource r  
    INNER JOIN ResourceProfile rp ON rp.Id = r.ResourceProfileId AND rp.ActiveForClient = 1 
    INNER JOIN ResourceWorkingHours rwh ON r.Id = rwh.ResourceId AND StartDayOfTheWeek =  DATEPART(dw, GETDATE()) AND EndDayOfTheWeek =  DATEPART(dw, GETDATE()) 
    INNER JOIN SiteResource sr ON sr.ResourceId = r.Id
    WHERE r.Active=1
)
, SITES AS (
    SELECT TOP(3) * FROM Site
)
, DEFERRABLE AS
(
    SELECT * FROM SiteTypeDeferralPriorityConfiguration stdpc
    WHERE stdpc.AutomaticallyDefer = 1 AND stdpc.DayOfTheWeek = DATEPART(dw, GETDATE())
)
SELECT TOP(1) acs.SiteId, r.ResourceId, r.ResourceName, r.ResourceProfileName, acs.id AS AssetClassificationSiteId, acs.AssetClassificationId
, r.StartAt, r.EndAt, r.StartDayOfTheWeek, r.EndDayOfTheWeek
, fpm.AssetClassificationName, fpm.AssetSubTypeName, fpm.Priority, fpm.ResponsePriorityId, fpm.FaultTypeId, fpm.FaultTypeName
FROM AssetClassificationSite acs
INNER JOIN SITES s ON s.Id=acs.SiteId
INNER JOIN RESOURCES_AVAILABLE_TODAY r ON r.SiteId = s.Id
INNER JOIN uvw_FaultPriorityMappings fpm ON fpm.SiteTypeId = s.SiteTypeId 
INNER JOIN FaultPriority fp ON fp.Id = fpm.ResponsePriorityId 
AND fpm.AssetClassificationId = acs.AssetClassificationId AND fpm.SelfAssigned = 0 
INNER JOIN DEFERRABLE d ON d.SiteTypeId = s.SiteTypeId
INNER JOIN FaultPriorityMapping fpm1 ON fpm.Id = fpm1.Id
INNER JOIN JobRoutingProcess jp ON jp.Id = fpm1.JobRoutingProcessId
WHERE fpm.ResponsePriorityId = d.FaultPriorityId  
AND jp.NonDeferrable = 0 
AND jp.RequiresReview = 0
AND s.Active = 1
ORDER BY NEWID()