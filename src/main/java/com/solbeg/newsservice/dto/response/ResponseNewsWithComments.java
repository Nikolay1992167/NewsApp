package com.solbeg.newsservice.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ResponseNewsWithComments(Long id,
                                       LocalDateTime time,
                                       String title,
                                       String text,
                                       String author,
                                       List<ResponseComment> comments) {
}
