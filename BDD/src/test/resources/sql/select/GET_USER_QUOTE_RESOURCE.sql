SELECT TOP(1) qasu.JobReference AS JobReference, au.UserName, qasu.ResourceId ResourceId, s.Id SiteId 
FROM %testdb.uvw_QuoteApprovalScenariosUsers qasu
INNER JOIN %helpdeskdb.ApplicationUser au ON au.ResourceId = qasu.ResourceId   
INNER JOIN %helpdeskdb.Site s ON s.id = qasu.StoreId
WHERE   au.active = 1  
        AND ( ( 
            CAST(:useResourceTypeName AS bit)  = 0 
            AND au.UserProfileId = (SELECT TOP(1) up.id FROM UserProfile up
                            INNER JOIN permission p ON up.Id = p.UserProfileId
                            INNER JOIN  AspNetRoles  anr ON p.AspNetRoleId = anr.Id 
                            WHERE up.name = :profileName
                            OR  anr.Name = :profileName)
           )
          OR
          ( 
            CAST(:useResourceTypeName AS bit) = 1 
            AND au.ResourceId IN (SELECT r.Id FROM Resource r INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id INNER JOIN ResourceType rt ON rp.ResourceTypeId = rt.id where  rp.JobCallouts = 1 AND rp.Active = 1 AND rt.Name = :resourceTypeName) 
          )
        )
        AND qasu.JobReference = :jobReference
ORDER BY NEWID()