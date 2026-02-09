# Bloc Opératoire – Backend Core

Backend **Spring Boot** constituant le **socle technique, réglementaire et architectural** d’une plateforme internationale de gestion intégrée du bloc opératoire, conforme aux exigences cliniques, médico-légales et industrielles définies dans le cahier des charges.

---

## Rôle du backend dans le projet global

Ce backend est le **cœur souverain du système**.  
Il porte :

- la logique métier médicale,
- la traçabilité légale non altérable,
- la sécurité des accès et des données,
- l’interopérabilité internationale (FHIR),
- l’exposition d’API REST normalisées vers :
  - Frontend clinique,
  - Moteurs IA,
  - Systèmes BI,
  - SI hospitaliers tiers.

Il est conçu pour un **déploiement multi-établissements, multi-pays, multi-langues**, avec auditabilité complète.


---

## Alignement cahier des charges

Le backend est conçu pour supporter nativement :

- Parcours patient péri-opératoire complet (pré-anesthésie → bloc → SSPI → réanimation → décès/morgue)
- Calculs de scores médicaux validés (ASA, SOFA, APACHE II, Aldrete…)
- Checklists OMS numériques opposables
- Traçabilité médico-légale horodatée
- IA assistive contrôlée (dictée, planification, qualité)
- Business Intelligence et pilotage stratégique
- Conformité ISO / RGPD / HIPAA / SaMD

---

## Stack technique

- **Java 21**
- **Spring Boot 3.3.x**
- Spring Web (API REST)
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server (JWT)
- Flyway (migrations versionnées)
- PostgreSQL
- Spring Boot Actuator (supervision)
- Support natif FHIR (interopérabilité)

Stack choisie pour :
- robustesse industrielle,
- maintenabilité long terme,
- conformité réglementaire,
- scalabilité internationale.

---

## Architecture logique cible (backend)

- API REST sécurisée (stateless)
- RBAC fin par rôle médical et administratif
- Journal d’événements non modifiable
- Séparation stricte :
  - domaine clinique,
  - sécurité,
  - audit,
  - interopérabilité,
  - BI / IA.

---

## Arborescence actuelle

```text
src/main/java/com/tn/softsys/blocoperatoire
└── BlocOperatoireApplication.java   # Point d’entrée Spring Boot

src/main/resources
└── application.properties           # Configuration centrale
