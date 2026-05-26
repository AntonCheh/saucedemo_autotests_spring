package selenium.pages.yandex_purchases;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import selenium.base.BasePage;
import selenium.models.FilterSelectionResult;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class YandexPurchasesPage extends BasePage {

    private static final By CATALOG_BUTTON = By.xpath("//button[.//*[text()='Каталог']]");
    private static final By ELECTRONICS_SECTION = By.xpath("//a[contains(@href, 'electronics_dep') and not(@rel='nofollow')]");
    private static final By LAPTOPS_LINK = By.xpath("//a[text()='Ноутбуки' and contains(@href, 'catalog')]");
    private static final By PRICE_MIN_INPUT = By.xpath("//input[contains(@id, 'glprice') and contains(@id, '_min')]");
    private static final By PRICE_MAX_INPUT = By.xpath("//input[contains(@id, 'glprice') and contains(@id, '_max')]");
    private static final By PRODUCT_TITLES = By.xpath("//span[@data-auto='snippet-title' and contains(@class, 'ds-text_weight_med')]");
    private static final By SEARCH_INPUT = By.xpath("//input[@id='header-search']");
    private static final By SEARCH_BUTTON = By.xpath("//button[@data-auto='search-button']");
    private static final By BRAND_SECTION_HEADER = By.xpath("//span[text()='Бренд']");
    private static final By SHOW_ALL_BRANDS_BUTTON = By.xpath("//button[contains(., 'Показать всё') and @aria-expanded='false']");
    private static final By BRAND_SEARCH_INPUT = By.xpath("//input[@placeholder='Найти']");
    private static final String CATEGORY_DROPDOWN_TEMPLATE = "//a[text()='%s' and contains(@href, 'catalog')]";
    private static final By PRICE_TITLES = By.xpath("//span[@data-auto='snippet-title' and contains(@class, 'ds-text_weight_med')]     /ancestor::div[@data-zone-name='productSnippet']     //div[@data-zone-name='price']     //span[contains(@class, 'ds-text_color_price-term') and contains(@class, 'headline-5')]");

    public YandexPurchasesPage(WebDriver driver) {
        super(driver);
    }

    @Step("Открыть главную страницу Яндекс Маркет")
    public YandexPurchasesPage open(String url) {
        log.info("Открытие страницы яндекс маркета: {}", url);
        driver.get(url);
        log.debug("URL загружен, ожидание появления значений страницы");
        waitActions.waitForElementVisible(CATALOG_BUTTON);
        log.info("Главная страница открыта: {}", url);
        return this;
    }

    @Step("Открыть каталог товаров")
    public YandexPurchasesPage openCatalog() {
        log.info("Открытие каталога");
        click(CATALOG_BUTTON);
        log.info("Каталог открыт");
        return this;
    }

    @Step("Навести курсор на раздел 'Электроника'")
    public YandexPurchasesPage hoverOverElectronics() {
        log.info("Наведение курсора на раздел 'Электроника'");
        hoverOver(ELECTRONICS_SECTION);

        // Ждем появления подменю
        waitActions.sleep(500); // Даем время на анимацию
        waitActions.waitForElementPresent(By.xpath("//a[contains(@href, 'catalog')]")); // Ждем появления любой категории

        log.debug("Курсор наведен, подменю появилось");
        return this;
    }

    /**
     * Ждет появления подменю и кликает по нему
     */
    @Step("Кликнуть по разделу {typeOfElectronics} в выпадающем меню")
    public YandexPurchasesPage clickLaptopsInDropdown(String typeOfElectronics) {
        log.info("Клик по разделу '{}' в выпадающем меню", typeOfElectronics);

        By categoryLink = By.xpath(String.format(CATEGORY_DROPDOWN_TEMPLATE, typeOfElectronics));

        waitActions.waitForElementClickable(categoryLink);
        click(categoryLink);

        log.debug("Клик по разделу '{}' выполнен", typeOfElectronics);
        return this;
    }



    @Step("Установить диапазон цен от {minPrice} до {maxPrice}")
    public YandexPurchasesPage setParameterPrice(String minPrice, String maxPrice) {
        log.info("Установка диапазона цен: {} - {}", minPrice, maxPrice);

        waitActions.waitForElementVisible(PRICE_MIN_INPUT);

        // Ввод минимальной цены
        WebElement minInput = driver.findElement(PRICE_MIN_INPUT);
        minInput.clear();
        assertionHelper.typeWithDelay(minInput, minPrice, 30);  // Минимальная задержка

        // Ввод максимальной цены
        minInput.sendKeys(Keys.TAB);
        WebElement maxInput = driver.switchTo().activeElement();
        maxInput.clear();
        assertionHelper.typeWithDelay(maxInput, maxPrice, 30);  // Минимальная задержка

        // Применяем фильтр
        maxInput.sendKeys(Keys.ENTER);

        // Только одна проверка - ждем появления/обновления товаров
        try {
            wait.until(driver -> !driver.findElements(PRODUCT_TITLES).isEmpty());
        } catch (TimeoutException e) {
            // Товаров нет - ок
        }

        // Короткая пауза
        sleep(500);

        log.info("Диапазон цен установлен");
        return this;
    }

    /**
     * Посимвольный ввод текста с задержкой
     */
//    private void typeWithDelay(WebElement element, String text, int delayMs) {
//        for (char c : text.toCharArray()) {
//            element.sendKeys(String.valueOf(c));
//            sleep(delayMs);
//        }
//    }

    /**
     * Ждет применения фильтра цены
     */
    private void waitForPriceFilterApplied(String minPrice, String maxPrice) {
        log.debug("Ожидание применения фильтра цены...");

        // Способ 1: Ждем исчезновения лоадера
        waitForLoaderDisappear();

        // Способ 2: Ждем обновления URL (если цена добавляется в URL)
        try {
            wait.until(driver -> {
                String url = driver.getCurrentUrl();
                return url.contains("price") ||
                        url.contains("from=") ||
                        url.contains("to=") ||
                        url.contains("glprice");
            });
            log.debug("URL обновлен с параметрами цены");
        } catch (TimeoutException e) {
            log.debug("URL не содержит параметров цены, продолжаем...");
        }

        // Способ 3: Ждем, что в полях остались введенные значения
        wait.until(driver -> {
            WebElement minInput = driver.findElement(PRICE_MIN_INPUT);
            WebElement maxInput = driver.findElement(PRICE_MAX_INPUT);

            String actualMin = minInput.getAttribute("value");
            String actualMax = maxInput.getAttribute("value");

            return actualMin.equals(minPrice) && actualMax.equals(maxPrice);
        });

        // Способ 4: Ждем обновления товаров (если они были)
        try {
            pageLoadActions.waitForContentUpdate(PRODUCT_TITLES);
        } catch (Exception e) {
            // Если товаров не было - просто ждем появления
            waitForContentLoad(PRODUCT_TITLES);
        }

        // Дополнительная пауза для рендеринга
        sleep(1000);

        log.debug("Фильтр цены применен");
    }

    @Step("Выбрать производителей: {brands}")
    public List<String> setParameterBrand(List<String> brands, boolean strict) {
        log.info("Выбор производителей: {}", brands);

        // Открываем секцию брендов
        click(BRAND_SECTION_HEADER);
        waitActions.sleep(500);
        click(SHOW_ALL_BRANDS_BUTTON);
        waitActions.waitForElementVisible(BRAND_SEARCH_INPUT);

        // Используем регистронезависимый XPath через translate()
        String checkboxTemplate = "//label[.//span[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = translate('%s', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')]]";

        FilterSelectionResult result = filterActions.selectCheckboxesWithResult(
                BRAND_SEARCH_INPUT,
                checkboxTemplate,
                brands
        );

        // Логируем детальную информацию
        log.info(result.toString());

        // Прикрепляем к Allure отчету
        attachBrandSelectionResult(result);

        // Проверяем результат
        if (!result.hasSelected()) {
            String errorMsg = String.format("Не удалось выбрать ни один бренд из: %s", brands);
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        // В строгом режиме требуем ВСЕ бренды
        if (strict && result.hasNotFound()) {
            String errorMsg = String.format(
                    "❌ СТРОГИЙ РЕЖИМ: не все бренды выбраны!\n" +
                            "Ожидалось (%d): %s\n" +
                            "✅ Выбрано (%d): %s\n" +
                            "❌ Не найдено (%d): %s",
                    brands.size(), brands,
                    result.getSelected().size(), result.getSelected(),
                    result.getNotFound().size(), result.getNotFound()
            );
            log.error(errorMsg);
            Allure.addAttachment("Ошибка выбора брендов", "text/plain", errorMsg);
            throw new AssertionError(errorMsg);
        }

        // В нестрогом режиме просто логируем предупреждение
        if (!strict && result.hasNotFound()) {
            log.warn("⚠️ НЕСТРОГИЙ РЕЖИМ: не найдены бренды: {}", result.getNotFound());
            Allure.addAttachment("Пропущенные бренды", "text/plain",
                    "Не найдены: " + result.getNotFound());
        }

        log.info("Итог: {}", result.getSummary());
        return result.getSelected();
    }

    /**
     * Прикрепляет результаты выбора брендов к Allure отчету
     */
    @Attachment(value = "Результаты выбора брендов", type = "text/plain")
    private String attachBrandSelectionResult(FilterSelectionResult result) {
        return result.toString();
    }

    @Step("Получить список названий товаров")
    public List<String> getProductNames() {
        log.info("Получение списка названий товаров");

        Set<String> uniqueNames = new LinkedHashSet<>();
        int maxScrollAttempts = 50;

        for (int i = 0; i < maxScrollAttempts; i++) {
            List<WebElement> products = driver.findElements(PRODUCT_TITLES);

            for (WebElement product : products) {
                try {
                    String text = product.getText();
                    if (text != null && !text.isEmpty()) {
                        uniqueNames.add(text);
                    }
                } catch (StaleElementReferenceException e) {
                    log.debug("Пропущен устаревший элемент");
                }
            }

            if (isPageEndReached()) {
                log.info("Достигнут конец страницы");

                sleep(1500);
                List<WebElement> finalProducts = driver.findElements(PRODUCT_TITLES);
                for (WebElement product : finalProducts) {
                    try {
                        String text = product.getText();
                        if (text != null && !text.isEmpty()) {
                            uniqueNames.add(text);
                        }
                    } catch (StaleElementReferenceException e) {
                        // Игнорируем
                    }
                }
                break;
            }

            scrollDown();
            sleep(500);
        }

        List<String> result = new ArrayList<>(uniqueNames);
        log.info("Найдено товаров: {}", result.size());

        // ✅ Вывод ВСЕХ товаров в Allure
        attachAllProducts(result);

        return result;
    }

    @Step("Получить список названий и цен товаров")
    public Map<String, List<String>> getProductNamesAndPrices() {
        log.info("Получение списка названий и цен товаров");

        Set<String> uniqueNames = new LinkedHashSet<>();
        Set<String> uniquePrices = new LinkedHashSet<>();
        int maxScrollAttempts = 50;

        for (int i = 0; i < maxScrollAttempts; i++) {
            // Собираем названия
            List<WebElement> products = driver.findElements(PRODUCT_TITLES);
            for (WebElement product : products) {
                try {
                    String text = product.getText();
                    if (text != null && !text.isEmpty()) {
                        uniqueNames.add(text);
                    }
                } catch (StaleElementReferenceException e) {
                    log.debug("Пропущен устаревший элемент названия");
                }
            }

            // Собираем цены
            List<WebElement> prices = driver.findElements(PRICE_TITLES);
            for (WebElement price : prices) {
                try {
                    String text = price.getText();
                    if (text != null && !text.isEmpty()) {
                        String cleanPrice = text.replaceAll("[^\\d]", "");
                        if (!cleanPrice.isEmpty()) {
                            uniquePrices.add(cleanPrice);
                        }
                    }
                } catch (StaleElementReferenceException e) {
                    log.debug("Пропущен устаревший элемент цены");
                }
            }

            log.debug("Прокрутка {}: названий={}, цен={}", i + 1, uniqueNames.size(), uniquePrices.size());

            if (isPageEndReached()) {
                log.info("Достигнут конец страницы");

                sleep(1500);

                // Финальный сбор названий
                List<WebElement> finalProducts = driver.findElements(PRODUCT_TITLES);
                for (WebElement product : finalProducts) {
                    try {
                        String text = product.getText();
                        if (text != null && !text.isEmpty()) {
                            uniqueNames.add(text);
                        }
                    } catch (StaleElementReferenceException e) {
                        // Игнорируем
                    }
                }

                // Финальный сбор цен
                List<WebElement> finalPrices = driver.findElements(PRICE_TITLES);
                for (WebElement price : finalPrices) {
                    try {
                        String text = price.getText();
                        if (text != null && !text.isEmpty()) {
                            String cleanPrice = text.replaceAll("[^\\d]", "");
                            if (!cleanPrice.isEmpty()) {
                                uniquePrices.add(cleanPrice);
                            }
                        }
                    } catch (StaleElementReferenceException e) {
                        // Игнорируем
                    }
                }
                break;
            }

            scrollDown();
            sleep(500);
        }

        List<String> namesResult = new ArrayList<>(uniqueNames);
        List<String> pricesResult = new ArrayList<>(uniquePrices);

        log.info("Найдено: названий={}, цен={}", namesResult.size(), pricesResult.size());

        // Формируем результат
        Map<String, List<String>> result = new LinkedHashMap<>();
        result.put("names", namesResult);
        result.put("prices", pricesResult);

        // Вывод в Allure
        attachAllProductsAndPrices(namesResult, pricesResult);

        return result;
    }

    /**
     * Прикрепляет названия и цены к Allure отчету
     */
    @Attachment(value = "📋 Полный список товаров и цен", type = "text/plain")
    private String attachAllProductsAndPrices(List<String> names, List<String> prices) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ПОЛНЫЙ СПИСОК ТОВАРОВ И ЦЕН ===\n");
        sb.append(String.format("Всего найдено: %d товаров, %d цен\n", names.size(), prices.size()));
        sb.append("=====================================\n\n");

        if (names.isEmpty() && prices.isEmpty()) {
            sb.append("❌ ТОВАРЫ НЕ НАЙДЕНЫ!\n");
        } else {
            int limit = Math.min(names.size(), prices.size());

            // Выводим попарно
            for (int i = 0; i < limit; i++) {
                sb.append(String.format("%d. %s\n", i + 1, names.get(i)));
                sb.append(String.format("   💰 %s ₽\n\n", prices.get(i)));
            }

            // Если названий больше, чем цен
            if (names.size() > limit) {
                sb.append("⚠️ ТОВАРЫ БЕЗ ЦЕН:\n");
                for (int i = limit; i < names.size(); i++) {
                    sb.append(String.format("%d. %s\n", i + 1, names.get(i)));
                }
            }

            // Если цен больше, чем названий
            if (prices.size() > limit) {
                sb.append("⚠️ ЦЕНЫ БЕЗ ТОВАРОВ:\n");
                for (int i = limit; i < prices.size(); i++) {
                    sb.append(String.format("%d. %s ₽\n", i + 1, prices.get(i)));
                }
            }
        }

        sb.append("\n=====================================\n");
        sb.append(String.format("ИТОГО: %d товаров, %d цен\n", names.size(), prices.size()));

        return sb.toString();
    }

    /**
     * Прикрепляет ВСЕ товары к Allure отчету
     */
    @Attachment(value = "📋 Полный список товаров", type = "text/plain")
    private String attachAllProducts(List<String> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ПОЛНЫЙ СПИСОК ТОВАРОВ ===\n");
        sb.append(String.format("Всего найдено: %d товаров\n", products.size()));
        sb.append("===============================\n\n");

        if (products.isEmpty()) {
            sb.append("❌ ТОВАРЫ НЕ НАЙДЕНЫ!\n");
        } else {
            for (int i = 0; i < products.size(); i++) {
                sb.append(String.format("%d. %s\n", i + 1, products.get(i)));
            }
        }

        sb.append("\n===============================\n");
        sb.append(String.format("ИТОГО: %d товаров\n", products.size()));

        return sb.toString();
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
    public YandexPurchasesPage searchProduct(String productName) {
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





//    private final WebDriver driver;
//    private final WebDriverWait wait;
//    private final Actions actions;  // Добавляем Actions для наведения
//    private final TestConfig config;

//    @FindBy(xpath = "//button[.//*[text()='Каталог']]")
//    private WebElement catalog;
//
//    @FindBy(xpath = "//a[contains(@href, 'electronics_dep') and not(@rel='nofollow')]")
//    private WebElement electronics;
//
//    @FindBy(xpath = "//a[text()='Ноутбуки' and contains(@href, 'catalog')]")
//    private WebElement laptops;
//
//    @FindBy(xpath = "//input[@id='range-filter-field-glprice_25563_min']")
//    private WebElement priceMin;
//
//    @FindBy(xpath = "//input[@id='range-filter-field-glprice_25563_max']")
//    private WebElement priceMax;
//
//    @FindBy(xpath = "//button[contains(., 'Показать всё') and @aria-expanded='false']")
//    private WebElement showAll;
//
//    @FindBy(xpath = "//input[@placeholder='Найти']")
//    private WebElement searchInsideBrand;
//
//    @FindBy(xpath = "//label[.//span[text()='HP']]")
//    private WebElement putBrandHP;
//
//    @FindBy(xpath = "//label[.//span[text()='Lenovo']]")
//    private WebElement putBrandLenovo;
//
//
//
//    protected YandexPurchasesPage(WebDriver driver) {
//        super(driver);
//    }


    // Конструктор принимает WebDriver и TestConfig
//    public YandexPurchasesPage(WebDriver driver, TestConfig config) {
//        log.info("Инициализация страницы Яндекс Маркета");
//        this.driver = driver;
//        this.config = config;  // Сохраняем конфиг
//        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//        this.actions = new Actions(driver);
//        PageFactory.initElements(driver, this);
//        log.debug("Элементы страницы инициализированы");
//    }

//    @Step("Открытие страницы яндекс маркета")
//    public YandexPurchasesPage open() {
//
//        String yandexUrl = config.yandexUrl();
//
//        log.info("Открытие страницы яндекс маркета: {}", yandexUrl);
//        long startTime = System.currentTimeMillis();
//
//        driver.get(yandexUrl);
//        log.debug("URL загружен, ожидание появления значений страницы");
//
//        try {
//            wait.until(ExpectedConditions.visibilityOf(catalog));
//            long endTime = System.currentTimeMillis();
//            log.info("Стартовая страница успешно открыта за {} мс", (endTime - startTime));
//            log.info("Текущий URL: {}", driver.getCurrentUrl());
//            log.info("Заголовок страницы: {}", driver.getTitle());
//        } catch (Exception e) {
//            log.error("Ошибка при открытии страницы", e);
//            throw new RuntimeException("Не удалось открыть страницу: " + e.getMessage(), e);
//        }
//
//        return this;
//    }

//
//
//    @Step("Открывает каталог (нажимает на кнопку \"Каталог\")")
//    public YandexPurchasesPage openCatalog() {
//        log.info("Открытие каталога");
//        try {
//            wait.until(ExpectedConditions.elementToBeClickable(catalog));
//            catalog.click();
//            log.info("Каталог открыт");
//        } catch (Exception e) {
//            log.error("Не удалось открыть каталог", e);
//            throw new RuntimeException("Не удалось открыть каталог", e);
//        }
//        return this;
//    }
//
//
//    @Step("Наводим курсор на раздел \"Электроника\"")
//    public YandexPurchasesPage hoverOverElectronics() {
//        log.info("Наведение курсора на раздел 'Электроника'");
//        try {
//            wait.until(ExpectedConditions.visibilityOf(electronics));
//            actions.moveToElement(electronics).perform();
//            log.info("Курсор наведен на 'Электроника'");
//
//            // Умное ожидание: ждем, пока элемент "Ноутбуки" станет кликабельным
//            wait.until(ExpectedConditions.elementToBeClickable(laptops));
//
//        } catch (Exception e) {
//            log.error("Не удалось навести курсор на 'Электроника'", e);
//            throw new RuntimeException("Не удалось навести курсор на 'Электроника'", e);
//        }
//        return this;
//    }
//
//
//    @Step("Переходим в раздел 'Ноутбуки' и проверяем, что находимся в нем")
//    public YandexPurchasesPage goToLaptops() {
//        log.info("Переход в раздел 'Ноутбуки'");
//        try {
//            wait.until(ExpectedConditions.elementToBeClickable(laptops));
//            laptops.click();
//            log.info("Переход в раздел 'Ноутбуки' выполнен");
//            log.info("Заголовок страницы ноутбуки: {}", driver.getTitle());
//        } catch (Exception e) {
//            log.error("Не удалось перейти в раздел 'Ноутбуки'", e);
//            throw new RuntimeException("Не удалось перейти в раздел 'Ноутбуки'", e);
//        }
//        return this;
//    }
//
//    @Step("Задаём параметр «Цена, Р» от {priceMinimal} до {priceMax} рублей.")
//    public YandexPurchasesPage setParameterPrice(String priceMinimal, String priceMax) {
//        log.info("Задаём параметр «Цена, Р» от {} до {} рублей", priceMinimal, priceMax);
//
//        try {
//            // Динамический поиск полей с умным ожиданием
//            WebElement minPriceField = wait.until(ExpectedConditions.presenceOfElementLocated(
//                    By.xpath("//input[contains(@id, 'glprice') and contains(@id, '_min')]")
//            ));
//
//            WebElement maxPriceField = wait.until(ExpectedConditions.presenceOfElementLocated(
//                    By.xpath("//input[contains(@id, 'glprice') and contains(@id, '_max')]")
//            ));
//
//            // Устанавливаем минимальную цену
//            minPriceField.clear();
//            minPriceField.sendKeys(priceMinimal);
//            log.debug("Установлена минимальная цена: {}", priceMinimal);
//
//            // Устанавливаем максимальную цену
//            maxPriceField.clear();
//            maxPriceField.sendKeys(priceMax);
//            log.debug("Установлена максимальная цена: {}", priceMax);
//
//            // Применяем фильтр
//            maxPriceField.sendKeys(Keys.ENTER);
//
//
//            log.info("Параметры цены успешно установлены, страница обновилась");
//
//        } catch (Exception e) {
//            log.error("Не удалось установить параметры цены: минимальная = {}, максимальная = {}",
//                    priceMinimal, priceMax, e);
//            throw new RuntimeException("Не удалось установить параметры цены", e);
//        }
//        return this;
//    }
//
//    /**
//     * Метод для ввода текста с настраиваемой задержкой
//     */
//    private void sendKeysWithDelay(WebElement element, String text, int delayBetweenChars) {
//        element.clear();
//        element.click();
//
//        for (int i = 0; i < text.length(); i++) {
//            String character = String.valueOf(text.charAt(i));
//            element.sendKeys(character);
//
//            try {
//                Thread.sleep(delayBetweenChars);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                break;
//            }
//
//            // Логируем каждый введённый символ (для отладки)
//            log.trace("Введён символ: '{}'", character);
//        }
//    }
//
//    @Step("Выбрать производителей: {producers}")
//    public List<String> setParameterBrand(List<String> producers) {
//        log.info("Задаём параметры производителей: {}", producers);
//
//        List<String> foundBrands = new ArrayList<>();    // Список найденных брендов
//        List<String> notFoundBrands = new ArrayList<>(); // Список ненайденных брендов
//
//        try {
//            // Открываем раздел "Бренд"
//            WebElement brandHeader = wait.until(ExpectedConditions.elementToBeClickable(
//                    By.xpath("//span[text()='Бренд']")
//            ));
//            brandHeader.click();
//            log.debug("Раздел 'Бренд' открыт");
//            Thread.sleep(500);
//
//            // Нажимаем "Показать всё"
//            WebElement showAllButton = wait.until(ExpectedConditions.elementToBeClickable(
//                    By.xpath("//button[contains(., 'Показать всё') and @aria-expanded='false']")
//            ));
//            showAllButton.click();
//            log.debug("Кнопка 'Показать всё' нажата");
//
//            // Ждём появления поля поиска
//            wait.until(ExpectedConditions.visibilityOfElementLocated(
//                    By.xpath("//input[@placeholder='Найти']")
//            ));
//            Thread.sleep(1000);
//
//            // Для каждого производителя
//            for (String producer : producers) {
//                try {
//                    // Находим поле поиска
//                    WebElement searchField = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                            By.xpath("//input[@placeholder='Найти']")
//                    ));
//
//                    // Вводим текст с задержкой
//                    sendKeysWithDelay(searchField, producer, 100);
//                    log.debug("Введён текст: {}", producer);
//
//                    Thread.sleep(1000);
//
//                    // Ищем и кликаем по бренду
//                    By brandSelector = By.xpath(String.format("//label[.//span[text()='%s']]", producer));
//                    WebElement brandElement = wait.until(ExpectedConditions.elementToBeClickable(brandSelector));
//
//                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", brandElement);
//                    Thread.sleep(500);
//                    brandElement.click();
//                    log.debug("Выбран бренд: {}", producer);
//
//                    foundBrands.add(producer); // Добавляем в список найденных
//                    Thread.sleep(1000);
//
//                } catch (Exception e) {
//                    log.error("Не удалось выбрать бренд: {}", producer, e);
//                    notFoundBrands.add(producer);
//                }
//            }
//
//            // Логируем результат
//            log.info("Найденные бренды: {}", foundBrands);
//            if (!notFoundBrands.isEmpty()) {
//                log.warn("Ненайденные бренды: {}", notFoundBrands);
//                attachNotFoundBrandsToReport(notFoundBrands);
//            }
//
//            if (foundBrands.isEmpty()) {
//                throw new RuntimeException("Не выбран ни один бренд из списка: " + producers);
//            }
//
//            log.info("Выбрано {} из {} брендов: {}", foundBrands.size(), producers.size(), foundBrands);
//
//            return foundBrands; // Возвращаем только реально выбранные бренды
//
//        } catch (Exception e) {
//            log.error("Критическая ошибка при выборе производителей: {}", producers, e);
//            throw new RuntimeException("Не удалось установить параметры производителей", e);
//        }
//    }
//
//
//    @Step("Получаем список всех товаров на странице и проверяем, что их больше 12")
//    public List<String> getProductNames() {
//        log.info("Получение списка названий товаров");
//
//        Set<String> uniqueProductNames = new LinkedHashSet<>();
//        int previousSize = 0;
//        int maxScrollAttempts = 100; // Увеличил для гарантии
//        int sameScrollCount = 0;
//        int lastScrollPosition = 0;
//
//        try {
//            for (int scrollAttempt = 0; scrollAttempt < maxScrollAttempts; scrollAttempt++) {
//                // Получаем текущие товары
//                List<WebElement> currentProducts = driver.findElements(
//                        By.xpath("//span[@data-auto='snippet-title' and contains(@class, 'ds-text_weight_med')]")
//                );
//
//                // Добавляем новые товары
//                for (WebElement product : currentProducts) {
//                    String productText = product.getText();
//                    if (!productText.isEmpty()) {
//                        uniqueProductNames.add(productText);
//                    }
//                }
//
//                log.debug("Попытка {}. Найдено уникальных товаров: {}", scrollAttempt + 1, uniqueProductNames.size());
//
//                // Получаем текущую позицию прокрутки
//                Number scrollPositionNum = (Number) ((JavascriptExecutor) driver)
//                        .executeScript("return window.pageYOffset + window.innerHeight;");
//                int currentScrollPosition = scrollPositionNum.intValue();
//
//                // Получаем общую высоту страницы
//                Number scrollHeightNum = (Number) ((JavascriptExecutor) driver)
//                        .executeScript("return document.body.scrollHeight;");
//                int totalScrollHeight = scrollHeightNum.intValue();
//
//                log.debug("Позиция прокрутки: {}, Высота страницы: {}", currentScrollPosition, totalScrollHeight);
//
//                // Проверяем, достигли ли конца страницы
//                if (currentScrollPosition >= totalScrollHeight - 100) {
//                    log.info("Достигнут конец страницы на попытке {}", scrollAttempt + 1);
//
//                    // Последний сбор товаров после достижения конца
//                    Thread.sleep(2000);
//                    List<WebElement> finalCheck = driver.findElements(
//                            By.xpath("//span[@data-auto='snippet-title' and contains(@class, 'ds-text_weight_med')]")
//                    );
//                    for (WebElement product : finalCheck) {
//                        String productText = product.getText();
//                        if (!productText.isEmpty()) {
//                            uniqueProductNames.add(productText);
//                        }
//                    }
//                    break;
//                }
//
//                // Проверяем, изменилась ли позиция прокрутки
//                if (currentScrollPosition == lastScrollPosition && scrollAttempt > 5) {
//                    sameScrollCount++;
//                    if (sameScrollCount >= 3) {
//                        log.info("Позиция прокрутки не меняется. Достигнут конец страницы.");
//                        break;
//                    }
//                } else {
//                    sameScrollCount = 0;
//                }
//                lastScrollPosition = currentScrollPosition;
//
//                // Если новые товары не добавляются, всё равно продолжаем прокрутку
//                if (uniqueProductNames.size() == previousSize) {
//                    log.debug("Новые товары не добавлены, но продолжаем прокрутку (попытка {})", scrollAttempt + 1);
//                    // Не выходим, а продолжаем прокрутку
//                }
//
//                previousSize = uniqueProductNames.size();
//
//                // Прокручиваем страницу вниз
//                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 800);");
//
//                // Гибкое ожидание загрузки новых элементов
//                waitForNewContent(By.xpath("//span[@data-auto='snippet-title' and contains(@class, 'ds-text_weight_med')]"), previousSize);
//
//                // Небольшая задержка для стабильности
//                Thread.sleep(500);
//            }
//
//            // Получаем финальный список
//            List<String> productNames = new ArrayList<>(uniqueProductNames);
//            log.info("Получено {} названий товаров", productNames.size());
//            log.info("=== СПИСОК ТОВАРОВ ===\n{}",
//                    IntStream.range(0, productNames.size())
//                            .mapToObj(i -> String.format("%d. %s", i + 1, productNames.get(i)))
//                            .collect(Collectors.joining("\n"))
//            );
//            log.info("=====================");
//
//            if (productNames.size() <= 12) {
//                log.warn("Найдено только {} товаров, ожидалось больше 12", productNames.size());
//            }
//
//
//            return productNames;
//
//        } catch (Exception e) {
//            log.error("Не удалось получить список товаров", e);
//            return new ArrayList<>();
//        }
//    }
//
//    @Step("Получить название первого товара в списке")
//    public String getFirstProductName() {
//        log.info("Получение названия первого товара");
//
//        try {
//            // Прокручиваем страницу в начало
//            log.debug("Прокрутка страницы в начало");
//            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
//            Thread.sleep(1000); // Ждём прокрутки
//
//            // Ждём появления элементов
//            wait.until(ExpectedConditions.presenceOfElementLocated(
//                    By.xpath("//span[@data-auto='snippet-title' and contains(@class, 'ds-text_weight_med')]")
//            ));
//
//            // Получаем все элементы
//            List<WebElement> productElements = driver.findElements(
//                    By.xpath("//span[@data-auto='snippet-title' and contains(@class, 'ds-text_weight_med')]")
//            );
//
//            if (productElements.isEmpty()) {
//                log.error("Не найдено ни одного товара на странице");
//                throw new RuntimeException("Список товаров пуст");
//            }
//
//            // Берём первый элемент
//            WebElement firstProduct = productElements.get(0);
//            String firstProductName = firstProduct.getText();
//
//            // Прокручиваем к первому элементу для уверенности
//            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", firstProduct);
//            Thread.sleep(500);
//
//            log.info("Первый товар: {}", firstProductName);
//            return firstProductName;
//
//        } catch (Exception e) {
//            log.error("Не удалось получить первый товар", e);
//            throw new RuntimeException("Не удалось получить первый товар", e);
//        }
//    }
//
//    @Step("Поиск товара: {productName}")
//    public YandexPurchasesPage searchProduct(String productName) {
//        log.info("Поиск товара: {}", productName);
//
//        try {
//            // 1. Находим поле поиска
//            WebElement searchField = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                    By.xpath("//input[@id='header-search']")
//            ));
//
//            // 2. Очищаем поле
//            searchField.clear();
//            log.debug("Поле поиска очищено");
//
//            // 3. Вводим текст с задержкой (опционально, для имитации реального ввода)
//            sendKeysWithDelay(searchField, productName, 50);
//            log.debug("Введён текст в поисковую строку: {}", productName);
//
//            // 4. Ждём появления кнопки "Найти" и проверяем, что она стала кликабельной
//            WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
//                    By.xpath("//button[@data-auto='search-button']")
//            ));
//            log.debug("Кнопка 'Найти' появилась и доступна для нажатия");
//
//            // 5. Небольшая пауза для стабильности
//            Thread.sleep(500);
//
//            // 6. Нажимаем кнопку "Найти"
//            searchButton.click();
//            log.info("Кнопка 'Найти' нажата");
//
//            // 7. Ждём загрузки результатов поиска
//            waitForSearchResults();
//
//            log.info("Поиск товара '{}' выполнен успешно", productName);
//
//        } catch (Exception e) {
//            log.error("Не удалось выполнить поиск товара: {}", productName, e);
//            throw new RuntimeException("Не удалось выполнить поиск товара: " + productName, e);
//        }
//        return this;
//    }
//
//    /**
//     * Ожидание загрузки результатов поиска
//     */
//    private void waitForSearchResults() {
//        try {
//            // Ждём изменения URL (появление параметров поиска)
//            wait.until(ExpectedConditions.urlContains("text="));
//            log.debug("URL обновлён с параметрами поиска");
//
//            // Ждём появления результатов
//            wait.until(ExpectedConditions.presenceOfElementLocated(
//                    By.xpath("//div[contains(@class, 'search-results') or contains(@class, 'catalog-content') or contains(@data-apiary-widget-name, 'SearchSerp')]")
//            ));
//
//            // Ждём, пока исчезнет индикатор загрузки
//            try {
//                wait.until(ExpectedConditions.invisibilityOfElementLocated(
//                        By.xpath("//div[contains(@class, 'spin') or contains(@class, 'loader')]")
//                ));
//                log.debug("Индикатор загрузки исчез");
//            } catch (TimeoutException e) {
//                log.debug("Индикатор загрузки не найден");
//            }
//
//            // Дополнительная пауза для стабильности
//            Thread.sleep(1000);
//
//        } catch (Exception e) {
//            log.warn("Ошибка при ожидании результатов поиска", e);
//        }
//    }
//
//    @Step("Проверить, что результаты поиска содержат искомый товар")
//    public void verifySearchResultsContainProduct(String expectedProductName) {
//        log.info("Проверка результатов поиска на наличие товара: {}", expectedProductName);
//
//        List<WebElement> searchResults = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
//                By.xpath("//span[@data-auto='snippet-title' and contains(@class, 'ds-text_weight_med')]")
//        ));
//
//        boolean found = searchResults.stream()
//                .anyMatch(result -> result.getText().toLowerCase().contains(expectedProductName.toLowerCase()));
//
//        if (found) {
//            log.info("✅ Искомый товар '{}' найден в результатах поиска", expectedProductName);
//        } else {
//            log.error("❌ Искомый товар '{}' не найден в результатах поиска", expectedProductName);
//            throw new RuntimeException("Товар не найден в результатах поиска: " + expectedProductName);
//        }
//    }
//
//    /**
//     * Гибкое ожидание появления новых элементов после прокрутки
//     *
//     * @param locator       локатор элементов
//     * @param previousCount предыдущее количество элементов
//     */
//    private void waitForNewContent(By locator, int previousCount) {
//        try {
//            // Ждём увеличения количества элементов или таймаут 3 секунды
//            wait.until(webDriver -> {
//                int currentCount = driver.findElements(locator).size();
//                return currentCount > previousCount;
//            });
//            log.debug("Новые элементы загружены");
//        } catch (TimeoutException e) {
//            log.debug("Новые элементы не появились за ожидаемое время, продолжаем прокрутку");
//        }
//    }
//
//    /**
//     * Добавляет информацию о ненайденных брендах в Allure отчёт
//     */
//    @Attachment(value = "Ненайденные бренды", type = "text/plain")
//    private String attachNotFoundBrandsToReport(List<String> notFoundBrands) {
//        return "Следующие бренды не были найдены:\n" + String.join("\n", notFoundBrands);
//    }


