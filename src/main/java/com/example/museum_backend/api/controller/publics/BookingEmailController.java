// BookingEmailController.java
package com.example.museum_backend.api.controller.publics;

import com.example.museum_backend.model.dto.TicketConfirmationRequest;
import com.example.museum_backend.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/booking")  // Լրիվ նոր path - ոչ մի կապ չունի /api/public/-ի հետ
@RequiredArgsConstructor
public class BookingEmailController {

    private static final Logger logger = LoggerFactory.getLogger(BookingEmailController.class);
    private final MailSenderService mailSenderService;

    @PostMapping("/send-ticket-email")  // POST /api/booking/send-ticket-email
    public ResponseEntity<?> sendTicketEmail(@RequestBody TicketConfirmationRequest request) {
        logger.info("Received ticket email request for: {}", request.getEmail());

        try {
            // Ուղարկել email-ը
            Map<String, Object> bookingDetails = new HashMap<>();
            bookingDetails.put("bookingId", request.getBookingId());
            bookingDetails.put("eventName", request.getEventName());
            bookingDetails.put("museumName", request.getMuseumName());
            bookingDetails.put("location", request.getLocation());
            bookingDetails.put("eventDate", request.getEventDate());
            bookingDetails.put("ticketQuantity", request.getTicketQuantity());
            bookingDetails.put("ticketPrice", request.getTicketPrice());
            bookingDetails.put("guidePrice", request.getGuidePrice());
            bookingDetails.put("includeGuide", request.isIncludeGuide());
            bookingDetails.put("totalAmount", request.getTotalAmount());
            bookingDetails.put("email", request.getEmail());
            bookingDetails.put("phoneNumber", request.getPhoneNumber());
            bookingDetails.put("fullName", request.getFullName());

            // Օգտագործել fullName-ը որպես userName
            String userName = request.getFullName() != null ? request.getFullName() : "Հարգելի հաճախորդ";

            mailSenderService.sendTicketConfirmation(request.getEmail(), userName, bookingDetails);

            logger.info("Ticket email sent successfully to: {}", request.getEmail());
            return ResponseEntity.ok("Ticket confirmation email sent successfully");

        } catch (Exception e) {
            logger.error("Failed to send ticket email to: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage());
        }
    }
}