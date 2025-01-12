package com.leo.javaForum.javaForum.controllers;

import com.leo.javaForum.javaForum.context.Context;
import com.leo.javaForum.javaForum.models.DTOs.CategoryDTO;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.models.responseModel.ResponseStatus;
import com.leo.javaForum.javaForum.services.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainPageController {
    private final CategoryService categoryService;

    @Autowired
    public MainPageController(Context context) {
        categoryService = context.getBean(CategoryService.class);
    }

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null || username.isEmpty()) {
            model.addAttribute("isLogged", false);
        } else {
            model.addAttribute("isLogged", true);
            model.addAttribute("username", username);
        }

        Response<List<CategoryDTO>> categories = categoryService.allCategories();
        if (categories.getStatusCode() != ResponseStatus.OK) {
            model.addAttribute("error", categories.getMessage());
        } else {
            model.addAttribute("categories", categories.getData());
        }
        return "index";
    }
}
