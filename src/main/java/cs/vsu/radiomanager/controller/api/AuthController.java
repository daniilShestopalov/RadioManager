package cs.vsu.radiomanager.controller.api;

import cs.vsu.radiomanager.dto.UserDto;
import cs.vsu.radiomanager.dto.auth.AuthUserDto;
import cs.vsu.radiomanager.dto.auth.CodeDto;
import cs.vsu.radiomanager.dto.auth.PasswordChangeDto;
import cs.vsu.radiomanager.dto.auth.PasswordResetRequestDto;
import cs.vsu.radiomanager.model.enumerate.Role;
import cs.vsu.radiomanager.security.JwtFilter;
import cs.vsu.radiomanager.security.JwtProvider;
import cs.vsu.radiomanager.service.AuthService;
import cs.vsu.radiomanager.service.ResetService;
import cs.vsu.radiomanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private static final String JWT_COOKIE_NAME = "jwt";

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private AuthService authService;

    private UserService userService;

    private JwtProvider jwtProvider;

    private JwtFilter jwtFilter;

    private ResetService resetService;

    @PostMapping
    @Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT token in the response header")
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthUserDto authUserDto, HttpServletResponse response) {
        try {
            LOGGER.info("Attempting to authenticate user: {}", authUserDto.getLogin());
            var userDto = authService.authenticate(authUserDto);
            if (userDto != null) {
                String token = jwtProvider.generateToken(userDto);

                Cookie jwtCookie = new Cookie(JWT_COOKIE_NAME, token);
                jwtCookie.setPath("/");
                jwtCookie.setHttpOnly(true);
                jwtCookie.setSecure(false);
                jwtCookie.setMaxAge(24 * 60 * 60);

                response.addCookie(jwtCookie);

                return ResponseEntity.ok().body(userDto);

            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login or password");
        } catch (Exception e) {
            LOGGER.error("Error during authentication", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during authentication");
        }
    }

    @PostMapping("/send-password-reset-code")
    @Operation(
            summary = "Send password reset code",
            description = "Sends a password reset code to the specified email address for resetting the password."
    )
    public ResponseEntity<?> sendPasswordResetCode(@RequestBody @Valid PasswordResetRequestDto passwordResetRequestDto) {
        try {
            LOGGER.info("Attempting to send password reset code to email: {}", passwordResetRequestDto.getEmail());
            resetService.sendPasswordReset(passwordResetRequestDto.getEmail());
            LOGGER.info("Password reset code sent successfully to email: {}", passwordResetRequestDto.getEmail());
            return ResponseEntity.ok("Password reset code sent successfully.");
        } catch (RuntimeException e) {
            LOGGER.error("Error sending password reset code to email: {}", passwordResetRequestDto.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send password reset code.");
        }
    }

    @PostMapping("/code-verification")
    @Operation(
            summary = "Verify code",
            description = "Validates the verification code by extracting the user ID from the token. Returns a success message if valid, or an error otherwise."
    )
    public ResponseEntity<?> codeVerification(@RequestBody CodeDto codeDto) {
        try {
            LOGGER.info("Starting verification for code: {}", codeDto.getCode());
            Long userId = jwtProvider.getUserIdFromToken(codeDto.getCode());
            if (userId != null) {
                LOGGER.info("Verification successful for code: {}. Extracted userId: {}", codeDto.getCode(), userId);
                return ResponseEntity.ok("Verification code is valid.");
            }

            LOGGER.warn("Verification failed: invalid code provided: {}", codeDto.getCode());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification code.");
        } catch (RuntimeException e) {
            LOGGER.error("Error validating verification code: {}", codeDto.getCode(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to validate verification code.");
        }
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset Password",
            description = "Resets the user's password using the provided verification token and new password."
    )
    public ResponseEntity<?> resetPassword(@RequestBody @Valid PasswordChangeDto passwordChangeDto) {
        String token = passwordChangeDto.getVerificationCode();
        LOGGER.info("Received password reset request with token: {}", token);

        try {
            Long userId = jwtProvider.getUserIdFromToken(token);
            LOGGER.info("Extracted user ID: {} from token", userId);

            boolean isUpdated = authService.updatePasswordById(userId, passwordChangeDto.getNewPassword());
            if (isUpdated) {
                LOGGER.info("Password successfully reset for user ID: {}", userId);
                return ResponseEntity.ok().build();
            }

            LOGGER.error("Failed to reset password for user ID: {}", userId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reset password.");
        } catch (Exception e) {
            LOGGER.error("Exception occurred while resetting password", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reset password.");
        }
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register new user",
            description = "Registers a new user. Registration with ADMIN role is forbidden."
    )
    public ResponseEntity<?> register(@RequestBody @Valid UserDto userDto) {
        LOGGER.info("Attempting to register user with login: {}", userDto.getLogin());

        if (userDto.getRole() == Role.ADMIN) {
            LOGGER.warn("Registration attempt with ADMIN role is forbidden for login: {}", userDto.getLogin());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Registration with admin role is not allowed.");
        }

        try {

            if (authService.checkEmailExists(userDto.getLogin())) {
                LOGGER.warn("User already exists with login: {}", userDto.getLogin());
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");
            }

            boolean isRegistered = authService.registerUser(userDto);
            if (isRegistered) {
                LOGGER.info("User registered successfully: {}", userDto.getLogin());
                return ResponseEntity.ok("User registered successfully.");
            }

            LOGGER.error("Failed to register user: {}", userDto.getLogin());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed.");
        } catch (Exception e) {
            LOGGER.error("Exception occurred during registration for user: {}", userDto.getLogin(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration error.");
        }
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout user",
            description = "Clears JWT cookie and logs out the user"
    )
    public ResponseEntity<?> logout(HttpServletResponse response, HttpServletRequest request) {
        try {
            Long userId = jwtFilter.getUserId(request);

            if (userId != null) {
                LOGGER.info("Logging out user: {}", userId);
                SecurityContextHolder.clearContext();

                Cookie jwtCookie = new Cookie(JWT_COOKIE_NAME, null);
                jwtCookie.setPath("/");
                jwtCookie.setHttpOnly(true);
                jwtCookie.setSecure(false);
                jwtCookie.setMaxAge(0);
                response.addCookie(jwtCookie);

                LOGGER.info("Logged out user: {}", userId);
                return ResponseEntity.ok("Logged out successfully.");
            }

            LOGGER.warn("Logging out failed: user is not authenticated.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Logging out failed.");
        } catch (Exception e) {
            LOGGER.error("Exception occurred during logout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception occurred during logout");
        }

    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get current user info",
            description = "Retrieves the currently authenticated user's information based on the JWT token stored in the HTTP-only cookie."
    )
    public ResponseEntity<UserDto> getCurrentUser(HttpServletRequest request) {
        try {
            LOGGER.info("Fetching current user from JWT cookie");
            Long userId = jwtFilter.getUserId(request);
            if (userId == null) {
                LOGGER.warn("No user ID found in JWT token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            UserDto userDto = userService.getUserById(userId);
            if (userDto == null) {
                LOGGER.warn("User not found with ID: {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            LOGGER.info("Fetched current user: {}", userDto);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            LOGGER.error("Error fetching current user info", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}