package com.solbeg.newsservice.validation;

import com.solbeg.newsservice.dto.request.JwtUser;
import com.solbeg.newsservice.enams.ErrorMessage;
import com.solbeg.newsservice.entity.News;
import com.solbeg.newsservice.exception.AccessException;
import com.solbeg.newsservice.exception.NotFoundException;
import com.solbeg.newsservice.repository.NewsRepository;
import com.solbeg.newsservice.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NewsValidator {
    private final NewsRepository newsRepository;

    public Boolean isAuthor(UUID newsId, UUID userId) {
        News news = getNewsOrThrowException(newsId);
        if (!userId.equals(news.getIdAuthor())) {
            throw new AccessException(ErrorMessage.ERROR_CHANGE.getMessage());
        }
        return true;
    }

    public Boolean isOwnerRightChange(UUID newsId) {
        JwtUser user = AuthUtil.getUser();
        News news = getNewsOrThrowException(newsId);
        return (isAdmin(user) ||
                (isJournalist(user) && user.getId().equals(news.getIdAuthor())));
    }

    private News getNewsOrThrowException(UUID newsId) {
        return newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NEWS_NOT_FOUND.getMessage() + newsId));
    }

    private boolean isAdmin(JwtUser user) {
        return user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    }

    private Boolean isJournalist(JwtUser user) {
        return user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("JOURNALIST"));
    }
}