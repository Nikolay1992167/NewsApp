package com.solbeg.newsservice.util.testdata;


import com.solbeg.newsservice.dto.response.UserResponse;
import com.solbeg.newsservice.enams.Status;

import java.time.LocalDateTime;
import java.util.List;

import static com.solbeg.newsservice.util.init.InitData.EMAIL_ADMIN_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.EMAIL_JOURNALIST_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.EMAIL_SUBSCRIBER_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.ID_ADMIN_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.ID_JOURNALIST_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.ID_SUBSCRIBER_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.ID_USER;
import static com.solbeg.newsservice.util.init.InitData.USER_EMAIL;
import static com.solbeg.newsservice.util.init.InitData.USER_FIRST_NAME;
import static com.solbeg.newsservice.util.init.InitData.USER_LAST_NAME;
import static com.solbeg.newsservice.util.init.InitData.USER_PASSWORD;


public class UserTestData {

    public static UserResponse getUserResponseSubscriber() {
        return UserResponse.builder()
                .id(ID_SUBSCRIBER_FOR_IT)
                .createdBy(null)
                .updatedBy(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .password(USER_PASSWORD)
                .email(EMAIL_SUBSCRIBER_FOR_IT)
                .roles(List.of(RoleTestData.getRoleResponseSubscriber()))
                .status(Status.ACTIVE)
                .build();
    }

    public static UserResponse getUserResponseJournalist() {
        return UserResponse.builder()
                .id(ID_JOURNALIST_FOR_IT)
                .createdBy(null)
                .updatedBy(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .password(USER_PASSWORD)
                .email(EMAIL_JOURNALIST_FOR_IT)
                .roles(List.of(RoleTestData.getRoleResponseJournalist()))
                .status(Status.ACTIVE)
                .build();
    }

    public static UserResponse getUserResponseAdmin() {
        return UserResponse.builder()
                .id(ID_ADMIN_FOR_IT)
                .createdBy(null)
                .updatedBy(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .password(USER_PASSWORD)
                .email(EMAIL_ADMIN_FOR_IT)
                .roles(List.of(RoleTestData.getRoleResponseAdmin()))
                .status(Status.ACTIVE)
                .build();
    }

    public static UserResponse getUserResponse() {
        return UserResponse.builder()
                .id(ID_USER)
                .createdBy(null)
                .updatedBy(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .password(USER_PASSWORD)
                .email(USER_EMAIL)
                .roles(List.of(RoleTestData.getRoleResponseJournalist()))
                .status(Status.ACTIVE)
                .build();
    }
}
