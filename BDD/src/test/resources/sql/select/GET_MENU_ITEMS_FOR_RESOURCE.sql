WITH Alice AS (
    SELECT Id, Null AS ParentId, CAST(DisplayName AS NVarChar(MAX)) AS Menu
    FROM MenuItem
    WHERE ParentMenuItemId IS NULL
    UNION ALL
    SELECT hole.id, rabbit.id AS ParentId, rabbit.Menu + ' > ' + hole.DisplayName
    FROM MenuItem hole
    JOIN Alice rabbit ON rabbit.id = hole.ParentMenuItemId
), ResourceMenus AS (
    SELECT DISTINCT r.Id, r.Name, rt.Name AS ResourceType, rp.Name AS ResourceProfile, mi.Menu, anr.Name AS RoleName, mi.ParentId , mi.Id AS MenuItemId, up.Name AS UserProfileName
    FROM ApplicationUser au 
    INNER JOIN UserProfile up ON up.Id = au.UserProfileId
    INNER JOIN Resource r ON au.ResourceId = r.Id 
    INNER JOIN ResourceProfile rp ON rp.Id = r.ResourceProfileId
    INNER JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId
    INNER JOIN Permission p ON au.UserProfileId = p.UserProfileId
    INNER JOIN AspNetRoles anr ON p.AspNetRoleId = anr.Id 
    INNER JOIN AspNetRoleToMenuItem anrtmi ON anrtmi.AspNetRole = anr.Id
    INNER JOIN Alice mi ON mi.Id = anrtmi.MenuItemId
    WHERE r.Id = %d
)
SELECT * FROM ResourceMenus 
WHERE UserProfileName = '%s'