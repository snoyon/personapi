package com.example.personnes.domain.port;

import com.example.personnes.domain.model.Personne;

import java.util.Optional;

public interface PersonneRepository {

    Optional<Personne> trouverParId(Long idPersonne);
}
