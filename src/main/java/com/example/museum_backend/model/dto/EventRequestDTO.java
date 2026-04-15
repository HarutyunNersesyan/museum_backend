package com.example.museum_backend.model.dto;

import com.example.museum_backend.model.enums.EventCategory;
import com.example.museum_backend.model.enums.EventType;
import com.example.museum_backend.model.enums.Location;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventRequestDTO {
    @NotBlank(message = "Event name is required")
    @Size(max = 30, message = "Event name must be less than 30 characters")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Event category is required")
    private EventCategory eventCategory;

    @NotNull(message = "Event type is required")
    private EventType eventType;

    @NotNull(message = "Event date is required")
    private LocalDateTime eventDate;

    @Pattern(regexp = "^[0-9]{8}$", message = "Phone number must be exactly 8 digits")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    private String contactEmail;

    @NotNull(message = "Guide price is required")
    @Min(value = 0, message = "Guide price must be positive")
    private Integer guidePrice;

    @NotNull(message = "Ticket price is required")
    @Min(value = 0, message = "Ticket price must be positive")
    private Integer ticketPrice;

    @NotNull(message = "Location is required")
    private Location location;

    @Min(value = 1, message = "Duration must be at least 1 hour")
    private Integer duration;

    @NotNull(message = "Museum ID is required")
    private Long museumId;
}