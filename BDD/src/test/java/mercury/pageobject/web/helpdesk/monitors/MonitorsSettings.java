package mercury.pageobject.web.helpdesk.monitors;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class MonitorsSettings extends Base_Page<MonitorsSettings> {

    private static final Logger logger = LogManager.getLogger();

    protected static final String SETTINGS_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]//monitors-settings";

    private static final String EXPAND_XPATH = SETTINGS_XPATH + "//i[contains(@class, 'glyphicon-chevron-right')]";
    private static final String COLLAPSE_XPATH = SETTINGS_XPATH + "//div[@class='monitors__category-header']//i[contains(@class, 'glyphicon-chevron-right')]";

    protected static final String OPTIONS_XPATH = "/following-sibling::div[contains(@class, 'monitors__settings-options')]/div[not(@aria-hidden='true')]";
    protected static final String PLUS_XPATH = "/i[contains(@class, 'fa-plus')]";
    protected static final String AUTO_COMPLETE_XPATH = "/..//input[@name='autoComplete']";
    protected static final String TIMES_XPATH = "/..//span[text()='%s']/following-sibling::i[contains(@class, 'fa-times')]";

    protected static final String TEAM = SETTINGS_XPATH + "//div[contains(@class, 'monitors__settings-title') and contains(text(), 'Team')]";
    protected static final String TEAMS_XPATH = TEAM + OPTIONS_XPATH;
    protected static final String TEAM_ADD_XPATH = TEAM + PLUS_XPATH;
    protected static final String TEAM_ADD_NAME_XPATH = TEAM + AUTO_COMPLETE_XPATH;
    protected static final String TEAM_REMOVE_XPATH = TEAM + TIMES_XPATH;

    protected static final String ASSET_TYPE = SETTINGS_XPATH + "//div[contains(@class, 'monitors__settings-title') and contains(text(), 'Asset Type')]";
    protected static final String ASSET_TYPES_XPATH = ASSET_TYPE + OPTIONS_XPATH;
    protected static final String ASSET_TYPE_ADD_XPATH = ASSET_TYPE + PLUS_XPATH;
    protected static final String ASSET_TYPE_ADD_NAME_XPATH = ASSET_TYPE + AUTO_COMPLETE_XPATH;
    protected static final String ASSET_TYPE_REMOVE_XPATH = ASSET_TYPE + TIMES_XPATH;

    @FindBy(xpath = EXPAND_XPATH)
    WebElement expandSettings;

    @FindBy(xpath = COLLAPSE_XPATH)
    WebElement collapseSettings;

    @FindBy(xpath = TEAMS_XPATH)
    List<WebElement> teams;

    @FindBy(xpath = TEAM_ADD_XPATH)
    WebElement addTeam;

    @FindBy(xpath = TEAM_ADD_NAME_XPATH)
    WebElement addTeamName;

    @FindBy(xpath = ASSET_TYPES_XPATH)
    List<WebElement> assetTypes;

    @FindBy(xpath = ASSET_TYPE_ADD_XPATH)
    WebElement addAssetType;

    @FindBy(xpath = ASSET_TYPE_ADD_NAME_XPATH)
    WebElement addAssetTypeName;


    public MonitorsSettings(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            waitForAngularRequestsToFinish();
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(SETTINGS_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch(NoSuchElementException ex){

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void expandSettings() {
        expandSettings.click();
        waitForAngularRequestsToFinish();
    }

    public void collapseSettings() {
        collapseSettings.click();
        waitForAngularRequestsToFinish();
    }

    public Boolean isSettingsCollapsed() {
        waitForAngularRequestsToFinish();
        return isElementPresent(By.xpath(COLLAPSE_XPATH));
    }

    public List<String> getTeams() {
        List<String> teamNames = new ArrayList<>();
        for (WebElement we : teams) {
            teamNames.add(we.getText());
        }
        return teamNames;
    }

    public void clickAddTeam() {
        addTeam.click();
        waitForAngularRequestsToFinish();
    }

    public void addTeam(String teamName) {
        addTeam.click();
        waitForAngularRequestsToFinish();
        addTeamName.sendKeys(teamName);
        addTeamName.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void removeTeam(String teamName) {
        String xpath = String.format(TEAM_REMOVE_XPATH, teamName);
        WebElement we = driver.findElement(By.xpath(xpath));
        we.click();
        waitForAngularRequestsToFinish();
    }

    public List<String> getAssetTypes() {
        List<String> assetTypeNames = new ArrayList<>();
        for (WebElement we : assetTypes) {
            assetTypeNames.add(we.getText());
        }
        return assetTypeNames;
    }

    public void clickAddAssetType() {
        addAssetType.click();
        waitForAngularRequestsToFinish();
    }

    public void addAssetType(String assetTypeName) {
        addAssetType.click();
        waitForAngularRequestsToFinish();
        addAssetTypeName.sendKeys(assetTypeName);
        addAssetTypeName.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void removeAssetType(String assetTypeName) {
        String xpath = String.format(ASSET_TYPE_REMOVE_XPATH, assetTypeName);
        WebElement we = driver.findElement(By.xpath(xpath));
        we.click();
        waitForAngularRequestsToFinish();
    }

    public boolean isDisplayed() {
        return isElementVisible(By.xpath(SETTINGS_XPATH));
    }

}
