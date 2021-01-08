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
import mercury.databuilders.TestData;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DbConfigV2.class)
public class DbHelperOnCallScheduler {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private DbHelperTimeZone dbhelperTimeZone;
    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;
    @Autowired private TestData testData;

    public String getOnCallSchedulerOutOfHoursTimes() {
        String sql = "SELECT CONVERT(VARCHAR, StartTime) + '-' + CONVERT(VARCHAR, EndTime) FROM OutOfHourTimes WHERE DayOfTheWeek = DATEPART(DW, GETUTCDATE()) - 1";
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getOutOfHoursTimes: " + sql);
        return jdbc_helpdesk.queryForObject(sql, params, String.class);
    }

    public void resetOnCallScheduler() {
        updateOnCallSchedulerOutOfHours(1, "07:00:00.0000000", "08:00:00.0000000");
        updateOnCallSchedulerOutOfHours(2, "17:00:00.0000000", "08:00:00.0000000");
        updateOnCallSchedulerOutOfHours(3, "17:00:00.0000000", "08:00:00.0000000");
        updateOnCallSchedulerOutOfHours(4, "17:00:00.0000000", "08:00:00.0000000");
        updateOnCallSchedulerOutOfHours(5, "17:00:00.0000000", "08:00:00.0000000");
        updateOnCallSchedulerOutOfHours(6, "17:00:00.0000000", "08:00:00.0000000");
        updateOnCallSchedulerOutOfHours(7, "07:00:00.0000000", "07:00:00.0000000");
    }

    private void updateOnCallSchedulerOutOfHours(int id, String startTime, String endTime) {
        String sql = "UPDATE OutOfHourTimes SET StartTime = '%s', EndTime = '%s' WHERE Id = %d";
        sql = String.format(sql, startTime, endTime, id);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("updateOnCallSchedulerOutOfHours: " + sql);
        jdbc_helpdesk.update(sql, params);
    }

    public void updateOnCallSchedulerOutOfHours(String startTime, String endTime) {
        testData.put("resetOnCallScheduler", true);
        String sql = "UPDATE OutOfHourTimes SET StartTime = '%s', EndTime = '%s'";
        sql = String.format(sql, startTime, endTime);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("updateOnCallSchedulerOutOfHours: " + sql);
        jdbc_helpdesk.update(sql, params);
    }

    public boolean isOnCallSchedulerActive() {
        String timeAtHO = dbhelperTimeZone.getTimeAtHeadOffice();
        String sql
        = "DECLARE @dt TIME = '%s' " + " SELECT COUNT(*) FROM OutOfHourTimes "
                + " WHERE (DayOfTheWeek = DATEPART(DW, GETUTCDATE()) - 1 "
                + " AND startTime < @dt) "
                + " OR (DayOfTheWeek = DATEPART(DW, GETUTCDATE()) - 2 AND endTime > @dt)";
        sql = String.format(sql, timeAtHO.split(" ")[1]);
        logger.debug("isOnCallSchedulerInHours: " + sql);
        int active = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        return active > 0;
    }

    public String getStartDayOfTheWeek() {
        String sql = "SELECT DATENAME(WEEKDAY, DayOfTheWeek - 1) FROM OutOfHourTimes WHERE IsFirstDayOfWeek = 1";
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getStartDayOfTheWeek: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }

    public Integer getFirstDayOfTheWeek() {
        String sql = "SELECT DayOfTheWeek FROM OutOfHourTimes WHERE IsFirstDayOfWeek = 1";
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getFirstDayOfTheWeek: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> getOutOfHoursTimes() throws Exception {
        String sql = "SELECT DATENAME(WEEKDAY, DayOfTheWeek - 1) [Week Day], CONVERT(varchar(15),CAST(StartTime AS TIME),100) [Start Time]," + " CONVERT(varchar(15),CAST(EndTime AS TIME),100) [End Time (Next Day)] FROM OutOfHourTimes";

        logger.debug("getOutOfHoursTimes: " + sql);

        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

    public String getRandomDayOfTheWeekThatIsNotStartDay() {
        String sql = "SELECT TOP(1) DATENAME(WEEKDAY, DayOfTheWeek - 1) FROM OutOfHourTimes WHERE IsFirstDayOfWeek = 0 ORDER BY NEWID()";
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getDayOfTheWeekThatIsNotStartDay: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getIntegerValueOfWeekDay(String day) {
        String sql = "SELECT CHARINDEX(SUBSTRING('%s',1,3), 'SUNMONTUEWEDTHUFRISAT') / 3";
        sql = String.format(sql, day);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("getIntegerValueOfWeekDay: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> getStartTimeEndTimeForDay(int dayOfTheWeek) throws Exception {
        String sql = "SELECT CONVERT(varchar(15),CAST(StartTime AS TIME),100) [Start Time],  CONVERT(varchar(15),CAST(EndTime AS TIME),100) [End Time (Next Day)] FROM OutOfHourTimes WHERE DayOfTheWeek = %d";
        sql = String.format(sql, dayOfTheWeek);
        logger.debug("getStartTimeEndTimeForDay: " + sql);
        try {
            return jdbc_helpdesk.queryForMap(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            return null;
        }
    }

}
