package com.solbeg.newsservice.exception.handler;

import com.solbeg.newsservice.exception.AccessException;
import com.solbeg.newsservice.exception.CreateObjectException;
import com.solbeg.newsservice.exception.NotFoundException;
import com.solbeg.newsservice.exception.ParsingException;
import com.solbeg.newsservice.exception.TimePeriodException;
import com.solbeg.newsservice.exception.model.IncorrectData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class NewsServiceExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<IncorrectData> exception(Exception exception) throws Exception {
        if (exception instanceof AccessDeniedException || exception instanceof AuthenticationException) {
            throw exception;
        }
        return getResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TimePeriodException.class)
    public ResponseEntity<IncorrectData> timePeriodException(TimePeriodException exception) {
        return getResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ParsingException.class)
    public ResponseEntity<IncorrectData> parsingException(ParsingException exception) {
        return getResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<IncorrectData> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        return getResponse("UUID was entered incorrectly!", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<IncorrectData> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return getResponse(errors.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<IncorrectData> notFoundException(NotFoundException exception) {
        return getResponse(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CreateObjectException.class)
    public ResponseEntity<IncorrectData> createObjectException(CreateObjectException exception) {
        return getResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessException.class)
    public ResponseEntity<IncorrectData> accessDeniedException(AccessException exception) {
        return getResponse(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<IncorrectData> getResponse(String message, HttpStatus status) {
        IncorrectData incorrectData = new IncorrectData(LocalDateTime.now(), message, status.value());
        return ResponseEntity.status(status).body(incorrectData);
    }
}
