/*

Run this script in the database in to be configured

This script will drop all objects and recreated them. All data will be lost.

Find schema [%testdb], [%helpdeskDb], [%portalDb] before running and replace 
with something more approrpiate (the values that would be in the env.properties file) 

*/
GO
SET NUMERIC_ROUNDABORT OFF
GO
SET ANSI_PADDING, ANSI_WARNINGS, CONCAT_NULL_YIELDS_NULL, ARITHABORT, QUOTED_IDENTIFIER, ANSI_NULLS ON
GO
SET XACT_ABORT ON
GO
SET TRANSACTION ISOLATION LEVEL Serializable
GO
BEGIN TRANSACTION
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testdb].[TestAutomation_Locks]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[TestAutomation_Locks]') AND type = 'U'
)
DROP TABLE [%testdb].[TestAutomation_Locks]
GO
CREATE TABLE [%testdb].[TestAutomation_Locks]
(
[Reference] [int] NOT NULL,
[Type] [nvarchar] (128) NULL,
[CreatedOn] [datetimeoffset] NOT NULL,
[Reason] [nvarchar] (128) NULL,
[Detail] [nvarchar] (4000) NULL
)
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testdb].[uvw_AvailaibleSites]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[uvw_AvailaibleSites]') AND type = 'V'
)
DROP VIEW [%testdb].[uvw_AvailaibleSites]
GO
CREATE VIEW [%testdb].[uvw_AvailaibleSites]
AS 
SELECT s.*
FROM [%helpdeskDb].Site s
LEFT JOIN (SELECT * FROM [%testdb].[%testdb].TestAutomation_Locks locks WHERE locks.Type = 'site') locks ON s.id = locks.Reference 
WHERE locks.Reference IS NULL 
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testdb].[uvw_CityTechStores]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[uvw_CityTechStores]') AND type = 'V'
)
DROP VIEW [%testdb].[uvw_CityTechStores]
GO
CREATE VIEW [%testdb].[uvw_CityTechStores]
AS 

SELECT DISTINCT 
    s.Id, s.Name, s.SiteTypeId
    , bwh.DayOfTheWeek AS BrandDayOfTheWeek, bwh.StartAt AS BrandStartAt, bwh.EndAt AS BrandEndAt
    , dwh.DayOfTheWeek AS DeferralDayOfTheWeek, dwh.StartAt AS DeferralStartAt, dwh.EndAt AS DeferralEndAt
FROM [%testdb].[%testdb].uvw_AvailaibleSites s
LEFT JOIN [%helpdeskDb].AllInclusiveContractorSiteMapping ascm ON ascm.SiteId = s.Id
INNER JOIN [%helpdeskDb].SiteResource sr ON sr.SiteId = s.Id
INNER JOIN [%helpdeskDb].Resource r ON r.Id = sr.ResourceId
LEFT JOIN [%helpdeskDb].BrandWorkingHoursGroup bwhg ON s.BrandId = bwhg.BrandId
 JOIN [%helpdeskDb].BrandWorkingHours bwh ON bwhg.BrandWorkingHoursId = bwh.Id AND bwh.DayOfTheWeek = Datepart(dw, Getdate())
LEFT JOIN [%helpdeskDb].DeferralWorkingHoursGroup dwhg ON s.BrandId = dwhg.BrandId
 JOIN [%helpdeskDb].DeferralWorkingHours dwh ON dwhg.DeferralWorkingHoursId = dwh.Id AND dwh.DayOfTheWeek = Datepart(dw, Getdate())
WHERE ascm.Id IS NULL
AND r.ResourceProfileId IN (3, 31, 134, 1155) 
AND s.active=1 
AND s.SiteStatusId IN (1,2)




GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testdb].[TestAutomation_IanaTimezones]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[TestAutomation_IanaTimezones]') AND type = 'U'
)
DROP TABLE [%testdb].[TestAutomation_IanaTimezones]
GO
CREATE TABLE [%testdb].[TestAutomation_IanaTimezones]
(
[CC] [nvarchar] (256) NULL,
[Coordinates] [nvarchar] (256) NULL,
[IanaCode] [nvarchar] (256) NOT NULL,
[Comments] [nvarchar] (256) NULL,
[Format] [nvarchar] (256) NULL,
[UtcOffset] [nvarchar] (256) NOT NULL,
[UtcDstOffset] [nvarchar] (256) NOT NULL,
[Notes] [nvarchar] (256) NULL
)
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testdb].'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[ufn_GetTimeAtSite]') AND type = 'FN'
)
DROP FUNCTION [%testdb].[ufn_GetTimeAtSite]
GO
CREATE FUNCTION [%testdb].[ufn_GetTimeAtSite] 
(
    -- Add the parameters for the function here
    @siteId int
)
RETURNS NVARCHAR(256)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @Result NVARCHAR(256)

    DECLARE @offset NVARCHAR(256)
    
    SELECT @offset = UtcDstOffset 
    FROM [%testdb].[%testdb].TestAutomation_IanaTimezones tait 
    JOIN [%helpdeskDb].IANATimezones it ON it.IanaCode = tait.IanaCode
    JOIN [%helpdeskDb].Site s ON s.IanaTimezoneId = it.Id WHERE s.Id = @siteId;

    SELECT @Result = SWITCHOFFSET(GETUTCDATE(), CAST(@offset AS VARCHAR));

    RETURN @Result

END
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testdb].[uvw_Resources]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[uvw_Resources]') AND type = 'V'
)
DROP VIEW [%testdb].[uvw_Resources]
GO
CREATE VIEW [%testdb].[uvw_Resources]
AS 

SELECT DISTINCT 
    r.Id, r.Name
    , rp.Name AS ResourceProfileName
    , rt.Name AS ResourceTypeName
    , rpn.PhoneNumber, pnt.Name AS PhoneNumberType
    , rea.EmailAddress
    , iPadUsers.NotificationMethodName
FROM [%helpdeskDb].Resource r
LEFT JOIN [%helpdeskDb].ResourceProfile rp ON rp.Id=r.ResourceProfileId
LEFT JOIN [%helpdeskDb].ResourceType rt ON rp.ResourceTypeId = rt.Id 
LEFT JOIN [%helpdeskDb].uvw_ResourceAbsence ra ON ra.ResourceId = r.Id 
LEFT JOIN [%helpdeskDb].ResourcePhoneNumber rpn ON rpn.ResourceId = r.Id 
LEFT JOIN [%helpdeskDb].PhoneNumberType pnt ON rpn.PhoneNumberTypeId = pnt.Id
LEFT JOIN [%helpdeskDb].ResourceEmailAddress rea ON rea.ResourceId = r.Id 
LEFT JOIN (SELECT r.Id, nmt.Name AS NotificationMethodName FROM [%helpdeskDb].Resource r
            LEFT JOIN [%helpdeskDb].ResourceNotificationMethod rnm ON r.Id = rnm.ResourceId 
            LEFT JOIN [%helpdeskDb].NotificationMethodType nmt ON rnm.NotificationMethodTypeId = nmt.Id
            LEFT JOIN [%helpdeskDb].ApplicationUser au ON au.ResourceId = r.Id
            LEFT JOIN [%helpdeskDb].ApplicationUserMobileApplication auma ON auma.ApplicationUserId = au.Id
            WHERE nmt.Name = 'iPad'
            AND auma.Id IS NOT NULL) iPadUsers ON iPadUsers.Id = r.Id 
WHERE
r.Active = 1
AND rp.Active = 1 and rp.ActiveForClient = 1
AND (coalesce(ra.AbsenceStartAt, '2999-12-01') > cast(SWITCHOFFSET(GETDATE(), '+00:00') AS DATE) OR coalesce(ra.AbsenceEndAt, '2999-12-01') < cast(SWITCHOFFSET(GETDATE(), '+00:00') AS DATE))


GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testdb].[ufn_GetTimeAtHomeOffice]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[ufn_GetTimeAtHomeOffice]') AND type = 'FN'
)
DROP FUNCTION [%testdb].[ufn_GetTimeAtHomeOffice]
GO
CREATE FUNCTION [%testdb].[ufn_GetTimeAtHomeOffice] 
(
    -- Add the parameters for the function here
    @siteId int
)
RETURNS NVARCHAR(256)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @Result NVARCHAR(256)

    DECLARE @offset NVARCHAR(256)
    
    SELECT @offset = UtcDstOffset FROM [%testdb].[%testdb].TestAutomation_IanaTimezones WHERE IanaCode = [%helpdeskDb].ufn_GetServiceCentreTimezoneForSite(@siteId);

    SELECT @Result = SWITCHOFFSET(GETUTCDATE(), CAST(@offset AS VARCHAR));

    RETURN @Result

END
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testdb].[uvw_Rota]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[uvw_Rota]') AND type = 'V'
)
DROP VIEW [%testdb].[uvw_Rota]
GO

CREATE VIEW [%testdb].[uvw_Rota]
AS 


SELECT DISTINCT 
    re.ResourceId
    , rp.Name AS ResourceProfileName
    , rt.Name AS ResourceTypeName
    , oss.SiteId
    , ret.Name AS RotaEntryType, re.StartAt, re.EndAt
FROM [%helpdeskDb].RotaEntry re 
INNER JOIN [%helpdeskDb].OrganisationStructureSite oss ON oss.id=re.OrganisationStructureSiteId 
LEFT JOIN [%helpdeskDb].RotaEntryType ret ON ret.Id=re.RotaEntryTypeId
LEFT JOIN [%helpdeskDb].ResourceProfile rp ON rp.Id=re.ResourceProfileId
LEFT JOIN [%helpdeskDb].ResourceType rt ON rp.ResourceTypeId = rt.Id 
LEFT JOIN [%helpdeskDb].uvw_ResourceAbsence ra ON ra.ResourceId = re.ID 
WHERE
re.Active=1
AND (coalesce(ra.AbsenceStartAt, '2999-12-01') > cast(SWITCHOFFSET(GETDATE(), '+00:00') AS DATETIMEOFFSET) OR coalesce(ra.AbsenceEndAt, '2999-12-01') < cast(SWITCHOFFSET(GETDATE(), '+00:00') AS DATETIMEOFFSET))
AND (re.StartAt < cast(SWITCHOFFSET(GETDATE(), '+00:00') AS DATETIMEOFFSET) AND cast(SWITCHOFFSET(GETDATE(), '+00:00') AS DATETIMEOFFSET) < coalesce(re.EndAt, '2999-12-01'))


GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testdb].[uvw_SiteResources]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[uvw_SiteResources]') AND type = 'V'
)
DROP VIEW [%testdb].[uvw_SiteResources]
GO
CREATE VIEW [%testdb].[uvw_SiteResources]
AS 

SELECT DISTINCT(SiteId), ResourceId 
FROM (
SELECT oss.SiteId, ResourceId 
FROM [%helpdeskDb].RotaEntry re 
INNER JOIN [%helpdeskDb].OrganisationStructureSite oss ON oss.id=re.OrganisationStructureSiteId 
WHERE (re.StartAt < cast(SWITCHOFFSET(GETDATE(), '+00:00') AS DATE) AND cast(SWITCHOFFSET(GETDATE(), '+00:00') AS DATE) < coalesce(re.EndAt, '2999-12-01'))
AND re.Active=1
UNION
SELECT SiteId, ResourceId 
FROM [%helpdeskDb].AssetClassificationSiteContractorMapping acscm
INNER JOIN [%helpdeskDb].AssetClassificationSite acs ON acscm.AssetClassificationSiteId = acs.Id

) u

GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testdb].[uvw_VendorStores]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[uvw_VendorStores]') AND type = 'V'
)
DROP VIEW [%testdb].[uvw_VendorStores]
GO
CREATE VIEW [%testdb].[uvw_VendorStores]
AS 

SELECT DISTINCT
    s.Id, s.Name, s.SiteTypeId
    , bwh.DayOfTheWeek AS BrandDayOfTheWeek, bwh.StartAt AS BrandStartAt, bwh.EndAt AS BrandEndAt
    , dwh.DayOfTheWeek AS DeferralDayOfTheWeek, dwh.StartAt AS DeferralStartAt, dwh.EndAt AS DeferralEndAt
FROM [%testdb].[%testdb].uvw_AvailaibleSites s
LEFT JOIN [%helpdeskDb].AllInclusiveContractorSiteMapping ascm ON ascm.SiteId = s.Id
LEFT JOIN [%helpdeskDb].BrandWorkingHoursGroup bwhg ON s.BrandId = bwhg.BrandId
 JOIN [%helpdeskDb].BrandWorkingHours bwh ON bwhg.BrandWorkingHoursId = bwh.Id AND bwh.DayOfTheWeek = Datepart(dw, Getdate())
LEFT JOIN [%helpdeskDb].DeferralWorkingHoursGroup dwhg ON s.BrandId = dwhg.BrandId
 JOIN [%helpdeskDb].DeferralWorkingHours dwh ON dwhg.DeferralWorkingHoursId = dwh.Id AND dwh.DayOfTheWeek = Datepart(dw, Getdate())
WHERE ascm.Id IS NULL
AND s.id NOT IN (SELECT sr.SiteId FROM [%helpdeskDb].SiteResource sr, [%helpdeskDb].Resource r WHERE sr.ResourceId=r.Id and r.ResourceProfileId IN (3, 31, 134, 1155))
AND s.active=1 
AND s.SiteStatusId IN (1,2)


GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testdb].[uvw_JobsWithSingleActiveResource]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[uvw_JobsWithSingleActiveResource]') AND type = 'V'
)
DROP VIEW [%testdb].[uvw_JobsWithSingleActiveResource]
GO
CREATE VIEW [%testdb].[uvw_JobsWithSingleActiveResource]
AS 

(SELECT DISTINCT ra.jobId, ra.ResourceAssignmentStatusId, ra.resourceId
FROM [%helpdeskDb].ResourceAssignment ra LEFT JOIN (SELECT * FROM [%helpdeskDb].ResourceAssignment WHERE ResourceAssignmentStatusId IN ( 13, 15, 16) ) rac
ON ra.JobId = rac.JobId
left join [%helpdeskDb].AdditionalResourceRequirement arr ON ra.JobId = arr.JobId
WHERE arr.id IS NULL )
UNION  (
SELECT DISTINCT ra.jobId, ra.ResourceAssignmentStatusId, ra.resourceId
FROM [%helpdeskDb].ResourceAssignment ra INNER JOIN (SELECT jobid FROM [%helpdeskDb].ResourceAssignment ra1 GROUP BY JobId HAVING COUNT(ra1.resourceid) = 1 ) rac
ON ra.JobId = rac.JobId
LEFT JOIN [%helpdeskDb].AdditionalResourceRequirement arr ON ra.JobId = arr.JobId
WHERE arr.id IS NULL)

GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testdb].[uvw_Stores]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[uvw_Stores]') AND type = 'V'
)
DROP VIEW [%testdb].[uvw_Stores]
GO
CREATE VIEW [%testdb].[uvw_Stores]
AS 

SELECT DISTINCT 
    s.Id, s.Name, s.SiteTypeId
    , bwh.DayOfTheWeek AS BrandDayOfTheWeek, bwh.StartAt AS BrandStartAt, bwh.EndAt AS BrandEndAt
    , dwh.DayOfTheWeek AS DeferralDayOfTheWeek, dwh.StartAt AS DeferralStartAt, dwh.EndAt AS DeferralEndAt
    , CASE WHEN c.SiteId IS NULL THEN 0 ELSE c.NumContractors END AS NumContractors
    , CASE WHEN ct.ResourceId IS NULL THEN 0 ELSE 1 END AS CityTechConfigured
FROM [%helpdeskDb].Site s
LEFT JOIN [%helpdeskDb].AllInclusiveContractorSiteMapping ascm ON ascm.SiteId = s.Id
LEFT JOIN [%helpdeskDb].BrandWorkingHoursGroup bwhg ON s.BrandId = bwhg.BrandId
 JOIN [%helpdeskDb].BrandWorkingHours bwh ON bwhg.BrandWorkingHoursId = bwh.Id AND bwh.DayOfTheWeek = Datepart(dw, Getdate())
LEFT JOIN [%helpdeskDb].DeferralWorkingHoursGroup dwhg ON s.BrandId = dwhg.BrandId
 JOIN [%helpdeskDb].DeferralWorkingHours dwh ON dwhg.DeferralWorkingHoursId = dwh.Id AND dwh.DayOfTheWeek = Datepart(dw, Getdate())
LEFT JOIN (SELECT acs.SiteId, COUNT(DISTINCT(ResourceId)) AS NumContractors FROM [%helpdeskDb].AssetClassificationSiteContractorMapping acscm JOIN [%helpdeskDb].AssetClassificationSite acs ON acscm.AssetClassificationSiteId = acs.Id GROUP BY acs.SiteId) c ON c.SiteId = s.Id 
LEFT JOIN (SELECT SiteId, ResourceId FROM [%testdb].[%testdb].uvw_Rota rota WHERE RotaEntryType = 'Permanent' AND ResourceTypeName = 'City Resource') ct ON ct.SiteId = s.Id 
WHERE ascm.Id IS NULL
AND s.active=1 
AND s.SiteStatusId IN (1,2)


GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testdb].[uvw_QuoteApprovalScenariosUsers]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[uvw_QuoteApprovalScenariosUsers]') AND type = 'V'
)
DROP VIEW [%testdb].[uvw_QuoteApprovalScenariosUsers]
GO
CREATE VIEW [%testdb].[uvw_QuoteApprovalScenariosUsers]
AS 

SELECT DISTINCT * FROM
((
    SELECT qas.FaultId AS JobReference, au.UserName, r.Id ResourceId, qas.QuoteJobApprovalStatusId, qas.ApprovalStatusId,  qas.StoreId, qas.FundingRouteId, qas.Cost, qas.IsMultiQuote
    FROM %portaldb.vw_QuoteApprovalScenarios qas  
    INNER JOIN [%helpdeskDb].Resource r ON qas.ResourceId = r.EpochId   
    INNER JOIN  [%helpdeskDb].ApplicationUser au ON au.ResourceId = r.id   
) 
UNION
(
    SELECT qas.FaultId AS JobReference, au.UserName, r.Id ResourceId, qas.QuoteJobApprovalStatusId, qas.ApprovalStatusId,  qas.StoreId, qas.FundingRouteId, qas.Cost, qas.IsMultiQuote
    FROM %portaldb.vw_QuoteApprovalScenarios qas  
    INNER JOIN [%helpdeskDb].Resource r ON qas.ManagerApproverID = r.EpochId   
    INNER JOIN  [%helpdeskDb].ApplicationUser au ON au.ResourceId = r.id   
)
UNION 
(
    SELECT qas.FaultId AS JobReference, au.UserName, r.Id ResourceId, qas.QuoteJobApprovalStatusId, qas.ApprovalStatusId,  qas.StoreId, qas.FundingRouteId, qas.Cost, qas.IsMultiQuote
    FROM %portaldb.vw_QuoteApprovalScenarios qas  
    INNER JOIN [%helpdeskDb].Resource r ON qas.SeniorManagerApproverID = r.EpochId   
    INNER JOIN  [%helpdeskDb].ApplicationUser au ON au.ResourceId = r.id   
)) AS QAS1

GO
IF @@ERROR <> 0 SET NOEXEC ON
GO

GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testdb].[TestAutomation_BrandWorkingHours]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[TestAutomation_BrandWorkingHours]') AND type = 'U'
)
DROP TABLE [%testdb].[TestAutomation_BrandWorkingHours]
GO
CREATE TABLE [%testdb].[TestAutomation_BrandWorkingHours]
(
[Id] [int] NOT NULL,
[DayOfTheWeek] [int] NOT NULL,
[StartAt] [time] NOT NULL,
[EndAt] [time] NOT NULL,
[TestRunDate] [datetimeoffset] NULL
)
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testdb].[TestAutomation_DeferralWorkingHours]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[TestAutomation_DeferralWorkingHours]') AND type = 'U'
)
DROP TABLE [%testdb].[TestAutomation_DeferralWorkingHours]
GO
CREATE TABLE [%testdb].[TestAutomation_DeferralWorkingHours]
(
[Id] [int] NOT NULL,
[DayOfTheWeek] [int] NOT NULL,
[StartAt] [time] NOT NULL,
[EndAt] [time] NOT NULL,
[TestRunDate] [datetimeoffset] NULL
)
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testdb].[TestAutomation_ResourceWorkingHours]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[TestAutomation_ResourceWorkingHours]') AND type = 'U'
)
DROP TABLE [%testdb].[TestAutomation_ResourceWorkingHours]
GO
CREATE TABLE [%testdb].[TestAutomation_ResourceWorkingHours]
(
[Id] [int] NOT NULL,
[ResourceId] [int] NOT NULL,
[StartDayOfTheWeek] [int] NOT NULL,
[EndDayOfTheWeek] [int] NOT NULL,
[StartAt] [time] NOT NULL,
[EndAt] [time] NOT NULL,
[CreatedOn] [datetimeoffset] NOT NULL,
[CreatedBy] [nvarchar] (128) NOT NULL,
[UpdatedOn] [datetimeoffset] NULL,
[UpdatedBy] [nvarchar] (128) NULL,
[TestRunDate] [datetimeoffset] NULL
)
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO

PRINT N'Creating [%testdb].[usp_Update_InvoiceTemplateCoordinates]'
GO

IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[usp_Update_InvoiceTemplateCoordinates]') AND type = 'P'
)
DROP PROCEDURE [%testdb].[usp_Update_InvoiceTemplateCoordinates]
GO
CREATE PROCEDURE  [%testdb].[usp_Update_InvoiceTemplateCoordinates]
(
@templateId int,
@taxWord VARCHAR(50)
)
AS
BEGIN
  update  %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 69 , fld_int_Y1 = 287 , fld_int_Width = 149, fld_int_Height = 84
  where fld_str_FieldName = 'Legal Entity' and fld_int_InvoiceTemplateID = @templateId

  update  %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 269 , fld_int_Y1 = 152 , fld_int_Width = 343, fld_int_Height = 50
  where fld_str_FieldName = 'Invoice Number' and fld_int_InvoiceTemplateID = @templateId

  update  %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 71 , fld_int_Y1 = 154 , fld_int_Width = 196, fld_int_Height = 59
  where fld_str_FieldName = 'Invoice Date' and fld_int_InvoiceTemplateID = @templateId

  update  %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 1023 , fld_int_Y1 = 1048, fld_int_Width = 130, fld_int_Height = 48
  where fld_str_FieldName = 'Net Amount' and fld_int_InvoiceTemplateID = @templateId

  update  %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 1021 , fld_int_Y1 = 1107 , fld_int_Width = 125, fld_int_Height = 36
  where fld_str_FieldName = CONCAT(@taxWord, ' Amount') and fld_int_InvoiceTemplateID = @templateId

  update  %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 1021 , fld_int_Y1 = 1208 , fld_int_Width = 127, fld_int_Height = 41
  where fld_str_FieldName = 'Gross Amount' and fld_int_InvoiceTemplateID = @templateId

END


GO
IF @@ERROR <> 0 SET NOEXEC ON
GO

IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[ufn_GetTimeAsSiteTime]') AND type = 'FN'
)
DROP FUNCTION [%testdb].[ufn_GetTimeAsSiteTime]
GO
CREATE FUNCTION [%testdb].[ufn_GetTimeAsSiteTime] 
(
    -- Add the parameters for the function here
    @siteId int,
    @dateTime Datetime
)
RETURNS Datetime
AS
BEGIN
    -- Declare the return variable here
    DECLARE @Result Datetime

    DECLARE @offset NVARCHAR(256)
    
    SELECT @offset = UtcDstOffset 
    FROM [%testdb].[%testdb].TestAutomation_IanaTimezones tait 
    JOIN [%helpdeskDb].IANATimezones it ON it.IanaCode = tait.IanaCode
    JOIN [%helpdeskDb].Site s ON s.IanaTimezoneId = it.Id WHERE s.Id = @siteId;

    SELECT @Result = SWITCHOFFSET(@dateTime, CAST(@offset AS VARCHAR));

    RETURN @Result

END
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO

PRINT N'Creating [%testdb].[usp_UpdatePrimaryCompany]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[usp_UpdatePrimaryCompany]') AND type = 'P'
)
DROP PROCEDURE [%testdb].[usp_UpdatePrimaryCompany]
GO
CREATE PROCEDURE [%testdb].[usp_UpdatePrimaryCompany]
                 @primaryCompanyName VARCHAR(MAX)
AS
BEGIN
    UPDATE [%helpdeskDb].Company 
    SET IsPrimary = 1
    WHERE 
    ([%helpdeskDb].Company.Name = @primaryCompanyName)
    
    UPDATE  [%helpdeskDb].CompanyBusinessUnit
    SET ParentCompanyId = (Select id from  [%helpdeskDb].Company c where c.Name = @primaryCompanyName)

    UPDATE  [%helpdeskDb].CompanyClient
    SET ParentCompanyId = (Select id from  [%helpdeskDb].Company c where c.Name = @primaryCompanyName)

END

IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testdb].[TestAutomation_Audit]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[TestAutomation_Audit]') AND type = 'U'
)
DROP TABLE [%testdb].[TestAutomation_Audit]
GO
CREATE TABLE [%testdb].TestAutomation_Audit (
    [Id] [int] IDENTITY(1,1) PRIMARY KEY,
    [Title] [nvarchar](128),
    [JobReference] [nvarchar](256),
    [Description] [nvarchar](512),
    [Status] [nvarchar](32),
    [Created] datetime DEFAULT(getdate())
)
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO

PRINT N'Creating [%testdb].[uvw_AvailaibleJobs]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[uvw_AvailaibleJobs]') AND type = 'V'
)
DROP VIEW [%testdb].[uvw_AvailaibleJobs]
GO
CREATE VIEW [%testdb].[uvw_AvailaibleJobs]
AS 
SELECT j.*
FROM [%helpdeskDb].Job j
LEFT JOIN (SELECT * FROM [%testdb].[%testdb].TestAutomation_Locks locks WHERE locks.Type = 'job') locks ON j.id = locks.Reference 
WHERE locks.Reference IS NULL 
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO


PRINT N'Creating [%testdb].[ufn_StripSpaces]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[ufn_StripSpaces]') AND type = 'FN'
)
DROP FUNCTION [%testdb].[ufn_GetTimeAtHomeOffice]
GO
CREATE FUNCTION [%testdb].ufn_StripSpaces(@str varchar(8000))
RETURNS varchar(8000) AS
BEGIN 
    WHILE CHARINDEX('  ', @str) > 0 
        SET @str = REPLACE(@str, '  ', ' ')

    RETURN @str
END
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO

PRINT N'Creating [%testdb].[usp_Update_InvoiceTemplateCoordinatesInvoicing]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[usp_Update_InvoiceTemplateCoordinatesInvoicing]') AND type = 'P'
)
DROP PROCEDURE [%testdb].[usp_Update_InvoiceTemplateCoordinatesInvoicing]
GO
CREATE PROCEDURE  [%testdb].[usp_Update_InvoiceTemplateCoordinatesInvoicing]
(
@templateId int,
@taxWord VARCHAR(50)
)
AS
BEGIN
  update  %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 26 , fld_int_Y1 = 130 , fld_int_Width = 63, fld_int_Height = 32
  where fld_str_FieldName = 'Legal Entity' and fld_int_InvoiceTemplateID = @templateId

  update  %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 206 , fld_int_Y1 = 62 , fld_int_Width = 141, fld_int_Height = 30
  where fld_str_FieldName = 'Invoice Number' and fld_int_InvoiceTemplateID = @templateId

  update  %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 24 , fld_int_Y1 = 44 , fld_int_Width = 85, fld_int_Height = 50
  where fld_str_FieldName = 'Invoice Date' and fld_int_InvoiceTemplateID = @templateId

  update  %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 417 , fld_int_Y1 = 465, fld_int_Width = 53, fld_int_Height = 17
  where fld_str_FieldName = 'Net Amount' and fld_int_InvoiceTemplateID = @templateId

  update  %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 424 , fld_int_Y1 = 485 , fld_int_Width = 48, fld_int_Height = 21
  where fld_str_FieldName = CONCAT(@taxWord, ' Amount') and fld_int_InvoiceTemplateID = @templateId

  update  %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 420 , fld_int_Y1 = 533 , fld_int_Width = 49, fld_int_Height = 21
  where fld_str_FieldName = 'Gross Amount' and fld_int_InvoiceTemplateID = @templateId

END
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO

PRINT N'Creating [%testdb].[usp_run_iPadSync]'
GO

IF EXISTS (
SELECT * FROM sys.objects
WHERE object_id = OBJECT_ID(N'[%testdb].[usp_run_iPadSync]') AND type = 'P'
)
DROP PROCEDURE [%testdb].[usp_run_iPadSync]
GO

CREATE PROCEDURE [%testdb].[%testdb].usp_run_iPadSync
(
@jobReference int
)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	-- Post Job Events to Epoch
	EXEC [%portalDb].ios.SP_CityWSJobs_Post_JobEvents

	-- Run PO Scripts & Update requests where the fault has not synced yet
	UPDATE PR SET PR.fld_int_EpochImportStatus = -100
	FROM [%portalDb].ios.tblEpochNewPartsRequest PR 
	LEFT OUTER JOIN %portaldb.tblHelpdeskFault F
	ON PR.fld_int_jobid = F.fld_int_id 
	WHERE PR.fld_int_jobType = 1 
	AND F.fld_int_id IS NULL
	AND PR.fld_int_jobid = @jobReference

	EXEC [%portalDb].ios.SP_CityWSParts_CreatePOsNewVersion
	EXEC [%portalDb].ios.SP_CityWSParts_CreatePPMPOsNewVersion

	--Reset requests without a fault for the next time
	UPDATE PR SET PR.fld_int_EpochImportStatus = 50
	FROM [%portalDb].ios.tblEpochNewPartsRequest PR 
	LEFT OUTER JOIN %portaldb.tblHelpdeskFault F
	ON PR.fld_int_jobid = F.fld_int_id 
	WHEREe PR.fld_int_jobType = 1 
	AND F.fld_int_id IS NULL
	AND PR.fld_int_jobid = @jobReference

	-- Generate GRNs
	DECLARE @dontRunGRNs BIT
	SELECT @dontRunGRNs = CASE WHEN GETDATE() BETWEEN CONVERT(VARCHAR,GETDATE(), 106) + ' 15:00:00' AND CONVERT(VARCHAR,GETDATE() + 1, 106) + ' 07:00:00' THEN 1 ELSE 0 END

	IF @dontRunGRNs = 0
	BEGIN
	PRINT 'Running CREATE GRN procedure'
		EXEC [%portalDb].ios.SP_CityWSParts_CreateGRNs
	END

    -- Update any Child PRojects where callout has been cancelled
	UPDATE CP SET cp.fld_dat_checksumUpdated = ce.fld_dat_checksumUpdated
	FROM %portaldb.tblPPMResourceCalloutEvent CE
	INNER JOIN %portaldb.tblChildProject CP ON ce.fld_int_ChildProjectID = cp.fld_int_ID
	INNER JOIN [%portalDb].ios.tblEpochNewPartsRequest PR ON PR.fld_int_childProject = CP.fld_int_ID
	WHERE ce.fld_dat_checksumUpdated >= GETDATE() - '00:30:00' 
	AND fld_int_StatusID <> 3 
	AND cp.fld_dat_checksumUpdated < ce.fld_dat_checksumUpdated
	AND PR.fld_int_jobid = @jobReference

    -- Update Job dates where PO Has been cancelled
	UPDATE F SET F.fld_dat_checksumUpdated = GETDATE() 
	FROM %portaldb.tblPo PO 
	INNER JOIN %portaldb.tblHelpdeskFault F ON po.fld_int_HelpdeskFaultID = f.fld_int_ID
	WHERE po.fld_dat_checksumUpdated >= GETDATE() - '00:02:00' 
	AND ISNULL(fld_int_HelpdeskFaultID , 0) <> 0 
	AND fld_int_POStatusID = 8 
	AND f.fld_dat_checksumUpdated < GETDATE() - '00:02:00'
	AND po.fld_int_HelpdeskFaultID = @jobReference

	UPDATE F SET F.fld_dat_checksumUpdated = GETDATE() 
	FROM %portaldb.tblPo PO 
	INNER JOIN %portaldb.tblHelpDeskFaultRedundantInfo F ON po.fld_int_HelpdeskFaultID = f.faultID
	WHERE po.fld_dat_checksumUpdated >= GETDATE() - '00:02:00' 
	AND ISNULL(fld_int_HelpdeskFaultID , 0) <> 0 
	AND fld_int_POStatusID = 8 
	AND f.fld_dat_checksumUpdated < GETDATE() - '00:02:00'
	AND po.fld_int_HelpdeskFaultID = @jobReference

	-- Register runtime
	EXEC [%portalDb].ios.insert_ScheduledJobRunTime

END
GO
IF @@ERROR <> 0 SET NOEXEC ON
PRINT N'Creating [%testdb].[uvw_SubmittedInvoicesAndCredits]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testdb].[uvw_SubmittedInvoicesAndCredits]') AND type = 'V'
)
DROP VIEW [%testdb].[uvw_SubmittedInvoicesAndCredits]
GO

CREATE VIEW [%testdb].[uvw_SubmittedInvoicesAndCredits]
AS
WITH SubmittedWithErrors_1_CTE AS (SELECT sih.fld_int_Id Id,
  'Invoice' AS Type,
  po.[Supplier Name] Supplier,
  LTRIM(sih.fld_str_InvoiceNumber) RefNum,
  sih.fld_str_PO_PONumber OrderRef,
  CAST(hdf.fld_int_ID AS varchar) JobRef,
  IIF(s.fld_bit_VIP = 1 , concat(s.fld_str_Name, ' (VIP) '), s.fld_str_Name) AS Site,
  sih.fld_dat_InvoiceDate Date,
  sih.fld_cur_NetAmount Net,
  sih.fld_cur_VatAmount Tax,
  sih.fld_cur_GrossAmount AS Gross,
  sis.fld_str_DisplayName Status,
  'No' AS HasCredit
FROM %portaldb.tblcfSupplierInvoiceHeader sih
JOIN %portaldb.vw_purchase_order po ON po.[PO No] = sih.fld_str_PO_PONumber
JOIN %portaldb.tblHelpdeskFault hdf ON po.FaultID = hdf.fld_int_ID
JOIN %portaldb.tblStore s ON hdf.fld_int_StoreID = s.fld_int_ID
JOIN %portaldb.tblStoreCluster sc ON s.fld_int_StoreClusterID = sc.fld_int_ID
JOIN %portaldb.tblHelpDeskResource hdr ON sc.fld_int_AreaManagerID = hdr.fld_int_ID
LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteHeader scnh ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id  
WHERE scnh.fld_int_SupplierInvoiceHeaderId IS NULL
AND sis.fld_str_DisplayName NOT IN ('New', 'Submitted With Errors')
AND po.POtypeID IN (5, 17)),
 
SubmittedWtihErrors_2_CTE AS (SELECT sih.fld_int_Id Id,
  'Invoice' AS Type,
  po.[Supplier Name] Supplier,
  LTRIM(sih.fld_str_InvoiceNumber) RefNum,
  sih.fld_str_PO_PONumber OrderRef,
  CAST(po.FaultID AS varchar) JobRef,
  IIF(s.fld_bit_VIP = 1 , concat(s.fld_str_Name, ' (VIP) '), s.fld_str_Name) AS Site,
  sih.fld_dat_InvoiceDate Date,
  sih.fld_cur_NetAmount Net,
  sih.fld_cur_VatAmount Tax,
  sih.fld_cur_GrossAmount AS Gross,
  sis.fld_str_DisplayName Status,
  'No' AS HasCredit
FROM %portaldb.tblcfSupplierInvoiceHeader sih
JOIN %portaldb.vw_purchase_order po ON po.[PO No] = sih.fld_str_PO_PONumber
JOIN %portaldb.tblHelpdeskFault hdf ON po.FaultID = hdf.fld_int_ID
JOIN %portaldb.tblStore s ON hdf.fld_int_StoreID = s.fld_int_ID
JOIN %portaldb.tblStoreCluster sc ON s.fld_int_StoreClusterID = sc.fld_int_ID
JOIN %portaldb.tblHelpDeskResource hdr ON sc.fld_int_AreaManagerID = hdr.fld_int_ID
LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteHeader scnh ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id  
WHERE scnh.fld_int_SupplierInvoiceHeaderId IS NOT NULL
AND sis.fld_str_DisplayName NOT IN ('New', 'Submitted With Errors')
AND scns.fld_str_DisplayName IN ('New', 'Submitted With Errors')
AND po.POtypeID IN (5, 17)),

SubmittedWtihErrors_3_CTE AS (SELECT sih.fld_int_Id Id,
  'Invoice' AS Type,
  po.[Supplier Name] Supplier,
  LTRIM(sih.fld_str_InvoiceNumber) RefNum,
  sih.fld_str_PO_PONumber OrderRef,
  (CASE WHEN CAST(po.FaultID AS varchar) = 0 THEN 'N/A' END) AS JobRef,
  ISNULL(StoreName, 'N/A') Site,
  sih.fld_dat_InvoiceDate Date,
  sih.fld_cur_NetAmount Net,
  sih.fld_cur_VatAmount Tax,
 sih.fld_cur_GrossAmount AS Gross,
  sis.fld_str_DisplayName Status,
  'No' AS HasCredit
FROM %portaldb.tblcfSupplierInvoiceHeader sih
LEFT JOIN %portaldb.vw_purchase_order po ON sih.fld_str_PO_PONumber = po.[PO No]
LEFT JOIN %portaldb.uvw_OpenPurchaseOrders opo ON opo.PONumber = sih.fld_str_PO_PONumber
LEFT JOIN %portaldb.tblHelpdeskResource hdr ON sih.fld_int_Stage1Approver_HelpDeskResourceId = hdr.fld_int_ID
LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteHeader scnh ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id
WHERE sis.fld_str_DisplayName NOT IN ('New', 'Submitted With Errors')
AND scnh.fld_int_SupplierInvoiceHeaderId IS NULL
AND po.POtypeID = 28),

SubmittedWtihErrors_4_CTE AS (SELECT sih.fld_int_Id Id,
  'Invoice' AS Type,
  po.[Supplier Name] Supplier,
  LTRIM(sih.fld_str_InvoiceNumber) RefNum,
  sih.fld_str_PO_PONumber OrderRef,
  CAST(po.FaultID AS varchar) JobRef,
  ISNULL(StoreName, 'N/A') Site,
  sih.fld_dat_InvoiceDate Date,
  sih.fld_cur_NetAmount Net,
  sih.fld_cur_VatAmount Tax,
  sih.fld_cur_GrossAmount AS Gross,
  sis.fld_str_DisplayName Status,
  'No' AS HasCredit
FROM %portaldb.tblcfSupplierInvoiceHeader sih
LEFT JOIN %portaldb.vw_purchase_order po ON sih.fld_str_PO_PONumber = po.[PO No]
LEFT JOIN %portaldb.uvw_OpenPurchaseOrders opo ON opo.PONumber = sih.fld_str_PO_PONumber
LEFT JOIN %portaldb.tblHelpdeskResource hdr ON sih.fld_int_Stage1Approver_HelpDeskResourceId = hdr.fld_int_ID
LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteHeader scnh ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id
WHERE scnh.fld_int_SupplierInvoiceHeaderId IS NOT NULL
AND sis.fld_str_DisplayName NOT IN ('New', 'Submitted With Errors')
AND scns.fld_str_DisplayName IN ('New', 'Submitted With Errors')
AND po.POtypeID = 28),

SubmittedWtihErrors_5_CTE AS (SELECT sih.fld_int_Id Id,
  'Invoice' AS Type,
  po.[Supplier Name] Supplier,
  LTRIM(sih.fld_str_InvoiceNumber) RefNum,
  sih.fld_str_PO_PONumber OrderRef,
  CAST(ppmsl.fld_int_ID AS VARCHAR) JobRef,
  IIF(s.fld_bit_VIP = 1 , concat(s.fld_str_Name, ' (VIP) '), s.fld_str_Name) AS Site,
  sih.fld_dat_InvoiceDate Date,
  sih.fld_cur_NetAmount Net,
  sih.fld_cur_VatAmount Tax,
  sih.fld_cur_GrossAmount AS Gross,
  sis.fld_str_DisplayName Status,
  'No' AS HasCredit
FROM %portaldb.tblcfSupplierInvoiceHeader sih
JOIN %portaldb.vw_purchase_order po ON sih.fld_str_PO_PONumber = po.[PO No]
JOIN %portaldb.tblPO tpo ON sih.fld_str_PO_PONumber = tpo.fld_str_PONumber
JOIN %portaldb.tblProjectHeader ph ON tpo.fld_int_ProjectHeaderId = ph.fld_int_Id
JOIN %portaldb.tblPPMScheduleLine ppmsl ON ph.fld_int_PPMScheduleRef = ppmsl.fld_int_ID
JOIN %portaldb.tblStore s ON ppmsl.fld_int_StoreID = s.fld_int_ID
JOIN %portaldb.tblStoreCluster sc ON s.fld_int_StoreClusterID = sc.fld_int_ID
LEFT JOIN %portaldb.tblHelpDeskResource hdr ON sc.fld_int_AreaManagerID = hdr.fld_int_ID
LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteHeader scnh ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id
WHERE sis.fld_str_DisplayName NOT IN ('New', 'Submitted With Errors')
AND scnh.fld_int_SupplierInvoiceHeaderId IS NULL
AND tpo.fld_int_POTypeID IN (37, 43)),

SubmittedWtihErrors_6_CTE AS (SELECT sih.fld_int_Id Id,
  'Invoice' AS Type,
  po.[Supplier Name] Supplier,
  LTRIM(sih.fld_str_InvoiceNumber) RefNum,
  sih.fld_str_PO_PONumber OrderRef,
  CAST(ppmsl.fld_int_ID AS VARCHAR) JobRef,
  IIF(s.fld_bit_VIP = 1 , concat(s.fld_str_Name, ' (VIP) '), s.fld_str_Name) AS Site,
  sih.fld_dat_InvoiceDate Date,
  sih.fld_cur_NetAmount Net,
  sih.fld_cur_VatAmount Tax,
  sih.fld_cur_GrossAmount AS Gross,
  sis.fld_str_DisplayName Status,
  'No' AS HasCredit
FROM %portaldb.tblcfSupplierInvoiceHeader sih
LEFT JOIN %portaldb.vw_purchase_order po ON sih.fld_str_PO_PONumber = po.[PO No]
LEFT JOIN %portaldb.tblPO tpo ON sih.fld_str_PO_PONumber = tpo.fld_str_PONumber
LEFT JOIN %portaldb.tblProjectHeader ph ON tpo.fld_int_ProjectHeaderId = ph.fld_int_Id
LEFT JOIN %portaldb.tblPPMScheduleLine ppmsl ON ph.fld_int_PPMScheduleRef = ppmsl.fld_int_ID
LEFT JOIN %portaldb.tblStore s ON ppmsl.fld_int_StoreID = s.fld_int_ID
LEFT JOIN %portaldb.tblStoreCluster sc ON s.fld_int_StoreClusterID = sc.fld_int_ID
LEFT JOIN %portaldb.tblHelpDeskResource hdr ON sc.fld_int_AreaManagerID = hdr.fld_int_ID
LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteHeader scnh ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id
WHERE scnh.fld_int_SupplierInvoiceHeaderId IS NOT NULL
AND sis.fld_str_DisplayName NOT IN ('New', 'Submitted With Errors')
AND scns.fld_str_DisplayName IN ('New', 'Submitted With Errors')
AND tpo.fld_int_POTypeID IN (37, 43)),

SubmittedWtihErrors_7_CTE AS (SELECT sih.fld_int_Id Id,
  'Invoice' AS Type,
  po.[Supplier Name] Supplier,
  LTRIM(sih.fld_str_InvoiceNumber) RefNum,
  sih.fld_str_PO_PONumber OrderRef,
  CAST(po.FaultID AS varchar) JobRef,
  StoreName Site,
  sih.fld_dat_InvoiceDate Date,
  sih.fld_cur_NetAmount Net,
  sih.fld_cur_VatAmount Tax,
  sih.fld_cur_GrossAmount AS Gross,
  sis.fld_str_DisplayName Status,
  'Yes' AS HasCredit
FROM %portaldb.tblcfSupplierInvoiceHeader sih
LEFT JOIN %portaldb.vw_purchase_order po ON po.[PO No] = sih.fld_str_PO_PONumber
LEFT JOIN %portaldb.uvw_OpenPurchaseOrders opo ON opo.PONumber = sih.fld_str_PO_PONumber
LEFT JOIN %portaldb.tblcfSupplierInvoiceStatus sis ON sih.fld_int_SupplierInvoiceStatusId = sis.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteHeader scnh ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scnh.fld_int_SupplierCreditNoteStatusId = scns.fld_int_Id
WHERE scnh.fld_int_SupplierInvoiceHeaderId IS NOT NULL
AND scns.fld_str_DisplayName NOT IN ('New', 'Submitted With Errors')),

SubmittedWtihErrors_8_CTE AS (SELECT sih.fld_int_Id Id,
  REPLACE('Credit', 'Against Invoice', scnt.fld_str_Description) Type,
  po.[Supplier Name] Supplier,
  LTRIM(scnh.fld_str_CreditNoteNumber) RefNum,
  sih.fld_str_PO_PONumber OrderRef,
  'N/A' AS JobRef,
  'N/A' AS Site,
  scnh.fld_dat_CreditNoteDate Date,
  scnh.fld_cur_NetAmount Net,
  scnh.fld_cur_TaxAmount Tax,
  (scnh.fld_cur_NetAmount + scnh.fld_cur_TaxAmount) AS Gross,
  scns.fld_str_DisplayName Status,
  'N/A' AS HasCredit
FROM %portaldb.tblcfSupplierCreditNoteHeader scnh
LEFT JOIN %portaldb.tblcfSupplierInvoiceHeader sih ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id
LEFT JOIN %portaldb.vw_purchase_order po ON po.[PO No] = sih.fld_str_PO_PONumber
LEFT JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scns.fld_int_Id = scnh.fld_int_SupplierCreditNoteStatusId
LEFT JOIN %portaldb.tblcfSupplierCreditNoteType scnt ON scnh.fld_int_SupplierCreditNoteTypeId = scnt.fld_int_Id
WHERE scnh.fld_int_SupplierInvoiceHeaderId IS NOT NULL
AND scns.fld_str_DisplayName NOT IN ('New', 'Submitted With Errors')),

SubmittedWtihErrors_9_CTE AS (SELECT sih.fld_int_Id Id,
 'Credit' AS Type,
  r.fld_str_Name Supplier,
  LTRIM(scnh.fld_str_CreditNoteNumber) RefNum,
  '' OrderRef,
  'N/A' AS JobRef,
  'N/A' AS Site,
  scnh.fld_dat_CreditNoteDate Date,
  scnh.fld_cur_NetAmount Net,
  scnh.fld_cur_TaxAmount Tax,
  (scnh.fld_cur_NetAmount + scnh.fld_cur_TaxAmount) AS Gross,
  scns.fld_str_DisplayName Status,
  'N/A' AS HasCredit
FROM %portaldb.tblcfSupplierCreditNoteHeader scnh
LEFT JOIN %portaldb.tblHelpDeskResource r ON r.fld_str_SupplierID = scnh.fld_str_SupplierId
LEFT JOIN %portaldb.tblcfSupplierCreditNoteStatus scns ON scns.fld_int_Id = scnh.fld_int_SupplierCreditNoteStatusId
LEFT JOIN %portaldb.tblcfSupplierCreditNoteType scnt ON scnh.fld_int_SupplierCreditNoteTypeId = scnt.fld_int_Id
LEFT JOIN %portaldb.tblcfSupplierInvoiceHeader sih ON scnh.fld_int_SupplierInvoiceHeaderId = sih.fld_int_Id
WHERE scnh.fld_int_SupplierInvoiceHeaderId IS NULL
AND scns.fld_str_DisplayName NOT IN ('New', 'Submitted With Errors')),

UNION_CTE AS (
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from SubmittedWithErrors_1_CTE
UNION
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from SubmittedWtihErrors_2_CTE 
UNION
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from SubmittedWtihErrors_3_CTE 
UNION
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from SubmittedWtihErrors_4_CTE 
UNION
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from SubmittedWtihErrors_5_CTE 
UNION
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from SubmittedWtihErrors_6_CTE 
UNION
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from SubmittedWtihErrors_7_CTE 
UNION
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from SubmittedWtihErrors_8_CTE
UNION
SELECT Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
from SubmittedWtihErrors_9_CTE)

SELECT  Id, Type, Supplier, RefNum, OrderRef, JobRef, Site, Date, Net, Tax, Gross, Status, HasCredit 
FROM UNION_CTE
WHERE Date BETWEEN DATEADD(DAY, -31, GETUTCDATE()) AND GETUTCDATE()
GO


COMMIT TRANSACTION
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO







-- This statement writes to the SQL Server Log so SQL Monitor can show this deployment.
IF HAS_PERMS_BY_NAME(N'sys.xp_logevent', N'OBJECT', N'EXECUTE') = 1
BEGIN
    DECLARE @databaseName AS nvarchar(2048), @eventMessage AS nvarchar(2048)
    SET @databaseName = REPLACE(REPLACE(DB_NAME(), N'\', N'\\'), N'"', N'\"')
    SET @eventMessage = N'Redgate SQL Compare: { "deployment": { "description": "Redgate SQL Compare deployed to ' + @databaseName + N'", "database": "' + @databaseName + N'" }}'
    --EXECUTE sys.xp_logevent 55000, @eventMessage
END
GO
DECLARE @Success AS BIT
SET @Success = 1
SET NOEXEC OFF
IF (@Success = 1) PRINT 'The database update succeeded'
ELSE BEGIN
   -- IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION
    PRINT 'The database update failed'
END
GO
