IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[Staging].[uvw_JobsWithSingleActiveResource]') AND type = 'V'
)
EXEC ('DROP VIEW [Staging].[uvw_JobsWithSingleActiveResource]')


EXEC ('

CREATE VIEW [Staging].[uvw_JobsWithSingleActiveResource]
AS 

(SELECT DISTINCT ra.jobId, ra.ResourceAssignmentStatusId, ra.resourceId
FROM ResourceAssignment ra LEFT JOIN (SELECT * FROM ResourceAssignment WHERE ResourceAssignmentStatusId IN ( 13, 15, 16) ) rac
ON ra.JobId = rac.JobId
left join AdditionalResourceRequirement arr ON ra.JobId = arr.JobId
WHERE arr.id IS NULL )
UNION  (
SELECT DISTINCT ra.jobId, ra.ResourceAssignmentStatusId, ra.resourceId
FROM ResourceAssignment ra INNER JOIN (SELECT jobid FROM ResourceAssignment ra1 GROUP BY JobId HAVING COUNT(ra1.resourceid) = 1 ) rac
ON ra.JobId = rac.JobId
LEFT JOIN AdditionalResourceRequirement arr ON ra.JobId = arr.JobId
WHERE arr.id IS NULL)

')
