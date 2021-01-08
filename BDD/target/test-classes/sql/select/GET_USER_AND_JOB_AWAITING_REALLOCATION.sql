WITH 
JobsAwaitingReallocation AS (
	SELECT j.JobReference JobReference, r.Id ResourceId , rp.Name AS ResourceProfileName, ras.Name ResourceAssignmentStatus, jte.JobEventTypeId, j.Id AS JobId, j.FaultPriorityId, j.CreatedOn, j.CallerId, rav.StartAt, j.SiteId, CoveringResourceId
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
	AND jcs.Status != 'Fixed' 
	AND ras.Name NOT IN ('Complete', 'Work Transferred', 'Removed', 'Removal Requested', 'Declined', 'Job Advise Deferred', 'Awaiting Parts Review')
), JobAssignments AS (
    SELECT JobId, COUNT(Id) AS NumResourcesAssigned
    FROM ResourceAssignment
    WHERE Active = 1
    GROUP BY JobId
)
SELECT TOP(1) NEWID() Id, jar.JobReference, jar.ResourceId, jar.ResourceProfileName, jar.SiteId 
FROM JobsAwaitingReallocation jar
JOIN JobAssignments ja ON ja.JobId = jar.JobId
WHERE jar.ResourceAssignmentStatus NOT IN ('New Job Notification Sent', 'Call Required')