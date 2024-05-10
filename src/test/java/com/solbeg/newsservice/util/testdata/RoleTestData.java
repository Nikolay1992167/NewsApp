package com.solbeg.newsservice.util.testdata;

import com.solbeg.newsservice.dto.response.RoleResponse;

import static com.solbeg.newsservice.util.init.InitData.ID_ROLE;
import static com.solbeg.newsservice.util.init.InitData.ROLE_NAME_ADMIN;
import static com.solbeg.newsservice.util.init.InitData.ROLE_NAME_JOURNALIST;
import static com.solbeg.newsservice.util.init.InitData.ROLE_NAME_SUBSCRIBER;

public class RoleTestData {

    public static RoleResponse getRoleResponseSubscriber() {
        return RoleResponse.builder()
                .id(ID_ROLE)
                .name(ROLE_NAME_SUBSCRIBER)
                .build();
    }

    public static RoleResponse getRoleResponseAdmin() {
        return RoleResponse.builder()
                .id(ID_ROLE)
                .name(ROLE_NAME_ADMIN)
                .build();
    }

    public static RoleResponse getRoleResponseJournalist() {
        return RoleResponse.builder()
                .id(ID_ROLE)
                .name(ROLE_NAME_JOURNALIST)
                .build();
    }
}