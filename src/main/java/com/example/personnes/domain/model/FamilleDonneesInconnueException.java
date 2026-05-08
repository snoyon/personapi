package com.example.personnes.domain.model;

public class FamilleDonneesInconnueException extends RuntimeException {

    public FamilleDonneesInconnueException(String code) {
        super("Famille de donnees inconnue: " + code);
    }
}
