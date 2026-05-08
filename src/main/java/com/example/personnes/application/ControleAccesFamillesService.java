package com.example.personnes.application;

import com.example.personnes.application.port.DroitsAccesFamillesRepository;
import com.example.personnes.domain.model.FamilleDonnees;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ControleAccesFamillesService {

    private final DroitsAccesFamillesRepository droitsAccesFamillesRepository;

    public ControleAccesFamillesService(DroitsAccesFamillesRepository droitsAccesFamillesRepository) {
        this.droitsAccesFamillesRepository = droitsAccesFamillesRepository;
    }

    public ResultatControleAcces controler(AppelantApi appelantApi, Set<FamilleDonnees> famillesDemandees) {
        return droitsAccesFamillesRepository.trouverFamillesAutorisees(appelantApi)
                .map(famillesAutorisees -> controlerFamilles(famillesAutorisees, famillesDemandees))
                .orElse(ResultatControleAcces.CLIENT_INCONNU);
    }

    private ResultatControleAcces controlerFamilles(
            Set<FamilleDonnees> famillesAutorisees,
            Set<FamilleDonnees> famillesDemandees
    ) {
        return famillesAutorisees.containsAll(famillesDemandees)
                ? ResultatControleAcces.AUTORISE
                : ResultatControleAcces.FAMILLE_INTERDITE;
    }
}
