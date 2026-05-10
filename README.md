# Personnes API

API Spring Boot organisee en quatre couches :

- `presentation/api` : controleur REST, DTO de sortie et gestion des erreurs HTTP.
- `presentation/security` : integration Spring Security, lecture des headers `CN` et `ClientId`, transformation des droits en authorities, controle d'acces par `@PreAuthorize` et traduction des refus en erreurs HTTP.
- `application` : cas d'utilisation pour recuperer une personne et controle applicatif des droits par famille.
- `domain` : modele metier, exceptions et port repository.
- `infrastructure/persistence` : acces base de donnees via Spring Data JPA.
- `infrastructure/security` : lecture des droits declares dans la configuration YAML.

## Lancer l'application

```bash
mvn spring-boot:run
```

L'API demarre sur `http://localhost:8080`.

## Endpoint

```http
GET /personnes/{idPersonne}?data={familles}
CN: {common-name-certificat-client}
ClientId: {identifiant-appelant-optionnel}
```

Codes de familles :

- `1` : identite, avec `nom` et `prenom`
- `2` : coordonnees, avec `email`, `telephone` et `adressePostale`
- `3` : revenus, avec `salaireMensuel`

Si `data` est absent ou vide, toutes les familles sont retournees.

Le header HTTP `CN` est obligatoire. Il contient le common name du certificat client utilise pour appeler l'API.

Le header HTTP `ClientId` est optionnel :

- appel direct sans API manager : `CN` identifie l'appelant et `ClientId` est absent
- appel via API manager : `CN` identifie l'API manager et `ClientId` identifie le client final de l'API

L'appelant est donc identifie par le couple `CN + ClientId`, avec `ClientId` vide en appel direct.

Les droits d'acces aux familles de donnees sont configures dans `src/main/resources/application.yml` :

```yaml
personnes-api:
  security:
    clients:
      - cn: api-manager
        client-id: client-mobile
        familles-autorisees:
          - "1"
          - "2"
          - "3"
      - cn: api-manager
        client-id: client-partenaire-identite
        familles-autorisees:
          - "1"
      - cn: api-manager
        client-id: client-rh
        familles-autorisees:
          - "1"
          - "3"
      - cn: application-directe-rh
        familles-autorisees:
          - "1"
          - "3"
```

Au moment de l'authentification, ces droits sont transformes en authorities Spring Security :

- famille `1` -> `FAMILLE_1`
- famille `2` -> `FAMILLE_2`
- famille `3` -> `FAMILLE_3`

Les retours en erreur sont normalises au format suivant :

```json
{
  "code": "<code>",
  "message": "<message>"
}
```

Codes de securite :

- `401.1` : header `CN` absent ou vide, avec le message `Unauthorized`
- `403.1` : client inconnu, avec le message `Forbidden`
- `403.2` : famille interdite pour ce client, avec le message `Forbidden`

Exemple :

```bash
curl -H "CN: api-manager" -H "ClientId: client-mobile" "http://localhost:8080/personnes/1?data=1,3"
```

Exemple en appel direct sans API manager :

```bash
curl -H "CN: application-directe-rh" "http://localhost:8080/personnes/1?data=1,3"
```

Reponse :

```json
{
  "id": 1,
  "identite": {
    "nom": "Dupont",
    "prenom": "Marie"
  },
  "revenus": {
    "salaireMensuel": 3200.00
  }
}
```

## Base de donnees

La base H2 en memoire est initialisee au demarrage avec :

- `src/main/resources/schema.sql`
- `src/main/resources/data.sql`

## Tests

```bash
mvn test
```
