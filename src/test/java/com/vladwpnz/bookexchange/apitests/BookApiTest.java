package com.vladwpnz.bookexchange.apitests;

import com.vladwpnz.bookexchange.apitests.config.EndpointPaths;
import com.vladwpnz.bookexchange.apitests.helpers.ApiRequests;
import com.vladwpnz.bookexchange.apitests.helpers.AuthHelper;
import com.vladwpnz.bookexchange.apitests.helpers.BookHelper;
import com.vladwpnz.bookexchange.apitests.helpers.TestDataFactory;
import com.vladwpnz.bookexchange.apitests.models.AddBookRequest;
import com.vladwpnz.bookexchange.apitests.models.BookWithUser;
import com.vladwpnz.bookexchange.apitests.models.ErrorResponse;
import com.vladwpnz.bookexchange.apitests.models.ItemsWithUserResponse;
import com.vladwpnz.bookexchange.apitests.models.TestUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookApiTest extends BaseApiTest {

    @Test
    @DisplayName("POST /book/add creates a book for authenticated user")
    void addBookCreatesBook() {
        TestUser user = AuthHelper.registerUser();
        AddBookRequest book = TestDataFactory.book();

        BookWithUser createdBook = BookHelper.addBook(user, book);

        assertThat(createdBook.author()).isEqualTo(book.author());
        assertThat(createdBook.title()).isEqualTo(book.title());
        assertThat(createdBook.person().email()).isEqualTo(user.email());
    }

    @Test
    @DisplayName("POST /book/add requires authentication")
    void anonymousUserCannotAddBook() {
        AddBookRequest book = TestDataFactory.book();

        ApiRequests.json()
                .body(book)
                .post(EndpointPaths.ADD_BOOK)
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("POST /book/add validates required author")
    void addBookValidationErrorWhenAuthorMissing() {
        TestUser user = AuthHelper.registerUser();
        AddBookRequest book = new AddBookRequest("", TestDataFactory.book().title());

        ErrorResponse error = BookHelper.addBookResponse(user, book)
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);

        assertThat(error.error()).containsIgnoringCase("author");
    }

    @Test
    @DisplayName("POST /book/add validates required title")
    void addBookValidationErrorWhenTitleMissing() {
        TestUser user = AuthHelper.registerUser();
        AddBookRequest book = new AddBookRequest(TestDataFactory.book().author(), "");

        ErrorResponse error = BookHelper.addBookResponse(user, book)
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);

        assertThat(error.error()).containsIgnoringCase("title");
    }

    @Test
    @DisplayName("GET /owned returns books owned by authenticated user")
    void ownedBooksContainCreatedBook() {
        TestUser user = AuthHelper.registerUser();
        AddBookRequest book = TestDataFactory.book();
        BookHelper.addBook(user, book);

        ItemsWithUserResponse ownedBooks = BookHelper.ownedBooks(user);

        assertThat(ownedBooks.booksOrEmpty())
                .extracting(BookWithUser::title)
                .contains(book.title());
    }

    @Test
    @DisplayName("GET /held returns books held by authenticated user")
    void heldBooksContainCreatedBook() {
        TestUser user = AuthHelper.registerUser();
        AddBookRequest book = TestDataFactory.book();
        BookHelper.addBook(user, book);

        ItemsWithUserResponse heldBooks = BookHelper.heldBooks(user);

        assertThat(heldBooks.booksOrEmpty())
                .extracting(BookWithUser::title)
                .contains(book.title());
    }

    @Test
    @DisplayName("GET /held requires authentication")
    void anonymousUserCannotReadHeldBooks() {
        ApiRequests.json()
                .get(EndpointPaths.HELD_BOOKS)
                .then()
                .statusCode(401);
    }
}
