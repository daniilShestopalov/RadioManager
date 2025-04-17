package cs.vsu.radiomanager.service;

import cs.vsu.radiomanager.config.BaseProperties;
import cs.vsu.radiomanager.dto.BroadcastSlotDto;
import cs.vsu.radiomanager.mapper.BroadcastSlotMapper;
import cs.vsu.radiomanager.model.BroadcastSlot;
import cs.vsu.radiomanager.model.enumerate.Status;
import cs.vsu.radiomanager.repository.BroadcastSlotRep;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BroadcastSlotService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BroadcastSlotService.class);

    private final BroadcastSlotRep broadcastSlotRep;

    private final BroadcastSlotMapper mapper;

    private final BaseProperties baseProperties;

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

    public BroadcastSlotDto getBroadcastSlotByStartTimeAndEndTimeAndRadioStation(LocalDateTime startTime,
                                                                                 LocalDateTime endTime,
                                                                                 Long radioStationId) {
        LOGGER.debug(
                "Fetching broadcast slot by startTime and endTime and RadioStation: {}",
                startTime.toString() + " --- " + endTime.toString() + "by radio station with id: " + radioStationId
        );
        return broadcastSlotRep.findByStartTimeAndEndTimeAndRadioStationId(startTime, endTime, radioStationId)
                .map(mapper::toDto)
                .orElse(null);
    }

    public List<BroadcastSlotDto> getBroadcastSlotsByStatus(Status status) {
        LOGGER.debug("Fetching broadcast slot by status: {}", status);
        return mapper.toDtoList(broadcastSlotRep.findByStatus(status));
    }

    public BroadcastSlotDto getBroadcastSlotByStartTimeAndEndTime(LocalDateTime startTime, LocalDateTime endTime) {
        LOGGER.debug(
                "Fetching broadcast slot by startTime and endTime: {}",
                startTime.toString() + " --- " + endTime.toString()
        );
        return broadcastSlotRep.findByStartTimeAndEndTime(startTime, endTime)
                .map(mapper::toDto)
                .orElse(null);
    }

    public  List<BroadcastSlotDto> getBroadcastSlotByRadioStationId(Long radioStationId) {
        LOGGER.debug("Fetching broadcast slot by radio station with id: {}", radioStationId);
        return mapper.toDtoList(broadcastSlotRep.findByRadioStationId(radioStationId));
    }

    public List<BroadcastSlotDto> getBroadcastSlotsByRadioStationIdWithStatus(Long radioStationId, Status status) {
        LOGGER.debug("Fetching broadcast slot by radio station with id: {}, with status: {}", radioStationId, status);
        return mapper.toDtoList(broadcastSlotRep.findByRadioStationIdAndStatus(radioStationId, status));
    }

    public List<BroadcastSlotDto> getEmptyBroadcastSlotsByPriorityWithRadioStation(
            Long radioStationId, boolean highPriority) {
        LOGGER.debug("Fetching broadcast slot by radio station with id: {} and high priority is {}",
                radioStationId, highPriority);
        List<BroadcastSlotDto> slots = getBroadcastSlotsByRadioStationIdWithStatus(radioStationId, Status.AVAILABLE);
        return slots.stream()
                .filter(slot -> {
                    LocalTime slotStart = slot.getStartTime().toLocalTime();

                    boolean isHigh = baseProperties.getPriorityHigh().stream()
                            .anyMatch(window ->
                                    !slotStart.isBefore(window.getStart()) &&
                                            slotStart.isBefore(window.getEnd())
                            );
                    return highPriority == isHigh;
                })
                .toList();
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

    public List<BroadcastSlotDto> createBroadcastSlots(List<BroadcastSlotDto> broadcastSlotsDto) {
        LOGGER.debug("Creating broadcast slots: {}", broadcastSlotsDto);
        try {
            List<BroadcastSlot> entities = mapper.toEntityList(broadcastSlotsDto);

            List<BroadcastSlot> savedEntities = broadcastSlotRep.saveAll(entities);

            List<BroadcastSlotDto> result = mapper.toDtoList(savedEntities);
            LOGGER.info("Successfully created {} broadcast slots", result.size());
            return result;
        } catch (Exception e) {
            LOGGER.error("Error creating broadcast slots", e);
            throw new RuntimeException("Error creating broadcast slots", e);
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
        LOGGER.warn("No broadcast slot found for update with id: {}", broadcastSlotDto.getId());
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
            LOGGER.warn("No broadcast slot found for delete with id: {}", id);
            return false;
        } catch (Exception e) {
            LOGGER.error("Error deleting broadcast slot", e);
            throw new RuntimeException("Error deleting broadcast slot", e);
        }
    }

    public boolean deleteBroadcastSlotsByRadioStationAfterStartTime(
            Long radioStationId, LocalDateTime startTime) {
        LOGGER.debug("Deleting broadcast slots of radio station {} after start time: {}",
                radioStationId, startTime);
        try {
            if (broadcastSlotRep.deleteAllByRadioStationIdAndStartTimeAfter(radioStationId, startTime)) {
                LOGGER.info("Deleted broadcast slots of radio station {} after start time: {}",
                        radioStationId, startTime);
                return true;
            }
            LOGGER.warn("No broadcast slots found for delete");
            return false;
        } catch (Exception e) {
            LOGGER.error("Error deleting broadcast slots of radio station", e);
            throw new RuntimeException("Error deleting broadcast slots of radio station", e);
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
        LOGGER.warn("No broadcast slot found for update status with id: {}", id);
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
        LOGGER.warn("No broadcast slot found for update end time with id: {}", id);
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

            if (Duration.between(newEndTime, originalEndTime)
                    .compareTo(Duration.ofSeconds(baseProperties.getMinSlotDuration())) >= 0) {
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
        return slots.stream()
                .sorted(Comparator.comparing(BroadcastSlot::getStartTime))
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BroadcastSlotDto> getBroadcastSlotsByRadioStationIdAfterStartTime(Long id, LocalDateTime startTime) {
        LOGGER.debug("Fetching broadcast slots for radio station: {} after {}", id, startTime.toString());
        List<BroadcastSlot> slots = broadcastSlotRep.findByRadioStationIdAndStartTimeAfter(id, startTime);
        return slots.stream()
                .sorted(Comparator.comparing(BroadcastSlot::getStartTime))
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BroadcastSlotDto> getBroadcastSlotsByRadioStationIdWithStatusAfterStartTime(
            Long id, Status status,LocalDateTime startTime) {
        LOGGER.debug("Fetching broadcast slots for radio station: {} with status: {} after {}",
                id, status,startTime.toString());
        List<BroadcastSlot> slots = broadcastSlotRep.findByRadioStationIdAndStatusAndStartTimeAfter(
                id, status, startTime);
        return slots.stream()
                .sorted(Comparator.comparing(BroadcastSlot::getStartTime))
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BroadcastSlotDto> createBroadcastSlotsDtoFromTimeAndStation(
            Long stationId, List<Pair<LocalDateTime, LocalDateTime>> timePair) {
        LOGGER.debug("Creating broadcast slot dto for station: {}", stationId);
        try {
            List<BroadcastSlotDto> dtos = new ArrayList<>();
            BroadcastSlotDto newDto;
            for (Pair<LocalDateTime, LocalDateTime> pair : timePair) {
                newDto = new BroadcastSlotDto();
                //newDto.setId(0L);
                newDto.setStartTime(pair.getFirst());
                newDto.setEndTime(pair.getSecond());
                newDto.setStatus(Status.AVAILABLE);
                newDto.setRadioStationId(stationId);
                dtos.add(newDto);
            }
            LOGGER.debug("Created {} broadcast slot DTOs for station {}", dtos.size(), stationId);
            return dtos;
        } catch (Exception e) {
            LOGGER.error("Error creating broadcast slot dto", e);
            throw new RuntimeException("Error creating broadcast slot dto", e);
        }

    }

}