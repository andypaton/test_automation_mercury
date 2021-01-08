SELECT TOP(1) ROW_NUMBER() OVER(ORDER BY LoggedAt DESC) AS ID, j.JobReference, es.* FROM EventSummary es 
JOIN Job j ON j.Id = es.JobId
JOIN AssetClassification ac ON ac.Id = j.AssetClassificationId 
JOIN AssetSubType ast ON ast.Id = ac.AssetSubTypeId 
JOIN AssetType at ON at.Id = ast.AssetTypeId 
JOIN ResourceAssignment ra ON ra.JobId = j.Id 
JOIN ResourceAssignmentEvent rae ON rae.ResourceAssignmentId = ra.Id 
JOIN ResourceAssignmentEventType raet ON raet.Id = rae.ResourceAssignmentEventTypeId 
JOIN SiteVisits sv ON sv.ResourceAssignmentId = ra.Id 
LEFT JOIN Joblock jl ON j.Id = jl.JobId 
WHERE at.UsesGas = 1 
AND GasUsage IS NOT NULL and GasUsage != '0'
AND es.Detail1 LIKE '%gas%'
AND jl.JobId IS NULL
