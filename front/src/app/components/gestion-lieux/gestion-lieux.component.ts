import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
    selector: 'gestion-lieux',
    templateUrl: './gestion-lieux.component.html',
    styleUrls: ['./gestion-lieux.component.css']
})
export class GestionLieuxComponent implements OnInit {
    lieux: any[] = [];
    model: any = {};
    showForm = false;
    currentUserId: number | null = null;
    constructor(private api: ApiService, private auth: AuthService, private router: Router) { }
    ngOnInit() { this.load(); this.resolveCurrentUser(); }
    load() {
        this.api.list('lieux/mine').subscribe((d: any[]) => {
            this.lieux = d || [];
            // fetch adresse for each lieu and attach the first adresse
            (this.lieux || []).forEach(l => {
                // attempt to fetch adresses for this lieu
                this.api.list(`adresses?lieuId=${l.id}`).subscribe((ads: any[]) => {
                    if (ads && ads.length) l.adresse = ads[0];
                }, _ => { /* ignore per-lieu address load errors */ });
            });
        });
    }
    resolveCurrentUser() {
        const pseudo = this.auth.getPseudoFromToken();
        if (!pseudo) return;
        this.api.list('utilisateurs').subscribe((u: any[]) => {
            const found = (u || []).find(x => x.pseudo === pseudo || x.adresseMail === pseudo);
            if (found) this.currentUserId = found.id;
        });
    }
    save() {
        const payload = { ...this.model };
        if (!this.model.id && this.currentUserId) payload.utilisateur = { id: this.currentUserId };
        if (this.model.id) this.api.update('lieux', this.model.id, payload).subscribe(() => this.load());
        else this.api.create('lieux', payload).subscribe(() => this.load());
        this.model = {};
        this.showForm = false;
    }
    edit(l: any) {
        this.model = { ...l };
        if (!this.model.adresse) {
            // try to fetch adresse for this lieu
            this.api.list(`adresses?lieuId=${l.id}`).subscribe((ads: any[]) => {
                if (ads && ads.length) this.model.adresse = ads[0]; else this.model.adresse = {};
                this.showForm = true;
            }, _ => { this.model.adresse = {}; this.showForm = true; });
        } else {
            if (!this.model.adresse) this.model.adresse = {};
            this.showForm = true;
        }
    }
    remove(l: any) { this.api.delete('lieux', l.id).subscribe(() => this.load()); }

    // show add form
    addNew() {
        this.model = {};
        if (!this.model.adresse) this.model.adresse = {};
        this.showForm = true;
    }

    // helper to compute ownership like in GestionBornes
    isOwnedByCurrentUser(l: any) {
        try {
            const pseudo = this.auth.getPseudoFromToken();
            if (pseudo && l && l.utilisateur && l.utilisateur.pseudo) return l.utilisateur.pseudo === pseudo;
            if (this.currentUserId && l && l.utilisateur && l.utilisateur.id) return l.utilisateur.id === this.currentUserId;
            return false;
        } catch (e) { return false; }
    }

    // format address for display
    formatAddress(l: any) {
        if (!l) return '';
        const a = l.adresse || l.address || null;
        if (!a) return (l.id ? ('Lieu #' + l.id) : '—');
        const parts: string[] = [];
        if (a.numeroEtRue) parts.push(a.numeroEtRue);
        if (a.nomAdresse) parts.push(a.nomAdresse);
        const cityParts: string[] = [];
        if (a.codePostal) cityParts.push(a.codePostal);
        if (a.ville) cityParts.push(a.ville);
        if (cityParts.length) parts.push(cityParts.join(' '));
        if (a.pays) parts.push(a.pays);
        return parts.join(', ');
    }
}
