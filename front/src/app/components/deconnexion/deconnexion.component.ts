import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
    selector: 'deconnexion',
    templateUrl: './deconnexion.component.html',
    styleUrls: ['./deconnexion.component.css']
})
export class DeconnexionComponent {
    constructor(private router: Router) { }
    ngOnInit() {
        localStorage.removeItem('token');
        this.router.navigateByUrl('/');
    }
}
