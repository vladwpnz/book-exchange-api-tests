package com.vladwpnz.bookexchange.apitests.helpers;

import com.vladwpnz.bookexchange.apitests.config.EndpointPaths;
import com.vladwpnz.bookexchange.apitests.models.AddBookRequest;
import com.vladwpnz.bookexchange.apitests.models.BookItem;
import com.vladwpnz.bookexchange.apitests.models.BookWithUser;
import com.vladwpnz.bookexchange.apitests.models.ItemsResponse;
import com.vladwpnz.bookexchange.apitests.models.ItemsWithUserResponse;
import com.vladwpnz.bookexchange.apitests.models.TestUser;
import io.restassured.response.Response;

public final class BookHelper {

    public static BookWithUser addBook(TestUser user, AddBookRequest book) {
        return addBookResponse(user, book)
                .then()
                .statusCode(201)
                .extract()
                .as(BookWithUser.class);
    }

    public static Response addBookResponse(TestUser user, AddBookRequest book) {
        return ApiRequests.authenticated(user)
                .body(book)
                .post(EndpointPaths.ADD_BOOK);
    }

    public static ItemsWithUserResponse ownedBooks(TestUser user) {
        return ApiRequests.authenticated(user)
                .get(EndpointPaths.OWNED_BOOKS)
                .then()
                .statusCode(200)
                .extract()
                .as(ItemsWithUserResponse.class);
    }

    public static ItemsWithUserResponse heldBooks(TestUser user) {
        return ApiRequests.authenticated(user)
                .get(EndpointPaths.HELD_BOOKS)
                .then()
                .statusCode(200)
                .extract()
                .as(ItemsWithUserResponse.class);
    }

    public static ItemsResponse adminItems(TestUser admin) {
        return ApiRequests.authenticated(admin)
                .get(EndpointPaths.ADMIN_ITEMS)
                .then()
                .statusCode(200)
                .extract()
                .as(ItemsResponse.class);
    }

    public static Long findBookIdByTitle(TestUser admin, String title) {
        return adminItems(admin).booksOrEmpty().stream()
                .filter(book -> title.equals(book.title()))
                .findFirst()
                .map(BookItem::bookId)
                .orElseThrow(() -> new AssertionError("Book was not visible in /items: " + title));
    }

    private BookHelper() {
    }
}
