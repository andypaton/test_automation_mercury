IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[Staging].[uvw_QuoteApprovalScenariosUsers]') AND type = 'V'
)
EXEC ('DROP VIEW [Staging].[uvw_QuoteApprovalScenariosUsers]')


EXEC ('

CREATE VIEW [Staging].[uvw_QuoteApprovalScenariosUsers]
AS 

SELECT DISTINCT * FROM
((
    SELECT qas.FaultId AS JobReference, au.UserName, r.Id ResourceId, qas.QuoteJobApprovalStatusId, qas.ApprovalStatusId,  qas.StoreId, qas.FundingRouteId, qas.Cost, qas.IsMultiQuote
    FROM %portaldb.vw_QuoteApprovalScenarios qas  
    INNER JOIN %helpdeskdb.Resource r ON qas.ResourceId = r.EpochId   
    INNER JOIN %helpdeskdb.ApplicationUser au ON au.ResourceId = r.id   
) 
UNION
(
    SELECT qas.FaultId AS JobReference, au.UserName, r.Id ResourceId, qas.QuoteJobApprovalStatusId, qas.ApprovalStatusId,  qas.StoreId, qas.FundingRouteId, qas.Cost, qas.IsMultiQuote
    FROM %portaldb.vw_QuoteApprovalScenarios qas  
    INNER JOIN %helpdeskdb.Resource r ON qas.ManagerApproverID = r.EpochId   
    INNER JOIN %helpdeskdb.ApplicationUser au ON au.ResourceId = r.id   
)
UNION 
(
    SELECT qas.FaultId AS JobReference, au.UserName, r.Id ResourceId, qas.QuoteJobApprovalStatusId, qas.ApprovalStatusId,  qas.StoreId, qas.FundingRouteId, qas.Cost, qas.IsMultiQuote
    FROM %portaldb.vw_QuoteApprovalScenarios qas  
    INNER JOIN %helpdeskdb.Resource r ON qas.SeniorManagerApproverID = r.EpochId   
    INNER JOIN %helpdeskdb.ApplicationUser au ON au.ResourceId = r.id   
)) AS QAS1

')
