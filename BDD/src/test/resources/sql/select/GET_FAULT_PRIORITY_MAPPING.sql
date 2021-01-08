SELECT TOP(1) fpm.*, at.Name AS AssetTypeName
FROM %testdb.uvw_Stores s
LEFT JOIN AssetClassificationSite acs ON s.Id=acs.SiteId
LEFT JOIN AssetClassificationSiteContractorMapping acscm ON acscm.AssetClassificationSiteId = acs.Id
LEFT JOIN uvw_faultprioritymappings fpm ON fpm.SiteTypeId = s.SiteTypeId AND fpm.AssetClassificationId = acs.AssetClassificationId
INNER JOIN FaultType ft ON fpm.FaultTypeId = ft.Id AND ft.AvailableForStoreSelfLoggedJobs = 1
INNER JOIN AssetSubType ast ON ast.Id = fpm.AssetSubTypeId
INNER JOIN AssetType at ON at.Id = ast.AssetTypeId
LEFT JOIN ResourceProfileCapability rpc ON
            (
                rpc.AssetClassificationId = fpm.AssetClassificationId 
            AND
                rpc.AssetSubTypeId = fpm.AssetSubTypeId
        /*    AND
                rpc.AssetTypeId = fpm.AssetTypeId */
            OR
                (rpc.AssetClassificationId IS NULL AND rpc.AssetSubTypeId IS NULL AND rpc.AssetTypeId IS NULL)
            )
            AND
            (
                rpc.FaultTypeId IS NULL
                OR
                rpc.FaultTypeId = ft.Id
            )
            AND
            (
                rpc.SiteTypeId = s.SiteTypeId
            )
WHERE fpm.priority IN (%s)