package com.seitov.news.service;

import com.seitov.news.dto.NewsDto;
import com.seitov.news.entity.News;
import com.seitov.news.exception.ResourceNotFoundException;
import com.seitov.news.repository.NewsRepository;
import com.seitov.news.supplier.NewsSupplier;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NewsServiceTest {

    @Mock
    private NewsRepository newsRepository;
    @Mock
    private MapperFacade orikaMapper;

    @InjectMocks
    private NewsService newsService;

    private List<NewsDto> newsDtos;
    private List<News> news = new ArrayList<>();

    @BeforeEach
    public void initData() {
        //given
        news.addAll(Stream.generate(NewsSupplier::randomNews).limit(30).collect(Collectors.toList()));
        newsDtos = news.subList(0,10).stream().map(source -> new NewsDto(source.getId(), source.getTitle(),
                    source.getBrief(), source.getContent(), source.getPublishedAt())).limit(10).collect(Collectors.toList());
    }

    private void mockMapperNewsToNewsDto() {
        //when
        when(orikaMapper.map(any(News.class), eq(NewsDto.class))).thenAnswer(source -> {
            News newsObj = (News) source.getArguments()[0];
            return new NewsDto(newsObj.getId(), newsObj.getTitle(), newsObj.getBrief(),
                    newsObj.getContent(), newsObj.getPublishedAt());
        });
    }

    private void mockMapperNewsDtoToNews() {
        //when
        when(orikaMapper.map(any(NewsDto.class), eq(News.class))).thenAnswer(source -> {
            NewsDto newsDto = (NewsDto) source.getArguments()[0];
            News newsObj = new News();
            newsObj.setId(newsDto.getArticleId());
            newsObj.setTitle(newsDto.getTitle());
            newsObj.setContent(newsDto.getContent());
            newsObj.setBrief(newsDto.getDescription());
            newsObj.setPublishedAt(newsDto.getPublishedAt());
            return newsObj;
        });
    }

    private void mockRepositorySave() {
        //when
        when(newsRepository.save(any(News.class))).thenAnswer(source -> source.getArguments()[0]);
    }

    @Test
    public void getNewsSuccessfully() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        //when
        when(newsRepository.findAllByOrderByPublishedAtDesc(pageable)).thenReturn(news.subList(0, 10));
        mockMapperNewsToNewsDto();
        //then
        assertEquals(newsService.getNews(pageable), newsDtos);
    }

    @Test
    public void getNewsNonExistingPage() {
        //given
        Pageable pageable = PageRequest.of(10,10);
        //when
        when(newsRepository.findAllByOrderByPublishedAtDesc(pageable)).thenReturn(new ArrayList<News>());
        //then
        Exception ex = assertThrows(ResourceNotFoundException.class, () -> newsService.getNews(pageable));
        assertEquals("Requested page does not exist!", ex.getMessage());
    }

    @Test
    public void getNewsViewPageSuccessfully() {
        //given
        News newsObj = news.get(0);
        NewsDto newsDto = newsDtos.get(0);
        //when
        when(newsRepository.findById(newsObj.getId())).thenReturn(Optional.of(newsObj));
        mockMapperNewsToNewsDto();
        //then
        assertEquals(newsDto, newsService.getNewsById(newsObj.getId()));
    }

    @Test
    public void getNewsViewWithNonExistingId() {
        //when
        when(newsRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(null));
        //then
        Exception ex = assertThrows(ResourceNotFoundException.class, () -> newsService.getNewsById(UUID.randomUUID()));
        assertEquals("Requested news does not exist!", ex.getMessage());
    }

    @Test
    public void addNewsSuccessfully() {
        //given
        NewsDto newsDto = new NewsDto(UUID.randomUUID(), "title", "description", "content", LocalDateTime.now());
        //when
        mockMapperNewsToNewsDto();
        mockMapperNewsDtoToNews();
        mockRepositorySave();
        //then
        assertEquals(newsDto, newsService.addNews(newsDto));
    }

    @Test
    public void updateNewsSuccessfully() {
        //given
        NewsDto newsDto = newsDtos.get(0);
        News newsObj = news.get(0);
        //when
        mockRepositorySave();
        mockMapperNewsToNewsDto();
        mockMapperNewsDtoToNews();
        when(newsRepository.findById(newsDto.getArticleId())).thenReturn(Optional.of(newsObj));
        //then
        assertEquals(newsDto, newsService.updateNews(newsDto));
    }

    @Test
    public void updateNonExistingNews() {
        //given
        NewsDto newsDto = newsDtos.get(0);
        //when
        when(newsRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(null));
        //then
        Exception ex = assertThrows(ResourceNotFoundException.class, () -> newsService.updateNews(newsDto));
        assertEquals("Requested news does not exist!", ex.getMessage());
    }

}
