import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';

@Component({
    selector: 'inscription',
    templateUrl: './inscription.component.html',
    styleUrls: ['./inscription.component.css']
})
export class InscriptionComponent implements OnInit {
    model: any = {};

    constructor(private api: ApiService, private router: Router) { }

    ngOnInit(): void { }

    register() {
        // backend expects RegisterRequest: adresseMail, motDePasse, nom, prenom, pseudo (optional)
        const payload = {
            adresseMail: this.model.adresseMail,
            motDePasse: this.model.motDePasse,
            nomUtilisateur: this.model.nomUtilisateur || this.model.nom || '',
            prenom: this.model.prenom || '',
            pseudo: this.model.pseudo || '',
            dateNaissance: this.model.dateNaissance || null,
            vehicule: this.model.vehicule || null
        };
        this.api.create('auth/register', payload).subscribe({
            next: res => {
                console.log('inscription success', res);
                this.router.navigateByUrl('/connexion');
            },
            error: err => console.error('inscription error', err)
        });
    }

}
