package com.wms.reservation.config;

import com.wms.common.security.SecurityHeaders;
import com.wms.common.security.UserContextFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final UserContextFilter userContextFilter;

  public SecurityConfig(UserContextFilter userContextFilter) {
    this.userContextFilter = userContextFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/actuator/health", "/actuator/info", "/actuator/prometheus")
                    .permitAll()
                    .requestMatchers("/api/v1/webhooks/**")
                    .permitAll()
                    .requestMatchers("/api/v1/internal/**")
                    .permitAll()
                    .requestMatchers("/api/v1/customer/**")
                    .hasRole("CUSTOMER")
                    .requestMatchers("/api/v1/owner/**")
                    .hasAnyRole("WAREHOUSE_OWNER", "SUPER_ADMIN")
                    .anyRequest()
                    .authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(
            gatewayHeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(userContextFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public OncePerRequestFilter gatewayHeaderAuthenticationFilter() {
    return new OncePerRequestFilter() {
      @Override
      protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          jakarta.servlet.http.@NonNull HttpServletResponse response,
          jakarta.servlet.@NonNull FilterChain filterChain)
          throws jakarta.servlet.ServletException, java.io.IOException {

        String userId = request.getHeader(SecurityHeaders.USER_ID);
        String userRole = request.getHeader(SecurityHeaders.USER_ROLE);

        if (userId != null && !userId.isBlank()) {
          List<SimpleGrantedAuthority> authorities =
              userRole != null
                  ? List.of(new SimpleGrantedAuthority("ROLE_" + userRole))
                  : List.of();

          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(userId, null, authorities);

          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
      }
    };
  }
}
