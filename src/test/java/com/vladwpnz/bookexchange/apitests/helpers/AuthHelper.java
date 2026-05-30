package com.vladwpnz.bookexchange.apitests.helpers;

import com.vladwpnz.bookexchange.apitests.config.EndpointPaths;
import com.vladwpnz.bookexchange.apitests.models.RegistrationRequest;
import com.vladwpnz.bookexchange.apitests.models.TestUser;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public final class AuthHelper {

    public static TestUser registerUser() {
        return register(TestDataFactory.user("user"));
    }

    public static TestUser registerAdmin() {
        return register(TestDataFactory.user("admin"));
    }

    public static TestUser register(TestUser user) {
        String body = registerResponse(user)
                .then()
                .statusCode(200)
                .extract()
                .asString();

        assertThat(body).contains("Successfully registered");
        return user;
    }

    public static Response registerResponse(TestUser user) {
        return ApiRequests.json()
                .body(new RegistrationRequest(user.name(), user.email(), user.password(), user.authority()))
                .post(EndpointPaths.REGISTER);
    }

    private AuthHelper() {
    }
}
