package apiTests.booker;


import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestBookerSimple {

    @Test
    public void firstTest() {
        given()
                .baseUri("https://restful-booker.herokuapp.com")
                .when()
                .get("/booking")  // Правильный путь
                .then()
                .log().body()
                .statusCode(200)  // Проверяем статус код
                .body("size()", greaterThan(0))  // Проверяем, что массив не пустой
                .body("[0].bookingid", notNullValue());  // Проверяем, что есть bookingid
    }

    @Test
    public void testFilterByName() {
        given()
                .baseUri("https://restful-booker.herokuapp.com")
                .queryParam("firstname", "John")
                .queryParam("lastname", "Smith")
                .when()
                .get("/booking")
                .then()
                .log().body()
                .statusCode(200);
    }

    @Test
    public void testFilterByDates() {
        given()
                .baseUri("https://restful-booker.herokuapp.com")
                .queryParam("checkin", "2024-01-01")
                .queryParam("checkout", "2024-12-31")
                .when()
                .get("/booking")
                .then()
                .log().body()
                .statusCode(200);
    }

    @Test
    public void testGetBookingIds() {
        // Получаем список ID бронирований
        Response response = given()
                .baseUri("https://restful-booker.herokuapp.com")
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .extract().response();

        // Извлекаем список ID
        List<Integer> bookingIds = response.jsonPath().getList("bookingid");

        // Проверки
        Assertions.assertNotNull(bookingIds, "Booking IDs list should not be null");
        Assertions.assertTrue(bookingIds.size() > 0, "Should have at least one booking");

        // Выводим первые 5 ID для информации
        bookingIds.stream().limit(5).forEach(id ->
                System.out.println("Booking ID: " + id));

        // Проверяем, что все ID не null
        Assertions.assertTrue(bookingIds.stream().allMatch(id -> id != null),
                "All booking IDs should not be null");
    }

    @Test
    public void testBookingIdsStructure() {
        given()
                .baseUri("https://restful-booker.herokuapp.com")
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .body("$", everyItem(hasKey("bookingid")))  // Каждый объект имеет поле bookingid
                .body("bookingid", everyItem(notNullValue()))  // Все bookingid не null
                .body("bookingid", everyItem(instanceOf(Integer.class)));  // Все bookingid - числа
    }

    @Test
    public void findExistingNames() {
        // Получаем все ID бронирований
        List<Integer> ids = given()
                .baseUri("https://restful-booker.herokuapp.com")
                .when()
                .get("/booking")
                .then()
                .extract()
                .jsonPath()
                .getList("bookingid");

        // Берем первый ID и смотрим его данные
        if (!ids.isEmpty()) {
            given()
                    .baseUri("https://restful-booker.herokuapp.com")
                    .when()
                    .get("/booking/" + ids.get(0))
                    .then()
                    .log().body()
                    .statusCode(200);
        }
    }

    @Test
    public void smartFilterTest() {
        // 1. Получаем первый существующий ID и его данные
        List<Integer> ids = given()
                .baseUri("https://restful-booker.herokuapp.com")
                .when()
                .get("/booking")
                .then()
                .extract()
                .jsonPath()
                .getList("bookingid");

        if (ids.isEmpty()) {
            System.out.println("Нет данных для теста");
            return;
        }

        // 2. Получаем имя и фамилию из первого бронирования
        String firstName = given()
                .baseUri("https://restful-booker.herokuapp.com")
                .when()
                .get("/booking/" + ids.get(0))
                .then()
                .extract()
                .jsonPath()
                .getString("firstname");

        String lastName = given()
                .baseUri("https://restful-booker.herokuapp.com")
                .when()
                .get("/booking/" + ids.get(0))
                .then()
                .extract()
                .jsonPath()
                .getString("lastname");

        // 3. Теперь фильтруем по этим динамическим данным
        given()
                .baseUri("https://restful-booker.herokuapp.com")
                .queryParam("firstname", firstName)
                .queryParam("lastname", lastName)
                .when()
                .get("/booking")
                .then()
                .log().body()
                .statusCode(200)
                .body("size()", greaterThan(0)); // Должен найти хотя бы себя
    }



}
