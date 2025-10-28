package ru.bmstu.iu7.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.bmstu.iu7.API.model.AUser;
import ru.bmstu.iu7.Main;
import ru.bmstu.iu7.Main;
import ru.bmstu.iu7.util.JwtUtil;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (!JwtUtil.isTokenExpired(token)) {
                    Long userId = JwtUtil.extractUserId(token);
                    AUser user = Main.userRepository.findById(userId);
                    if (user != null)
                        request.setAttribute("user", user);
                }
            }
        } catch (Exception ignored) { }

        filterChain.doFilter(request, response);
    }
}
