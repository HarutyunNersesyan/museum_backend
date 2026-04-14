package com.example.museum_backend.model.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SignUp {


    @NotNull(message = "User name cannot be null")
    private String userName;

    @Email(message = "Email should be valid")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$",
            message = "Email should be a valid Gmail address (example@gmail.com)"
    )
    private String email;

    private String password;

    private String repeatPassword;

}

