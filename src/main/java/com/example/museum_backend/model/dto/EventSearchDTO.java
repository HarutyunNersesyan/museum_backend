package com.example.museum_backend.model.dto;

import com.example.museum_backend.model.enums.EventCategory;
import com.example.museum_backend.model.enums.EventType;
import com.example.museum_backend.model.enums.Location;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventSearchDTO {
    private String query;
    private EventCategory eventCategory;
    private Location location;
    private Long museumId;
    private String museumName;  // NEW: Search by museum name
    private Integer minGuidePrice;
    private Integer maxGuidePrice;
    private Integer minTicketPrice;
    private Integer maxTicketPrice;
    private LocalDateTime startDateFrom;
    private LocalDateTime startDateTo;
    private String eventType;
    private Integer minDuration;
    private Integer maxDuration;
}