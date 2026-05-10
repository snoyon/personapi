package com.example.personnes.presentation.security;

import com.example.personnes.domain.model.FamilleDonnees;

final class FamilleAuthority {

    private static final String PREFIX = "FAMILLE_";

    private FamilleAuthority() {
    }

    static String depuis(FamilleDonnees familleDonnees) {
        return PREFIX + familleDonnees.code();
    }
}
