# HepsiBurada E2E Test Automation

End-to-end test automation for **Product Search + Filtering + Add to Cart** scenario on [hepsiburada.com](https://www.hepsiburada.com).

## Technologies Used

- **Java 17**
- **Selenium WebDriver 4.25**
- **TestNG 7.10**
- **Maven**
- **Page Object Model (POM)** design pattern
- **Explicit Waits** (WebDriverWait) — `Thread.sleep` is **not** used

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Chrome browser (default) — Firefox and Edge are also supported

## How to Run

### Run all tests
```bash
mvn clean test
```

### Run with a specific browser
```bash
mvn clean test -Dbrowser=chrome
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=edge
```

### Run a specific test class
```bash
mvn clean test -Dtest=ProductSearchAddToCartTest
```

## Project Structure

```
src/
├── main/java/com/hepsiburada/
│   ├── core/
│   │   └── DriverFactory.java
│   └── pages/
│       ├── BasePage.java
│       ├── HomePage.java
│       ├── SearchResultsPage.java
│       ├── ProductDetailPage.java
│       └── CartPage.java
└── test/java/com/hepsiburada/tests/
    ├── BaseTest.java
    └── ProductSearchAddToCartTest.java
```

## Test Scenario (10 Steps)

1. Navigate to https://www.hepsiburada.com
2. Search for a product (e.g., laptop)
3. Verify search results are displayed
4. Apply at least one filter (brand, price, rating)
5. Verify results are updated after filtering
6. Select a product from the results
7. Verify product detail page is opened
8. Add the product to the cart
9. Navigate to the cart page
10. Verify the selected product is in the cart

## Submission Zip

When creating a zip for submission, **exclude**:
- `target/` (build output, surefire-reports)
- `__MACOSX/` (Mac zip metadata)
- `.DS_Store` (Mac finder metadata)

Use: `zip -r submission.zip . -x "target/*" -x "__MACOSX/*" -x "*.DS_Store"` or ensure `.gitignore` is respected.

## Assumptions

- **Cookie consent**: The framework attempts to accept cookie banners if present (e.g., "Kabul Et" button).
- **Add-to-cart modal**: After adding to cart, HepsiBurada may show a modal with "Sepete Git". The framework handles both cases: clicking the modal link if present, or navigating directly to `/sepetim`.
- **Selectors**: The site structure may change. Selectors use flexible patterns (placeholder text, common class names, XPath). If tests fail, selectors may need updating.
- **Filters**: The first available filter checkbox is applied. Filter options (brand, price, rating) vary by search results.
- **Product availability**: Tests assume products are in stock and addable to cart.
- **No login required**: The scenario runs without user authentication.
