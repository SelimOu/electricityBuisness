package com.example.electricitybusiness.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "bornes")
public class Borne {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomBorne;
    private String coordGPS; // store as "lat,lon" string for simplicity
    private Double tarif;
    private Integer puissance;
    private String instruction;
    private Boolean surPied;
    private String etat;
    private Boolean occupee;

    @ManyToOne
    @JoinColumn(name = "id_lieu")
    private Lieu lieu;

    @JsonIgnore
    @OneToMany(mappedBy = "borne", cascade = CascadeType.ALL)
    private List<Reservation> reservations;

    @JsonIgnore
    @OneToMany(mappedBy = "borne", cascade = CascadeType.ALL)
    private List<Media> medias;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomBorne() { return nomBorne; }
    public void setNomBorne(String nomBorne) { this.nomBorne = nomBorne; }
    public String getCoordGPS() { return coordGPS; }
    public void setCoordGPS(String coordGPS) { this.coordGPS = coordGPS; }
    public Double getTarif() { return tarif; }
    public void setTarif(Double tarif) { this.tarif = tarif; }
    public Integer getPuissance() { return puissance; }
    public void setPuissance(Integer puissance) { this.puissance = puissance; }
    public String getInstruction() { return instruction; }
    public void setInstruction(String instruction) { this.instruction = instruction; }
    public Boolean getSurPied() { return surPied; }
    public void setSurPied(Boolean surPied) { this.surPied = surPied; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
    public Boolean getOccupee() { return occupee; }
    public void setOccupee(Boolean occupee) { this.occupee = occupee; }
    public Lieu getLieu() { return lieu; }
    public void setLieu(Lieu lieu) { this.lieu = lieu; }
    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }
    public List<Media> getMedias() { return medias; }
    public void setMedias(List<Media> medias) { this.medias = medias; }
}
