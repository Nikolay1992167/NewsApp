package com.solbeg.newsservice.mapper;

import com.solbeg.newsservice.dto.request.CreateNewsDto;
import com.solbeg.newsservice.dto.response.ResponseNews;
import com.solbeg.newsservice.entity.News;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NewsMapper {

    /**
     * Converts {@link News} to {@link ResponseNews} containing information about the news.
     *
     * @param news object {@link News}.
     * @return {@link ResponseNews} with information about the news.
     */
    ResponseNews toResponseNews(News news);

    /**
     * Converts {@link CreateNewsDto} containing news information to {@link News}.
     *
     * @param createNewsDto object {@link News}.
     * @return {@link News} without ID, creation time and comments.
     */
    News toNews(CreateNewsDto createNewsDto);

    /**
     * Updates {@link News} based on information from {@link CreateNewsDto}.
     *
     * @param oldNews updated {@link News}.
     * @param createNewsDto object {@link CreateNewsDto} with information about the object.
     * @return {@link News} updated news.
     */
    News merge(@MappingTarget News oldNews, CreateNewsDto createNewsDto);
}
