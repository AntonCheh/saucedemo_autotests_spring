package selenium.tests.yandex;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import selenium.providers.yandex.CheckPurchaseTests;
import selenium.sources.yandexTest.PurchasesSource;


@Epic("Яндекс маркет")
@Feature("Функциональность на сайте")
@DisplayName("Проверка поиска товаров на сайте")
public class TestYandex extends CheckPurchaseTests {

    @TmsLink("https://market.yandex.ru/")
    @ParameterizedTest(name = "Проверка поиска Yandex")
    @MethodSource("dataYandexPurchases")
    public void successSearchLaptopsTest(PurchasesSource testsSource) {
        executeWithSoftAssert(testsSource);
    }


}


