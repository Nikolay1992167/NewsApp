package com.solbeg.newsservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solbeg.newsservice.dto.response.UserResponse;
import com.solbeg.newsservice.enams.ErrorMessage;
import com.solbeg.newsservice.exception.CustomServerException;
import com.solbeg.newsservice.exception.NotFoundException;
import com.solbeg.newsservice.exception.ParsingException;
import com.solbeg.newsservice.exception.model.IncorrectData;
import com.solbeg.newsservice.service.UserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDataServiceImpl implements UserDataService {
    private final RestClient restClient;

    private final ObjectMapper objectMapper;

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
        try {
            return restClient.get()
                    .uri("/api/v1/admin/" + userId)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, authorizationToken)
                    .retrieve()
                    .body(UserResponse.class);
        } catch (HttpClientErrorException exception) {
            IncorrectData incorrectData;
            try {
                incorrectData = objectMapper.readValue(exception.getResponseBodyAsString(), IncorrectData.class);
            } catch (JsonProcessingException e) {
                throw new ParsingException(ErrorMessage.ERROR_PARSING_RESPONSE_TO_ERROR.getMessage());
            }
            throw new NotFoundException(incorrectData.errorMessage());
        } catch (HttpServerErrorException exception) {
            throw new CustomServerException(exception.getMessage());
        }
    }
}