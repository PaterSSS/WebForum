package com.leo.javaForum.javaForum.controllers;

import com.leo.javaForum.javaForum.context.Context;
import com.leo.javaForum.javaForum.models.DTOs.CommentDTO;
import com.leo.javaForum.javaForum.models.DTOs.ReactionDTO;
import com.leo.javaForum.javaForum.models.DTOs.types.ReactionType;
import com.leo.javaForum.javaForum.services.CommentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(Context context) {
        this.commentService = context.getBean(CommentService.class);
    }

    @PostMapping("/comment/add")
    public String addComment(@RequestParam String content, @RequestParam Long postId,
                             RedirectAttributes redirectAttributes, HttpSession session) {
        CommentDTO commentDTO = new CommentDTO(null, content, null, postId
                , (Long) session.getAttribute("userId"));
        commentService.addNewComment(commentDTO);
        redirectAttributes.addAttribute("postId", postId);
        return "redirect:/post";
    }

    @GetMapping("/comment/delete")
    public String deleteComment(@RequestParam Long commentId, @RequestParam Long postId, RedirectAttributes redirectAttributes) {
        var response = commentService.deleteComment(new CommentDTO(commentId,
                null, null, null, null));
        redirectAttributes.addAttribute("postId", postId);
        return "redirect:/post";
    }

    @PostMapping("/comment/update")
    public String updateComment(@RequestParam Long commentId, @RequestParam String content, @RequestParam Long postId,
                                RedirectAttributes redirectAttributes) {
        var response = commentService.updateComment(new CommentDTO(commentId,content, null, null, null));
        redirectAttributes.addAttribute("postId", postId);
        return "redirect:/post";
    }

    @GetMapping("/comment/like")
    public String likeComment(@RequestParam Long commentId, @RequestParam Long postId,
                              RedirectAttributes redirectAttributes,HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        commentService.reactOnComment(new ReactionDTO(userId,commentId, ReactionType.LIKE));
        redirectAttributes.addAttribute("postId", postId);
        return "redirect:/post";
    }

    @GetMapping("/comment/dislike")
    public String dislikeComment(@RequestParam Long commentId, @RequestParam Long postId,
                                 RedirectAttributes redirectAttributes,HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        commentService.reactOnComment(new ReactionDTO(userId, commentId, ReactionType.DISLIKE));
        redirectAttributes.addAttribute("postId", postId);
        return "redirect:/post";
    }
}
