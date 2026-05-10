# Personnes API

API Spring Boot organisee en quatre couches :

- `presentation/api` : controleur REST, DTO de sortie et gestion des erreurs HTTP.
- `presentation/security` : integration Spring Security, lecture du header `ClientId`, controle d'acces par `@PreAuthorize` et traduction des refus en erreurs HTTP.
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
ClientId: {identifiant-appelant}
```

Codes de familles :

- `1` : identite, avec `nom` et `prenom`
- `2` : coordonnees, avec `email`, `telephone` et `adressePostale`
- `3` : revenus, avec `salaireMensuel`

Si `data` est absent ou vide, toutes les familles sont retournees.

Le header HTTP `ClientId` est obligatoire. Il contient l'identifiant unique de l'appelant transmis de maniere securisee par l'API manager en amont de l'API.

Les droits d'acces aux familles de donnees sont configures dans `src/main/resources/application.yml` :

```yaml
personnes-api:
  security:
    clients:
      client-mobile:
        familles-autorisees:
          - "1"
          - "2"
          - "3"
      client-partenaire-identite:
        familles-autorisees:
          - "1"
      client-rh:
        familles-autorisees:
          - "1"
          - "3"
```

Les retours en erreur sont normalises au format suivant :

```json
{
  "code": "<code>",
  "message": "<message>"
}
```

Codes de securite :

- `401.1` : header `ClientId` absent ou vide, avec le message `Unauthorized`
- `403.1` : client inconnu, avec le message `Forbidden`
- `403.2` : famille interdite pour ce client, avec le message `Forbidden`

Exemple :

```bash
curl -H "ClientId: client-mobile" "http://localhost:8080/personnes/1?data=1,3"
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
