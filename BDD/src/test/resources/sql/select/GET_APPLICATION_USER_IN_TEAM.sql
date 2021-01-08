SELECT TOP(1) a.*
FROM (SELECT au.* FROM ApplicationUser au 
INNER JOIN TeamUserMapping tum ON tum.UserId = au.Id 
INNER JOIN Team t ON tum.TeamId = t.Id AND t.Name = ':team'
INNER JOIN UserProfile up ON up.Id = au.userProfileId 
WHERE au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) 
AND up.Name = ':profile' 
) a
WHERE a.Id NOT IN (SELECT au.Id FROM ApplicationUser au 
INNER JOIN TeamUserMapping tum ON tum.UserId = au.Id 
INNER JOIN Team t ON tum.TeamId = t.Id AND t.Name <> ':team'
INNER JOIN UserProfile up ON up.Id = au.userProfileId 
WHERE au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL)
AND up.Name = ':profile')
AND a.ResourceId NOT IN (SELECT Id FROM Resource WHERE Active = 0)
ORDER BY NEWID()