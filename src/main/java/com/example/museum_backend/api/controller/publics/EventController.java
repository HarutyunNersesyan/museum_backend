package com.example.museum_backend.api.controller.publics;

import com.example.museum_backend.exceptions.CustomExceptions;
import com.example.museum_backend.model.dto.EventResponseDTO;
import com.example.museum_backend.model.dto.EventSearchDTO;
import com.example.museum_backend.model.enums.EventCategory;
import com.example.museum_backend.model.enums.Location;
import com.example.museum_backend.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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
            Page<EventResponseDTO> events = eventService.getUpcomingEvents(pageable);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            logger.error("Failed to get upcoming events", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== SEARCH ENDPOINTS ====================

    @PostMapping("/search")
    @Operation(summary = "Search events with multiple filters (including ticket price)")
    public ResponseEntity<Page<EventResponseDTO>> searchEvents(
            @RequestBody EventSearchDTO searchDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "eventDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        try {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

            Page<EventResponseDTO> events = eventService.searchEvents(searchDTO, pageable);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            logger.error("Failed to search events", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/ticket-price")
    @Operation(summary = "Search events by ticket price range")
    public ResponseEntity<Page<EventResponseDTO>> searchByTicketPrice(
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ticketPrice") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        try {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

            Page<EventResponseDTO> events = eventService.searchByTicketPrice(minPrice, maxPrice, pageable);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            logger.error("Failed to search by ticket price", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/both-prices")
    @Operation(summary = "Search events by both guide price and ticket price ranges")
    public ResponseEntity<Page<EventResponseDTO>> searchByBothPrices(
            @RequestParam(required = false) Integer minGuidePrice,
            @RequestParam(required = false) Integer maxGuidePrice,
            @RequestParam(required = false) Integer minTicketPrice,
            @RequestParam(required = false) Integer maxTicketPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ticketPrice") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        try {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

            Page<EventResponseDTO> events = eventService.searchByBothPrices(
                    minGuidePrice, maxGuidePrice, minTicketPrice, maxTicketPrice, pageable);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            logger.error("Failed to search by both prices", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/simple")
    @Operation(summary = "Simple search by query string")
    public ResponseEntity<Page<EventResponseDTO>> simpleSearch(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "eventDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        try {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

            Page<EventResponseDTO> events = eventService.simpleSearch(query, pageable);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            logger.error("Failed to simple search events", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/category/{category}")
    @Operation(summary = "Get events by category")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByCategory(
            @PathVariable EventCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "eventDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        try {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

            Page<EventResponseDTO> events = eventService.getEventsByCategory(category, pageable);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            logger.error("Failed to get events by category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/location/{location}")
    @Operation(summary = "Get events by location")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByLocation(
            @PathVariable Location location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "eventDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        try {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

            Page<EventResponseDTO> events = eventService.getEventsByLocation(location, pageable);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            logger.error("Failed to get events by location", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/guide-price")
    @Operation(summary = "Get events by guide price range")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByGuidePriceRange(
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "guidePrice") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        try {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

            Page<EventResponseDTO> events = eventService.getEventsByGuidePriceRange(minPrice, maxPrice, pageable);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            logger.error("Failed to get events by guide price range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/ticket-price-range")
    @Operation(summary = "Get events by ticket price range")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByTicketPriceRange(
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ticketPrice") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        try {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

            Page<EventResponseDTO> events = eventService.getEventsByTicketPriceRange(minPrice, maxPrice, pageable);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            logger.error("Failed to get events by ticket price range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all event categories")
    public ResponseEntity<List<EventCategory>> getAllCategories() {
        return ResponseEntity.ok(List.of(EventCategory.values()));
    }

    @GetMapping("/locations")
    @Operation(summary = "Get all locations")
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(List.of(Location.values()));
    }
}