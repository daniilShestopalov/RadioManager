package cs.vsu.radiomanager.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "daniilShestopalov",
                        email = "danya.shestopalov@gmail.com",
                        url = "https://github.com/daniilShestopalov"
                ),
                description = "Swagger documentation for RadioManagerAPI",
                title = "Swagger RadioManagerAPI"
        )
)
public class SwaggerConfig {
}
