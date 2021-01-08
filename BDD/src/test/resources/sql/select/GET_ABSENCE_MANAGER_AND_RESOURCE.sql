WITH OrgStructure AS (
    SELECT OS.Id AS OrganisationStructureId, OS.ParentId AS ParentOrganisationStructureId, R.ResourceProfileId, R.Id AS ResourceId, R.Name AS ResourceName, R.Active, OST.Name AS OrgStructureType
    FROM 
        OrganisationStructure OS
        LEFT JOIN OrganisationStructureSite OSS ON OS.Id = OSS.OrganisationStructureId AND OS.Active = 1 AND OS.Active = 1
        LEFT JOIN OrganisationStructureManager OSM ON OSM.OrganisationStructureId = OS.Id AND OSM.Active = 1
        LEFT JOIN ApplicationUser AU ON AU.Id = OSM.ApplicationUserId
        LEFT JOIN Resource R ON R.Id = AU.ResourceId
        INNER JOIN OrganisationStructureType OST ON OST.Id = OS.OrganisationStructureTypeId
    UNION ALL
    SELECT OS.Id AS OrganisationStructureId, OS.ParentId AS ParentOrganisationStructureId, R.ResourceProfileId, R.Id AS ResourceId, R.Name AS ResourceName, R.Active, OST.Name AS OrgStructureType
    FROM
        OrganisationStructure OS 
        INNER JOIN OrgStructure OSChild ON OS.Id = OSChild.ParentOrganisationStructureId
        OUTER APPLY (SELECT * FROM OrganisationStructureManager OSM WHERE OSM.OrganisationStructureId = Os.Id AND OSM.Active = 1) OSM
        OUTER APPLY (SELECT * FROM ApplicationUser AU WHERE AU.Id = OSM.ApplicationUserId) AU
        OUTER APPLY (SELECT * FROM Resource R WHERE R.Id = AU.ResourceId) R
        INNER JOIN OrganisationStructureType OST ON OST.Id = OS.OrganisationStructureTypeId
)
, UnavailableResource AS (
    SELECT os.*, rav.StartAt, rav.EndAt, rav.CoveringResourceId
    FROM ResourceAvailability rav 
    INNER JOIN Resource r ON rav.ResourceId = r.Id
    INNER JOIN OrgStructure os ON r.Id = os.ResourceId
    WHERE r.Active = 1 AND rav.Active = 1 
    AND (
            (rav.StartAt BETWEEN GETDATE() AND DATEADD(day, 13, GETDATE())) 
            OR 
            (rav.EndAt BETWEEN GETDATE() AND DATEADD(day, 13, GETDATE()))
            OR 
            (rav.StartAt < GETDATE() AND rav.EndAt > DATEADD(day, 13, GETDATE()))
        )
    AND rav.ResourceAvailabilityTypeId NOT IN (4, 5) -- not in Meeting or a on a Call
)
SELECT DISTINCT ur.ResourceId, ur.ResourceName, rp.Name AS ResourceProfileName, ur.OrgStructureType, ur.StartAt, ur.EndAt, mgr.Id AS ManagerResourceId, mgr.Name AS ManagerName, au.UserName AS ManagerUserName, up.Name AS ManagerUserProfile, ur.CoveringResourceId, cover.Name AS CoveringName
FROM UnavailableResource ur 
LEFT JOIN ResourceProfile rp ON rp.Id = ur.ResourceProfileId
LEFT JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId
INNER JOIN OrganisationStructureManager m ON m.OrganisationStructureId = ur.ParentOrganisationStructureId AND m.Active = 1
LEFT JOIN ApplicationUser au ON au.Id = m.ApplicationUserId
LEFT JOIN UserProfile up ON up.Id = au.UserProfileId
LEFT JOIN Resource mgr ON mgr.Id = au.ResourceId
LEFT JOIN Resource cover ON cover.Id = ur.CoveringResourceId
