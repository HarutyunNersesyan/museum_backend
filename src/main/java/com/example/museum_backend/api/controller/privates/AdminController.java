package com.example.museum_backend.api.controller.privates;

import com.example.museum_backend.model.dto.*;
import com.example.museum_backend.model.enums.EventCategory;
import com.example.museum_backend.model.enums.EventType;
import com.example.museum_backend.model.enums.Location;
import com.example.museum_backend.service.EventService;
import com.example.museum_backend.service.ImageStorageService;
import com.example.museum_backend.service.MuseumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/private/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final MuseumService museumService;
    private final EventService eventService;
    private final ImageStorageService imageStorageService;

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    // ==================== Museum Management ====================

    @PostMapping("/museums")
    @Operation(summary = "Create new museum")
    public ResponseEntity<?> createMuseum(@Valid @RequestBody MuseumRequestDTO request) {
        try {
            MuseumResponseDTO museum = museumService.createMuseum(request, getCurrentUserEmail());
            logger.info("Museum created successfully by admin: {}", getCurrentUserEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(museum);
        } catch (Exception e) {
            logger.error("Failed to create museum", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create museum: " + e.getMessage());
        }
    }

    @PutMapping("/museums/{id}")
    @Operation(summary = "Update museum")
    public ResponseEntity<?> updateMuseum(@PathVariable Long id, @Valid @RequestBody MuseumRequestDTO request) {
        try {
            MuseumResponseDTO museum = museumService.updateMuseum(id, request);
            logger.info("Museum updated successfully with id: {}", id);
            return ResponseEntity.ok(museum);
        } catch (Exception e) {
            logger.error("Failed to update museum with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update museum: " + e.getMessage());
        }
    }

    @DeleteMapping("/museums/{id}")
    @Operation(summary = "Delete museum")
    public ResponseEntity<?> deleteMuseum(@PathVariable Long id) {
        try {
            museumService.deleteMuseum(id);
            logger.info("Museum deleted successfully with id: {}", id);
            return ResponseEntity.ok("Museum deleted successfully");
        } catch (Exception e) {
            logger.error("Failed to delete museum with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete museum: " + e.getMessage());
        }
    }

    @GetMapping("/museums")
    @Operation(summary = "Get all museums for admin")
    public ResponseEntity<?> getAllMuseums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(museumService.getAllMuseums(pageable));
        } catch (Exception e) {
            logger.error("Failed to get museums", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get museums: " + e.getMessage());
        }
    }

    @GetMapping("/museums/{id}")
    @Operation(summary = "Get museum by id")
    public ResponseEntity<?> getMuseumById(@PathVariable Long id) {
        try {
            MuseumResponseDTO museum = museumService.getMuseumById(id);
            return ResponseEntity.ok(museum);
        } catch (Exception e) {
            logger.error("Failed to get museum with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // ==================== Event Management ====================

    @PostMapping("/events")
    @Operation(summary = "Create new event")
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventRequestDTO request) {
        try {
            EventResponseDTO event = eventService.createEvent(request, getCurrentUserEmail());
            logger.info("Event created successfully by admin: {}", getCurrentUserEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(event);
        } catch (Exception e) {
            logger.error("Failed to create event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create event: " + e.getMessage());
        }
    }

    @PostMapping(value = "/events/with-images", consumes = {"multipart/form-data"})
    @Operation(summary = "Create new event with image upload")
    public ResponseEntity<?> createEventWithImages(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("eventCategory") String eventCategory,
            @RequestParam("eventType") String eventType,
            @RequestParam("eventDate") String eventDate,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "contactEmail", required = false) String contactEmail,
            @RequestParam("guidePrice") Integer guidePrice,
            @RequestParam("ticketPrice") Integer ticketPrice,
            @RequestParam("location") String location,
            @RequestParam(value = "duration", required = false) Integer duration,
            @RequestParam("museumId") Long museumId,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        try {
            logger.info("Creating event with params:");
            logger.info("name: {}", name);
            logger.info("eventCategory: {}", eventCategory);
            logger.info("eventDate: {}", eventDate);
            logger.info("museumId: {}", museumId);
            logger.info("phoneNumber received: {}", phoneNumber);
            logger.info("contactEmail received: {}", contactEmail);

            LocalDateTime parsedEventDate;
            try {
                parsedEventDate = LocalDateTime.parse(eventDate);
            } catch (Exception e) {
                try {
                    parsedEventDate = LocalDateTime.parse(eventDate.replace(" ", "T"));
                } catch (Exception e2) {
                    parsedEventDate = LocalDateTime.parse(eventDate + "T00:00:00");
                }
            }

            EventRequestDTO requestDTO = new EventRequestDTO();
            requestDTO.setName(name);
            requestDTO.setDescription(description);
            requestDTO.setEventCategory(EventCategory.valueOf(eventCategory));
            requestDTO.setEventType(EventType.valueOf(eventType));
            requestDTO.setEventDate(parsedEventDate);
            requestDTO.setPhoneNumber(phoneNumber);
            requestDTO.setContactEmail(contactEmail);
            requestDTO.setGuidePrice(guidePrice);
            requestDTO.setTicketPrice(ticketPrice);
            requestDTO.setLocation(Location.valueOf(location));
            requestDTO.setDuration(duration);
            requestDTO.setMuseumId(museumId);

            EventResponseDTO event = eventService.createEvent(requestDTO, getCurrentUserEmail());

            if (images != null && !images.isEmpty()) {
                List<String> imageUrls = imageStorageService.saveImages(images);
                eventService.updateEventImages(event.getId(), imageUrls);
                event = eventService.getEventById(event.getId());
            }

            logger.info("Event with images created successfully. Phone number saved: {}", event.getPhoneNumber());
            return ResponseEntity.status(HttpStatus.CREATED).body(event);
        } catch (Exception e) {
            logger.error("Failed to create event with images", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create event: " + e.getMessage());
        }
    }

    @PutMapping("/events/{id}")
    @Operation(summary = "Update event")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequestDTO request) {
        try {
            EventResponseDTO event = eventService.updateEvent(id, request);
            logger.info("Event updated successfully with id: {}", id);
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            logger.error("Failed to update event with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update event: " + e.getMessage());
        }
    }

    @PutMapping(value = "/events/{id}/with-images", consumes = {"multipart/form-data"})
    @Operation(summary = "Update event with image upload")
    public ResponseEntity<?> updateEventWithImages(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("eventCategory") String eventCategory,
            @RequestParam("eventType") String eventType,
            @RequestParam("eventDate") String eventDate,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "contactEmail", required = false) String contactEmail,
            @RequestParam("guidePrice") Integer guidePrice,
            @RequestParam("ticketPrice") Integer ticketPrice,
            @RequestParam("location") String location,
            @RequestParam(value = "duration", required = false) Integer duration,
            @RequestParam("museumId") Long museumId,
            @RequestParam(value = "existingImageUrls", required = false) List<String> existingImageUrls,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        try {
            logger.info("Updating event with id: {}", id);
            logger.info("phoneNumber received: {}", phoneNumber);
            logger.info("contactEmail received: {}", contactEmail);

            LocalDateTime parsedEventDate;
            try {
                parsedEventDate = LocalDateTime.parse(eventDate);
            } catch (Exception e) {
                try {
                    parsedEventDate = LocalDateTime.parse(eventDate.replace(" ", "T"));
                } catch (Exception e2) {
                    parsedEventDate = LocalDateTime.parse(eventDate + "T00:00:00");
                }
            }

            EventRequestDTO requestDTO = new EventRequestDTO();
            requestDTO.setName(name);
            requestDTO.setDescription(description);
            requestDTO.setEventCategory(EventCategory.valueOf(eventCategory));
            requestDTO.setEventType(EventType.valueOf(eventType));
            requestDTO.setEventDate(parsedEventDate);
            requestDTO.setPhoneNumber(phoneNumber);
            requestDTO.setContactEmail(contactEmail);
            requestDTO.setGuidePrice(guidePrice);
            requestDTO.setTicketPrice(ticketPrice);
            requestDTO.setLocation(Location.valueOf(location));
            requestDTO.setDuration(duration);
            requestDTO.setMuseumId(museumId);

            EventResponseDTO event = eventService.updateEvent(id, requestDTO);

            List<String> allImageUrls = new ArrayList<>();
            if (existingImageUrls != null) {
                allImageUrls.addAll(existingImageUrls);
            }
            if (images != null && !images.isEmpty()) {
                List<String> newImageUrls = imageStorageService.saveImages(images);
                allImageUrls.addAll(newImageUrls);
            }
            if (!allImageUrls.isEmpty()) {
                eventService.updateEventImages(id, allImageUrls);
                event = eventService.getEventById(id);
            }

            logger.info("Event updated successfully with id: {}", id);
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            logger.error("Failed to update event with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update event: " + e.getMessage());
        }
    }

    @DeleteMapping("/events/{id}")
    @Operation(summary = "Delete event")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        try {
            eventService.deleteEvent(id);
            logger.info("Event deleted successfully with id: {}", id);
            return ResponseEntity.ok("Event deleted successfully");
        } catch (Exception e) {
            logger.error("Failed to delete event with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete event: " + e.getMessage());
        }
    }

    @GetMapping("/events")
    @Operation(summary = "Get all events for admin")
    public ResponseEntity<?> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(eventService.getAllEvents(pageable));
        } catch (Exception e) {
            logger.error("Failed to get events", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get events: " + e.getMessage());
        }
    }

    @GetMapping("/events/museum/{museumId}")
    @Operation(summary = "Get events by museum id")
    public ResponseEntity<?> getEventsByMuseumId(
            @PathVariable Long museumId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(eventService.getEventsByMuseumId(museumId, pageable));
        } catch (Exception e) {
            logger.error("Failed to get events for museum: {}", museumId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get events: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Get admin dashboard statistics")
    public ResponseEntity<?> getStats() {
        try {
            AdminStatsDTO stats = new AdminStatsDTO();
            stats.setTotalMuseums(museumService.getTotalMuseumsCount());
            stats.setTotalEvents(eventService.getTotalEventsCount());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Failed to get stats", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get stats: " + e.getMessage());
        }
    }
}