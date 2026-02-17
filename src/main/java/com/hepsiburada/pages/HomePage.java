package com.hepsiburada.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HomePage extends BasePage {

    // Search box - HepsiBurada data-test-id
    private static final By SEARCH_INPUT = By.cssSelector("input[data-test-id='search-bar-input']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    private static final By COOKIE_ACCEPT = By.xpath(
            "//button[contains(text(), 'Kabul')] | " +
            "//button[contains(text(), 'Accept')] | " +
            "//a[contains(text(), 'Kabul')] | " +
            "//*[@id='onetrust-accept-btn-handler']"
    );

    public HomePage navigate() {
        navigateTo(BASE_URL);
        waitForPageReady();
        acceptCookiesIfPresent();
        return this;
    }

    private void waitForPageReady() {
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
                    .until(d -> ((org.openqa.selenium.JavascriptExecutor) d)
                            .executeScript("return document.readyState").equals("complete"));
        } catch (TimeoutException e) {
            System.out.println("[Home] Page ready state check timeout: document.readyState");
        }
    }

    private void acceptCookiesIfPresent() {
        try {
            org.openqa.selenium.support.ui.WebDriverWait cookieWait =
                    new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5));
            WebElement acceptBtn = cookieWait.until(
                    org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(COOKIE_ACCEPT));
            acceptBtn.click();
        } catch (TimeoutException e) {
            System.out.println("[Home] Cookie banner not found (optional): " + COOKIE_ACCEPT);
        }
    }

    public SearchResultsPage searchProduct(String searchTerm) {
        try {
            searchProductViaInput(searchTerm);
        } catch (TimeoutException | org.openqa.selenium.NoSuchElementException | org.openqa.selenium.StaleElementReferenceException e) {
            System.out.println("[Home] Search input interaction failed (" + e.getClass().getSimpleName() + "): " + SEARCH_INPUT + ", using URL fallback");
            String encoded = java.net.URLEncoder.encode(searchTerm, java.nio.charset.StandardCharsets.UTF_8);
            navigateTo(BASE_URL + "/ara?q=" + encoded);
        }
        return new SearchResultsPage(driver);
    }

    private void searchProductViaInput(String searchTerm) {
        WebElement searchInput = waitForElementClickable(SEARCH_INPUT);
        searchInput.click();
        searchInput.sendKeys(searchTerm);
        searchInput.sendKeys(Keys.ENTER);
    }

    /** Fallback: arama URL çalışmazsa kategori sayfasına git */
    public SearchResultsPage navigateToLaptopCategory() {
        navigateTo(BASE_URL + "/laptop-notebook-dizustu-bilgisayarlar-c-98");
        return new SearchResultsPage(driver);
    }
}
