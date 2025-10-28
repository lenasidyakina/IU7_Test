package ru.bmstu.iu7.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY = "mySuperSecretKey12345mySuperSecretKey12345"; // длина >= 256 бит
    private static final long EXPIRATION_MS = 1000 * 60 * 60; // 1 час

    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // 🔹 Генерация токена
    public static String generateToken(String username, Long userId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("id", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // 🔹 Извлекаем ID пользователя из токена
    public static Long extractUserId(String token) {
        Claims claims = getClaims(token);
        Object id = claims.get("id");
        if (id instanceof Integer) return ((Integer) id).longValue();
        if (id instanceof Long) return (Long) id;
        return Long.parseLong(id.toString());
    }

    // 🔹 Извлекаем имя пользователя
    public static String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // 🔹 Проверка срока действия
    public static boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    // 🔹 Парсим тело токена
    private static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
