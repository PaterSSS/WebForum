package com.leo.javaForum.javaForum.controllers.configuration;

import com.leo.javaForum.javaForum.context.Context;
import com.leo.javaForum.javaForum.models.responseModel.ResponseStatus;
import com.leo.javaForum.javaForum.services.AuthentificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthentificationService authentificationService;

    @Autowired
    public CustomUserDetailsService(Context context) {
        this.authentificationService = context.getBean(AuthentificationService.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var resp = authentificationService.findByUsername(username);
        if (resp.getStatusCode() == ResponseStatus.NotFound) {
            throw new UsernameNotFoundException("User not found");
        }
        return User.withUsername(username).password(resp.getData().password()).build();
    }
}
