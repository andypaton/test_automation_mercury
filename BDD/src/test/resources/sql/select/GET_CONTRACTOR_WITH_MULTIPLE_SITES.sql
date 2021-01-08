WITH CONTRACTOR_SITES AS (
    SELECT  SiteId, resourceId
    FROM AssetClassificationSiteContractorMapping acscm
    INNER JOIN AssetClassificationSite acs ON acs.Id = acscm.AssetClassificationSiteId
)
SELECT TOP(1) s1.id Site1Id, s1.Name Site1Name, s1.Location.Lat Site1Latitude, s1.Location.Long Site1Longitude
            , s2.id Site2Id, s2.Name Site2Name, s2.Location.Lat Site2Latitude, s2.Location.Long Site2Longitude
            , cs1.ResourceId, r.Name AS ResourceName, au.UserName
            , GEOGRAPHY::Point(s1.Location.Lat, s1.Location.Long, 4326).STDistance(GEOGRAPHY::Point(s2.Location.Lat, s2.Location.Long, 4326)) AS Metres
FROM Site s1
INNER JOIN Site s2 ON s1.Id != s2.Id AND s2.Active = 1 
INNER JOIN CONTRACTOR_SITES cs1 ON cs1.SiteId = s1.Id
INNER JOIN CONTRACTOR_SITES cs2 ON cs2.SiteId = s2.Id
INNER JOIN Resource r ON cs1.ResourceId = r.Id AND cs2.ResourceId = r.Id AND r.Active = 1 
INNER JOIN ApplicationUser au ON au.ResourceId = r.Id AND au.Active = 1
INNER JOIN UserProfile up ON au.UserProfileId = up.Id AND up.Name = 'Contractor Technician'
WHERE cs1.ResourceId = cs2.ResourceId
AND s1.Active = 1 
--AND s1.StoreOpenDate < GETDATE() - 356         --not a warranty site  -- commenting out as there is no data in advocate
--AND s2.StoreOpenDate < GETDATE() - 356         --not a warranty site  -- commenting out as there is no data in advocate
AND s1.SiteTypeId != 22 AND s2.SiteTypeId != 22    -- not Sam's Club
