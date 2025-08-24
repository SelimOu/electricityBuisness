package com.example.electricitybusiness.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "medias")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String type;
    private String nomMedia;
    private String description;
    private Long taille;

    @ManyToOne
    @JoinColumn(name = "id_borne")
    private Borne borne;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getNomMedia() { return nomMedia; }
    public void setNomMedia(String nomMedia) { this.nomMedia = nomMedia; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getTaille() { return taille; }
    public void setTaille(Long taille) { this.taille = taille; }
    public Borne getBorne() { return borne; }
    public void setBorne(Borne borne) { this.borne = borne; }
}
