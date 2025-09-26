package com.example.productmanager.dto;

import com.example.productmanager.model.Produit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public class ProduitCreateDTO {

    @NotBlank(message = "Le nom du produit est obligatoire.")
    @Size(max = 100, message = "Le nom du produit ne doit pas dépasser 100 caractères.")
    private String nom;

    @NotNull(message = "Le prix du produit est obligatoire.")
    @DecimalMin(value = "0.01", message = "Le prix doit être supérieur à zéro.")
    private Double prix;

    @NotBlank(message = "La catégorie du produit est obligatoire.")
    @Size(max = 50, message = "La catégorie ne doit pas dépasser 50 caractères.")
    private String categorie;

    private String reference;
    private String matricule;
    private LocalDate dateExpiration;

    // Le fichier uploadé
    private MultipartFile file;

   

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }

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

    public Produit toEntity() {
        Produit produit = new Produit();
        produit.setNom(this.nom);
        produit.setPrix(this.prix);
        produit.setCategorie(this.categorie);
        produit.setReference(this.reference);
        produit.setMatricule(this.matricule);
        produit.setDateExpiration(this.dateExpiration);
        return produit;
    }
}
