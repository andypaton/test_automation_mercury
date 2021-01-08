package mercury.helpers.dbhelper;

import java.util.Map;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mercury.database.config.DbConfigV2;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfigV2.class)
public class DbHelperUsers {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;


    /**
     * Return Contractor Admin (if he uses engineers), otherwise assigned resource
     * @param jobReference
     * @return
     * @throws Exception
     */
    public Map<String, Object> getLoginUserForJob(int jobReference) throws Exception {
        String sql = "SELECT TOP(1) j.Id AS JobId, j.JobReference, ra.ResourceId, r.Name AS ResourceName, au.UserName, au.Id AS ApplicationUserId, up.Name As UserProfileName, r.UsesEngineers, "
                + "CASE WHEN (up.Name = 'Contractor Admin' AND r.UsesEngineers = 1) THEN 1 "
                + "WHEN (up.Name = 'Contractor Admin' AND r.UsesEngineers = 0) THEN 3 "
                + "ELSE 2 "
                + "END rnk "
                + "FROM Job j "
                + "INNER JOIN ResourceAssignment ra ON ra.JobId = j.Id "
                + "INNER JOIN Resource r ON ra.ResourceId = r.Id "
                + "INNER JOIN ApplicationUser au ON au.ResourceId = ra.ResourceId "
                + "INNER JOIN UserProfile up ON au.UserProfileId = up.Id "
                + "WHERE au.Active = 1 AND ra.Active = 1 AND j.JobReference = %d "
                + "ORDER BY rnk ";
        sql = String.format(sql, jobReference);
        logger.debug("getLoginUserForJob: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public Map<String, Object> getContractorAdminForJob(int jobReference) throws Exception {
        String sql = "SELECT j.Id AS JobId, j.JobReference, ra.ResourceId, r.Name AS ResourceName, au.UserName, au.Id AS ApplicationUserId, up.Name As UserProfileName "
                + "FROM Job j "
                + "INNER JOIN ResourceAssignment ra ON ra.JobId = j.Id "
                + "INNER JOIN Resource r ON ra.ResourceId = r.Id "
                + "INNER JOIN ApplicationUser au ON au.ResourceId = ra.ResourceId "
                + "INNER JOIN UserProfile up ON au.UserProfileId = up.Id "
                + "WHERE au.Active = 1 AND ra.Active = 1 AND j.JobReference = %d "
                + "AND up.Name = 'Contractor Admin'";
        sql = String.format(sql, jobReference);
        logger.debug("getLoginUserForJob: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    public void resetPassword(String id, String passwordHash) {
        String sql = String.format("UPDATE ApplicationUser SET PasswordHash = '%s' WHERE Id = '%s'", passwordHash, id);
        logger.debug("resetPassword: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public Map<String, Object> getApplicationUserFromUserName(String userName){
        String sql = "SELECT * FROM ApplicationUser WHERE UserName = '%s'";
        sql = String.format(sql, userName);
        logger.debug("getApplicationUserFromUserName: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            return null;
        }
    }

    //Query to fetch compliance profile
    public List<String> getComplianceConfigResourceProfile(String userProfile) {
        String sql = "SELECT anr.Name " +
                "FROM UserProfile up " +
                "INNER JOIN Permission p ON up.Id = p.UserProfileId " +
                "INNER JOIN AspNetRoles anr ON p.AspNetRoleId = anr.Id " +
                "WHERE up.Name = '%s'";

        sql = String.format(sql, userProfile);
        logger.debug("getComplianceConfigResourceProfile: " + sql);
        return (jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource(), String.class));
    }
}