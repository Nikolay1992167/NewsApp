package com.solbeg.newsservice.util.testdata;

import com.solbeg.newsservice.dto.request.CreateCommentDto;
import com.solbeg.newsservice.dto.request.UpdateCommentDto;
import com.solbeg.newsservice.dto.response.ResponseComment;
import com.solbeg.newsservice.dto.response.ResponseCommentNews;
import com.solbeg.newsservice.dto.response.ResponseNewsWithComments;
import com.solbeg.newsservice.entity.Comment;

import java.util.List;

import static com.solbeg.newsservice.util.init.InitData.CREATED_AT;
import static com.solbeg.newsservice.util.init.InitData.CREATED_BY;
import static com.solbeg.newsservice.util.init.InitData.ID_AUTHOR_NEWS;
import static com.solbeg.newsservice.util.init.InitData.ID_COMMENT;
import static com.solbeg.newsservice.util.init.InitData.ID_NEWS;
import static com.solbeg.newsservice.util.init.InitData.ID_NEWS_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.TEXT_COMMENT;
import static com.solbeg.newsservice.util.init.InitData.TEXT_NEWS;
import static com.solbeg.newsservice.util.init.InitData.TEXT_UPDATE_COMMENT;
import static com.solbeg.newsservice.util.init.InitData.TITLE_NEWS;
import static com.solbeg.newsservice.util.init.InitData.UPDATED_AT;
import static com.solbeg.newsservice.util.init.InitData.UPDATED_BY;
import static com.solbeg.newsservice.util.init.InitData.USER_NAME_COMMENT;

public class CommentTestData {

    public static UpdateCommentDto getUpdateCommentDtoIncorrect() {
        return UpdateCommentDto.builder()
                .text("")
                .build();
    }

    public static UpdateCommentDto getUpdateCommentDto() {
        return UpdateCommentDto.builder()
                .text(TEXT_UPDATE_COMMENT)
                .build();
    }

    public static CreateCommentDto getCommentDtoIncorrect() {
        return CreateCommentDto.builder()
                .text("")
                .newsId(ID_NEWS_FOR_IT)
                .build();
    }

    public static CreateCommentDto getCommentDtoIT() {
        return CreateCommentDto.builder()
                .text(TEXT_COMMENT)
                .newsId(ID_NEWS_FOR_IT)
                .build();
    }

    public static CreateCommentDto getCommentDto() {
        return CreateCommentDto.builder()
                .text(TEXT_COMMENT)
                .newsId(ID_NEWS)
                .build();
    }

    public static ResponseNewsWithComments getResponseNewsWithComments() {
        return ResponseNewsWithComments.builder()
                .id(ID_COMMENT)
                .createdBy(CREATED_BY)
                .updatedBy(UPDATED_BY)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .title(TITLE_NEWS)
                .text(TEXT_NEWS)
                .idAuthor(ID_AUTHOR_NEWS)
                .comments(List.of(CommentTestData.getResponseComment()))
                .build();
    }

    public static ResponseComment getResponseComment() {
        return ResponseComment.builder()
                .id(ID_COMMENT)
                .createdBy(CREATED_BY)
                .updatedBy(UPDATED_BY)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .text(TEXT_COMMENT)
                .username(USER_NAME_COMMENT)
                .build();
    }

    public static ResponseCommentNews getResponseCommentNews() {
        return ResponseCommentNews.builder()
                .id(ID_COMMENT)
                .createdBy(CREATED_BY)
                .updatedBy(UPDATED_BY)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .text(TEXT_COMMENT)
                .username(USER_NAME_COMMENT)
                .newsId(ID_NEWS)
                .build();
    }

    public static Comment getComment() {
        return Comment.builder()
                .id(ID_COMMENT)
                .createdBy(CREATED_BY)
                .updatedBy(UPDATED_BY)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .text(TEXT_COMMENT)
                .username(USER_NAME_COMMENT)
                .news(NewsTestData.getNews())
                .build();
    }
}