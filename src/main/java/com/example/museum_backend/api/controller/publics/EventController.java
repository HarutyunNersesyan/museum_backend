package com.example.museum_backend.api.controller.publics;

import com.example.museum_backend.exceptions.CustomExceptions;
import com.example.museum_backend.model.dto.EventResponseDTO;
import com.example.museum_backend.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/events")
@RequiredArgsConstructor
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;

    @GetMapping
    @Operation(summary = "Get all events with pagination and sorting")
    public ResponseEntity<Page<EventResponseDTO>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "eventDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        try {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            Page<EventResponseDTO> events = eventService.getAllEvents(pageable);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            logger.error("Failed to get events", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by id")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
        try {
            EventResponseDTO event = eventService.getEventById(id);
            return ResponseEntity.ok(event);
        } catch (CustomExceptions.UserNotFoundException e) {
            logger.warn("Event not found with id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Failed to get event with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/museum/{museumId}")
    @Operation(summary = "Get events by museum id")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByMuseumId(
            @PathVariable Long museumId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate").ascending());
            Page<EventResponseDTO> events = eventService.getEventsByMuseumId(museumId, pageable);
            return ResponseEntity.ok(events);
        } catch (CustomExceptions.UserNotFoundException e) {
            logger.warn("Museum not found with id: {}", museumId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Failed to get events for museum: {}", museumId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming events")
    public ResponseEntity<Page<EventResponseDTO>> getUpcomingEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate").ascending());
            Page<EventResponseDTO> events = eventService.getAllEvents(pageable);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            logger.error("Failed to get upcoming events", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}