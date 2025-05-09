package cs.vsu.radiomanager.controller.api;

import cs.vsu.radiomanager.dto.UserDto;
import cs.vsu.radiomanager.model.enumerate.Role;
import cs.vsu.radiomanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all users."
    )
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            LOGGER.info("Fetching all users");
            List<UserDto> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            LOGGER.error("Error fetching all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a user by its ID."
    )
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            LOGGER.info("Fetching user with ID: {}", id);
            UserDto user = userService.getUserById(id);
            if (user != null) {
                return ResponseEntity.ok(user);
            }

            LOGGER.warn("User with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.error("Error fetching user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-login")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get user by login",
            description = "Retrieves a user (without password) by login."
    )
    public ResponseEntity<?> getUserByLogin(@RequestParam String login) {
        try {
            LOGGER.info("Fetching user with login: {}", login);
            UserDto user = userService.getUserByLogin(login);
            if (user != null) {
                return ResponseEntity.ok(user);
            }

            LOGGER.warn("User with login {} not found", login);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.error("Error fetching user with login: {}", login, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get users by role",
            description = "Retrieves a list of users with the specified role."
    )
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            LOGGER.info("Fetching users with role: {}", role);
            Role roleEnum = Role.valueOf(role.toUpperCase());
            List<UserDto> users = userService.getUsersByRole(roleEnum);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException ex) {
            LOGGER.warn("Invalid role provided: {}", role);
            return ResponseEntity.badRequest().body("Invalid role.");
        } catch (Exception e) {
            LOGGER.error("Error fetching users by role: {}", role, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create new user",
            description = "Creates a new user."
    )
    public ResponseEntity<?> createUser(@RequestBody @Valid UserDto userDto) {
        try {
            LOGGER.info("Creating user: {}", userDto);
            UserDto createdUser = userService.createUser(userDto);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            LOGGER.error("Error creating user: {}", userDto.getLogin(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update user",
            description = "Updates an existing user."
    )
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserDto userDto) {
        try {
            LOGGER.info("Updating user: {}", userDto);
            UserDto updatedUser = userService.updateUser(userDto);
            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser);
            }

            LOGGER.warn("User with ID {} not found for update", userDto.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.error("Error updating user: {}", userDto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete user",
            description = "Deletes a user by its ID."
    )
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            LOGGER.info("Deleting user with ID: {}", id);
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            }

            LOGGER.warn("User with ID {} not found for deletion", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.error("Error deleting user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/balance")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Update user balance",
            description = "Updates the balance of an existing user."
    )
    public ResponseEntity<?> updateBalance(@RequestParam Long userId, @RequestParam Double balance) {
        try {
            LOGGER.info("Updating balance for user ID: {}", userId);
            boolean isUpdated = userService.updateBalance(userId, balance);
            if (isUpdated) {
                return ResponseEntity.ok().build();
            }

            LOGGER.warn("User with ID {} not found for balance update", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.error("Error updating balance for user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/roles")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get available roles for user",
            description = "\"Retrieves a list of all roles that the authenticated user can assume or assign." +
                    " Useful for populating role selection dropdowns in the UI."
    )
    public ResponseEntity<?> getRoles() {
        try {
            LOGGER.info("Fetching available roles for user");
            List<Role> roles = Arrays.asList(Role.values());
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            LOGGER.error("Error fetching available roles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
