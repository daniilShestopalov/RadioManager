package cs.vsu.radiomanager.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordResetDto {

    @NotBlank
    @Email
    private String email;

    private String newPassword;

}
