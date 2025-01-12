package com.leo.javaForum.javaForum.controllers;

import com.leo.javaForum.javaForum.context.Context;
import com.leo.javaForum.javaForum.models.DTOs.PostDTO;
import com.leo.javaForum.javaForum.models.DTOs.UserDTO;
import com.leo.javaForum.javaForum.models.responseModel.ResponseStatus;
import com.leo.javaForum.javaForum.services.CommentService;
import com.leo.javaForum.javaForum.services.PostService;
import com.leo.javaForum.javaForum.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PostController {
    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;

    @Autowired
    public PostController(Context context) {
        this.postService = context.getBean(PostService.class);
        this.commentService = context.getBean(CommentService.class);
        this.userService = context.getBean(UserService.class);
    }

    @GetMapping("/post")
    public String post(@RequestParam long postId, Model model) {
        var post = postService.getPost(postId);
        var commentAndReacts = commentService.getAllCommentsWithReactions(postId);
        var postCreator = userService.getById(new UserDTO(post.getData().authorId(), null, null));



        if (post.getStatusCode() != ResponseStatus.OK) {
            model.addAttribute("postError", post.getMessage());
        }
        if (commentAndReacts.getStatusCode() != ResponseStatus.OK && commentAndReacts.getStatusCode() != ResponseStatus.NotFound) {
            model.addAttribute("commentError", commentAndReacts.getMessage());
        }
        model.addAttribute("post", post.getData());
        model.addAttribute("wholeComment", commentAndReacts.getData());
        model.addAttribute("postCreator", postCreator.getData());
        return "post";
    }

    @PostMapping("/post/create")
    public String createPost(@RequestParam String title, @RequestParam String content, @RequestParam String categoryName,
                             HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        PostDTO postToCreate = new PostDTO(null, title, content, userId, null, categoryName);
        var response = postService.createPost(postToCreate);
        redirectAttributes.addAttribute("categoryName", categoryName);
        return "redirect:/category";

    }

    @GetMapping("/post/delete")
    public String deletePost(@RequestParam long postId,@RequestParam String categoryName, RedirectAttributes redirectAttributes) {
        postService.deletePost(postId);
        redirectAttributes.addAttribute("categoryName", categoryName);
        return "redirect:/category";
    }

    @PostMapping("/post/update")
    public String updatePost(@RequestParam long postId, @RequestParam String title,
                             @RequestParam String content, RedirectAttributes redirectAttributes) {
        postService.updatePost(new PostDTO(postId, title, content, null,null,null));
        redirectAttributes.addAttribute("postId", postId);
        return "redirect:/post";
    }
}
