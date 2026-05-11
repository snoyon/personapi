package com.example.personnes.presentation.security;

import com.example.personnes.application.AppelantApi;
import com.example.personnes.application.DroitsAccesFamilles;
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

class AppelantAuthenticationFilter extends OncePerRequestFilter {

    static final String CN_HEADER = "CN";
    static final String CLIENT_ID_HEADER = "ClientId";

    private final DroitsAccesFamillesRepository droitsAccesFamillesRepository;

    AppelantAuthenticationFilter(DroitsAccesFamillesRepository droitsAccesFamillesRepository) {
        this.droitsAccesFamillesRepository = droitsAccesFamillesRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String cn = request.getHeader(CN_HEADER);
        String clientId = request.getHeader(CLIENT_ID_HEADER);

        if (cn != null && !cn.isBlank()) {
            String cnNormalise = cn.trim();
            String clientIdNormalise = normaliserClientId(clientId);
            AppelantApi appelantApi = new AppelantApi(cnNormalise, clientIdNormalise);
            DroitsAccesFamilles droitsAcces = droitsAccesFamillesRepository
                    .trouverDroitsAcces(appelantApi)
                    .orElse(null);
            List<GrantedAuthority> authorities = droitsAcces == null
                    ? clientInconnu(request)
                    : toAuthorities(droitsAcces.famillesAutorisees());

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal(cnNormalise, clientIdNormalise, droitsAcces),
                    null,
                    authorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String normaliserClientId(String clientId) {
        return clientId == null ? "" : clientId.trim();
    }

    private AppelantPrincipal principal(String cn, String clientId, DroitsAccesFamilles droitsAcces) {
        String clientName = droitsAcces == null ? "client-inconnu" : droitsAcces.clientName();
        return new AppelantPrincipal(cn, clientId, clientName);
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
