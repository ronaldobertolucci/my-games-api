package io.github.ronaldobertolucci.mygames.infra.exception;

import jakarta.persistence.EntityNotFoundException;
import org.hibernate.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

@RestControllerAdvice
public class MyGamesExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handle400(MethodArgumentNotValidException ex) {
        var errors = ex.getFieldErrors();
        return ResponseEntity.badRequest().body(errors.stream().map(ErrorValidation::new).toList());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity handle400(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body(new ErrorValidation(ex.getParameterName(), ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handle400(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity handle400(TypeMismatchException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity handle400(MultipartException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity handle401() {
        return ResponseEntity.status(401).build();
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity handle403() {
        return ResponseEntity.status(403).build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity handle404Entity() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(JpaObjectRetrievalFailureException.class)
    public ResponseEntity handle404Jpa() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ObjectRetrievalFailureException.class)
    public ResponseEntity handle404Hibernate() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handle409(org.springframework.dao.DataIntegrityViolationException ex) {
        return new ResponseEntity<>(ex.getMostSpecificCause().getMessage(),  HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnprocessableEntity.class)
    public ResponseEntity handle422(UnprocessableEntity ex) {
        return ResponseEntity.status(422).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handle500() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
    }

    private record ErrorValidation(String field, String message) {
        public ErrorValidation(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
}