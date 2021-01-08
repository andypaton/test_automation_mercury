-- Return org structure, either:
-- all resources in org structure
-- all resources for a given site
-- all resources above a given resource

WITH OrgStructure AS (
    SELECT
        OS.Id AS OrganisationStructureId,
        OS.ParentId AS ParentOrganisationStructureId,
        R.ResourceProfileId,
        R.Id AS ResourceId,
        R.Name AS ResourceName,
        R.Active,
        OST.Name AS OrgStructureType,
        OS.Name AS OrgStructureName
    FROM 
        OrganisationStructure OS
        LEFT JOIN OrganisationStructureSite OSS ON OS.Id = OSS.OrganisationStructureId AND OS.Active = 1 AND OS.Active = 1
        LEFT JOIN OrganisationStructureManager OSM ON OSM.OrganisationStructureId = OS.Id AND OSM.Active = 1
        LEFT JOIN ApplicationUser AU ON AU.Id = OSM.ApplicationUserId
        LEFT JOIN Resource R ON R.Id = AU.ResourceId
        INNER JOIN OrganisationStructureType OST ON OST.Id = OS.OrganisationStructureTypeId
    :whereClause
    UNION ALL
    SELECT
        OS.Id AS OrganisationStructureId,
        OS.ParentId AS ParentOrganisationStructureId,
        R.ResourceProfileId,
        R.Id AS ResourceId,
        R.Name AS ResourceName,
        R.Active,
        OST.Name AS OrgStructureType,
        OS.Name AS OrgStructureName
    FROM
        OrganisationStructure OS 
        INNER JOIN OrgStructure OSChild ON OS.Id = OSChild.ParentOrganisationStructureId
        OUTER APPLY (SELECT * FROM OrganisationStructureManager OSM WHERE OSM.OrganisationStructureId = OS.Id AND OSM.Active = 1) OSM
        OUTER APPLY (SELECT * FROM ApplicationUser AU WHERE AU.Id = OSM.ApplicationUserId) AU
        OUTER APPLY (SELECT * FROM Resource R WHERE R.Id = AU.ResourceId) R
        INNER JOIN OrganisationStructureType OST ON OST.Id = OS.OrganisationStructureTypeId
)
, Alice AS (
    SELECT Id, CAST(Name AS NVarChar(MAX)) AS RabbitHole 
    FROM OrganisationStructure OS
    UNION ALL 
    SELECT hole.id, rabbit.RabbitHole + ' > ' + hole.Name 
    FROM OrganisationStructure hole 
    JOIN Alice rabbit ON rabbit.id = hole.ParentID  
)
, Breadcrumb AS (
    SELECT a.*
    FROM Alice a
    INNER JOIN (SELECT Id, MAX(LEN(RabbitHole)) AS MaxLength FROM Alice GROUP BY Id) b ON a.id = b.id AND LEN(a.RabbitHole) = MaxLength
)

SELECT DISTINCT
    os.OrganisationStructureId,
    os.ParentOrganisationStructureId,
    os.ResourceId,
    os.ResourceName,
    rp.Name AS ResourceProfileName,
    rt.Name AS ResourceTypeName,
    os.ResourceProfileId,
    os.Active, 
    os.OrgStructureType,
    b.RabbitHole AS Breadcrumb,
    au.UserName
FROM OrgStructure os
LEFT JOIN ResourceProfile rp ON rp.Id = os.ResourceProfileId
LEFT JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId
INNER JOIN Breadcrumb b ON b.Id = os.OrganisationStructureId
INNER JOIN ApplicationUser au ON au.ResourceId = os.ResourceId
WHERE os.ResourceId IS NOT NULL
ORDER BY ParentOrganisationStructureId
