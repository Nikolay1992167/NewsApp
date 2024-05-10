package com.solbeg.newsservice.validation;

import com.solbeg.newsservice.dto.request.JwtUser;
import com.solbeg.newsservice.enams.ErrorMessage;
import com.solbeg.newsservice.entity.Comment;
import com.solbeg.newsservice.exception.AccessException;
import com.solbeg.newsservice.exception.NotFoundException;
import com.solbeg.newsservice.repository.CommentRepository;
import com.solbeg.newsservice.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    private final CommentRepository commentRepository;

    public Boolean isOwnerRightByChange(UUID commentId) {
        JwtUser user = AuthUtil.getUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.COMMENT_NOT_FOUND.getMessage() + commentId));
        if (!isAdmin(user) &&
                (!isSubscriber(user) || !user.getId().equals(comment.getCreatedBy()))) {
            throw new AccessException(ErrorMessage.ERROR_CHANGE.getMessage());
        }
        return true;
    }

    private boolean isAdmin(JwtUser user) {
        return user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    }

    private boolean isSubscriber(JwtUser user) {
        return user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("SUBSCRIBER"));
    }
}
