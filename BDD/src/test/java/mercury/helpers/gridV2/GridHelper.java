package mercury.helpers.gridV2;

import static mercury.helpers.Globalisation.localize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import mercury.helpers.DateHelper;
import mercury.helpers.POHelper;
import mercury.helpers.gridV2.Grid.Cell;
import mercury.helpers.gridV2.Grid.Row;

@Deprecated
public class GridHelper {

    private static final String TABLE_HEADER_XPATH = "//thead//th[contains(text(), '%s')] | //thead//th//a[contains(text(), '%s')]";
    private static final String TABLE_ROWS_XPATH = "//tbody//tr[not(contains(@style, 'display: none'))]";

    private static final String PAGE_QUICKLINKS_TYPE_1_XPATH = "//div[@class='k-pager-wrap k-grid-pager k-widget k-floatwrap']";
    private static final String PREVIOUS_PAGE_TYPE_1_XPATH = PAGE_QUICKLINKS_TYPE_1_XPATH + "/a[2]/span";
    private static final String FIRST_PAGE_TYPE_1_XPATH = PAGE_QUICKLINKS_TYPE_1_XPATH + "/a[1]/span";
    private static final String NEXT_PAGE_TYPE_1_XPATH = PAGE_QUICKLINKS_TYPE_1_XPATH + "/a[3]/span";
    private static final String LAST_PAGE_TYPE_1_XPATH = PAGE_QUICKLINKS_TYPE_1_XPATH + "/a[4]/span";
    private static final String PAGE_NUMBERS_TYPE_1_XPATH = PAGE_QUICKLINKS_TYPE_1_XPATH + "//ul";
    private static final String CURRENT_PAGE_TYPE_1_XPATH = PAGE_QUICKLINKS_TYPE_1_XPATH + "//li[contains(@class, 'k-current-page')]//span";
    private static final String PAGE_LINK_TYPE_1_XPATH = PAGE_NUMBERS_TYPE_1_XPATH + "/descendant::*[contains(text(),'%s')]";
    private static final String LABEL_XPATH = PAGE_QUICKLINKS_TYPE_1_XPATH + "/span[contains(@class, 'k-pager-info') and contains(@class, 'k-label')]";
    private static final String ITEMS_PER_PAGE_XPATH = PAGE_QUICKLINKS_TYPE_1_XPATH + "/span[contains(@class, 'k-pager-sizes') and contains(@class, 'k-label')]";
    private static final String ITEMS_PER_PAGE_ARROW_XPATH = ITEMS_PER_PAGE_XPATH + "//span[contains(@class, 'k-i-arrow-s')]";

    private static final String PAGE_QUICKLINKS_TYPE_2_XPATH = "//div[@class='table-footer']";
    private static final String PREVIOUS_PAGE_TYPE_2_XPATH = PAGE_QUICKLINKS_TYPE_2_XPATH + "//a[@class='paginate_button previous']";
    private static final String FIRST_PAGE_TYPE_2_XPATH = PAGE_QUICKLINKS_TYPE_2_XPATH + "//a[@class='paginate_button first']";
    private static final String NEXT_PAGE_TYPE_2_XPATH = PAGE_QUICKLINKS_TYPE_2_XPATH + "//a[@class='paginate_button next']";
    private static final String LAST_PAGE_TYPE_2_XPATH = PAGE_QUICKLINKS_TYPE_2_XPATH + "//a[@class='paginate_button last']";
    private static final String CURRENT_PAGE_TYPE_2_XPATH = PAGE_QUICKLINKS_TYPE_2_XPATH + "//a[@class='paginate_button current']";
    private static final String PAGE_NUMBERS_TYPE_2_XPATH = PAGE_QUICKLINKS_TYPE_2_XPATH + "//span";
    private static final String PAGE_LINK_TYPE_2_XPATH = PAGE_NUMBERS_TYPE_2_XPATH + "/descendant::*[contains(text(),'%s')]";

    private static String getPaginationType(String gridXpath) {
        String paginationType = null;
        if (POHelper.isElementPresent(By.xpath(gridXpath + PAGE_NUMBERS_TYPE_1_XPATH))) {
            paginationType = "Type1";
        } else if (POHelper.isElementPresent(By.xpath(gridXpath + PAGE_NUMBERS_TYPE_2_XPATH))) {
            paginationType = "Type2";
        }
        return paginationType;
    }

    private static final String VISIBLE_DROPDOWN_OPTION_XPATH = "//div[@class='k-animation-container' and contains(@style, 'display: block')]/div/div/ul/li[contains(text(), '%s')]";

    public static Cell createCell(String css, Object text) {
        Cell cell = (new Grid()).new Cell();
        if (text == null) {
            cell.setText(null);
            cell.addCellLocation(null);
        } else if (text instanceof String) {
            cell.setText((String) text);
            cell.addCellLocation(css);
        }
        cell.setSubText(null);
        cell.addCellControl(null);
        return cell;
    }

    public static Grid getGrid(String gridXpath) {

        Grid grid = new Grid();
        String html = getWebDriver().findElement(By.xpath(gridXpath)).getAttribute("outerHTML");
        Document doc = Jsoup.parse(html);

        String paginationType = getPaginationType(gridXpath);
        grid.setPaginationType(paginationType);

        // get headers
        Element table = doc.select("table").get(0);
        Elements headers = table.select("th");
        table = doc.select("table").get(doc.select("table").size() - 1);
        Elements bodies = table.select("tbody");
        Elements rows = bodies.select("tr");
        Elements detailRows = bodies.select("tr.k-detail-row");

        for (Element th : headers) {
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
        // add detail row header if it exists
        if (!detailRows.isEmpty()) {
            grid.addHeader("detail");
            grid.addSubHeader("detail");
        }

        // get rows
        for (Element tr : rows) {
            Row row = grid.new Row();
            if (tr.hasClass("k-master-row")) {
                Elements cells = tr.select("td");
                for (Element td : cells) {
                    Cell cell = grid.new Cell();
                    cell.addCellLocation(td.cssSelector());
                    if (td.attr("role").equalsIgnoreCase("gridcell")) {
                        if (td.html().contains("onclick=")) {
                            Element button = td.selectFirst("button");
                            cell.addCellControl(button.cssSelector());
                            td.select("button").remove();
                        } else {
                            cell.addCellControl(null);
                        }
                        if (td.html().contains("span")) {
                            if (td.html().contains("class=\"second-row\"")) {
                                Element subCell = td.selectFirst("span.second-row");
                                if (subCell.text().isEmpty()) {
                                    cell.setSubText(null);
                                } else {
                                    cell.setSubText(subCell.text());
                                }
                                td.select("span.second-row").remove();
                                if (td.text().isEmpty()) {
                                    cell.setText(null);
                                } else {
                                    cell.setText(td.text());
                                }
                            } else {
                                cell.setText(td.select("span").text());
                                td.select("span").remove();
                                cell.setSubText(td.text());
                            }
                        } else {
                            cell.setText(td.text());
                            cell.setSubText(null);
                        }
                        row.addCell(cell);
                    }
                }
                row.addCell(createCell(detailRows.get((grid.getRows().size())).cssSelector(), detailRows.get((grid.getRows().size())).text()));
                grid.addRow(row);
            } else if (!tr.hasClass("k-detail-row")) {
                Elements cells = tr.select("td");
                for (Element td : cells) {
                    Cell cell = grid.new Cell();
                    cell.addCellLocation(td.cssSelector());
                    if (td.html().contains("<input")) {
                        Element input = td.selectFirst("input");
                        cell.setText(null);
                        cell.setSubText(null);
                        cell.addCellControl(input.cssSelector());

                    } else {
                        cell.setText(td.text());
                        cell.setSubText(null);
                        cell.addCellControl(null);
                    }
                    row.addCell(cell);

                    if (td.hasAttr("colspan")) {
                        for (int i = 1; i < Integer.valueOf(td.attr("colspan")); i++) {
                            row.addCell(createCell(null, null));
                        }
                    }
                }
                grid.addRow(row);
            }
        }

        if (paginationType != null && getCurrentPage(gridXpath, grid.getPaginationType()) != getLastPageNumberShownInPagination(gridXpath, grid.getPaginationType())) {
            do {
                gotoNextPage(gridXpath, grid.getPaginationType());

                html = getWebDriver().findElement(By.xpath(gridXpath)).getAttribute("outerHTML");
                doc = Jsoup.parse(html);
                // get rows
                table = doc.select("table").get(0);
                table = doc.select("table").get(doc.select("table").size() - 1);
                Element body = table.selectFirst("tbody");
                rows = body.select("tr");
                for (Element tr : rows) {
                    Row row = grid.new Row();
                    Elements cells = tr.select("td");
                    for (Element td : cells) {
                        row.addCell(GridHelper.createCell(td.cssSelector(), td.text()));
                        if (td.hasAttr("colspan")) {
                            for (int i = 1; i < Integer.valueOf(td.attr("colspan")); i++) {
                                row.addCell(null);
                            }
                        }
                    }
                    grid.addRow(row);
                }
            } while (getCurrentPage(gridXpath, grid.getPaginationType()) < getLastPageNumberShownInPagination(gridXpath, grid.getPaginationType()));

        }
        return grid;
    }

    public static Grid getDivGrid(String gridXpath, String headerCss) {

        Grid grid = new Grid();

        String html = getWebDriver().findElement(By.xpath(gridXpath)).getAttribute("outerHTML");

        Document doc = Jsoup.parse(html);

        // get headers
        Elements headers = doc.select(headerCss);
        for (Element th : headers) {
            grid.addHeader(th.text());
        }

        // get rows
        Elements rows = doc.select("div.row.ng-scope[ng-repeat]");
        for (Element tr : rows) {
            Row row = grid.new Row();

            Elements cells = tr.select("div.ng-binding");
            for (Element td : cells) {
                row.addCell(createCell(td.cssSelector(), td.text()));
            }
            grid.addRow(row);
        }

        return grid;
    }

    public static Grid getRowDivGrid(String gridXpath, String headerCss) {

        Grid grid = new Grid();

        String html = getWebDriver().findElement(By.xpath(gridXpath)).getAttribute("outerHTML");

        Document doc = Jsoup.parse(html);

        Elements hRows = doc.select("div.ng-scope[ng-repeat]");
        Elements hCells = hRows.eq(0);
        for (Element th : hCells.select(" div.row :first-child")) {
            grid.addHeader(th.text());
        }

        // get rows
        Elements rows = doc.select("div.ng-scope[ng-repeat]");
        for (Element tr : rows) {
            Row row = grid.new Row();

            Elements cells = tr.select("div.ng-binding");
            for (Element td : cells) {
                row.addCell(createCell(td.cssSelector(), td.text()));
            }
            grid.addRow(row);
        }

        return grid;
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

    public static void tableSort(String gridXpath, String header) {
        getWebDriver().findElement(By.xpath(String.format(gridXpath + TABLE_HEADER_XPATH, header, header))).click();
    }

    public static boolean isAscending(String gridXpath, String header) {
        return getWebDriver().findElement(By.xpath(String.format(gridXpath + TABLE_HEADER_XPATH + "/..", header, header))).getAttribute("aria-sort").contains("ascending");
    }

    public static Integer getNumberOfDisplayedRows(String gridXpath) {
        return getWebDriver().findElements(By.xpath(gridXpath + TABLE_ROWS_XPATH)).size();
    }

    public static String getLabel(String gridXpath) {
        return getWebDriver().findElement(By.xpath(gridXpath + LABEL_XPATH)).getText();
    }

    public static void gotoPage(String gridXpath, Integer pageNumber, String paginationType) {
        if (paginationType.equals("Type1")) {
            getWebDriver().findElement(By.xpath(String.format(PAGE_LINK_TYPE_1_XPATH, pageNumber))).click();
        } else if (paginationType.equals("Type2")) {
            getWebDriver().findElement(By.xpath(String.format(PAGE_LINK_TYPE_2_XPATH, pageNumber))).click();
        }
        POHelper.waitForKendoLoadingToComplete();
    }

    public static void gotoNextPage(String gridXpath, String paginationType) {
        if (paginationType.equals("Type1") && POHelper.isElementPresent(By.xpath(gridXpath + NEXT_PAGE_TYPE_1_XPATH))) {
            POHelper.clickJavascript(getWebDriver().findElement(By.xpath(gridXpath + NEXT_PAGE_TYPE_1_XPATH)));
        } else if (paginationType.equals("Type2") && POHelper.isElementPresent(By.xpath(gridXpath + NEXT_PAGE_TYPE_2_XPATH))) {
            POHelper.clickJavascript(getWebDriver().findElement(By.xpath(gridXpath + NEXT_PAGE_TYPE_2_XPATH)));
        }
        POHelper.waitForKendoLoadingToComplete();
    }

    public static void gotoFirstPage(String gridXpath) {
        String paginationType = getPaginationType(gridXpath);
        if (paginationType.equals("Type1") && POHelper.isElementPresent(By.xpath(gridXpath + FIRST_PAGE_TYPE_1_XPATH))) {
            POHelper.clickJavascript(getWebDriver().findElement(By.xpath(gridXpath + FIRST_PAGE_TYPE_1_XPATH)));
        } else if (paginationType.equals("Type2") && POHelper.isElementPresent(By.xpath(gridXpath + FIRST_PAGE_TYPE_2_XPATH))) {
            POHelper.clickJavascript(getWebDriver().findElement(By.xpath(gridXpath + FIRST_PAGE_TYPE_2_XPATH)));
        }
        POHelper.waitForKendoLoadingToComplete();
    }

    public static void gotoPreviousPage(String gridXpath, String paginationType) {
        if (paginationType.equals("Type1") && POHelper.isElementPresent(By.xpath(gridXpath + PREVIOUS_PAGE_TYPE_1_XPATH))) {
            POHelper.clickJavascript(getWebDriver().findElement(By.xpath(gridXpath + PREVIOUS_PAGE_TYPE_1_XPATH)));
        } else if (paginationType.equals("Type2") && POHelper.isElementPresent(By.xpath(gridXpath + PREVIOUS_PAGE_TYPE_2_XPATH))) {
            POHelper.clickJavascript(getWebDriver().findElement(By.xpath(gridXpath + PREVIOUS_PAGE_TYPE_2_XPATH)));
        }
        POHelper.waitForKendoLoadingToComplete();
    }

    public static void gotoLastPage(String gridXpath, String paginationType) {
        if (paginationType.equals("Type1") && POHelper.isElementPresent(By.xpath(gridXpath + LAST_PAGE_TYPE_1_XPATH))) {
            POHelper.clickJavascript(getWebDriver().findElement(By.xpath(gridXpath + LAST_PAGE_TYPE_1_XPATH)));
        } else if (paginationType.equals("Type2") && POHelper.isElementPresent(By.xpath(gridXpath + LAST_PAGE_TYPE_2_XPATH))) {
            POHelper.clickJavascript(getWebDriver().findElement(By.xpath(gridXpath + LAST_PAGE_TYPE_2_XPATH)));
        }
        POHelper.waitForKendoLoadingToComplete();
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

    public static Integer getLastPageNumberShownInPagination(String gridXpath, String paginationType) {
        if (paginationType.equals("Type1") && POHelper.isElementPresent(By.xpath(gridXpath + LAST_PAGE_TYPE_1_XPATH))) {
            WebElement pageNumberUL = getWebDriver().findElement(By.xpath(gridXpath + PAGE_NUMBERS_TYPE_1_XPATH));
            List<WebElement> pageNumberList = pageNumberUL.findElements(By.tagName("li"));
            Integer lastPageNumberOnScreen;
            if ("...".equalsIgnoreCase(pageNumberList.get(pageNumberList.size() - 1).getAttribute("innerText"))) {
                lastPageNumberOnScreen = Integer.valueOf(pageNumberList.get(pageNumberList.size() - 2).getAttribute("innerText"));
            } else {
                lastPageNumberOnScreen = Integer.valueOf(pageNumberList.get(pageNumberList.size() - 1).getAttribute("innerText"));
            }
            return lastPageNumberOnScreen;
        } else if (paginationType.equals("Type2") && POHelper.isElementPresent(By.xpath(gridXpath + LAST_PAGE_TYPE_2_XPATH))) {
            WebElement pageNumberUL = getWebDriver().findElement(By.xpath(gridXpath + PAGE_NUMBERS_TYPE_2_XPATH));
            List<WebElement> pageNumberList = pageNumberUL.findElements(By.tagName("a"));
            Integer lastPageNumberOnScreen;
            if ("...".equalsIgnoreCase(pageNumberList.get(pageNumberList.size() - 1).getAttribute("innerText"))) {
                lastPageNumberOnScreen = Integer.valueOf(pageNumberList.get(pageNumberList.size() - 2).getAttribute("innerText"));
            } else {
                lastPageNumberOnScreen = Integer.valueOf(pageNumberList.get(pageNumberList.size() - 1).getAttribute("innerText"));
            }
            return lastPageNumberOnScreen;
        } else {
            return 1;
        }
    }

    public static Integer getCurrentPage(String gridXpath, String paginationType) {
        if (paginationType.equals("Type1") && POHelper.isElementPresent(By.xpath(gridXpath + CURRENT_PAGE_TYPE_1_XPATH))) {
            return Integer.valueOf(getWebDriver().findElement(By.xpath(gridXpath + CURRENT_PAGE_TYPE_1_XPATH)).getAttribute("innerText"));

        } else if (paginationType.equals("Type2") && POHelper.isElementPresent(By.xpath(gridXpath + CURRENT_PAGE_TYPE_2_XPATH))) {
            return Integer.valueOf(getWebDriver().findElement(By.xpath(gridXpath + CURRENT_PAGE_TYPE_2_XPATH)).getAttribute("innerText"));
        } else {
            return 1;
        }
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

    public static void assertGridHeaders(Grid grid, String[] expectedHeaders) throws IOException {

        for (int index = 0; index < expectedHeaders.length; index++){
            expectedHeaders[index] = localize(expectedHeaders[index]);
        }

        List<String> headers = Arrays.asList(expectedHeaders);

        assertTrue("Unexpected grid headers, expected " + expectedHeaders + " but found " + grid.getHeaders(),  grid.getHeaders().containsAll(headers));
    }

    public static void assertSortedAlphaNumerics(Grid grid, String header, boolean isAscending, java.util.Comparator<String> caseSensitive) throws Exception {
        List<String> original = grid.getColumn(header);

        if (isAscending) {
            List<String> ascendingOrder = new ArrayList<>(original);
            Collections.sort(ascendingOrder, caseSensitive);
            assertEquals("Alphanumeric column is not sorted: " + header + " expected: \n" + original + "\nbut got:\n" + ascendingOrder, ascendingOrder, original);
        } else {
            List<String> descendingOrder = new ArrayList<>(original);
            Collections.sort(descendingOrder, Collections.reverseOrder(caseSensitive));
            assertEquals("Alphanumeric column is not sorted: " + header + " expected: \n" + original + "\nbut got:\n" + descendingOrder, descendingOrder, original);
        }
    }

    public static void assertSortedDates(Grid grid, String header, String format, boolean isAscending) throws Exception {
        List<String> original = new ArrayList<String>();
        original = grid.getColumn(header);
        // if cannot find header try second row of headers
        if (original == null) {
            original = grid.getColumnSubtext(header);
        }
        Collections.replaceAll(original, null, "1 Jan 1901");
        Collections.replaceAll(original, "", "1 Jan 1901");

        List<String> originalFormatted = DateHelper.formatStringDates(original, format);

        List<String> sorted = DateHelper.sortDates(original, format, isAscending);

        assertEquals("Cell Date column is not sorted: " + header + " expected: \n" + sorted + "\nbut got:\n" + originalFormatted, sorted, originalFormatted);
    }

    public static void assertSortedNumerics(Grid grid, String header, boolean isAscending) throws Exception {
        List<String> column = grid.getColumn(header);
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
}