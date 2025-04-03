package cs.vsu.radiomanager.service;

import cs.vsu.radiomanager.dto.UserDto;
import cs.vsu.radiomanager.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ResetService {

    private final JwtProvider jwtProvider;

    private final AuthService authService;

    private final UserService userService;

    private final JavaMailSender mailSender;

    public void sendPasswordReset(String email) {
        if (authService.checkEmailExists(email)) {
            UserDto user = userService.getUserByLogin(email);
            Long userId = user.getId();
            String token = jwtProvider.generatePasswordResetToken(userId);

            String subject = "Password Reset Request";
            String text = "Your password reset code is: " + token;

            sendEmail(email, subject, text);
        } else {
            throw new RuntimeException("User with email " + email + " not found");
        }
    }

    private void sendEmail(String email, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("${spring.mail.username}");
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

}
