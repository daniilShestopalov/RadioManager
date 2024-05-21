package cs.vsu.radiomanager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PlacementDto {

    @NotNull
    private Long id;

    @NotNull
    private Long broadcastSlotId;

    @NotNull
    private Long audioRecordingId;

    @NotNull
    private LocalDateTime placementDate;

}
