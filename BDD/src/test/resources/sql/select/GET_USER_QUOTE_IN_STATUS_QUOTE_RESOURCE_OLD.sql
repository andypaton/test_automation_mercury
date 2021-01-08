SELECT TOP(1) NEWID() id, qasu.JobReference AS JobReference, au.UserName, qasu.ResourceId ResourceId , s.Id SiteId
FROM %testdb.uvw_QuoteApprovalScenariosUsers qasu
INNER JOIN %helpdeskdb.ApplicationUser au ON au.ResourceId = qasu.ResourceId   
INNER JOIN %helpdeskdb.Site s ON s.id = qasu.StoreId
WHERE qasu.QuoteJobApprovalStatusId IN (SELECT fld_int_ID  
            FROM %portaldb.tblQuoteJobApprovalStatus  
            WHERE fld_str_Name = :quoteJobApprovalStatus  
        ) 
       AND (( qasu.ApprovalStatusId  IN (SELECT fld_int_ID  
            FROM %portaldb.tblApprovalStatus  
            WHERE fld_str_Name = :approvalstatus  
        ) ) /*or (:approvalstatus   =:approvalstatus  )*/   ) 
        AND au.active = 1  
        AND ( ( 
            CAST(:useResourceTypeName AS bit)  = 0 
            AND au.UserProfileId = (SELECT TOP(1) up.id FROM UserProfile up
                            INNER JOIN permission p ON up.Id = p.UserProfileId
                            INNER JOIN  AspNetRoles  anr ON p.AspNetRoleId = anr.Id 
                            WHERE up.name = :profileName
                            OR  anr.Name = :profileName)
           )
          OR
          ( 
            CAST(:useResourceTypeName AS bit) = 1 
            AND au.ResourceId IN (SELECT r.Id FROM Resource r INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id INNER JOIN ResourceType rt ON rp.ResourceTypeId = rt.id where  rp.JobCallouts = 1 AND rp.Active = 1 AND rt.Name = :resourceTypeName) 
          )
        )                            
       AND  
      (  
        (  
         (:multiQuote = 'ignore')  
         )  
         OR  
         (  
         (:multiQuote = 'single')  
         AND   
         ( qasu.IsMultiQuote = 0 )  
         )
          OR  
         (  
         (:multiQuote = 'multi')  
         AND   
         ( qasu.IsMultiQuote = 1 )  
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
          ( qasu.FundingRouteId = (SELECT Id from %helpdeskdb.FundingRoute fr Where fr.Name = :fundingRoute ))  
          )  
      ) 
       AND 
      (  
         (  
         (:budget = 'greater')  
         AND   
         ((SELECT MaximumCost FROM %helpdeskdb.InitialApproverRule WHERE FundingRouteId = (SELECT Id from %helpdeskdb.FundingRoute fr Where fr.Name = :fundingRoute ) ) >= 0.00)  
         AND   
         (COALESCE(qasu.Cost,0) >= (SELECT  MaximumCost FROM %helpdeskdb.InitialApproverRule WHERE FundingRouteId = (SELECT Id from %helpdeskdb.FundingRoute fr Where fr.Name = :fundingRoute ) ))  
         )  
         OR  
         (  
         (:budget = 'greater')  
         AND   
         ((SELECT MaximumCost FROM %helpdeskdb.InitialApproverRule WHERE FundingRouteId = (SELECT Id from %helpdeskdb.FundingRoute fr Where fr.Name = :fundingRoute ) ) = 0.00) 
         )   
         OR  
         (   
         (:budget = 'less')  
         AND   
         ((SELECT MaximumCost FROM %helpdeskdb.InitialApproverRule WHERE FundingRouteId = (SELECT Id from %helpdeskdb.FundingRoute fr Where fr.Name = :fundingRoute ) ) >= 0.00)  
         AND   
         (COALESCE(qasu.Cost,0.00) <= (SELECT  MaximumCost FROM %helpdeskdb.InitialApproverRule WHERE FundingRouteId = (SELECT Id from %helpdeskdb.FundingRoute fr Where fr.Name = :fundingRoute ) ))  
         )  
         OR  
         (  
         (:budget = 'less')  
         AND   
         ((SELECT MaximumCost FROM %helpdeskdb.InitialApproverRule WHERE FundingRouteId = (SELECT Id from %helpdeskdb.FundingRoute fr Where fr.Name = :fundingRoute ) ) = 0.00)   
         )  
        OR 
        (:budget = 'ignore')  
      ) 
ORDER BY NEWID()