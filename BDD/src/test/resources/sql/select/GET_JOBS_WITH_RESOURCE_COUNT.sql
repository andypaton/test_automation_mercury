WITH ContractorAdmin AS (
    SELECT au.Id AS ApplicationUserId, au.UserName, r.id AS ResourceId, r.Name AS ResourceName, up.Id, up.Name AS UserProfileName
    FROM ApplicationUser au
    INNER JOIN UserProfile up ON au.UserProfileId = up.Id 
    INNER JOIN Resource r ON au.ResourceId = r.Id 
    WHERE up.Name = 'Contractor Admin'
    AND r.Active = 1 AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) AND r.UsesEngineers = 0
), ContractorTechnician AS (
    SELECT au.Id AS ApplicationUserId, au.UserName, r.id AS ResourceId, r.Name AS ResourceName, up.Id, up.Name AS UserProfileName
    FROM ApplicationUser au
    INNER JOIN UserProfile up ON au.UserProfileId = up.Id 
    INNER JOIN Resource r ON au.ResourceId = r.Id 
    INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id 
    INNER JOIN ResourceType rt ON rp.ResourceTypeId = rt.id 
    WHERE up.Name = 'Contractor Technician'
    AND rp.JobCallouts = 1 AND r.Active = 1 AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) AND rp.Active = 1 AND rt.Name = 'Contractor' 
), CityTech AS (
    SELECT au.Id AS ApplicationUserId, au.UserName, r.id AS ResourceId, r.Name AS ResourceName, up.Id, up.Name AS UserProfileName
    FROM ApplicationUser au
    INNER JOIN UserProfile up ON au.UserProfileId = up.Id 
    INNER JOIN Resource r ON au.ResourceId = r.Id 
    INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id 
    INNER JOIN ResourceType rt ON rp.ResourceTypeId = rt.id 
    WHERE rt.Name = 'City Resource'
    AND rp.JobCallouts = 1 AND r.Active = 1 AND au.Active = 1 AND rp.Active = 1
), NumResources AS (
    SELECT JobId, JobReference, COUNT(ResourceId) AS Total 
    FROM ResourceAssignment ra 
    INNER JOIN Job j ON j.Id = ra.JobId 
    WHERE Active = 1 
    AND ResourceAssignmentStatusId NOT IN (13, 15, 16)
    GROUP BY JobId, JobReference
)
SELECT TOP(1) ra.ResourceId, j.JobReference, r.UserName, r.UserProfileName, j.SiteId, nr.Total AS NumResourcesAssigned
FROM Job j 
INNER JOIN ResourceAssignment ra ON j.Id = ra.JobId
INNER JOIN ResourceAssignmentStatus ras ON ras.Id = ra.ResourceAssignmentStatusId
--INNER JOIN ContractorTechnician r ON ra.ResourceId = r.ResourceId
--INNER JOIN ContractorAdmin r ON ra.ResourceId = r.ResourceId
--INNER JOIN CityTech r ON ra.ResourceId = r.ResourceId
INNER JOIN JobStatus js ON j.JobStatusId = js.id
INNER JOIN JobType jt ON j.JobTypeId = jt.id
INNER JOIN NumResources nr ON nr.JobId = j.Id
WHERE js.Name = :jobStatus 
AND ras.Name IN (:resourceAssignmentStatus)  
AND jt.Name = :jobType
AND nr.Total = 1
