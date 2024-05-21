package cs.vsu.radiomanager.dto;

import cs.vsu.radiomanager.model.enumerate.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class UserDto {

    private int id;

    @NotBlank(message = "Login is required")
    private String login;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Surname is required")
    private String surname;

    @NotNull(message = "Role cannot be null")
    private Double balance;

    @NotNull(message = "Role cannot be null")
    private Role role;
}
