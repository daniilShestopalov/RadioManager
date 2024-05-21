package cs.vsu.radiomanager.dto;

import cs.vsu.radiomanager.model.enumerate.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BroadcastSlotDto {

    @NotNull
    private Long id;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    private Status status;

}
