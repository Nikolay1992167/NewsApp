package com.solbeg.newsservice.filter;

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
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClientRequestException;

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
        } catch (WebClientRequestException exception) {
            handleWebClientRequestException(response, exception);
        } catch (RuntimeException exception) {
            handleException(response, exception);
        }
    }

    public void handleException(HttpServletResponse response, Exception exception) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        response.setCharacterEncoding("utf-8");
        IncorrectData incorrectData = new IncorrectData(
                LocalDateTime.now(),
                exception.getMessage(),
                HttpStatus.UNAUTHORIZED.value());
        String responseMessage = mapper.writeValueAsString(incorrectData);
        response.getWriter().write(responseMessage);
    }

    private void handleWebClientRequestException(HttpServletResponse response, WebClientRequestException exception) throws IOException {
        response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        response.setCharacterEncoding("utf-8");
        IncorrectData incorrectData = new IncorrectData(
                LocalDateTime.now(),
                exception.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE.value());
        String responseMessage = mapper.writeValueAsString(incorrectData);
        response.getWriter().write(responseMessage);
    }
}