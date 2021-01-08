package mercury.helpers.gridV3;

import java.util.ArrayList;
import java.util.List;

import mercury.helpers.gridV3.Cell;


public class Row extends Headers {

    private List<Cell> cells = new ArrayList<>();

    public List<Cell> getCells() {
        return cells;
    }

    public void addCell(Cell cell) {
        this.cells.add(cell);
    }

    public Cell getCell(String header) {
        int pos;
        if (headers.contains(header)) {
            pos = headers.indexOf(header);
        } else {
            pos = subHeaders.indexOf(header);
        }
        return pos == -1 ? null : cells.get(pos);
    }

    public Cell getCell(int pos) {
        return cells.get(pos);
    }

}
