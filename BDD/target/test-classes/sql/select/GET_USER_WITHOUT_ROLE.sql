SELECT TOP(1) au.*
FROM ApplicationUser au
INNER JOIN UserProfile up ON up.Id = au.UserProfileId
INNER JOIN permission p ON up.Id = p.UserProfileId
INNER JOIN AspNetRoles anr ON p.AspNetRoleId = anr.Id
INNER JOIN Resource r ON r.Id = au.ResourceId
WHERE anr.Name = 'Mercury_Helpdesk_Access'
AND r.Active = 1
AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL)
AND (up.Name = 'IT' OR up.Name LIKE 'Helpdesk%')
AND au.Id NOT IN (
  SELECT au.Id
  FROM ApplicationUser au
  INNER JOIN UserProfile up ON up.Id = au.UserProfileId
  INNER JOIN permission p ON up.Id = p.UserProfileId
  INNER JOIN AspNetRoles anr ON p.AspNetRoleId = anr.Id
  INNER JOIN Resource r ON r.Id = au.ResourceId
  WHERE anr.Name = ':role'
)
ORDER BY NEWID()