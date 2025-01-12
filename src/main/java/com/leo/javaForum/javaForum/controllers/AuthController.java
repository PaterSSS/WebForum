package com.leo.javaForum.javaForum.controllers;


import com.leo.javaForum.javaForum.context.Context;
import com.leo.javaForum.javaForum.models.responseModel.ResponseStatus;
import com.leo.javaForum.javaForum.services.AuthentificationService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    private final AuthentificationService authentificationService;

    @Autowired
    public AuthController(Context context) {
        authentificationService = context.getBean(AuthentificationService.class);
    }

    @GetMapping("/auth")
    public String auth(@RequestParam(defaultValue = "login") String type, Model model) {
        model.addAttribute("type", type);
        return "authenticate";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/auth/register")
    public String auth(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        var resp = authentificationService.register(username, password);
        if (resp.getStatusCode() == ResponseStatus.OK) {
            session.setAttribute("username", username);
            var userId = authentificationService.findByUsername(username);
            session.setAttribute("userId", userId.getData().id());


            return "redirect:/";
        } else if (resp.getStatusCode() == ResponseStatus.CONFLICT) {
            model.addAttribute("error", username + " is already taken");
            model.addAttribute("type", "register");
            return "authenticate";
        } else {
            model.addAttribute("type", "register");
            model.addAttribute("error", resp.getMessage());
            return "authenticate";
        }
    }

    @PostMapping("/auth/login")
    public String auth(@RequestParam String username, @RequestParam String password, HttpServletRequest session, Model model) {
        var resp = authentificationService.login(username, password);
        if (resp.getStatusCode() == ResponseStatus.OK) {
            session.getSession().setAttribute("username", username);
            session.getSession().setAttribute("userId", resp.getData().id());

            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/";
        }
        model.addAttribute("error", resp.getMessage());
        model.addAttribute("type", "login");
        return "authenticate";
    }
}

