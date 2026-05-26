package selenium.sources.mvideoTest;

import selenium.sources.yandexTest.PurchasesSource;

import java.util.List;
import java.util.stream.Stream;

public interface DataForMvideoPurchases {

    static Stream<PurchasesSource> dataMvideoPurchases() {
        return Stream.of(
                PurchasesSource.builder()
                        .laptopTitles("Ноутбуки")
                        .priceMin("15000")
                        .priceMax("100000")
                        .producer(List.of("HP", "Lenovo", "Huawei"))
                        .build()
        );
    }
}


