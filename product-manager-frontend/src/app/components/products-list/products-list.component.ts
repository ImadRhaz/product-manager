import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Subscription, Observable, BehaviorSubject } from 'rxjs';
import { CommonModule } from '@angular/common';

import { Produit } from '../../model/produit';
import { ProductService } from '../../services/product.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-products-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
  ],
  templateUrl: './products-list.component.html',
  styleUrls: ['./products-list.component.css']
})
export class ProductListComponent implements OnInit, OnDestroy {
  produits$: Observable<Produit[]> | undefined;
  produitsSubject = new BehaviorSubject<Produit[]>([]);
  error: string | null = null;
  private produitsSubscription: Subscription | undefined;

  // Propriétés pour la pagination
  currentPage: number = 1;
  itemsPerPage: number = 5;
  totalItems: number = 0;

  // Exposer Math pour le template
  Math = Math;

  constructor(
    private productService: ProductService,
    private router: Router,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.loadProducts();
  }

  ngOnDestroy(): void {
    if (this.produitsSubscription) {
      this.produitsSubscription.unsubscribe();
    }
  }

  loadProducts(): void {
    this.produits$ = this.produitsSubject.asObservable();
    
    this.productService.getAllProduits().subscribe({
      next: (data) => {
        this.produitsSubject.next(data);
        this.totalItems = data.length;
      },
      error: (err) => {
        this.error = err.message || 'Erreur lors du chargement des produits.';
        console.error('Erreur capturée dans le composant pour le chargement des produits:', err);
      }
    });
  }

  // Méthodes pour la pagination
  get paginatedProduits(): Produit[] {
    const allProduits = this.produitsSubject.value;
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    return allProduits.slice(startIndex, startIndex + this.itemsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.totalItems / this.itemsPerPage);
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  deleteProduct(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) {
      this.productService.deleteProduit(id).subscribe({
        next: () => {
          console.log('Appel à deleteProduit réussi côté composant.');
          // Mettre à jour la liste localement sans recharger la page
          const currentProduits = this.produitsSubject.value;
          const updatedProduits = currentProduits.filter(p => p.id !== id);
          this.produitsSubject.next(updatedProduits);
          this.totalItems = updatedProduits.length;
          
          // Ajuster la page actuelle si nécessaire
          if (this.paginatedProduits.length === 0 && this.currentPage > 1) {
            this.currentPage--;
          }
          
          this.notificationService.showSuccess('Produit supprimé avec succès');
        },
        error: (err) => {
          console.error('Erreur capturée dans le composant pour la suppression:', err);
          this.error = 'Erreur lors de la suppression du produit.';
          this.notificationService.showError('Erreur lors de la suppression du produit');
        }
      });
    }
  }

  editProduct(id: number): void {
    this.router.navigate(['/products', id, 'edit']);
  }

  // Méthode pour obtenir la classe CSS du badge de catégorie
  getCategoryBadgeClass(category: string): string {
    const classes: { [key: string]: string } = {
      'Informatique': 'bg-primary-subtle',
      'Véhicule': 'bg-warning-subtle',
      'Alimentaire': 'bg-success-subtle'
    };
    return classes[category] || 'bg-secondary-subtle';
  }

  // Méthode pour calculer la valeur totale des produits
  getTotalValue(produits: Produit[]): number {
    return produits.reduce((total, produit) => total + produit.prix, 0);
  }
}