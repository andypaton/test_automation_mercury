package mercury.pageobject.web.portal.invoices;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class OrdersAwaitingInvoicePage extends Base_Page<OrdersAwaitingInvoicePage> {

	private static final Logger logger = LogManager.getLogger();

	// Page title
	private static final String PAGE_TITLE = "Orders Awaiting Invoice";

	//Main content
	private static final String ORDER_DETAIL_CONTAINER_XPATH = ".//div[contains(@class,'body-content')]";
	private static final String ORDERS_AWAITING_INVOICE_HEADER_XPATH = ORDER_DETAIL_CONTAINER_XPATH + "//h1[contains(text(), '" + PAGE_TITLE + "')]";
	private static final String ORDERS_SEARCH_XPATH = ".//label[contains(text(), 'Search')]//input";


	//Table
	private static final String ORDERS_GRID_XPATH = ".//table[@id='awaiting-supplier-invoice']";
	private static final String SUPPLY_ORDERS_GRID_XPATH = ".//table[contains(@id,'invoice') and contains(@class,'dataTable no-footer')]";

	//Table rows
	private static final String TABLE_ROW_TYPE_XPATH_SUFFIX =  "//td[contains(text(),'%s')]";

	//Grid
	private static final String GRID_XPATH = ".//div[@id='awaiting-supplier-invoice_wrapper']";
	private static final String SUPPLY_ONLY_GRID_XPATH = ".//div[contains(@id,'invoice_wrapper') and @class='dataTables_wrapper no-footer']";
	private static final String TABLE_INFO_SUPPLY_ONLY_XPATH = ORDER_DETAIL_CONTAINER_XPATH + "//div[@class='dataTables_info' and contains(@id, 'invoice_info')]";

	//Buttons
	private static final String CONSOLIDATED_INVOICE_BUTTON_XPATH = ORDER_DETAIL_CONTAINER_XPATH + "//input[@id='consolidatedInvoice']";

	@FindBy(xpath = ORDERS_SEARCH_XPATH)
	private WebElement searchBox;

	@FindBy(xpath = TABLE_INFO_SUPPLY_ONLY_XPATH)
	private WebElement tableInfo;

	@FindBy(xpath = CONSOLIDATED_INVOICE_BUTTON_XPATH)
	private WebElement consolidatedInvoice;

	public OrdersAwaitingInvoicePage(WebDriver driver) {
		super(driver);
	}

	@Override
	protected void isLoaded() throws Error {
		logger.info(PAGE_TITLE + " isloaded");
		try {
			POHelper.isLoaded().isFluentElementIsVisible(By.xpath(ORDERS_AWAITING_INVOICE_HEADER_XPATH));
			logger.info(PAGE_TITLE + " isloaded success");
		} catch (NoSuchElementException ex) {
			logger.info(PAGE_TITLE + "isloaded error");
			throw new AssertionError();
		}
	}

	@Override
	protected void load() {
		logger.info(PAGE_TITLE + " Load");
	}

	public Boolean isPageLoaded() {
		return this.isElementPresent(By.xpath(ORDERS_AWAITING_INVOICE_HEADER_XPATH));
	}

	public OrdersAwaitingInvoicePage searchOrders(String searchQuery) {
		this.searchBox.sendKeys(searchQuery);
		this.searchBox.sendKeys(Keys.RETURN);
		waitForAngularRequestsToFinish();
		return this;
	}

	public void openOrderAwaitingInvoice(String jobReference) throws InterruptedException {
		By by = By.xpath(ORDERS_GRID_XPATH + String.format(TABLE_ROW_TYPE_XPATH_SUFFIX, jobReference ));
		POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
		driver.findElement(by).click();
		waitForAngularRequestsToFinish();
	}

	public UploadInvoiceDocumentPage openSupplierOrderAwaitingInvoice(String jobReference) {
		By by = By.xpath(SUPPLY_ORDERS_GRID_XPATH + String.format(TABLE_ROW_TYPE_XPATH_SUFFIX, jobReference ));
		POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
		driver.findElement(by).click();
		waitForAngularRequestsToFinish();
		return PageFactory.initElements(driver, UploadInvoiceDocumentPage.class);
	}

	public Grid getGrid() {
		return GridHelper.getGrid(GRID_XPATH);
	}

	public Grid getSupplyOnlyGrid() {
		return GridHelper.getGrid(SUPPLY_ONLY_GRID_XPATH);
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

	public Boolean isConsolidatedInvoiceButtonVisible() {
		return this.isElementPresent(By.xpath(CONSOLIDATED_INVOICE_BUTTON_XPATH));
	}

	public void clickConsolidatedInvoice() {
		consolidatedInvoice.click();
	}

}
