<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&height=150&section=header&text=Gestion%20de%20Produits%20🚀&fontSize=40&fontColor=ffffff&animation=fadeIn" alt="banner"/>
</p>

<p align="center">
  <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Backend-Spring%20Boot-green" /></a>
  <a href="https://angular.io/"><img src="https://img.shields.io/badge/Frontend-Angular-red" /></a>
  <a href="https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html"><img src="https://img.shields.io/badge/Java-17-blue" /></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-lightgrey" /></a>
</p>

# Projet de Gestion de Produits

## 📝 Présentation

Cette application web full-stack, développée avec **Angular** pour le frontend et **Spring Boot** pour le backend, permet de gérer une liste de produits. Elle inclut des fonctionnalités clés comme :
*   CRUD complet des produits.
*   Validations de champs conditionnelles basées sur la catégorie.
*   Upload de fichiers associés aux produits.
*   Notifications par email pour les actions sur les produits.
*   Tâches planifiées pour  l'envoi d'un récapitulatif quotidien.

## 🚀 Fonctionnalités Implémentées

*   **Backend (Java / Spring Boot) :**
    *   API REST pour le CRUD des produits (`/api/produits`).
    *   Validation dynamique des champs obligatoires selon la catégorie (Informatique, Véhicule, Alimentaire).
    *   Gestion de l'upload de fichiers pour les produits.
    *   Envoi d'emails de confirmation à la création/modification d'un produit.
    *   Tâches planifiées : suppression des produits périmés (tous les jours à minuit) et envoi d'un email récapitulatif quotidien (tous les jours à 8h).
*   **Frontend (Angular) :**
    *   Affichage réactif de la liste des produits.
    *   Formulaire réactif pour créer et modifier des produits.
    *   Affichage dynamique des champs requis selon la catégorie sélectionnée.
    *   Upload de fichier avec nom du fichier affiché et prévisualisation pour les images.
    *   Notifications visuelles (succès) pour les opérations.

## 🛠️ Installation et Exécution

Ce projet se compose d'un backend (Spring Boot) et d'un frontend (Angular).

### Prérequis

*   **Backend :**
    *   Java Development Kit (JDK) 17 ou supérieur.
    *   Maven.
    *   Base de données H2 (fournie avec le projet, en mémoire).
*   **Frontend :**
    *   Node.js et npm (ou yarn).
    *   Angular CLI : `npm install -g @angular/cli`

### Backend (Spring Boot)

1.  **Récupérer le code :** Clonez le dépôt ou extrayez le code source du backend.
2.  **Configurer les propriétés :**
    *   Ouvrez le fichier `src/main/resources/application.properties`.
    *   Configurez les paramètres d'envoi d'e-mails (`spring.mail.*`) pour Gmail vous pourriez avoir besoin d'un mot de passe d'application.
3.  **Lancer l'application :**
    *   **Via IDE :** Exécutez la classe principale `ProductManagerBackendApplication.java`.
    *   **Via Maven :** Depuis la racine du répertoire backend, lancez la commande : `mvn spring-boot:run`.
4.  **Tester l'API :** Utilisez un outil comme Postman pour faire des requêtes aux endpoints `/api/produits` (GET, POST, PUT, DELETE). Vous pouvez tester l'upload de fichier avec une requête `POST` à `/api/produits/{id}/upload` (pour le test) ou en utilisant le formulaire frontend.

### Frontend (Angular)

1.  **Récupérer le code :** Clonez le dépôt ou extrayez le code source du frontend.
2.  **Naviguer dans le répertoire frontend :** Ouvrez un terminal et déplacez-vous dans le répertoire du projet Angular :
    ```bash
    cd product-manager-frontend
    ```
3.  **Installer les dépendances :**
    ```bash
    npm install
    ```
4.  **Lancer l'application Angular :**
    ```bash
    ng serve --open
    ```
    L'application s'ouvrira automatiquement dans votre navigateur à `http://localhost:4200/`.
5.  **Utilisation :**
    *   Naviguez vers `/products` pour voir la liste des produits.
    *   Cliquez sur "Nouveau Produit" pour accéder au formulaire de création.
    *   Utilisez le formulaire pour créer, modifier des produits, y compris l'upload de fichiers.
    *   Vérifiez les notifications d'action (succès) qui apparaissent en haut à droite.

### <a name="explication-des-validations-dynamiques"></a>4. Explication des validations dynamiques

La validation dynamique des champs selon la catégorie est implémentée de la manière suivante :

*   **Backend (Spring Boot) :**
    *   Dans l'entité `Produit`, tous les champs potentiellement requis (`reference`, `matricule`, `dateExpiration`) sont inclus.
    *   Dans le `ProduitService`, la méthode `validateProduit()` contient une logique `switch` sur le champ `categorie`. Selon la catégorie, elle vérifie si les champs spécifiques sont présents et valides (non nuls, non vides) en utilisant `StringUtils.hasText()` ou en vérifiant si la date est non nulle. Si une validation échoue, elle lève une `IllegalArgumentException`.
*   **Frontend (Angular) :**
    *   Dans le `ProductFormComponent`, nous utilisons des formulaires réactifs (`FormGroup`, `FormControl`).
    *   Le composant maintient une carte (`CATEGORY_FIELDS`) qui mappe chaque catégorie aux noms des champs qui doivent être requis pour cette catégorie.
    *   La méthode `setupConditionalValidators()` s'abonne aux changements de la valeur du champ `categorie`.
    *   La méthode `updateConditionalValidators(category)` est appelée lors du changement de catégorie (ou lors du chargement initial des données). Elle réinitialise d'abord tous les validateurs conditionnels, puis ajoute `Validators.required` aux champs spécifiés pour la catégorie sélectionnée.
    *   Le template HTML utilise `*ngIf="isFieldVisible('fieldName')"` pour afficher conditionnellement les champs concernés, et `[ngClass]="{'is-invalid': ...}"` avec les messages `invalid-feedback` pour montrer les erreurs de validation si les champs requis ne sont pas remplis.

### <a name="gestion-des-fichiers-uploades"></a>5. Gestion des fichiers uploadés

*   **Backend (Spring Boot) :**
    *   **Dépendances :** `spring-boot-starter-web` inclut les dépendances nécessaires pour gérer les requêtes `multipart/form-data`.
    *   **Configuration :** Le répertoire de stockage est défini via la propriété `app.upload-dir` dans `application.properties`. Spring Boot crée ce répertoire s'il n'existe pas.
    *   **Entité `Produit` :** Contient le champ `fileName` pour stocker le nom du fichier.
    *   **`ProduitService` :**
        *   La méthode `saveFile(MultipartFile file)` prend le fichier uploadé, génère un nom de fichier unique (UUID + extension originale), et le sauvegarde dans le répertoire `uploadDir` en utilisant `java.nio.file.Files`.
        *   Le nom du fichier généré est ensuite associé à l'entité `Produit`.
        *   La méthode `deleteFile()` est appelée lors de la suppression d'un produit pour supprimer le fichier du système de fichiers.
    *   **`ProduitController` :**
        *   Le endpoint `POST /api/produits` est configuré pour accepter les requêtes `multipart/form-data`.
        *   Il prend un `ProduitCreateDTO` comme modèle d'argument, qui inclut un champ `MultipartFile file`.
        *   Il appelle le service pour sauvegarder le fichier et créer le produit.
*   **Frontend (Angular) :**
    *   Le `ProductFormComponent` utilise un `<input type="file">`.
    *   L'événement `(change)="onFileSelected($event)"` est utilisé pour capturer le fichier sélectionné.
    *   La méthode `onFileSelected()` :
        *   Récupère le `File` sélectionné.
        *   Stocke le `File` dans le contrôle `productForm.get('file')`.
        *   Affiche le nom du fichier sélectionné (`selectedFileName`).
        *   Pour les images, elle utilise `FileReader` pour générer une URL de prévisualisation (`imageUrl`) et l'afficher dans le template.
    *   Lors de la soumission du formulaire (`onSubmit()`), les valeurs du `productForm` (y compris le `File` dans `formValues.file`) sont regroupées dans un objet `produitCreateDTO`.
    *   Ce `produitCreateDTO` est ensuite passé à `productService.createProduit()`. Le service est responsable de la conversion en `FormData` pour l'envoi HTTP.

### <a name="configuration-des-emails-et-taches-planifiees"></a>6. Configuration des emails et tâches planifiées

*   **Emails :**
    *   **Configuration SMTP :** Les détails du serveur SMTP (host, port, username, password, starttls/ssl) sont configurés dans `application.properties` (ou `application.yml`) du backend. Il est crucial d'utiliser des identifiants valides et fonctionnels (ex: mot de passe d'application pour Gmail).
    *   **Envoi d'emails :**
        *   Dans le backend : Le `EmailService` contient les méthodes pour envoyer des emails simples (`sendSimpleMessage`) et HTML (`sendHtmlMessage`). Le `ProductService` appelle ces méthodes après la création/modification d'un produit.
        *   Frontend : Le frontend ne gère pas l'envoi direct des emails, mais il affiche des notifications pour confirmer que l'action (qui déclenche l'email) a été effectuée.
    *   **Alertes critiques :** le code est prévu pour pouvoir capturer les exceptions dans les services et appeler une méthode d'alerte qui enverrait un email à l'administrateur.
*   **Tâches planifiées (Cron Jobs) :**
    *   Dans le backend, la classe `ProductScheduler` utilise l'annotation `@Scheduled` avec des expressions CRON.
    *   `archiveOrDeleteExpiredProducts()` est planifiée pour s'exécuter tous les jours à minuit (`0 0 0 * * ?`). Elle trouve les produits périmés via `ProductService.getProduitsPerimes()` et les supprime.
    *   `sendDailySummaryEmail()` est planifiée pour s'exécuter tous les jours à 8h du matin (`0 0 8 * * ?`). Elle récupère les produits ajoutés récemment (simulé pour l'instant) et les produits périmés, puis envoie un email récapitulatif.
    *   L'annotation `@EnableScheduling` doit être présente sur la classe principale de l'application Spring Boot pour activer ces tâches.

---


