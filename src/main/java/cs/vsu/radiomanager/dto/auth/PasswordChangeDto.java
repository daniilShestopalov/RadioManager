package cs.vsu.radiomanager.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordChangeDto {

    @NotBlank
    private String verificationCode;

    @NotBlank
    private String newPassword;

}
