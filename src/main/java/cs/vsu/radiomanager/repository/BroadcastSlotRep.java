package cs.vsu.radiomanager.repository;

import cs.vsu.radiomanager.model.BroadcastSlot;
import cs.vsu.radiomanager.model.RadioStation;
import cs.vsu.radiomanager.model.enumerate.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BroadcastSlotRep extends JpaRepository<BroadcastSlot, Integer> {

    Optional<BroadcastSlot> findById(Long id);
    Optional<BroadcastSlot> findByStartTimeAndEndTimeAndRadioStationId(LocalDateTime startTime, LocalDateTime endTime, Long radioStationId);
    List<BroadcastSlot> findByStatus(Status status);
    Optional<BroadcastSlot> findByStartTimeAndEndTime(LocalDateTime startTime, LocalDateTime endTime);
    List<BroadcastSlot> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    List<BroadcastSlot> findByRadioStationIdAndStartTimeAfter(Long radioStationId, LocalDateTime startTime);
    List<BroadcastSlot> findByRadioStationIdAndStatusAndStartTimeAfter(Long radioStationId, Status status,LocalDateTime startTime);
    boolean deleteAllByRadioStationIdAndStartTimeAfter(Long radioStationId, LocalDateTime startTime);

}
