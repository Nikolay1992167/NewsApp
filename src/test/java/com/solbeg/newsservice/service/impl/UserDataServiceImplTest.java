package com.solbeg.newsservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solbeg.newsservice.exception.CustomServerException;
import com.solbeg.newsservice.exception.NotFoundException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.UUID;

import static com.solbeg.newsservice.util.init.InitData.TOKEN_JOURNALIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserDataServiceImplTest {
    private MockWebServer mockWebServer;

    @InjectMocks
    private UserDataServiceImpl userDataService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        ObjectMapper objectMapper = new ObjectMapper();
        mockWebServer.start();
        RestClient restClient = RestClient.create(mockWebServer.url("/").toString());
        userDataService = new UserDataServiceImpl(restClient, objectMapper);
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldReturnExpectedUserResponseAndStatus200WithUserID() {
        // given
        UUID userId = UUID.randomUUID();
        String userToken = TOKEN_JOURNALIST;
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        // when
        userDataService.getUserData(userId, userToken);

        // then
        assertThat(mockWebServer.getRequestCount()).isEqualTo(1);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenStatus4xx() {
        // given
        UUID userId = UUID.randomUUID();
        String userToken = TOKEN_JOURNALIST;
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"error_message\":\"Not Found!\"}");
        mockWebServer.enqueue(mockResponse);

        // when
        assertThrows(NotFoundException.class, () -> userDataService.getUserData(userId, userToken));
    }

    @Test
    void shouldThrowCustomServerExceptionWhenStatus5xx() {
        // given
        UUID userId = UUID.randomUUID();
        String userToken = TOKEN_JOURNALIST;
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(500)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"error_message\":\"Internal Server Error\"}");
        mockWebServer.enqueue(mockResponse);

        // when
        assertThrows(CustomServerException.class, () -> userDataService.getUserData(userId, userToken));
    }
}