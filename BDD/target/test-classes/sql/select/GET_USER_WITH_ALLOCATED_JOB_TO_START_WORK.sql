WITH InProgressJobs AS ( 
    SELECT DISTINCT NEWID() Id, au.ResourceId, j.JobReference, au.UserName, j.SiteId SiteId  
    FROM Job j 
    INNER JOIN ResourceAssignment ra ON j.id = ra.JobId  
    INNER JOIN ApplicationUser au ON ra.resourceId = au.ResourceId 
    WHERE au.UserProfileId = 131 --Contractor Technician
    AND j.JobStatusId IN (3,2) 
    AND  ra.ResourceAssignmentStatusId  = 9
    AND au.Active = 1  
), NotStartedJobs AS (  
    SELECT DISTINCT NEWID() Id, au.ResourceId, j.JobReference, au.UserName, j.SiteId SiteId 
    FROM Job j 
    INNER JOIN ResourceAssignment ra ON j.id = ra.JobId  
    INNER JOIN ApplicationUser au ON ra.resourceId = au.ResourceId  
    WHERE au.UserProfileId = 131 -- Contractor Technician
    AND j.JobStatusId = 2 
    AND ra.ResourceAssignmentStatusId = 7
    AND au.Active = 1
), ResourcesJobCount AS (
    SELECT SiteId, ResourceId, COUNT(Id) AS Total
    FROM (
        SELECT * FROM InProgressJobs 
        UNION 
        SELECT * FROM NotStartedJobs 
    ) AllJobs
    GROUP BY SiteId, ResourceId
)
SELECT TOP(1) NEWID() Id, nsj.JobReference, nsj.UserName, nsj.ResourceId, nsj.SiteId SiteId   
FROM NotStartedJobs nsj 
LEFT JOIN InProgressJobs ipj ON ipj.ResourceId = nsj.ResourceId AND ipj.SiteId = nsj.SiteId  
INNER JOIN ResourcesJobCount rjc ON rjc.ResourceId = nsj.ResourceId
WHERE ipj.JobReference IS NULL  
AND rjc.Total = 1
