package com.solbeg.newsservice.config;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RestClientConfigTest {

    @InjectMocks
    private RestClientConfig restClientConfig;

    @Test
    void shouldReturnExpectedInstanceOfWebclient() {
        // given
        Class<RestClient> expectedClass = RestClient.class;

        // when
        RestClient actualClass = restClientConfig.webClient();

        // then
        assertThat(actualClass).isInstanceOf(expectedClass);
    }
}