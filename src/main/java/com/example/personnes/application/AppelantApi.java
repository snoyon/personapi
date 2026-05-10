package com.example.personnes.application;

public record AppelantApi(String cn, String clientId) {

    public AppelantApi {
        clientId = clientId == null ? "" : clientId;
    }
}
