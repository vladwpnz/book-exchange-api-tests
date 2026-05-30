package com.vladwpnz.bookexchange.apitests;

import com.vladwpnz.bookexchange.apitests.helpers.AuthHelper;
import com.vladwpnz.bookexchange.apitests.helpers.BookHelper;
import com.vladwpnz.bookexchange.apitests.helpers.TestDataFactory;
import com.vladwpnz.bookexchange.apitests.helpers.TransferHelper;
import com.vladwpnz.bookexchange.apitests.models.AddBookRequest;
import com.vladwpnz.bookexchange.apitests.models.BookTransferRequest;
import com.vladwpnz.bookexchange.apitests.models.BookWithUser;
import com.vladwpnz.bookexchange.apitests.models.ErrorResponse;
import com.vladwpnz.bookexchange.apitests.models.ReturnBookRequest;
import com.vladwpnz.bookexchange.apitests.models.TestUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookTransferApiTest extends BaseApiTest {

    @Test
    @DisplayName("POST /book/share shares a book with another user")
    void shareBookWithAnotherUser() {
        TestUser owner = AuthHelper.registerUser();
        TestUser borrower = AuthHelper.registerUser();
        AddBookRequest book = TestDataFactory.book();
        BookHelper.addBook(owner, book);

        BookWithUser sharedBook = TransferHelper.shareBook(
                owner,
                new BookTransferRequest(book.title(), borrower.email())
        );

        assertThat(sharedBook.title()).isEqualTo(book.title());
        assertThat(sharedBook.person().email()).isEqualTo(borrower.email());
        assertThat(BookHelper.heldBooks(borrower).booksOrEmpty())
                .extracting(BookWithUser::title)
                .contains(book.title());
    }

    @Test
    @DisplayName("POST /book/give transfers ownership permanently")
    void giveBookTransfersOwnership() {
        TestUser owner = AuthHelper.registerUser();
        TestUser recipient = AuthHelper.registerUser();
        AddBookRequest book = TestDataFactory.book();
        BookHelper.addBook(owner, book);

        BookWithUser givenBook = TransferHelper.giveBook(
                owner,
                new BookTransferRequest(book.title(), recipient.email())
        );

        assertThat(givenBook.title()).isEqualTo(book.title());
        assertThat(givenBook.person().email()).isEqualTo(recipient.email());
        assertThat(BookHelper.ownedBooks(recipient).booksOrEmpty())
                .extracting(BookWithUser::title)
                .contains(book.title());
    }

    @Test
    @DisplayName("POST /book/return returns a shared book to its owner")
    void borrowerCanReturnSharedBook() {
        TestUser owner = AuthHelper.registerUser();
        TestUser borrower = AuthHelper.registerUser();
        AddBookRequest book = TestDataFactory.book();
        BookHelper.addBook(owner, book);
        TransferHelper.shareBook(owner, new BookTransferRequest(book.title(), borrower.email()));

        String body = TransferHelper.returnBook(borrower, new ReturnBookRequest(book.title()));

        assertThat(body).contains("The book was returned");
        assertThat(BookHelper.heldBooks(owner).booksOrEmpty())
                .extracting(BookWithUser::title)
                .contains(book.title());
    }

    @Test
    @DisplayName("POST /book/share fails for unknown recipient")
    void shareBookToUnknownUserFails() {
        TestUser owner = AuthHelper.registerUser();
        AddBookRequest book = TestDataFactory.book();
        BookHelper.addBook(owner, book);

        ErrorResponse error = TransferHelper.shareBookResponse(
                        owner,
                        new BookTransferRequest(book.title(), TestDataFactory.unknownEmail())
                )
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);

        assertThat(error.error()).containsIgnoringCase("no users");
    }

    @Test
    @DisplayName("POST /book/share fails when book is already shared")
    void cannotShareAlreadySharedBook() {
        TestUser owner = AuthHelper.registerUser();
        TestUser firstBorrower = AuthHelper.registerUser();
        TestUser secondBorrower = AuthHelper.registerUser();
        AddBookRequest book = TestDataFactory.book();
        BookHelper.addBook(owner, book);
        TransferHelper.shareBook(owner, new BookTransferRequest(book.title(), firstBorrower.email()));

        ErrorResponse error = TransferHelper.shareBookResponse(
                        owner,
                        new BookTransferRequest(book.title(), secondBorrower.email())
                )
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);

        assertThat(error.error()).containsIgnoringCase("already");
    }

    @Test
    @DisplayName("POST /book/return fails when user does not hold the book")
    void returnBookNotHeldFails() {
        TestUser user = AuthHelper.registerUser();
        AddBookRequest book = TestDataFactory.book();

        ErrorResponse error = TransferHelper.returnBookResponse(user, new ReturnBookRequest(book.title()))
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorResponse.class);

        assertThat(error.error()).containsIgnoringCase("do not hold");
    }
}
