package mercury.helpers;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

public class StringHelper {

    /**
     * multiple spaces replaced by single space character
     * @param str
     * @return
     */
    public static String normalize(String str) {
        return str.replaceAll("\\p{Z}",  " ").replaceAll("\\s+",  " ").trim();
    }

    public static List<Map<String, Object>> normalize(List<Map<String, Object>> dbData) {
        for (Map<String, Object> row : dbData) {
            row = normalize(row);
        }
        return dbData;
    }

    public static Map<String, Object> normalize(Map<String, Object> dbData) {
        for (String key : dbData.keySet()) {
            if (dbData.get(key) instanceof String) {
                dbData.put(key, normalize((String) dbData.get(key)));
            }
        }
        return dbData;
    }

    /**
     * place single quotes around str
     * @param str
     * @return
     */
    public static String quote(String str) {
        return "'" + str + "'";
    }

    public static String[] quote(String[] strArray) {
        for (int i = 0; i < strArray.length; i++) {
            strArray[i] = quote(strArray[i].trim());
        }
        return strArray;
    }


    /**
     * Return random ascii string with :
     *       - no preceding or trailing spaces
     *       - nothing between <> because cucumber report strips it
     * @param length
     * @return
     */
    public static String randomAscii(int length) {
        String result;
        do {
            result = RandomStringUtils.randomAscii(length);
        } while ( (result.trim().length() < length) || (result.contains("<") && result.contains(">")) );
        return result;
    }

    /**
     * Return true if str contains regex
     * @param str
     * @param val
     * @return
     */
    public static boolean contains(String str, String regex) {
        return str.replaceAll("[\t\r\n]", "").matches(".*" + regex + ".*");
    }

    public static String splitCamelCase(String camelName) {
        return camelName.replaceAll("([a-z]+)([A-Z])", "$1 $2") // Words with UpperCase that follow LowerCase letter
                .replaceAll("([A-Z][A-Z]+)", " $1") // "Words" of only UpperCase
                .replaceAll("\\s+",  " ") // replace multiple space chars with single space
                .trim();
    }

    /**
     * input a number as a string and remove trailing 0's
     * eg. given       return
     *     120         120
     *     120.10      120.1
     *     120.0       120
     * @param val
     * @return
     */
    public static String trimZeros(String val) {
        return val.indexOf(".") < 0 ? val : val.replaceAll("0*$", "").replaceAll("\\.$", "");
    }

    public static boolean isNumeric(String strNum) {
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)"+regex+"(?!.*?"+regex+")", replacement);
    }

}
