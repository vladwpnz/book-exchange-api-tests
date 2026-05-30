package com.vladwpnz.bookexchange.apitests;

import com.vladwpnz.bookexchange.apitests.config.ApiConfig;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class BaseApiTest {

    private static ApiConfig apiConfig;

    @BeforeAll
    static void configureRestAssured() {
        apiConfig = ApiConfig.load();

        RestAssured.baseURI = apiConfig.baseUrl();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        verifyApiIsReachable();
    }

    protected static ApiConfig apiConfig() {
        return apiConfig;
    }

    private static void verifyApiIsReachable() {
        try {
            RestAssured.given()
                    .relaxedHTTPSValidation()
                    .get("/");
        } catch (Exception exception) {
            String message = "Book Exchange API is not reachable at " + apiConfig.baseUrl()
                    + ". Start the API or set " + ApiConfig.BASE_URL_ENV + ".";

            if (apiConfig.requireAvailable()) {
                fail(message, exception);
            }

            assumeTrue(false, message);
        }
    }
}
