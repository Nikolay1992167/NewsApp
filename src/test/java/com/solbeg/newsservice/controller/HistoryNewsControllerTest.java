package com.solbeg.newsservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solbeg.newsservice.dto.request.TimePeriod;
import com.solbeg.newsservice.enams.ErrorMessage;
import com.solbeg.newsservice.util.JwtTokenTestUtils;
import com.solbeg.newsservice.util.ManagerMockWebServer;
import com.solbeg.newsservice.util.PostgresSqlContainerInitializer;
import com.solbeg.newsservice.util.testdata.TimePeriodTestData;
import com.solbeg.newsservice.util.testdata.UserTestData;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static com.solbeg.newsservice.util.init.InitData.BEARER;
import static com.solbeg.newsservice.util.init.InitData.EMAIL_ADMIN_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.ID_ADMIN_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.ID_NEWS;
import static com.solbeg.newsservice.util.init.InitData.ID_NEWS_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.URL_NEWS_HISTORY;
import static com.solbeg.newsservice.util.init.InitData.URL_NEWS_HISTORY_IN_PERIOD;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RequiredArgsConstructor
class HistoryNewsControllerTest extends PostgresSqlContainerInitializer {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final ManagerMockWebServer managerMockWebServer;

    private String tokenAdmin;

    @BeforeEach
    void setUp() {
        tokenAdmin = JwtTokenTestUtils.generateToken(EMAIL_ADMIN_FOR_IT, ID_ADMIN_FOR_IT, List.of("ADMIN"));
    }

    @Test
    void shouldCheckFindHistoryOfNewsAndReturnStatus200() throws Exception {
        // given
        UUID newsId = ID_NEWS_FOR_IT;
        String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

        managerMockWebServer.startMockWebServer(userResponseJson);

        // when, then
        mockMvc.perform(get(URL_NEWS_HISTORY + "/" + newsId)
                        .header(AUTHORIZATION, BEARER + tokenAdmin)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        managerMockWebServer.stopMockWebServer();
    }

    @Test
    void shouldCheckFindHistoryOfNewsAndReturnStatus404WhenNewsNotExist() throws Exception {
        // given
        UUID newsId = ID_NEWS;
        String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

        managerMockWebServer.startMockWebServer(userResponseJson);

        // when, then
        mockMvc.perform(get(URL_NEWS_HISTORY + "/" + newsId)
                        .header(AUTHORIZATION, BEARER + tokenAdmin)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());

        managerMockWebServer.stopMockWebServer();
    }

    @Test
    void shouldCheckFindHistoryOfNewsAndReturnStatus401WhenUserNotAuthorization() throws Exception {
        // given
        UUID newsId = ID_NEWS_FOR_IT;
        String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

        managerMockWebServer.startMockWebServer(userResponseJson);

        // when, then
        mockMvc.perform(get(URL_NEWS_HISTORY + "/" + newsId)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        managerMockWebServer.stopMockWebServer();
    }

    @Test
    void shouldCheckFindHistoryOfNewsForTimePeriodAndReturnStatus200() throws Exception {
        // given
        UUID newsId = ID_NEWS_FOR_IT;
        String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());
        TimePeriod timePeriod = TimePeriodTestData.getTimePeriod();
        String json = objectMapper.writeValueAsString(timePeriod);

        managerMockWebServer.startMockWebServer(userResponseJson);

        // when, then
        mockMvc.perform(get(URL_NEWS_HISTORY_IN_PERIOD + "/" + newsId)
                        .header(AUTHORIZATION, BEARER + tokenAdmin)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        managerMockWebServer.stopMockWebServer();
    }

    @Test
    void shouldCheckFindHistoryOfNewsForTimePeriodAndReturnStatus404WhenNewsNotExist() throws Exception {
        // given
        UUID newsId = ID_NEWS;
        String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());
        TimePeriod timePeriod = TimePeriodTestData.getTimePeriod();
        String json = objectMapper.writeValueAsString(timePeriod);

        managerMockWebServer.startMockWebServer(userResponseJson);

        // when, then
        mockMvc.perform(get(URL_NEWS_HISTORY_IN_PERIOD + "/" + newsId)
                        .header(AUTHORIZATION, BEARER + tokenAdmin)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());

        managerMockWebServer.stopMockWebServer();
    }

    @Test
    void shouldCheckFindHistoryOfNewsForTimePeriodAndReturnStatus401WhenUserNotAuthorization() throws Exception {
        // given
        UUID newsId = ID_NEWS_FOR_IT;
        String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());
        TimePeriod timePeriod = TimePeriodTestData.getTimePeriod();
        String json = objectMapper.writeValueAsString(timePeriod);

        managerMockWebServer.startMockWebServer(userResponseJson);

        // when, then
        mockMvc.perform(get(URL_NEWS_HISTORY_IN_PERIOD + "/" + newsId)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());

        managerMockWebServer.stopMockWebServer();
    }

    @Test
    void shouldCheckFindHistoryOfNewsForTimePeriodAndReturnStatus400WhenTimePeriodIncorrect() throws Exception {
        // given
        UUID newsId = ID_NEWS_FOR_IT;
        String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());
        TimePeriod timePeriod = TimePeriodTestData.getTimePeriodIncorrect();
        String json = objectMapper.writeValueAsString(timePeriod);

        managerMockWebServer.startMockWebServer(userResponseJson);

        // when, then
        mockMvc.perform(get(URL_NEWS_HISTORY_IN_PERIOD + "/" + newsId)
                        .header(AUTHORIZATION, BEARER + tokenAdmin)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_message")
                        .value(ErrorMessage.ERROR_TIME_MESSAGE.getMessage()));

        managerMockWebServer.stopMockWebServer();
    }
}