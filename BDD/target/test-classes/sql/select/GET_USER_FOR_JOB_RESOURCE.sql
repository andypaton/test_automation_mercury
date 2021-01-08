SELECT TOP(1) NEWID() Id, ra.ResourceId, j.JobReference, au.UserName, s.Id SiteId  
FROM Job j INNER JOIN ResourceAssignment ra ON j.Id = ra.JobId   
    INNER JOIN ApplicationUser au ON au.ResourceId = ra.ResourceId     
    INNER JOIN ResourceAssignmentStatus ras ON ra.ResourceAssignmentStatusId = ras.Id 
    INNER JOIN Site s ON s.Id = j.SiteId 
    INNER JOIN UserProfile up ON up.Id = au.UserProfileId
WHERE j.JobReference = :jobReference    
    AND au.Active = 1 
    AND ra.Active = 1
    AND ( ( 
            CAST(:useResourceTypeName AS bit)  = 0 
            AND au.UserProfileId = (SELECT Id FROM dbo.UserProfile WHERE Name = :profileName)
           )
          OR
          ( 
            CAST(:useResourceTypeName AS bit) = 1 
            AND au.ResourceId IN (SELECT r.Id FROM Resource r INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id INNER JOIN ResourceType rt ON rp.ResourceTypeId = rt.id WHERE rp.JobCallouts = 1 AND rp.Active = 1 AND rt.Name = :resourceTypeName) 
          )
        )    
    /*AND j.id NOT IN (SELECT JobId FROM jobLock)*/
     AND up.Name = :profileName
     GROUP BY ra.ResourceId, j.JobReference, au.UserName, s.Id  
     ORDER BY NEWID()