WITH StructureCTE AS
(
    SELECT
        os.Id AS OrganisationStructureId,
        os.ParentId
    FROM
        OrganisationStructure os
    WHERE
        os.Id IN (
            SELECT
                os1.OrganisationStructureId
            FROM 
                OrganisationStructureSite os1
                INNER JOIN OrganisationStructure o ON o.Id = os1.OrganisationStructureId
            WHERE
                os1.SiteId = :siteId
        )
    UNION ALL
    SELECT
        os.Id AS OrganisationStructureId,
        os.ParentId
    FROM
        OrganisationStructure os
        INNER JOIN StructureCTE ct ON os.Id = ct.ParentId
)


SELECT TOP 1
    u.Id,
    r.Name,
    r.Id ResourceId,
    (
        SELECT TOP 1
            EmailAddress
        FROM
            ResourceEmailAddress re
        WHERE
            re.ResourceId = r.Id
        ORDER BY
            re.[Priority] DESC
    ) AS Email
FROM 
    StructureCTE s
    INNER JOIN OrganisationStructureManager m ON s.OrganisationStructureId = m.OrganisationStructureId
    INNER JOIN ApplicationUser u ON u.Id = m.ApplicationUserId
    INNER JOIN UserProfile up ON up.Id = u.UserProfileId
    INNER JOIN Resource r ON r.Id = u.ResourceId
WHERE
    up.Name = :managerPosition
    AND r.Active = 1