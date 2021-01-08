package mercury.pageobject.web.admin.refrigerantGasUsage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.pageHelpers.QuestionHelper;

import static mercury.helpers.Globalisation.setWeightLabel;

public class AdminRefrigerantSourceModal extends Base_Page<AdminRefrigerantSourceModal> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Refrigerant Source Modal";

    private static final String PAGE_BODY_XPATH = "//body[contains(@class, 'modal-open')]";
    private static final String PAGE_MODAL_XPATH = PAGE_BODY_XPATH + "//div[contains(@class, 'refrigerant-source__modal')]//div[contains(@class, 'modal-dialog')]//div[contains(@class, 'modal-content')]";
    private static final String PAGE_MODAL_BODY_XPATH = PAGE_MODAL_XPATH + "//div[contains(@class, 'modal-body')]";
    private static final String PAGE_MODAL_FOOTER_XPATH = PAGE_MODAL_XPATH + "//div[contains(@class, 'modal-footer')]";

    private static final String REFRIGERANT_FORM_XPATH = PAGE_MODAL_BODY_XPATH + "//form[@name='refrigerantForm']";

    private static final String REFRIGERANT_INSTALLED_XPATH = REFRIGERANT_FORM_XPATH + "//input[@id='installed']";
    private static final String REFRIGERANT_SOURCE_TOTAL_XPATH = REFRIGERANT_FORM_XPATH + "//label[contains(text(),'Total (%s)')]";

    private static final String FULLY_USED_YES_XPATH = PAGE_MODAL_XPATH + "//input[@id='fullyUsedYes']";
    private static final String FULLY_USED_NO_XPATH = PAGE_MODAL_XPATH + "//input[@id='fullyUsedNo']";
    private static final String GAS_AVAILABLE_XPATH = PAGE_MODAL_XPATH + "//label/b[contains(text(), '%s available')]";
    private static final String GAS_INSTALLED_XPATH = PAGE_MODAL_XPATH + "//input[@id='installed']";
    private static final String SURPLUS_XPATH = PAGE_MODAL_XPATH + "//label[contains(., 'Surplus (%s)')]";

    private static final String ADD_REFRIGERANT_SOURCE = PAGE_MODAL_FOOTER_XPATH + "//button[contains(@class,'btn-primary')]";
    private static final String CANCEL_REFRIGERANT_SOURCE = PAGE_MODAL_FOOTER_XPATH + "//button[contains(text(),'Cancel')]";

    @FindBy(xpath = ADD_REFRIGERANT_SOURCE)
    private WebElement addSource;

    @FindBy(xpath = CANCEL_REFRIGERANT_SOURCE)
    private WebElement cancel;

    @FindBy(xpath = FULLY_USED_YES_XPATH)
    private WebElement fullyUsedYes;

    @FindBy(xpath = FULLY_USED_NO_XPATH)
    private WebElement fullyUsedNo;

    @FindBy(xpath = GAS_INSTALLED_XPATH)
    private WebElement gasInstalled;


    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(REFRIGERANT_FORM_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    @Override
    public String getPageTitle() {
        return super.getPageTitle();
    }

    public AdminRefrigerantSourceModal(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    public String getFormXpath() {
        return REFRIGERANT_FORM_XPATH;
    }

    public void addSource() {
        addSource.click();
        waitForAngularRequestsToFinish();
    }

    public void cancel() {
        cancel.click();
        waitForAngularRequestsToFinish();
    }

    public void save() {
        addSource.click();
        waitForAngularRequestsToFinish();
    }

    public void setFullyUsed(boolean checked) {
        if (checked) {
            if (!fullyUsedYes.isSelected()) {
                driver.findElement(By.xpath(FULLY_USED_YES_XPATH + "/following-sibling::label")).click();
            }
        } else {
            if (!fullyUsedNo.isSelected()) {
                driver.findElement(By.xpath(FULLY_USED_NO_XPATH + "/following-sibling::label")).click();
            }
        }
        waitForAngularRequestsToFinish();
    }

    public boolean isFullyUsed() {
        return fullyUsedYes.isSelected();
    }

    public String getGasAvailable() {
        WebElement gasAvailable = driver.findElement(By.xpath(setWeightLabel(GAS_AVAILABLE_XPATH)));
        String str = setWeightLabel(" %s available");
        return gasAvailable.getText().replaceAll(str, "");
    }

    public String getSurplus() {
        WebElement surplus = driver.findElement(By.xpath(setWeightLabel(SURPLUS_XPATH)));
        String str = setWeightLabel("Surplus \\(%s\\) ");
        return surplus.getText().replaceAll(str, "");
    }

    public String getGasInstalled() {
        return gasInstalled.getAttribute("value");
    }

    public void enterGasInstalled(Double val) {
        gasInstalled.clear();

        if (val == Math.round(val)) {
            // strip decimal places if 0
            gasInstalled.sendKeys(String.valueOf(Math.round(val)));
        } else {
            gasInstalled.sendKeys(String.valueOf(val));
        }
        waitForAngularRequestsToFinish();
    }

    public QuestionHelper getQuestionHelper() {
        QuestionHelper questionHelper = PageFactory.initElements(driver, QuestionHelper.class);
        questionHelper.load(REFRIGERANT_FORM_XPATH);
        return questionHelper;
    }

    public String getTotal() {
        WebElement total = driver.findElement(By.xpath(setWeightLabel(REFRIGERANT_SOURCE_TOTAL_XPATH)));
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(total.getText());
        if (m.find()) {
            return m.group();
        } else {
            return null;
        }
    }

    public boolean isRefrigerantInstalledDispayed() {
        return isElementVisible(By.xpath(REFRIGERANT_INSTALLED_XPATH));
    }

    public boolean isDisplayed() {
        return isElementVisible(By.xpath(REFRIGERANT_FORM_XPATH));
    }

}
