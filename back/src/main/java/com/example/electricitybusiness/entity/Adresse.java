package com.example.electricitybusiness.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "adresses")
public class Adresse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomAdresse;
    private String numeroEtRue;
    private String codePostal;
    private String ville;
    private String pays;
    private String region;
    private String complement;
    private String etage;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "id_lieu")
    private Lieu lieu;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomAdresse() { return nomAdresse; }
    public void setNomAdresse(String nomAdresse) { this.nomAdresse = nomAdresse; }
    public String getNumeroEtRue() { return numeroEtRue; }
    public void setNumeroEtRue(String numeroEtRue) { this.numeroEtRue = numeroEtRue; }
    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getComplement() { return complement; }
    public void setComplement(String complement) { this.complement = complement; }
    public String getEtage() { return etage; }
    public void setEtage(String etage) { this.etage = etage; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public Lieu getLieu() { return lieu; }
    public void setLieu(Lieu lieu) { this.lieu = lieu; }
}
