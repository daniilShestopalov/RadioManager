package cs.vsu.radiomanager.dto;

import cs.vsu.radiomanager.model.enumerate.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AudioRecordingDto {

    @NotNull
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Double duration;

    @NotNull
    private Double cost;

    @NotNull
    private Status approvalStatus;

    @NotNull
    private String filePath;
}
