import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'reservations-courantes',
    templateUrl: './reservations-courantes.component.html',
    styleUrls: ['./reservations-courantes.component.css']
})
export class ReservationsCourantesComponent implements OnInit {
    reservations: any[] = [];
    currentUserId: number | null = null;
    constructor(private api: ApiService, private auth: AuthService) { }
    ngOnInit() { this.resolveCurrentUser(); }

    resolveCurrentUser() {
        const pseudo = this.auth.getPseudoFromToken();
        if (!pseudo) {
            // not logged in -> no reservations
            this.currentUserId = null;
            this.reservations = [];
            return;
        }

        // try to resolve numeric id for the connected user, then load reservations
        this.api.list('utilisateurs').subscribe((u: any) => {
            const found = (u || []).find((x: any) => x.pseudo === pseudo || x.adresseMail === pseudo);
            if (found) this.currentUserId = found.id;
            this.load();
        }, () => {
            // on error, still attempt to load and fall back to pseudo matching
            this.currentUserId = null;
            this.load();
        });
    }

    load() {
    this.api.list('reservations/mine').subscribe((d: any) => {
            // treat CONFIRMED (and common case-insensitive variants) as active as well
            const activeEtats = ['ACTIVE', 'active', 'CONFIRMED', 'confirmed'];
            let raw = (d || []).filter((r: any) => !r.etat || activeEtats.includes(String(r.etat)));

            const pseudo = this.auth.getPseudoFromToken();
            // show only reservations that belong to the connected user
            raw = raw.filter((r: any) => {
                // numeric id match
                if (this.currentUserId) {
                    if (r.utilisateur && (r.utilisateur.id === this.currentUserId || r.utilisateur === this.currentUserId)) return true;
                    if (r.client && (r.client.id === this.currentUserId || r.client === this.currentUserId)) return true;
                    if (r.idUtilisateur && r.idUtilisateur === this.currentUserId) return true;
                }

                // fallback to pseudo matching when numeric id not available
                if (pseudo) {
                    if (r.utilisateur && (r.utilisateur.pseudo === pseudo || r.utilisateur.adresseMail === pseudo)) return true;
                    if (r.client && (r.client.pseudo === pseudo || r.client.adresseMail === pseudo)) return true;
                }

                return false;
            });

            this.reservations = raw.map((r: any) => {
                const borneName = r.borne?.nomBorne || r.borne?.nom || (r.nomBorne || null) || (r.idBorne ? ('Borne #' + r.idBorne) : 'Borne');
                let dateDisplay = '-';
                try {
                    if (r.dateDebut || r.dateFin) {
                        const d1 = r.dateDebut ? new Date(r.dateDebut) : null;
                        const d2 = r.dateFin ? new Date(r.dateFin) : null;
                        if (d1 && d2) dateDisplay = d1.toLocaleString() + ' → ' + d2.toLocaleString();
                        else if (d1) dateDisplay = d1.toLocaleString();
                        else if (d2) dateDisplay = d2.toLocaleString();
                    } else if (r.heureDebut) {
                        dateDisplay = r.heureDebut;
                    }
                } catch (e) { dateDisplay = r.dateDebut || r.heureDebut || '-'; }
                // we intentionally keep clientName internally for potential use but do not show it in templates
                const clientName = r.client?.nom || r.utilisateur?.nom || r.utilisateur?.pseudo || r.client || 'Inconnu';

                return { ...r, borneName, dateDisplay, clientName };
            });
        });
    }
    cancel(r: any) { this.api.delete('reservations', r.id).subscribe(() => this.load()); }
}
