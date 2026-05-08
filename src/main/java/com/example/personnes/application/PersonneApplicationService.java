package com.example.personnes.application;

import com.example.personnes.domain.model.FamilleDonnees;
import com.example.personnes.domain.model.Personne;
import com.example.personnes.domain.model.PersonneIntrouvableException;
import com.example.personnes.domain.port.PersonneRepository;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Set;

@Service
public class PersonneApplicationService {

    private final PersonneRepository personneRepository;

    public PersonneApplicationService(PersonneRepository personneRepository) {
        this.personneRepository = personneRepository;
    }

    public PersonneDemandee recupererPersonne(AppelantApi appelantApi, Long idPersonne, Set<FamilleDonnees> familles) {
        Personne personne = personneRepository.trouverParId(idPersonne)
                .orElseThrow(() -> new PersonneIntrouvableException(idPersonne));

        Set<FamilleDonnees> famillesDemandees = familles.isEmpty()
                ? EnumSet.allOf(FamilleDonnees.class)
                : EnumSet.copyOf(familles);

        return new PersonneDemandee(
                personne.id(),
                famillesDemandees.contains(FamilleDonnees.IDENTITE) ? personne.identite() : null,
                famillesDemandees.contains(FamilleDonnees.COORDONNEES) ? personne.coordonnees() : null,
                famillesDemandees.contains(FamilleDonnees.REVENUS) ? personne.revenus() : null
        );
    }
}
