package me.dev.onlinestoreapi.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dev.onlinestoreapi.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.secret-key}")
    private String secretKey;

    public String generateToken(User user) {
        try {
            String token = Jwts.builder()
                    .issuer("tinnguyen.dev")
                    .claim("phoneNumber", user.getPhoneNumber())
                    .claim("userId", user.getId())
                    .subject(user.getPhoneNumber())
                    .issuedAt(new Date())
                    .expiration(new Date(
                            Instant.now().plus(expiration, ChronoUnit.SECONDS).toEpochMilli()
                    ))
                    .signWith(getSignKey(), Jwts.SIG.HS256)
                    .compact();
            return token;
        } catch (Exception e) {
            log.error("Cannot create jwt, error: {}", e.getMessage());
            return null;
        }
    }

    private SecretKey getSignKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    public String extractPhoneNumber(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String phoneNumber = extractPhoneNumber(token);
        return phoneNumber.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
