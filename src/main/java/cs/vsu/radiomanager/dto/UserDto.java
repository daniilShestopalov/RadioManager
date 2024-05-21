package cs.vsu.radiomanager.dto;

import cs.vsu.radiomanager.model.enumerate.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class UserDto {

    @NotNull
    private Long id;

    @NotBlank
    private String login;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @NotNull
    private Double balance;

    @NotNull
    private Role role;
}
