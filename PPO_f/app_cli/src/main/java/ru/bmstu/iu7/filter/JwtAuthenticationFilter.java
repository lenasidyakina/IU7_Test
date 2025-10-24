package ru.bmstu.iu7.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.bmstu.iu7.API.model.AUser;
import ru.bmstu.iu7.LabService;
import ru.bmstu.iu7.util.JwtUtil;

import java.io.IOException;

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
                    String username = JwtUtil.extractUsername(token);

                    // Находим пользователя в БД
                    AUser user = LabService.mainManager.getM_user_manager().findById(userId);
                    LabService.currentUser = user; // <-- текущий пользователь
                }
            }
        } catch (Exception e) {
            LabService.currentUser = null;
        }

        filterChain.doFilter(request, response);
    }
}
