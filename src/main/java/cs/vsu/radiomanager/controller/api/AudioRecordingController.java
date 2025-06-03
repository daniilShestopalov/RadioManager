package cs.vsu.radiomanager.controller.api;

import cs.vsu.radiomanager.dto.AudioRecordingDto;
import cs.vsu.radiomanager.dto.CombinedAudioRecordingResponseDto;
import cs.vsu.radiomanager.model.enumerate.ApprovalStatus;
import cs.vsu.radiomanager.model.enumerate.Role;
import cs.vsu.radiomanager.security.JwtFilter;
import cs.vsu.radiomanager.service.AudioRecordingService;
import cs.vsu.radiomanager.service.FileService;
import cs.vsu.radiomanager.util.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/audio-recording")
@AllArgsConstructor
public class AudioRecordingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudioRecordingController.class);

    private final AudioRecordingService audioRecordingService;

    private final FileService fileService;

    private final JwtFilter jwtFilter;

    private boolean isNotAuthorizedToAccessRecordings(Long userIdFromToken, Role role, Long requestedUserId) {
        return !userIdFromToken.equals(requestedUserId) && !Role.ADMIN.equals(role);
    }

    private List<CombinedAudioRecordingResponseDto> formCombinedList(List<AudioRecordingDto> audioRecordings) {
        List<CombinedAudioRecordingResponseDto> responseList = new ArrayList<>(audioRecordings.size());
        for (AudioRecordingDto recording : audioRecordings) {
            String uniqueFileName = fileService.generateUniqueFilename(recording.getId(), recording.getFilePath());
            byte[] fileData = fileService.getAudio(uniqueFileName);
            String fileContentBase64 = Base64.getEncoder().encodeToString(fileData);

            CombinedAudioRecordingResponseDto dto = new CombinedAudioRecordingResponseDto();
            dto.setRecording(recording);
            dto.setFileContentBase64(fileContentBase64);

            responseList.add(dto);
        }
        return responseList;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all audio recordings",
            description = "Returns a list of all audio recordings."
    )
    public ResponseEntity<?> getAllAudioRecordings() {
        try {
            LOGGER.info("Fetching all audio recordings");
            List<AudioRecordingDto> audioRecordings = audioRecordingService.getAllRecordings();

            return ResponseEntity.ok(formCombinedList(audioRecordings));

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
            if (recording == null) {
                LOGGER.warn("Recording with ID {} not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String uniqueFileName = fileService.generateUniqueFilename(id, recording.getFilePath());
            byte[] fileData = fileService.getAudio(uniqueFileName);
            String fileContentBase64 = Base64.getEncoder().encodeToString(fileData);

            CombinedAudioRecordingResponseDto responseDto = new CombinedAudioRecordingResponseDto();
            responseDto.setFileContentBase64(fileContentBase64);
            responseDto.setRecording(recording);

            LOGGER.info("Fetched recording with ID {} and file content", id);
            return ResponseEntity.ok(responseDto);

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
            return ResponseEntity.ok(formCombinedList(recordings));
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
                approvalStatus = ApprovalStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ex) {
                LOGGER.warn("Invalid approval status: {}", status);
                return ResponseEntity.badRequest().body("Invalid approval status. Valid values: APPROVED, PENDING, REJECTED.");
            }
            List<AudioRecordingDto> recordings = audioRecordingService.getRecordingByStatus(approvalStatus);
            LOGGER.info("Fetched {} recordings with status {}", recordings.size(), approvalStatus);
            return ResponseEntity.ok(formCombinedList(recordings));
        } catch (Exception e) {
            LOGGER.error("Error fetching recordings with status {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status_user")
    @PreAuthorize("isAuthenticated()")
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
            return ResponseEntity.ok(formCombinedList(recordings));
        } catch (Exception e) {
            LOGGER.error("Error fetching recordings with status {} for user {}", status, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADVERTISER', 'ADMIN')")
    @Operation(
            summary = "Create new audio recording",
            description = "Uploads an audio file, calculates its duration and cost, creates a new" +
                    " audio recording in the database, and saves the physical file with a unique filename" +
                    " based on the generated audio recording ID."
    )
    public ResponseEntity<?> createRecording(@RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        try {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            Long userId = jwtFilter.getUserId(request);
            LOGGER.info("User {} is uploading file: {}", userId, originalFilename);
            LOGGER.info("Type is {}", file.getContentType());

            Double duration = fileService.getAudioDuration(file);
            Double cost = audioRecordingService.getCostByDuration(duration);

            AudioRecordingDto recordingDto = new AudioRecordingDto();
            recordingDto.setUserId(userId);
            recordingDto.setFilePath(originalFilename);
            recordingDto.setDuration(duration);
            recordingDto.setCost(cost);
            recordingDto.setApprovalStatus(ApprovalStatus.PENDING);
            AudioRecordingDto created = audioRecordingService.createRecording(recordingDto);
            LOGGER.info("Created audio recording: {}", created);

            String uniqueFilename = fileService.saveAudio(file, created.getId());
            LOGGER.info("Unique file saved: {}", uniqueFilename);

            CombinedAudioRecordingResponseDto result = new CombinedAudioRecordingResponseDto();
            result.setRecording(created);
            result.setFileContentBase64(Base64.getEncoder().encodeToString(file.getBytes()));

            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Invalid filename during file upload: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
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
            AudioRecordingDto deleted = audioRecordingService.deleteRecording(id);
            if (deleted != null) {
                String uniqueFilename = fileService.generateUniqueFilename(deleted.getId(), deleted.getFilePath());

                if (fileService.deleteAudio(uniqueFilename)) {
                    LOGGER.info("Audio recording with ID {} and file {} deleted successfully", id, uniqueFilename);
                    return ResponseEntity.ok().build();
                } else {
                    LOGGER.warn("Audio recording with ID {} was deleted from DB but file {} was not found for deletion", id, uniqueFilename);
                    return ResponseEntity.ok("Recording deleted, but file not found.");
                }
            }
            LOGGER.warn("Audio recording with ID {} not found for delete", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (Exception e) {
            LOGGER.error("Error deleting audio recording with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update audio recording status",
            description = "Updates the approval status of an audio recording by its ID. Valid statuses: APPROVED, PENDING, REJECTED."
    )
    public ResponseEntity<?> updateRecordingStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            ApprovalStatus newStatus;
            try {
                newStatus = ApprovalStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ex) {
                LOGGER.warn("Invalid status provided: {}", status);
                return ResponseEntity.badRequest().body("Invalid status. Allowed values: APPROVED, PENDING, REJECTED.");
            }
            LOGGER.info("Updating audio recording with ID: {}", id);
            AudioRecordingDto updated = audioRecordingService.updateRecordingStatus(id, newStatus);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            }
            LOGGER.warn("Audio recording with ID {} not found for update status", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (Exception e) {
            LOGGER.error("Error updating audio recording status with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Download audio file",
            description = "Downloads the actual MP3 file for the given audio recording ID."
    )
    public ResponseEntity<Resource> downloadRecording(@PathVariable Long id) {
        try {
            LOGGER.info("Downloading audio file for recording ID: {}", id);
            AudioRecordingDto recording = audioRecordingService.getRecordingById(id);

            if (recording == null) {
                LOGGER.warn("Audio recording with ID {} not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String uniqueFilename = fileService.generateUniqueFilename(id, recording.getFilePath());
            LOGGER.debug("Resolved unique filename: {}", uniqueFilename);

            byte[] fileData = fileService.getAudio(uniqueFilename);
            LOGGER.info("Read {} bytes for file {}", fileData.length, uniqueFilename);

            ResponseEntity<Resource> response = FileUtils.getFileResponse(fileData, uniqueFilename);
            LOGGER.info("Returning file response for recording ID: {}", id);
            return response;
        } catch (Exception e) {
            LOGGER.error("Error downloading audio recording with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/approval_statuses")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get available approval statuses for audio recording",
            description = "Retrieves a list of all available approval statuses for audio recordings." +
                    " This is useful for populating status selection dropdowns in the UI," +
                    " allowing users to easily select or view the current status of a recording."
    )
    public ResponseEntity<?> getApprovalStatuses() {
        try {
            LOGGER.info("Fetching available approval statuses for audio");
            List<ApprovalStatus> statuses = Arrays.asList(ApprovalStatus.values());
            return ResponseEntity.ok(statuses);
        } catch (Exception e) {
            LOGGER.error("Error fetching approval statuses roles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
