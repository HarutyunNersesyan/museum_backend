package com.example.museum_backend.model.dto;

import com.example.museum_backend.model.enums.EventCategory;
import com.example.museum_backend.model.enums.Location;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSearchDTO {

    // Text search
    private String query;

    // Category filter
    private EventCategory eventCategory;

    // Location filter
    private Location location;

    // Museum filter
    private Long museumId;

    // Guide price range
    private Integer minGuidePrice;
    private Integer maxGuidePrice;

    // Ticket price range
    private Integer minTicketPrice;
    private Integer maxTicketPrice;

    // Date range
    private LocalDateTime startDateFrom;
    private LocalDateTime startDateTo;

    // Event type
    private String eventType;

    // Duration range (in hours)
    private Integer minDuration;
    private Integer maxDuration;
}