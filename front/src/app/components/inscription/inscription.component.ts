import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';

@Component({
    selector: 'inscription',
    templateUrl: './inscription.component.html',
    styleUrls: ['./inscription.component.css']
})
export class InscriptionComponent implements OnInit {
    step1!: FormGroup; // identité
    step2!: FormGroup; // compte
    step3!: FormGroup; // infos complémentaires
    submitting = false;
    error: string | null = null;

    constructor(private api: ApiService, private router: Router, private fb: FormBuilder) { }

    ngOnInit(): void {
        this.step1 = this.fb.group({
            nom: ['', [Validators.required, Validators.minLength(2)]],
            prenom: ['', [Validators.required, Validators.minLength(2)]],
            dateNaissance: [null]
        });
        this.step2 = this.fb.group({
            pseudo: ['', [Validators.required, Validators.minLength(3)]],
            adresseMail: ['', [Validators.required, Validators.email]],
            motDePasse: ['', [Validators.required, Validators.minLength(6)]]
        });
        this.step3 = this.fb.group({
            vehicule: [''],
            iban: ['']
        });
    }

    register() {
        if (this.step1.invalid || this.step2.invalid || this.step3.invalid) {
            this.error = 'Veuillez compléter les champs requis.';
            return;
        }
        this.submitting = true;
        this.error = null;
        const payload = {
            adresseMail: this.step2.value.adresseMail,
            motDePasse: this.step2.value.motDePasse,
            nomUtilisateur: this.step1.value.nom,
            prenom: this.step1.value.prenom,
            pseudo: this.step2.value.pseudo,
            dateNaissance: this.step1.value.dateNaissance,
            vehicule: this.step3.value.vehicule,
            iban: this.step3.value.iban
        };
        this.api.create('auth/register', payload).subscribe({
            next: () => {
                this.submitting = false;
                this.router.navigateByUrl('/connexion');
            },
            error: err => {
                console.error('inscription error', err);
                this.submitting = false;
                this.error = 'Erreur lors de l\'inscription';
            }
        });
    }
}
