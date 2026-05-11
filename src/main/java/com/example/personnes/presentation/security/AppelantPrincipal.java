package com.example.personnes.presentation.security;

import org.springframework.security.core.AuthenticatedPrincipal;

public record AppelantPrincipal(String cn, String clientId, String clientName) implements AuthenticatedPrincipal {

    @Override
    public String getName() {
        return clientName;
    }
}
