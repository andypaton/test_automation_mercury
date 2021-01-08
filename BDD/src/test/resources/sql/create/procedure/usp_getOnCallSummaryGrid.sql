USE [UAT_UKRB_TestAutomation]
GO
/****** Object:  StoredProcedure [UAT_UKRB].[usp_getOnCallSummaryGrid]    Script Date: 17/04/2019 15:19:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Description:	Stored Proce to Retrieve On Call Smmary Grid Data from DB
-- =============================================
CREATE PROCEDURE [UAT_UKRB].[usp_getOnCallSummaryGridNew] 
	@resourceID int,
	@dateFormat NVARCHAR(10),
	@startOfWeekOffset NVARCHAR(2)
AS
BEGIN
DECLARE @cols AS NVARCHAR(MAX);
DECLARE @query AS nvarchar(max);
DECLARE @case AS NVARCHAR(MAX);


WITH organisationStructuresParents AS
(
SELECT mtos.OrganisationStructureId Id, os.Name, os.ParentId
FROM %helpdeskdb.uvw_ManagerToOrganisationStructures mtos
JOIN %helpdeskdb.organisationstructure os ON mtos.OrganisationStructureId = os.id
WHERE ManagerResourceId = @resourceID
UNION ALL
SELECT os2.Id,
os2.Name,
os2.Parentid
FROM %helpdeskdb.OrganisationStructure os2
INNER JOIN OrganisationStructuresParents r ON r.parentid = os2.id
),

resourceProfileIds AS
(
SELECT DISTINCT rp.Alias orgName, rp.Id AS orgType FROM %helpdeskdb.RotaResourceProfileRoleMapping rrprm
JOIN %helpdeskdb.OrganisationStructureResourceProfile osrp ON osrp.ResourceProfileId = rrprm.RotaEntryResourceProfileId
JOIN %helpdeskdb.ResourceProfile rp ON rp.Id = rrprm.ResourceProfileId
JOIN OrganisationStructuresParents osp ON osrp.OrganisationStructureId = osp.id
WHERE rp.Active = 1
AND osp.ParentId IS NULL
),

resourceProfileIdsForTable as
(
SELECT DISTINCT rp.Alias + ' Schedules Complete' orgName, rp.Id FROM %helpdeskdb.RotaResourceProfileRoleMapping rrprm
JOIN %helpdeskdb.OrganisationStructureResourceProfile osrp ON osrp.ResourceProfileId = rrprm.RotaEntryResourceProfileId
JOIN %helpdeskdb.ResourceProfile rp ON rp.Id = rrprm.ResourceProfileId
JOIN OrganisationStructuresParents osp ON osrp.OrganisationStructureId = osp.id
WHERE rp.Active = 1
AND osp.ParentId IS NULL
)

SELECT @cols  = STUFF((SELECT ',' + QUOTENAME(OrgType) 
                    FROM ResourceProfileIDS  ORDER BY OrgType
                   
            FOR XML PATH(''), TYPE
            ).value('.', 'NVARCHAR(MAX)') 
        ,1,1,''),
		@case  = STUFF((SELECT ',CASE WHEN ' + QUOTENAME(Id) + ' = totalSites  then ''Yes'' ELSE ''No'' END AS '  + QUOTENAME(orgName) + ' '
                    FROM resourceProfileIdsForTable  ORDER BY Id
                   
            FOR XML PATH(''), TYPE
            ).value('.', 'NVARCHAR(MAX)') 
        ,1,1,'');

SET @query = '
WITH workDays AS
(SELECT DATEADD(DAY, nbr - 1, (SELECT DATEADD(DAY, 1-DATEPART(WEEKDAY,  DATEADD(hh, 12, DATEADD(dd, DATEDIFF(dd, 0, GETUTCDATE()), 0))),  
  DATEADD(hh, 12, DATEADD(dd, DATEDIFF(dd, 0, GETUTCDATE()), 0)))) ) workDate
FROM
(SELECT ROW_NUMBER() OVER ( ORDER BY c.object_id ) AS Nbr FROM UAT_UKRB_MercuryHelpdesk.sys.columns c ) nbrs
WHERE nbr - 1 <= DATEDIFF(DAY, (SELECT DATEADD(DAY, 1-DATEPART(WEEKDAY, GETUTCDATE()), GETUTCDATE())),
CONVERT(varchar, DATEADD(DAY, 371 - (SELECT  DATEDIFF(DAY, 
(SELECT DATEADD(DAY, 1-DATEPART(WEEKDAY, GETUTCDATE()), GETUTCDATE()))
,getdate())), (SELECT DATEADD(DAY, 1-DATEPART(WEEKDAY, GETUTCDATE()), GETUTCDATE()))), 101) )),

organisationStructuresParents AS
(
SELECT mtos.OrganisationStructureId Id, os.Name, os.ParentId
FROM %helpdeskdb.uvw_ManagerToOrganisationStructures mtos
JOIN %helpdeskdb.organisationstructure os ON mtos.OrganisationStructureId = os.id
WHERE ManagerResourceId = ' + convert(nvarchar, @resourceID) +'
UNION ALL
SELECT os2.Id,
os2.Name,
os2.Parentid
FROM %helpdeskdb.OrganisationStructure os2
INNER JOIN OrganisationStructuresParents r ON r.parentid = os2.id
),

resourceProfileIds AS
(
SELECT DISTINCT rp.Alias, rp.Id AS OrgType FROM %helpdeskdb.RotaResourceProfileRoleMapping rrprm
JOIN %helpdeskdb.OrganisationStructureResourceProfile osrp ON osrp.ResourceProfileId = rrprm.RotaEntryResourceProfileId
JOIN %helpdeskdb.ResourceProfile rp ON rp.Id = rrprm.ResourceProfileId
JOIN OrganisationStructuresParents osp ON osrp.OrganisationStructureId = osp.id
WHERE rp.Active = 1
AND osp.ParentId IS NULL
),

allSiteWorkDays AS
(
SELECT DISTINCT ost.id OrgType, rp.Alias AS Name 
FROM %helpdeskdb.RotaEntry re 
JOIN %helpdeskdb.ResourceProfile rp ON re.ResourceProfileId = rp.Id
JOIN %helpdeskdb.organisationstructuresite oss ON oss.id = re.OrganisationStructureSiteId
JOIN organisationStructuresParents osp ON osp.Id = oss.OrganisationStructureId
JOIN %helpdeskdb.OrganisationStructure OS ON OS.id = oss.OrganisationStructureId
JOIN %helpdeskdb.OrganisationStructureType ost ON ost.id = os.OrganisationStructureTypeId
JOIN %helpdeskdb.OrganisationStructureResourceProfile osrp ON osrp.ResourceProfileId = rp.id
LEFT JOIN %helpdeskdb.RotaEntryType ret ON ret.Id=re.RotaEntryTypeId
WHERE rp.active = 1
AND ret.id = 8
AND rp.id IN (SELECT OrgType FROM ResourceProfileIDS)
),

sitesCovered AS (
SELECT (COUNT(DISTINCT s.name) * 7) totalSites
		FROM %helpdeskdb.uvw_ManagerToOrganisationStructures mtos
		JOIN %helpdeskdb.organisationstructure os ON mtos.OrganisationStructureId = os.id
		JOIN %helpdeskdb.organisationstructuresite oss ON oss.OrganisationStructureId = os.Id
		join %helpdeskdb.SIte s ON s.id = oss.SiteId
		WHERE ManagerResourceId = ' + CONVERT(NVARCHAR, @resourceID) +' ),

allRawReport AS (
SELECT DISTINCT totalSites,
FORMAT(DATEADD(DAY, 1 ' + @startOfWeekOffset + ' - DATEPART(WEEKDAY, workDays.workDate), CAST(workDays.workDate AS DATE)), ''' + @dateFormat + ''') startDate,
FORMAT(DATEADD(DAY, 8 ' + @startOfWeekOffset + '- DATEPART(WEEKDAY, workDays.workDate), CAST(workDays.workDate AS DATE)), ''' + @dateFormat + ''') endDate,
OrgType, 0 daysCovered
FROM workDays, sitescovered, allSiteWorkDays),

allPivotreport AS (SELECT * FROM allRawReport
PIVOT (sum(dayscovered) for OrgType IN (' +  @cols + ')) AS dayscovered),

allReport AS (
SELECT startDate [From], endDate [To], ' + @case + '
 FROM allPivotreport ),

siteWorkDays AS
(SELECT s.name AS sitename, ost.Id AS OrgType, workDays.workDate, re.StartAt AS startedfrom, re.EndAt AS endedAT   
FROM workDays, %helpdeskdb.rotaentry re
JOIN %helpdeskdb.organisationstructuresite oss ON oss.id = re.OrganisationStructureSiteId
JOIN organisationStructuresParents osp ON osp.Id = oss.OrganisationStructureId
JOIN %helpdeskdb.OrganisationStructure OS ON OS.id = oss.OrganisationStructureId
JOIN %helpdeskdb.Site s ON s.id = oss.SiteId
JOIN %helpdeskdb.OrganisationStructureType ost ON ost.id = os.OrganisationStructureTypeId
LEFT JOIN %helpdeskdb.RotaEntryType ret ON ret.Id=re.RotaEntryTypeId
WHERE re.active = 1
AND ret.id = 8
AND SWITCHOFFSET (CAST (workDays.workDate AS DATETIMEOFFSET), 101) BETWEEN re.StartAt AND re.EndAt
),

siteWorkWeeks AS (
SELECT DISTINCT siteWorkDays.SiteName SiteName,
FORMAT(DATEADD(DAY, 1 ' + @startOfWeekOffset + '- DATEPART(WEEKDAY, workDate), CAST(workDate AS DATE)), ''' + @dateFormat + ''') startDate,
FORMAT(DATEADD(DAY, 8 ' + @startOfWeekOffset + '- DATEPART(WEEKDAY, workDate), CAST(workDate AS DATE)), ''' + @dateFormat + ''') endDate,
workDate, OrgType
FROM siteWorkDays
),

rawReport AS (
SELECT startDate, endDate, COUNT(1) daysCovered, OrgType FROM siteWorkWeeks
GROUP BY startDate, endDate, OrgType),
/*Actual data in pivot report */
pivotreport AS (SELECT * FROM sitescovered, rawReport
pivot (sum(dayscovered) for OrgType IN (' +  @cols + ')) AS dayscovered),
/*Final actual report*/
report AS (
SELECT startDate [From], endDate [To],
' + @case + '
 FROM pivotreport)
 SELECT  ar.* FROM allReport ar LEFT JOIN report r ON ar.[From] = r.[From] AND ar.[To] = r.[To]
 WHERE r.[From] IS NULL
 UNION 
 SELECT * FROM report
 
 '
;
execute(@query);
END
