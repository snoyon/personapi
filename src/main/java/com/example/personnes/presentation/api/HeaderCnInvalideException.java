package com.example.personnes.presentation.api;

class HeaderCnInvalideException extends RuntimeException {

    HeaderCnInvalideException() {
        super("Le header HTTP CN est obligatoire");
    }
}
