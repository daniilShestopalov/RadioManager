package cs.vsu.radiomanager.config;

import cs.vsu.radiomanager.dto.UserDto;
import cs.vsu.radiomanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AdminInitConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(AdminInitConfig.class);

    @Value("${admin.initial.login}")
    private String adminLogin;

    @Value("${admin.initial.password}")
    private String adminPassword;

    @Value("${admin.initial.name}")
    private String adminName;

    @Value("${admin.initial.surname}")
    private String adminSurname;

    @Value("${admin.initial.balance}")
    private Double adminBalance;

    @Bean
    public CommandLineRunner createInitialAdmin(AuthService authService) {
        return args -> {
            if (!authService.checkEmailExists(adminLogin)) {
                UserDto admin = new UserDto();
                admin.setLogin(adminLogin);
                admin.setPassword(adminPassword);
                admin.setName(adminName);
                admin.setSurname(adminSurname);
                admin.setBalance(adminBalance);
                authService.registerUser(admin);
                LOGGER.info("Initial admin user created successfully.");
            } else {
                LOGGER.info("Admin user already exists.");
            }
        };
    }

}
