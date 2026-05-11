package com.example.personnes.infrastructure.security;

import com.example.personnes.application.AppelantApi;
import com.example.personnes.application.DroitsAccesFamilles;
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
    public Optional<DroitsAccesFamilles> trouverDroitsAcces(AppelantApi appelantApi) {
        return clientAccessProperties.getClients().stream()
                .filter(clientAccess -> memeAppelant(clientAccess, appelantApi))
                .findFirst()
                .map(this::toDroitsAcces);
    }

    private DroitsAccesFamilles toDroitsAcces(ClientAccessProperties.ClientAccess clientAccess) {
        return new DroitsAccesFamilles(
                clientAccess.getClientName(),
                toFamillesDonnees(clientAccess.getFamillesAutorisees())
        );
    }

    private boolean memeAppelant(ClientAccessProperties.ClientAccess clientAccess, AppelantApi appelantApi) {
        return normaliser(clientAccess.getCn()).equals(normaliser(appelantApi.cn()))
                && normaliser(clientAccess.getClientId()).equals(normaliser(appelantApi.clientId()));
    }

    private String normaliser(String value) {
        return value == null ? "" : value.trim();
    }

    private Set<FamilleDonnees> toFamillesDonnees(Set<String> codes) {
        return codes.stream()
                .map(FamilleDonnees::depuisCode)
                .collect(Collectors.toSet());
    }
}
