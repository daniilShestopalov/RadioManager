package cs.vsu.radiomanager.model;

import cs.vsu.radiomanager.model.enumerate.ApprovalStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "audio_recording")
@Getter
@Setter
public class AudioRecording {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "duration", nullable = false)
    private Double duration;

    @Column(name = "cost", nullable = false)
    private Double cost;

    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "approval_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @Column(name = "file_path", nullable = false)
    private String filePath;

}
