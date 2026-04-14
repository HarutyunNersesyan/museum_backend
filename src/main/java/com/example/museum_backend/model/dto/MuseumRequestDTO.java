package com.example.museum_backend.model.dto;

import com.example.museum_backend.model.enums.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MuseumRequestDTO {
    private String name;
    private List<Long> eventIds;
}