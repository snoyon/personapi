package com.example.personnes.application;

import com.example.personnes.domain.model.FamilleDonnees;

import java.util.Set;

public record DroitsAccesFamilles(String clientName, Set<FamilleDonnees> famillesAutorisees) {
}
