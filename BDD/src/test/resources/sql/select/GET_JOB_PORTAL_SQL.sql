 SELECT PPMID Id, PPMID JobReference, PPMType Description, 3 JobTypeId,    
    IIF(PPMTypeClassification = 'Compliance', 'PPM', PPMTypeClassification) JobTypeName, ppmsl.fld_int_StoreID SiteId,    
    NULL LocationId, NULL LocationName, NULL SubLocationName, 
    NULL AssetClassificationId, StoreName Name,    
    s.SiteCode SiteCode, NULL AssetSubTypeName, NULL AssetName, NULL AssetClassificationName, NULL FaultPriorityId,    
    'N/A' FaultPriority, NULL FailtTypeId, 'N/A' FaultType, NULL ResourceAssignmentStatusId,    
    ppmsa.CalloutStatus ResourceAssignmentStatusName, NULL contractorReference,    
    NULL JobStatusId, ppmsl.fld_dat_ProjectCreatedDate CreatedOn,    
    CASE WHEN PPMRCE.fld_dat_ContractorETADateTime IS NOT NULL    
         THEN PPMRCE.fld_dat_ContractorETADateTime   
         ELSE PPMSL.fld_dat_WeekEnding
    END AS ETAFrom,    
    ppmsl.fld_dat_WeekEnding ETATo,    
    NULL ETAWindowId    
FROM %portaldb.uvw_report_PPMScheduleAll_ART_Active ppmsa    
LEFT JOIN %portaldb.tblPPMScheduleLine ppmsl ON ppmsa.PPMID = ppmsl.fld_int_ID    
INNER JOIN %portaldb.tblProjectHeader AS PH ON ppmsl.fld_int_ID = PH.fld_int_PPMScheduleRef    
INNER JOIN %portaldb.tblChildProject AS CP ON PH.fld_int_ID = CP.fld_int_ProjectHeaderID    
INNER JOIN %portaldb.tblPPMResourceCalloutEvent AS PPMRCE ON CP.fld_int_ID = PPMRCE.fld_int_ChildProjectID 
INNER JOIN %helpdeskdb.Site s ON ppmsl.fld_int_StoreID = s.Id
LEFT JOIN %portaldb.tblHelpDeskResource hdr ON ppmsa.ResourceName = hdr.fld_str_Name    
WHERE COALESCE(hdr.fld_str_NTLogon, hdr.fld_str_SupplierId) LIKE :Username
AND ppmsa.CalloutStatus ! = 'Completed'
AND PH.fld_int_ProjectStatusID NOT IN (5,6,7)