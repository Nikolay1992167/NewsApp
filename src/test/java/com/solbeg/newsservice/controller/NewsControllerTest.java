package com.solbeg.newsservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solbeg.newsservice.dto.request.CreateNewsDto;
import com.solbeg.newsservice.dto.request.CreateNewsDtoJournalist;
import com.solbeg.newsservice.dto.request.Filter;
import com.solbeg.newsservice.enams.ErrorMessage;
import com.solbeg.newsservice.util.JwtTokenTestUtils;
import com.solbeg.newsservice.util.ManagerMockWebServer;
import com.solbeg.newsservice.util.PostgresSqlContainerInitializer;
import com.solbeg.newsservice.util.testdata.NewsTestData;
import com.solbeg.newsservice.util.testdata.UserTestData;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.solbeg.newsservice.util.init.InitData.BEARER;
import static com.solbeg.newsservice.util.init.InitData.EMAIL_ADMIN_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.EMAIL_JOURNALIST_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.EMAIL_SUBSCRIBER_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.ID_ADMIN_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.ID_JOURNALIST_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.ID_NEWS;
import static com.solbeg.newsservice.util.init.InitData.ID_NEWS_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.ID_NEWS_FOR_IT_DELETE;
import static com.solbeg.newsservice.util.init.InitData.ID_NEWS_FOR_IT_JOURNALIST;
import static com.solbeg.newsservice.util.init.InitData.ID_NOT_EXIST;
import static com.solbeg.newsservice.util.init.InitData.ID_SUBSCRIBER_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.URL_NEWS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RequiredArgsConstructor
class NewsControllerTest extends PostgresSqlContainerInitializer {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final ManagerMockWebServer managerMockWebServer;

    private String tokenAdmin;
    private String tokenJournalist;
    private String tokenSubscriber;

    @BeforeEach
    void setUp() {
        tokenAdmin = JwtTokenTestUtils.generateToken(EMAIL_ADMIN_FOR_IT, ID_ADMIN_FOR_IT, List.of("ADMIN"));
        tokenJournalist = JwtTokenTestUtils.generateToken(EMAIL_JOURNALIST_FOR_IT, ID_JOURNALIST_FOR_IT, List.of("JOURNALIST"));
        tokenSubscriber = JwtTokenTestUtils.generateToken(EMAIL_SUBSCRIBER_FOR_IT, ID_SUBSCRIBER_FOR_IT, List.of("SUBSCRIBER"));
    }

    @Nested
    class FindNewsByIdGetEndpointTest {

        @Test
        void shouldReturnExpectedJsonAndStatus200() throws Exception {
            // given
            UUID newsId = ID_NEWS_FOR_IT;

            // when, then
            mockMvc.perform(get(URL_NEWS + "/" + newsId)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(newsId.toString()));
        }

        @Test
        void shouldThrowExceptionAndStatus404() throws Exception {
            // given
            UUID newsId = ID_NOT_EXIST;

            // when, then
            mockMvc.perform(get(URL_NEWS + "/" + newsId)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error_message")
                            .value(ErrorMessage.NEWS_NOT_FOUND.getMessage() + newsId));
        }
    }

    @Nested
    class GetAllNewsGetEndpointTest {

        @Test
        void shouldReturnExpectedJsonAndStatus200() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get(URL_NEWS)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            MockHttpServletResponse response = mvcResult.getResponse();
            JSONObject jsonObject = new JSONObject(response.getContentAsString());
            assertThat(jsonObject.get("totalPages")).isEqualTo(1);
            assertThat(jsonObject.get("totalElements")).isEqualTo(5);
            assertThat(jsonObject.get("number")).isEqualTo(0);
        }
    }

    @Nested
    class FindNewsByFilterGetEndpointTest {

        @Test
        void shouldReturnExpectedJsonAndStatus200() throws Exception {
            // given
            Filter filter = Filter.builder()
                    .part("is")
                    .build();
            String json = objectMapper.writeValueAsString(filter);
            MvcResult mvcResult = mockMvc.perform(get(URL_NEWS + "/filter")
                            .contentType(APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andReturn();
            MockHttpServletResponse response = mvcResult.getResponse();
            JSONObject jsonObject = new JSONObject(response.getContentAsString());
            assertThat(jsonObject.get("totalPages")).isEqualTo(1);
            assertThat(jsonObject.get("totalElements")).isEqualTo(1);
            assertThat(jsonObject.get("number")).isEqualTo(0);
        }
    }

    @Nested
    class CreateNewsAdminPostEndpointTest {

        @Test
        void shouldReturnExpectedJsonAndStatus201() throws Exception {
            // given
            CreateNewsDto createNewsDto = NewsTestData.getCreateNewsDtoITForAdmin();
            String json = objectMapper.writeValueAsString(createNewsDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(post(URL_NEWS + "/admin")
                            .header(AUTHORIZATION, BEARER + tokenAdmin)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isCreated());

            managerMockWebServer.stopMockWebServer();
        }

        @Test
        void shouldThrowExceptionAndStatus401() throws Exception {
            // given
            CreateNewsDto createNewsDto = NewsTestData.getCreateNewsDtoITForAdmin();
            String json = objectMapper.writeValueAsString(createNewsDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(post(URL_NEWS + "/admin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isUnauthorized());

            managerMockWebServer.stopMockWebServer();
        }

        @Test
        void shouldThrowExceptionAndStatus403() throws Exception {
            // given
            CreateNewsDto createNewsDto = NewsTestData.getCreateNewsDtoITForAdmin();
            String json = objectMapper.writeValueAsString(createNewsDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseSubscriber());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(post(URL_NEWS + "/admin")
                            .header(AUTHORIZATION, BEARER + tokenSubscriber)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isForbidden());

            managerMockWebServer.stopMockWebServer();
        }

        @Test
        void shouldThrowExceptionAndStatus400WhenInvalidRequestData() throws Exception {
            // given
            CreateNewsDto createNewsDto = NewsTestData.getCreateNewsDtoIncorrect();
            String json = objectMapper.writeValueAsString(createNewsDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(post(URL_NEWS + "/admin")
                            .header(AUTHORIZATION, BEARER + tokenAdmin)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isBadRequest());

            managerMockWebServer.stopMockWebServer();
        }
    }

    @Nested
    class CreateNewsJournalistPostEndpointTest {

        @Test
        void shouldReturnExpectedJsonAndStatus201() throws Exception {
            // given
            CreateNewsDtoJournalist createNewsDto = NewsTestData.getCreateNewsDtoJournalist();
            String json = objectMapper.writeValueAsString(createNewsDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseJournalist());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(post(URL_NEWS + "/journalist")
                            .header(AUTHORIZATION, BEARER + tokenJournalist)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isCreated());

            managerMockWebServer.stopMockWebServer();
        }

        @Test
        void shouldThrowExceptionAndStatus401() throws Exception {
            // given
            CreateNewsDtoJournalist createNewsDto = NewsTestData.getCreateNewsDtoJournalist();
            String json = objectMapper.writeValueAsString(createNewsDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseJournalist());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(post(URL_NEWS + "/journalist")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isUnauthorized());

            managerMockWebServer.stopMockWebServer();
        }

        @Test
        void shouldThrowExceptionAndStatus403() throws Exception {
            // given
            CreateNewsDtoJournalist createNewsDto = NewsTestData.getCreateNewsDtoJournalist();
            String json = objectMapper.writeValueAsString(createNewsDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseSubscriber());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(post(URL_NEWS + "/journalist")
                            .header(AUTHORIZATION, BEARER + tokenSubscriber)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isForbidden());

            managerMockWebServer.stopMockWebServer();
        }

        @Test
        void shouldThrowExceptionAndStatus400WhenInvalidRequestData() throws Exception {
            // given
            CreateNewsDtoJournalist createNewsDto = NewsTestData.getCreateNewsDtoJournalistIncorrect();
            String json = objectMapper.writeValueAsString(createNewsDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseJournalist());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(post(URL_NEWS + "/journalist")
                            .header(AUTHORIZATION, BEARER + tokenJournalist)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isBadRequest());

            managerMockWebServer.stopMockWebServer();
        }
    }

    @Nested
    class UpdateNewsAdminPutEndpointTest {

        @Test
        void shouldReturnExpectedJsonAndStatus200() throws Exception {
            // given
            UUID newsId = ID_NEWS_FOR_IT;
            CreateNewsDto updateNewsDto = NewsTestData.getCreateNewsDto();
            String json = objectMapper.writeValueAsString(updateNewsDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(put(URL_NEWS + "/admin/" + newsId)
                            .header(AUTHORIZATION, BEARER + tokenAdmin)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.id").value(newsId.toString()),
                            jsonPath("$.text").value(updateNewsDto.text()));

            managerMockWebServer.stopMockWebServer();
        }

        @Test
        void shouldThrowExceptionAndStatus401() throws Exception {
            // given
            UUID newsId = ID_NEWS_FOR_IT;
            CreateNewsDto updateNewsDto = NewsTestData.getCreateNewsDto();
            String json = objectMapper.writeValueAsString(updateNewsDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(put(URL_NEWS + "/admin/" + newsId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isUnauthorized());

            managerMockWebServer.stopMockWebServer();
        }

        @Test
        void shouldThrowExceptionAndStatus403() throws Exception {
            // given
            UUID newsId = ID_NEWS_FOR_IT;
            CreateNewsDto updateNewsDto = NewsTestData.getCreateNewsDto();
            String json = objectMapper.writeValueAsString(updateNewsDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseSubscriber());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(put(URL_NEWS + "/admin/" + newsId)
                            .header(AUTHORIZATION, BEARER + tokenSubscriber)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isForbidden());

            managerMockWebServer.stopMockWebServer();
        }

        @Test
        void shouldThrowExceptionAndStatus404WhenNewsNotFound() throws Exception {
            // given
            UUID newsId = ID_NEWS;
            CreateNewsDto updateNewsDto = NewsTestData.getCreateNewsDto();
            String json = objectMapper.writeValueAsString(updateNewsDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(put(URL_NEWS + "/admin/" + newsId)
                            .header(AUTHORIZATION, BEARER + tokenAdmin)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.error_message")
                                    .value(ErrorMessage.NEWS_NOT_FOUND.getMessage() + newsId));

            managerMockWebServer.stopMockWebServer();
        }

        @Test
        void shouldThrowExceptionAndStatus400WhenInvalidRequestData() throws Exception {
            // given
            UUID newsId = ID_NEWS_FOR_IT;
            CreateNewsDto updateNewsDto = NewsTestData.getCreateNewsDtoIncorrect();
            String json = objectMapper.writeValueAsString(updateNewsDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(put(URL_NEWS + "/admin/" + newsId)
                            .header(AUTHORIZATION, BEARER + tokenAdmin)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isBadRequest());

            managerMockWebServer.stopMockWebServer();
        }
    }

    @Nested
    @Transactional
    class UpdateNewsJournalistPutEndpointTest {

        @Test
        void shouldReturnExpectedJsonAndStatus200() throws Exception {
            // given
            UUID newsId = ID_NEWS_FOR_IT_JOURNALIST;
            CreateNewsDtoJournalist updateNewsDto = NewsTestData.getCreateNewsDtoJournalist();
            String json = objectMapper.writeValueAsString(updateNewsDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseJournalist());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(put(URL_NEWS + "/journalist/" + newsId)
                            .header(AUTHORIZATION, BEARER + tokenJournalist)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.id").value(newsId.toString()),
                            jsonPath("$.text").value(updateNewsDto.text()));

            managerMockWebServer.stopMockWebServer();
        }

        @Test
        void shouldThrowExceptionAndStatus401() throws Exception {
            // given
            UUID newsId = ID_NEWS_FOR_IT_JOURNALIST;
            CreateNewsDtoJournalist updateNewsDto = NewsTestData.getCreateNewsDtoJournalist();
            String json = objectMapper.writeValueAsString(updateNewsDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseJournalist());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(put(URL_NEWS + "/journalist/" + newsId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isUnauthorized());

            managerMockWebServer.stopMockWebServer();
        }

        @Test
        void shouldThrowExceptionAndStatus403() throws Exception {
            // given
            UUID newsId = ID_NEWS_FOR_IT_JOURNALIST;
            CreateNewsDtoJournalist updateNewsDto = NewsTestData.getCreateNewsDtoJournalist();
            String json = objectMapper.writeValueAsString(updateNewsDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseSubscriber());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(put(URL_NEWS + "/journalist/" + newsId)
                            .header(AUTHORIZATION, BEARER + tokenSubscriber)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isForbidden());

            managerMockWebServer.stopMockWebServer();
        }

        @Test
        void shouldThrowExceptionAndStatus404WhenNewsNotFound() throws Exception {
            // given
            UUID newsId = ID_NEWS;
            CreateNewsDtoJournalist updateNewsDto = NewsTestData.getCreateNewsDtoJournalist();
            String json = objectMapper.writeValueAsString(updateNewsDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseJournalist());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(put(URL_NEWS + "/journalist/" + newsId)
                            .header(AUTHORIZATION, BEARER + tokenJournalist)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.error_message")
                                    .value(ErrorMessage.NEWS_NOT_FOUND.getMessage() + newsId));

            managerMockWebServer.stopMockWebServer();
        }

        @Test
        void shouldThrowExceptionAndStatus400WhenInvalidRequestData() throws Exception {
            // given
            UUID newsId = ID_NEWS_FOR_IT_JOURNALIST;
            CreateNewsDtoJournalist updateNewsDto = NewsTestData.getCreateNewsDtoJournalistIncorrect();
            String json = objectMapper.writeValueAsString(updateNewsDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseJournalist());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(put(URL_NEWS + "/journalist/" + newsId)
                            .header(AUTHORIZATION, BEARER + tokenJournalist)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isBadRequest());

            managerMockWebServer.stopMockWebServer();
        }
    }

    @Nested
    @Transactional
    class DeleteNewsByIdDeleteEndPointTest {

        @Test
        void shouldDeleteCommentAndStatus200() throws Exception {
            // given
            UUID newsId = ID_NEWS_FOR_IT_DELETE;
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(delete(URL_NEWS + "/" + newsId)
                            .header(AUTHORIZATION, BEARER + tokenAdmin)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isOk());

            managerMockWebServer.stopMockWebServer();
        }

        @Test
        void shouldThrowExceptionAndStatus401() throws Exception {
            // given
            UUID newsId = ID_NEWS_FOR_IT_DELETE;
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(delete(URL_NEWS + "/" + newsId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isUnauthorized());

            managerMockWebServer.stopMockWebServer();
        }

        @Test
        void shouldThrowExceptionAndStatus403() throws Exception {
            // given
            UUID newsId = ID_NEWS_FOR_IT;
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseJournalist());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(delete(URL_NEWS + "/" + newsId)
                            .header(AUTHORIZATION, BEARER + tokenJournalist)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isForbidden());

            managerMockWebServer.stopMockWebServer();
        }

        @Test
        void shouldThrowExceptionAndStatus403WhenUserNotRightChangeData() throws Exception {
            // given
            UUID newsId = ID_NEWS_FOR_IT_DELETE;
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseJournalist());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(delete(URL_NEWS + "/" + newsId)
                            .header(AUTHORIZATION, BEARER + tokenJournalist)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isForbidden());

            managerMockWebServer.stopMockWebServer();
        }

        @Test
        void shouldThrowExceptionAndStatus404WhenCommentNotFound() throws Exception {
            // given
            UUID newsId = ID_NEWS;
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            managerMockWebServer.startMockWebServer(userResponseJson);

            // when, then
            mockMvc.perform(delete(URL_NEWS + "/" + newsId)
                            .header(AUTHORIZATION, BEARER + tokenAdmin)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.error_message")
                                    .value(ErrorMessage.NEWS_NOT_FOUND.getMessage() + newsId));

            managerMockWebServer.stopMockWebServer();
        }
    }
}