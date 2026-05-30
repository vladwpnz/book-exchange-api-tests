package com.vladwpnz.bookexchange.apitests.models;

public record RegistrationRequest(String name, String email, String password, String authority) {
}
