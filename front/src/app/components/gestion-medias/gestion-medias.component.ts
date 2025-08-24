import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';

@Component({
    selector: 'gestion-medias',
    templateUrl: './gestion-medias.component.html',
    styleUrls: ['./gestion-medias.component.css']
})
export class GestionMediasComponent implements OnInit {
    medias: any[] = [];
    file: any;
    selectedBorne: any = null;
    mediasBornes: any[] = [];
    constructor(private api: ApiService) { }
    ngOnInit() { this.load(); this.loadBornes(); }
    load() { this.api.list('medias').subscribe((d: any) => this.medias = d || []); }
    loadBornes() { this.api.list('bornes/mine').subscribe((d: any) => this.mediasBornes = d || []); }
    onFile(e: any) {
        this.file = e.target.files[0];
        if (!this.file) return;
        // backend doesn't expose multipart upload endpoint; try fallback to metadata create
        const payload: any = { filename: this.file.name };
        if (this.selectedBorne) payload.borne = { id: this.selectedBorne };
        this.api.create('medias', payload).subscribe(() => this.load(), () => alert('Erreur upload'));
    }
    del(m: any) { this.api.delete('medias', m.id).subscribe(() => this.load()); }
}
