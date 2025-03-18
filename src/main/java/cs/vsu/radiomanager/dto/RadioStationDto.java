package cs.vsu.radiomanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @Positive
    @NotNull
    private BigDecimal frequency;

    @NotNull
    private Long cityId;

    @NotNull
    private Long representativeId;

}
