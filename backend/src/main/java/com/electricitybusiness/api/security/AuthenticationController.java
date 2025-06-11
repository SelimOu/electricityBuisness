package com.electricitybusiness.api.security;

import com.electricitybusiness.api.model.Lieu;
import com.electricitybusiness.api.model.RoleUtilisateur;
import com.electricitybusiness.api.model.Utilisateur;
import com.electricitybusiness.api.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        // Créer un nouvel utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setPseudo(request.pseudo());
        utilisateur.setNomUtilisateur(request.nomUtilisateur());
        utilisateur.setPrenom(request.prenom());
        utilisateur.setAdresseMail(request.adresseMail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        utilisateur.setRole(request.role());
        utilisateur.setDateDeNaissance(LocalDate.parse(request.dateDeNaissance()));
        utilisateur.setCompteValide(true);
        utilisateur.setBanni(false);

        Lieu lieu = new Lieu();
        lieu.setNumLieu(request.lieu().numLieu());
        utilisateur.setLieu(lieu);

        // Sauvegarder l'utilisateur
        utilisateur = utilisateurService.save(utilisateur);

        // Générer le token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(utilisateur.getPseudo());
        final String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody AuthenticationRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.pseudo(),
                request.password()
            )
        );

        final UserDetails user = userDetailsService.loadUserByUsername(request.pseudo());
        final String jwtToken = jwtService.generateToken(user);

        return ResponseEntity.ok(jwtToken);
    }
}

record AuthenticationRequest(String pseudo, String password) {}

record RegisterRequest(
    String pseudo,
    String nomUtilisateur,
    String prenom,
    String adresseMail,
    String motDePasse,
    RoleUtilisateur role,
    String dateDeNaissance,
    LieuRequest lieu
) {}

record LieuRequest(Long numLieu) {}
