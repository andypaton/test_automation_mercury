package mercury.pageobject.web.portal.invoices;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class InvoicesAndCreditsAllOrders extends Base_Page<InvoicesAndCreditsAllOrders> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "All Orders";

    // Page elements
    private static final String ALL_ORDERS_PAGE_XPATH = "//div[contains(@class,'body-container')]//h1[contains(text(),'All Orders')]";
    private static final String ALL_ORDERS_GRID_XPATH = ALL_ORDERS_PAGE_XPATH + "/following-sibling::div//div[contains(@id,'all-orders')]//table[@id='all-orders']";

    // Table pager
    private static final String TABLE_INFO_XPATH = ALL_ORDERS_PAGE_XPATH + "/following-sibling::div//div[@id='all-orders_wrapper']//div[@class='dataTables_info']";

    @FindBy(xpath = TABLE_INFO_XPATH)
    private WebElement tableInfo;

    public InvoicesAndCreditsAllOrders(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(ALL_ORDERS_PAGE_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    public Boolean isPageLoaded() {
        return this.isElementClickable(By.xpath(ALL_ORDERS_PAGE_XPATH));
    }

    public Grid getGrid() {
        waitForAngularRequestsToFinish();
        return GridHelper.getGrid(ALL_ORDERS_GRID_XPATH);
    }

    public Integer getTotalRows(int index) {
        ArrayList<Integer> pageInfo = new ArrayList<Integer>();
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(tableInfo.getText().replace(",", ""));
        while (m.find()) {
            pageInfo.add(Integer.valueOf(m.group()));
        }
        return pageInfo.get(index);
    }
}
