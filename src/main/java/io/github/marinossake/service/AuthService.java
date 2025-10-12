package io.github.marinossake.service;

import io.github.marinossake.dto.LoginRequestDTO;
import io.github.marinossake.dto.LoginResponseDTO;
import io.github.marinossake.security.CustomUserPrincipal;
import io.github.marinossake.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * AuthService handles credential authentication and JWT issuance.
 * Stateless by design. No session storage.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


    /**
     * Authenticates the user and returns a signed JWT.
     * @param requestDTO username/password payload
     * @return JWT wrapped in LoginResponseDTO
     */
    public LoginResponseDTO login(LoginRequestDTO requestDTO) {

        // Delegate credential check to Spring Security (UserDetailsService + PasswordEncoder)
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getUsername(),
                        requestDTO.getPassword()
                )
        );

        // Principal contains only minimal identity + authorities (no entity, no PII)
        var principal = (CustomUserPrincipal) authentication.getPrincipal();

        // Extract logical role names for JWT (drop "ROLE_" prefix used by Spring Security)
        var roleNames = principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
                .toList();

        String token = jwtUtil.generateToken(
                principal.getPublicId(),
                principal.getUsername(),
                roleNames
        );

        return new LoginResponseDTO(token);

    }


}
