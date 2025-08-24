import { Component } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { ApiService } from './services/api.service';
import { ThemeService } from './services/theme.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html'
})
export class AppComponent {
    constructor(private api: ApiService, private router: Router, public theme: ThemeService) {
        // update body classes on route change to control page backgrounds
        this.router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe((evt: any) => {
            const url: string = evt.urlAfterRedirects || evt.url || '';
            const body = document.body;
            body.classList.remove('home-bg', 'login-bg', 'dashboard-bg', 'map-bg', 'management-bg', 'reservations-bg');
            if (url.startsWith('/connexion') || url.startsWith('/inscription')) body.classList.add('login-bg');
            else if (url === '/' || url.startsWith('/tableau-de-bord')) body.classList.add('dashboard-bg');
            else if (url.startsWith('/carte')) body.classList.add('map-bg');
            else if (url.startsWith('/gestion') || url.startsWith('/bornes') || url.startsWith('/lieux')) body.classList.add('management-bg');
            else if (url.startsWith('/reservations')) body.classList.add('reservations-bg');
            else body.classList.add('home-bg');
        });
    }

    isLogged() { return !!localStorage.getItem('token'); }

    demoLogin() {
        const payload = { pseudo: 'alice', motDePasse: 'secret' };
        this.api.create('auth/login', payload).subscribe((res: any) => {
            if (res && res.token) {
                localStorage.setItem('token', res.token);
                this.router.navigateByUrl('/');
            }
        });
    }
}
