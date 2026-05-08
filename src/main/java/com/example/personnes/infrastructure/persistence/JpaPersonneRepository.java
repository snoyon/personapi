package com.example.personnes.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface JpaPersonneRepository extends JpaRepository<PersonneEntity, Long> {
}
