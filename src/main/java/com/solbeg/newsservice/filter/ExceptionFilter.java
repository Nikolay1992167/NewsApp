package com.solbeg.newsservice.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solbeg.newsservice.exception.model.IncorrectData;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ExceptionFilter extends OncePerRequestFilter {
    private final ObjectMapper mapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (RestClientException exception) {
            handleException(response, exception, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException exception) {
            handleException(response, exception, HttpStatus.UNAUTHORIZED);
        }
    }

    public void handleException(HttpServletResponse response, Exception exception, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        response.setCharacterEncoding("utf-8");

        String errorMessage = exception.getMessage();
        IncorrectData incorrectData;

        if (errorMessage.matches("^\\d{3} :.*")) {
            incorrectData = getIncorrectDataFromExceptionMessage(errorMessage);
        } else {
            incorrectData = IncorrectData.builder()
                    .timestamp(LocalDateTime.now())
                    .errorMessage(exception.getMessage())
                    .errorStatus(status.value())
                    .build();
        }
        String responseMessage = mapper.writeValueAsString(incorrectData);
        response.getWriter().write(responseMessage);
    }

    private IncorrectData getIncorrectDataFromExceptionMessage(String errorMessage) throws JsonProcessingException {
        errorMessage = errorMessage.split(" : ", 2)[1]
                .replace("\\\"", "\"")
                .replace("\"{", "{")
                .replace("}\"", "}");
        return mapper.readValue(errorMessage, IncorrectData.class);
    }
}