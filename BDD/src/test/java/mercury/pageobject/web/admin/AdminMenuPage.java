package mercury.pageobject.web.admin;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class AdminMenuPage extends Base_Page<AdminMenuPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ADMIN_MENU_CONTAINER_XPATH = "//div[@class = 'admin-action']";
    private static final String ADMIN_MENU_LIST_XPATH = ADMIN_MENU_CONTAINER_XPATH + "//nav";

    private static final String ADMIN_MENU_XPATH = ADMIN_MENU_LIST_XPATH + "//a[contains(text(),'%s')]";

    private static final String ADMIN_MENU_NAVIGATION_OPTIONS_LIST_XPATH = ADMIN_MENU_LIST_XPATH + "//ul//li//a";

    private static final String ADMIN_MENU_HIGHLIGHTED_OPTION_XPATH = ADMIN_MENU_NAVIGATION_OPTIONS_LIST_XPATH + "[contains(@class, 'highlighted')]";

    @FindBy(xpath = ADMIN_MENU_HIGHLIGHTED_OPTION_XPATH) private WebElement highlightedNavigationMenuOption;

    public AdminMenuPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(ADMIN_MENU_CONTAINER_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectAdminMenu(String menuName) {
        WebElement requiredAdminMenu = waitForElement(By.xpath(String.format(ADMIN_MENU_XPATH, menuName)), ELEMENT_IS_CLICKABLE);
        requiredAdminMenu.click();
        waitForAngularRequestsToFinish();
    }

    public List<String> getNavigationMenuOptions() {
        List<String> navigationMenuoptions = new ArrayList<>();
        List<WebElement> navigationMenuoptionsList = driver.findElements(By.xpath(ADMIN_MENU_NAVIGATION_OPTIONS_LIST_XPATH));
        for (WebElement navigationMenuoption : navigationMenuoptionsList) {
            navigationMenuoptions.add(navigationMenuoption.getText());
        }
        return navigationMenuoptions;
    }

    public String getHighlightedNavigationMenuOption() {
        return highlightedNavigationMenuOption.getText();
    }

}
