package com.seitov.news.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsDto {

    private UUID articleId;
    private String title;
    private String description;
    private String content;
    private LocalDateTime publishedAt;

}
