package com.example.personnes.application;

import com.example.personnes.domain.model.Coordonnees;
import com.example.personnes.domain.model.Identite;
import com.example.personnes.domain.model.Revenus;

public record PersonneDemandee(
        Long id,
        Identite identite,
        Coordonnees coordonnees,
        Revenus revenus
) {
}
