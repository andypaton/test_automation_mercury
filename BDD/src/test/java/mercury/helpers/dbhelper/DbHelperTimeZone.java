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
import mercury.helpers.DbUtilityHelper;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DbConfigV2.class)
public class DbHelperTimeZone {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private DbUtilityHelper dbUtilityHelper;
    @Autowired private NamedParameterJdbcTemplate jdbc_portal;
    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;

    public boolean isHelpdeskOutOfHours() {
        String sql = "SELECT OutOfHours FROM uvw_IsHelpdeskOutHours";
        logger.debug("isHelpdeskOutOfHours: " + sql);
        int outOfHours = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        return outOfHours > 0;
    }

    public String getHeadOfficeTimezone() {
        String sql = " SELECT st.StandardDescription + ' (' + CAST(st.StandardOffsetMinutes / 60 AS VARCHAR(6)) + ' hours)' "
                + " FROM %portaldb.tblSetting ts INNER JOIN %portaldb.segTimezones st ON ts.fld_str_Value = st.TimeZoneID "
                + " WHERE ts.fld_str_Key = 'HEADOFFICE_TIME_ZONE'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();

        logger.debug("getHeadOfficeTimezone: " + sql);
        return jdbc_portal.queryForObject(sql, params, String.class);
    }

    public String getHeadOfficeTimeDifference() {
        String sql = "SELECT CAST(st.StandardOffsetMinutes / 60 AS VARCHAR(6)) " + " FROM %portaldb.tblSetting ts "
                + " INNER JOIN %portaldb.segTimezones st ON ts.fld_str_Value = st.TimeZoneID "
                + " WHERE ts.fld_str_Key = 'HEADOFFICE_TIME_ZONE'";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();

        logger.debug("getHeadOfficeTimeDifference: " + sql);
        try {
            return jdbc_portal.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            return "0";
        }

    }

    public String getTimeAtHeadOffice() {
        String sql = "DECLARE @offset int " + "SELECT @offset = st.DaylightOffsetMinutes FROM %portaldb.tblSetting ts "
                + " INNER JOIN %portaldb.segTimezones st ON ts.fld_str_Value = st.TimeZoneID "
                + " WHERE ts.fld_str_Key = 'HEADOFFICE_TIME_ZONE'; "
                + " SELECT dateadd(minute, @offset, getdate())";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();

        logger.debug("getTimeAtHeadOffice: " + sql);
        return jdbc_portal.queryForObject(sql, params, String.class);
    }

    public String getTimezone() {
        int offset = Integer.valueOf(getHeadOfficeTimeDifference());
        return ((offset < 0) ? "-" : "") + String.format("%02d:00", Math.abs(offset));
    }

    /**
     * Get site IanaCode for job reference
     * @param jobReference
     * @return
     */
    public String getIanaCodeForJobReference(int jobReference) {
        String sql = "SELECT itz.IanaCode FROM IanaTimezones itz " +
                " INNER JOIN Site s ON s.IanaTimezoneId = itz.Id " +
                " INNER JOIN Job j ON j.SiteId = s.Id " +
                " WHERE j.JobReference = %d";
        sql = String.format(sql, jobReference);
        logger.debug("getSiteIanaCodeForJobReference: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    /**
     * Get site IanaCode for PPM job reference
     * @param ppmJobReference
     * @return
     */
    public String getIanaCodeForPPMJobReference(int ppmJobReference) {
        String sql = "SELECT itz.IanaCode FROM %helpdeskdb.IanaTimezones itz " +
                " INNER JOIN %helpdeskdb.Site s ON s.IanaTimezoneId = itz.Id " +
                " INNER JOIN %portaldb.tblPPMScheduleLine psl ON psl.fld_int_StoreID = s.Id " +
                " WHERE psl.fld_int_ID = %d";
        sql = String.format(sql, ppmJobReference);
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getIanaCodeForPPMJobReference: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    /**
     * Get IanaCode for siteId
     * @param siteId
     * @return
     */
    public String getIanaCodeForSite(int siteId) {
        String sql = "SELECT IanaCode "
                + "FROM IANATimezones it "
                + "JOIN Site s ON s.IanaTimezoneId = it.Id WHERE s.Id = %d";
        sql = String.format(sql, siteId);
        logger.debug("getIanaCodeForSite: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    /**
     * Get IanaCode for timezoneId
     * @param siteId
     * @return
     */
    public String getIanaCodeForTimezoneId(int timezoneId) {
        String sql = "SELECT IanaCode FROM IANATimezones it WHERE Id = %d";
        sql = String.format(sql, timezoneId);
        logger.debug("getIanaCodeForTimezoneId: " + sql);
        return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
    }

    /**
     * Get IanaCode for a sites Home Office
     * @param siteId
     * @return
     */
    public String getIanaCodeForHomeOffice(int siteId) {
        String sql = "SELECT %helpdeskdb.ufn_GetServiceCentreTimezoneForSite(%d)";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        sql = String.format(sql, siteId);
        logger.debug("getIanaCodeForHomeOffice: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            return null;
        }
    }

}
