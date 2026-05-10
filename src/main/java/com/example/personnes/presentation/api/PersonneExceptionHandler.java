package com.example.personnes.presentation.api;

import com.example.personnes.domain.model.FamilleDonneesInconnueException;
import com.example.personnes.domain.model.PersonneIntrouvableException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class PersonneExceptionHandler {

    @ExceptionHandler(PersonneIntrouvableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiError personneIntrouvable(PersonneIntrouvableException exception) {
        return new ApiError("404.1", exception.getMessage());
    }

    @ExceptionHandler(FamilleDonneesInconnueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError familleInconnue(FamilleDonneesInconnueException exception) {
        return new ApiError("400.1", exception.getMessage());
    }

    @ExceptionHandler(HeaderCnInvalideException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError headerCnInvalide(HeaderCnInvalideException exception) {
        return new ApiError("400.2", exception.getMessage());
    }
}
