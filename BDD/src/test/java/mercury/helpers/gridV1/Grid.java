package mercury.helpers.gridV1;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class Grid {

    List<String> headers = new ArrayList<>();
    List<Row> rows = new ArrayList<>();

    public class Row {
        public List<String> cells = new ArrayList<>();

        public List<String> getCells() {
            return cells;
        }

        public void addCell(String cell) {
            this.cells.add(cell);
        }

        public String getCell(String header) {
            int pos = headers.indexOf(header);
            return pos == -1 ? null : cells.get(pos);
        }

        public String getCell(int pos) {
            return cells.get(pos);
        }
    }


    public List<String> getHeaders() {
        return headers;
    }

    public void addHeader(String header) {
        this.headers.add(header);
    }

    public List<Row> getRows() {
        return rows;
    }

    public void addRow(Row row) {
        this.rows.add(row);
    }

}
