package com.wms.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class UserContextFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String userIdHeader = request.getHeader(SecurityHeaders.USER_ID);
      if (userIdHeader != null && !userIdHeader.isBlank()) {
        UUID userId = UUID.fromString(userIdHeader);
        String userRole = request.getHeader(SecurityHeaders.USER_ROLE);
        String userEmail = request.getHeader(SecurityHeaders.USER_EMAIL);

        UserContext userContext = new UserContext(userId, userRole, userEmail);
        UserContextHolder.set(userContext);
      }
      filterChain.doFilter(request, response);
    } finally {
      UserContextHolder.clear();
    }
  }
}
