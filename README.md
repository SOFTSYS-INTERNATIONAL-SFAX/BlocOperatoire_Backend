# Bloc Operatoire Backend

Backend **Spring Boot** pour une plateforme hospitalière de gestion du bloc opératoire.

## Objectif de cette étape
Ce dépôt contient uniquement la **préparation de l'environnement technique** :
- structure Maven,
- dépendances essentielles,
- configuration applicative,
- point d'entrée Spring Boot.

Aucun module métier n'est implémenté dans cette phase.

## Stack technique
- Java 21
- Spring Boot 3.3.x
- Spring Web
- Spring Data JPA
- Spring Security + OAuth2 Resource Server
- Flyway
- PostgreSQL
- Spring Boot Actuator

## Arborescence principale
```text
src/main/java/com/tn/softsys/blocoperatoire
└── BlocOperatoireApplication.java

src/main/resources
└── application.properties
```

## Configuration fournie
Le fichier `application.properties` contient une base de configuration pour :
- serveur HTTP,
- datasource PostgreSQL,
- JPA/Hibernate,
- migrations Flyway,
- OAuth2 Resource Server (JWT issuer),
- endpoints de supervision Actuator,
- paramètres de journalisation,
- clés de configuration applicative (audit, conformité, interop FHIR).

## Démarrage local
### 1) Pré-requis
- JDK 21+
- Maven 3.9+
- PostgreSQL disponible localement

### 2) Ajuster la configuration
Mettre à jour au minimum :
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.security.oauth2.resourceserver.jwt.issuer-uri`
- `app.interop.fhir.base-url`

### 3) Lancer l'application
```bash
mvn spring-boot:run
```

## Prochaines étapes recommandées
- Ajouter des profils (`dev`, `test`, `prod`) avec variables d'environnement.
- Mettre en place une stratégie de secrets management.
- Initialiser les scripts Flyway dans `src/main/resources/db/migration`.
- Ajouter les modules métier (patients, pré-anesthésie, bloc, SSPI, réanimation, morgue, BI).
