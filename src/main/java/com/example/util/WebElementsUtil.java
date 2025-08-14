package com.example.util;

import com.example.service.aggregator.SeleniumManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class WebElementsUtil extends SeleniumManager {

    public void clickElement(By by) {
        if (isElementAvailable(by)) {
            WebElement webElement = getWait().until(ExpectedConditions.elementToBeClickable(by));
            webElement.click();
        }
    }

    public void clickElementNoWait(By by) {
        if (isElementAvailableNoWait(by)) {
            WebElement webElement = getWait().until(ExpectedConditions.elementToBeClickable(by));
            webElement.click();
        }
    }

    public boolean isElementAvailable(By by) {
        try {
            WebElement webElement = getWait().until(
                    ExpectedConditions.visibilityOfElementLocated(by)
            );
            return webElement.isDisplayed() && webElement.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isElementAvailableNoWait(By by) {
        try {
            WebElement webElement = getDriver().findElement(by);
            return webElement.isDisplayed() && webElement.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public WebElement putTextToInputField(By by, String text) {
        WebElement inputField = getWait().until(
                ExpectedConditions.presenceOfElementLocated(by)
        );
        inputField.clear();
        inputField.sendKeys(text);
        return inputField;
    }

    public boolean isEnterAccount(By by, String exitField) {
        WebElement element = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(by));
        String str = element.getText();
        List<String> array = Arrays.stream(str.split(" ")).toList();
        return array.contains(exitField);
    }

    public List<WebElement> getElements(By by) {
        return getDriver().findElements(by);
    }

    public String getText(By by) {
        if (isElementAvailable(by)) {
            WebElement webElement = getWait().until(ExpectedConditions.visibilityOfElementLocated(by));
            return webElement.getText();
        }
        return "";
    }

    public void readyStateDocument(){
        getWait().until(
                webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete")
        );
    }
}
