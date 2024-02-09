package it.smartcommunitylabdhub.core.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import it.smartcommunitylabdhub.core.components.validators.ValidatorFactory;
import it.smartcommunitylabdhub.core.models.validators.interfaces.BaseValidator;

@Configuration
public class ValidatorConfig {
    @Bean
    protected ValidatorFactory validatorFactory(
            List<? extends BaseValidator> frameworks) {
        return new ValidatorFactory(frameworks);
    }
}
