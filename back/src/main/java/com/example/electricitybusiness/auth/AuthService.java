package com.example.electricitybusiness.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.electricitybusiness.entity.Utilisateur;
import com.example.electricitybusiness.repository.UtilisateurRepository;
import com.example.electricitybusiness.security.JwtUtil;

@Service
public class AuthService {
    private final UtilisateurRepository utilisateurRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UtilisateurRepository utilisateurRepository, JwtUtil jwtUtil) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtil = jwtUtil;
    }

    public Utilisateur register(RegisterRequest req) {
        Utilisateur u = new Utilisateur();
        u.setPseudo(req.getPseudo());
        u.setMotDePasse(passwordEncoder.encode(req.getMotDePasse()));
        u.setAdresseMail(req.getAdresseMail());
        u.setNomUtilisateur(req.getNomUtilisateur());
        u.setPrenom(req.getPrenom());
        u.setRole("USER");
        return utilisateurRepository.save(u);
    }

    public String login(AuthRequest req) {
        String identifier = req.getPseudo();
        // Try to find by pseudo first, then by adresseMail to allow login with email
        java.util.Optional<Utilisateur> opt = utilisateurRepository.findFirstByPseudo(identifier);
        if (opt.isEmpty()) {
            opt = utilisateurRepository.findFirstByAdresseMail(identifier);
        }
        return opt.filter(u -> passwordEncoder.matches(req.getMotDePasse(), u.getMotDePasse()))
            .map(u -> jwtUtil.generateToken(u.getPseudo())).orElse(null);
    }
}
