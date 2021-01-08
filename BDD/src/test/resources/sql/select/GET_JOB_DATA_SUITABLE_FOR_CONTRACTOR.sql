WITH RESOURCES AS (
    SELECT r.Name ResourceName, r.Id ResourceId, rp.Name ResourceProfileName, rt.Name ResourceTypeName, up.Name UserProfileName, r.UsesEngineers
    FROM Resource r 
      INNER JOIN ResourceProfile rp ON r.ResourceProfileId = rp.Id 
      INNER JOIN ResourceType rt ON rp.ResourceTypeId = rt.Id 
      INNER JOIN ApplicationUser au ON au.ResourceId = r.id 
      INNER JOIN UserProfile up ON up.Id = au.UserProfileId 
      INNER JOIN %portaldb.tblHelpDeskResource hdr ON r.EpochId = hdr.fld_int_ID 
      INNER JOIN %portaldb.tblPriceBook pb ON pb.fld_int_resourceid = hdr.fld_int_Id 
    WHERE up.Active = 1 AND r.Active = 1 AND au.Active = 1 
      AND pb.fld_str_PartCode = 'Standard Callout' 
      AND pb.fld_cur_CostEach > 0 
      AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) 
      AND rt.Name = 'Contractor'
      AND au.PasswordHash = 'ACf6bmCh1o2t1BxtF1U0BFi4RArKmKf5mmxubtBFcV0lakWIO+ay29uicgAEkc2DxA=='
), CONTRACTOR_STORES AS (
    SELECT DISTINCT s.Name, s.Id, s.SiteTypeId, acscm.ResourceId
    FROM Site s 
      RIGHT JOIN AssetClassificationSite acs ON acs.SiteId = s.Id  
      LEFT JOIN AssetClassificationSiteContractorMapping acscm ON acscm.AssetClassificationSiteId = acs.Id 
    WHERE acscm.ResourceId IS NOT NULL 
    AND s.SiteStatusId = 1
    AND s.Active = 1
), FPM AS (
    SELECT fpm1.* , at.Name AS AssetTypeName 
    FROM uvw_faultprioritymappings fpm1 
    INNER JOIN FaultPriorityMapping fpm ON fpm.Id = fpm1.Id 
    INNER JOIN JobRoutingProcess jp ON jp.Id = fpm.JobRoutingProcessId 
      LEFT JOIN (SELECT * FROM FaultType WHERE AvailableForStoreSelfLoggedJobs = 1) ft ON fpm1.FaultTypeId = ft.Id 
      LEFT JOIN AssetSubType ast ON ast.Id = fpm1.AssetSubTypeId 
      INNER JOIN AssetType at ON at.Id = ast.AssetTypeId 
      LEFT JOIN AssetClassification ac ON fpm1.AssetClassificationId = ac.id 
    WHERE jp.NonDeferrable = 1 AND fpm1.Priority != 0 AND jp.RequiresReview = 0 
) 
SELECT TOP(1) s.Name AS SiteName, s.Id AS SiteId, s.SiteTypeId, r.ResourceName, s.ResourceId, r.ResourceProfileName, r.ResourceTypeName, r.UserProfileName, r.UsesEngineers, fpm.AssetClassificationId, fpm.AssetClassificationName, fpm.AssetTypeName, fpm.AssetSubTypeName, fpm.ResponsePriorityId, fpm.FaultTypeId, fpm.FaultTypeName, l.Name AS LocationName, l.Id AS LocationId 
FROM CONTRACTOR_STORES s 
  INNER JOIN RESOURCES r ON r.ResourceId = s.ResourceId
  LEFT JOIN RESOURCES rb ON r.ResourceId = rb.ResourceId
  INNER JOIN FPM fpm ON fpm.SiteTypeId = s.SiteTypeId 
  INNER JOIN SiteTypeLocationMapping stlm ON stlm.SiteTypeId = s.SiteTypeId 
  INNER JOIN Location l ON stlm.LocationId = l.id 
  INNER JOIN Asset a ON fpm.AssetClassificationId = a.AssetClassificationId AND a.SiteId = s.Id
WHERE 
    (rb.ResourceId IS NULL AND r.UserProfileName = 'Contractor Technician')
    OR
    (rb.UserProfileName = 'Contractor Technician' AND r.UsesEngineers = 1)
    OR 
    (r.UserProfileName = 'Contractor Admin' AND r.UsesEngineers = 0)
    AND r.ResourceName != 'Southern Tank & Pump'  -- User is causing page timeout errors in Advocate. Remove this once bug MCP-21358 is fixed