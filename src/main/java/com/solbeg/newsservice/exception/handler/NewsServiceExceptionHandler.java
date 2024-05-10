package com.solbeg.newsservice.exception.handler;

import com.solbeg.newsservice.exception.AccessException;
import com.solbeg.newsservice.exception.CreateObjectException;
import com.solbeg.newsservice.exception.CustomServerException;
import com.solbeg.newsservice.exception.NotFoundException;
import com.solbeg.newsservice.exception.ParsingException;
import com.solbeg.newsservice.exception.model.IncorrectData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    @ExceptionHandler(ParsingException.class)
    public ResponseEntity<IncorrectData> parsingException(ParsingException exception) {
        return getResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomServerException.class)
    public ResponseEntity<IncorrectData> customServerException(CustomServerException exception) {
        return getResponse(exception.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<IncorrectData> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        return getResponse("UUID was entered incorrectly!", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<IncorrectData> httpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return getResponse("Incorrectly entered newsId", HttpStatus.BAD_REQUEST);
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
