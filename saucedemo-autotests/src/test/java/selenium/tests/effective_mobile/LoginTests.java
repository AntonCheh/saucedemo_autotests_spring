package selenium.tests.effective_mobile;

import selenium.providers.loginTests.CheckLoginTests;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import selenium.sources.loginTestEffectiveMobile.LoginTestsSource;


@Epic("Авторизация пользователя")
@Feature("Функциональность логина")
@DisplayName("Тесты авторизации на SauceDemo")
public class LoginTests extends CheckLoginTests {

    @TmsLink("https://www.saucedemo.com/")
    @DisplayName("Успешный логин с валидными данными")
    @ParameterizedTest(name = "{displayName}")
    @Severity(SeverityLevel.CRITICAL)
    @MethodSource("dataSuccessful")
    @Tag("Positive")
    @Tag("Smoke")
    public void successfulLoginTest(LoginTestsSource testsSource) {
        execute(testsSource);
    }

    @TmsLink("https://www.saucedemo.com/")
    @DisplayName("Логин с неверным паролем")
    @ParameterizedTest(name = "{displayName}")
    @Severity(SeverityLevel.CRITICAL)
    @MethodSource("dataWrongPassword")
    @Tag("Negative")
    @Tag("Smoke")
    public void wrongPasswordTest(LoginTestsSource testsSource) {
        executeLoginIncorrect(testsSource);
    }

    @TmsLink("https://www.saucedemo.com/")
    @DisplayName("Логин заблокированного пользователя")
    @ParameterizedTest(name = "{displayName}")
    @Severity(SeverityLevel.CRITICAL)
    @MethodSource("dataBlockedUser")
    @Tag("Negative")
    @Tag("Smoke")
    public void lockedOutUserTest(LoginTestsSource testsSource) {
        executeBlockedUser(testsSource);
    }

    @TmsLink("https://www.saucedemo.com/")
    @DisplayName("Логин с пустыми полями")
    @ParameterizedTest(name = "{displayName}")
    @Severity(SeverityLevel.CRITICAL)
    @MethodSource("dataEmpty")
    @Tag("Negative")
    @Tag("Smoke")
    public void emptyFieldsTest(LoginTestsSource testsSource) {
        executeEmpty(testsSource);
    }

    @TmsLink("https://www.saucedemo.com/")
    @DisplayName("Логин performance_glitch_user")
    @ParameterizedTest(name = "{displayName}")
    @Severity(SeverityLevel.CRITICAL)
    @MethodSource("dataWithTimeOut")
    @Tag("Positive")
    @Tag("Smoke")
    public void performanceGlitchUserTest(LoginTestsSource testsSource) {
        executePerformanceGlitch(testsSource);
    }

    @TmsLink("https://www.saucedemo.com/")
    @DisplayName("Логин performance_glitch_user")
    @ParameterizedTest(name = "{displayName}")
    @Severity(SeverityLevel.CRITICAL)
    @MethodSource("dataWithTimeOut")
    @Tag("Positive")
    @Tag("Smoke")
    public void performanceGlitchUserTest2(LoginTestsSource testsSource) {
        executePerformanceGlitch(testsSource);
    }
}


