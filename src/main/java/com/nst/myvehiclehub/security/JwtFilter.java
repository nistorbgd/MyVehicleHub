package com.nst.myvehiclehub.security;

import com.nst.myvehiclehub.serviceImpl.JWTServiceImpl;
import com.nst.myvehiclehub.serviceImpl.MyUserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

  private final JWTServiceImpl jwtServiceImpl;
  private final ApplicationContext context;

  public JwtFilter(JWTServiceImpl jwtServiceImpl, ApplicationContext context) {
    this.jwtServiceImpl = jwtServiceImpl;
    this.context = context;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");
    String token = null;
    String username = null;

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      token = authHeader.substring(7);
      try {
        username = jwtServiceImpl.extractUserName(token);
      } catch (ExpiredJwtException e) {
        sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT token has expired");
        return;
      } catch (JwtException e) {
        sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
        return;
      }
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails =
          context.getBean(MyUserDetailsServiceImpl.class).loadUserByUsername(username);

      if (jwtServiceImpl.validateToken(token, userDetails)) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request, response);
  }

  private void sendErrorResponse(HttpServletResponse response, int status, String message)
      throws IOException {
    response.setContentType("application/json");
    response.setStatus(status);
    response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + message + "\"}");
  }
}
