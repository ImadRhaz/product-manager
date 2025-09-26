package com.example.productmanager.controller;

import com.example.productmanager.dto.ProduitUpdateDTO;
import com.example.productmanager.model.Produit;
import com.example.productmanager.service.ProduitService;
import com.example.productmanager.dto.ProduitCreateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/produits")
@CrossOrigin(origins = "http://localhost:4200")
public class ProduitController {

    @Autowired
    private ProduitService produitService;

    // --- Endpoints CRUD ---

    /**
     * Récupère la liste de tous les produits.
     * @return ResponseEntity contenant la liste des produits ou un message d'erreur.
     */
    @GetMapping
    public ResponseEntity<Object> getAllProduits() {
        try {
            List<Produit> produits = produitService.getAllProduits();
            return ResponseEntity.ok(produits); // Succès : retourne la liste des produits
        } catch (Exception e) {
            // En cas d'erreur lors de la récupération, log l'erreur et retourne un message d'erreur générique.
            e.printStackTrace(); // Log pour débogage
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Erreur lors de la récupération des produits."));
        }
    }

    /**
     * Récupère un produit spécifique par son ID.
     * @param id L'ID du produit à rechercher.
     * @return ResponseEntity contenant le produit trouvé (200 OK) ou 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Produit> getProduitById(@PathVariable Long id) {
        return produitService.getProduitById(id)
                .map(produit -> ResponseEntity.ok(produit)) // Trouvé : retourne 200 OK avec le produit
                .orElse(ResponseEntity.notFound().build()); // Non trouvé : retourne 404 Not Found
    }

    /**
     * Crée un nouveau produit avec un fichier associé.
     * Utilise un DTO pour recevoir les données du formulaire (champs du produit + fichier).
     * @param produitCreateDTO Le DTO contenant les informations du produit et le fichier, validé automatiquement.
     * @return ResponseEntity avec le produit créé (201 Created) ou une réponse d'erreur (400/500).
     */
    @PostMapping
    public ResponseEntity<Object> createProduitWithFile(@Valid @ModelAttribute ProduitCreateDTO produitCreateDTO) {
        try {
            // Appel du service pour créer le produit et gérer l'upload du fichier
            Produit createdProduit = produitService.createProduit(produitCreateDTO);
            // TODO : Appeler le service d'email pour confirmation
            return new ResponseEntity<>(createdProduit, HttpStatus.CREATED); // Succès : 201 Created
        } catch (IllegalArgumentException e) {
            // Gère les erreurs de validation métier spécifiques (lancées par le service)
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage())); // 400 Bad Request avec message d'erreur
        } catch (IOException e) {
            // Gère les erreurs liées à l'upload du fichier
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Erreur lors de l'upload du fichier : " + e.getMessage())); // 500 Internal Server Error
        } catch (Exception e) {
            // Gère toutes les autres exceptions imprévues
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Une erreur interne est survenue : " + e.getMessage())); // 500 Internal Server Error
        }
    }


    // Dans ProduitController.java
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProduit(
            @PathVariable Long id,
            @Valid @ModelAttribute ProduitUpdateDTO produitUpdateDTO) {
        try {
            Produit updatedProduit = produitService.updateProduit(id, produitUpdateDTO);
            return ResponseEntity.ok(updatedProduit);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de l'upload du fichier : " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Une erreur interne est survenue : " + e.getMessage()));
        }
    }

    /**
     * Supprime un produit par son ID.
     * @param id L'ID du produit à supprimer.
     * @return ResponseEntity avec un statut 204 No Content en cas de succès, ou une erreur (404/500).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable Long id) {
        try {
            produitService.deleteProduit(id);
            // La suppression du fichier associé est gérée dans le service deleteProduit.
            return ResponseEntity.noContent().build(); // Succès : 204 No Content
        } catch (RuntimeException e) { // Gère le cas où le produit n'est pas trouvé
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (Exception e) {
            // Gère toutes les autres exceptions imprévues
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    // --- Classe d'aide pour les réponses d'erreur JSON ---
    // Cette classe est définie à l'intérieur du Controller pour simplifier.
    // Tu peux la déplacer dans un package dédié comme com.example.productmanager.dto ou com.example.productmanager.exception.
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        // Getters et Setters sont nécessaires pour que Jackson puisse sérialiser l'objet en JSON.
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}