package cs.vsu.radiomanager.config;

import cs.vsu.radiomanager.dto.UserDto;
import cs.vsu.radiomanager.model.enumerate.Role;
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
public class SystemEntityConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(SystemEntityConfig.class);

    @Value("${system.entity.login}")
    private String entityLogin;

    @Value("${system.entity.password}")
    private String entityPassword;

    @Value("${system.entity.name}")
    private String entityName;

    @Value("${system.entity.surname}")
    private String entitySurname;

    @Value("${system.entity.balance}")
    private Double entityBalance;

    @Bean
    public CommandLineRunner createSystemEntity(AuthService authService) {
        return args -> {
            if (!authService.checkEmailExists(entityLogin)) {
                UserDto systemEntity = new UserDto();
                systemEntity.setLogin(entityLogin);
                systemEntity.setPassword(entityPassword);
                systemEntity.setName(entityName);
                systemEntity.setSurname(entitySurname);
                systemEntity.setBalance(entityBalance);
                systemEntity.setRole(Role.ADMIN);
                authService.registerUser(systemEntity);
                LOGGER.info("System entity user created successfully.");
            } else {
                LOGGER.info("System entity user already exists.");
            }
        };
    }

}
