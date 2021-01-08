package mercury.pageobject.web.helpdesk.jobs;

import static mercury.runtime.ThreadManager.getWebDriver;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.gridV3.Cell;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.helpdesk.incidents.HelpdeskLogAnIncidentPage;

public class HelpdeskLinkedIncidentsModal extends Base_Page<HelpdeskLinkedIncidentsModal>{

    private static final Logger logger = LogManager.getLogger();

    private static final String MODAL_XPATH = "//div[@class='modal-dialog modal-xl']";
    private static final String MODAL_CONTENT_XPATH = MODAL_XPATH + "//div[@class='modal-content']";
    private static final String LINKED_INCIDENT_MODAL_XPATH = MODAL_CONTENT_XPATH + "//div[contains(@class, 'modal-body') and contains(@class, 'forced-incident__modal')]";
    private static final String LINKED_INCIDENT_MODAL_TEXT_XPATH = LINKED_INCIDENT_MODAL_XPATH + "//p";

    //BUTTONS
    private static final String LINK_BUTTONS_XPATH = LINKED_INCIDENT_MODAL_XPATH + "//button[contains(text(), 'Link')]";
    private static final String CREATE_NEW_INCIDENT_BUTTON_XPATH = LINKED_INCIDENT_MODAL_XPATH + "//button[contains(text(), 'Create New Incident')]";
    private static final String CLOSE_BUTTON_XPATH = LINKED_INCIDENT_MODAL_XPATH + "//button[contains(text(), 'Close')]";

    //GRID
    private static final String GRID_XPATH = "//div[contains(@class, 'modal-body') and contains(@class, 'forced-incident__modal')]";
    private static final String GRID_CSS = "div[class='modal-body forced-incident__modal']";
    private static final String HEADER_ROW_CSS = "div[class='row no-margin']";
    private static final String DATA_ROWS_CSS = "div[class='row forced-incident__row']";
    private static final String CELLS_CSS = "div.row div";


    @FindBy(xpath = CREATE_NEW_INCIDENT_BUTTON_XPATH)
    private WebElement createNewIncidentButton;

    @FindBy(xpath = CLOSE_BUTTON_XPATH)
    private WebElement closeButton;

    public HelpdeskLinkedIncidentsModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try{
            waitForLoadingToComplete();
            Assert.assertTrue("Modal is not displayed", driver.findElement(By.xpath(LINKED_INCIDENT_MODAL_XPATH)).isDisplayed());
            logger.info("Page loaded");
        }catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public List<String> getTextMessagesOnLinkedIncidentModal() {
        List<String> textMessages = new ArrayList<>();

        List<WebElement> textElements = driver.findElements(By.xpath(LINKED_INCIDENT_MODAL_TEXT_XPATH));
        for (WebElement textMessage : textElements) {
            String optionText = textMessage.getText();
            if (optionText.length() > 0) {
                textMessages.add(optionText);
            }
        }
        return textMessages;
    }

    public Grid getLinkedIncidentTable() {
        Grid grid = new Grid(GRID_XPATH);
        String html = getWebDriver().findElement(By.xpath(GRID_XPATH)).getAttribute("outerHTML");
        Document doc = Jsoup.parse(html);

        // get headers
        Element table = doc.select(GRID_CSS).get(0);
        Element headerRow = table.select(HEADER_ROW_CSS).get(0);
        Elements headers = headerRow.select(CELLS_CSS);
        for (Element th : headers) {
            grid.addHeader(th.text());
        }
        logger.debug("Linked Incident table headers: " + grid.getHeaders());

        // get rows
        Elements rows = table.select(DATA_ROWS_CSS);
        for (Element tr : rows) {
            Row row = new Row();
            Elements cells = tr.select(CELLS_CSS);
            for (Element td : cells) {
                Cell cell = new Cell();

                if (td.html().contains("button")) {
                    // cell has web element
                    String cssSelector = td.cssSelector().replaceAll("html > body > ", "");
                    cell.addWebElement( getWebDriver().findElement(By.cssSelector(cssSelector)));
                } else {
                    cell.setText(td.text());
                }

                row.addCell(cell);
            }
            grid.addRow(row);
        }
        return grid;
    }

    public Boolean isCloseButtonEnabled() {
        return isElementClickable(By.xpath(CLOSE_BUTTON_XPATH));
    }

    // Linking the first incident displayed in the linked incident modal
    public void clickLinkButton() {
        List<WebElement> linkButtons = driver.findElements(By.xpath(LINK_BUTTONS_XPATH));
        linkButtons.get(0).click();
        waitForAngularRequestsToFinish();
    }

    public HelpdeskJobPage closeLinkedIncidentsModal() {
        closeButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskJobPage.class).get();
    }

    public HelpdeskLogAnIncidentPage clickCreateNewIncidentButton() {
        createNewIncidentButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskLogAnIncidentPage.class).get();
    }
}
