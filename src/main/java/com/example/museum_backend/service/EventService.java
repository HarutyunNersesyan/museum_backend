package com.example.museum_backend.service;

import com.example.museum_backend.exceptions.CustomExceptions;
import com.example.museum_backend.model.dto.EventRequestDTO;
import com.example.museum_backend.model.dto.EventResponseDTO;
import com.example.museum_backend.model.entity.Event;
import com.example.museum_backend.model.entity.Museum;
import com.example.museum_backend.repository.EventRepository;
import com.example.museum_backend.repository.MuseumRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);
    private final EventRepository eventRepository;
    private final MuseumRepository museumRepository;

    @Transactional
    public EventResponseDTO createEvent(EventRequestDTO request, String adminEmail) {
        logger.info("Creating event by admin: {}", adminEmail);
        logger.info("Phone number received in service: {}", request.getPhoneNumber());
        logger.info("Contact email received in service: {}", request.getContactEmail());

        Museum museum = museumRepository.findById(request.getMuseumId())
                .orElseThrow(() -> new CustomExceptions.UserNotFoundException("Museum not found with id: " + request.getMuseumId()));

        Event event = new Event();
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setEventCategory(request.getEventCategory());
        event.setEventType(request.getEventType());
        event.setEventDate(request.getEventDate());
        event.setPhoneNumber(request.getPhoneNumber());
        event.setContactEmail(request.getContactEmail());
        event.setGuidePrice(request.getGuidePrice());
        event.setTicketPrice(request.getTicketPrice());
        event.setLocation(request.getLocation());
        event.setDuration(request.getDuration());
        event.setMuseum(museum);

        Event savedEvent = eventRepository.save(event);
        logger.info("Event created successfully with id: {}", savedEvent.getId());
        logger.info("Saved phone number: {}", savedEvent.getPhoneNumber());
        logger.info("Saved contact email: {}", savedEvent.getContactEmail());

        return convertToResponseDTO(savedEvent);
    }

    @Transactional
    public EventResponseDTO updateEvent(Long id, EventRequestDTO request) {
        logger.info("Updating event with id: {}", id);
        logger.info("Phone number received for update: {}", request.getPhoneNumber());
        logger.info("Contact email received for update: {}", request.getContactEmail());

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new CustomExceptions.UserNotFoundException("Event not found with id: " + id));

        if (request.getMuseumId() != null && !request.getMuseumId().equals(event.getMuseum().getId())) {
            Museum museum = museumRepository.findById(request.getMuseumId())
                    .orElseThrow(() -> new CustomExceptions.UserNotFoundException("Museum not found with id: " + request.getMuseumId()));
            event.setMuseum(museum);
        }

        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setEventCategory(request.getEventCategory());
        event.setEventType(request.getEventType());
        event.setEventDate(request.getEventDate());
        event.setPhoneNumber(request.getPhoneNumber());
        event.setContactEmail(request.getContactEmail());
        event.setGuidePrice(request.getGuidePrice());
        event.setTicketPrice(request.getTicketPrice());
        event.setLocation(request.getLocation());
        event.setDuration(request.getDuration());

        Event updatedEvent = eventRepository.save(event);
        logger.info("Event updated successfully with id: {}", id);
        logger.info("Updated phone number: {}", updatedEvent.getPhoneNumber());
        logger.info("Updated contact email: {}", updatedEvent.getContactEmail());

        return convertToResponseDTO(updatedEvent);
    }

    @Transactional
    public void deleteEvent(Long id) {
        logger.info("Deleting event with id: {}", id);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new CustomExceptions.UserNotFoundException("Event not found with id: " + id));

        eventRepository.delete(event);
        logger.info("Event deleted successfully with id: {}", id);
    }

    public EventResponseDTO getEventById(Long id) {
        logger.debug("Fetching event by id: {}", id);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new CustomExceptions.UserNotFoundException("Event not found with id: " + id));

        return convertToResponseDTO(event);
    }

    public Page<EventResponseDTO> getAllEvents(Pageable pageable) {
        logger.debug("Fetching all events with pagination");
        return eventRepository.findAll(pageable).map(this::convertToResponseDTO);
    }

    public Page<EventResponseDTO> getEventsByMuseumId(Long museumId, Pageable pageable) {
        logger.debug("Fetching events for museum id: {}", museumId);

        Museum museum = museumRepository.findById(museumId)
                .orElseThrow(() -> new CustomExceptions.UserNotFoundException("Museum not found with id: " + museumId));

        return eventRepository.findByMuseum(museum, pageable).map(this::convertToResponseDTO);
    }

    public long getTotalEventsCount() {
        logger.debug("Fetching total events count");
        return eventRepository.count();
    }

    @Transactional
    public void updateEventImages(Long eventId, List<String> imageUrls) {
        logger.info("Updating images for event id: {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomExceptions.UserNotFoundException("Event not found with id: " + eventId));

        event.setImageUrls(imageUrls);
        eventRepository.save(event);
        logger.info("Images updated successfully for event id: {}", eventId);
    }

    private EventResponseDTO convertToResponseDTO(Event event) {
        EventResponseDTO dto = new EventResponseDTO();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setImageUrls(event.getImageUrls());
        dto.setEventCategory(event.getEventCategory());
        dto.setEventType(event.getEventType());
        dto.setEventDate(event.getEventDate());
        dto.setPhoneNumber(event.getPhoneNumber());
        dto.setContactEmail(event.getContactEmail());
        dto.setGuidePrice(event.getGuidePrice());
        dto.setTicketPrice(event.getTicketPrice());
        dto.setLocation(event.getLocation());
        dto.setDuration(event.getDuration());
        dto.setMuseumId(event.getMuseum().getId());
        dto.setMuseumName(event.getMuseum().getName());
        return dto;
    }
}