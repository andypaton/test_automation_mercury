IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[Staging].[TestAutomation_IanaTimezones]') AND type IN (N'U')) 
BEGIN 


CREATE TABLE [Staging].[TestAutomation_IanaTimezones](
    [CC] [nvarchar](256) ,
    [Coordinates] [nvarchar](256),
    [IanaCode] [nvarchar](256) NOT NULL,
    [Comments] [nvarchar](256),
    [Format] [nvarchar](256) ,
    [UtcOffset] [nvarchar](256) NOT NULL,
    [UtcDstOffset] [nvarchar](256) NOT NULL,
    [Notes] [nvarchar](256) 
)


END