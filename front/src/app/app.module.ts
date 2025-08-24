import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { InscriptionComponent } from './components/inscription/inscription.component';
import { ConnexionComponent } from './components/connexion/connexion.component';
import { DeconnexionComponent } from './components/deconnexion/deconnexion.component';
import { TableauDeBordComponent } from './components/tableau-de-bord/tableau-de-bord.component';
import { GestionLieuxComponent } from './components/gestion-lieux/gestion-lieux.component';
import { GestionAdressesComponent } from './components/gestion-adresses/gestion-adresses.component';
import { GestionBornesComponent } from './components/gestion-bornes/gestion-bornes.component';
import { NouvelleBorneComponent } from './components/nouvelle-borne/nouvelle-borne.component';
import { CarteComponent } from './components/carte/carte.component';
import { ReservationComponent } from './components/reservation/reservation.component';
import { ReservationsCourantesComponent } from './components/reservations-courantes/reservations-courantes.component';
import { ReservationsHistoriqueComponent } from './components/reservations-historique/reservations-historique.component';
import { GestionMediasComponent } from './components/gestion-medias/gestion-medias.component';
import { ApiService } from './services/api.service';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './services/auth.interceptor';
import { AuthService } from './services/auth.service';
import { AuthGuard } from './services/auth.guard';
import { NonAuthGuard } from './services/nonauth.guard';
import { FilterPipe } from './pipes/filter.pipe';
import { HeaderComponent } from './shared/header/header.component';

const routes: Routes = [
    { path: '', component: TableauDeBordComponent, canActivate: [AuthGuard] },
    { path: 'inscription', component: InscriptionComponent, canActivate: [NonAuthGuard] },
    { path: 'connexion', component: ConnexionComponent, canActivate: [NonAuthGuard] },
    { path: 'deconnexion', component: DeconnexionComponent, canActivate: [AuthGuard] },
    { path: 'lieux', component: GestionLieuxComponent, canActivate: [AuthGuard] },
    { path: 'adresses', component: GestionAdressesComponent, canActivate: [AuthGuard] },
    { path: 'bornes', component: GestionBornesComponent, canActivate: [AuthGuard] },
    { path: 'bornes/new', component: NouvelleBorneComponent, canActivate: [AuthGuard] },
    { path: 'carte', component: CarteComponent, canActivate: [AuthGuard] },
    { path: 'reservation', component: ReservationComponent, canActivate: [AuthGuard] },
    { path: 'reservations', component: ReservationsCourantesComponent, canActivate: [AuthGuard] },
    { path: 'historique', component: ReservationsHistoriqueComponent, canActivate: [AuthGuard] },
    { path: 'medias', component: GestionMediasComponent, canActivate: [AuthGuard] }
];

@NgModule({
    declarations: [
        AppComponent,
        HeaderComponent,
        InscriptionComponent,
        ConnexionComponent,
        DeconnexionComponent,
        TableauDeBordComponent,
        GestionLieuxComponent,
        GestionAdressesComponent,
        GestionBornesComponent,
        NouvelleBorneComponent,
        CarteComponent,
        ReservationComponent,
        ReservationsCourantesComponent,
        ReservationsHistoriqueComponent,
        GestionMediasComponent
        , FilterPipe
    ],
    imports: [BrowserModule, FormsModule, ReactiveFormsModule, HttpClientModule, RouterModule.forRoot(routes)],
    providers: [ApiService, AuthService, AuthGuard, NonAuthGuard, { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }],
    bootstrap: [AppComponent]
})
export class AppModule { }
