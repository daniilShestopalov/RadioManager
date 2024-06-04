package cs.vsu.radiomanager.service;

import cs.vsu.radiomanager.dto.UserDto;
import cs.vsu.radiomanager.mapper.UserMapper;
import cs.vsu.radiomanager.model.User;
import cs.vsu.radiomanager.model.enumerate.Role;
import cs.vsu.radiomanager.repository.UserRep;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRep userRep;

    private UserMapper mapper;

    private final BCryptPasswordEncoder passwordEncoder;

    public List<UserDto> getAllUsers() {
        LOGGER.debug("Fetching all users");
        return mapper.toDtoList(userRep.findAll());
    }

    public UserDto getUserById(Long id) {
        LOGGER.debug("Fetching user with password by id: {}", id);
        return userRep.findById(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    public List<UserDto> getUsersByRole(Role role) {
        LOGGER.debug("Fetching users by role: {}", role);
        return mapper.toDtoList(userRep.findByRole(role));
    }

    public UserDto getUserByLogin(String login) {
        LOGGER.debug("Fetching user without password by login: {}", login);
        return userRep.findByLogin(login)
                .map(mapper::toDto)
                .orElse(null);
    }

    public UserDto createUser(UserDto userDto) {
        try {
            User user = mapper.toEntity(userDto);
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user = userRep.save(user);
            LOGGER.info("User created: {}", userDto.getLogin());
            return mapper.toDto(user);
        } catch (Exception e) {
            LOGGER.error("Error creating user: {}", userDto.getLogin(), e);
            throw new RuntimeException("Error creating user", e);
        }
    }

    public boolean deleteUser(Long id) {
        try {
            Optional<User> user = userRep.findById(id);
            if (user.isPresent()) {
                userRep.deleteById(Math.toIntExact(id));
                LOGGER.info("User deleted with id: {}", id);
                return true;
            }
            return false;

        } catch (Exception e) {
            LOGGER.error("Error deleting user with id: {}", id, e);
            throw new RuntimeException("Error deleting user", e);
        }
    }

    public UserDto updateUser(@NonNull UserDto userDTO) {
        Optional<User> existingUserOptional = userRep.findById(userDTO.getId());
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            mapper.updateEntityFromDto(userDTO, existingUser);
            User updatedUser = userRep.save(existingUser);
            LOGGER.info("User updated: {}", userDTO.getId());
            return mapper.toDto(updatedUser);
        }
        LOGGER.warn("User with id {} not found for update", userDTO.getId());
        return null;
    }

    public boolean updatePassword(Long userId, String newPassword) {
        Optional<User> existingUserOptional = userRep.findById(userId);
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            existingUser.setPassword(passwordEncoder.encode(newPassword));
            userRep.save(existingUser);
            LOGGER.info("User password updated for user id: {}", userId);
            return true;
        }
        LOGGER.warn("User with id {} not found for password update", userId);
        return false;
    }

    public boolean updateBalance(Long userId, Double balance) {
        Optional<User> existingUserOptional = userRep.findById(userId);
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            existingUser.setBalance(balance);
            userRep.save(existingUser);
            LOGGER.info("User balance updated for user id: {}", userId);
            return true;
        }
        LOGGER.warn("User with id {} not found for balance update", userId);
        return false;
    }

    public Role getRoleById(Long id) {
        LOGGER.debug("Getting role by id: {}", id);
        return getUserById(id).getRole();
    }

}
