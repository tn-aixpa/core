package it.smartcommunitylabdhub.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = { "it.smartcommunitylabdhub" })
@EnableJpaRepositories(basePackages = { "it.smartcommunitylabdhub" })
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableScheduling
@EntityScan(basePackages = { "it.smartcommunitylabdhub" })
@ComponentScan(basePackages = { "it.smartcommunitylabdhub" })
public class CoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }
}
