package com.example.personnes.application.port;

import com.example.personnes.application.AppelantApi;
import com.example.personnes.application.DroitsAccesFamilles;

import java.util.Optional;

public interface DroitsAccesFamillesRepository {

    Optional<DroitsAccesFamilles> trouverDroitsAcces(AppelantApi appelantApi);
}
