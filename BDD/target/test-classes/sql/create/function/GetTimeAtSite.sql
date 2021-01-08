IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[Staging].[ufn_GetTimeAtSite]') AND type = 'FN'
)
EXEC ('DROP FUNCTION [Staging].[ufn_GetTimeAtSite]')


EXEC ('


CREATE FUNCTION [Staging].[ufn_GetTimeAtSite] 
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
    FROM %testdb.%testschema.TestAutomation_IanaTimezones tait 
    JOIN dbo.IANATimezones it ON it.IanaCode = tait.IanaCode
    JOIN %helpdeskdb.Site s ON s.IanaTimezoneId = it.Id WHERE s.Id = @siteId;

    SELECT @Result = SWITCHOFFSET(GETUTCDATE(), CAST(@offset AS VARCHAR));

    RETURN @Result

END



')

