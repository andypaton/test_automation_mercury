package mercury.helpers;

import static mercury.runtime.ThreadManager.getWebDriver;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.internal.AssumptionViolatedException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;


public class ErrorCollector {

    public static List<Throwable> errors = new ArrayList<>();
    public static List<byte[]> screenshots = new ArrayList<>();
    public static List<String> urls = new ArrayList<>();


    public static void reset() {
        errors.clear();
        screenshots.clear();
        urls.clear();
    }

    /**
     * Adds a Throwable to the table.  Execution continues, but the test will fail at the end.
     */
    public static void addError(Throwable error) {
        if (error == null) {
            throw new NullPointerException("Error cannot be null");
        }
        if (error instanceof AssumptionViolatedException) {
            AssertionError e = new AssertionError(error.getMessage());
            e.initCause(error);
            errors.add(e);
        } else {
            errors.add(error);
        }
    }

    public static void addSnapshot() {
        screenshots.add(((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES));
    }

    public static void assertTrue(String message, boolean condition) {
        try {
            Assert.assertTrue(message, condition);
        } catch (AssertionError e) {
            if (!errors.contains(e)) {
                addError(e);
                urls.add(getWebDriver().getCurrentUrl());
            }
        }
    }

    public static void assertFalse(String message, boolean condition) {
        assertTrue(message, !condition);
    }

}
