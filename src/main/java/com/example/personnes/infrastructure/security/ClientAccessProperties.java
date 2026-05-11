package com.example.personnes.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ConfigurationProperties(prefix = "personnes-api.security")
public class ClientAccessProperties {

    private List<ClientAccess> clients = new ArrayList<>();

    public List<ClientAccess> getClients() {
        return clients;
    }

    public void setClients(List<ClientAccess> clients) {
        this.clients = clients;
    }

    public static class ClientAccess {
        private String cn;
        private String clientId = "";
        private String clientName;
        private Set<String> famillesAutorisees = new HashSet<>();

        public String getCn() {
            return cn;
        }

        public void setCn(String cn) {
            this.cn = cn;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientName() {
            return clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public Set<String> getFamillesAutorisees() {
            return famillesAutorisees;
        }

        public void setFamillesAutorisees(Set<String> famillesAutorisees) {
            this.famillesAutorisees = famillesAutorisees;
        }
    }
}
