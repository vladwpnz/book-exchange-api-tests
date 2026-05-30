package com.vladwpnz.bookexchange.apitests.models;

import java.util.List;

public record ItemsWithUserResponse(List<BookWithUser> books) {

    public List<BookWithUser> booksOrEmpty() {
        return books == null ? List.of() : books;
    }
}
