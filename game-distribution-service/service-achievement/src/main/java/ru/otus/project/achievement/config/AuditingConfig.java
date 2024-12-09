package ru.otus.project.achievement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
class AuditingConfig {

    @Bean
    public AuditorAware<Long> auditorAware() {
        return new AuditorAwareImpl();
    }
}