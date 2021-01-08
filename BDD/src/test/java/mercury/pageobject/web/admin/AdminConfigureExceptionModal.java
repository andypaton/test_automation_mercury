package mercury.pageobject.web.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class AdminConfigureExceptionModal extends Base_Page<AdminConfigureExceptionModal> {

    private static final Logger logger = LogManager.getLogger();

    private static final String CONFIGURE_EXCEPTION_MODAL_XPATH = "//div[contains(@class, 'modal-dialog') and contains(@class, 'modal-md')]";
    private static final String CONFIGURE_EXCEPTION_MODAL_CONTENT_XPATH = CONFIGURE_EXCEPTION_MODAL_XPATH + "//div[@class='modal-content']";

    private static final String EXCEPTION_DROPDOWN_XPATH = CONFIGURE_EXCEPTION_MODAL_CONTENT_XPATH + "//div[@class='exception-wrapper__exception']//span[contains(@class, 'exception-wrapper__dropdown')]";
    private static final String EXCEPTION_OPTION_XPATH = "//li[contains(text(), '%s')]";

    private static final String CALLOUT_TYPE_LABEL_XPATH = CONFIGURE_EXCEPTION_MODAL_CONTENT_XPATH + "//label[contains(text(), '%s')]";

    private static final String BUTTON_XPATH = CONFIGURE_EXCEPTION_MODAL_CONTENT_XPATH + "//button[contains(text(), '%s')]";


    @FindBy(xpath = EXCEPTION_DROPDOWN_XPATH)
    private WebElement exceptionDropdown;


    public AdminConfigureExceptionModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(CONFIGURE_EXCEPTION_MODAL_XPATH));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectExceptionDropdownOption(String site) {
        exceptionDropdown.click();
        waitForAngularRequestsToFinish();
        WebElement option = driver.findElement(By.xpath(String.format(EXCEPTION_OPTION_XPATH, site)));
        option.click();
        waitForAngularRequestsToFinish();
    }

    public void selectCalloutType(String calloutType) {
        WebElement type = driver.findElement(By.xpath(String.format(CALLOUT_TYPE_LABEL_XPATH, calloutType)));
        type.click();
        waitForAngularRequestsToFinish();
    }

    public void clickButton(String buttonName) {
        WebElement button = driver.findElement(By.xpath(String.format(BUTTON_XPATH, buttonName)));
        button.click();
        waitForAngularRequestsToFinish();
    }
}
