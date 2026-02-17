package com.hepsiburada.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    public static final String BASE_URL = "https://www.hepsiburada.com";
    private static final int DEFAULT_TIMEOUT_SECONDS = 15;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
    }

    protected WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForElementClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void waitForUrlToContain(String urlPart) {
        wait.until(ExpectedConditions.urlContains(urlPart));
    }

    protected void click(By locator) {
        WebElement element = waitForElementClickable(locator);
        element.click();
    }

    /** Overlay veya özel bileşenlerde normal click çalışmıyorsa JS click kullanır. */
    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void sendKeys(By locator, String text) {
        WebElement element = waitForElement(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    /** Sayfayı dikey olarak ortaya kaydırır. Filtre paneli görünür olması için kullanılır. */
    protected void scrollToPageMiddle() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight / 2);");
    }

    /** Sayfayı aşağı kaydırır (Sepete ekle vb. alt bölümleri görünür yapar). */
    protected void scrollDown() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight * 0.8);");
    }

    public void navigateTo(String url) {
        driver.get(url);
    }
}
