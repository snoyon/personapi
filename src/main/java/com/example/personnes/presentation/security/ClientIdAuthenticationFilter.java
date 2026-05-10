package com.example.personnes.presentation.security;

import com.example.personnes.application.AppelantApi;
import com.example.personnes.application.port.DroitsAccesFamillesRepository;
import com.example.personnes.domain.model.FamilleDonnees;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class ClientIdAuthenticationFilter extends OncePerRequestFilter {

    static final String CLIENT_ID_HEADER = "ClientId";

    private final DroitsAccesFamillesRepository droitsAccesFamillesRepository;

    ClientIdAuthenticationFilter(DroitsAccesFamillesRepository droitsAccesFamillesRepository) {
        this.droitsAccesFamillesRepository = droitsAccesFamillesRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String clientId = request.getHeader(CLIENT_ID_HEADER);

        if (clientId != null && !clientId.isBlank()) {
            String clientIdNormalise = clientId.trim();
            List<GrantedAuthority> authorities = droitsAccesFamillesRepository
                    .trouverFamillesAutorisees(new AppelantApi(clientIdNormalise))
                    .map(this::toAuthorities)
                    .orElseGet(() -> clientInconnu(request));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    clientIdNormalise,
                    null,
                    authorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private List<GrantedAuthority> toAuthorities(Set<FamilleDonnees> famillesAutorisees) {
        return famillesAutorisees.stream()
                .map(FamilleAuthority::depuis)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private List<GrantedAuthority> clientInconnu(HttpServletRequest request) {
        request.setAttribute(SecurityErrorAttributes.ERROR_CODE, SecurityErrorAttributes.CLIENT_INCONNU);
        return List.of();
    }
}
