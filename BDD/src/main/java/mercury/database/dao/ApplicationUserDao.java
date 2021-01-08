package mercury.database.dao;

import static mercury.helpers.StringHelper.quote;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.ApplicationUser;
import mercury.databuilders.TestData;
import mercury.helpers.DbUtilityHelper;
import mercury.helpers.FileHelper;

@Repository
public class ApplicationUserDao {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private DbUtilityHelper dbUtilityHelper;
    @Autowired private TestData testData;

    private static final String GET_PORTAL_USER_WITH_OPEN_JOBS_SQL = "SELECT  TOP(1) au.* " +
            "FROM ApplicationUser au " +
            "WHERE  " +
            "au.ResourceId IN (SELECT ra.ResourceId " +
            "FROM Job j LEFT JOIN ResourceAssignment ra 	ON j.Id = ra.JobId " +
            "WHERE j.JobStatusId IN (2, 3, 7, 15) " +
            "AND ra.ResourceAssignmentStatusId NOT IN (4, 13, 15, 16) " +
            "GROUP BY ra.ResourceId	HAVING COUNT( ra.ResourceId)>0) " +
            "AND au.active = 1  " +
            "AND au.PasswordHash = 'ACf6bmCh1o2t1BxtF1U0BFi4RArKmKf5mmxubtBFcV0lakWIO+ay29uicgAEkc2DxA==' " +
            "AND au.UserProfileId = (SELECT Id FROM dbo.UserProfile WHERE Name = :profileName) " +
            "AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) " +
            "ORDER BY NEWID() ";

    //  Fix this query to be only 1 active resource
    private static final String GET_PORTAL_USER_WITH_OPEN_JOBS_ONLY_ASSIGNED_TO_ME_SQL = "SELECT  TOP(1) au.* " +
            "FROM ApplicationUser au " +
            "WHERE  " +
            "au.ResourceId IN ( SELECT DISTINCT srj.ResourceId " +
            "FROM Job j INNER JOIN " +
            "(SELECT ra.jobid, ra.ResourceId FROM  ResourceAssignment ra " +
            "WHERE ra.ResourceAssignmentStatusId NOT IN (4, 13, 15, 16) " +
            "GROUP BY ra.jobid, ra.ResourceId  HAVING COUNT( ra.ResourceId)=1)  srj ON j.id = srj.jobid  " +
            "WHERE j.JobStatusId IN (2, 3, 7, 15)) " +
            "AND au.active = 1  " +
            "AND au.PasswordHash = 'ACf6bmCh1o2t1BxtF1U0BFi4RArKmKf5mmxubtBFcV0lakWIO+ay29uicgAEkc2DxA==' " +
            "AND au.UserProfileId = (SELECT Id FROM dbo.UserProfile WHERE Name = :profileName) " +
            "AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) " +
            "ORDER BY NEWID() ";

    private static final String GET_PORTAL_USER_WITH_AWAITING_JOBS_SQL = "SELECT  TOP(1) au.* " +
            "FROM ApplicationUser au " +
            "WHERE  " +
            "au.ResourceId IN (SELECT ra.ResourceId " +
            "FROM Job j LEFT JOIN ResourceAssignment ra 	ON j.Id = ra.JobId " +
            "WHERE ra.ResourceAssignmentStatusId IN (1, 4, 5) " +
            "AND j.JobStatusId NOT IN (11) " +
            "GROUP BY ra.ResourceId	HAVING COUNT(ra.ResourceId)>0) " +
            "AND au.active = 1  " +
            "AND au.PasswordHash = 'ACf6bmCh1o2t1BxtF1U0BFi4RArKmKf5mmxubtBFcV0lakWIO+ay29uicgAEkc2DxA==' " +
            "AND au.UserProfileId = (SELECT Id FROM dbo.UserProfile WHERE Name = :profileName) " +
            "AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) " +
            "ORDER BY NEWID() ";

    private static final String GET_USER_WITH_PROFILE_SQL = "SELECT TOP(1) au.* "
            + "FROM ApplicationUser au "
            + "LEFT OUTER JOIN Resource r ON au.ResourceId = r.Id "
            + "WHERE au.Active = 1 "
            + "AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) "
            + "AND (r.Active = 1 OR r.active IS NULL) "
            + "AND au.UserProfileId = (SELECT Id FROM dbo.UserProfile WHERE Name = :profileName) "
            + "AND au.UserName NOT LIKE '%Donnelll%' "  // To be removed once bug MCP-20619 is fixed
            + "ORDER BY NEWID()";

    private static final String GET_PORTAL_USER_WITH_SUBMITTED_INVOICES_AND_CREDITS_SQL = "SELECT TOP(1) au.* "
            + "FROM ApplicationUser au "
            + "LEFT JOIN Resource r ON au.ResourceId = r.Id "
            + "WHERE au.ResourceId IN (SELECT r.Id FROM %portaldb.tblcfSupplierInvoiceHeader sih "
            + "LEFT JOIN %portaldb.vw_purchase_order po ON sih.fld_str_PO_PONumber = po.[PO No] "
            + "LEFT JOIN %portaldb.tblPO tpo ON sih.fld_str_PO_PONumber = tpo.fld_str_PONumber "
            + "INNER JOIN %helpdeskdb.Job j ON tpo.fld_int_HelpdeskFaultID = j.JobReference "
            + "INNER JOIN %portaldb.tblHelpDeskFault hdf ON tpo.fld_int_HelpdeskFaultID = hdf.fld_int_ID "
            + "INNER JOIN %portaldb.tblHelpDeskResource hdr ON tpo.fld_str_SupplierID = hdr.fld_str_SupplierID "
            + "INNER JOIN %helpdeskdb.Resource r ON hdr.fld_int_ID = r.EpochId) "
            + "AND au.Active = 1 "
            + "AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) "
            + "AND (r.Active = 1 OR r.active IS NULL) "
            + "AND au.UserProfileId = (SELECT Id FROM dbo.UserProfile WHERE Name = :profileName) "
            + "ORDER BY NEWID() ";

    private static final String GET_CONTRACTOR_SQL = "SELECT TOP(1) au.* "
            + "FROM ApplicationUser au "
            + "INNER JOIN Resource r ON au.ResourceId = r.Id "
            + "INNER JOIN AssetClassificationSiteContractorMapping acscm ON acscm.ResourceId = r.Id "
            + "WHERE au.Active = 1 "
            + "AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) "
            + "AND (r.Active = 1 OR r.active IS NULL) "
            + "AND au.UserProfileId = (SELECT Id FROM dbo.UserProfile WHERE Name = :profileName) "
            + "ORDER BY NEWID()";

    private static final String GET_USER_WITH_PROFILE_AND_PASSWORDEXPIRYDATE_SQL = "SELECT TOP(1) au.*" + " FROM ApplicationUser au LEFT OUTER JOIN Resource r"
            + " ON au.ResourceId = r.Id"
            + " WHERE au.Active = 1"
            + " AND r.Active = 1"
            + " AND au.UserProfileId = (SELECT Id FROM dbo.UserProfile WHERE Name = :profileName)"
            + " AND au.PasswordExpiryDate > GETDATE()"
            + " ORDER BY NEWID()";

    private static final String GET_AD_USER_WITH_PROFILE_SQL = "SELECT TOP(1) au.*" + " FROM ApplicationUser au LEFT OUTER JOIN Resource r"
            + " ON au.ResourceId = r.Id"
            + " INNER JOIN ResourceProfile rp ON rp.id = r.resourceprofileid"
            + " INNER JOIN ResourceType rt ON rt.id = rp.resourceTypeId"
            + " WHERE au.Active = 1"
            + " AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) "
            + " AND r.Active = 1"
            + " AND rt.AdLogin = 1"
            + " AND au.UserProfileId = (SELECT Id FROM dbo.UserProfile WHERE Name = :profileName)";

    private static final String GET_NON_AD_USER_WITH_FAILED_LOGIN_ATTEMPTS_SQL = "SELECT TOP(1) au.* FROM ApplicationUser au " + "INNER JOIN Resource r ON r.id = au.resourceId "
            + "INNER JOIN ResourceProfile rp ON r.resourceProfileId = rp.id "
            + "INNER JOIN ResourceType rt  ON rp.resourceTypeId = rt.Id "
            + "WHERE au.UserProfileId = (SELECT id FROM dbo.UserProfile WHERE name = :profileName) "
            + "AND rt.AdLogin = 0 "
            + "AND au.PasswordExpiryDate > GETDATE() "
            + "AND au.Active = 1";

    private static final String GET_PORTAL_USER_WITH_DECLINED_AWAITING_JOBS_SQL = "SELECT  TOP(1) au.* " +
            "FROM ApplicationUser au " +
            "WHERE  " +
            "au.ResourceId IN (  SELECT DISTINCT srj.ResourceId " +
            "FROM Job j INNER JOIN " +
            "(SELECT ra.jobid, ra.ResourceId FROM ResourceAssignment ra INNER JOIN ResourceAssignment ra_d ON ra.JobId = ra_d.JobId  " +
            "WHERE ra.ResourceAssignmentStatusId IN (1, 4, 5)  " +
            "AND ra_d.ResourceAssignmentStatusId IN (16) " +
            "GROUP BY ra.jobid, ra.ResourceId  HAVING COUNT(ra.ResourceId) = 1  AND COUNT(ra_d.ResourceId) = 1)  srj ON j.id = srj.jobid  " +
            "WHERE j.JobStatusId IN (1, 11) )  " +
            "AND au.active = 1  " +
            "AND au.PasswordHash = 'ACf6bmCh1o2t1BxtF1U0BFi4RArKmKf5mmxubtBFcV0lakWIO+ay29uicgAEkc2DxA==' " +
            "AND au.UserProfileId = (SELECT id FROM dbo.UserProfile WHERE name = :profileName)  " +
            "AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) " +
            "ORDER BY NEWID() ";

    private static final String GET_PORTAL_USER_WITH_QUOTES_AWAITING_APPROVAL_JOBS_SQL = "SELECT TOP(1) au.*  " +
            "FROM ApplicationUser au  " +
            "WHERE   " +
            "au.ResourceId IN (SELECT q.QuoteRequestApproverId  " +
            "FROM Job j LEFT JOIN Quote q 	ON j.Id = q.JobId  " +
            "INNER JOIN Resource r ON q.QuoteRequestApproverId = r.Id" +
            "AND j.JobStatusId IN (16)  " +
            "GROUP BY q.QuoteRequestApproverId	HAVING COUNT(q.QuoteRequestApproverId)>0)  " +
            "AND au.active = 1   			" +
            "AND au.PasswordHash = 'ACf6bmCh1o2t1BxtF1U0BFi4RArKmKf5mmxubtBFcV0lakWIO+ay29uicgAEkc2DxA=='  " +
            "AND au.UserProfileId = (SELECT id FROM dbo.UserProfile WHERE name = :profileName)  " +
            "AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) " +
            "ORDER BY NEWID() ";

    private static final String GET_PORTAL_USER_WITH_RESOURCE_TYPE = "SELECT TOP(1) au.* "
            + " FROM ApplicationUser au "
            + " INNER JOIN UserProfile up ON up.Id = au.UserProfileId "
            + " INNER JOIN Resource r ON au.ResourceId = r.Id "
            + " INNER JOIN ResourceProfile rp ON rp.Id = r.ResourceProfileId "
            + " INNER JOIN ResourceType rt ON rt.Id = rp.ResourceTypeId "
            + " INNER JOIN Portals p ON p.Id = up.DefaultPortal "
            + " WHERE au.Active = 1 AND up.Active = 1 AND r.Active = 1 "
            + " AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) "
            + " AND p.Id = 6 "; // Portal users

    private static final String GET_PORTAL_FINANCE_USER = "SELECT TOP(1) au.* "
            + "FROM organisationstructuremanager osm INNER JOIN ApplicationUser au ON osm.applicationuserid = au.id "
            + "INNER JOIN resource r ON au.resourceid = r.id "
            + "INNER JOIN resourceprofile rp ON r.resourceprofileid = rp.id "
            + "INNER JOIN InitialApproverRule iar ON rp.id = iar.resourceprofileid "
            + "INNER JOIN ApprovalType atype ON iar.approvaltypeid = atype.id "
            + "WHERE atype.name = 'Invoice' "
            + "AND au.Active = 1 "
            + "AND (au.PasswordExpiryDate > GETDATE() OR au.PasswordExpiryDate IS NULL) "
            + "AND rp.Name = :resourceProfile";

    private static String RANDOM_SQL = "SELECT TOP(1) * FROM (%s) RandomOrder ORDER BY NEWID()";

    @Autowired private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<ApplicationUser> getAll() {
        return entityManager.createQuery("from mercury.database.models.ApplicationUser").getResultList();
    }

    public ApplicationUser getByUsername(String userName) {
        String sql = "SELECT au.* FROM ApplicationUser au WHERE au.UserName = :userName";
        String queryStatement = String.format(sql, userName);
        entityManager.clear();
        Query query =  entityManager.createNativeQuery(queryStatement, ApplicationUser.class);
        try {
            query.setParameter("userName", userName);
            return (ApplicationUser) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public ApplicationUser getRandomByUsernames(String userNames) {
        String sql = "SELECT TOP(1) au.* FROM ApplicationUser au WHERE au.UserName IN (%s) ORDER BY NEWID()";
        String queryStatement = String.format(sql, userNames);
        entityManager.clear();
        Query query =  entityManager.createNativeQuery(queryStatement, ApplicationUser.class);
        try {
            return (ApplicationUser) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public ApplicationUser getByResourceId(int resourceId) {
        String sql = "SELECT au.* FROM ApplicationUser au WHERE au.ResourceId = " + resourceId;
        logger.debug("getByResourceId: " + sql);
        Query query =  entityManager.createNativeQuery(sql, ApplicationUser.class);
        try {
            return (ApplicationUser) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ApplicationUser entity who has jobs in the open state and profile of profileName
     * @param profileName
     * @return List<ApplicationUser>
     */
    public ApplicationUser getUserWithOpenJobs(String profileName) {
        String queryStatement = String.format(GET_PORTAL_USER_WITH_OPEN_JOBS_SQL, profileName);
        Query query =  entityManager.createNativeQuery(queryStatement, ApplicationUser.class);
        try {
            query.setParameter("profileName", profileName);
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            return appUser;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * Fetch ApplicationUser entity who has jobs in the open state and profile of profileName and the job is only assigned to me
     * @param  profileName
     * @return List<ApplicationUser>
     */
    public ApplicationUser getUserWithOpenJobsOnlyAssignedToMe(String profileName) {
        String queryStatement = String.format(GET_PORTAL_USER_WITH_OPEN_JOBS_ONLY_ASSIGNED_TO_ME_SQL, profileName);
        Query query =  entityManager.createNativeQuery(queryStatement, ApplicationUser.class);
        try {
            query.setParameter("profileName", profileName);
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            return appUser;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * Fetch ApplicationUser entity who has jobs in the awaiting state and profile of profileName
     * @param profileName
     * @return List<ApplicationUser>
     */
    public ApplicationUser getUserWithAwaitingJobs(String profileName) {
        String queryStatement = String.format(GET_PORTAL_USER_WITH_AWAITING_JOBS_SQL, profileName);
        Query query =  entityManager.createNativeQuery(queryStatement, ApplicationUser.class);
        try {
            query.setParameter("profileName", profileName);
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            return appUser;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ApplicationUser entity who has jobs in the awaiting state and profile of profileName
     * @param profileName
     * @return List<ApplicationUser>
     */
    public ApplicationUser getUserWithQuotesAwaitingApprovalJobs(String profileName) {
        String queryStatement = String.format(GET_PORTAL_USER_WITH_QUOTES_AWAITING_APPROVAL_JOBS_SQL, profileName);
        Query query =  entityManager.createNativeQuery(queryStatement, ApplicationUser.class);
        try {
            query.setParameter("profileName", profileName);
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            return appUser;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ApplicationUser entity with user profile
     * @param profileName
     * @return List<ApplicationUser>
     */
    public ApplicationUser getForUserProfile(String profileName) {
        String queryStatement = GET_USER_WITH_PROFILE_SQL;
        logger.debug("getForUserProfile: " + queryStatement.replace(":profileName", "'" + profileName + "'"));
        Query query =  entityManager.createNativeQuery(queryStatement, ApplicationUser.class);
        try {
            query.setParameter("profileName", profileName);
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            return appUser;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ApplicationUser entity for a Supplier with submitted invoices and credits
     * @param profileName
     * @return List<ApplicationUser>
     */
    public ApplicationUser getUserWithSubmittedInvoicesAndCredits(String profileName) {
        String queryStatement = GET_PORTAL_USER_WITH_SUBMITTED_INVOICES_AND_CREDITS_SQL;
        queryStatement = dbUtilityHelper.dbNameSchemaReplacement(queryStatement);
        logger.debug("getForUserProfile: " + queryStatement.replace(":profileName", "'" + profileName + "'"));
        Query query =  entityManager.createNativeQuery(queryStatement, ApplicationUser.class);
        try {
            query.setParameter("profileName", profileName);
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            testData.put("supplierName", (appUser.getFirstName() + " " + appUser.getLastName()).trim());
            return appUser;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ApplicationUser entity for a Contractor with user profile (eg. 'Contractor Admin', 'Contractor Technician')
     * @param profileName
     * @return List<ApplicationUser>
     * @throws Exception
     */
    public ApplicationUser getContractor(String profileName) throws Exception {
        //  String queryStatement = String.format(GET_CONTRACTOR_SQL, profileName);
        int profileId = profileName.equalsIgnoreCase("Contractor Technician") ? 131 : 115;
        String sql = FileHelper.getResourceFile("sql/select/", "GET_CONTRACTOR_WITH_CORRECT_ASSETCLASSIFICATION.sql");
        logger.debug("getContractor: " + sql.replace(":profileName", "'" + profileName + "'"));
        Query query =  entityManager.createNativeQuery(sql, ApplicationUser.class);
        try {
            query.setParameter("profileId", profileId);
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            return appUser;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ApplicationUser entity who has jobs in the awaiting state and profile of profileName TODO pass in the user profile
     * @param profileName
     * @return List<ApplicationUser>
     * @throws Exception
     */
    public ApplicationUser getHelpdeskOperatorUserInTeam(String profileName, String teamName) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_APPLICATION_USER_IN_TEAM.sql");
        sql = sql.replaceAll(":team", teamName).replaceAll(":profile", profileName);
        logger.debug(sql);
        Query query =  entityManager.createNativeQuery(sql, ApplicationUser.class);
        try {
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            return appUser;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ApplicationUser entity who has jobs in the awaiting state and profile of profileName
     * @param profileName
     * @return List<ApplicationUser>
     */
    public ApplicationUser getUserWithAwaitingJobsPreviouslyDeclined(String profileName) {
        String queryStatement = String.format(GET_PORTAL_USER_WITH_DECLINED_AWAITING_JOBS_SQL, profileName);
        Query query =  entityManager.createNativeQuery(queryStatement, ApplicationUser.class);
        try {
            query.setParameter("profileName", profileName);
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            return appUser;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ApplicationUser entity who has the requested role
     *
     * @param portal: default portal for user profile
     * @param role: AspNetRole
     * @return ApplicationUser
     * @throws Exception
     */
    public ApplicationUser getUserWithRole(String portal, String role) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_USER_WITH_ROLE.sql");
        sql = sql.replace(":roleName", "'" + role + "'");
        sql = portal == null ? sql : sql +" AND prtl.Name = " + "'" + portal + "' ";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(RANDOM_SQL, sql);
        logger.debug("getUserWithRole: " + sql);
        Query query = entityManager.createNativeQuery(sql, ApplicationUser.class);
        try {
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            return appUser;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ApplicationUser entity without the requested role
     *
     * @param profileName
     * @return List<ApplicationUser>
     * @throws Exception
     */
    public ApplicationUser getAdminUserWithoutRole(String role) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_USER_WITHOUT_ROLE.sql");
        sql = sql.replace(":role", role);
        logger.debug("getUserWithoutRole: " + sql);
        Query query = entityManager.createNativeQuery(sql, ApplicationUser.class);
        try {
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            return appUser;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ApplicationUser entity who has the requested profile and role
     *
     * @param userProfile: user profile
     * @param role: AspNetRole
     * @return ApplicationUser
     * @throws Exception
     */
    public ApplicationUser getUserWithProfileAndRole(String userProfile, String role) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_USER_WITH_ROLE.sql");
        sql = sql.replace(":roleName", "'" + role + "'");
        sql = userProfile == null ? sql : sql + " AND r.ResourceProfileId IN (SELECT Id FROM ResourceProfile WHERE Name = ':rp' OR Alias = ':rp') ".replaceAll(":rp", userProfile);
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(RANDOM_SQL, sql);
        logger.debug("getUserWithRole: " + sql);
        testData.put("sql", sql);
        Query query = entityManager.createNativeQuery(sql, ApplicationUser.class);
        try {
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            return appUser;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * @param profileName
     * @param resourceTypeName
     * @param roleName
     * @param useResourceTypeName
     * @return
     * @throws Exception
     */
    public ApplicationUser getUserWithResourceProfileAndRole(String profileName, String resourceTypeName, String roleName, Boolean useResourceTypeName) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_USER_WITH_PROFILE_AND_ROLE.sql");
        Query query = entityManager.createNativeQuery(sql, ApplicationUser.class);

        query.setParameter("profileName", profileName);
        query.setParameter("resourceTypeName", resourceTypeName);
        query.setParameter("roleName", roleName);
        query.setParameter("useResourceTypeName", useResourceTypeName);


        String val = useResourceTypeName ? "1" : "0";
        logger.debug("getUserWithResourceProfileAndRole: " + sql.replace(":profileName", quote(profileName)).replace(":resourceTypeName", quote(resourceTypeName)).replace(":roleName", quote(roleName)).replace(":useResourceTypeName", val));
        try {
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            return appUser;
        } catch (Exception e) {
            return null;
        }
    }


    public ApplicationUser getNonADUserWithFailedLoginAttempts(String profileName, Integer failCount) {
        String queryStatement = null;
        if (failCount != null) {
            queryStatement = String.format(GET_NON_AD_USER_WITH_FAILED_LOGIN_ATTEMPTS_SQL + " AND au.AccessFailedCount = :failcount ", profileName, failCount);
        } else {
            queryStatement = String.format(GET_NON_AD_USER_WITH_FAILED_LOGIN_ATTEMPTS_SQL, profileName);
        }
        logger.debug(queryStatement);
        Query query = entityManager.createNativeQuery(queryStatement, ApplicationUser.class);
        try {
            query.setParameter("profileName", profileName);
            if (failCount != null) {
                query.setParameter("failcount", failCount);
            }
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            return appUser;
        } catch (Exception e) {
            return null;
        }
    }

    public ApplicationUser getUserWithMatchingResourceAndPasswordExpiryDate(String profileName) {
        String queryStatement = String.format(GET_USER_WITH_PROFILE_AND_PASSWORDEXPIRYDATE_SQL, profileName);
        logger.debug(queryStatement);
        Query query = entityManager.createNativeQuery(queryStatement, ApplicationUser.class);
        try {
            query.setParameter("profileName", profileName);
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            return appUser;
        } catch (Exception e) {
            return null;
        }
    }

    public ApplicationUser getActiveDirectoryPortalUser(boolean isActiveDirectory) {
        String sql = "SELECT TOP(1) au.* "
                + "FROM ApplicationUser au "
                + "INNER JOIN UserProfile up ON up.Id = au.UserProfileId "
                + "INNER JOIN Resource r ON au.ResourceId = r.Id "
                + "INNER JOIN ResourceProfile rp ON rp.id = r.resourceprofileid "
                + "INNER JOIN ResourceType rt ON rt.id = rp.resourceTypeId "
                + "WHERE au.Active = 1 AND r.Active = 1 "
                + "AND au.PasswordExpiryDate > GETDATE() "
                + "AND up.DefaultPortal = 6 ";

        sql = isActiveDirectory ? sql + "AND rt.AdLogin = 1" : "AND rt.AdLogin = 0";

        logger.debug("getActiveDirectoryPortalUser: " + sql);
        Query query = entityManager.createNativeQuery(sql, ApplicationUser.class);
        try {
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            return appUser;
        } catch (Exception e) {
            return null;
        }
    }

    public ApplicationUser getActiveDirectoryUserWithMatchingResource(String profileName) {
        String queryStatement = String.format(GET_AD_USER_WITH_PROFILE_SQL, profileName);
        logger.debug("getActiveDirectoryUserWithMatchingResource: " + queryStatement);
        Query query = entityManager.createNativeQuery(queryStatement, ApplicationUser.class);
        try {
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            return appUser;
        } catch (Exception e) {
            return null;
        }
    }

    public ApplicationUser getPortalUserWithResourceType(String resourceType) {

        String sql = GET_PORTAL_USER_WITH_RESOURCE_TYPE;
        if ("Contractor Admin".equals(resourceType)) {
            sql = sql + " AND up.Name = '%s' ";
        } else if ("Directors".equals(resourceType)) {
            sql = sql + " AND up.Name LIKE '%Director%' ";
        } else {
            sql = sql + " AND (up.Name = '%s' OR rt.Name = '%s' OR rp.Name = '%s' OR rp.Alias = '%s') ";
        }
        sql = sql + " ORDER BY NEWID()";
        sql = sql.replaceAll("%s",  resourceType);

        logger.debug("getPortalUserWithResourceType: " + sql);
        Query query = entityManager.createNativeQuery(sql, ApplicationUser.class);
        try {
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            testData.put("userName", appUser.getUserName());
            return appUser;
        } catch (Exception e) {
            return null;
        }
    }

    public ApplicationUser getUserFromOrganisationStructure(String resourceProfile) {

        String queryStatement = String.format(GET_PORTAL_FINANCE_USER, resourceProfile);
        logger.debug("getUserFromOrganisationStructure: " + queryStatement.replace(":resourceProfile", "'" + resourceProfile + "'"));
        Query query = entityManager.createNativeQuery(queryStatement, ApplicationUser.class);
        try {
            query.setParameter("resourceProfile", resourceProfile);
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            return appUser;
        } catch (Exception e) {
            return null;
        }
    }

    public ApplicationUser getContractorAdmin(int resourceId) {
        String sql = "SELECT * FROM ApplicationUser WHERE UserProfileId = 115 AND ResourceId = %d";
        sql = String.format(sql, resourceId);

        logger.debug("getContractorAdmin:" + sql);
        Query query = entityManager.createNativeQuery(sql, ApplicationUser.class);
        try {
            ApplicationUser appUser = (ApplicationUser) query.getSingleResult();
            return appUser;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch ApplicationUser entity by primary key
     * @param id
     * @return
     */
    public ApplicationUser get(String id) {
        return entityManager.find(ApplicationUser.class, id);
    }

    /**
     * Create new ApplicationUser entity
     * @param c
     */
    public void create(ApplicationUser applicationUser) {
        entityManager.persist(applicationUser);
    }

    /**
     * Update existing ApplicationUser entity
     * @param applicaitonUser
     */
    public void update(ApplicationUser applicationUser) {
        entityManager.merge(applicationUser);
    }

    /**
     * Update existing contact entity
     * @param c
     */
    public void delete(ApplicationUser applicationUser) {
        entityManager.remove(applicationUser);
    }

}