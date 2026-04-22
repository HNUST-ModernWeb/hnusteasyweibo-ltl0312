package cn.edu.hnust.hnusteasyweibo.config;

import cn.edu.hnust.hnusteasyweibo.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/users/login", "/api/v1/users/register").permitAll()
                        .requestMatchers("/api/v1/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/category/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/user/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/*/liked").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/*/like-count").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/*/comment-count").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/follows/*/stats").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/files/download/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/files/upload").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/files/avatar").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/posts").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/posts/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/posts/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/posts/*/like").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/posts/*/unlike").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/posts/*/comments").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/comments/**").authenticated()
                        .requestMatchers("/api/v1/follows/**").authenticated()
                        .requestMatchers("/api/v1/users/profile").authenticated()
                        .requestMatchers("/api/v1/users/statistics").authenticated()
                        .requestMatchers("/api/v1/users/logout").authenticated()
                        .requestMatchers("/api/v1/posts/user").authenticated()
                        .requestMatchers("/api/v1/posts/*/editable").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
