IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[Staging].[ufn_GetTimeAtHomeOffice]') AND type = 'FN'
)
EXEC ('DROP FUNCTION [Staging].[ufn_GetTimeAtHomeOffice]')


EXEC ('


CREATE FUNCTION [Staging].[ufn_GetTimeAtHomeOffice] 
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
    
    SELECT @offset = UtcDstOffset FROM %testdb.TestAutomation_IanaTimezones WHERE IanaCode = dbo.ufn_GetServiceCentreTimezoneForSite(@siteId);

    SELECT @Result = SWITCHOFFSET(GETUTCDATE(), CAST(@offset AS VARCHAR));

    RETURN @Result

END



')

