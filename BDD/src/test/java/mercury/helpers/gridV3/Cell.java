package mercury.helpers.gridV3;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;

import mercury.helpers.POHelper;

public class Cell {

    private String text;
    private String subText;
    private List<WebElement> webElements;
    private String cssSelector;

    public String getText() {
        return text;
    }

    public String getSubText() {
        return subText;
    }

    public WebElement getWebElement() {
        return webElements.get(0);
    }

    public List<WebElement> getWebElements() {
        return webElements;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }

    public void addWebElement(WebElement webElement) {
        if (webElements == null) webElements = new ArrayList<>();
        webElements.add(webElement);
    }

    public void setCssSelector(String cssSelector) {
        this.cssSelector = cssSelector;
    }

    public void clickButton(String button) {
        for (WebElement we : webElements) {
            if (we.getText().equals(button)) {
                we.click();
                POHelper.waitForAngularRequestsToFinish();
                break;
            }
        }
    }

    public boolean isCheckBoxChecked() {
        return webElements.get(0).isSelected();
    }

    public boolean isCheckBoxEnabled() {
        return webElements.get(0).isEnabled();
    }

    public void clickCheckbox(String name) {
        for (WebElement we : webElements) {
            if (we.getAttribute("class").contains("showCheckbox") || we.getAttribute("type").contains("checkbox")) {
                if (we.getText().equals("name") || name.isEmpty()) {
                    POHelper.clickJavascript(we);
                    break;
                }
            }
        }
    }

    public String getCssSelector(){
        return cssSelector;
    }

    public String getValue(){
        return webElements.get(0).getAttribute("value");
    }

    public void sendText(String text) {
        webElements.get(0).clear();
        webElements.get(0).sendKeys(text);;
    }

    public Boolean isCellDisabled(){
        boolean disabled = true;
        for (WebElement we : webElements) {
            if (we.isEnabled()) disabled = false;
        }
        return disabled;
    }

}
