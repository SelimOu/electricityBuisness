package com.example.electricitybusiness.service;

import java.time.LocalDate;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.electricitybusiness.entity.Adresse;
import com.example.electricitybusiness.entity.Borne;
import com.example.electricitybusiness.entity.Lieu;
import com.example.electricitybusiness.entity.Media;
import com.example.electricitybusiness.entity.Reservation;
import com.example.electricitybusiness.entity.Utilisateur;
import com.example.electricitybusiness.repository.AdresseRepository;
import com.example.electricitybusiness.repository.BorneRepository;
import com.example.electricitybusiness.repository.LieuRepository;
import com.example.electricitybusiness.repository.MediaRepository;
import com.example.electricitybusiness.repository.ReservationRepository;
import com.example.electricitybusiness.repository.UtilisateurRepository;

@Service
public class SeedService {
    private final UtilisateurRepository userRepo;
    private final LieuRepository lieuRepo;
    private final AdresseRepository adresseRepo;
    private final BorneRepository borneRepo;
    private final MediaRepository mediaRepo;
    private final ReservationRepository reservationRepo;

    public SeedService(UtilisateurRepository userRepo, LieuRepository lieuRepo, AdresseRepository adresseRepo,
            BorneRepository borneRepo, MediaRepository mediaRepo, ReservationRepository reservationRepo) {
        this.userRepo = userRepo;
        this.lieuRepo = lieuRepo;
        this.adresseRepo = adresseRepo;
        this.borneRepo = borneRepo;
        this.mediaRepo = mediaRepo;
        this.reservationRepo = reservationRepo;
    }

    public void seed(boolean force) {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (force) {
            // delete in order to avoid FK constraint errors
            mediaRepo.deleteAll();
            reservationRepo.deleteAll();
            borneRepo.deleteAll();
            adresseRepo.deleteAll();
            lieuRepo.deleteAll();
            userRepo.deleteAll();
        } else {
            if (userRepo.count() > 0 || borneRepo.count() > 0) return;
        }

        // create users
    Utilisateur u1 = new Utilisateur();
    u1.setPrenom("Alice"); u1.setNomUtilisateur("Dupont"); u1.setPseudo("alice"); u1.setMotDePasse(passwordEncoder.encode("password")); u1.setAdresseMail("alice@example.com");
        u1.setRole("USER"); u1.setDateNaissance(LocalDate.of(1990,1,1));

        Utilisateur u2 = new Utilisateur();
    u2.setPrenom("Bob"); u2.setNomUtilisateur("Martin"); u2.setPseudo("bob"); u2.setMotDePasse(passwordEncoder.encode("password")); u2.setAdresseMail("bob@example.com");
        u2.setRole("USER"); u2.setDateNaissance(LocalDate.of(1985,5,10));

        userRepo.save(u1); userRepo.save(u2);

        // create lieux
        Lieu l1 = new Lieu(); l1.setInstructions("Parking center"); l1.setUtilisateur(u1);
        Lieu l2 = new Lieu(); l2.setInstructions("wswwwsws"); l2.setUtilisateur(u2);
        lieuRepo.save(l1); lieuRepo.save(l2);

        // addresses
        Adresse a1 = new Adresse(); a1.setNomAdresse("Parking Nord"); a1.setNumeroEtRue("10 Rue de la Paix"); a1.setCodePostal("75001"); a1.setVille("Paris"); a1.setPays("France"); a1.setLieu(l1); adresseRepo.save(a1);
        Adresse a2 = new Adresse(); a2.setNomAdresse("Supermarché Sud"); a2.setNumeroEtRue("5 Avenue du Commerce"); a2.setCodePostal("69002"); a2.setVille("Lyon"); a2.setPays("France"); a2.setLieu(l2); adresseRepo.save(a2);

        // bornes
        Borne b1 = new Borne(); b1.setNomBorne("Borne A"); b1.setCoordGPS("48.8566,2.3522"); b1.setTarif(0.25); b1.setPuissance(22); b1.setInstruction("Near mall"); b1.setSurPied(true); b1.setEtat("OPERATIONAL"); b1.setOccupee(false); b1.setLieu(l1);
        Borne b2 = new Borne(); b2.setNomBorne("Borne A"); b2.setCoordGPS("48.8566,2.3522"); b2.setTarif(0.25); b2.setPuissance(22); b2.setInstruction("Near mall"); b2.setSurPied(true); b2.setEtat("OPERATIONAL"); b2.setOccupee(false); b2.setLieu(l1);
        borneRepo.save(b1); borneRepo.save(b2);

        // medias
        Media m1 = new Media(); m1.setNomMedia("image.jpg"); m1.setUrl("http://example.com/image.jpg"); m1.setType("image"); m1.setDescription("Photo"); m1.setTaille(12345L); m1.setBorne(null);
        mediaRepo.save(m1);

        // reservations
        Reservation r1 = new Reservation(); r1.setDateDebut(java.time.LocalDateTime.now().minusDays(1)); r1.setDateFin(java.time.LocalDateTime.now().minusDays(1).plusHours(1)); r1.setUtilisateur(u1); r1.setBorne(b1);
        reservationRepo.save(r1);
    }
}
