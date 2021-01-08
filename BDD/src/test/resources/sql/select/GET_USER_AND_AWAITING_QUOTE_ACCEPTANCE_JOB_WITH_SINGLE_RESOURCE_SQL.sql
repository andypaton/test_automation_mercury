SELECT TOP(1) NEWID() id, qas.FaultId AS JobReference, au.UserName, r.Id ResourceId, s.Id SiteId
    FROM %portaldb.vw_QuoteApprovalScenarios qas 
        INNER JOIN %helpdeskdb.Resource r ON qas.ResourceId = r.EpochId  
        INNER JOIN %helpdeskdb.ApplicationUser au ON au.ResourceId = r.id  
        INNER JOIN %helpdeskdb.Site s ON s.id = qas.StoreId
    WHERE qas.ApprovalStatusId IN (SELECT fld_int_ID 
			FROM %portaldb.tblApprovalStatus 
		    WHERE fld_str_Description = :quoteStatusDescription 
        )  
        AND qas.IsMultiQuote = 0 
        AND qas.ResourceHasAccepted = 0 
        AND qas.IsRetrospectiveQuote = 0 
	    AND au.active = 1 
		AND ( ( 
            CAST(:useResourceTypeName AS bit)  = 0 
            AND au.UserProfileId = (SELECT id FROM dbo.UserProfile WHERE name = :profileName)
           )
          OR
          ( 
            CAST(:useResourceTypeName AS bit) = 1 
            AND au.ResourceId IN (SELECT r.Id FROM Resource r INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id INNER JOIN ResourceType rt ON rp.ResourceTypeId = rt.id where  rp.JobCallouts = 1 AND rp.Active = 1 AND rt.Name = :resourceTypeName) 
          )
        )   
ORDER BY NEWID()