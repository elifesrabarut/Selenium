package com.hepsiburada.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class ProductDetailPage extends BasePage {

    // Ürün detay sayfası göstergeleri - contains(., 'x') iç içe metinleri de kapsar
    private static final By PRODUCT_DETAIL_INDICATORS = By.xpath(
            "//*[contains(., 'Sepete Ekle') or contains(., 'Sepete ekle')] | " +
            "//*[@data-test-id='add-to-basket'] | " +
            "//*[contains(@class, 'product-detail')] | " +
            "//*[contains(@id, 'product')] | " +
            "//*[contains(@class, 'merchant-list')] | " +
            "//h1[contains(@class, 'product') or contains(@id, 'product')]"
    );

    // Sepete ekle butonları - data-test-id="addToCart" HepsiBurada'da kullanılıyor
    private static final By[] ADD_TO_CART_LOCATORS = {
            By.cssSelector("[data-test-id='addToCart']"),
            By.xpath("//button[contains(., 'Sepete ekle')]"),
            By.xpath("//button[contains(., 'Sepete Ekle')]"),
            By.xpath("//a[contains(., 'Sepete Ekle')]"),
            By.xpath("//*[contains(., 'Sepete ekle')]"),
            By.xpath("//*[@data-test-id='add-to-basket']")
    };
    // Ürün başlığı - sepet doğrulaması için
    private static final By PRODUCT_TITLE = By.cssSelector("h1, [data-test-id*='product-name'], [class*='productName']");

    // Modal "Go to Cart" button - Turkish: "Sepete Git"
    private static final By GO_TO_CART_MODAL = By.xpath(
            "//a[contains(text(), 'Sepete Git')] | " +
            "//button[contains(text(), 'Sepete Git')] | " +
            "//a[contains(@href, 'sepet')]"
    );

    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Ürün detay sayfasının açıldığını doğrular:
     * 1) URL'de ürün sayfası pattern'i (-p-, /p-, product)
     * 2) Sayfada ürün detay elementi (Sepete Ekle, product-detail vb.) yüklenene kadar bekler
     */
    public boolean isProductDetailPageOpened() {
        String url = driver.getCurrentUrl();
        boolean urlMatches = url.contains("/p-") || url.contains("-p-") || url.contains("/product/")
                || (url.contains("hepsiburada.com/") && url.contains("-p"));
        if (!urlMatches) return false;
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(PRODUCT_DETAIL_INDICATORS));
            return true;
        } catch (TimeoutException e) {
            System.out.println("[ProductDetail] Product detail element not found: " + PRODUCT_DETAIL_INDICATORS);
            return !driver.findElements(PRODUCT_DETAIL_INDICATORS).isEmpty();
        }
    }

    public void addToCart() {
        scrollDown();
        wait.until(ExpectedConditions.presenceOfElementLocated(PRODUCT_DETAIL_INDICATORS));
        WebElement addBtn = null;
        for (By locator : ADD_TO_CART_LOCATORS) {
            List<WebElement> els = driver.findElements(locator);
            if (!els.isEmpty()) {
                addBtn = els.get(0);
                break;
            }
        }
        if (addBtn == null) {
            addBtn = wait.until(ExpectedConditions.presenceOfElementLocated(ADD_TO_CART_LOCATORS[0]));
        }
        scrollToElement(addBtn);
        jsClick(addBtn);
        try {
            WebElement goToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(GO_TO_CART_MODAL));
            jsClick(goToCartBtn);
        } catch (TimeoutException e) {
            System.out.println("[ProductDetail] Go to cart modal not found (optional): " + GO_TO_CART_MODAL);
        }
    }

    /** Sepet doğrulaması için ürün başlığını döner. */
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
                    System.out.println("[ProductDetail] Element stale while getting title, trying next: " + e.getClass().getSimpleName());
                    continue;
                }
            }
        } catch (Exception e) {
            System.out.println("[ProductDetail] Product title not found: " + PRODUCT_TITLE);
        }
        return "";
    }

    /** URL'den ürün ID'sini çıkarır (-p- sonrası, örn. HBCV0000AD46FT). Sepet doğrulaması için. */
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
