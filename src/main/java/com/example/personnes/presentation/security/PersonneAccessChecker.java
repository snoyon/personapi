package com.example.personnes.presentation.security;

import com.example.personnes.domain.model.FamilleDonnees;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PersonneAccessChecker {

    public boolean hasRequestedAuthorities(Authentication authentication, String data) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        if (clientInconnu()) {
            return false;
        }

        Set<FamilleDonnees> famillesDemandees = parseFamillesDemandees(data);
        if (famillesDemandees == null) {
            return true;
        }

        boolean autorise = famillesDemandees.stream()
                .map(FamilleAuthority::depuis)
                .allMatch(authority -> hasAuthority(authentication, authority));

        return autorise || refuser(SecurityErrorAttributes.FAMILLE_INTERDITE);
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority::equals);
    }

    private boolean clientInconnu() {
        return SecurityErrorAttributes.CLIENT_INCONNU.equals(
                currentRequest().getAttribute(SecurityErrorAttributes.ERROR_CODE)
        );
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
