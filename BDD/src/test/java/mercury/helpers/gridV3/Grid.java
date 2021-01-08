package mercury.helpers.gridV3;

import java.util.ArrayList;
import java.util.List;

public class Grid extends Headers{

    private String gridXpath;
    private List<Row> rows = new ArrayList<>();
    private Row filterRow;
    private List<Row> footerRows = new ArrayList<>();

    public Grid(String gridXpath) {
        this.gridXpath = gridXpath;
        headers = new ArrayList<>();
        subHeaders = new ArrayList<>();
    }

    public List<Row> getRows() {
        return rows;
    }

    public void addRow(Row row) {
        this.rows.add(row);
    }

    public Row getFilterRow() {
        return filterRow;
    }

    public void addFilterRow(Row filterRow) {
        this.filterRow = filterRow;
    }

    public List<Row> getFooterRows() {
        return footerRows;
    }

    public void addFooterRow(Row row) {
        this.footerRows.add(row);
    }

    /**
     * Return text for column
     * @param header
     * @return
     * @throws Exception
     */
    public List<String> getColumnText(String header) throws Exception{
        List<String> column = new ArrayList<>();
        for (Row row : rows) {
            String text = row.getCell(header).getText() == null ? "" : row.getCell(header).getText();
            String columnText = text;
            column.add(columnText.trim());
        }
        return column;
    }

    /**
     * Return subText for column
     * @param header
     * @return
     * @throws Exception
     */
    public List<String> getColumnSubText(String header) throws Exception {
        List<String> column = new ArrayList<>();
        for (Row row : rows) {
            String subText = row.getCell(header).getSubText() == null ? "" : row.getCell(header).getSubText();
            column.add(subText.trim());
        }
        return column;
    }

    /**
     * Return text + subText for column
     * @param header
     * @return
     * @throws Exception
     */
    public List<String> getColumnTextAndSubText(String header) throws Exception{
        List<String> column = new ArrayList<>();
        for (Row row : rows) {
            String text = row.getCell(header).getText() == null ? "" : row.getCell(header).getText();
            String subText = row.getCell(header).getSubText() == null ? "" : row.getCell(header).getSubText();
            String columnText = null;
            if (!subText.isEmpty()) {
                columnText = text + ", " + subText;
            } else {
                columnText = text;
            }
            column.add(columnText.trim());
        }
        return column;
    }

    /**
     * Return text if not blank, or subText, for column
     * @param header
     * @return
     * @throws Exception
     */
    public List<String> getColumnTextOrSubText(String header) throws Exception{
        List<String> column = new ArrayList<>();
        for (Row row : rows) {
            String text = row.getCell(header).getText() == null ? "" : row.getCell(header).getText();
            String subText = row.getCell(header).getSubText() == null ? "" : row.getCell(header).getSubText();
            String columnText = text.isEmpty() ? subText : text;
            column.add(columnText.trim());
        }
        return column;
    }

    public String getGridXpath() {
        return gridXpath;
    }

    public void setGridXpath(String gridXpath) {
        this.gridXpath = gridXpath;
    }

}
