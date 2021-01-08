package mercury.helpers.gridV1;

import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.runtime.ThreadManager.getWebDriver;

import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import mercury.helpers.POHelper;
import mercury.helpers.gridV1.Grid.Row;

@Deprecated
public class GridHelper {

    private static final String TABLE_HEADERS_XPATH = "//table/thead//th";
    private static final String TABLE_HEADER_XPATH = TABLE_HEADERS_XPATH + "[.='%s']";
    private static final String TABLE_ROWS_XPATH = "//table/tbody//tr[not(contains(@style, 'display: none'))]";

    private static final String FILTER_HEADER_XPATH = TABLE_HEADERS_XPATH + "//span[contains(@class, 'k-filter')]";
    private static final String FILTER_CONTAINS_XPATH = "//input[@data-bind='value:filters[0].value']";
    private static final String FILTER_XPATH = "//button[contains(text(), 'Filter')]";

    private static final String PAGE_QUICKLINKS_XPATH = "/div[@class='k-pager-wrap k-grid-pager k-widget k-floatwrap']";
    private static final String NEXT_PAGE_XPATH = PAGE_QUICKLINKS_XPATH + "/a[3]/span";
    private static final String LAST_PAGE_XPATH = PAGE_QUICKLINKS_XPATH + "/a[4]/span";
    private static final String PAGE_LINK_XPATH = PAGE_QUICKLINKS_XPATH + "//ul/descendant::*[contains(text(),'%s')]";
    private static final String LABEL_XPATH = PAGE_QUICKLINKS_XPATH + "/span[contains(@class, 'k-pager-info') and contains(@class, 'k-label')]";
    private static final String ITEMS_PER_PAGE_XPATH = PAGE_QUICKLINKS_XPATH + "/span[contains(@class, 'k-pager-sizes') and contains(@class, 'k-label')]";
    private static final String ITEMS_PER_PAGE_ARROW_XPATH = ITEMS_PER_PAGE_XPATH + "//span[contains(@class, 'k-i-arrow-s')]";

    private static final String VISIBLE_DROPDOWN_OPTION_XPATH = "//div[@class='k-animation-container' and contains(@style, 'display: block')]/div/div/ul/li[contains(text(), '%s')]";

    public static Grid getGrid(String gridXpath) {

        Grid grid = new Grid();

        String html = getWebDriver().findElement(By.xpath(gridXpath)).getAttribute("outerHTML");

        Document doc = Jsoup.parse(html);

        // get headers
        Element table = doc.select("table").get(0);
        Elements headers = table.select("th");
        for (Element th : headers) {
            grid.addHeader(th.text());
        }

        // get rows
        table = doc.select("table").get(doc.select("table").size()-1);
        Element body = table.selectFirst("tbody");
        Elements rows = body.select("tr");
        for (Element tr : rows) {
            Row row = grid.new Row();

            Elements cells = tr.select("td");
            for (Element td : cells) {
                row.addCell(td.text());
                if (td.hasAttr("colspan")) {
                    for (int i = 1; i < Integer.valueOf(td.attr("colspan")); i++) {
                        row.addCell(null);
                    }
                }
            }
            grid.addRow(row);
        }

        return grid;
    }

    public static void tableSort(String gridXpath, String columnDataField) {
        getWebDriver().findElement(By.xpath(String.format(gridXpath + TABLE_HEADER_XPATH, columnDataField))).click();
    }

    public static boolean isAscending(String gridXpath, String header) {
        return getWebDriver().findElement(By.xpath(String.format(gridXpath + TABLE_HEADER_XPATH, header, header))).getAttribute("aria-sort").contains("ascending");
    }

    public static Integer getNumberOfDisplayedRows(String gridXpath) {
        return getWebDriver().findElements(By.xpath(gridXpath + TABLE_ROWS_XPATH)).size();
    }

    public static String getLabel(String gridXpath) {
        return getWebDriver().findElement(By.xpath(gridXpath + LABEL_XPATH)).getText();
    }

    public static void gotoPage(String gridXpath, Integer pageNumber) {
        getWebDriver().findElement(By.xpath(String.format(PAGE_LINK_XPATH, pageNumber))).click();
    }

    public static void gotoNextPage(String gridXpath) {
        getWebDriver().findElement(By.xpath(gridXpath + NEXT_PAGE_XPATH)).click();
    }

    public static void gotoLastPage(String gridXpath) {
        getWebDriver().findElement(By.xpath(gridXpath + LAST_PAGE_XPATH)).click();
    }

    public static Integer getNumberOfItemsPerPage(String gridXpath) {
        String numberOfItemsPerPage = getWebDriver().findElement(By.xpath(gridXpath + ITEMS_PER_PAGE_XPATH + "//span[contains(@class, 'k-input')]")).getText();
        return Integer.valueOf(numberOfItemsPerPage);
    }

    public static void setNumberOfItemsPerPage(String gridXpath, int numberOfItemsPerPage) {
        getWebDriver().findElement(By.xpath(gridXpath + ITEMS_PER_PAGE_ARROW_XPATH)).click();

        WebElement option = getWebDriver().findElement(By.xpath(String.format(VISIBLE_DROPDOWN_OPTION_XPATH, numberOfItemsPerPage)));
        option.click();
    }

    public static boolean isElementPresent(By by) {
        getWebDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        try {
            getWebDriver().findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
        finally {
            getWebDriver().manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    public static boolean isChildElementPresent(WebElement webElement, By by) {
        getWebDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        try {
            webElement.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
        finally {
            getWebDriver().manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    public static void filter(String gridXpath, String header, String filter) {
        getWebDriver().findElement(By.xpath(String.format(gridXpath + FILTER_HEADER_XPATH, header))).click();
        POHelper.waitForKendoLoadingToComplete();
        getWebDriver().findElement(By.xpath(FILTER_CONTAINS_XPATH)).sendKeys(filter);
        POHelper.waitForKendoLoadingToComplete();
        getWebDriver().findElement(By.xpath(FILTER_XPATH)).click();
        POHelper.waitForKendoLoadingToComplete();
    }

    public static void selectFirstRow(String gridXpath) {
        getWebDriver().findElements(By.xpath(gridXpath + TABLE_ROWS_XPATH)).get(0).click();
        POHelper.waitForAngularRequestsToFinish();
    }
}
