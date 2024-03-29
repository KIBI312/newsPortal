package com.seitov.news.service;

import com.seitov.news.dto.NewsDto;
import com.seitov.news.entity.News;
import com.seitov.news.exception.ResourceNotFoundException;
import com.seitov.news.repository.NewsRepository;
import ma.glasnost.orika.MapperFacade;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NewsService {

    private final NewsRepository newsRepository;
    private final MapperFacade orikaMapper;

    public NewsService(NewsRepository newsRepository, MapperFacade orikaMapper) {
        this.newsRepository = newsRepository;
        this.orikaMapper = orikaMapper;
    }

    public List<NewsDto> getNews(Pageable pageable) {
        List<News> news = newsRepository.findAllByOrderByPublishedAtDesc(pageable);
        if(news.size() <= 0) {
            throw new ResourceNotFoundException("Requested page does not exist!");
        }
        return news.stream().map(source -> orikaMapper.map(source, NewsDto.class)).collect(Collectors.toList());
    }

    public NewsDto getNewsById(UUID id) {
        Optional<News> news = newsRepository.findById(id);
        if(news.isEmpty()) {
            throw new ResourceNotFoundException("Requested news does not exist!");
        }
        return orikaMapper.map(news.get(), NewsDto.class);
    }

    public NewsDto addNews(NewsDto newsDto) {
        News news = orikaMapper.map(newsDto, News.class);
        return orikaMapper.map(newsRepository.save(news), NewsDto.class);
    }

    public NewsDto updateNews(NewsDto newsDto) {
        Optional<News> news = newsRepository.findById(newsDto.getArticleId());
        if(news.isEmpty()) {
            throw new ResourceNotFoundException("Requested news does not exist!");
        }
        News updatedNews = orikaMapper.map(newsDto, News.class);
        updatedNews.setLastModifiedAt(LocalDateTime.now());
        return orikaMapper.map(newsRepository.save(updatedNews), NewsDto.class);
    }

    public void deleteById(UUID id) {
        newsRepository.deleteById(id);
    }

    public void deleteAllById(Set<UUID> ids) {
        newsRepository.deleteAllById(ids);
    }


}
