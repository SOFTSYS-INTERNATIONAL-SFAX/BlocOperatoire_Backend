package com.tn.softsys.blocoperatoire.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /* ===================================================== */
    /* ================= 404 NOT FOUND ===================== */
    /* ===================================================== */

    @ExceptionHandler({ResourceNotFoundException.class, EntityNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(Exception ex,
                                                   HttpServletRequest request) {

        log.warn("404 - {}", ex.getMessage());

        return buildResponse(HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request,
                null);
    }

    /* ===================================================== */
    /* ================= 400 VALIDATION ==================== */
    /* ===================================================== */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     HttpServletRequest request) {

        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        log.warn("400 - Validation error: {}", details);

        return buildResponse(HttpStatus.BAD_REQUEST,
                "Validation failed",
                request,
                details);
    }

    /* ===================================================== */
    /* ================= 400 CONSTRAINT ==================== */
    /* ===================================================== */

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex,
                                                     HttpServletRequest request) {

        log.warn("400 - Constraint violation: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request,
                null);
    }

    /* ===================================================== */
    /* ================= 400 DB INTEGRITY ================== */
    /* ===================================================== */

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex,
                                                        HttpServletRequest request) {

        log.error("400 - DB integrity violation", ex);

        return buildResponse(HttpStatus.BAD_REQUEST,
                "Database integrity violation",
                request,
                null);
    }

    /* ===================================================== */
    /* ================= 401 AUTH ========================== */
    /* ===================================================== */

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex,
                                                         HttpServletRequest request) {

        log.warn("401 - Bad credentials");

        return buildResponse(HttpStatus.UNAUTHORIZED,
                "Bad credentials",
                request,
                null);
    }

    /* ===================================================== */
    /* ================= 403 FORBIDDEN ===================== */
    /* ===================================================== */

    @ExceptionHandler({
            AccessDeniedException.class,
            AuthorizationDeniedException.class
    })
    public ResponseEntity<ApiError> handleAccessDenied(Exception ex,
                                                       HttpServletRequest request) {

        log.warn("403 - Access denied");

        return buildResponse(HttpStatus.FORBIDDEN,
                "Access denied",
                request,
                null);
    }

    /* ===================================================== */
    /* ================= 429 RATE LIMIT ==================== */
    /* ===================================================== */

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ApiError> handleTooManyRequests(TooManyRequestsException ex,
                                                          HttpServletRequest request) {

        log.warn("429 - Too many requests");

        return buildResponse(HttpStatus.TOO_MANY_REQUESTS,
                ex.getMessage(),
                request,
                null);
    }

    /* ===================================================== */
    /* ================= 500 GLOBAL ======================== */
    /* ===================================================== */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobal(Exception ex,
                                                 HttpServletRequest request) {

        log.error("500 - Unexpected error", ex);

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error occurred",
                request,
                null);
    }

    /* ===================================================== */
    /* ================= BUILDER METHOD ==================== */
    /* ===================================================== */

    private ResponseEntity<ApiError> buildResponse(HttpStatus status,
                                                   String message,
                                                   HttpServletRequest request,
                                                   List<String> details) {

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .details(details)
                .build();

        return new ResponseEntity<>(error, status);
    }
}