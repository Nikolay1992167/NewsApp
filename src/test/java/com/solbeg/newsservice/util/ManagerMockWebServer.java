package com.solbeg.newsservice.util;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ManagerMockWebServer {

    private static MockWebServer mockWebServer;

    public void startMockWebServer(String userResponseJson) throws IOException {
        mockWebServer = new MockWebServer();
        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public @NotNull MockResponse dispatch(RecordedRequest request) {
                assert request.getPath() != null;
                if (request.getPath().startsWith("/api/v1/admin/")) {
                    return new MockResponse()
                            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .setBody(userResponseJson);
                } else if (request.getPath().equals("/api/v1/users/details")) {
                    return new MockResponse()
                            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .setBody(userResponseJson);
                }
                return new MockResponse().setResponseCode(404);
            }
        };
        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start(8081);
    }

    public void stopMockWebServer() throws IOException {
        mockWebServer.shutdown();
    }
}