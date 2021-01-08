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

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfigV2.class)
public class DbHelperGlobalisation {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;


    public String getCultureInfoCode() {
        String sql = "SELECT CultureInfoCode FROM SystemConfig";
        logger.debug("getCultureInfoCode: " + sql);
        return jdbc_helpdesk.queryForObject(sql,  new MapSqlParameterSource(), String.class);
    }

    public List<Map<String, Object>> getSystemConfigDateTime() {
        String sql = "SELECT * FROM SystemConfigDateTime";
        logger.debug("getSystemConfigDateTime: " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getTerminology() {
        String sql = "SELECT * FROM Terminology WHERE CultureCode = (SELECT CultureInfoCode FROM SystemConfig ) AND Active = 1";
        logger.debug("getTerminology: " + sql);
        return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
    }

}