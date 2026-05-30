package com.vladwpnz.bookexchange.apitests.helpers;

import com.vladwpnz.bookexchange.apitests.config.EndpointPaths;
import com.vladwpnz.bookexchange.apitests.models.BookTransferRequest;
import com.vladwpnz.bookexchange.apitests.models.BookWithUser;
import com.vladwpnz.bookexchange.apitests.models.ReturnBookRequest;
import com.vladwpnz.bookexchange.apitests.models.TestUser;
import io.restassured.response.Response;

public final class TransferHelper {

    public static BookWithUser shareBook(TestUser owner, BookTransferRequest request) {
        return shareBookResponse(owner, request)
                .then()
                .statusCode(200)
                .extract()
                .as(BookWithUser.class);
    }

    public static Response shareBookResponse(TestUser owner, BookTransferRequest request) {
        return ApiRequests.authenticated(owner)
                .body(request)
                .post(EndpointPaths.SHARE_BOOK);
    }

    public static BookWithUser giveBook(TestUser owner, BookTransferRequest request) {
        return giveBookResponse(owner, request)
                .then()
                .statusCode(200)
                .extract()
                .as(BookWithUser.class);
    }

    public static Response giveBookResponse(TestUser owner, BookTransferRequest request) {
        return ApiRequests.authenticated(owner)
                .body(request)
                .post(EndpointPaths.GIVE_BOOK);
    }

    public static String returnBook(TestUser holder, ReturnBookRequest request) {
        return returnBookResponse(holder, request)
                .then()
                .statusCode(200)
                .extract()
                .asString();
    }

    public static Response returnBookResponse(TestUser holder, ReturnBookRequest request) {
        return ApiRequests.authenticated(holder)
                .body(request)
                .post(EndpointPaths.RETURN_BOOK);
    }

    public static Response forceReturnBookResponse(TestUser admin, Long bookId) {
        return ApiRequests.authenticated(admin)
                .queryParam("id", bookId)
                .post(EndpointPaths.FORCE_RETURN_BOOK);
    }

    private TransferHelper() {
    }
}
