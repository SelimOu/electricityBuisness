package com.example.electricitybusiness.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.electricitybusiness.entity.Media;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
}
