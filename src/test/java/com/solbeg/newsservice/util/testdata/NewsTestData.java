package com.solbeg.newsservice.util.testdata;

import com.solbeg.newsservice.dto.request.CreateNewsDto;
import com.solbeg.newsservice.dto.request.CreateNewsDtoJournalist;
import com.solbeg.newsservice.dto.response.ResponseNews;
import com.solbeg.newsservice.entity.News;

import java.util.List;

import static com.solbeg.newsservice.util.init.InitData.CREATED_AT;
import static com.solbeg.newsservice.util.init.InitData.CREATED_BY;
import static com.solbeg.newsservice.util.init.InitData.ID_ADMIN_FOR_IT;
import static com.solbeg.newsservice.util.init.InitData.ID_AUTHOR_NEWS;
import static com.solbeg.newsservice.util.init.InitData.ID_NEWS;
import static com.solbeg.newsservice.util.init.InitData.TEXT_NEWS;
import static com.solbeg.newsservice.util.init.InitData.TITLE_NEWS;
import static com.solbeg.newsservice.util.init.InitData.UPDATED_AT;
import static com.solbeg.newsservice.util.init.InitData.UPDATED_BY;

public class NewsTestData {

    public static CreateNewsDtoJournalist getCreateNewsDtoJournalistIncorrect() {
        return CreateNewsDtoJournalist.builder()
                .title("")
                .text(TEXT_NEWS)
                .build();
    }

    public static CreateNewsDtoJournalist getCreateNewsDtoJournalist() {
        return CreateNewsDtoJournalist.builder()
                .title(TITLE_NEWS)
                .text(TEXT_NEWS)
                .build();
    }

    public static CreateNewsDto getCreateNewsDtoITForAdmin() {
        return CreateNewsDto.builder()
                .title(TITLE_NEWS)
                .text(TEXT_NEWS)
                .idAuthor(ID_ADMIN_FOR_IT)
                .build();
    }

    public static CreateNewsDto getCreateNewsDtoIncorrect() {
        return CreateNewsDto.builder()
                .title("")
                .text("")
                .idAuthor(ID_AUTHOR_NEWS)
                .build();
    }

    public static CreateNewsDto getCreateNewsDto() {
        return CreateNewsDto.builder()
                .title(TITLE_NEWS)
                .text(TEXT_NEWS)
                .idAuthor(ID_AUTHOR_NEWS)
                .build();
    }

    public static News getNews() {
        return News.builder()
                .id(ID_NEWS)
                .createdBy(CREATED_BY)
                .updatedBy(UPDATED_BY)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .title(TITLE_NEWS)
                .text(TEXT_NEWS)
                .idAuthor(ID_AUTHOR_NEWS)
                .comments(List.of())
                .build();
    }

    public static ResponseNews getResponseNews() {
        return ResponseNews.builder()
                .id(ID_NEWS)
                .createdBy(CREATED_BY)
                .updatedBy(UPDATED_BY)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .title(TITLE_NEWS)
                .text(TEXT_NEWS)
                .idAuthor(ID_AUTHOR_NEWS)
                .build();
    }
}