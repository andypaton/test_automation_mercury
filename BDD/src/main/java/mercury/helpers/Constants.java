package mercury.helpers;

public class Constants {

    public static final int DEFAULT_TIMEOUT = 120; // Seconds
    public static int MAX_TIMEOUT = DEFAULT_TIMEOUT; // Seconds
    public static final int MAX_TIMEOUT_MILLIS = 60000; // Milliseconds
    public static final int TWO_MINUTES = 120; // Seconds
    public static final int THREE_MINUTES = 180; // Seconds
    public static final Integer CLEANUP_DB_AFTER_MINUTES = 10; // Minutes
    public static final String AUTOMATION_USER = "test.automation";
    public static final int MAX_SYNC_TIMEOUT = 240; // Seconds
    public static final int MAX_PORTAL_TIMEOUT = 120; // Seconds
    public static final int POLLING_INTERVAL = 1000; // Milliseconds
    public static final int DB_POLLING_INTERVAL = 5000; // Milliseconds
    public static final int POLLING_INTERVAL_LONG = 4000; // Milliseconds
    public static final String DB_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String GRID_DATE_FORMAT = "MMM dd, yyyy";
    public static final int MAX_ATTEMPTS = 3;

    public static final String PA55W0RD_HASH = "ACf6bmCh1o2t1BxtF1U0BFi4RArKmKf5mmxubtBFcV0lakWIO+ay29uicgAEkc2DxA==";
    public static final String PA55W0RD = "Pa55w0rd";
    public static final String City2019 = "City2019";

    public static final String LABEL_WEIGHT_UK = "Kilogrammes";
    public static final String LABEL_WEIGHT_US = "lbs";

    public static void resetTimeout() {
        MAX_TIMEOUT = DEFAULT_TIMEOUT;
    }

    public static void setTimeout(int seconds) {
        MAX_TIMEOUT = seconds;
    }

}
