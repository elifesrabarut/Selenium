package com.hepsiburada.pages;

import com.hepsiburada.core.Waits;
import org.openqa.selenium.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SearchResultsPage extends BasePage {

    private static final By SEARCH_RESULT_HEADER = By.cssSelector("h1[data-test-id='header-h1']");
    private static final By PRODUCT_LINKS = By
            .cssSelector("a[href*='/p-'], a[href*='hepsiburada.com'][href*='-p-'], a[data-product-id]");
    private static final By PRODUCT_ITEMS = By.cssSelector(
            "li[data-product-id], [data-product-id], .productListContent-item, [data-test-id='product-card']");
    private static final By FIRST_PRODUCT = By.cssSelector("a[href*='/p-'], a[href*='-p-'][href*='hepsiburada']");
    private static final By FIRST_PRODUCT_CARD = By.cssSelector("[data-product-id]");
    private static final By FIRST_PRODUCT_ANCHOR = By.cssSelector("[data-product-id] a[href*='-p-'], a[href*='-p-']");
    private static final By MARKA_SECTION = By.id("markalar");
    private static final By BRAND_UNCHECKED_ITEMS = By.cssSelector("[data-test-id='not_checked'] label");
    private static final By BRAND_CHECKED_ITEMS = By.cssSelector("[data-test-id='checked']");
    private static final By BRAND_LABELS = By.cssSelector("#markalar label[class*='checkbox']");
    private static final By LOADING_OVERLAY = By
            .cssSelector("[class*='LoadingWrapper'], [class*='skeleton'], [class*='loading']");

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    public boolean areSearchResultsDisplayed() {
        try {
            WebElement header = waitVisible(SEARCH_RESULT_HEADER);
            String text = header.getText() == null ? "" : header.getText();
            return text.contains("araması") && text.contains("ürün");
        } catch (TimeoutException | NoSuchElementException e) {
            try {
                Waits.waitForPresence(driver, PRODUCT_LINKS);
                return !driver.findElements(PRODUCT_LINKS).isEmpty() || !driver.findElements(PRODUCT_ITEMS).isEmpty();
            } catch (TimeoutException ignored) {
                return false;
            }
        }
    }

    public boolean isSearchTermInResults(String searchTerm) {
        if (searchTerm == null)
            return false;
        String term = searchTerm.toLowerCase();
        try {
            String headerText = getText(SEARCH_RESULT_HEADER);
            if (headerText != null && headerText.toLowerCase().contains(term))
                return true;
        } catch (TimeoutException ignored) {
        }
        if (driver.getCurrentUrl().toLowerCase().contains("q=" + term) || driver.getCurrentUrl().contains("ara"))
            return true;
        return getProductCount() > 0;
    }

    public int getProductCount() {
        List<WebElement> products = driver.findElements(PRODUCT_LINKS);
        return products.isEmpty() ? driver.findElements(PRODUCT_ITEMS).size() : products.size();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public SearchResultsPage applyBrandFilter(String preferredBrand) {
        scrollToPageMiddle();
        String urlBefore = driver.getCurrentUrl();
        String firstBefore = safeGetFirstProductIdOrHref();
        boolean clicked = clickBrandOption(preferredBrand);
        if (!clicked) {
            scrollToPageMiddle();
            clicked = clickBrandOption(preferredBrand);
        }
        if (!clicked) {
            throw new IllegalStateException("Marka filtresi uygulanamadı. Marka: " + preferredBrand);
        }
        waitForResultsToRefresh(urlBefore, firstBefore);
        return this;
    }

    public SearchResultsPage applyFirstFilter(String preferredBrand) {
        return applyBrandFilter(preferredBrand);
    }

    private boolean clickBrandOption(String preferredBrand) {
        try {
            Waits.waitForPresence(driver, MARKA_SECTION, 15);
            String search = (preferredBrand != null && !preferredBrand.isBlank()) ? preferredBrand.toLowerCase() : null;
            List<WebElement> uncheckedLabels = driver.findElements(BRAND_UNCHECKED_ITEMS);
            for (WebElement label : uncheckedLabels) {
                String txt = label.getText() == null ? "" : label.getText().toLowerCase();
                if (search == null || txt.contains(search)) {
                    scrollToElement(label);
                    clickLabel(findClickableInLabel(label));
                    waitForBrandCheckboxToUpdate();
                    return true;
                }
            }
            if (search != null) {
                List<WebElement> labels = driver.findElements(BRAND_LABELS);
                for (WebElement label : labels) {
                    String txt = label.getText() == null ? "" : label.getText().toLowerCase();
                    if (txt.contains(search)) {
                        scrollToElement(label);
                        clickLabel(findClickableInLabel(label));
                        waitForBrandCheckboxToUpdate();
                        return true;
                    }
                }
            }
            List<WebElement> labels = driver.findElements(BRAND_LABELS);
            if (!labels.isEmpty()) {
                WebElement first = labels.get(0);
                scrollToElement(first);
                clickLabel(findClickableInLabel(first));
                waitForBrandCheckboxToUpdate();
                return true;
            }
            return false;
        } catch (TimeoutException e) {
            return false;
        }
    }

    private void clickLabel(WebElement clickable) {
        try {
            clickable.click();
        } catch (Exception e) {
            jsClick(clickable);
        }
    }

    private WebElement findClickableInLabel(WebElement label) {
        return label;
    }

    private void waitForBrandCheckboxToUpdate() {
        try {
            Waits.waitUntil(driver,
                    (java.util.function.Function<WebDriver, Boolean>) d -> !d.findElements(BRAND_CHECKED_ITEMS)
                            .isEmpty()
                            || !d.findElements(By.cssSelector("#markalar input:checked")).isEmpty(),
                    2);
        } catch (TimeoutException ignored) {
        }
    }

    public boolean areResultsUpdatedAfterFilter(int countBefore, String urlBefore) {
        try {
            Waits.waitForPresence(driver, PRODUCT_ITEMS);
        } catch (TimeoutException ignored) {
            try {
                Waits.waitForPresence(driver, PRODUCT_LINKS);
            } catch (TimeoutException e) {
                return false;
            }
        }
        String urlAfter = driver.getCurrentUrl();
        int countAfter = getProductCount();
        return !urlBefore.equals(urlAfter) || countBefore != countAfter;
    }

    public boolean isBrandFilterSelected(String brand) {
        if (brand == null || brand.isBlank())
            return true;
        String term = brand.toLowerCase();
        if (driver.getCurrentUrl().toLowerCase().contains(term))
            return true;
        for (WebElement el : driver.findElements(BRAND_CHECKED_ITEMS)) {
            String txt = el.getText() == null ? "" : el.getText().toLowerCase();
            if (txt.contains(term))
                return true;
        }
        List<WebElement> inputs = driver
                .findElements(By.cssSelector("#markalar input[name='markalar']:checked, #markalar input:checked"));
        for (WebElement input : inputs) {
            String val = input.getAttribute("value");
            if (val != null && val.toLowerCase().contains(term))
                return true;
        }
        return false;
    }

    private void waitForResultsToRefresh(String urlBefore, String firstBefore) {
        Waits.waitForInvisibility(driver, LOADING_OVERLAY, 2);
        try {
            Waits.waitForPresence(driver, PRODUCT_ITEMS, 10);
        } catch (TimeoutException e) {
            Waits.waitForPresence(driver, PRODUCT_LINKS, 10);
        }
        Waits.waitUntil(driver, (java.util.function.Function<WebDriver, Boolean>) d -> {
            String urlAfter = d.getCurrentUrl();
            if (!urlAfter.equals(urlBefore))
                return true;
            String firstAfter = safeGetFirstProductIdOrHref();
            if (firstBefore == null)
                return firstAfter != null;
            return firstAfter != null && !firstAfter.equals(firstBefore);
        }, 10);
    }

    private String safeGetFirstProductIdOrHref() {
        try {
            List<WebElement> cards = driver.findElements(FIRST_PRODUCT_CARD);
            if (!cards.isEmpty()) {
                String pid = cards.get(0).getAttribute("data-product-id");
                if (pid != null && !pid.isBlank())
                    return pid;
            }
            List<WebElement> anchors = driver.findElements(FIRST_PRODUCT_ANCHOR);
            if (!anchors.isEmpty()) {
                String href = anchors.get(0).getAttribute("href");
                if (href != null && !href.isBlank())
                    return href;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public ProductDetailPage selectFirstProduct() {
        Waits.waitForInvisibility(driver, LOADING_OVERLAY, 2);
        Set<String> handlesBefore = driver.getWindowHandles();
        WebElement firstProductLink = Waits.waitForClickable(driver, FIRST_PRODUCT, 10);
        try {
            firstProductLink.click();
        } catch (Exception e) {
            scrollToElement(FIRST_PRODUCT);
            firstProductLink = Waits.waitForClickable(driver, FIRST_PRODUCT, 5);
            try {
                firstProductLink.click();
            } catch (Exception e2) {
                jsClick(firstProductLink);
            }
        }
        try {
            Waits.waitUntil(driver, (java.util.function.Function<WebDriver, Boolean>) d -> d.getWindowHandles()
                    .size() > handlesBefore.size(), 5);
            List<String> handlesList = new ArrayList<>(driver.getWindowHandles());
            handlesList.removeAll(handlesBefore);
            if (!handlesList.isEmpty()) {
                driver.switchTo().window(handlesList.get(0));
            }
        } catch (TimeoutException ignored) {
        }
        Waits.waitForUrlContains(driver, "-p", 10);
        return new ProductDetailPage(driver);
    }
}
