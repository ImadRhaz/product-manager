import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router'; // Router importé si utilisé ailleurs
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';

// Assure-toi que le chemin d'importation est correct
import { NotificationService, Notification } from './services/notification.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    CommonModule, // Nécessaire pour *ngIf
    // Assure-toi que HttpClientModule est fourni dans app.config.ts
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'product-manager-frontend';

  currentNotification: Notification | null = null;
  private notificationSubscription: Subscription | undefined;

  constructor(
    private notificationService: NotificationService // Injection du NotificationService
  ) { }

  ngOnInit(): void {
    // S'abonner au flux de notifications du service
    this.notificationSubscription = this.notificationService.notification$.subscribe((notification) => {
      this.currentNotification = notification; // Stocke la notification reçue pour l'affichage
      console.log('Notification reçue dans AppComponent:', notification); // Log pour confirmer la réception

      // Si une durée est définie, ferme la notification après ce délai
      if (notification.duration && notification.duration > 0) {
        setTimeout(() => {
          this.currentNotification = null; // Cache la notification
        }, notification.duration);
      }
    });
    console.log('AppComponent ngOnInit: Abonné au NotificationService pour les notifications.');
  }

  ngOnDestroy(): void {
    // Se désabonne de la souscription lors de la destruction du composant pour éviter les fuites mémoire
    if (this.notificationSubscription) {
      this.notificationSubscription.unsubscribe();
      console.log('AppComponent ngOnDestroy: Désabonné du NotificationService.');
    }
  }

  // Méthode optionnelle pour fermer manuellement une notification
  closeNotification(): void {
    this.currentNotification = null;
  }
}