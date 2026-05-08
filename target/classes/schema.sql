create table personne (
    id bigint primary key,
    nom varchar(100) not null,
    prenom varchar(100) not null,
    email varchar(255) not null,
    telephone varchar(30) not null,
    adresse_postale varchar(255) not null,
    salaire_mensuel numeric(12, 2) not null
);
