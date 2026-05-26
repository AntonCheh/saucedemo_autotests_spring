package selenide.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import selenide.providers.CheckMvideoSelenide;
import selenium.sources.yandexTest.PurchasesSource;

@Epic("Мвидео")
@Feature("Функциональность на сайте")
@DisplayName("Проверка поиска товаров на сайте")
public class TestMvideoSelenide extends CheckMvideoSelenide {

    @TmsLink("https://www.mvideo.ru/")
    @ParameterizedTest(name = "Проверка поиска mvideo")
    @MethodSource("dataMvideoPurchases")
    public void successSearchTest(PurchasesSource testsSource) {

        executeWithSoftAssertMvideo(testsSource);
    }
}


