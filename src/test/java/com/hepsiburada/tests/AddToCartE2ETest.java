package com.hepsiburada.tests;

import com.hepsiburada.base.BaseTest;
import com.hepsiburada.pages.CartPage;
import com.hepsiburada.pages.HomePage;
import com.hepsiburada.pages.ProductDetailPage;
import com.hepsiburada.pages.SearchResultsPage;
import com.hepsiburada.utils.TestData;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AddToCartE2ETest extends BaseTest {

        private static final String SEARCH_TERM = TestData.SEARCH_KEYWORD_LAPTOP;
        private static final String BRAND_FILTER = TestData.BRAND_HP;

        @Test(description = "E2E: Ara -> Filtrele -> Ürün seç -> Sepete ekle -> Sepette doğrula")
        public void testProductSearchFilterAndAddToCart() {
                HomePage homePage = new HomePage(driver);
                homePage.navigate();

                SearchResultsPage resultsPage = homePage.searchProduct(SEARCH_TERM);

                Assert.assertTrue(resultsPage.areSearchResultsDisplayed(),
                                "Arama sonuçları görüntülenmeli: " + SEARCH_TERM);
                Assert.assertTrue(resultsPage.isSearchTermInResults(SEARCH_TERM),
                                "Arama başlığı arama terimini içermeli: " + SEARCH_TERM);

                int countBefore = resultsPage.getProductCount();
                String urlBefore = resultsPage.getCurrentUrl();
                resultsPage.applyBrandFilter(BRAND_FILTER);

                Assert.assertTrue(resultsPage.isBrandFilterSelected(BRAND_FILTER),
                                "Marka filtresi seçili olmalı: " + BRAND_FILTER);
                Assert.assertTrue(resultsPage.areResultsUpdatedAfterFilter(countBefore, urlBefore),
                                "Filtre uygulandıktan sonra sonuçlar güncellenmeli (adet/url).");

                ProductDetailPage detailPage = resultsPage.selectFirstProduct();

                Assert.assertTrue(detailPage.isProductDetailPageOpened(),
                                "Ürün detay sayfası açılmış olmalı.");

                String productName = detailPage.getProductTitle();
                String productId = detailPage.getProductIdFromUrl();
                detailPage.addToCart();

                CartPage cartPage = new CartPage(driver);
                cartPage.navigateToCart();

                Assert.assertTrue(cartPage.isProductInCart(),
                                "Sepette en az bir ürün olmalı.");
                Assert.assertTrue(cartPage.isSelectedProductInCart(productName, productId),
                                "Seçilen ürün sepette bulunmalı (ad veya id ile). Beklenen ad: " + productName
                                                + " | Beklenen id: " + productId);
        }
}
