package com.solbeg.newsservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static com.solbeg.newsservice.util.Constants.SIZE_FROM_3_TO_500_CHARACTERS;

@Builder
public record UpdateCommentDto(

        @NotBlank
        @Size(min = 3, max = 500, message = SIZE_FROM_3_TO_500_CHARACTERS)
        String text) {
}