WITH StructureCTE AS
(
    SELECT os.Id AS OrganisationStructureId, os.ParentId
    FROM OrganisationStructure os
    WHERE os.Id IN (
            SELECT os1.OrganisationStructureId
            FROM OrganisationStructureSite os1
            INNER JOIN Job j ON j.SiteId = os1.SiteId
            INNER JOIN OrganisationStructure o ON o.Id = os1.OrganisationStructureId
            WHERE j.JobReference = :jobReference
        )
    UNION ALL
    SELECT os.Id AS OrganisationStructureId, os.ParentId
    FROM OrganisationStructure os
    INNER JOIN StructureCTE ct ON os.Id = ct.ParentId
)
SELECT TOP(1) au.Id AS ApplicationUserId, au.UserName, r.Name, r.Id ResourceId, r.EpochId, up.Name UserProfileName, rp.Name ResourceProfileName, rt.Name ResourceTypeName
FROM StructureCTE s
INNER JOIN OrganisationStructureManager m ON s.OrganisationStructureId = m.OrganisationStructureId
INNER JOIN ApplicationUser au ON au.Id = m.ApplicationUserId
INNER JOIN UserProfile up ON up.Id = au.UserProfileId
INNER JOIN Resource r ON r.Id = au.ResourceId
INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id
INNER JOIN ResourceType rt ON rp.ResourceTypeId = rt.Id
WHERE r.Active = 1 AND m.Active = 1 AND au.Active = 1 
AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL)
AND (up.Name = :resourceType OR rp.Name = :resourceType OR rt.Name = :resourceType) 
    