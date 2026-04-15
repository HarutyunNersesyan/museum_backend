package com.example.museum_backend.model.dto;

import com.example.museum_backend.model.enums.EventCategory;
import com.example.museum_backend.model.enums.EventType;
import com.example.museum_backend.model.enums.Location;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventResponseDTO {
    private Long id;
    private String name;
    private String description;
    private List<String> imageUrls;
    private EventCategory eventCategory;
    private EventType eventType;
    private LocalDateTime eventDate;
    private String phoneNumber;
    private String contactEmail;
    private Integer guidePrice;
    private Integer ticketPrice;
    private Location location;
    private Integer duration;
    private Long museumId;
    private String museumName;
}