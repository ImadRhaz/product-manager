import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl, FormArray } from '@angular/forms';
import { ActivatedRoute, Router, ParamMap } from '@angular/router';
import { Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { Produit } from '../../model/produit';
import { ProductService } from '../../services/product.service';
import { ProduitCreateDTO } from '../../dto/produit-create.dto';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.css']
})
export class ProductFormComponent implements OnInit, OnDestroy {
  productForm!: FormGroup;
  produitId: number | null = null;
  isEditMode = false;
  private routeSub!: Subscription;
  error: string | null = null;
  selectedFileName: string | null = null;
  imageUrl: string | null = null;
  hasNewFile: boolean = false;

  readonly CATEGORY_FIELDS: { [key: string]: string[] } = {
    Informatique: ['reference'],
    Véhicule: ['matricule'],
    Alimentaire: ['dateExpiration']
  };

  // RENDRE PUBLIC pour l'utiliser dans le template
  currentProduct: Produit | null = null;

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.initForm();
    this.checkEditMode();
  }

  ngOnDestroy(): void {
    if (this.routeSub) {
      this.routeSub.unsubscribe();
    }
  }

  private initForm(): void {
    this.productForm = this.fb.group({
      nom: ['', [Validators.required, Validators.maxLength(100)]],
      prix: [null, [Validators.required, Validators.min(0.01)]],
      categorie: ['', [Validators.required]],
      reference: [''],
      matricule: [''],
      dateExpiration: [''],
      file: [null]
    });
    this.setupConditionalValidators();
  }

  private checkEditMode(): void {
    this.routeSub = this.route.paramMap.subscribe((params: ParamMap) => {
      const id = params.get('id');
      if (id) {
        this.produitId = +id;
        this.isEditMode = true;
        this.loadProduct(this.produitId);
      } else {
        this.isEditMode = false;
        this.hasNewFile = false;
        this.updateConditionalValidators(this.productForm.get('categorie')?.value);
      }
    });
  }

  private loadProduct(id: number): void {
    this.productService.getProduitById(id).subscribe({
      next: (produit) => {
        this.currentProduct = produit;
        this.productForm.patchValue({
          nom: produit.nom,
          prix: produit.prix,
          categorie: produit.categorie,
          reference: produit.reference,
          matricule: produit.matricule,
          dateExpiration: produit.dateExpiration,
        });
        this.selectedFileName = produit.fileName;
        this.hasNewFile = false;
        this.updateConditionalValidators(produit.categorie);
        
        if (produit.fileName) {
          this.loadExistingImage(produit.fileName);
        }
      },
      error: (err) => {
        console.error('Erreur lors du chargement du produit pour édition:', err);
        this.error = err.error?.message || 'Erreur lors du chargement du produit.';
      }
    });
  }

  private loadExistingImage(fileName: string): void {
    this.imageUrl = `http://localhost:8080/api/produits/files/${fileName}`;
  }

  private setupConditionalValidators(): void {
    this.productForm.get('categorie')?.valueChanges.subscribe((selectedCategory: string | null) => {
      this.onCategoryChange(selectedCategory);
    });
  }

  onCategoryChange(selectedCategory: string | null): void {
    this.updateConditionalValidators(selectedCategory);
  }

  private updateConditionalValidators(category: string | null): void {
    this.resetConditionalFieldValidators();

    if (category && this.CATEGORY_FIELDS[category]) {
      const requiredFields = this.CATEGORY_FIELDS[category];
      requiredFields.forEach(field => {
        const control = this.productForm.get(field);
        if (control) {
          control.setValidators([Validators.required]);
        }
      });
    }
    this.productForm.updateValueAndValidity();
  }

  private resetConditionalFieldValidators(): void {
    Object.keys(this.CATEGORY_FIELDS).forEach(category => {
      this.CATEGORY_FIELDS[category].forEach(field => {
        const control = this.productForm.get(field);
        if (control) {
          control.clearValidators();
          control.updateValueAndValidity();
        }
      });
    });
  }

  isFieldVisible(fieldName: string): boolean {
    const selectedCategory = this.productForm.get('categorie')?.value;
    
    switch (fieldName) {
      case 'reference':
        return selectedCategory === 'Informatique';
      case 'matricule':
        return selectedCategory === 'Véhicule';
      case 'dateExpiration':
        return selectedCategory === 'Alimentaire';
      default:
        return false;
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      this.productForm.get('file')?.setValue(file);
      this.selectedFileName = file.name;
      this.hasNewFile = true;

      if (file.type.startsWith('image/')) {
        const reader = new FileReader();
        reader.onload = (e: ProgressEvent<FileReader>) => {
          this.imageUrl = e.target?.result as string;
        };
        reader.readAsDataURL(file);
      } else {
        this.imageUrl = null;
      }
    } else {
      this.productForm.get('file')?.setValue(null);
      this.selectedFileName = null;
      this.imageUrl = null;
      this.hasNewFile = false;
    }
  }

  removeFile(): void {
    this.productForm.get('file')?.setValue(null);
    this.selectedFileName = null;
    this.imageUrl = null;
    this.hasNewFile = false;
    
    if (this.isEditMode && this.currentProduct?.fileName) {
      this.loadExistingImage(this.currentProduct.fileName);
    }
  }

  onSubmit(): void {
    if (this.productForm.valid) {
      const formValues = this.productForm.value;

      if (this.isEditMode && this.produitId !== null) {
        const produitAModifier: Produit = {
          id: this.produitId,
          nom: formValues.nom,
          prix: formValues.prix,
          categorie: formValues.categorie,
          reference: formValues.reference,
          matricule: formValues.matricule,
          dateExpiration: formValues.dateExpiration,
          fileName: this.currentProduct?.fileName || null
        };

        const newFile = formValues.file;

        this.productService.updateProduitWithFile(this.produitId, produitAModifier, newFile).subscribe({
          next: () => {
            console.log('Produit mis à jour.');
            this.router.navigate(['/products']);
          },
          error: (err) => {
            console.error('Erreur lors de la mise à jour:', err);
            this.error = err.error?.message || 'Erreur lors de la mise à jour du produit.';
          }
        });

      } else {
        const produitCreateDTO: ProduitCreateDTO = {
          nom: formValues.nom,
          prix: formValues.prix,
          categorie: formValues.categorie,
          reference: formValues.reference,
          matricule: formValues.matricule,
          dateExpiration: formValues.dateExpiration,
          file: formValues.file
        };

        this.productService.createProduit(produitCreateDTO).subscribe({
          next: () => {
            console.log('Produit créé avec succès.');
            this.router.navigate(['/products']);
          },
          error: (err) => {
            console.error('Erreur lors de la création:', err);
            this.error = err.error?.message || 'Erreur lors de la création du produit.';
          }
        });
      }
    } else {
      console.warn('Le formulaire n\'est pas valide. Veuillez vérifier les champs.');
      this.markFormGroupTouched(this.productForm);
    }
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      if (control instanceof FormControl) {
        control.markAsTouched();
      } else if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      } else if (control instanceof FormArray) {
        (control as FormArray).controls.forEach(item => {
          if (item instanceof FormGroup) {
            this.markFormGroupTouched(item);
          } else if (item instanceof FormControl) {
            item.markAsTouched();
          }
        });
      }
    });
  }

  getCurrentFileName(): string {
    if (this.hasNewFile && this.selectedFileName) {
      return `Nouveau fichier: ${this.selectedFileName}`;
    } else if (this.currentProduct?.fileName) {
      return `Fichier actuel: ${this.currentProduct.fileName}`;
    }
    return 'Aucun fichier sélectionné';
  }
}