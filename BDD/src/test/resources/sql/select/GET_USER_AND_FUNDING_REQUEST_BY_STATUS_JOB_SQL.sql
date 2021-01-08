SELECT TOP(1) NEWID() id, qas.FaultId AS JobReference, au.UserName, r.Id ResourceId, s.Id SiteId 
FROM %portaldb.vw_QuoteApprovalScenarios qas 
INNER JOIN %helpdeskdb.Resource r ON qas.SeniorManagerApproverID = r.EpochId  
INNER JOIN %helpdeskdb.ApplicationUser au ON au.ResourceId = r.id  
INNER JOIN %helpdeskdb.Site s ON s.id = qas.StoreId
WHERE qas.QuoteJobApprovalStatusId IN (SELECT fld_int_ID 
            FROM %portaldb.tblQuoteJobApprovalStatus 
            WHERE fld_str_Name = :quoteApprovalStatusName 
        )
        AND qas.ApprovalStatusName = 'AwaitingApproval'
        AND au.active = 1
        AND au.UserProfileId IN (SELECT up.id FROM UserProfile up
							INNER JOIN %helpdeskdb.permission p ON up.Id = p.UserProfileId
							INNER JOIN %helpdeskdb.AspNetRoles  anr ON p.AspNetRoleId = anr.Id 
							WHERE up.name = :profileName
							OR  anr.Name = :profileName )
ORDER BY NEWID()