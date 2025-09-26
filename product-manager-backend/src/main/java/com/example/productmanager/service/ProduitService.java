package com.example.productmanager.service;
import com.example.productmanager.dto.ProduitUpdateDTO;

import com.example.productmanager.model.Produit;
import com.example.productmanager.repository.ProduitRepository;
import com.example.productmanager.dto.ProduitCreateDTO;
import com.example.productmanager.service.mail.EmailService; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException; 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProduitService {

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired // Injection du service d'email
    private EmailService emailService;

    @Value("${app.upload-dir:uploads/}")
    private String uploadDir;

    
    private void validateProduit(Produit produit) {
        if (produit.getNom() == null || produit.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit est obligatoire.");
        }
        if (produit.getPrix() <= 0) {
            throw new IllegalArgumentException("Le prix du produit doit être un nombre positif.");
        }
        if (produit.getCategorie() == null || produit.getCategorie().trim().isEmpty()) {
            throw new IllegalArgumentException("La catégorie du produit est obligatoire.");
        }

        switch (produit.getCategorie()) {
            case "Informatique":
                if (!StringUtils.hasText(produit.getReference())) {
                    throw new IllegalArgumentException("La référence est obligatoire pour la catégorie 'Informatique'.");
                }
                break;
            case "Véhicule":
                if (!StringUtils.hasText(produit.getMatricule())) {
                    throw new IllegalArgumentException("Le matricule est obligatoire pour la catégorie 'Véhicule'.");
                }
                break;
            case "Alimentaire":
                if (produit.getDateExpiration() == null) {
                    throw new IllegalArgumentException("La date d'expiration est obligatoire pour la catégorie 'Alimentaire'.");
                }
                break;
            default:
                break;
        }
    }

    public List<Produit> getAllProduits() {
        return produitRepository.findAll();
    }

    public Optional<Produit> getProduitById(Long id) {
        return produitRepository.findById(id);
    }

    /**
     * Crée un nouveau produit à partir d'un DTO, gérant aussi l'upload du fichier.
     * @param produitCreateDTO Le DTO contenant les informations du produit et le fichier.
     * @return Le produit créé.
     * @throws IOException si une erreur survient lors de l'upload du fichier.
     */
    public Produit createProduit(ProduitCreateDTO produitCreateDTO) throws IOException {
        Produit produit = produitCreateDTO.toEntity();

        String fileName = saveFile(produitCreateDTO.getFile());
        if (fileName != null) {
            produit.setFileName(fileName);
        }

        validateProduit(produit);

        Produit savedProduit = produitRepository.save(produit);

        // --- Envoi de l'email de confirmation ---
        sendConfirmationEmail(savedProduit, "création");

        return savedProduit;
    }

    // Dans ProduitService.java
    public Produit updateProduit(Long id, ProduitUpdateDTO produitUpdateDTO) throws IOException {
        Produit produitAModifier = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));

        // Mettre à jour les champs
        produitAModifier.setNom(produitUpdateDTO.getNom());
        produitAModifier.setPrix(produitUpdateDTO.getPrix());
        produitAModifier.setCategorie(produitUpdateDTO.getCategorie());
        produitAModifier.setReference(produitUpdateDTO.getReference());
        produitAModifier.setMatricule(produitUpdateDTO.getMatricule());
        produitAModifier.setDateExpiration(produitUpdateDTO.getDateExpiration());

        // Gérer le fichier
        if (produitUpdateDTO.getFile() != null && !produitUpdateDTO.getFile().isEmpty()) {
            // Supprimer l'ancien fichier s'il existe
            if (produitAModifier.getFileName() != null && !produitAModifier.getFileName().isEmpty()) {
                this.deleteFile(produitAModifier.getFileName());
            }
            // Sauvegarder le nouveau fichier
            String newFileName = saveFile(produitUpdateDTO.getFile());
            produitAModifier.setFileName(newFileName);
        } else if (produitUpdateDTO.getFileName() != null) {
            // Garder l'ancien nom de fichier
            produitAModifier.setFileName(produitUpdateDTO.getFileName());
        }

        validateProduit(produitAModifier);
        Produit produitMisAJour = produitRepository.save(produitAModifier);

        // Envoi de l'email de confirmation
        sendConfirmationEmail(produitMisAJour, "modification");

        return produitMisAJour;
    }
    public void deleteProduit(Long id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));

        if (produit.getFileName() != null && !produit.getFileName().isEmpty()) {
            this.deleteFile(produit.getFileName());
        }
        produitRepository.deleteById(id);
    }

    public List<Produit> getProduitsPerimes() {
        return produitRepository.findByDateExpirationBefore(LocalDate.now());
    }

    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(uniqueFileName);
        file.transferTo(filePath);
        return uniqueFileName;
    }

    private void deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }
        try {
            Path fileToDeletePath = Paths.get(uploadDir).resolve(fileName).normalize();
            if (Files.exists(fileToDeletePath)) {
                Files.delete(fileToDeletePath);
                System.out.println("Fichier supprimé : " + fileName);
            } else {
                System.out.println("Fichier non trouvé pour suppression : " + fileName);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la suppression du fichier " + fileName + ": " + e.getMessage());
        }
    }

    /**
     * Envoie un email de confirmation après une action sur un produit.
     * @param produit Le produit concerné.
     * @param action L'action effectuée ("création" ou "modification").
     */
    private void sendConfirmationEmail(Produit produit, String action) {
       
        String recipientEmail = "test_recipient@example.com";
        String subject = "Confirmation de " + action + " du produit : " + produit.getNom();

        // Construction du contenu HTML de l'email
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<html><body>");
        htmlContent.append("<h1>Confirmation de ").append(action).append("</h1>");
        htmlContent.append("<p>Le produit <strong>").append(produit.getNom()).append("</strong> (ID: ").append(produit.getId()).append(") a été ").append(action).append(" avec succès.</p>");
        htmlContent.append("<p>Détails :</p>");
        htmlContent.append("<ul>");
        htmlContent.append("<li>Nom: ").append(produit.getNom()).append("</li>");
        htmlContent.append("<li>Prix: ").append(produit.getPrix()).append("</li>");
        htmlContent.append("<li>Catégorie: ").append(produit.getCategorie()).append("</li>");

        // Ajout des champs conditionnels s'ils sont présents
        if (produit.getCategorie().equals("Informatique") && produit.getReference() != null) {
            htmlContent.append("<li>Référence: ").append(produit.getReference()).append("</li>");
        } else if (produit.getCategorie().equals("Véhicule") && produit.getMatricule() != null) {
            htmlContent.append("<li>Matricule: ").append(produit.getMatricule()).append("</li>");
        } else if (produit.getCategorie().equals("Alimentaire") && produit.getDateExpiration() != null) {
            htmlContent.append("<li>Date d'expiration: ").append(produit.getDateExpiration()).append("</li>");
        }

        if (produit.getFileName() != null) {
            htmlContent.append("<li>Fichier joint: ").append(produit.getFileName()).append("</li>");
        }
        htmlContent.append("</ul>");
        htmlContent.append("<p>Cordialement,<br/>L'équipe de gestion des produits</p>");
        htmlContent.append("</body></html>");

        try {
            // Appel du service d'email pour envoyer le message HTML
            emailService.sendHtmlMessage(recipientEmail, subject, htmlContent.toString());
            System.out.println("Tentative d'envoi d'email de confirmation pour le produit ID " + produit.getId() + " (action: " + action + ")");
        } catch (MessagingException e) {
            System.err.println("Erreur lors de la construction du message pour l'email de confirmation pour le produit ID " + produit.getId() + ": " + e.getMessage());
            // Loguer cette erreur plus formellement
        } catch (Exception e) {
            System.err.println("Erreur générale lors de l'envoi de l'email de confirmation pour le produit ID " + produit.getId() + ": " + e.getMessage());
            // Loguer cette erreur
        }
    }
}
