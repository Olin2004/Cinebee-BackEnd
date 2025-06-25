package com.cinebee.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST(400, "Bad request", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR(500, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),

    // User-related errors
    USER_EXISTED(400, "User already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(404, "User does not exist", HttpStatus.NOT_FOUND),
    USERNAME_INVALID(1001, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1002, "Invalid email format", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1003, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH(1004, "Password and confirm password do not match", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1005, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),

    // Auth / Token errors
    TOKEN_NOT_FOUND(2001, "Token not found", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED(2002, "Token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(2003, "Invalid token", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED(2004, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(2005, "You do not have permission", HttpStatus.FORBIDDEN),

    // File upload / download errors
    FILE_EMPTY(3001, "File must not be empty", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(3002, "File size exceeds the limit", HttpStatus.BAD_REQUEST),
    FILE_TYPE_NOT_SUPPORTED(3003, "Unsupported file type", HttpStatus.BAD_REQUEST),

    // Validation errors
    REQUIRED_FIELD_MISSING(4001, "Required field is missing: {field}", HttpStatus.BAD_REQUEST),
    INVALID_ENUM_VALUE(4002, "Invalid value for field: {field}", HttpStatus.BAD_REQUEST),
    VALUE_OUT_OF_RANGE(4003, "Value of {field} must be between {min} and {max}", HttpStatus.BAD_REQUEST),
    STRING_TOO_SHORT(4004, "Length of {field} must be at least {min} characters", HttpStatus.BAD_REQUEST),
    STRING_TOO_LONG(4005, "Length of {field} must be at most {max} characters", HttpStatus.BAD_REQUEST),
    NUMBER_TOO_SMALL(4006, "Value of {field} must be at least {min}", HttpStatus.BAD_REQUEST),
    NUMBER_TOO_LARGE(4007, "Value of {field} must be at most {max}", HttpStatus.BAD_REQUEST),
    INVALID_DATE_FORMAT(4008, "Invalid date format for {field}. Expected format: {format}", HttpStatus.BAD_REQUEST),

    // Database errors
    DATA_INTEGRITY_VIOLATION(5001, "Data integrity violation", HttpStatus.CONFLICT),
    DUPLICATE_KEY(5002, "Duplicate key value violates unique constraint", HttpStatus.CONFLICT),

    // Other possible errors
    SERVICE_UNAVAILABLE(6001, "Service is temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    TOO_MANY_REQUESTS(6002, "Too many requests - try again later", HttpStatus.TOO_MANY_REQUESTS);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
        this.errorCode = name();
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
    private final String errorCode;
}