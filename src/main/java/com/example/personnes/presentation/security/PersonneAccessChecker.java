package com.example.personnes.presentation.security;

import com.example.personnes.application.AppelantApi;
import com.example.personnes.application.ControleAccesFamillesService;
import com.example.personnes.application.ResultatControleAcces;
import com.example.personnes.domain.model.FamilleDonnees;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PersonneAccessChecker {

    private final ControleAccesFamillesService controleAccesFamillesService;

    PersonneAccessChecker(ControleAccesFamillesService controleAccesFamillesService) {
        this.controleAccesFamillesService = controleAccesFamillesService;
    }

    public boolean peutAcceder(Authentication authentication, String data) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Set<FamilleDonnees> famillesDemandees = parseFamillesDemandees(data);
        if (famillesDemandees == null) {
            return true;
        }

        ResultatControleAcces resultat = controleAccesFamillesService.controler(
                new AppelantApi(authentication.getName()),
                famillesDemandees
        );

        return switch (resultat) {
            case AUTORISE -> true;
            case CLIENT_INCONNU -> refuser(SecurityErrorAttributes.CLIENT_INCONNU);
            case FAMILLE_INTERDITE -> refuser(SecurityErrorAttributes.FAMILLE_INTERDITE);
        };
    }

    private boolean refuser(String codeErreur) {
        currentRequest().setAttribute(SecurityErrorAttributes.ERROR_CODE, codeErreur);
        return false;
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return requestAttributes.getRequest();
    }

    private Set<FamilleDonnees> parseFamillesDemandees(String data) {
        if (data == null || data.isBlank()) {
            return EnumSet.allOf(FamilleDonnees.class);
        }

        Set<String> codes = Arrays.stream(data.split(","))
                .map(String::trim)
                .filter(code -> !code.isBlank())
                .collect(Collectors.toSet());

        boolean contientUneFamilleInconnue = codes.stream()
                .anyMatch(code -> Arrays.stream(FamilleDonnees.values()).noneMatch(famille -> famille.code().equals(code)));

        if (contientUneFamilleInconnue) {
            return null;
        }

        return codes.stream()
                .map(FamilleDonnees::depuisCode)
                .collect(Collectors.toSet());
    }
}
