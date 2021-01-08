WITH Candidates AS (
    SELECT hdft.fld_int_HelpDeskFaultID AS HelpDeskFaultID, hdft.fld_int_ID AS HelpDeskFaultTimeId, aps.fld_str_Name AS ApprovalStatus, ROW_NUMBER() OVER (ORDER BY hdft.fld_int_HelpDeskFaultID DESC) AS RowNum
    FROM %portaldb.tblHelpDeskFault hdf
    INNER JOIN %portaldb.tblHelpDeskFaultTime hdft ON hdf.fld_int_ID = hdft.fld_int_HelpDeskFaultID 
    INNER JOIN %portaldb.tblHelpDeskResource hdr ON hdft.fld_int_HelpDeskResourceID = hdr.fld_int_ID
    INNER JOIN %portaldb.tblProjectHeader ph ON hdft.fld_int_ID = ph.fld_int_InvitationToQuoteID
    LEFT JOIN %portaldb.tblHelpDeskResourceType hdrt ON hdr.fld_int_HelpDeskResourceTypeID = hdrt.fld_int_ID
    LEFT JOIN %portaldb.tblQuoteApprovalScenario qas ON ph.fld_int_ID = qas.fld_int_ProjectHeaderID                           
    LEFT JOIN %portaldb.tblApprovalStatus aps ON qas.fld_int_ApprovalStatusID = aps.fld_int_ID
    WHERE hdf.fld_bit_QuoteJob = 1 
    AND hdf.fld_int_HelpDeskFaultStageID != 6 
    AND fld_int_HelpDeskTimeStampTypeID = 27 
    AND hdft.fld_bit_IsCancelled = 0 
    AND hdft.fld_bit_declined != 1
    AND aps.fld_str_Name = '%s'
)
SELECT * FROM Candidates WHERE RowNum > %d
