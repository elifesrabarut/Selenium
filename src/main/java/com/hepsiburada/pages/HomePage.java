package com.hepsiburada.pages;

import com.hepsiburada.core.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ElementNotInteractableException;

public class HomePage extends BasePage {

    private static final By SEARCH_INPUT = By.cssSelector("input[data-test-id='search-bar-input']");
    private static final By COOKIE_ACCEPT = By.xpath(
            "//button[contains(text(), 'Kabul')] | " +
                    "//button[contains(text(), 'Accept')] | " +
                    "//a[contains(text(), 'Kabul')] | " +
                    "//*[@id='onetrust-accept-btn-handler']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage navigate() {
        navigateToBaseUrl();
        waitForPageReady();
        acceptCookiesIfPresent();
        return this;
    }

    private void waitForPageReady() {
        try {
            Waits.waitUntil(driver, (java.util.function.Function<WebDriver, Boolean>) d ->
                    "complete".equals(((org.openqa.selenium.JavascriptExecutor) d)
                            .executeScript("return document.readyState")), 10);
        } catch (TimeoutException e) {
            System.out.println("[Home] Sayfa hazır olma kontrolü zaman aşımı: document.readyState");
        }
    }

    private void acceptCookiesIfPresent() {
        try {
            WebElement acceptBtn = Waits.waitForClickable(driver, COOKIE_ACCEPT, 5);
            acceptBtn.click();
        } catch (TimeoutException e) {
            System.out.println("[Home] Çerez banner bulunamadı (isteğe bağlı): " + COOKIE_ACCEPT);
        }
    }

    public SearchResultsPage searchProduct(String searchTerm) {
        try {
            WebElement input = waitClickable(SEARCH_INPUT);
            scrollToElement(input);
            type(SEARCH_INPUT, searchTerm);
            waitClickable(SEARCH_INPUT).sendKeys(Keys.ENTER);
        } catch (TimeoutException | ElementNotInteractableException
                | org.openqa.selenium.NoSuchElementException
                | org.openqa.selenium.StaleElementReferenceException e) {
            System.out.println("[Home] Arama alanı etkileşimi başarısız (" + e.getClass().getSimpleName() + "), URL ile devam ediliyor");
            String encoded = java.net.URLEncoder.encode(searchTerm, java.nio.charset.StandardCharsets.UTF_8);
            navigateTo(baseUrl + "/ara?q=" + encoded);
        }
        return new SearchResultsPage(driver);
    }

    public SearchResultsPage navigateToLaptopCategory() {
        navigateTo(baseUrl + "/laptop-notebook-dizustu-bilgisayarlar-c-98");
        return new SearchResultsPage(driver);
    }
}
