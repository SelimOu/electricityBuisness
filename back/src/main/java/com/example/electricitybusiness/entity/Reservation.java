package com.example.electricitybusiness.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_utilisateur")
    private Utilisateur utilisateur;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_borne")
    private Borne borne;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Double prixMinuteHisto;
    private String etat;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    @JsonIgnore
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    @JsonIgnore
    public Borne getBorne() { return borne; }
    public void setBorne(Borne borne) { this.borne = borne; }
    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    public Double getPrixMinuteHisto() { return prixMinuteHisto; }
    public void setPrixMinuteHisto(Double prixMinuteHisto) { this.prixMinuteHisto = prixMinuteHisto; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
}
