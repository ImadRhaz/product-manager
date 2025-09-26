package com.example.productmanager.repository;

import com.example.productmanager.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository // Indique que c'est un composant Spring de type Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    /**
     * Méthode personnalisée pour trouver les produits dont la date d'expiration est passée.
     * Spring Data JPA dérive automatiquement l'implémentation de cette méthode.
     * @param date La date limite pour vérifier l'expiration.
     * @return Une liste de produits expirés.
     */
    List<Produit> findByDateExpirationBefore(LocalDate date);
}