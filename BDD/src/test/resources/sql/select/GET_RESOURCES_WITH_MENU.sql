WITH Menus AS (
    SELECT Id, Null AS ParentId, CAST(DisplayName AS NVarChar(MAX)) AS Menu
    FROM MenuItem
    WHERE ParentMenuItemId IS NULL
    UNION ALL
    SELECT hole.id, rabbit.id AS ParentId, rabbit.Menu + ' > ' + hole.DisplayName
    FROM MenuItem hole
    JOIN Menus rabbit ON rabbit.id = hole.ParentMenuItemId
), ResourceMenus AS (
    SELECT DISTINCT r.Id, r.Name, rt.Name AS ResourceType, rp.Name AS ResourceProfile, mi.Menu, anr.Name AS RoleName, mi.ParentId , mi.Id AS MenuItemId, au.UserName, up.Name AS UserProfileName
    FROM ApplicationUser au 
    INNER JOIN UserProfile up ON up.Id = au.UserProfileId
    INNER JOIN Resource r ON au.ResourceId = r.Id 
    INNER JOIN ResourceProfile rp ON rp.Id = r.ResourceProfileId
    INNER JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId
    INNER JOIN Permission p ON au.UserProfileId = p.UserProfileId
    INNER JOIN AspNetRoles anr ON p.AspNetRoleId = anr.Id 
    INNER JOIN AspNetRoleToMenuItem anrtmi ON anrtmi.AspNetRole = anr.Id
    INNER JOIN Menus mi ON mi.Id = anrtmi.MenuItemId 
    WHERE r.Active = 1 AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL)
    AND up.DefaultPortal = 6 -- gets user profile with portal as default
), AbsentResources AS (
    SELECT * FROM ResourceAvailability rav
    WHERE rav.StartAt < GETDATE() AND rav.EndAt > GETDATE()-1 AND rav.Active = 1
)
SELECT rm.* FROM ResourceMenus rm
LEFT JOIN AbsentResources ar ON rm.Id = ar.ResourceId
LEFT JOIN ResourceAvailability ra ON ra.CoveringResourceId = rm.Id AND ra.StartAt < GETDATE() AND ra.EndAt > GETDATE()-1 
WHERE (ParentId IS NULL OR ParentId IN (SELECT MenuItemId FROM ResourceMenus WHERE ParentId IS NULL)) 
AND ar.Id IS NULL
AND ra.Id IS NULL 