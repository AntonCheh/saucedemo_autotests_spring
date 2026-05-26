package selenium.sources.yandexTest;

import java.util.List;

public record PurchasesSource(String laptopTitles, String priceMin,
                              String priceMax, List<String>producer, String url) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String priceMin;
        private String priceMax;
        private String laptopTitles;
        private List<String>producer;
        private String url;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder laptopTitles(String laptopTitles) {
            this.laptopTitles = laptopTitles;
            return this;
        }

        public Builder priceMin(String priceMin) {
            this.priceMin = priceMin;
            return this;
        }

        public Builder priceMax(String priceMax) {
            this.priceMax = priceMax;
            return this;
        }

        public Builder producer(List<String>producer) {
            this.producer = producer;
            return this;
        }

        public PurchasesSource build() {
            return new PurchasesSource(laptopTitles, priceMin, priceMax, producer, url);
        }

    }
}
