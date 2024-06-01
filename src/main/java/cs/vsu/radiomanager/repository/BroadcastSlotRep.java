package cs.vsu.radiomanager.repository;

import cs.vsu.radiomanager.model.BroadcastSlot;
import cs.vsu.radiomanager.model.enumerate.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BroadcastSlotRep extends JpaRepository<BroadcastSlot, Integer> {

    List<BroadcastSlot> findByStatus(Status status);
    List<BroadcastSlot> findByStartTimeAndEndTime(LocalDateTime startTime, LocalDateTime endTime);
    List<BroadcastSlot> findByStartTimeAndEndTimeAndStatus(LocalDateTime startTime, LocalDateTime endTime, Status status);

}
