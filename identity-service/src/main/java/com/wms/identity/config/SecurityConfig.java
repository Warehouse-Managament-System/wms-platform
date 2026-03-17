package com.wms.identity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth ->
                auth
                    // Public endpoints
                    .requestMatchers("/api/v1/auth/**")
                    .permitAll()
                    .requestMatchers("/actuator/health", "/actuator/info")
                    .permitAll()

                    // Super admin only
                    .requestMatchers("/api/v1/warehouse-owners/*/approve")
                    .hasRole("SUPER_ADMIN")
                    .requestMatchers("/api/v1/warehouse-owners/*/reject")
                    .hasRole("SUPER_ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**")
                    .hasRole("SUPER_ADMIN")

                    // Admin and warehouse owners can manage staff/delivery agents
                    .requestMatchers(HttpMethod.POST, "/api/v1/staff")
                    .hasAnyRole("SUPER_ADMIN", "WAREHOUSE_OWNER")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/staff/**")
                    .hasAnyRole("SUPER_ADMIN", "WAREHOUSE_OWNER")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/staff/**")
                    .hasAnyRole("SUPER_ADMIN", "WAREHOUSE_OWNER")
                    .requestMatchers(HttpMethod.POST, "/api/v1/delivery-agents")
                    .hasAnyRole("SUPER_ADMIN", "WAREHOUSE_OWNER")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/delivery-agents/**")
                    .hasAnyRole("SUPER_ADMIN", "WAREHOUSE_OWNER")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/delivery-agents/**")
                    .hasAnyRole("SUPER_ADMIN", "WAREHOUSE_OWNER")

                    // Admin-only warehouse owner management
                    .requestMatchers(HttpMethod.POST, "/api/v1/warehouse-owners")
                    .hasRole("SUPER_ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/warehouse-owners/**")
                    .hasRole("SUPER_ADMIN")

                    // Everything else
                    .anyRequest()
                    .authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
