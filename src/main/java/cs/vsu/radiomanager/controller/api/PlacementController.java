package cs.vsu.radiomanager.controller.api;

import cs.vsu.radiomanager.dto.PlacementDto;
import cs.vsu.radiomanager.service.PlacementService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/placement")
@AllArgsConstructor
public class PlacementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlacementController.class);

    private final PlacementService placementService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all placements", description = "Retrieves a list of all placements.")
    public ResponseEntity<List<PlacementDto>> getAllPlacements() {
        try {
            LOGGER.info("Fetching all placements");
            List<PlacementDto> placements = placementService.getAll();
            return ResponseEntity.ok(placements);
        } catch (Exception e) {
            LOGGER.error("Error fetching all placements", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get placement by ID", description = "Retrieves a placement by its ID.")
    public ResponseEntity<?> getPlacementById(@PathVariable Long id) {
        try {
            LOGGER.info("Fetching placement with ID: {}", id);
            PlacementDto placement = placementService.getById(id);
            if (placement != null) {
                return ResponseEntity.ok(placement);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.error("Error fetching placement with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-date")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get placements by date", description = "Retrieves placements for a given placement date.")
    public ResponseEntity<?> getPlacementsByDate(@RequestParam("date")
                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        try {
            LOGGER.info("Fetching placements for date: {}", date);
            List<PlacementDto> placements = placementService.getByPlacementDate(date);
            return ResponseEntity.ok(placements);
        } catch (Exception e) {
            LOGGER.error("Error fetching placements for date: {}", date, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-audio-recording/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get placements by audio recording ID", description = "Retrieves placements associated with a given audio recording ID.")
    public ResponseEntity<?> getPlacementsByAudioRecordingId(@PathVariable Long id) {
        try {
            LOGGER.info("Fetching placements for audio recording ID: {}", id);
            List<PlacementDto> placements = placementService.getByAudioRecordingId(id);
            return ResponseEntity.ok(placements);
        } catch (Exception e) {
            LOGGER.error("Error fetching placements for audio recording ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-broadcast-slot/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get placement by broadcast slot ID", description = "Retrieves a placement by its broadcast slot ID.")
    public ResponseEntity<?> getPlacementByBroadcastSlotId(@PathVariable Long id) {
        try {
            LOGGER.info("Fetching placement for broadcast slot ID: {}", id);
            PlacementDto placement = placementService.getByBroadcastSlotId(id);
            if (placement != null) {
                return ResponseEntity.ok(placement);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.error("Error fetching placement for broadcast slot ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADVERTISER', 'ADMIN')")
    @Operation(summary = "Create new placement", description = "Creates a new placement.")
    public ResponseEntity<?> createPlacement(@RequestBody PlacementDto placementDto) {
        try {
            LOGGER.info("Creating placement: {}", placementDto);
            PlacementDto created = placementService.createPlacement(placementDto);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            LOGGER.error("Error creating placement: {}", placementDto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADVERTISER', 'ADMIN')")
    @Operation(summary = "Update placement", description = "Updates an existing placement.")
    public ResponseEntity<?> updatePlacement(@RequestBody PlacementDto placementDto) {
        try {
            LOGGER.info("Updating placement: {}", placementDto);
            PlacementDto updated = placementService.updatePlacement(placementDto);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            }

            LOGGER.warn("Placement with ID {} not found for update", placementDto.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.error("Error updating placement: {}", placementDto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADVERTISER', 'ADMIN')")
    @Operation(summary = "Delete placement", description = "Deletes a placement by its ID.")
    public ResponseEntity<?> deletePlacement(@PathVariable Long id) {
        try {
            LOGGER.info("Deleting placement with ID: {}", id);
            boolean deleted = placementService.deletePlacement(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            }

            LOGGER.warn("Placement with ID {} not found for delete", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.error("Error deleting placement with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
