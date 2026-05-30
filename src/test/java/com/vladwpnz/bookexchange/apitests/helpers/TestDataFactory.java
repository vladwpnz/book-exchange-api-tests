package com.vladwpnz.bookexchange.apitests.helpers;

import com.vladwpnz.bookexchange.apitests.models.AddBookRequest;
import com.vladwpnz.bookexchange.apitests.models.TestUser;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

public final class TestDataFactory {

    private static final AtomicLong SEQUENCE = new AtomicLong(System.currentTimeMillis());

    public static TestUser user(String authority) {
        long id = nextId();
        String role = authority.toLowerCase(Locale.ROOT);

        return new TestUser(
                "API Test " + role + " " + id,
                "api-test-" + role + "-" + id + "@example.com",
                "Password-" + id + "!",
                authority
        );
    }

    public static AddBookRequest book() {
        long id = nextId();
        return new AddBookRequest("API Test Author " + id, "API Test Book " + id);
    }

    public static String unknownEmail() {
        return "missing-user-" + nextId() + "@example.com";
    }

    public static Long unlikelyBookId() {
        return 999_999_999L;
    }

    private static long nextId() {
        return SEQUENCE.incrementAndGet();
    }

    private TestDataFactory() {
    }
}
