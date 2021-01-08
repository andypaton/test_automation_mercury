WITH
  Submitted AS (SELECT FaultId, SUM(CASE WHEN ApprovalStatusName IN ( 'AwaitingApproval') THEN 1 ELSE 0 END) Submitted, NumberOfQuotesRequired   
      FROM %portaldb.vw_QuoteApprovalScenariosWithRejected qas 
      GROUP BY FaultId, NumberOfQuotesRequired
    ),
  MaxCost AS (SELECT FaultId, MAX(cost) MaxCost   
      FROM %portaldb.vw_QuoteApprovalScenariosWithRejected qas  
      GROUP BY FaultId)
SELECT TOP(1) NEWID() id, qasu.JobReference AS JobReference, au.UserName, qasu.ResourceId ResourceId, s.Id SiteID 
FROM %testdb.uvw_QuoteApprovalScenariosUsers qasu
INNER JOIN  %helpdeskdb.ApplicationUser au ON au.ResourceId = qasu.ResourceId   
INNER JOIN %helpdeskdb.Site s ON s.id = qasu.StoreId
INNER JOIN %helpdeskdb.Job j ON j.JobReference = qasu.JobReference
LEFT JOIN Submitted sub ON qasu.JobReference = sub.FaultId
LEFT JOIN MaxCost mc ON qasu.JobReference = mc.FaultId
LEFT JOIN %testdb.TestAutomation_Locks tal ON qasu.JobReference = tal.Reference AND tal.Type = 'job'
WHERE tal.Reference IS NULL 
        AND qasu.QuoteJobApprovalStatusId IN (%s) 
        AND qasu.ApprovalStatusId IN (%s) 
        AND au.active = 1  
        AND ( ( 
            CAST(:useResourceTypeName AS BIT)  = 0 
            AND au.UserProfileId = (SELECT TOP(1) up.id FROM UserProfile up
                            INNER JOIN permission p ON up.Id = p.UserProfileId
                            INNER JOIN  AspNetRoles  anr ON p.AspNetRoleId = anr.Id 
                            WHERE up.name = :profileName
                            OR  anr.Name = :profileName)
           )
          OR
          ( 
            CAST(:useResourceTypeName AS BIT) = 1 
            AND au.ResourceId IN (SELECT r.Id FROM Resource r INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id INNER JOIN ResourceType rt ON rp.ResourceTypeId = rt.id WHERE  rp.JobCallouts = 1 AND rp.Active = 1 AND rt.Name = :resourceTypeName) 
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
          ( qasu.FundingRouteId = (SELECT Id FROM %helpdeskdb.FundingRoute fr WHERE fr.Name = :fundingRoute ))  
          )  
      ) 
       AND 
      (  
         (  
         (:budget = 'greater')  
         AND   
         ((SELECT MaximumCost FROM %helpdeskdb.InitialApproverRule WHERE ApprovalTypeId = 1 AND FundingRouteId = (SELECT Id FROM %helpdeskdb.FundingRoute fr WHERE fr.Name = :fundingRoute ) ) >= 0.00)  
         AND   
         (COALESCE(qasu.Cost,0) >= (SELECT  MaximumCost FROM %helpdeskdb.InitialApproverRule WHERE ApprovalTypeId = 1 AND FundingRouteId = (SELECT Id FROM %helpdeskdb.FundingRoute fr WHERE fr.Name = :fundingRoute ) ))  
         )  
         OR  
         (  
         (:budget = 'greater')  
         AND   
         ((SELECT MaximumCost FROM %helpdeskdb.InitialApproverRule WHERE ApprovalTypeId = 1 AND FundingRouteId = (SELECT Id FROM %helpdeskdb.FundingRoute fr WHERE fr.Name = :fundingRoute ) ) = 0.00) 
         )   
         OR  
         (   
         (:budget = 'less')  
         AND   
         ((SELECT MaximumCost FROM %helpdeskdb.InitialApproverRule WHERE ApprovalTypeId = 1 AND FundingRouteId = (SELECT Id FROM %helpdeskdb.FundingRoute fr WHERE fr.Name = :fundingRoute ) ) >= 0.00)  
         AND   
         (COALESCE(qasu.Cost,0.00) <= (SELECT  MaximumCost FROM %helpdeskdb.InitialApproverRule WHERE ApprovalTypeId = 1 AND FundingRouteId = (SELECT Id FROM %helpdeskdb.FundingRoute fr WHERE fr.Name = :fundingRoute ) ))  
         )  
         OR  
         (  
         (:budget = 'less')  
         AND   
         ((SELECT MaximumCost FROM %helpdeskdb.InitialApproverRule WHERE ApprovalTypeId = 1 AND FundingRouteId = (SELECT Id FROM %helpdeskdb.FundingRoute fr WHERE fr.Name = :fundingRoute ) ) = 0.00)   
         )  
        OR 
        (:budget = 'ignore')  
      )
       AND
      (  
        (
         (:submitted = 'ignore')  
        )  
         OR  
        (  
         (
          :submitted <> 'ignore'
         )  
         AND
         (
          :submitted <> 'all'
         )  
         AND
         ( 
          sub.Submitted = (CAST(IIF ( ISNUMERIC(:submitted) = 1 , :submitted , '0' )  AS INT) )  
         )
        )
        OR
        (
         ('all' = 'all')  
         AND 
         (sub.Submitted = sub.NumberOfQuotesRequired)
        )
       )
       AND
      (  
        (
         (:aboveThreshhold = 'ignore')  
        )  
         OR  
        (  
         (
          :aboveThreshhold <> 'ignore'
         )  
        /* Not sure about this condition now. Leaving in as might need to rethink this when walmart is tested
         * AND   
         ( 
          mc.maxCost >= CAST((SELECT fld_str_Value FROM %portaldb.tblSetting WHERE fld_str_key = 'QuoteJob/QuoteCostAtWhichSeniorManagerApprovalIsRequired')  AS INT)  
         )*/
        )
       )      
ORDER BY NEWID()