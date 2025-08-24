package com.example.electricitybusiness.dto;

import java.time.LocalDateTime;

public class ReservationDto {
    private Long id;
    private Long idBorne;
    private String borneName;
    private Long idUtilisateur;
    private String utilisateurName;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Double prixMinuteHisto;
    private String etat;

    public ReservationDto() {}

    public ReservationDto(Long id, Long idBorne, String borneName, Long idUtilisateur, String utilisateurName,
            LocalDateTime dateDebut, LocalDateTime dateFin, Double prixMinuteHisto, String etat) {
        this.id = id;
        this.idBorne = idBorne;
        this.borneName = borneName;
        this.idUtilisateur = idUtilisateur;
        this.utilisateurName = utilisateurName;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prixMinuteHisto = prixMinuteHisto;
        this.etat = etat;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdBorne() { return idBorne; }
    public void setIdBorne(Long idBorne) { this.idBorne = idBorne; }
    public String getBorneName() { return borneName; }
    public void setBorneName(String borneName) { this.borneName = borneName; }
    public Long getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Long idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    public String getUtilisateurName() { return utilisateurName; }
    public void setUtilisateurName(String utilisateurName) { this.utilisateurName = utilisateurName; }
    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    public Double getPrixMinuteHisto() { return prixMinuteHisto; }
    public void setPrixMinuteHisto(Double prixMinuteHisto) { this.prixMinuteHisto = prixMinuteHisto; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
}
