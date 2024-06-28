package com.solbeg.newsservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static com.solbeg.newsservice.util.Constants.SIZE_FROM_3_TO_500_CHARACTERS;
import static com.solbeg.newsservice.util.Constants.SIZE_FROM_5_TO_100_CHARACTERS;

@Builder
public record CreateNewsDtoJournalist(

        @NotBlank
        @Size(min = 5, max = 100, message = SIZE_FROM_5_TO_100_CHARACTERS)
        String title,

        @NotBlank
        @Size(min = 5, max = 500, message = SIZE_FROM_3_TO_500_CHARACTERS)
        String text
) {
}
