package cs.vsu.radiomanager.controller.html;

import cs.vsu.radiomanager.dto.UserDto;
import cs.vsu.radiomanager.dto.auth.AuthUserDto;
import cs.vsu.radiomanager.dto.auth.PasswordResetDto;
import cs.vsu.radiomanager.model.enumerate.Role;
import cs.vsu.radiomanager.security.JwtProvider;
import cs.vsu.radiomanager.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@AllArgsConstructor
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    private AuthService authService;

    private JwtProvider jwtProvider;

    @GetMapping("/login")
    public String loginPage(Model model) {
        LOGGER.info("Navigating to login page");
        model.addAttribute("authUserDto", new AuthUserDto());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute AuthUserDto authUserDto, Model model, HttpServletResponse response) {
        LOGGER.info("Attempting to authenticate user: {}", authUserDto.getLogin());
        var userDto = authService.authenticate(authUserDto);
        if (userDto != null) {
            String token = jwtProvider.generateToken(userDto);
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
            LOGGER.info("Created JWT cookie : {}", token);
            LOGGER.debug("Authentication successful for user: {}", authUserDto.getLogin());
            return "redirect:/home";
        } else {
            LOGGER.warn("Authentication failed for user: {}", authUserDto.getLogin());
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        LOGGER.info("Navigating to register page");
        UserDto userDto = new UserDto();
        userDto.setId(0L); // Устанавливаем id по умолчанию
        model.addAttribute("userDto", userDto);

        Map<String, String> rolesMap = new HashMap<>();
        rolesMap.put(Role.ADMIN.name(), "Администратор");
        rolesMap.put(Role.USER.name(), "Пользователь");
        model.addAttribute("rolesMap", rolesMap);

        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UserDto userDto, Model model) {
        LOGGER.info("Attempting to register user: {}", userDto.getLogin());
        userDto.setBalance(0.0);
        boolean isRegistered = authService.registerUser(userDto);
        if (isRegistered) {
            LOGGER.info("Registration successful for user: {}", userDto.getLogin());
            return "redirect:/login";
        } else {
            LOGGER.warn("Registration failed for user: {}", userDto.getLogin());
            model.addAttribute("error", "Ошибка регистрации. Попробуйте снова.");
            return "register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        LOGGER.info("Navigating to forgot password page");
        model.addAttribute("passwordResetDto", new PasswordResetDto());
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@ModelAttribute PasswordResetDto passwordResetDto, Model model) {
        LOGGER.info("Checking if email exists: {}", passwordResetDto.getEmail());
        boolean exists = authService.checkEmailExists(passwordResetDto.getEmail());
        if (exists) {
            LOGGER.info("Email exists: {}", passwordResetDto.getEmail());
            return "redirect:/reset-password?email=" + passwordResetDto.getEmail();
        } else {
            LOGGER.warn("Email does not exist: {}", passwordResetDto.getEmail());
            model.addAttribute("error", "Пользователь с таким адресом электронной почты не найден.");
            return "forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@ModelAttribute("email") String email, Model model) {
        LOGGER.info("Navigating to reset password page for email: {}", email);
        PasswordResetDto passwordResetDto = new PasswordResetDto();
        passwordResetDto.setEmail(email);
        LOGGER.info("Checking if email exists: {}", passwordResetDto.getEmail());
        model.addAttribute("passwordResetDto", passwordResetDto);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@ModelAttribute PasswordResetDto passwordResetDto, Model model) {
        LOGGER.info("Attempting to reset password for email: {}", passwordResetDto.getEmail());
        boolean isUpdated = authService.updatePasswordByLogin(passwordResetDto.getEmail(), passwordResetDto.getNewPassword());
        if (isUpdated) {
            LOGGER.info("Password reset successful for email: {}", passwordResetDto.getEmail());
            return "redirect:/login";
        } else {
            LOGGER.warn("Password reset failed for email: {}", passwordResetDto.getEmail());
            model.addAttribute("error", "Ошибка при смене пароля. Попробуйте снова.");
            return "reset-password";
        }
    }

}
