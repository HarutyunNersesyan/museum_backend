package com.example.museum_backend;

import com.example.museum_backend.config.FileStorageProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(FileStorageProperties.class)
public class MuseumBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MuseumBackendApplication.class, args);
    }

}