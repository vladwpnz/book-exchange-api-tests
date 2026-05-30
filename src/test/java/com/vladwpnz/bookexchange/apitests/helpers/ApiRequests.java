package com.vladwpnz.bookexchange.apitests.helpers;

import com.vladwpnz.bookexchange.apitests.models.TestUser;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public final class ApiRequests {

    public static RequestSpecification json() {
        return RestAssured.given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    public static RequestSpecification authenticated(TestUser user) {
        return json()
                .auth()
                .preemptive()
                .basic(user.email(), user.password());
    }

    private ApiRequests() {
    }
}
