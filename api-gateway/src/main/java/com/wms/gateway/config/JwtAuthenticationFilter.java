package com.wms.gateway.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Set<String> STRIPPED_HEADERS =
      Set.of("x-user-id", "x-user-role", "x-user-email");

  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    HttpServletRequest sanitized = new HeaderStrippingRequestWrapper(request);

    String authHeader = sanitized.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(sanitized, response);
      return;
    }

    String jwt = authHeader.substring(7);

    Claims claims;

    try {
      claims = jwtService.validateAndExtract(jwt);
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    if (jwtService.isExpired(claims)) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    String email = claims.getSubject();
    String userId = claims.get("userId", String.class);
    String role = claims.get("role", String.class);

    if (email == null || userId == null || role == null) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(
            email, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
    SecurityContextHolder.getContext().setAuthentication(authToken);

    HttpServletRequest wrappedRequest =
        new HeaderInjectingRequestWrapper(sanitized, userId, role, email);
    filterChain.doFilter(wrappedRequest, response);
  }

  private static class HeaderStrippingRequestWrapper extends HttpServletRequestWrapper {
    HeaderStrippingRequestWrapper(HttpServletRequest request) {
      super(request);
    }

    @Override
    public String getHeader(String name) {
      if (STRIPPED_HEADERS.contains(name.toLowerCase())) {
        return null;
      }
      return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
      if (STRIPPED_HEADERS.contains(name.toLowerCase())) {
        return Collections.emptyEnumeration();
      }
      return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
      List<String> names = new ArrayList<>();
      Enumeration<String> original = super.getHeaderNames();
      while (original.hasMoreElements()) {
        String name = original.nextElement();
        if (!STRIPPED_HEADERS.contains(name.toLowerCase())) {
          names.add(name);
        }
      }
      return Collections.enumeration(names);
    }
  }

  private static class HeaderInjectingRequestWrapper extends HttpServletRequestWrapper {
    private final Map<String, String> injectedHeaders =
        new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    HeaderInjectingRequestWrapper(
        HttpServletRequest request, String userId, String role, String email) {
      super(request);
      injectedHeaders.put("X-User-Id", userId);
      injectedHeaders.put("X-User-Role", role);
      injectedHeaders.put("X-User-Email", email);
    }

    @Override
    public String getHeader(String name) {
      String injected = injectedHeaders.get(name);
      return injected != null ? injected : super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
      String injected = injectedHeaders.get(name);
      if (injected != null) {
        return Collections.enumeration(List.of(injected));
      }
      return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
      Set<String> names = new LinkedHashSet<>();
      Enumeration<String> original = super.getHeaderNames();
      while (original.hasMoreElements()) {
        names.add(original.nextElement());
      }
      names.addAll(injectedHeaders.keySet());
      return Collections.enumeration(names);
    }
  }
}
