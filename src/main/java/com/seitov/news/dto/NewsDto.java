package com.seitov.news.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsDto {

    private UUID articleId;

    @NotNull
    @Length(min = 5, max = 100, message = "Title length must be between 5 and 100 characters")
    private String title;
    @NotNull
    @Length(min = 5, max = 500, message = "Description length must be between 5 and 500 characters")
    private String description;
    @NotNull
    @Length(min = 5, max = 2048, message = "Content length must be between 5 and 2048 characters")
    private String content;
    @NotNull
    private LocalDateTime publishedAt;

}
