import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

import { Produit } from '../model/produit';
import { ProduitCreateDTO } from '../dto/produit-create.dto';
import { NotificationService } from './notification.service';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private apiUrl = 'http://localhost:8080/api/produits';

  private productsSubject = new BehaviorSubject<Produit[]>([]);
  products$ = this.productsSubject.asObservable();

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService
  ) { }

  // --- Méthodes pour les appels API ---

  getAllProduits(): Observable<Produit[]> {
    return this.http.get<Produit[]>(this.apiUrl)
      .pipe(
        tap(data => {
          console.log('Fetch all products:', data);
          this.productsSubject.next(data);
        }),
        catchError((error: HttpErrorResponse) => {
          const errorMessage = this.handleError(error);
          this.notificationService.showError(errorMessage.message || 'Erreur lors du chargement des produits.');
          console.log('Notification d\'erreur déclenchée pour getAllProduits');
          return throwError(() => new Error(errorMessage.message));
        })
      );
  }

  getProduitById(id: number): Observable<Produit> {
    return this.http.get<Produit>(`${this.apiUrl}/${id}`)
      .pipe(
        tap(data => console.log(`Fetch product with ID ${id}:`, data)),
        catchError((error: HttpErrorResponse) => {
          const errorMessage = this.handleError(error);
          this.notificationService.showError(errorMessage.message || `Erreur lors du chargement du produit ${id}.`);
          console.log(`Notification d'erreur déclenchée pour getProduitById(${id})`);
          return throwError(() => new Error(errorMessage.message));
        })
      );
  }

  createProduit(produitData: ProduitCreateDTO): Observable<Produit> {
    const formData = this.prepareFormData(produitData);
    return this.http.post<Produit>(this.apiUrl, formData)
      .pipe(
        tap(createdProduit => {
          console.log('Product created:', createdProduit);
          this.notificationService.showSuccess(`Produit "${createdProduit.nom}" créé avec succès !`);
          console.log('Notification de succès déclenchée depuis createProduit');

          // Recharger la liste complète après création
          this.getAllProduits().subscribe();
        }),
        catchError((error: HttpErrorResponse) => {
          const errorMessage = this.handleError(error);
          this.notificationService.showError(errorMessage.message || 'Échec de la création du produit.');
          console.log('Notification d\'erreur déclenchée depuis createProduit');
          return throwError(() => new Error(errorMessage.message));
        })
      );
  }

  // NOUVELLE MÉTHODE : Mise à jour avec support des fichiers
  updateProduitWithFile(id: number, produit: Produit, file?: File): Observable<Produit> {
    const formData = this.prepareUpdateFormData(produit, file);
    
    return this.http.put<Produit>(`${this.apiUrl}/${id}`, formData)
      .pipe(
        tap(updatedProduit => {
          console.log('Product updated:', updatedProduit);
          this.notificationService.showSuccess(`Produit "${updatedProduit.nom}" mis à jour avec succès !`);
          console.log('Notification de succès déclenchée depuis updateProduit');

          // Mettre à jour la liste locale
          const currentProducts = this.productsSubject.getValue();
          const updatedProducts = currentProducts.map(p => p.id === id ? updatedProduit : p);
          this.productsSubject.next(updatedProducts);
        }),
        catchError((error: HttpErrorResponse) => {
          const errorMessage = this.handleError(error);
          this.notificationService.showError(errorMessage.message || `Erreur lors de la mise à jour du produit ${id}.`);
          console.log(`Notification d'erreur déclenchée depuis updateProduit(${id})`);
          return throwError(() => new Error(errorMessage.message));
        })
      );
  }

  // MÉTHODE EXISTANTE : Gardée pour compatibilité (sans fichier)
  updateProduit(id: number, produit: Produit): Observable<Produit> {
    return this.updateProduitWithFile(id, produit);
  }

  deleteProduit(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
      .pipe(
        tap(() => {
          console.log('Product deleted successfully, ID:', id);
          this.notificationService.showSuccess(`Produit avec l'ID ${id} supprimé avec succès !`);
          console.log('Notification de succès déclenchée depuis deleteProduit');

          // Mettre à jour la liste locale
          const currentProducts = this.productsSubject.getValue();
          const updatedProducts = currentProducts.filter(p => p.id !== id);
          this.productsSubject.next(updatedProducts);
        }),
        catchError((error: HttpErrorResponse) => {
          const errorMessage = this.handleError(error);
          this.notificationService.showError(errorMessage.message || `Erreur lors de la suppression du produit avec l'ID ${id}.`);
          console.log(`Notification d'erreur déclenchée depuis deleteProduit(${id})`);
          return throwError(() => new Error(errorMessage.message));
        })
      );
  }

  // Préparation des données pour la création
  private prepareFormData(produitData: ProduitCreateDTO): FormData {
    const formData = new FormData();

    if (produitData.file) {
      formData.append('file', produitData.file, produitData.file.name);
    }

    formData.append('nom', produitData.nom);
    formData.append('prix', produitData.prix!.toString());
    formData.append('categorie', produitData.categorie);

    if (produitData.reference) {
      formData.append('reference', produitData.reference);
    }
    if (produitData.matricule) {
      formData.append('matricule', produitData.matricule);
    }
    if (produitData.dateExpiration) {
      formData.append('dateExpiration', produitData.dateExpiration);
    }

    return formData;
  }

  // NOUVELLE MÉTHODE : Préparation des données pour la mise à jour
  private prepareUpdateFormData(produit: Produit, file?: File): FormData {
    const formData = new FormData();

    // Ajouter le fichier s'il y en a un nouveau
    if (file) {
      formData.append('file', file, file.name);
    } else if (produit.fileName) {
      // Garder l'ancien nom de fichier si pas de nouveau fichier
      formData.append('fileName', produit.fileName);
    }

    // Ajouter tous les champs du produit
    formData.append('nom', produit.nom);
    formData.append('prix', produit.prix.toString());
    formData.append('categorie', produit.categorie);

    // Ajouter les champs conditionnels
    if (produit.reference) {
      formData.append('reference', produit.reference);
    } else {
      formData.append('reference', '');
    }

    if (produit.matricule) {
      formData.append('matricule', produit.matricule);
    } else {
      formData.append('matricule', '');
    }

    if (produit.dateExpiration) {
      formData.append('dateExpiration', produit.dateExpiration.toString());
    } else {
      formData.append('dateExpiration', '');
    }

    return formData;
  }

  private handleError(error: HttpErrorResponse): { message: string } {
    let errorMessage = '';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Erreur: ${error.error.message}`;
    } else {
      if (error.error && typeof error.error === 'object' && error.error.message) {
        errorMessage = `Erreur ${error.status} : ${error.error.message}`;
      } else {
        errorMessage = `Erreur serveur: ${error.status} - ${error.statusText}`;
      }
    }
    return { message: errorMessage };
  }
}