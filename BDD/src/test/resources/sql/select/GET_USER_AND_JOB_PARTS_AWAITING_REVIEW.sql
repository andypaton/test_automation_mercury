SELECT TOP(1) NEWID() Id, poaa.[Job Number] JobReference, au.UserName, ra.ResourceId ResourceId, s.Id SiteId
FROM %portaldb.uvw_PartsOrdersAwaitingApproval poaa
    INNER JOIN %helpdeskdb.Job j ON j.JobReference = poaa.[Job Number]
    INNER JOIN %helpdeskdb.JobType jt ON j.JobTypeId = jt.Id
    INNER JOIN %helpdeskdb.JobStatus js ON j.JobStatusId = js.Id
    INNER JOIN %helpdeskdb.ResourceAssignment ra ON j.Id = ra.JobId
    INNER JOIN %helpdeskdb.ResourceAssignmentStatus ras ON ra.ResourceAssignmentStatusId = ras.Id
    INNER JOIN %helpdeskdb.Site s ON s.id = j.SiteId
    INNER JOIN %helpdeskdb.uvw_ManagerSites ms ON s.Id = ms.SiteId
    INNER JOIN %helpdeskdb.Resource r ON ms.ResourceId = r.Id
    INNER JOIN %helpdeskdb.ApplicationUser au ON r.id = au.ResourceId
WHERE  
    ras.Name = 'Awaiting Parts Review'
    AND r.Active = 1 
    AND jt.Name = :jobType
    AND js.IsTerminal = 0
