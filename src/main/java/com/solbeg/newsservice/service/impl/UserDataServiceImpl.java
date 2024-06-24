package com.solbeg.newsservice.service.impl;

import com.solbeg.newsservice.dto.response.UserResponse;
import com.solbeg.newsservice.exception.NotFoundException;
import com.solbeg.newsservice.service.UserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDataServiceImpl implements UserDataService {
    private final RestClient restClient;

    @Override
    public UserResponse getUserData(String authorizationToken) {
        return restClient.get()
                .uri("/api/v1/users/details")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, authorizationToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(UserResponse.class);
    }

    @Override
    public UserResponse getUserData(UUID userId, String authorizationToken) {
        return restClient.get()
                .uri("/api/v1/admin/" + userId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, authorizationToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new NotFoundException(getErrorMessage(response));
                })
                .body(UserResponse.class);
    }

    private String getErrorMessage(ClientHttpResponse response) throws IOException {
        InputStream body = response.getBody();
        String errorLine = new BufferedReader(new InputStreamReader(body))
                .lines().collect(Collectors.joining("\n"));
        String key = "\"error_message\":\"";
        int startIndex = errorLine.indexOf(key) + key.length();
        int endIndex = errorLine.indexOf("\",", startIndex);
        return errorLine.substring(startIndex, endIndex);
    }
}