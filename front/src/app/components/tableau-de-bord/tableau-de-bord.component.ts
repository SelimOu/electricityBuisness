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
            const now = new Date().getTime();
            const upcomingWindowMs = 30 * 60 * 1000; // 30 minutes window for "about to start"
            this.activeReservationsCount = (this.reservations || []).filter((res: any) => {
                try {
                    // prefer explicit state if present
                    const s = (res.etat || '').toLowerCase();
                    // treat explicit states that imply activity as active
                    if (s === 'active' || s === 'en_cours') return true;
                    // treat confirmed reservations as active (many backends use CONFIRMED)
                    if (s.includes('confirm')) return true;
                    // fallback: check if current time is between dateDebut and dateFin
                    const start = res.dateDebut ? new Date(res.dateDebut).getTime() : null;
                    const end = res.dateFin ? new Date(res.dateFin).getTime() : null;
                    if (start && end) {
                        if (now >= start && now <= end) return true;
                        // treat as active if it will start within the upcoming window
                        if (start > now && (start - now) <= upcomingWindowMs) return true;
                    }
                    return false;
                } catch (e) { return false; }
            }).length;
            this.loading = false;
        }, () => { this.reservations = []; this.activeReservationsCount = 0; this.loading = false; });
    }
}
