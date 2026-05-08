package com.example.personnes.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ConfigurationProperties(prefix = "personnes-api.security")
public class ClientAccessProperties {

    private Map<String, ClientAccess> clients = new HashMap<>();

    public Map<String, ClientAccess> getClients() {
        return clients;
    }

    public void setClients(Map<String, ClientAccess> clients) {
        this.clients = clients;
    }

    public static class ClientAccess {
        private Set<String> famillesAutorisees = new HashSet<>();

        public Set<String> getFamillesAutorisees() {
            return famillesAutorisees;
        }

        public void setFamillesAutorisees(Set<String> famillesAutorisees) {
            this.famillesAutorisees = famillesAutorisees;
        }
    }
}
