import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'gestion-lieux',
    templateUrl: './gestion-lieux.component.html',
    styleUrls: ['./gestion-lieux.component.css']
})
export class GestionLieuxComponent implements OnInit {
    lieux: any[] = [];
    model: any = {};
    currentUserId: number | null = null;
    constructor(private api: ApiService, private auth: AuthService) { }
    ngOnInit() { this.load(); this.resolveCurrentUser(); }
    load() { this.api.list('lieux/mine').subscribe((d: any) => this.lieux = d || []); }
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
    }
    edit(l: any) { this.model = { ...l }; }
    remove(l: any) { this.api.delete('lieux', l.id).subscribe(() => this.load()); }
}
