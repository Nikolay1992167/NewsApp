package com.solbeg.newsservice.controller.openapi;

import com.solbeg.newsservice.dto.request.TimePeriod;
import com.solbeg.newsservice.dto.response.HistoryResponse;
import com.solbeg.newsservice.exception.model.IncorrectData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Tag(name = "HistoryNews", description = "The HistoryNews Api")
public interface HistoryNewsOpenApi {

    @Operation(
            method = "GET",
            tags = "HistoryNews",
            description = "Get page of HistoryResponse for all time period by news id.",
            parameters = {
                    @Parameter(name = "newsId", description = "Id of news", example = "80fbdf76-0732-4145-9793-640630cafca3")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HistoryResponse.class),
                                    examples = @ExampleObject("""
                                            {
                                                "content": [
                                                    {
                                                        "id": "80fbdf76-0732-4145-9793-640630cafca3",
                                                        "createdAt": "2024-06-14T14:06:29.141",
                                                        "historyMessage": "The comment has been created: News(title=News from Journalist Nikolay, text=The weather is determined by an eastern cyclone with abundant rainfall., idAuthor=b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12)"
                                                    },
                                                    {
                                                        "id": "80fbdf76-0732-4145-9793-640630cafca3",
                                                        "createdAt": "2024-06-14T14:07:13.235",
                                                        "historyMessage": "The comment has been changed: News(title=Updated news from journalist Nikolay, text=The weather is great today., idAuthor=b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12)"
                                                    }
                                                ],
                                                "pageable": "INSTANCE",
                                                "totalPages": 1,
                                                "totalElements": 2,
                                                "last": true,
                                                "size": 2,
                                                "number": 0,
                                                "sort": {
                                                    "empty": true,
                                                    "unsorted": true,
                                                    "sorted": false
                                                },
                                                "numberOfElements": 2,
                                                "first": true,
                                                "empty": false
                                            }
                                            """))),
                    @ApiResponse(responseCode = "400", description = "Id is incorrect.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = IncorrectData.class), examples = @ExampleObject("""
                                    {
                                        "timestamp": "2024-06-15T19:42:45.360526498",
                                        "error_message": "UUID was entered incorrectly!",
                                        "error_status": 400
                                    }
                                    """))),
                    @ApiResponse(responseCode = "404", description = "News not found.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = IncorrectData.class), examples = @ExampleObject("""
                                    {
                                        "timestamp": "2024-06-15T19:27:42.763128844",
                                        "error_message": "News not found with 80fbdf76-0732-4145-9793-640630cafca4",
                                        "error_status": 404
                                    }
                                    """))),
                    @ApiResponse(responseCode = "401", description = "Not authorization.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = IncorrectData.class), examples = @ExampleObject("""
                                    {
                                        "timestamp": "2024-06-15T19:37:55.917094694",
                                        "error_message": "Full authentication is required to access this resource",
                                        "error_status": 401
                                    }
                                    """))),
                    @ApiResponse(responseCode = "403", description = "User with not admin role.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = IncorrectData.class), examples = @ExampleObject("""
                                    {
                                        "timestamp": "2024-06-15T19:42:34.846076377",
                                        "error_message": "Access Denied",
                                        "error_status": 403
                                    }
                                    """)))
            }
    )
    Page<HistoryResponse> findHistoryOfNews(UUID newsId, Pageable pageable);

    @Operation(
            method = "GET",
            tags = "HistoryNews",
            description = "Get page of HistoryResponse for period by news id.",
            parameters = {
                    @Parameter(name = "newsId", description = "Id of news", example = "80fbdf76-0732-4145-9793-640630cafca3")
            },
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TimePeriod.class),
                            examples = @ExampleObject("""
                                    {
                                        "startDate": "2024-06-10T06:00:00",
                                        "endDate": "2024-06-15T06:00:00"
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HistoryResponse.class),
                                    examples = @ExampleObject("""
                                            {
                                                "content": [
                                                    {
                                                        "id": "80fbdf76-0732-4145-9793-640630cafca3",
                                                        "createdAt": "2024-06-14T14:06:29.141",
                                                        "historyMessage": "The comment has been created: News(title=News from Journalist Nikolay, text=The weather is determined by an eastern cyclone with abundant rainfall., idAuthor=b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12)"
                                                    },
                                                    {
                                                        "id": "80fbdf76-0732-4145-9793-640630cafca3",
                                                        "createdAt": "2024-06-14T14:07:13.235",
                                                        "historyMessage": "The comment has been changed: News(title=Updated news from journalist Nikolay, text=The weather is great today., idAuthor=b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12)"
                                                    }
                                                ],
                                                "pageable": "INSTANCE",
                                                "totalPages": 1,
                                                "totalElements": 2,
                                                "last": true,
                                                "size": 2,
                                                "number": 0,
                                                "sort": {
                                                    "empty": true,
                                                    "unsorted": true,
                                                    "sorted": false
                                                },
                                                "numberOfElements": 2,
                                                "first": true,
                                                "empty": false
                                            }
                                            """))),
                    @ApiResponse(responseCode = "400", description = "Id is incorrect.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = IncorrectData.class), examples = @ExampleObject("""
                                    {
                                        "timestamp": "2024-06-15T21:48:51.103722432",
                                        "error_message": "UUID was entered incorrectly!",
                                        "error_status": 400
                                    }
                                    """))),
                    @ApiResponse(responseCode = "404", description = "News not found.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = IncorrectData.class), examples = @ExampleObject("""
                                    {
                                        "timestamp": "2024-06-15T21:49:11.757656297",
                                        "error_message": "News not found with 80fbdf76-0732-4145-9793-640630cafca4",
                                        "error_status": 404
                                    }
                                    """))),
                    @ApiResponse(responseCode = "401", description = "Not authorization.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = IncorrectData.class), examples = @ExampleObject("""
                                    {
                                        "timestamp": "2024-06-15T19:37:55.917094694",
                                        "error_message": "Full authentication is required to access this resource",
                                        "error_status": 401
                                    }
                                    """))),
                    @ApiResponse(responseCode = "403", description = "User with not admin role.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = IncorrectData.class), examples = @ExampleObject("""
                                    {
                                        "timestamp": "2024-06-15T19:42:34.846076377",
                                        "error_message": "Access Denied",
                                        "error_status": 403
                                    }
                                    """))),
                    @ApiResponse(responseCode = "400", description = "Time period is incorrect.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = IncorrectData.class), examples = @ExampleObject("""
                                    {
                                        "timestamp": "2024-06-15T21:56:02.161689422",
                                        "error_message": "StartDate must be earlier than EndDate, and they cannot be the same date.",
                                        "error_status": 400
                                    }
                                    """)))
            }
    )
    Page<HistoryResponse> findHistoryOfCommentForTimePeriod(UUID newsId, TimePeriod timePeriod, Pageable pageable);
}
