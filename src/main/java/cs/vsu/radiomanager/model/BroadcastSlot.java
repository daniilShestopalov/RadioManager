package cs.vsu.radiomanager.model;

import cs.vsu.radiomanager.model.enumerate.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Entity
@Table(name = "broadcast_slot")
@Getter
@Setter
public class BroadcastSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slotID;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime endTime;

    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

}
