package ru.otus.project.review.config;

import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Objects;
import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Long> {

    @NonNull
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || !(authentication.getPrincipal() instanceof Jwt)) {
            return Optional.empty();
        } else {
            Long userId = ((Jwt) authentication.getPrincipal()).getClaim("userId");
            return Optional.of(userId);
        }
    }
}
