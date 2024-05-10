package com.solbeg.newsservice.util.testdata;
import com.solbeg.newsservice.dto.request.JwtUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static com.solbeg.newsservice.util.init.InitData.CREATED_BY;
import static com.solbeg.newsservice.util.init.InitData.EMAIL_JOURNALIST;
import static com.solbeg.newsservice.util.init.InitData.FIRST_NAME_JOURNALIST;
import static com.solbeg.newsservice.util.init.InitData.ID_AUTHOR_NEWS;
import static com.solbeg.newsservice.util.init.InitData.ID_JOURNALIST;
import static com.solbeg.newsservice.util.init.InitData.ID_USER;
import static com.solbeg.newsservice.util.init.InitData.LAST_NAME_JOURNALIST;
import static com.solbeg.newsservice.util.init.InitData.PASSWORD_JOURNALIST;
import static com.solbeg.newsservice.util.init.InitData.ROLE_NAME_ADMIN;
import static com.solbeg.newsservice.util.init.InitData.ROLE_NAME_JOURNALIST;
import static com.solbeg.newsservice.util.init.InitData.ROLE_NAME_SUBSCRIBER;

public class JwtUserTestData {

    public static JwtUser getJwtUserValidationCommentAdmin() {
        return JwtUser.builder()
                .id(CREATED_BY)
                .firstName(FIRST_NAME_JOURNALIST)
                .lastName(LAST_NAME_JOURNALIST)
                .password(PASSWORD_JOURNALIST)
                .email(EMAIL_JOURNALIST)
                .enabled(false)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(ROLE_NAME_ADMIN)))
                .build();
    }

    public static JwtUser getJwtUserValidationCommentNotAuthor() {
        return JwtUser.builder()
                .id(ID_USER)
                .firstName(FIRST_NAME_JOURNALIST)
                .lastName(LAST_NAME_JOURNALIST)
                .password(PASSWORD_JOURNALIST)
                .email(EMAIL_JOURNALIST)
                .enabled(false)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(ROLE_NAME_SUBSCRIBER)))
                .build();
    }

    public static JwtUser getJwtUserValidationComment() {
        return JwtUser.builder()
                .id(CREATED_BY)
                .firstName(FIRST_NAME_JOURNALIST)
                .lastName(LAST_NAME_JOURNALIST)
                .password(PASSWORD_JOURNALIST)
                .email(EMAIL_JOURNALIST)
                .enabled(false)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(ROLE_NAME_SUBSCRIBER)))
                .build();
    }

    public static JwtUser getJwtUserValidationNews() {
        return JwtUser.builder()
                .id(ID_AUTHOR_NEWS)
                .firstName(FIRST_NAME_JOURNALIST)
                .lastName(LAST_NAME_JOURNALIST)
                .password(PASSWORD_JOURNALIST)
                .email(EMAIL_JOURNALIST)
                .enabled(false)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(ROLE_NAME_JOURNALIST)))
                .build();
    }

    public static JwtUser getJwtUser() {
        return JwtUser.builder()
                .id(ID_JOURNALIST)
                .firstName(FIRST_NAME_JOURNALIST)
                .lastName(LAST_NAME_JOURNALIST)
                .password(PASSWORD_JOURNALIST)
                .email(EMAIL_JOURNALIST)
                .enabled(false)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(ROLE_NAME_JOURNALIST)))
                .build();
    }
}
