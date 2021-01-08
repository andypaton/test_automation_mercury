package mercury.helpers;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class ExcelUtils {
    private static XSSFSheet ExcelWSheet;
    private static XSSFWorkbook ExcelWBook;
    private static XSSFCell Cell;
    private static XSSFRow Row;


    public void setExcelFile(String pathname, String worksheet) throws Exception {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(pathname).getFile());
            FileInputStream ExcelFile = new FileInputStream(file);
            ExcelWBook = new XSSFWorkbook(ExcelFile);
            ExcelWSheet = ExcelWBook.getSheet(worksheet);
        } catch (Exception e) {
            throw (e);
        }
    }

    public boolean worksheetFound() {
        return ExcelWSheet != null;
    }

    public String getCell(int rowNum, int ColNum) throws Exception {
        try {
            Cell = ExcelWSheet.getRow(rowNum).getCell(ColNum);
            String CellData = Cell.getStringCellValue();
            return CellData;
        } catch (Exception e) {
            return "";
        }
    }

    public String getCell(int rowNum, String header) throws Exception {
        int pos = getHeaders().indexOf(header);
        assertTrue("Header not found: " + header + ". Headers found: " + getHeaders(), pos != -1);
        try {
            Cell = ExcelWSheet.getRow(rowNum).getCell(pos);
            return Cell.getStringCellValue();
        } catch (Exception se) {
            try {
                return StringHelper.trimZeros(String.valueOf(Cell.getNumericCellValue()));
            } catch (Exception ne) {
                return "";
            }
        }
    }

    public List<String> getHeaders() throws Exception {
        List<String> headers = new ArrayList<>();
        try {
            XSSFRow row = ExcelWSheet.getRow(0);
            for (int i = 0; i < row.getLastCellNum(); i++) {
                headers.add(row.getCell(i).getStringCellValue());
            }
            return headers;
        } catch (Exception e) {
            return null;
        }
    }

    public int getNubmerOfRowsInWorksheet() {
        int maxRow = ExcelWSheet.getLastRowNum();
        return maxRow;
    }

    public List<String> getColumn(String header) throws Exception {
        try {
            int pos = getHeaders().indexOf(header);
            List<String> column = new ArrayList<>();
            int maxRow = ExcelWSheet.getLastRowNum();
            for (int i = 1; i < maxRow; i++) {
                XSSFRow row = ExcelWSheet.getRow(i);
                column.add(row.getCell(pos).getStringCellValue());
            }
            return column;
        } catch (Exception e) {
            return null;
        }
    }

    public int getLastRowNum() throws Exception {
        return ExcelWSheet.getLastRowNum();

    }

    public void setCell(String Result, int RowNum, int ColNum, File Path) throws Exception {
        try {
            Row = ExcelWSheet.createRow(RowNum - 1);
            Cell = Row.createCell(ColNum - 1);
            Cell.setCellValue(Result);
            FileOutputStream fileOut = new FileOutputStream(Path);
            ExcelWBook.write(fileOut);
            fileOut.flush();
            fileOut.close();
        } catch (Exception e) {
            throw (e);
        }
    }

}
