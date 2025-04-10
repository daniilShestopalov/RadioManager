package cs.vsu.radiomanager.controller.api;

import cs.vsu.radiomanager.dto.RadioStationDto;
import cs.vsu.radiomanager.model.RadioStation;
import cs.vsu.radiomanager.service.RadioStationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/radio-station")
@AllArgsConstructor
public class RadioStationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RadioStationController.class);

    private final RadioStationService radioStationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get all radio stations",
            description = "Retrieves a list of all radio stations."
    )
    public ResponseEntity<?> getAllRadioStations() {
        try {
            LOGGER.info("Fetching all radio stations");
            List<RadioStation> stations = radioStationService.getAllRadioStation();
            return ResponseEntity.ok(stations);
        } catch (Exception e) {
            LOGGER.error("Error fetching all radio stations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get radio station by ID",
            description = "Retrieves a radio station by its ID."
    )
    public ResponseEntity<?> getRadioStationById(@PathVariable Long id) {
        try {
            LOGGER.info("Fetching radio station with ID: {}", id);
            RadioStationDto station = radioStationService.getRadioStationById(id);
            if (station != null) {
                return ResponseEntity.ok(station);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Radio station not found.");
        } catch (Exception e) {
            LOGGER.error("Error fetching radio station with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
