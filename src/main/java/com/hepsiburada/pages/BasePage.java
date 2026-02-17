package com.hepsiburada.pages;

import com.hepsiburada.core.Waits;
import com.hepsiburada.utils.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ElementClickInterceptedException;

public abstract class BasePage {
    protected final WebDriver driver;
    protected final String baseUrl;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.baseUrl = Config.getBaseUrl();
    }

    protected WebElement waitVisible(By locator) {
        return Waits.waitForVisibility(driver, locator);
    }

    protected WebElement waitClickable(By locator) {
        return Waits.waitForClickable(driver, locator);
    }

    protected WebElement waitPresence(By locator) {
        return Waits.waitForPresence(driver, locator);
    }

    protected boolean waitUrlContains(String urlPart) {
        return Waits.waitForUrlContains(driver, urlPart);
    }

    protected boolean waitInvisible(By locator) {
        return Waits.waitForInvisibility(driver, locator);
    }

    protected void click(By locator) {
        WebElement element = waitClickable(locator);
        try {
            element.click();
        } catch (ElementClickInterceptedException | StaleElementReferenceException e) {
            jsClick(element);
        }
    }

    protected void type(By locator, String text) {
        WebElement element = waitVisible(locator);
        try {
            element.click();
            element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            element.sendKeys(Keys.BACK_SPACE);
            element.sendKeys(text);
        } catch (StaleElementReferenceException e) {
            waitVisible(locator).sendKeys(text);
        }
    }

    protected String getText(By locator) {
        return waitVisible(locator).getText();
    }

    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", element);
    }

    protected void scrollToElement(By locator) {
        for (int attempt = 0; attempt < 2; attempt++) {
            try {
                WebElement el = waitPresence(locator);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", el);
                return;
            } catch (StaleElementReferenceException e) {
                if (attempt == 1) throw e;
            }
        }
    }

    protected void scrollToPageMiddle() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight / 2);");
    }

    protected void scrollDown() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight * 0.8);");
    }

    public void navigateTo(String url) {
        driver.get(url);
    }

    public void navigateToBaseUrl() {
        driver.get(baseUrl);
    }
}
