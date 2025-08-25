import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'nouvelle-borne',
    templateUrl: './nouvelle-borne.component.html',
    styleUrls: ['./nouvelle-borne.component.css']
})
export class NouvelleBorneComponent implements OnInit {
    model: any = {};
    lieux: any[] = [];
    creatingLieu = false;
    newLieu: any = { instructions: '', utilisateur: null, adresse: {} };
    selectedFiles: File[] = [];
    filePreviews: Array<{ name: string; size: number; url: string; type: string }> = [];

    constructor(private api: ApiService, private router: Router, private auth: AuthService) { }

    ngOnInit(): void {
        this.loadLieux();
    }

    // only load lieux owned by the authenticated user
    loadLieux() { this.api.list('lieux/mine').subscribe((d: any) => this.lieux = d || []); }

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
        this.selectedFiles.splice(idx, 1);
        try { URL.revokeObjectURL(p.url); } catch (e) { }
    }

    cancel() { this.router.navigate(['/bornes']); }

    async save() {
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
        if (!this.creatingLieu && this.model.lieuId) payload.lieu = { id: this.model.lieuId };
        else if (this.creatingLieu) {
            // set utilisateur to currently logged user by pseudo
            const pseudo = this.auth.getPseudoFromToken();
            payload.lieu = { instructions: this.newLieu.instructions };
            if (pseudo) payload.lieu.utilisateur = { pseudo };
            if (this.newLieu.adresse && (this.newLieu.adresse.nomAdresse || this.newLieu.adresse.numeroEtRue)) payload.lieu.adresse = { ...this.newLieu.adresse };
        }

        try {
            const createdMedias: any[] = [];
            for (let i = 0; i < this.selectedFiles.length; i++) {
                const f = this.selectedFiles[i];
                const res: any = await this.api.uploadFile(f).toPromise();
                if (res && res.id) createdMedias.push({ id: res.id });
            }
            if (createdMedias.length) payload.medias = createdMedias;

            this.api.create('bornes', payload).subscribe(() => {
                this.router.navigate(['/bornes']);
            }, () => alert('Erreur création borne'));
        } catch (err) {
            console.error(err);
            alert('Erreur upload média');
        }
    }
}
