package cs.vsu.radiomanager.controller.html;

import cs.vsu.radiomanager.dto.AudioRecordingDto;
import cs.vsu.radiomanager.model.enumerate.ApprovalStatus;
import cs.vsu.radiomanager.security.JwtFilter;
import cs.vsu.radiomanager.service.AudioRecordingService;
import cs.vsu.radiomanager.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class AudioController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudioController.class);

    private final AudioRecordingService audioRecordingService;

    private final FileService fileService;

    private final JwtFilter jwtFilter;

    @GetMapping("/user/audio")
    @PreAuthorize("hasRole('USER')")
    public String audioPage(HttpServletRequest request, Model model) {

        Long userId = jwtFilter.getUserId(request);

        List<AudioRecordingDto> audioRecordingDtoList = audioRecordingService.getRecordingByUserId(userId);

        Map<String, String> statusMap = new HashMap<>();
        statusMap.put(ApprovalStatus.APPROVED.name(), "Одобрено");
        statusMap.put(ApprovalStatus.PENDING.name(), "В ожидании");
        statusMap.put(ApprovalStatus.REJECTED.name(), "Отклонено");
        model.addAttribute("statusMap", statusMap);

        LOGGER.info("Audio page accessed for user: {}", userId);
        model.addAttribute("audioList", audioRecordingDtoList);
        return "user-audio";
    }

    @GetMapping("/user/audio/{filename}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public byte[] getAudio(@PathVariable String filename) {
        LOGGER.info("Audio file requested: {}", filename);
        return fileService.getAudio(filename);
    }

    @PostMapping("/user/audio/upload")
    @PreAuthorize("hasRole('USER')")
    public String uploadAudio(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        Long userId = jwtFilter.getUserId(request);
        LOGGER.info("Type is {}", file.getContentType());
        String filePath = fileService.saveAudio(file);
        Double duration = fileService.getAudioDuration(file);
        AudioRecordingDto audioRecordingDto = new AudioRecordingDto();
        audioRecordingDto.setUserId(userId);
        audioRecordingDto.setFilePath(filePath);
        audioRecordingDto.setDuration(fileService.getAudioDuration(file));
        audioRecordingDto.setCost(audioRecordingService.getCostByDuration(duration));
        audioRecordingDto.setApprovalStatus(ApprovalStatus.PENDING);
        audioRecordingService.createRecording(audioRecordingDto);

        LOGGER.info("Audio file uploaded by user {}: {}", userId, filePath);
        return "redirect:/user/audio";
    }

    @PostMapping("/user/audio/delete/{id}")
    @PreAuthorize("hasRole('USER')")
    public String deleteAudio(@PathVariable Long id, HttpServletRequest request) {
        Long userId = jwtFilter.getUserId(request);
        AudioRecordingDto audio = audioRecordingService.getRecordingById(id);

        if (audio != null && audio.getUserId().equals(userId)) {
            fileService.deleteAudio(audio.getFilePath());
            audioRecordingService.deleteRecording(id);
            LOGGER.info("Audio recording with ID {} deleted by user {}", id, userId);
        } else {
            LOGGER.warn("Failed attempt to delete audio recording with ID {} by user {}", id, userId);
        }

        return "redirect:/user/audio";
    }

}
