import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';
import { filter, tap } from 'rxjs/operators'; // tap est optionnel ici pour le logging, mais peut être utile

// Définir un type pour nos notifications
export interface Notification {
  message: string;
  type: 'success' | 'error' | 'info'; // Types de notifications possibles
  duration?: number; // Durée optionnelle en ms avant fermeture automatique
}

@Injectable({
  providedIn: 'root' // Rend ce service disponible globalement dans l'application
})
export class NotificationService {

  // Un Subject pour émettre les notifications. Les composants s'y abonneront.
  private notificationSubject = new Subject<Notification>();
  // Un observable pour écouter les notifications émises
  notification$ = this.notificationSubject.asObservable();

  constructor() { }

  /**
   * Affiche une notification de succès.
   * @param message Le message à afficher.
   * @param duration La durée d'affichage en millisecondes (par défaut 3000ms).
   */
  showSuccess(message: string, duration: number = 3000): void {
    this.notificationSubject.next({ message, type: 'success', duration });
    console.log(`Notification (success): ${message}`); // Log pour vérification
  }

  /**
   * Affiche une notification d'erreur.
   * @param message Le message d'erreur à afficher.
   * @param duration La durée d'affichage en millisecondes (par défaut 5000ms).
   */
  showError(message: string, duration: number = 5000): void {
    this.notificationSubject.next({ message, type: 'error', duration });
    console.log(`Notification (error): ${message}`); // Log pour vérification
  }

  /**
   * Affiche une notification d'information.
   * @param message Le message d'information à afficher.
   * @param duration La durée d'affichage en millisecondes (par défaut 3000ms).
   */
  showInfo(message: string, duration: number = 3000): void {
    this.notificationSubject.next({ message, type: 'info', duration });
    console.log(`Notification (info): ${message}`);
  }
}