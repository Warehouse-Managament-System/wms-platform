package com.wms.gateway.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtService jwtService;

  @Value("${wms.cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
  private List<String> allowedOrigins;

  public SecurityConfig(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/actuator/health", "/actuator/info", "/actuator/prometheus")
                    .permitAll()
                    .requestMatchers("/identity/api/v1/auth/**")
                    .permitAll()
                    .requestMatchers("/reservation/api/v1/webhooks/**")
                    .permitAll()
                    .requestMatchers("/inventory/api/v1/warehouses/**")
                    .permitAll()
                    .requestMatchers("/inventory/api/v1/zones/*/rooms/**")
                    .permitAll()
                    .requestMatchers("/inventory/api/v1/categories/**")
                    .permitAll()
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/**/swagger-ui/**",
                        "/**/v3/api-docs/**")
                    .permitAll()
                    .requestMatchers("/**/api/v1/internal/**")
                    .denyAll()
                    .requestMatchers("/identity/api/v1/users/*/status")
                    .hasRole("SUPER_ADMIN")
                    .requestMatchers("/identity/api/v1/users/*/delete")
                    .hasRole("SUPER_ADMIN")
                    .requestMatchers("/identity/api/v1/warehouse-owners/*/approve")
                    .hasRole("SUPER_ADMIN")
                    .requestMatchers("/identity/api/v1/warehouse-owners/*/reject")
                    .hasRole("SUPER_ADMIN")
                    .requestMatchers("/**/api/v1/owner/**")
                    .hasAnyRole("WAREHOUSE_OWNER", "SUPER_ADMIN")
                    .requestMatchers("/**/api/v1/customer/**")
                    .hasAnyRole("CUSTOMER", "SUPER_ADMIN")
                    .requestMatchers("/**/api/v1/staff/**")
                    .hasAnyRole("STAFF", "WAREHOUSE_OWNER", "SUPER_ADMIN")
                    .requestMatchers("/**/api/v1/agent/**")
                    .hasAnyRole("DELIVERY_AGENT", "SUPER_ADMIN")
                    .requestMatchers("/delivery/api/v1/delivery/requests/**")
                    .hasAnyRole("CUSTOMER", "SUPER_ADMIN")
                    .anyRequest()
                    .authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(
            new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(allowedOrigins);
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setExposedHeaders(List.of("Authorization"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
