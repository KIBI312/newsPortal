package com.seitov.news.controller;

import com.seitov.news.dto.NewsDto;
import com.seitov.news.entity.News;
import com.seitov.news.exception.ResourceNotFoundException;
import com.seitov.news.service.NewsService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class NewsControllerTest {

    @MockBean
    private NewsService newsService;

    @InjectMocks
    private NewsController newsController;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getMainPage() throws Exception {
        MvcResult result = mockMvc.perform(get("/"))
                .andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("News"));
    }

    @Test
    public void getMainPageRussian() throws Exception {
        MvcResult result = mockMvc.perform(get("/?lang=ru"))
                .andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Новости"));
    }

    @Test
    public void getMainPageWithPageParam() throws Exception {
        MvcResult result = mockMvc.perform(get("/?page=0"))
                .andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("News"));
    }

    @Test
    public void getMainPageWithNegativePage() throws Exception {
        MvcResult result = mockMvc.perform(get("/?page=-10"))
                .andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("News"));
    }

    @Test
    public void getMainPageWithNonExistingPage() throws Exception {
        //given
        Pageable pageable = PageRequest.of(100, 10);
        //when
        when(newsService.getNews(pageable)).thenThrow(new ResourceNotFoundException("Requested page does not exist!"));
        //then
        MvcResult result = mockMvc.perform(get("/?page=100"))
                .andExpect(status().isNotFound()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Requested page does not exist!"));
    }

    @Test
    public void getNewsView() throws Exception {
        //given
        News newsObj = new News();
        newsObj.setId(UUID.randomUUID());
        newsObj.setTitle("SomeTitle");
        NewsDto newsDto = new NewsDto();
        newsDto.setArticleId(newsObj.getId());
        newsDto.setTitle(newsObj.getTitle());
        //when
        when(newsService.getNewsById(newsObj.getId())).thenReturn(newsDto);
        //then
        MvcResult result = mockMvc.perform(get("/news/"+newsObj.getId()))
                .andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains(newsObj.getTitle()));
    }

    @Test
    public void getNewsViewNonExisting() throws Exception {
        //when
        when(newsService.getNewsById(any(UUID.class))).thenThrow(new ResourceNotFoundException("Requested news does not exist!"));
        //then
        MvcResult result = mockMvc.perform(get("/news/" + UUID.randomUUID()))
                .andExpect(status().isNotFound()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Requested news does not exist!"));
    }

}
