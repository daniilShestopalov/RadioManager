package cs.vsu.radiomanager.controller.api;

import cs.vsu.radiomanager.dto.AudioRecordingDto;
import cs.vsu.radiomanager.model.enumerate.ApprovalStatus;
import cs.vsu.radiomanager.model.enumerate.Role;
import cs.vsu.radiomanager.security.JwtFilter;
import cs.vsu.radiomanager.service.AudioRecordingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audio-recording")
@AllArgsConstructor
public class AudioRecordingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudioRecordingController.class);

    private final AudioRecordingService audioRecordingService;

    private final JwtFilter jwtFilter;

    private boolean isNotAuthorizedToAccessRecordings(Long userIdFromToken, Role role, Long requestedUserId) {
        return !userIdFromToken.equals(requestedUserId) && !Role.ADMIN.equals(role);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all audio recordings",
            description = "Returns a list of all audio recordings."
    )
    public ResponseEntity<List<AudioRecordingDto>> getAllAudioRecordings() {
        try {
            LOGGER.info("Fetching all audio recordings");
            List<AudioRecordingDto> audioRecordings = audioRecordingService.getAllRecordings();
            return ResponseEntity.ok(audioRecordings);

        } catch (Exception e) {
            LOGGER.error("Error fetching all audio recordings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get audio recording by ID",
            description = "Returns the audio recording corresponding to the given ID."
    )
    public ResponseEntity<?> getAudioRecordingById(@PathVariable Long id) {
        try {
            AudioRecordingDto recording = audioRecordingService.getRecordingById(id);
            if (recording != null) {
                LOGGER.info("Fetched recording with ID {}", id);
                return ResponseEntity.ok(recording);
            } else {
                LOGGER.warn("Recording with ID {} not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recording not found.");
            }
        } catch (Exception e) {
            LOGGER.error("Error fetching recording with ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get audio recordings by user ID",
            description = "Returns all audio recordings for a specific user."
    )
    public ResponseEntity<?> getRecordingsByUserId(@PathVariable Long userId, HttpServletRequest request) {
        try {
            Long tokenUserId = jwtFilter.getUserId(request);
            Role role = jwtFilter.getRole(request);

            if (isNotAuthorizedToAccessRecordings(tokenUserId, role, userId)) {
                LOGGER.warn("Unauthorized access: token user ID {} does not match requested user ID {} and role is {}",
                        tokenUserId, userId, role.toString());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You are not authorized to access recordings for this user.");
            }

            List<AudioRecordingDto> recordings = audioRecordingService.getRecordingByUserId(userId);
            LOGGER.info("Fetched {} recordings for user {}", recordings.size(), userId);
            return ResponseEntity.ok(recordings);
        } catch (Exception e) {
            LOGGER.error("Error fetching recordings for user {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get audio recordings by approval status",
            description = "Returns all audio recordings filtered by the given approval status (APPROVED, PENDING, REJECTED)."
    )
    public ResponseEntity<?> getRecordingsByStatus(@PathVariable String status) {
        try {
            ApprovalStatus approvalStatus;
            try {
                //TODO определиться во фронте с этим
                approvalStatus = ApprovalStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ex) {
                LOGGER.warn("Invalid approval status: {}", status);
                return ResponseEntity.badRequest().body("Invalid approval status. Valid values: APPROVED, PENDING, REJECTED.");
            }
            List<AudioRecordingDto> recordings = audioRecordingService.getRecordingByStatus(approvalStatus);
            LOGGER.info("Fetched {} recordings with status {}", recordings.size(), approvalStatus);
            return ResponseEntity.ok(recordings);
        } catch (Exception e) {
            LOGGER.error("Error fetching recordings with status {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status_user")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get audio recordings by approval status and user ID",
            description = "Returns audio recordings filtered by approval status and user ID extracted from request headers. Headers: 'approvalStatus' and 'userId'."
    )
    public ResponseEntity<?> getRecordingsByStatusAndUserId(
            @RequestHeader("approvalStatus") String status,
            @RequestHeader("userId") Long userId,
            HttpServletRequest request) {
        try {
            Long tokenUserId = jwtFilter.getUserId(request);
            Role tokenRole = jwtFilter.getRole(request);

            if (isNotAuthorizedToAccessRecordings(tokenUserId, tokenRole, userId)) {
                LOGGER.warn("Unauthorized access: token user ID {} with role {} is not allowed to access recordings for user {}",
                        tokenUserId, tokenRole, userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You are not authorized to access recordings for this user.");
            }

            ApprovalStatus approvalStatus;
            try {
                approvalStatus = ApprovalStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ex) {
                LOGGER.warn("Invalid approval status : {}, with user id {}", status, userId);
                return ResponseEntity.badRequest().body("Invalid approval status. Valid values: APPROVED, PENDING, REJECTED.");
            }
            List<AudioRecordingDto> recordings = audioRecordingService.getRecordingByStatusAndUserId(approvalStatus, userId);
            LOGGER.info("Fetched {} recordings with status {} for user {}", recordings.size(), approvalStatus, userId);
            return ResponseEntity.ok(recordings);
        } catch (Exception e) {
            LOGGER.error("Error fetching recordings with status {} for user {}", status, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADVERTISER', 'ADMIN')")
    @Operation(
            summary = "Create new audio recording",
            description = "Creates a new audio recording. Only data is accepted here; file operations are handled separately."
    )
    public ResponseEntity<?> createRecording(@RequestBody @Valid AudioRecordingDto recordingDto) {
        try {
            LOGGER.info("Created audio recording: {}", recordingDto);
            AudioRecordingDto created = audioRecordingService.createRecording(recordingDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            LOGGER.error("Error creating audio recording", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update audio recording",
            description = "Updates an existing audio recording."
    )
    public ResponseEntity<?> updateRecording(@RequestBody @Valid AudioRecordingDto recordingDto) {
        try {
            LOGGER.info("Updated audio recording: {}", recordingDto);
            AudioRecordingDto updated = audioRecordingService.updateRecording(recordingDto);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            }
            LOGGER.warn("Audio recording with ID {} not found for update", recordingDto.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (Exception e) {
            LOGGER.error("Error updating audio recording: {}", recordingDto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADVERTISER', 'ADMIN')")
    @Operation(
            summary = "Delete audio recording",
            description = "Deletes an audio recording by its ID."
    )
    public ResponseEntity<?> deleteRecording(@PathVariable Long id) {
        try {
            LOGGER.info("Deleted audio recording with ID: {}", id);
            boolean deleted = audioRecordingService.deleteRecording(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            }
            LOGGER.warn("Audio recording with ID {} not found for delete", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (Exception e) {
            LOGGER.error("Error deleting audio recording with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
