import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    constructor(private router: Router) { }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const token = localStorage.getItem('token');
        const request = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

        return next.handle(request).pipe(
            tap((evt: HttpEvent<any>) => {
                // noop for successful responses
            }),
            catchError((err: HttpErrorResponse) => {
                if (err.status === 401 || err.status === 403) {
                    // token invalid or expired -> remove and redirect to login
                    localStorage.removeItem('token');
                    // preserve navigation outside of HTTP context
                    setTimeout(() => this.router.navigateByUrl('/connexion'));
                }
                return throwError(() => err);
            })
        );
    }
}
