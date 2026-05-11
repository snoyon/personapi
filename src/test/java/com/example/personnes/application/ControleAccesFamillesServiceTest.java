package com.example.personnes.application;

import com.example.personnes.application.port.DroitsAccesFamillesRepository;
import com.example.personnes.domain.model.FamilleDonnees;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ControleAccesFamillesServiceTest {

    private DroitsAccesFamillesRepository droitsAccesFamillesRepository;
    private ControleAccesFamillesService controleAccesFamillesService;

    @BeforeEach
    void setUp() {
        droitsAccesFamillesRepository = mock(DroitsAccesFamillesRepository.class);
        controleAccesFamillesService = new ControleAccesFamillesService(droitsAccesFamillesRepository);
    }

    @Test
    void retourneAutoriseQuandToutesLesFamillesDemandeesSontAutorisees() {
        AppelantApi appelantApi = new AppelantApi("api-manager", "client-rh");
        when(droitsAccesFamillesRepository.trouverDroitsAcces(appelantApi))
                .thenReturn(Optional.of(new DroitsAccesFamilles(
                        "Application RH via API Manager",
                        Set.of(FamilleDonnees.IDENTITE, FamilleDonnees.REVENUS)
                )));

        ResultatControleAcces resultat = controleAccesFamillesService.controler(
                appelantApi,
                Set.of(FamilleDonnees.IDENTITE, FamilleDonnees.REVENUS)
        );

        assertThat(resultat).isEqualTo(ResultatControleAcces.AUTORISE);
        verify(droitsAccesFamillesRepository).trouverDroitsAcces(appelantApi);
    }

    @Test
    void retourneFamilleInterditeQuandUneFamilleDemandeeNestPasAutorisee() {
        AppelantApi appelantApi = new AppelantApi("api-manager", "client-partenaire-identite");
        when(droitsAccesFamillesRepository.trouverDroitsAcces(appelantApi))
                .thenReturn(Optional.of(new DroitsAccesFamilles(
                        "Partenaire Identite",
                        Set.of(FamilleDonnees.IDENTITE)
                )));

        ResultatControleAcces resultat = controleAccesFamillesService.controler(
                appelantApi,
                Set.of(FamilleDonnees.IDENTITE, FamilleDonnees.REVENUS)
        );

        assertThat(resultat).isEqualTo(ResultatControleAcces.FAMILLE_INTERDITE);
    }

    @Test
    void retourneClientInconnuQuandAucunDroitNestDeclarePourLappelant() {
        AppelantApi appelantApi = new AppelantApi("api-manager", "client-inconnu");
        when(droitsAccesFamillesRepository.trouverDroitsAcces(appelantApi))
                .thenReturn(Optional.empty());

        ResultatControleAcces resultat = controleAccesFamillesService.controler(
                appelantApi,
                Set.of(FamilleDonnees.IDENTITE)
        );

        assertThat(resultat).isEqualTo(ResultatControleAcces.CLIENT_INCONNU);
    }
}
