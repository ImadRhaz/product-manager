// ProduitUpdateDTO.java
package com.example.productmanager.dto;

import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

public class ProduitUpdateDTO {
    private String nom;
    private double prix;
    private String categorie;
    private String reference;
    private String matricule;
    private LocalDate dateExpiration;
    private MultipartFile file;
    private String fileName; // Pour garder l'ancien fichier si pas de nouveau

    // Constructeurs, getters et setters
    public ProduitUpdateDTO() {}

    // Getters et setters pour tous les champs
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }

    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
}