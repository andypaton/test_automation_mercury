WITH OrganisationStructuresParents AS
(
SELECT mtos.OrganisationStructureId Id, os.Name, os.ParentId
FROM uvw_ManagerToOrganisationStructures mtos
JOIN organisationstructure os ON mtos.OrganisationStructureId = os.id
WHERE ManagerResourceId = :resourceId
UNION ALL
SELECT os2.Id,
os2.Name,
os2.Parentid
FROM OrganisationStructure os2
INNER JOIN OrganisationStructuresParents r ON r.parentid = os2.id
)
SELECT DISTINCT alias
FROM RotaResourceProfileRoleMapping rrprm
JOIN OrganisationStructureResourceProfile osrp ON osrp.ResourceProfileId = rrprm.RotaEntryResourceProfileId
JOIN ResourceProfile rp ON rp.Id = rrprm.ResourceProfileId
JOIN Resource r ON r.ResourceProfileId = rp.id 
JOIN OrganisationStructuresParents osp ON osrp.OrganisationStructureId = osp.id
WHERE rp.Active = 1
AND r.Active = 1 
AND rp.ActiveForClient = 1
AND osp.ParentId IS NULL
