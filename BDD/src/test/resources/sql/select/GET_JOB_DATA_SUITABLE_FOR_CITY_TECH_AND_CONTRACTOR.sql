WITH CityTech AS (
    SELECT DISTINCT sr.SiteId, r.Id AS ResourceId, r.Name, rea.EmailAddress, rpn.PhoneNumber, pnt.Name AS PhoneNumberType, nmt.name AS Ipad, auma.Id AS HasLoggedIn
    FROM SiteResource sr 
    INNER JOIN Resource r ON r.Id = sr.ResourceId 
    INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id 
    LEFT JOIN ResourceNotificationMethod rnm ON r.Id = rnm.ResourceId
    LEFT JOIN NotificationMethodType nmt ON rnm.NotificationMethodTypeId = nmt.Id 
    LEFT JOIN ResourcePhoneNumber rpn ON rpn.ResourceId = r.Id 
    INNER JOIN PhoneNumberType pnt ON pnt.Id = rpn.PhoneNumberTypeId 
    LEFT JOIN ResourceEmailAddress rea ON rea.ResourceId = r.Id 
    LEFT JOIN ApplicationUser au ON au.ResourceId = r.Id 
    LEFT JOIN ApplicationUserMobileApplication auma ON auma.ApplicationUserId = au.Id 
    WHERE r.Active = 1
    AND rp.Name IN (SELECT DISTINCT rp.name FROM  ResourceProfile rp WHERE rp.ResourceTypeId = 1 AND rp.Active = 1 AND rp.ActiveForClient = 1 AND rp.JobCallouts = 1)
)
SELECT TOP(1) fp.FaultTypeId, fp.AssetClassificationId, fp.ResponsePriorityId, ct.*
FROM uvw_FaultPriorityMappings fp
INNER JOIN FaultPriorityMapping fpm ON fpm.Id = fp.Id 
INNER JOIN JobRoutingProcess jp ON jp.Id = fpm.JobRoutingProcessId
INNER JOIN Site s ON fp.SiteTypeId = s.SiteTypeId
LEFT JOIN ForcedIncidentCriterion fic ON fp.AssetClassificationId = fic.AssetClassificationId AND fp.FaultTypeId = fic.FaultTypeId
INNER JOIN AssetClassificationSite acs ON acs.SiteId = s.Id AND acs.AssetClassificationId = fp.AssetClassificationId
INNER JOIN AssetClassificationSiteContractorMapping acscm ON acscm.AssetClassificationSiteId = acs.Id -- contractor capable
INNER JOIN CityTech ct ON ct.SiteId = s.Id -- city tech assigned to site
WHERE jp.NonDeferrable = 1 
AND s.Active = 1
AND fp.FaultTypeId IS NOT NULL 
AND fp.AssetClassificationId IS NOT NULL
AND fic.AssetClassificationId IS NULL AND fic.FaultTypeId IS NULL  --no forced incidents
AND (s.StoreOpenDate < GETDATE() - 356  OR s.StoreOpenDate IS NULL)         --not a warranty site 
AND ct.SiteId IN (SELECT DISTINCT SiteId FROM %testdb.uvw_SiteResources sr JOIN Resource r ON sr.ResourceId = r.Id WHERE r.Active = 1 GROUP BY SiteId HAVING COUNT(SiteId) > 2 )
--no ipad: AND ct.Ipad IS NULL
--with ipad: AND ct.Ipad='iPad' AND ct.HasLoggedIn IS NOT NULL
--no email: AND ct.EmailAddress IS NULL
--with email: AND ct.EmailAddress IS NOT NULL
--with phone: AND ct.PhoneNumber IS NOT NULL
--with mobile: AND ct.PhoneNumberType = 'Mobile' AND ct.PhoneNumber != ''
--no mobile: AND ct.ResourceId NOT IN (SELECT ResourceId FROM CityTech WHERE PhoneNumberType = 'Mobile')
--with priority: AND fp.Detail = '%s' AND fp.Priority = %d AND fp.ImmediateCallout= 1
ORDER BY NEWID()