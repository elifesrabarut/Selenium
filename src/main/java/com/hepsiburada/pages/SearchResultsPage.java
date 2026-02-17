package com.hepsiburada.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SearchResultsPage extends BasePage {

    // Arama sonucu başlığı - "laptop araması (10.000+ ürün)" - arama yapıldığını doğrular
    private static final By SEARCH_RESULT_HEADER = By.cssSelector("h1[data-test-id='header-h1']");

    // Ürün linkleri
    private static final By PRODUCT_LINKS = By.cssSelector(
            "a[href*='/p-'], " +
            "a[href*='hepsiburada.com'][href*='-p-'], " +
            "a[data-product-id]"
    );
    private static final By PRODUCT_ITEMS = By.cssSelector(
            "li[data-product-id], [data-product-id], " +
            ".productListContent-item, [data-test-id='product-card']"
    );
    private static final By FIRST_PRODUCT = By.cssSelector(
            "a[href*='/p-'], a[href*='-p-'][href*='hepsiburada']"
    );
    
    // Filtre paneli - Marka (HepsiBurada: data-test-id="not_checked"/"checked" + label + input name="markalar")
    private static final By FILTER_REGION = By.cssSelector("[role='region'][aria-label='Sonuçları filtrele']");
    private static final By MARKA_SECTION = By.id("markalar");
    private static final By BRAND_UNCHECKED_ITEMS = By.cssSelector("[data-test-id='not_checked'] label");
    private static final By BRAND_CHECKED_ITEMS = By.cssSelector("[data-test-id='checked']");
    private static final By BRAND_CHECKBOXES = By.cssSelector("#markalar input[name='markalar'], input[name='markalar']");
    private static final By BRAND_LABELS = By.cssSelector("#markalar label[class*='checkbox']");

    // Fiyat filtresi - HepsiBurada: fiyat bölümü, aralık linkleri
    private static final By PRICE_SECTION = By.cssSelector("#fiyat, [id*='fiyat'], [aria-label*='Fiyat']");
    private static final By PRICE_OPTIONS = By.cssSelector("#fiyat a[href*='fiyat'], #fiyat label[class*='checkbox'], [id*='fiyat'] input, [id*='fiyat'] a");

    // Değerlendirme filtresi - 4 yıldız ve üzeri vb.
    private static final By RATING_SECTION = By.cssSelector("#degerlendirme, [id*='degerlendirme'], [id*='puan'], [aria-label*='Değerlendirme']");
    private static final By RATING_CHECKBOXES = By.cssSelector("#degerlendirme input, [id*='degerlendirme'] input, [id*='puan'] input");
    private static final By RATING_LABELS = By.cssSelector("#degerlendirme label[class*='checkbox'], [id*='degerlendirme'] label, [id*='puan'] label");

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Arama sonuçlarının geldiğini doğrular.
     * HepsiBurada: h1[data-test-id='header-h1'] - "laptop araması (10.000+ ürün)" metni.
     */
    public boolean areSearchResultsDisplayed() {
        try {
            WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_RESULT_HEADER));
            return header.getText().contains("araması") && header.getText().contains("ürün");
        } catch (TimeoutException | NoSuchElementException e) {
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(PRODUCT_LINKS));
                return !driver.findElements(PRODUCT_LINKS).isEmpty();
            } catch (TimeoutException | NoSuchElementException e2) {
                System.out.println("[SearchResults] Product links not found: " + PRODUCT_LINKS);
                return false;
            }
        }
    }

    /** Aranan terimin (örn. laptop) sonuç başlığında olduğunu doğrular */
    public boolean isSearchTermInResults(String searchTerm) {
        try {
            WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_RESULT_HEADER));
            return header.getText().toLowerCase().contains(searchTerm.toLowerCase());
        } catch (TimeoutException | NoSuchElementException e) {
            System.out.println("[SearchResults] Search term not found in results: " + e.getClass().getSimpleName());
            return false;
        }
    }

    public int getProductCount() {
        List<WebElement> products = driver.findElements(PRODUCT_LINKS);
        return products.isEmpty() ? driver.findElements(PRODUCT_ITEMS).size() : products.size();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * En az bir filtre uygular. Sırayla dener: marka → fiyat → değerlendirme.
     * Filtre paneli görünür olması için önce sayfa ortasına scroll yapar.
     * Filtre uygulandıktan sonra sonuçların güncellenmesi beklenir.
     *
     * @param preferredBrand tercih edilen marka adı (örn. "HP"); null/boş ise ilk uygun marka seçilir
     */
    public SearchResultsPage applyFirstFilter(String preferredBrand) {
        scrollToPageMiddle();
        if (applyBrandFilter(preferredBrand)) {
            waitForFilterToApply();
            return this;
        }
        if (applyPriceFilter()) {
            waitForFilterToApply();
            return this;
        }
        if (applyRatingFilter()) {
            waitForFilterToApply();
            return this;
        }
        return this;
    }

    /** @see #applyFirstFilter(String) */
    public SearchResultsPage applyFirstFilter() {
        return applyFirstFilter(null);
    }

    /** Filtre uygulandıktan sonra sonuçların yüklenmesini bekler. */
    private void waitForFilterToApply() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class*='loading'], [class*='skeleton']")));
        } catch (TimeoutException | NoSuchElementException e) {
            System.out.println("[SearchResults] Loading indicator not found or already hidden: " + e.getClass().getSimpleName());
        }
        wait.until(ExpectedConditions.or(
                ExpectedConditions.numberOfElementsToBeMoreThan(PRODUCT_ITEMS, 0),
                ExpectedConditions.numberOfElementsToBeMoreThan(PRODUCT_LINKS, 0)
        ));
    }

    /** @deprecated {@link #applyFirstFilter(String)} kullanın */
    public SearchResultsPage applyFirstBrandFilter() {
        return applyFirstFilter();
    }

    private boolean applyBrandFilter(String preferredBrand) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(MARKA_SECTION));
            List<WebElement> uncheckedLabels = driver.findElements(BRAND_UNCHECKED_ITEMS);
            if (!uncheckedLabels.isEmpty()) {
                String search = (preferredBrand != null && !preferredBrand.isBlank()) ? preferredBrand.toLowerCase() : null;
                for (WebElement label : uncheckedLabels) {
                    if (search == null || label.getText().toLowerCase().contains(search)) {
                        scrollToElement(label);
                        wait.until(ExpectedConditions.elementToBeClickable(label));
                        jsClick(findClickableInLabel(label));
                        return true;
                    }
                }
            }
            if (preferredBrand != null && !preferredBrand.isBlank()) {
                List<WebElement> labels = driver.findElements(BRAND_LABELS);
                String search = preferredBrand.toLowerCase();
                for (WebElement label : labels) {
                    if (label.getText().toLowerCase().contains(search)) {
                        scrollToElement(label);
                        wait.until(ExpectedConditions.elementToBeClickable(label));
                        jsClick(findClickableInLabel(label));
                        return true;
                    }
                }
            }
            List<WebElement> checkboxes = driver.findElements(BRAND_CHECKBOXES);
            for (WebElement checkbox : checkboxes) {
                try {
                    if (!checkbox.isSelected()) {
                        scrollToElement(checkbox);
                        wait.until(ExpectedConditions.elementToBeClickable(checkbox));
                        jsClick(checkbox);
                        return true;
                    }
                } catch (TimeoutException | NoSuchElementException e) {
                    try {
                        WebElement label = checkbox.findElement(By.xpath("./ancestor::label"));
                        scrollToElement(label);
                        wait.until(ExpectedConditions.elementToBeClickable(label));
                        jsClick(findClickableInLabel(label));
                        return true;
                    } catch (TimeoutException | NoSuchElementException | org.openqa.selenium.StaleElementReferenceException e2) {
                        System.out.println("[SearchResults] Checkbox fallback failed for brand '" + preferredBrand + "': " + e2.getClass().getSimpleName());
                        continue;
                    }
                }
            }
            List<WebElement> labels = driver.findElements(BRAND_LABELS);
            if (!labels.isEmpty()) {
                WebElement firstLabel = labels.get(0);
                scrollToElement(firstLabel);
                wait.until(ExpectedConditions.elementToBeClickable(firstLabel));
                jsClick(findClickableInLabel(firstLabel));
                return true;
            }
        } catch (TimeoutException | NoSuchElementException e) {
            System.out.println("[SearchResults] Brand filter element not found for brand '" + preferredBrand + "': " + e.getClass().getSimpleName());
            return false;
        }
        return false;
    }

    /** Label içindeki input varsa onu, yoksa label'ı döner - tıklanacak element. */
    private WebElement findClickableInLabel(WebElement label) {
        try {
            WebElement input = label.findElement(By.cssSelector("input[name='markalar']"));
            return input;
        } catch (NoSuchElementException e) {
            System.out.println("[SearchResults] Input element not found in label, using label itself: input[name='markalar']");
            return label;
        }
    }

    private boolean applyPriceFilter() {
        try {
            if (driver.findElements(PRICE_SECTION).isEmpty()) return false;
            wait.until(ExpectedConditions.presenceOfElementLocated(PRICE_SECTION));
            List<WebElement> options = driver.findElements(PRICE_OPTIONS);
            for (WebElement opt : options) {
                try {
                    scrollToElement(opt);
                    wait.until(ExpectedConditions.elementToBeClickable(opt));
                    opt.click();
                    return true;
                } catch (TimeoutException | NoSuchElementException e) {
                    System.out.println("[SearchResults] Price option not clickable: " + e.getClass().getSimpleName());
                    continue;
                }
            }
        } catch (TimeoutException | NoSuchElementException e) {
            System.out.println("[SearchResults] Price filter element not found: " + PRICE_SECTION);
            return false;
        }
        return false;
    }

    private boolean applyRatingFilter() {
        try {
            if (driver.findElements(RATING_SECTION).isEmpty()) return false;
            wait.until(ExpectedConditions.presenceOfElementLocated(RATING_SECTION));
            List<WebElement> checkboxes = driver.findElements(RATING_CHECKBOXES);
            for (WebElement cb : checkboxes) {
                try {
                    if (!cb.isSelected()) {
                        scrollToElement(cb);
                        wait.until(ExpectedConditions.elementToBeClickable(cb));
                        cb.click();
                        return true;
                    }
                } catch (TimeoutException | NoSuchElementException e) {
                    System.out.println("[SearchResults] Rating checkbox not clickable: " + e.getClass().getSimpleName());
                    continue;
                }
            }
            List<WebElement> labels = driver.findElements(RATING_LABELS);
            if (!labels.isEmpty()) {
                WebElement first = labels.get(0);
                scrollToElement(first);
                wait.until(ExpectedConditions.elementToBeClickable(first));
                first.click();
                return true;
            }
        } catch (TimeoutException | NoSuchElementException e) {
            System.out.println("[SearchResults] Rating filter element not found: " + RATING_SECTION);
            return false;
        }
        return false;
    }

    /**
     * Filtre sonrası sonuçların güncellendiğini doğrular.
     * URL veya ürün sayısı değiştiyse true.
     */
    public boolean areResultsUpdatedAfterFilter(int countBefore, String urlBefore) {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.numberOfElementsToBeMoreThan(PRODUCT_ITEMS, 0),
                    ExpectedConditions.numberOfElementsToBeMoreThan(PRODUCT_LINKS, 0)
            ));
        } catch (TimeoutException e) {
            System.out.println("[SearchResults] Filter results not updated (URL or count unchanged): " + e.getClass().getSimpleName());
            return false;
        }
        String urlAfter = getCurrentUrl();
        int countAfter = getProductCount();
        return !urlBefore.equals(urlAfter) || countBefore != countAfter;
    }

    /**
     * Marka filtresinin seçildiğini doğrular. Üç kontrol:
     * 1) URL'de filtre parametresi
     * 2) Seçili filtre chip'i (data-test-id="checked")
     * 3) Marka checkbox selected
     */
    public boolean isBrandFilterSelected(String brand) {
        if (brand == null || brand.isBlank()) return true;
        if (isBrandInUrl(brand)) return true;
        if (isFilterChipVisible(brand)) return true;
        return isBrandCheckboxSelected(brand);
    }

    private boolean isBrandInUrl(String brand) {
        return driver.getCurrentUrl().toLowerCase().contains(brand.toLowerCase());
    }

    private boolean isFilterChipVisible(String brand) {
        List<WebElement> checked = driver.findElements(BRAND_CHECKED_ITEMS);
        String term = brand.toLowerCase();
        for (WebElement el : checked) {
            if (el.getText().toLowerCase().contains(term)) return true;
        }
        return false;
    }

    private boolean isBrandCheckboxSelected(String brand) {
        List<WebElement> inputs = driver.findElements(By.cssSelector("#markalar input[name='markalar']:checked"));
        String term = brand.toLowerCase();
        for (WebElement input : inputs) {
            String val = input.getAttribute("value");
            if (val != null && val.toLowerCase().contains(term)) return true;
        }
        return false;
    }

    public ProductDetailPage selectFirstProduct() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class*='LoadingWrapper'], [class*='loading']")));
        } catch (TimeoutException e) {
            System.out.println("[SearchResults] Loading overlay not found or already gone: " + e.getClass().getSimpleName());
        }
        Set<String> handlesBefore = driver.getWindowHandles();
        WebElement firstProductLink = wait.until(ExpectedConditions.elementToBeClickable(FIRST_PRODUCT));
        scrollToElement(firstProductLink);
        jsClick(firstProductLink);
        // HepsiBurada ürün linkleri yeni sekmede açılabiliyor; yeni sekmeye geç
        try {
            wait.until(d -> {
                Set<String> handles = driver.getWindowHandles();
                handles.removeAll(handlesBefore);
                return !handles.isEmpty();
            });
            List<String> handlesList = new ArrayList<>(driver.getWindowHandles());
            handlesList.removeAll(handlesBefore);
            if (!handlesList.isEmpty()) {
                driver.switchTo().window(handlesList.get(0));
                wait.until(ExpectedConditions.urlContains("-p"));
            }
        } catch (TimeoutException e) {
            System.out.println("[SearchResults] New tab not opened (opened in same tab): " + e.getClass().getSimpleName());
        }
        return new ProductDetailPage(driver);
    }
}
