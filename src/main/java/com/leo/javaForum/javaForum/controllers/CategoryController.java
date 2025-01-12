package com.leo.javaForum.javaForum.controllers;

import com.leo.javaForum.javaForum.context.Context;
import com.leo.javaForum.javaForum.models.DTOs.CategoryDTO;
import com.leo.javaForum.javaForum.models.responseModel.Response;
import com.leo.javaForum.javaForum.models.responseModel.ResponseStatus;
import com.leo.javaForum.javaForum.services.CategoryService;
import com.leo.javaForum.javaForum.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CategoryController {
    private final CategoryService categoryService;
    private final PostService postService;

    @Autowired
    public CategoryController(Context context) {
        categoryService = context.getBean(CategoryService.class);
        postService = context.getBean(PostService.class);
    }

    @GetMapping("/category")
    public String category(@RequestParam String categoryName, @RequestParam(defaultValue = "newest") String sort
            , @RequestParam(defaultValue = "1") int pageNumber,
                           Model model) {
        Response<CategoryDTO> categoryInfo = categoryService.category(categoryName);
        if (categoryInfo.getStatusCode() != ResponseStatus.OK) {
            model.addAttribute("categoryError", categoryInfo.getMessage());
        }
        var postsOnPage = postService.getAllPostsOnPage(categoryName, pageNumber, sort);
        if (postsOnPage.getStatusCode() == ResponseStatus.InternalServerError) {
            model.addAttribute("postError", postsOnPage.getMessage());
        }
        if (postsOnPage.getStatusCode() == ResponseStatus.NotFound) {
            model.addAttribute("postNotFound", postsOnPage.getMessage());
        }
        var countOfPosts = postService.countPostsInCategory(categoryName);
        if (countOfPosts.getStatusCode() != ResponseStatus.OK) {
            model.addAttribute("postCountError", countOfPosts.getMessage());
        }
        var countOfPages = postService.countPagesInCategory(categoryName);
        if (countOfPages.getStatusCode() != ResponseStatus.OK) {
            model.addAttribute("pageCountError", countOfPages.getMessage());
        }

        model.addAttribute("category", categoryInfo.getData());
        model.addAttribute("posts", postsOnPage.getData());
        model.addAttribute("countPosts", countOfPosts.getData());
        model.addAttribute("countPages", countOfPages.getData());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("sort", sort);

        return "category";
    }
}
