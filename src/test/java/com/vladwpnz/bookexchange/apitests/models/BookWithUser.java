package com.vladwpnz.bookexchange.apitests.models;

public record BookWithUser(String author, String title, UserSummary person) {
}
