package cs.vsu.radiomanager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "system.entity")
public class SystemEntityProperties {

    private String login;

    private String password;

    private String name;

    private String surname;

    private Double balance;

}
