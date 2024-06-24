package com.solbeg.newsservice.service.impl;

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

@ExtendWith(MockitoExtension.class)
class UserDataServiceImplTest {
    private MockWebServer mockWebServer;

    @InjectMocks
    private UserDataServiceImpl userDataService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        RestClient restClient = RestClient.create(mockWebServer.url("/").toString());
        userDataService = new UserDataServiceImpl(restClient);
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
}