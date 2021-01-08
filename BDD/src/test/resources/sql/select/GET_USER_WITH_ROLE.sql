SELECT au.*
FROM ApplicationUser au
INNER JOIN UserProfile up ON up.Id = au.UserProfileId
INNER JOIN Permission p ON up.Id = p.UserProfileId
INNER JOIN AspNetRoles anr ON p.AspNetRoleId = anr.Id
INNER JOIN Resource r ON r.Id = au.ResourceId
INNER JOIN Portals prtl ON prtl.Id = up.DefaultPortal
WHERE anr.Name = :roleName
AND r.Active = 1
AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL)
AND au.LockoutEnabled = 0
AND au.Username != 'systemuser'
AND au.UserName NOT LIKE '%Donnelll%'  -- remove this condition once bug MCP-20619 is fixed
