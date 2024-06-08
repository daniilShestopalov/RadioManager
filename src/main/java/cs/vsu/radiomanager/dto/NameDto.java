package cs.vsu.radiomanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NameDto {

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

}
