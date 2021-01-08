package mercury.helpers;

import static mercury.helpers.Constants.LABEL_WEIGHT_UK;
import static mercury.helpers.Constants.LABEL_WEIGHT_US;
import static mercury.helpers.StringHelper.normalize;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;

import mercury.helpers.dbhelper.DbHelperGlobalisation;

@Component
public class Globalisation {

    public static String LOCALE;
    public static String CURRENCY_SYMBOL;
    public static int TELPHONE_NUMBER_LENGTH;

    public static String SHORT_DATE;
    public static String SHORT_TIME;
    public static String SHORT_TIME2;
    public static String SHORT;
    public static String SHORT2;
    public static String MEDIUM_DATE;
    public static String MEDIUM_TIME;
    public static String MEDIUM;
    public static String MEDIUM2;
    public static String MEDIUM3;

    public static String FULL_DATE;
    public static String SITE_TIME;

    public static String MS_SHORT_DATE;
    public static String MS_SHORT_TIME;
    public static String MS_SHORT;
    public static String MS_MEDIUM_DATE;
    public static String MS_MEDIUM_TIME;
    public static String MS_MEDIUM;

    public static String REVERSE_DATE;

    public static Double TAX_RATE;

    public static List<String> goodTerminology;
    public static List<String> badTerminology;

    private static final Logger logger = LogManager.getLogger();


    private String reformatDate(String format) {
        return format.replace("tt", "a").replaceAll("Y", "y").replaceAll("D", "d").replace("A", "a");
    }

    public Globalisation(DbHelperGlobalisation dbHelperGlobalisation) {
        LOCALE = dbHelperGlobalisation.getCultureInfoCode();

        TAX_RATE = (double) (LOCALE.equalsIgnoreCase("en-GB") ? 24 : 14);

        try {
            logger.debug("Initializing Globalisation statics for: " + LOCALE);

            Properties props = new Properties();
            ClassLoader classLoader = FileHelper.class.getClassLoader();
            URI uri  = classLoader.getResource("date_time.properties").toURI();
            InputStream stream = Files.newInputStream(Paths.get(uri));
            props.load(stream);

            Locale locale = Locale.forLanguageTag(LOCALE);
            CURRENCY_SYMBOL = Currency.getInstance(locale).getSymbol(locale);

            try {
                List<Map<String, Object>> dbData = dbHelperGlobalisation.getSystemConfigDateTime();

                Map<String, String> dateFormats = new HashMap<>();
                for (Map<String, Object> mapping : dbData) {
                    dateFormats.put((String) mapping.get("FormatKey"), (String) mapping.get("FormatValue"));
                }

                MS_SHORT_DATE = dateFormats.get("shortdate");
                MS_SHORT_TIME = dateFormats.get("shorttime");
                MS_SHORT = dateFormats.get("short");
                MS_MEDIUM_DATE = dateFormats.get("mediumdate");
                MS_MEDIUM_TIME = dateFormats.get("mediumtime");
                MS_MEDIUM = dateFormats.get("medium");
                REVERSE_DATE = dateFormats.get("reversedate");

                SHORT_DATE = reformatDate(MS_SHORT_DATE);
                SHORT_TIME = reformatDate(MS_SHORT_TIME);
                SHORT = reformatDate(MS_SHORT);
                MEDIUM_DATE = reformatDate(MS_MEDIUM_DATE);
                MEDIUM_TIME = reformatDate(MS_MEDIUM_TIME);
                MEDIUM = reformatDate(MS_MEDIUM);

                SITE_TIME = SHORT_TIME;
                SHORT2 = SHORT;             // for post Globalisation there should only be one version of SHORT
                SHORT_TIME2 = SHORT_TIME;   // for post Globalisation there should only be one version of SHORT_TIME
                MEDIUM2 = MEDIUM.replace("h:mm a", "h:mma"); // for post Globalisation there should only be one version of MEDIUM2


                logger.debug("Date/Time formats read from DB table SystemConfigDateTime");

            } catch (Exception e) {
                logger.debug("Date/Time formats read from date_time.properties file");

                MS_SHORT_DATE = props.getProperty("MS_SHORT_DATE");
                MS_SHORT_TIME = props.getProperty("MS_SHORT_TIME");
                MS_SHORT = props.getProperty("MS_SHORT");
                MS_MEDIUM_DATE = props.getProperty("MS_MEDIUM_DATE");
                MS_MEDIUM_TIME = props.getProperty("MS_MEDIUM_TIME");
                MS_MEDIUM = props.getProperty("MS_MEDIUM");

                SHORT_DATE = props.getProperty("SHORT_DATE");
                SHORT_TIME = props.getProperty("SHORT_TIME");
                SHORT_TIME2 = props.getProperty("SHORT_TIME2");  // for pre Globalisation there was no consistency for SHORT formats
                SHORT = props.getProperty("SHORT");
                SHORT2 = props.getProperty("SHORT2");            // for pre Globalisation there was no consistency for SHORT formats
                MEDIUM_DATE = props.getProperty("MEDIUM_DATE");
                MEDIUM_TIME = props.getProperty("MEDIUM_TIME");
                MEDIUM = props.getProperty("MEDIUM");
                MEDIUM2 = props.getProperty("MEDIUM").replace("h:mm a", "h:mma"); // for pre Globalisation there was no consistency for MEDIUM2 formats

                REVERSE_DATE = props.getProperty("REVERSE_DATE");

                SITE_TIME = SHORT_TIME.replace(":ss", "");
            }

            readTerminology(dbHelperGlobalisation);

            TELPHONE_NUMBER_LENGTH = LOCALE.equalsIgnoreCase("en-GB") ? 11 : 10;
            FULL_DATE = LOCALE.equalsIgnoreCase("en-GB") ? "dd MMMM yyyy" : "EEEE, MMMM dd, yyyy";
            MEDIUM3 = MEDIUM_DATE.concat(" h:mma"); // Used in the timeline validation during Portal FGas tests

        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * update str to local format, ie. currency symbol / date formats
     * @param str
     * @return
     */
    public static String localize(String str) {

        // localize currency
        String localizedString = str.replaceAll("[$£]", "\\" + CURRENCY_SYMBOL);

        //localize terminology
        for (int index = 0; index < goodTerminology.size(); index++) {
            String badWord = badTerminology.get(index);
            String goodWord = goodTerminology.get(index);

            localizedString = searchForBadWordAndReplace(localizedString, badWord, goodWord);
        }
        return localizedString;
    }

    public static String setWeightLabel(String label) {
        return LOCALE.equals("en-GB") ? String.format(label, LABEL_WEIGHT_UK) : String.format(label, LABEL_WEIGHT_US);
    }

    private static String searchForBadWordAndReplace(String localizedString, String badWord, String goodWord) {
        String foundWord = getWord(localizedString, badWord);
        while (foundWord != null) {
            // might have to repeat multiple times if the word occurs in the string with multiple cases

            if ( Character.isUpperCase(foundWord.charAt(0)) ) {
                // replacement word should start with capital if the original word started with a capital
                goodWord = goodWord.substring(0, 1).toUpperCase() + goodWord.substring(1);
            }
            // replace bad word for good word
            localizedString = localizedString.replaceAll(foundWord, goodWord);

            foundWord = getWord(localizedString, badWord);
        }
        return localizedString;
    }

    /**
     * return true if str contains word, only if its not part of a longer word
     *         eg. str = cancelled, word=cancel, returns false
     * @param str
     * @param word
     * @return
     */
    public static String getWord(String str, String word) {
        Pattern p = Pattern.compile("(?i).*(?=\\b\\w|\\W)(" + word + ")(?<=\\w\\b|\\W).*");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }


    /**
     * return amount with 2 decimal places, preceded by currency symbol
     *   eg. if locale = en-US then
     *       when amount = 212,     return: $212.00
     *       when amount = 212.0,   return: $212.00
     *       when amount = 212.12,  return: $212.12
     *       when amount = 2212.12, return: $2,212.12
     * @param amount
     * @return
     */
    public static String toCurrency(Object amount) {
        if ("en-US".equals(LOCALE)) {
            return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
        }
        return NumberFormat.getCurrencyInstance(Locale.UK).format(amount);
    }

    /**
     * assert provided dateTime is in correct DATE/TIME format
     * @param time
     * @throws ParseException
     */
    public static void assertDateTimeFormat(String format, String dateTime) throws Exception {
        try {
            new SimpleDateFormat(format).parse(dateTime);
        } catch (ParseException pe) {
            throw new Exception("Expected format: " + format + "\n" + pe.getMessage());
        }
    }

    /**
     * Read in a list of custom spellings that are required by the tests but not dealt with via the terminology table
     * @throws Exception
     */
    private static void readCustomerTerminology() throws Exception {
        String pathToCsv = "src/test/resources/spreadsheets/UK_V_USA_Spelling_Custom.csv";

        File csvFile = new File(pathToCsv);
        if (csvFile.isFile()) {
            List<List<String>> records = new ArrayList<List<String>>();
            CSVReader csvReader = new CSVReader(new FileReader(pathToCsv));
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
            csvReader.close();

            for (int i = 1; i < records.size(); i++) {
                String good = "en-US".equals(LOCALE) ? records.get(i).get(2) : records.get(i).get(1);
                String bad = "en-US".equals(LOCALE) ? records.get(i).get(1) : records.get(i).get(2);
                if ( !goodTerminology.contains(good) ) {
                    goodTerminology.add(good);
                    badTerminology.add(bad);
                }
            }

        } else {
            throw new Exception("*** File NOT Found! ***");
        }
    }

    /**
     * to be replaced with read of DB Terminology table
     * @throws Exception
     */
    private static void readTerminology(DbHelperGlobalisation dbHelperGlobalisation) throws Exception {
        goodTerminology = new ArrayList<>();
        badTerminology = new ArrayList<>();

        String pathToCsv = "src/test/resources/spreadsheets/";
        Map<String, String> terminology = new HashMap<>();

        File csvFile1 = new File(pathToCsv + "UK_V_USA_Spelling_Sheet1.csv");
        if (csvFile1.isFile()) {
            List<List<String>> records = new ArrayList<List<String>>();
            CSVReader csvReader = new CSVReader(new FileReader(pathToCsv + "UK_V_USA_Spelling_Sheet1.csv"));
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
            csvReader.close();

            for (int i = 1; i < records.size(); i++) {
                terminology.put(records.get(i).get(0), records.get(i).get(1));
            }

        } else {
            throw new Exception("*** File NOT Found! ***");
        }

        File csvFile2 = new File(pathToCsv + "UK_V_USA_Spelling_Custom.csv");
        if (csvFile2.isFile()) {
            List<List<String>> records = new ArrayList<List<String>>();
            CSVReader csvReader = new CSVReader(new FileReader(pathToCsv + "UK_V_USA_Spelling_Custom.csv"));
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
            csvReader.close();

            for (int i = 1; i < records.size(); i++) {
                terminology.put(records.get(i).get(1), records.get(i).get(2));
            }

        } else {
            throw new Exception("*** File NOT Found! ***");
        }

        List<Map<String, Object>> dbData = dbHelperGlobalisation.getTerminology();
        for (int i = 0; i < dbData.size(); i++) {
            String cultureCode = (String) dbData.get(i).get("CultureCode");

            String good = (String) dbData.get(i).get("Value");
            String key = (String) dbData.get(i).get("Key");
            String bad = cultureCode.equalsIgnoreCase("en-US") ? key : terminology.get(key);

            if (good != null && bad != null) {
                goodTerminology.add(good);
                badTerminology.add(bad);
            }
        }

        // Add custom terminology
        readCustomerTerminology();

        logger.debug(badTerminology.size() + " words to be asserted on each page for terminology tests");
    }

    public static void assertTerminology(String text) {
        if (System.getProperty("terminology").equalsIgnoreCase("false")) return;

        String incorrectCurrencySymbol = CURRENCY_SYMBOL.equals("$") ? "£" : "$";
        ErrorCollector.assertFalse("Unexpected currency symbol found in: " + text, text.contains(incorrectCurrencySymbol));

        Instant start = Instant.now();
        for (int i = 0; i < badTerminology.size(); i++) {
            String bad = badTerminology.get(i);
            String good = goodTerminology.get(i);
            String cleanedTest = normalize(text.replaceAll("\n", " ").replaceAll("([A-Z][a-z]+)", " $1 ")).toLowerCase();

            cleanedTest = cleanedTest.replaceAll("forklift truck", ""); // exclude from assertions

            ErrorCollector.assertFalse("Expected '" + good + "' but found '" + bad + "' in page:\n " + cleanedTest, cleanedTest.matches(".*\\b" + bad.toLowerCase() + "\\b.*"));
        }
        //        logger.debug("assertTerminology elapsed time: " + Duration.between(start, Instant.now()).toMillis() + "ms");
    }

    public static Map<String, String> localiseMap( Map<String, String> map) {
        Map<String, String> localisedMap = new HashMap<String, String>();

        for (Map.Entry<String, String> entry :  map.entrySet())
        {
            localisedMap.put(localize(entry.getKey()), localize(entry.getValue()));
        }

        return localisedMap;
    }
}
