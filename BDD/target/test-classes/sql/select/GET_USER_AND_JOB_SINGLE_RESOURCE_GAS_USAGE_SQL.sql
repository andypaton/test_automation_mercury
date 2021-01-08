WITH GasRequiredToReturn AS (
SELECT TOP(1) sv.Id, svgd.IsRequiredToReturn
FROM Job j INNER JOIN ResourceAssignment ra ON j.Id = ra.JobId  
INNER JOIN SiteVisits sv ON ra.id = sv.resourceassignmentid
INNER JOIN SiteVisitGasDetails svgd ON sv.Id = svgd.SiteVisitId
ORDER BY svgd.SiteVisitId desc
), LeakSiteRequiredToReturn AS (
SELECT sv.Id, CAST(MAX(CAST(svglsc.IsRequiredToReturn AS INT)) AS BIT) IsRequiredToReturn
FROM Job j INNER JOIN ResourceAssignment ra ON j.Id = ra.JobId  
INNER JOIN SiteVisits sv ON ra.id = sv.resourceassignmentid
INNER JOIN SiteVisitGasLeakSiteCheck svglsc ON sv.Id = svglsc.SiteVisitId
WHERE svglsc.IsRequiredToReturn = 1 and SubsequentGasLeakSiteCheckUuid IS NULL
GROUP BY sv.Id
)
SELECT TOP(1) NEWID() Id, ra.ResourceId, j.JobReference, au.UserName, s.Id SiteId
FROM Job j INNER JOIN ResourceAssignment ra ON j.Id = ra.JobId   
    INNER JOIN ApplicationUser au ON au.ResourceId = ra.ResourceId 
    INNER JOIN Permission p ON au.UserProfileId = p.UserProfileId
    INNER JOIN AspNetRoles anr ON p.AspNetRoleId = anr.Id 
    INNER JOIN AssetClassification ac ON j.AssetClassificationId = ac.Id  
    INNER JOIN AssetSubType ast ON ac.AssetSubTypeId = ast.Id 
    INNER JOIN AssetType at ON at.id = ast.AssetTypeID
    INNER JOIN ResourceAssignmentStatus ras ON ra.ResourceAssignmentStatusId = ras.id 
    INNER JOIN JobStatus js ON j.JobStatusId = js.id
    INNER JOIN %testdb.uvw_JobsWithSingleActiveResource jwsar ON jwsar.Jobid = j.Id
    INNER JOIN %helpdeskdb.Site s ON s.id = j.SiteId
    LEFT JOIN SiteVisits sv ON ra.id = sv.resourceassignmentid
    LEFT JOIN SiteVisitGasDetails svgd ON sv.Id = svgd.SiteVisitId
    LEFT JOIN SiteVisitCylinderDetails svcd ON svgd.SiteVisitId = svcd.SiteVisitGasDetailsId
    LEFT JOIN SiteVisitGasLeakSiteCheck svglsc ON svglsc.SiteVisitId = sv.Id
WHERE 
    anr.name IN ('Portal_Tech_iPad_Access', 'Portal_Contractor_Section_Access')
    AND ras.Name = :resourceAssignmentStatus  
    AND js.Name = :jobStatus  
    AND au.active = 1 
    AND at.UsesGas = 1
    --AND ast.IsAssetPlant =  CAST(:inPlant AS bit)
    AND j.CreatedOn < DATEADD(day,-1,GETDATE())
    AND ( ( 
            CAST(:useResourceTypeName AS bit)  = 0 
            AND au.UserProfileId = (SELECT id FROM dbo.UserProfile WHERE name = :profileName)
           )
          OR
          ( 
            CAST(:useResourceTypeName AS bit) = 1 
            AND au.ResourceId IN (SELECT r.Id FROM Resource r INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id INNER JOIN ResourceType rt ON rp.ResourceTypeId = rt.id where  rp.JobCallouts = 1 AND rp.Active = 1 AND rt.Name = :resourceTypeName) 
          )
        )
    --AND j.ReasonNotLoggedAgainstAssetId IS NULL  
    AND ( ( 
            CAST(:gasUsage AS bit)  = 1 
            AND svcd.id IS NOT NULL
            AND svcd.CreatedOn > DATEADD(day,-2,GETDATE())
           )
          OR
          ( 
           CAST(:gasUsage AS bit)  = 0 
            AND svcd.id IS NULL
          )
        )
   AND ( ( 
            CAST(:leakCheck AS bit)  = 1 
            AND svglsc.id IS NOT NULL
            AND svglsc.CreatedOn > DATEADD(day,-2,GETDATE())
           )
          OR
          ( 
           CAST(:leakCheck AS bit)  = 0 
            AND svglsc.id IS NULL
          )
        ) 
 GROUP BY ra.ResourceId, j.JobReference, au.UserName, s.Id    
 /*HAVING COUNT(ra.ResourceId) = 1*/  
 ORDER BY NEWID()