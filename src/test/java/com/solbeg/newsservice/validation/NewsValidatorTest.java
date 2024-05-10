package com.solbeg.newsservice.validation;

import com.solbeg.newsservice.dto.request.JwtUser;
import com.solbeg.newsservice.enams.ErrorMessage;
import com.solbeg.newsservice.entity.News;
import com.solbeg.newsservice.exception.AccessException;
import com.solbeg.newsservice.repository.NewsRepository;
import com.solbeg.newsservice.util.testdata.JwtUserTestData;
import com.solbeg.newsservice.util.testdata.NewsTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static com.solbeg.newsservice.util.init.InitData.ID_AUTHOR_NEWS;
import static com.solbeg.newsservice.util.init.InitData.ID_NEWS;
import static com.solbeg.newsservice.util.init.InitData.ID_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsValidatorTest {

    @InjectMocks
    private NewsValidator newsValidator;

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Test
    void shouldReturnTrueIfUserIsAuthor() {
        //given
        UUID newsId = ID_NEWS;
        UUID userId = ID_AUTHOR_NEWS;
        News news = NewsTestData.getNews();

        when(newsRepository.findById(newsId))
                .thenReturn(Optional.of(news));

        // when
        Boolean actual = newsValidator.isAuthor(newsId, userId);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldThrowExceptionIfUserNotAuthor() {
        //given
        UUID newsId = ID_NEWS;
        UUID userId = ID_USER;
        News news = NewsTestData.getNews();

        when(newsRepository.findById(newsId))
                .thenReturn(Optional.of(news));

        // when, then
        assertThatThrownBy(() -> newsValidator.isAuthor(newsId, userId))
                .isInstanceOf(AccessException.class)
                .hasMessageContaining(ErrorMessage.ERROR_CHANGE.getMessage());
    }

    @Test
    void shouldReturnTrueIfUserIsOwnerRightChange() {
        // given
        UUID newsId = ID_NEWS;
        News news = NewsTestData.getNews();
        JwtUser jwtUser = JwtUserTestData.getJwtUserValidationNews();
        when(authentication.getPrincipal())
                .thenReturn(jwtUser);
        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(newsRepository.findById(newsId))
                .thenReturn(Optional.of(news));

        // when
        Boolean actual = newsValidator.isOwnerRightChange(newsId);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldReturnFalseIfUserIsNotOwnerRightChange() {
        // given
        UUID newsId = ID_NEWS;
        News news = NewsTestData.getNews();
        JwtUser jwtUser = JwtUserTestData.getJwtUser();
        when(authentication.getPrincipal())
                .thenReturn(jwtUser);
        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(newsRepository.findById(newsId))
                .thenReturn(Optional.of(news));

        // when
        Boolean actual = newsValidator.isOwnerRightChange(newsId);

        // then
        assertThat(actual).isFalse();
    }
}