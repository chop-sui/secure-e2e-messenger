package org.sec.secureapp.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthSuccessHandler authSuccessHandler;
    private final AuthFailureHandler authFailureHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeHttpRequests(authorize -> {
                try {
                    authorize
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                        .and()
//                        .csrf().ignoringRequestMatchers(
//                            new AntPathRequestMatcher("/h2-console/**")
//                        )
//                        .and()
                        .headers()
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
                        .and()
                        .formLogin().loginPage("/user/login").permitAll()
                        .loginProcessingUrl("/login/action").permitAll()
                        .successHandler(authSuccessHandler)
                        .failureHandler(authFailureHandler)
                        .and()
                        .sessionManagement()
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl("/login?error=true&exception=Have attempted to login from a new place or session has expired");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        );

        return http.build();
    }

}
