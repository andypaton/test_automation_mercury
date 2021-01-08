SELECT TOP(1) NEWID() id, qas.FaultId AS JobReference, au.UserName, r.Id ResourceId, s.Id SiteId 
FROM %portaldb.vw_QuoteApprovalScenarios qas  
INNER JOIN %helpdeskdb.Resource r ON qas.ManagerApproverID = r.EpochId   
INNER JOIN %helpdeskdb.ApplicationUser au ON au.ResourceId = r.id   
INNER JOIN %helpdeskdb.Site s ON s.id = qas.StoreId
LEFT JOIN %testdb.TestAutomation_Locks tal ON qas.FaultId = tal.Reference AND tal.Type = 'job'
WHERE tal.Reference IS NULL
      AND qas.QuoteJobApprovalStatusId IN (SELECT fld_int_ID  
            FROM %portaldb.tblQuoteJobApprovalStatus  
            WHERE fld_str_Name = :quoteJobApprovalStatus  
        ) 
       AND qas.ApprovalStatusId  IN (SELECT fld_int_ID  
            FROM %portaldb.tblApprovalStatus  
            WHERE fld_str_Name = :approvalstatus  
        )  
        AND au.active = 1  
        AND au.UserProfileId = (SELECT TOP(1) up.id FROM UserProfile up
                            INNER JOIN permission p ON up.Id = p.UserProfileId
                            INNER JOIN  AspNetRoles  anr ON p.AspNetRoleId = anr.Id 
                            WHERE up.name = :profileName
                            OR anr.Name = :profileName )  
       AND  
      (  
        (  
         (:multiQuote = 'ignore')  
         )  
         OR  
         (  
         (:multiQuote = 'single')  
         AND   
         ( qas.IsMultiQuote = 0 )  
         )
          OR  
         (  
         (:multiQuote = 'multi')  
         AND   
         ( qas.IsMultiQuote = 1 )  
         )  
      ) 
       AND  
      (  
        (  
          (:fundingRoute = 'ignore')  
          )  
          OR  
          (  
          (:fundingRoute != 'ignore')  
          AND   
          ( qas.FundingRouteId = (SELECT Id from %helpdeskdb.FundingRoute fr Where fr.Name = :fundingRoute ))  
          )  
      ) 
       AND 
      (  
         (  
         (:budget = 'greater')  
         AND   
         ((SELECT MaximumCost FROM %helpdeskdb.InitialApproverRule WHERE ApprovalTypeId = 1 AND FundingRouteId = (SELECT Id from %helpdeskdb.FundingRoute fr Where fr.Name = :fundingRoute ) ) >= 0.00)  
         AND   
         (COALESCE(qas.Cost,0) >= (SELECT  MaximumCost FROM %helpdeskdb.InitialApproverRule WHERE ApprovalTypeId = 1 AND FundingRouteId = (SELECT Id from %helpdeskdb.FundingRoute fr Where fr.Name = :fundingRoute ) ))  
         )  
         OR  
         (  
         (:budget = 'greater')  
         AND   
         ((SELECT MaximumCost FROM %helpdeskdb.InitialApproverRule WHERE ApprovalTypeId = 1 AND FundingRouteId = (SELECT Id from %helpdeskdb.FundingRoute fr Where fr.Name = :fundingRoute ) ) = 0.00) 
         )   
         OR  
         (   
         (:budget = 'less')  
         AND   
         ((SELECT MaximumCost FROM %helpdeskdb.InitialApproverRule WHERE ApprovalTypeId = 1 AND FundingRouteId = (SELECT Id from %helpdeskdb.FundingRoute fr Where fr.Name = :fundingRoute ) ) >= 0.00)  
         AND   
         (COALESCE(qas.Cost,0.00) <= (SELECT  MaximumCost FROM %helpdeskdb.InitialApproverRule WHERE ApprovalTypeId = 1 AND FundingRouteId = (SELECT Id from %helpdeskdb.FundingRoute fr Where fr.Name = :fundingRoute ) ))  
         )  
         OR  
         (  
         (:budget = 'less')  
         AND   
         ((SELECT MaximumCost FROM %helpdeskdb.InitialApproverRule WHERE ApprovalTypeId = 1 AND FundingRouteId = (SELECT Id from %helpdeskdb.FundingRoute fr Where fr.Name = :fundingRoute ) ) = 0.00)   
         )  
        OR 
        (:budget = 'ignore')  
       ) 
ORDER BY NEWID()