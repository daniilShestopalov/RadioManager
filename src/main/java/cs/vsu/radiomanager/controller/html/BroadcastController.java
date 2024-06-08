package cs.vsu.radiomanager.controller.html;

import cs.vsu.radiomanager.dto.BroadcastSlotDto;
import cs.vsu.radiomanager.model.enumerate.Role;
import cs.vsu.radiomanager.model.enumerate.Status;
import cs.vsu.radiomanager.service.BroadcastSlotService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class BroadcastController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BroadcastController.class);

    private final BroadcastSlotService broadcastSlotService;

    @GetMapping("/user/broadcast-slots")
    @PreAuthorize("hasRole('USER')")
    public String broadcastSlotsPage(Model model) {

        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        List<BroadcastSlotDto> broadcastSlotList = broadcastSlotService.getBroadcastSlotsByMonth(year, month);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        Map<String, List<BroadcastSlotDto>> slotsByDay = broadcastSlotList.stream()
                .collect(Collectors.groupingBy(slot -> {
                    LocalDate startDate = slot.getStartTime().toLocalDate();
                    String dayOfWeek = startDate.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault());
                    int weekOfMonth = startDate.get(weekFields.weekOfMonth());
                    return dayOfWeek + ", " + startDate.format(dateFormatter) + " (неделя " + weekOfMonth + ")";
                }));

        Map<String, String> statusMap = new HashMap<>();
        statusMap.put(Status.AVAILABLE.name(), "Доступно");
        statusMap.put(Status.OCCUPIED.name(), "Занято");
        model.addAttribute("statusMap", statusMap);

        model.addAttribute("slotsByDay", slotsByDay);
        return "user-broadcast-slots";
    }

}
