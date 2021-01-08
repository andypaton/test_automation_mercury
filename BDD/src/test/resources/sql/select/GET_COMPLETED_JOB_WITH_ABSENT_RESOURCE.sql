SELECT TOP(1) j.JobReference JobReference, r.Id ResourceId, rp.Name AS ResourceProfileName, ras.Name ResourceAssignmentStatus, jte.JobEventTypeId, j.Id AS JobId, j.ResponsePriorityId, j.CreatedOn, j.CallerId, rav.StartAt, j.SiteId, CoveringResourceId
FROM Job j 
INNER JOIN ResourceAssignment ra ON ra.JobId = j.Id
INNER JOIN Resource r ON ra.ResourceId = r.Id 
INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id
INNER JOIN ResourceAvailability rav ON rav.ResourceId = r.Id
INNER JOIN JobStatus js ON j.JobStatusId = js.Id 
INNER JOIN JobStatusJobClientStatusMapping jsjcsm ON js.Id = jsjcsm.JobStatusId
INNER JOIN JobClientStatus jcs ON jsjcsm.JobClientStatusId = jcs.Id 
LEFT JOIN ResourceAssignmentStatus ras ON ra.ResourceAssignmentStatusId = ras.Id
LEFT JOIN JobTimelineEvent jte ON j.Id = jte.JobId 
WHERE rav.Active = 1 AND (rav.StartAt <= GETUTCDATE() AND rav.EndAt >= GETUTCDATE())
AND rp.ResourceTypeId = 1 AND rp.JobCallouts = 1 AND r.Active = 1
AND ras.Name = 'Complete'
ORDER BY NEWID()
