package com.example.museum_backend.service;

import com.example.museum_backend.exceptions.CustomExceptions;
import com.example.museum_backend.model.dto.EventResponseDTO;
import com.example.museum_backend.model.dto.EventRequestDTO;
import com.example.museum_backend.model.dto.EventSearchDTO;
import com.example.museum_backend.model.entity.Event;
import com.example.museum_backend.model.entity.Museum;
import com.example.museum_backend.model.enums.EventCategory;
import com.example.museum_backend.model.enums.Location;
import com.example.museum_backend.repository.EventRepository;
import com.example.museum_backend.repository.MuseumRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);
    private final EventRepository eventRepository;
    private final MuseumRepository museumRepository;

    @Transactional
    public EventResponseDTO createEvent(EventRequestDTO request, String adminEmail) {
        logger.info("Creating event by admin: {}", adminEmail);

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

        return convertToResponseDTO(savedEvent);
    }

    @Transactional
    public EventResponseDTO updateEvent(Long id, EventRequestDTO request) {
        logger.info("Updating event with id: {}", id);

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

    public Page<EventResponseDTO> searchEventsByMuseumName(String museumName, Pageable pageable) {
        logger.debug("Searching events by museum name: {}", museumName);

        if (!StringUtils.hasText(museumName)) {
            return getAllEvents(pageable);
        }

        List<Event> allEvents = eventRepository.findAll();

        String searchName = museumName.toLowerCase().trim();

        List<Event> filteredEvents = allEvents.stream()
                .filter(event -> event.getMuseum() != null &&
                        event.getMuseum().getName() != null &&
                        event.getMuseum().getName().toLowerCase().contains(searchName))
                .collect(Collectors.toList());

        applySorting(filteredEvents, pageable.getSort());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredEvents.size());

        if (start >= filteredEvents.size()) {
            return new PageImpl<>(List.of(), pageable, filteredEvents.size());
        }

        List<EventResponseDTO> paginatedResults = filteredEvents.subList(start, end)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        logger.info("Found {} events for museum name containing: {}", filteredEvents.size(), museumName);

        return new PageImpl<>(paginatedResults, pageable, filteredEvents.size());
    }

    public Page<EventResponseDTO> searchEventsByExactMuseumName(String museumName, Pageable pageable) {
        logger.debug("Searching events by exact museum name: {}", museumName);

        if (!StringUtils.hasText(museumName)) {
            return getAllEvents(pageable);
        }

        List<Event> allEvents = eventRepository.findAll();

        List<Event> filteredEvents = allEvents.stream()
                .filter(event -> event.getMuseum() != null &&
                        event.getMuseum().getName() != null &&
                        event.getMuseum().getName().equalsIgnoreCase(museumName.trim()))
                .collect(Collectors.toList());

        applySorting(filteredEvents, pageable.getSort());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredEvents.size());

        if (start >= filteredEvents.size()) {
            return new PageImpl<>(List.of(), pageable, filteredEvents.size());
        }

        List<EventResponseDTO> paginatedResults = filteredEvents.subList(start, end)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        logger.info("Found {} events for exact museum name: {}", filteredEvents.size(), museumName);

        return new PageImpl<>(paginatedResults, pageable, filteredEvents.size());
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

    public Page<EventResponseDTO> searchEvents(EventSearchDTO searchDTO, Pageable pageable) {
        logger.debug("Searching events with filters: {}", searchDTO);

        List<Event> allEvents = eventRepository.findAll();

        List<Event> filteredEvents = allEvents.stream()
                .filter(event -> filterBySearchQuery(event, searchDTO.getQuery()))
                .filter(event -> filterByCategory(event, searchDTO.getEventCategory()))
                .filter(event -> filterByLocation(event, searchDTO.getLocation()))
                .filter(event -> filterByMuseumId(event, searchDTO.getMuseumId()))
                .filter(event -> filterByMuseumName(event, searchDTO.getMuseumName()))  // NEW filter
                .filter(event -> filterByGuidePrice(event, searchDTO.getMinGuidePrice(), searchDTO.getMaxGuidePrice()))
                .filter(event -> filterByTicketPrice(event, searchDTO.getMinTicketPrice(), searchDTO.getMaxTicketPrice()))
                .filter(event -> filterByDateRange(event, searchDTO.getStartDateFrom(), searchDTO.getStartDateTo()))
                .filter(event -> filterByEventType(event, searchDTO.getEventType()))
                .filter(event -> filterByDuration(event, searchDTO.getMinDuration(), searchDTO.getMaxDuration()))
                .collect(Collectors.toList());

        applySorting(filteredEvents, pageable.getSort());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredEvents.size());

        List<EventResponseDTO> paginatedResults = filteredEvents.subList(
                        Math.min(start, filteredEvents.size()),
                        Math.min(end, filteredEvents.size())
                ).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        logger.info("Search completed. Found {} events, returning {} results",
                filteredEvents.size(), paginatedResults.size());

        return new PageImpl<>(paginatedResults, pageable, filteredEvents.size());
    }

    /**
     * Search by ticket price range only
     */
    public Page<EventResponseDTO> searchByTicketPrice(Integer minPrice, Integer maxPrice, Pageable pageable) {
        logger.debug("Searching events by ticket price range: {} - {}", minPrice, maxPrice);

        List<Event> allEvents = eventRepository.findAll();

        List<Event> filteredEvents = allEvents.stream()
                .filter(event -> filterByTicketPrice(event, minPrice, maxPrice))
                .collect(Collectors.toList());

        applySorting(filteredEvents, pageable.getSort());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredEvents.size());

        List<EventResponseDTO> paginatedResults = filteredEvents.subList(
                        Math.min(start, filteredEvents.size()),
                        Math.min(end, filteredEvents.size())
                ).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(paginatedResults, pageable, filteredEvents.size());
    }

    /**
     * Combined search by guide price and ticket price
     */
    public Page<EventResponseDTO> searchByBothPrices(Integer minGuidePrice, Integer maxGuidePrice,
                                                     Integer minTicketPrice, Integer maxTicketPrice,
                                                     Pageable pageable) {
        logger.debug("Searching events by guide price: {}-{} and ticket price: {}-{}",
                minGuidePrice, maxGuidePrice, minTicketPrice, maxTicketPrice);

        List<Event> allEvents = eventRepository.findAll();

        List<Event> filteredEvents = allEvents.stream()
                .filter(event -> filterByGuidePrice(event, minGuidePrice, maxGuidePrice))
                .filter(event -> filterByTicketPrice(event, minTicketPrice, maxTicketPrice))
                .collect(Collectors.toList());

        applySorting(filteredEvents, pageable.getSort());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredEvents.size());

        List<EventResponseDTO> paginatedResults = filteredEvents.subList(
                        Math.min(start, filteredEvents.size()),
                        Math.min(end, filteredEvents.size())
                ).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(paginatedResults, pageable, filteredEvents.size());
    }

    /**
     * Simple search by query string (name or description)
     */
    public Page<EventResponseDTO> simpleSearch(String query, Pageable pageable) {
        logger.debug("Simple search for query: {}", query);

        List<Event> allEvents = eventRepository.findAll();

        List<Event> filteredEvents = allEvents.stream()
                .filter(event -> matchesQuery(event, query))
                .collect(Collectors.toList());

        applySorting(filteredEvents, pageable.getSort());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredEvents.size());

        List<EventResponseDTO> paginatedResults = filteredEvents.subList(
                        Math.min(start, filteredEvents.size()),
                        Math.min(end, filteredEvents.size())
                ).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(paginatedResults, pageable, filteredEvents.size());
    }

    /**
     * Get upcoming events (events with date >= current date)
     */
    public Page<EventResponseDTO> getUpcomingEvents(Pageable pageable) {
        logger.debug("Fetching upcoming events");

        LocalDateTime now = LocalDateTime.now();

        List<Event> allEvents = eventRepository.findAll();

        List<Event> upcomingEvents = allEvents.stream()
                .filter(event -> event.getEventDate() != null && event.getEventDate().isAfter(now))
                .sorted((e1, e2) -> e1.getEventDate().compareTo(e2.getEventDate()))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), upcomingEvents.size());

        List<EventResponseDTO> paginatedResults = upcomingEvents.subList(
                        Math.min(start, upcomingEvents.size()),
                        Math.min(end, upcomingEvents.size())
                ).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(paginatedResults, pageable, upcomingEvents.size());
    }

    /**
     * Get events by category
     */
    public Page<EventResponseDTO> getEventsByCategory(EventCategory category, Pageable pageable) {
        logger.debug("Fetching events by category: {}", category);

        List<Event> allEvents = eventRepository.findAll();

        List<Event> filteredEvents = allEvents.stream()
                .filter(event -> event.getEventCategory() == category)
                .collect(Collectors.toList());

        applySorting(filteredEvents, pageable.getSort());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredEvents.size());

        List<EventResponseDTO> paginatedResults = filteredEvents.subList(
                        Math.min(start, filteredEvents.size()),
                        Math.min(end, filteredEvents.size())
                ).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(paginatedResults, pageable, filteredEvents.size());
    }

    /**
     * Get events by location
     */
    public Page<EventResponseDTO> getEventsByLocation(Location location, Pageable pageable) {
        logger.debug("Fetching events by location: {}", location);

        List<Event> allEvents = eventRepository.findAll();

        List<Event> filteredEvents = allEvents.stream()
                .filter(event -> event.getLocation() == location)
                .collect(Collectors.toList());

        applySorting(filteredEvents, pageable.getSort());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredEvents.size());

        List<EventResponseDTO> paginatedResults = filteredEvents.subList(
                        Math.min(start, filteredEvents.size()),
                        Math.min(end, filteredEvents.size())
                ).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(paginatedResults, pageable, filteredEvents.size());
    }

    /**
     * Get events by guide price range
     */
    public Page<EventResponseDTO> getEventsByGuidePriceRange(Integer minPrice, Integer maxPrice, Pageable pageable) {
        logger.debug("Fetching events by guide price range: {} - {}", minPrice, maxPrice);

        List<Event> allEvents = eventRepository.findAll();

        List<Event> filteredEvents = allEvents.stream()
                .filter(event -> filterByGuidePrice(event, minPrice, maxPrice))
                .collect(Collectors.toList());

        applySorting(filteredEvents, pageable.getSort());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredEvents.size());

        List<EventResponseDTO> paginatedResults = filteredEvents.subList(
                        Math.min(start, filteredEvents.size()),
                        Math.min(end, filteredEvents.size())
                ).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(paginatedResults, pageable, filteredEvents.size());
    }

    /**
     * Get events by ticket price range
     */
    public Page<EventResponseDTO> getEventsByTicketPriceRange(Integer minPrice, Integer maxPrice, Pageable pageable) {
        logger.debug("Fetching events by ticket price range: {} - {}", minPrice, maxPrice);

        List<Event> allEvents = eventRepository.findAll();

        List<Event> filteredEvents = allEvents.stream()
                .filter(event -> filterByTicketPrice(event, minPrice, maxPrice))
                .collect(Collectors.toList());

        applySorting(filteredEvents, pageable.getSort());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredEvents.size());

        List<EventResponseDTO> paginatedResults = filteredEvents.subList(
                        Math.min(start, filteredEvents.size()),
                        Math.min(end, filteredEvents.size())
                ).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(paginatedResults, pageable, filteredEvents.size());
    }

    private boolean filterBySearchQuery(Event event, String query) {
        if (!StringUtils.hasText(query)) return true;

        String lowerQuery = query.toLowerCase().trim();
        return (event.getName() != null && event.getName().toLowerCase().contains(lowerQuery)) ||
                (event.getDescription() != null && event.getDescription().toLowerCase().contains(lowerQuery));
    }

    private boolean matchesQuery(Event event, String query) {
        return filterBySearchQuery(event, query);
    }

    private boolean filterByCategory(Event event, EventCategory category) {
        if (category == null) return true;
        return event.getEventCategory() == category;
    }

    private boolean filterByLocation(Event event, Location location) {
        if (location == null) return true;
        return event.getLocation() == location;
    }

    private boolean filterByMuseumId(Event event, Long museumId) {
        if (museumId == null) return true;
        return event.getMuseum() != null && event.getMuseum().getId().equals(museumId);
    }

    private boolean filterByMuseumName(Event event, String museumName) {
        if (!StringUtils.hasText(museumName)) return true;

        if (event.getMuseum() == null || event.getMuseum().getName() == null) {
            return false;
        }

        return event.getMuseum().getName().toLowerCase().contains(museumName.toLowerCase().trim());
    }

    private boolean filterByGuidePrice(Event event, Integer minPrice, Integer maxPrice) {
        Integer guidePrice = event.getGuidePrice();
        if (guidePrice == null) return true;

        if (minPrice != null && guidePrice < minPrice) return false;
        if (maxPrice != null && guidePrice > maxPrice) return false;
        return true;
    }

    private boolean filterByTicketPrice(Event event, Integer minPrice, Integer maxPrice) {
        Integer ticketPrice = event.getTicketPrice();
        if (ticketPrice == null) return true;

        if (minPrice != null && ticketPrice < minPrice) return false;
        if (maxPrice != null && ticketPrice > maxPrice) return false;
        return true;
    }

    private boolean filterByDateRange(Event event, LocalDateTime from, LocalDateTime to) {
        LocalDateTime eventDate = event.getEventDate();
        if (eventDate == null) return true;

        if (from != null && eventDate.isBefore(from)) return false;
        if (to != null && eventDate.isAfter(to)) return false;
        return true;
    }

    private boolean filterByEventType(Event event, String eventType) {
        if (!StringUtils.hasText(eventType)) return true;
        return event.getEventType() != null && event.getEventType().name().equalsIgnoreCase(eventType);
    }

    private boolean filterByDuration(Event event, Integer minDuration, Integer maxDuration) {
        Integer duration = event.getDuration();
        if (duration == null) return true;

        if (minDuration != null && duration < minDuration) return false;
        if (maxDuration != null && duration > maxDuration) return false;
        return true;
    }


    private void applySorting(List<Event> events, Sort sort) {
        if (sort == null || !sort.iterator().hasNext()) return;

        sort.forEach(order -> {
            String property = order.getProperty();
            boolean isAscending = order.isAscending();

            events.sort((e1, e2) -> {
                int comparison = 0;

                switch (property) {
                    case "eventDate":
                        comparison = compareDates(e1.getEventDate(), e2.getEventDate());
                        break;
                    case "guidePrice":
                        comparison = compareIntegers(e1.getGuidePrice(), e2.getGuidePrice());
                        break;
                    case "ticketPrice":
                        comparison = compareIntegers(e1.getTicketPrice(), e2.getTicketPrice());
                        break;
                    case "name":
                        comparison = compareStrings(e1.getName(), e2.getName());
                        break;
                    case "duration":
                        comparison = compareIntegers(e1.getDuration(), e2.getDuration());
                        break;
                    case "location":
                        comparison = compareStrings(
                                e1.getLocation() != null ? e1.getLocation().name() : null,
                                e2.getLocation() != null ? e2.getLocation().name() : null
                        );
                        break;
                    case "eventCategory":
                        comparison = compareStrings(
                                e1.getEventCategory() != null ? e1.getEventCategory().name() : null,
                                e2.getEventCategory() != null ? e2.getEventCategory().name() : null
                        );
                        break;
                    default:
                        comparison = 0;
                }

                return isAscending ? comparison : -comparison;
            });
        });
    }

    private int compareDates(LocalDateTime d1, LocalDateTime d2) {
        if (d1 == null && d2 == null) return 0;
        if (d1 == null) return 1;
        if (d2 == null) return -1;
        return d1.compareTo(d2);
    }

    private int compareIntegers(Integer i1, Integer i2) {
        if (i1 == null && i2 == null) return 0;
        if (i1 == null) return 1;
        if (i2 == null) return -1;
        return i1.compareTo(i2);
    }

    private int compareStrings(String s1, String s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return 1;
        if (s2 == null) return -1;
        return s1.compareToIgnoreCase(s2);
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
        if (event.getMuseum() != null) {
            dto.setMuseumId(event.getMuseum().getId());
            dto.setMuseumName(event.getMuseum().getName());
        }
        return dto;
    }
}