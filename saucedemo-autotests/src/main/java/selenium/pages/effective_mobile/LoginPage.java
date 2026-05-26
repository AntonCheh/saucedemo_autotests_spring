package selenium.pages.effective_mobile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class LoginPage {
    private static final Logger logger = LoggerFactory.getLogger(LoginPage.class);
    private static final String LOGIN_PAGE_URL = "https://www.saucedemo.com/";

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(xpath = "//input[@data-test='username']")
    private WebElement usernameField;

    @FindBy(xpath = "//input[@data-test='priceMax']")
    private WebElement passwordField;

    @FindBy(xpath = "//input[@data-test='login-button']")
    private WebElement loginButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    public LoginPage(WebDriver driver) {
        logger.info("Инициализация страницы логина");
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
        logger.debug("Элементы страницы логина инициализированы");
    }

    public LoginPage open() {
        logger.info("Открытие страницы логина: {}", LOGIN_PAGE_URL);
        long startTime = System.currentTimeMillis();

        driver.get(LOGIN_PAGE_URL);
        logger.debug("URL загружен, ожидание появления поля username");

        try {
            wait.until(ExpectedConditions.visibilityOf(usernameField));
            long endTime = System.currentTimeMillis();
            logger.info("Страница логина успешно открыта за {} мс", (endTime - startTime));
            logger.info("Текущий URL: {}", driver.getCurrentUrl());
            logger.info("Заголовок страницы: {}", driver.getTitle());
        } catch (Exception e) {
            logger.error("Ошибка при открытии страницы логина", e);
            throw new RuntimeException("Не удалось открыть страницу логина: " + e.getMessage(), e);
        }

        return this;
    }

    public LoginPage enterUsername(String username) {
        logger.info("Ввод username: {}", username);
        try {
            usernameField.clear();
            logger.debug("Поле username очищено");
            usernameField.sendKeys(username);
            logger.debug("Username '{}' введен успешно", username);
        } catch (Exception e) {
            logger.error("Ошибка при вводе username '{}'", username, e);
            throw new RuntimeException("Не удалось ввести username: " + e.getMessage(), e);
        }
        return this;
    }

    public LoginPage enterPassword(String password) {
        logger.info("Ввод пароля (длина: {} символов)", password != null ? password.length() : 0);
        try {
            passwordField.clear();
            logger.debug("Поле пароля очищено");
            passwordField.sendKeys(password);
            logger.debug("Пароль введен успешно");
        } catch (Exception e) {
            logger.error("Ошибка при вводе пароля", e);
            throw new RuntimeException("Не удалось ввести пароль: " + e.getMessage(), e);
        }
        return this;
    }

    public void clickLogin() {
        logger.info("Нажатие кнопки Login");
        try {
            String buttonText = loginButton.getAttribute("value");
            logger.debug("Текст кнопки: {}", buttonText);

            loginButton.click();
            logger.info("Кнопка Login нажата");
            logger.debug("После нажатия кнопки, текущий URL: {}", driver.getCurrentUrl());
        } catch (Exception e) {
            logger.error("Ошибка при нажатии кнопки Login", e);
            throw new RuntimeException("Не удалось нажать кнопку Login: " + e.getMessage(), e);
        }
    }

    // Основной метод для логина
    public void login(String username, String password) {
        logger.info("=== Начало процедуры логина ===");
        logger.info("Логин: {}, Пароль: {}", username, password != null ? "***" : "null");
        long startTime = System.currentTimeMillis();

        try {
            open()
                    .enterUsername(username)
                    .enterPassword(password)
                    .clickLogin();

            long endTime = System.currentTimeMillis();
            logger.info("Процедура логина завершена за {} мс", (endTime - startTime));
            logger.info("Текущий URL после логина: {}", driver.getCurrentUrl());
        } catch (Exception e) {
            logger.error("Ошибка во время процедуры логина", e);
            throw e;
        }
    }

    // Методы для проверок
    public boolean isErrorMessageDisplayed() {
        logger.debug("Проверка отображения сообщения об ошибке");
        try {
            boolean isDisplayed = wait.until(ExpectedConditions.visibilityOf(errorMessage)).isDisplayed();
            logger.debug("Сообщение об ошибке отображено: {}", isDisplayed);
            return isDisplayed;
        } catch (Exception e) {
            logger.debug("Сообщение об ошибке не найдено: {}", e.getMessage());
            return false;
        }
    }

    public String getErrorMessageText() {
        logger.debug("Получение текста сообщения об ошибке");
        try {
            String errorText = errorMessage.getText();
            logger.info("Текст ошибки: {}", errorText);
            return errorText;
        } catch (Exception e) {
            logger.warn("Не удалось получить текст ошибки: {}", e.getMessage());
            return "";
        }
    }

    public String getCurrentUrl() {
        String currentUrl = driver.getCurrentUrl();
        logger.debug("Текущий URL: {}", currentUrl);
        return currentUrl;
    }

    public String getPageTitle() {
        String title = driver.getTitle();
        logger.debug("Заголовок страницы: {}", title);
        return title;
    }
}