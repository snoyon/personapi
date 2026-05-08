package com.example.personnes.domain.model;

public record Personne(
        Long id,
        Identite identite,
        Coordonnees coordonnees,
        Revenus revenus
) {
}
