/*** resource is not absent and either in normal working hours OR is during rota hours ***/

/*** 
*    for Notifications to be sent the resource must have email and phone numbers configured!
*    and for a notification to an ipad a row must exist in ApplicationUserMobileApplication - that table gets populated when the user logs in via the MyJobs iOS app
***/
        
        
IF EXISTS (
	SELECT * FROM sys.objects 
	WHERE object_id = OBJECT_ID(N'[Staging].[uvw_Resources]') AND type = 'V'
)
EXEC ('DROP VIEW %testdb.uvw_Resources')

EXEC ( '

CREATE VIEW %testdb.uvw_Resources
AS 


SELECT DISTINCT 
    r.Id, r.Name
    , rp.Name AS ResourceProfileName
    , rt.Name AS ResourceTypeName
    , rpn.PhoneNumber, pnt.Name AS PhoneNumberType
    , rea.EmailAddress
    , iPadUsers.NotificationMethodName
FROM Resource r
LEFT JOIN ResourceProfile rp ON rp.Id=r.ResourceProfileId
LEFT JOIN ResourceType rt ON rp.ResourceTypeId = rt.Id 
LEFT JOIN uvw_ResourceAbsence ra ON ra.ResourceId = r.Id 
LEFT JOIN ResourcePhoneNumber rpn ON rpn.ResourceId = r.Id 
LEFT JOIN PhoneNumberType pnt ON rpn.PhoneNumberTypeId = pnt.Id
LEFT JOIN ResourceEmailAddress rea ON rea.ResourceId = r.Id 
LEFT JOIN (SELECT r.Id, nmt.Name AS NotificationMethodName FROM Resource r
            LEFT JOIN ResourceNotificationMethod rnm ON r.Id = rnm.ResourceId 
            LEFT JOIN NotificationMethodType nmt ON rnm.NotificationMethodTypeId = nmt.Id
            LEFT JOIN ApplicationUser au ON au.ResourceId = r.Id
            LEFT JOIN ApplicationUserMobileApplication auma ON auma.ApplicationUserId = au.Id
            WHERE nmt.Name = 'iPad'
            AND auma.Id IS NOT NULL) iPadUsers ON iPadUsers.Id = r.Id 
WHERE
r.Active = 1
AND rp.Active = 1 and rp.ActiveForClient = 1
AND (coalesce(ra.AbsenceStartAt, '2999-12-01') > cast(SWITCHOFFSET(GETDATE(), '+00:00') AS DATE) OR coalesce(ra.AbsenceEndAt, '2999-12-01') < cast(SWITCHOFFSET(GETDATE(), '+00:00') AS DATE))
AND rp.JobCallouts = 1

')
