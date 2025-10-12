package io.github.marinossake.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
public class JwtUtil {

    // HMAC signing key derived from Base64 secret
    private final SecretKey key;

    // Access token lifetime in milliseconds
    private final long expirationMillis;

    public JwtUtil(@Value("${jwt.secret}")String secret,
                   @Value("${jwt.expirationMillis}")long expirationMillis) {

        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(String publicId, String username, Collection<String> roles) {

        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMillis);

        // Build compact JWS: subject + minimal custom claims + exp/iat
        return Jwts.builder()
                .setSubject(publicId)
                .claim("username", username)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    /**
     * Parses and validates a token and returns all claims if signature/exp are valid.
     * Throws on tampering or expiration.
     */
    private Claims parseAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Optional<Claims> tryParse(String token) {
        try {
            return Optional.of(parseAllClaims(token));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

}
