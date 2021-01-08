package mercury.pageobject.web.helpdesk.incidents;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskJobPage;
import mercury.helpers.POHelper;

public class HelpdeskIncidentsLinkedJobsModal extends Base_Page<HelpdeskIncidentsLinkedJobsModal>{

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "Linked Jobs";

    private static final String LINKED_JOBS_MODAL_XPATH = "//div[@class='modal-dialog modal-lg']//div[@class='modal-content']";

    private static final String MODAL_HEADER_XPATH = LINKED_JOBS_MODAL_XPATH + "//h3[contains(text(), '"
            + PAGE_TITLE
            + "')]";
    private static final String MODAL_BODY_XPATH = LINKED_JOBS_MODAL_XPATH + "//div[@class='modal-body']";
    private static final String MODAL_FOOTER_XPATH = LINKED_JOBS_MODAL_XPATH + "//div[@class='modal-footer']";

    //BUTTONS
    private static final String OPEN_BUTTON_XPATH = MODAL_BODY_XPATH + "//table[contains(@class,'table-condensed')]//tr//td[contains(text(),'%s')]/following-sibling::td/button[text()='Open']";
    private static final String CLOSE_BUTTON_XPATH = MODAL_FOOTER_XPATH + "//button[contains(text(), 'Close')]";

    // GRID
    private static final String GRID_XPATH = MODAL_BODY_XPATH + "//table";

    @FindBy(xpath = LINKED_JOBS_MODAL_XPATH)
    private WebElement linkedJobModal;

    @FindBy(xpath = CLOSE_BUTTON_XPATH)
    private WebElement closeButton;

    @FindBy(xpath = OPEN_BUTTON_XPATH)
    private WebElement openButton;

    public HelpdeskIncidentsLinkedJobsModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(MODAL_HEADER_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public HelpdeskJobPage clickOpenButton(int refereneceNumber) throws Throwable {
        waitForKendoLoadingToComplete();
        WebElement button = waitForElement(By.xpath(String.format(OPEN_BUTTON_XPATH, refereneceNumber)), ELEMENT_IS_CLICKABLE);
        POHelper.clickJavascript(button);
        POHelper.refreshPage();
        return PageFactory.initElements(driver, HelpdeskJobPage.class).get();
    }

    public HelpdeskViewIncidentPage closeLinkedJobsModal() {
        waitForElement(By.xpath(CLOSE_BUTTON_XPATH), ELEMENT_IS_CLICKABLE);
        closeButton.click();
        return PageFactory.initElements(driver, HelpdeskViewIncidentPage.class).get();
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

}
