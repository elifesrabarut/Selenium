package com.hepsiburada.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class CartPage extends BasePage {

    private static final By CART_ITEMS = By.cssSelector(
            ".cart-item, .basket-item, [data-test-id='cart-item'], " +
            ".product-item, .cartProduct, .basket-product, " +
            "[class*='cart-item'], [class*='basket-item'], [class*='cartProduct'], " +
            "li[class*='product'], .pb-item"
    );
    private static final By CART_PAGE_INDICATOR = By.cssSelector(
            "h1, .cart-title, .basket-title, [data-test-id='cart-page']"
    );
    private static final By CART_ICON = By.cssSelector(
            "a[href*='sepet'], .cart-icon, [data-test-id='cart'], .header-cart"
    );

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public CartPage navigateToCart() {
        navigateTo(BASE_URL + "/sepetim");
        wait.until(ExpectedConditions.urlContains("sepet"));
        return this;
    }

    public boolean isProductInCart() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(CART_ITEMS));
            List<WebElement> items = driver.findElements(CART_ITEMS);
            return !items.isEmpty();
        } catch (TimeoutException e) {
            System.out.println("[Cart] Cart items element not found: " + CART_ITEMS);
            return !driver.findElements(CART_ITEMS).isEmpty();
        }
    }

    /** Sepette ürün adının geçtiğini doğrular. */
    public boolean isProductNameInCart(String productName) {
        if (productName == null || productName.isBlank()) return true;
        return driver.getPageSource().contains(productName);
    }

    /** Sepette ürün ID veya linkinin geçtiğini doğrular (örn. HBCV0000AD46FT). */
    public boolean isProductIdInCart(String productId) {
        if (productId == null || productId.isBlank()) return true;
        return driver.getPageSource().contains(productId);
    }

    /** Seçilen ürünün sepette olduğunu doğrular: ad veya ID ile. */
    public boolean isSelectedProductInCart(String productName, String productId) {
        if (productName != null && !productName.isBlank() && isProductNameInCart(productName)) return true;
        if (productId != null && !productId.isBlank() && isProductIdInCart(productId)) return true;
        return false;
    }

    public int getCartItemCount() {
        List<WebElement> items = driver.findElements(CART_ITEMS);
        return items.size();
    }
}
