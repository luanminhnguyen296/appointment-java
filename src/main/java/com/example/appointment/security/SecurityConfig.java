package com.example.appointment.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return builder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF since we use JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**", "/webjars/**",
                                "/uploads/**", "/error")
                        .permitAll()
                        .requestMatchers("/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/hospitals/**", "/specialties/**", "/doctors/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SUB_ADMIN")
                        .requestMatchers("/patients/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUB_ADMIN", "ROLE_USER")
                        .requestMatchers("/appointments/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SUB_ADMIN", "ROLE_USER", "ROLE_DOCTOR")
                        .requestMatchers("/api/v1/**").authenticated() // Allow API access for data binding in forms
                        .anyRequest().authenticated())
                .logout(logout -> logout
                        .logoutRequestMatcher(new org.springframework.security.web.util.matcher.AntPathRequestMatcher(
                                "/logout", "GET"))
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("apo_token")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .permitAll())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Redirect to login page if unauthenticated access is detected for HTML pages
                            // Or return 401 for API calls based on Accept header
                            String acceptHeader = request.getHeader("Accept");
                            if (acceptHeader != null && acceptHeader.contains("text/html")) {
                                response.sendRedirect("/login");
                            } else {
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                            }
                        }))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
