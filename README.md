# App Citoyenne

Application mobile Android native (Kotlin) de participation citoyenne, permettant aux citoyens de signaler des incidents urbains, de suivre leur traitement et de soutenir les signalements prioritaires. Développée dans le cadre du Master 1 MICDA (P8) — EC *Programmation native d'applications mobiles*, Université Numérique Cheikh Hamidou Kane.

## Fonctionnalités

**Espace citoyen**
- Création de compte et authentification (Firebase Authentication)
- Signalement d'un incident : photo, description, catégorie, niveau de priorité, géolocalisation automatique
- Historique personnel des signalements
- Vote pour soutenir les signalements prioritaires d'autres citoyens

**Espace administrateur**
- Consultation de l'ensemble des incidents remontés
- Modification du statut d'un signalement (Reçu → En cours → Résolu)
- Statistiques synthétiques en temps réel

**Fonctionnalités innovantes**
- Système de vote citoyen
- Carte temps réel des incidents (OpenStreetMap / osmdroid), marqueurs colorés par priorité

## Stack technique

| Composant | Choix |
|---|---|
| Langage | Kotlin |
| Architecture | MVVM |
| Interface | XML / ConstraintLayout |
| Persistance locale | Room Database |
| Réseau | Retrofit (API REST) |
| Authentification | Firebase Authentication |
| Cartographie | osmdroid (OpenStreetMap) |
| Test | Appareil physique — Tecno Spark 20 |

## Structure du projet

```
app/src/main/java/sn/uncgh/citoyen/
├── ui/auth/          → LoginActivity
├── ui/signalement/   → SignalementActivity, HistoriqueActivity, AdminActivity, TousSignalementsActivity
├── ui/carte/          → CarteActivity, CarteViewModel
├── data/local/        → Room (entités, DAO, AppDatabase)
├── data/remote/       → Retrofit (RetrofitInstance, API)
└── data/repository/   → IncidentRepository
```

## Installation

1. Cloner le dépôt : `git clone https://github.com/Thiernocodeur/app-citoyenne.git`
2. Ouvrir le projet dans Android Studio
3. Synchroniser Gradle
4. Lancer sur un appareil physique ou un émulateur (API 24+)

## Livrables du projet

- Cahier des charges
- Diagrammes UML (cas d'utilisation, classes, séquences) et diagramme d'architecture
- Maquettes
- Modèle de base de données
- Rapport technique
- Support de présentation orale

## Auteur

Thierno Barra Diallo — Master 1 MICDA (P8), Université Numérique Cheikh Hamidou Kane, 2025-2026