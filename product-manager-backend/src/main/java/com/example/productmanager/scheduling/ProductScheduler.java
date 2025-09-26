package com.example.productmanager.scheduling;

import com.example.productmanager.model.Produit;
import com.example.productmanager.service.ProduitService;
import com.example.productmanager.service.mail.EmailService; // Pour envoyer l'email récapitulatif
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException; // Pour les exceptions d'envoi d'email
import java.time.LocalDate;
import java.util.List;

@Component // Marque cette classe comme un composant Spring géré par le contexte
public class ProductScheduler {

    @Autowired
    private ProduitService produitService;

    @Autowired
    private EmailService emailService; // Service pour envoyer les emails

    /**
     * Tâche planifiée pour archiver ou supprimer les produits périmés.
     * Elle s'exécute tous les jours à 00:00 (minuit).
     */
    @Scheduled(cron = "0 * * * * ?") // Exécute toutes les minutes    // L'expression "0 0 0 * * ?" signifie : à la 0ème minute, 0ème heure, de chaque jour.
    public void archiveOrDeleteExpiredProducts() {
        System.out.println("Exécution de la tâche planifiée : Archivage/suppression des produits périmés.");

        // Récupérer la date actuelle
        LocalDate today = LocalDate.now();

        // Trouver les produits périmés
        List<Produit> expiredProducts = produitService.getProduitsPerimes(); // Utilise la méthode du service

        if (expiredProducts.isEmpty()) {
            System.out.println("Aucun produit périmé trouvé pour aujourd'hui.");
            return; // Sortir si aucun produit n'est périmé
        }

        System.out.println("Produits périmés trouvés : " + expiredProducts.size());

        // Pour chaque produit périmé, on le supprime (ou on le marque comme archivé)
        for (Produit produit : expiredProducts) {
            try {
                // Logique de suppression. On pourrait aussi avoir une logique d'archivage (ex: déplacer vers une autre table)
                produitService.deleteProduit(produit.getId());
                System.out.println("Produit périmé supprimé : ID=" + produit.getId() + ", Nom=" + produit.getNom());
            } catch (Exception e) {
                // Gérer l'erreur si la suppression échoue pour un produit particulier
                System.err.println("Erreur lors de la suppression du produit périmé ID=" + produit.getId() + " : " + e.getMessage());
                // On pourrait envoyer un email d'alerte ici si la suppression échoue de manière répétée
            }
        }
    }

    /**
     * Tâche planifiée pour envoyer un email récapitulatif quotidien.
     * Elle s'exécute tous les jours à 08:00 du matin.
     */
    @Scheduled(cron = "0 */2 * * * ?") // Exécute toutes les 2 minutes
    public void sendDailySummaryEmail() {
        System.out.println("Exécution de la tâche planifiée : Envoi de l'email récapitulatif quotidien.");

        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();

        // 1. Récupérer les produits ajoutés récemment (par exemple, ceux créés hier)
        // Pour cela, il faudrait ajouter une méthode dans ProduitRepository et ProduitService
        // Exemple : List<Produit> addedRecently = produitRepository.findByCreationDateAfter(yesterday);
        // Pour l'instant, on va simuler cette liste.
        List<Produit> addedRecently = getSimulatedAddedProducts(); // Simule les produits ajoutés hier

        // 2. Récupérer les produits périmés (ceux qui devraient être supprimés aujourd'hui)
        List<Produit> expiredProducts = produitService.getProduitsPerimes();

        // Construire le contenu de l'email récapitulatif
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("<html><body>");
        emailContent.append("<h1>Récapitulatif Quotidien des Produits</h1>");

        // Section : Produits ajoutés récemment
        if (!addedRecently.isEmpty()) {
            emailContent.append("<p><strong>Produits ajoutés le ").append(yesterday).append(" :</strong></p>");
            emailContent.append("<ul>");
            for (Produit produit : addedRecently) {
                emailContent.append("<li>").append(produit.getNom()).append(" (ID: ").append(produit.getId()).append(")</li>");
            }
            emailContent.append("</ul>");
        } else {
            emailContent.append("<p>Aucun nouveau produit n'a été ajouté hier.</p>");
        }

        // Section : Produits périmés aujourd'hui
        if (!expiredProducts.isEmpty()) {
            emailContent.append("<p><strong>Produits périmés aujourd'hui (").append(today).append(") :</strong></p>");
            emailContent.append("<ul>");
            for (Produit produit : expiredProducts) {
                emailContent.append("<li>").append(produit.getNom()).append(" (ID: ").append(produit.getId()).append(", Expire le: ").append(produit.getDateExpiration()).append(")</li>");
            }
            emailContent.append("</ul>");
        } else {
            emailContent.append("<p>Aucun produit n'est périmé aujourd'hui.</p>");
        }

        emailContent.append("<p>Cordialement,<br/>L'équipe de gestion des produits</p>");
        emailContent.append("</body></html>");

        // Envoyer l'email récapitulatif
        String recipientEmail = "imadoulfa11@gmail.com"; // REMPLACER CETTE ADRESSE
        String subject = "Récapitulatif Quotidien des Produits - " + today;

        try {
            emailService.sendHtmlMessage(recipientEmail, subject, emailContent.toString());
            System.out.println("Email récapitulatif quotidien envoyé à " + recipientEmail);
        } catch (MessagingException e) {
            System.err.println("Erreur lors de la construction du message pour l'email récapitulatif : " + e.getMessage());
            // Loguer cette erreur
        } catch (Exception e) {
            System.err.println("Erreur générale lors de l'envoi de l'email récapitulatif : " + e.getMessage());
            // Loguer cette erreur
        }
    }

    // Méthode simulée pour obtenir les produits ajoutés récemment (à remplacer par une vraie requête DB)
    private List<Produit> getSimulatedAddedProducts() {
        // Dans une vraie application, tu devrais ajouter une méthode dans ProduitRepository
        // pour récupérer les produits créés après une certaine date (ex: yesterday).
        // Pour l'instant, on simule quelques produits.
        return List.of(
                // new Produit(1L, "Clavier XYZ", 80.0, "Informatique", "KB-XYZ-123", null, null, "clavier_xyz.png"),
                // new Produit(2L, "Souris Gamer", 45.50, "Informatique", "MS-GAMER-002", null, null, "souris_gamer.jpg")
                // Note : Ces produits ne seront pas réellement chargés depuis la base de données avec ce code simulé.
                // Il faudrait une logique pour les récupérer de la DB.
                // Pour réellement tester cela, il faudrait adapter le ProduitRepository pour trouver les produits par date.
        );
    }
}