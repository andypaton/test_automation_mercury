SELECT au.Username, r.Name AS ResourceName, RTRIM(au.FirstName + ' ' + au.LastName) AS Name, r.Id AS ResourceId, au.Id AS ApplicationUserId
FROM ApplicationUser au 
INNER JOIN UserProfile up ON up.Id = au.UserProfileId
INNER JOIN Resource r ON r.Id = au.ResourceId
WHERE up.Name = 'IT'
AND au.Active = 1 AND r.Active = 1
--AND au.PasswordHash = 'ACf6bmCh1o2t1BxtF1U0BFi4RArKmKf5mmxubtBFcV0lakWIO+ay29uicgAEkc2DxA=='    -- Pa55w0rd
--AND au.PasswordExpiryDate > GETDATE()     -- not required for IT users
--AND au.LockoutEnabled = 0                 -- not required for IT users
ORDER BY NEWID()