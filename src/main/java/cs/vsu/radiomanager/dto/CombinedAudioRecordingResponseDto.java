package cs.vsu.radiomanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CombinedAudioRecordingResponseDto {

    @NotNull
    private AudioRecordingDto recording;

    @NotBlank
    private String fileContentBase64;

}
