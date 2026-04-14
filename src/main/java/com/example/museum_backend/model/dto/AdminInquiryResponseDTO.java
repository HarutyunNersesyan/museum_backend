package com.example.museum_backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminInquiryResponseDTO {

    @NotBlank(message = "Response message is required")
    private String response;
}