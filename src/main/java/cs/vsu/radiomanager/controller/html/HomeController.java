package cs.vsu.radiomanager.controller.html;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@ConditionalOnProperty(
        name = "old.api.enabled",
        havingValue = "true",
        matchIfMissing = false
)
@Controller
@AllArgsConstructor
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/user/home")
    @PreAuthorize("hasRole('ADVERTISER')")
    public String userHomePage() {
        LOGGER.info("Navigating to user home page");
        return "user-home";
    }

}
