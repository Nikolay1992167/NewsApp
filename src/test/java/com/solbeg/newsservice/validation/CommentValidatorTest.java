package com.solbeg.newsservice.validation;

import com.solbeg.newsservice.dto.request.JwtUser;
import com.solbeg.newsservice.enams.ErrorMessage;
import com.solbeg.newsservice.entity.Comment;
import com.solbeg.newsservice.exception.AccessException;
import com.solbeg.newsservice.exception.NotFoundException;
import com.solbeg.newsservice.repository.CommentRepository;
import com.solbeg.newsservice.util.testdata.CommentTestData;
import com.solbeg.newsservice.util.testdata.JwtUserTestData;
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

import static com.solbeg.newsservice.util.init.InitData.ID_COMMENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentValidatorTest {

    @InjectMocks
    private CommentValidator commentValidator;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Test
    void shouldReturnTrueIfUserIsOwnerRightByChange() {
        // given
        UUID commentId = ID_COMMENT;
        Comment comment = CommentTestData.getComment();
        JwtUser jwtUser = JwtUserTestData.getJwtUserValidationComment();
        when(authentication.getPrincipal())
                .thenReturn(jwtUser);
        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(commentRepository.findById(commentId))
                .thenReturn(Optional.of(comment));

        // when
        Boolean actual = commentValidator.isOwnerRightByChange(commentId);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldReturnTrueIfUserAdminIsOwnerRightByChange() {
        // given
        UUID commentId = ID_COMMENT;
        Comment comment = CommentTestData.getComment();
        JwtUser jwtUser = JwtUserTestData.getJwtUserValidationCommentAdmin();
        when(authentication.getPrincipal())
                .thenReturn(jwtUser);
        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(commentRepository.findById(commentId))
                .thenReturn(Optional.of(comment));

        // when
        Boolean actual = commentValidator.isOwnerRightByChange(commentId);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenCommentNotFound() {
        // given
        UUID commentId = ID_COMMENT;
        JwtUser jwtUser = JwtUserTestData.getJwtUserValidationCommentAdmin();
        when(authentication.getPrincipal())
                .thenReturn(jwtUser);
        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(commentRepository.findById(commentId))
                .thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> commentValidator.isOwnerRightByChange(commentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ErrorMessage.COMMENT_NOT_FOUND.getMessage() + commentId);
    }

    @Test
    void shouldThrowExceptionIfUserJournalistIsNotOwnerRightByChange() {
        // given
        UUID commentId = ID_COMMENT;
        Comment comment = CommentTestData.getComment();
        JwtUser jwtUser = JwtUserTestData.getJwtUser();
        when(authentication.getPrincipal())
                .thenReturn(jwtUser);
        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(commentRepository.findById(commentId))
                .thenReturn(Optional.of(comment));

        // when, then
        assertThatThrownBy(() -> commentValidator.isOwnerRightByChange(commentId))
                .isInstanceOf(AccessException.class)
                .hasMessageContaining(ErrorMessage.ERROR_CHANGE.getMessage());
    }

    @Test
    void shouldThrowExceptionIfUserSubscriberIsNotOwnerRightByChange() {
        // given
        UUID commentId = ID_COMMENT;
        Comment comment = CommentTestData.getComment();
        JwtUser jwtUser = JwtUserTestData.getJwtUserValidationCommentNotAuthor();
        when(authentication.getPrincipal())
                .thenReturn(jwtUser);
        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(commentRepository.findById(commentId))
                .thenReturn(Optional.of(comment));

        // when, then
        assertThatThrownBy(() -> commentValidator.isOwnerRightByChange(commentId))
                .isInstanceOf(AccessException.class)
                .hasMessageContaining(ErrorMessage.ERROR_CHANGE.getMessage());
    }
}