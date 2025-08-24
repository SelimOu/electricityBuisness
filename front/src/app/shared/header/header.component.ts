import { Component } from '@angular/core';
import { ThemeService } from '../../services/theme.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
    selector: 'eb-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css']
})
export class HeaderComponent {
    constructor(public theme: ThemeService, public auth: AuthService, private router: Router) { }

    toggleSidebar() {
        document.body.classList.toggle('sidebar-open');
    }

    logout() {
        localStorage.removeItem('token');
        this.router.navigateByUrl('/connexion');
    }
}
