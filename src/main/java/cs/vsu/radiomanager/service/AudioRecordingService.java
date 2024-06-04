package cs.vsu.radiomanager.service;

import cs.vsu.radiomanager.dto.AudioRecordingDto;
import cs.vsu.radiomanager.mapper.AudioRecordingMapper;
import cs.vsu.radiomanager.model.AudioRecording;
import cs.vsu.radiomanager.model.enumerate.ApprovalStatus;
import cs.vsu.radiomanager.repository.AudioRecordingRep;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AudioRecordingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudioRecordingService.class);

    private AudioRecordingRep audioRecordingRep;

    private AudioRecordingMapper mapper;

    public List<AudioRecordingDto> getAllRecordings() {
        LOGGER.debug("Fetching all recordings");
        return mapper.toDtoList(audioRecordingRep.findAll());
    }

    public AudioRecordingDto getRecordingById(Long id) {
        LOGGER.debug("Fetching recording {}", id);
        return audioRecordingRep.findById(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    public List<AudioRecordingDto> getRecordingByUserId(Long userId) {
        LOGGER.debug("Fetching recordings by user {}", userId);
        return mapper.toDtoList(audioRecordingRep.findByUserId(userId));
    }

    public List<AudioRecordingDto> getRecordingByStatus(ApprovalStatus status) {
        LOGGER.debug("Fetching recordings by status {}", status);
        return mapper.toDtoList(audioRecordingRep.findByApprovalStatus(status));
    }

    public List<AudioRecordingDto> getRecordingByStatusAndUserId(ApprovalStatus status, Long userId) {
        LOGGER.debug("Fetching recordings by status {} and user {}", status, userId);
        return mapper.toDtoList(audioRecordingRep.findByApprovalStatusAndUserId(status, userId));
    }

    public AudioRecordingDto createRecording(AudioRecordingDto recordingDto) {
        LOGGER.debug("Creating recording {}", recordingDto);
        try {
            AudioRecording audioRecording = audioRecordingRep.save(mapper.toEntity(recordingDto));
            LOGGER.debug("Recording created with id {}", audioRecording.getId());
            return mapper.toDto(audioRecording);
        } catch (Exception e) {
            LOGGER.error("Error creating recording {}", recordingDto, e);
            throw new RuntimeException("Error creating recording: " + recordingDto, e);
        }
    }

    public AudioRecordingDto updateRecording(AudioRecordingDto recordingDto) {
        LOGGER.debug("Updating recording {}", recordingDto);
        try {
            Optional<AudioRecording> optionalAudioRecording = audioRecordingRep.findById(recordingDto.getId());
            if (optionalAudioRecording.isPresent()) {
                AudioRecording audioRecording = optionalAudioRecording.get();
                mapper.updateEntityFromDto(recordingDto, audioRecording);
                LOGGER.debug("Recording updated with id {}", audioRecording.getId());
                return mapper.toDto(audioRecording);
            }
            LOGGER.warn("Recording with id for update {} not found", recordingDto.getId());
            return null;
        } catch (Exception e) {
            LOGGER.error("Error updating recording {}", recordingDto, e);
            throw new RuntimeException("Error updating recording: " + recordingDto, e);
        }
    }

    public AudioRecordingDto deleteRecording(Long id) {
        LOGGER.debug("Deleting recording {}", id);
        try {
            Optional<AudioRecording> optionalAudioRecording = audioRecordingRep.findById(id);
            if (optionalAudioRecording.isPresent()) {
                AudioRecording audioRecording = optionalAudioRecording.get();
                audioRecordingRep.delete(audioRecording);
                LOGGER.debug("Recording deleted with id {}", audioRecording.getId());
                return mapper.toDto(audioRecording);
            }
            LOGGER.warn("Recording with id for delete {} not found", id);
            return null;
        } catch (Exception e) {
            LOGGER.error("Error deleting recording {}", id, e);
            throw new RuntimeException("Error deleting recording: " + id, e);
        }
    }

    public AudioRecordingDto updateRecordingStatus(Long id, ApprovalStatus approvalStatus) {
        LOGGER.debug("Updating recording status {}", id);
        try {
            Optional<AudioRecording> optionalAudioRecording = audioRecordingRep.findById(id);
            if (optionalAudioRecording.isPresent()) {
                AudioRecording audioRecording = optionalAudioRecording.get();
                audioRecording.setApprovalStatus(approvalStatus);
                AudioRecording updatedAudioRecording = audioRecordingRep.save(audioRecording);
                LOGGER.debug("Recording status updated with id {}", updatedAudioRecording.getId());
                return mapper.toDto(updatedAudioRecording);
            }
            LOGGER.warn("Recording status with id for update {} not found", id);
            return null;
        } catch (Exception e) {
            LOGGER.error("Error updating recording status {}", id, e);
            throw new RuntimeException("Error updating recording status: " + id, e);
        }
    }
}
