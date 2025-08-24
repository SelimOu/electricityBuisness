import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {
    get token() { return localStorage.getItem('token'); }
    isLogged() { return !!this.token; }
    // crude JWT payload parser to extract 'sub' (pseudo)
    getPseudoFromToken(): string | null {
        const t = this.token;
        if (!t) return null;
        const parts = t.split('.');
        if (parts.length < 2) return null;
        try {
            const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')));
            return payload.sub || null;
        } catch (e) { return null; }
    }
}
