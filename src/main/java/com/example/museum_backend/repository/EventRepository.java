package com.example.museum_backend.repository;

import com.example.museum_backend.model.entity.Event;
import com.example.museum_backend.model.entity.Museum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByMuseum(Museum museum, Pageable pageable);
}