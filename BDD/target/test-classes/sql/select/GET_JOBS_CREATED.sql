SELECT CONCAT(j.JobReference, ',', s.Name, ',', st.Name
, ',', at.Name
, ',', ast.Name
, ',', ac.Name
, ',', ft.Name
, ',', j.FaultPriorityId
, ',', j.CreatedOn) AS 'JobReference,SiteName,SiteType,AssetType,AssetSubType,Classification,FaultType,Priority,CreatedOn'
FROM Job j
JOIN Site s ON s.Id = j.SiteId
JOIN SiteTypes st ON st.id = s.SiteTypeId 
JOIN AssetClassification ac ON ac.Id = j.AssetClassificationId
JOIN AssetsubType ast ON ast.Id = ac.AssetSubTypeId
JOIN AssetType at ON at.Id = ast.AssetTypeId
JOIN FaultType ft ON ft.Id = j.FaultTypeId
WHERE j.Description = '%s' 
AND j.createdOn >= '%s' AND j.createdOn <= '%s' 
ORDER BY j.CreatedOn DESC