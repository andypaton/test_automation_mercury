SELECT TOP(1) au.* 
FROM ApplicationUser au LEFT OUTER JOIN Resource r  ON au.ResourceId = r.Id 
	INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id
	INNER JOIN UserProfile up ON au.UserProfileId = up.Id
	INNER JOIN Permission p ON au.UserProfileId = p.UserProfileId
	INNER JOIN AspNetRoles anr ON p.AspNetRoleId = anr.Id 
WHERE   
	anr.Name = :roleName
	AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL)
	AND (r.Active = 1  OR  r.active IS NULL)    
	AND ( ( 
	    CAST(:useResourceTypeName AS bit) = 0 
	    AND r.ResourceProfileId IN (SELECT id FROM ResourceProfile WHERE Name LIKE '%' + :profileName + '%')
	    )
	    OR
	    ( 
	    CAST(:useResourceTypeName AS bit) = 1 
	    AND au.ResourceId IN (SELECT r.Id FROM Resource r INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id INNER JOIN ResourceType rt ON rp.ResourceTypeId = rt.id where  rp.ActiveForClient = 1  AND rp.Active = 1 AND rt.Name = :resourceTypeName) 
	    )
	)
ORDER BY NEWID()