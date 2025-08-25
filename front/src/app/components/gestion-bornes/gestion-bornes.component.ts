import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'gestion-bornes',
    templateUrl: './gestion-bornes.component.html',
    styleUrls: ['./gestion-bornes.component.css']
})
export class GestionBornesComponent implements OnInit {
    bornes: any[] = [];
    model: any = {};
    lieux: any[] = [];
    medias: any[] = [];
    selectedMedia: any = null;
    creatingLieu = false;
    newLieu: any = { instructions: '', utilisateur: null, adresse: {} };

    // selected files and previews
    selectedFiles: File[] = [];
    filePreviews: Array<{ name: string; size: number; url: string; type: string }> = [];
    currentUserId: number | null = null;

    constructor(private api: ApiService, private auth: AuthService) { }

    ngOnInit() {
        // try resolve current user first so ownership flags can be computed
        this.resolveCurrentUser();
        // load medias first so we can attach them to bornes when bornes arrive
        this.loadMedias();
        this.loadLieux();
        this.load();
    }

    loadLieux() { this.api.list('lieux/mine').subscribe((d: any) => this.lieux = d || []); }
    loadMedias() { this.api.list('medias').subscribe((d: any) => this.medias = d || []); }
    load() {
        // load only bornes owned by the current user
        this.api.list('bornes/mine').subscribe((d: any) => {
            // debug: show token length and request URL so we can verify the browser is sending auth
            try {
                const token = localStorage.getItem('token');
                console.debug('DEBUG: token length:', token ? token.length : 0);
            } catch (e) { /* ignore */ }
            console.debug('DEBUG: /api/bornes/mine response:', d);
            this.bornes = (d || []).map((b: any) => ({ ...b, _isOwner: this.isOwnedByCurrentUser(b) }));
            // attach medias that were already loaded
            this.attachMediasToBornes();
            // debug: summarize ownership computation
            try {
                console.debug('DEBUG: bornes mapped _isOwner:', this.bornes.map(b => ({ id: b.id, _isOwner: b._isOwner, pseudo: b.lieu?.utilisateur?.pseudo, userId: b.lieu?.utilisateur?.id })));
            } catch (e) { /* ignore */ }
        }, err => {
            console.error('DEBUG: /api/bornes/mine error', err);
        });
    }

    attachMediasToBornes() {
        if (!this.bornes || !this.medias) return;
        // ensure each borne has a medias array with the medias that reference it
        this.bornes = this.bornes.map(b => {
            const m = (this.medias || []).filter((mm: any) => mm && mm.borne && Number(mm.borne.id) === Number(b.id));
            return { ...b, medias: m };
        });
    }

    getMediaFullUrl(m: any) {
        if (!m || !m.url) return '';
        try {
            if (m.url.startsWith('http')) return m.url;
            // Prefer the backend origin if available (ApiService.baseUrl ends with '/api')
            try {
                const base = (this.api && this.api.baseUrl) ? (this.api.baseUrl.replace(/\/api\/?$/, '')) : window.location.origin;
                return `${base}${m.url}`;
            } catch (e) {
                return `${window.location.origin}${m.url}`;
            }
        } catch (e) { return m.url; }
    }

    isImage(m: any) {
        return m && m.type && m.type.startsWith && m.type.startsWith('image/');
    }

    shouldRenderImage(m: any) {
        if (!m) return false;
        if (this.isImage(m)) return true;
        if (m.url && typeof m.url === 'string') {
            return /\.(jpg|jpeg|png|gif|webp|bmp|svg)(\?.*)?$/i.test(m.url);
        }
        return false;
    }

    openLightbox(m: any) {
        this.selectedMedia = m;
    }

    closeLightbox() {
        this.selectedMedia = null;
    }

    resolveCurrentUser() {
        const pseudo = this.auth.getPseudoFromToken();
        if (!pseudo) return;
        this.api.list('utilisateurs').subscribe((u: any[]) => {
            const found = (u || []).find(x => x.pseudo === pseudo || x.adresseMail === pseudo);
            if (found) this.currentUserId = found.id;
            // refresh ownership flags
            this.bornes = this.bornes.map(b => ({ ...b, _isOwner: this.isOwnedByCurrentUser(b) }));
        });
    }

    isOwnedByCurrentUser(b: any) {
        try {
            const pseudo = this.auth.getPseudoFromToken();
            // if we can compare by pseudo (from token) prefer that; fallback to id if available
            if (pseudo && b && b.lieu && b.lieu.utilisateur && b.lieu.utilisateur.pseudo) {
                return b.lieu.utilisateur.pseudo === pseudo;
            }
            if (this.currentUserId && b && b.lieu && b.lieu.utilisateur && b.lieu.utilisateur.id) {
                return b.lieu.utilisateur.id === this.currentUserId;
            }
            return false;
        } catch (e) { return false; }
    }

    save() {
        const bornePayload: any = {
            nomBorne: this.model.nomBorne,
            coordGPS: this.model.coordGPS,
            tarif: Number(this.model.tarif) || 0,
            puissance: Number(this.model.puissance) || 0,
            instruction: this.model.instruction,
            surPied: !!this.model.surPied,
            etat: this.model.etat,
            occupee: !!this.model.occupee
        };

        const payload: any = { borne: bornePayload };

        if (!this.creatingLieu && this.model.lieuId) {
            payload.lieu = { id: this.model.lieuId };
        } else if (this.creatingLieu) {
            payload.lieu = {
                instructions: this.newLieu.instructions,
                utilisateur: { id: this.newLieu.utilisateur?.id || this.newLieu.utilisateur }
            };
            if (this.newLieu.adresse && (this.newLieu.adresse.nomAdresse || this.newLieu.adresse.numeroEtRue)) {
                payload.lieu.adresse = { ...this.newLieu.adresse };
            }
        }

        const uploadAllAndCreate = async () => {
            try {
                const createdMedias: any[] = [];
                for (let i = 0; i < this.selectedFiles.length; i++) {
                    const f = this.selectedFiles[i];
                    const res: any = await this.api.uploadFile(f).toPromise();
                    if (res && res.id) createdMedias.push({ id: res.id });
                }
                if (createdMedias.length) payload.medias = createdMedias;

                if (this.model.id) {
                    // update existing borne
                    this.api.update('bornes', this.model.id, payload.borne).subscribe(() => this.load(), () => alert('Erreur update'));
                } else {
                    this.api.create('bornes', payload).subscribe((created: any) => {
                        this.load();
                    }, () => alert('Erreur création borne'));
                }
            } catch (err) {
                console.error(err);
                alert('Erreur upload média');
            }
        };

        uploadAllAndCreate();
        this.resetForm();
    }

    edit(b: any) { this.model = { ...b }; }

    resetForm() {
        this.model = {};
        this.creatingLieu = false;
        this.newLieu = { instructions: '', utilisateur: null, adresse: {} };
        this.filePreviews = [];
    }

    onFilesSelected(e: any) {
        const files: FileList = e.target.files;
        for (let i = 0; i < files.length; i++) {
            const f = files.item(i);
            const url = URL.createObjectURL(f);
            this.selectedFiles.push(f);
            this.filePreviews.push({ name: f.name, size: f.size, url, type: f.type });
        }
    }

    removePreview(idx: number) {
        const p = this.filePreviews.splice(idx, 1)[0];
        try { URL.revokeObjectURL(p.url); } catch (e) { }
    }

    remove(b: any) {
        if (!confirm('Supprimer cette borne ?')) return;
        // check reservations
        this.api.list('reservations/mine').subscribe((r: any[]) => {
            const active = (r || []).find(x => Number(x.idBorne) === Number(b.id) && x.etat && x.etat !== 'CANCELLED');
            if (active) { alert('Impossible: cette borne a des réservations actives.'); return; }
            this.api.delete('bornes', b.id).subscribe(() => this.load(), () => alert('Erreur suppression'));
        }, () => {
            // if reservations endpoint fails, fallback to direct delete with confirmation
            if (confirm('Impossible de vérifier les réservations. Supprimer quand même ?')) this.api.delete('bornes', b.id).subscribe(() => this.load());
        });
    }
}
