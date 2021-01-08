SELECT TOP(1) j.JobReference, au.UserName, r.Id ResourceId, s.Id SiteId 
FROM Job j
INNER JOIN JobFeedback jf ON jf.JobId = j.Id
LEFT JOIN JobFeedbackResponse jfr ON jf.Id = jfr.JobFeedbackId
INNER JOIN Site s ON j.SiteId = s.Id
INNER JOIN OrganisationStructureSite oss ON oss.SiteId = s.Id
INNER JOIN OrganisationStructure os ON oss.OrganisationStructureId = os.Id
INNER JOIN uvw_ManagerToOrganisationStructures mos ON mos.OrganisationStructureId = os.Id
INNER JOIN Resource r ON mos.ManagerResourceId = r.Id
INNER JOIN ApplicationUser au ON au.ResourceId = r.Id
INNER JOIN UserProfile up ON au.UserProfileId = up.Id
WHERE j.JobStatusId IN (8, 9)
AND au.Active = 1 AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL)
AND j.JobReference = %d
AND up.Name = '%s'
AND mos.CoveredManagerResourceId IS NULL
AND jfr.Id IS NULL
ORDER BY j.JobReference
