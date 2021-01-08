package mercury.helpers.pdfhelper;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;

import mercury.databuilders.TestData;
import mercury.helpers.dbhelper.DbHelperInvoices;

@RunWith(SpringJUnit4ClassRunner.class)
public class PdfHelperOCRInvoice {

    @Autowired PdfHelper pdfHelper;
    @Autowired TestData testData;
    @Autowired DbHelperInvoices dbHelperInvoices;


    public void createSimpleOCRInvoice(String companyName, String invoiceNumber, String invoiceDate, String netValue, String taxAmount) throws Throwable {
        // Create pdf file in path
        testData.addStringTag("pdfPath", "\\src\\test\\resources\\portalfiles\\SimpleOCRInvoice.pdf");
        Document doc = pdfHelper.createPDFDocumentObject(System.getProperty("user.dir") + testData.getString("pdfPath"));
        // create table
        Table newTable = pdfHelper.createPDFTable();
        // add cells to table
        pdfHelper.addCellsToTable(newTable, new Cell().add(companyName));
        pdfHelper.addCellsToTable(newTable, new Cell().add(invoiceNumber));
        pdfHelper.addCellsToTable(newTable, new Cell().add(invoiceDate));
        pdfHelper.addCellsToTable(newTable, new Cell().add(netValue));
        pdfHelper.addCellsToTable(newTable, new Cell().add(taxAmount));
        // add table to document
        pdfHelper.addTableToDocument(doc, newTable);
        // close document
        pdfHelper.closeDocument(doc);

    }

}
