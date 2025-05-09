package cs.vsu.radiomanager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "base")
public class BaseProperties {

    @Data
    public static class TimeWindow {
        private LocalTime start;
        private LocalTime end;
    }

    private Double filePrice;

    private Double priorityMultiplier;

    private List<TimeWindow> priorityHigh;

    private long minSlotDuration;

}
