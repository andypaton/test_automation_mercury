IF EXISTS (
    SELECT * FROM sys.objects 
    WHERE object_id = OBJECT_ID(N'[Staging].[uvw_AvailaibleSites]') AND type = 'V'
)
EXEC ('DROP VIEW [Staging].[uvw_AvailaibleSites]')


EXEC ('

CREATE VIEW [Staging].[uvw_AvailaibleSites]
AS 

SELECT s.*
FROM Site s
LEFT JOIN (SELECT * FROM %testdb.TestAutomation_Locks locks WHERE locks.Type = 'site') locks ON s.id = locks.Reference 
WHERE locks.Reference IS NULL 

')