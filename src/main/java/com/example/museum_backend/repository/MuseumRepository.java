package com.example.museum_backend.repository;

import com.example.museum_backend.model.entity.Museum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MuseumRepository extends JpaRepository<Museum, Long> {
}
