package com.example.personnes.presentation.security;

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

    private final ClientAccessProperties clientAccessProperties;

    PersonneDataAuthorizationManager(ClientAccessProperties clientAccessProperties) {
        this.clientAccessProperties = clientAccessProperties;
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
        ClientAccessProperties.ClientAccess clientAccess = clientAccessProperties.getClients().get(clientId);
        if (clientAccess == null) {
            context.getRequest().setAttribute(SecurityErrorAttributes.ERROR_CODE, SecurityErrorAttributes.CLIENT_INCONNU);
            return new AuthorizationDecision(false);
        }

        Set<String> famillesDemandees = parseFamillesDemandees(context.getRequest());
        if (famillesDemandees == null) {
            return new AuthorizationDecision(true);
        }

        boolean autorise = clientAccess.getFamillesAutorisees().containsAll(famillesDemandees);
        if (!autorise) {
            context.getRequest().setAttribute(SecurityErrorAttributes.ERROR_CODE, SecurityErrorAttributes.FAMILLE_INTERDITE);
        }

        return new AuthorizationDecision(autorise);
    }

    private Set<String> parseFamillesDemandees(HttpServletRequest request) {
        String data = request.getParameter("data");
        if (data == null || data.isBlank()) {
            return EnumSet.allOf(FamilleDonnees.class).stream()
                    .map(FamilleDonnees::code)
                    .collect(Collectors.toSet());
        }

        Set<String> codes = Arrays.stream(data.split(","))
                .map(String::trim)
                .filter(code -> !code.isBlank())
                .collect(Collectors.toSet());

        boolean contientUneFamilleInconnue = codes.stream()
                .anyMatch(code -> Arrays.stream(FamilleDonnees.values()).noneMatch(famille -> famille.code().equals(code)));

        return contientUneFamilleInconnue ? null : codes;
    }
}
