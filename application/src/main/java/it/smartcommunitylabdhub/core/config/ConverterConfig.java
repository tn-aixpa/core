package it.smartcommunitylabdhub.core.config;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.converters.ConverterTypeFactory;
import it.smartcommunitylabdhub.core.models.converters.CommandFactory;
import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;
import it.smartcommunitylabdhub.core.models.converters.interfaces.ConverterFactory;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConverterConfig {

    @Bean
    protected ConverterFactory converterFactory(List<Converter<?, ?>> converters) {
        return new ConverterTypeFactory(converters);
    }

    @Bean
    protected CommandFactory commandFactory(ConverterFactory converterFactory) {
        return new CommandFactory(converterFactory);
    }
}
