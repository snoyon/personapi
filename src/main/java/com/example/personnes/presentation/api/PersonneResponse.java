package com.example.personnes.presentation.api;

import com.example.personnes.application.PersonneDemandee;
import com.example.personnes.domain.model.Coordonnees;
import com.example.personnes.domain.model.Identite;
import com.example.personnes.domain.model.Revenus;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PersonneResponse(
        Long id,
        Identite identite,
        Coordonnees coordonnees,
        Revenus revenus
) {

    static PersonneResponse depuis(PersonneDemandee personne) {
        return new PersonneResponse(
                personne.id(),
                personne.identite(),
                personne.coordonnees(),
                personne.revenus()
        );
    }
}
