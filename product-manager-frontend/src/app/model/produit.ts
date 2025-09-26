// src/app/model/produit.ts

export interface Produit {
  id: number | null;
  nom: string;
  prix: number;
  categorie: string;
  reference?: string | null;
  matricule?: string | null;
  dateExpiration?: string | null;

  // Ajoute la propriété fileName ici
  fileName: string | null;
}