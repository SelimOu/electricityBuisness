package com.example.electricitybusiness.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "utilisateurs")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomUtilisateur;
    private String prenom;
    private String pseudo;
    private String motDePasse;
    private String role;
    private String adresseMail;
    private LocalDate dateNaissance;
    @JsonIgnore
    private String iban;
    private String vehicule;
    private boolean banni;

    @JsonIgnore
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Reservation> reservations;

    @JsonIgnore
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Adresse> adresses;

    @JsonIgnore
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Lieu> lieux;

    // getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomUtilisateur() { return nomUtilisateur; }
    public void setNomUtilisateur(String nomUtilisateur) { this.nomUtilisateur = nomUtilisateur; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getAdresseMail() { return adresseMail; }
    public void setAdresseMail(String adresseMail) { this.adresseMail = adresseMail; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }
    public String getVehicule() { return vehicule; }
    public void setVehicule(String vehicule) { this.vehicule = vehicule; }
    public boolean isBanni() { return banni; }
    public void setBanni(boolean banni) { this.banni = banni; }
    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }
    public List<Adresse> getAdresses() { return adresses; }
    public void setAdresses(List<Adresse> adresses) { this.adresses = adresses; }
    public List<Lieu> getLieux() { return lieux; }
    public void setLieux(List<Lieu> lieux) { this.lieux = lieux; }
}
