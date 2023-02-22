package com.seitov.news.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "news")
@Data
@NoArgsConstructor
public class News {

    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "title")
    private String title;
    @Column(name = "brief")
    private String brief;
    @Column(name = "content")
    private String content;
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

}
