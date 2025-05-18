package cs.vsu.radiomanager.config;

import cs.vsu.radiomanager.dto.UserDto;
import cs.vsu.radiomanager.model.enumerate.Role;
import cs.vsu.radiomanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SystemEntityConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(SystemEntityConfig.class);

    private final SystemEntityProperties systemEntityProperties;

    @Bean
    public CommandLineRunner createSystemEntity(AuthService authService) {
        return args -> {
            if (!authService.checkEmailExists(systemEntityProperties.getLogin())) {
                UserDto systemEntity = new UserDto();
                systemEntity.setId((long) 2147483647);
                systemEntity.setLogin(systemEntityProperties.getLogin());
                systemEntity.setPassword(systemEntityProperties.getPassword());
                systemEntity.setName(systemEntityProperties.getName());
                systemEntity.setSurname(systemEntityProperties.getSurname());
                systemEntity.setBalance(systemEntityProperties.getBalance());
                systemEntity.setRole(Role.ADMIN);
                authService.registerUser(systemEntity);
                LOGGER.info("System entity user created successfully.");
            } else {
                LOGGER.info("System entity user already exists.");
            }
        };
    }

}
