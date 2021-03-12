package com.tiparo.tripway.views.ui;

enum SignInState {
    EXIT,
    FAILED_EXIT,
    FAILED_REGISTERED,
    USER_REGISTERED,
    UNAUTHENTICATED_ON_START,
    UNAUTHENTICATED,        // Initial state, the user needs to authenticate
    LOADING,                // Authentication is loading
    AUTHENTICATED,          // The user has authenticated successfully
    FAILED_AUTHENTICATION,
    FAILED_AUTHENTICATION_ON_START// Authentication failed
}
