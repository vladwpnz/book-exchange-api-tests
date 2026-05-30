package com.vladwpnz.bookexchange.apitests.models;

import java.util.List;

public record ItemsResponse(List<BookItem> books) {

    public List<BookItem> booksOrEmpty() {
        return books == null ? List.of() : books;
    }
}
