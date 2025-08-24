import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'tableau-de-bord',
    templateUrl: './tableau-de-bord.component.html',
    styleUrls: ['./tableau-de-bord.component.css']
})
export class TableauDeBordComponent implements OnInit {
    bornes: any[] = [];
    reservations: any[] = [];
    pseudo: string | null = null;
    bornesByUser: any[] = [];
    activeReservationsCount = 0;
    userCount = 0;
    loading = true;

    constructor(private api: ApiService, private auth: AuthService) { }

    ngOnInit() {
        this.pseudo = this.auth.getPseudoFromToken();

        // load bornes owned by the connected user for dashboard metrics
        this.api.list('bornes/mine').subscribe((b: any) => {
            this.bornes = b || [];
            this.bornesByUser = this.bornes; // already scoped to current user
        }, () => { this.bornes = []; this.bornesByUser = []; });

    // load *current user's* reservations and compute active count
    this.api.list('reservations/mine').subscribe((r: any) => {
            this.reservations = r || [];
            this.activeReservationsCount = (this.reservations || []).filter((res: any) => {
                const s = (res.etat || '').toLowerCase();
                return s === 'active' || s === 'en_cours' || !res.etat;
            }).length;
        });

        // total users - useful summary metric
        this.api.list('utilisateurs').subscribe((u: any) => { this.userCount = (u || []).length; this.loading = false; });
    }
}
