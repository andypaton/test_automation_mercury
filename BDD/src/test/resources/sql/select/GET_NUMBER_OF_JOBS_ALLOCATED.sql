-- Allocated
SELECT SiteName, COUNT(*) AS JobCount
FROM (
    SELECT s.Name AS SiteName, j.JobReference, ra.ResourceId 
    FROM JOB j
    INNER JOIN Site s ON s.Id = j.SiteId
    INNER JOIN ResourceAssignment ra ON ra.JobId = j.Id
    INNER JOIN ResourceAssignmentStatus ras ON ras.Id = ra.ResourceAssignmentStatusId
    INNER JOIN JobStatus js ON js.Id = j.JobStatusId
    WHERE 
    (
    ras.Name IN ('ETA Advised to Site', 'ETA Provided', 'On Site', 'Awaiting Parts', 'Returning') 
    OR 
    (ras.Name IN ('Removal Requested') AND js.Name IN ('Logged'))
    OR  
    (ras.Name IN ('Awaiting Funding Authorisation') AND js.Name IN ('In Progress', 'Allocated'))
    )
UNION
    -- dont know why, but the PPM tables occasionally return the incorrect ResourceId (ie. ResourceEpochId) but the correct ResourceName, 
    -- so having to join to Resource table to get correct ResourceId
    SELECT Site AS SiteName, JobReference, r.Id AS ResourceId
    FROM uvw_PpmJobs pj
    INNER JOIN Resource r ON r.Name = pj.ResourceName
    WHERE ScheduleLineStatus = 'Active' AND CalloutStatus IN ('Called Out', 'Started', 'Returning')
) X
WHERE ResourceId = %d 
GROUP BY SiteName