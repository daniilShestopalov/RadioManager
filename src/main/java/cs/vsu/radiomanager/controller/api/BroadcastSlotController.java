package cs.vsu.radiomanager.controller.api;

import cs.vsu.radiomanager.dto.BroadcastSlotDto;
import cs.vsu.radiomanager.model.enumerate.Status;
import cs.vsu.radiomanager.service.BroadcastSlotService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/broadcast-slot")
@AllArgsConstructor
public class BroadcastSlotController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BroadcastSlotController.class);

    private final BroadcastSlotService broadcastSlotService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get all broadcast slots",
            description = "Retrieves a list of all broadcast slots."
    )
    public ResponseEntity<?> getAllBroadcastSlots() {
        try {
            LOGGER.info("Fetching all broadcast slots");
            List<BroadcastSlotDto> slots = broadcastSlotService.getAllBroadcastSlots();
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            LOGGER.error("Error fetching all broadcast slots", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get broadcast slot by ID",
            description = "Retrieves a broadcast slot by its ID."
    )
    public ResponseEntity<?> getBroadcastSlotById(@PathVariable Long id) {
        try {
            LOGGER.info("Fetching broadcast slot with ID: {}", id);
            BroadcastSlotDto slot = broadcastSlotService.getBroadcastSlotById(id);
            if (slot != null) {
                return ResponseEntity.ok(slot);
            }
            LOGGER.warn("Broadcast slot with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Broadcast slot not found.");
        } catch (Exception e) {
            LOGGER.error("Error fetching broadcast slot with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-time-and-station")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get broadcast slot by start time, end time and radio station",
            description = "Retrieves a broadcast slot by its start time, end time and associated radio station ID."
    )
    public ResponseEntity<?> getBroadcastSlotByStartEndAndStation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam Long radioStationId) {
        try {
            LOGGER.info("Fetching broadcast slot with startTime: {} and endTime: {} for radio station ID: {}",
                    startTime, endTime, radioStationId);
            BroadcastSlotDto slot = broadcastSlotService.getBroadcastSlotByStartTimeAndEndTimeAndRadioStation(startTime, endTime, radioStationId);
            if (slot != null) {
                return ResponseEntity.ok(slot);
            }

            LOGGER.warn("Broadcast slot with startTime: {} and endTime: {} for radio station ID: {} not found",
                    startTime, endTime, radioStationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.error("Error fetching broadcast slot by startTime, endTime and radio station", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-status/{status}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get broadcast slots by status",
            description = "Retrieves a list of broadcast slots filtered by their status."
    )
    public ResponseEntity<?> getBroadcastSlotsByStatus(@PathVariable String status) {
        try {
            Status slotStatus;
            try {
                slotStatus = Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ex) {
                LOGGER.warn("Invalid status provided: {}", status);
                return ResponseEntity.badRequest().build();
            }

            LOGGER.info("Fetching broadcast slots with status: {}", slotStatus);
            List<BroadcastSlotDto> slots = broadcastSlotService.getBroadcastSlotsByStatus(slotStatus);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            LOGGER.error("Error fetching broadcast slots by status: {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-time")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get broadcast slot by start and end time",
            description = "Retrieves a broadcast slot by its start time and end time."
    )
    public ResponseEntity<?> getBroadcastSlotByStartEnd(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            LOGGER.info("Fetching broadcast slot with startTime: {} and endTime: {}", startTime, endTime);
            BroadcastSlotDto slot = broadcastSlotService.getBroadcastSlotByStartTimeAndEndTime(startTime, endTime);
            if (slot != null) {
                return ResponseEntity.ok(slot);
            }

            LOGGER.warn("Broadcast slot with startTime: {} and endTime: {} not found", startTime, endTime);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Broadcast slot not found.");
        } catch (Exception e) {
            LOGGER.error("Error fetching broadcast slot by start and end time", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/station/{radioStationId}/after")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get broadcast slots after a given start time for a radio station",
            description = "Retrieves broadcast slots for the specified radio station that start after the provided start time."
    )
    public ResponseEntity<?> getBroadcastSlotsAfterStartTime(
            @PathVariable Long radioStationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        try {
            LOGGER.info("Fetching broadcast slots for radio station {} after {}", radioStationId, startTime);
            List<BroadcastSlotDto> slots = broadcastSlotService.getBroadcastSlotsByRadioStationIdAfterStartTime(radioStationId, startTime);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            LOGGER.error("Error fetching broadcast slots for radio station {} after {}", radioStationId, startTime, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/station/{radioStationId}/by-status-after")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get broadcast slots by status after a given start time for a radio station",
            description = "Retrieves broadcast slots for the specified radio station that start after the given time and have the specified status."
    )
    public ResponseEntity<?> getBroadcastSlotsByStatusAfterStartTime(
            @PathVariable Long radioStationId,
            @RequestParam String status,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        try {
            Status slotStatus;
            try {
                slotStatus = Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ex) {
                LOGGER.warn("Invalid status provided: {}", status);
                return ResponseEntity.badRequest().body("Invalid status. Allowed values: " + java.util.Arrays.toString(Status.values()));
            }
            LOGGER.info("Fetching broadcast slots for radio station {} with status {} after {}", radioStationId, slotStatus, startTime);
            List<BroadcastSlotDto> slots = broadcastSlotService.getBroadcastSlotsByRadioStationIdWithStatusAfterStartTime(radioStationId, slotStatus, startTime);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            LOGGER.error("Error fetching broadcast slots for radio station {} with status {} after {}", radioStationId, status, startTime, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create broadcast slot",
            description = "Creates a new broadcast slot."
    )
    public ResponseEntity<?> createBroadcastSlot(@RequestBody @Valid BroadcastSlotDto broadcastSlotDto) {
        try {
            LOGGER.info("Creating broadcast slot: {}", broadcastSlotDto);
            BroadcastSlotDto created = broadcastSlotService.createBroadcastSlot(broadcastSlotDto);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            LOGGER.error("Error creating broadcast slot: {}", broadcastSlotDto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update broadcast slot",
            description = "Updates an existing broadcast slot."
    )
    public ResponseEntity<?> updateBroadcastSlot(@RequestBody @Valid BroadcastSlotDto broadcastSlotDto) {
        try {
            LOGGER.info("Updating broadcast slot: {}", broadcastSlotDto);
            BroadcastSlotDto updated = broadcastSlotService.updateBroadcastSlot(broadcastSlotDto);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            }

            LOGGER.warn("Broadcast slot with ID {} not found for update", broadcastSlotDto.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Broadcast slot not found.");
        } catch (Exception e) {
            LOGGER.error("Error updating broadcast slot: {}", broadcastSlotDto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete broadcast slot",
            description = "Deletes a broadcast slot by its ID."
    )
    public ResponseEntity<?> deleteBroadcastSlot(@PathVariable Long id) {
        try {
            LOGGER.info("Deleting broadcast slot with ID: {}", id);
            boolean deleted = broadcastSlotService.deleteBroadcastSlot(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            }

            LOGGER.warn("Broadcast slot with ID {} not found for deletion", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Broadcast slot not found.");
        } catch (Exception e) {
            LOGGER.error("Error deleting broadcast slot with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/station/{radioStationId}/after")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete broadcast slots for a radio station after a given start time",
            description = "Deletes all broadcast slots for the specified radio station that start after the provided start time."
    )
    public ResponseEntity<?> deleteBroadcastSlotsAfterStartTime(
            @PathVariable Long radioStationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        try {
            LOGGER.info("Deleting broadcast slots for radio station {} after {}", radioStationId, startTime);
            boolean deleted = broadcastSlotService.deleteBroadcastSlotsByRadioStationAfterStartTime(radioStationId, startTime);
            if (deleted) {
                return ResponseEntity.ok().build();
            }

            LOGGER.warn("No broadcast slots found to delete for radio station {} after {}", radioStationId, startTime);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No broadcast slots found for deletion.");
        } catch (Exception e) {
            LOGGER.error("Error deleting broadcast slots for radio station {} after {}", radioStationId, startTime, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update broadcast slot status",
            description = "Updates the status of a broadcast slot by its ID. Valid statuses are defined in the Status enumeration."
    )
    public ResponseEntity<?> updateBroadcastSlotStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Status newStatus;
            try {
                newStatus = Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ex) {
                LOGGER.warn("Invalid status provided: {}", status);
                return ResponseEntity.badRequest().body("Invalid status. Allowed values: " + java.util.Arrays.toString(Status.values()));
            }
            LOGGER.info("Updating broadcast slot status for ID: {}", id);
            BroadcastSlotDto updated = broadcastSlotService.updateBroadcastSlotStatus(id, newStatus);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            }

            LOGGER.warn("Broadcast slot with ID {} not found for status update", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.error("Error updating broadcast slot status for ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/end-time")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update broadcast slot end time",
            description = "Updates the end time of a broadcast slot by its ID."
    )
    public ResponseEntity<?> updateBroadcastSlotEndTime(@PathVariable Long id,
                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            LOGGER.info("Updating broadcast slot end time for ID: {}", id);
            BroadcastSlotDto updated = broadcastSlotService.updateBroadcastSlotEndTime(id, endTime);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            }
            LOGGER.warn("Broadcast slot with ID {} not found for end time update", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.error("Error updating broadcast slot end time for ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/split")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Split broadcast slot",
            description = "Splits an existing broadcast slot at the specified new end time. " +
                    "The original slot's end time is updated and a new slot is created for the remaining period."
    )
    public ResponseEntity<?> splitBroadcastSlot(@RequestParam Long id,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newEndTime) {
        try {
            LOGGER.info("Splitting broadcast slot with ID: {} at new end time: {}", id, newEndTime);
            BroadcastSlotDto updatedSlot = broadcastSlotService.splitBroadcastSlot(id, newEndTime);
            if (updatedSlot != null) {
                return ResponseEntity.ok(updatedSlot);
            }

            LOGGER.warn("Broadcast slot with ID {} not found for splitting", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.error("Error splitting broadcast slot with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-month")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get broadcast slots by month",
            description = "Retrieves broadcast slots for a specific year and month."
    )
    public ResponseEntity<?> getBroadcastSlotsByMonth(@RequestParam int year, @RequestParam int month) {
        try {
            LOGGER.info("Fetching broadcast slots for year: {} and month: {}", year, month);
            List<BroadcastSlotDto> slots = broadcastSlotService.getBroadcastSlotsByMonth(year, month);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            LOGGER.error("Error fetching broadcast slots for year {} and month {}", year, month, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
