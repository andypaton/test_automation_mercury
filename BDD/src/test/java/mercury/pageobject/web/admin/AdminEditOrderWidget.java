package mercury.pageobject.web.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class AdminEditOrderWidget extends Base_Page<AdminEditOrderWidget> {

    private static final Logger logger = LogManager.getLogger();

    private static final String EDIT_FIELD_XPATH = "//label[text()='%s']/..//following-sibling::div//input";
    private static final String UPDATE_BUTTON_XPATH = "//a[contains(@class, 'k-button') and text()='Update']";


    @FindBy(xpath = UPDATE_BUTTON_XPATH)
    private WebElement updateButton;


    public AdminEditOrderWidget(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            waitWhileBusy();
            driver.findElement(By.xpath("//span[text()='Edit']"));
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String getValueInsideTextBox(String field) {
        WebElement textBox = driver.findElement(By.xpath(String.format(EDIT_FIELD_XPATH, field)));
        return textBox.getAttribute("value");
    }

    public void enterValueIntoTextBox(String field, String value) {
        WebElement textBox = driver.findElement(By.xpath(String.format(EDIT_FIELD_XPATH, field)));
        textBox.sendKeys(value);
    }

    public void removeValueFromTextBox(String field) {
        WebElement textBox = driver.findElement(By.xpath(String.format(EDIT_FIELD_XPATH, field)));
        textBox.clear();
    }

    public void clickUpdateButton() {
        updateButton.click();
        waitForAngularRequestsToFinish();
        POHelper.waitWhileBusy();
    }
}
