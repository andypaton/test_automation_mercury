IF EXISTS (
	SELECT * FROM sys.objects 
	WHERE object_id = OBJECT_ID(N'[Staging].[uvw_VendorStores]') AND type = 'V'
)
EXEC ('DROP VIEW [Staging].[uvw_VendorStores]')


EXEC ('


CREATE VIEW [Staging].[uvw_VendorStores]
AS 

SELECT DISTINCT
	s.Id, s.Name, s.SiteTypeId
	, bwh.DayOfTheWeek AS BrandDayOfTheWeek, bwh.StartAt AS BrandStartAt, bwh.EndAt AS BrandEndAt
	, dwh.DayOfTheWeek AS DeferralDayOfTheWeek, dwh.StartAt AS DeferralStartAt, dwh.EndAt AS DeferralEndAt
FROM %testdb.uvw_AvailaibleSites s
LEFT JOIN dbo.AllInclusiveContractorSiteMapping ascm ON ascm.SiteId = s.Id
LEFT JOIN dbo.BrandWorkingHoursGroup bwhg ON s.BrandId = bwhg.BrandId
 JOIN dbo.BrandWorkingHours bwh ON bwhg.BrandWorkingHoursId = bwh.Id AND bwh.DayOfTheWeek = Datepart(dw, Getdate())
LEFT JOIN dbo.DeferralWorkingHoursGroup dwhg ON s.BrandId = dwhg.BrandId
 JOIN dbo.DeferralWorkingHours dwh ON dwhg.DeferralWorkingHoursId = dwh.Id AND dwh.DayOfTheWeek = Datepart(dw, Getdate())
WHERE ascm.Id IS NULL
AND s.id NOT IN (SELECT sr.SiteId FROM dbo.SiteResource sr, dbo.Resource r WHERE sr.ResourceId=r.Id and r.ResourceProfileId IN (3, 31, 134, 1155))
AND s.active=1 
AND s.SiteStatusId IN (1,2)


')
