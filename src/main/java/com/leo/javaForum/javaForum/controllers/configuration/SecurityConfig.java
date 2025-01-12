package com.leo.javaForum.javaForum.controllers.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final SessionAuthenticationFilter sessionAuthenticationFilter;

    @Autowired
    public SecurityConfig(SessionAuthenticationFilter sessionAuthenticationFilter) {
        this.sessionAuthenticationFilter = sessionAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/styles/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/img/**").permitAll()
                        .requestMatchers("/category").permitAll()
                        .requestMatchers("/post").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/post/**").authenticated()
                        .requestMatchers("/comment/**").authenticated()
                        .requestMatchers("/profile").authenticated()
                        .requestMatchers("/profile/update").authenticated()
                        .requestMatchers("/logout").authenticated()
                        .anyRequest().denyAll()
                ).exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    if (request.getRequestURI().equals("/auth/login") && request.getSession().getAttribute("SPRING_SECURITY_CONTEXT") != null) {
                        response.sendRedirect("/");
                    } else {
                        response.sendRedirect("/auth");
                    }
                })
                .and()
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                )
                .addFilterBefore(sessionAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Добавляем фильтр

        return http.build();
    }
}

