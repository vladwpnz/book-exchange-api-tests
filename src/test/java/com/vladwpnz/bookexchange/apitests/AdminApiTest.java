package com.vladwpnz.bookexchange.apitests;

import com.vladwpnz.bookexchange.apitests.config.EndpointPaths;
import com.vladwpnz.bookexchange.apitests.helpers.ApiRequests;
import com.vladwpnz.bookexchange.apitests.helpers.AuthHelper;
import com.vladwpnz.bookexchange.apitests.helpers.BookHelper;
import com.vladwpnz.bookexchange.apitests.helpers.TestDataFactory;
import com.vladwpnz.bookexchange.apitests.helpers.TransferHelper;
import com.vladwpnz.bookexchange.apitests.models.AddBookRequest;
import com.vladwpnz.bookexchange.apitests.models.BookItem;
import com.vladwpnz.bookexchange.apitests.models.BookTransferRequest;
import com.vladwpnz.bookexchange.apitests.models.BookWithUser;
import com.vladwpnz.bookexchange.apitests.models.TestUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AdminApiTest extends BaseApiTest {

    @Test
    @DisplayName("GET /items returns all books for admin")
    void adminCanListAllBooks() {
        TestUser admin = AuthHelper.registerAdmin();
        TestUser user = AuthHelper.registerUser();
        AddBookRequest book = TestDataFactory.book();
        BookHelper.addBook(user, book);

        assertThat(BookHelper.adminItems(admin).booksOrEmpty())
                .extracting(BookItem::title)
                .contains(book.title());
    }

    @Test
    @DisplayName("GET /items is forbidden for regular user")
    void regularUserCannotListAllItems() {
        TestUser user = AuthHelper.registerUser();

        ApiRequests.authenticated(user)
                .get(EndpointPaths.ADMIN_ITEMS)
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("GET /items requires authentication")
    void anonymousUserCannotListAllItems() {
        ApiRequests.json()
                .get(EndpointPaths.ADMIN_ITEMS)
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("DELETE /book/delete is forbidden for regular user")
    void regularUserCannotDeleteBook() {
        TestUser user = AuthHelper.registerUser();

        ApiRequests.authenticated(user)
                .queryParam("id", 1L)
                .delete(EndpointPaths.DELETE_BOOK)
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("DELETE /book/delete removes an existing book for admin")
    void adminCanDeleteExistingBook() {
        TestUser admin = AuthHelper.registerAdmin();
        TestUser owner = AuthHelper.registerUser();
        AddBookRequest book = TestDataFactory.book();
        BookHelper.addBook(owner, book);
        Long bookId = BookHelper.findBookIdByTitle(admin, book.title());

        String body = ApiRequests.authenticated(admin)
                .queryParam("id", bookId)
                .delete(EndpointPaths.DELETE_BOOK)
                .then()
                .statusCode(200)
                .extract()
                .asString();

        assertThat(body).contains("Book deleted");
        assertThat(BookHelper.adminItems(admin).booksOrEmpty())
                .extracting(BookItem::bookId)
                .doesNotContain(bookId);
    }

    @Test
    @DisplayName("DELETE /book/delete returns not found for unknown id")
    void adminDeleteUnknownBookIdReturnsNotFound() {
        TestUser admin = AuthHelper.registerAdmin();

        String body = ApiRequests.authenticated(admin)
                .queryParam("id", TestDataFactory.unlikelyBookId())
                .delete(EndpointPaths.DELETE_BOOK)
                .then()
                .statusCode(404)
                .extract()
                .asString();

        assertThat(body).contains("Book not found");
    }

    @Test
    @DisplayName("POST /book/return/force returns a shared book for admin")
    void adminCanForceReturnSharedBook() {
        TestUser admin = AuthHelper.registerAdmin();
        TestUser owner = AuthHelper.registerUser();
        TestUser borrower = AuthHelper.registerUser();
        AddBookRequest book = TestDataFactory.book();
        BookHelper.addBook(owner, book);
        TransferHelper.shareBook(owner, new BookTransferRequest(book.title(), borrower.email()));
        Long bookId = BookHelper.findBookIdByTitle(admin, book.title());

        String body = TransferHelper.forceReturnBookResponse(admin, bookId)
                .then()
                .statusCode(200)
                .extract()
                .asString();

        assertThat(body).contains("The book was returned");
        assertThat(BookHelper.heldBooks(owner).booksOrEmpty())
                .extracting(BookWithUser::title)
                .contains(book.title());
    }

    @Test
    @DisplayName("POST /book/return/force returns not found for unknown id")
    void adminForceReturnUnknownBookIdReturnsNotFound() {
        TestUser admin = AuthHelper.registerAdmin();

        String body = TransferHelper.forceReturnBookResponse(admin, TestDataFactory.unlikelyBookId())
                .then()
                .statusCode(404)
                .extract()
                .asString();

        assertThat(body).contains("Book not found");
    }

    @Test
    @DisplayName("POST /book/return/force is forbidden for regular user")
    void regularUserCannotForceReturnBook() {
        TestUser user = AuthHelper.registerUser();

        ApiRequests.authenticated(user)
                .queryParam("id", TestDataFactory.unlikelyBookId())
                .post(EndpointPaths.FORCE_RETURN_BOOK)
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("POST /book/return/force requires authentication")
    void anonymousUserCannotForceReturnBook() {
        ApiRequests.json()
                .queryParam("id", TestDataFactory.unlikelyBookId())
                .post(EndpointPaths.FORCE_RETURN_BOOK)
                .then()
                .statusCode(401);
    }
}
