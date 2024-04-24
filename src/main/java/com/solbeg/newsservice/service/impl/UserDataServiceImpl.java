package com.solbeg.newsservice.service.impl;

import com.solbeg.newsservice.dto.response.UserResponse;
import com.solbeg.newsservice.exception.CustomServerException;
import com.solbeg.newsservice.exception.NotFoundException;
import com.solbeg.newsservice.exception.model.IncorrectData;
import com.solbeg.newsservice.service.UserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDataServiceImpl implements UserDataService {
    private final WebClient webClient;

    @Override
    public UserResponse getUserData(String token) {
        return webClient.post()
                .uri("/api/v1/users/details")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(token), String.class)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block();
    }

    @Override
    public UserResponse getUserData(UUID userId, String token) {
        return webClient.get()
                .uri("/api/v1/admin/" + userId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(IncorrectData.class)
                                .flatMap(errorResponse -> Mono.error(new NotFoundException(errorResponse.errorMessage()))))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(IncorrectData.class)
                                .flatMap(errorResponse -> Mono.error(new CustomServerException(errorResponse.errorMessage()))))
                .bodyToMono(UserResponse.class)
                .block();
    }
}