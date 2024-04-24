package com.solbeg.newsservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateNewsDtoJournalist(

        @NotBlank
        @Size(min = 5, max = 100)
        String title,

        @NotBlank
        @Size(min = 5, max = 500)
        String text
) {
}
