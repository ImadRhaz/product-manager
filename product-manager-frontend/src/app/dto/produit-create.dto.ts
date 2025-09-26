

export interface ProduitCreateDTO {
  
  nom: string;
  prix: number;
  categorie: string;


  reference?: string | null;
  matricule?: string | null;
  dateExpiration?: string | null; 

  
  file: File | null; 
}
