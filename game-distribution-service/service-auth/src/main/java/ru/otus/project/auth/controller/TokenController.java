package ru.otus.project.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.project.auth.security.CustomUserDetails;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class TokenController {

    private final JwtEncoder encoder;

    @Operation(summary = "Get token", security = @SecurityRequirement(name = "httpBasic"))
    @PostMapping("/token")
    public String token(Authentication authentication) {
        Instant now = Instant.now();
        long expiry = 360000L;
        List<String> scope = getScope(authentication);

        CustomUserDetails customUserDetails = ((CustomUserDetails) authentication.getPrincipal());
        long userId = customUserDetails.getUser().getId();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scope)
                .claim("userId", userId)
                .build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private List<String> getScope(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.substring("ROLE_".length()))
                .toList();
    }
}
