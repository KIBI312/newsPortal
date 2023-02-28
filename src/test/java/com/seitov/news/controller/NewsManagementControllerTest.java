package com.seitov.news.controller;

import com.seitov.news.dto.NewsDto;
import com.seitov.news.entity.News;
import com.seitov.news.entity.User;
import com.seitov.news.exception.ResourceNotFoundException;
import com.seitov.news.service.NewsService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class NewsManagementControllerTest {

    @MockBean
    private NewsService newsService;

    @InjectMocks
    private NewsController newsController;

    @Autowired
    MockMvc mockMvc;

    private static User admin;
    private static User user;

    @BeforeAll
    public static void setPrincipal() {
        admin = new User();
        admin.setUsername("admin");
        admin.setPassword("adminPassword");
        admin.setRole("ROLE_ADMIN");
        user = new User();
        user.setUsername("user");
        user.setPassword("userPassword");
        user.setRole("ROLE_USER");
    }

    @Test
    public void getManagementPagesWithUserRole() throws Exception {
        //when-then
        mockMvc.perform(get("/management").with(user(user)))
                .andExpect(status().isForbidden()).andReturn();
        mockMvc.perform(get("/management/"+UUID.randomUUID()).with(user(user)))
                .andExpect(status().isForbidden()).andReturn();
        mockMvc.perform(get("/management/add").with(user(user)))
                .andExpect(status().isForbidden()).andReturn();
        mockMvc.perform(get("/management/edit"+UUID.randomUUID()).with(user(user)))
                .andExpect(status().isForbidden()).andReturn();
    }

    @Test
    public void getManagementPageWithAdminRole() throws Exception {
        //when-then
        MvcResult result = mockMvc.perform(get("/management").with(user(admin)))
                .andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("News management"));
    }

    @Test
    public void getManagementPageWithNegativePageParam() throws Exception {
        //when-then
        MvcResult result = mockMvc.perform(get("/management?page=-10").with(user(admin)))
                .andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("News management"));
    }

    @Test
    public void getManagementPageWithPageParam() throws Exception {
        //when-then
        MvcResult result = mockMvc.perform(get("/management?page=5").with(user(admin)))
                .andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("News management"));
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
        MvcResult result = mockMvc.perform(get("/management/"+newsObj.getId()).with(user(admin)))
                .andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains(newsObj.getTitle()));
    }

    @Test
    public void getNewsViewNonExisting() throws Exception {
        //when
        when(newsService.getNewsById(any(UUID.class))).thenThrow(new ResourceNotFoundException("Requested news does not exist!"));
        //then
        MvcResult result = mockMvc.perform(get("/management/" + UUID.randomUUID()).with(user(admin)))
                .andExpect(status().isNotFound()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Requested news does not exist!"));
    }

    @Test
    public void getManagementAddNewsPage() throws Exception {
        //when-then
        MvcResult result = mockMvc.perform(get("/management/add").with(user(admin)))
                .andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Save"));
    }

    @Test
    public void postManagementAddNews() throws Exception {
        //when-then
        MvcResult result = mockMvc.perform(post("/management/add")
                        .param("title", "SomeTitle")
                        .param("publishedAt", "2023-02-25T20:00")
                        .param("description", "SomeDescription")
                        .param("content", "SomeContent")
                        .with(user(admin)).with(csrf()))
                .andExpect(redirectedUrl("/management")).andReturn();
    }

    @Test
    public void postManagementAddNewsInvalidTitle() throws Exception {
        //when-then
        MvcResult result = mockMvc.perform(post("/management/add")
                        .param("title", "Som")
                        .param("publishedAt", "2023-02-25T20:00")
                        .param("description", "SomeDescription")
                        .param("content", "SomeContent")
                        .with(user(admin)).with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Title length must be between 5 and 100 characters"));
    }

    @Test
    public void postManagementAddNewsInvalidDate() throws Exception {
        //when-then
        MvcResult result = mockMvc.perform(post("/management/add")
                        .param("title", "Som")
                        .param("publishedAt", "2023/02/25T20:00")
                        .param("description", "SomeDescription")
                        .param("content", "SomeContent")
                        .with(user(admin)).with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Please use specified datetime format!"));
    }

    @Test
    public void postManagementAddNewsInvalidDescription() throws Exception {
        //when-then
        MvcResult result = mockMvc.perform(post("/management/add")
                        .param("title", "Som")
                        .param("publishedAt", "2023-02-25T20:00")
                        .param("description", "")
                        .param("content", "SomeContent")
                        .with(user(admin)).with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Description length must be between 5 and 500 characters"));
    }

    @Test
    public void postManagementAddNewsInvalidContent() throws Exception {
        //when-then
        MvcResult result = mockMvc.perform(post("/management/add")
                        .param("title", "Som")
                        .param("publishedAt", "2023-02-25T20:00")
                        .param("description", "SomeDescription")
                        .param("content", "")
                        .with(user(admin)).with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Content length must be between 5 and 2048 characters"));
    }

    @Test
    public void getNewsEditPage() throws Exception {
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
        MvcResult result = mockMvc.perform(get("/management/edit/"+newsObj.getId()).with(user(admin)))
                .andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains(newsObj.getTitle()));
    }

    @Test
    public void getNewsEditPageNonExisting() throws Exception {
        //when
        when(newsService.getNewsById(any(UUID.class))).thenThrow(new ResourceNotFoundException("Requested news does not exist!"));
        //then
        MvcResult result = mockMvc.perform(get("/management/edit/" + UUID.randomUUID()).with(user(admin)))
                .andExpect(status().isNotFound()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Requested news does not exist!"));
    }

    @Test
    public void postManagementEditNews() throws Exception {
        //when-then
        MvcResult result = mockMvc.perform(post("/management/edit/")
                        .param("articleId", UUID.randomUUID().toString())
                        .param("title", "SomeTitle")
                        .param("publishedAt", "2023-02-25T20:00")
                        .param("description", "SomeDescription")
                        .param("content", "SomeContent")
                        .with(user(admin)).with(csrf()))
                .andExpect(redirectedUrl("/management")).andReturn();
    }

    @Test
    public void postManagementEditNewsInvalidTitle() throws Exception {
        //when-then
        MvcResult result = mockMvc.perform(post("/management/edit/")
                        .param("articleId", UUID.randomUUID().toString())
                        .param("title", "Som")
                        .param("publishedAt", "2023-02-25T20:00")
                        .param("description", "SomeDescription")
                        .param("content", "SomeContent")
                        .with(user(admin)).with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Title length must be between 5 and 100 characters"));
    }

    @Test
    public void postManagementEditNewsInvalidDate() throws Exception {
        //when-then
        MvcResult result = mockMvc.perform(post("/management/edit/")
                        .param("articleId", UUID.randomUUID().toString())
                        .param("title", "SomeTitle")
                        .param("publishedAt", "2023/02/25T20:00")
                        .param("description", "SomeDescription")
                        .param("content", "SomeContent")
                        .with(user(admin)).with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Please use specified datetime format!"));
    }

    @Test
    public void postManagementEditNewsInvalidDescription() throws Exception {
        //when-then
        MvcResult result = mockMvc.perform(post("/management/edit/")
                        .param("articleId", UUID.randomUUID().toString())
                        .param("title", "Som")
                        .param("publishedAt", "2023-02-25T20:00")
                        .param("description", "")
                        .param("content", "SomeContent")
                        .with(user(admin)).with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Description length must be between 5 and 500 characters"));
    }

    @Test
    public void postManagementEditNewsInvalidContent() throws Exception {
        //when-then
        MvcResult result = mockMvc.perform(post("/management/edit/")
                        .param("articleId", UUID.randomUUID().toString())
                        .param("title", "SomeTitle")
                        .param("publishedAt", "2023-02-25T20:00")
                        .param("description", "SomeDescription")
                        .param("content", "")
                        .with(user(admin)).with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Content length must be between 5 and 2048 characters"));
    }

    @Test
    public void postManagementEditNonExistingNews() throws Exception {
        //when-then
        when(newsService.updateNews(any(NewsDto.class))).thenThrow(new ResourceNotFoundException("Requested news does not exist!"));
        MvcResult result = mockMvc.perform(post("/management/edit/")
                        .param("articleId", UUID.randomUUID().toString())
                        .param("title", "SomeTitle")
                        .param("publishedAt", "2023-02-25T20:00")
                        .param("description", "SomeDescription")
                        .param("content", "SomeContent")
                        .with(user(admin)).with(csrf()))
                .andExpect(status().isNotFound()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Requested news does not exist!"));
    }

    @Test
    public void postManagementDeleteNewsList() throws Exception {
        //when-then
        MvcResult result = mockMvc.perform(post("/management/delete/")
                        .param("toDelete", UUID.randomUUID().toString())
                        .param("toDelete", UUID.randomUUID().toString())
                        .param("toDelete", UUID.randomUUID().toString())
                        .param("toDelete", UUID.randomUUID().toString())
                        .with(user(admin)).with(csrf()))
                .andExpect(redirectedUrl("/management")).andReturn();
        assertNull(result.getFlashMap().get("error"));
    }

    @Test
    public void postManagementDeleteNewsListWithEmptyList() throws Exception {
        //when-then
        MvcResult result = mockMvc.perform(post("/management/delete/")
                        .with(user(admin)).with(csrf()))
                .andExpect(redirectedUrl("/management")).andReturn();
        assertTrue(result.getFlashMap().get("error").toString().contains("must not be empty"));
    }

    @Test
    public void postManagementDeleteSingleNews() throws Exception {
        //when-then
        MvcResult result = mockMvc.perform(post("/management/delete/"+UUID.randomUUID())
                        .with(user(admin)).with(csrf()))
                .andExpect(redirectedUrl("/management")).andReturn();
    }

}
