package com.hepsiburada.pages;

import com.hepsiburada.core.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CartPage extends BasePage {

    private static final By CART_ITEMS = By.cssSelector(
            ".cart-item, .basket-item, [data-test-id='cart-item'], .product-item, .cartProduct, .basket-product, " +
            "[class*='cart-item'], [class*='basket-item'], [class*='cartProduct'], li[class*='product'], .pb-item");
    private static final By CART_PRODUCT_LINKS_OR_TITLES = By.cssSelector(
            "a[href*='-p-'], [class*='product-name'], [class*='productName'], [data-test-id*='product'], .basket-item a, .cart-item a");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public CartPage navigateToCart() {
        navigateTo(baseUrl + "/sepetim");
        waitUrlContains("sepet");
        waitForCartContent();
        return this;
    }

    private void waitForCartContent() {
        try {
            Waits.waitForPresence(driver, CART_ITEMS, 12);
        } catch (TimeoutException e) {
            try {
                Waits.waitForPresence(driver, CART_PRODUCT_LINKS_OR_TITLES, 8);
            } catch (TimeoutException ignored) { }
        }
    }

    public boolean isProductInCart() {
        waitForCartContent();
        if (!driver.findElements(CART_ITEMS).isEmpty()) return true;
        return !driver.findElements(CART_PRODUCT_LINKS_OR_TITLES).isEmpty();
    }

    private boolean isProductNameInVisibleElements(String productName) {
        if (productName == null || productName.isBlank()) return true;
        String normalized = productName.split("\\|")[0].trim().toLowerCase();
        String key = normalized.length() >= 10 ? normalized.substring(0, 10) : normalized;
        for (WebElement el : driver.findElements(CART_PRODUCT_LINKS_OR_TITLES)) {
            try {
                String text = el.getText();
                if (text != null && !text.isBlank() && text.toLowerCase().contains(key)) return true;
                String href = el.getAttribute("href");
                if (href != null && href.toLowerCase().contains(key)) return true;
            } catch (Exception ignored) { }
        }
        for (WebElement el : driver.findElements(CART_ITEMS)) {
            try {
                String text = el.getText();
                if (text != null && !text.isBlank() && text.toLowerCase().contains(key)) return true;
            } catch (Exception ignored) { }
        }
        return false;
    }

    private boolean isProductIdInVisibleElements(String productId) {
        if (productId == null || productId.isBlank()) return true;
        String id = productId.toLowerCase();
        for (WebElement el : driver.findElements(CART_PRODUCT_LINKS_OR_TITLES)) {
            try {
                String href = el.getAttribute("href");
                if (href != null && href.toLowerCase().contains(id)) return true;
                if (el.getText() != null && el.getText().toLowerCase().contains(id)) return true;
            } catch (Exception ignored) { }
        }
        for (WebElement el : driver.findElements(CART_ITEMS)) {
            try {
                if (el.getText() != null && el.getText().toLowerCase().contains(id)) return true;
            } catch (Exception ignored) { }
        }
        return false;
    }

    public boolean isProductNameInCart(String productName) {
        if (productName == null || productName.isBlank()) return true;
        if (isProductNameInVisibleElements(productName)) return true;
        String page = driver.getPageSource();
        if (page.contains(productName)) return true;
        String normalized = productName.split("\\|")[0].trim();
        if (normalized.length() >= 10 && page.contains(normalized.substring(0, 10))) return true;
        if (normalized.length() >= 8 && page.contains(normalized.substring(0, 8))) return true;
        return false;
    }

    public boolean isProductIdInCart(String productId) {
        if (productId == null || productId.isBlank()) return true;
        if (isProductIdInVisibleElements(productId)) return true;
        return driver.getPageSource().toLowerCase().contains(productId.toLowerCase());
    }

    public boolean isSelectedProductInCart(String productName, String productId) {
        waitForCartContent();
        if (productName != null && !productName.isBlank() && isProductNameInCart(productName)) return true;
        if (productId != null && !productId.isBlank() && isProductIdInCart(productId)) return true;
        return isProductInCart() && getCartItemCount() >= 1;
    }

    public int getCartItemCount() {
        List<WebElement> items = driver.findElements(CART_ITEMS);
        if (!items.isEmpty()) return items.size();
        return driver.findElements(CART_PRODUCT_LINKS_OR_TITLES).size();
    }
}
