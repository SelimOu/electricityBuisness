import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../services/api.service';

@Component({
    selector: 'reservation',
    templateUrl: './reservation.component.html',
    styleUrls: ['./reservation.component.css']
})
export class ReservationComponent implements OnInit {
    model: any = {};
    bornes: any[] = [];
    selectedBorne: any = null;
    estimatedPrice = 0;

    constructor(private api: ApiService, private route: ActivatedRoute, private router: Router) { }

    ngOnInit(): void {
        this.api.list('bornes').subscribe((b: any) => {
            this.bornes = b || [];
            const bid = Number(this.route.snapshot.queryParamMap.get('borne'));
            if (bid) this.selectBorneById(bid);
        });
    }

    selectBorneById(id: number) {
        this.selectedBorne = this.bornes.find(x => x.id === id) || null;
        this.model.borneId = id;
        this.calculatePrice();
    }

    calculatePrice() {
        if (!this.selectedBorne || !this.model.debut || !this.model.fin) { this.estimatedPrice = 0; return; }
        const d1 = new Date(this.model.debut);
        const d2 = new Date(this.model.fin);
        if (d2.getTime() <= d1.getTime()) { this.estimatedPrice = 0; return; }
        const minutes = Math.max(0, (d2.getTime() - d1.getTime()) / 60000);
        const tarif = Number(this.selectedBorne.tarif) || 0;
        this.estimatedPrice = +(minutes * tarif).toFixed(2);
    }

    reserve() {
        const d1 = new Date(this.model.debut || 0);
        const d2 = new Date(this.model.fin || 0);
        if (d2.getTime() <= d1.getTime()) { alert('La date de fin doit être après la date de début'); return; }
        const payload = {
            idBorne: Number(this.model.borneId),
            idUtilisateur: this.model.utilisateurId || null,
            dateDebut: this.model.debut,
            dateFin: this.model.fin,
            prixMinuteHisto: Number(this.selectedBorne?.tarif) || 0,
            etat: 'CONFIRMED'
        };
        this.api.create('reservations', payload).subscribe(() => {
            alert('Réservé');
            this.router.navigate(['/reservations']);
        }, err => alert('Erreur'));
    }
}
