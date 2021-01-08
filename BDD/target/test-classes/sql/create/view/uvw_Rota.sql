
IF EXISTS (
	SELECT * FROM sys.objects 
	WHERE object_id = OBJECT_ID(N'[Staging].[uvw_Rota]') AND type = 'V'
)
EXEC ('DROP VIEW %testdb.uvw_Rota')

EXEC ( '

CREATE VIEW %testdb.uvw_Rota
AS 


SELECT DISTINCT 
	re.ResourceId
	, rp.Name AS ResourceProfileName
    , rt.Name AS ResourceTypeName
	, oss.SiteId
	, ret.Name AS RotaEntryType, re.StartAt, re.EndAt
FROM RotaEntry re 
INNER JOIN OrganisationStructureSite oss ON oss.id=re.OrganisationStructureSiteId 
LEFT JOIN RotaEntryType ret ON ret.Id=re.RotaEntryTypeId
LEFT JOIN ResourceProfile rp ON rp.Id=re.ResourceProfileId
LEFT JOIN ResourceType rt ON rp.ResourceTypeId = rt.Id 
LEFT JOIN uvw_ResourceAbsence ra ON ra.ResourceId = re.ID 
WHERE
re.Active=1
AND (coalesce(ra.AbsenceStartAt, ''2999-12-01'') > cast(SWITCHOFFSET(GETDATE(), ''+00:00'') AS DATETIMEOFFSET) OR coalesce(ra.AbsenceEndAt, ''2999-12-01'') < cast(SWITCHOFFSET(GETDATE(), ''+00:00'') AS DATETIMEOFFSET))
AND (re.StartAt < cast(SWITCHOFFSET(GETDATE(), ''+00:00'') AS DATETIMEOFFSET) AND cast(SWITCHOFFSET(GETDATE(), ''+00:00'') AS DATETIMEOFFSET) < coalesce(re.EndAt, ''2999-12-01''))


')
