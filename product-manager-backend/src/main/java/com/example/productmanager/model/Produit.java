package com.example.productmanager.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity // Marque la classe comme une entité JPA gérée par Hibernate/JPA
@Table(name = "produits") // Nom de la table en base de données
@Data // Génère Getters, Setters, toString(), equals(), hashCode()
@NoArgsConstructor // Génère un constructeur sans arguments (requis par JPA)
@AllArgsConstructor // Génère un constructeur avec tous les arguments
public class Produit {

    @Id // Indique que ce champ est la clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Stratégie de génération de clé : auto-incrémentée
    private Long id;

    @Column(nullable = false, length = 100) // Champ non nul, longueur max 100 caractères
    private String nom;

    @Column(nullable = false)
    private double prix;

    @Column(nullable = false, length = 50)
    private String categorie; // Ex: "Informatique", "Véhicule", "Alimentaire"

    // Champs spécifiques à la catégorie (ils ne seront pas obligatoires par défaut au niveau de la base de données)
    private String reference;       // requis si catégorie = Informatique
    private String matricule;       // requis si catégorie = Véhicule
    private LocalDate dateExpiration; // requis si catégorie = Alimentaire

    // Champ pour stocker le nom du fichier uploadé. Le fichier lui-même sera stocké sur le système de fichiers.
    private String fileName;
}