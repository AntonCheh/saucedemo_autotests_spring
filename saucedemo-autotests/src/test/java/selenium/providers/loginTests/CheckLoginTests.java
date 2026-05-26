package selenium.providers.loginTests;

import selenium.assertions.AssertionsWeb;

import selenium.constants.Messages;
import selenium.helpers.BaseTest;
import selenium.pages.effective_mobile.LoginPage;
import selenium.sources.loginTestEffectiveMobile.DataForLoginTests;
import selenium.sources.loginTestEffectiveMobile.LoginTestsSource;


public class CheckLoginTests extends BaseTest implements DataForLoginTests {

    private final AssertionsWeb assertionsWeb;


    public CheckLoginTests(AssertionsWeb assertionsWeb) {
        this.assertionsWeb = assertionsWeb;
    }

    public CheckLoginTests() {
        this(new AssertionsWeb());
    }

    public void commonMethod (LoginTestsSource testsSource) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(testsSource.login(), testsSource.password());
    }

    /**
     * 1. Успешный логин
     */
    public void execute(LoginTestsSource testsSource) {
        commonMethod(testsSource);
        assertionsWeb.assertLoginCorrect(driver, testsSource.login(), testsSource.password());
    }

    /**
     * 2. Логин с неверным паролем
     */
    public void executeLoginIncorrect(LoginTestsSource testsSource) {
        commonMethod(testsSource);
        assertionsWeb.assertLoginWrong(driver, Messages.WRONG_PASSWORD);
    }

    /**
     * 3. Логин заблокированного пользователя
     */
    public void executeBlockedUser(LoginTestsSource testsSource) {
        commonMethod(testsSource);
        assertionsWeb.assertBlockedUser(driver, testsSource.login(), Messages.LOCKED_OUT_USER);
    }

    /**
     * 4. Логин с пустыми полями
     */
    public void executeEmpty(LoginTestsSource testsSource) {
        commonMethod(testsSource);
        assertionsWeb.assertEmptyStrings(driver, Messages.USERNAME_REQUIRED);
    }

    /**
     * 5. Логин performance_glitch_user (с замером времени)
     */
    public long executePerformanceGlitch(LoginTestsSource testsSource) {
        LoginPage loginPage = new LoginPage(driver);

        long startTime = System.currentTimeMillis();
        loginPage.login(testsSource.login(), testsSource.password());
        long endTime = System.currentTimeMillis();

        assertionsWeb.assertLoginCorrect(driver, testsSource.login(), testsSource.password());
        return endTime - startTime;
    }
}
