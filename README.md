# Venhancer — HepsiBurada E2E

HepsiBurada’da arama → filtreleme → ürün seçimi → sepete ekleme akışını otomatik test eden proje.

**Kullandıklarım:** Java 17, Selenium 4, TestNG, Maven, Page Object Model. Beklemeler element hazır olana kadar explicit wait (WebDriverWait) ile yapılıyor; sabit süre beklemek yerine koşul sağlanınca devam edildiği için `Thread.sleep` kullanılmıyor (daha stabil ve gereksiz bekleme yok).

**Gereksinimler:** Java 17+, Maven, Chrome (isteğe göre Firefox/Edge).

## Çalıştırma

```bash
mvn clean test
```

Farklı tarayıcı: `mvn clean test -Dbrowser=firefox`  
Sadece E2E testi: `mvn clean test -Dtest=AddToCartE2ETest`

## Yapı

- `src/main/java/.../core` — DriverFactory, DriverManager, Waits
- `src/main/java/.../pages` — BasePage, HomePage, SearchResultsPage, ProductDetailPage, CartPage
- `src/main/java/.../utils` — Config, TestData
- `src/test/.../base` — BaseTest
- `src/test/.../tests` — AddToCartE2ETest
- `src/test/resources/config.properties` — base.url, browser vb.

## Test akışı

Ana sayfa → arama (örn. laptop) → sonuçların gelmesi → marka filtresi (örn. HP) → ilk ürüne tıklama → ürün detay → sepete ekle → sepete git → sepette ürünün olması kontrolü.

Çerez banner’ı varsa kabul ediliyor; sepete ekledikten sonra “Sepete Git” modal’ı varsa oradan, yoksa doğrudan `/sepetim` ile sepete gidiliyor.

## Teslim zip’i

Zip alırken `target/`, `__MACOSX/`, `.DS_Store` dahil etme. Örnek:
