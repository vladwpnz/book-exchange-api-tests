package com.vladwpnz.bookexchange.apitests.config;

public record ApiConfig(String baseUrl, boolean requireAvailable) {

    public static final String BASE_URL_ENV = "BOOK_EXCHANGE_API_BASE_URL";
    public static final String REQUIRE_AVAILABLE_ENV = "BOOK_EXCHANGE_API_REQUIRE_AVAILABLE";
    public static final String DEFAULT_BASE_URL = "http://localhost:8080";

    public static ApiConfig load() {
        String baseUrl = firstNonBlank(
                System.getProperty(BASE_URL_ENV),
                System.getenv(BASE_URL_ENV),
                System.getProperty("book.exchange.api.base-url"),
                DEFAULT_BASE_URL
        );

        String requireAvailable = firstNonBlank(
                System.getProperty(REQUIRE_AVAILABLE_ENV),
                System.getenv(REQUIRE_AVAILABLE_ENV),
                "false"
        );

        return new ApiConfig(stripTrailingSlashes(baseUrl), Boolean.parseBoolean(requireAvailable));
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }

        return "";
    }

    private static String stripTrailingSlashes(String value) {
        String normalized = value.trim();
        while (normalized.endsWith("/") && normalized.length() > 1) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        return normalized;
    }
}
