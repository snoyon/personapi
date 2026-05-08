package com.example.personnes.domain.model;

public class PersonneIntrouvableException extends RuntimeException {

    public PersonneIntrouvableException(Long idPersonne) {
        super("Personne introuvable: " + idPersonne);
    }
}
