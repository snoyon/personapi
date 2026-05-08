package com.example.personnes.presentation.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PersonneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void recupereLesFamillesDemandees() throws Exception {
        mockMvc.perform(get("/personnes/1")
                        .header("ClientId", "client-mobile")
                        .param("data", "1,3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.identite.nom").value("Dupont"))
                .andExpect(jsonPath("$.revenus.salaireMensuel").value(3200.0))
                .andExpect(jsonPath("$.coordonnees").doesNotExist());
    }

    @Test
    void recupereToutesLesFamillesQuandDataEstAbsent() throws Exception {
        mockMvc.perform(get("/personnes/1").header("ClientId", "client-mobile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identite").exists())
                .andExpect(jsonPath("$.coordonnees").exists())
                .andExpect(jsonPath("$.revenus").exists());
    }

    @Test
    void retourne404QuandLaPersonneEstIntrouvable() throws Exception {
        mockMvc.perform(get("/personnes/999")
                .header("ClientId", "client-mobile")
                        .param("data", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404.1"))
                .andExpect(jsonPath("$.message").value("Personne introuvable: 999"));
    }

    @Test
    void retourne400QuandUneFamilleEstInconnue() throws Exception {
        mockMvc.perform(get("/personnes/1")
                        .header("ClientId", "client-mobile")
                        .param("data", "1,9"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400.1"))
                .andExpect(jsonPath("$.message").value("Famille de donnees inconnue: 9"));
    }

    @Test
    void retourne400QuandLeHeaderClientIdEstAbsent() throws Exception {
        mockMvc.perform(get("/personnes/1").param("data", "1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401.1"))
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @Test
    void retourne400QuandLeHeaderClientIdEstVide() throws Exception {
        mockMvc.perform(get("/personnes/1")
                        .header("ClientId", " ")
                        .param("data", "1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401.1"))
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @Test
    void retourne403QuandLeClientNaPasAccesAUneFamilleDemandee() throws Exception {
        mockMvc.perform(get("/personnes/1")
                        .header("ClientId", "client-partenaire-identite")
                        .param("data", "1,3"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403.2"))
                .andExpect(jsonPath("$.message").value("Forbidden"));
    }

    @Test
    void retourne403QuandLeClientEstInconnu() throws Exception {
        mockMvc.perform(get("/personnes/1")
                        .header("ClientId", "client-inconnu")
                        .param("data", "1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403.1"))
                .andExpect(jsonPath("$.message").value("Forbidden"));
    }
}
