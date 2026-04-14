package com.example.museum_backend.exceptions;

public class CustomExceptions extends RuntimeException {

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidOldPasswordException extends RuntimeException {
        public InvalidOldPasswordException(String message) {
            super(message);
        }
    }

    public static class InvalidNewPasswordException extends RuntimeException {
        public InvalidNewPasswordException(String message) {
            super(message);
        }
    }

    public static class InvalidPassword extends RuntimeException {
        public InvalidPassword(String message) {
            super(message);
        }
    }

    public static class PasswordMismatchException extends RuntimeException {
        public PasswordMismatchException(String message) {
            super(message);
        }
    }

    public static class InvalidPinExceptions extends RuntimeException {
        public InvalidPinExceptions(String message) {
            super(message);
        }
    }

    public static class UserNotActiveException extends RuntimeException {
        public UserNotActiveException(String message) {
            super(message);
        }
    }

    public static class NotVerifiedMailException extends RuntimeException {
        public NotVerifiedMailException(String message) {
            super(message);
        }
    }

    public static class AlreadyVerifiedEmail extends RuntimeException {
        public AlreadyVerifiedEmail(String message) {
            super(message);
        }
    }

}

