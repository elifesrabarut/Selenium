package com.hepsiburada.tests;

import com.hepsiburada.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * End-to-end test: Product Search + Filtering + Add to Cart
 * Test scenario for https://www.hepsiburada.com
 */
public class ProductSearchAddToCartTest extends BaseTest {

    private static final String SEARCH_TERM = "laptop";

    @Test(description = "E2E: Search, filter, select product, add to cart and verify")
    public void testProductSearchFilterAndAddToCart() {
        // Step 1: Navigate to hepsiburada.com
        HomePage homePage = new HomePage(driver);
        homePage.navigate();

        // Step 2: Search for a product (e.g., laptop)
        SearchResultsPage searchResultsPage = homePage.searchProduct(SEARCH_TERM);
        if (!searchResultsPage.areSearchResultsDisplayed()) {
            searchResultsPage = homePage.navigateToLaptopCategory();
        }

        // Step 3: Verify that search results are displayed (h1: "laptop araması (X ürün)")
        Assert.assertTrue(searchResultsPage.areSearchResultsDisplayed(),
                "Search results should be displayed for: " + SEARCH_TERM);
        Assert.assertTrue(searchResultsPage.isSearchTermInResults(SEARCH_TERM),
                "Search header should contain search term: " + SEARCH_TERM);

        // Step 4: Filtre öncesi durumu al, filtre uygula
        int countBefore = searchResultsPage.getProductCount();
        String urlBefore = searchResultsPage.getCurrentUrl();
        searchResultsPage.applyFirstFilter("HP");

        // Step 5: URL veya ürün sayısı değişti mi, marka filtresi seçili mi
        Assert.assertTrue(searchResultsPage.areResultsUpdatedAfterFilter(countBefore, urlBefore),
                "Results should be updated after filter (URL or product count changed)");
        Assert.assertTrue(searchResultsPage.isBrandFilterSelected("HP"),
                "Brand filter (HP) should be selected - URL should contain filter");

        // Step 6: Select any product from the results list
        ProductDetailPage productDetailPage = searchResultsPage.selectFirstProduct();

        // Step 7: Verify that the product detail page is opened
        Assert.assertTrue(productDetailPage.isProductDetailPageOpened(),
                "Product detail page should be opened");

        // Step 8: Seçilen ürünü kaydet (ad + ID) ve sepete ekle
        String productName = productDetailPage.getProductTitle();
        String productId = productDetailPage.getProductIdFromUrl();
        productDetailPage.addToCart();

        // Step 9: Navigate to the cart page
        CartPage cartPage = new CartPage(driver);
        cartPage.navigateToCart();

        // Step 10: Seçilen ürünün sepette olduğunu doğrula (ad veya ID ile)
        Assert.assertTrue(cartPage.isProductInCart(),
                "Cart should contain items");
        Assert.assertTrue(cartPage.isSelectedProductInCart(productName, productId),
                "Selected product (by name or ID) should be present in cart");
    }
}
