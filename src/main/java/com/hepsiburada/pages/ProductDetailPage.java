package com.hepsiburada.pages;

import com.hepsiburada.core.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ProductDetailPage extends BasePage {

    private static final By PRODUCT_DETAIL_INDICATORS = By.xpath(
            "//*[contains(., 'Sepete Ekle') or contains(., 'Sepete ekle')] | " +
            "//*[@data-test-id='add-to-basket'] | " +
            "//*[contains(@class, 'product-detail')] | //*[contains(@id, 'product')] | " +
            "//*[contains(@class, 'merchant-list')] | //h1[contains(@class, 'product') or contains(@id, 'product')]");
    private static final By[] ADD_TO_CART_LOCATORS = {
            By.cssSelector("[data-test-id='addToCart']"),
            By.xpath("//button[contains(., 'Sepete ekle')]"),
            By.xpath("//button[contains(., 'Sepete Ekle')]"),
            By.xpath("//a[contains(., 'Sepete Ekle')]"),
            By.xpath("//*[contains(., 'Sepete ekle')]"),
            By.xpath("//*[@data-test-id='add-to-basket']")
    };
    private static final By PRODUCT_TITLE = By.cssSelector("h1, [data-test-id*='product-name'], [class*='productName']");
    private static final By GO_TO_CART_MODAL = By.xpath(
            "//a[contains(., 'Sepete Git')] | //button[contains(., 'Sepete Git')] | " +
            "//*[contains(@class, 'modal') or contains(@class, 'Modal') or contains(@class, 'overlay')]//a[contains(@href, 'sepet')]");

    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }

    public boolean isProductDetailPageOpened() {
        String url = driver.getCurrentUrl();
        boolean urlMatches = url.contains("/p-") || url.contains("-p-") || url.contains("/product/")
                || (url.contains("hepsiburada.com/") && url.contains("-p"));
        if (!urlMatches) return false;
        try {
            waitPresence(PRODUCT_DETAIL_INDICATORS);
            return true;
        } catch (TimeoutException e) {
            return !driver.findElements(PRODUCT_DETAIL_INDICATORS).isEmpty();
        }
    }

    public void addToCart() {
        scrollDown();
        waitPresence(PRODUCT_DETAIL_INDICATORS);
        By chosenLocator = null;
        for (By locator : ADD_TO_CART_LOCATORS) {
            if (!driver.findElements(locator).isEmpty()) {
                chosenLocator = locator;
                break;
            }
        }
        if (chosenLocator == null) chosenLocator = ADD_TO_CART_LOCATORS[0];
        scrollToElement(chosenLocator);
        WebElement addBtn = waitClickable(chosenLocator);
        jsClick(addBtn);
        try {
            Waits.waitForVisibility(driver, GO_TO_CART_MODAL, 15);
            WebElement goToCartBtn = Waits.waitForClickable(driver, GO_TO_CART_MODAL, 5);
            jsClick(goToCartBtn);
        } catch (TimeoutException e) {
            System.out.println("[ProductDetail] Sepete Git modal bulunamadı, /sepetim ile gidilecek.");
        }
    }

    public String getProductTitle() {
        try {
            List<WebElement> titles = driver.findElements(PRODUCT_TITLE);
            for (WebElement el : titles) {
                try {
                    String text = el.getText();
                    if (text != null && !text.isBlank() && text.length() > 2 && text.length() < 200) {
                        return text.trim();
                    }
                } catch (org.openqa.selenium.StaleElementReferenceException e) {
                    continue;
                }
            }
        } catch (Exception ignored) { }
        return "";
    }

    public String getProductIdFromUrl() {
        String url = driver.getCurrentUrl();
        int idx = url.indexOf("-p-");
        if (idx < 0) return "";
        String after = url.substring(idx + 3);
        int end = after.indexOf("?");
        if (end > 0) after = after.substring(0, end);
        return after.trim();
    }
}
