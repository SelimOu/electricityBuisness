package com.example.electricitybusiness.controller;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.electricitybusiness.entity.Media;
import com.example.electricitybusiness.service.MediaService;

@RestController
@RequestMapping("/api/medias")
public class MediaController {
    private final MediaService service;
    public MediaController(MediaService service) { this.service = service; }

    @GetMapping
    public List<Media> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Media> getById(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Media> create(@RequestBody Media m) {
        Media saved = service.save(m);
        return ResponseEntity.created(URI.create("/api/medias/" + saved.getId())).body(saved);
    }

    // Upload a file and create a Media entry
    @PostMapping("/upload")
    public ResponseEntity<Media> upload(@RequestParam("file") MultipartFile file, @RequestParam(value = "nomMedia", required = false) String nomMedia, @RequestParam(value = "description", required = false) String description) {
        try {
            if (file == null || file.isEmpty()) return ResponseEntity.badRequest().build();
            Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);
            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) ext = original.substring(original.lastIndexOf('.'));
            String filename = UUID.randomUUID().toString() + ext;
            Path target = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            Media m = new Media();
            m.setNomMedia(nomMedia != null ? nomMedia : original);
            m.setDescription(description != null ? description : "");
            m.setType(file.getContentType());
            m.setTaille(file.getSize());
            // URL exposed under /uploads/{filename}
            m.setUrl("/uploads/" + filename);
            Media saved = service.save(m);
            return ResponseEntity.created(URI.create("/api/medias/" + saved.getId())).body(saved);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Media> update(@PathVariable Long id, @RequestBody Media m) {
        return service.findById(id).map(existing -> {
            m.setId(existing.getId());
            return ResponseEntity.ok(service.save(m));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
