package org.dspace.app.rest.diracai.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableAspectJAutoProxy
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF enabled by default
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/transcription/upload")
                )
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/transcription/upload").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
