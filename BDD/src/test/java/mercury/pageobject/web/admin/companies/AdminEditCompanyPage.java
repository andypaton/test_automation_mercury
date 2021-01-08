package mercury.pageobject.web.admin.companies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class AdminEditCompanyPage extends Base_Page<AdminEditCompanyPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String EDIT_COMPANY_PAGE_CONTAINER_XPATH = "//div[@class = 'container']";
    private static final String EDIT_COMPANY_PAGE_CONTENT_CONTAINER_XPATH = EDIT_COMPANY_PAGE_CONTAINER_XPATH + "//div[@class = 'admin-action__content']";

    //Page Elements
    private static final String EDIT_COMPANY_HEADER_XPATH = EDIT_COMPANY_PAGE_CONTENT_CONTAINER_XPATH + "//h2[contains(text(),'Edit Company')]";
    private static final String CORE_DETAILS_SUBHEADER_XPATH = EDIT_COMPANY_PAGE_CONTENT_CONTAINER_XPATH + "//h3[contains(.,'Core Details')]";
    private static final String ADDRESS_SUBHEADER_XPATH = EDIT_COMPANY_PAGE_CONTENT_CONTAINER_XPATH + "//h3[contains(.,'Address')]";
    private static final String ALIASES_SUBHEADER_XPATH = EDIT_COMPANY_PAGE_CONTENT_CONTAINER_XPATH + "//h3[contains(.,'Aliases')]";
    private static final String ADDITIONAL_DETAILS_SUBHEADER_XPATH = EDIT_COMPANY_PAGE_CONTENT_CONTAINER_XPATH + "//h3[contains(.,'Additional details')]";
    private static final String EDIT_COMPANY_PAGE_SECTIONS_XPATH = EDIT_COMPANY_PAGE_CONTENT_CONTAINER_XPATH + "//h3[contains(.,'%s')]";
    private static final String EXPANDED_SECTION_XPATH = EDIT_COMPANY_PAGE_CONTENT_CONTAINER_XPATH + "//h3[contains(.,'%s')]/i[contains(@class, 'glyphicon-chevron-down')]";

    //Buttons
    private static final String EDIT_COMAPANY_SAVE_BUTTON_XPATH = EDIT_COMPANY_PAGE_CONTENT_CONTAINER_XPATH + "//button//span[contains(text(),'Save')]";
    private static final String EDIT_COMAPANY_CANCEL_BUTTON_XPATH = EDIT_COMPANY_PAGE_CONTENT_CONTAINER_XPATH + "//button[contains(text(),'Cancel')]";


    @FindBy(xpath = EDIT_COMAPANY_SAVE_BUTTON_XPATH)
    private WebElement saveButton;

    @FindBy(xpath = EDIT_COMAPANY_CANCEL_BUTTON_XPATH)
    private WebElement cancelButton;

    @FindBy(xpath = CORE_DETAILS_SUBHEADER_XPATH)
    private WebElement coreDetailsSectionDropDown;

    @FindBy(xpath = ADDRESS_SUBHEADER_XPATH)
    private WebElement addressSectionDropDown;

    @FindBy(xpath = ALIASES_SUBHEADER_XPATH)
    private WebElement aliasesSectionDropDown;

    @FindBy(xpath = ADDITIONAL_DETAILS_SUBHEADER_XPATH)
    private WebElement additonalDetailsSectionDropDown;


    public AdminEditCompanyPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            waitForAngularRequestsToFinish();
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(EDIT_COMPANY_HEADER_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public boolean isEditCompanyPageDisplayed() {
        return this.isElementPresent(By.xpath(EDIT_COMPANY_HEADER_XPATH));
    }

    public void clickPageSection(String sectionName) {
        WebElement section = driver.findElement(By.xpath(String.format(EDIT_COMPANY_PAGE_SECTIONS_XPATH, sectionName)));
        waitForAnimation();
        section.click();
    }

    public void expandPageSection(String sectionName) {
        if ( ! isElementPresent(By.xpath(String.format(EXPANDED_SECTION_XPATH, sectionName)))) {
            WebElement section = waitForElement(By.xpath(String.format(EDIT_COMPANY_PAGE_SECTIONS_XPATH, sectionName)), State.ELEMENT_IS_CLICKABLE);
            section.click();
            waitForAngularRequestsToFinish();
        }
    }

    public void clickSave() {
        saveButton.click();
        waitForAngularRequestsToFinish();
    }
}
