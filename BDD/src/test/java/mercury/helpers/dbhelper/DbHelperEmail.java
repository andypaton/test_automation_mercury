package mercury.helpers.dbhelper;

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
@ContextConfiguration(classes = DbConfigV2.class)
public class DbHelperEmail {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private NamedParameterJdbcTemplate jdbc_communicator;

    public String getCommunicatorEmail(String subject, Integer jobReference, String dateSent) {
        String sql = "SELECT TOP(1) Body FROM EmailAuditLog WHERE Subject LIKE '%" + subject + "' AND Body LIKE '%' + CAST(" + jobReference + " AS NVARCHAR(15)) + '%' AND DateSent > '" + dateSent + "'"
                + " AND Id > (SELECT MAX(Id) FROM EmailAuditLog) - 500"; // table is indexed by Id - so this improves performance

        try {
            logger.debug("getCommunicatorEmail: " + sql);
            String result = jdbc_communicator.queryForObject(sql, new MapSqlParameterSource(), String.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getCommunicatorEmail(String subject, String dateSent) {
        String sql = "SELECT TOP(1) Body FROM EmailAuditLog WHERE Subject LIKE '%" + subject +  "%' AND DateSent > '" + dateSent + "'"
                + " AND Id > (SELECT MAX(Id) FROM EmailAuditLog) - 500"; // table is indexed by Id - so this improves performance

        try {
            logger.debug("getCommunicatorEmail: " + sql);
            String result = jdbc_communicator.queryForObject(sql, new MapSqlParameterSource(), String.class);
            logger.debug("result: " + result);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

}
