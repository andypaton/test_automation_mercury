IF  NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'%testdb.TestAutomation_Audit') AND type IN (N'U')) 
BEGIN 


CREATE TABLE %testdb.TestAutomation_Audit (
    [Id] [int] IDENTITY(1,1) PRIMARY KEY,
    [Title] [nvarchar](128),
    [JobReference] [nvarchar](256),
    [Description] [nvarchar](512),
    [Status] [nvarchar](32),
    [Created] datetime DEFAULT(getdate())
)


END