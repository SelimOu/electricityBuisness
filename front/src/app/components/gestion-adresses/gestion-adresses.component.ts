import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';

@Component({
    selector: 'gestion-adresses',
    templateUrl: './gestion-adresses.component.html',
    styleUrls: ['./gestion-adresses.component.css']
})
export class GestionAdressesComponent implements OnInit {
    adresses: any[] = [];
    model: any = {};
    page = 1;
    pageSize = 10;

    constructor(private api: ApiService) { }

    ngOnInit() { this.load(); }

    load() { this.api.list('adresses').subscribe((d: any) => this.adresses = d || []); }

    save() {
        if (this.model.id) {
            this.api.update('adresses', this.model.id, this.model).subscribe(() => { this.load(); this.model = {}; });
        } else {
            this.api.create('adresses', this.model).subscribe(() => { this.load(); this.model = {}; });
        }
    }

    edit(a: any) { this.model = { ...a }; }

    remove(a: any) {
        if (!confirm('Supprimer cette adresse ?')) return;
        this.api.delete('adresses', a.id).subscribe(() => this.load());
    }

    get paged() {
        const start = (this.page - 1) * this.pageSize;
        return this.adresses.slice(start, start + this.pageSize);
    }

    prev() { if (this.page > 1) this.page--; }
    next() { if (this.page * this.pageSize < this.adresses.length) this.page++; }
}
