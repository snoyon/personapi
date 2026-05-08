package com.example.personnes.infrastructure.persistence;

import com.example.personnes.domain.model.Coordonnees;
import com.example.personnes.domain.model.Identite;
import com.example.personnes.domain.model.Personne;
import com.example.personnes.domain.model.Revenus;
import com.example.personnes.domain.port.PersonneRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class PersonnePersistenceAdapter implements PersonneRepository {

    private final JpaPersonneRepository jpaPersonneRepository;

    PersonnePersistenceAdapter(JpaPersonneRepository jpaPersonneRepository) {
        this.jpaPersonneRepository = jpaPersonneRepository;
    }

    @Override
    public Optional<Personne> trouverParId(Long idPersonne) {
        return jpaPersonneRepository.findById(idPersonne)
                .map(this::toDomain);
    }

    private Personne toDomain(PersonneEntity entity) {
        return new Personne(
                entity.getId(),
                new Identite(entity.getNom(), entity.getPrenom()),
                new Coordonnees(entity.getEmail(), entity.getTelephone(), entity.getAdressePostale()),
                new Revenus(entity.getSalaireMensuel())
        );
    }
}
