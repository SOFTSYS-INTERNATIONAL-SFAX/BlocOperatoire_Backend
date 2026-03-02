package com.tn.softsys.blocoperatoire.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    /* ============================
       404 - Resource Not Found
       ============================ */

    @ExceptionHandler({ResourceNotFoundException.class, EntityNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(Exception ex,
                                                   HttpServletRequest request) {

        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    /* ============================
       400 - Validation errors
       ============================ */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    /* ============================
       400 - Constraint violation
       ============================ */

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex,
                                                     HttpServletRequest request) {

        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    /* ============================
       400 - Data integrity (DB)
       ============================ */

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex,
                                                        HttpServletRequest request) {

        return buildResponse(HttpStatus.BAD_REQUEST,
                "Database integrity violation",
                request);
    }

    /* ============================
       401 - Authentication
       ============================ */

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex,
                                                         HttpServletRequest request) {

        return buildResponse(HttpStatus.UNAUTHORIZED,
                "Bad credentials",
                request);
    }

    /* ============================
       403 - Authorization
       ============================ */

    @ExceptionHandler({
            AccessDeniedException.class,
            AuthorizationDeniedException.class
    })
    public ResponseEntity<ApiError> handleAccessDenied(Exception ex,
                                                       HttpServletRequest request) {

        return buildResponse(HttpStatus.FORBIDDEN,
                "Access denied",
                request);
    }

    /* ============================
       429 - Too Many Requests
       ============================ */

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ApiError> handleTooManyRequests(TooManyRequestsException ex,
                                                          HttpServletRequest request) {

        return buildResponse(HttpStatus.TOO_MANY_REQUESTS,
                ex.getMessage(),
                request);
    }

    /* ============================
       500 - Fallback
       ============================ */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobal(Exception ex,
                                                 HttpServletRequest request) {

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error occurred",
                request);
    }

    /* ============================
       Builder
       ============================ */

    private ResponseEntity<ApiError> buildResponse(HttpStatus status,
                                                   String message,
                                                   HttpServletRequest request) {

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, status);
    }
}