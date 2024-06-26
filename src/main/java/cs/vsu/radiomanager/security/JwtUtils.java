package cs.vsu.radiomanager.security;

import cs.vsu.radiomanager.model.auth.AuthModel;
import cs.vsu.radiomanager.model.enumerate.Role;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    public static AuthModel generate(Claims claims) {
        final AuthModel jwtInfoToken = new AuthModel();
        jwtInfoToken.setRole(Role.valueOf(claims.get("role", String.class)));
        jwtInfoToken.setLogin(claims.getSubject());
        jwtInfoToken.setName(claims.get("name", String.class));
        return jwtInfoToken;
    }
}
