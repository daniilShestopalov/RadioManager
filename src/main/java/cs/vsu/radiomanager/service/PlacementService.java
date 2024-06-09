package cs.vsu.radiomanager.service;

import cs.vsu.radiomanager.dto.PlacementDto;
import cs.vsu.radiomanager.mapper.PlacementMapper;
import cs.vsu.radiomanager.model.Placement;
import cs.vsu.radiomanager.repository.PlacementRep;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PlacementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlacementService.class);

    private final PlacementRep placementRep;

    private final PlacementMapper mapper;

    public List<PlacementDto> getAll() {
        LOGGER.debug("Fetching all placements");
        return mapper.toDtoList(placementRep.findAll());
    }

    public PlacementDto getById(Long id) {
        LOGGER.debug("Fetching placement by id: {}", id);
        return placementRep.findById(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    public List<PlacementDto> getByPlacementDate(LocalDateTime date) {
        LOGGER.debug("Fetching placements by date {}", date);
        return mapper.toDtoList(placementRep.findByPlacementDate(date));
    }

    public List<PlacementDto> getByAudioRecordingId(Long id) {
        LOGGER.debug("Fetching placements by audio recording id {}", id);
        return mapper.toDtoList(placementRep.findByAudioRecordingId(id));
    }

    public PlacementDto getByBroadcastSlotId(Long id) {
        LOGGER.debug("Fetching placement by broadcast slot id {}", id);
        return mapper.toDto(placementRep.findByBroadcastSlotId(id).orElse(null));
    }

    public PlacementDto createPlacement(PlacementDto placementDto) {
        LOGGER.debug("Creating placement {}", placementDto);
        try {
            Placement placement = placementRep.save(mapper.toEntity(placementDto));
            LOGGER.debug("Placement created: {}", placement);
            return mapper.toDto(placement);
        } catch (Exception e) {
            LOGGER.error("Failed to create placement: {}", placementDto, e);
            throw new RuntimeException("Failed to create placement: " + placementDto, e);
        }
    }

    public PlacementDto updatePlacement(PlacementDto placementDto) {
        LOGGER.debug("Updating placement {}", placementDto);
        try {
            Optional<Placement> optional = placementRep.findById(placementDto.getId());
            if (optional.isPresent()) {
                Placement placement = optional.get();
                mapper.updateEntityFromDto(placementDto, placement);
                Placement updated = placementRep.save(placement);
                LOGGER.debug("Placement updated: {}", updated);
                return mapper.toDto(updated);
            }
            LOGGER.warn("Placement with id {} not found", placementDto.getId());
            return null;
        } catch (Exception e) {
            LOGGER.error("Failed to update placement: {}", placementDto, e);
            throw new RuntimeException("Failed to update placement: " + placementDto, e);
        }
    }

    public boolean deletePlacement(Long id) {
        LOGGER.debug("Deleting placement {}", id);
        try {
            Optional<Placement> optional = placementRep.findById(id);
            if (optional.isPresent()) {
                Placement placement = optional.get();
                placementRep.delete(placement);
                LOGGER.debug("Placement deleted: {}", placement);
                return true;
            }
            LOGGER.warn("Placement with id for delete {} not found", id);
            return false;
        } catch (Exception e) {
            LOGGER.error("Failed to delete placement: {}", id, e);
            throw new RuntimeException("Failed to delete placement: " + id, e);
        }
    }

}
