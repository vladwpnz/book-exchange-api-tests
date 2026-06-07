package com.vladwpnz.bookexchange.apitests.config;

public final class EndpointPaths {

    // Current API has no /login endpoint. Auth tests verify Basic Auth through /owned.
    public static final String REGISTER = "/register";

    public static final String ADD_BOOK = "/book/add";
    public static final String OWNED_BOOKS = "/owned";
    public static final String HELD_BOOKS = "/held";

    public static final String SHARE_BOOK = "/book/share";
    public static final String GIVE_BOOK = "/book/give";
    public static final String RETURN_BOOK = "/book/return";

    public static final String ADMIN_ITEMS = "/items";
    public static final String DELETE_BOOK = "/book/delete";

    // Admin tests verify this POST path rejects non-admin users.
    public static final String FORCE_RETURN_BOOK = "/book/return/force";

    private EndpointPaths() {
    }
}
