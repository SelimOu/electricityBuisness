import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';

@Component({
    selector: 'reservations-historique',
    templateUrl: './reservations-historique.component.html',
    styleUrls: ['./reservations-historique.component.css']
})
export class ReservationsHistoriqueComponent implements OnInit {
    reservations: any[] = [];
    q = '';
    filterBorne = '';
    filterEtat = '';
    fromDate: string = '';
    toDate: string = '';

    constructor(private api: ApiService) { }

    ngOnInit() { this.api.list('reservations/mine').subscribe((d: any) => this.reservations = d || []); }

    get filtered() {
        return this.reservations.filter(r => {
            if (this.q && !(r.borneName || '').toLowerCase().includes(this.q.toLowerCase()) && !(r.utilisateurName || '').toLowerCase().includes(this.q.toLowerCase())) return false;
            if (this.filterBorne && String(r.idBorne) !== String(this.filterBorne)) return false;
            if (this.filterEtat && r.etat !== this.filterEtat) return false;
            if (this.fromDate) { const fd = new Date(this.fromDate); if (new Date(r.dateDebut) < fd) return false; }
            if (this.toDate) { const td = new Date(this.toDate); if (new Date(r.dateFin) > td) return false; }
            return true;
        });
    }

    clearFilters() { this.q = ''; this.filterBorne = ''; this.filterEtat = ''; this.fromDate = ''; this.toDate = ''; }
}
