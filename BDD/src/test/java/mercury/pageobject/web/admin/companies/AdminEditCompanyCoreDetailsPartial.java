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

public class AdminEditCompanyCoreDetailsPartial extends Base_Page<AdminEditCompanyCoreDetailsPartial>{

    private static final Logger logger = LogManager.getLogger();

    private static final String EDIT_COMPANY_PAGE_CONTAINER_XPATH = "//div[@class = 'container']";
    private static final String EDIT_COMPANY_PAGE_CONTENT_CONTAINER_XPATH = EDIT_COMPANY_PAGE_CONTAINER_XPATH + "//div[@class = 'admin-action__content']";

    private static final String EDIT_COMPANY_HEADER_XPATH = EDIT_COMPANY_PAGE_CONTENT_CONTAINER_XPATH + "//h2[contains(text(),'Edit Company')]";
    private static final String CORE_DETAILS_SUBHEADER_XPATH = EDIT_COMPANY_PAGE_CONTENT_CONTAINER_XPATH + "//h3[contains(.,'Core Details')]";
    private static final String CORE_DETAILS_SECTION_XPATH = CORE_DETAILS_SUBHEADER_XPATH + "//parent::div//parent::div[@class = 'admin-action__section']";

    //labels
    private static final String COMPANY_TYPE_LABEL_XPATH = CORE_DETAILS_SECTION_XPATH + "//label[contains(text(),'Company Type')]";
    private static final String NAME_LABEL_XPATH = CORE_DETAILS_SECTION_XPATH + "//label[contains(text(),'Name')]";
    private static final String MAKE_COMPANY_ACTIVE_LABEL_XPATH = CORE_DETAILS_SECTION_XPATH + "//label[contains(text(),'Make Company Active')]";
    private static final String PROCESSED_BEHALF_OF_CLIENT_LABEL_XPATH = CORE_DETAILS_SECTION_XPATH + "//label[contains(text(),'processed on behalf of the Client')]";
    private static final String DEBTOR_ACCOUNT_LABEL_XPATH = CORE_DETAILS_SECTION_XPATH + "//label[contains(text(),'Debtor Account')]";

    private static final String NAME_INPUT_BOX_XPATH =  NAME_LABEL_XPATH + "//parent::div//input[contains(@name,'Name')]";
    private static final String COMPANY_TYPE_DROPDOWN_XPATH = COMPANY_TYPE_LABEL_XPATH + "//parent::div" + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String DEBTOR_ACCOUNT_DROPDOWN_XPATH = DEBTOR_ACCOUNT_LABEL_XPATH + "//parent::div" + DROPDOWN_SEARCH_ARROW_XPATH;

    private static final String PROCESSED_BEHALF_OF_CLIENT_YES_RADIO_BUTTON_XPATH = PROCESSED_BEHALF_OF_CLIENT_LABEL_XPATH + "/following-sibling::div//label[text()='Yes']";
    private static final String PROCESSED_BEHALF_OF_CLIENT_NO_RADIO_BUTTON_XPATH = PROCESSED_BEHALF_OF_CLIENT_LABEL_XPATH + "/following-sibling::div//label[text()='No']";
    private static final String MAKE_COMPANY_ACTIVE_YES_RADIO_BUTTON_XPATH = MAKE_COMPANY_ACTIVE_LABEL_XPATH + "/following-sibling::div//input[@id='Active_Yes']";
    private static final String MAKE_COMPANY_ACTIVE_NO_RADIO_BUTTON_XPATH = MAKE_COMPANY_ACTIVE_LABEL_XPATH + "/following-sibling::div//input[@id='Active_No']";

    @FindBy(xpath = NAME_INPUT_BOX_XPATH)
    private WebElement nameInputBox;

    @FindBy(xpath = COMPANY_TYPE_DROPDOWN_XPATH)
    private WebElement companyTypeDropDown;

    @FindBy(xpath = DEBTOR_ACCOUNT_DROPDOWN_XPATH)
    private WebElement debtorAccountDropDown;

    @FindBy(xpath = PROCESSED_BEHALF_OF_CLIENT_YES_RADIO_BUTTON_XPATH)
    private WebElement processedOnBehalfOfYesRadio;

    @FindBy(xpath = PROCESSED_BEHALF_OF_CLIENT_NO_RADIO_BUTTON_XPATH)
    private WebElement processedBehalfOfNoRadioButton;

    @FindBy(xpath = MAKE_COMPANY_ACTIVE_YES_RADIO_BUTTON_XPATH)
    private WebElement companyActiveYesRadioButton;

    @FindBy(xpath = MAKE_COMPANY_ACTIVE_NO_RADIO_BUTTON_XPATH)
    private WebElement companyActiveNoRadioButton;


    public AdminEditCompanyCoreDetailsPartial(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(EDIT_COMPANY_HEADER_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String getCompanyName() {
        return nameInputBox.getAttribute("value");
    }

    public void selectYesRadioOnProccessedOnBehalfOfClient() {
        processedOnBehalfOfYesRadio.click();
        waitForAngularRequestsToFinish();
    }

    public void setCompanyName(String name) {
        nameInputBox.clear();
        nameInputBox.sendKeys(name);
    }

}
