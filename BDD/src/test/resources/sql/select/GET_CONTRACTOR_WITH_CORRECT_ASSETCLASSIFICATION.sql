WITH FaultPrioityMappings AS (
    SELECT fpm.Id, fpm.SiteTypeId, st.Name SiteTypeName, fpm.AssetSubTypeId, ast.Name AssetSubTypeName, fpm.AssetClassificationId, 
                      ac.Name AssetClassificationName, fpm.FaultTypeId, ft.Name FaultTypeName, fpm.ResponsePriorityId, fpm1.Priority, fpm1.Detail, fpm1.SelfAssigned, fpm1.TechBureau, 
                       fpm1.ImmediateCallout, fpm1.RequiresReview
    FROM dbo.FaultPriorityMapping fpm 
    INNER JOIN uvw_faultprioritymappings fpm1 ON fpm.Id = fpm1.Id
	INNER JOIN JobRoutingProcess jp ON jp.Id = fpm.JobRoutingProcessId
    INNER JOIN dbo.SiteTypes st ON fpm.SiteTypeId = st.Id 
    INNER JOIN dbo.FaultType ft ON fpm.FaultTypeId = ft.Id --AND AvailableForStoreSelfLoggedJobs = 1 --commenting out as there is no data in advocate
    INNER JOIN dbo.AssetSubType ast ON fpm.AssetSubTypeId = ast.Id 
    INNER JOIN dbo.AssetClassification ac ON fpm.AssetClassificationId = ac.Id
    WHERE st.Id != 22
    AND jp.NonDeferrable = 1 
	AND fpm1.RequiresReview = 0
	AND fpm.ResponsePriorityId = fpm1.ResponsePriorityId
    --AND fp.priority = :priority
), SITE_RESOURCES AS (
    SELECT DISTINCT(SiteId), ResourceId 
    FROM (
        SELECT oss.SiteId, ResourceId 
        FROM RotaEntry re 
            INNER JOIN OrganisationStructureSite oss ON oss.id=re.OrganisationStructureSiteId 
        WHERE (re.StartAt < cast(SWITCHOFFSET(GETDATE(), '+00:00') AS DATE) AND cast(SWITCHOFFSET(GETDATE(), '+00:00') AS DATE) < coalesce(re.EndAt, '2999-12-01'))
            AND re.Active=1
        UNION
        SELECT SiteId, ResourceId 
        FROM AssetClassificationSiteContractorMapping acscm
            INNER JOIN AssetClassificationSite acs ON acscm.AssetClassificationSiteId = acs.Id
    ) u
)   
SELECT TOP(1) au.*
FROM FaultPrioityMappings fpm
INNER JOIN Site s ON fpm.SiteTypeId = s.SiteTypeId
INNER JOIN SiteTypes st ON s.SiteTypeId = st.Id
LEFT JOIN ForcedIncidentCriterion fic ON fpm.AssetClassificationId = fic.AssetClassificationId AND fpm.FaultTypeId = fic.FaultTypeId
INNER JOIN AssetClassificationSite acs ON acs.SiteId = s.Id AND acs.AssetClassificationId = fpm.AssetClassificationId
INNER JOIN SITE_RESOURCES sr ON s.Id = sr.SiteId
INNER JOIN Resource r ON sr.ResourceId = r.Id
INNER JOIN ApplicationUser au ON au.ResourceId = r.Id
INNER JOIN AssetClassificationSiteTypeLocationsMapping acstlm ON acstlm.AssetClassificationId = acs.AssetClassificationId
INNER JOIN SiteTypeLocationMapping stlm ON acstlm.SiteTypeLocationMappingId = stlm.Id AND st.Id = stlm.SiteTypeId
INNER JOIN Location l ON l.Id = stlm.LocationId
WHERE 
fic.AssetClassificationId IS NULL AND fic.FaultTypeId IS NULL  --no forced incidents
--AND s.StoreOpenDate < GETDATE() - 356         --not a warranty site -- commenting out as there is no data in advocate
AND s.Active = 1
AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) AND (r.Active = 1 OR r.active IS NULL) 
AND au.UserProfileId = :profileId