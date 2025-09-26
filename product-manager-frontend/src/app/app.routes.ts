import { Routes } from '@angular/router';
// Importe tes composants
import { ProductListComponent } from './components/products-list/products-list.component'; // Assure-toi que le chemin est correct
import { ProductFormComponent } from './components/product-form/product-form.component'; // Assure-toi que le chemin est correct

export const routes: Routes = [
  { path: '', redirectTo: '/products', pathMatch: 'full' }, // Redirection vers la liste par défaut
  { path: 'products', component: ProductListComponent }, // Route pour afficher la liste des produits
  { path: 'products/new', component: ProductFormComponent }, // Route pour créer un nouveau produit
  { path: 'products/:id/edit', component: ProductFormComponent } // Route pour modifier un produit (recevra un ID)
];