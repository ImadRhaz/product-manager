# 🚀 Gestion de Produits – Application Full-Stack

[![Backend](https://img.shields.io/badge/Backend-Spring%20Boot-green)](https://spring.io/projects/spring-boot)
[![Frontend](https://img.shields.io/badge/Frontend-Angular-red)](https://angular.io/)
[![Java](https://img.shields.io/badge/Java-17-blue)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![License](https://img.shields.io/badge/License-MIT-lightgrey)](LICENSE)

---

## 📋 Table des matières
1. [Introduction](#-1-introduction)  
2. [Fonctionnalités](#-2-fonctionnalités)  
3. [Installation et exécution](#-3-installation-et-exécution)  
4. [Validations dynamiques](#-4-validations-dynamiques)  
5. [Gestion des fichiers uploadés](#-5-gestion-des-fichiers-uploadés)  
6. [Emails et tâches planifiées](#-6-emails-et-tâches-planifiées)  
7. [Architecture et diagramme](#-7-architecture-et-diagramme)  
8. [Livrables](#-8-livrables)  

---

## 💡 1. Introduction
Ce projet démontre la capacité à développer une application web complète :  

- **Backend** : Spring Boot, API REST sécurisée.  
- **Frontend** : Angular moderne et réactif.  
- Validation dynamique selon la catégorie du produit.  
- Upload et gestion de fichiers.  
- Notifications email et tâches planifiées automatisées.  

---

## ⚙️ 2. Fonctionnalités

### 🔹 Backend
- CRUD complet via API REST.  
- Validation conditionnelle des champs selon la catégorie.  
- Upload sécurisé de fichiers.  
- Notifications par email (création, modification).  
- Tâches planifiées :
  - Archiver/supprimer les produits périmés.  
  - Envoyer un récapitulatif quotidien.  

### 🔹 Frontend
- Liste interactive des produits.  
- Formulaire réactif pour création/modification.  
- Upload et prévisualisation des fichiers.  
- Notifications visuelles pour succès et erreurs.  

---

## 💻 3. Installation et exécution

### 🔧 Prérequis
- Java 17+, Maven, Node.js, npm, Angular CLI (`npm install -g @angular/cli`)  
- Postman ou équivalent pour tester l’API  

### ▶️ Backend
```bash
git clone <URL_DU_DEPOT>
cd product-manager-backend
mvn spring-boot:run
Base H2 par défaut. Pour persistance :

bash
Copy code
spring.datasource.url=jdbc:h2:file:./h2_data/productdb
📌 Endpoints API

POST /api/produits

GET /api/produits

GET /api/produits/{id}

PUT /api/produits/{id}

DELETE /api/produits/{id}

H2 Console : http://localhost:8080/h2-console (login: sa / password: password)

▶️ Frontend
bash
Copy code
cd product-manager-frontend
npm install
ng serve --open
✅ 4. Validations dynamiques
Backend : ProduitCreateDTO avec @NotBlank, @NotNull.

Frontend : FormGroup Angular avec Validators.required.

Le ProductFormComponent met à jour les validations automatiquement.

📂 5. Gestion des fichiers uploadés
🔹 Backend
Stockage dans uploads/ avec nom unique.

Suppression automatique lors de la suppression du produit.

🔹 Frontend
Sélection via <input type="file">.

Prévisualisation des images dans le formulaire.

Envoi via FormData.

📧 6. Emails et tâches planifiées
✉️ Emails

Config SMTP dans application.properties.

EmailService pour envoi HTML ou texte.

Emails automatiques à la création/modification.

⏰ Tâches planifiées

ProductScheduler.java avec @Scheduled.

Suppression des produits périmés.

Récapitulatif quotidien.

Nécessite @EnableScheduling dans la classe principale.

🏗️ 7. Architecture et diagramme

📊 Diagramme simplifié


Frontend Angular
┌─────────────────────┐
│ ProductFormComponent │
│ ProductsListComponent│
└──────────┬───────────┘
           │ HTTP Requests
           ▼
Backend Spring Boot
┌─────────────────────┐
│ ProductController   │
│ ProductService      │
│ EmailService        │
└──────────┬──────────┘
           │ JPA/Hibernate
           ▼
     Database H2

📁 Structure Backend



src/main/java/com/example/productmanager/
├─ model/           # Entités
├─ repository/      # Repositories
├─ service/         # Logique métier, EmailService
├─ controller/      # API REST
├─ dto/             # Data Transfer Objects
├─ scheduling/      # Tâches planifiées
└─ ProductManagerBackendApplication.java

📁 Structure Frontend

src/app/
├─ components/
│  ├─ product-form/
│  └─ products-list/
├─ services/
├─ model/
├─ dto/
├─ app.component.*
├─ app.routes.ts
└─ environments/

📦 8. Livrables
Code source complet sur GitHub/GitLab.

README détaillé et structuré.

Instructions d’installation et d’utilisation.

Backend et frontend pleinement fonctionnels.
