package com.seitov.news.controller;

import com.seitov.news.dto.NewsDto;
import com.seitov.news.service.NewsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping(path = "/")
    public String home(Principal principal, Model model, @RequestParam(required = false) Integer page) {
        page = page == null || page<0 ? 0 : page;
        Pageable pageable = PageRequest.of(page, 10);
        List<NewsDto> news = newsService.getNews(pageable);
        model.addAttribute("page", page);
        model.addAttribute("news", news);
        return "index";
    }

    @GetMapping(path = "/news/{id}")
    public String getNewsById(Principal principal, Model model, @PathVariable UUID id) {
        NewsDto newsDto = newsService.getNewsById(id);
        model.addAttribute("article", newsDto);
        return "news";
    }


}
