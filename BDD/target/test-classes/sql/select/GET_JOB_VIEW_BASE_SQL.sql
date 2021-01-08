SELECT DISTINCT j.Id, j.JobReference, j.Description, j.JobTypeId, jt.Name JobTypeName, j.SiteId,    
			 j.LocationId, l.Name LocationName, sl.Name SubLocationName, j.AssetClassificationId, s.Name,    
			 s.SiteCode, astype.Name AssetSubTypeName, atype.Name AssetName, ac.name AssetClassificationName, j.ResponsePriorityId, fpms.Priority, fpms.Detail FaultPriority,    
			 j.FaultTypeId, ft.Name FaultType, ra.ResourceAssignmentStatusId, ras.Name ResourceAssignmentStatusName, ra.contractorReference,  j.JobStatusId, 
			 j.CreatedOn,
			 ETA.ETAFrom,    
			 ETA.ETATo,    
			 ETA.Id EtaWindowId    
FROM %helpdeskdb.Job j LEFT OUTER JOIN %helpdeskdb.JobType jt ON j.JobTypeId = jt.Id    
			 LEFT OUTER JOIN %helpdeskdb.location sl ON j.LocationId = sl.Id    
			 LEFT OUTER JOIN %helpdeskdb.Location l ON l.Id = sl.WithinLocationId    
			 LEFT OUTER JOIN %helpdeskdb.AssetClassification ac ON j.AssetClassificationId  = ac.Id    
			 LEFT OUTER JOIN %helpdeskdb.AssetSubType astype ON ac.AssetSubTypeId = astype.Id    
			 LEFT OUTER JOIN %helpdeskdb.AssetType atype ON atype.id = astype.AssetTypeId    
			 LEFT OUTER JOIN %helpdeskdb.FaultPriorityMapping fpm ON j.ResponsePriorityId = fpm.ResponsePriorityId 
			 AND j.RepairPriorityId = fpm.RepairPriorityId 
			 AND j.JobRoutingProcessId = fpm.JobRoutingProcessId 
			 LEFT OUTER JOIN %helpdeskdb.uvw_FaultPriorityMappings fpms ON fpms.Id = fpm.Id  
			 LEFT OUTER JOIN %helpdeskdb.FaultType ft ON j.FaultTypeId = ft.Id    
			 LEFT OUTER JOIN %helpdeskdb.ResourceAssignment ra ON j.Id = ra.JobId    
			 LEFT OUTER JOIN %helpdeskdb.ResourceAssignmentStatus ras ON ra.ResourceAssignmentStatusId = ras.Id    
			 LEFT OUTER JOIN %helpdeskdb.Site s ON j.SiteId = s.Id    
			 LEFT OUTER JOIN %helpdeskdb.ResourceAssignmentEvent rae ON ra.id = rae.ResourceAssignmentId    
			 LEFT OUTER JOIN %helpdeskdb.SiteVisits sv ON ra.id = sv.ResourceAssignmentId    
			 LEFT OUTER JOIN %helpdeskdb.JobStatus js ON j.JobStatusId = js.Id    
			 OUTER APPLY (SELECT TOP(1) sv.Id, sv.ETAFrom, ETATo FROM %helpdeskdb.SiteVisits sv WHERE ra.id = sv.ResourceAssignmentId  ORDER BY sv.Id DESC ) AS ETA 
			 LEFT JOIN %helpdeskdb.FundingRequest fr ON fr.ResourceAssignmentId = ra.Id
			 