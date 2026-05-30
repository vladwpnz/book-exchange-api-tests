package com.vladwpnz.bookexchange.apitests.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BookItem(
        // Current API DTO exposes snake_case names from Java fields. Adjust these if response fields are renamed.
        @JsonProperty("book_id") Long bookId,
        String author,
        String title,
        @JsonProperty("holder_id") Long holderId,
        @JsonProperty("owner_id") Long ownerId
) {
}
