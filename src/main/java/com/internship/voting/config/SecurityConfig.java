package com.internship.voting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.internship.voting.security.LoginFailureHandler;
import com.internship.voting.security.LoginSuccessHandler;
import com.internship.voting.security.RoleAuthenticationFilter;

import jakarta.servlet.DispatcherType;

/**
 * Spring Security 6 configuration.
 *
 * - BCrypt password hashing
 * - Role-based URL security
 * - Voter/Admin role selection during login
 * - Custom success/failure handlers
 * - CSRF protection
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            LoginSuccessHandler successHandler,
            LoginFailureHandler failureHandler,
            RoleAuthenticationFilter roleAuthenticationFilter) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth

                .dispatcherTypeMatchers(
                        DispatcherType.FORWARD,
                        DispatcherType.ERROR
                ).permitAll()

                .requestMatchers(
                        "/",
                        "/login",
                        "/register",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/error",
                        "/error/**"
                ).permitAll()

                .requestMatchers("/admin/**")
                .hasRole("ADMIN")

                .requestMatchers("/voter/**")
                .hasRole("VOTER")

                .anyRequest()
                .authenticated()
            )

            .addFilterBefore(
                    roleAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            )

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403")
            );

        return http.build();
    }
}