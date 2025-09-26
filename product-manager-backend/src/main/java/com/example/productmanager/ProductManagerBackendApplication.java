package com.example.productmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan; // Nécessaire si tes classes sont dans des packages différents
import org.springframework.scheduling.annotation.EnableScheduling; // Annotation clé pour les tâches planifiées

@SpringBootApplication
@EnableScheduling // Active la détection et l'exécution des tâches planifiées
// @ComponentScan(basePackages = {"com.example.productmanager"}) // Assure-toi que Spring scanne tous tes packages
public class ProductManagerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductManagerBackendApplication.class, args);
	}

}