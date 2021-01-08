IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[Staging].[TestAutomation_Users]') AND type IN (N'U')) 
BEGIN 


CREATE TABLE %testdb.TestAutomation_Users (
    [Id] [int] IDENTITY(1,1) PRIMARY KEY,
    [UserName] [nvarchar](256) NOT NULL UNIQUE,
    [Password] [nvarchar](256) NOT NULL,
    [Type] [nvarchar](256) NOT NULL,
    [LoggedOn] [bit] NOT NULL,
    [UpdatedOn] [datetimeoffset](7),
    [Active] [bit] NOT NULL,
    [DomainUser] [nvarchar](256),
)


END