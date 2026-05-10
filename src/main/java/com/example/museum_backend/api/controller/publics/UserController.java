package com.example.museum_backend.api.controller.publics;

import com.example.museum_backend.exceptions.CustomExceptions;
import com.example.museum_backend.model.dto.*;
import com.example.museum_backend.model.entity.User;
import com.example.museum_backend.service.*;
import com.example.museum_backend.validation.PasswordValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final MailSenderService mailSenderService;
    private final MuseumService museumService;
    private final EventService eventService;

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    // ==================== User Management Endpoints ====================

    @GetMapping("/get/userName/{email}")
    public ResponseEntity<String> getUserNameByEmail(@PathVariable String email) {
        logger.debug("Getting username by email: {}", email);
        Optional<User> userOptional = userService.findUserByEmail(email);

        if (userOptional.isPresent()) {
            logger.debug("Found username: {} for email: {}", userOptional.get().getUserName(), email);
            return ResponseEntity.ok(userOptional.get().getUserName());
        } else {
            logger.warn("User not found for email: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with email: " + email);
        }
    }

    @GetMapping("/get/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        logger.debug("Getting user by email: {}", email);
        Optional<User> userOptional = userService.findUserByEmail(email);
        return userOptional
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        logger.debug("Getting user by ID: {}", id);
        return ResponseEntity.ok(userService.getUserById(id).orElseThrow(() -> {
            logger.warn("User not found with ID: {}", id);
            return new RuntimeException("User not found with id: " + id);
        }));
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUp signUp, BindingResult result) {
        logger.info("Received signup request for username: {}", signUp.getUserName());

        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            logger.warn("Signup validation failed for {}: {}", signUp.getUserName(), errors);
            return ResponseEntity.badRequest().body(errors);
        }

        if (userService.findUserByUserName(signUp.getUserName()).isPresent()) {
            logger.warn("Signup failed - username already taken: {}", signUp.getUserName());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("This username is already taken. Please use a different username.");
        }

        if (userService.findUserByEmail(signUp.getEmail()).isPresent()) {
            logger.warn("Signup failed - email already registered: {}", signUp.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("This email address is already registered. Please use a different email.");
        }

        if (!PasswordValidator.isValidPassword(signUp.getPassword())) {
            logger.warn("Signup failed - invalid password for: {}", signUp.getUserName());
            return ResponseEntity.badRequest()
                    .body("Invalid password: password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        }

        if (!signUp.getPassword().equals(signUp.getRepeatPassword())){
            logger.warn("Signup failed - invalid confirm password for: {}", signUp.getUserName());
            return ResponseEntity.badRequest()
                    .body("Invalid password: Repeat password is not match with password ");
        }

        User createdUser = new User(signUp);
        userService.signUp(createdUser);

        mailSenderService.handlePinRequest(signUp.getEmail(), false);

        logger.info("Signup completed successfully for: {}", signUp.getUserName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody User user, BindingResult result) {
        logger.debug("Updating user: {}", user.getUserName());
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            logger.warn("User update validation failed for {}: {}", user.getUserName(), errors);
            return ResponseEntity.badRequest().body(errors);
        }
        if (!PasswordValidator.isValidPassword(user.getPassword())) {
            logger.warn("Invalid password during update for: {}", user.getUserName());
            throw new IllegalArgumentException("Invalid password: password must be contain least one uppercase, one lowercase, one digit, and one special character");
        }
        User updatedUser = userService.update(user);
        logger.info("User updated successfully: {}", user.getUserName());
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestParam String email,
                                            @Valid @RequestBody ChangePassword changePassword, BindingResult result) {
        logger.debug("Password change request for: {}", email);
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            logger.warn("Password change validation failed for {}: {}", email, errors);
            return ResponseEntity.badRequest().body(errors);
        }
        if (!PasswordValidator.isValidPassword(changePassword.getNewPassword())) {
            logger.warn("Invalid new password for: {}", email);
            throw new IllegalArgumentException("Invalid password: password must be contain least one uppercase, one lowercase, one digit, and one special character");
        }
        try {
            String responseMessage = userService.changePassword(email, changePassword.getOldPassword(), changePassword.getNewPassword(), changePassword.getNewPasswordRepeat());
            logger.info("Password changed successfully for: {}", email);
            return ResponseEntity.ok(responseMessage);
        } catch (CustomExceptions.NotVerifiedMailException e) {
            logger.warn("Password change failed - email not verified: {}", email);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (CustomExceptions.UserNotFoundException e) {
            logger.warn("Password change failed - user not found: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomExceptions.InvalidOldPasswordException | CustomExceptions.InvalidNewPasswordException |
                 CustomExceptions.PasswordMismatchException e) {
            logger.warn("Password change failed - validation error for {}: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Password change failed - internal error for: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPassword forgotPassword) {
        logger.debug("Forgot password request for: {}", forgotPassword.getEmail());
        try {
            String responseMessage = userService.forgotPassword(forgotPassword);
            return ResponseEntity.ok(responseMessage);
        } catch (CustomExceptions.NotVerifiedMailException e) {
            logger.warn("Forgot password failed - email not verified: {}", forgotPassword.getEmail());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (CustomExceptions.InvalidPinExceptions | CustomExceptions.InvalidNewPasswordException e) {
            logger.warn("Forgot password failed - validation error for {}: {}", forgotPassword.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Forgot password failed - internal error for: {}", forgotPassword.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody Verify verify) {
        logger.debug("Email verification request for: {}", verify.getEmail());
        try {
            String result = userService.verify(verify);
            logger.info("Email verified successfully: {}", verify.getEmail());
            return ResponseEntity.ok(result);
        } catch (CustomExceptions.AlreadyVerifiedEmail e) {
            logger.warn("Email already verified: {}", verify.getEmail());
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(e.getMessage());
        } catch (CustomExceptions.InvalidPinExceptions e) {
            logger.warn("Invalid PIN for email verification: {}", verify.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (CustomExceptions.UserNotFoundException e) {
            logger.warn("User not found or verification timeout: {}", verify.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Email verification failed for: {}", verify.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerificationCode(@RequestBody ResendVerificationRequest request) {
        logger.debug("Resend verification code request for: {}", request.getEmail());

        try {
            String result = mailSenderService.resendVerificationCode(request.getEmail());
            logger.info("Verification code resent successfully to: {}", request.getEmail());
            return ResponseEntity.ok(result);
        } catch (CustomExceptions.UserNotFoundException e) {
            logger.warn("Resend failed - user not found: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomExceptions.AlreadyVerifiedEmail e) {
            logger.warn("Resend failed - email already verified: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Resend failed - internal error for: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to resend verification code. Please try again later.");
        }
    }

    @DeleteMapping("/delete/account")
    public ResponseEntity<String> delete(@RequestBody DeleteAccount deleteAccount) {
        logger.debug("Account deletion request for: {}", deleteAccount.getEmail());
        try {
            userService.delete(deleteAccount);
            logger.info("Account deleted successfully: {}", deleteAccount.getEmail());
            return ResponseEntity.ok("Account deleted successfully.");
        } catch (CustomExceptions.UserNotFoundException e) {
            logger.warn("Account deletion failed - user not found: {}", deleteAccount.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomExceptions.InvalidPassword e) {
            logger.warn("Account deletion failed - invalid password for: {}", deleteAccount.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Account deletion failed - internal error for: {}", deleteAccount.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @DeleteMapping("/delete/verify/{email}")
    public ResponseEntity<String> cancelVerify(@PathVariable String email) {
        logger.debug("Cancel verification request for: {}", email);
        try {
            userService.deleteVerify(email);
            logger.info("Unverified account deleted: {}", email);
            return ResponseEntity.ok("Account deleted successfully.");
        } catch (CustomExceptions.UserNotFoundException e) {
            logger.warn("Cancel verification failed - user not found: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Cancel verification failed for: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping("/username/{userName}")
    public ResponseEntity<User> getUserByUserName(@PathVariable String userName) {
        logger.debug("Getting user by username: {}", userName);
        Optional<User> userOptional = userService.getUserByUserName(userName);
        return userOptional
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== Museum Endpoints for User ====================

    @GetMapping("/museums")
    public ResponseEntity<Page<MuseumResponseDTO>> getAllMuseums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<MuseumResponseDTO> museums = museumService.getAllMuseums(pageable);
            return ResponseEntity.ok(museums);
        } catch (Exception e) {
            logger.error("Failed to get museums", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/museums/{id}")
    public ResponseEntity<MuseumResponseDTO> getMuseumById(@PathVariable Long id) {
        try {
            MuseumResponseDTO museum = museumService.getMuseumById(id);
            return ResponseEntity.ok(museum);
        } catch (CustomExceptions.UserNotFoundException e) {
            logger.warn("Museum not found with id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Failed to get museum with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== Event Endpoints for User ====================

    @GetMapping("/events")
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

    @GetMapping("/events/{id}")
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

    @GetMapping("/events/museum/{museumId}")
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

    @GetMapping("/events/upcoming")
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

    // UserController.java - ավելացնել նոր endpoint
    @PostMapping("/send-ticket-confirmation")
    public ResponseEntity<?> sendTicketConfirmation(@RequestBody TicketConfirmationRequest request) {
        logger.debug("Sending ticket confirmation to: {}", request.getEmail());
        try {
            User user = userService.findUserByEmail(request.getEmail())
                    .orElseThrow(() -> new CustomExceptions.UserNotFoundException("User not found"));

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

            mailSenderService.sendTicketConfirmation(request.getEmail(), user.getUserName(), bookingDetails);

            return ResponseEntity.ok("Ticket confirmation sent successfully");
        } catch (CustomExceptions.UserNotFoundException e) {
            logger.warn("User not found: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to send ticket confirmation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
        }
    }
}