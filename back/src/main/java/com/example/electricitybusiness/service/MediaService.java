package com.example.electricitybusiness.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.electricitybusiness.entity.Media;
import com.example.electricitybusiness.repository.MediaRepository;

@Service
public class MediaService {
    private final MediaRepository repository;
    public MediaService(MediaRepository repository) { this.repository = repository; }
    public List<Media> findAll() { return repository.findAll(); }
    public Optional<Media> findById(Long id) { return repository.findById(id); }
    public Media save(Media m) { return repository.save(m); }
    public void deleteById(Long id) { repository.deleteById(id); }
}
