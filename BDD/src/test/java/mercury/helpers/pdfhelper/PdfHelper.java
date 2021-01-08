package mercury.helpers.pdfhelper;

import static mercury.runtime.ThreadManager.getWebDriver;
import java.io.BufferedInputStream;
import java.net.URL;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.itextpdf.io.IOException;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

@RunWith(SpringJUnit4ClassRunner.class)

public class PdfHelper {
    
    public WebDriver driver;

    public Document createPDFDocumentObject(String destPath) throws Throwable {
        // Creating a PdfDocument object
        String dest = destPath;
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);
        return doc;

    }

    public Table createPDFTable() {
        // Create Table
        float [] pointColumnWidths = {500f};
        Table table = new Table(pointColumnWidths);
        return table;
    }

    public void createImage(Document doc, String imageFile, float x, float y) throws Exception {
        ImageData imageData = ImageDataFactory.create(imageFile);
        Image image = new Image(imageData);
        image.setFixedPosition(x, y);
        doc.add(image);
    }

    public void addCellsToTable(Table table, Cell cell) {
        table.addCell(cell);
    }

    public void addTableToDocument(Document doc, Table table) {
        doc.add(table);
    }

    public Cell createCell(Cell cell) {
        return cell;
    }

    public void closeDocument(Document doc) {
        doc.close();
    }

    public void areaBreak(Document doc) {
        AreaBreak areaBreak = new AreaBreak();
        doc.add(areaBreak);
    }

    public void addParagraph(Document doc, String para) {
        Paragraph paragraph = new Paragraph(para);
        doc.add(paragraph);
    }

    public void addList(Document doc) {
        List list = new List();
        doc.add(list);
    }
    
    public String readPDFInURL(String pdfUrl) throws IOException, Exception {
       
        String output ="";
       
        try{
            getWebDriver().get(pdfUrl);
            URL url = new URL(pdfUrl);
            
            BufferedInputStream file = new BufferedInputStream(url.openStream());
            PDDocument document = null;
            try {
                document = PDDocument.load(file);
                output = new PDFTextStripper().getText(document);
                System.out.println(output);
            } finally {
                if (document != null) {
                    document.close();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return output;
    }
}
