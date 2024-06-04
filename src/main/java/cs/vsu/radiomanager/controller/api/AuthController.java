package cs.vsu.radiomanager.controller.api;

import cs.vsu.radiomanager.dto.UserDto;
import cs.vsu.radiomanager.dto.auth.AuthUserDto;
import cs.vsu.radiomanager.dto.auth.CheckEmailDto;
import cs.vsu.radiomanager.security.JwtProvider;
import cs.vsu.radiomanager.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthUserDto authUserDTO) {
        try {
            UserDto user = authService.authenticate(authUserDTO);
            if (user != null) {
                String token = jwtProvider.generateToken(user);

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + token);

                return ResponseEntity.ok().headers(headers).body(user);

            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid login or password");
            }
        } catch (Exception e) {
            LOGGER.error("Error during authentication", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during authentication");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserDto registrationUser) {
        try {
            if (authService.registerUser(registrationUser)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("User registration failed. Possible reasons: invalid data.");
            }
        } catch (Exception e) {
            LOGGER.error("Error during registration", e);
            return ResponseEntity.status((HttpStatus.INTERNAL_SERVER_ERROR))
                    .body("An error occurred during registration");
        }
    }

    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestBody @Valid CheckEmailDto check) {
        try {
            if (authService.checkEmailExists(check.getEmail())) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status((HttpStatus.NOT_FOUND))
                    .body("Email does not exist");
        } catch (Exception e) {
            LOGGER.error("Error during check email", e);
            return ResponseEntity.status((HttpStatus.INTERNAL_SERVER_ERROR))
                    .body("An error occurred during check email");
        }
    }

}
