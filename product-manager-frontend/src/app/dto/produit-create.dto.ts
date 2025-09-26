

export interface ProduitCreateDTO {
  // Propriétés obligatoires (sans '?' car elles sont dans les validations @NotBlank/@NotNull)
  nom: string;
  prix: number;
  categorie: string;

  // Propriétés optionnelles
  reference?: string | null;
  matricule?: string | null;
  dateExpiration?: string | null; // Laisser en string pour la compatibilité avec le backend

  // Le fichier uploadé
  file: File | null; // Utilise le type File natif d'Angular/JS
}