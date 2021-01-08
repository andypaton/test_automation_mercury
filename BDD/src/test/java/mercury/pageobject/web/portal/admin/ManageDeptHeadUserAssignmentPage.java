package mercury.pageobject.web.portal.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class ManageDeptHeadUserAssignmentPage extends Base_Page<ManageDeptHeadUserAssignmentPage>{

    private static final Logger logger = LogManager.getLogger();
    // Page title
    private static final String PAGE_TITLE = "Manage Department Head User Assignment";

    //Main Content
    private static final String FORM_XPATH = "//form[@id = 'DHForm']";
    private static final String SELECT_DEPT_HEAD_XPATH = FORM_XPATH + "//select[@id='SelectedDepartmentHeadHelpdeskId']";
    private static final String SAVE_CHANGES_XPATH = FORM_XPATH + "//input[@value='Save Changes']";
    private static final String COLLEAGUE_TABLE_XPATH = FORM_XPATH + "//table[@id = 'department-head-user-management']";
    private static final String COLLEAGUE_XPATH = FORM_XPATH + "//td[contains(text(), '%s')]/following-sibling::td/input[@type='checkbox']";
    private static final String SEARCH_XPATH = FORM_XPATH + "//input[@type = 'search']";
    private static final String CONFIRM_XPATH = "//button[@id = 'closeDHSavedModal']";

    @FindBy(xpath = SELECT_DEPT_HEAD_XPATH)
    private WebElement selectDeptHead;

    @FindBy(xpath = SAVE_CHANGES_XPATH)
    private WebElement saveChanges;

    @FindBy(xpath = CONFIRM_XPATH)
    private WebElement confirm;

    @FindBy(xpath = SEARCH_XPATH)
    private WebElement search;

    public ManageDeptHeadUserAssignmentPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(FORM_XPATH));
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

    public void selectDepartmentHead(String deptHead) {
        Select select = new Select(selectDeptHead);
        select.selectByVisibleText(deptHead);
    }

    public String selectRandomDepartmentHead() {
        selectDeptHead.click();
        String selection = selectRandomOptionFromSelect(selectDeptHead);
        return selection;
    }

    public void saveChanges() {
        saveChanges.click();
        waitForAngularRequestsToFinish();
    }

    public void confirmSavedChanges() {
        confirm.click();
    }

    public void search(String str) {
        search.clear();
        search.sendKeys(str);
    }

    public Grid getColleaguesTable(){
        waitForAngularRequestsToFinish();
        return GridHelper.getGrid(COLLEAGUE_TABLE_XPATH);
    }

    public void selectColleague(String colleague) {
        WebElement we = driver.findElement(By.xpath(String.format(COLLEAGUE_XPATH, colleague)));
        if ( !we.isSelected() ) {
            we.click();
        }
    }

    public void deselectColleague(String colleague) {
        WebElement we = driver.findElement(By.xpath(String.format(COLLEAGUE_XPATH, colleague)));
        if ( we.isSelected() ) {
            we.click();
        }
    }

}
