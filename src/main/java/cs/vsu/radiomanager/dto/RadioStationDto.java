package cs.vsu.radiomanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class RadioStationDto {

    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private BigDecimal frequency;

    @NotNull
    private Long cityId;

    @NotNull
    private Long representativeId;

}
