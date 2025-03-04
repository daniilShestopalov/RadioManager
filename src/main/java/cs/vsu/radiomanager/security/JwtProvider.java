package cs.vsu.radiomanager.security;

import cs.vsu.radiomanager.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtProvider.class);

    private final SecretKey jwtAccessSecret;

    public JwtProvider(@Value("${jwt.secret}") String jwtAccessSecret) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
    }

    public String generateToken(@NonNull UserDto user) {
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .signWith(jwtAccessSecret)
                .claim("role", user.getRole().name())
                .compact();
    }

    /*public String generatePasswordResetToken(@NonNull Integer userId) {
        Instant now = Instant.now();
        Instant expiryDate = now.plus(1, ChronoUnit.HOURS); // 1 hour

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(jwtAccessSecret, Jwts.SIG.HS256)
                .compact();
    }*/

    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    private boolean validateToken(@NonNull String token, @NonNull SecretKey secret) {
        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (UnsupportedJwtException unsEx) {
            LOGGER.error("Unsupported JWT token: {}", unsEx.getMessage());
        } catch (MalformedJwtException mjEx) {
            LOGGER.error("Malformed JWT token: {}", mjEx.getMessage());
        } catch (Exception e) {
            LOGGER.error("Invalid token: {}", e.getMessage());
        }
        return false;
    }

    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token);
    }

    private Claims getClaims(@NonNull String token) {
        return Jwts.parser()
                .verifyWith(jwtAccessSecret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserIdFromToken(@NonNull String token) {
        Claims claims = getClaims(token);
        return Long.parseLong(claims.getSubject());
    }
}
