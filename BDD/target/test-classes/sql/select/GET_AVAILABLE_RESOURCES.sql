WITH Resources AS (
    SELECT au.UserName, r.Id, r.Name, rp.Name ResourceProfileName, rt.Name ResourceTypeName, up.Name UserProfileName
    FROM ApplicationUser au
    INNER JOIN UserProfile up ON au.UserProfileId = up.Id
    INNER JOIN Resource r ON au.ResourceId = r.Id
    INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id
    INNER JOIN ResourceType rt ON rp.ResourceTypeId = rt.Id
    WHERE au.Active=1 AND r.Active=1 AND rp.Active=1 AND rt.Active=1 AND up.Active=1
), UnavailableResources AS (
    SELECT rav.ResourceId, rav.StartAt, rav.EndAt
    FROM ResourceAvailability rav 
    WHERE rav.Active = 1 
    AND rav.StartAt < DATEADD(day, 14, GETDATE())
    AND rav.EndAt > GETDATE()
)
SELECT TOP(1) r.Id FROM Resources r
LEFT JOIN UnavailableResources ur ON ur.ResourceId = r.Id
WHERE ur.ResourceId IS NULL
AND (ResourceProfileName = :profileName OR ResourceTypeName = :profileName OR UserProfileName = :profileName)
AND r.Id != :resourceId