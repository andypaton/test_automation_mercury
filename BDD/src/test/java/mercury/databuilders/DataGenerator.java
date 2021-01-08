package mercury.databuilders;

import static mercury.helpers.Globalisation.FULL_DATE;
import static mercury.helpers.Globalisation.LOCALE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import com.github.javafaker.Faker;

import mercury.helpers.DateHelper;

public class DataGenerator {

    public static String GenerateRandomString(int minLength, int maxLength, int minLCaseCount, int minUCaseCount, int minNumCount, int minSpecialCount) {
        char[] randomString;

        String LCaseChars = "abcdefgijkmnopqrstwxyz";
        String UCaseChars = "ABCDEFGHJKLMNPQRSTWXYZ";
        String NumericChars = "0123456789";
        String SpecialChars = "!£&*()";

        Map<String, Integer> charGroupsUsed = new HashMap<String, Integer>();
        charGroupsUsed.put("lcase", minLCaseCount);
        charGroupsUsed.put("ucase", minUCaseCount);
        charGroupsUsed.put("num", minNumCount);
        charGroupsUsed.put("special", minSpecialCount);

        // Because we cannot use the default randomizer, which is based on the
        // current time (it will produce the same "random" number within a
        // second), we will use a random number generator to seed the
        // randomizer.

        // Use a 4-byte array to fill it with random bytes and convert it then
        // to an integer value.
        byte[] randomBytes = new byte[4];

        // Generate 4 random bytes.
        new Random().nextBytes(randomBytes);

        // Convert 4 bytes into a 32-bit integer value.
        int seed = (randomBytes[0] & 0x7f) << 24 | randomBytes[1] << 16 | randomBytes[2] << 8 | randomBytes[3];

        // Create a randomizer from the seed.
        Random random = new Random(seed);

        // Allocate appropriate memory for the password.
        int randomIndex = -1;
        if (minLength < maxLength) {
            randomIndex = random.nextInt((maxLength - minLength) + 1) + minLength;
            randomString = new char[randomIndex];
        } else {
            randomString = new char[minLength];
        }

        int requiredCharactersLeft = minLCaseCount + minUCaseCount + minNumCount + minSpecialCount;

        // Build the password.
        for (int i = 0; i < randomString.length; i++) {
            String selectableChars = "";

            // if we still have plenty of characters left to acheive our minimum
            // requirements.
            if (requiredCharactersLeft < randomString.length - i) {
                // choose from any group at random
                selectableChars = LCaseChars + UCaseChars + NumericChars + SpecialChars;
            } else // we are out of wiggle room, choose from a random group that
                // still needs to have a minimum required.
            {
                // choose only from a group that we need to satisfy a minimum
                // for.
                for (Map.Entry<String, Integer> charGroup : charGroupsUsed.entrySet()) {
                    if (charGroup.getValue() > 0) {
                        if ("lcase".equals(charGroup.getKey())) {
                            selectableChars += LCaseChars;
                        } else if ("ucase".equals(charGroup.getKey())) {
                            selectableChars += UCaseChars;
                        } else if ("num".equals(charGroup.getKey())) {
                            selectableChars += NumericChars;
                        } else if ("special".equals(charGroup.getKey())) {
                            selectableChars += SpecialChars;
                        }
                    }
                }
            }

            // Now that the string is built, get the next random character.
            randomIndex = random.nextInt((selectableChars.length()) - 1);
            char nextChar = selectableChars.charAt(randomIndex);

            // Tac it onto our password.
            randomString[i] = nextChar;

            // Now figure out where it came from, and decrement the appropriate
            // minimum value.
            if (LCaseChars.indexOf(nextChar) > -1) {
                charGroupsUsed.put("lcase", charGroupsUsed.get("lcase") - 1);
                if (charGroupsUsed.get("lcase") >= 0) {
                    requiredCharactersLeft--;
                }
            } else if (UCaseChars.indexOf(nextChar) > -1) {
                charGroupsUsed.put("ucase", charGroupsUsed.get("ucase") - 1);
                if (charGroupsUsed.get("ucase") >= 0) {
                    requiredCharactersLeft--;
                }
            } else if (NumericChars.indexOf(nextChar) > -1) {
                charGroupsUsed.put("num", charGroupsUsed.get("num") - 1);
                if (charGroupsUsed.get("num") >= 0) {
                    requiredCharactersLeft--;
                }
            } else if (SpecialChars.indexOf(nextChar) > -1) {
                charGroupsUsed.put("special", charGroupsUsed.get("special") - 1);
                if (charGroupsUsed.get("special") >= 0) {
                    requiredCharactersLeft--;
                }
            }
        }
        return new String(randomString);
    }

    public static String GenerateRandomDOB() {
        GregorianCalendar gc = new GregorianCalendar();

        int year = randBetween(1945, 1998);
        gc.set(Calendar.YEAR, year);
        int dayOfYear = randBetween(1, gc.getActualMaximum(Calendar.DAY_OF_YEAR));
        gc.set(Calendar.DAY_OF_YEAR, dayOfYear);

        String DOB = gc.get(Calendar.YEAR) + "-" + String.format("%2s",(gc.get(Calendar.MONTH) + 1)).replace(' ', '0') + "-" + String.format("%2s",gc.get(Calendar.DAY_OF_MONTH)).replace(' ', '0');

        return DOB;
    }

    public static int randBetween(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }

    public static Double GenerateRandomDouble(Double min, Double max) {
        DecimalFormat formatter = new DecimalFormat("#0.00");
        Double range = max - min;
        Double randomValue = min + Math.random( ) * range;
        return Double.valueOf(formatter.format(randomValue));
    }

    public static BigDecimal generateRandomBigDecimalFromRange(BigDecimal min, BigDecimal max) {
        BigDecimal randomBigDecimal = min.add(new BigDecimal(Math.random()).multiply(max.subtract(min)));
        return randomBigDecimal.setScale(2, RoundingMode.HALF_UP);
    }

    public static String generateLegacyPhone(){
        Random rand = new Random();
        String possible = "0123456789";
        String text = "";
        for ( int i=0; i < 10; i++ ) {
            text += possible.charAt(rand.nextInt(10));
        }
        text = "0" + text;
        return text;
    }

    public static String generatePhoneNumber(){
        if (LOCALE.equals("en-US")) {
            return generateUsPhoneNumber();
        } else {
            return generateUkPhoneNumber();
        }
    }

    public static String generateInvalidPhoneNumber(){
        if (LOCALE.equals("en-US")) {
            return generateInvalidUsPhoneNumber();
        } else {
            return generateInvalidUkPhoneNumber();
        }
    }

    public static String generateInvalidUkPhoneNumber(){
        String phoneNumber = generatePhoneNumber();

        if (RandomUtils.nextBoolean()) {
            // return phone number with an alphabetic character
            return phoneNumber.replaceFirst("[0-9]", RandomStringUtils.randomAlphabetic(1));
        }

        if (RandomUtils.nextBoolean()) {
            // insert dash
            return phoneNumber.substring(0, 4) + "-" + phoneNumber.substring(5, phoneNumber.length() - 1);
        }

        // return phone number with incorrect length
        return phoneNumber.substring(0, phoneNumber.length() - 2);
    }

    public static String generateInvalidUsPhoneNumber(){
        String phoneNumber = generateUsPhoneNumber();

        if (RandomUtils.nextBoolean()) {
            // return phone number with an alphabetic character
            char digit = phoneNumber.charAt(RandomUtils.nextInt(0, phoneNumber.length()-1));
            return phoneNumber.replace(String.valueOf(digit), RandomStringUtils.randomAlphabetic(1));
        }

        if (RandomUtils.nextBoolean()) {
            // invalid international code
            phoneNumber = String.valueOf(RandomUtils.nextInt(2, 9)) + "###-###-####";
            while (phoneNumber.contains("#")) {
                phoneNumber = phoneNumber.replaceFirst("#", String.valueOf(RandomUtils.nextInt(2, 9)));
            }
            return phoneNumber;
        }

        // return phone number with incorrect length
        return RandomUtils.nextBoolean() ? phoneNumber.substring(0, phoneNumber.length() - 2) : phoneNumber + RandomUtils.nextInt(10, 99);
    }

    public static String generateUkPhoneNumber(){
        String[] validFormats = {"0N### ######", "0N# #### ####", "0N## ### ####", "0N### ######"};
        int[] validCodes = {1, 2, 3, 5, 7, 8, 9};

        String phoneNumber = validFormats[RandomUtils.nextInt(0, validFormats.length)];

        phoneNumber = phoneNumber.replace("N", String.valueOf(validCodes[RandomUtils.nextInt(0, validCodes.length)]));

        while (phoneNumber.contains("#")) {
            phoneNumber = phoneNumber.replaceFirst("#", String.valueOf(RandomUtils.nextInt(0, 9)));
        }

        // randomly convert to international code
        if (RandomUtils.nextBoolean()) {
            phoneNumber = phoneNumber.replaceAll("^0.(.*)", "+44 $1");
        }

        // randomly remove spaces
        if (RandomUtils.nextBoolean()) {
            phoneNumber = phoneNumber.replaceAll(" ", "");
        }

        return phoneNumber;
    }

    public static String generateUsPhoneNumber(){
        String[] validFormats = {"(N##)-N##-####", "N##-N##-####", "1N##-N##-####"};
        String phoneNumber = validFormats[RandomUtils.nextInt(0, validFormats.length)];
        while (phoneNumber.contains("N")) {
            phoneNumber = phoneNumber.replaceFirst("N", String.valueOf(RandomUtils.nextInt(2, 9)));
        }
        while (phoneNumber.contains("#")) {
            phoneNumber = phoneNumber.replaceFirst("#", String.valueOf(RandomUtils.nextInt(0, 9)));
        }

        // randomly remove dashes
        if (RandomUtils.nextBoolean()) {
            phoneNumber = phoneNumber.replaceAll("-", "");
        }

        return phoneNumber;
    }

    public static String generateRandomDepartment() {
        final String[] noun = { "Finance",
                "Helpdesk",
                "Support",
                "Health",
                "Custom",
        "Technical" };
        Random rand = new Random();
        return noun[rand.nextInt(noun.length)];
    }

    public static String generateRandomName() {
        Faker faker = new Faker();

        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String fullName = firstName + " " + lastName;

        return fullName;
    }

    public static String generateRandomJobTitle() {
        Faker faker = new Faker();

        String jobTitle = faker.company().profession();

        return jobTitle;
    }

    public static String generateRandomEmailAddress() {
        Faker faker = new Faker();

        String emailAddress = faker.internet().emailAddress();

        return emailAddress;
    }

    public static String generateRandomWord() {
        return RandomStringUtils.randomAlphanumeric(5, 10);
    }

    public static String generateRandomSentence() throws ParseException{
        final String[] articles= {"a ", "the ", "some "};
        final String[] noun= {"painter ", "plumber ", "electrician ", "aquarist ", "zookeeper ", "fireman ", "astronaut ", "jockey ", "dentist ", "developer ", "tester " };
        final String[] modal= {"will be ", "is ", "might be ", "wont be ", "shall be "};
        final String[] status = {"removing stains ", "checking for leaks ", "installing a TV ", "burying treasure ", "playing with monkeys ", "climbing a ladder ", "moon-walking ", "horse riding ", "pulling teeth ", "learning a new language ", "finding bugs "};
        final String[] when = {"on ", "before ", "until ", "after ", "from "};

        Random rand = new Random();
        return articles[rand.nextInt(articles.length)] +
                noun[rand.nextInt(noun.length)] +
                modal[rand.nextInt(modal.length)] +
                status[rand.nextInt(status.length)] +
                when[rand.nextInt(when.length)] +
                DateHelper.getNowDatePlusOffset(24, FULL_DATE);
    }

    /**
     *
     * @param startInclusive - min number of random alphanumeric words
     * @param endExclusive - max number of random alphanumeric words
     * @return
     */
    public static String randomAlphaNumericWords(int startInclusive, int endExclusive) {
        String words = "";
        int count = RandomUtils.nextInt(startInclusive, endExclusive);
        for (int i = 0; i <= count; i++) {
            String randomString = RandomStringUtils.randomAlphanumeric(1, 10);
            words = words.isEmpty() ? randomString : words + " " + randomString;
        }
        return words;
    }

}
