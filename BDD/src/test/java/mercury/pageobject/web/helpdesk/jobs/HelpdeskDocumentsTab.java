package mercury.pageobject.web.helpdesk.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.State;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class HelpdeskDocumentsTab extends Base_Page<HelpdeskDocumentsTab> {

        private static final Logger logger = LogManager.getLogger();

        private static final String ACTIVE_TAB_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";

        private static final String DOCUMENTS_TAB_XPATH = ACTIVE_TAB_XPATH + "//li//a[text()='Documents']";
        private static final String DOCUMENTS_TAB_GRID_XPATH = ACTIVE_TAB_XPATH + "//div[@id='poGrid']";
        private static final String DOCUMENTS_TAB_DOWNLOAD_BUTTON_XPATH = DOCUMENTS_TAB_GRID_XPATH + "//td//button[text()='Download']";
        
        @FindBy(xpath = DOCUMENTS_TAB_DOWNLOAD_BUTTON_XPATH)
        private WebElement downloadButton;
        
       
        public HelpdeskDocumentsTab(WebDriver driver) {
            super(driver);
        }

        @Override
        protected void isLoaded() throws Error {

            try {
                Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(DOCUMENTS_TAB_XPATH)).isDisplayed());
                logger.info("Page loaded");

            } catch(NoSuchElementException ex){

                logger.info("Page failed to load");
                throw new AssertionError();
            }
        }
        
        public Grid getGrid() {
            waitForElement(By.xpath(DOCUMENTS_TAB_GRID_XPATH), State.ELEMENT_IS_VISIBLE);
            return GridHelper.getGrid(DOCUMENTS_TAB_GRID_XPATH);
        }
        
        public boolean isDownloadButtonDisplayed() {
            return downloadButton.isEnabled();      
        }
}
