package mercury.helpers.dbhelper;

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
import mercury.databuilders.TestData;
import mercury.helpers.DbUtilityHelper;
import mercury.runtime.RuntimeState;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfigV2.class)
public class DbHelperTestData {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private NamedParameterJdbcTemplate jdbc_test;
    @Autowired private DbUtilityHelper dbUtilityHelper;
    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;


    public void saveTestData() {
        try {
            String scenarioId = runtimeState.scenario.getId();
            String scenarioName = runtimeState.scenario.getName();

            boolean auditRowExists = getTestData(scenarioId, scenarioName) != null;

            if ( runtimeState.scenario.isFailed() ) {
                // just record DateFailed
                if (auditRowExists) {
                    updateFailed(scenarioId, scenarioName);
                } else {
                    insertFailed(scenarioId, scenarioName);
                }

            } else {
                // record TestData and DatePassed
                if (auditRowExists) {
                    updatePassed(scenarioId, scenarioName, testData.toString());
                } else {
                    insertPassed(scenarioId, scenarioName, testData.toString());
                }
            }
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * If there exists TestData from a previous run then get it
     * @throws ParseException
     */
    public Map<String, Object> getTestData() throws Exception {
        String scenarioId = runtimeState.scenario.getId();
        String scenarioName = runtimeState.scenario.getName();
        return getTestData(scenarioId, scenarioName);
    }

    private Map<String, Object> getTestData(String scenarioId, String scenarioName) {
        String sql = "SELECT TOP(1) * FROM %testdb.TestData WHERE ScenarioId = '%s' AND ScenarioName = '%s'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, scenarioId, scenarioName);

        logger.debug("getTestData: " + sql);
        try {
            return jdbc_test.queryForMap(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    private void insertPassed(String scenarioId, String scenarioName, String testData) {
        String sql = "INSERT INTO %testdb.TestData (ScenarioId, ScenarioName, TestData, Created, DatePassed) VALUES('%s', '%s', '%s', GETDATE(), GETDATE())";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, scenarioId, scenarioName, testData);

        logger.debug("insertPassed: " + sql);
        try {
            jdbc_test.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }

    private void insertFailed(String scenarioId, String scenarioName) {
        String sql = "INSERT INTO %testdb.TestData (ScenarioId, ScenarioName, FailedTestData, Created, DateFailed) VALUES('%s', '%s', '%s', GETDATE(), GETDATE())";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, scenarioId, scenarioName, testData);

        logger.debug("insertFailed: " + sql);
        try {
            jdbc_test.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }

    private void updatePassed(String scenarioId, String scenarioName, String testData) {
        String sql = "UPDATE %testdb.TestData SET TestData = '%s', Created = GETDATE(), DatePassed = GETDATE() WHERE ScenarioId = '%s' AND ScenarioName = '%s'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, testData, scenarioId, scenarioName);

        logger.debug("updatePassed: " + sql);
        try {
            jdbc_test.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }

    private void updateFailed(String scenarioId, String scenarioName) {
        String sql = "UPDATE %testdb.TestData SET FailedTestData = '%s', Created = GETDATE(), DateFailed = GETDATE() WHERE ScenarioId = '%s' AND ScenarioName = '%s'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, testData, scenarioId, scenarioName);

        logger.debug("updateFailed: " + sql);
        try {
            jdbc_test.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }

    public void delete(String scenarioId, String scenarioName) {
        String sql = "DELETE %testdb.TestData WHERE ScenarioId = '%s' AND ScenarioName = '%s'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, scenarioId, scenarioName);

        logger.debug("delete: " + sql);
        try {
            jdbc_test.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }

}