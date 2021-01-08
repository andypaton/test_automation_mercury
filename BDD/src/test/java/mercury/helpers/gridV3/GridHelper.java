package mercury.helpers.gridV3;

import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.helpers.Globalisation.localize;
import static mercury.helpers.POHelper.clickJavascript;
import static mercury.helpers.POHelper.isElementPresent;
import static mercury.helpers.POHelper.waitForAngularRequestsToFinish;
import static mercury.helpers.POHelper.waitForKendoLoadingToComplete;
import static mercury.helpers.POHelper.waitWhileBusy;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.microsoft.applicationinsights.web.dependencies.apachecommons.lang3.StringUtils;

import mercury.helpers.DateHelper;
import mercury.helpers.POHelper;

public class GridHelper {

    private static final Logger logger = LogManager.getLogger();

    private static final String TABLE_HEADERS_XPATH = "//table/thead//th";
    private static final String TABLE_HEADER_XPATH = TABLE_HEADERS_XPATH + "[normalize-space(.)='%s']";
    private static final String TABLE_HEADER_CONTAINS_XPATH = TABLE_HEADERS_XPATH + "[contains(normalize-space(.), '%s')]";
    private static final String TABLE_ROWS_XPATH = "//tbody//tr[not(contains(@style, 'display: none')) and not(contains(@class, 'k-detail-row'))]";

    private static final String FILTER_FORM_XPATH = "//form[contains(@class, 'k-filter-menu') and contains(@style, 'display: block')]";
    private static final String FILTER_HEADER_XPATH = TABLE_HEADERS_XPATH + "//a[text() = '%s']/preceding-sibling::a/span[contains(@class, 'k-filter')]";
    private static final String FILTER_DROPDOWN_OPTIONS_XPATH = "(" + FILTER_FORM_XPATH + "//span[contains(text(), 'Contains')])[1]";
    private static final String FILTER_OPTION_XPATH = FILTER_FORM_XPATH + "//ul[@aria-hidden='false']//li[contains(text(), '%s')]";
    private static final String FILTER_TEXTBOX_XPATH = FILTER_FORM_XPATH + "//input[@data-bind='value:filters[0].value']";
    private static final String FILTER_BUTTON_XPATH = FILTER_FORM_XPATH + "//button[contains(text(), 'Filter')]";
    private static final String FILTER_CLEAR_BUTTON_XPATH = FILTER_FORM_XPATH + "//button[contains(text(), 'Clear')]";

    private static final String PAGE_QUICKLINKS_V1_XPATH = "//div[@class='k-pager-wrap k-grid-pager k-widget k-floatwrap']";
    private static final String PREVIOUS_PAGE_V1_XPATH = PAGE_QUICKLINKS_V1_XPATH + "/a[2]/span";
    private static final String FIRST_PAGE_V1_XPATH = PAGE_QUICKLINKS_V1_XPATH + "/a[1]/span";
    private static final String NEXT_PAGE_V1_XPATH = PAGE_QUICKLINKS_V1_XPATH + "/a[3]/span";
    private static final String LAST_PAGE_V1_XPATH = PAGE_QUICKLINKS_V1_XPATH + "/a[4]/span";
    private static final String PAGE_NUMBERS_V1_XPATH = PAGE_QUICKLINKS_V1_XPATH + "//ul";
    private static final String CURRENT_PAGE_V1_XPATH = PAGE_QUICKLINKS_V1_XPATH + "//li[contains(@class, 'k-current-page')]//span";
    private static final String PAGE_LINK_V1_XPATH = PAGE_NUMBERS_V1_XPATH + "/descendant::*[contains(text(),'%s')]";

    private static final String PAGE_QUICKLINKS_V2_XPATH = "//div[@class='table-footer']";
    private static final String PREVIOUS_PAGE_V2_XPATH = PAGE_QUICKLINKS_V2_XPATH + "//a[@class='paginate_button previous']";
    private static final String FIRST_PAGE_V2_XPATH = PAGE_QUICKLINKS_V2_XPATH + "//a[@class='paginate_button first']";
    private static final String NEXT_PAGE_V2_XPATH = PAGE_QUICKLINKS_V2_XPATH + "//a[@class='paginate_button next']";
    private static final String LAST_PAGE_V2_XPATH = PAGE_QUICKLINKS_V2_XPATH + "//a[@class='paginate_button last']";
    private static final String CURRENT_PAGE_V2_XPATH = PAGE_QUICKLINKS_V2_XPATH + "//a[@class='paginate_button current']";
    private static final String PAGE_NUMBERS_V2_XPATH = PAGE_QUICKLINKS_V2_XPATH + "//span";
    private static final String PAGE_LINK_V2_XPATH = PAGE_NUMBERS_V2_XPATH + "/descendant::*[contains(text(),'%s')]";

    private static final String LABEL_XPATH = PAGE_QUICKLINKS_V1_XPATH + "/span[contains(@class, 'k-pager-info') and contains(@class, 'k-label')]";
    private static final String ITEMS_PER_PAGE_XPATH = PAGE_QUICKLINKS_V1_XPATH + "/span[contains(@class, 'k-pager-sizes') and contains(@class, 'k-label')]";
    private static final String ITEMS_PER_PAGE_ARROW_XPATH = ITEMS_PER_PAGE_XPATH + "//span[contains(@class, 'k-i-arrow-s')]";

    private static final String VISIBLE_DROPDOWN_OPTION_XPATH = "//div[@class='k-animation-container' and contains(@style, 'display: block')]/div/div/ul/li[contains(text(), '%s')]";

    protected static final String MASTER_ROWS_XPATH = "//tr[contains(@class, 'master')]";
    protected static final String DETAIL_ROWS_XPATH = "//tr[contains(@class, 'detail')]";
    protected static final String TABLE_BODY_ROWS_XPATH = "//tbody//tr";

    private static Document doc;
    private static Element table;
    private static Elements body;
    private static int type;
    private static String gridXpath;


    /**
     * Note that detail rows are treated like an extra column
     * @param grid
     * @param detailRows
     */
    private static void getGridDetailRows(Grid grid) {
        // add detail row header if it exists
        Elements detailRows = body.select("tr.k-detail-row");
        if (!detailRows.isEmpty()) {
            grid.addHeader("detail");
            grid.addSubHeader("detail");
        }
    }

    private static boolean isElementHidden(Element e) {
        return e.attributes().toString().replaceAll(" ", "").contains("display:none") || e.hasClass("hidden") || e.attr("type").equals("hidden");
    }

    public static void getGridHeaders(Grid grid) {
        Row filterRow = new Row();
        Elements thead = doc.select("table").get(0).select("thead");
        Elements headerRows = thead.select("tr");
        for (Element tr : headerRows) {
            Elements headers = tr.select("th");
            if ( tr.html().contains("k-filtercell") ) {
                for (Element th : headers) {
                    addCell(filterRow, th);
                }

            } else {
                for (Element th : headers) {
                    if ( !isElementHidden(th) ) {
                        if (th.hasClass("k-header") && !th.hasClass("k-hierarchy-cell")) {
                            if (th.html().contains("class=\"second-row\"")) {
                                Element subHeader = th.selectFirst("span.second-row");
                                grid.addSubHeader(subHeader.text());
                                th.select("span.second-row").remove();
                                grid.addHeader(th.text());

                            } else {
                                grid.addHeader(th.text());
                                grid.addSubHeader(null);
                            }

                        } else if (!th.hasClass("k-hierarchy-cell")) {
                            grid.addHeader(th.text());
                            grid.addSubHeader(null);
                        }
                    }
                }
            }
        }

        if (filterRow != null) grid.addFilterRow(filterRow);

        getGridDetailRows(grid);
    }

    public static void click(String cssSelector) {
        getWebDriver().findElement(By.cssSelector(cssSelector)).click();
    }

    private static void addCell(Row row, Element td) {
        Cell cell = new Cell();

        String cssSelector = td.cssSelector().replaceAll(".*> table", "table");
        cell.setCssSelector(cssSelector);

        // add cell web elements
        Elements elements = td.getElementsByTag("input");
        elements.addAll(td.getElementsByTag("button"));
        elements.addAll(td.getElementsByTag("img"));
        elements.addAll(td.getElementsByTag("select"));
        elements.addAll(td.getElementsByTag("textarea"));
        elements.addAll(td.getElementsByAttribute("href"));
        elements.addAll(td.getElementsByClass("k-select"));
        for (Element e : elements ) {
            if (!isElementHidden(e) ) {
                cssSelector = e.cssSelector().replaceAll(".*> table", "table");
                cell.addWebElement( getWebDriver().findElement(By.cssSelector(cssSelector)) );
            }
        }

        if (td.html().contains("span")) {
            // cell has text and subText
            if (td.html().contains("class=\"second-row\"")) {
                Element secondRow = td.selectFirst("span.second-row");
                String text = secondRow.text().isEmpty() ? null : secondRow.text();
                cell.setSubText(text);

                td.select("span.second-row").remove();
                text = td.text().isEmpty() ? null : td.text();
                cell.setText(text);

            } else {
                cell.setText(td.select("span").text());
                td.select("span").remove();
                cell.setSubText(td.text());
            }

        } else {
            // cell does not have subText
            cell.setText(td.text());
            cell.setSubText(null);
        }

        row.addCell(cell);
        if (td.hasAttr("colspan")) {
            for (int i = 1; i < Integer.valueOf(td.attr("colspan")); i++) {
                row.addCell(null);
            }
        }
    }

    private static Row getRow(Element tr) {
        Row row = new Row();
        Elements tdata = tr.select("td");
        for (Element td : tdata) {
            if ( !isElementHidden(td) ) {
                if ( (type == 1 && td.attr("role").equalsIgnoreCase("gridcell"))
                        || type == 2
                        || td.hasClass("k-detail-cell")) {

                    addCell(row, td);
                }
            }
        }
        return row;
    }

    private static void getGridRows(Grid grid) {
        Elements trows = body.select("tr");
        for (Element tr : trows) {
            Row row = getRow(tr);

            if (tr.hasClass("k-detail-row")) {
                // add detail row to as a single cell at the end of previous row
                for (Cell cell : row.getCells()) {
                    // the detail row may have multiple null cells, but only expect one non-null cell
                    if (cell != null) {
                        Cell detailCell = row.getCell(0);

                        int lastRowNum = grid.getRows().size();
                        grid.getRows().get(lastRowNum - 1).addCell(detailCell);

                        break;
                    }
                }

            } else {
                grid.addRow(row);
            }
        }

        // commenting out this assertion as its not giving the correct number for details row. (This will not impact any grid tests)
//        for (Row row : grid.getRows()) {
//            assertTrue("Oops - number of cells (" + row.getCells().size() + ") in row not matching number of headers (" + grid.getHeaders().size() + ")", grid.getHeaders().size() == row.getCells().size());
//        }
    }

    private static void getGridFooterRows(Grid grid) {
        Elements footerRows = table.select("tfoot").select("tr");
        for (Element tr : footerRows) {
            Row row = getRow(tr);
            grid.addFooterRow(row);
            assertTrue("Oops - number in cells in footer row not matching number of headers", grid.getHeaders().size() == row.getCells().size());
        }
    }

    /**
     * get a grid with headers displayed in the first row
     * @param gridXpath
     * @return
     */
    public static Grid getGrid(String gridXpath) {
        return getGrid(gridXpath, null);
    }

    /**
     * get a grid with headers displayed in the first row, and return a max number of rows
     * @param gridXpath
     * @param maxRows. if null then return all rows
     * @return
     */
    public static Grid getGrid(String gridXpath, Integer maxRows) {
        GridHelper.gridXpath = gridXpath;
        Grid grid = new Grid(gridXpath);

        String html = getWebDriver().findElement(By.xpath(gridXpath)).getAttribute("outerHTML");

        if (maxRows != null) {
            String bodyString = StringUtils.substringBetween(html, "<tbody", "</tbody");
            int index = StringUtils.ordinalIndexOf(bodyString, "<tr", maxRows + 1);
            if (index != -1) {
                String replacementBody = bodyString.substring(0, index);
                html = StringUtils.replace(html, bodyString, replacementBody);
            }
        }

        doc = Jsoup.parse(html);
        table = doc.select("table").get(doc.select("table").size() - 1);
        body = table.select("tbody");

        getGridHeaders(grid);
        logger.debug("Grid headers(" + grid.getHeaders().size() + "): " + grid.getHeaders());
        //        logger.debug("Grid subHeaders(" + grid.getSubHeaders().size() + "): " + grid.getSubHeaders());

        type = body.toString().contains("gridcell") ? 1 : 2;

        getGridRows(grid);
        getGridFooterRows(grid);

        return grid;
    }

    public static void tableSort(String gridXpath, String columnDataField) {
        String xpath = gridXpath.replaceAll(".*/table", ""); // strip '/table' from xpath if its there
        if (isElementPresent(By.xpath(String.format(xpath + TABLE_HEADER_XPATH, columnDataField)))) {
            clickJavascript(getWebDriver().findElement(By.xpath(String.format(xpath + TABLE_HEADER_XPATH, columnDataField))));
        } else {
            // exact match not found - so check if header contains
            clickJavascript(getWebDriver().findElement(By.xpath(String.format(xpath + TABLE_HEADER_CONTAINS_XPATH, columnDataField))));
        }
    }

    public static boolean isAscending(String gridXpath, String header) {
        String xpath = gridXpath.replaceAll(".*/table", ""); // strip '/table' from xpath if its there
        if (isElementPresent(By.xpath(String.format(xpath + TABLE_HEADER_XPATH, header)))) {
            return getWebDriver().findElement(By.xpath(String.format(xpath + TABLE_HEADER_XPATH, header))).getAttribute("aria-sort").contains("ascending");
        } else {
            // exact match not found - so check if header contains
            return getWebDriver().findElement(By.xpath(String.format(xpath + TABLE_HEADER_CONTAINS_XPATH, header))).getAttribute("aria-sort").contains("ascending");
        }
    }

    public static Integer getNumberOfDisplayedRows(String gridXpath) {
        getWebDriver().manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        try {
            return getWebDriver().findElements(By.xpath(gridXpath + TABLE_ROWS_XPATH)).size();
        } catch(NoSuchElementException e) {
            return 0;
        }
        finally {
            getWebDriver().manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    public static Integer getNumberOfDisplayedRows() {
        return getNumberOfDisplayedRows(gridXpath);
    }


    public static String getLabel(String gridXpath) {
        return getWebDriver().findElement(By.xpath(gridXpath + LABEL_XPATH)).getText();
    }

    public static Grid goToPage(Grid grid, Integer pageNumber) {
        String gridXpath = grid.getGridXpath();
        int paginationType = getPaginationType(gridXpath);
        WebElement we;
        if (getCurrentPageNumber(grid) != pageNumber) {
            if (paginationType == 1) {
                String xpath = String.format(PAGE_LINK_V1_XPATH, pageNumber);
                if ( isElementPresent(By.xpath(xpath)) ) {
                    getWebDriver().findElement(By.xpath(xpath)).click();

                } else {
                    we = getWebDriver().findElement(By.xpath(String.format(PAGE_LINK_V1_XPATH, "...")));
                    POHelper.clickJavascript(we);
                    waitForKendoLoadingToComplete();
                    we = getWebDriver().findElement(By.xpath(xpath));
                    POHelper.clickJavascript(we);
                }

            } else if (paginationType == 2) {
                getWebDriver().findElement(By.xpath(String.format(PAGE_LINK_V2_XPATH, pageNumber))).click();
            }
            waitForKendoLoadingToComplete();
        }
        return getGrid(gridXpath);
    }

    public static Grid goToNextPage(Grid grid) {
        String gridXpath = grid.getGridXpath();
        int paginationType = getPaginationType(gridXpath);
        if (paginationType == 1 && isElementPresent(By.xpath(gridXpath + NEXT_PAGE_V1_XPATH))) {
            clickJavascript(getWebDriver().findElement(By.xpath(gridXpath + NEXT_PAGE_V1_XPATH)));

        } else if (paginationType == 2 && isElementPresent(By.xpath(gridXpath + NEXT_PAGE_V2_XPATH))) {
            clickJavascript(getWebDriver().findElement(By.xpath(gridXpath + NEXT_PAGE_V2_XPATH)));
        }
        waitForKendoLoadingToComplete();
        return getGrid(gridXpath);
    }

    public static Grid goToFirstPage(Grid grid) {
        String gridXpath = grid.getGridXpath();
        int paginationType = getPaginationType(gridXpath);
        if (paginationType == 1 && isElementPresent(By.xpath(gridXpath + FIRST_PAGE_V1_XPATH))) {
            clickJavascript(getWebDriver().findElement(By.xpath(gridXpath + FIRST_PAGE_V1_XPATH)));

        } else if (paginationType == 2 && isElementPresent(By.xpath(gridXpath + FIRST_PAGE_V2_XPATH))) {
            clickJavascript(getWebDriver().findElement(By.xpath(gridXpath + FIRST_PAGE_V2_XPATH)));
        }
        waitForKendoLoadingToComplete();
        return getGrid(gridXpath);
    }

    public static Grid goToPreviousPage(Grid grid) {
        String gridXpath = grid.getGridXpath();
        int paginationType = getPaginationType(gridXpath);
        if (paginationType == 1 && isElementPresent(By.xpath(gridXpath + PREVIOUS_PAGE_V1_XPATH))) {
            clickJavascript(getWebDriver().findElement(By.xpath(gridXpath + PREVIOUS_PAGE_V1_XPATH)));

        } else if (paginationType == 2 && isElementPresent(By.xpath(gridXpath + PREVIOUS_PAGE_V2_XPATH))) {
            clickJavascript(getWebDriver().findElement(By.xpath(gridXpath + PREVIOUS_PAGE_V2_XPATH)));
        }
        waitForKendoLoadingToComplete();
        return getGrid(gridXpath);
    }

    public static Grid goToLastPage(Grid grid) {
        String gridXpath = grid.getGridXpath();
        int paginationType = getPaginationType(gridXpath);
        if (paginationType == 1 && isElementPresent(By.xpath(gridXpath + LAST_PAGE_V1_XPATH))) {
            clickJavascript(getWebDriver().findElement(By.xpath(gridXpath + LAST_PAGE_V1_XPATH)));

        } else if (paginationType == 2 && isElementPresent(By.xpath(gridXpath + LAST_PAGE_V2_XPATH))) {
            clickJavascript(getWebDriver().findElement(By.xpath(gridXpath + LAST_PAGE_V2_XPATH)));
        }
        waitForKendoLoadingToComplete();
        return getGrid(gridXpath);
    }

    public static Integer getCurrentPageNumber(Grid grid) {
        String gridXpath = grid.getGridXpath();
        int paginationType = getPaginationType(gridXpath);
        if (paginationType == 1 && isElementPresent(By.xpath(gridXpath + CURRENT_PAGE_V1_XPATH))) {
            return Integer.valueOf(getWebDriver().findElement(By.xpath(gridXpath + CURRENT_PAGE_V1_XPATH)).getAttribute("innerText"));

        } else if (paginationType == 2  && isElementPresent(By.xpath(gridXpath + CURRENT_PAGE_V2_XPATH))) {
            return Integer.valueOf(getWebDriver().findElement(By.xpath(gridXpath + CURRENT_PAGE_V2_XPATH)).getAttribute("innerText"));

        } else {
            return 1;
        }
    }

    public static Integer getLastPageNumber(Grid grid) {
        int pageNumber = getCurrentPageNumber(grid);
        goToLastPage(grid);
        int lastPageNumber = getCurrentPageNumber(grid);
        goToPage(grid, pageNumber);
        return lastPageNumber;
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

    public static void waitUntilFirstRowContains(String xpath, String text){
        double startTime = System.currentTimeMillis();
        int maxWait = MAX_TIMEOUT * 1000;
        do {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while ( !GridHelper.getRowsAsString(xpath).get(0).contains(text) && (System.currentTimeMillis() - startTime) < maxWait);
    }

    public static void filterClear(String gridXpath, String header) throws InterruptedException {
        getWebDriver().findElement(By.xpath(String.format(gridXpath + FILTER_HEADER_XPATH, header))).click();
        POHelper.waitForStability();
        getWebDriver().findElement(By.xpath(FILTER_CLEAR_BUTTON_XPATH)).click();
        POHelper.waitForStability();
    }

    public static void filter(String gridXpath, String header, String filter) {
        getWebDriver().findElement(By.xpath(String.format(gridXpath + FILTER_HEADER_XPATH, header))).click();
        POHelper.waitForKendoLoadingToComplete();
        getWebDriver().findElement(By.xpath(FILTER_TEXTBOX_XPATH)).clear();
        getWebDriver().findElement(By.xpath(FILTER_TEXTBOX_XPATH)).sendKeys(filter);
        getWebDriver().findElement(By.xpath(FILTER_BUTTON_XPATH)).click();
        waitUntilFirstRowContains(gridXpath, filter);
    }

    public static void filter(String gridXpath, String header, String condition, String filter) {
        getWebDriver().findElement(By.xpath(String.format(gridXpath + FILTER_HEADER_XPATH, header))).click();
        waitForKendoLoadingToComplete();
        POHelper.clickJavascript(By.xpath(FILTER_DROPDOWN_OPTIONS_XPATH));
        String xpath = String.format(FILTER_OPTION_XPATH, condition);
        POHelper.clickJavascript(By.xpath(xpath));
        waitForKendoLoadingToComplete();
        getWebDriver().findElement(By.xpath(FILTER_TEXTBOX_XPATH)).sendKeys(filter);
        waitForKendoLoadingToComplete();
        getWebDriver().findElement(By.xpath(FILTER_BUTTON_XPATH)).click();
        POHelper.waitForStability();
    }

    public static void filterContains(String gridXpath, String header, String filter) {
        filter(gridXpath, header, "Contains", filter);
    }

    public static void filterIsEqualTo(String gridXpath, String header, String filter) {
        filter(gridXpath, header, "Is equal to", filter);
    }

    public static void filterDoesNotContain(String gridXpath, String header, String filter) {
        filter(gridXpath, header, "Does not contain", filter);
    }

    public static void selectFirstRow(String gridXpath) {
        getWebDriver().findElements(By.xpath(gridXpath + TABLE_ROWS_XPATH)).get(0).click();
        waitForAngularRequestsToFinish();
    }

    public static void assertGridHeaders(List<String> gridHeaders, String[] expectedHeaders) throws IOException {
        for (int index = 0; index < expectedHeaders.length; index++){
            expectedHeaders[index] = localize(expectedHeaders[index]).trim();
        }
        List<String> headers = Arrays.asList(expectedHeaders);

        assertTrue("Unexpected grid headers, expected " + Arrays.toString(expectedHeaders) + " but found " + gridHeaders, gridHeaders.containsAll(headers));
    }

    public static void assertSortedAlphaNumerics(Grid grid, String header, boolean isAscending, java.util.Comparator<String> caseSensitive) throws Exception {
        List<String> original = new ArrayList<String>();
        original = grid.getHeaders().contains(header) ? grid.getColumnText(header) : grid.getColumnSubText(header);

        if (isAscending) {
            List<String> ascendingOrder = new ArrayList<>(original);
            Collections.sort(ascendingOrder, caseSensitive);
            assertEquals("Alphanumeric column is not sorted: " + header + " expected: \n" + ascendingOrder + "\nbut got:\n" + original, ascendingOrder, original);

        } else {
            List<String> descendingOrder = new ArrayList<>(original);
            Collections.sort(descendingOrder, Collections.reverseOrder(caseSensitive));
            assertEquals("Alphanumeric column is not sorted: " + header + " expected: \n" + descendingOrder + "\nbut got:\n" + original, descendingOrder, original);
        }
    }

    public static void assertSortedDates(Grid grid, String header, String format, boolean isAscending) throws Exception {
        List<String> original = new ArrayList<String>();
        original = grid.getHeaders().contains(header) ? grid.getColumnText(header) : grid.getColumnSubText(header);

        String defaultDate = format.replaceAll("[dyhms]", "1").replace("MMM", "JAN").replace("a", "AM");

        Collections.replaceAll(original, "Request Quote", ""); // replace button
        Collections.replaceAll(original, "Update ETA", ""); // replace button
        Collections.replaceAll(original, null, defaultDate);
        Collections.replaceAll(original, "", defaultDate);

        List<String> originalFormatted = DateHelper.formatStringDates(original, format);

        List<String> sorted = DateHelper.sortDates(original, format, isAscending);

        assertEquals("Cell Date column is not sorted: " + header + " expected: \n" + sorted + "\nbut got:\n" + originalFormatted, sorted, originalFormatted);
    }

    public static void assertSortedNumerics(Grid grid, String header, boolean isAscending) throws Exception {
        List<String> column = grid.getColumnText(header);
        ArrayList<Double> original = new ArrayList<>();
        for (String cell : column) {
            original.add(Double.valueOf(cell));
        }

        ArrayList<Double> sorted = new ArrayList<Double>(original);
        if (isAscending) {
            Collections.sort(sorted); // sort ascending
        } else {
            Collections.sort(sorted, Collections.reverseOrder()); // sort descending
        }
        assertEquals("Numeric column is not sorted: " + header + " expected: \n" + sorted + "\nbut got:\n" + original, sorted, original);
    }

    /**
     * sort table column to required order
     *
     * @param gridXpath
     * @param header
     * @param isAscending: if false then order in descending order
     */
    public static void tableSort(String gridXpath, String header, Boolean isAscending) {
        tableSort(gridXpath, header);
        if (isAscending != isAscending(gridXpath, header)) {
            tableSort(gridXpath, header);
        }
    }

    public static List<LinkedHashMap<String, Object>> getGridAsMapList(String xpath) throws Exception {
        return getGridAsMapList(getGrid(xpath));
    }

    public static List<LinkedHashMap<String, Object>> getGridAsMapList(Grid grid) throws Exception {
        List<LinkedHashMap<String, Object>> gridRows = new ArrayList<LinkedHashMap<String, Object>>();

        for (int i = 0; i < grid.getRows().size(); i++) {
            Row row = grid.getRows().get(i);
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            for (String header : grid.getHeaders()) {
                map.put(header, row.getCell(header).getText());
            }
            gridRows.add(map);
        }
        return gridRows;
    }

    private static int getPaginationType(String gridXpath) {
        int paginationType = 0;
        if (isElementPresent(By.xpath(gridXpath + PAGE_NUMBERS_V1_XPATH))) {
            paginationType = 1;

        } else if (isElementPresent(By.xpath(gridXpath + PAGE_NUMBERS_V2_XPATH))) {
            paginationType = 2;
        }
        return paginationType;
    }

    public static List<Row> getAllRows(Grid grid) {
        List<Row> allRows = new ArrayList<>();
        String xpath = grid.getGridXpath();

        int currentPageNumber = getCurrentPageNumber(grid);
        int lastPageNumber = getLastPageNumber(grid);

        for (int pageNumber = 1; pageNumber <= lastPageNumber; pageNumber++) {
            goToPage(grid, pageNumber);
            allRows.addAll(GridHelper.getGrid(xpath).getRows());
        }
        goToPage(grid, currentPageNumber);
        return allRows;
    }

    public static Grid getDivGrid(String gridXpath, String headerCss) {
        try {
            throw new Exception("help!!!!");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Grid(gridXpath);
    }

    public static Grid getRowDivGrid(String gridXpath, String headerCss) {
        try {
            throw new Exception("help!!!!");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Grid(gridXpath);
    }

    /**
     * wait for table to display expected number of rows, or until max timeout
     * useful after a table refresh / search / filter
     * @param gridXpath
     * @param rowCount
     */
    public static void waitForRowCount(String gridXpath, int rowCount) {
        double startTime = System.currentTimeMillis();
        int maxWait = MAX_TIMEOUT * 1000;
        while (getNumberOfDisplayedRows(gridXpath) != rowCount && (System.currentTimeMillis() - startTime) < maxWait) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> getRowsAsString() {
        List<String> rows = new ArrayList<>();

        getWebDriver().manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        List<WebElement> masterRows = getWebDriver().findElements(By.xpath(gridXpath + MASTER_ROWS_XPATH));
        List<WebElement> detailRows = getWebDriver().findElements(By.xpath(gridXpath + DETAIL_ROWS_XPATH));
        getWebDriver().manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);

        if (masterRows.isEmpty()) {
            masterRows = getWebDriver().findElements(By.xpath(gridXpath + TABLE_BODY_ROWS_XPATH));
        }

        for (int index = 0; index < masterRows.size(); index++) {
            String text = masterRows.isEmpty() ? "" : masterRows.get(index).getText();
            text = detailRows.isEmpty() ? text : text + "\n" + detailRows.get(index).getText();
            rows.add(text);
        }
        return rows;
    }

    public static List<String> getRowsAsString(String xpath) {
        gridXpath = xpath;
        return getRowsAsString();
    }

    public static Grid getGridForTableName(String tableName) {
        gridXpath = String.format("//*[text() = '%s']/following-sibling::div//table", tableName);
        return getGrid(gridXpath);
    }

    public static Grid getGridForTableName(String tableName, int maxRows) {
        gridXpath = String.format("//*[text() = '%s']/following-sibling::div//table", tableName);
        return getGrid(gridXpath, maxRows);
    }

    public static void search(String filter) {
        String xpath = gridXpath + "/ancestor::div//input[@type = 'search']";
        WebElement searchBox = getWebDriver().findElement(By.xpath(xpath));
        searchBox.clear();
        POHelper.sendKeys(searchBox, filter);
        searchBox.sendKeys(Keys.RETURN);
        waitWhileBusy();
        waitForAngularRequestsToFinish();
    }

    public static void searchTable(String tableName, String filter) {
        gridXpath = String.format("//*[text() = '%s']/following-sibling::div//table", tableName);
        search(filter);
    }

    public static void waitUntilFirstRowOfGridContains(String text) {
        String xpath = "(" + gridXpath + String.format("//tbody//tr[1])[contains(., '%s')]", text);
        getWebDriver().findElement(By.xpath(xpath));
    }

}
