package com.example.museum_backend.model.entity;

import com.example.museum_backend.model.dto.SignUp;
import com.example.museum_backend.model.enums.Role;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_entity")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "userId"
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotNull(message = "User name cannot be null")
    @Column(name = "user_name", nullable = false, unique = true, length = 50)
    private String userName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    @Email(message = "Email should be valid")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$",
            message = "Email should be a valid Gmail address (example@gmail.com)"
    )
    private String email;

    @NotNull(message = "Password should be valid and can`t be empty")
    @Column(name = "password", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "verify_mail")
    private Boolean verifyMail;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Pattern(regexp = "^[1-9][0-9]{5}$")
    @Column(name = "pin", length = 6)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String pin;



    public User(SignUp signUp) {
        this.userName = signUp.getUserName();
        this.email = signUp.getEmail();
        this.password = signUp.getPassword();
        this.verifyMail = false;
        this.role = Role.USER;
    }
}