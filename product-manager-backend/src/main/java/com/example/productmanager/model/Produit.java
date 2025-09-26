package com.example.productmanager.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity 
@Table(name = "produits") 
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class Produit {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable = false, length = 100) 
    private String nom;

    @Column(nullable = false)
    private double prix;

    @Column(nullable = false, length = 50)
    private String categorie; 

    private String reference;       
    private String matricule;      
    private LocalDate dateExpiration; 

    private String fileName;
}
