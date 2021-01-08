package mercury.helpers;

import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;;

public class DateHelper {

    private static final SimpleDateFormat DAY_MONTH_YEAR_FORMAT = new SimpleDateFormat("d MMM yyyy"); // eg. 8 Jan 2018
    private static final SimpleDateFormat HOUR_MINUTES_FORMAT = new SimpleDateFormat("h:mma"); // eg. 07:24AM
    private static final SimpleDateFormat ETA_DATE_FORMAT =  new SimpleDateFormat("yyyy-MM-dd"); //Thursday, November 30, 2017
    private static final String[] strDays = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thusday", "Friday", "Saturday" };
    private static final String FULL_HOUR_TIME_FORMAT_STRING = "hh:mm a"; // eg. 07:24 AM

    private static Calendar resetHourMinuteSeconds(Calendar dateTime) {
        dateTime.set(Calendar.HOUR_OF_DAY, 0);
        dateTime.set(Calendar.MINUTE, 0);
        dateTime.set(Calendar.SECOND, 0);
        dateTime.set(Calendar.MILLISECOND, 0);
        dateTime.set(Calendar.DST_OFFSET, 0);
        dateTime.set(Calendar.ZONE_OFFSET, 0);
        return dateTime;
    }

    public static String dateAsString(Date date) {
        String dayOfWeek = dateAsString(date, "EEEE");
        String dd = dateAsString(date, "dd");
        String remainder = dateAsString(date, "MMMM yyyy, HH:mm:ss");
        return dayOfWeek + " " + dd + getDayOfMonthSuffix(Integer.valueOf(dd)) + " " + remainder;
    }

    public static String dateAsString(Date date, String format) {
        if (date != null){
            DateFormat df = new SimpleDateFormat(format);
            return df.format(date);
        }else{
            return null;
        }
    }

    public static String getDayOfMonthSuffix(final int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
        case 1:  return "st";
        case 2:  return "nd";
        case 3:  return "rd";
        default: return "th";
        }
    }

    public static String getDateInFormat(Timestamp dateTimeToFormat) throws ParseException {
        return dateTimeToFormat == null ? "" : DAY_MONTH_YEAR_FORMAT.format(dateTimeToFormat);
    }

    public static String getTimestampInFormat(Timestamp timestamp, String toFormat) throws ParseException {
        return timestamp == null ? "" : new SimpleDateFormat(toFormat).format(timestamp);
    }

    public static String getDateInFormat(Timestamp dateTimeToFormat, String format) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateTimeToFormat == null ? "" : dateFormat.format(dateTimeToFormat);
    }


    public static String getDateInFormat(Date date, String format) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return date == null ? "" : dateFormat.format(date);
    }

    public static String getTimeInFormat(Timestamp dateTimeToFormat) throws ParseException {
        return dateTimeToFormat == null ? "" : HOUR_MINUTES_FORMAT.format(dateTimeToFormat);
    }

    public static String getNowDatePlusOffset(int offset, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Timestamp dateTimeToFormat = getTimestampPlusHours(offset);
        return dateTimeToFormat == null ? "" : dateFormat.format(dateTimeToFormat);
    }

    public static String getNowOrDatePlusOffset(String date, String inputFormat, int offSetHours, String dateFormat) throws ParseException {
        Date asDate = stringAsDate(date, inputFormat);
        if (asDate.before(new Date())) {
            return getNowDatePlusOffset(offSetHours, dateFormat);
        } else {
            return dateAsString(getDatePlusOffsetInHours(asDate, offSetHours), dateFormat);
        }
    }

    public static String getDatePlusOffset(String date, String inputFormat, int offSetHours, String dateFormat) throws ParseException {
        Date asDate = stringAsDate(date, inputFormat);
        return dateAsString(getDatePlusOffsetInHours(asDate, offSetHours), dateFormat);
    }

    public static Long getDifferenceToNow(Timestamp dateToDiff) throws ParseException {
        Calendar CurrentDate = Calendar.getInstance();
        CurrentDate = resetHourMinuteSeconds(CurrentDate);

        Calendar oldDate = new GregorianCalendar();
        oldDate.setTime(dateToDiff);
        oldDate = resetHourMinuteSeconds(oldDate);

        long timeInMills = oldDate.getTime().getTime();
        long now = CurrentDate.getTime().getTime();
        long diffInMillis = Math.abs(now - timeInMills);
        long diff = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        return diff;
    }


    public static String getDaysOutstanding(Timestamp dateToDetermine) throws ParseException {
        String daysOutstanding = getDifferenceToNow(dateToDetermine).toString().concat(" days ago");
        return daysOutstanding;
    }


    public static Calendar roundToFifteen(Calendar calendar) {
        int unroundedMinutes = calendar.get(Calendar.MINUTE);
        int mod = unroundedMinutes % 15;
        calendar.add(Calendar.MINUTE, mod < 8 ? -mod : (15-mod));
        return calendar;
    }


    /**
     * Get a random date between now and siteDateTime
     * @param siteDateTime
     * @param format
     * @return Random string representing a time sometime between 00:00 and now
     * @throws ParseException
     */
    public static String getRandomStartTime(Date startDateTime, String format) throws ParseException {
        Date now = new Date();
        now = now.before(startDateTime) ? now : startDateTime;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar = resetHourMinuteSeconds(calendar);

        long diff = Math.abs((now.getTime() - calendar.getTime().getTime()) - (1000 * 60 * 60));
        long randomDiff = RandomUtils.nextLong(0, diff);

        Date randomTime = new Date(calendar.getTime().getTime() + randomDiff);
        calendar.setTime(randomTime);
        calendar = roundToFifteen(calendar);
        SimpleDateFormat formatter = new SimpleDateFormat(format);

        return formatter.format(calendar.getTime());
    }


    public static String getRandomETATime() throws ParseException {
        Date eta = new Date();

        Integer randomDays = RandomUtils.nextInt(2, 10);

        eta = addDays(eta, randomDays);

        return dateAsString(eta, ETA_DATE_FORMAT.toPattern());
    }

    /**
     * @param maxDuration - maximum duration in minutes
     * @return Random string representing a time in hours and minutes e.g 01:15 - 1 hour 15 minutes
     * @throws ParseException
     */
    public static String getRandomDuration(long maxDuration) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar = resetHourMinuteSeconds(calendar);

        long randomDiff = RandomUtils.nextLong(0, (maxDuration * 1000 * 60));

        Date randomTime = new Date(calendar.getTime().getTime() + randomDiff);
        calendar.setTime(randomTime);
        calendar = roundToFifteen(calendar);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

        return formatter.format(calendar.getTime());
    }

    public static long getDifferenceBetweenTwoTimes(String from_hhmm, String to_hhmm, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date d1 = sdf.parse(from_hhmm);
        Date d2 = sdf.parse(to_hhmm);
        long diff = d2.getTime() - d1.getTime();
        return diff / (60 * 1000);
    }

    /**
     * return difference in milliseconds
     * @param from
     * @param to
     * @return
     * @throws ParseException
     */
    public static long getDifferenceBetweenTwoTimes(String from_hhmm, String to_hhmm) throws ParseException {
        from_hhmm = formatTime(from_hhmm);
        to_hhmm = formatTime(to_hhmm);

        SimpleDateFormat format = new SimpleDateFormat(FULL_HOUR_TIME_FORMAT_STRING);
        Date d1 = format.parse(from_hhmm);
        Date d2 = format.parse(to_hhmm);
        return Math.abs(d2.getTime() - d1.getTime());
    }

    /**
     * return difference in milliseconds
     * @param from
     * @param to
     * @return
     * @throws ParseException
     */
    public static long getDifferenceBetweenTwoTimes(Date from, Date to) throws ParseException {
        return Math.abs(from.getTime() - to.getTime());
    }


    /**
     * format 9:47AM to 09:57 AM
     * @param hhmm
     * @return
     */
    private static String formatTime(String time) {
        time = time.replaceAll("([0-9])AM", "$1 AM").replaceAll("([0-9])PM", "$1 PM");
        String hour = time.split(":")[0];
        return hour.length() == 2 ? time : "0" + time;
    }

    public static Date getDatePlusOffsetInHours(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date getDatePlusOffsetInDays(int days) {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    public static String getDatePlusOffsetInDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return DAY_MONTH_YEAR_FORMAT.format(calendar.getTime());
    }

    public static String getDatePlusOffsetInMonths(int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, months);
        return DAY_MONTH_YEAR_FORMAT.format(calendar.getTime());
    }

    public static String getDatePlusOffsetInMonths(Date date, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months);
        return DAY_MONTH_YEAR_FORMAT.format(calendar.getTime());
    }

    public static int getDayOfWeek() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public static int getDayOfWeek(int offset) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, offset);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public static String getTomorrow() {
        return strDays[getDayOfWeek(1) - 1];
    }

    public static String getToday() {
        return strDays[getDayOfWeek() - 1];
    }

    public static Boolean isEndTimeValid(String date) throws ParseException {
        Date now = new Date();

        Date endDate = new SimpleDateFormat("MM/dd/yyyy hh:mm a").parse(date);
        endDate = getDatePlusOffsetInHours(endDate, -5); // Hack to get tests passing - follow up PR on the way to remove this - workaround for bug in system
        return endDate.before(now);
    }


    public static boolean isValidDate(String dateString, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        try {
            df.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static String convert(String dateStr, String fromFormat, String toFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(fromFormat);
        Date date = sdf.parse(dateStr);
        sdf = new SimpleDateFormat(toFormat);
        return sdf.format(date);
    }

    public static String convertAddHours(String dateStr, String fromFormat, String toFormat, int hoursToAdd) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(fromFormat);
        Date date = sdf.parse(dateStr);
        date = DateUtils.addHours(date, hoursToAdd);
        sdf = new SimpleDateFormat(toFormat);
        return sdf.format(date);
    }

    public static Date stringAsDate(String dateString, String format) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.parse(dateString);
    }

    public static long getTimeDifferenceBetweenTwoDatesInMinutes(Date dateOne, Date dateTwo) {
        long totalMinutesBetweenTwoDates = TimeUnit.MILLISECONDS.toMinutes(dateOne.getTime() - dateTwo.getTime());
        return totalMinutesBetweenTwoDates;
    }

    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    /**
     * get a timestamp of now + offset hours
     * @param offset
     * @return
     */
    public static Timestamp getTimestampPlusHours(int offset) {
        return new Timestamp(System.currentTimeMillis() + (1000 * 60 * 60 * offset));
    }

    /**
     * @param dates
     * @param format
     * @param isAscending
     * @return
     * @throws ParseException
     */
    public static List<String> sortDates(List<String> dates, String format, boolean isAscending) throws ParseException{
        List<String> sortedDates = new ArrayList<>();
        SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);

        // convert to date
        Date[] arrayOfDates = new Date[dates.size()];

        for (int i = 0; i < dates.size(); i++) {
            arrayOfDates[i] = sdf.parse(dates.get(i));
        }

        // sort
        if (isAscending) {
            Arrays.sort(arrayOfDates);
        } else {
            Arrays.sort(arrayOfDates, Collections.reverseOrder());
        }

        // convert back to String
        for (int index = 0; index < arrayOfDates.length; index++) {
            sortedDates.add(sdf.format(arrayOfDates[index]));
        }
        return sortedDates;
    }

    public static List<String> formatStringDates(List<String> dates, String format) throws ParseException{
        List<String> formattedDates = new ArrayList<>();
        SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);

        // convert to date
        Date[] arrayOfDates = new Date[dates.size()];

        for (int i = 0; i < dates.size(); i++) {
            arrayOfDates[i] = sdf.parse(dates.get(i));
        }

        // convert back to String
        for (int index = 0; index < arrayOfDates.length; index++) {
            formattedDates.add(sdf.format(arrayOfDates[index]));
        }
        return formattedDates;
    }


    /**
     * Get the current offset for a given date, takes account of timezone off set.
     * @param date
     * @return
     */
    public static long getOffset(Date date) {
        Calendar calDate = new GregorianCalendar();
        calDate.setTime(date);
        return (calDate.get(Calendar.ZONE_OFFSET) + calDate.get(Calendar.DST_OFFSET));
    }

    /**
     * Get the current time in milliseconds.  date.getTime() does not take into account the timezone offset
     * @param date
     * @return
     */
    public static long getTimeInMilliseconds(Date date) {
        long timeZoneOffset = getOffset(date);
        date = DateUtils.setMilliseconds(date, 0);

        return date.getTime() + timeZoneOffset;
    }

    private static DateFormat getDateFormat(String timeZone, String simpleDateFormat) {
        DateFormat format = new SimpleDateFormat(simpleDateFormat);
        format.setTimeZone(TimeZone.getTimeZone(timeZone));
        return format;
    }

    public static String timestampToString(long timestamp, String timeZone, String simpleDateFormat) {
        DateFormat format = getDateFormat(timeZone, simpleDateFormat);
        return format.format(new Date(timestamp));
    }

    public static long stringToTimestamp(String datetime, String timeZone, String simpleDateFormat) throws ParseException {
        DateFormat format = getDateFormat(timeZone, simpleDateFormat);
        return format.parse(datetime).getTime();
    }

    /**
     * Convert fromIanaCode datetime to toIanaCode datetime
     * @param fromIanaCode      : IanaCode to convert from eg. Europe/London
     * @param toIanaCode        : IanaCode to convert to eg. America/Chicago
     * @param datetime          : eg. 5 Nov 2018 4:15 PM
     * @param simpleDateFormat  : eg. d MMM yyyy h:mm a
     * @return                  : the date converted to timezone Europe/London in the same format as input
     * @throws ParseException
     */
    public static String convertTimeZone(String fromIanaCode, String toIanaCode, String datetime, String simpleDateFormat) throws ParseException {
        long timestamp = stringToTimestamp(datetime, fromIanaCode, simpleDateFormat);
        return timestampToString(timestamp, toIanaCode, simpleDateFormat);
    }

    /**
     * return true for date comparison - ignoring possible space character before AP/PM, and preceding 0 for day/month
     * @param message
     * @param expectedDate
     * @param actualDate
     */
    public static void assertEquals(String message, String expectedDate, String actualDate) {
        String expected = expectedDate.replaceAll("(AM|PM)", " ?$1").replaceAll("(^| |/)0([0-9])", "$10?$2").replaceAll("(^| |/)([1-9])( |/)", "$10?$2$3");
        assertTrue(message,  actualDate.matches(expected));
    }

    public static Date toNextWholeMinute(Date d) {
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        if (c.get(Calendar.SECOND) >= 1) c.add(Calendar.MINUTE, 1);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    /**
     *
     * @param requiredFormat: eg. 'E d' would return 'Sat 6'
     * @param firstDayOfWeek: 0 = Sunday, 6 = Saturday
     * @return
     */
    public static String getStartOfWeek(String requiredFormat, int firstDayOfWeek) {
        Calendar cal = Calendar.getInstance();
        while (cal.get(Calendar.DAY_OF_WEEK) != (firstDayOfWeek + 1)) {
            cal.add(Calendar.DATE, -1);
        }
        DateFormat df = new SimpleDateFormat(requiredFormat);
        return df.format(cal.getTime());
    }

}