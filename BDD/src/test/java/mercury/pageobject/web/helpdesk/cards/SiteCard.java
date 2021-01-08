package mercury.pageobject.web.helpdesk.cards;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.helpdesk.HelpdeskSitePage;

public class SiteCard extends Base_Page<SiteCard> {

    private static final Logger logger = LogManager.getLogger();

    private static final String VISIBLE_SIDE_PANEL_CSS = "#side-panel ";

    private static final String SITE_CARD_CSS = VISIBLE_SIDE_PANEL_CSS + ".card.site ";
    private static final String SITE_CARD_XPATH = "//ph-site-card";

    private static final String CARD_HEADER_ICON_CSS = SITE_CARD_CSS + ".card-header__icon-container ";

    private static final String SITE_NAME_CSS = SITE_CARD_CSS + ".card-header .card-header__headline ";
    private static final String SITE_TYPE_CODE_CSS = SITE_CARD_CSS + ".card-header .card-header__subhead ";
    private static final String SITE_TIME_XPATH = SITE_CARD_XPATH + "//span[@class='time-label' and text()='Site']/following-sibling::span[@class='time-value']";
    private static final String HOME_OFFICE_TIME_XPATH = SITE_CARD_XPATH + "//span[@class='time-label' and text()='Home Office']/following-sibling::span[@class='time-value']";

    private static final String CARD_SUBHEADER_LEFT_CSS = SITE_CARD_CSS +  ".card-subheader__left ";
    private static final String CARD_SUBHEADER_RIGHT_CSS = SITE_CARD_CSS + ".card-subheader__right ";
    private static final String CARD_CONTENT_CSS = SITE_CARD_CSS + ".card-content ";

    private static final String WRENCH_XPATH = "//i[@class='icons__wrench']/..";
    private static final String EXCLAMATION_TRIANGLE_XPATH = "//i[@class='icons__exclamation-triangle']/..";


    @FindBy(css = SITE_CARD_CSS)
    private static WebElement siteCard;

    @FindBy(css = SITE_NAME_CSS)
    private static WebElement siteName;

    @FindBy(css = SITE_TYPE_CODE_CSS)
    private static WebElement siteTypeCode;

    @FindBy(xpath = SITE_TIME_XPATH)
    private static WebElement siteTime;

    @FindBy(xpath = HOME_OFFICE_TIME_XPATH)
    private static WebElement homeOfficeTime;

    @FindBy(css = CARD_HEADER_ICON_CSS)
    private static WebElement headerIcon;

    @FindBy(css = CARD_SUBHEADER_LEFT_CSS)
    private static WebElement subHeader_left;

    @FindBy(css = CARD_SUBHEADER_RIGHT_CSS)
    private static WebElement subHeader_right;

    @FindBy(css = CARD_CONTENT_CSS)
    private static WebElement content;

    @FindBy(xpath = WRENCH_XPATH)
    private static WebElement wrench;

    @FindBy(xpath = EXCLAMATION_TRIANGLE_XPATH)
    private static WebElement exclamationTriangle;

    public SiteCard(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.cssSelector(SITE_CARD_CSS)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public boolean isCardDisplayed() throws Exception {
        return isElementPresent(By.cssSelector(SITE_CARD_CSS));
    }

    public String getSiteName() throws Exception {
        return siteName.getText();
    }

    public String getSiteTypeAndCode() throws Exception {
        return siteTypeCode.getText();
    }

    public String getSiteTime() throws Exception {
        return siteTime.getText();
    }

    public String getHomeOfficeTime() throws Exception {
        return homeOfficeTime.getText();
    }

    public String getSubHeaderLeft() throws Exception {
        return subHeader_left.getText();
    }

    public String getSubHeaderRight() throws Exception {
        return subHeader_right.getText();
    }

    public String getContent() throws Exception {
        return content.getText();
    }

    public Integer getWrenchCount() throws Exception {
        String countAsString = wrench.getText();
        return countAsString.isEmpty() ? 0 : Integer.valueOf(countAsString);
    }

    public Integer getExclamationTriangleCount() throws Exception {
        String countAsString = exclamationTriangle.getText();
        return countAsString.isEmpty() ? 0 : Integer.valueOf(countAsString);
    }

    public HelpdeskSitePage selectCard() {
        siteCard.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskSitePage.class).get();
    }
}
