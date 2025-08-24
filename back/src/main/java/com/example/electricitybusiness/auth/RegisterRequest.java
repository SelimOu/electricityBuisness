package com.example.electricitybusiness.auth;

public class RegisterRequest {
    private String pseudo;
    private String motDePasse;
    private String adresseMail;
    private String nomUtilisateur;
    private String prenom;

    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getAdresseMail() { return adresseMail; }
    public void setAdresseMail(String adresseMail) { this.adresseMail = adresseMail; }
    public String getNomUtilisateur() { return nomUtilisateur; }
    public void setNomUtilisateur(String nomUtilisateur) { this.nomUtilisateur = nomUtilisateur; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
}
