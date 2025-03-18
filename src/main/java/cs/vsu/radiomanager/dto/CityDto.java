package cs.vsu.radiomanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CityDto {

    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String region;

}
