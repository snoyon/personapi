package com.example.personnes.presentation.api;

class HeaderClientIdInvalideException extends RuntimeException {

    HeaderClientIdInvalideException() {
        super("Le header HTTP ClientId est obligatoire");
    }
}
