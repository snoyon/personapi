package com.example.personnes.application.port;

import com.example.personnes.application.AppelantApi;
import com.example.personnes.domain.model.FamilleDonnees;

import java.util.Optional;
import java.util.Set;

public interface DroitsAccesFamillesRepository {

    Optional<Set<FamilleDonnees>> trouverFamillesAutorisees(AppelantApi appelantApi);
}
