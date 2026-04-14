package com.example.museum_backend.service;

import com.example.museum_backend.exceptions.CustomExceptions;
import com.example.museum_backend.model.dto.EventResponseDTO;
import com.example.museum_backend.model.dto.MuseumRequestDTO;
import com.example.museum_backend.model.dto.MuseumResponseDTO;
import com.example.museum_backend.model.entity.Museum;
import com.example.museum_backend.repository.MuseumRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MuseumService {

    private static final Logger logger = LoggerFactory.getLogger(MuseumService.class);
    private final MuseumRepository museumRepository;
    private final EventService eventService;

    @Transactional
    public MuseumResponseDTO createMuseum(MuseumRequestDTO request, String adminEmail) {
        logger.info("Creating museum by admin: {}", adminEmail);

        Museum museum = new Museum();
        museum.setName(request.getName());

        Museum savedMuseum = museumRepository.save(museum);
        logger.info("Museum created successfully with id: {}", savedMuseum.getId());

        return convertToResponseDTO(savedMuseum);
    }

    @Transactional
    public MuseumResponseDTO updateMuseum(Long id, MuseumRequestDTO request) {
        logger.info("Updating museum with id: {}", id);

        Museum museum = museumRepository.findById(id)
                .orElseThrow(() -> new CustomExceptions.UserNotFoundException("Museum not found with id: " + id));

        museum.setName(request.getName());

        Museum updatedMuseum = museumRepository.save(museum);
        logger.info("Museum updated successfully with id: {}", id);

        return convertToResponseDTO(updatedMuseum);
    }

    @Transactional
    public void deleteMuseum(Long id) {
        logger.info("Deleting museum with id: {}", id);

        Museum museum = museumRepository.findById(id)
                .orElseThrow(() -> new CustomExceptions.UserNotFoundException("Museum not found with id: " + id));

        museumRepository.delete(museum);
        logger.info("Museum deleted successfully with id: {}", id);
    }

    public MuseumResponseDTO getMuseumById(Long id) {
        logger.debug("Fetching museum by id: {}", id);

        Museum museum = museumRepository.findById(id)
                .orElseThrow(() -> new CustomExceptions.UserNotFoundException("Museum not found with id: " + id));

        return convertToResponseDTO(museum);
    }

    public Page<MuseumResponseDTO> getAllMuseums(Pageable pageable) {
        logger.debug("Fetching all museums with pagination");

        return museumRepository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    public long getTotalMuseumsCount() {
        logger.debug("Fetching total museums count");
        return museumRepository.count();
    }

    private MuseumResponseDTO convertToResponseDTO(Museum museum) {
        MuseumResponseDTO dto = new MuseumResponseDTO();
        dto.setId(museum.getId());
        dto.setName(museum.getName());
        return dto;
    }
}