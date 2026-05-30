package com.vladwpnz.bookexchange.apitests;

import com.vladwpnz.bookexchange.apitests.config.EndpointPaths;
import com.vladwpnz.bookexchange.apitests.helpers.ApiRequests;
import com.vladwpnz.bookexchange.apitests.helpers.AuthHelper;
import com.vladwpnz.bookexchange.apitests.helpers.TestDataFactory;
import com.vladwpnz.bookexchange.apitests.models.ErrorResponse;
import com.vladwpnz.bookexchange.apitests.models.ItemsWithUserResponse;
import com.vladwpnz.bookexchange.apitests.models.RegistrationRequest;
import com.vladwpnz.bookexchange.apitests.models.TestUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthApiTest extends BaseApiTest {

    @Test
    @DisplayName("POST /register creates a new user")
    void registerNewUser() {
        TestUser user = TestDataFactory.user("user");

        String body = AuthHelper.registerResponse(user)
                .then()
                .statusCode(200)
                .extract()
                .asString();

        assertThat(body).contains("Successfully registered");
    }

    @Test
    @DisplayName("POST /register rejects duplicate email")
    void registerDuplicateEmailRejected() {
        TestUser user = AuthHelper.registerUser();

        String body = AuthHelper.registerResponse(user)
                .then()
                .statusCode(400)
                .extract()
                .asString();

        assertThat(body).contains("Such a user already exists");
    }

    @Test
    @DisplayName("POST /register rejects unsupported authority")
    void registerRejectsInvalidAuthority() {
        TestUser user = TestDataFactory.user("manager");

        String body = AuthHelper.registerResponse(user)
                .then()
                .statusCode(400)
                .extract()
                .asString();

        assertThat(body).contains("Wrong authority provided");
    }

    @Test
    @DisplayName("POST /register returns validation error for missing email")
    void registerValidationErrorWhenEmailMissing() {
        TestUser user = TestDataFactory.user("user");
        RegistrationRequest request = new RegistrationRequest(user.name(), "", user.password(), user.authority());

        ErrorResponse error = ApiRequests.json()
                .body(request)
                .post(EndpointPaths.REGISTER)
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);

        assertThat(error.error()).containsIgnoringCase("email");
    }

    @Test
    @DisplayName("Basic Auth works after registration")
    void basicAuthWorksAfterRegistration() {
        TestUser user = AuthHelper.registerUser();

        // The current API has no /login endpoint. If one is added, this test can be moved there.
        ItemsWithUserResponse response = ApiRequests.authenticated(user)
                .get(EndpointPaths.OWNED_BOOKS)
                .then()
                .statusCode(200)
                .extract()
                .as(ItemsWithUserResponse.class);

        assertThat(response.booksOrEmpty()).isNotNull();
    }

    @Test
    @DisplayName("Invalid Basic Auth password is rejected")
    void invalidBasicCredentialsRejected() {
        TestUser user = AuthHelper.registerUser();

        ApiRequests.json()
                .auth()
                .preemptive()
                .basic(user.email(), "wrong-" + user.password())
                .get(EndpointPaths.OWNED_BOOKS)
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Anonymous user cannot read owned books")
    void anonymousUserCannotReadOwnedBooks() {
        ApiRequests.json()
                .get(EndpointPaths.OWNED_BOOKS)
                .then()
                .statusCode(401);
    }
}
