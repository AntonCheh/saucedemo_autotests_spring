package selenium.pages.mvideo_purchases;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import selenium.base.BasePage;
import selenium.models.FilterSelectionResult;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class MvideoPurchasesPage extends BasePage {

    private static final By CATALOG_BUTTON = By.xpath("//button[@aria-label='Каталог']");
    private static final By ELECTRONICS_SECTION = By.xpath("//a[contains(@class, 'left-menu__item-text')][contains(@href, 'noutbuki')]");
    private static final By PRODUCT_TITLES = By.xpath("//span[@class='name']");
    private static final By SEARCH_INPUT = By.xpath("//input[@type='search' and @placeholder='Поиск в М.Видео']");
    private static final By SEARCH_BUTTON = By.xpath("//button[@class = 'mui-button main-search__submit main-search__submit--desktop']");
    private static final By PRICE_TITLES = By.xpath("//span[@class='current-price']");
    private static final String SUBCATEGORY_TEMPLATE = "//span[contains(@class, 'last-level-category__name') and text()='%s']";
//    private static final By PRICE = By.xpath("//button[contains(@class, 'mui-chip')][contains(text(), 'Цена')]");
    private static final By PRICE = By.xpath("//button[contains(@class, 'mui-chip')][.//span[contains(@class, 'chip-label') and text()='Цена']]");

    private static final By READY = By.xpath("//button[contains(@class, 'mui-button button') and contains(text(), 'Готово')]");
    private static final By PRICE_MIN_INPUT = By.xpath("//label[contains(text(), 'От')]/preceding-sibling::input");
    private static final By PRICE_MAX_INPUT = By.xpath("//label[contains(text(), 'До')]/preceding-sibling::input");
//    private static final By BRAND_SECTION_HEADER = By.xpath("//button[contains(@class, 'mui-chip')][contains(text(), 'Бренд')]");
    private static final By BRAND_SECTION_HEADER = By.xpath("//button[contains(@class, 'mui-chip')][.//span[contains(@class, 'chip-label') and text()='Бренд']]");

    private static final By SHOW_ALL_BRANDS_BUTTON = By.xpath("//button[contains(@class, 'mui-button show-all-link')][contains(text(), 'Показать всё')]");
    private static final By BRAND_SEARCH_INPUT = By.xpath("//input[@placeholder='Поиск по списку']");
    private static final By SHOW_MORE_BUTTON = By.xpath("//button[@class='mui-button load-button' and contains(text(), 'Показать ещё')]");
    private static final String BRAND_CHECKBOX_TEMPLATE =
            "(//mui-checkbox[.//span[contains(@class, 'checkbox__content')]" +
                    "[translate(normalize-space(text()), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '%s']]" +
                    "//label[@class='mui-checkbox'])";

    public MvideoPurchasesPage(WebDriver driver) {
        super(driver);
    }

    @Step("Открыть главную страницу Mvideo")
    public MvideoPurchasesPage open(String url) {
        log.info("Открытие страницы Mvideo: {}", url);
        driver.get(url);
        log.debug("URL загружен, ожидание появления значений страницы");
        waitActions.waitForElementVisible(CATALOG_BUTTON);

        popupHelper.smartClosePopups();
        log.info("Главная страница открыта: {}", url);

        // Повторно ждем кнопку каталога (могла перекрыться)
        waitActions.waitForElementClickable(CATALOG_BUTTON);

        log.info("Главная страница открыта: {}", url);
        return this;
    }


    @Step("Открыть каталог товаров")
    public MvideoPurchasesPage openCatalog() {
        log.info("Открытие каталога");
        click(CATALOG_BUTTON);
        log.info("Каталог открыт");
        return this;
    }

    @Step("Навести курсор на раздел 'Электроника'")
    public MvideoPurchasesPage hoverOverElectronics() {
        log.info("Наведение курсора на раздел 'Электроника'");
        hoverOver(ELECTRONICS_SECTION);

        // Ждем появления подменю
        waitActions.sleep(500); // Даем время на анимацию
        waitActions.waitForElementPresent(By.xpath("//a[contains(@href, 'catalog')]")); // Ждем появления любой категории

        log.debug("Курсор наведен, подменю появилось");
        return this;
    }

    /**
     * Кликнуть по подкатегории в меню
     *
     * @param subCategory название подкатегории (например, "Все ноутбуки")
     */
    @Step("Кликнуть по подкатегории '{subCategory}'")
    public MvideoPurchasesPage clickSubCategory(String subCategory) {
        log.info("Клик по подкатегории '{}'", subCategory);

        By subCategoryLink = By.xpath(String.format(SUBCATEGORY_TEMPLATE, subCategory));

        waitActions.waitForElementClickable(subCategoryLink);
        click(subCategoryLink);

        log.debug("Клик по подкатегории '{}' выполнен", subCategory);
        return this;
    }

    /**
     * Перейти в раздел "Все ноутбуки"
     */
    @Step("Перейти в раздел 'Все ноутбуки'")
    public MvideoPurchasesPage goToAllLaptops() {
        return clickSubCategory("Все ноутбуки");
    }


    @Step("Установить диапазон цен от {minPrice} до {maxPrice}")
    public MvideoPurchasesPage setParameterPrice(String minPrice, String maxPrice) {
        log.info("Установка диапазона цен: {} - {}", minPrice, maxPrice);

        // 1. Открываем фильтр цены
        shopFilter.openFilter(PRICE);

        // 2. Ввод минимальной цены
        shopFilter.typeInField(PRICE_MIN_INPUT, minPrice);

        // 3. Ввод максимальной цены
        shopFilter.typeInField(PRICE_MAX_INPUT, maxPrice);

        // 4. Применяем фильтр
        shopFilter.applyFilter(READY);
        log.info("Диапазон цен установлен");
        return this;
    }

    @Step("Выбрать производителей: {brands}")
    public List<String> setParameterBrand(List<String> brands, boolean strict) {
        log.info("Выбор производителей: {}", brands);

        // 1. Открываем фильтр брендов
        shopFilter.openFilter(BRAND_SECTION_HEADER);
        click(SHOW_ALL_BRANDS_BUTTON);
        waitActions.waitForElementVisible(BRAND_SEARCH_INPUT);


        FilterSelectionResult result = shopFilter.selectMultipleCheckboxes(
                BRAND_SEARCH_INPUT,
                BRAND_CHECKBOX_TEMPLATE,
                brands,
                strict
        );

        shopFilter.applyFilter(READY);
        shopFilter.attachSelectionResult(result);

        return result.getSelected();
    }

    @Step("Получить названия и цены товаров")
    public Map<String, List<String>> getProductNamesAndPrices() {
        Set<String> names = new LinkedHashSet<>();
        Set<String> prices = new LinkedHashSet<>();

        shopFilter.scrollAndCollect(names, prices, PRODUCT_TITLES, PRICE_TITLES, SHOW_MORE_BUTTON);

        List<String> namesList = new ArrayList<>(names);
        List<String> pricesList = new ArrayList<>(prices);

        shopFilter.attachProductsReport(namesList, pricesList);

        return Map.of("names", namesList, "prices", pricesList);
    }

    @Step("Получить название первого товара")
    public String getFirstProductName() {
        log.info("Получение названия первого товара");

        // Прокручиваем в начало
        scrollActions.scrollToTop();
        sleep(800);

        // Используем универсальный метод
        String firstName = elementActions.getFirstElementText(PRODUCT_TITLES);

        log.info("Первый товар: {}", firstName);
        return firstName;
    }

    @Step("Выполнить поиск товара: {productName}")
    public MvideoPurchasesPage searchProduct(String productName) {
        log.info("Выполнение поиска товара: {}", productName);

        // Используем универсальный метод
        searchActions.performSearchAndWait(
                SEARCH_INPUT,
                SEARCH_BUTTON,
                PRODUCT_TITLES,
                productName
        );

        // Проверяем, что поиск выполнился
        if (!searchActions.isSearchExecuted("text=")) {
            log.warn("Поиск мог не выполниться корректно");
        }

        log.info("Поиск '{}' выполнен успешно", productName);
        return this;
    }

    @Step("Проверить наличие товара в результатах поиска")
    public boolean verifySearchResultsContainProduct(String productName) {
        log.info("Проверка наличия товара '{}' в результатах поиска", productName);

        // Ждем появления хотя бы одного товара (быстро)
        try {
            waitActions.waitForElementPresent(PRODUCT_TITLES);
        } catch (TimeoutException e) {
            log.warn("❌ Товары не найдены на странице");
            return false;
        }

        // Ищем только среди видимых товаров (быстро)
        List<WebElement> results = driver.findElements(PRODUCT_TITLES);

        String searchTerm = productName.toLowerCase().trim();

        boolean found = results.stream()
                .filter(WebElement::isDisplayed)  // Только видимые
                .map(WebElement::getText)
                .filter(text -> text != null && !text.isEmpty())
                .anyMatch(text -> text.toLowerCase().contains(searchTerm));

        if (found) {
            log.info("✅ Товар '{}' найден", productName);
        } else {
            log.warn("❌ Товар '{}' не найден среди {} видимых товаров", productName, results.size());

            // Для отладки - только если нужно (можно закомментировать)
            if (log.isDebugEnabled()) {
                List<String> visibleTexts = results.stream()
                        .filter(WebElement::isDisplayed)
                        .map(WebElement::getText)
                        .filter(text -> !text.isEmpty())
                        .limit(5)
                        .collect(Collectors.toList());
                log.debug("Примеры товаров: {}", visibleTexts);
            }
        }

        return found;
    }
}







