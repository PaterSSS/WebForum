package com.leo.javaForum.javaForum.controllers;

import com.leo.javaForum.javaForum.context.Context;
import com.leo.javaForum.javaForum.models.DTOs.UserProfileDTO;
import com.leo.javaForum.javaForum.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {
    private final UserService userService;

    public ProfileController(Context context) {
        this.userService = context.getBean(UserService.class);
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        var userProfile = userService.getUserProfile(userId);
        var usersPosts = userService.getAllUserPosts(userId);
        model.addAttribute("userProfile", userProfile.getData());
        model.addAttribute("usersPosts", usersPosts.getData());
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String about, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        var resp = userService.updateUserBio(new UserProfileDTO(userId,null,null,about));
        return "redirect:/profile";
    }
}
