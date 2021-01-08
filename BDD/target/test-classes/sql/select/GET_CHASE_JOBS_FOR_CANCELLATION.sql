WITH Candidates AS (
    SELECT jc.Id AS ChaseId, jc.JobId AS JobId, j.JobReference, ct.Name AS ChaseType, jc.ProblemDescription, jc.ProblemFixDescription, ROW_NUMBER() OVER (ORDER BY jc.Id DESC) AS RowNum
    FROM JobChase jc
    INNER JOIN ChaseType ct ON jc.ChaseTypeId = ct.Id
    INNER JOIN Job j ON jc.JobId = j.Id
    WHERE Active = 1
    AND JobChaseStatusId = 0
    AND ct.Name = '%s'
)
SELECT * FROM Candidates WHERE RowNum > %d
