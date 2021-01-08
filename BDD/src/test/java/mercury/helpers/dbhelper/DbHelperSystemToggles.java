package mercury.helpers.dbhelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mercury.database.config.DbConfigV2;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfigV2.class)
public class DbHelperSystemToggles {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;
    /**
     *
     * @param feature
     * @return will return -1 for any exception, eg. table / row does not exist
     */
    public int getSystemFeatureToggle(String feature) {
        feature = feature.replace(" ", "%");
        String sql = String.format("SELECT Active FROM SystemFeatureToggle WHERE Feature LIKE '%s' ", feature);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getSystemFeatureToggle: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        }
        catch(Exception e) {
            return -1;
        }
    }

    public int getSystemSubFeatureToggle(String feature) {
        feature = feature.replace(" ", "%");
        String sql = String.format("SELECT Active FROM SystemSubFeatureToggle WHERE SubFeature LIKE '%s' ", feature);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getSystemFeatureToggle: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        }
        catch(Exception e) {
            return -1;
        }
    }

    public List<String> getSystemSubFeatureToggleNames(String feature) {
        String sql = String.format("SELECT sf.SubFeature from SystemSubFeatureToggle sf JOIN SystemFeatureToggle f ON sf.FeatureId = f.Id WHERE f.Feature = '%s'", feature);

        logger.debug("getSystemSubFeatureToggleNames: " + sql);

        List<String> subFeatures = jdbc_helpdesk.query(sql, new MapSqlParameterSource(), new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("SubFeature"));
            }
        });

        return subFeatures;
    }

}
