package com.example.productmanager.repository;

import com.example.productmanager.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository 
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    
    List<Produit> findByDateExpirationBefore(LocalDate date);
}
