package mercury.pageobject.web.admin.companies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class AdminEditCompanyAdditionalDetailsPartial extends Base_Page<AdminEditCompanyAdditionalDetailsPartial>{

    private static final Logger logger = LogManager.getLogger();

    private static final String EDIT_COMPANY_PAGE_CONTAINER_XPATH = "//div[@class = 'container']";
    private static final String EDIT_COMPANY_PAGE_CONTENT_CONTAINER_XPATH = EDIT_COMPANY_PAGE_CONTAINER_XPATH + "//div[@class = 'admin-action__content']";

    private static final String ADDITIONAL_DETAILS_SUBHEADER_XPATH = EDIT_COMPANY_PAGE_CONTENT_CONTAINER_XPATH + "//h3[contains(.,'Additional details')]";
    private static final String ADDITIONAL_DETAILS_SECTION_XPATH = ADDITIONAL_DETAILS_SUBHEADER_XPATH + "//parent::div//parent::div[@class = 'admin-action__section ng-scope']";


    //labels
    private static final String MAKE_PRIMARY_LABEL_XPATH = ADDITIONAL_DETAILS_SECTION_XPATH + "//label[contains(text(),'Make Company Primary?')]";

    //Buttons
    private static final String MAKE_PRIMARY_YES_RADIO_BUTTON_XPATH = MAKE_PRIMARY_LABEL_XPATH + "/following-sibling::div//label[.='Yes']";
    private static final String MAKE_PRIMARY_NO_RADIO_BUTTON_XPATH = MAKE_PRIMARY_LABEL_XPATH + "/following-sibling::div//label[.='No']";

    @FindBy(xpath = MAKE_PRIMARY_YES_RADIO_BUTTON_XPATH)
    private WebElement primaryYesRadioButton;

    @FindBy(xpath = MAKE_PRIMARY_NO_RADIO_BUTTON_XPATH)
    private WebElement primaryNoRadioButton;


    public AdminEditCompanyAdditionalDetailsPartial(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(ADDITIONAL_DETAILS_SUBHEADER_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void clickPrimaryYes() {
        waitForAnimation();
        primaryYesRadioButton.click();
    }

    public void clickPrimaryNo() {
        waitForAnimation();
        primaryNoRadioButton.click();
    }

}
