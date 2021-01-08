package mercury.helpers.dbhelper;

import java.util.List;
import java.util.Map;

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
import mercury.helpers.DbUtilityHelper;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfigV2.class)
public class DbHelperFundingRequests {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;
    @Autowired private DbUtilityHelper dbUtilityHelper;

    public List<Map<String, Object>> getLatestInitialFundingRequestJob(String userName) {
        String sql = "SELECT TOP (1) CONVERT(VARCHAR(10), j.JobReference) [Job Ref], %testdb.ufn_StripSpaces(s.Name) Site, RTRIM(%testdb.ufn_StripSpaces(r.Name)) Resource, " +
                " %testdb.ufn_StripSpaces(REPLACE(REPLACE(f.Description, CHAR(13), ' '), CHAR(10), '')) Description, f.CreatedOn AS [Logged Date] " +
                " FROM Job j   " +
                " INNER JOIN ResourceAssignment ra ON j.id = ra.jobId " +
                " INNER JOIN FundingRequest f ON f.ResourceAssignmentId = ra.Id " +
                " INNER JOIN Resource ar ON f.InitialRequestApproverId = ar.Id " +
                " INNER JOIN ApplicationUser au ON au.ResourceId = ar.Id " +
                " INNER JOIN Site s ON s.id = j.SiteId " +
                " INNER JOIN Resource r ON ra.ResourceId = r.Id " +
                " INNER JOIN JobStatus js ON j.JobStatusId = js.Id " +
                " WHERE ra.ResourceAssignmentStatusId = 19 " +
                " AND f.StatusId = 1 " +
                " AND f.InitialFunding = 1 " +
                " AND js.IsTerminal = 0 " +
                " AND au.UserName = :userName " +
                " ORDER BY f.CreatedOn DESC";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getLatestInitialFundingRequestJob: " + sql.replaceAll(":userName", userName.toString()));
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userName", userName);

        try {
            return jdbc_helpdesk.queryForList(sql, params);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public int getFundingRouteId(String fundingRoute) {
        String sql = String.format("SELECT Id FROM FundingRoute WHERE Name = '%s'", fundingRoute);
        logger.debug("getFundingRouteId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

}
