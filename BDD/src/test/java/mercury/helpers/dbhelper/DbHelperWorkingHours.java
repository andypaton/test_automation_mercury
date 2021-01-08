package mercury.helpers.dbhelper;

import static mercury.helpers.Constants.AUTOMATION_USER;
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
import mercury.database.dao.BrandWorkingHoursDao;
import mercury.database.dao.DeferralWorkingHoursDao;
import mercury.database.dao.ResourceWorkingHoursDao;
import mercury.database.models.BrandWorkingHours;
import mercury.database.models.DeferralWorkingHours;
import mercury.database.models.ResourceWorkingHours;
import mercury.databuilders.TestData;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfigV2.class)
public class DbHelperWorkingHours {

    @Autowired private BrandWorkingHoursDao brandWorkingHoursDao;
    @Autowired private DeferralWorkingHoursDao deferralWorkingHoursDao;
    @Autowired private ResourceWorkingHoursDao resourceWorkingHoursDao;
    @Autowired private TestData testData;
    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;

    private static final Logger logger = LogManager.getLogger();


    public void addResourceWorkingHours(Integer resourceId, String startAt, String endAt) throws Exception {

        String sql = "INSERT INTO ResourceWorkingHours (ResourceId, StartDayOfTheWeek, EndDayOfTheWeek, StartAt, EndAt, CreatedOn, CreatedBy, UpdatedOn, UpdatedBy)"
                + " VALUES (%d, Datepart(dw, Getdate()), Datepart(dw, Getdate()), '%s', '%s', SYSDATETIMEOFFSET(), '%s', SYSDATETIMEOFFSET(), '%s')";

        sql = String.format(sql, resourceId, startAt, endAt, AUTOMATION_USER, AUTOMATION_USER);

        MapSqlParameterSource params = new MapSqlParameterSource();

        logger.debug("addResourceWorkingHours: " + sql);

        int rows = jdbc_helpdesk.update(sql, params);
        if (rows == 0) {
            throw new Exception("row not updated");
        }
    }

    public void updateBrandWorkingHours(Integer bwhId, String startAt, String endAt) throws Exception {

        saveOriginalBrandWorkingHours(bwhId);

        String sql = "update BrandWorkingHours " + " set StartAt = :startAt " + "   , EndAt   = :endAt  where id = :bwhId ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("bwhId", bwhId);
        params.addValue("startAt", startAt);
        params.addValue("endAt", endAt);

        logger.debug("updateBrandWorkingHours: " + sql.replace(":bwhId", String.valueOf(bwhId)).replace(":startAt", "'" + startAt + "'").replace(":endAt", "'" + endAt + "'"));

        int rows = jdbc_helpdesk.update(sql, params);
        if (rows == 0) {
            throw new Exception("row not updated");
        }
    }

    public void updateBrandWorkingHoursForSite(Integer siteId, String startAt, String endAt) throws Exception {
        BrandWorkingHours bwh = brandWorkingHoursDao.getBrandWorkingHoursForSiteId(siteId);
        updateBrandWorkingHours(bwh.getId(), startAt, endAt);
    }

    public void updateDeferralWorkingHours(Integer dwhId, String startAt, String endAt) throws Exception {

        String sql = "update DeferralWorkingHours " + " set StartAt = :startAt " + "   , EndAt   = :endAt  where id = :dwhId ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("dwhId", dwhId);
        params.addValue("startAt", startAt);
        params.addValue("endAt", endAt);

        logger.debug("updateDeferralWorkingHours: " + sql.replace(":dwhId", String.valueOf(dwhId)).replace(":startAt", "'" + startAt + "'").replace(":endAt", "'" + endAt + "'"));

        int rows = jdbc_helpdesk.update(sql, params);
        if (rows == 0) {
            throw new Exception("row not updated");
        }
    }

    public void updateDeferralWorkingHoursForSite(Integer siteId, String startAt, String endAt) throws Exception {
        DeferralWorkingHours dwh = deferralWorkingHoursDao.getDeferralWorkingHoursForSiteId(siteId);
        if (testData.getInt("dwhId") == null) {
            testData.put("dwhId", dwh.getId());
            testData.put("dwhStartAt", dwh.getStartAt());
            testData.put("dwhEndAt", dwh.getEndAt());
        }
        updateDeferralWorkingHours(dwh.getId(), startAt, endAt);
    }



    public void deleteResourceWorkingHoursCreatedByTestAutomation() {
        String sql = String.format("DELETE resourceWorkingHours WHERE CreatedBy = '%s' AND CreatedOn < DATEADD(MINUTE, -30, GETUTCDATE())", AUTOMATION_USER);
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("deleteResourceWorkingHours: " + sql);
        jdbc_helpdesk.update(sql, params);
    }

    public void updateResourceWorkingHours(Integer resourceId, String startAt, String endAt) throws Exception {

        ResourceWorkingHours rwh = resourceWorkingHoursDao.getResourceWorkingHoursForToday(resourceId);

        if (rwh == null) {
            addResourceWorkingHours(resourceId, startAt, endAt);
            return;
        }

        saveOriginalResourceWorkingHours(rwh.getId());

        String sql = "update ResourceWorkingHours " + " set StartAt = :startAt " + "   , EndAt   = :endAt "
                + "   , UpdatedBy   = '%s' " + "   , UpdatedOn = SYSDATETIMEOFFSET() "
                + " where Id = %d ";

        sql = String.format(sql, AUTOMATION_USER, rwh.getId());

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startAt", startAt);
        params.addValue("endAt", endAt);

        logger.debug("updateResourceWorkingHours: " + sql.replace(":startAt", "'" + startAt + "'").replace(":endAt", "'" + endAt + "'"));

        int rows = jdbc_helpdesk.update(sql, params);
        if (rows == 0) {
            throw new Exception("row not updated");
        }
    }

    private void saveOriginalBrandWorkingHours(int id) throws Exception {
        String sql = "SELECT Id, DayOfTheWeek, StartAt, EndAt, GETDATE() FROM BrandWorkingHours bwh WHERE id=:id";

        Map<String, Object> dbData = jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);

        if (!testData.tagExist("origBrandWorkingHours")) {
            testData.put("origBrandWorkingHours", dbData);
        }
    }

    private void saveOriginalResourceWorkingHours(int id) {
        String sql = "SELECT Id, ResourceId, StartDayOfTheWeek, EndDayOfTheWeek, StartAt, EndAt, CreatedOn, CreatedBy, UpdatedOn, UpdatedBy FROM ResourceWorkingHours rwh WHERE id=:id AND rwh.id";

        Map<String, Object> dbData = jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);

        if (!testData.tagExist("origResourceWorkingHours")) {
            testData.put("origResourceWorkingHours", dbData);
        }
    }

    public void resetBrandWorkingHours() {
        Map<String, Object> dbData = testData.getMap("origBrandWorkingHours");
        if (dbData == null) return;
        String dow = dbData.get("DayOfTheWeek").toString();
        String startAt = dbData.get("StartAt").toString();
        String endAt = dbData.get("EndAt").toString();
        String id = dbData.get("Id").toString();

        String sql = String.format("UPDATE BrandWorkingHours SET DayOfTheWeek = %s, StartAt = %s, EndAt = %s WHERE Id = %s", dow, startAt, endAt, id);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void resetDeferralWorkingHours() {
        if (testData.getInt("dwhId") != null) {
            String sql = "UPDATE DeferralWorkingHours SET StartAt = '%s', EndAt = '%s' WHERE Id = %d";
            sql = String.format(sql,  testData.getString("dwhStartAt"), testData.getString("dwhEndAt"), testData.getInt("dwhId"));
            jdbc_helpdesk.update(sql, new MapSqlParameterSource());
        }
    }

    public void resetResourceWorkingHours() {
        Map<String, Object> dbData = testData.getMap("origResourceWorkingHours");
        if (dbData == null) return;
        String startDoW = dbData.get("StartDayOfTheWeek").toString();
        String endDoW = dbData.get("EndDayOfTheWeek").toString();
        String startAt = dbData.get("StartAt").toString();
        String endAt = dbData.get("EndAt").toString();
        String id = dbData.get("Id").toString();

        String sql = String.format("UPDATE ResourceWorkingHours SET StartDayOfTheWeek = %s ,EndDayOfTheWeek = %s, StartAt = %s, EndAt = %s WHERE Id = %s", startDoW, endDoW, startAt, endAt, id);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public Boolean isResourceInHours(int resourceId, String timezone) {

        String sql = "SELECT COUNT(0) FROM uvw_resourceWorkingHours rwh "
                + "WHERE "
                + "(rwh.startdayoftheweek < Datepart(dw, Getdate()) OR (rwh.startdayoftheweek = Datepart(dw, Getdate()) AND rwh.StartAt <= CONVERT(TIME, SWITCHOFFSET(GETDATE(), ':timezone')))) "
                + "AND  "
                + "(rwh.EndDayoftheweek > Datepart(dw, Getdate()) OR (rwh.enddayoftheweek = Datepart(dw, Getdate()) AND  rwh.EndAt >= CONVERT(TIME, SWITCHOFFSET(GETDATE(), ':timezone')))) "
                + "AND ResourceId = :resourceId ";

        sql = sql.replace(":timezone", timezone).replace(":resourceId", String.valueOf(resourceId));

        logger.debug("isResourceInHours: " + sql.replace(":resourceId", String.valueOf(resourceId)).replace(":timezone", timezone));

        int count = jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        return count > 0;
    }

    public String getweekStartDay() {
        String sql = "SELECT DATENAME(DW,CAST(DayOfTheWeek AS INT)-1) FROM dbo.OutOfHourTimes WHERE IsFirstDayOfWeek = 1";
        MapSqlParameterSource params = new MapSqlParameterSource();
        String returnDay = jdbc_helpdesk.queryForObject(sql, params, String.class);
        return returnDay;
    }
}
