package com.example.personnes.presentation.security;

import com.example.personnes.application.AppelantApi;
import com.example.personnes.application.ControleAccesFamillesService;
import com.example.personnes.application.ResultatControleAcces;
import com.example.personnes.domain.model.FamilleDonnees;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
class PersonneDataAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final ControleAccesFamillesService controleAccesFamillesService;

    PersonneDataAuthorizationManager(ControleAccesFamillesService controleAccesFamillesService) {
        this.controleAccesFamillesService = controleAccesFamillesService;
    }

    @Override
    public AuthorizationDecision check(
            Supplier<Authentication> authenticationSupplier,
            RequestAuthorizationContext context
    ) {
        Authentication authentication = authenticationSupplier.get();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        String clientId = authentication.getName();
        Set<FamilleDonnees> famillesDemandees = parseFamillesDemandees(context.getRequest());
        if (famillesDemandees == null) {
            return new AuthorizationDecision(true);
        }

        ResultatControleAcces resultat = controleAccesFamillesService.controler(
                new AppelantApi(clientId),
                famillesDemandees
        );

        return switch (resultat) {
            case AUTORISE -> new AuthorizationDecision(true);
            case CLIENT_INCONNU -> refuser(context, SecurityErrorAttributes.CLIENT_INCONNU);
            case FAMILLE_INTERDITE -> refuser(context, SecurityErrorAttributes.FAMILLE_INTERDITE);
        };
    }

    private AuthorizationDecision refuser(RequestAuthorizationContext context, String codeErreur) {
        context.getRequest().setAttribute(SecurityErrorAttributes.ERROR_CODE, codeErreur);
        return new AuthorizationDecision(false);
    }

    private Set<FamilleDonnees> parseFamillesDemandees(HttpServletRequest request) {
        String data = request.getParameter("data");
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
