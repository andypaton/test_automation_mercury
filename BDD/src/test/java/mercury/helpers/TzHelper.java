package mercury.helpers;

import static mercury.helpers.Constants.DB_DATE_FORMAT;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;

import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperTimeZone;
import mercury.runtime.RuntimeState;

public class TzHelper {

    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperTimeZone dbHelperTimeZone;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private RuntimeState runtimeState;

    private static boolean timezoneOutput = false;

    /**
     * converts UK datetime to the given IANA code
     * Note: time read from the database will be converted to the local timezone from UTC
     * @param ianaCode
     * @param datetime              : UTC date time in simpleDateFormat
     * @param simpleDateFormat      : the simple date format of datetime
     * @return                      : the converted datetime for the job references site IanaCode timezone
     * @throws ParseException
     */
    public String adjustTimeForIanaCode(String ianaCode, String datetime, String simpleDateFormat) throws ParseException {
        if (!timezoneOutput) {
            runtimeState.scenario.write("Server timezone: " + TimeZone.getDefault().getDisplayName());
            timezoneOutput = true;
        }

        if (simpleDateFormat.equals(DB_DATE_FORMAT)) {
            datetime = datetime.substring(0, 19) + ".000";
        }

        if (TimeZone.getDefault().getDisplayName().equals("Greenwich Mean Time")) {
            return DateHelper.convertTimeZone("Europe/London", ianaCode, datetime, simpleDateFormat);
        } else {
            // defaulting to Coordinated Universal Time (UTC) - eg. Jenkins server
            return DateHelper.convertTimeZone("Etc/GMT", ianaCode, datetime, simpleDateFormat);
        }
    }

    /**
     * converts UK datetime to the the site time for a given job reference
     * Note: time read from the database will be converted to the local timezone from UTC
     * @param jobReference
     * @param datetime              : UTC date time in simpleDateFormat
     * @param simpleDateFormat      : the simple date format of datetime
     * @return                      : the converted datetime for the job references site IanaCode timezone
     * @throws ParseException
     */
    public String adjustTimeForJobReference(int jobReference, String datetime, String simpleDateFormat) throws ParseException {
        String ianaCode = dbHelperTimeZone.getIanaCodeForJobReference(jobReference);
        return adjustTimeForIanaCode(ianaCode, datetime, simpleDateFormat);
    }

    /**
     * converts UK datetime to the site time
     * Note: time read from the database will be converted to the local timezone from UTC
     * @param siteId
     * @param datetime              : UTC date time in simpleDateFormat
     * @param simpleDateFormat      : the simple date format of datetime
     * @return                      : the converted datetime for the sites IanaCode timezone
     * @throws ParseException
     */
    public String adjustTimeForSite(int siteId, String datetime, String simpleDateFormat) throws ParseException {
        String ianaCode = dbHelperTimeZone.getIanaCodeForSite(siteId);
        return adjustTimeForIanaCode(ianaCode, datetime, simpleDateFormat);
    }

    /**
     * converts UK datetime to the home store time for a given resource Id, or service centre time if they dont have a home store
     * Note: time read from the database will be converted to the local timezone from UTC
     * @param siteId
     * @param datetime              : UTC date time in simpleDateFormat
     * @param simpleDateFormat      : the simple date format of datetime
     * @return                      : the converted datetime for the sites IanaCode timezone
     * @throws ParseException
     */
    public String adjustTimeForResourceId(int resourceId, String datetime, String simpleDateFormat) throws ParseException {

        Integer homeStoreId = dbHelperResources.getHomeStoreIdForResourceId(resourceId);

        if (homeStoreId == null) {

            int timezoneId = dbHelper.getServiceCentreTimezoneId();
            String ianaCode = dbHelperTimeZone.getIanaCodeForTimezoneId(timezoneId);
            return adjustTimeForIanaCode(ianaCode, datetime, simpleDateFormat);

        } else {
            return adjustTimeForSite(homeStoreId, datetime, simpleDateFormat);
        }
    }

    /**
     * return current time at site
     * @param ianaCode
     * @return
     * @throws ParseException
     */
    public Date getCurrentTimeAtIanaCode(String ianaCode) throws ParseException {
        TimeZone zone = TimeZone.getTimeZone(ianaCode);
        String dateFormat = "dd-MMM-yyyy HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setTimeZone(zone);
        return DateHelper.stringAsDate(sdf.format(new Date()), dateFormat);
    }

    public Date getCurrentTimeAtSite(int siteId) throws ParseException {
        String ianaCode = dbHelperTimeZone.getIanaCodeForSite(siteId);
        Date siteDate = getCurrentTimeAtIanaCode(ianaCode);
        if ( !runtimeState.scenario.getSourceTagNames().contains("@maintenance") ) runtimeState.scenario.write("Current time at site: " + siteDate.toString() + " (" + ianaCode + ")");
        return siteDate;
    }

    public Date getCurrentTimeAtHomeOffice(int siteId) throws ParseException {
        String ianaCode = dbHelperTimeZone.getIanaCodeForHomeOffice(siteId);
        Date homeOfficeDate = getCurrentTimeAtIanaCode(ianaCode);
        runtimeState.scenario.write("Home Office Date: " + homeOfficeDate.toString() + " (" + ianaCode + ")");
        return homeOfficeDate;
    }

}
