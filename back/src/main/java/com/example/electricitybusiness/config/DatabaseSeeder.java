package com.example.electricitybusiness.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner seedDatabase(UtilisateurRepository userRepo,
            LieuRepository lieuRepo,
            AdresseRepository adresseRepo,
            BorneRepository borneRepo,
            MediaRepository mediaRepo,
            ReservationRepository reservationRepo) {

        return args -> {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (userRepo.count() > 0 || borneRepo.count() > 0) return; // avoid reseeding

            // create users
            Utilisateur u1 = new Utilisateur();
            u1.setPrenom("Alice"); u1.setNomUtilisateur("Dupont"); u1.setPseudo("alice"); u1.setMotDePasse(passwordEncoder.encode("password")); u1.setAdresseMail("alice@example.com");
            u1.setRole("USER"); u1.setDateNaissance(LocalDate.of(1990,1,1));

            Utilisateur u2 = new Utilisateur();
            u2.setPrenom("Bob"); u2.setNomUtilisateur("Martin"); u2.setPseudo("bob"); u2.setMotDePasse(passwordEncoder.encode("password")); u2.setAdresseMail("bob@example.com");
            u2.setRole("USER"); u2.setDateNaissance(LocalDate.of(1985,5,10));

            userRepo.save(u1); userRepo.save(u2);

            // create lieux
            Lieu l1 = new Lieu(); l1.setInstructions("Parking Centre-ville"); l1.setUtilisateur(u1);
            Lieu l2 = new Lieu(); l2.setInstructions("Station Supermarché"); l2.setUtilisateur(u2);
            lieuRepo.save(l1); lieuRepo.save(l2);

            // addresses
            Adresse a1 = new Adresse(); a1.setNomAdresse("Parking Nord"); a1.setNumeroEtRue("10 Rue de la Paix"); a1.setCodePostal("75001"); a1.setVille("Paris"); a1.setPays("France"); a1.setLieu(l1); adresseRepo.save(a1);
            Adresse a2 = new Adresse(); a2.setNomAdresse("Supermarché Sud"); a2.setNumeroEtRue("5 Avenue du Commerce"); a2.setCodePostal("69002"); a2.setVille("Lyon"); a2.setPays("France"); a2.setLieu(l2); adresseRepo.save(a2);

            // bornes
            Borne b1 = new Borne(); b1.setNomBorne("Borne A1"); b1.setCoordGPS("48.8566,2.3522"); b1.setTarif(0.25); b1.setPuissance(22); b1.setInstruction("Près de l'entrée"); b1.setSurPied(true); b1.setEtat("disponible"); b1.setOccupee(false); b1.setLieu(l1);
            Borne b2 = new Borne(); b2.setNomBorne("Borne B1"); b2.setCoordGPS("45.7640,4.8357"); b2.setTarif(0.30); b2.setPuissance(11); b2.setInstruction("Zone C"); b2.setSurPied(false); b2.setEtat("disponible"); b2.setOccupee(false); b2.setLieu(l2);
            borneRepo.save(b1); borneRepo.save(b2);

            // medias
            Media m1 = new Media(); m1.setNomMedia("photo_parking.jpg"); m1.setUrl("/uploads/photo_parking.jpg"); m1.setType("image/jpeg"); m1.setDescription("Photo du parking"); m1.setTaille(102400L); m1.setBorne(b1);
            Media m2 = new Media(); m2.setNomMedia("photo_station.jpg"); m2.setUrl("/uploads/photo_station.jpg"); m2.setType("image/jpeg"); m2.setDescription("Photo du supermarché"); m2.setTaille(204800L); m2.setBorne(b2);
            mediaRepo.save(m1); mediaRepo.save(m2);

            // reservations
            Reservation r1 = new Reservation(); r1.setDateDebut(java.time.LocalDateTime.now().minusDays(1)); r1.setDateFin(java.time.LocalDateTime.now().minusDays(1).plusHours(1)); r1.setUtilisateur(u1); r1.setBorne(b1);
            reservationRepo.save(r1);

            System.out.println("Database seeded: users, lieux, adresses, bornes, medias, reservations created.");
        };
    }
}
