package mercury.pageobject.web.portal.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import mercury.helpers.State;

public class JobsAwaitingReallocationForResourcePage extends JobsAwaitingReallocationPage {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE_XPATH = "//h1[contains(text(), 'Jobs Awaiting Reallocation for')]";

    private static String pageTitle;

    // Page methods
    public JobsAwaitingReallocationForResourcePage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            WebElement we = waitForElement(By.xpath(PAGE_TITLE_XPATH), State.ELEMENT_IS_VISIBLE);
            pageTitle = we.getText();
            logger.info("Page isloaded success: " + pageTitle);
        } catch (NoSuchElementException ex) {
            logger.info("Jobs Awaiting Reallocation For Resource Page isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    public String getPageHeaderText() {
        return pageTitle;
    }

    @Override
    public String getPageTitle() {
        return super.getPageTitle();
    }

}
