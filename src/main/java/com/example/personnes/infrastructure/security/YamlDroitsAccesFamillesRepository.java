package com.example.personnes.infrastructure.security;

import com.example.personnes.application.AppelantApi;
import com.example.personnes.application.port.DroitsAccesFamillesRepository;
import com.example.personnes.domain.model.FamilleDonnees;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
class YamlDroitsAccesFamillesRepository implements DroitsAccesFamillesRepository {

    private final ClientAccessProperties clientAccessProperties;

    YamlDroitsAccesFamillesRepository(ClientAccessProperties clientAccessProperties) {
        this.clientAccessProperties = clientAccessProperties;
    }

    @Override
    public Optional<Set<FamilleDonnees>> trouverFamillesAutorisees(AppelantApi appelantApi) {
        return Optional.ofNullable(clientAccessProperties.getClients().get(appelantApi.clientId()))
                .map(ClientAccessProperties.ClientAccess::getFamillesAutorisees)
                .map(this::toFamillesDonnees);
    }

    private Set<FamilleDonnees> toFamillesDonnees(Set<String> codes) {
        return codes.stream()
                .map(FamilleDonnees::depuisCode)
                .collect(Collectors.toSet());
    }
}
