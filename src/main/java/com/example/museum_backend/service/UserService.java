package com.example.museum_backend.service;

import com.example.museum_backend.exceptions.CustomExceptions;
import com.example.museum_backend.model.dto.DeleteAccount;
import com.example.museum_backend.model.dto.ForgotPassword;
import com.example.museum_backend.model.dto.SignUp;
import com.example.museum_backend.model.dto.Verify;
import com.example.museum_backend.model.entity.User;
import com.example.museum_backend.model.enums.Role;
import com.example.museum_backend.repository.UserRepository;
import com.example.museum_backend.util.LoggingUtil;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderService mailSenderService;

    @Autowired
    public UserService(UserRepository userRepository,
                       @Lazy AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder,
                       @Lazy MailSenderService mailSenderService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.mailSenderService = mailSenderService;
    }

    public Optional<User> getUserById(Long id) {
        logger.debug("Fetching user by ID: {}", id);
        return userRepository.findById(id);
    }

    public List<User> getAll() {
        logger.debug("Fetching all users");
        return userRepository.findAll();
    }

    public User signUp(@Valid User user) {
        LoggingUtil.logInfo(UserService.class, "Starting user signup for email: {}", user.getEmail());

        try {
            user.setRole(Role.USER);
            user.setPassword(hashPassword(user.getPassword()));
            user.setVerifyMail(false);

            User savedUser = userRepository.save(user);

            LoggingUtil.logUserAction(user.getUserName(), "SIGNUP", "User registered successfully");
            logger.info("User signup completed successfully for: {}. User will be deleted if not verified within 6 minutes 30 seconds",
                    user.getEmail());

            return savedUser;

        } catch (Exception e) {
            LoggingUtil.logError(UserService.class, "Error during user signup for email: {}", user.getEmail(), e);
            throw e;
        }
    }

    public Page<User> getUsersWithPaginationAndSorting(int offset, int pageSize, String field) {
        logger.debug("Fetching users with pagination - offset: {}, pageSize: {}, field: {}", offset, pageSize, field);
        return userRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field)));
    }

    public User update(User user) {
        logger.debug("Updating user: {}", user.getUserName());
        return userRepository.save(user);
    }

    public void delete(DeleteAccount deleteAccount) {
        logger.debug("Attempting to delete account for email: {}", deleteAccount.getEmail());
        Optional<User> userOptional = userRepository.findUserByEmail(deleteAccount.getEmail());

        if (userOptional.isEmpty()) {
            logger.warn("User not found for deletion: {}", deleteAccount.getEmail());
            throw new CustomExceptions.UserNotFoundException("User not found.");
        }

        User user = userOptional.get();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), deleteAccount.getPassword()));
        } catch (Exception e) {
            logger.warn("Invalid password during account deletion for: {}", deleteAccount.getEmail());
            throw new CustomExceptions.InvalidPassword("Invalid password");
        }

        userRepository.delete(user);
        logger.info("Account deleted successfully for: {}", deleteAccount.getEmail());
    }

    public void deleteVerify(String email) {
        logger.debug("Deleting unverified user: {}", email);
        Optional<User> userOptional = userRepository.findUserByEmail(email);

        if (userOptional.isEmpty()) {
            logger.warn("User not found for deletion: {}", email);
            throw new CustomExceptions.UserNotFoundException("User not found.");
        }

        userRepository.delete(userOptional.get());
        logger.info("Unverified user deleted: {}", email);
    }

    public String verify(Verify verify) {
        logger.debug("Verifying email: {}", verify.getEmail());
        Optional<User> userOptional = findUserByEmail(verify.getEmail());

        if (userOptional.isEmpty()) {
            logger.warn("User not found for verification: {}", verify.getEmail());
            throw new CustomExceptions.UserNotFoundException("User not found.");
        }

        User user = userOptional.get();

        if (user.getVerifyMail()) {
            logger.warn("Email already verified: {}", verify.getEmail());
            throw new CustomExceptions.AlreadyVerifiedEmail("Your email is already verified.");
        }

        if (user.getPin() == null || user.getPin().trim().isEmpty()) {
            logger.warn("No PIN found for user: {}", verify.getEmail());
            throw new CustomExceptions.InvalidPinExceptions("No verification code found. Please request a new one.");
        }

        if (!user.getPin().equals(verify.getPin())) {
            logger.warn("Invalid PIN for email: {}. Expected: {}, Got: {}",
                    verify.getEmail(), user.getPin(), verify.getPin());
            throw new CustomExceptions.InvalidPinExceptions("Invalid verification code.");
        }


        user.setVerifyMail(true);
        user.setPin(null);
        update(user);

        logger.info("Email verified successfully: {}", verify.getEmail());
        return "Email verified successfully";
    }

    public String hashPassword(String newPassword) {
        return passwordEncoder.encode(newPassword);
    }

    public Optional<User> findUserByUserName(String userName) {
        logger.debug("Finding user by username: {}", userName);
        return userRepository.findUserByUserName(userName);
    }

    public String changePassword(String email, String oldPassword, String newPassword, String newPasswordRepeat) {
        logger.debug("Attempting password change for user: {}", email);

        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        if (optionalUser.isEmpty()) {
            logger.warn("User not found for password change: {}", email);
            throw new CustomExceptions.UserNotFoundException("User not found.");
        }

        User user = optionalUser.get();

        if (!user.getVerifyMail()) {
            logger.warn("Password change attempted for unverified email: {}", email);
            throw new CustomExceptions.NotVerifiedMailException("Email must be verified");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, oldPassword));
        } catch (Exception e) {
            logger.warn("Invalid old password for user: {}", email);
            throw new CustomExceptions.InvalidOldPasswordException("Old password is incorrect.");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            logger.warn("New password same as current for user: {}", email);
            throw new CustomExceptions.InvalidNewPasswordException("New password cannot be the same as the current password.");
        }

        if (!newPassword.equals(newPasswordRepeat)) {
            logger.warn("Password mismatch for user: {}", email);
            throw new CustomExceptions.PasswordMismatchException("New passwords do not match.");
        }

        user.setPassword(hashPassword(newPassword));
        userRepository.save(user);
        logger.info("Password changed successfully for user: {}", email);

        return "Password updated successfully.";
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Loading user by username (email): {}", email);

        User user = userRepository.findUserByEmail(email).orElseThrow(() -> {
            logger.error("User not found for authentication: {}", email);
            return new UsernameNotFoundException("User not found with email: " + email);
        });

        String role = user.getRole().name();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(role))
        );
    }

    public Optional<User> findUserByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        return userRepository.findUserByEmail(email);
    }

    public String forgotPassword(ForgotPassword forgotPassword) {
        logger.debug("Processing forgot password for: {}", forgotPassword.getEmail());
        Optional<User> optionalUser = findUserByEmail(forgotPassword.getEmail());

        if (optionalUser.isEmpty()) {
            logger.warn("User not found for forgot password: {}", forgotPassword.getEmail());
            throw new CustomExceptions.UserNotFoundException("User not found with email: " + forgotPassword.getEmail());
        }

        User existingUser = optionalUser.get();

        if (!existingUser.getVerifyMail()) {
            logger.warn("Forgot password attempted for unverified email: {}", forgotPassword.getEmail());
            throw new CustomExceptions.NotVerifiedMailException("Email must be verified");
        }

        String newPassword = forgotPassword.generatePassword();
        existingUser.setPassword(hashPassword(newPassword));
        update(existingUser);

        mailSenderService.sendEmail(forgotPassword.getEmail(), "New password", newPassword);
        logger.info("New password sent to email: {}", forgotPassword.getEmail());

        return "New password has been sent to your email";
    }

    public Optional<User> getUserByUserName(String userName) {
        logger.debug("Getting user by username: {}", userName);
        return userRepository.findUserByUserName(userName);
    }

    @PostConstruct
    public void fillDB() {
        logger.info("Starting to fill database with sample users");

        User admin = new User(new SignUp("admin", "admin@gmail.com", hashPassword("Admin777#"),
                hashPassword("Admin777#")));
        admin.setRole(Role.ADMIN);
        admin.setVerifyMail(true);
        userRepository.save(admin);


        User user = new User(new SignUp("user2026", "harut.037n@gmail.com", hashPassword("User777#"),
                hashPassword("User777#")));
        user.setRole(Role.USER);
        user.setVerifyMail(true);
        userRepository.save(user);
    }
}