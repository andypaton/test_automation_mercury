package mercury.helpers;

import static mercury.runtime.ThreadManager.getWebDriver;
import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.helpers.POHelper.scrollTo;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import mercury.config.CucumberConfig;
import mercury.database.dao.EventSummaryDao;
import mercury.database.models.EventSummary;
import mercury.runtime.RuntimeState;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import ru.yandex.qatools.ashot.shooting.ShootingStrategy;

@ContextConfiguration(classes=CucumberConfig.class)
public class OutputHelper {

    @Autowired private RuntimeState runtimeState;
    @Autowired private PropertyHelper propertyHelper;
    @Autowired private EventSummaryDao eventSummaryDao;

    private static byte[] screenshot;

    private static String previousPageSource = "";
    private static String pageSource;

    private static boolean override = false;

    private static String navbarAbsolute = "<div class=\"navbar navbar-fixed-top\" style=\"position: absolute;\">";
    private static String navbarFixed = "<div class=\"navbar navbar-fixed-top\" style=\"position: fixed;\">";


    /**
     * @param force - override system properties : true / false
     */
    public void takeScreenshots(boolean force) {
        override = force;
        try {
            takeScreenshots();
        } catch (Throwable e ) {
            if (!System.getProperty("web.driver").equals("Chromeheadless")) {
                Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                BufferedImage capture;
                try {
                    capture = new Robot().createScreenCapture(screenRect);
                    runtimeState.scenario.embed(bufferedImageToBytes(capture), "image/png");
                } catch (AWTException e1) {
                    runtimeState.scenario.write("Unable to take screenshot!!!");
                }
            } else {
                runtimeState.scenario.write("Unable to take screenshot!!!");
            }
        }
    }

    /**
     * take screenshot of xpath only if system property screenshots = true OR scenario tagged with @screenshots
     * @param xpath
     */
    public void takeScreenshot(String xpath) {
        if ( !doEmbed() ) return;
        try {
            takeScreenshotOfElement(xpath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getBrowserInfo(){
        // we have to cast WebDriver object to RemoteWebDriver here, because the first one does not have a method that would tell you which browser it is driving.
        Capabilities cap = ((RemoteWebDriver) getWebDriver()).getCapabilities();
        String b = cap.getBrowserName();
        String os = cap.getPlatform().toString();
        String v = cap.getVersion();
        return String.format("%s v:%s %s", b, v, os);
    }

    /**
     * Take a screenshot of the viewable part of the viewport - excludes address bar and browser tabs
     */
    public void takeScreenshot() {
        if ( !doEmbed() ) return;

        screenshot = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
        embedScreenshot(screenshot);
    }

    private boolean setNavbar(String toStyle) {
        try {
            getWebDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
            WebElement we = getWebDriver().findElement(By.xpath("(//*[contains(@class, 'navbar')])[1]"));
            getWebDriver().manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);

            String style = String.format("arguments[0].style.position='%s'", toStyle);
            ((JavascriptExecutor) getWebDriver()).executeScript(style, we);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean doEmbed() {
        return propertyHelper.showScreenshots() || runtimeState.scenario.getSourceTagNames().contains("@screenshots") || override;
    }

    /**
     * Take a screenshot of the entire viewport - excludes address bar and browser tabs
     * @throws IOException
     * @throws AWTException
     */
    public void takeScreenshots() {
        if ( !doEmbed() ) return;

        if (runtimeState.loginPage != null && runtimeState.loginPage.isModalDisplayed()) {
            // only take 1 screenshot
            takeScreenshot();
            return;
        }

        // update navbar to it can be scrolled
        boolean isNavbarAbsolute = setNavbar("absolute");

        boolean scaleScreenshot = System.getProperty("scaleScreenshot").equals("true");

        ShootingStrategy shootingStrategy = scaleScreenshot ? ShootingStrategies.viewportPasting(ShootingStrategies.scaling(0.75f), 1000) : ShootingStrategies.viewportPasting(100);

        BufferedImage image = new AShot().shootingStrategy(shootingStrategy).takeScreenshot(getWebDriver()).getImage();

        embedScreenshot(bufferedImageToBytes(image));
        scrollTo(0);

        if (isNavbarAbsolute) setNavbar("fixed");

        override = false;
    }

    /**
     * take screenshot of desktop - ie. viewable area of browser including address bar and browser tabs
     * @throws IOException
     * @throws AWTException
     */
    public BufferedImage getScreenshotOfAddressBar() {
        try {
            Double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
            Dimension screenSize = new Dimension();
            screenSize.setSize(width, 76);
            Rectangle screenRectangle = new Rectangle(screenSize);
            Robot robot;
            robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRectangle);
            return image;
        } catch (AWTException e) {
            return null;
        }
    }

    /**
     * take screenshot of desktop - ie. viewable area of browser including address bar and browser tabs
     * @throws IOException
     * @throws AWTException
     */
    public void takeScreenshotOfDesktop() {
        try {
            if ( !doEmbed() || System.getProperty("user.name").toLowerCase().contains("jenkins")) return;

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle screenRectangle = new Rectangle(screenSize);
            Robot robot;
            robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRectangle);
            screenshot = bufferedImageToBytes(image);
            pageSource = getWebDriver().getPageSource().replace(navbarAbsolute, navbarFixed);
            if (!previousPageSource.equals(pageSource)) {
                previousPageSource = pageSource;
                runtimeState.scenario.write("Current Page URL is : " + getWebDriver().getCurrentUrl());
                runtimeState.scenario.embed(screenshot, "image/png");
            }

        } catch (AWTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private byte[] bufferedImageToBytes(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();
            byte[] imageInBytes = baos.toByteArray();
            baos.close();
            return imageInBytes;
        } catch (IOException e) {
            return null;
        }
    }

    private void embedScreenshot(byte[] screenshot) {
        pageSource = getWebDriver().getPageSource().replace(navbarAbsolute, navbarFixed);
        if (!previousPageSource.equals(pageSource)) {
            previousPageSource = pageSource;
            runtimeState.scenario.write("Current Page URL is : " + getWebDriver().getCurrentUrl());

            if ( ! System.getProperty("user.name").toLowerCase().contains("jenkins") ) {
                runtimeState.scenario.embed(bufferedImageToBytes(getScreenshotOfAddressBar()), "image/png");
            }

            runtimeState.scenario.embed(screenshot, "image/png");
        }
    }

    /**
     * Take screenshot of element at xpath
     * @param xpath
     * @throws IOException
     */
    private void takeScreenshotOfElement(String xpath) throws IOException {
        WebElement we = getWebDriver().findElement(By.xpath(xpath));
        BufferedImage image = new AShot().coordsProvider(new WebDriverCoordsProvider()).takeScreenshot(getWebDriver(), we).getImage();
        embedScreenshot(bufferedImageToBytes(image));
    }

    /**
     * write a table to the cucumber report for the Map
     * @param myMap
     */
    public void writeMapList(Map<String, Object> myMap) {
        List<Map<String, Object>> myList = new ArrayList<>();
        myList.add(myMap);
        writeMapList(myList);
    }

    /**
     * write a table to the cucumber report for the Map List
     * @param myList
     */
    @SuppressWarnings("rawtypes")
    public void writeMapList(List<Map<String, Object>> myList) {
        if (myList.size() == 0) return;

        //get longest strings per column
        Map<String, Integer> max = new HashMap<>();

        // update column max length for  headers
        Iterator it = myList.get(0).entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String key = pair.getKey().toString();
            max.put(key, key.length());
        }

        // update column max length for values
        for (Map<String, Object> row : myList) {
            it = row.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                String key = pair.getKey().toString();
                String value = pair.getValue() == null ? "NULL" : pair.getValue().toString();
                if (max.get(key) == null || max.get(key) < value.length()) {
                    max.put(key, value.length());
                }
            }
        }

        String table = "";

        // add headers to table
        it = myList.get(0).entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String key = pair.getKey().toString();
            table = table  + "| " + key;
            // right pad
            for (int i = 0; i + key.length() < max.get(key) + 1; i++) {
                table = table + " ";
            }
        }
        table = table + "|\n---\n";

        // add rows to table
        for (Map<String, Object> row : myList) {
            it = row.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                String key = pair.getKey().toString();
                String value = pair.getValue() == null ? "NULL" : pair.getValue().toString();

                table = table  + "| " + value;
                // right pad
                for (int i = 0; i + value.length() < max.get(key) + 1; i++) {
                    table = table + " ";
                }
            }
            table = table + "|\n";
        }

        runtimeState.scenario.write(table);
    }

    public void writeEventSumary(int jobReference) {
        List<EventSummary> events = eventSummaryDao.getEventSummaryForJobReference(jobReference);

        List<Map<String, Object>> myEvents = new ArrayList<>();
        for (EventSummary event : events) {
            Map<String, Object> myEvent = new LinkedHashMap<String, Object>();
            myEvent.put("Title", event.getTitle());
            myEvent.put("Detail1", event.getDetail1());
            myEvent.put("Detail2", event.getDetail2());
            myEvent.put("Notes", event.getNotes());
            myEvent.put("LoggedBy", event.getLoggedBy());
            myEvent.put("LoggedAt", event.getLoggedAt());
            myEvent.put("Icon", event.getIconIdentifier());
            myEvents.add(myEvent);
        }
        writeMapList(myEvents);
    }

}
