package selenium.sources.yandexTest;

import java.util.List;
import java.util.stream.Stream;

public interface DataForYandexPurchases {

//    static Stream<PurchasesSource> dataYandexPurchases(){
//        return Stream.of(PurchasesSource.builder()
//                .laptopTitles("ноутбуки")
//                .priceMin("10000")
//                .priceMax("40000")
//                .producer(List.of("HP", "Lenovo", "Samsung"))
//                .build());
//    }
    static Stream<PurchasesSource> dataYandexPurchases() {
        return Stream.of(
                // Тестовый набор 1
                PurchasesSource.builder()
                        .laptopTitles("Ноутбуки")
                        .priceMin("10000")
                        .priceMax("40000")
                        .producer(List.of("HP", "Lenovo", "Samsung", "Vityas"))
                        .build(),

                // Тестовый набор 2
                PurchasesSource.builder()
                        .laptopTitles("Планшеты")
                        .priceMin("2000")
                        .priceMax("50000")
                        .producer(List.of("HUAWEI", "Xiaomi", "TECNO"))
                        .build(),

                // Тестовый набор 3
                PurchasesSource.builder()
                        .laptopTitles("Телевизоры")
                        .priceMin("30000")
                        .priceMax("100000")
                        .producer(List.of("Hisense", "SBER", "LG"))
                        .build()
        );
    }
}


