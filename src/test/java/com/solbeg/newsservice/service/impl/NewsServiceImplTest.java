package com.solbeg.newsservice.service.impl;

import com.solbeg.newsservice.dto.request.CreateNewsDto;
import com.solbeg.newsservice.dto.request.CreateNewsDtoJournalist;
import com.solbeg.newsservice.dto.request.Filter;
import com.solbeg.newsservice.dto.request.JwtUser;
import com.solbeg.newsservice.dto.response.ResponseNews;
import com.solbeg.newsservice.dto.response.UserResponse;
import com.solbeg.newsservice.enams.ErrorMessage;
import com.solbeg.newsservice.entity.News;
import com.solbeg.newsservice.exception.AccessException;
import com.solbeg.newsservice.exception.CreateObjectException;
import com.solbeg.newsservice.exception.NotFoundException;
import com.solbeg.newsservice.mapper.NewsMapper;
import com.solbeg.newsservice.repository.NewsRepository;
import com.solbeg.newsservice.service.UserDataService;
import com.solbeg.newsservice.util.testdata.FilterTestData;
import com.solbeg.newsservice.util.testdata.JwtUserTestData;
import com.solbeg.newsservice.util.testdata.NewsTestData;
import com.solbeg.newsservice.util.testdata.UserTestData;
import com.solbeg.newsservice.validation.NewsValidator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.solbeg.newsservice.util.init.InitData.AUTHORIZATION_TOKEN;
import static com.solbeg.newsservice.util.init.InitData.DEFAULT_PAGE_REQUEST_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.ID_JOURNALIST;
import static com.solbeg.newsservice.util.init.InitData.ID_NEWS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
class NewsServiceImplTest {

    @InjectMocks
    private NewsServiceImpl newsService;

    @Mock
    private NewsValidator newsValidator;

    @Mock
    private NewsMapper newsMapper;

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private UserDataService userDataService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Nested
    class FindNewsById {

        @Test
        void shouldReturnExpectedValue() {
            // given
            UUID newsId = ID_NEWS;
            News news = NewsTestData.getNews();
            ResponseNews expected = NewsTestData.getResponseNews();
            when(newsRepository.findById(newsId))
                    .thenReturn(Optional.of(news));
            when(newsMapper.toResponseNews(news))
                    .thenReturn(expected);
            // when
            ResponseNews actual = newsService.findNewsById(newsId);

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void shouldThrowExceptionWhenNewsIsNotExist() {
            // given
            UUID newsId = UUID.randomUUID();

            // when
            assertThatThrownBy(() -> newsService.findNewsById(newsId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining(ErrorMessage.NEWS_NOT_FOUND.getMessage() + newsId);
        }
    }

    @Nested
    class GetAllNews {
        @Test
        void shouldReturnPageOfResponseNews() {
            // given
            int expectedSize = 1;
            List<News> newsList = List.of(NewsTestData.getNews());
            Page<News> page = new PageImpl<>(newsList);
            when(newsRepository.findAll(any(PageRequest.class)))
                    .thenReturn(page);

            // when
            Page<ResponseNews> actual = newsService.getAllNews(DEFAULT_PAGE_REQUEST_FOR_IT);

            // then
            assertThat(actual.getTotalElements()).isEqualTo(expectedSize);
        }

        @Test
        void shouldCheckEmpty() {
            // given
            Page<News> page = new PageImpl<>(List.of());
            when(newsRepository.findAll(any(PageRequest.class)))
                    .thenReturn(page);

            // when
            Page<ResponseNews> actual = newsService.getAllNews(DEFAULT_PAGE_REQUEST_FOR_IT);

            // then
            assertThat(actual).isEmpty();
        }
    }

    @Nested
    class FindNewsByFilter {

        @Test
        void shouldReturnPageOfResponseNews() {
            // given
            Filter filter = FilterTestData.getFilter();
            int expectedSize = 1;
            List<News> newsList = List.of(NewsTestData.getNews());
            Page<News> page = new PageImpl<>(newsList);
            when(newsRepository.findAll(any(PageRequest.class)))
                    .thenReturn(page);

            // when
            Page<ResponseNews> actual = newsService.findNewsByFilter(filter, DEFAULT_PAGE_REQUEST_FOR_IT);

            // then
            assertThat(actual.getTotalElements()).isEqualTo(expectedSize);
        }

        @Test
        void shouldCheckEmpty() {
            // given
            Filter filter = FilterTestData.getFilter();
            Page<News> page = new PageImpl<>(List.of());
            when(newsRepository.findAll(any(PageRequest.class)))
                    .thenReturn(page);

            // when
            Page<ResponseNews> actual = newsService.findNewsByFilter(filter, DEFAULT_PAGE_REQUEST_FOR_IT);

            // then
            assertThat(actual).isEmpty();
        }
    }

    @Nested
    class CreateNewsAdmin {

        @Test
        void shouldReturnExpectedValue() {
            // given
            JwtUser jwtUser = JwtUserTestData.getJwtUser();
            UserResponse userResponse = UserTestData.getUserResponse();
            News news = NewsTestData.getNews();
            CreateNewsDto createNewsDto = NewsTestData.getCreateNewsDto();
            ResponseNews expected = NewsTestData.getResponseNews();
            when(authentication.getPrincipal())
                    .thenReturn(jwtUser);
            when(securityContext.getAuthentication())
                    .thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);
            when(userDataService.getUserData(createNewsDto.idAuthor(), AUTHORIZATION_TOKEN))
                    .thenReturn(userResponse);
            when(newsMapper.toNews(createNewsDto))
                    .thenReturn(news);
            when(newsRepository.persistAndFlush(any(News.class)))
                    .thenReturn(news);
            when(newsMapper.toResponseNews(news))
                    .thenReturn(expected);

            // when
            ResponseNews actual = newsService.createNewsAdmin(createNewsDto, AUTHORIZATION_TOKEN);

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void shouldThrowException() {
            // given
            UserResponse userResponse = UserTestData.getUserResponse();
            News news = NewsTestData.getNews();
            CreateNewsDto createNewsDto = NewsTestData.getCreateNewsDto();
            ResponseNews expected = NewsTestData.getResponseNews();
            when(userDataService.getUserData(createNewsDto.idAuthor(), AUTHORIZATION_TOKEN))
                    .thenReturn(userResponse);
            when(newsMapper.toNews(createNewsDto))
                    .thenReturn(null);
            when(newsRepository.persistAndFlush(any(News.class)))
                    .thenReturn(news);
            when(newsMapper.toResponseNews(news))
                    .thenReturn(expected);

            // when, then
            assertThatThrownBy(() -> newsService.createNewsAdmin(createNewsDto, AUTHORIZATION_TOKEN))
                    .isInstanceOf(CreateObjectException.class)
                    .hasMessageContaining(ErrorMessage.ERROR_CREATE_OBJECT.getMessage());
        }
    }

    @Nested
    class CreateNewsJournalist {

        @Test
        void shouldReturnExpectedValue() {
            // given
            JwtUser jwtUser = JwtUserTestData.getJwtUser();
            News news = NewsTestData.getNews();
            CreateNewsDtoJournalist createNewsDtoJournalist = NewsTestData.getCreateNewsDtoJournalist();
            ResponseNews expected = NewsTestData.getResponseNews();
            when(authentication.getPrincipal())
                    .thenReturn(jwtUser);
            when(securityContext.getAuthentication())
                    .thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);
            when(newsMapper.toNews(createNewsDtoJournalist))
                    .thenReturn(news);
            when(newsRepository.persistAndFlush(any(News.class)))
                    .thenReturn(news);
            when(newsMapper.toResponseNews(news))
                    .thenReturn(expected);

            // when
            ResponseNews actual = newsService.createNewsJournalist(createNewsDtoJournalist);

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void shouldThrowException() {
            // given
            JwtUser jwtUser = JwtUserTestData.getJwtUser();
            News news = NewsTestData.getNews();
            CreateNewsDtoJournalist createNewsDtoJournalist = NewsTestData.getCreateNewsDtoJournalist();
            ResponseNews expected = NewsTestData.getResponseNews();
            when(authentication.getPrincipal())
                    .thenReturn(jwtUser);
            when(securityContext.getAuthentication())
                    .thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);
            when(newsMapper.toNews(createNewsDtoJournalist))
                    .thenReturn(null);
            when(newsRepository.persistAndFlush(any(News.class)))
                    .thenReturn(news);
            when(newsMapper.toResponseNews(news))
                    .thenReturn(expected);

            // when, then
            assertThatThrownBy(() -> newsService.createNewsJournalist(createNewsDtoJournalist))
                    .isInstanceOf(CreateObjectException.class)
                    .hasMessageContaining(ErrorMessage.ERROR_CREATE_OBJECT.getMessage());
        }
    }

    @Nested
    class UpdateNewsAdmin {

        @Test
        void shouldReturnExpectedValue() {
            // given
            UUID newsId = ID_NEWS;
            JwtUser jwtUser = JwtUserTestData.getJwtUser();
            UserResponse userResponse = UserTestData.getUserResponse();
            News news = NewsTestData.getNews();
            CreateNewsDto createNewsDto = NewsTestData.getCreateNewsDto();
            ResponseNews expected = NewsTestData.getResponseNews();
            when(authentication.getPrincipal())
                    .thenReturn(jwtUser);
            when(securityContext.getAuthentication())
                    .thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);
            when(userDataService.getUserData(createNewsDto.idAuthor(), AUTHORIZATION_TOKEN))
                    .thenReturn(userResponse);
            when(newsRepository.findById(newsId))
                    .thenReturn(Optional.of(news));
            when(newsMapper.merge(news, createNewsDto))
                    .thenReturn(news);
            when(newsRepository.updateAndFlush(any(News.class)))
                    .thenReturn(news);
            when(newsMapper.toResponseNews(news))
                    .thenReturn(expected);

            // when
            ResponseNews actual = newsService.updateNewsAdmin(newsId, createNewsDto, AUTHORIZATION_TOKEN);

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void shouldThrowException() {
            // given
            UUID newsId = ID_NEWS;
            UserResponse userResponse = UserTestData.getUserResponse();
            News news = NewsTestData.getNews();
            CreateNewsDto createNewsDto = NewsTestData.getCreateNewsDto();
            ResponseNews expected = NewsTestData.getResponseNews();
            when(userDataService.getUserData(createNewsDto.idAuthor(), AUTHORIZATION_TOKEN))
                    .thenReturn(userResponse);
            when(newsRepository.findById(newsId))
                    .thenReturn(Optional.empty());
            when(newsMapper.merge(news, createNewsDto))
                    .thenReturn(news);
            when(newsRepository.updateAndFlush(any(News.class)))
                    .thenReturn(news);
            when(newsMapper.toResponseNews(news))
                    .thenReturn(expected);

            // when, then
            assertThatThrownBy(() -> newsService.updateNewsAdmin(newsId, createNewsDto, AUTHORIZATION_TOKEN))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining(ErrorMessage.NEWS_NOT_FOUND.getMessage() + newsId);
        }
    }

    @Nested
    class UpdateNewsJournalist {

        @Test
        void shouldReturnNull() {
            // given
            UUID newsId = ID_NEWS;
            JwtUser jwtUser = JwtUserTestData.getJwtUser();
            CreateNewsDtoJournalist createNewsDto = NewsTestData.getCreateNewsDtoJournalist();
            when(authentication.getPrincipal())
                    .thenReturn(jwtUser);
            when(securityContext.getAuthentication())
                    .thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);
            when(newsValidator.isAuthor(newsId, ID_JOURNALIST))
                    .thenReturn(Boolean.FALSE);

            // when
            ResponseNews actual = newsService.updateNewsJournalist(newsId, createNewsDto);

            // then
            assertThat(actual).isNull();
        }

        @Test
        void shouldReturnExpectedValue() {
            // given
            JwtUser jwtUser = JwtUserTestData.getJwtUser();
            News news = NewsTestData.getNews();
            CreateNewsDtoJournalist createNewsDto = NewsTestData.getCreateNewsDtoJournalist();
            ResponseNews expected = NewsTestData.getResponseNews();
            when(authentication.getPrincipal())
                    .thenReturn(jwtUser);
            when(securityContext.getAuthentication())
                    .thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);
            when(newsValidator.isAuthor(ID_NEWS, ID_JOURNALIST))
                    .thenReturn(Boolean.TRUE);
            when(newsRepository.findById(ID_NEWS))
                    .thenReturn(Optional.of(news));
            when(newsMapper.merge(news, createNewsDto))
                    .thenReturn(news);
            when(newsRepository.updateAndFlush(news))
                    .thenReturn(news);
            when(newsMapper.toResponseNews(news))
                    .thenReturn(expected);

            // when
            ResponseNews actual = newsService.updateNewsJournalist(ID_NEWS, createNewsDto);

            // then
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class DeleteNews {

        @Test
        void shouldDeleteNewsWhenUserIsOwner() {
            // given
            UUID newsId = ID_NEWS;
            when(newsValidator.isOwnerRightChange(newsId))
                    .thenReturn(Boolean.TRUE);
            doNothing().when(newsRepository).deleteById(newsId);

            // when
            newsService.deleteNews(newsId);

            // then
            verify(newsValidator, times(1)).isOwnerRightChange(ID_NEWS);
        }

        @Test
        void shouldThrowAccessExceptionWhenUserIsNotOwner() {
            // given
            UUID newsId = ID_NEWS;
            when(newsValidator.isOwnerRightChange(newsId))
                    .thenReturn(Boolean.FALSE);

            // when & then
            assertThatThrownBy(() -> newsService.deleteNews(newsId))
                    .isInstanceOf(AccessException.class)
                    .hasMessage(ErrorMessage.ERROR_CHANGE.getMessage());
        }
    }
}