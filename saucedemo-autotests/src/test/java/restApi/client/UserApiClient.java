package restApi.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import restApi.pojo.users.CreateUserRequest;
import restApi.pojo.users.CreateUserResponse;

import static io.restassured.RestAssured.given;

public class UserApiClient {
    private final RequestSpecification requestSpec;

    public UserApiClient(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }

    public CreateUserResponse createUser(CreateUserRequest request) {
        return given()
                .spec(requestSpec)
                .body(request)
                .when()
                .post("/api/users")
                .then()
                .statusCode(201)
                .extract()
                .as(CreateUserResponse.class);
    }

    public Response createUserWithResponse(CreateUserRequest request) {
        return given()
                .spec(requestSpec)
                .body(request)
                .when()
                .post("/api/users")
                .then()
                .extract()
                .response();
    }

    public CreateUserResponse getUserById(String userId) {
        return given()
                .spec(requestSpec)
                .when()
                .get("/api/users/" + userId)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getObject("data", CreateUserResponse.class);
    }
}