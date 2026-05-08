package com.example.personnes.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "personne")
class PersonneEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String telephone;

    @Column(name = "adresse_postale", nullable = false)
    private String adressePostale;

    @Column(name = "salaire_mensuel", nullable = false)
    private BigDecimal salaireMensuel;

    protected PersonneEntity() {
    }

    Long getId() {
        return id;
    }

    String getNom() {
        return nom;
    }

    String getPrenom() {
        return prenom;
    }

    String getEmail() {
        return email;
    }

    String getTelephone() {
        return telephone;
    }

    String getAdressePostale() {
        return adressePostale;
    }

    BigDecimal getSalaireMensuel() {
        return salaireMensuel;
    }
}
