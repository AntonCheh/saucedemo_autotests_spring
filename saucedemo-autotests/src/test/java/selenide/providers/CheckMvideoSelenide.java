package selenide.providers;

import lombok.extern.slf4j.Slf4j;
import org.aeonbits.owner.ConfigFactory;
import selenide.helpers.BaseTestSelenide;
import selenide.helpers.SelenideSoftAssert;
import selenide.pages.MvideoSelenidePage;
import selenium.assertions.AssertionsWeb;
import selenium.config.TestConfig;
import selenium.sources.mvideoTest.DataForMvideoPurchases;
import selenium.sources.yandexTest.PurchasesSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class CheckMvideoSelenide extends BaseTestSelenide implements DataForMvideoPurchases {

    private final AssertionsWeb assertionsWeb;
    private final TestConfig config;
    private final MvideoSelenidePage page;

    public CheckMvideoSelenide(AssertionsWeb assertionsWeb, MvideoSelenidePage page) {
        this.assertionsWeb = assertionsWeb;
        this.config = ConfigFactory.create(TestConfig.class);
        this.page = page;
    }

    public CheckMvideoSelenide() {
        this(new AssertionsWeb(), new MvideoSelenidePage());
    }

    public void executeWithSoftAssertMvideo(PurchasesSource testsSource) {
        SelenideSoftAssert softAssert = new SelenideSoftAssert();
        List<String> selectedBrands = new ArrayList<>();

        // ==================== 1. НАВИГАЦИЯ ====================
        softAssert.executeStep(() -> page.open(config.mvideoUrl()), "Открытие страницы");
        softAssert.executeStep(page::openCatalog, "Открытие каталога");
        softAssert.executeStep(page::hoverOverLaptops, "Наведение на Ноутбуки");
        softAssert.executeStep(
                () -> page.goToSubCategory("Все ноутбуки"),
                "Переход в " + testsSource.laptopTitles()
        );

        // ==================== 2. ФИЛЬТРЫ ====================
        softAssert.executeStep(
                () -> page.setPriceRange(testsSource.priceMin(), testsSource.priceMax()),
                "Установка цены"
        );

        softAssert.executeStep(
                () -> selectedBrands.addAll(page.selectBrands(testsSource.producer(), true)),
                "Выбор производителей"
        );

        // ==================== 3. СБОР ДАННЫХ ====================
        Map<String, List<String>> data = page.getProductNamesAndPrices();
        List<String> productNames = data.get("names");
        List<String> prices = data.get("prices");

        // ==================== 4. ПРОВЕРКИ ====================
        softAssert.executeStep(
                () -> assertionsWeb.assertCountGreaterThan(12, productNames.size()),
                "Проверка количества товаров"
        );

        softAssert.executeStep(
                () -> assertionsWeb.assertProductNamesContainBrands(productNames, testsSource.producer()),
                "Проверка соответствия брендов"
        );

        softAssert.executeStep(
                () -> assertionsWeb.assertPricesInRange(prices, testsSource.priceMin(), testsSource.priceMax()),
                "Проверка диапазона цен"
        );

        // ==================== 5. ПОИСК ====================
        String firstProduct = page.getFirstProductName();

        softAssert.executeStep(
                () -> page.searchProduct(firstProduct),
                "Поиск первого товара"
        );

        softAssert.executeStep(
                () -> {
                    boolean found = page.verifySearchResultsContainProduct(firstProduct);
                    if (!found) throw new AssertionError("Товар не найден: " + firstProduct);
                },
                "Проверка наличия товара в результатах"
        );

        // ==================== 6. ФИНАЛИЗАЦИЯ ====================
        softAssert.assertAll();
    }
}