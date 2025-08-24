import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';

@Component({
    selector: 'connexion',
    templateUrl: './connexion.component.html',
    styleUrls: ['./connexion.component.css']
})
export class ConnexionComponent {
    model: any = {};
    error: string | null = null;
    constructor(private api: ApiService, private router: Router) { }
    login() {
        // UI asks for email, backend expects pseudo; try using adresseMail as pseudo when provided
        const pseudo = this.model.pseudo || this.model.adresseMail || '';
        const payload = { pseudo: pseudo, motDePasse: this.model.motDePasse };
        this.api.create('auth/login', payload).subscribe((res: any) => {
            if (res && res.token) {
                localStorage.setItem('token', res.token);
                this.error = null;
                this.router.navigateByUrl('/');
            } else {
                this.error = 'Réponse inattendue du serveur';
            }
        }, err => {
            this.error = 'Identifiants invalides';
            console.error('login error', err);
        });
    }
}
