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

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    private final UserService userService;

    private final BCryptPasswordEncoder passwordEncoder;

    private final UserMapper mapper;

    private final UserRep userRepository;

    public UserDto authenticate(@NonNull AuthUserDto authUserDto) {
        LOGGER.info("Authenticating user with login: {}", authUserDto.getLogin());
        Optional<User> userOptional = userRepository.findByLogin(authUserDto.getLogin());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(authUserDto.getPassword(), user.getPassword())) {
                LOGGER.info("Authentication successful for user: {}", user.getLogin());
                return mapper.toDto(user);
            } else {
                LOGGER.warn("Authentication failed for user: {}", authUserDto.getLogin());
            }
        } else {
            LOGGER.warn("User not found with login: {}", authUserDto.getLogin());
        }
        return null;
    }

    public boolean checkEmailExists(@NotNull String email) {
        LOGGER.info("Checking if email exists: {}", email);
        Optional<User> userOptional = userRepository.findByLogin(email);
        boolean exists = userOptional.isPresent();
        if (exists) {
            LOGGER.info("Email exists: {}", email);
        } else {
            LOGGER.warn("Email does not exist: {}", email);
        }
        return exists;
    }

    public boolean updatePasswordByLogin(@NotNull String login, @NotNull String password) {
        LOGGER.info("Updating password by login: {}", login);
        Optional<User> userOptional = userRepository.findByLogin(login);
        if (userOptional.isPresent()) {
            userService.updatePassword(userOptional.get().getId(), password);
            LOGGER.info("Password updated successfully");
            return true;
        }
        return false;
    }

    public boolean updatePasswordById(Integer userId, String newPassword) {
        LOGGER.info("Updating password for user ID: {}", userId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            userService.updatePassword(userOptional.get().getId(), newPassword);
            LOGGER.info("Password updated successfully for user ID: {}", userId);
            return true;
        }
        LOGGER.warn("User not found with ID: {}", userId);
        return false;
    }

    public boolean registerUser(@NotNull UserDto userDto) {
        LOGGER.info("Registering user with ID: {}", userDto.getId());
        Optional<User> userOptional = userRepository.findById(userDto.getId());
        if (userOptional.isEmpty()) {
            userService.createUser(userDto);
            LOGGER.info("User created successfully");
            return true;
        }
        LOGGER.warn("User already exists with ID: {}", userDto.getId());
        return false;
    }

}
