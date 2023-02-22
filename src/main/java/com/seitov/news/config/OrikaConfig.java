package com.seitov.news.config;

import com.seitov.news.dto.NewsDto;
import com.seitov.news.entity.News;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrikaConfig {

    MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    @Bean
    public MapperFacade orikaMapper() {
        mapperFactory.classMap(News.class, NewsDto.class)
                .field("id", "articleId")
                .field("brief", "description")
                .byDefault().register();
        return mapperFactory.getMapperFacade();
    }

}
