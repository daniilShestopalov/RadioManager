package cs.vsu.radiomanager.repository;

import cs.vsu.radiomanager.model.Placement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlacementRep extends JpaRepository<Placement, Integer> {

    Optional<Placement> findById(Long id);
    List<Placement> findByPlacementDate(LocalDateTime placementDate);
    Optional<Placement> findByAudioRecordingId(Long audioRecordingId);
    Optional<Placement> findByBroadcastSlotId(Long broadcastSlotId);

}
