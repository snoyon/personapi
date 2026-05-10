package com.example.personnes.presentation.api;

import com.example.personnes.application.AppelantApi;
import com.example.personnes.application.PersonneApplicationService;
import com.example.personnes.application.PersonneDemandee;
import com.example.personnes.domain.model.FamilleDonnees;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class PersonneController {

    private final PersonneApplicationService personneApplicationService;

    PersonneController(PersonneApplicationService personneApplicationService) {
        this.personneApplicationService = personneApplicationService;
    }

    @GetMapping("/personnes/{idPersonne}")
    @PreAuthorize("@personneAccessChecker.hasRequestedAuthorities(authentication, #data)")
    public PersonneResponse recupererPersonne(
            @RequestHeader(name = "CN", required = false) String cn,
            @RequestHeader(name = "ClientId", required = false) String clientId,
            @PathVariable Long idPersonne,
            @P("data") @RequestParam(name = "data", required = false, defaultValue = "") String data
    ) {
        PersonneDemandee personne = personneApplicationService.recupererPersonne(
                parseAppelantApi(cn, clientId),
                idPersonne,
                parseFamilles(data)
        );
        return PersonneResponse.depuis(personne);
    }

    private AppelantApi parseAppelantApi(String cn, String clientId) {
        if (cn == null || cn.isBlank()) {
            throw new HeaderCnInvalideException();
        }

        return new AppelantApi(cn.trim(), clientId == null ? "" : clientId.trim());
    }

    private Set<FamilleDonnees> parseFamilles(String data) {
        if (data == null || data.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(data.split(","))
                .map(String::trim)
                .filter(code -> !code.isBlank())
                .map(FamilleDonnees::depuisCode)
                .collect(Collectors.toSet());
    }
}
