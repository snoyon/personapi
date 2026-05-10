package com.example.personnes.presentation.security;

final class SecurityErrorAttributes {

    static final String ERROR_CODE = "personnesApi.security.errorCode";

    static final String CN_ABSENT = "401.1";
    static final String CLIENT_INCONNU = "403.1";
    static final String FAMILLE_INTERDITE = "403.2";

    private SecurityErrorAttributes() {
    }
}
