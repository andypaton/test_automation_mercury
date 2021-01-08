/*

Run this script in the database in to be configured

This script will drop all objects and recreated them. All data will be lost.

Find schema [%testSchema], [%helpdeskDb], [%portalDb] before running and replace 
with something more approrpiate (the values that would be in the env.properties file) 

*/
USE [%testdb]
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
-- gas functions


PRINT N'Creating [%testSchema].[ufn_GetOffSiteLocationQuestionStatus]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testSchema].[ufn_GetOffSiteLocationQuestionStatus]') AND type = 'FN'
)
DROP FUNCTION [%testSchema].[ufn_GetOffSiteLocationQuestionStatus]
GO
CREATE FUNCTION [%testSchema].[ufn_GetOffSiteLocationQuestionStatus]
(
    -- Add the parameters for the function here
    @gasTypeName NVARCHAR(255)
)
RETURNS NVARCHAR(256)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @Result bit
       
    SELECT @Result =  IsOffSiteLocationRequired FROM  [%helpdeskDb].GasSourceType gst
    WHERE gst.Name = @gasTypeName AND gst.Active = 1;
    
    RETURN @Result

END
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testSchema].[ufn_GetLeakQuestionCheckStatus]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testSchema].[ufn_GetLeakQuestionCheckStatus]') AND type = 'FN'
)
DROP FUNCTION [%testSchema].[ufn_GetLeakQuestionCheckStatus]
GO
CREATE FUNCTION [%testSchema].[ufn_GetLeakQuestionCheckStatus]
(
    -- Add the parameters for the function here
    @gasLeakCheckStatusName NVARCHAR(255),
    @gasLeakCheckResultTypeName NVARCHAR(255)
)
RETURNS NVARCHAR(256)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @Result bit
       
    -- used to determine if the question "Have you completed any leak checks during this visit?" should be displayed
    SELECT  @Result = (glcs.IsResourceRequiredToReturn | glcrt.IsResourceRequiredToReturn)   FROM 
       [%helpdeskDb].GasLeakCheckStatus glcs,
       [%helpdeskDb].GasLeakCheckResultType glcrt 
    WHERE
       glcs.Name = @gasLeakCheckStatusName AND glcs.Active = 1
       AND glcrt.Name = @gasLeakCheckResultTypeName AND glcrt.Active = 1;

    RETURN @Result

END
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testSchema].[ufn_GetGasCylinderMaxCapacity]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testSchema].[ufn_GetGasCylinderMaxCapacity]') AND type = 'FN'
)
DROP FUNCTION [%testSchema].[ufn_GetGasCylinderMaxCapacity]
GO
CREATE FUNCTION [%testSchema].[ufn_GetGasCylinderMaxCapacity]
(
    -- Add the parameters for the function here
    @gasCylinderTypeName NVARCHAR(255),
    @gasTypeName NVARCHAR(255)
)
RETURNS NVARCHAR(256)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @Result decimal(18,2)
       
    -- used to determine if the max capacity of a gas cylinder
    SELECT @Result = gcc.Capacity 
    FROM [%helpdeskDb].GasCylinderCapacity gcc INNER JOIN [%helpdeskDb].GasCylinderType gct 
        ON gcc.GasCylinderTypeId = gct.Id
    INNER JOIN [%helpdeskDb].GasType gt ON gcc.GasTypeId = gt.Id
    WHERE gct.Name = @gasCylinderTypeName AND gt.Name = @gasTypeName
    
    RETURN @Result

END
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testSchema].[ufn_GetFullOrPartialCylinderQuestionStatus]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testSchema].[ufn_GetFullOrPartialCylinderQuestionStatus]') AND type = 'FN'
)
DROP FUNCTION [%testSchema].[ufn_GetFullOrPartialCylinderQuestionStatus]
GO
CREATE FUNCTION [%testSchema].[ufn_GetFullOrPartialCylinderQuestionStatus]
(
    -- Add the parameters for the function here
    @gasTypeName NVARCHAR(255)
)
RETURNS NVARCHAR(256)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @Result bit
       
    SELECT @Result =  IsPartialAllowed FROM  [%helpdeskDb].GasSourceType gst
    WHERE gst.Name = @gasTypeName AND gst.Active = 1;
    
    RETURN @Result

END

GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testSchema].[ufn_GetFullOrPartialCylinderQuestionStatus]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testSchema].[ufn_GetApplianceIdentificationVisible]') AND type = 'FN'
)
DROP FUNCTION [%testSchema].[ufn_GetApplianceIdentificationVisible]
GO
CREATE FUNCTION [%testSchema].[ufn_GetApplianceIdentificationVisible]
(
    -- Add the parameters for the function here
    @siteName NVARCHAR(255),
    @applianceType NVARCHAR(255)
)
RETURNS NVARCHAR(256)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @Result bit
       
    -- used to determine if the question "Have you completed any leak checks during this visit?" should be displayed
    IF ( @applianceType = 'Commercial Refrigeration - Remote System')
     SELECT @Result = 
     (CASE WHEN (COUNT(a.id)>0) THEN 1
        WHEN (COUNT(a.id)=0) THEN 0 END) 
     FROM [%helpdeskDb].Site s INNER JOIN [%helpdeskDb].Asset a  ON s.id = a.siteid
     INNER JOIN [%helpdeskDb].AssetClassification ac ON a.AssetClassificationId = ac.Id  
     INNER JOIN [%helpdeskDb].AssetSubType ast ON ac.AssetSubTypeId = ast.Id 
     INNER JOIN [%helpdeskDb].AssetType at ON at.id = ast.AssetTypeID
     WHERE  ast.IsAssetPlant = 1
     AND at.UsesGas = 1
     AND at.Name = 'Refrigeration'
     AND s.name = @siteName;

   IF ( @applianceType = 'Commercial Refrigeration - Self Contained')
     SELECT @Result = 0;

   IF ( @applianceType = 'Comfort Cooling - Remote System')
     SELECT @Result = (CASE WHEN (COUNT(a.id)>0) THEN 1
        WHEN (COUNT(a.id)=0) THEN 0 END) 
     FROM [%helpdeskDb].Site s INNER JOIN [%helpdeskDb].Asset a ON s.id = a.siteid
     INNER JOIN [%helpdeskDb].AssetClassification ac ON a.AssetClassificationId = ac.Id  
     INNER JOIN [%helpdeskDb].AssetSubType ast ON ac.AssetSubTypeId = ast.Id 
     INNER JOIN [%helpdeskDb].AssetType at ON at.id = ast.AssetTypeID     
     WHERE  at.UsesGas = 1
     AND at.Name = 'HVAC'
     AND s.name = @siteName;

   IF ( @applianceType = 'Comfort Cooling - Self Contained')
    SELECT @Result = 0;

   RETURN @Result

END

GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testSchema].[ufn_GetApplianceDetailsVisible]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testSchema].[ufn_GetApplianceDetailsVisible]') AND type = 'FN'
)
DROP FUNCTION [%testSchema].[ufn_GetApplianceDetailsVisible]
GO
CREATE FUNCTION [%testSchema].[ufn_GetApplianceDetailsVisible]
(
    -- Add the parameters for the function here
    @applianceType NVARCHAR(255),
    @applianceIdentification NVARCHAR(255)
)
RETURNS NVARCHAR(256)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @Result bit

    
    -- used to determine if the question "Please provide appliance details" should be displayed
    SELECT @Result = 1;    --default to show

    IF ( (@applianceType = 'Commercial Refrigeration - Remote System') AND (@applianceIdentification <> 'Appliance not on list'))
    BEGIN
     SELECT @Result = 0;
    END


   IF (( @applianceType = 'Comfort Cooling - Remote System') AND (@applianceIdentification <> 'Appliance not on list'))
   BEGIN
     SELECT @Result = 0;
   END

   RETURN @Result

END
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testSchema].[ufn_GetSurplusReturnedToQuestionStatus]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testSchema].[ufn_GetSurplusReturnedToQuestionStatus]') AND type = 'FN'
)
DROP FUNCTION [%testSchema].[ufn_GetSurplusReturnedToQuestionStatus]
GO
CREATE FUNCTION [%testSchema].[ufn_GetSurplusReturnedToQuestionStatus]
(
    -- Add the parameters for the function here
    @gasSurplusDestinationName NVARCHAR(255)    
)
RETURNS NVARCHAR(256)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @Result bit
       
    -- used to determine if the question "Have you completed any leak checks during this visit?" should be displayed
    SELECT  @Result = (gsd.IsReturnedToRequired)   FROM 
       [%helpdeskDb].GasSurplusDestination gsd
    WHERE
       gsd.Name = @gasSurplusDestinationName AND gsd.Active = 1       

    RETURN @Result

END
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testSchema].[ufn_GetLeakCheckMethodStatus]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testSchema].[ufn_GetLeakCheckMethodStatus]') AND type = 'FN'
)
DROP FUNCTION [%testSchema].[ufn_GetLeakCheckMethodStatus]
GO
CREATE FUNCTION [%testSchema].[ufn_GetLeakCheckMethodStatus]
(
    -- Add the parameters for the function here
    @gasLeakCheckStatusName NVARCHAR(255)
)
RETURNS NVARCHAR(256)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @Result bit
       
    -- used to determine if the question "Have you completed any leak checks during this visit?" should be displayed
    SELECT  @Result = (~glcs.NoCheckPerformed)   FROM 
       [%helpdeskDb].GasLeakCheckStatus glcs
    WHERE
       glcs.Name = @gasLeakCheckStatusName AND glcs.Active = 1
      

    RETURN @Result

END
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO

GO
PRINT N'Creating [%testSchema].[ufn_GetPoundsInCylinderDisplayed]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testSchema].[ufn_GetPoundsInCylinderDisplayed]') AND type = 'FN'
)
DROP FUNCTION [%testSchema].[ufn_GetPoundsInCylinderDisplayed]
GO
CREATE FUNCTION [%testSchema].[ufn_GetPoundsInCylinderDisplayed]
(
    -- Add the parameters for the function here
    @refrigerantSourceID NVARCHAR(255),
    @IsPartialCylinder NVARCHAR(255)
)
RETURNS NVARCHAR(256)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @Result int = 1 

    IF @IsPartialCylinder = 'Full'
    BEGIN
        SELECT @Result = 0 
    END

    IF @refrigerantSourceID = 'New Purchase Order'
    BEGIN
        SELECT @Result = 0 
    END

    RETURN @Result

END
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testSchema].[ufn_GetQuantityofBallsFloatingMax]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testSchema].[ufn_GetQuantityofBallsFloatingMax]') AND type = 'FN'
)
DROP FUNCTION [%testSchema].[ufn_GetQuantityofBallsFloatingMax]
GO
CREATE FUNCTION [%testSchema].[ufn_GetQuantityofBallsFloatingMax]
(
    @applianceTypeId NVARCHAR(255)
)
RETURNS NVARCHAR(256)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @Result int = 4

    RETURN @Result

END
GO
IF @@ERROR <> 0 SET NOEXEC ON
GO
PRINT N'Creating [%testSchema].[ufn_GetSurplusDestinationFullyUsed]'
GO
IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[%testSchema].[ufn_GetSurplusDestinationFullyUsed]') AND type = 'FN'
)
DROP FUNCTION [%testSchema].[ufn_GetSurplusDestinationFullyUsed]
GO
CREATE FUNCTION [%testSchema].[ufn_GetSurplusDestinationFullyUsed]
(
    -- Add the parameters for the function here
    @IsPartialCylinder NVARCHAR(255),
    @gasTypeId NVARCHAR(255),
    @gasCylinderTypeId NVARCHAR(255),
    @gasPoundsInCylinderID decimal,
    @fullyUsed NVARCHAR(255),    
    @lbsInstalled decimal

)
RETURNS NVARCHAR(256)
AS
BEGIN
    -- Declare the return variable here
    DECLARE @Result int
    DECLARE @Surplus bit = 0 
    DECLARE @maxCapacity decimal 

    IF @IsPartialCylinder = 'Full'
    BEGIN
        SELECT @maxCapacity = gcc.Capacity
        FROM [%helpdeskDb].GasType gt 
        INNER JOIN [%helpdeskDb].GasCylinderCapacity gcc ON gt.Id = gcc.GasTypeId
        INNER JOIN [%helpdeskDb].GasCylinderType gct ON gcc.GasCylinderTypeId = gct.Id
        where gt.Name = @gasTypeId
        AND gct.Name = @gasCylinderTypeId
        
        IF  (@maxCapacity - @lbsInstalled) > 0
        BEGIN
            SELECT @Surplus = 1
            SELECT @Result = @Surplus 
            RETURN @Result
        END
    END

    IF ISNUMERIC(@gasPoundsInCylinderID) + ISNUMERIC(@lbsInstalled)  = 2
    BEGIN
        IF  (@gasPoundsInCylinderID - @lbsInstalled) > 0
        BEGIN
            SELECT @Surplus = 1
        END
    END
    
    SELECT  @Result = @Surplus 


    RETURN @Result

END
GO
IF @@ERROR <> 0 SET NOEXEC ON
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
