-- Awaiting Acceptance
SELECT s.Name AS SiteName, COUNT(*) AS JobCount
FROM JOB j
INNER JOIN Site s ON s.Id = j.SiteId
INNER JOIN ResourceAssignment ra ON ra.JobId = j.Id
INNER JOIN ResourceAssignmentStatus ras ON ras.Id = ra.ResourceAssignmentStatusId
INNER JOIN JobStatus js ON js.Id = j.JobStatusId
WHERE ras.Name IN ('Call Required', 'Job Advised Deferred', 'New Job Notification Sent')
AND ra.ResourceId = %d 
GROUP BY s.SiteName