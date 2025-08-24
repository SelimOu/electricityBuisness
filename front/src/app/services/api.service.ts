import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApiService {
    // When running in Docker Compose, backend service is reachable at 'http://back:8080'
    // For local dev (ng serve) use 'http://localhost:8080'
    baseUrl = (window && (window as any).location && (window as any).location.hostname === 'localhost') ? 'http://localhost:8080/api' : 'http://back:8080/api';

    constructor(private http: HttpClient) { }

    // Generic CRUD helpers
    list<T>(path: string): Observable<T[]> {
        return this.http.get<T[]>(`${this.baseUrl}/${path}`);
    }

    get<T>(path: string, id: any): Observable<T> {
        return this.http.get<T>(`${this.baseUrl}/${path}/${id}`);
    }

    create<T>(path: string, payload: any): Observable<T> {
        return this.http.post<T>(`${this.baseUrl}/${path}`, payload);
    }

    update<T>(path: string, id: any, payload: any): Observable<T> {
        return this.http.put<T>(`${this.baseUrl}/${path}/${id}`, payload);
    }

    delete(path: string, id: any): Observable<any> {
        return this.http.delete(`${this.baseUrl}/${path}/${id}`);
    }

    // Upload a single file to medias/upload
    uploadFile(file: File, nomMedia?: string, description?: string) {
        const form = new FormData();
        form.append('file', file, file.name);
        if (nomMedia) form.append('nomMedia', nomMedia);
        if (description) form.append('description', description);
        return this.http.post<any>(`${this.baseUrl}/medias/upload`, form);
    }
}
