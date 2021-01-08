IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[Staging].[uvw_Stores]') AND type = 'V'
)
EXEC ('DROP VIEW [Staging].[uvw_Stores]')


EXEC ('

CREATE VIEW [Staging].[uvw_Stores]
AS 

SELECT DISTINCT 
    s.Id, s.Name, s.SiteTypeId
    , bwh.DayOfTheWeek AS BrandDayOfTheWeek, bwh.StartAt AS BrandStartAt, bwh.EndAt AS BrandEndAt
    , dwh.DayOfTheWeek AS DeferralDayOfTheWeek, dwh.StartAt AS DeferralStartAt, dwh.EndAt AS DeferralEndAt
    , CASE WHEN c.SiteId IS NULL THEN 0 ELSE c.NumContractors END AS NumContractors
    , CASE WHEN ct.ResourceId IS NULL THEN 0 ELSE 1 END AS CityTechConfigured
FROM %testdb.uvw_AvailaibleSites s
LEFT JOIN AllInclusiveContractorSiteMapping ascm ON ascm.SiteId = s.Id
LEFT JOIN dbo.BrandWorkingHoursGroup bwhg ON s.BrandId = bwhg.BrandId
INNER JOIN dbo.BrandWorkingHours bwh ON bwhg.BrandWorkingHoursId = bwh.Id AND bwh.DayOfTheWeek = Datepart(dw, Getdate())
LEFT JOIN dbo.DeferralWorkingHoursGroup dwhg ON s.BrandId = dwhg.BrandId
LEFT JOIN dbo.DeferralWorkingHours dwh ON dwhg.DeferralWorkingHoursId = dwh.Id AND dwh.DayOfTheWeek = Datepart(dw, Getdate())
LEFT JOIN (SELECT acs.SiteId, COUNT(DISTINCT(ResourceId)) AS NumContractors FROM AssetClassificationSiteContractorMapping acscm JOIN AssetClassificationSite acs ON acscm.AssetClassificationSiteId = acs.Id GROUP BY acs.SiteId) c ON c.SiteId = s.Id 
LEFT JOIN (SELECT SiteId, ResourceId FROM %testdb.uvw_Rota rota WHERE RotaEntryType = 'Permanent' AND ResourceTypeName = 'City Resource') ct ON ct.SiteId = s.Id
WHERE ascm.Id IS NULL
AND s.active=1 
AND s.SiteStatusId IN (1,2)


')