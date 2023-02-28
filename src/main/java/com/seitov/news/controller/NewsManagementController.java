package com.seitov.news.controller;

import com.seitov.news.dto.NewsDto;
import com.seitov.news.dto.ToDeleteForm;
import com.seitov.news.service.NewsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
public class NewsManagementController {

    private final NewsService newsService;

    public NewsManagementController(NewsService newsService) {
        this.newsService = newsService;
    }


    @GetMapping(path = "/management")
    public String newsManagement(Principal principal, Model model, @RequestParam(required = false) Integer page) {
        page = page == null || page<0 ? 0 : page;
        Pageable pageable = PageRequest.of(page, 10);
        List<NewsDto> news = newsService.getNews(pageable);
        ToDeleteForm toDeleteForm = new ToDeleteForm();
        model.addAttribute("toDeleteForm", toDeleteForm);
        model.addAttribute("page", page);
        model.addAttribute("news", news);
        return "management";
    }

    @GetMapping(path = "/management/{id}")
    public String getManagementNewsById(Principal principal, Model model, @PathVariable UUID id) {
        NewsDto newsDto = newsService.getNewsById(id);
        model.addAttribute("article", newsDto);
        return "newsView";
    }

    @GetMapping(path = "/management/add")
    public String newsAdd(Model model) {
        NewsDto newsDto = new NewsDto();
        model.addAttribute("newsDto", newsDto);
        return "newsAdd";
    }

    @PostMapping(path = "/management/add")
    public String submitNewNews(@ModelAttribute("newsDto") @Valid NewsDto newsDto, BindingResult result,
                                HttpServletRequest request, HttpServletResponse response) {
        if(result.hasErrors()) {
            response.setStatus(400);
            return "newsAdd";
        } else {
            newsService.addNews(newsDto);
            return "redirect:/management";
        }
    }

    @GetMapping(path = "/management/edit/{id}")
    public String newsEdit(Model model, @PathVariable UUID id) {
        NewsDto newsDto = newsService.getNewsById(id);
        model.addAttribute("newsDto", newsDto);
        return "newsEdit";
    }

    @PostMapping(path = "/management/edit")
    public String submitEditNews(@ModelAttribute("newsDto") @Valid NewsDto newsDto, BindingResult result,
                                 Model model, HttpServletRequest request, HttpServletResponse response) {
        if(result.hasErrors()) {
            response.setStatus(400);
            return "newsAdd";
        }
        newsService.updateNews(newsDto);
        return "redirect:/management";
    }

    @PostMapping(path = "/management/delete")
    public String deleteNews(@ModelAttribute("toDeleteForm") @Valid ToDeleteForm toDelete, BindingResult result,
                             Model model, HttpServletRequest request, HttpServletResponse response,
                             RedirectAttributes redirectAttributes) {
        if(result.hasErrors()) {
            response.setStatus(400);
            redirectAttributes.addFlashAttribute("error", result.getFieldError("toDelete"));
            return "redirect:/management";
        }
        newsService.deleteAllById(toDelete.getToDelete());
        return "redirect:/management";
    }

    @PostMapping(path = "/management/delete/{id}")
    public String deleteNews(@PathVariable UUID id, Model model, HttpServletResponse response) {
        newsService.deleteById(id);
        return "redirect:/management";
    }

}
