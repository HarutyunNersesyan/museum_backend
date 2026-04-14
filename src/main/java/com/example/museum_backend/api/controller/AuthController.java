package com.example.museum_backend.api.controller;

import com.example.museum_backend.api.security.JwtTokenUtils;
import com.example.museum_backend.model.dto.TokenResponse;
import com.example.museum_backend.model.dto.UserDto;
import com.example.museum_backend.model.entity.User;
import com.example.museum_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/account")
public class AuthController {

    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(UserService userService, JwtTokenUtils jwtTokenUtils, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtTokenUtils = jwtTokenUtils;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody UserDto userDto) {
        try {
            User user = userService.findUserByEmail(userDto.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));


            UserDetails userDetails = userService.loadUserByUsername(userDto.getEmail());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    userDto.getPassword()
            ));
            String token = jwtTokenUtils.generateToken(userDetails);
            return ResponseEntity.ok(new TokenResponse(200, token, user.getUserName()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong email or password");
        }
    }

    @GetMapping("/profile/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
    }
}