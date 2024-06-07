package cs.vsu.radiomanager.controller.html;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/user/home")
    @PreAuthorize("hasRole('USER')")
    public String userHomePage() {
        LOGGER.info("Navigating to user home page");
        return "user-home";
    }

}
