package selenide.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import selenide.util.SelenidePopupHelper;

import java.util.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

@Slf4j
public class MvideoSelenidePage {

    private final SelenidePopupHelper popupHelper = new SelenidePopupHelper();


    // Локаторы
    private static final By CATALOG_BUTTON = By.xpath("//button[@aria-label='Каталог']");
    private static final By ELECTRONICS_SECTION = By.xpath("//a[contains(@class, 'left-menu__item-text')][contains(@href, 'noutbuki')]");
    private static final By PRODUCT_TITLES = By.xpath("//span[@class='name']");
    private static final By PRICE_TITLES = By.xpath("//span[@class='current-price']");
    private static final By PRICE_FILTER = By.xpath("//button[contains(@class, 'mui-chip')][.//span[contains(@class, 'chip-label') and text()='Цена']]");
    private static final By PRICE_MIN_INPUT = By.xpath("//label[contains(text(), 'От')]/preceding-sibling::input");
    private static final By PRICE_MAX_INPUT = By.xpath("//label[contains(text(), 'До')]/preceding-sibling::input");
    private static final By BRAND_FILTER = By.xpath("//button[contains(@class, 'mui-chip')][.//span[contains(@class, 'chip-label') and text()='Бренд']]");    private static final By SHOW_ALL_BRANDS = By.xpath("//button[contains(@class, 'show-all-link')][contains(text(), 'Показать всё')]");
    private static final By BRAND_SEARCH = By.xpath("//input[@placeholder='Поиск по списку']");
    private static final By APPLY_BUTTON = By.xpath("//button[contains(@class, 'mui-button') and contains(text(), 'Готово')]");
    private static final By SHOW_MORE = By.xpath("//button[contains(@class, 'load-button')][contains(text(), 'Показать ещё')]");
    private static final By SEARCH_INPUT = By.xpath("//input[@type='search' and @placeholder='Поиск в М.Видео']");
    private static final By SEARCH_BUTTON = By.xpath("//button[contains(@class, 'main-search__submit')]");

    @Step("Открыть главную страницу Mvideo")
    public MvideoSelenidePage open(String url) {
        log.info("Открытие страницы Mvideo: {}", url);
        Selenide.open(url);

        // Закрываем всплывающие окна
        popupHelper.smartClosePopups();

        $(CATALOG_BUTTON).shouldBe(visible);
        log.info("Главная страница открыта");
        return this;
    }

    @Step("Открыть каталог")
    public MvideoSelenidePage openCatalog() {
        $(CATALOG_BUTTON).click();
        return this;
    }

    @Step("Навести на раздел 'Ноутбуки'")
    public MvideoSelenidePage hoverOverLaptops() {
        $(ELECTRONICS_SECTION).hover();
        sleep(500);
        return this;
    }

    @Step("Перейти в раздел '{subCategory}'")
    public MvideoSelenidePage goToSubCategory(String subCategory) {
        By locator = By.xpath(String.format("//span[contains(@class, 'last-level-category__name') and text()='%s']", subCategory));
        $(locator).shouldBe(visible).click();
        return this;
    }

    @Step("Установить диапазон цен от {minPrice} до {maxPrice}")
    public MvideoSelenidePage setPriceRange(String minPrice, String maxPrice) {
        $(PRICE_FILTER).click();
        sleep(300);

        $(PRICE_MIN_INPUT).shouldBe(visible).clear();
        $(PRICE_MIN_INPUT).sendKeys(minPrice);

        $(PRICE_MAX_INPUT).shouldBe(visible).clear();
        $(PRICE_MAX_INPUT).sendKeys(maxPrice);

        $(APPLY_BUTTON).click();
        sleep(500);

        log.info("Цены установлены: {} - {}", minPrice, maxPrice);
        return this;
    }

    @Step("Выбрать производителей: {brands}")
    public List<String> selectBrands(List<String> brands, boolean strict) {
        List<String> selected = selectBrands(brands); // Вызываем базовый метод

        // Проверка в строгом режиме
        if (strict && selected.size() != brands.size()) {
            List<String> notFound = new ArrayList<>(brands);
            notFound.removeAll(selected);
            throw new AssertionError(String.format(
                    "❌ Не все бренды выбраны!\nОжидалось (%d): %s\n✅ Выбрано (%d): %s\n❌ Не найдено (%d): %s",
                    brands.size(), brands,
                    selected.size(), selected,
                    notFound.size(), notFound
            ));
        }

        return selected;
    }

    @Step("Выбрать производителей: {brands}")
    public List<String> selectBrands(List<String> brands) {
        $(BRAND_FILTER).click();
        sleep(300);
        $(SHOW_ALL_BRANDS).click();
        $(BRAND_SEARCH).shouldBe(visible);

        List<String> selected = new ArrayList<>();

        for (String brand : brands) {
            $(BRAND_SEARCH).clear();
            $(BRAND_SEARCH).sendKeys(brand);
            sleep(500);

            String xpath = String.format(
                    "//mui-checkbox[.//span[contains(@class, 'checkbox__content')]" +
                            "[translate(normalize-space(text()), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '%s']]" +
                            "//label[@class='mui-checkbox']",
                    brand.toLowerCase()
            );

            ElementsCollection checkboxes = $$(By.xpath(xpath));
            if (!checkboxes.isEmpty()) {
                checkboxes.first().scrollIntoView(true).click();
                selected.add(brand);
                log.debug("✅ Выбран: {}", brand);
            } else {
                log.warn("❌ Не найден: {}", brand);
            }
            sleep(200);
        }

        $(APPLY_BUTTON).click();
        sleep(500);

        return selected;
    }

    @Step("Получить названия и цены товаров")
    public Map<String, List<String>> getProductNamesAndPrices() {
        Set<String> names = new LinkedHashSet<>();
        Set<String> prices = new LinkedHashSet<>();

        // Прокрутка и сбор данных
        for (int i = 0; i < 50; i++) {
            // Собираем названия
            $$(PRODUCT_TITLES).forEach(el -> {
                String text = el.getText();
                if (!text.isEmpty()) names.add(text);
            });

            // Собираем цены
            $$(PRICE_TITLES).forEach(el -> {
                String text = el.getText().replaceAll("[^\\d]", "");
                if (!text.isEmpty()) prices.add(text);
            });

            // Проверяем кнопку "Показать ещё"
            if ($$(SHOW_MORE).size() > 0) {
                $(SHOW_MORE).scrollIntoView(true);
                sleep(300);
                Selenide.executeJavaScript("arguments[0].click();", $(SHOW_MORE));
                sleep(1500);
                continue;
            }

            // Проверяем конец страницы
            if (isPageEndReached()) {
                sleep(1500);
                break;
            }

            Selenide.executeJavaScript("window.scrollBy(0, 800);");
            sleep(500);
        }

        log.info("Собрано: названий={}, цен={}", names.size(), prices.size());
        return Map.of("names", new ArrayList<>(names), "prices", new ArrayList<>(prices));
    }

    @Step("Получить название первого товара")
    public String getFirstProductName() {
        Selenide.executeJavaScript("window.scrollTo(0, 0);");
        sleep(500);
        return $(PRODUCT_TITLES).getText();
    }

    @Step("Выполнить поиск товара: {productName}")
    public MvideoSelenidePage searchProduct(String productName) {
        $(SEARCH_INPUT).shouldBe(visible).clear();
        $(SEARCH_INPUT).sendKeys(productName);
        $(SEARCH_BUTTON).click();
        sleep(1000);
        return this;
    }

    @Step("Проверить наличие товара в результатах поиска")
    public boolean verifySearchResultsContainProduct(String productName) {
        String script =
                "return Array.from(document.querySelectorAll('span.name'))" +
                        ".some(el => (el.textContent || '').toLowerCase().includes('" +
                        productName.toLowerCase() + "'));";

        return Boolean.TRUE.equals(Selenide.executeJavaScript(script));
    }

    private boolean isPageEndReached() {
        try {
            // JavaScript возвращает Double, а не Long!
            Number scrollY = (Number) Selenide.executeJavaScript("return window.pageYOffset + window.innerHeight;");
            Number height = (Number) Selenide.executeJavaScript("return document.body.scrollHeight;");

            // Используем doubleValue() вместо longValue()
            return scrollY.doubleValue() >= height.doubleValue() - 100;
        } catch (Exception e) {
            log.warn("Не удалось проверить конец страницы: {}", e.getMessage());
            return false;
        }
    }
}
