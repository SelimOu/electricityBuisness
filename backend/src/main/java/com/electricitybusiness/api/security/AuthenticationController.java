package com.electricitybusiness.api.security;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electricitybusiness.api.model.Lieu;
import com.electricitybusiness.api.model.RoleUtilisateur;
import com.electricitybusiness.api.model.Utilisateur;
import com.electricitybusiness.api.service.UtilisateurService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder;

    // À stocker en base dans une vraie app, ici pour l'exemple :
    private final Map<String, String> refreshTokenStore = new java.util.concurrent.ConcurrentHashMap<>();

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
    public ResponseEntity<String> authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.pseudo(),
                request.password()
            )
        );

        final UserDetails user = userDetailsService.loadUserByUsername(request.pseudo());
        final String jwtToken = jwtService.generateToken(user);

        // Générer un refresh token
        String refreshToken = UUID.randomUUID().toString();
        refreshTokenStore.put(request.pseudo(), refreshToken);

        // Envoyer le refresh token dans un cookie HttpOnly/Secure
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // à désactiver en dev si pas en HTTPS
        cookie.setPath("/"); // accessible sur tout le site
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 jours
        response.addCookie(cookie);

        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            @RequestBody(required = false) Map<String, String> body // optionnel
    ) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No refresh token"));
        }
        // Ici, on suppose que le pseudo est envoyé dans le body (à adapter selon ton front)
        String pseudo = (body != null) ? body.get("pseudo") : null;
        if (pseudo == null || !refreshToken.equals(refreshTokenStore.get(pseudo))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
        }
        final UserDetails user = userDetailsService.loadUserByUsername(pseudo);
        final String newJwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(Map.of("token", newJwtToken));
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
