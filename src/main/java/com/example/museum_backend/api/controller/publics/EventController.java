package com.example.museum_backend.api.controller.publics;

import com.example.museum_backend.exceptions.CustomExceptions;
import com.example.museum_backend.model.dto.EventResponseDTO;
import com.example.museum_backend.model.dto.EventSearchDTO;
import com.example.museum_backend.model.enums.EventCategory;
import com.example.museum_backend.model.enums.Location;
import com.example.museum_backend.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.List;

@RestController
@RequestMapping("/api/public/events")
@RequiredArgsConstructor
@Tag(name = "Public Event Controller", description = "Public endpoints for viewing events")
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;

    // ==================== BASIC ENDPOINTS ====================

    @GetMapping
    @Operation(summary = "Get all events with pagination and sorting")
    public ResponseEntity<Page<EventResponseDTO>> getAllEvents(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field: eventDate, guidePrice, ticketPrice, name") @RequestParam(defaultValue = "eventDate") String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(defaultValue = "asc") String direction) {

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
    public ResponseEntity<EventResponseDTO> getEventById(
            @Parameter(description = "Event ID") @PathVariable Long id) {
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

    // ==================== MUSEUM FILTERING ENDPOINTS ====================

    @GetMapping("/museum/{museumId}")
    @Operation(summary = "Get events by museum ID (for museum filtering)")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByMuseumId(
            @Parameter(description = "Museum ID") @PathVariable Long museumId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "eventDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

        try {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
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

    // NEW: Search events by museum name (partial match)
    @GetMapping("/search/by-museum-name")
    @Operation(summary = "Search events by museum name (partial match supported)")
    public ResponseEntity<Page<EventResponseDTO>> searchEventsByMuseumName(
            @Parameter(description = "Museum name (partial or full)") @RequestParam String museumName,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "eventDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

        try {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            Page<EventResponseDTO> events = eventService.searchEventsByMuseumName(museumName, pageable);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            logger.error("Failed to search events by museum name: {}", museumName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // NEW: Search events by exact museum name
    @GetMapping("/search/by-exact-museum-name")
    @Operation(summary = "Search events by exact museum name")
    public ResponseEntity<Page<EventResponseDTO>> searchEventsByExactMuseumName(
            @Parameter(description = "Exact museum name") @RequestParam String museumName,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "eventDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

        try {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            Page<EventResponseDTO> events = eventService.searchEventsByExactMuseumName(museumName, pageable);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            logger.error("Failed to search events by exact museum name: {}", museumName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== UPCOMING EVENTS ====================

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming events (events with date >= current date)")
    public ResponseEntity<Page<EventResponseDTO>> getUpcomingEvents(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "eventDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

        try {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            Page<EventResponseDTO> events = eventService.getUpcomingEvents(pageable);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            logger.error("Failed to get upcoming events", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== SEARCH ENDPOINTS ====================

    @PostMapping("/search")
    @Operation(summary = "Search events with multiple filters (including museum name)")
    public ResponseEntity<Page<EventResponseDTO>> searchEvents(
            @RequestBody EventSearchDTO searchDTO,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "eventDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

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
            @Parameter(description = "Minimum ticket price") @RequestParam(required = false) Integer minPrice,
            @Parameter(description = "Maximum ticket price") @RequestParam(required = false) Integer maxPrice,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "ticketPrice") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

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
            @Parameter(description = "Minimum guide price") @RequestParam(required = false) Integer minGuidePrice,
            @Parameter(description = "Maximum guide price") @RequestParam(required = false) Integer maxGuidePrice,
            @Parameter(description = "Minimum ticket price") @RequestParam(required = false) Integer minTicketPrice,
            @Parameter(description = "Maximum ticket price") @RequestParam(required = false) Integer maxTicketPrice,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "ticketPrice") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

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
    @Operation(summary = "Simple search by query string (name or description)")
    public ResponseEntity<Page<EventResponseDTO>> simpleSearch(
            @Parameter(description = "Search query") @RequestParam(required = false) String query,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "eventDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

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

    // ==================== CATEGORY AND LOCATION FILTERS ====================

    @GetMapping("/search/category/{category}")
    @Operation(summary = "Get events by category")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByCategory(
            @Parameter(description = "Event category") @PathVariable EventCategory category,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "eventDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

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
            @Parameter(description = "Location") @PathVariable Location location,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "eventDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

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

    // ==================== PRICE RANGE FILTERS ====================

    @GetMapping("/search/guide-price")
    @Operation(summary = "Get events by guide price range")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByGuidePriceRange(
            @Parameter(description = "Minimum guide price") @RequestParam(required = false) Integer minPrice,
            @Parameter(description = "Maximum guide price") @RequestParam(required = false) Integer maxPrice,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "guidePrice") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

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
            @Parameter(description = "Minimum ticket price") @RequestParam(required = false) Integer minPrice,
            @Parameter(description = "Maximum ticket price") @RequestParam(required = false) Integer maxPrice,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "ticketPrice") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

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

    // ==================== UTILITY ENDPOINTS ====================

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