package cs.vsu.radiomanager.repository;

import cs.vsu.radiomanager.model.BroadcastSlot;
import cs.vsu.radiomanager.model.enumerate.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BroadcastSlotRep extends JpaRepository<BroadcastSlot, Integer> {

    Optional<BroadcastSlot> findById(Long id);
    List<BroadcastSlot> findByStatus(Status status);
    Optional<BroadcastSlot> findByStartTimeAndEndTime(LocalDateTime startTime, LocalDateTime endTime);
    List<BroadcastSlot> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);


}
