package com.example.personnes.domain.model;

import java.util.Arrays;

public enum FamilleDonnees {
    IDENTITE("1"),
    COORDONNEES("2"),
    REVENUS("3");

    private final String code;

    FamilleDonnees(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static FamilleDonnees depuisCode(String code) {
        return Arrays.stream(values())
                .filter(famille -> famille.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new FamilleDonneesInconnueException(code));
    }
}
