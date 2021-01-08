/*** mapping from Site to ALL resources ***/

IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[Staging].[uvw_SiteResources]') AND type = 'V'
)
EXEC ('DROP VIEW %testdb.uvw_SiteResources')

EXEC ( '

CREATE VIEW %testdb.uvw_SiteResources
AS 


SELECT DISTINCT(SiteId), ResourceId 
FROM (
SELECT oss.SiteId, ResourceId 
FROM RotaEntry re 
INNER JOIN OrganisationStructureSite oss ON oss.id=re.OrganisationStructureSiteId 
WHERE (re.StartAt < cast(SWITCHOFFSET(GETDATE(), '+00:00') AS DATE) AND cast(SWITCHOFFSET(GETDATE(), '+00:00') AS DATE) < coalesce(re.EndAt, '2999-12-01'))
AND re.Active=1
UNION
SELECT SiteId, ResourceId 
FROM AssetClassificationSiteContractorMapping acscm
INNER JOIN AssetClassificationSite acs ON acscm.AssetClassificationSiteId = acs.Id
) u



')
