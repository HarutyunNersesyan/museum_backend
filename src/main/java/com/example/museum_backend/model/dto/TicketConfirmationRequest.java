// TicketConfirmationRequest.java
package com.example.museum_backend.model.dto;

import lombok.Data;

@Data
public class TicketConfirmationRequest {
    private String email;
    private String bookingId;
    private String eventName;
    private String museumName;
    private String location;
    private String eventDate;
    private Integer ticketQuantity;
    private Integer ticketPrice;
    private Integer guidePrice;
    private boolean includeGuide;
    private Integer totalAmount;
    private String phoneNumber;
    private String fullName;
}