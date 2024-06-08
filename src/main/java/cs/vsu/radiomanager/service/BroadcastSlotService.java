package cs.vsu.radiomanager.service;

import cs.vsu.radiomanager.dto.BroadcastSlotDto;
import cs.vsu.radiomanager.mapper.BroadcastSlotMapper;
import cs.vsu.radiomanager.model.BroadcastSlot;
import cs.vsu.radiomanager.model.enumerate.Status;
import cs.vsu.radiomanager.repository.BroadcastSlotRep;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BroadcastSlotService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BroadcastSlotService.class);

    private static final Duration MIN_DURATION = Duration.ofSeconds(10);

    private final BroadcastSlotRep broadcastSlotRep;

    private final BroadcastSlotMapper mapper;

    public List<BroadcastSlotDto> getAllBroadcastSlots() {
        LOGGER.debug("Fetching all broadcast slots");
        return mapper.toDtoList(broadcastSlotRep.findAll());
    }

    public BroadcastSlotDto getBroadcastSlotById(Long id) {
        LOGGER.debug("Fetching broadcast slot by id: {}", id);
        return broadcastSlotRep.findById(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    public List<BroadcastSlotDto> getBroadcastSlotsByStatus(Status status) {
        LOGGER.debug("Fetching broadcast slot by status: {}", status);
        return mapper.toDtoList(broadcastSlotRep.findByStatus(status));
    }

    public BroadcastSlotDto getBroadcastSlotByStartTimeAndEndTime(LocalDateTime startTime, LocalDateTime endTime) {
        LOGGER.debug("Fetching broadcast slot by startTime and endTime: {}", startTime.toString() + " --- " + endTime.toString());
        return broadcastSlotRep.findByStartTimeAndEndTime(startTime, endTime)
                .map(mapper::toDto)
                .orElse(null);
    }

    public BroadcastSlotDto createBroadcastSlot(BroadcastSlotDto broadcastSlotDto) {
        LOGGER.debug("Creating broadcast slot: {}", broadcastSlotDto);
        try {
            BroadcastSlot broadcastSlot = broadcastSlotRep.save(mapper.toEntity(broadcastSlotDto));
            return mapper.toDto(broadcastSlot);
        } catch (Exception e) {
            LOGGER.error("Error creating broadcast slot", e);
            throw new RuntimeException("Error creating broadcast slot", e);
        }
    }

    public BroadcastSlotDto updateBroadcastSlot(BroadcastSlotDto broadcastSlotDto) {
        LOGGER.debug("Updating broadcast slot: {}", broadcastSlotDto);
        Optional<BroadcastSlot> broadcastSlot = broadcastSlotRep.findById(broadcastSlotDto.getId());
        if (broadcastSlot.isPresent()) {
            BroadcastSlot broadcastSlotEntity = broadcastSlot.get();
            mapper.updateEntityFromDto(broadcastSlotDto, broadcastSlotEntity);
            BroadcastSlot updatedBroadcastSlot = broadcastSlotRep.save(broadcastSlotEntity);
            LOGGER.info("Updated broadcast slot: {}", updatedBroadcastSlot);
            return mapper.toDto(updatedBroadcastSlot);
        }
        LOGGER.warn("No broadcast slot found with id: {}", broadcastSlotDto.getId());
        return null;
    }

    public boolean deleteBroadcastSlot(Long id) {
        LOGGER.debug("Deleting broadcast slot: {}", id);
        try {
            Optional<BroadcastSlot> broadcastSlot = broadcastSlotRep.findById(id);
            if (broadcastSlot.isPresent()) {
                BroadcastSlot broadcastSlotEntity = broadcastSlot.get();
                broadcastSlotRep.delete(broadcastSlotEntity);
                LOGGER.info("Deleted broadcast slot: {}", broadcastSlot);
                return true;
            }
            LOGGER.warn("No broadcast slot found with id: {}", id);
            return false;
        } catch (Exception e) {
            LOGGER.error("Error deleting broadcast slot", e);
            throw new RuntimeException("Error deleting broadcast slot", e);
        }
    }

    public BroadcastSlotDto updateBroadcastSlotStatus(Long id, Status status) {
        LOGGER.debug("Updating broadcast slot status: {}", id);
        Optional<BroadcastSlot> broadcastSlot = broadcastSlotRep.findById(id);
        if (broadcastSlot.isPresent()) {
            BroadcastSlot broadcastSlotEntity = broadcastSlot.get();
            broadcastSlotEntity.setStatus(status);
            BroadcastSlot updatedBroadcastSlot = broadcastSlotRep.save(broadcastSlotEntity);
            LOGGER.info("Updated broadcast slot status: {}", updatedBroadcastSlot);
            return mapper.toDto(updatedBroadcastSlot);
        }
        LOGGER.warn("No broadcast slot found with id: {}", id);
        return null;
    }

    public BroadcastSlotDto updateBroadcastSlotEndTime(Long id, LocalDateTime endTime) {
        LOGGER.debug("Updating broadcast slot end time: {}", id);
        Optional<BroadcastSlot> broadcastSlot = broadcastSlotRep.findById(id);
        if (broadcastSlot.isPresent()) {
            BroadcastSlot broadcastSlotEntity = broadcastSlot.get();
            broadcastSlotEntity.setEndTime(endTime);
            BroadcastSlot updatedBroadcastSlot = broadcastSlotRep.save(broadcastSlotEntity);
            LOGGER.info("Updated broadcast slot endTime: {}", updatedBroadcastSlot);
            return mapper.toDto(updatedBroadcastSlot);
        }
        LOGGER.warn("No broadcast slot found with id: {}", id);
        return null;
    }

    public BroadcastSlotDto splitBroadcastSlot(Long id, LocalDateTime newEndTime) {
        LOGGER.debug("Splitting broadcast slot with id: {}", id);
        Optional<BroadcastSlot> broadcastSlotOpt = broadcastSlotRep.findById(id);
        if (broadcastSlotOpt.isPresent()) {
            BroadcastSlot broadcastSlot = broadcastSlotOpt.get();

            if (newEndTime.isAfter(broadcastSlot.getEndTime())) {
                LOGGER.warn("New end time exceeds original broadcast slot end time.");
                return null;
            }

            LocalDateTime originalEndTime = broadcastSlot.getEndTime();

            broadcastSlot.setEndTime(newEndTime);
            broadcastSlot.setStatus(Status.OCCUPIED);
            BroadcastSlot updatedBroadcastSlot = broadcastSlotRep.save(broadcastSlot);
            LOGGER.info("Updated original broadcast slot endTime and status: {}", updatedBroadcastSlot);

            if (Duration.between(newEndTime, originalEndTime).compareTo(MIN_DURATION) >= 0) {
                BroadcastSlot newBroadcastSlot = new BroadcastSlot();
                newBroadcastSlot.setStartTime(newEndTime);
                newBroadcastSlot.setEndTime(originalEndTime);
                newBroadcastSlot.setStatus(Status.AVAILABLE);

                BroadcastSlot savedNewBroadcastSlot = broadcastSlotRep.save(newBroadcastSlot);
                LOGGER.info("Created new broadcast slot with id: {}", savedNewBroadcastSlot.getId());
            }

            return mapper.toDto(updatedBroadcastSlot);
        }

        LOGGER.warn("No broadcast slot found with id: {}", id);
        return null;
    }

    public List<BroadcastSlotDto> getBroadcastSlotsByMonth(int year, int month) {
        LOGGER.debug("Fetching broadcast slots for year: {} and month: {}", year, month);
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);
        List<BroadcastSlot> slots = broadcastSlotRep.findByStartTimeBetween(start, end);
        return mapper.toDtoList(slots);
    }

}
