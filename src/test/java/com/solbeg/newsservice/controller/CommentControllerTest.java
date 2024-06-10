package com.solbeg.newsservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solbeg.newsservice.dto.request.CreateCommentDto;
import com.solbeg.newsservice.dto.request.Filter;
import com.solbeg.newsservice.dto.request.UpdateCommentDto;
import com.solbeg.newsservice.enams.ErrorMessage;
import com.solbeg.newsservice.util.JwtTokenTestUtils;
import com.solbeg.newsservice.util.PostgresSqlContainerInitializer;
import com.solbeg.newsservice.util.testdata.CommentTestData;
import com.solbeg.newsservice.util.testdata.UserTestData;
import lombok.RequiredArgsConstructor;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.solbeg.newsservice.util.init.InitData.BEARER;
import static com.solbeg.newsservice.util.init.InitData.EMAIL_ADMIN_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.EMAIL_JOURNALIST_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.EMAIL_SUBSCRIBER_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.ID_ADMIN_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.ID_COMMENT;
import static com.solbeg.newsservice.util.init.InitData.ID_COMMENT_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.ID_COMMENT_FOR_IT_DELETE;
import static com.solbeg.newsservice.util.init.InitData.ID_JOURNALIST_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.ID_NEWS_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.ID_NOT_EXIST;
import static com.solbeg.newsservice.util.init.InitData.ID_SUBSCRIBER_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.URL_COMMENT;
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
class CommentControllerTest extends PostgresSqlContainerInitializer {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private MockWebServer mockWebServer;

    private String tokenAdmin;
    private String tokenJournalist;
    private String tokenSubscriber;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8081);
        tokenAdmin = JwtTokenTestUtils.generateToken(EMAIL_ADMIN_FOR_IT, ID_ADMIN_FOR_IT, List.of("ADMIN"));
        tokenJournalist = JwtTokenTestUtils.generateToken(EMAIL_JOURNALIST_FOR_IT, ID_JOURNALIST_FOR_IT, List.of("JOURNALIST"));
        tokenSubscriber = JwtTokenTestUtils.generateToken(EMAIL_SUBSCRIBER_FOR_IT, ID_SUBSCRIBER_FOR_IT, List.of("SUBSCRIBER"));
    }

    @AfterEach
    void stop() throws IOException {
        mockWebServer.shutdown();
    }

    @Nested
    class FindCommentByIdGetEndpointTest {

        @Test
        void shouldReturnExpectedJsonAndStatus200() throws Exception {
            // given
            UUID commentId = ID_COMMENT_FOR_IT;

            // when, then
            mockMvc.perform(get(URL_COMMENT + "/" + commentId)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(commentId.toString()));
        }

        @Test
        void shouldThrowExceptionAndStatus404() throws Exception {
            // given
            UUID commitId = ID_NOT_EXIST;

            // when, then
            mockMvc.perform(get(URL_COMMENT + "/" + commitId)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error_message")
                            .value(ErrorMessage.COMMENT_NOT_FOUND.getMessage() + commitId));
        }
    }

    @Nested
    class FindCommentsByNewsIdGetEndpointTest {

        @Test
        void shouldReturnExpectedJsonAndStatus200() throws Exception {
            // given
            UUID newsId = ID_NEWS_FOR_IT;

            // when, then
            mockMvc.perform(get(URL_COMMENT + "/news/" + newsId)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(newsId.toString()));
        }

        @Test
        void shouldThrowExceptionAndStatus404() throws Exception {
            // given
            UUID newsId = ID_NOT_EXIST;

            // when, then
            mockMvc.perform(get(URL_COMMENT + "/news/" + newsId)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error_message")
                            .value(ErrorMessage.NEWS_NOT_FOUND.getMessage() + newsId));
        }
    }

    @Nested
    class GetAllCommentsGetEndpointTest {

        @Test
        void shouldReturnExpectedJsonAndStatus200() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get(URL_COMMENT)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            MockHttpServletResponse response = mvcResult.getResponse();
            JSONObject jsonObject = new JSONObject(response.getContentAsString());
            assertThat(jsonObject.get("totalPages")).isEqualTo(1);
            assertThat(jsonObject.get("totalElements")).isEqualTo(10);
            assertThat(jsonObject.get("number")).isEqualTo(0);
        }
    }

    @Nested
    class FindCommentsByFilterGetEndpointTest {

        @Test
        void shouldReturnExpectedJsonAndStatus200() throws Exception {
            // given
            Filter filter = Filter.builder()
                    .part("is")
                    .build();
            String json = objectMapper.writeValueAsString(filter);
            MvcResult mvcResult = mockMvc.perform(get(URL_COMMENT + "/filter")
                            .contentType(APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andReturn();
            MockHttpServletResponse response = mvcResult.getResponse();
            JSONObject jsonObject = new JSONObject(response.getContentAsString());
            assertThat(jsonObject.get("totalPages")).isEqualTo(1);
            assertThat(jsonObject.get("totalElements")).isEqualTo(3);
            assertThat(jsonObject.get("number")).isEqualTo(0);
        }
    }

    @Nested
    class CreateCommentPostEndpointTest {

        @Test
        void shouldReturnExpectedJsonAndStatus201() throws Exception {
            // given
            CreateCommentDto createCommentDto = CommentTestData.getCommentDtoIT();
            String json = objectMapper.writeValueAsString(createCommentDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            mockWebServer.enqueue(new MockResponse()
                    .setBody(userResponseJson)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            // when, then
            mockMvc.perform(post(URL_COMMENT)
                            .header(AUTHORIZATION, BEARER + tokenAdmin)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isCreated(),
                            jsonPath("$.id").isNotEmpty(),
                            jsonPath("$.text").value(createCommentDto.text()),
                            jsonPath("$.newsId").value(createCommentDto.newsId().toString()));
        }

        @Test
        void shouldThrowExceptionAndStatus401() throws Exception {
            // given
            CreateCommentDto createCommentDto = CommentTestData.getCommentDtoIT();
            String json = objectMapper.writeValueAsString(createCommentDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            mockWebServer.enqueue(new MockResponse()
                    .setBody(userResponseJson)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            // when, then
            mockMvc.perform(post(URL_COMMENT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isUnauthorized());
        }

        @Test
        void shouldThrowExceptionAndStatus403() throws Exception {
            // given
            CreateCommentDto createCommentDto = CommentTestData.getCommentDtoIT();
            String json = objectMapper.writeValueAsString(createCommentDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseJournalist());

            mockWebServer.enqueue(new MockResponse()
                    .setBody(userResponseJson)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            // when, then
            mockMvc.perform(post(URL_COMMENT)
                            .header(AUTHORIZATION, BEARER + tokenJournalist)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isForbidden());
        }

        @Test
        void shouldThrowExceptionAndStatus404WhenNewsNotFound() throws Exception {
            // given
            CreateCommentDto createCommentDto = CommentTestData.getCommentDto();
            String json = objectMapper.writeValueAsString(createCommentDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            mockWebServer.enqueue(new MockResponse()
                    .setBody(userResponseJson)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            // when, then
            mockMvc.perform(post(URL_COMMENT)
                            .header(AUTHORIZATION, BEARER + tokenAdmin)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.error_message")
                                    .value(ErrorMessage.NEWS_NOT_FOUND.getMessage() + createCommentDto.newsId()));
        }

        @Test
        void shouldThrowExceptionAndStatus400WhenInvalidRequestData() throws Exception {
            // given
            CreateCommentDto createCommentDto = CommentTestData.getCommentDtoIncorrect();
            String json = objectMapper.writeValueAsString(createCommentDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            mockWebServer.enqueue(new MockResponse()
                    .setBody(userResponseJson)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            // when, then
            mockMvc.perform(post(URL_COMMENT)
                            .header(AUTHORIZATION, BEARER + tokenAdmin)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isBadRequest());
        }
    }

    @Nested
    @Transactional
    class UpdateCommentPutEndpointTest {

        @Test
        void shouldReturnExpectedJsonAndStatus200() throws Exception {
            // given
            UUID commentId = ID_COMMENT_FOR_IT;
            UpdateCommentDto updateCommentDto = CommentTestData.getUpdateCommentDto();
            String json = objectMapper.writeValueAsString(updateCommentDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            mockWebServer.enqueue(new MockResponse()
                    .setBody(userResponseJson)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            // when, then
            mockMvc.perform(put(URL_COMMENT + "/" + commentId)
                            .header(AUTHORIZATION, BEARER + tokenAdmin)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.id").value(commentId.toString()),
                            jsonPath("$.text").value(updateCommentDto.text()));
        }

        @Test
        void shouldThrowExceptionAndStatus401() throws Exception {
            // given
            UUID commentId = ID_COMMENT_FOR_IT;
            UpdateCommentDto updateCommentDto = CommentTestData.getUpdateCommentDto();
            String json = objectMapper.writeValueAsString(updateCommentDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            mockWebServer.enqueue(new MockResponse()
                    .setBody(userResponseJson)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            // when, then
            mockMvc.perform(put(URL_COMMENT + "/" + commentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isUnauthorized());
        }

        @Test
        void shouldThrowExceptionAndStatus403() throws Exception {
            // given
            UUID commentId = ID_COMMENT_FOR_IT;
            UpdateCommentDto updateCommentDto = CommentTestData.getUpdateCommentDto();
            String json = objectMapper.writeValueAsString(updateCommentDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseJournalist());

            mockWebServer.enqueue(new MockResponse()
                    .setBody(userResponseJson)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            // when, then
            mockMvc.perform(put(URL_COMMENT + "/" + commentId)
                            .header(AUTHORIZATION, BEARER + tokenJournalist)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isForbidden());
        }

        @Test
        void shouldThrowExceptionAndStatus403WhenUserNotRightChangeData() throws Exception {
            // given
            UUID commentId = ID_COMMENT_FOR_IT;
            UpdateCommentDto updateCommentDto = CommentTestData.getUpdateCommentDto();
            String json = objectMapper.writeValueAsString(updateCommentDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseSubscriber());

            mockWebServer.enqueue(new MockResponse()
                    .setBody(userResponseJson)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            // when, then
            mockMvc.perform(put(URL_COMMENT + "/" + commentId)
                            .header(AUTHORIZATION, BEARER + tokenSubscriber)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isForbidden());
        }

        @Test
        void shouldThrowExceptionAndStatus404WhenCommentNotFound() throws Exception {
            // given
            UUID commentId = ID_COMMENT;
            UpdateCommentDto updateCommentDto = CommentTestData.getUpdateCommentDto();
            String json = objectMapper.writeValueAsString(updateCommentDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            mockWebServer.enqueue(new MockResponse()
                    .setBody(userResponseJson)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            // when, then
            mockMvc.perform(put(URL_COMMENT + "/" + commentId)
                            .header(AUTHORIZATION, BEARER + tokenAdmin)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.error_message")
                                    .value(ErrorMessage.COMMENT_NOT_FOUND.getMessage() + commentId));
        }

        @Test
        void shouldThrowExceptionAndStatus400WhenInvalidRequestData() throws Exception {
            // given
            UUID commentId = ID_COMMENT_FOR_IT;
            UpdateCommentDto updateCommentDto = CommentTestData.getUpdateCommentDtoIncorrect();
            String json = objectMapper.writeValueAsString(updateCommentDto);
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            mockWebServer.enqueue(new MockResponse()
                    .setBody(userResponseJson)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            // when, then
            mockMvc.perform(put(URL_COMMENT + "/" + commentId)
                            .header(AUTHORIZATION, BEARER + tokenAdmin)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpectAll(
                            status().isBadRequest());
        }
    }

    @Nested
    @Transactional
    class DeleteCommentByIdDeleteEndpointTest {

        @Test
        void shouldDeleteCommentAndStatus200() throws Exception {
            // given
            UUID commentId = ID_COMMENT_FOR_IT_DELETE;
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            mockWebServer.enqueue(new MockResponse()
                    .setBody(userResponseJson)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            // when, then
            mockMvc.perform(delete(URL_COMMENT + "/" + commentId)
                            .header(AUTHORIZATION, BEARER + tokenAdmin)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isOk());
        }

        @Test
        void shouldThrowExceptionAndStatus401() throws Exception {
            // given
            UUID commentId = ID_COMMENT_FOR_IT;
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            mockWebServer.enqueue(new MockResponse()
                    .setBody(userResponseJson)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            // when, then
            mockMvc.perform(delete(URL_COMMENT + "/" + commentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isUnauthorized());
        }

        @Test
        void shouldThrowExceptionAndStatus403() throws Exception {
            // given
            UUID commentId = ID_COMMENT_FOR_IT;
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseJournalist());

            mockWebServer.enqueue(new MockResponse()
                    .setBody(userResponseJson)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            // when, then
            mockMvc.perform(delete(URL_COMMENT + "/" + commentId)
                            .header(AUTHORIZATION, BEARER + tokenJournalist)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isForbidden());
        }

        @Test
        void shouldThrowExceptionAndStatus403WhenUserNotRightChangeData() throws Exception {
            // given
            UUID commentId = ID_COMMENT_FOR_IT;
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseSubscriber());

            mockWebServer.enqueue(new MockResponse()
                    .setBody(userResponseJson)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            // when, then
            mockMvc.perform(delete(URL_COMMENT + "/" + commentId)
                            .header(AUTHORIZATION, BEARER + tokenSubscriber)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isForbidden());
        }

        @Test
        void shouldThrowExceptionAndStatus404WhenCommentNotFound() throws Exception {
            // given
            UUID commentId = ID_COMMENT;
            String userResponseJson = objectMapper.writeValueAsString(UserTestData.getUserResponseAdmin());

            mockWebServer.enqueue(new MockResponse()
                    .setBody(userResponseJson)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            // when, then
            mockMvc.perform(delete(URL_COMMENT + "/" + commentId)
                            .header(AUTHORIZATION, BEARER + tokenAdmin)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.error_message")
                                    .value(ErrorMessage.COMMENT_NOT_FOUND.getMessage() + commentId));
        }
    }
}