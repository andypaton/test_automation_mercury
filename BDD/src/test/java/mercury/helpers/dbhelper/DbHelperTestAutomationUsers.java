package mercury.helpers.dbhelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import mercury.helpers.FileHelper;
import mercury.runtime.RuntimeState;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfigV2.class)
public class DbHelperTestAutomationUsers {

    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;

    private static final Logger logger = LogManager.getLogger();

    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;

    private Properties initializeUserProperties(String propertiesFileName) {
        try {
            Properties props = new Properties();
            ClassLoader classLoader = FileHelper.class.getClassLoader();
            URI uri  = classLoader.getResource(propertiesFileName).toURI();
            InputStream stream = Files.newInputStream(Paths.get(uri));
            props.load(stream);

            return props;

        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> getITUsers() throws Exception {
        // add the logged in user to the top of the list
        List<Map<String, Object>> result = new ArrayList<>();
        Properties props = initializeUserProperties("automation_users.properties");
        String alias = props.getProperty(runtimeState.domainUser.toLowerCase());
        Map<String, Object> domainUser = getITUser(runtimeState.domainUser, alias);
        if (domainUser != null) result.add(domainUser);

        // now add all other users
        String sql = FileHelper.getResourceFile("sql/select/", "GET_IT_USERS.sql");
        logger.debug("getITUsers: " + sql);
        try {
            List<Map<String, Object>> allITUsers = jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
            result.addAll(allITUsers);
            return result;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            testData.put("sql", sql);
            return null;
        }
    }

    public Map<String, Object> getITUser(String username, String alias) throws Exception {
        String sql = FileHelper.getResourceFile("sql/select/", "GET_IT_USERS.sql");
        sql = sql.replace("ORDER BY NEWID()", String.format("AND (UserName = '%s' OR UserName = '%s')", username, alias));
        logger.debug("getITUser: " + sql);
        try {
            return jdbc_helpdesk.queryForMap(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getITUser() throws Exception {
        try {
            Properties props = initializeUserProperties("automation_users.properties");
            String alias = props.getProperty(runtimeState.domainUser.toLowerCase());
            return getITUser(runtimeState.domainUser, alias);

        } catch (Exception e) {
            try {
                return getITUsers().get(0);

            } catch (Exception e2) {
                return null;
            }
        }
    }

    public void updatePasswordExpiryDate(String userName) throws Exception {
        String sql = String.format("UPDATE ApplicationUser SET PasswordExpiryDate = '2025-06-23 15:08' WHERE UserName = '%s'", userName); // any future date
        logger.debug("updatePasswordExpiryDate: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

}
