package cs.vsu.radiomanager.controller.html;

import cs.vsu.radiomanager.dto.AudioRecordingDto;
import cs.vsu.radiomanager.dto.BroadcastSlotDto;
import cs.vsu.radiomanager.dto.PlacementDto;
import cs.vsu.radiomanager.dto.UserDto;
import cs.vsu.radiomanager.model.enumerate.ApprovalStatus;
import cs.vsu.radiomanager.model.enumerate.Status;
import cs.vsu.radiomanager.security.JwtFilter;
import cs.vsu.radiomanager.service.AudioRecordingService;
import cs.vsu.radiomanager.service.BroadcastSlotService;
import cs.vsu.radiomanager.service.PlacementService;
import cs.vsu.radiomanager.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/details")
@AllArgsConstructor
public class BroadcastDetailsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BroadcastDetailsController.class);

    private final AudioRecordingService audioRecordingService;

    private final BroadcastSlotService broadcastSlotService;

    private final PlacementService placementService;

    private final UserService userService;

    private final JwtFilter jwtFilter;

    @GetMapping("/{slotId}")
    @PreAuthorize("isAuthenticated()")
    public String getBroadcastSlotDetails(@PathVariable Long slotId, Model model, HttpServletRequest request) {

        Long userId = jwtFilter.getUserId(request);
        LOGGER.info("Fetching broadcast slot details with id: {} for user with id: {}", slotId, userId);

        Map<String, String> statusMap = new HashMap<>();
        statusMap.put(Status.AVAILABLE.name(), "Доступно");
        statusMap.put(Status.OCCUPIED.name(), "Занято");
        model.addAttribute("statusMap", statusMap);

        BroadcastSlotDto broadcastSlot = broadcastSlotService.getBroadcastSlotById(slotId);
        if (broadcastSlot.getStatus().equals(Status.OCCUPIED)) {
            //TODO
            return "occupied-details";
        }

        List<AudioRecordingDto> availableAudios = audioRecordingService.getRecordingByStatusAndUserId(ApprovalStatus.APPROVED, userId);

        model.addAttribute("broadcastSlot", broadcastSlot);
        model.addAttribute("availableAudios", availableAudios);
        model.addAttribute("userId", userId);

        LOGGER.debug("Broadcast slot details: {}", broadcastSlot);
        LOGGER.debug("Available audio recordings: {}", availableAudios);

        return "details";
    }

    @PostMapping("/attach-audio")
    @PreAuthorize("isAuthenticated()")
    public String attachAudioToSlot(@RequestParam Long slotId, @RequestParam Long audioId, @RequestParam Long userId, Model model) {
        LOGGER.info("Attaching audio recording with id: {} to broadcast slot with id: {} for user with id: {}", audioId, slotId, userId);

        BroadcastSlotDto broadcastSlot = broadcastSlotService.getBroadcastSlotById(slotId);
        AudioRecordingDto audioRecording = audioRecordingService.getRecordingById(audioId);
        UserDto user = userService.getUserById(userId);

        Double cost = audioRecording.getCost();
        Double balance = user.getBalance();

        if (balance >= cost) {
            LOGGER.info("Sufficient funds available. Updating user balance.");
            userService.updateBalance(userId, balance - cost);
        } else {
            LOGGER.warn("Insufficient funds for attaching audio recording. Current balance: {}, cost of audio recording: {}", balance, cost);
            model.addAttribute("error", "Insufficient funds for attaching audio recording.");
            return "details";
        }

        LocalDateTime newEndTime = broadcastSlot
                .getStartTime()
                .plus(Duration.ofSeconds(Math.round(audioRecording.getDuration())));
        LOGGER.debug("New end time for broadcast slot: {}", newEndTime);

        BroadcastSlotDto updatedSlot = broadcastSlotService.splitBroadcastSlot(slotId, newEndTime);

        if (updatedSlot != null) {
            PlacementDto placementDto = new PlacementDto();
            placementDto.setBroadcastSlotId(updatedSlot.getId());
            placementDto.setAudioRecordingId(audioId);
            placementDto.setPlacementDate(LocalDateTime.now());

            placementService.createPlacement(placementDto);
            LOGGER.info("Audio recording successfully attached to broadcast slot. Created new slot with id: {}", updatedSlot.getId());
            return "redirect:/user/broadcast-slots";
        } else {
            LOGGER.error("Error splitting broadcast slot.");
            model.addAttribute("error", "Error splitting broadcast slot.");
            return "details";
        }
    }

}
