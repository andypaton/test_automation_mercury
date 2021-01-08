SELECT JobReference, ResourceAssignmentStatus, JobStatus 
FROM (
    SELECT j.JobReference, ras.Name AS ResourceAssignmentStatus, js.Name AS JobStatus, ROW_NUMBER() OVER(PARTITION BY ras.Name, js.Name ORDER BY j.CreatedOn DESC) AS RowNum
    FROM JOB j
    INNER JOIN ResourceAssignment ra ON ra.JobId = j.Id
    INNER JOIN ResourceAssignmentStatus ras ON ras.Id = ra.ResourceAssignmentStatusId
    INNER JOIN JobStatus js ON js.Id = j.JobStatusId
    WHERE (ras.Name IN ('Declined', 'Removed', 'Job Advise Deferred') OR js.Name IN ('Fixed', 'Cancelled'))
    AND ra.ResourceId = %s 
) X
--WHERE RowNum = 1