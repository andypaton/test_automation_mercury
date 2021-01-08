IF EXISTS (
	SELECT * FROM sys.objects 
	WHERE object_id = OBJECT_ID(N'[Staging].[uvw_CityTechStores]') AND type = 'V'
)
EXEC ('DROP VIEW [Staging].[uvw_CityTechStores]')


EXEC ('

CREATE VIEW [Staging].[uvw_CityTechStores]
AS 

SELECT DISTINCT 
	s.Id, s.Name, s.SiteTypeId
	, bwh.DayOfTheWeek AS BrandDayOfTheWeek, bwh.StartAt AS BrandStartAt, bwh.EndAt AS BrandEndAt
	, dwh.DayOfTheWeek AS DeferralDayOfTheWeek, dwh.StartAt AS DeferralStartAt, dwh.EndAt AS DeferralEndAt
FROM %testdb.uvw_AvailaibleSites s
LEFT JOIN AllInclusiveContractorSiteMapping ascm ON ascm.SiteId = s.Id
INNER JOIN SiteResource sr ON sr.SiteId = s.Id
INNER JOIN Resource r ON r.Id = sr.ResourceId
LEFT JOIN dbo.BrandWorkingHoursGroup bwhg ON s.BrandId = bwhg.BrandId
 JOIN dbo.BrandWorkingHours bwh ON bwhg.BrandWorkingHoursId = bwh.Id AND bwh.DayOfTheWeek = Datepart(dw, Getdate())
LEFT JOIN dbo.DeferralWorkingHoursGroup dwhg ON s.BrandId = dwhg.BrandId
 JOIN dbo.DeferralWorkingHours dwh ON dwhg.DeferralWorkingHoursId = dwh.Id AND dwh.DayOfTheWeek = Datepart(dw, Getdate())
WHERE ascm.Id IS NULL
AND r.ResourceProfileId IN (3, 31, 134, 1155) 
AND s.active=1 
AND s.SiteStatusId IN (1,2)




')
