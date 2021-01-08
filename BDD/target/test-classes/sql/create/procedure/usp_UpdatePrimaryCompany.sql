-- =============================================
-- Description:   Sets the primary company to the one provided.
-- Sets parentIDCompany to the primary in the client and business unit tables.
-- =============================================

CREATE PROCEDURE %testdb.usp_UpdatePrimaryCompany
                 @primaryCompanyName VARCHAR(MAX)
AS
BEGIN
    UPDATE %helpdeskdb.Company 
    SET IsPrimary = 1
    WHERE 
    (%helpdeskdb.Company.Name = @primaryCompanyName)
    
    UPDATE  %helpdeskdb.CompanyBusinessUnit
    SET ParentCompanyId = (SELECT id FROM  %helpdeskdb.Company c WHERE c.Name = @primaryCompanyName)

    UPDATE  [%helpdeskdb].CompanyClient
    SET ParentCompanyId = (SELECT id FROM  %helpdeskdb.Company c WHERE c.Name = @primaryCompanyName)

END