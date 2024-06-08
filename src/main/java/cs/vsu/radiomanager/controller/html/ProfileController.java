package cs.vsu.radiomanager.controller.html;

import cs.vsu.radiomanager.dto.TransactionDto;
import cs.vsu.radiomanager.dto.UserDto;
import cs.vsu.radiomanager.security.JwtFilter;
import cs.vsu.radiomanager.security.JwtProvider;
import cs.vsu.radiomanager.service.TransactionService;
import cs.vsu.radiomanager.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class ProfileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileController.class);

    private final UserService userService;

    private final TransactionService transactionService;

    private final JwtProvider jwtProvider;

    private final JwtFilter jwtFilter;

    @GetMapping("/user/profile")
    @PreAuthorize("hasRole('USER')")
    public String userProfilePage(HttpServletRequest request, Model model) {

        Long userId = jwtFilter.getUserId(request);

        UserDto userDto = userService.getUserById(userId);

        if (userDto != null) {
            model.addAttribute("user", userDto);
            LOGGER.info("Profile page accessed for user: {}", userId);
            return "user-profile";
        } else {
            LOGGER.warn("User not found for profile page: {}", userId);
            return "redirect:/login";
        }
    }

    @GetMapping("/user/transactions")
    @PreAuthorize("hasRole('USER')")
    public String userTransactionsPage(HttpServletRequest request, Model model) {

        Long userId = jwtFilter.getUserId(request);

        List<TransactionDto> transactions = transactionService.getTransactionsByUserId(userId);

        model.addAttribute("transactions", transactions);
        LOGGER.info("Transaction page accessed for user: {}", userId);
        return "user-transactions";
    }

}
