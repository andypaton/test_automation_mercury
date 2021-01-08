package mercury.helpers.gridV2;

import static mercury.runtime.ThreadManager.getWebDriver;
import static mercury.helpers.Globalisation.localize;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import mercury.helpers.POHelper;

@Deprecated
public class Grid {

    private List<String> headers = new ArrayList<>();
    private List<String> subHeaders = new ArrayList<>();
    private List<Row> rows = new ArrayList<>();
    private String paginationType;

    public class Cell {
        private String text;
        private String value;
        private String subText;
        private List<String> controls = new ArrayList<>();
        private String cellLocation;

        public String getText() {
            // Return the text in the cell or if there is a text control return that text
            for (int index = 0; index < controls.size(); index++) {
                if (controls.get(index) != null) {
                    WebElement element = getWebDriver().findElement(By.cssSelector(controls.get(index).toString()));
                    if (element.getAttribute("type").equalsIgnoreCase("text")){
                        return element.getAttribute("value");
                    } else if (element.getAttribute("class").contains("empty")) {
                        return element.getAttribute("value");
                    } else {
                        return text;
                    }

                }
            }
            return text;
        }

        public String getValue() {
            for (int index = 0; index < controls.size(); index++) {
                if (controls.get(index) != null) {
                    WebElement element = getWebDriver().findElement(By.cssSelector(controls.get(index).toString()));
                    if (element.getAttribute("type").equalsIgnoreCase("number")) {
                        return element.getAttribute("value");
                    } else {
                        return value;
                    }
                }
            }
            return value;
        }

        public void setText(String top) {
            this.text = top;
        }

        public void addCellLocation(String css) {
            this.cellLocation = css;
        }

        public String getCellLocation() {
            return this.cellLocation;
        }

        public String getSubText() {
            return subText;
        }

        public void setSubText(String bottom) {
            this.subText = bottom;
        }

        public void addCellControl(String css) {
            controls.add(css);
        }

        public boolean isCellDisabled() {
            for (String control : controls) {
                if (control == null) {
                    return true;
                }
            }
            return false;
        }

        public List<String> getButtons() {
            List<String> column = new ArrayList<>();
            for (String control : controls) {
                if (control.endsWith("button")) {
                    column.add(getWebDriver().findElement(By.cssSelector(control)).getText());
                }
            }
            return column;
        }

        public List<String> getCheckboxes() {
            List<String> column = new ArrayList<>();
            for (String control : controls) {
                if (getWebDriver().findElement(By.cssSelector(control)).getAttribute("class").contains("showCheckbox")) {
                    column.add(getWebDriver().findElement(By.cssSelector(control)).getText());
                }
            }
            return column;
        }

        public void clickButton(String name) {
            controls.removeAll(Collections.singleton(null));
            for (String control : controls) {
                if (getWebDriver().findElement(By.cssSelector(control)).getText().equalsIgnoreCase(name)) {
                    POHelper.click(By.cssSelector(control));
                    break;
                }
            }
        }

        public void clickSelect() {
            controls.removeAll(Collections.singleton(null));
            for (String control : controls) {
                if (getWebDriver().findElement(By.cssSelector(control)).getAttribute("class").contains("empty")) {
                    POHelper.clickJavascript(By.cssSelector(control));
                    break;
                }
            }
        }

        public void sendText(String text) {
            controls.removeAll(Collections.singleton(null));
            for (String control : controls) {
                if(getWebDriver().findElement(By.cssSelector(control)).getAttribute("class").contains("textbox")) {
                    getWebDriver().findElement(By.cssSelector(control)).sendKeys(text);
                    break;
                } else if (getWebDriver().findElement(By.cssSelector(control)).getAttribute("type").contains("text")) {
                    getWebDriver().findElement(By.cssSelector(control)).clear();
                    getWebDriver().findElement(By.cssSelector(control)).sendKeys(text);
                    break;
                } else if (getWebDriver().findElement(By.cssSelector(control)).getAttribute("type").contains("number")) {
                    getWebDriver().findElement(By.cssSelector(control)).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.BACK_SPACE), text);
                    break;
                }
            }
        }

        public void clickCheckbox(String name) {
            for (String control : controls) {
                if (name.equalsIgnoreCase("")) {
                    if (getWebDriver().findElement(By.cssSelector(control)).getAttribute("class").contains("showCheckbox")) {
                        POHelper.clickJavascript(By.cssSelector(control));
                        break;
                    } else if (getWebDriver().findElement(By.cssSelector(control)).getAttribute("type").contains("checkbox")) {
                        WebElement tickBox = getWebDriver().findElement(By.cssSelector(control));
                        POHelper.clickJavascript(tickBox);
                        break;
                    }
                } else {
                    if (getWebDriver().findElement(By.cssSelector(control)).getText().equalsIgnoreCase(name)) {
                        if (getWebDriver().findElement(By.cssSelector(control)).getAttribute("class").contains("showCheckbox")) {
                            POHelper.clickJavascript(By.cssSelector(control));
                            break;
                        }
                    }
                }
            }
        }

        public boolean isCheckBoxEnabled() {
            for (String control : controls) {
                if (getWebDriver().findElement(By.cssSelector(control)).getAttribute("class").contains("showCheckbox")) {
                    return getWebDriver().findElement(By.cssSelector(control)).isEnabled();
                } else if (getWebDriver().findElement(By.cssSelector(control)).getAttribute("type").contains("checkbox")) {
                    return getWebDriver().findElement(By.cssSelector(control)).isEnabled();
                }
            }
            return false;
        }

        public boolean isCheckBoxChecked() {
            for (String control : controls) {
                if (getWebDriver().findElement(By.cssSelector(control)).getAttribute("class").contains("showCheckbox")) {
                    return getWebDriver().findElement(By.cssSelector(control)).isSelected();
                } else if (getWebDriver().findElement(By.cssSelector(control)).getAttribute("type").contains("checkbox")) {
                    return getWebDriver().findElement(By.cssSelector(control)).isSelected();
                }
            }
            return false;
        }
    }

    public class Row {
        private List<Cell> cells = new ArrayList<>();

        public List<Cell> getCells() {
            return cells;
        }

        public void addCell(Cell cell) {
            this.cells.add(cell);
        }

        public Cell getCell(String header) throws Exception {
            int pos = headers.indexOf(localize(header));
            if (pos == -1) {
                throw new Exception("Header not found: " + localize(header));
            }
            return cells.get(pos);
        }

        public Cell getCell(int pos) {
            return cells.get(pos);
        }

        public Cell getSubCell(String subHeader) throws Exception {
            int pos = subHeaders.indexOf(localize(subHeader));
            if (pos == -1) {
                throw new Exception("Sub-Header not found: " + localize(subHeader));
            }
            return cells.get(pos);
        }

        public Cell getDetail() {
            int pos = headers.indexOf("detail");
            return cells.get(pos);
        }

        public void clickCell(String header) throws Exception {
            int pos = headers.indexOf(localize(header));
            if (pos == -1) {
                throw new Exception("Header not found: " + localize(header));
            }
            getWebDriver().findElement(By.cssSelector(cells.get(pos).cellLocation)).click();
        }

    }

    public List<String> getHeaders() {
        return headers;
    }

    public void addHeader(String header) {
        this.headers.add(header);
    }

    // added for subHeaders
    public List<String> getSubHeaders() {
        return subHeaders;
    }

    public void addSubHeader(String header) {
        this.subHeaders.add(header);
    }

    public List<Row> getRows() {
        return rows;
    }

    public void addRow(Row row) {
        this.rows.add(row);
    }

    public int getUsedRowsSize() {
        int rowSize = this.getRows().size();
        List<Row> row = this.getRows();
        List<Cell> cell = row.get(0).cells;
        // If there is a null cell location, set row size to 0
        for (int i = 0; i < cell.size(); i++) {
            if(cell.get(i).cellLocation == null) {
                rowSize = 0;
                break;
            }
        }
        return rowSize;
    }

    public List<String> getColumn(String header) throws Exception{
        List<String> column = new ArrayList<>();
        for (Row row : rows) {
            column.add(row.getCell(header).text);
        }
        return column;
    }

    public List<String> getColumnSubtext(String header) throws Exception {
        List<String> column = new ArrayList<>();
        for (Row row : rows) {
            column.add(row.getCell(header).getSubText());
        }
        return column;
    }

    public void setPaginationType(String paginationType) {
        this.paginationType = paginationType;
    }

    public String getPaginationType() {
        return paginationType;
    }
}
