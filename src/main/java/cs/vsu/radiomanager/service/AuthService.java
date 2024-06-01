package cs.vsu.radiomanager.service;

import cs.vsu.radiomanager.dto.UserDto;
import cs.vsu.radiomanager.dto.auth.AuthUserDto;
import cs.vsu.radiomanager.mapper.UserMapper;
import cs.vsu.radiomanager.model.User;
import cs.vsu.radiomanager.repository.UserRep;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserService userService;

    private final BCryptPasswordEncoder passwordEncoder;

    private final UserMapper mapper;

    private final UserRep userRepository;

    public UserDto authenticate(@NonNull AuthUserDto authUserDto) {
        logger.info("Authenticating user with login: {}", authUserDto.getLogin());
        Optional<User> userOptional = userRepository.findByLogin(authUserDto.getLogin());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(authUserDto.getPassword(), user.getPassword())) {
                logger.info("Authentication successful for user: {}", user.getLogin());
                return mapper.toDto(user);
            } else {
                logger.warn("Authentication failed for user: {}", authUserDto.getLogin());
            }
        } else {
            logger.warn("User not found with login: {}", authUserDto.getLogin());
        }
        return null;
    }

    public boolean checkEmailExists(@NotNull String email) {
        logger.info("Checking if email exists: {}", email);
        Optional<User> userOptional = userRepository.findByLogin(email);
        boolean exists = userOptional.isPresent();
        if (exists) {
            logger.info("Email exists: {}", email);
        } else {
            logger.warn("Email does not exist: {}", email);
        }
        return exists;
    }

    public boolean updatePasswordById(Integer userId, String newPassword) {
        logger.info("Updating password for user ID: {}", userId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            userService.updatePassword(userOptional.get().getId(), newPassword);
            logger.info("Password updated successfully for user ID: {}", userId);
            return true;
        }
        logger.warn("User not found with ID: {}", userId);
        return false;
    }

    public boolean registerUser(@NotNull UserDto userDto) {
        logger.info("Registering user with ID: {}", userDto.getId());
        Optional<User> userOptional = userRepository.findById(userDto.getId());
        if (userOptional.isEmpty()) {
            userService.createUser(userDto);
            logger.info("User created successfully");
        }
        logger.warn("User already exists with ID: {}", userDto.getId());
        return false;
    }

}
