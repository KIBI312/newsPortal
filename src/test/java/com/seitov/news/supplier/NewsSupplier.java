package com.seitov.news.supplier;

import com.seitov.news.entity.News;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

public class NewsSupplier {

    public static News randomNews() {
        News news = new News();
        news.setId(UUID.randomUUID());
        news.setTitle(RandomStringUtils.randomAlphabetic(50));
        news.setBrief(RandomStringUtils.randomAlphanumeric(100));
        news.setContent(RandomStringUtils.randomAlphanumeric(500));
        news.setPublishedAt(LocalDateTime.now());
        return news;
    }

}
