package cs.vsu.radiomanager.dto;

import cs.vsu.radiomanager.model.User;
import cs.vsu.radiomanager.model.enumerate.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

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
